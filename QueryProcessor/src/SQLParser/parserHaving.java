/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is SQL Parser.
 * For having clause
 */
package SQLParser;

import java.util.*;

import utils.expParser;
import utils.partialParser;

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
    @Override
    public void parseClause(Map<String, String> attrToType, List<String> varToNum, Set<String> aggFuns){
        Set<String> selfAggFuncs = new HashSet<>();
        if(sql != null && sql.length() != 0){
            this.parsedSql.add(expParser.parserCond(sql.trim(), attrToType, varToNum, aggFuns, new ArrayList<>(), selfAggFuncs));
            if(selfAggFuncs.size() != 0){
                StringBuilder tempPre = new StringBuilder();
                tempPre.append("(");
                for(String preCond: selfAggFuncs){
                    tempPre.append("curStruct.");
                    tempPre.append(preCond);
                    tempPre.append("!=null&&");
                }
                String preCondStr = tempPre.toString();
                preCondStr = preCondStr.substring(0, preCondStr.length() - 2);
                preCondStr = preCondStr + ")?" + parsedSql.get(parsedSql.size() - 1)+ ":false";
                parsedSql.set(parsedSql.size() - 1, preCondStr);
            }
        }
    }
    @Override
    public List<String> getParsedClause(){
        return this.parsedSql;
    }

}
