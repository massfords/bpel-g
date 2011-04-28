// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/IAeEngineAdministration.java,v 1.36 2008/02/17 21:38:54 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.server.catalog.report.IAeCatalogAdmin;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.bpel.urn.IAeURNResolver;

import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessList;

/**
 * Interface for engine administration/console support
 */
public interface IAeEngineAdministration
{
	// FIXME enum
   /** engine state constants */
   public static final int CREATED      = 0;
   public static final int STARTING     = 1;
   public static final int RUNNING      = 2;
   public static final int STOPPING     = 3;
   public static final int STOPPED      = 4;
   public static final int SHUTTINGDOWN = 5;
   public static final int SHUTDOWN     = 6;
   public static final int ERROR        = 7;

   /**
    * Gets the details for all of the deployed services.
    */
   public IAeServiceDeploymentInfo[] getDeployedServices();

   /**
    * Gets the details for all of the deployed processes
    */
   public AeProcessDeploymentDetail[] getDeployedProcesses();

   /**
    * Get the details for a single process identified by its QName.
    * @param aQName
    */
   public AeProcessDeploymentDetail getDeployedProcessDetail(QName aQName);

   /**
    * Gets a list of the unmatched inbound queued receives from the engine's
    * queue.
    */
   public AeQueuedReceiveDetail[] getUnmatchedQueuedReceives();

   /**
    * Gets a listing of the queued message receivers from the engine's queue.
    */
   public AeMessageReceiverListResult getMessageReceivers( AeMessageReceiverFilter aFilter);

   /**
    * Gets a listing of alarms matching the passed filter.
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter);

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
    * Returns a list of processes currently running on the BPEL engine. This
    * list may be optionally filtered by a process name.
    * @param aFilter the process filter to use when obtaining the result set.
    */
   public ProcessList getProcessList(ProcessFilterType aFilter) throws AeBusinessProcessException;

   /**
    * Returns the count of processes that match the given process filter.
    * 
    * @param aFilter
    * @throws AeBusinessProcessException
    */
   public int getProcessCount(ProcessFilterType aFilter) throws AeBusinessProcessException;

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

   /**
    * Returns the interface into catalog administration.
    */
   public IAeCatalogAdmin getCatalogAdmin();

   /**
    * Starts the engine.
    * @throws AeBusinessProcessException
 * @throws AeException 
    */
   public void start() throws AeException;

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
    * Deploys a BPR file to the engine.
    *
    * @param aBprFile The BPR to deploy.
    * @param aBprFilename The name of the BPR file (could be different than aBprFile if it is a temp file).
    * @param aLogger A logger to use.
    */
   public void deployNewBpr(File aBprFile, String aBprFilename, IAeDeploymentLogger aLogger) throws AeException;

   /**
    * Getter for the urn resolver.
    */
   public IAeURNResolver getURNAddressResolver();

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
   public List getParticipantForProcessId(long aParentProcessId) throws AeException;

}