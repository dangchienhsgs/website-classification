package preprocessing;

import com.mongodb.*;
import common.Config;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Scanner;

/**
 *  Import database from text file, with format each row format:
 *  <publisher_id> <publisher_name> <domain>
 *  Database in mongodb
 */
public class ImportDatabase {
    public static void importDatabase(String fileName) {
        try {


            int i = 0;
            String line = "";


            Config config = new Config("configuration.xml");

            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int second = Calendar.getInstance().get(Calendar.SECOND);


            MongoClient mongoClient = new MongoClient(config.MONGO_HOST, config.MONGO_PORT);
            DB db = mongoClient.getDB("eway");
            DBCollection dbCollection = db.getCollection("domain");

            FileInputStream fileInputStream = new FileInputStream(fileName);
            Scanner scanner = new Scanner(fileInputStream);

            while (scanner.hasNextLine()) {
                i++;
                line = scanner.nextLine().trim();
                String temp[] = line.split(" ");

                if (temp.length == 3) {
                    String publisher_id = temp[0];
                    String publisher_name = temp[1];
                    String publisher_domain = temp[2];
                    System.out.println(i + ": " + publisher_id + " " + publisher_domain + " " + publisher_name);
                    System.out.println(temp.length);

                    if (!publisher_domain.contains("http://")) {
                        publisher_domain = "http://" + publisher_domain;
                    }


                    DBObject dbObject = new BasicDBObject();
                    dbObject.put(Config.PUBLISHER_ID, publisher_id);
                    dbObject.put(config.DOMAIN, publisher_domain);
                    dbObject.put(Config.PUBLISHER_NAME, publisher_name);
                    dbObject.put(Config.FLAG, Config.FLAG_NOT_ANALYZE);
                    dbObject.put(Config.CATEGORY, Config.CATEGORY_NOT_ANALYZE);
                    dbObject.put(Config.ADULT, Config.ADULT_NOT_ANALYZE);
                    dbObject.put(Config.CREATED_TIME, hour + ":" + minute + ":" + second + "-" + day + "/" + month + "/" + year);
                    dbObject.put(Config.LAST_UPDATED, hour + ":" + minute + ":" + second + "-" + day + "/" + month + "/" + year);

                    dbCollection.insert(dbObject);
                } else {
                    System.out.println("Missing " + i + ": " + line);
                }
            }

            mongoClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String args[]){
        importDatabase("csdl/short_data.txt");
    }
}
