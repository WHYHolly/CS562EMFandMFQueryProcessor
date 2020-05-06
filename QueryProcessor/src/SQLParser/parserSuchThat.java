/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is SQL Parser.
 * For suth that clause
 */
package SQLParser;

import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.partialParser;
import org.apache.commons.lang3.math.NumberUtils; 
import utils.expParser;
import org.json.JSONArray;
import org.json.JSONObject;

public class parserSuchThat extends partialParser{

    private List<String> condsList = new ArrayList<>();
    private JSONArray graph = new JSONArray();

    parserSuchThat(){
        super("suchthat", null);
    }
    parserSuchThat(String sql){
        super("suchthat", sql);
    }
    public void setSql(String sql){
        super.sql = sql;
    }
    
    public void parseClause(Map<String, String> attrToType, List<String> varToNum, Set<String> aggFuns){
        String[] gvConds = super.sql.split(",");
        Set<String> selfAggFuncs = new HashSet<>();
        int to = 1;
        
        for(String cond: gvConds){

            List<Integer> fromList = new ArrayList<>();
            condsList.add(expParser.parserCond(cond.trim(), attrToType, varToNum, aggFuns, fromList, selfAggFuncs));

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
                preCondStr = preCondStr + ")?" + condsList.get(condsList.size() - 1)+ ":false";
                condsList.set(condsList.size() - 1, preCondStr);
            }
            for(int from: fromList){
                JSONObject edge = new JSONObject();
                try{
                    edge.put("from", from);
                    edge.put("to", to);
                }catch(Exception e){
                    e.printStackTrace();
                }
                graph.put(edge);
            }
            to++;
        }
    }

    @Override
    public List<String> getParsedClause(){
        return this.condsList;
    }
    
    public JSONArray getGraph(){
        return this.graph;
    }
    
    public String toString(){
        String res = "";
        for(String con: condsList){
            res += con;
        }
        return res;
    }
}
