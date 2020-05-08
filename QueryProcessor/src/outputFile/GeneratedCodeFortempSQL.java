/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is the generated code.
 * This should be runnable if your input is right.
 */
package outputFile;

/**
 * 
 * 
 */
import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class GeneratedCodeFortempSQL{
    class MFStruct{
        String cust;
        String prod;
        Integer month;
        Integer quant;
        Integer day;
        Integer year;
        String state;
        MFStruct(){

                cust = "";
                prod = "";
                month = null;
                quant = null;
                day = null;
                year = null;
                state = "";
        }
    }
    private static final String USER = "postgres";
    private static final String PWD = "m8kimmWhyholly";
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";

    private Connection con = null;

    
    /*
     * main function
     */
    public static void main(String[] args){
        GeneratedCodeFortempSQL res = new GeneratedCodeFortempSQL();
        res.connect();
        res.retreive();
        res.close();
    }

    /*
     * connect DB
     */
    void connect(){
        try{
            con = DriverManager.getConnection(URL, USER, PWD);

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

            if (con != null) {
                con.close();
            }


        }catch(Exception e){
                e.printStackTrace();
        }
    }
    
    void retreive(){
        try{
            
           
            ResultSet rstm = null;
            PreparedStatement pstm = null;
            String ret = "select * from sales";
            ///////////////First Scan////////////
            
            boolean more;
            pstm = con.prepareStatement(ret);
            rstm = pstm.executeQuery(); 
            Map<String, MFStruct> structList = new HashMap<>();
            

            more = rstm.next();
            
            while(more){
                if(true) {
                    String key = "" + rstm.getString("cust")+ rstm.getString("prod")+ rstm.getInt("month")+ rstm.getInt("quant")+ rstm.getInt("day")+ rstm.getInt("year")+ rstm.getString("state");
                    if(!structList.containsKey(key)){
                        MFStruct newStrcut = new MFStruct();
                        newStrcut.cust = rstm.getString("cust");
                        newStrcut.prod = rstm.getString("prod");
                        newStrcut.month = rstm.getInt("month");
                        newStrcut.quant = rstm.getInt("quant");
                        newStrcut.day = rstm.getInt("day");
                        newStrcut.year = rstm.getInt("year");
                        newStrcut.state = rstm.getString("state");
                        structList.put(key, newStrcut);

                    }
                    MFStruct curStruct = structList.get(key);
                    
                }
                more = rstm.next();
            }
            ///////////////Other Scan(s)////////////
            int count = 1;
            for(int i = 1; i <= count; i++){
                for(MFStruct curStruct: structList.values()){
                    rstm = pstm.executeQuery(); 
                    more = rstm.next();
                    while(more){
                        switch(i){
                            case 1:
                            break;
                        }
                        more = rstm.next();  
                    }   
                }
            }

            ///////////////Print Out////////////
            System.out.printf("%-7s  ", "cust");
            System.out.printf("%-7s  ", "prod");
            System.out.printf("%-7s  ", "month");
            System.out.printf("%-7s  ", "quant");
            System.out.printf("%-7s  ", "day");
            System.out.printf("%-7s  ", "year");
            System.out.printf("%-7s  \n", "state");
            for(MFStruct curStruct: structList.values()){
                
                if(true){
                    System.out.printf("%-7s  ", curStruct.cust);
                    System.out.printf("%-7s  ", curStruct.prod);
                    System.out.printf("%7s  ", curStruct.month);
                    System.out.printf("%7s  ", curStruct.quant);
                    System.out.printf("%7s  ", curStruct.day);
                    System.out.printf("%7s  ", curStruct.year);
                    System.out.printf("%-7s  ", curStruct.state);
                    System.out.println();
                }
                 
            }
        
        }catch(Exception exception){
            System.out.println("Something Wrong with the Retrieve!");
            exception.printStackTrace();
        }
    
    
    }
}
