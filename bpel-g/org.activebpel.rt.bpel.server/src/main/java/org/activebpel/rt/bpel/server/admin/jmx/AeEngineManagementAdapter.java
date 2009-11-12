package org.activebpel.rt.bpel.server.admin.jmx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.server.admin.AeProcessDeploymentDetail;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;

public class AeEngineManagementAdapter implements IAeEngineManagementMXBean {
    
    private IAeEngineAdministration mAdmin;
    
    public AeEngineManagementAdapter(IAeEngineAdministration aAdmin) {
        mAdmin = aAdmin;
    }

    @Override
    public List<AeServiceDeploymentBean> getDeployedServices() {
        List<AeServiceDeploymentBean> result = new ArrayList();
        for (IAeServiceDeploymentInfo info : mAdmin.getDeployedServices()) {
            result.add(new AeServiceDeploymentBean(info.getServiceName(), info.getProcessQName(), info.getPartnerLinkName(), info.getBinding()));
        }
        return result;
    }

    @Override
    public List<AeProcessDeploymentDetail> getDeployedProcesses() {
        return Arrays.asList(mAdmin.getDeployedProcesses());
    }

    @Override
    public AeProcessInstanceDetail getProcessDetail(long aId) {
        return mAdmin.getProcessDetail(aId);
    }
    
    

}
