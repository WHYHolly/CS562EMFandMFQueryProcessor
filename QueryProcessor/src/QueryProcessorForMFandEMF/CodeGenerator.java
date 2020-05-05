/*
 * This is a top level file for running the SQL Processor.
 * Here you need to input sereval information to run the program
 * 1. For DataBase connection: USER, PWD, URL
 */
package QueryProcessorForMFandEMF;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 *
 * @author Hangyu Wang
 */
public class CodeGenerator {
    public static void main(String[] args){

        String USER = "postgres";
        String PWD = "m8kimmWhyholly";
        String URL = "jdbc:postgresql://localhost:5432/postgres";
        List<String> SQLList = new ArrayList<String>(){{
            add("sql1.sql"); add("sql2.sql"); add("sql3.sql"); add("sql4.sql");
            add("sql5.sql"); add("sql6.json");
        }};
        
        boolean loop = true;
        String instruction = "Hi! Here is the OnePlusOne EMF Platform.\n"
                + "If you want to check you generated code. Please go to outputFile folder and run manually.\n"
                + "Other operations:\n"
                + "1. run the demo list, enter 1;\n"
                + "2. generate your onw CODE with SQL format, enter 2;\n"
                + "3. generate your onw CODE with JSON format, enter 3;\n"
                + "4. exit, enter 0.\n"
                + "Thank you for using OnePluaOne.\n"
                + "////////////////////////////////////////////////\n"
                + "Please Enter: ";
        String SQLInstruction = "Your input have to contain group by clause and a right input.\n"
                + "To indicate your input is finished, please enter END.\n"
                + "Please enter your SQL(clause in lowercase): \n";
        String JSONInstruction = "Here is the format for your JSON:\n"
                + "{\n" 
                + "  \"Opt\": [{from: FROM_NODE_ID, to: TO_NODE_ID}],\n" 
                + "  \"projAttrs\": [ATTRS_AND_AGG_FOR_OUTPUT],\n" 
                + "  \"gAttrs\": \"GROUP_BY_ATTRIBUTES split with ,\",\n" 
                + "  \"aggFuncs\": [AGGFUNCTIONS_FOR_EACH_VAR],\n" 
                + "  \"numOfGVars\": NUMBER_OF_GROUP_VARS,\n" 
                + "  \"condOfHaving\": \"HAVING CLAUSE\",\n" 
                + "  \"condOfGVars\": [CONDITION_OF_GROUP_VARS]\n" 
                + "}\n"
                + "More sample to see *.json in inputfile folder.\n"
                + "To indicate your input is finished, please enter END.\n"
                + "Please enter your JSON: \n";
        String byeInstruction = "////////////////////////////////////////////////\n"
                + "Bye Bye! Thank you for using!"
                + "////////////////////////////////////////////////\n";
        String outputPath = "./src/inputFile/";
        Scanner input = new Scanner(System.in);
        while(loop){
            System.out.println(instruction);
            try{
                int ID = input.nextInt();
                switch(ID){
                    case 0:
                        loop = false;
                        System.out.println(byeInstruction);                      
                        break;
                    case 1:
                        for(String sql: SQLList){
                            System.out.println("For " + sql + ":");
                            Processor p = new Processor(USER, PWD, URL);
                            Integer test = null;

                            p.getTypeFromDB();
                            p.readInput(sql);
                            p.createFile();
                            p.writeStruct();
                            p.writeDBConnetionSetup();
                            p.firstScan();
                            p.otherScans();
                            p.printResult();

                            System.out.println("DONE!");
                        }
                        break;
                    case 2:
                        System.out.println(SQLInstruction);
                        Scanner sqlInput = new Scanner(System.in);
                        String res = "";
                        File fileSQL = new File(outputPath + "tempSQL" + ".sql");
                        PrintWriter outSQL = new PrintWriter(fileSQL);
                        while(sqlInput.hasNextLine()){
                            System.out.println("Here is the test");
                            String line = sqlInput.nextLine();
                            if(line.equals("END")){
                                break;
                            }
                            outSQL.println(line);
                            outSQL.flush();
                        }
                        System.out.println("YOUR SQL FILE IS READY!");
                        outSQL.close();
                        System.out.println("For " + "tempSQL.sql" + ":");
                        Processor pTempSQL = new Processor(USER, PWD, URL);

                        pTempSQL.getTypeFromDB();
                        pTempSQL.readInput("tempSQL.sql");
                        pTempSQL.createFile();
                        pTempSQL.writeStruct();
                        pTempSQL.writeDBConnetionSetup();
                        pTempSQL.firstScan();
                        pTempSQL.otherScans();
                        pTempSQL.printResult();

                        System.out.println("DONE!");
                        break;
                    case 3:
                        System.out.println(JSONInstruction);
//                        Scanner jsonInput = new Scanner(System.in);
                        Scanner jsonInput = new Scanner(System.in);
                        File fileJSON = new File(outputPath + "tempJSON" + ".json");
                        PrintWriter outJSON = new PrintWriter(fileJSON);
                        while(jsonInput.hasNextLine()){
                            String line = jsonInput.nextLine();
                            if(line.equals("END")){
                                break;
                            }
                            outJSON.println(line);
                            outJSON.flush();
                        }
                        System.out.println("YOUR JSON FILE IS READY!");
                        outJSON.close();
                        System.out.println("For " + "tempJSON.json" + ":");
                        Processor pTempJSON = new Processor(USER, PWD, URL);

                        pTempJSON.getTypeFromDB();
                        pTempJSON.readInput("tempJSON.json");
                        pTempJSON.createFile();
                        pTempJSON.writeStruct();
                        pTempJSON.writeDBConnetionSetup();
                        pTempJSON.firstScan();
                        pTempJSON.otherScans();
                        pTempJSON.printResult();

                        System.out.println("DONE!");
                        break;
                }
            }catch(Exception e){
                System.out.println("////////////////////////Error////////////////////////");
                System.out.println("Please make sure you input the right instruction");
                System.out.println("/////////////////////////End/////////////////////////");
            }
//            
        }
        input.close();
        
    }
}
