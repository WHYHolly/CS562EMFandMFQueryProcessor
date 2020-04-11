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

public class codeSample {
    
    class MFStruct{
        /*
        *  TODO: Here to generate by using Processor;
        */
        
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
//                if(true){
//                    String key = "" + rstm.getString("prod") + rs.getInt("month");
                    if(!keySet.contains(key)){
                        MFStruct newStrcut = new MFStruct();
                        
                        structList.add(newStrcut);
                        keySet.add(key);
                    }
                    

            }
            
            for(int i = 1; i <= count; i++){
                for(MFStruct curStruct: structList){
                    rstm = pstm.executeQuery(); 
                    more = rstm.next();
                    while(more){
                        switch(i){
//              Here is the code to fill
                        }
                        more = rstm.next();  
                    }   
                }
            }
//          Here is the out put
            for(MFStruct curStruct: structList){
//                MFStruct curStruct = keyToStruct.get(key);

//                System.out.println();
            }
        
        }catch(Exception exception){
            System.out.println("Retrieve!");
            exception.printStackTrace();
        }
    
    
    }
}
