package classficier;

import common.Config;
import data.DataBuilder;
import data.Dictionary;
import data.DictionaryBuilder;
import data.Website;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sun.security.jca.GetInstance;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DomainClassifier {
    private List<String> urls;
    private Config config;
    private PrintWriter writer;
    public DomainClassifier(List<String> urls, Config config, PrintWriter writer) {
        this.urls = urls;
        this.config = config;
        this.writer = writer;
    }

    public String classify(){

        // READING CONFIG
        List<String> classNames=new ArrayList<String>(config.listTrainData.keySet());

        // READ TOPICS WORDS AND BUILD A DICTIONARY
        DictionaryBuilder dictionaryBuilder=new DictionaryBuilder();
        dictionaryBuilder.setListFileName(config.listDictionaryWords);
        Dictionary dictionary=dictionaryBuilder.build();

        // CREATE DATA BUILDER
        DataBuilder dataBuilder=new DataBuilder(dictionary, config.MODELS_DIRECTORY);

        // CREATE EMPTY TRAIN SET
        Instances data=dataBuilder.newData("train_data", classNames);
        DataReader dataReader=new DataReader(dataBuilder);

        // READ TRAIN SET
        for (String className: classNames){
            for (String source:config.listTrainData.get(className)){
                dataReader.readTrainData(data, source, className);
            }
        }
        // CREATE SEX CHECKER
        SexChecker sexChecker = new SexChecker();

        // ADD WORD TO SEX CHECKER
        for (String string : config.listSexWordsFile) {
            sexChecker.addSexWords(string);
        }

        // ADD SPECIAL WORD TO DOMAIN FILTER
        DomainFilter domainFilter = new DomainFilter();
        for (int i = 0; i < classNames.size(); i++) {
            domainFilter.listKeywords.add(config.listSpecialWords.get(classNames.get(i)));
        }

        // CREATE A CLASSIFIER
        Classifier classifier=new NaiveBayes();

        try{
            classifier.buildClassifier(data);
            JSONArray mainResult = new JSONArray();
            for (String domain:urls){

                // CONNECT

                Website website=Website.connect(domain);
                JSONObject result = new JSONObject();
                result.put(Config.DOMAIN, domain);

                if (website==null){
                    /* if this website can not connect*/
                    result.put(Config.FLAG, "Domain Missing");

                } else {
                    /* if it connected */
                    result.put(Config.FLAG, "Analyzed");
                    Instance instance = dataBuilder.build(website);
                    instance.setDataset(data);
                    instance.setClassMissing();

                    double classProportion[] = classifier.distributionForInstance(instance);
                    double topicProportionUrl[] = domainFilter.filter(domain);
                    double topicProportionTitle[] = domainFilter.filter(website.getDocument().title());

                    // GET THE TOPIC HAVE MAX PROPORTION
                    int index_max = 0;
                    double value_max = 0;
                    for (int i = 0; i < classProportion.length; i++) {
                        double temp_max = topicProportionTitle[i] + classProportion[i] + topicProportionUrl[i];

                        if (temp_max > value_max) {
                            value_max = temp_max;

                            index_max = i;
                        }
                    }
                    double className = index_max;

                    // SET THE CATEGORY
                    result.put(Config.CATEGORY, classNames.get((int) className));

                    // CHECKING SEX
                    boolean isHaveSex = sexChecker.checkText(website.getText());
                    if (isHaveSex){
                        result.put(Config.ADULT, Config.ADULT_YES);
                    } else {
                        result.put(Config.ADULT, Config.ADULT_NO);
                    }

                }

                mainResult.add(result);
            }

            return mainResult.toJSONString();
        } catch (Exception e){
            e.printStackTrace();
            return "Data in server is error";
        }
    }

    public static void main(String args[]){
        try{
            Config config = new Config("configuration.xml");

            DomainClassifier domainClassifier=new DomainClassifier(Arrays.asList(
                    "http://vnexpress.net",
                    "http://xvideos.com",
                    "http://mp3.zing.vn",
                    "http://apk.vn",
                    "http://truyensubviet.com",
                    "http://tinhte.vn",
                    "http://nhac.vui.vn",
                    "http://doctruyen18.net",
                    "http://motsach.info",
                    "http://www.goldmansachs.com/",
                    "http://news.zing.vn",
                    "http://truyentranhtuan.com",
                    "http://v1vn.com",
                    "http://hayhaytv.vn",
                    "http://nhacso.net",
                    "http://vietnamnet.vn",
                    "http://24h.com.vn"
            ), config, null);

            System.out.println (domainClassifier.classify());

        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
