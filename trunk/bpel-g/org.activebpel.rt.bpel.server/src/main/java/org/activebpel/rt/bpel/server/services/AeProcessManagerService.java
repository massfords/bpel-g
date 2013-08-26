package org.activebpel.rt.bpel.server.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.AeServiceMap;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

import bpelg.services.processes.AeProcessManager;
import bpelg.services.processes.ProcessStateErrorMessage;
import bpelg.services.processes.StorageErrorMessage;
import bpelg.services.processes.types.GetProcessDeployments;
import bpelg.services.processes.types.GetServiceDeployments;
import bpelg.services.processes.types.ProcessDeployment;
import bpelg.services.processes.types.ProcessDeployments;
import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessInstanceDetail;
import bpelg.services.processes.types.ProcessList;
import bpelg.services.processes.types.ServiceDeployment;
import bpelg.services.processes.types.ServiceDeployments;

public class AeProcessManagerService implements AeProcessManager {

    /**
     * comparator for sorting the deployment detail objects
     */
    private final Comparator<ProcessDeployment> mDeploymentComparator = new Comparator<ProcessDeployment>() {
        public int compare(ProcessDeployment one, ProcessDeployment two) {
            return one.getProcess().getName().getLocalPart().compareToIgnoreCase(
                    two.getProcess().getName().getLocalPart());
        }
    };

    /**
     * comparator for service deployment objects
     */
    private final Comparator<ServiceDeployment> mServiceComparator = new Comparator<ServiceDeployment>() {
        public int compare(ServiceDeployment one, ServiceDeployment two) {
            return one.getService().compareTo(two.getService());
        }
    };

    @Inject
    private IAeProcessManager mProcessManager;

    public IAeProcessManager getProcessManager() {
        return mProcessManager;
    }

    public void setProcessManager(IAeProcessManager aProcessManager) {
        mProcessManager = aProcessManager;
    }

    @Override
    public int removeProcessByQuery(ProcessFilterType aFilter)
            throws StorageErrorMessage {
        try {
            return getProcessManager().removeProcesses(aFilter);
        } catch (AeBusinessProcessException e) {
            throw new StorageErrorMessage(e.getMessage(), e);
        }
    }

    @Override
    public ProcessList getProcesses(ProcessFilterType aBody)
            throws StorageErrorMessage {
        try {
            return getProcessManager().getProcesses(aBody);
        } catch (AeBusinessProcessException e) {
            throw new StorageErrorMessage(e.getMessage(), e);
        }
    }

    @Override
    public void restartProcess(long aProcessId) throws ProcessStateErrorMessage {
        try {
            getProcessManager().getEngine().restartProcess(aProcessId);
        } catch (AeBusinessProcessException e) {
            throw new ProcessStateErrorMessage(e.getMessage(), e);
        }
    }

    @Override
    public void resumeProcess(long aProcessId) throws ProcessStateErrorMessage {
        try {
            getProcessManager().getEngine().resumeProcess(aProcessId);
        } catch (AeBusinessProcessException e) {
            throw new ProcessStateErrorMessage(e.getMessage(), e);
        }
    }

    @Override
    public void terminateProcess(long aProcessId)
            throws ProcessStateErrorMessage {
        try {
            getProcessManager().getEngine().terminateProcess(aProcessId);
        } catch (AeBusinessProcessException e) {
            throw new ProcessStateErrorMessage(e.getMessage(), e);
        }
    }

    @Override
    public void suspendProcess(long aProcessId) throws ProcessStateErrorMessage {
        try {
            getProcessManager().getEngine().suspendProcess(aProcessId);
        } catch (AeBusinessProcessException e) {
            throw new ProcessStateErrorMessage(e.getMessage(), e);
        }
    }

    @Override
    public ProcessInstanceDetail getProcessDetail(long aBody)
            throws StorageErrorMessage {
        return getProcessManager().getProcessInstanceDetails(aBody);
    }

    @Override
    public ServiceDeployments getServiceDeployments(GetServiceDeployments aBody) {
        List<ServiceDeployment> sortedList = AeServiceMap.getServiceEntries();
        Collections.sort(sortedList, mServiceComparator);

        return new ServiceDeployments().withServiceDeployment(sortedList);
    }

    @Override
    public ProcessDeployment getProcessDeploymentByName(QName aBody) {
        ProcessDeployment detail = null;
        try {
            IAeDeploymentProvider deploymentProvider = AeEngineFactory.getBean(IAeDeploymentProvider.class);
            IAeProcessDeployment deploymentPlan = deploymentProvider.findCurrentDeployment(aBody);
            // deployment plan maybe null if it was removed (e.g. .bpr removed.
            // See defect # 1368)
            if (deploymentPlan != null) {
                detail = createProcessDetail(deploymentPlan);
            }
        } catch (AeBusinessProcessException abe) {
            abe.logError();
        }
        return detail;
    }

    @Override
    public ProcessDeployments getProcessDeployments(GetProcessDeployments aBody) {
        IAeDeploymentProvider deploymentProvider = AeEngineFactory.getBean(IAeDeploymentProvider.class);
        List<ProcessDeployment> list = new ArrayList<>();
        for (Iterator iter = deploymentProvider.getDeployedPlans(); iter.hasNext(); ) {
            IAeProcessDeployment deployedProcess = (IAeProcessDeployment) iter.next();
            list.add(createProcessDetail(deployedProcess));
        }
        Collections.sort(list, mDeploymentComparator);
        return new ProcessDeployments().withProcessDeployment(list);
    }

    /**
     * Create the <code>AeProcessDeploymentDetail</code> from the given
     * <code>IAeProcessDeployment</code>.
     *
     * @param aDeployment
     */
    protected ProcessDeployment createProcessDetail(
            IAeProcessDeployment aDeployment) {
        ProcessDeployment detail = new ProcessDeployment().withProcess(
                aDeployment.getPdd()).withSource(aDeployment.getBpelSource());
        return detail;
    }
}
