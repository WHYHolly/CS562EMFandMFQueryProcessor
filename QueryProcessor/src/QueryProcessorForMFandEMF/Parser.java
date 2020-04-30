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
import utils.CONSTANTS;
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
            if(curString.length() == 1){
                res.append(curString);
            }else{
                res.append("curStruct." + curString);
                
            }
        }
        return str.contains("/")? "(double) " + res.toString(): res.toString();
    }
    
    
    public static void main(String[] args){
        System.out.println(formatExpWithAggFunc("sum_3_quant * sum_2_quant"));
    }
}
