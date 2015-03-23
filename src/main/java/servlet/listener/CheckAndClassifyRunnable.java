package servlet.listener;


import classficier.DatabaseClassifier;
import common.Config;
import utils.DatabaseChecker;
import utils.LockManagement;

public class CheckAndClassifyRunnable implements  Runnable {
    @Override
    public void run() {
        try{
            Config config = new Config("configuration.xml");
            DatabaseClassifier databaseClassifier = new DatabaseClassifier(config);
            databaseClassifier.init();
            DatabaseChecker databaseChecker = new DatabaseChecker(config);

            while (true){
                // if some new domain has came, the lock file will show the lock word
                // LockManager will return true if have some new
                boolean isHasNew = LockManagement.readLock(config.LOCK_FILE);

                System.out.println ("Reading lock file: "+isHasNew);
                // check if the databaseClassifier trained return true, false if it not trained
                boolean isTrained = false;

                if (isHasNew){
                    if (isTrained == false){
                        databaseClassifier.train();
                        isTrained = true;
                    }

                    do{
                        // do this until all domains in database has been classified
                        databaseClassifier.classify();
                        System.out.println ("Log: "+databaseChecker.checkAllAnalyzed());
                    } while (!databaseChecker.checkAllAnalyzed());


                    // after it sleep and change value lock in lock file
                    LockManagement.saveLock(false, config.LOCK_FILE);

                }

                Thread.sleep(1000*config.timeUpdate);
            }

        } catch (Exception e){
            e.printStackTrace();
            System.out.println ("Can not start CheckAndRun thread !");
        }

    }
}
