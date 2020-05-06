/*
 * @author Hangyu Wang (CWID: 10444246)
 * This is a struct that made for the Query Processor.
 * To fetch the data in a easier way instead of read the json once an donce again.
 * And here records the 6 ops.
 */
package QueryProcessorForMFandEMF;

import java.util.*;
import utils.Group;

public class PhiStruct {
    private List<String> projectedAttributes;
    private Integer numOfGVars;
    private List<String> gAttributes;
    private List<Group> aggregateFuncs;
    private List<String> predicateOfGVars;
    private String predicateOfHaving; 
    
    /*
     * initial of the struct
     */
    PhiStruct(List<String> projectedAttributes, Integer numOfGVars,
             List<String> gAttributes, List<String> aggregateFuncs, 
             List<String> predicateOfGVars, String predicateOfHaving){
        this.projectedAttributes = projectedAttributes;
        this.numOfGVars = numOfGVars;
        this.gAttributes = gAttributes;
        this.aggregateFuncs = new ArrayList<>();
        /*
         * To make the aggaregation functions easier to use,
         * here I use the Group struct I made to store.
         * So in the code, the agg id, agg type and attr type can be easily obtained.
         */
        for(String str: aggregateFuncs){
            String[] tempArr = str.split("_");
            this.aggregateFuncs.add(new Group(tempArr[1], tempArr[0], tempArr[2]));
        }
        this.predicateOfGVars = predicateOfGVars;
        this.predicateOfHaving = predicateOfHaving;
    }
    
    /*
     * The functions to get the stored ops.
     */
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