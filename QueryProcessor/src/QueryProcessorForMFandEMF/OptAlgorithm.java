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
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.sql.*;
import utils.PreReq;
import QueryProcessorForMFandEMF.Parser;

public class OptAlgorithm {
    Map<Integer, Set<Integer>> preToNext = new HashMap<>();
    Map<Integer, Set<Integer>> nextToPre = new HashMap<>();
    public int cnt;
    public List<List<Integer>> order;
//    public void topoSort(int num, List<PreReq> preRequisites){
//        boolean[] visited = new boolean[num + 1];
//        visited[0] = true;
//        List<List<Integer>> order = new ArrayList<>();
//        
//        ///////////////////
//        for(PreReq pair: preRequisites){
//            if(!preToNext.containsKey(pair.from)){
//                preToNext.put(pair.from, new HashSet<Integer>());
//            }
//            preToNext.get(pair.from).add(pair.to);
//            
//            if(!nextToPre.containsKey(pair.to)){
//                nextToPre.put(pair.to, new HashSet<Integer>());
//            }
//            nextToPre.get(pair.to).add(pair.from);
//        }
//        //////////////
//        int count = 0;
//        while(!nextToPre.keySet().isEmpty() || count++ == 0){
//            List<Integer> list = new ArrayList<>();
//            for(int index = 0; index <= num; index++){
////                System.out.println(index);
//                if(!visited[index] && !nextToPre.containsKey(index)){
////                    System.out.println("Here is the inner: " + index);
//                    list.add(index);
//                    visited[index] = true;
//                }
//            }
//            
//            for(int fromVal: list){
//                if(!preToNext.containsKey(fromVal))
//                    continue;
//                for(int toVal: preToNext.get(fromVal)){
//                    Set<Integer> temp = nextToPre.get(toVal);
//                    temp.remove(fromVal);
//                    if(temp.size() == 0){
//                        nextToPre.remove(toVal);
//                    }
//                }
//            }
//            order.add(list);
//        }
//        
//        cnt = order.size();
//        this.order = order;
//        System.out.println(cnt);
//        System.out.println(order);
////        return order;
//    }
    
     public void topoSort(int num, JSONArray preRequisites){
        boolean[] visited = new boolean[num + 1];
        visited[0] = true;
        List<List<Integer>> order = new ArrayList<>();
        
        ///////////////////
        for(int i = 0; i < preRequisites.length(); i++){
            try{
                JSONObject jObj = preRequisites.getJSONObject(i);
    //        for(JSONObject pair: preRequisites)
                if(jObj.getInt("from") == 0)
                    continue;
                if(!preToNext.containsKey(jObj.getInt("from"))){
                    preToNext.put(jObj.getInt("from"), new HashSet<Integer>());
                }
                preToNext.get(jObj.getInt("from")).add(jObj.getInt("to"));

                if(!nextToPre.containsKey(jObj.getInt("to"))){
                    nextToPre.put(jObj.getInt("to"), new HashSet<Integer>());
                }
                nextToPre.get(jObj.getInt("to")).add(jObj.getInt("from"));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        //////////////
        System.out.println("After build");
        int count = 0;
        while(!nextToPre.keySet().isEmpty() || count++ == 0){
            List<Integer> list = new ArrayList<>();
            for(int index = 0; index <= num; index++){
//                System.out.println(index);
                if(!visited[index] && !nextToPre.containsKey(index)){
//                    System.out.println("Here is the inner: " + index);
                    list.add(index);
                    visited[index] = true;
                }
            }
            
            for(int fromVal: list){
                if(!preToNext.containsKey(fromVal))
                    continue;
                for(int toVal: preToNext.get(fromVal)){
                    Set<Integer> temp = nextToPre.get(toVal);
                    temp.remove(fromVal);
                    if(temp.size() == 0){
                        nextToPre.remove(toVal);
                    }
                }
            }
            System.out.println(list);
            order.add(list);
        }
        
        cnt = order.size();
        this.order = order;
        System.out.println(cnt);
        System.out.println(order);
//        return order;
    }
    
    
//    public static void main(String[] args){
//        OptAlgorithm thing = new OptAlgorithm();
//        List<PreReq> list = new ArrayList<>();
////        list.add(new PreReq(1,2));
//        thing.topoSort(4, list);
//        System.out.println(thing.order);
//        System.out.println(thing.cnt);
//    
//    }
}
