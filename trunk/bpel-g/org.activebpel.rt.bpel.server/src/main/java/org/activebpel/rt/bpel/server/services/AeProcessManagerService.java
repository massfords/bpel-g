package org.activebpel.rt.bpel.server.services;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;

import bpelg.services.processes.AeProcessManager;
import bpelg.services.processes.ProcessStateErrorMessage;
import bpelg.services.processes.StorageErrorMessage;
import bpelg.services.processes.types.GetInboundMessages;
import bpelg.services.processes.types.GetProcessDeployments;
import bpelg.services.processes.types.GetServiceDeployments;
import bpelg.services.processes.types.InboundMessages;
import bpelg.services.processes.types.ProcessDeployment;
import bpelg.services.processes.types.ProcessDeployments;
import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessInstanceDetail;
import bpelg.services.processes.types.ProcessList;
import bpelg.services.processes.types.ServiceDeployments;

public class AeProcessManagerService implements AeProcessManager {

    private IAeProcessManager mProcessManager;
    private IAeEngineAdministration mEngineAdmin;

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
        return getEngineAdmin().getDeployedServices();
    }

    public IAeEngineAdministration getEngineAdmin() {
        return mEngineAdmin;
    }

    public void setEngineAdmin(IAeEngineAdministration aEngineAdmin) {
        mEngineAdmin = aEngineAdmin;
    }

    @Override
    public ProcessDeployment getProcessDeploymentByName(QName aBody) {
        return getEngineAdmin().getDeployedProcessDetail(aBody);
    }

    @Override
    public ProcessDeployments getProcessDeployments(GetProcessDeployments aBody) {
        return getEngineAdmin().getDeployedProcesses();
    }

    @Override
    public InboundMessages getInboundMessages(GetInboundMessages aBody) {
        return null;
    }
}
