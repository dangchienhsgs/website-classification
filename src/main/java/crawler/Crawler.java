package crawler;

import common.Config;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import data.Website;
import preprocessing.TextProcessor;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class Crawler {


    public String getURLFromLink(String link) throws URISyntaxException {
        List<NameValuePair> list = URLEncodedUtils.parse(new URI(link), "UTF-8");

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals("url")) {
                return (list.get(i).getValue());
            }
        }
        return null;
    }


    public void crawler(String file, String folder){
        try{
            FileInputStream fileInputStream=new FileInputStream(file);
            Scanner scanner=new Scanner(fileInputStream);

            int i=0;

            Config config =new Config("configuration.xml");
            TextProcessor textProcessor=new TextProcessor(config.MODELS_DIRECTORY);

            while (scanner.hasNextLine()){
                i=i+1;
                String url=scanner.nextLine().trim();
                Website handler= Website.connect(url);


                if (handler!=null){

                    // create directory
                    new File(folder+"/"+url.replace("http://", "")).mkdirs();

                    PrintWriter writer = new PrintWriter(folder+"/"+url.replace("http://","")+"/main.txt", "UTF-8");
                    writer.write(url+"\n");
                    String content = textProcessor.preProcessing(handler.getText(), textProcessor.detectLanguage(handler.getText()));
                    System.out.println(content);
                    writer.write(content);
                    writer.close();


                    List<String> linksSameHost=handler.getLinkSameHost(url);

                    int count=0;
                    for (int j=0; j<linksSameHost.size(); j++){
                        String link=linksSameHost.get(j);
                        Website subHandler= Website.connect(link);
                        if (subHandler!=null){

                            PrintWriter subWriter = new PrintWriter(folder+"/"+url.replace("http://","")+"/subUrl."+j+".txt", "UTF-8");
                            content = textProcessor.preProcessing(subHandler.getText(), textProcessor.detectLanguage(subHandler.getText()));
                            System.out.println(content);

                            subWriter.write(link+"\n");
                            writer.write(content);
                            subWriter.close();

                            count=count+1;

                        }

                        if (count>10){
                            break;
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        Crawler crawler=new Crawler();
        crawler.crawler("temp.txt", "preprocessedData");
    }

}
