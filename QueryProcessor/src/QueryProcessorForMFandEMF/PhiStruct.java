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
public class PhiStruct {
    private List<String> projectedAttributes;
    private Integer numOfGVars;
    private List<String> gAttributes;
    private List<String> aggregateFuncs;
    private List<String> predicateOfGVars;
    private List<String> predicateOfHaving; 
    PhiStruct(List<String> projectedAttributes, Integer numOfGVars,
             List<String> gAttributes, List<String> aggregateFuncs, 
             List<String> predicateOfGVars, List<String> predicateOfHaving){
        this.projectedAttributes = projectedAttributes;
        this.numOfGVars = numOfGVars;
        this.gAttributes = gAttributes;
        this.aggregateFuncs = aggregateFuncs;
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
    public List<String> getAggFunc(){
        return aggregateFuncs;
    }
    public List<String> getCond_GV(){
        return predicateOfGVars;
    }
    public List<String> getCond_Having(){
        return predicateOfHaving;
    }
}
