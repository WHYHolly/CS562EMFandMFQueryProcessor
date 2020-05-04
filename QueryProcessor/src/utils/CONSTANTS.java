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
        put("bigint", "Long");
        put("numeric", "Double");
    }};
    public static final Map<String, String> typeToInitVal = new HashMap<String, String>() {{
        put("character varying", " = \"\"");
        put("character", " = \"\"");
        put("integer", " = null");
        put("bigint", " = null");
        put("numeric", " = null");
    }};
    
    public static final List<String> PQ_AGG_FUNCS = new ArrayList<String>() {{
        add("sum"); add("count"); add("max");
        add("min");add("avg");
    }};
    
    public static final Map<String, String> AGG_FUNCS_TO_TYPE = new HashMap<String, String>() {{
        put("sum", "bigint"); 
        put("count", "bigint"); 
        put("max", "integer");
        put("min", "integer");
        put("avg", "numeric");
    }};
    
    public static final List<String> OP_LIST = new ArrayList<String>() {{
        add("+"); add("-"); add("*"); add("/"); 
        add("%"); add("<"); add(">"); add("=");
        add("("); add(")");
    }};
    
    public static final List<String> CMP_OP_LIST = new ArrayList<String>() {{
       add("<"); add(">"); add("="); add("<>"); add(">="); add("<=");
    }};
    
    public static final Map<String, String> PRINT_MAP = new HashMap<String, String>() {{
        put("character varying", "System.out.printf(\"%-7s  \", ");
        put("character", "System.out.printf(\"%-7s  \", ");
        put("integer", "System.out.printf(\"%7s  \", ");
        put("bigint", "System.out.printf(\"%12s  \", ");
        put("numeric", "System.out.printf(\"%24.16f  \", ");
    }};
    
    public static final Map<String, String> PRINT_ATTR_MAP = new HashMap<String, String>() {{
        put("character varying", "System.out.printf(\"%-7s  \", \"");
        put("character", "System.out.printf(\"%-7s  \", \"");
        put("integer", "System.out.printf(\"%-7s  \", \"");
        put("bigint", "System.out.printf(\"%-12s  \", \"");
        put("numeric", "System.out.printf(\"%-24s  \", \"");
    }};

    public static final Map<String, String> PRINT_LAST_ATTR_MAP = new HashMap<String, String>() {{
        put("character varying", "System.out.printf(\"%-7s  \\n\", \"");
        put("character", "System.out.printf(\"%-7s  \\n\", \"");
        put("integer", "System.out.printf(\"%-7s  \\n\", \"");
        put("bigint", "System.out.printf(\"%-12s  \\n\", \"");
        put("numeric", "System.out.printf(\"%-24s  \\n\", \"");
    }};
}