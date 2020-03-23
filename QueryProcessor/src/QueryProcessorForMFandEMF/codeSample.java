/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryProcessorForMFandEMF;

/**
 *
 * @author Hangyu Wang
 */
import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class codeSample {
    
    class PhiStrcut{
        /*
        *  TODO: Here to generate by using Processor;
        */
    }

    private static final String USER ="postgres";
    private static final String PWD ="m8kimmWhyholly";
    private static final String URL ="jdbc:postgresql://localhost:5432/postgres";
    private PreparedStatement ps = null;
    private Connection conn = null;
    private ResultSet rs = null;
    
    /*
    * connect DB
    */

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
    * connect DB
    */
    
    void retreive(){
        try{
        
        }catch(Exception exception){
            System.out.println("Retrieve!");
            exception.printStackTrace();
        }
    
    
    }
    /*
    * disconnect DB
    */
    void close(){
        try{
            if(rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex) {
                ex.printStackTrace();
        }
    
    
    }
    
    public static void main(String[] args){
        codeSample res = new codeSample();
        res.connect();
        res.retreive();
        res.close();
    }
}
