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

public class GeneratedCodeForinput1{
    class MFStruct{
        String cust;
        String prod;
        Integer sum_1_quant;
        Integer cnt_1_quant;
        Integer avg_1_quant;
        Integer sum_2_quant;
        Integer sum_3_quant;
        Integer cnt_3_quant;
        Integer avg_3_quant;
        MFStruct(){

                cust = "";
                prod = "";
                sum_1_quant = null;
                cnt_1_quant = null;
                avg_1_quant = null;
                sum_2_quant = null;
                sum_3_quant = null;
                cnt_3_quant = null;
                avg_3_quant = null;
        }
    }
    private static final String USER ="postgres";
    private static final String PWD ="m8kimmWhyholly";
    private static final String URL ="jdbc:postgresql://localhost:5432/postgres";
//    private PreparedStatement ps = null;
    private Connection conn = null;
//    private ResultSet rs = null;
    
    /*
    * connect DB
    */
    public static void main(String[] args){
        codeSample res = new codeSample();
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
            List<MFStruct> structList = new ArrayList<>(); Set<String> keySet = new HashSet<>();
            
            //////
            more = rstm.next();
            
            while(more){
                if(true) {
                    String key = "" + rstm.getString("cust")+ rstm.getString("prod");
                    if(!keySet.contains(key)){
                        MFStruct newStrcut = new MFStruct();
                        newStrcut.cust = rstm.getString("cust");
                        newStrcut.prod = rstm.getString("prod");
                        structList.add(newStrcut);
                        keySet.add(key);
                    }
                }
                more = rstm.next();
            }
///////////////Other Scan////////////
            int count = 2;
            for(int i = 1; i <= count; i++){
                for(MFStruct curStruct: structList){
                    rstm = pstm.executeQuery(); 
                    more = rstm.next();
                    while(more){
                        switch(i){
                            case 1:
                                if(rstm.getString("state").equals("NY") && curStruct.cust.equals(rstm.getString("cust"))){
                                    curStruct.sum_1_quant = curStruct.sum_1_quant == null ? rstm.getInt("quant") : curStruct.sum_1_quant+rstm.getInt("quant");
                                    curStruct.cnt_1_quant = curStruct.cnt_1_quant == null ? 1 : curStruct.cnt_1_quant + 1;
                                    curStruct.avg_1_quant = curStruct.sum_1_quant/curStruct.cnt_1_quant;
                                }
                                if(rstm.getString("state").equals("CT") && curStruct.cust.equals(rstm.getString("cust"))){
                                    curStruct.sum_3_quant = curStruct.sum_3_quant == null ? rstm.getInt("quant") : curStruct.sum_3_quant+rstm.getInt("quant");
                                    curStruct.cnt_3_quant = curStruct.cnt_3_quant == null ? 1 : curStruct.cnt_3_quant + 1;
                                    curStruct.avg_3_quant = curStruct.sum_3_quant/curStruct.cnt_3_quant;
                                }
                            break;
                            case 2:
                                if(rstm.getString("state").equals("NJ") && curStruct.cust.equals(rstm.getString("cust"))){
                                    curStruct.sum_2_quant = curStruct.sum_2_quant == null ? rstm.getInt("quant") : curStruct.sum_2_quant+rstm.getInt("quant");
                                }
                            break;
                        }
                        more = rstm.next();  
                    }   
                }
            }

            System.out.printf("%-7s  ", "cust");
            System.out.printf("%-7s  ", "prod");
            System.out.printf("%-7s  ", "1_sum_quant");
            System.out.printf("%-7s  ", "2_sum_quant");
            System.out.printf("%-7s  \n", "3_sum_quant");
            for(MFStruct curStruct: structList){
//                MFStruct curStruct = keyToStruct.get(key);
                if(curStruct.sum_1_quant > 2 * curStruct.sum_2_quant || curStruct.avg_1_quant > curStruct.avg_3_quant){
                    System.out.printf("%-7s  ", curStruct.cust);
                    System.out.printf("%-7s  ", curStruct.prod);
                    System.out.printf("%-7s  ", curStruct.sum_1_quant);
                    System.out.printf("%-7s  ", curStruct.sum_2_quant);
                    System.out.printf("%-7s  ", curStruct.sum_3_quant);
                    System.out.println();
                }
//                System.out.println();
            }
        
        }catch(Exception exception){
            System.out.println("Retrieve!");
            exception.printStackTrace();
        }
    
    
    }
}
