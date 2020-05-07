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

    private Connection con = null;

    
    /*
     * main function
     */
    public static void main(String[] args){
        GeneratedCodeForsql6 res = new GeneratedCodeForsql6();
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
                    String key = "" + rstm.getString("prod")+ rstm.getInt("quant");
                    if(!structList.containsKey(key)){
                        MFStruct newStrcut = new MFStruct();
                        newStrcut.prod = rstm.getString("prod");
                        newStrcut.quant = rstm.getInt("quant");
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

            ///////////////Print Out////////////
            System.out.printf("%-7s  ", "prod");
            System.out.printf("%-7s  \n", "quant");
            for(MFStruct curStruct: structList.values()){
                
                if((curStruct.count_2_prod!=null&&curStruct.count_1_prod!=null)?curStruct.count_2_prod==curStruct.count_1_prod/2.0:false){
                    System.out.printf("%-7s  ", curStruct.prod);
                    System.out.printf("%7s  ", curStruct.quant);
                    System.out.println();
                }
                 
            }
        
        }catch(Exception exception){
            System.out.println("Something Wrong with the Retrieve!");
            exception.printStackTrace();
        }
    
    
    }
}
