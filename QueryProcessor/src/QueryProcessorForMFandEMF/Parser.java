/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is a easy parser for processor to parse the info in json to six ops
 */
package QueryProcessorForMFandEMF;

import java.util.*;
import utils.CONSTANTS;
import org.apache.commons.lang3.math.NumberUtils;

public class Parser {
    /*
     * add the curStruct. to fit the code.
     */
    public static String projAttrs(String exp){
        return "curStruct." +formatAggFunc(exp);
    }
    /*
     * Distinguish whether the string is a agg funsction or common attributes 
     */
    private static String formatAggFunc(String str){
        String[] carrier = str.split("_");
        if(carrier.length == 1){
            return carrier[0];
        }
        return carrier[0] + "_" + carrier[1] + "_" + carrier[2];
    }
    /*
     * parse the output with expressions
     */
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
    
}
