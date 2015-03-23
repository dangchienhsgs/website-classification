package common;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Config {
    public String MONGO_HOST = "localhost";
    public int MONGO_PORT = 27017;

    public String MONGO_DB = "eway";
    public String MONGO_COLLECTION = "domain";

    public static String DOMAIN = "domain";

    public static String PUBLISHER_ID = "publisher_id";
    public static String PUBLISHER_NAME = "publisher_name";

    public static String CATEGORY = "category";
    public static int CATEGORY_NOT_ANALYZE = -1;

    public static String ADULT = "adult";
    public static String ADULT_YES = "yes";
    public static String ADULT_NO = "no";
    public static int ADULT_UNKNOWN = 2;
    public static int ADULT_NOT_ANALYZE = -1;


    public static String FLAG = "flag";
    public static int FLAG_NOT_ANALYZE = 0;
    public static int FLAG_ANALYZED = 1;
    public static int FLAG_DOMAIN_MISSING = 2;

    public String MODELS_DIRECTORY = "models";
    public String LOCK_FILE;

    public static String CREATED_TIME = "createdTime";
    public static String LAST_UPDATED = "lastUpdate";

    public Map<String, List<String>> listTrainData;
    public Map<String, List<String>> listSpecialWords;

    public List<String> listSexWordsFile;
    public List<String> listDictionaryWords;

    public int timeUpdate;


    public Config(String filename) throws Exception {

        // open file
        FileInputStream file = new FileInputStream(filename);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.parse(file);
        doc.getDocumentElement().normalize();

        // read mongo host and port
        this.MONGO_HOST = doc.getElementsByTagName("mongo-host").item(0).getTextContent();
        this.MONGO_PORT = Integer.parseInt(doc.getElementsByTagName("mongo-port").item(0).getTextContent());

        // read mongo db name and collection name
        this.MONGO_DB = doc.getElementsByTagName("mongo-db").item(0).getTextContent();
        this.MONGO_COLLECTION = doc.getElementsByTagName("mongo-collection").item(0).getTextContent();

        // get model path of model directory support for tokenize string
        this.MODELS_DIRECTORY = doc.getElementsByTagName("model-directory").item(0).getTextContent();

        // get path of lock file
        this.LOCK_FILE = doc.getElementsByTagName("lock-file").item(0).getTextContent();

        // get time to update
        this.timeUpdate = Integer.parseInt(doc.getElementsByTagName("time-update").item(0).getTextContent());

        // init data
        listTrainData = new HashMap<String, List<String>>();
        listSpecialWords = new HashMap<String, List<String>>();
        listDictionaryWords = new ArrayList<String>();
        listSexWordsFile = new ArrayList<String>();


        // read class resources
        Element className = (Element) doc.getElementsByTagName("class").item(0);

        // each classParam is a class resource
        NodeList classParams = className.getElementsByTagName("class-params");


        for (int i = 0; i < classParams.getLength(); i++) {
            // get classParam ith
            Element element = (Element) classParams.item(i);

            // name is the name of class
            String name = element.getAttribute("name");


            // add train data path of class name to list
            listTrainData.put(name, new ArrayList<String>());
            NodeList trainSources = element.getElementsByTagName("train-data");

            for (int j = 0; j < trainSources.getLength(); j++) {
                String source = trainSources.item(j).getTextContent();
                listTrainData.get(name).add(source);
            }

            // add special words file path to list
            listSpecialWords.put(name, new ArrayList<String>());
            Element listSpecials = (Element) element.getElementsByTagName("special-words").item(0);
            String words[] = listSpecials.getTextContent().split(",");
            for (String word : words) {
                listSpecialWords.get(name).add(word.trim());
            }
        }


        // read dictionary file paths
        Element dictionary = (Element) doc.getElementsByTagName("dictionary").item(0);
        NodeList wordFiles = dictionary.getElementsByTagName("words");
        for (int i = 0; i < wordFiles.getLength(); i++) {
            Element element = (Element) wordFiles.item(i);
            listDictionaryWords.add(element.getTextContent());
        }

        // read sex words file paths
        Element sexData = (Element) doc.getElementsByTagName("sex").item(0);
        NodeList wordList = sexData.getElementsByTagName("word-list");

        for (int i = 0; i < wordList.getLength(); i++) {
            Element element = (Element) wordList.item(i);
            listSexWordsFile.add(element.getTextContent());
        }

        file.close();

    }
}
