package org.activebpel.rt.bpeladmin.war;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AeContextListener implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent aArg0) {
        AeEngineManagementFactory.close();
    }

    @Override
    public void contextInitialized(ServletContextEvent aArg0) {
    }

}
