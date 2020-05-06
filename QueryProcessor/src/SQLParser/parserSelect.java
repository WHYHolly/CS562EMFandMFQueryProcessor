/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is SQL Parser.
 * For select clause
 */
package SQLParser;

import java.util.*;
import utils.partialParser;
import utils.expParser;

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
    
    @Override
    public void parseClause(List<String> varToNum, Set<String> aggFuncs){
        for(String str: sql.split(",")){
            attrs.add(expParser.parserAttr(str.trim(), varToNum, aggFuncs));
        }
    }
    @Override
    public List<String> getParsedClause(){
        return this.attrs;
    }
    
}
