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
import java.io.*;
public class Processor {
    private final int SELECTATTRIBUTE = 0, NUMOFGV = 1, GATTR = 2,
            FVECT = 3, SELECTCONDVECT = 4, HAVINGCOND = 5;
    
    private PhiStruct oneStruct;
//    private File file;
    private static String outputPath;
    private static final String samplePath = "./src/QueryProcessorForMFandEMF/codeSample.txt";
    File sampleFile;
    FileWriter fw;
    BufferedWriter bw;
    
    public void readInput(String path, String fileName){
        this.outputPath = "./src/outputFile/GeneratedCodeFor" + fileName;
        Scanner s = null;
        int status = 0;
        try{
            s = new Scanner(new BufferedReader(new FileReader(path + fileName)));
        }catch(java.io.FileNotFoundException  e){
            e.printStackTrace();
        }
        s.nextLine();
        List<String> projectedAttributes = new ArrayList<>();
        Integer numOfGVars = null;
        List<String> gAttributes = new ArrayList<>();
        List<String> aggregateFuncs = new ArrayList<>();
        List<String> predicateOfGVars = new ArrayList<>();
        String predicateOfHaving = new String(); 
        while(s.hasNextLine()){
            if(!s.hasNext()){
                break;
            }
            //Check status, make sure the reading content
            String line = s.next();
            if(line.startsWith("SELECT ATTRIBUTE(S)")){
                status = SELECTATTRIBUTE;
            }else if(line.startsWith("NUMBER OF GROUPING VARIABLES(n)")){
                status = NUMOFGV;
            }else if(line.startsWith("GROUPING ATTRIBUTES(V)")){
                status = GATTR;
            }else if(line.startsWith("F-VECT([F])")){
                status = FVECT;
            }else if(line.startsWith("SELECT CONDITION-VECT([σ])")){
                status = SELECTCONDVECT;
            }else if(line.startsWith("HAVING_CONDITION(G)")){
                status = HAVINGCOND;
            }else{
                switch(status){
                    case SELECTATTRIBUTE:
                        projectedAttributes = Arrays.asList(line.split(","));
                        break;
                    case NUMOFGV:
                        numOfGVars = Integer.parseInt(line);
                        break;
                    case GATTR:
                        projectedAttributes = Arrays.asList(line.split(","));
                        break;
                    case FVECT:
                        
                        break;
                    case SELECTCONDVECT:
                        predicateOfGVars.add(line);
                        break;
                    case HAVINGCOND:
                        predicateOfHaving = line;
                        break;
                }
            }
        
        
        }
        
        oneStruct = new PhiStruct(  projectedAttributes, 
                                    numOfGVars, 
                                    gAttributes,
                                    aggregateFuncs, 
                                    predicateOfGVars, 
                                    predicateOfHaving);
        
    }
    
    public void createFile(){
        try{
            File file = new File(outputPath);
            sampleFile = new File("");
            fw = new FileWriter(outputPath);
            bw = new BufferedWriter(fw);
            
            
            
        }catch(Exception e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
    public void writeCode(String codeLine){
        try{
            bw.write(codeLine);
        
        }catch(Exception e){
            System.out.println("Something wrong wiht the print");
            System.out.println(e);
        }
    }
    
    public void writeFromSample(int startLine, int endLine){
    }
    
    public static void main(String[] args){
        
    }
}
