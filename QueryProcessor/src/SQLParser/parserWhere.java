/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLParser;

import java.util.*;

import utils.partialParser;
import utils.expParser;

/**
 *
 * @author Holly
 */
public class parserWhere extends partialParser{
    private List<String> parsedSql = new ArrayList<>();
    parserWhere(){
        super("where", null);
    }
    parserWhere(String sql){
        super("where", sql);
    }
    public void setSql(String sql){
//        System.out.println("WHERE");
//        System.out.println(sql);
        super.sql = sql;
    }
//    @Override
    public void parseClause(Map<String, String> attrToType, List<String> varToNum, Set<String> aggFuns){
//        System.out.println("WHERE");
//        System.out.println(sql);
        if(sql != null && sql.length() != 0){
//            System.out.println("WHERE");
//            System.out.println(sql);
            this.parsedSql.add(expParser.parserCondWhere(sql.trim(), attrToType));
        }
    }
//    @Override
    public List<String> getParsedClause(){
        return this.parsedSql;
    }
    
}
