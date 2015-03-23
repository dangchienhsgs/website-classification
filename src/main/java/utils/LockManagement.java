package utils;

import common.Config;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

/**
 * Created by dangchienhsgs on 12/03/2015.
 */
public class LockManagement {

    public static boolean readLock(String filePath){
        Boolean checkLock = false;

        try{
            File file = new File(filePath);
            if (!file.exists()){
                LockManagement.saveLock(true, filePath);
                return false;
            } else {
                FileInputStream fileInputStream = new FileInputStream(filePath);
                Scanner scanner = new Scanner(fileInputStream);

                if (scanner.hasNext()){
                    String line = scanner.nextLine();
                    if (line.trim().equals("lock")){
                        checkLock = true;
                    }
                }

                scanner.close();
                fileInputStream.close();
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            // do nothing
        }

        return  checkLock;
    }

    public static void saveLock(boolean lockValue, String filePath){
        try{
            File file = new File(filePath);

            if (file.exists()){
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());

            if (lockValue){
                fileWriter.write("lock");
            } else {
                fileWriter.write("nolock");
            }

            fileWriter.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        try{
            Config config = new Config("configuration.xml");

            LockManagement.saveLock(false, "classify.lock");
            System.out.println (LockManagement.readLock("classify.lock"));
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
