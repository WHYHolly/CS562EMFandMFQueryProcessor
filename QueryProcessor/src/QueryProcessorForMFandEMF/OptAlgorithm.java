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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.*;

public class OptAlgorithm {
    Map<Integer, Set<Integer>> preToNext = new HashMap<>();
    Map<Integer, Set<Integer>> nextToPre = new HashMap<>();
    public int cnt;
    public List<List<Integer>> order;
    
     public void topoSort(int num, JSONArray preRequisites){
        boolean[] visited = new boolean[num + 1];
        visited[0] = true;
        List<List<Integer>> order = new ArrayList<>();
        
        for(int i = 0; i < preRequisites.length(); i++){
            try{
                JSONObject jObj = preRequisites.getJSONObject(i);
                if(jObj.getInt("from") == 0 || jObj.getInt("from") == jObj.getInt("to"))
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

        int count = 0;
        while(!nextToPre.keySet().isEmpty() || count++ == 0){
            List<Integer> list = new ArrayList<>();
            for(int index = 0; index <= num; index++){
                if(!visited[index] && !nextToPre.containsKey(index)){
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
//            System.out.println(list);
            order.add(list);
        }
        
        cnt = order.size();
        this.order = order;
//        System.out.println(cnt);
//        System.out.println(order);
    }
}
