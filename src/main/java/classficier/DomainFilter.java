package classficier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DomainFilter {
    public List<List<String>> listKeywords;

    public DomainFilter() {
        this.listKeywords = new ArrayList<List<String>>();
    }

    public double[] filter(String text){
        String temp=text.toLowerCase();
        double[] result=new double[listKeywords.size()];
        Arrays.fill(result, 0);

        double sum=0;
        for (int i=0; i<listKeywords.size(); i++){
            for (int j=0; j<listKeywords.get(i).size(); j++){
                if (temp.contains(listKeywords.get(i).get(j))){
                    result[i]=result[i]+1;
                }
            }
            sum=sum+result[i];
        }

        if (sum==0){
            Arrays.fill(result, 1/listKeywords.size());
        } else {
            for (int i=0; i<listKeywords.size(); i++){
                result[i]=result[i]/sum;
            }
        }

        return result;
    }
}
