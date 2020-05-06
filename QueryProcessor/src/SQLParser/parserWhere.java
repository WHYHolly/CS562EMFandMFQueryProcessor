/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is SQL Parser.
 * For where clause
 */
package SQLParser;

import java.util.*;

import utils.partialParser;
import utils.expParser;

public class parserWhere extends partialParser{
    private List<String> parsedSql = new ArrayList<>();
    
    parserWhere(){
        super("where", null);
    }
    
    parserWhere(String sql){
        super("where", sql);
    }
    
    public void setSql(String sql){
        super.sql = sql;
    }
    
    @Override
    public void parseClause(Map<String, String> attrToType, List<String> varToNum, Set<String> aggFuns){
        if(sql != null && sql.length() != 0){
            this.parsedSql.add(expParser.parserCondWhere(sql.trim(), attrToType));
        }
    }
    
    @Override
    public List<String> getParsedClause(){
        return this.parsedSql;
    }
}
