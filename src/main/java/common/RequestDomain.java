package common;


import classficier.DatabaseClassifier;
import com.mongodb.*;
import common.Config;
import data.DataBuilder;
import data.Dictionary;
import data.DictionaryBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.DatabaseChecker;
import utils.LockManagement;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

import java.io.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

public class RequestDomain {
    private List<String> domains;
    private MongoClient mongoClient;
    private DB db;
    private DBCollection dbCollection;
    private Config config;


    public RequestDomain(List<String> domains, Config config) {
        this.domains = domains;
        this.config = config;
    }


    public String getDomainInfo() {

        try {
            // INIT MONGO DATABASE PARAMS
            mongoClient = new MongoClient(config.MONGO_HOST, config.MONGO_PORT);
            db = mongoClient.getDB(config.MONGO_DB);
            dbCollection = db.getCollection(config.MONGO_COLLECTION);

            // READING DATABASE
            JSONArray result = new JSONArray();

            // SET TIMEOUT FOR DB CURSOR
            DBCursor dbCursor = dbCollection.find();
            dbCursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);


            Boolean[] check = new Boolean[domains.size()];
            Arrays.fill(check, false);

            // GET DOMAIN WHICH AS ANALYZED BEFORE
            while (dbCursor.hasNext()) {

                DBObject dbObject = dbCursor.next();
                String domain = (String) dbObject.get(Config.DOMAIN);

                for (int i = 0; i < domains.size(); i++) {

                    String newDomain = domains.get(i);
                    if (domain.toLowerCase().trim().equals(newDomain.toLowerCase().trim())) {
                        result.add(dbObject);
                        check[i] = true;
                    }
                }

            }

            // GET TIME TO UPDATE IN TIME FIELDS : LAST UPDATED, CREATED TIME
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int second = Calendar.getInstance().get(Calendar.SECOND);


            // TELL USER WHICH DOMAINS NOT YET ANALYZED
            Boolean hasNewDomain = false;
            for (int i = 0; i < domains.size(); i++) {
                if (check[i] == false) {
                    // we have some domain not analyzed
                    hasNewDomain = true;

                    // create new dbObject of that domain and put it to database
                    DBObject dbObject = new BasicDBObject();
                    dbObject.put(Config.DOMAIN, domains.get(i));
                    dbObject.put(Config.FLAG, Config.FLAG_NOT_ANALYZE);
                    dbObject.put(Config.ADULT, Config.ADULT_NOT_ANALYZE);
                    dbObject.put(Config.CREATED_TIME, hour + ":" + minute + ":" + second + "-" + day + "/" + month + "/" + year);
                    dbObject.put(Config.LAST_UPDATED, hour + ":" + minute + ":" + second + "-" + day + "/" + month + "/" + year);
                    dbObject.put(Config.CATEGORY, Config.CATEGORY_NOT_ANALYZE);

                    // add that object to the result
                    result.add(dbObject);

                    // save to db
                    dbCollection.save(dbObject);
                }
            }


            if (hasNewDomain) {
                // save to lock file
                LockManagement.saveLock(true, config.LOCK_FILE);
            }

            return result.toJSONString();
        } catch (Exception e) {
            return ("SERVER ERROR: FILE NOT FOUND EXCEPTION: configuration.xml");
        }


    }

    public static void main(String args[]) {
        try {
            Config config = new Config("configuration.xml");
            RequestDomain requestDomain = new RequestDomain(
                    Arrays.asList(
                            "http://vnexpress.net", "http://news.zing.vn", "http://dongthapbay.com"
                    ),
                    config);

            System.out.println (requestDomain.getDomainInfo());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

