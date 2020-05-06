/*
 * @author Hangyu Wang (CWID: 10444246)
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.partialParser;
public class parserGroupBy extends partialParser{
    private static final Pattern p1 = Pattern.compile("(?<GA>[\\s|\\d|\\w|,]*)([:|;](?<GV>.*))*");
//    private Pattern p2 = Pattern.compile("(?<GA>.*)(?:;|:)(?<GV>.*)");
    private Matcher m = null;
    private int numOfGV = 0;
    private String gAttrs;
    private List<String> listofAttrs;
    parserGroupBy(){
        super("groupby", null);
    }
    parserGroupBy(String sql){
        super("groupby", sql);
    }
    public void setSql(String sql){
        super.sql = sql;
    }
    
    @Override
    public void parseClause(){
        if(super.sql != null){
            this.m = p1.matcher(this.sql);
            if(m.find()){
                gAttrs = m.group("GA").trim();
//                System.out.println("gAttrs:"+  gAttrs);
                if(m.group("GV") != null){
                    listofAttrs = new ArrayList<>();
                    String[] arr = m.group("GV").split(",");
                    int count = 0;
                    for(String attr: arr){
//                        System.out.println(attr.trim().length());
                        if(attr.trim().length() != 0){
//                            System.out.println(attr.trim().length());
                            listofAttrs.add(attr.trim());
                            count ++;
                        }
                        numOfGV = count;  
                    }
                }
            }
        }
    }
    
    @Override
    public List<String> getParsedClause(){
            return listofAttrs;
    }
    
    public int getNumofG(){
        return numOfGV;
    }
    
    public String getGAttributes(){
        return gAttrs;
    }
    
    public String toString(){
        return "The grouping attributes are: " + this.getGAttributes() 
                + "Number of grouping variables: " + this.getNumofG()
                + "And the list of variables: " + this.getParsedClause();
    
    }
}
