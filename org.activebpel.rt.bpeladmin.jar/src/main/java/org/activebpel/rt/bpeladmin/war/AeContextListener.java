package org.activebpel.rt.bpeladmin.war;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AeContextListener implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent aContextEvent) {
        AeEngineManagementFactory.close();
    }

    @Override
    public void contextInitialized(ServletContextEvent aContextEvent) {
        ServletContext servletContext = aContextEvent.getServletContext();
        String serviceURL = servletContext.getInitParameter("jmx.serviceURL");
        String objectName = servletContext.getInitParameter("jmx.objectName");
        String user = servletContext.getInitParameter("jmx.user");
        String password = servletContext.getInitParameter("jmx.password");
        
        try {
            AeEngineManagementFactory.initBean(serviceURL, objectName, user, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
