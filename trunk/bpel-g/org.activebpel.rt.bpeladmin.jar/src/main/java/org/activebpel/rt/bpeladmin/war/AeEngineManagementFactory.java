package org.activebpel.rt.bpeladmin.war;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
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
    
    public static IAeEngineManagementMXBean getBean() {
        if (sBean == null)
            throw new IllegalStateException("Bean not initialized");
        return sBean;
    }
    
    
    public static IAeEngineManagementMXBean initBean(String aServiceURL, String aObjectName, String aUser, String aPassword) throws Exception {
        if (sBean != null) {
            close();
        }
        if (AeEngineFactory.getEngineAdministration() != null) {
            sBean = new AeEngineManagementAdapter(AeEngineFactory.getEngineAdministration());
        } else {
            JMXServiceURL url = new JMXServiceURL(aServiceURL);
            String[] creds = {aUser, aPassword};
            Map map = Collections.singletonMap(JMXConnector.CREDENTIALS, creds);
            JMXConnector connector = JMXConnectorFactory.connect(url, map);
            MBeanServerConnection mbs = connector.getMBeanServerConnection();
            ObjectName objectName = ObjectName.getInstance(aObjectName);

            sBean = JMX.newMXBeanProxy(mbs, objectName, IAeEngineManagementMXBean.class);
        }
        return sBean;
    }
    
    public static void close() {
        if (sConnector != null) {
            try {
                sConnector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sBean = null;
    }
}
