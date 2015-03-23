package classficier;


import data.Dictionary;
import data.DataBuilder;
import data.DictionaryBuilder;
import data.Website;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.io.*;
import java.util.*;

public class DataReader {
    private DataBuilder dataBuilder;

    public DataReader(DataBuilder dataBuilder) {
        this.dataBuilder=dataBuilder;
    }

    public void readTrainData(Instances data, String classfolderName, String className){

        System.out.println ("Read "+classfolderName);

        File classFolder=new File(classfolderName);

        for (File urlFolder:classFolder.listFiles()){

            //String url=urlFolder.getName();
            for (File file:urlFolder.listFiles()){
                try{
                    FileReader fileReader=new FileReader(file);
                    BufferedReader bufferedReader=new BufferedReader(fileReader);
                    Scanner scanner=new Scanner(bufferedReader);

//                    String subUrl=scanner.nextLine();

                    String text=scanner.nextLine();
                    Instance temp=dataBuilder.build(text);
                    temp.setDataset(data);
                    temp.setClassValue(className);
                    data.add(temp);

                    fileReader.close();
                    bufferedReader.close();
                    scanner.close();

                } catch (Exception e){
                    //e.printStackTrace();
                    System.out.println(classfolderName+"/"+urlFolder.getName()+"/"+file.getName()+" is missing");
                }
            }

        }
    }


}


