/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryProcessorForMFandEMF;
import java.util.*;

/**
 *
 * @author Hangyu Wang
 */
public class CodeGenerator {
    public static void main(String[] args){
//     System.out.println("afd");
String USER = "postgres";
        String PWD = "m8kimmWhyholly";
        String URL = "jdbc:postgresql://localhost:5432/postgres";
        List<String> SQLList = new ArrayList<String>(){{
            add("sql1.sql"); add("sql2.sql"); add("sql3.sql"); add("sql4.sql");
            add("sql5.sql"); add("sql6.sql");
            
        }};
        
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

    }
    
}
