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

import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.admin.jmx.AeEngineManagementAdapter;
import org.activebpel.rt.bpel.server.admin.jmx.IAeEngineManagementMXBean;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

import bpelg.services.processes.AeProcessManager;
import bpelg.services.urnresolver.AeURNResolver;

public class AeEngineManagementFactory {
    
    private static IAeEngineManagementMXBean sBean; 
    private static JMXConnector sConnector;
    private static String sServiceURL;
    private static String sObjectName;
    private static String sUser;
    private static String sPassword;
    private static DisconnectedListener sNotificationlistener = new DisconnectedListener();
    private static boolean sRemote;
    private static AeProcessManager sProcessManager;
    private static AeURNResolver sResolverService;
    
    
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
        } else if (isRemote()) {
            reconnect();
        }
        return sBean;
    }

    private static void reconnect() {
        close();
        connect();
    }
    
    public static void initBean(String aServiceURL, String aObjectName, String aUser, String aPassword) {
    	sRemote = aServiceURL != null;
        sServiceURL = aServiceURL;
        sObjectName = aObjectName;
        sUser = aUser;
        sPassword = aPassword;
        connect();
    }
    
    private static void connect() {
        if ( isLocal()) {
            if (sBean == null ) {
                IAeEngineAdministration admin = AeEngineFactory.getEngineAdministration();
                sBean = new AeEngineManagementAdapter(admin);
            }
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
        if (isRemote() && sConnector != null) {
            JMXConnector connector = sConnector;
            sBean = null;
            sConnector = null;
            // remove listener
            try {
                connector.removeConnectionNotificationListener(sNotificationlistener);
            } catch (ListenerNotFoundException e) {
            }
            // close connection
            try {
                connector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    
    private static boolean isRemote() {
    	return sRemote;
    }
    private static boolean isLocal() {
    	return !isRemote();
    }

	public static AeProcessManager getProcessManager() {
		if (sProcessManager == null) {
			sProcessManager = AeEngineFactory.getBean(AeProcessManager.class);
		}
		return sProcessManager;
	}

	public static void setProcessManager(AeProcessManager aProcessManager) {
		sProcessManager = aProcessManager;
	}

    public static AeURNResolver getResolverService() {
        if (sResolverService == null) {
            sResolverService = AeEngineFactory.getBean(AeURNResolver.class);
        }
        return sResolverService;
    }

    public static void setsResolverService(AeURNResolver aSResolverService) {
        sResolverService = aSResolverService;
    }
}
