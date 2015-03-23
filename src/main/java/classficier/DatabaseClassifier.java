package classficier;

import com.mongodb.*;
import common.Config;
import data.DataBuilder;
import data.Dictionary;
import data.DictionaryBuilder;
import data.Website;
import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Jsoup;
import preprocessing.TextProcessor;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.pmml.MiningSchema;

import java.net.UnknownHostException;
import java.util.*;

public class DatabaseClassifier {

    private Config config;

    private MongoClient mongoClient;
    private DB db;
    private DBCollection dbCollection;

    private Classifier classifier;
    private Dictionary dictionary;
    private Instances data;

    private int numThreads = 3;

    private boolean isAvailable[];

    private DataBuilder dataBuilder;
    private SexChecker sexChecker;

    private DomainFilter domainFilter;

    private int year, month, day, hour, minute, second;

    private List<String> classNames;
    public DatabaseClassifier(Config config) throws UnknownHostException {

        this.config = config;

        // INIT MONGO DATABASE PARAMS
        mongoClient = new MongoClient(config.MONGO_HOST, config.MONGO_PORT);
        db = mongoClient.getDB(config.MONGO_DB);
        dbCollection = db.getCollection(config.MONGO_COLLECTION);

    }

    public void init() throws UnknownHostException{
        // READING CONFIG
        classNames=new ArrayList<String>(config.listTrainData.keySet());

        // READ TOPICS WORDS AND BUILD A DICTIONARY
        DictionaryBuilder dictionaryBuilder=new DictionaryBuilder();
        dictionaryBuilder.setListFileName(config.listDictionaryWords);
        dictionary=dictionaryBuilder.build();

        // CREATE DATA BUILDER
        dataBuilder=new DataBuilder(dictionary, config.MODELS_DIRECTORY);

        // CREATE EMPTY TRAIN SET
        data=dataBuilder.newData("train_data", classNames);
        DataReader dataReader=new DataReader(dataBuilder);

        // READ TRAIN SET
        for (String className: classNames){
            for (String source:config.listTrainData.get(className)){
                dataReader.readTrainData(data, source, className);
            }
        }
        // CREATE SEX CHECKER
        sexChecker = new SexChecker();

        // ADD WORD TO SEX CHECKER
        for (String string : config.listSexWordsFile) {
            sexChecker.addSexWords(string);
        }

        // ADD SPECIAL WORD TO DOMAIN FILTER
        domainFilter = new DomainFilter();
        for (int i = 0; i < classNames.size(); i++) {
            domainFilter.listKeywords.add(config.listSpecialWords.get(classNames.get(i)));
        }

        // CREATE A CLASSIFIER
        classifier=new NaiveBayes();
    }



    public void classify() {
        System.out.println("Start to classify ");

        DBCursor dbCursor = dbCollection.find();


        // GET TIME TO UPDATE IN TIME FIELDS : LAST UPDATED, CREATED TIME
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        minute = Calendar.getInstance().get(Calendar.MINUTE);
        second = Calendar.getInstance().get(Calendar.SECOND);


        isAvailable = new boolean[numThreads];
        Arrays.fill(isAvailable, true);

        dbCursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);

        if (dbCursor!=null){

            while (dbCursor.hasNext()) {

                DBObject domainObject = dbCursor.next();

                Integer analyzed = (Integer) domainObject.get(Config.FLAG);

                // CHECK IF THIS DOMAIN IF IT CHECKED
                boolean check = true;
                if (analyzed != null) {
                    if ((analyzed == Config.FLAG_ANALYZED)) {
                        check = false;
                    }
                    if (analyzed == Config.FLAG_DOMAIN_MISSING) {
                        check = true;
                    }
                } else {
                    check = true;
                }


                if (check) {
                /* Check this domain */
                    String domain = (String) domainObject.get(Config.DOMAIN);

                    System.out.println ("Start classify "+domain);

                    Website website = Website.connect(domain);

                    if (website == null) {
                        System.out.println(domain + " is missing");
                        domainObject.put(Config.FLAG, Config.FLAG_DOMAIN_MISSING);
                        domainObject.put(Config.LAST_UPDATED, hour + ":" + minute + ":" + second + "-" + day + "/" + month + "/" + year);
                        dbCollection.save(domainObject);

                    } else {
                        dataBuilder = new DataBuilder(dictionary, config.MODELS_DIRECTORY);
                        Instance instance = dataBuilder.build(website);

                        instance.setDataset(data);
                        instance.setClassMissing();

                        double topicProportionUrl[] = domainFilter.filter(domain);
                        double topicProportionTitle[] = domainFilter.filter(website.getDocument().title());

                        boolean isHaveSex = sexChecker.checkText(website.getText());

                        try {
                            double classProportion[] = classifier.distributionForInstance(instance);
                            System.out.println("Proportion: ");

                            for (int i = 0; i < topicProportionTitle.length; i++) {
                                System.out.print(topicProportionTitle[i] + ", ");
                            }

                            for (int i = 0; i < topicProportionUrl.length; i++) {
                                System.out.print(topicProportionUrl[i] + ", ");
                            }

                            for (int i = 0; i < classProportion.length; i++) {
                                System.out.print(classProportion[i] + ", ");
                            }

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



                            // UPDATE RESULT
                            domainObject.put(Config.CATEGORY, classNames.get(index_max));
                            if (isHaveSex) {
                                domainObject.put(Config.ADULT, Config.ADULT_YES);
                            } else {
                                domainObject.put(Config.ADULT, Config.ADULT_NO);
                            }
                            domainObject.put(Config.FLAG, Config.FLAG_ANALYZED);
                            domainObject.put(Config.LAST_UPDATED, hour + ":" + minute + ":" + second + "-" + day + "/" + month + "/" + year);

                            dbCollection.save(domainObject);

                            System.out.println("Report: " + domain + ": " + classNames.get(index_max) + " sex: " + isHaveSex);
                        } catch (Exception e) {
                            System.out.println(instance + " is not suitable for data");
                        }
                    }
                }
            }
        }

    }


    public void train() {
        if (this.classifier != null && this.data != null) {
            try {
                this.classifier.buildClassifier(data);
            } catch (Exception e) {
                System.out.println("Can not learn train data");
                e.printStackTrace();
            }
        } else {
            if (classifier == null) {
                System.out.println("Classifier is null");
            } else {
                System.out.println("Train data is null");
            }
        }
    }

    public static void main(String args[]){
        try{
            Config config = new Config("configuration.xml");

            DatabaseClassifier databaseClassifier = new DatabaseClassifier(config);
            databaseClassifier.init();
            databaseClassifier.train();
            databaseClassifier.classify();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}


