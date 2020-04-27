/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLParser;

import java.util.*;
//import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.partialParser;
import org.apache.commons.lang3.math.NumberUtils; 
import utils.expParser;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Holly
 */
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
        int to = 1;
        
        for(String cond: gvConds){
//            System.out.println("This condition:" + cond);
            List<Integer> fromList = new ArrayList<>();
            condsList.add(expParser.parserCond(cond.trim(), attrToType, varToNum, aggFuns, fromList));
//            System.out.println("//////////the opt list is there:" + to + fromList);

            for(int from: fromList){
                JSONObject edge = new JSONObject();
                try{
                    edge.put("from", from);
                    edge.put("to", to);
                }catch(Exception e){
                    e.printStackTrace();
                }
                System.out.println(edge);
                graph.put(edge);
            }
            to++;
        }
    }

//    @Override
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
//    
//    public static void main(String[] args){
//       String sql = "x.state = \"NY\",\n" +
//                        "          y.state = \"NJ\",\n" +
//                        "          z.state = \"NJ\"\n";
//       parserSuchThat parser = new parserSuchThat(sql);
//       
//       parser.parseClause();
//       
//       System.out.println(parser);
//    }
}
