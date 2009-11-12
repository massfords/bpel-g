package org.activebpel.rt.bpel.server.admin.jmx;

import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.server.admin.AeProcessDeploymentDetail;

public interface IAeEngineManagementMXBean {

    /**
     * Gets the details for all of the deployed services.
     */
    public List<AeServiceDeploymentBean> getDeployedServices();

    /**
     * Gets the details for all of the deployed processes
     */
    public List<AeProcessDeploymentDetail> getDeployedProcesses();

    /**
     * Gets the details for a single process id
     * @param aId
     */
    public AeProcessInstanceDetail getProcessDetail(long aId);
}
