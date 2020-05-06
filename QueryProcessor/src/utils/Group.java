/*
 * @author Hangyu Wang (CWID: 10444246)
 * Supporting class for Phistruct
 */
package utils;

public class Group{
    public String sub;
    public String aggType;
    public String attr;
    
    public Group(String sub, String aggType, String attr){
        this.sub = sub;
        this.aggType = aggType;
        this.attr = attr;
    }
    
    public Group(String attr){
        this(null, null, attr);
    }
    
    public String toString(){
        return this.aggType + "_" + this.sub + "_" + this.attr;
    }
}