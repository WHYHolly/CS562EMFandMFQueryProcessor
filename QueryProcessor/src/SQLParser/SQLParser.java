/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is SQL Parser.
 * Here a SQL will be parsed into JSON
 */
package SQLParser;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.*;
import java.util.regex.*;

import java.io.*;

import java.sql.*;

import java.nio.charset.StandardCharsets;

import utils.*;

public class SQLParser {
    private String sql;
    String USER;
    String PWD;
    String URL;
    private Pattern p = Pattern.compile("select\\s+(?<select>(.|\\n)*)"
                                        + "(from\\s+(?<from>((?!(where|group by|such that|having)).|\\n)*))"
                                        + "(where\\s+(?<where>((?!(group by|such that|having)).|\\n)*))?"
                                        + "(group by\\s+(?<groupby>((?!(such that|having)).|\\n)*))?"
                                        + "(such that\\s+(?<suchthat>((?!having).|\\n)*))?"
                                        + "(having\\s+(?<having>(.|\\n)*))?");


    private List<String> groupName = new ArrayList<>(Arrays.asList("from", "groupby", "suchthat",  "where", "having", "select"));
    private Map<String, partialParser> clauseToParser = new HashMap<String, partialParser>() {{
        put("from", new parserFrom());
        put("select", new parserSelect());
        put("where", new parserWhere());
        put("groupby", new parserGroupBy());
        put("suchthat", new parserSuchThat());
        put("having", new parserHaving());
    }};
    private Map<String, String> nameToType = new HashMap<>();
    private List<String> varToNum = new ArrayList<>();
    private sixOperators sixOps;
    private Matcher m = null;
    
    public SQLParser(String USER, String PWD, String URL){
        this.USER = USER;
        this.PWD = PWD;
        this.URL = URL;
    }
    
    void generalParser(){
        this.m = p.matcher(this.sql);
        while(m.find()){
            for(String g: groupName){
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
//                        System.out.println(clauseToParser.get("suchthat").getParsedClause());
                        sixOps.setCondOfGVars((List<String>) clauseToParser.get("suchthat").getParsedClause());
                        sixOps.setOpt(clauseToParser.get("suchthat").getGraph());
                    }
                    break;
                case "select":
                    clauseToParser.get("select").parseClause(varToNum, aggFuns);
                    sixOps.setProjAttrs((List<String>) clauseToParser.get("select").getParsedClause());
                    break;
                case "where":{
                    clauseToParser.get("where").parseClause(nameToType, varToNum, aggFuns);
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
    }
    
    public sixOperators getSixOps(){
        return this.sixOps;
    }
    
    public JSONObject getJSONFormat(){
        JSONObject j = new JSONObject();
        try{
            j.put("projAttrs", this.getSixOps().getProjAttrs().toArray());
            j.put("numOfGVars", this.getSixOps().getNum());
            j.put("gAttrs", this.getSixOps().getGAttrs() == null
                                                        ? "":  this.getSixOps().getGAttrs());
            j.put("aggFuncs", this.getSixOps().getAggFuncs());
            j.put("condOfGVars", this.getSixOps().getCondOfGVars());
            j.put("condOfHaving", this.getSixOps().getCondOfHaving());
            j.put("Opt", this.getSixOps().getOpt() == null
                                                ? new ArrayList<>()
                                                : this.getSixOps().getOpt());
        }catch(JSONException e){
            System.out.println("Something wrong when you JSON IT!");
        }

        return j;
    }
    
    
    public void performSQLParser(String fromFile, String toFile){
        File inputSQl = new File("./src/inputFile/" + fromFile);
        try{
            String sql = FileUtils.readFileToString(inputSQl, StandardCharsets.UTF_8);
            File newfile = new File("./src/inputFile/" + toFile);
            FileWriter p = new FileWriter("./src/inputFile/" + toFile);
            this.sql = sql;
            this.generalParser();
            this.initParser();
            p.write(this.getJSONFormat().toString());
            p.flush();
        }catch(IOException e){
            System.out.println("Your SQL CANNOT be parsed. Please make sure you enter the right EMF SQL!\nAnd in the right location!");
            e.printStackTrace();
        }
    
    }
    
//    public static void main(String[] args){
//        String USER ="postgres";
//        String PWD ="m8kimmWhyholly";
//        String URL ="jdbc:postgresql://localhost:5432/postgres";
//        
//        SQLParser pTest = new SQLParser(USER, PWD, URL);
//        pTest.performSQLParser("sql4.sql", "input4.json");
//    }
}

