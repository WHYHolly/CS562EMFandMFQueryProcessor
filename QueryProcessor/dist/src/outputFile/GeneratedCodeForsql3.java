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

public class GeneratedCodeForsql3{
    class MFStruct{
        String cust;
        Integer month;
        Long sum_0_quant;
        Long count_0_quant;
        Double avg_0_quant;
        Long sum_1_quant;
        Long count_1_quant;
        Double avg_1_quant;
        Long sum_2_quant;
        Long count_2_quant;
        Double avg_2_quant;
        MFStruct(){

                cust = "";
                month = null;
                sum_0_quant = null;
                count_0_quant = 0L;
                avg_0_quant = null;
                sum_1_quant = null;
                count_1_quant = 0L;
                avg_1_quant = null;
                sum_2_quant = null;
                count_2_quant = 0L;
                avg_2_quant = null;
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
        GeneratedCodeForsql3 res = new GeneratedCodeForsql3();
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
                    String key = "" + rstm.getString("cust")+ rstm.getInt("month");
                    if(!structList.containsKey(key)){
                        MFStruct newStrcut = new MFStruct();
                        newStrcut.cust = rstm.getString("cust");
                        newStrcut.month = rstm.getInt("month");
                        structList.put(key, newStrcut);

                    }
                    MFStruct curStruct = structList.get(key);
                    curStruct.sum_0_quant = curStruct.sum_0_quant == null ? rstm.getInt("quant") : curStruct.sum_0_quant+rstm.getInt("quant");
                    curStruct.count_0_quant = curStruct.count_0_quant == null ? 1 : curStruct.count_0_quant + 1;
                    curStruct.avg_0_quant = (curStruct.sum_0_quant + 0.0)/curStruct.count_0_quant;
                    
                }
                more = rstm.next();
            }
            ///////////////Other Scan(s)////////////
            int count = 1;
            for(int i = 1; i <= count; i++){
                rstm = pstm.executeQuery(); 
                more = rstm.next();
                while(more){
                    for(MFStruct curStruct: structList.values()){
                        switch(i){
                            case 1:
                                if(rstm.getInt("year")==2004 && (rstm.getString("cust").compareTo(curStruct.cust) == 0&&rstm.getInt("month")<curStruct.month) ){
                                    curStruct.sum_1_quant = curStruct.sum_1_quant == null ? rstm.getInt("quant") : curStruct.sum_1_quant+rstm.getInt("quant");
                                    curStruct.count_1_quant = curStruct.count_1_quant == null ? 1 : curStruct.count_1_quant + 1;
                                    curStruct.avg_1_quant = (curStruct.sum_1_quant + 0.0)/curStruct.count_1_quant;
                                }
                                if(rstm.getInt("year")==2004 && (rstm.getString("cust").compareTo(curStruct.cust) == 0&&rstm.getInt("month")>curStruct.month) ){
                                    curStruct.sum_2_quant = curStruct.sum_2_quant == null ? rstm.getInt("quant") : curStruct.sum_2_quant+rstm.getInt("quant");
                                    curStruct.count_2_quant = curStruct.count_2_quant == null ? 1 : curStruct.count_2_quant + 1;
                                    curStruct.avg_2_quant = (curStruct.sum_2_quant + 0.0)/curStruct.count_2_quant;
                                }
                            break;
                        }
                    }   
                    more = rstm.next(); 
                }
            }

            ///////////////Print Out////////////
            System.out.printf("%-7s  ", "cust");
            System.out.printf("%-7s  ", "month");
            System.out.printf("%-24s  ", "avg_1_quant");
            System.out.printf("%-24s  ", "avg_0_quant");
            System.out.printf("%-24s  \n", "avg_2_quant");
            for(MFStruct curStruct: structList.values()){
                
                if(true){
                    System.out.printf("%-7s  ", curStruct.cust);
                    System.out.printf("%7s  ", curStruct.month);
                    System.out.printf("%24.16f  ", curStruct.avg_1_quant);
                    System.out.printf("%24.16f  ", curStruct.avg_0_quant);
                    System.out.printf("%24.16f  ", curStruct.avg_2_quant);
                    System.out.println();
                }
                 
            }
        
        }catch(Exception exception){
            System.out.println("Something Wrong with the Retrieve!");
            exception.printStackTrace();
        }
    
    
    }
}
