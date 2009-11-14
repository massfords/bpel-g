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
        if (sBean == null) {
            if (AeEngineFactory.getEngineAdministration() != null) {
                sBean = new AeEngineManagementAdapter(AeEngineFactory.getEngineAdministration());
            } else {
                try {
                    JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
                    String[] creds = {"smx", "smx"};
                    Map map = Collections.singletonMap(JMXConnector.CREDENTIALS, creds);
                    JMXConnector connector = JMXConnectorFactory.connect(url, map);
                    MBeanServerConnection mbs = connector.getMBeanServerConnection();
                    ObjectName objectName = ObjectName.getInstance("org.apache.servicemix:ContainerName=ServiceMix,Name=bpel-g-jbi,SubType=Management,Type=Component");
    
                    sBean = JMX.newMXBeanProxy(mbs, objectName, IAeEngineManagementMXBean.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
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
    }
}
