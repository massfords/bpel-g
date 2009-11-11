package org.activebpel.rt.bpel.server.admin.jmx;

import java.util.List;

public interface IAeEngineManagementMXBean {
    public List<AeServiceDeploymentBean> getDeployedServices();
}
