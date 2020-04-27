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

/**
 *
 * @author Holly
 */
public class parserSuchThat extends partialParser{

    private List<String> condsList = new ArrayList<>();

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
        for(String cond: gvConds){
//            System.out.println("This condition:" + cond);
            condsList.add(expParser.parserCond(cond.trim().toLowerCase(), attrToType, varToNum, aggFuns));
        }
    }

//    @Override
    public List<String> getParsedClause(){
        return this.condsList;
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
