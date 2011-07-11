// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AeEngineAdministration.java,v 1.69 2007/12/26 17:38:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.coord.AeCoordinationNotFoundException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.impl.AeMonitorStatus;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.list.AeAlarmExt;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeAlarm;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.admin.AeBuildInfo;
import org.activebpel.rt.bpel.server.admin.AeEngineStatus;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.catalog.IAeCatalog;
import org.activebpel.rt.bpel.server.catalog.report.IAeCatalogAdmin;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessList;

/**
 * Provides administration/console support for the engine. This class uses the
 * AeEngineFactory to get access to the engine and deployment descriptor
 * information.
 * 
 * TODO This catches various BusinessProcessExceptions, logs them, and returns
 * null (in several methods). Those exceptions should be propagated to the
 * caller.
 */
public class AeEngineAdministration implements IAeEngineAdministration {
	/** Holds build information. */
	private AeBuildInfo[] mBuildInfo = null;

	/**
	 * Getter for the bpel engine
	 */
	protected AeBpelEngine getBpelEngine() {
		return (AeBpelEngine) AeEngineFactory.getEngine();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getMessageReceivers(org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter)
	 */
	public AeMessageReceiverListResult getMessageReceivers(
			AeMessageReceiverFilter aFilter) {
		try {
			Map<Long, AeProcessDef> processDefMap = new HashMap<Long, AeProcessDef>();

			AeMessageReceiverListResult results = getBpelEngine()
					.getQueueManager().getMessageReceivers(aFilter);
			for (int i = 0; i < results.getResults().length; i++) {
				AeMessageReceiver qObj = results.getResults()[i];
				AeProcessDef def = getProcessDef(qObj.getProcessId(),
						processDefMap);
				if (def != null) {
					String path = def.getLocationPath(qObj
							.getMessageReceiverPathId());
					results.addPathMapping(qObj.getMessageReceiverPathId(),
							path);
				}
			}
			return results;
		} catch (AeBusinessProcessException ex) {
			ex.logError();
			return null;
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getAlarms(org.activebpel.rt.bpel.impl.list.AeAlarmFilter)
	 */
	public AeAlarmListResult<AeAlarmExt> getAlarms(AeAlarmFilter aFilter) {
		try {
			Map<Long, AeProcessDef> processDefMap = new HashMap<Long, AeProcessDef>();

			AeListResult<? extends AeAlarm> results = getBpelEngine().getQueueManager().getAlarms(aFilter);
			List<AeAlarmExt> extList = new ArrayList<AeAlarmExt>();
			for (int i = 0; i < results.getResults().size(); i++) {
				AeAlarm alarm = results.getResults().get(i);

				long pid = alarm.getProcessId();
				AeProcessDef processDef = getProcessDef(pid, processDefMap);
				QName name = null;
				String path = null;
				if (processDef != null) {
					path = processDef.getLocationPath(alarm.getPathId());
					name = processDef.getQName();
				}
				AeAlarmExt aeAlarmExt = new AeAlarmExt(alarm.getProcessId(),
						alarm.getPathId(), alarm.getGroupId(),
						alarm.getAlarmId(), alarm.getDeadline(), name);
				aeAlarmExt.setLocation(path);
				extList.add(aeAlarmExt);
			}
			AeAlarmListResult<AeAlarmExt> result = new AeAlarmListResult<AeAlarmExt>(extList.size(), extList);
			return result;
		} catch (AeBusinessProcessException ex) {
			ex.logError();
			return null;
		}
	}

	/**
	 * Gets the process def from the deployment provider, using the map as a
	 * cache.
	 * 
	 * @param aProcessId
	 * @param aProcessDefMap
	 */
	private AeProcessDef getProcessDef(long aProcessId,
			Map<Long, AeProcessDef> aProcessDefMap) {
		AeProcessDef processDef = (AeProcessDef) aProcessDefMap.get(aProcessId);
		if (processDef == null) {
			try {
				QName processName = getBpelEngine().getProcessManager()
						.getProcessQName(aProcessId);
				IAeProcessPlan plan = AeEngineFactory.getBean(
						IAeDeploymentProvider.class).findDeploymentPlan(
						aProcessId, processName);
				processDef = plan.getProcessDef();
				aProcessDefMap.put(aProcessId, processDef);
			} catch (AeException e) {
				e.logError();
			}
		}
		return processDef;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getBuildInfo()
	 */
	public AeBuildInfo[] getBuildInfo() {
		if (mBuildInfo == null) {
			List<AeBuildInfo> list = createBuildInfo();
			mBuildInfo = list.toArray(new AeBuildInfo[list.size()]);
		}
		return mBuildInfo;
	}

	/**
	 * Creates the list of build info objects.
	 */
	protected List<AeBuildInfo> createBuildInfo() {
		List<AeBuildInfo> list = new ArrayList<AeBuildInfo>();
		list.add(new AeBuildInfo(
				AeMessages.getString("AeEngineAdministration.RT"), org.activebpel.rt.AeBuildNumber.getBuildNumber(), org.activebpel.rt.AeBuildNumber.getBuildDate(), org.activebpel.rt.AeBuildNumber.getVersionNumber())); //$NON-NLS-1$
		list.add(new AeBuildInfo(
				AeMessages.getString("AeEngineAdministration.RT_BPEL"), org.activebpel.rt.bpel.AeBuildNumber.getBuildNumber(), org.activebpel.rt.bpel.AeBuildNumber.getBuildDate(), org.activebpel.rt.bpel.AeBuildNumber.getVersionNumber())); //$NON-NLS-1$
		list.add(new AeBuildInfo(
				AeMessages.getString("AeEngineAdministration.RT_BPEL_SERVER"), org.activebpel.rt.bpel.server.AeBuildNumber.getBuildNumber(), org.activebpel.rt.bpel.server.AeBuildNumber.getBuildDate(), org.activebpel.rt.bpel.server.AeBuildNumber.getVersionNumber())); //$NON-NLS-1$

		AeBuildInfo
				.createBuildInfoFor(
						list,
						"org.activebpel.rt.axis.bpel.AeBuildNumber", AeMessages.getString("AeEngineAdministration.RT_AXIS")); //$NON-NLS-1$ //$NON-NLS-2$

		return list;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getStartDate()
	 */
	public Date getStartDate() {
		return getBpelEngine().getStartDate();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getEngineState()
	 */
	public AeEngineStatus getEngineState() {
		return getBpelEngine().getState();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getMonitorStatus()
	 */
	public AeMonitorStatus getMonitorStatus() {
		return getBpelEngine().getMonitorStatus();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getEngineErrorInfo()
	 */
	public String getEngineErrorInfo() {
		return getBpelEngine().getErrorInfo();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getProcessLog(long)
	 */
	public String getProcessLog(long aProcessId) {
		try {
			return AeEngineFactory.getBean(IAeProcessLogger.class)
					.getAbbreviatedLog(aProcessId).toString();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			e.printStackTrace(pw);
			return sw.toString();
		}
	}

	public ProcessList getProcessList(ProcessFilterType aFilter)
			throws AeBusinessProcessException {
		return getBpelEngine().getProcessManager().getProcesses(aFilter);
	}

	public int getProcessCount(ProcessFilterType aFilter)
			throws AeBusinessProcessException {
		return getBpelEngine().getProcessManager().getProcessCount(aFilter);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getProcessState(long)
	 */
	public String getProcessState(long aPid) throws AeBusinessProcessException {
		Document processState = getBpelEngine().getProcessState(aPid);
		return AeXMLParserBase.documentToString(processState, true);
	}

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
			throws AeBusinessProcessException {
		Document doc = getBpelEngine().getProcessVariable(aPid, aVariablePath);
		return AeXMLParserBase.documentToString(doc, true);
	}

	/**
	 * Overrides method to return location path given the id of the BPEL object
	 * and the process id.
	 * 
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getLocationPathById(long,
	 *      int)
	 */
	public String getLocationPathById(long aProcessId, int aLocationId)
			throws AeBusinessProcessException {
		return getBpelEngine().getLocationPathById(aProcessId, aLocationId);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getCatalogAdmin()
	 */
	public IAeCatalogAdmin getCatalogAdmin() {
		return (IAeCatalogAdmin) AeEngineFactory.getBean(IAeCatalog.class);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#start()
	 */
	public void start() throws AeException {
		AeEngineFactory.start();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#stop()
	 */
	public void stop() throws AeBusinessProcessException {
		getBpelEngine().stop();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#isRunning()
	 */
	public boolean isRunning() {
		return getEngineState() == AeEngineStatus.Running;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#deployNewBpr(java.io.File,
	 *      java.lang.String,
	 *      org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger)
	 */
	public void deployNewBpr(File aBprFile, String aBprFilename,
			IAeDeploymentLogger aLogger) throws AeException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#isInternalWorkManager()
	 */
	public boolean isInternalWorkManager() {
		return AeEngineFactory.isInternalWorkManager();
	}

	/**
	 * Returns the coordination information for the parent process given the
	 * child process id.
	 * 
	 * @param aChildProcessId
	 * @return AeCoordinationDetail of the coordinator or null if not found.
	 * @throws AeException
	 */
	public AeCoordinationDetail getCoordinatorForProcessId(long aChildProcessId)
			throws AeException {
		try {
			return getBpelEngine().getCoordinationManager()
					.getCoordinatorDetail(aChildProcessId);
		} catch (AeCoordinationNotFoundException cnfe) {
			return null;
		}
	}

	/**
	 * Returns a list of AeCoordinationDetail for all subprocess (participants)
	 * given the parent process id.
	 * 
	 * @param aParentProcessId
	 * @throws AeException
	 */
	public List<AeCoordinationDetail> getParticipantForProcessId(long aParentProcessId)
			throws AeException {
		try {
			return getBpelEngine().getCoordinationManager().getParticipantDetail(aParentProcessId);
		} catch (AeCoordinationNotFoundException cnfe) {
			return Collections.<AeCoordinationDetail>emptyList();
		}
	}
}