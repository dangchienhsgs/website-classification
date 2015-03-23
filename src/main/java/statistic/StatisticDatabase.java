package statistic;

import com.mongodb.*;
import common.Config;

import java.io.FileWriter;
import java.util.*;

/**
 * Created by dangchienhsgs on 10/02/2015.
 */
public class StatisticDatabase {
    public static void main(String args[]) {
        try {
            List<String> categories = Arrays.asList("News", "Store-Review", "Truyen", "Music");
            Config config = new Config("configuration.xml");

            MongoClient mongoClient = new MongoClient(config.MONGO_HOST, config.MONGO_PORT);
            DB db = mongoClient.getDB("eway");
            DBCollection dbCollection = db.getCollection("domain");

            DBCursor dbCursor = dbCollection.find();
            System.out.println(dbCursor.count());

            FileWriter fileWriter = new FileWriter("result4.csv");
            fileWriter.write("publisher_id, domain, flag, category, having sex \n");


            while (dbCursor.hasNext()) {
                DBObject dbObject = dbCursor.next();
                Integer FLAG = (Integer) dbObject.get(Config.FLAG);

                String publisher_id = (String) dbObject.get(Config.PUBLISHER_ID);
                String domain = (String) dbObject.get(Config.DOMAIN);
                Integer flag = (Integer) dbObject.get(Config.FLAG);

                String flagString = "";
                String categoryString = "";

                if (flag == Config.FLAG_ANALYZED) {
                    flagString = "Da phan tich";

                    try {
                        double category = (Double) dbObject.get(Config.CATEGORY);
                        categoryString = categories.get((int) category);
                    } catch (Exception e) {
                        categoryString = categories.get(categories.size() - 1);
                    }

                } else if (flag == Config.FLAG_NOT_ANALYZE) {
                    flagString = "Chua phan tich";
                } else if (flag == Config.FLAG_DOMAIN_MISSING) {
                    flagString = "Domain Missing";
                }


                String sex = (String) dbObject.get(Config.ADULT);
                String sexString = "";
                if (sex.equals(Config.ADULT_YES)) {
                    sexString = "Yes";
                } else if (sex.equals(Config.ADULT_NO)) {
                    sexString = "No";
                }

                fileWriter.write(publisher_id + ", " + domain + ", " + flagString + ", " + categoryString + ", " + sexString + "\n");

            }

            fileWriter.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
