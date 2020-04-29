/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryProcessorForMFandEMF;

/**
 *
 * @author Hangyu Wang
 */
import java.util.*;
import utils.Group;

public class PhiStruct {
    private List<String> projectedAttributes;
    private Integer numOfGVars;
    private List<String> gAttributes;
    private List<Group> aggregateFuncs;
    private List<String> predicateOfGVars;
    private String predicateOfHaving; 
//    public Group(String sub, String aggType, String attr){
//        this.sub = sub;
//        this.aggType = aggType;
//        this.attr = attr;
//    }
    PhiStruct(List<String> projectedAttributes, Integer numOfGVars,
             List<String> gAttributes, List<String> aggregateFuncs, 
             List<String> predicateOfGVars, String predicateOfHaving){
        this.projectedAttributes = projectedAttributes;
        this.numOfGVars = numOfGVars;
        this.gAttributes = gAttributes;
        this.aggregateFuncs = new ArrayList<>();
        for(String str: aggregateFuncs){
            String[] tempArr = str.split("_");
            this.aggregateFuncs.add(new Group(tempArr[1], tempArr[0], tempArr[2]));
        }
        this.predicateOfGVars = predicateOfGVars;
        this.predicateOfHaving = predicateOfHaving;
    }
    
    public List<String> getProjATTR(){
        return projectedAttributes;
    }
    public Integer getNumOfGV(){
        return numOfGVars;
    }
    public List<String> getG_ATTR(){
        return gAttributes;
    }
    public List<Group> getAggFunc(){
        return aggregateFuncs;
    }
    public List<String> getCond_GV(){
        return predicateOfGVars;
    }
    public String getCond_Having(){
        return predicateOfHaving;
    }
}