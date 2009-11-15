package org.activebpel.rt.bpel.server.admin.jmx;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.base64.Base64;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.impl.list.AeAlarmExt;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeCatalogItem;
import org.activebpel.rt.bpel.impl.list.AeCatalogItemDetail;
import org.activebpel.rt.bpel.impl.list.AeCatalogListingFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.server.admin.AeBuildInfo;
import org.activebpel.rt.bpel.server.admin.AeProcessDeploymentDetail;
import org.activebpel.rt.bpel.server.admin.AeQueuedReceiveDetail;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeQName;
import org.activebpel.rt.xml.schema.AeSchemaDuration;

public class AeEngineManagementAdapter implements IAeEngineManagementMXBean {
    
    private static final String CONFIG_ALARM_MAX_WORK_COUNT_PATH = IAeEngineConfiguration.WORK_MANAGER_ENTRY
    + "/" + IAeEngineConfiguration.CHILD_WORK_MANAGERS_ENTRY //$NON-NLS-1$
    + "/" + IAeEngineConfiguration.ALARM_CHILD_WORK_MANAGER_ENTRY //$NON-NLS-1$
    + "/" + IAeEngineConfiguration.MAX_WORK_COUNT_ENTRY; //$NON-NLS-1$

    private static final String CONFIG_B4P_MANAGER_FINALIZATION_DURATION = "CustomManagers/BPEL4PeopleManager/FinalizationDuration"; //$NON-NLS-1$
    private static final String CONFIG_B4P_MANAGER_FINALIZATION_CLASS = "CustomManagers/BPEL4PeopleManager/Class"; //$NON-NLS-1$

    private IAeEngineAdministration mAdmin;
    
    public AeEngineManagementAdapter(IAeEngineAdministration aAdmin) {
        mAdmin = aAdmin;
    }

    @Override
    public List<AeServiceDeploymentBean> getDeployedServices() {
        List<AeServiceDeploymentBean> result = new ArrayList();
        for (IAeServiceDeploymentInfo info : mAdmin.getDeployedServices()) {
            result.add(new AeServiceDeploymentBean(info.getServiceName(), new AeQName(info.getProcessQName()), info.getPartnerLinkName(), info.getBinding()));
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

    @Override
    public List<AeQueuedReceiveDetail> getUnmatchedQueuedReceives() {
        return Arrays.asList(mAdmin.getUnmatchedQueuedReceives());
    }

    @Override
    public AeMessageReceiverListResult getMessageReceivers(long aProcessId, String aPartnerLinkName, String aPortTypeNamespace, String aPortTypeLocalPart,
            String aOperation, int aMaxReturn, int aListStart) {
        AeMessageReceiverFilter filter = new AeMessageReceiverFilter();
        filter.setProcessId(aProcessId);
        filter.setPartnerLinkName(aPartnerLinkName);
        filter.setPortType(new QName(aPortTypeNamespace, aPortTypeLocalPart));
        filter.setOperation(aOperation);
        filter.setMaxReturn(aMaxReturn);
        filter.setListStart(aListStart);
        return mAdmin.getMessageReceivers(filter);
    }

    @Override
    public List<AeAlarmExt> getAlarms(long aProcessId, Date aAlarmFilterStart, Date aAlarmFilterEnd,
            String aProcessNamespace, String aProcessLocalPart, int aMaxReturn, int aListStart) {
        AeAlarmFilter filter = new AeAlarmFilter();
        filter.setProcessId(aProcessId);
        filter.setAlarmFilterStart(aAlarmFilterStart);
        filter.setAlarmFilterEnd(aAlarmFilterEnd);
        filter.setProcessName(new QName(aProcessNamespace, aProcessLocalPart));
        filter.setMaxReturn(aMaxReturn);
        filter.setListStart(aListStart);
        
        return mAdmin.getAlarms(filter).getResults();
    }

    @Override
    public AeBuildInfo[] getBuildInfo() {
        return mAdmin.getBuildInfo();
    }

    @Override
    public String getEngineErrorInfo() {
        return mAdmin.getEngineErrorInfo();
    }

    @Override
    public int getEngineState() {
        return mAdmin.getEngineState();
    }

    @Override
    public int getMonitorStatus() {
        return mAdmin.getMonitorStatus();
    }

    @Override
    public String getProcessLog(long aProcessId) {
        return mAdmin.getProcessLog(aProcessId);
    }

    @Override
    public Date getStartDate() {
        return mAdmin.getStartDate();
    }

    @Override
    public AeProcessListResultBean getProcessList(String aProcessNamespace, String aProcessName, String aProcessGroup, boolean aHidSystemProcessGroup,
            int aProcessState, Date aProcessCreateStart, Date aProcessCreateEnd, Date aProcessCompleteStart, Date aProcessCompleteEnd,
            String aAdvancedQuery, int aPlanId, Date aDeletableDate, long[] aProcessIdRange, int aMaxReturn, int aListStart) throws AeBusinessProcessException {
        AeProcessFilter filter = new AeProcessFilter();
        if (aProcessNamespace != null && aProcessName != null) {
            filter.setProcessName(new QName(aProcessNamespace, aProcessName));
        }
        filter.setProcessGroup(aProcessGroup);
        filter.setHideSystemProcessGroup(aHidSystemProcessGroup);
        filter.setProcessCreateStart(aProcessCreateStart);
        filter.setProcessCreateEnd(aProcessCreateEnd);
        filter.setProcessCompleteStart(aProcessCompleteStart);
        filter.setProcessCompleteEnd(aProcessCompleteEnd);
        filter.setAdvancedQuery(aAdvancedQuery);
        filter.setPlanId(aPlanId);
        filter.setDeletableDate(aDeletableDate);
        filter.setProcessIdRange(aProcessIdRange);
        filter.setMaxReturn(aMaxReturn);
        filter.setListStart(aListStart);
        
        AeProcessListResult processList = mAdmin.getProcessList(filter);
        
        return new AeProcessListResultBean(processList.getTotalRowCount(), processList.getResults(), processList.isCompleteRowCount());
    }

    @Override
    public String getProcessState(long aPid) throws AeBusinessProcessException {
        return mAdmin.getProcessState(aPid);
    }

    @Override
    public String getDeploymentLog() {
        return mAdmin.getDeploymentLog();
    }

    @Override
    public String[] getDeploymentLogListing() {
        return mAdmin.getDeploymentLogListing();
    }

    @Override
    public String getLocationPathById(long aProcessId, int aLocationId) throws AeBusinessProcessException {
        return mAdmin.getLocationPathById(aProcessId, aLocationId);
    }

    @Override
    public String getVariable(long aPid, String aVariablePath) throws AeBusinessProcessException {
        return mAdmin.getVariable(aPid, aVariablePath);
    }

    @Override
    public boolean isInternalWorkManager() {
        return mAdmin.isInternalWorkManager();
    }

    @Override
    public boolean isRunning() {
        return mAdmin.isRunning();
    }

    @Override
    public int removeProcesses(String aProcessNamespace, String aProcessName, String aProcessGroup,
            boolean aHidSystemProcessGroup, int aProcessState, Date aProcessCreateStart, Date aProcessCreateEnd,
            Date aProcessCompleteStart, Date aProcessCompleteEnd, String aAdvancedQuery, int aPlanId,
            Date aDeletableDate, long[] aProcessIdRange)
            throws AeBusinessProcessException {
        AeProcessFilter filter = new AeProcessFilter();
        if (aProcessNamespace != null && aProcessName != null) {
            filter.setProcessName(new QName(aProcessNamespace, aProcessName));
        }
        filter.setProcessGroup(aProcessGroup);
        filter.setHideSystemProcessGroup(aHidSystemProcessGroup);
        filter.setProcessCreateStart(aProcessCreateStart);
        filter.setProcessCreateEnd(aProcessCreateEnd);
        filter.setProcessCompleteStart(aProcessCompleteStart);
        filter.setProcessCompleteEnd(aProcessCompleteEnd);
        filter.setAdvancedQuery(aAdvancedQuery);
        filter.setPlanId(aPlanId);
        filter.setDeletableDate(aDeletableDate);
        filter.setProcessIdRange(aProcessIdRange);
        return mAdmin.removeProcesses(filter);
    }

    @Override
    public void start() throws AeBusinessProcessException {
        mAdmin.start();
    }

    @Override
    public void stop() throws AeBusinessProcessException {
        mAdmin.stop();
    }

    @Override
    public void addURNMapping(String aURN, String aURL) {
        mAdmin.getURNAddressResolver().addMapping(aURN, aURL);
    }

    @Override
    public Map<String, String> getURNMappings() {
        return mAdmin.getURNAddressResolver().getMappings();
    }

    @Override
    public void removeURNMappings(String[] aURN) {
        mAdmin.getURNAddressResolver().removeMappings(aURN);
    }

    @Override
    public AeCoordinationDetail getCoordinatorForProcessId(long aChildProcessId) throws AeException {
        return mAdmin.getCoordinatorForProcessId(aChildProcessId);
    }

    @Override
    public List<AeCoordinationDetail> getParticipantForProcessId(long aParentProcessId) throws AeException {
        return mAdmin.getParticipantForProcessId(aParentProcessId);
    }

    @Override
    public int getCatalogCacheDiskReads() {
        return mAdmin.getCatalogAdmin().getResourceStats().getDiskReads();
    }

    @Override
    public int getCatalogCacheTotalReads() {
        return mAdmin.getCatalogAdmin().getResourceStats().getTotalReads();
    }

    @Override
    public AeCatalogItemDetail getCatalogItemDetail(String aLocationHint) {
        return mAdmin.getCatalogAdmin().getCatalogItemDetail(aLocationHint);
    }

    @Override
    public List<AeCatalogItem> getCatalogListing(String aTypeURI, String aResource, String aNamespace, int aMaxReturn, int aListStart) {
        AeCatalogListingFilter filter = new AeCatalogListingFilter();
        filter.setTypeURI(aTypeURI);
        filter.setResource(aResource);
        filter.setNamespace(aNamespace);
        filter.setMaxReturn(aMaxReturn);
        filter.setListStart(aListStart);
        return mAdmin.getCatalogAdmin().getCatalogListing(filter).getResults();
    }

    @Override
    public int getCatalogCacheSize() {
        return mAdmin.getEngineConfig().getResourceCacheMax();
    }

    @Override
    public String getEngineDescription() {
        return mAdmin.getEngineConfig().getDescription();
    }

    @Override
    public boolean isProcessRestartEnabled() {
        return mAdmin.getEngineConfig().isProcessRestartEnabled();
    }

    @Override
    public int getAlarmMaxWorkCount() {
        int result;
        try
        {
           String entry = (String) mAdmin.getEngineConfig().getEntryByPath(CONFIG_ALARM_MAX_WORK_COUNT_PATH);
           result = Integer.parseInt(entry);
        }
        catch (Exception e)
        {
           AeException.logError(e);

           result = IAeEngineConfiguration.DEFAULT_CHILD_MAX_WORK_COUNT;
        }
        return result;
    }

    @Override
    public int getProcessWorkCount() {
        return mAdmin.getEngineConfig().getProcessWorkCount();
    }

    @Override
    public int getTaskFinalizationDuration() {
        //get duration from entry: CustomManagers/BPEL4PeopleManager/FinalizationDuration
        String durationStr = (String) mAdmin.getEngineConfig().getEntryByPath(CONFIG_B4P_MANAGER_FINALIZATION_DURATION);
        if (AeUtil.isNullOrEmpty(durationStr))
        {
           return 1;
        }
        try
        {
           return new AeSchemaDuration(durationStr).getDays();
        }
        catch (Exception e)
        {
           return 1;
        }
    }

    @Override
    public int getThreadPoolMax() {
        return mAdmin.getEngineConfig().getWorkManagerThreadPoolMax();
    }

    @Override
    public int getThreadPoolMin() {
        return mAdmin.getEngineConfig().getWorkManagerThreadPoolMin();
    }

    @Override
    public int getUnmatchedCorrelatedReceiveTimeout() {
        return mAdmin.getEngineConfig().getUnmatchedCorrelatedReceiveTimeout();
    }

    @Override
    public int getWebServiceInvokeTimeout() {
        return mAdmin.getEngineConfig().getWebServiceInvokeTimeout();
    }

    @Override
    public int getWebServiceReceiveTimeout() {
        return mAdmin.getEngineConfig().getWebServiceReceiveTimeout();
    }

    @Override
    public boolean isAllowCreateXPath() {
        return mAdmin.getEngineConfig().allowCreateXPath();
    }

    @Override
    public boolean isAllowEmptyQuerySelection() {
        return mAdmin.getEngineConfig().allowEmptyQuerySelection();
    }

    @Override
    public boolean isResourceReplaceEnabled() {
        return mAdmin.getEngineConfig().isResourceReplaceEnabled();
    }

    @Override
    public boolean isValidateServiceMessages() {
        return mAdmin.getEngineConfig().validateServiceMessages();
    }

    @Override
    public void setAlarmMaxWorkCount(int aValue) {
        mAdmin.getEngineConfig().getEntryByPath(CONFIG_ALARM_MAX_WORK_COUNT_PATH);
    }

    @Override
    public void setAllowCreateXPath(boolean aAllowedCreateXPath) {
        getMutableConfig().setAllowCreateXPath(aAllowedCreateXPath);
        getMutableConfig().update();
    }

    @Override
    public void setAllowEmptyQuerySelection(boolean aAllowedEmptyQuerySelection) {
        getMutableConfig().setAllowEmptyQuerySelection(aAllowedEmptyQuerySelection);
        getMutableConfig().update();
    }

    @Override
    public void setCatalogCacheSize(int aSize) {
        getMutableConfig().setResourceCacheMax(aSize);
        getMutableConfig().update();
    }

    @Override
    public void setLoggingFilter(String aFilter) {
        getMutableConfig().setLoggingFilter(aFilter);
        getMutableConfig().update();
    }

    @Override
    public String getLoggingFilter() {
        return mAdmin.getEngineConfig().getLoggingFilter();
    }

    @Override
    public void setProcessWorkCount(int aValue) {
        getMutableConfig().setProcessWorkCount(aValue);
        getMutableConfig().update();
    }

    @Override
    public void setResourceReplaceEnabled(boolean aEnabled) {
        getMutableConfig().setResourceReplaceEnabled(aEnabled);
        getMutableConfig().update();
    }

    @Override
    public void setTaskFinalizationDuration(int aDays) {
        if (aDays > 0)
        {
            if (getMutableConfig().getEntryByPath(CONFIG_B4P_MANAGER_FINALIZATION_CLASS) != null) {
               AeSchemaDuration duration = new AeSchemaDuration();
               duration.setDays(aDays);
               String durationStr = duration.toString();
               getMutableConfig().addEntryByPath(CONFIG_B4P_MANAGER_FINALIZATION_DURATION, durationStr);
               getMutableConfig().update();
            }
        }
    }

    @Override
    public void setThreadPoolMax(int aValue) {
        getMutableConfig().setWorkManagerThreadPoolMax(aValue);
        getMutableConfig().update();
    }

    @Override
    public void setThreadPoolMin(int aValue) {
        getMutableConfig().setWorkManagerThreadPoolMin(aValue);
        getMutableConfig().update();
    }

    @Override
    public void setUnmatchedCorrelatedReceiveTimeout(int aTimeout) {
        getMutableConfig().setUnmatchedCorrelatedReceiveTimeout(aTimeout);
        getMutableConfig().update();
    }

    @Override
    public void setValidateServiceMessages(boolean aValidateServiceMessages) {
        getMutableConfig().setValidateServiceMessages(aValidateServiceMessages);
        getMutableConfig().update();
    }

    @Override
    public void setWebServiceInvokeTimeout(int aTimeout) {
        getMutableConfig().setWebServiceInvokeTimeout(aTimeout);
        getMutableConfig().update();
    }

    @Override
    public void setWebServiceReceiveTimeout(int aTimeout) {
        getMutableConfig().setWebServiceReceiveTimeout(aTimeout);
        getMutableConfig().update();
    }

    private IAeUpdatableEngineConfig getMutableConfig() {
        IAeUpdatableEngineConfig config =  (IAeUpdatableEngineConfig) mAdmin.getEngineConfig();
        return config;
    }

    @Override
    public AeProcessDeploymentDetail getDeployedProcessDetail(String aNamespace, String aName) {
        return mAdmin.getDeployedProcessDetail(new QName(aNamespace, aName));
    }

    @Override
    public int getProcessCount(String aProcessNamespace, String aProcessName, String aProcessGroup,
            boolean aHidSystemProcessGroup, int aProcessState, Date aProcessCreateStart, Date aProcessCreateEnd,
            Date aProcessCompleteStart, Date aProcessCompleteEnd, String aAdvancedQuery, int aPlanId,
            Date aDeletableDate, long[] aProcessIdRange, int aMaxReturn, int aListStart)
            throws AeBusinessProcessException {
        AeProcessFilter filter = new AeProcessFilter();
        if (aProcessNamespace != null && aProcessName != null) {
            filter.setProcessName(new QName(aProcessNamespace, aProcessName));
        }
        filter.setProcessGroup(aProcessGroup);
        filter.setHideSystemProcessGroup(aHidSystemProcessGroup);
        filter.setProcessCreateStart(aProcessCreateStart);
        filter.setProcessCreateEnd(aProcessCreateEnd);
        filter.setProcessCompleteStart(aProcessCompleteStart);
        filter.setProcessCompleteEnd(aProcessCompleteEnd);
        filter.setAdvancedQuery(aAdvancedQuery);
        filter.setPlanId(aPlanId);
        filter.setDeletableDate(aDeletableDate);
        filter.setProcessIdRange(aProcessIdRange);
        filter.setMaxReturn(aMaxReturn);
        filter.setListStart(aListStart);
        
        return mAdmin.getProcessCount(filter);
    }

    @Override
    public AeProcessListResultBean getProcessList(AeProcessFilter aFilter) throws AeBusinessProcessException {
        AeProcessListResult processList = mAdmin.getProcessList(aFilter);
        return new AeProcessListResultBean(processList.getTotalRowCount(), processList.getResults(), processList.isCompleteRowCount());
    }

    @Override
    public String getRawConfig() {
        AeDefaultEngineConfiguration engineConfig = (AeDefaultEngineConfiguration) mAdmin.getEngineConfig();
        StringWriter sw = new StringWriter();
        try {
            engineConfig.save(sw);
        } catch (AeException e) {
            // FIXME handle exception
            e.printStackTrace();
        }
        return sw.toString();
    }

    @Override
    public void setRawConfig(String aRaw) {
        try {
            AeDefaultEngineConfiguration engineConfig = (AeDefaultEngineConfiguration) mAdmin.getEngineConfig();
            Map entries = AeConfigurationUtil.loadConfig(new StringReader(aRaw));
            engineConfig.setEntries(entries);
            engineConfig.update();
        } catch (AeException e) {
            // FIXME handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void resumeProcess(long aPid) throws AeBusinessProcessException {
        AeEngineFactory.getEngine().resumeProcess(aPid);
    }

    @Override
    public void suspendProcess(long aPid) throws AeBusinessProcessException {
        AeEngineFactory.getEngine().suspendProcess(aPid);
    }

    @Override
    public void terminateProcess(long aPid) throws AeBusinessProcessException {
        AeEngineFactory.getEngine().terminateProcess(aPid);
    }

    @Override
    public void restartProcess(long aPid) throws AeBusinessProcessException {
        AeEngineFactory.getEngine().restartProcess(aPid);
    }

    @Override
    public String getCompiledProcessDef(long aProcessId, AeQName aName) throws AeBusinessProcessException {
        AeProcessDef def = null;
        if (aProcessId <= 0) {
            def = AeEngineFactory.getDeploymentProvider().findCurrentDeployment(aName.toQName()).getProcessDef();
        } else {
            def = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(aProcessId, aName.toQName()).getProcessDef();
        }
        byte[] b = AeUtil.serializeObject(def);
        String s = Base64.encodeBytes(b);
        return s;
    }

    @Override
    public String getStorageError() {
        return AeEngineFactory.getPersistentStoreError();
    }

    @Override
    public void initializeStorage() throws AeStorageException {
        AeEngineFactory.initializePersistentStoreFactory();
    }

    @Override
    public boolean isEngineStorageReady() {
        return AeEngineFactory.isEngineStorageReady();
    }

    @Override
    public boolean isRestartable(long aPid) {
        try {
            return AeEngineFactory.getEngine().isRestartable(aPid);
        } catch (AeBusinessProcessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
