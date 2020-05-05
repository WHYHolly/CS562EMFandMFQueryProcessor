/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package outputFile;

/**
 *
 * @author Hangyu Wang
 */
import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class GeneratedCodeForsql6{
    class MFStruct{
        String prod;
        Integer quant;
        Long count_1_prod;
        Long count_2_prod;
        MFStruct(){

                prod = "";
                quant = null;
                count_1_prod = 0L;
                count_2_prod = 0L;
        }
    }
    private static final String USER = "postgres";
    private static final String PWD = "m8kimmWhyholly";
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
//    private PreparedStatement ps = null;
    private Connection conn = null;
//    private ResultSet rs = null;
    
    /*
    * connect DB
    */
    public static void main(String[] args){
        GeneratedCodeForsql6 res = new GeneratedCodeForsql6();
        res.connect();
        res.retreive();
        res.close();
    }
    
    
    void connect(){
        try{
            Class.forName("org.postgresql.Driver");     //Loads the required driver
            System.out.println("Success loading Driver!");
        }catch(Exception exception){
            System.out.println("Fail loading Driver!");
            exception.printStackTrace();
        }
    }
    /*
    * disconnect DB
    */
    void close(){
        try{
//            if(rs != null) {
//                rs.close();
//            }
//            if (ps != null) {
//                ps.close();
//            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex) {
                ex.printStackTrace();
        }
    }
    
    void retreive(){
        try{
            Connection con = DriverManager.getConnection(URL, USER, PWD);    //connect to the database using the password and username
            System.out.println("Success connecting server!");
            ResultSet rstm = null;
            PreparedStatement pstm = null;
            String ret = "select * from sales";
            //resultset object gets the set of values retreived from the database
            boolean more;
            pstm = con.prepareStatement(ret);
            rstm = pstm.executeQuery(); 
            Map<String, MFStruct> structList = new HashMap<>(); //Set<String> keySet = new HashSet<>();
            
            //////
            more = rstm.next();
            
            while(more){
                if(true) {
                    String key = "" + rstm.getString("prod")+ rstm.getInt("quant");
                    if(!structList.containsKey(key)){
                        MFStruct newStrcut = new MFStruct();
                        newStrcut.prod = rstm.getString("prod");
                        newStrcut.quant = rstm.getInt("quant");
                        structList.put(key, newStrcut);
                        //keySet.add(key);
                    }
                    MFStruct curStruct = structList.get(key);
                    
                }
                more = rstm.next();
            }
///////////////Other Scan////////////
            int count = 1;
            for(int i = 1; i <= count; i++){
                for(MFStruct curStruct: structList.values()){
                    rstm = pstm.executeQuery(); 
                    more = rstm.next();
                    while(more){
                        switch(i){
                            case 1:
                                if(rstm.getString("prod").compareTo(curStruct.prod) == 0){
                                    curStruct.count_1_prod = curStruct.count_1_prod == null ? 1 : curStruct.count_1_prod + 1;
                                }
                                if(rstm.getString("prod").compareTo(curStruct.prod) == 0&&rstm.getInt("quant")<curStruct.quant){
                                    curStruct.count_2_prod = curStruct.count_2_prod == null ? 1 : curStruct.count_2_prod + 1;
                                }
                            break;
                        }
                        more = rstm.next();  
                    }   
                }
            }

            System.out.printf("%-7s  ", "prod");
            System.out.printf("%-7s  \n", "quant");
            for(MFStruct curStruct: structList.values()){
//                MFStruct curStruct = keyToStruct.get(key);
                if((curStruct.count_2_prod!=null&&curStruct.count_1_prod!=null)?curStruct.count_2_prod==curStruct.count_1_prod/2.0:false){
                    System.out.printf("%-7s  ", curStruct.prod);
                    System.out.printf("%7s  ", curStruct.quant);
                    System.out.println();
                }
//                System.out.println();
            }
        
        }catch(Exception exception){
            System.out.println("Something Wrong with the Retrieve!");
            exception.printStackTrace();
        }
    
    
    }
}
