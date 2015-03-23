package test;

import data.Dictionary;
import org.junit.Test;
import data.Website;
import preprocessing.TextProcessor;
import vn.hus.nlp.tokenizer.VietTokenizer;

import java.io.FileNotFoundException;
import java.util.Arrays;


public class DictionaryTest {

    @Test
    public void testReadHtmlFile(){
        try{
            Website handler= Website.readHtmlFile("sex/1.html", true);

            System.out.println (handler.getDocument().html());
            System.out.println (handler.getUrl());

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testCreateDictionaryFromFolder(){
        Dictionary dictionary= Dictionary.createDictionary("data/news", 150, "data/news_words.txt");
        System.out.println (dictionary.getWords());
    }
}
