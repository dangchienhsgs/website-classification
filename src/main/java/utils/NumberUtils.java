package utils;

import java.util.*;

/**
 * Created by dangchienbn on 1/8/15.
 */
public class NumberUtils {
    public static List<String> sort(Map<String, Double> map, int number){

        List<String> list=new ArrayList<String>();

        Object[] keySet=map.keySet().toArray();
        for (int i=0; i<keySet.length; i++){
            String key=(String) keySet[i];

            if (list.size()<number){
                list.add(key);
            } else {
                for (int j=0; j<number; j++){
                    String temp=list.get(j);
                    if (map.get(temp)<map.get(key)){
                        list.remove(temp);
                        list.add(key);
                        break;
                    }
                }
            }
        }

        return list;
    }

    public static void printArray(double[][] array){
        for (int i=0; i<array.length; i++){

            for (int j=0; j<array[i].length; j++){
                System.out.print (array[i][j]+" ");
            }

            System.out.println ();
        }
    }
}
