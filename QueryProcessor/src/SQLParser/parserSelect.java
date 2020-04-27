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
public class parserSelect extends partialParser{
    private List<String> attrs = new ArrayList<>();
    parserSelect(){
        super("select", null);
    }
    parserSelect(String sql){
        super("select", sql);
    }
    public void setSql(String sql){
        super.sql = sql;
    }
    
//    @Override
    public void parseClause(List<String> varToNum, Set<String> aggFuncs){
        for(String str: sql.split(",")){
            System.out.println(aggFuncs.size());
            attrs.add(expParser.parserAttr( str.trim().toLowerCase(),varToNum, aggFuncs));
        }
    }
//    @Override
    public List<String> getParsedClause(){
        return this.attrs;
    }
    
}
