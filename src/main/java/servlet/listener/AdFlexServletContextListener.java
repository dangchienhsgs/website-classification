package servlet.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class AdFlexServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // start check and run thread in background
        System.out.println ("Start thread listen all changes of database");

        Thread threadCheckAndRun = new Thread(new CheckAndClassifyRunnable());

        threadCheckAndRun.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
