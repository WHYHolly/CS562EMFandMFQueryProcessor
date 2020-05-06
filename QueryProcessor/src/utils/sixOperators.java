/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is a class to support parser
 */
package utils;

import java.util.*;
import org.json.JSONArray;

public class sixOperators{
    private List<String> projectedAttributes;
    private Integer numOfGVars;
    private String gAttributes;
    private List<String> aggregateFuncs;
    private List<String> predicateOfGVars;
    private String predicateOfHaving; 
    private JSONArray opt;
    public void setProjAttrs(List<String> list){
        this.projectedAttributes = list;
    }
    public void setNum(int num){
        this.numOfGVars = num;
    }
    public void setGAttrs(String gAttrs){
        this.gAttributes = gAttrs;
    }
    public void setAggFuncs(List<String> list){
        this.aggregateFuncs = list;
    }
    public void setCondOfGVars(List<String> list){
        this.predicateOfGVars = list;
    }
    public void setCondOfHaving(String having){
        this.predicateOfHaving = having;
    }
    public void setOpt(JSONArray opt){
        this.opt = opt;
    }
    
    
    public List<String> getProjAttrs(){
        return this.projectedAttributes;
    }
    public int getNum(){
        return this.numOfGVars;
    }
    public String getGAttrs(){
        return this.gAttributes;
    }
    public List<String> getAggFuncs(){
        return this.aggregateFuncs;
    }
    public List<String> getCondOfGVars(){
        return this.predicateOfGVars;
    }
    public String getCondOfHaving(){
        return this.predicateOfHaving;
    }
    public JSONArray getOpt(){
        return this.opt;
    }
    public String toString(){
        return  "Projected Attributes: " + this.getProjAttrs() + "\n" +
                "Number of Grouping Vars: " + this.getNum() + "\n" +
                "Grouping Attrs: " + this.getGAttrs() + "\n" +
                "Agg Funstions: " + this.getAggFuncs() + "\n" +
                "Conds of Vars: " + this.getCondOfGVars() + "\n" +
                "Having: " + this.getCondOfHaving() +
                "Opt: " + this.getOpt();
    }
}