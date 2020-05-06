/*
 * @author Hangyu Wang (CWID: 10444246)
 * =============================================================================
 * This is the core part of the query processor.
 * Env: Here everything is built and run on Netbeans 8.2. 
 *      Better to use the Netbeans 8.2 to run my codes. Since the IDE is 
 *      different from Eclipse. It may hold its own default and cause 
 *      file read probelms and further problems. More info about IDE: Ant.
 *      Sorry for the limitations. And thank you for your understanding!
 * Lib/JAR: postgresql-8.3-604.jdbc4.jar; 
 *          commons-lang3-3.10.jar; 
 *          java-json.jar; 
 *          commons-io-2.6.jar; 
 *          JDK 1.8(java 8)
 * To keep consistency, I put all the *.jar in the folder JARS. 
 * =============================================================================
 * How to run:
 * -----------------------------------------------------------------------------
 * => For a brief run(only run based on the existing *.sql and *.json):
 * 1. You can uncomment the main in this file;
 * 2. Update the USER, PWD, URL to yours;
 * 3. Update the fileName to yours (*.sql or *.json);
 * PS. the tested file (*.sql or *.json) should be put into the inputFile folder;
 *     and you need to ensure the correctness of your input.
 * -----------------------------------------------------------------------------
 * => For a general run:
 * 1. Please go to the CodeGenerator.java;
 * 2. Update the USER, PWD, URL to yours;
 * 3. Update the SQLList to yours. Actually you can also put the name of *.json 
 *    in it;
 * 4. Here you can also input your own sql and json via run view based on the 
 *    instruction. (see more detials and format requirement in the 
 *    CodeGenerator.java and the instruction when you start running the code)
 * PS. the tested file (*.sql or *.json) in the SQLList should be put into the 
 *     inputFile folder; and you need to ensure the correctness of your input.
 * =============================================================================
 * Algorithm and Implementation:
 * It is a idea about how to write the generated code.
 * 1. Write the MFStruct for the code based one the grouping attributes and 
 *    aggeration function.
 * 2. Write the first scan: collect the data for the attributes in MFstruct;
 *    also updates the aggeration functions for the grouping variable 0 based 
 *    on the condition(where);
 * 3. Write the other scans: updates the aggeration functions for other
 *    grouping variable based on the conditions(where and such that);
 * 4. Write the code of output(select).
 * PS. For the point 3, I also use the topological sort for optimization.
 */
package QueryProcessorForMFandEMF;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.util.*;
import java.io.*;
import java.sql.*;

//import utils.PreReq;
import QueryProcessorForMFandEMF.Parser;
import QueryProcessorForMFandEMF.OptAlgorithm;
import SQLParser.SQLParser;
import java.nio.charset.StandardCharsets;

import utils.Group;
import utils.CONSTANTS;

public class Processor {

    private static String USER;
    private static String PWD;
    private static String URL;

    /*
     * For the optimazation struct.
     */
    private OptAlgorithm opt = new OptAlgorithm();
    
    /*
     * For the connection of the database. 
     * And use the nameToType to record the name of attributes to type of it.
     * All info get from the actual DataBase.
     */
    private PreparedStatement ps = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private Map<String, String> nameToType = new HashMap<>();

    /*
     * For record the six ops.
     */
    private PhiStruct oneStruct;

    /*
     * For output inforamtions:
     * outputPath => the path for the generated code. 
     * eg. ./src/outputFile/GeneratedCodeForsql1.java
     * outputFile => the name info for the generated code. 
     * eg. ./src/outputFile/GeneratedCodeFor{outputFile}.java
     * also used for other info relating to code generation.
     */
    private String outputPath;
    private String outputFile = "";

    /*
     * Template of the generated codes.
     */
    private final String samplePath = "./src/utils/codeSample.txt";
    
    /*
     * For write the code.
     */
    FileWriter fw;
    BufferedWriter bw;
    
    /*
     * initialize the processor.
     * Here I default the Processor() to be mine connection;
     * But the Processor(String USER, String PWD, String URL) is mainly used 
     * in running tests.
     */
    public Processor(){
        this("postgres", "m8kimmWhyholly", "jdbc:postgresql://localhost:5432/postgres");
    }
    
    public Processor(String USER, String PWD, String URL){
        this.USER = USER;
        this.PWD = PWD;
        this.URL = URL;
    }
    
    /*
     * For read the input(*.sql and *json):
     * If the input is sql, call the sqlParser(in SQLParser) to parse to *.json 
     * and  call readInputFromJSON(String fileName) process;
     * If the input is json, readInputFromJSON(String fileName) to process;
     */
    public void readInput(String fileName){
        try{
            if(fileName.endsWith("json")){
                this.readInputFromJSON(fileName);
                this.outputFile = fileName.substring(0, fileName.length() - 5);
            }else if(fileName.endsWith("sql")){
                SQLParser sqlParser = new SQLParser(USER, PWD, URL);
                String jsonFileName = fileName.substring(0, fileName.length() - 3) + "json";
                this.outputFile = fileName.substring(0, fileName.length() - 4);
                sqlParser.performSQLParser(fileName, jsonFileName);
                this.readInputFromJSON(jsonFileName);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /*
     * For read the input(*.json):
     * After read the json, the PhiStruct will record the six ops 
     * and opt will obtain the order list optimization.
     */
    public void readInputFromJSON(String fileName){
        String tempOutPath = "./src/outputFile/GeneratedCodeFor" + fileName;
        this.outputPath = (tempOutPath.substring(0, tempOutPath.length() - 4) + "java");
        File inputJSON = new File("./src/inputFile/" + fileName);
        JSONObject j = null;
        try{
            /*
             * Read whole file from *.json and format to JSONObject to j
             */
            String jStr = FileUtils.readFileToString(inputJSON, StandardCharsets.UTF_8);
            j = new JSONObject(jStr);

            /*
             * Get the projAttrs from JSONObject 
             * ==> projectedAttributes in the PhiStruct (List<String>)
             */
            List<String> projectedAttributes = new ArrayList<>();
            for(int i = 0; i < j.getJSONArray("projAttrs").length(); i++){
                projectedAttributes.add(j.getJSONArray("projAttrs").getString(i));
            }
            /*
             * Get the numOfGVars from JSONObject 
             * ==> numOfGVars in the PhiStruct (int)
             */
            Integer numOfGVars = (Integer) j.get("numOfGVars");
            List<String> gAttributes = new ArrayList<>();
            String gAttrStr = (String) j.get("gAttrs");
            for(String attr: gAttrStr.split(",")){
                gAttributes.add(attr.trim());
            }
            /*
             * Get the aggFuncs from JSONObject 
             * ==> aggregateFuncs in the PhiStruct (List<Group>) 
             * it will be auto parsed when initialization.
             */
            List<String> aggregateFuncs = new ArrayList<>();
            for(int i = 0; i < j.getJSONArray("aggFuncs").length(); i++){
                aggregateFuncs.add(j.getJSONArray("aggFuncs").getString(i));
            }
            /*
             * Get the condOfGVars from JSONObject 
             * ==> predicateOfGVars in the PhiStruct (List<String>)
             */
            List<String> predicateOfGVars = new ArrayList<>();
            for(int i = 0; i < j.getJSONArray("condOfGVars").length(); i++){
                predicateOfGVars.add(j.getJSONArray("condOfGVars").getString(i));
            }
            /*
             * Get the condOfHaving from JSONObject 
             * ==> predicateOfHaving in the PhiStruct (String)
             */
            String predicateOfHaving = (String) j.get("condOfHaving"); 
            /*
             * initialze the PhiStruct for this processor
             */
            oneStruct = new PhiStruct(  projectedAttributes, 
                                        numOfGVars, 
                                        gAttributes,
                                        aggregateFuncs, 
                                        predicateOfGVars, 
                                        predicateOfHaving);

            /*
             * do topoSort to create the opt order list
             */
            opt.topoSort(oneStruct.getNumOfGV(), j.getJSONArray("Opt"));

        }catch(Exception e){
            e.printStackTrace();
        }
        return;
    }
    
    /*
     * Write the MFstruct in the generated code
     */
    public void writeStruct(){
        List<String> name = new ArrayList<>();
        List<String> type = new ArrayList<>();
        try{
            /*
             * Get what need to be put in the MFStrcuts from Phistruct
             */
            for(String attr: oneStruct.getG_ATTR()){
                String originType = nameToType.get(attr);
                String typeTemp = CONSTANTS.dbTypeToJavaType.get(originType);
                bw.write(Tab(2) + typeTemp + " " + attr +";");
                bw.newLine();
                name.add(attr);
                type.add(originType);
            }
            bw.flush();
            /*
             * write para in the MFsrtuct
             */
            for(Group body: oneStruct.getAggFunc()){
                String attr = body.aggType + "_" + body.sub + "_" + body.attr;
                String originType = CONSTANTS.AGG_FUNCS_TO_TYPE.get(body.aggType);
                String typeTemp = CONSTANTS.dbTypeToJavaType.get(originType);
                bw.write(Tab(2) + typeTemp + " " + attr +";");
                bw.newLine();
                name.add(attr);
                type.add(originType);
            }
            bw.flush();
            bw.write(Tab(2) + "MFStruct(){");
            bw.newLine();
            bw.newLine();
            bw.flush();
            /*
             * init the value of the para in the MFsrtuct
             */
            for(int i = 0; i < name.size(); i++){
                if(name.get(i).startsWith("count")){
                    bw.write(Tab(4) + name.get(i) + " = 0L" + ";");
                }else{
                    bw.write(Tab(4) + name.get(i) + CONSTANTS.typeToInitVal.get(type.get(i)) + ";");
                }
                bw.newLine();
                bw.flush();
            }
            bw.write(Tab(2) + "}");
            bw.newLine();
            bw.flush();
            writeFromSample(24, 24);
        }catch(Exception e){
            System.out.println("writeStruct ERROR");
             e.printStackTrace();
        }
    }
    
    /*
     * Setup the database connection info in the generated code
     * Also write the connect() and close() function in the code.
     * Also setup the main in the generated code
     */
    public void writeDBConnetionSetup(){
        try{
            bw.write(Tab(1) + "private static final String USER = \"" + this.USER + "\";\n");
            bw.write(Tab(1) + "private static final String PWD = \"" + this.PWD + "\";\n");
            bw.write(Tab(1) + "private static final String URL = \"" + this.URL + "\";\n");
            writeFromSample(29, 36);
            bw.write(Tab(2) + "GeneratedCodeFor" + this.outputFile +  " res = new " + "GeneratedCodeFor" + this.outputFile +"();\n");
            writeFromSample(38, 71);
        }catch(Exception e){
            System.out.println("Here is the writeDBConnetionSetup ERROR");
            e.printStackTrace();
        }
    }
    
    /*
     * Write the first scan: 
     * collect the data for the attributes in MFstruct;
     * also updates the aggeration functions for the grouping variable 0 based 
     * on the condition(where);
     */
    public void firstScan(){
        try{

            List<String> g_Attrs = oneStruct.getG_ATTR();
            writeFromSample(72, 78);
            
            bw.write(Tab(3) + "///////////////First Scan////////////");
            bw.newLine();
            bw.flush();
            
            writeFromSample(79, 88);
            String generalCond = oneStruct.getCond_GV().get(0);
            generalCond = generalCond.equals("") ? "true": generalCond;
            bw.write(Tab(4) + (generalCond.equals("_")? "if(true)": generalCond) + " {");
            bw.newLine();

            /*
             * Create HashMap for the MFstruct:
             * key: the combination of grouping attributes; 
             * value: the corresponding MFstruct.
             */
            String st = "String key = \"\" ";
            for(String attr: g_Attrs){
                String type = CONSTANTS.dbTypeToJavaType.get(nameToType.get(attr));
                type = type.equals("String") ? "String": "Int";
                st += "+ rstm.get" + type + "(\"" + attr + "\")";
            }
            bw.write(Tab(5) + st + ";");
            bw.newLine();
            writeFromSample(91, 92);
            for(String attr: g_Attrs){
                String type = CONSTANTS.dbTypeToJavaType.get(nameToType.get(attr));
                type = type.equals("String") ? "String": "Int";
                String defSt = "newStrcut.";
                defSt += attr + " = ";
                defSt += "rstm.get" + type + "(\"" + attr + "\")";
                bw.write(Tab(6) + defSt + ";");
                bw.newLine();
                bw.flush();
            }
            
            /*
             * init the value of MFstruct for grouping variable 0
             */
            writeFromSample(94, 96);
            bw.write(Tab(5) + "MFStruct curStruct = structList.get(key);");
            bw.newLine();
            for(Group aF: oneStruct.getAggFunc()){
                String curType = aF.aggType;
                String curAttr = aF.attr;
                String curSub = aF.sub;
                String codes = "";
                String tempStr = "";
                if(Integer.parseInt(curSub) == 0){
                    switch(curType){
                        case "sum":
                            tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                            codes = tempStr + " = " + tempStr+ " == null ? "
                                    + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                    + " : " + tempStr + '+'
                                    + "rstm.getInt(" + "\"" + curAttr + "\"" +");";
                        break;
                        case "count":
                            tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                            codes = tempStr + " = " + tempStr + " == null ? "
                                    + "1"
                                    + " : " + tempStr
                                    + " + 1;";
                        break;
                        case "max":
                            tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                            codes = tempStr + " = " + tempStr + " == null ? "
                                    + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                    + " : (" + tempStr + " > "
                                    + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                    +" ? "+ tempStr+" : " + "rstm.getInt(" + "\"" + curAttr + "\"" +")" +");";
                        break;
                        case "min":
                            tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                            codes = tempStr + " = " + tempStr + " == null ? "
                                    + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                    + " : (" + tempStr + " < "
                                    + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                    +" ? "+ tempStr+" : " + "rstm.getInt(" + "\"" + curAttr + "\"" +")" +");";
                        break;
                        case "avg":
                            tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                            codes = tempStr + " = (" + "curStruct." + "sum" + "_" + curSub + "_" + curAttr + " + 0.0)"
                                    + "/" 
                                    + "curStruct." + "count" + "_" + curSub + "_" + curAttr + ";";

                        break;
                    }

                }else{
                    continue;
                }
                bw.write(Tab(5) + codes);
                bw.newLine();
            }
            
            
            writeFromSample(98, 98);
            bw.write(Tab(4) + "}");
            bw.newLine();
            bw.write(Tab(4) + "more = rstm.next();");
            bw.newLine();
            bw.write(Tab(3) + "}");
            bw.newLine();

            bw.flush();
            
        }catch(Exception e){
            System.out.println("Here is the firstScan ERROR");
            e.printStackTrace();
        }

    }
    
    /* 
     * Write the other scans: 
     * updates the aggeration functions for other
     * grouping variable based on the conditions(where and such that);
     */
    public void otherScans(){
        try{
            bw.write(Tab(3) + "///////////////Other Scan(s)////////////");
            bw.flush();
            bw.newLine();
            bw.write(Tab(3) + "int count = " + opt.cnt +";");
            bw.newLine();
            writeFromSample(101, 106);
            

            Iterator<List<Integer>> iter = opt.order.iterator();
            List<String> cond_Gv = oneStruct.getCond_GV();

            int count = 0;
            /*
             * updates the value of MFstruct for other grouping variables
             */
            while(count  < opt.cnt){

                bw.write(Tab(7) +"case " + (count + 1) + ":");
                bw.newLine();
                
                /*
                 * updates according to the opt list
                 */
                List<Integer> preList = iter.next();
                
                for(int val: preList){
                    
                    bw.write(Tab(8) + cond_Gv.get(val) + "{");
                    bw.newLine();
                    for(Group aF: oneStruct.getAggFunc()){
                        String curType = aF.aggType;
                        String curAttr = aF.attr;
                        String curSub = aF.sub;
                        String codes = "";
                        String tempStr = "";
                        if(Integer.parseInt(curSub) == val){
                            switch(curType){
                                case "sum":
                                    tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                                    codes = tempStr + " = " + tempStr+ " == null ? "
                                            + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                            + " : " + tempStr + '+'
                                            + "rstm.getInt(" + "\"" + curAttr + "\"" +");";
                                break;
                                case "count":
                                    tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                                    codes = tempStr + " = " + tempStr + " == null ? "
                                            + "1"
                                            + " : " + tempStr
                                            + " + 1;";
                                break;
                                case "max":
                                    tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                                    codes = tempStr + " = " + tempStr + " == null ? "
                                            + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                            + " : (" + tempStr + " > "
                                            + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                            +" ? "+ tempStr+" : " + "rstm.getInt(" + "\"" + curAttr + "\"" +")" +");";
                                break;
                                case "min":
                                    tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                                    codes = tempStr + " = " + tempStr + " == null ? "
                                            + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                            + " : (" + tempStr + " < "
                                            + "rstm.getInt(" + "\"" + curAttr + "\"" +")"
                                            +" ? "+ tempStr+" : " + "rstm.getInt(" + "\"" + curAttr + "\"" +")" +");";
                                break;
                                case "avg":
                                    tempStr = "curStruct." + curType + "_" + curSub + "_" + curAttr;
                                    codes = tempStr + " = (" + "curStruct." + "sum" + "_" + curSub + "_" + curAttr + " + 0.0)"
                                            + "/" 
                                            + "curStruct." + "count" + "_" + curSub + "_" + curAttr + ";";

                                break;
                            }

                        }else{
                            continue;
                        }
                        bw.write(Tab(9) + codes);
                        bw.newLine();
                    }
                    bw.write(Tab(8) + "}");
                    bw.newLine();
                }
                
                
                bw.write(Tab(7) + "break;");
                
                bw.newLine();
                count++;
            }
            writeFromSample(108, 112);
            bw.newLine();
            
        }catch(Exception e){
            System.out.println("Here is the other Scans ERROR");
            e.printStackTrace();
        }
    }
    
    /* 
     * Write print out: 
     * walk through the HashMap to print the table
     * String left aligned; number right aligned
     */
    public void printResult(){
        try{
            bw.write(Tab(3) + "///////////////Print Out////////////");
            bw.newLine();
            bw.flush();
            final int len = oneStruct.getProjATTR().size();
            /* 
             * Write print out for the title: 
             */
            for(int i = 0; i < len; i++){
                if(i == len - 1){
                    if(oneStruct.getProjATTR().get(i).startsWith("avg")
                        || oneStruct.getProjATTR().get(i).contains("+")
                        || oneStruct.getProjATTR().get(i).contains("-")
                        || oneStruct.getProjATTR().get(i).contains("*")
                        || oneStruct.getProjATTR().get(i).contains("/")){
                        writeCode(Tab(3) + "System.out.printf(\"%-24s  \\n\", \"" + oneStruct.getProjATTR().get(i) + "\");");
                    }else if(oneStruct.getProjATTR().get(i).startsWith("count") 
                            || oneStruct.getProjATTR().get(i).startsWith("min")
                            || oneStruct.getProjATTR().get(i).startsWith("max")
                            || oneStruct.getProjATTR().get(i).startsWith("sum")){
                        writeCode(Tab(3) + "System.out.printf(\"%-12s  \\n\", \"" + oneStruct.getProjATTR().get(i) + "\");");
                    }else{
                        writeCode(Tab(3) + CONSTANTS.PRINT_LAST_ATTR_MAP.get(nameToType.get(oneStruct.getProjATTR().get(i))) + oneStruct.getProjATTR().get(i) + "\");");
                    }
                    break;
                }
                if(oneStruct.getProjATTR().get(i).startsWith("avg")
                    || oneStruct.getProjATTR().get(i).contains("+")
                    || oneStruct.getProjATTR().get(i).contains("-")
                    || oneStruct.getProjATTR().get(i).contains("*")
                    || oneStruct.getProjATTR().get(i).contains("/")){
                    writeCode(Tab(3) + "System.out.printf(\"%-24s  \", \"" + oneStruct.getProjATTR().get(i) + "\");");
                }else if(oneStruct.getProjATTR().get(i).startsWith("count") 
                            || oneStruct.getProjATTR().get(i).startsWith("min")
                            || oneStruct.getProjATTR().get(i).startsWith("max")
                            || oneStruct.getProjATTR().get(i).startsWith("sum")){
                    writeCode(Tab(3) + "System.out.printf(\"%-12s  \", \"" + oneStruct.getProjATTR().get(i) + "\");");
                }else{
                    writeCode(Tab(3) + CONSTANTS.PRINT_ATTR_MAP.get(nameToType.get(oneStruct.getProjATTR().get(i))) + oneStruct.getProjATTR().get(i) + "\");");
                }
                
            }
            writeFromSample(114, 115);

            /* 
             * Write print out for values: 
             */
            bw.write(Tab(4) + (oneStruct.getCond_Having().equals("_")
                                            ? "if(true)"
                                            : oneStruct.getCond_Having()) 
                    + "{");
            bw.newLine();
            for(String attr: oneStruct.getProjATTR()){
                if(attr.startsWith("avg") 
                    || attr.contains("+") || attr.contains("-")
                    || attr.contains("*") || attr.contains("/")){
                    writeCode(Tab(5) + "System.out.printf(\"%24.16f  \", " + Parser.formatExpWithAggFunc(attr) + ");");
                }else if(attr.startsWith("count") 
                        || attr.startsWith("max")
                        || attr.startsWith("min") 
                        || attr.startsWith("sum")){
                    writeCode(Tab(5) + "System.out.printf(\"%12s  \", " + Parser.formatExpWithAggFunc(attr) + ");");
                }else{
                    writeCode(Tab(5) + CONSTANTS.PRINT_MAP.get(nameToType.get(attr)) + Parser.formatExpWithAggFunc(attr) + ");");
                }
            }
            bw.write(Tab(5) + "System.out.println();\n");
            bw.write(Tab(4) + "}");
            bw.newLine();
            writeFromSample(117, 128);
        }catch(Exception e){
            System.out.println("Here is the printResult ERROR");
            e.printStackTrace();
        }

    }
    /* 
     * Supporting function on setup. 
     */
    public void createFile(){
        try{
            File file = new File(outputPath);
            fw = new FileWriter(outputPath);
            bw = new BufferedWriter(fw);
            writeFromSample(1, 16);
            bw.write("public class GeneratedCodeFor" + this.outputFile + "{");
            bw.newLine();
            bw.flush();
            writeFromSample(19, 19);
        }catch(Exception e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    /* 
     * Supporting function for write the code.
     */
    private void writeCode(String codeLine){
        try{
            bw.write(codeLine);
            bw.newLine();
            bw.flush();
        }catch(Exception e){
            System.out.println("Something wrong with the print");
            System.out.println(e);
        }
    }
    /* 
     * Supporting function for write from the sample text.
     */
    private void writeFromSample(int startLine, int endLine){
        int count = 0;
        try{
            Scanner tempS = new Scanner(new BufferedReader(new FileReader(samplePath)));
            while(tempS.hasNextLine()){
                count++;
                String temp = tempS.nextLine();
                if(count >= startLine && count <= endLine){
                    writeCode(temp);
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    /* 
     * Small function on creating Tab in the code 
     */
    private String Tab(int n){
        return new String(new char[n * 4]).replace("\0", " ");
    } 
    
    /* 
     * create the nameToType
     */
    public void getTypeFromDB(){
        try{
            conn = DriverManager.getConnection(URL, USER, PWD);
            ps = conn.prepareStatement("select * from Information_schema.columns where table_name = 'sales'"); 
            rs = ps.executeQuery();
            while(rs.next()){
                nameToType.put(rs.getString("column_name"), rs.getString("data_type"));
            }
            conn.close();
        }catch(Exception e){
            System.out.println("Please maek sure that you provide the right URL, USER, PWD!");
            e.printStackTrace();
        }
    }
            
            
//    public static void main(String[] args){
//        String USER = "postgres";
//        String PWD = "m8kimmWhyholly";
//        String URL = "jdbc:postgresql://localhost:5432/postgres";
//        Processor p = new Processor(USER, PWD, URL);
//        
//        String fileName = "tempSQL.sql";
//
//        p.getTypeFromDB();
////        System.out.println("DBTHING DONE");
//        p.readInput(fileName);
////        System.out.println("CreateFile");
//        p.createFile();
////        System.out.println("CreateFile");
//        p.writeStruct();
////        System.out.println("writeFile");
//        p.writeDBConnetionSetup();
//        p.firstScan();
////        System.out.println("FirstScan");
//        p.otherScans();
////        System.out.println("OtherScan");
//        p.printResult();
////        System.out.println("OtherScan");
//            
//    }
}
