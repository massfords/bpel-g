package org.activebpel.rt.bpel.server.admin.jmx;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeCatalogItemDetail;
import org.activebpel.rt.bpel.impl.list.AeCatalogListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.server.admin.AeBuildInfo;
import org.activebpel.rt.bpel.server.admin.AeProcessDeploymentDetail;
import org.activebpel.rt.bpel.server.admin.AeQueuedReceiveDetail;

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

    /**
     * Gets a list of the unmatched inbound queued receives from the engine's
     * queue.
     */
    public List<AeQueuedReceiveDetail> getUnmatchedQueuedReceives();

    /**
     * Gets a listing of the queued message receivers from the engine's queue.
     */
    public AeMessageReceiverListResult getMessageReceivers(long aProcessId, String aPartnerLinkName,
            String aPortTypeNamespace, String aPortTypeLocalPart, String aOperation, int aMaxReturn, int aListStart);

    /**
     * Gets a listing of alarms matching the passed filter.
     */
    public AeAlarmListResult getAlarms(long aProcessId, Date aAlarmFilterStart, Date aAlarmFilterEnd, String aProcessNamespace, String aProcessLocalPart, int aMaxReturn, int aListStart);

    /**
     * Gets the build info for the libraries currently in use.
     */
    public AeBuildInfo[] getBuildInfo();

    /**
     * Gets the date/time the engine started
     */
    public Date getStartDate();

    /**
     * Returns the current state of the engine.
     */
    public int getEngineState();

    /**
     * Returns the current monitor state of the engine.
     */
    public int getMonitorStatus();
    
    /**
     * Returns an error message if the state is ERROR, null otherwise.
     */
    public String getEngineErrorInfo();

    /**
     * Gets the log for the given process
     * @param aProcessId
     */
    public String getProcessLog(long aProcessId);
    
    /**
     * Returns a list of processes currently running on the BPEL engine. 
     */
    public AeProcessListResult getProcessList(String aProcessNamespace, String aProcessName, String aProcessGroup,
            boolean aHidSystemProcessGroup, int aProcessState, Date aProcessCreateStart, Date aProcessCreateEnd,
            Date aProcessCompleteStart, Date aProcessCompleteEnd, String aAdvancedQuery, int aPlanId,
            Date aDeletableDate, long[] aProcessIdRange, int aMaxReturn, int aListStart) throws AeBusinessProcessException;;

    /**
     * Returns the state of the process specified by the given process ID.
     * @param aPid the ID of the process we want state information for.
     * @throws AeBusinessProcessException
     */
    public String getProcessState(long aPid) throws AeBusinessProcessException;

    /**
     * Returns process variable for the specified process and the variable
     * location path.
     * @param aPid the ID of the process.
     * @param aVariablePath location path of the variable
     * @throws AeBusinessProcessException
     */
    public String getVariable(long aPid, String aVariablePath) throws AeBusinessProcessException;

    /**
     * Returns the locationPath string given the locationId and the processId
     * @param aProcessId process id
     * @param aLocationId location id of the BPEL object.
     * @throws AeBusinessProcessException
     */
    public String getLocationPathById(long aProcessId, int aLocationId) throws AeBusinessProcessException;
//
//    /**
//     * Returns the current engine configuration.
//     */
//    // FIXME (admin) replace all of these calls with a get/set of config as string
//    public IAeEngineConfiguration getEngineConfig();
    
    public AeCatalogItemDetail getCatalogItemDetail(String aLocationHint);
    public int getCatalogCacheDiskReads();
    public int getCatalogCacheTotalReads();
    public AeCatalogListResult getCatalogListing(String aTypeURI, String aResource, String aNamespace);
    public int getCatalogCacheSize();
    public void setCatalogCacheSize(int aSize);

    
    /**
     * Returns a listing of any deployment log file names or null if none are
     * found.
     */
    public String[] getDeploymentLogListing();

    /**
     * Return the log file contents.
     */
    public String getDeploymentLog();

    /**
     * Starts the engine.
     * @throws AeBusinessProcessException
     */
    public void start() throws AeBusinessProcessException;

    /**
     * Stops the engine.
     * @throws AeBusinessProcessException
     */
    public void stop() throws AeBusinessProcessException;

    /**
     * Returns true if the engine is currently running.
     */
    public boolean isRunning();

    /**
     * Removes processes based upon filter specification and returns the number
     * of processes removed.
     */
    public int removeProcesses(String aProcessNamespace, String aProcessName, String aProcessGroup,
            boolean aHidSystemProcessGroup, int aProcessState, Date aProcessCreateStart, Date aProcessCreateEnd,
            Date aProcessCompleteStart, Date aProcessCompleteEnd, String aAdvancedQuery, int aPlanId,
            Date aDeletableDate, long[] aProcessIdRange) throws AeBusinessProcessException;;

     public void addURNMapping(String aURN, String aURL);
     public Map<String,String> getURNMappings();
     public void removeURNMappings(String[] aURN);
            
            
    /**
     * Returns True if using internal WorkManager or False if using server implementation.
     */
    public boolean isInternalWorkManager();

    /**
     * Returns the coordination information for the parent process given the child process id.
     * @param aChildProcessId
     * @return AeCoordinationDetail of the coordinator or null if not found.
     * @throws AeException
     */
    public AeCoordinationDetail getCoordinatorForProcessId(long aChildProcessId) throws AeException;

    /**
     * Returns a list of AeCoordinationDetail for all subprocess (participants) given the parent process id.
     * @param aParentProcessId
     * @throws AeException
     */
    public List<AeCoordinationDetail> getParticipantForProcessId(long aParentProcessId) throws AeException;
    
    public String getEngineDescription();
    public boolean isProcessRestartEnabled();
    
    public void setAllowCreateXPath( boolean aAllowedCreateXPath );
    public void setLoggingFilter( String aFilter );
    public void setAllowEmptyQuerySelection( boolean aAllowedEmptyQuerySelection );
    public void setValidateServiceMessages( boolean aValidateServiceMessages );
    public void setResourceReplaceEnabled( boolean aEnabled );
    public void setUnmatchedCorrelatedReceiveTimeout( int aTimeout );
    public void setWebServiceInvokeTimeout(int aTimeout);
    public void setWebServiceReceiveTimeout(int aTimeout);
    public void setThreadPoolMin( int aValue );
    public void setThreadPoolMax( int aValue );
    public void setProcessWorkCount( int aValue );
    public void setAlarmMaxWorkCount(int aValue);
    public void setTaskFinalizationDuration(int aDays);

    public boolean isAllowCreateXPath();
    public String getLoggingFilter();
    public boolean isAllowEmptyQuerySelection();
    public boolean isValidateServiceMessages();
    public boolean isResourceReplaceEnabled();
    public int getUnmatchedCorrelatedReceiveTimeout();
    public int getWebServiceInvokeTimeout();
    public int getWebServiceReceiveTimeout();
    public int getThreadPoolMin();
    public int getThreadPoolMax();
    public int getProcessWorkCount();
    public int getAlarmMaxWorkCount();
    public int getTaskFinalizationDuration();
}
