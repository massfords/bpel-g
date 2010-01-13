package org.activebpel.rt.bpeladmin.war;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.management.JMX;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.activebpel.rt.bpel.server.admin.jmx.AeEngineManagementAdapter;
import org.activebpel.rt.bpel.server.admin.jmx.IAeEngineManagementMXBean;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

public class AeEngineManagementFactory {
    
    private static IAeEngineManagementMXBean sBean; 
    private static JMXConnector sConnector;
    private static String sServiceURL;
    private static String sObjectName;
    private static String sUser;
    private static String sPassword;
    private static DisconnectedListener sNotificationlistener = new DisconnectedListener();
    
    
    public synchronized static IAeEngineManagementMXBean getBean() {
        /*
         * Attempting to recover from a communications error here. The typical scenario is that I'm running the console 
         * in a separate container and I'm frequently restarting servicemix but don't want to have to also restart
         * the web container.
         * 
         * Need to research the notification messages below to determine the frequency and why I'm not always getting notified
         * of a broken pipe. Another possible solution would be to create a proxy for the bean which resets the bean reference
         * here when a ConnectException (or whatever the appropriate Exception) is thrown. 
         */
        if (sBean == null) {
            connect();
        } else if (sConnector != null) {
            // verify that we're still connected
            try {
                sConnector.getConnectionId();
            } catch (IOException e) {
                reconnect();
            }
        } else {
            reconnect();
        }
        return sBean;
    }

    private static void reconnect() {
        close();
        connect();
    }
    
    public static void initBean(String aServiceURL, String aObjectName, String aUser, String aPassword) {
        sServiceURL = aServiceURL;
        sObjectName = aObjectName;
        sUser = aUser;
        sPassword = aPassword;
        connect();
    }
    
    private static void connect() {
        if (AeEngineFactory.getEngineAdministration() != null) {
            sBean = new AeEngineManagementAdapter(AeEngineFactory.getEngineAdministration());
        } else {
            try {
                JMXServiceURL url = new JMXServiceURL(sServiceURL);
                String[] creds = {sUser, sPassword};
                Map map = Collections.singletonMap(JMXConnector.CREDENTIALS, creds);
                JMXConnector connector = JMXConnectorFactory.connect(url, map);
                MBeanServerConnection mbs = connector.getMBeanServerConnection();
                ObjectName objectName = ObjectName.getInstance(sObjectName);
    
                sBean = JMX.newMXBeanProxy(mbs, objectName, IAeEngineManagementMXBean.class);
                sConnector = connector;
                sConnector.addConnectionNotificationListener(sNotificationlistener, new DisconnectedFilter(), objectName);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static void close() {
        if (sConnector != null) {
            // remove listener
            try {
                sConnector.removeConnectionNotificationListener(sNotificationlistener);
            } catch (ListenerNotFoundException e) {
            }
            // close connection
            try {
                sConnector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sConnector = null;
        sBean = null;
    }
    
    private static class DisconnectedListener implements NotificationListener {

        @Override
        public void handleNotification(Notification aNotification, Object aObj) {
            close();
        }
        
    }
    
    private static class DisconnectedFilter implements NotificationFilter {

        @Override
        public boolean isNotificationEnabled(Notification aNotification) {
            return "jmx.remote.connection.closed".equals(aNotification.getType()) ||
                   "jmx.remote.connection.failed".equals(aNotification.getType());
        }
    }
}
