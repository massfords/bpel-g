package org.activebpel.rt.bpel.server.admin.jmx;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.impl.AeMonitorStatus;
import org.activebpel.rt.bpel.impl.list.AeAlarmExt;
import org.activebpel.rt.bpel.impl.list.AeCatalogItem;
import org.activebpel.rt.bpel.impl.list.AeCatalogItemDetail;
import org.activebpel.rt.bpel.server.admin.AeBuildInfo;
import org.activebpel.rt.bpel.server.admin.AeEngineStatus;
import org.activebpel.rt.bpel.server.admin.AeQueuedReceiveDetail;
import org.activebpel.rt.xml.AeQName;

import bpelg.services.processes.types.ProcessFilterType;

public interface IAeEngineManagementMXBean {

	// FIXME ! move all of the listing calls to services
	
	/**
	 * Gets a list of the unmatched inbound queued receives from the engine's
	 * queue.
	 */
	public List<AeQueuedReceiveDetail> getUnmatchedQueuedReceives();

	/**
	 * Gets a listing of the queued message receivers from the engine's queue.
	 */
	public List<AeMessageReceiverBean> getMessageReceivers(long aProcessId,
			String aPartnerLinkName, String aPortTypeNamespace,
			String aPortTypeLocalPart, String aOperation, int aMaxReturn,
			int aListStart);

	/**
	 * Gets a listing of alarms matching the passed filter.
	 */
	public List<AeAlarmExt> getAlarms(long aProcessId, Date aAlarmFilterStart,
			Date aAlarmFilterEnd, String aProcessNamespace,
			String aProcessLocalPart, int aMaxReturn, int aListStart);

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
	public AeEngineStatus getEngineState();

	/**
	 * Returns the current monitor state of the engine.
	 */
	public AeMonitorStatus getMonitorStatus();

	/**
	 * Returns an error message if the state is ERROR, null otherwise.
	 */
	public String getEngineErrorInfo();

	/**
	 * Gets the log for the given process
	 * 
	 * @param aProcessId
	 */
	public String getProcessLog(long aProcessId);

	public AeProcessLogPart getProcessLogPart(long aProcessId, int aPart)
			throws Exception;

	public int getProcessCount(ProcessFilterType aFilter)
			throws AeBusinessProcessException;

	/**
	 * Returns the state of the process specified by the given process ID.
	 * 
	 * @param aPid
	 *            the ID of the process we want state information for.
	 * @throws AeBusinessProcessException
	 */
	public String getProcessState(long aPid) throws AeBusinessProcessException;

	/**
	 * Returns process variable for the specified process and the variable
	 * location path.
	 * 
	 * @param aPid
	 *            the ID of the process.
	 * @param aVariablePath
	 *            location path of the variable
	 * @throws AeBusinessProcessException
	 */
	public String getVariable(long aPid, String aVariablePath)
			throws AeBusinessProcessException;

	/**
	 * Returns the locationPath string given the locationId and the processId
	 * 
	 * @param aProcessId
	 *            process id
	 * @param aLocationId
	 *            location id of the BPEL object.
	 * @throws AeBusinessProcessException
	 */
	public String getLocationPathById(long aProcessId, int aLocationId)
			throws AeBusinessProcessException;

	public AeCatalogItemDetail getCatalogItemDetail(String aLocationHint);

	public long getCacheMisses();

	public long getCacheHits();

	public List<AeCatalogItem> getCatalogListing(String aTypeURI,
			String aResource, String aNamespace, int aMaxReturn, int aListStart);

	/**
	 * Starts the engine.
	 * 
	 * @throws AeException
	 */
	public void start() throws AeException;

	/**
	 * Stops the engine.
	 * 
	 * @throws AeBusinessProcessException
	 */
	public void stop() throws AeBusinessProcessException;

	/**
	 * Returns true if the engine is currently running.
	 */
	public boolean isRunning();

	public void addURNMapping(String aURN, String aURL);

	public Map<String, String> getURNMappings();

	public void removeURNMappings(String[] aURN);

	/**
	 * Returns True if using internal WorkManager or False if using server
	 * implementation.
	 */
	public boolean isInternalWorkManager();

	/**
	 * Returns the coordination information for the parent process given the
	 * child process id.
	 * 
	 * @param aChildProcessId
	 * @return AeCoordinationDetail of the coordinator or null if not found.
	 * @throws AeException
	 */
	public AeCoordinationDetail getCoordinatorForProcessId(long aChildProcessId)
			throws AeException;

	/**
	 * Returns a list of AeCoordinationDetail for all subprocess (participants)
	 * given the parent process id.
	 * 
	 * @param aParentProcessId
	 * @throws AeException
	 */
	public List<AeCoordinationDetail> getParticipantForProcessId(
			long aParentProcessId) throws AeException;

	public boolean isProcessRestartEnabled();

	public AeProcessListResultBean getProcessList(ProcessFilterType aFilter)
			throws AeBusinessProcessException;

	public boolean isRestartable(long aPid);

	public String getCompiledProcessDef(long aProcessId, AeQName aName)
			throws AeBusinessProcessException;

	// move all of these methods to some config service interface
	public String getEngineDescription();

	public int getCatalogCacheSize();

	public void setCatalogCacheSize(int aSize);

	public void setAllowCreateXPath(boolean aAllowedCreateXPath);

	public void setAllowEmptyQuerySelection(boolean aAllowedEmptyQuerySelection);

	public void setValidateServiceMessages(boolean aValidateServiceMessages);

	public void setResourceReplaceEnabled(boolean aEnabled);

	public void setUnmatchedCorrelatedReceiveTimeoutMillis(long aTimeout);

	public void setWebServiceInvokeTimeout(int aTimeout);

	public void setWebServiceReceiveTimeout(int aTimeout);

	public void setThreadPoolMin(int aValue);

	public void setThreadPoolMax(int aValue);

	public void setProcessWorkCount(int aValue);

	public void setAlarmMaxWorkCount(int aValue);

	public boolean isAllowCreateXPath();

	public boolean isAllowEmptyQuerySelection();

	public boolean isValidateServiceMessages();

	public boolean isResourceReplaceEnabled();

	public long getUnmatchedCorrelatedReceiveTimeoutMillis();

	public int getWebServiceInvokeTimeout();

	public int getWebServiceReceiveTimeout();

	public int getThreadPoolMin();

	public int getThreadPoolMax();

	public int getProcessWorkCount();

	public int getAlarmMaxWorkCount();
}
