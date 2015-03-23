package utils;

import com.mongodb.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import common.Config;

import java.net.UnknownHostException;


public class DatabaseChecker {
    private Config config;

    public DatabaseChecker(Config config){
        this.config = config;
    }

    public Boolean checkAllAnalyzed(){

        try {
            MongoClient mongoClient = new MongoClient(config.MONGO_HOST, config.MONGO_PORT);
            DB db = mongoClient.getDB(config.MONGO_DB);
            DBCollection dbCollection = db.getCollection(config.MONGO_COLLECTION);

            DBCursor dbCursor = dbCollection.find();
            dbCursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);

            boolean check = true;

            if (dbCursor.count()==0){
                check = true;

            } else {
                while (dbCursor.hasNext()){

                    DBObject dbObject = dbCursor.next();

                    int flag  = (Integer) dbObject.get(Config.FLAG);

                    if (flag==Config.FLAG_NOT_ANALYZE){
                        check = false;
                    }
                }
            }

            return check;
        } catch (UnknownHostException e){
            e.printStackTrace();
            return null;
        }
    }
}
