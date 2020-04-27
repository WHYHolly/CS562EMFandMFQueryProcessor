/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Holly
 */
import java.util.*;
public class sixOperators{
    private List<String> projectedAttributes;
    private Integer numOfGVars;
    private String gAttributes;
    private List<String> aggregateFuncs;
    private List<String> predicateOfGVars;
    private String predicateOfHaving; 
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
    public String toString(){
        return "Projected Attributes: " + this.getProjAttrs() + "\n" +
               "Number of Grouping Vars: " + this.getNum() + "\n" +
               "Grouping Attrs: " + this.getGAttrs() + "\n" +
               "Agg Funstions: " + this.getAggFuncs() + "\n" +
               "Conds of Vars" + this.getCondOfGVars() + "\n" +
               "Having" + this.getCondOfHaving();
    }
}