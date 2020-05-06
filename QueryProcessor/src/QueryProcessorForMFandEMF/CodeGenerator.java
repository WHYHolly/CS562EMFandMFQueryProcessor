/*
 * @author Hangyu Wang (CWID: 10444246)
 * =============================================================================
 * This is a top level file for running the SQL Processor.
 * Here you need to input sereval information to run the program:
 * 1. For DataBase connection: USER, PWD, URL;
 * 2. For test list, if you want to use, prepare the *.sql and *.json.file 
 *    in the folder of inputFile;
 * 3. Also, you can input your own sql and json to get the generated code.
 * Further details like env and lib are in the file Processor.java.
 * =============================================================================
 * quick view of the format:
 * => SQL:
 *      select      YOUR_PROJECTED_OUTPUTS
 *      from        YOUR_TABLE (Here should be sales)
 *      where       YOUR_WHERE_CONDITION
 *      group by    YOUR_GROUPBY_CONDITION
 *      such that   YOUR_SUCHTHAT_CONDITION
 *      having      YOUR_HAVING_CONDITION
 * -----------------------------------------------------------------------------
 *      EXAMPLE:
 *      select prod, month, count(z.*)
 *      from sales
 *      where year = 2004
 *      group by prod, month; x, y, z
 *      such that x.prod = prod and x.month = month - 1,
 *                y.prod = prod and y.month = month + 1,
 *                z.prod = prod and z.month = month and z.quant > avg(x.quant) and z.quant < avg(y.quant)
 * =============================================================================
 * => JSON:
 *      {
 *          "Opt": [],
 *          "projAttrs": [ PROJECTED_OUTPUTS_STRING_TYPE ],
 *          "gAttrs": GROUP_ATTRIBUTES_SPLIT_BY_COMMA,
 *          "aggFuncs": [ AGG_TYPE__GROUP_VAR_ID__ATTR ],
 *          "numOfGVars": NUMBER_OF_GROUP_VARS,
 *          "condOfHaving": HAVING_CLAUSE,
 *          "condOfGVars": [ COND_GROUP_VARS ] => from 0 to NUMBER_OF_GROUP_VARS
 *      }
 *      PS: 1. the Opt is an array of Objects with the format:
 *              { 
 *                  "from": FROM_GROUP_VAR_ID,
 *                  "to":   TO_GROUP_VAR_ID
 *              },
 *              which means if you want the scan and update the MFstruct info 
 *              for "to", you need to scan and update the info "from" since "to"
 *              depends on "from". Just put [] if all variables are independent.
 *          2. the order in the JSON does not care, but all keys and values 
 *             should be filled properly.
 * -----------------------------------------------------------------------------
 *      EXAMPLE:
 *      {
 *          "Opt": [{ "from": 1, "to": 3 }, { "from": 2, "to": 3 }],
 *          "projAttrs": ["prod", "month", "count_3_star"],
 *          "gAttrs": "prod, month",
 *          "aggFuncs": [
 *              "sum_1_quant",
 *              "count_1_quant",
 *              "avg_1_quant",
 *              "sum_2_quant",
 *              "count_2_quant",
 *              "avg_2_quant",
 *              "count_3_star"
 *          ],
 *          "numOfGVars": 3,
 *          "condOfHaving": "_",
 *          "condOfGVars": [
 *              "if(rstm.getInt(\"year\")==2004)",
 *              "if(rstm.getInt(\"year\")==2004 && (rstm.getString(\"prod\").compareTo(curStruct.prod) == 0&&rstm.getInt(\"month\")==curStruct.month-1) )",
 *              "if(rstm.getInt(\"year\")==2004 && (rstm.getString(\"prod\").compareTo(curStruct.prod) == 0&&rstm.getInt(\"month\")==curStruct.month+1) )",
 *              "if(rstm.getInt(\"year\")==2004 && ((curStruct.avg_2_quant!=null&&curStruct.avg_1_quant!=null)?rstm.getString(\"prod\").compareTo(curStruct.prod) == 0&&rstm.getInt(\"month\")==curStruct.month&&rstm.getInt(\"quant\")>curStruct.avg_1_quant&&rstm.getInt(\"quant\")<curStruct.avg_2_quant:false) )"
 *          ]
 *      }
 */
package QueryProcessorForMFandEMF;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class CodeGenerator {
    public static void main(String[] args){
        
        /*
         * Here you need to update your connection info:
         * String USER = YOUR_USERNAME;
         * String PWD = YOUR_PWD;
         * String URL = YOUR_URL;
        */
        
        String USER = "postgres";
        String PWD = "m8kimmWhyholly";
        String URL = "jdbc:postgresql://localhost:5432/postgres";
        
        /*
         * Here you can use your own test here:
         * Only two types are supported SQL and JSON:
         * SQL: 
         *      1. Please ensure your input is correct;
         *      2. Please ensure your input has group by clause;
         *      3. Please ensure your claues are in lowercase;
         * JSON:
         *      1. Please input 6 ops and the opt info the optimization;
         *         if all valus are independent, please input [] instead 
         *         of null);
         *      2. The format will be shown when you run this part.
         *      3. More detials and example please refer to the *.json 
         *         in the folder of inputFiles;
         */
        
        List<String> SQLList = new ArrayList<String>(){{
            add("sql1.sql"); add("sql2.sql"); add("sql3.sql"); add("sql4.sql");
            add("sql5.sql"); add("sql6.sql");
        }};
        
        /*
         * instructions prepared.
        */
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
                + "Bye Bye! Thank you for using!\n"
                + "////////////////////////////////////////////////\n";
        String outputPath = "./src/inputFile/";
        Scanner input = new Scanner(System.in);
        while(loop){
            System.out.println();
            System.out.println(instruction);
            try{
                int ID = input.nextInt();
                /*
                 * case 1: Run the list and generate the code;
                 * case 2: You can input your own SQL and it will generate code;
                 * case 3: You can input your own JSON and it will generate code.
                */
                switch(ID){
                    case 0:
                        loop = false;
                        System.out.println(byeInstruction);                      
                        break;
                    case 1:
                        for(String sql: SQLList){
                            System.out.println("/////////////////Generating Code/////////////////");
                            System.out.println("// For " + sql + "");
                            Processor p = new Processor(USER, PWD, URL);

                            p.getTypeFromDB();
                            p.readInput(sql);
                            p.createFile();
                            p.writeStruct();
                            p.writeDBConnetionSetup();
                            p.firstScan();
                            p.otherScans();
                            p.printResult();

                            System.out.println("//////////////////////DONE!//////////////////////");
                        }
                        System.out.println("******************* All DEMO are generated *******************");
                        System.out.println("*********** Please go to outputFile folder to check ***********");
                        break;
                    case 2:
                        System.out.println(SQLInstruction);
                        Scanner sqlInput = new Scanner(System.in);
                        String res = "";
                        File fileSQL = new File(outputPath + "tempSQL" + ".sql");
                        PrintWriter outSQL = new PrintWriter(fileSQL);
                        System.out.println("/////////////////SQL starts/////////////////");
                        while(sqlInput.hasNextLine()){
//                            System.out.println("Here is the test");
                            String line = sqlInput.nextLine();
                            if(line.equals("END")){
                                break;
                            }
                            outSQL.println(line);
                            outSQL.flush();
                        }
                        System.out.println("/////////////////SQL ends/////////////////");
                        System.out.println("YOUR SQL FILE IS READY!(tempSQL.sql)");
                        outSQL.close();
                        Processor pTempSQL = new Processor(USER, PWD, URL);

                        pTempSQL.getTypeFromDB();
                        pTempSQL.readInput("tempSQL.sql");
                        pTempSQL.createFile();
                        pTempSQL.writeStruct();
                        pTempSQL.writeDBConnetionSetup();
                        pTempSQL.firstScan();
                        pTempSQL.otherScans();
                        pTempSQL.printResult();

                        System.out.println("******************* Your code is generated *******************");
                        System.out.println("*********** Please go to outputFile folder to check ***********");
                        break;
                    case 3:
                        System.out.println(JSONInstruction);
                        Scanner jsonInput = new Scanner(System.in);
                        File fileJSON = new File(outputPath + "tempJSON" + ".json");
                        PrintWriter outJSON = new PrintWriter(fileJSON);
                        System.out.println("/////////////////JSON starts/////////////////");
                        while(jsonInput.hasNextLine()){
                            String line = jsonInput.nextLine();
                            if(line.equals("END")){
                                break;
                            }
                            outJSON.println(line);
                            outJSON.flush();
                        }
                        System.out.println("/////////////////JSON ends/////////////////");
                        System.out.println("YOUR JSON FILE IS READY!(tempJSON.json)");
                        outJSON.close();
//                        System.out.println("For " + "tempJSON.json" + ":");
                        Processor pTempJSON = new Processor(USER, PWD, URL);

                        pTempJSON.getTypeFromDB();
                        pTempJSON.readInput("tempJSON.json");
                        pTempJSON.createFile();
                        pTempJSON.writeStruct();
                        pTempJSON.writeDBConnetionSetup();
                        pTempJSON.firstScan();
                        pTempJSON.otherScans();
                        pTempJSON.printResult();

                        System.out.println("******************* Your code is generated *******************");
                        System.out.println("*********** Please go to outputFile folder to check ***********");
                        break;
                }
            }catch(Exception e){
                System.out.println("////////////////////////Error////////////////////////");
                System.out.println("Please make sure you input the right instruction");
                System.out.println("/////////////////////////End/////////////////////////");
            } 
        }
        input.close();
    }
}
