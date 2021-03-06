/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is a Parser for the SQL PARSER
 * Various type of parsers in this file
 */
package utils;

import java.util.*;
import utils.CONSTANTS;
import utils.Group;
import org.apache.commons.lang3.math.NumberUtils;
public class expParser{
    
    public static String parserAttr(String attr, List<String> varToNum, Set<String> aggFuncs){
        String temp = attr;
        for(String op: CONSTANTS.OP_LIST){
            temp = temp.replace(op, " " + op + " ");
        }
        String[] strArr = temp.split("\\s+");
        Stack<String> tempExp = new Stack();
        for(int i = 0; i < strArr.length;i++){
            String str = strArr[i];
            if(CONSTANTS.PQ_AGG_FUNCS.contains(str)){
                String para = strArr[i + 2];
                if(para.contains(".")){
                    if(para.endsWith(".")){
                        String[] arr = para.split("\\.");
                        aggFuncs.add(str + "_" + (varToNum.indexOf(arr[0]) + 1) + "_" + (strArr[i + 3].equals("*")? "star": strArr[i + 3]));
                        tempExp.push(str + "_" + (varToNum.indexOf(arr[0]) + 1) + "_" + (strArr[i + 3].equals("*")? "star": strArr[i + 3]));
                        i++;
                    }else{
                        String[] arr = para.split("\\.");
                        aggFuncs.add(str + "_" + (varToNum.indexOf(arr[0]) + 1) + "_" + (arr[1].equals("*")? "star": arr[1]));
                        tempExp.push(str + "_" + (varToNum.indexOf(arr[0]) + 1) + "_" + (arr[1].equals("*")? "star": arr[1]));
                    }
                }else{
                    aggFuncs.add(str + "_" + 0 + "_" + (para.equals("*")? "star": para));
                    tempExp.push(str + "_" + 0 + "_" + (para.equals("*")? "star": para));
                }                
                i = i + 3;
            }else{
                tempExp.push(str);
            }
        }
        return stackToString(tempExp);
    }
    
    
    public static String parserCondWhere(String cond, Map<String, String> attrToType){
        String temp = cond;
        for(String op: CONSTANTS.OP_LIST){
            temp = temp.replace(op, " " + op + " ");
        }

        Stack<String> tempExp = new Stack();

        String[] strArr = temp.split("\\s+");
        for(int i = 0; i < strArr.length; i++){
            String str = strArr[i];

            if(str.length() == 0){
                continue;
            }else if(str.equals("and") || str.equals("or")){
                switch(str) {
                    case "and":
                        tempExp.push("&&");
                        break;
                    case "or":
                        tempExp.push("||");
                        break;
                }
            }else{
                if(!CONSTANTS.OP_LIST.contains(str)){
                    if(NumberUtils.isParsable(str) || (str.startsWith("'") && str.endsWith("'"))){
                        tempExp.push(str);
                        if(str.startsWith("'") && str.endsWith("'")){
                            StringCmb(tempExp);
                        }
                        
                    }else if(str.equals("<") || str.equals("=") || str.equals(">")){
                        if(!tempExp.isEmpty() && CONSTANTS.OP_LIST.contains(tempExp.peek()) ){
                            tempExp.push(tempExp.pop() + str);
                        }
                        tempExp.push(str);
                    }else{
                        if(CONSTANTS.dbTypeToJavaType.get(attrToType.get(str)).equals("String")){
                            tempExp.push("rstm.getString(\"" + str +"\")");
                        }else{
                            tempExp.push("rstm.getInt(\"" + str +"\")");
                        }
                    }
                }else{
                    tempExp.push(str);
                }
            }
        }
        return stackToString(tempExp);
    }
    
    public static String parserCond(String cond, Map<String, String> attrToType, List<String> varToNum, Set<String> aggFuns, List<Integer> fromList, Set<String> selfAggFuncs){
        String temp = cond;
//        System.out.println(cond);
        for(String op: CONSTANTS.OP_LIST){
            temp = temp.replace(op, " " + op + " ");
        }

        Stack<String> tempExp = new Stack();

        String[] strArr = temp.split("\\s+");
        for(int i = 0; i < strArr.length;i++){
            String str = strArr[i];

            if(str.length() == 0){
                continue;
            }else if(str.equals("and") || str.equals("or")){
                switch(str) {
                    case "and":
                        tempExp.push("&&");
                        break;
                    case "or":
                        tempExp.push("||");
                        break;
                }
            }else{
                if(!CONSTANTS.OP_LIST.contains(str)){
                    if(NumberUtils.isParsable(str) || (str.startsWith("'") && str.endsWith("'"))){
                        tempExp.push(str);
                        if(str.startsWith("'") && str.endsWith("'")){
                            StringCmb(tempExp);
                        }
                    }else if(str.equals("<") || str.equals("=") || str.equals(">")){
                        if(!tempExp.isEmpty() && CONSTANTS.OP_LIST.contains(tempExp.peek()) ){
                            tempExp.push(tempExp.pop() + str);
                        }
                        tempExp.push(str);
                    }else{
                        if(str.contains(".")){
                            String[] para = strArr[i].split("\\.");
                            if(CONSTANTS.dbTypeToJavaType.get(attrToType.get(para[1])).equals("String")){
                                if(!tempExp.isEmpty() && CONSTANTS.CMP_OP_LIST.contains(tempExp.peek())){
                                    tempExp.push("rstm.getString(\"" + para[1] +"\")");
                                    StringCmb(tempExp);
                                }else{
                                    tempExp.push("rstm.getString(\"" + para[1] +"\")");
                                }
                                
                            }else{
                                tempExp.push("rstm.getInt(\"" + para[1] +"\")");
                            }
                            
                        }else if(CONSTANTS.PQ_AGG_FUNCS.contains(str)){
                            String DBType = "";
                            String op = "";
                            if(strArr[i + 2].contains(".")){
                                if(strArr[i + 2].endsWith(".")){
                                    String[] para = strArr[i + 2].split("\\.");
                                    fromList.add((varToNum.indexOf(para[0]) + 1));
                                    aggFuns.add(str+"_"+ (varToNum.indexOf(para[0]) + 1)+ "_" + (strArr[i + 3].equals("*") ? "star": strArr[i + 3]));
       
                                    selfAggFuncs.add(str+"_"+ (varToNum.indexOf(para[0]) + 1)+ "_" + (strArr[i + 3].equals("*") ? "star": strArr[i + 3]));
 
                                    if(!tempExp.isEmpty()){
                                        op = tempExp.peek();
                                    }
                                    tempExp.push("curStruct." + str+"_"+ (varToNum.indexOf(para[0]) + 1)+ "_" + (strArr[i + 3].equals("*") ? "star": strArr[i + 3]));
                                    DBType = strArr[i + 3];
                                    i++;
                                }else{
                                    String[] para = strArr[i + 2].split("\\.");
                                    fromList.add((varToNum.indexOf(para[0]) + 1));
                                    aggFuns.add(str+"_"+ (varToNum.indexOf(para[0]) + 1)+ "_" + (para[1].equals("*") ? "star": para[1]));
                                    selfAggFuncs.add(str+"_"+ (varToNum.indexOf(para[0]) + 1)+ "_" + (para[1].equals("*") ? "star": para[1]));
                                    if(!tempExp.isEmpty()){
                                        op = tempExp.peek();
                                    }
                                    tempExp.push("curStruct." + str+"_"+ (varToNum.indexOf(para[0]) + 1)+ "_" + (para[1].equals("*") ? "star": para[1]));
                                    DBType = para[1];
                                
                                }

                            }else{
                                fromList.add(0);
                                aggFuns.add(str + "_0_" + (strArr[i + 2].equals("*") ? "star": strArr[i + 2]));
                                selfAggFuncs.add(str + "_0_" + (strArr[i + 2].equals("*") ? "star": strArr[i + 2]));
                                if(!tempExp.isEmpty()){
                                    op = tempExp.peek();
                                }
                                tempExp.push("curStruct." + str+"_0_" + (strArr[i + 2].equals("*") ? "star": strArr[i + 2]));
                                DBType = strArr[i + 2];
                            }
//                            if( !DBType.equals("*") && CONSTANTS.dbTypeToJavaType.get(attrToType.get(DBType)).equals("String") 
//                                && !tempExp.isEmpty() && CONSTANTS.CMP_OP_LIST.contains(op)){
//                            }
                            i = i + 3;
                            
                        }else{
                            
                            if(CONSTANTS.dbTypeToJavaType.get(attrToType.get(str)).equals("String") 
                               && !tempExp.isEmpty() && CONSTANTS.CMP_OP_LIST.contains(tempExp.peek())){
                                tempExp.push("curStruct." + str);
                                StringCmb(tempExp);
                            }else{
                                tempExp.push("curStruct." + str);
                            }
                        }
                    }
                }else{
                    tempExp.push(str);
                }
            }
        }
        return stackToString(tempExp);
    }
    
    static void StringCmb(Stack<String> stack){
        if(!stack.isEmpty()){
            String right = stack.pop();
            
            String op = "";
            while(CONSTANTS.OP_LIST.contains(stack.peek())){
                op = stack.pop() + op;
            }
            String left = stack.pop();
            if(right.startsWith("'") && right.endsWith("'")){
                right = "\"" + right.substring(1, right.length()-1) + "\"";
            }
            if(left.startsWith("'") && left.endsWith("'")){
                left = "\"" + left.substring(1, left.length()-1) + "\"";
            }
            switch(op){
                case "=":
                    stack.push(left + ".compareTo(" + right + ") == 0");
                    break;
                case "<":
                    stack.push(left + ".compareTo(" + right + ") < 0");
                    break;
                case ">":
                    stack.push(left + ".compareTo(" + right + ") > 0");
                    break;
                case "<=":
                    stack.push(left + ".compareTo(" + right + ") <= 0");
                    break;
                case ">=":
                    stack.push(left + ".compareTo(" + right + ") >= 0");
                    break;
                case "<>":
                    stack.push(left + ".compareTo(" + right + ") != 0");
                    break;
            
            }
        }
    }
    
    public static List<String> parserFuncs(Set<String> set){

        List<Group> help = new ArrayList<>();
        List<String> res = new ArrayList<>();

        for(String func: set){
            String[] para = func.split("_");
            if(para[0].equals("avg")){
                if(!set.contains("sum"+"_"+para[1]+"_"+para[2])){
                    help.add(new Group(para[1], "sum", para[2]));
                }
                if(!set.contains("count"+"_"+para[1]+"_"+para[2])){
                    help.add(new Group(para[1], "count", para[2]));
                }
            }
            help.add(new Group(para[1], para[0], para[2]));
        }
        help.sort((g1, g2) ->  Integer.parseInt(g1.sub) == Integer.parseInt(g2.sub)
                                ? (CONSTANTS.PQ_AGG_FUNCS.indexOf(g1.aggType) - CONSTANTS.PQ_AGG_FUNCS.indexOf(g2.aggType))
                                : Integer.parseInt(g1.sub) - Integer.parseInt(g2.sub));
        for(Group g: help){
            res.add(g.aggType+"_"+g.sub+"_"+g.attr);
        }
        return res;
    }
    
     
    static String stackToString(Stack<String> stack){
        StringBuilder res = new StringBuilder();
        while(!stack.isEmpty()){
            StringBuilder ops = new StringBuilder();
            while(CONSTANTS.OP_LIST.contains(stack.peek())){
                ops.insert(0, stack.pop());
            }
            
            if(ops.length() != 0){
                String opStr = ops.toString();
                switch(opStr) {
                    case "=":
                        res.insert(0, "==");
                        break;
                    case "<>":
                        res.insert(0, "!=");
                        break;
                    default:
                        res.insert(0, opStr);
                        break;
                }
            }else{
                res.insert(0, stack.pop());
            }

        }
        return res.toString();
    }

}