package classficier;


import data.Website;
import org.apache.commons.lang3.StringUtils;
import preprocessing.TextProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SexChecker {
    private List<String> listSexWords;
    private List<String> listSexUrls;
    private int maxNumWords=10;

    /**
     * Constructor
     */
    public SexChecker() {
        listSexWords=new ArrayList<String>();
        listSexUrls=new ArrayList<String>();
    }


    /**
     * Add sex words to list sex words
     * These words use to check an website
     * @param filename: File link
     */
    public void addSexWords(String filename){
        try{
            FileInputStream fileInputStream=new FileInputStream(filename);
            Scanner scanner=new Scanner(fileInputStream);

            while (scanner.hasNextLine()){
                String line=scanner.nextLine().trim();

                if (line.isEmpty() | !listSexWords.contains(line)){

                    listSexWords.add(line);
                }
            }
            fileInputStream.close();
            scanner.close();

        } catch (Exception e){
            System.out.println (filename+" is missing");
        }
    }

    /**
     * Check a string if it have sex content
     * @param text
     * @return
     */
    public boolean checkText(String text){
        String[] words=text.split(" ");

        boolean check=false;

        int num=0;
//        for (String word:words){
//            if (listSexWords.contains(word.toLowerCase().trim())){
//                System.out.println (word.toLowerCase());
//                num=num+1;
//            }
//        }
        for (String sexWord:listSexWords){
            // WHY: " " + sexword +" ":
            // Sample:  sextgem contains sex, but it not meaning sex
            // So we must only check " sex "
            int times = StringUtils.countMatches(text, " "+sexWord+" ");
            num = num + times;

            if (times>0) {
                System.out.println (sexWord+": "+times);
            }

        }

        if (num>maxNumWords){
            check=true;
        }

        if (num/words.length>0.5){
            check=true;
        }

        System.out.println (num+"  "+words.length);

        return check;

    }

//    public static void main(String args[]) {
//
//        SexChecker sexChecker=new SexChecker();
//        sexChecker.addSexWords("preprocessedData/google_blacklist_final.txt");
//        sexChecker.addSexWords("preprocessedData/vietnamese_sex_words.txt");
//
//        Website website=Website.connect("http://roiloancuongduong.edu.vn/tinh-duc/anh-khieu-dam-anh-sexy-xem-la-suong.html");
//        TextProcessor textProcessor=new TextProcessor();
//        String text=textProcessor.preProcessing(website.getText(), TextProcessor.Language.VIETNAM);
//        System.out.println (sexChecker.checkText(text));
//
//    }
}
