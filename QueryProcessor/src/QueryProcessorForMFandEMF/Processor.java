/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryProcessorForMFandEMF;
//package utils;
/**
 *
 * @author Hangyu Wang
 */
import java.util.*;
import java.io.*;
import java.sql.*;

import utils.PreReq;
import QueryProcessorForMFandEMF.Parser;
import QueryProcessorForMFandEMF.OptAlgorithm;

import utils.Group;
import utils.CONSTANTS;
public class Processor {
    private final static int SELECTATTRIBUTE = 0, NUMOFGV = 1, GATTR = 2,
            FVECT = 3, SELECTCONDVECT = 4, HAVINGCOND = 5;
    private static final String USER ="postgres";
    private static final String PWD ="m8kimmWhyholly";
    private static final String URL ="jdbc:postgresql://localhost:5432/postgres";
    private OptAlgorithm opt = new OptAlgorithm();
    private PreparedStatement ps = null;
    private Connection conn = null;
    private ResultSet rs = null;
    
    private static Map<String, String> nameToType = new HashMap<>();
//    private static Map<String, String> dbTypeToJavaType = new HashMap<String, String>() {{
//        put("character varying", "String");
//        put("character", "String");
//        put("integer", "Integer");
//    }};
//    private final static Map<String, String> typeToInitVal = new HashMap<String, String>() {{
//        put("character varying", " = \"\"");
//        put("character", " = \"\"");
//        put("integer", " = null");
//    }};
    private final static String SPACE = " ";
    
    private PhiStruct oneStruct;
//    private File file;
    private static String outputPath;
    private static final String samplePath = "./src/QueryProcessorForMFandEMF/codeSample.java";
    File sampleFile;
    FileWriter fw;
    BufferedWriter bw;

    public void readInput(String path, String fileName){
        String tempOutPath = "./src/outputFile/GeneratedCodeFor" + fileName;
        this.outputPath = (tempOutPath.substring(0, tempOutPath.length() - 3) + "java");
        Scanner s = null;
        int status = 0;
        try{
            s = new Scanner(new BufferedReader(new FileReader(path + fileName)));
        }catch(Exception  e){
            e.printStackTrace();
        }
        s.nextLine();
        List<String> projectedAttributes = new ArrayList<>();
        Integer numOfGVars = null;
        List<String> gAttributes = new ArrayList<>();
        List<String> aggregateFuncs = new ArrayList<>();
        List<String> predicateOfGVars = new ArrayList<>();
        String predicateOfHaving = new String(); 
        while(s.hasNextLine()){
            if(!s.hasNext()){
                break;
            }
            //Check status, make sure the reading content
            String line = s.nextLine();
            if(line.startsWith("SELECT ATTRIBUTE(S)")){
                status = SELECTATTRIBUTE;
            }else if(line.startsWith("NUMBER OF GROUPING VARIABLES(n)")){
                status = NUMOFGV;
            }else if(line.startsWith("GROUPING ATTRIBUTES(V)")){
                status = GATTR;
            }else if(line.startsWith("F-VECT([F])")){
                status = FVECT;
            }else if(line.startsWith("SELECT CONDITION-VECT([σ])")){
                status = SELECTCONDVECT;
            }else if(line.startsWith("HAVING_CONDITION(G)")){
                status = HAVINGCOND;
            }else{
                String[] tempArr;
                switch(status){
                    case SELECTATTRIBUTE:
                        tempArr = line.split(",");
                        for(String str: tempArr)
                            projectedAttributes.add(str.trim());
                        break;
                    case NUMOFGV:
                        numOfGVars = Integer.parseInt(line);
                        break;
                    case GATTR:
                        tempArr = line.split(",");
                        for(String str: tempArr)
                            gAttributes.add(str.trim());
//                        gAttributes = Arrays.asList(line.split(","));
                        break;
                    case FVECT:
                        tempArr = line.split(",");
                        for(String str: tempArr)
                            aggregateFuncs.add(str.trim());
//                        aggregateFuncs = Arrays.asList(line.split(","));
                        break;
                    case SELECTCONDVECT:
                        predicateOfGVars.add(line);
                        break;
                    case HAVINGCOND:
                        predicateOfHaving = line;
                        break;
                }
            }
        
        
        }
        
        System.out.println(projectedAttributes);
        System.out.println(numOfGVars);
        System.out.println(gAttributes);
        System.out.println(aggregateFuncs);
        System.out.println(predicateOfGVars);
        System.out.println(predicateOfHaving);
        
        List<PreReq> list = new ArrayList<>();
        list.add(new PreReq(1, 2));
        opt.topoSort(numOfGVars, list);
        
        oneStruct = new PhiStruct(  projectedAttributes, 
                                    numOfGVars, 
                                    gAttributes,
                                    aggregateFuncs, 
                                    predicateOfGVars, 
                                    predicateOfHaving);

        
    }
    
    public void writeStruct(){
        List<String> name = new ArrayList<>();
        List<String> type = new ArrayList<>();
        try{
            for(String attr: oneStruct.getG_ATTR()){
                String originType = nameToType.get(attr);
                String typeTemp = CONSTANTS.dbTypeToJavaType.get(originType);
//                String typeTemp = nameToType.get(attr);
                bw.write(Tab(2) + typeTemp + " " + attr +";");
                bw.newLine();
                name.add(attr);
                type.add(originType);
            }
            bw.flush();
            System.out.println("/////////////Here//////");
            for(Group body: oneStruct.getAggFunc()){
                String attr = body.aggType + "_" + body.sub + "_" + body.attr;
                String originType = nameToType.get(body.attr);
                String typeTemp = CONSTANTS.dbTypeToJavaType.get(originType);
//                System.out.println(originType+"=>"+typeTemp);
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
//            System.out.println(name);
//            System.out.println(name);
//            System.out.println("///////////////////////");
            for(int i = 0; i < name.size(); i++){
//                System.out.println(type.get(i));
//                System.out.println(name.get(i) + typeToInitVal.get(type.get(i)) + ";");
                bw.write(Tab(4) + name.get(i) + CONSTANTS.typeToInitVal.get(type.get(i)) + ";");
//                bw.flush();
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
    
    public void writeDBConnetionSetup(){
        writeFromSample(26, 71);
    }
    //use for group by 
    public void firstScan(){
        try{
            List<String> g_Attrs = oneStruct.getG_ATTR();
            writeFromSample(72, 88);
            String generalCond = oneStruct.getCond_GV().get(0);
            generalCond = generalCond.equals("") ? "true": generalCond;
            bw.write(Tab(4) + "if(" + (generalCond.equals("_")? "true": generalCond) + ") {");
            bw.newLine();
    //        String key = "" + rstm.getString("prod") + rs.getInt("month");
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
//                st += "+ rstm.get" + type + "(\"" + attr + "\")";
                String defSt = "newStrcut.";
                defSt += attr + " = ";
                defSt += "rstm.get" + type + "(\"" + attr + "\")";
                bw.write(Tab(6) + defSt + ";");
                bw.newLine();
                bw.flush();
            }
            writeFromSample(94, 96);
            bw.write(Tab(4) + "}");
            bw.newLine();
            bw.write(Tab(4) + "more = rstm.next();");
            bw.newLine();
            bw.write(Tab(3) + "}");
            bw.newLine();

            bw.flush();
            
        }catch(Exception e){
            System.out.println("Here is the firstScan ERROR");
        }

    }
    
    private void otherScans(){
        try{
            bw.write("///////////////Other Scan////////////");
            bw.flush();
            bw.newLine();
            bw.write(Tab(3) + "int count = " + opt.cnt +";");
//            bw.write(Tab(3) + "int count = " + oneStruct.getNumOfGV()+";");
            bw.newLine();
            writeFromSample(101, 106);
            

            Iterator<List<Integer>> iter = opt.order.iterator();
            List<String> cond_Gv = oneStruct.getCond_GV();
//            iter.next();
            int count = 0;
//            int count 
            while(count  < opt.cnt){
//                case
//                System.out.println("Here is the condition: " + iter.next());
                bw.write(Tab(7) +"case " + (count + 1) + ":");
                bw.newLine();
                List<Integer> preList = iter.next();
                for(int val: preList){
                    bw.write(Tab(8) + cond_Gv.get(val) + "{");
                    bw.newLine();
                    for(Group aF: oneStruct.getAggFunc()){
    //                    System.out.println(aF.aggType + aF.attr + aF.sub);
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
                                case "cnt":
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
                                    codes = tempStr + " = " + "curStruct." + "sum" + "_" + curSub + "_" + curAttr
                                            + "/" 
                                            + "curStruct." + "cnt" + "_" + curSub + "_" + curAttr + ";";

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
            System.out.println("Here is the firstScan ERROR");
        }
    }
    
    
    public void printResult(){
        try{
            final int len = oneStruct.getProjATTR().size();
            for(int i = 0; i < len; i++){
                if(i == len - 1){
                    writeCode(Tab(3) + "System.out.printf(\"%-7s  \\n\", \"" + oneStruct.getProjATTR().get(i) + "\");");
                    break;
                }
                writeCode(Tab(3) + "System.out.printf(\"%-7s  \", \"" + oneStruct.getProjATTR().get(i) + "\");");
            }
            writeFromSample(114, 115);

            bw.write(Tab(4) + oneStruct.getCond_Having() + "{");
            bw.newLine();
            for(String attr: oneStruct.getProjATTR())
                writeCode(Tab(5) + "System.out.printf(\"%-7s  \", " + Parser.projAttrs(attr) + ");");
            bw.write(Tab(5) + "System.out.println();\n");
            bw.write(Tab(4) + "}");
            bw.newLine();
            writeFromSample(117, 128);
        }catch(Exception e){
            System.out.println("Here is the firstScan ERROR");
        }

    }

    public void createFile(){
        try{
            File file = new File(outputPath);
            fw = new FileWriter(outputPath);
            bw = new BufferedWriter(fw);
            writeFromSample(1, 16);
            bw.write("public class GeneratedCodeFor" + "input1" + "{");
            bw.newLine();
            bw.flush();
            writeFromSample(19, 19);
        }catch(Exception e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
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
    
    private void writeFromSample(int startLine, int endLine){
        int count = 0;
//        sampleFile = new File(samplePath);
        try{
            Scanner tempS = new Scanner(new BufferedReader(new FileReader(samplePath)));
            while(tempS.hasNextLine()){
                count++;
                String temp = tempS.nextLine();
//                System.out.println(temp);
                if(count >= startLine && count <= endLine){
                    writeCode(temp);
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    private String Tab(int n){
        return new String(new char[n * 4]).replace("\0", " ");
    } 
    
    
    private void getTypeFromDB(){
        try{
            conn = DriverManager.getConnection(URL, USER, PWD);
            ps = conn.prepareStatement("select * from Information_schema.columns where table_name = 'sales'"); 
            rs = ps.executeQuery();
//            System.out.println("Here to get the type from DB");
//            more=rs.next(); 
            // Connect 
            while(rs.next()){
                nameToType.put(rs.getString("column_name"), rs.getString("data_type"));
            }
//            System.out.println("Here is the new type");
//            System.out.println(nameToType);
            conn.close();
        }catch(Exception exception){
            System.out.println("Retrieve!");
            exception.printStackTrace();
        }
    }
            
            
    public static void main(String[] args){
        Processor p = new Processor();
        Integer test = null;

        p.getTypeFromDB();
//
        p.readInput("./src/inputFile/", "input1.txt");
        p.createFile();
//        p.writeFromSample(1, 16);
        p.writeStruct();
        p.writeDBConnetionSetup();
        p.firstScan();
        p.otherScans();
        p.printResult();
            
    }
}
