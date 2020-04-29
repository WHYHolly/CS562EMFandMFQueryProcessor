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
public class Parser {
    public static String projAttrs(String exp){
//        if(exp.contains("+")|| exp.contains("-") 
////        || exp.contains("*") || exp.contains("/")){
////            exp
////            
////        }
//        String res = "";
//        String[] exp;
//        
//        return

//        String res = "";
        return "curStruct." +formatAggFunc(exp);
    }
    
    private static String formatAggFunc(String str){
        String[] carrier = str.split("_");
        if(carrier.length == 1){
            return carrier[0];
        }
        return carrier[0] + "_" + carrier[1] + "_" + carrier[2];
    }
    
    public static void main(String[] args){
        System.out.println(projAttrs("sum_3_quant"));
    }
}
