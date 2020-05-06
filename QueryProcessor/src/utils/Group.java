/*
 * @author Hangyu Wang (CWID: 10444246)
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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