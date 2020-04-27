/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.*;
//import java.util.Map;

/**
 *
 * @author Holly
 */
public class CONSTANTS{
//    public ;
    public static final Map<String, String> dbTypeToJavaType = new HashMap<String, String>() {{
        put("character varying", "String");
        put("character", "String");
        put("integer", "Integer");
    }};
    public static final Map<String, String> typeToInitVal = new HashMap<String, String>() {{
        put("character varying", " = \"\"");
        put("character", " = \"\"");
        put("integer", " = null");
    }};
    
    public static final List<String> PQ_AGG_FUNCS = new ArrayList<String>() {{
        add("sum"); add("count"); add("max");
        add("min");add("avg");
    }};
    
    public static final List<String> OP_LIST = new ArrayList<String>() {{
        add("+"); add("-"); add("*"); add("/"); 
        add("%"); add("<"); add(">"); add("=");
        add("("); add(")");
    }};
    
    public static final List<String> CMP_OP_LIST = new ArrayList<String>() {{
       add("<"); add(">"); add("="); add("<>"); add(">="); add("<=");
    }};
}