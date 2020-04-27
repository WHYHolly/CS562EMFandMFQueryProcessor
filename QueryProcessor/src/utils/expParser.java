/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Holly
 */
import java.util.*;
import utils.CONSTANTS;
import utils.Group;
import org.apache.commons.lang3.math.NumberUtils;
public class expParser{
//    private static final List<String> opList = new ArrayList<String>() {{
//        add("+"); add("-"); add("*"); add("/"); 
//        add("%"); add("<"); add(">"); add("=");
//        add("("); add(")");
//    }};
//    private static final List<String> aggList = new ArrayList<String>() {{
//        add("sum"); add("count"); add("min"); add("max"); add("avg"); 
//    }};
    
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
//                System.out.println(para);
                if(para.contains(".")){
                    String[] arr = para.split("\\.");
//                    System.out.println(arr[1]);
//                    System.out.println("Here the set is: " + aggFuncs);
//                    System.out.println(varToNum.indexOf(arr[0]) + 1);
                    aggFuncs.add(str + "_" + (varToNum.indexOf(arr[0]) + 1) + "_" + arr[1]);
                    tempExp.push(str + "_" + (varToNum.indexOf(arr[0]) + 1) + "_" + arr[1]);
                }else{
                    aggFuncs.add(str + "_" + 0 + "_" + para);
                    tempExp.push(str + "_" + 0 + "_" + para);
                }                
                i = i + 3;
            }else{
                tempExp.push(str);
            }
        }
        return stackToString(tempExp);
    }
    
    public static String parserCond(String cond, Map<String, String> attrToType, List<String> varToNum, Set<String> aggFuns, List<Integer> fromList){
        String temp = cond;
        for(String op: CONSTANTS.OP_LIST){
            temp = temp.replace(op, " " + op + " ");
        }
//        System.out.println("\\\\\\\\"+temp);
        
//        StringBuilder res = new StringBuilder();
        Stack<String> tempExp = new Stack();

        String[] strArr = temp.split("\\s+");
        for(int i = 0; i < strArr.length;i++){
            String str = strArr[i];
//            System.out.println("Now the: " + str+" end");
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
//                            System.out.println(Arrays.toString(para));
//                            System.out.println(attrToType.get(para[1]));
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
                                String[] para = strArr[i + 2].split("\\.");
//                                System.out.println(str+"_"+ para[0]+ "_" + para[1]);
//                                System.out.println(varToNum);
                                fromList.add((varToNum.indexOf(para[0]) + 1));
                                aggFuns.add(str+"_"+ (varToNum.indexOf(para[0]) + 1)+ "_" + para[1]);
//                                System.out.println(str+"_"+ para[0]+ "_" + para[1]);
                                if(!tempExp.isEmpty()){
                                    op = tempExp.peek();
                                }
                                tempExp.push("curStruct." + str+"_"+ (varToNum.indexOf(para[0]) + 1)+ "_" + para[1]);
                                DBType = para[1];
                            }else{
                                fromList.add(0);
                                aggFuns.add(str+"_0_"+strArr[i + 2]);
                                if(!tempExp.isEmpty()){
                                    op = tempExp.peek();
                                }
                                tempExp.push("curStruct." + str+"_0_"+strArr[i + 2]);
                                DBType = strArr[i + 2];
                            }
                            if(CONSTANTS.dbTypeToJavaType.get(attrToType.get(DBType)).equals("String") 
                                && !tempExp.isEmpty() && CONSTANTS.CMP_OP_LIST.contains(op)){
                                StringCmb(tempExp);
                            }
                            i = i + 3;
                            
                        }else{
//                            System.out.println("Here in the parser");
//                            System.out.println(CONSTANTS.dbTypeToJavaType.get(attrToType.get(str)).equals("String") + "______"+str);
//                            System.out.println( !tempExp.isEmpty() +"______"+tempExp.peek());
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
            System.out.println();
        }
        return stackToString(tempExp);
    }
    static void StringCmb(Stack<String> stack){
        if(!stack.isEmpty()){
            String right = stack.pop();
            String op = stack.pop();
            String left = stack.pop();
//            if()
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
//        public Group(String sub, String aggType, String attr){
//        this.sub = sub;
//        this.aggType = aggType;
//        this.attr = attr;
//    }
        List<Group> help = new ArrayList<>();
        List<String> res = new ArrayList<>();
//        for(String){
//        
//        }
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
                res.insert(0, stack.peek().equals("=")
                                            ? stack.pop() + "="
                                            :  stack.pop());
        }
        return res.toString();
    }
    public static void main(String[] args){
//       String sql = "(x.state=\"NY\") and (x.state = state)";
//       List<String> varToNum = new ArrayList<>();
//       varToNum.add("x");varToNum.add("y");
//       System.out.println(parserAttr("2*avg(quant)", varToNum));
//       parserSuchThat parser = new parserSuchThat(sql);
//       parser.parseClause();
//       
//       System.out.println(parser);
    }
}