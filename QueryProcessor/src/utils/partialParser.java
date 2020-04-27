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
import org.json.JSONArray;
public class partialParser{
    public String clause;
    public String sql;
    public partialParser(String clause, String sql){
        this.clause = clause;
        this.sql = sql;
    }
    
    public void setSql(String sql){};
    
    public void parseClause(){};
    public void parseClause(List<String> varToNum){};
    public void parseClause(List<String> varToNum, Set<String> aggFuncs){};
    public void parseClause(String USER, String PWD, String URL){};
    public void parseClause(Map<String, String> attrToType, List<String> varToNum){};
    public void parseClause(Map<String, String> attrToType, List<String> varToNum, Set<String> aggFuns){};
    
    
    public Object getParsedClause(){
        return new HashMap<String, String>();
    };
    public int getNumofG(){
        return -1;
    }
    public String getGAttributes(){
        return null;
    }
    
    public JSONArray getGraph(){
        return null;
    }
}