/*
 * @author Hangyu Wang (CWID: 10444246)
 * 
 * 
 * 
 */
package QueryProcessorForMFandEMF;

import java.util.*;
import utils.CONSTANTS;
import org.apache.commons.lang3.math.NumberUtils;

public class Parser {
    public static String projAttrs(String exp){
        return "curStruct." +formatAggFunc(exp);
    }
    
    private static String formatAggFunc(String str){
        String[] carrier = str.split("_");
        if(carrier.length == 1){
            return carrier[0];
        }
        return carrier[0] + "_" + carrier[1] + "_" + carrier[2];
    }
    
    public static String formatExpWithAggFunc(String str){
        String temp = str;
        for(String op: CONSTANTS.OP_LIST){
            temp = temp.replace(op, " " + op + " ");
        }
        String[] strArr = temp.split("\\s+");
        
        StringBuilder res = new StringBuilder();
        for(String exp: strArr){
            String curString = exp.trim();
            if(curString.length() == 1 || NumberUtils.isParsable(curString)){
                res.append(curString);
            }else{
                res.append("curStruct." + curString);
                
            }
        }
        return str.contains("/")? "(double) " + res.toString(): res.toString();
    }
//    
//    
//    public static void main(String[] args){
//        System.out.println(formatExpWithAggFunc("sum_0_quant"));
//    }
}
