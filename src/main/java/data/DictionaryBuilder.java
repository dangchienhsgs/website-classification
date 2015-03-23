package data;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictionaryBuilder {
    private List<String> listFileName=new ArrayList<String>();

    public DictionaryBuilder() {
        // do nothing
    }

    public DictionaryBuilder(List<String> listFileName) {
        listFileName=new ArrayList<String>();
    }

    public void addSourceWord(String filename){
        this.listFileName.add(filename);
    }

    public Dictionary build(){
        List<String> words=new ArrayList<String>();

        if (listFileName.size()==0){
            System.out.println ("Error ! The source files is empty ");
            return null;
        } else {
            for (String filename:listFileName){
                try{
                    //System.out.println ("Reading "+filename+"...");
                    FileInputStream fileInputStream=new FileInputStream(filename);
                    Scanner scanner=new Scanner(fileInputStream);

                    while (scanner.hasNextLine()){
                        String word=scanner.nextLine().trim();
                        if (!words.contains(word)){
                            words.add(word);
                        }
                    }

                    scanner.close();
                    fileInputStream.close();
                } catch (FileNotFoundException e){
                    System.out.println (filename+" is missing");
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            return new Dictionary(words);
        }
    }

    public void setListFileName(List<String> listFileName) {
        this.listFileName = listFileName;
    }

}
