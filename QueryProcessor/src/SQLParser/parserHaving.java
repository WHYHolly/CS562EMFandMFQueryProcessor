/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLParser;

import java.util.*;

import utils.expParser;
import utils.partialParser;

/**
 *
 * @author Holly
 */
public class parserHaving extends partialParser{
    private List<String> parsedSql = new ArrayList<>();
    parserHaving(){
        super("where", null);
    }
    parserHaving(String sql){
        super("where", sql);
    }
    public void setSql(String sql){
        super.sql = sql;
    }
//    @Override
    public void parseClause(Map<String, String> attrToType, List<String> varToNum, Set<String> aggFuns){
        if(sql != null && sql.length() != 0){
            this.parsedSql.add(expParser.parserCond(sql.trim(), attrToType, varToNum, aggFuns, new ArrayList<>()));
        }
    }
//    @Override
    public List<String> getParsedClause(){
        return this.parsedSql;
    }

}
