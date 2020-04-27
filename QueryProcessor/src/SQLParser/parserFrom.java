/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLParser;

/**
 *
 * @author Holly
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import utils.partialParser;
public class parserFrom extends partialParser{

    private PreparedStatement ps = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private static Map<String, String> nameToType = new HashMap<>();
    parserFrom(){
        super("from", null);
    }
    parserFrom(String sql){
        super("from", sql);
    }
    
    public void setSql(String sql){
        super.sql = sql;
    }
//    @Override
    public void parseClause(String USER, String PWD, String URL){
        try{
            conn = DriverManager.getConnection(URL, USER, PWD);
            ps = conn.prepareStatement("select * from Information_schema.columns where table_name = " + "\'" + sql.trim() + "\'"); 
            System.out.println("select * from Information_schema.columns where table_name = " + "\'" + sql + "\'");
            rs = ps.executeQuery();

            while(rs.next()){
               
                nameToType.put(rs.getString("column_name"), rs.getString("data_type"));
            }

            conn.close();
        }catch(Exception exception){
            System.out.println("Retrieve!");
            exception.printStackTrace();
        }
    
    }

    public Map<String, String> getParsedClause(){
        return nameToType;
    }
    
//    public static void main(String[] args){
//        parserFrom parser = new parserFrom("sales");
//        String USER ="postgres";
//        String PWD ="m8kimmWhyholly";
//        String URL ="jdbc:postgresql://localhost:5432/postgres";
//        parser.parseClause(USER, PWD, URL);
//        System.out.println(parser.getParsedClause().toString());
//    }
}
