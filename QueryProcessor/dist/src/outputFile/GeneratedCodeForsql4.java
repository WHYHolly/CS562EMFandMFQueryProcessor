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

public class GeneratedCodeForsql4{
    class MFStruct{
        String prod;
        Integer month;
        Long sum_1_quant;
        Long count_1_quant;
        Double avg_1_quant;
        Long sum_2_quant;
        Long count_2_quant;
        Double avg_2_quant;
        Long count_3_star;
        MFStruct(){

                prod = "";
                month = null;
                sum_1_quant = null;
                count_1_quant = 0L;
                avg_1_quant = null;
                sum_2_quant = null;
                count_2_quant = 0L;
                avg_2_quant = null;
                count_3_star = 0L;
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
        GeneratedCodeForsql4 res = new GeneratedCodeForsql4();
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
                if(rstm.getInt("year")==2004) {
                    String key = "" + rstm.getString("prod")+ rstm.getInt("month");
                    if(!structList.containsKey(key)){
                        MFStruct newStrcut = new MFStruct();
                        newStrcut.prod = rstm.getString("prod");
                        newStrcut.month = rstm.getInt("month");
                        structList.put(key, newStrcut);

                    }
                    MFStruct curStruct = structList.get(key);
                    
                }
                more = rstm.next();
            }
            ///////////////Other Scan(s)////////////
            int count = 2;
            for(int i = 1; i <= count; i++){
                for(MFStruct curStruct: structList.values()){
                    rstm = pstm.executeQuery(); 
                    more = rstm.next();
                    while(more){
                        switch(i){
                            case 1:
                                if(rstm.getInt("year")==2004 && (rstm.getString("prod").compareTo(curStruct.prod) == 0&&rstm.getInt("month")==curStruct.month-1) ){
                                    curStruct.sum_1_quant = curStruct.sum_1_quant == null ? rstm.getInt("quant") : curStruct.sum_1_quant+rstm.getInt("quant");
                                    curStruct.count_1_quant = curStruct.count_1_quant == null ? 1 : curStruct.count_1_quant + 1;
                                    curStruct.avg_1_quant = (curStruct.sum_1_quant + 0.0)/curStruct.count_1_quant;
                                }
                                if(rstm.getInt("year")==2004 && (rstm.getString("prod").compareTo(curStruct.prod) == 0&&rstm.getInt("month")==curStruct.month+1) ){
                                    curStruct.sum_2_quant = curStruct.sum_2_quant == null ? rstm.getInt("quant") : curStruct.sum_2_quant+rstm.getInt("quant");
                                    curStruct.count_2_quant = curStruct.count_2_quant == null ? 1 : curStruct.count_2_quant + 1;
                                    curStruct.avg_2_quant = (curStruct.sum_2_quant + 0.0)/curStruct.count_2_quant;
                                }
                            break;
                            case 2:
                                if(rstm.getInt("year")==2004 && ((curStruct.avg_2_quant!=null&&curStruct.avg_1_quant!=null)?rstm.getString("prod").compareTo(curStruct.prod) == 0&&rstm.getInt("month")==curStruct.month&&rstm.getInt("quant")>curStruct.avg_1_quant&&rstm.getInt("quant")<curStruct.avg_2_quant:false) ){
                                    curStruct.count_3_star = curStruct.count_3_star == null ? 1 : curStruct.count_3_star + 1;
                                }
                            break;
                        }
                        more = rstm.next();  
                    }   
                }
            }

            ///////////////Print Out////////////
            System.out.printf("%-7s  ", "prod");
            System.out.printf("%-7s  ", "month");
            System.out.printf("%-12s  \n", "count_3_star");
            for(MFStruct curStruct: structList.values()){
                
                if(true){
                    System.out.printf("%-7s  ", curStruct.prod);
                    System.out.printf("%7s  ", curStruct.month);
                    System.out.printf("%12s  ", curStruct.count_3_star);
                    System.out.println();
                }
                 
            }
        
        }catch(Exception exception){
            System.out.println("Something Wrong with the Retrieve!");
            exception.printStackTrace();
        }
    
    
    }
}
