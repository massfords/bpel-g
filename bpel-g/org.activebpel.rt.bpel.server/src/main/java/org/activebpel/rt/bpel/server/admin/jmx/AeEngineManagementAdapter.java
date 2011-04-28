package org.activebpel.rt.bpel.server.admin.jmx;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.base64.Base64;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AePreferences;
import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.impl.list.AeAlarmExt;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeCatalogItem;
import org.activebpel.rt.bpel.impl.list.AeCatalogItemDetail;
import org.activebpel.rt.bpel.impl.list.AeCatalogListingFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.admin.AeBuildInfo;
import org.activebpel.rt.bpel.server.admin.AeProcessDeploymentDetail;
import org.activebpel.rt.bpel.server.admin.AeQueuedReceiveDetail;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.IAeProcessLogger;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeQName;

import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessList;

public class AeEngineManagementAdapter implements IAeEngineManagementMXBean {
    
    private IAeEngineAdministration mAdmin;
    
    public AeEngineManagementAdapter(IAeEngineAdministration aAdmin) {
        mAdmin = aAdmin;
    }

    public List<AeServiceDeploymentBean> getDeployedServices() {
        List<AeServiceDeploymentBean> result = new ArrayList();
        for (IAeServiceDeploymentInfo info : mAdmin.getDeployedServices()) {
            result.add(new AeServiceDeploymentBean(info.getServiceName(), new AeQName(info.getProcessQName()), info.getPartnerLinkName(), info.getBinding().toString()));
        }
        return result;
    }

    public List<AeProcessDeploymentDetail> getDeployedProcesses() {
        return Arrays.asList(mAdmin.getDeployedProcesses());
    }

    public List<AeQueuedReceiveDetail> getUnmatchedQueuedReceives() {
        return Arrays.asList(mAdmin.getUnmatchedQueuedReceives());
    }

    public List<AeMessageReceiverBean> getMessageReceivers(long aProcessId, String aPartnerLinkName, String aPortTypeNamespace, String aPortTypeLocalPart,
            String aOperation, int aMaxReturn, int aListStart) {
        AeMessageReceiverFilter filter = new AeMessageReceiverFilter();
        filter.setProcessId(aProcessId);
        filter.setPartnerLinkName(aPartnerLinkName);
        if (AeUtil.notNullOrEmpty(aPortTypeNamespace) && AeUtil.notNullOrEmpty(aPortTypeLocalPart))
            filter.setPortType(new QName(aPortTypeNamespace, aPortTypeLocalPart));
        filter.setOperation(aOperation);
        filter.setMaxReturn(aMaxReturn);
        filter.setListStart(aListStart);
        AeMessageReceiverListResult messageReceivers = mAdmin.getMessageReceivers(filter);
        List<AeMessageReceiverBean> receivers = new ArrayList<AeMessageReceiverBean>();
        for(AeMessageReceiver receiver : messageReceivers.getResults()) {
            Map<AeQName,String> correlation = null;
            if (receiver.isCorrelated()) {
                correlation = new HashMap<AeQName,String>();
                for(Map.Entry<QName,String> entry : receiver.getCorrelation().entrySet()) {
                    correlation.put(new AeQName(entry.getKey()), entry.getValue());
                }
            }
            receivers.add(new AeMessageReceiverBean(receiver.getProcessId(), 
                    receiver.getPartnerLinkName(),
                    new AeQName(receiver.getPortType()),
                    receiver.getOperation(),
                    correlation,
                    receiver.getMessageReceiverPathId()));
        }
        return receivers;
    }

    public List<AeAlarmExt> getAlarms(long aProcessId, Date aAlarmFilterStart, Date aAlarmFilterEnd,
            String aProcessNamespace, String aProcessLocalPart, int aMaxReturn, int aListStart) {
        AeAlarmFilter filter = new AeAlarmFilter();
        filter.setProcessId(aProcessId);
        filter.setAlarmFilterStart(aAlarmFilterStart);
        filter.setAlarmFilterEnd(aAlarmFilterEnd);
        if (AeUtil.notNullOrEmpty(aProcessNamespace) &&  AeUtil.notNullOrEmpty(aProcessLocalPart))
            filter.setProcessName(new QName(aProcessNamespace, aProcessLocalPart));
        filter.setMaxReturn(aMaxReturn);
        filter.setListStart(aListStart);
        
        return mAdmin.getAlarms(filter).getResults();
    }

    public AeBuildInfo[] getBuildInfo() {
        return mAdmin.getBuildInfo();
    }

    public String getEngineErrorInfo() {
        return mAdmin.getEngineErrorInfo();
    }

    public int getEngineState() {
        return mAdmin.getEngineState();
    }

    public int getMonitorStatus() {
        return mAdmin.getMonitorStatus();
    }

    public String getProcessLog(long aProcessId) {
        return mAdmin.getProcessLog(aProcessId);
    }
    
    public AeProcessLogPart getProcessLogPart(long aProcessId, int aPart) throws Exception {

        AeProcessLogPart part = new AeProcessLogPart();
        part.setPart(aPart);
        
        // get a reader onto the log
        Reader reader = AeEngineFactory.getBean(IAeProcessLogger.class).getFullLog(aProcessId);
        
        skipAndRead(part, reader, AeProcessLogPart.PART_SIZE);
        return part;
    }

    /**
     * Skips to where we want to be in the reader and starts reading til the buffer is filled.
     * @param aPart
     * @param aReader
     * @param aPartSize
     * @throws IOException
     */
    protected static void skipAndRead(AeProcessLogPart aPart, Reader aReader, int aPartSize)
            throws IOException {
        aPart.setLog(null);
        try {
            // skip to where we want to be in the log
            int skipCount = aPart.getPart() * aPartSize;
            long skipped = aReader.skip(skipCount);
            
            // if we don't skip to that point, then there's nothing to read here
            if (skipped != skipCount) {
                aPart.setMoreAvailable(false);
            } else {
                // fill the buffer for the part
                char[] buffer = new char[aPartSize];
                int read = aReader.read(buffer);
                if (read > 0) {
                    aPart.setLog(new String(buffer, 0, read));
                    // signal that there's more to read if we didn't fill the buffer
                    aPart.setMoreAvailable(read == buffer.length);
                } else {
                    aPart.setMoreAvailable(false);
                }
            }
        } finally {
            AeCloser.close(aReader);
        }
    }

    public Date getStartDate() {
        return mAdmin.getStartDate();
    }

    public String getProcessState(long aPid) throws AeBusinessProcessException {
        return mAdmin.getProcessState(aPid);
    }

    public String getLocationPathById(long aProcessId, int aLocationId) throws AeBusinessProcessException {
        return mAdmin.getLocationPathById(aProcessId, aLocationId);
    }

    public String getVariable(long aPid, String aVariablePath) throws AeBusinessProcessException {
        return mAdmin.getVariable(aPid, aVariablePath);
    }

    public boolean isInternalWorkManager() {
        return mAdmin.isInternalWorkManager();
    }

    public boolean isRunning() {
        return mAdmin.isRunning();
    }

    public void start() throws AeException {
        mAdmin.start();
    }

    public void stop() throws AeBusinessProcessException {
        mAdmin.stop();
    }

    public void addURNMapping(String aURN, String aURL) {
        mAdmin.getURNAddressResolver().addMapping(aURN, aURL);
    }

    public Map<String, String> getURNMappings() {
        return mAdmin.getURNAddressResolver().getMappings();
    }

    public void removeURNMappings(String[] aURN) {
        mAdmin.getURNAddressResolver().removeMappings(aURN);
    }

    public AeCoordinationDetail getCoordinatorForProcessId(long aChildProcessId) throws AeException {
        return mAdmin.getCoordinatorForProcessId(aChildProcessId);
    }

    public List<AeCoordinationDetail> getParticipantForProcessId(long aParentProcessId) throws AeException {
        return mAdmin.getParticipantForProcessId(aParentProcessId);
    }

    public long getCacheMisses() {
        return mAdmin.getCatalogAdmin().getCacheStatistics().getCacheMisses();
    }

    public long getCacheHits() {
        return mAdmin.getCatalogAdmin().getCacheStatistics().getCacheHits();
    }

    public AeCatalogItemDetail getCatalogItemDetail(String aLocationHint) {
        return mAdmin.getCatalogAdmin().getCatalogItemDetail(aLocationHint);
    }

    public List<AeCatalogItem> getCatalogListing(String aTypeURI, String aResource, String aNamespace, int aMaxReturn, int aListStart) {
        AeCatalogListingFilter filter = new AeCatalogListingFilter();
        filter.setTypeURI(aTypeURI);
        filter.setResource(aResource);
        filter.setNamespace(aNamespace);
        filter.setMaxReturn(aMaxReturn);
        filter.setListStart(aListStart);
        return mAdmin.getCatalogAdmin().getCatalogListing(filter).getResults();
    }

    public int getCatalogCacheSize() {
        return AePreferences.getResourceCacheSize();
    }

    public String getEngineDescription() {
        return "bpel-g";
    }

    public boolean isProcessRestartEnabled() {
        return AePreferences.isRestartEnabled();
    }

    public int getAlarmMaxWorkCount() {
    	return AePreferences.getAlarmMaxCount();
    }

    public int getProcessWorkCount() {
        return AePreferences.getProcessWorkCount();
    }

    public int getThreadPoolMax() {
        return AePreferences.getThreadPoolMax();
    }

    public int getThreadPoolMin() {
        return AePreferences.getThreadPoolMin();
    }

    public long getUnmatchedCorrelatedReceiveTimeoutMillis() {
        return AePreferences.getUnmatchedCorrelatedReceiveTimeoutMillis();
    }

    public int getWebServiceInvokeTimeout() {
        return AePreferences.getSendTimeout();
    }

    public int getWebServiceReceiveTimeout() {
        return AePreferences.getReceiveTimeout();
    }

    public boolean isAllowCreateXPath() {
        return AePreferences.isAllowCreateXpath();
    }

    public boolean isAllowEmptyQuerySelection() {
        return AePreferences.isAllowEmptyQuerySelection();
    }

    public boolean isResourceReplaceEnabled() {
        return AePreferences.isResourceReplaceEnabled();
    }

    public boolean isValidateServiceMessages() {
        return AePreferences.isValidateServiceMessages();
    }

    public void setAlarmMaxWorkCount(int aValue) {
        AePreferences.setAlarmMaxCount(aValue);
    }

    public void setAllowCreateXPath(boolean aAllowedCreateXPath) {
    	AePreferences.setAllowCreateXpath(aAllowedCreateXPath);
    }

    public void setAllowEmptyQuerySelection(boolean aAllowedEmptyQuerySelection) {
    	AePreferences.setAllowEmptyQuerySelection(aAllowedEmptyQuerySelection);
    }

    public void setCatalogCacheSize(int aSize) {
    	AePreferences.setResourceCacheSize(aSize);
    }

    public void setProcessWorkCount(int aValue) {
        AePreferences.setProcessWorkCount(aValue);
    }

    public void setResourceReplaceEnabled(boolean aEnabled) {
    	AePreferences.setResourceReplaceEnabled(aEnabled);
    }

    public void setThreadPoolMax(int aValue) {
    	AePreferences.setThreadPoolMax(aValue);
    }

    public void setThreadPoolMin(int aValue) {
    	AePreferences.setThreadPoolMin(aValue);
    }

    public void setUnmatchedCorrelatedReceiveTimeoutMillis(long aTimeout) {
    	AePreferences.setUnmatchedCorrelatedReceiveTimeoutMillis(aTimeout);
    }

    public void setValidateServiceMessages(boolean aValidateServiceMessages) {
    	AePreferences.setValidateServiceMessages(aValidateServiceMessages);
    }

    public void setWebServiceInvokeTimeout(int aTimeout) {
    	AePreferences.setSendTimeout(aTimeout);
    }

    public void setWebServiceReceiveTimeout(int aTimeout) {
    	AePreferences.setReceiveTimeout(aTimeout);
    }

    public AeProcessDeploymentDetail getDeployedProcessDetail(String aNamespace, String aName) {
        return mAdmin.getDeployedProcessDetail(new QName(aNamespace, aName));
    }

    public int getProcessCount(ProcessFilterType aFilter)
            throws AeBusinessProcessException {
        return mAdmin.getProcessCount(aFilter);
    }

    public AeProcessListResultBean getProcessList(ProcessFilterType aFilter) throws AeBusinessProcessException {
        ProcessList processList = mAdmin.getProcessList(aFilter);
        return new AeProcessListResultBean(processList.getTotalRowCount(), processList.getProcessInstanceDetail(), processList.isComplete());
    }

    public String getCompiledProcessDef(long aProcessId, AeQName aName) throws AeBusinessProcessException {
        AeProcessDef def = null;
        if (aProcessId <= 0) {
            def = AeEngineFactory.getBean(IAeDeploymentProvider.class).findCurrentDeployment(aName.toQName()).getProcessDef();
        } else {
            def = AeEngineFactory.getBean(IAeDeploymentProvider.class).findDeploymentPlan(aProcessId, aName.toQName()).getProcessDef();
        }
        byte[] b = AeUtil.serializeObject(def);
        String s = Base64.encodeBytes(b);
        return s;
    }

    public boolean isRestartable(long aPid) {
        try {
            return AeEngineFactory.getEngine().isRestartable(aPid);
        } catch (AeBusinessProcessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
