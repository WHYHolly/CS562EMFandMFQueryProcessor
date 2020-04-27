/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLParser;

/**
 *
 * @author Hangyu Wang
 */
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.*;
import java.util.regex.*;

import java.io.*;

import java.sql.*;

import java.nio.charset.StandardCharsets;


import utils.*;

public class SQLParser {
    private String sql;
    String USER ="postgres";
    String PWD ="m8kimmWhyholly";
    String URL ="jdbc:postgresql://localhost:5432/postgres";
    private Pattern p = Pattern.compile("select\\s+(?<select>(.|\\n)*)"
                                        + "(from\\s+(?<from>((?!(where|group by|such that|having)).|\\n)*))"
                                        + "(where\\s+(?<where>((?!(group by|such that|having)).|\\n)*))?"
                                        + "(group by\\s+(?<groupby>((?!(such that|having)).|\\n)*))?"
                                        + "(such that\\s+(?<suchthat>((?!having).|\\n)*))?"
                                        + "(having\\s+(?<having>(.|\\n)*))?");


    private List<String> groupName = new ArrayList<>(Arrays.asList("from", "groupby", "suchthat",  "where", "having", "select"));
    private static Map<String, partialParser> clauseToParser = new HashMap<String, partialParser>() {{
        put("from", new parserFrom());
        put("select", new parserSelect());
        put("where", new parserWhere());
        put("groupby", new parserGroupBy());
        put("suchthat", new parserSuchThat());
        put("having", new parserHaving());
    }};
    private Map<String, String> nameToType = new HashMap<>();
    private List<String> varToNum = new ArrayList<>();
//    private Map<String, String> nameToType = new HashMap<>();
    private sixOperators sixOps;
    private Matcher m = null;
    public SQLParser(String sql){
        this.sql = sql;
    }
    void generalParser(){
        this.m = p.matcher(this.sql);
        while(m.find()){
//            System.out.println("matched");
            for(String g: groupName){
//                System.out.println(g + ":\n" + m.group(g));
                if(m.group(g) != null){
                    clauseToParser.get(g).setSql(m.group(g));
                }
            }
        }
    }
    void initParser(){
        sixOps = new sixOperators();
        Set<String> aggFuns = new HashSet<>();
        for(String g: groupName){
            switch(g){
                case "from":
                    clauseToParser.get("from").parseClause(USER, PWD, URL);
                    nameToType = (Map<String,String>)clauseToParser.get("from").getParsedClause();
                    break;
                case "groupby":
                    clauseToParser.get("groupby").parseClause();
                    sixOps.setNum(clauseToParser.get("groupby").getNumofG());
                    varToNum = (List<String>) clauseToParser.get("groupby").getParsedClause();
//                    System.out.println("Here is the number of var"+varToNum);
                    sixOps.setGAttrs(clauseToParser.get("groupby").getGAttributes());
                    break;
                case "suchthat":
                    if(clauseToParser.get("suchthat").sql == null){
                        sixOps.setCondOfGVars(new ArrayList<>());
                        sixOps.setOpt(null);
                    }else{
                        clauseToParser.get("suchthat").parseClause(nameToType, varToNum, aggFuns);
    //                    System.out.println(clauseToParser.get("suchthat").getParsedClause());
                        sixOps.setCondOfGVars((List<String>) clauseToParser.get("suchthat").getParsedClause());
                        sixOps.setOpt(clauseToParser.get("suchthat").getGraph());
                    }
                    break;
                case "select":
                    clauseToParser.get("select").parseClause(varToNum, aggFuns);
                    sixOps.setProjAttrs((List<String>) clauseToParser.get("select").getParsedClause());
                    break;
                case "where":{
                    clauseToParser.get("where").parseClause(nameToType, varToNum);
                    List<String> tempList = (List<String>) clauseToParser.get("where").getParsedClause();
                    if(tempList.size() == 0){
                        List<String> conds = new ArrayList<>();
                        conds.add("_");
                        for(String c: sixOps.getCondOfGVars()){
                            conds.add("if(" + c + ")");
                        }
                        sixOps.setCondOfGVars(conds);
                    }else{
                        List<String> conds = new ArrayList<>();
                        String cond0 = tempList.get(0);
                        conds.add("if(" + cond0 + ")");
//                        System.out.println(sixOps.getCondOfGVars());
                        for(String c: sixOps.getCondOfGVars()){
                            conds.add("if(" + cond0 +" && "+ "(" + c +") )");
                        }
                        sixOps.setCondOfGVars(conds);
                    }
                    break;}
                case "having":
                    clauseToParser.get("having").parseClause(nameToType, varToNum, aggFuns);
                    List<String> tempList = (List<String>) clauseToParser.get("having").getParsedClause();
                    if(tempList.size() == 0){
                        sixOps.setCondOfHaving("_");
                    }else{
                        sixOps.setCondOfHaving("if("+tempList.get(0)+")");
                    }
                    break;
            }
        }
        sixOps.setAggFuncs(expParser.parserFuncs(aggFuns));
//        System.out.println(sixOps);
    }
    
    public sixOperators getSixOps(){
        return this.sixOps;
    }
    
    public JSONObject getJSONFormat(){
        JSONObject j = new JSONObject();
        try{
            j.put("projAttrs", this.getSixOps().getProjAttrs().toArray());
            j.put("numOfGVars", this.getSixOps().getNum());
            j.put("gAttrs", this.getSixOps().getNum());
            j.put("aggFuncs", this.getSixOps().getAggFuncs());
            j.put("condOfGVars", this.getSixOps().getCondOfGVars());
            j.put("condOfHaving", this.getSixOps().getCondOfHaving());
            j.put("Opt", this.getSixOps().getOpt());
        }catch(Exception e){
            System.out.println("Something wrong when you JSON IT!");
        }

        return j;
    }

    
    public static void main(String[] args){

        String file = "input1.json";
        File inputSQl = new File("./src/SQLFile/sql1.sql");
       
        
        try{
            String sql = FileUtils.readFileToString(inputSQl, StandardCharsets.UTF_8);
//            System.out.println("Here is the SQL;///////////////");
//            System.out.println(sql);
            File newfile = new File("./src/inputFile/" + file);
            FileWriter p = new FileWriter("./src/inputFile/" + file);
            SQLParser sqlP = new SQLParser(sql);
            sqlP.generalParser();
            sqlP.initParser();
            p.write(sqlP.getJSONFormat().toString());
            p.flush();
            System.out.println(sqlP.getJSONFormat().toString());

        }catch(Exception e){
            System.out.println("Something wrong with the SQL parser!");
            e.printStackTrace();
        }
    }
}

