// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AePersistentProcessManager.java,v 1.82.2.1 2008/04/21 16:12:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.io.Reader;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeMonitorListener;
import org.activebpel.rt.bpel.IAePlanManager;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor;
import org.activebpel.rt.bpel.impl.AeAbstractProcessManager;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.bpel.impl.storage.AeProcessImplStateAttributeCounts;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.AeProcessDeploymentFactory;
import org.activebpel.rt.bpel.server.deploy.AeProcessPersistenceType;
import org.activebpel.rt.bpel.server.engine.process.AeProcessStateReader;
import org.activebpel.rt.bpel.server.engine.process.AeProcessStateWriter;
import org.activebpel.rt.bpel.server.engine.process.AeProcessWrapper;
import org.activebpel.rt.bpel.server.engine.process.AeProcessWrapperMap;
import org.activebpel.rt.bpel.server.engine.process.IAeProcessStateReader;
import org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter;
import org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMap;
import org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMapCallback;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeInboundReceiveJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeDbUtils;
import org.activebpel.rt.bpel.server.logging.IAePersistentLogger;
import org.activebpel.rt.bpel.server.logging.IAeProcessLogEntry;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeMutex;
import org.activebpel.rt.xml.AeQName;
import org.activebpel.work.AeAbstractWork;

import commonj.timers.Timer;
import commonj.timers.TimerListener;
import commonj.work.Work;

/**
 * Implements process manager that persists processes to an instance of
 * <code>IAeStorage</code>. This implementation counts references
 * to process instances to decide when to persist a process to storage. This
 * means that every call to {@link #createBusinessProcess(IAeProcessPlan)} or
 * {@link #getProcess(long)} <strong>must</strong> must be balanced by a
 * corresponding call to {@link #releaseProcess(IAeBusinessProcess)}.
 */
public class AePersistentProcessManager extends AeAbstractProcessManager implements IAePersistentProcessManager, IAeProcessWrapperMapCallback, IAeRecoverableProcessManager
{
   public static final String CONFIG_PROCESS_COUNT = "ProcessCount"; //$NON-NLS-1$
   public static final String CONFIG_RELEASE_LAG   = "ReleaseLag"; //$NON-NLS-1$

   public static final int DEFAULT_PROCESS_COUNT   = 50;
   public static final int DEFAULT_RELEASE_LAG     = 10; // seconds
   public static final int DEADLOCK_TRY_COUNT      = 5;

   private static final long MILLIS_PER_SECOND     = 1000L;

   /** The maximum number of processes allowed in memory. */
   private int mMaxProcessCount;

   /** The number of processes required for recovery. */
   private int mRecoveryProcessCount;

   /**
    * The plan manager that resolves a process <code>QName</code> to a context
    * WSDL provider for the process.
    */
   private IAePlanManager mPlanManager;

   /** Maps process ids to process wrappers. */
   private IAeProcessWrapperMap mProcessWrapperMap;

   /** Stores process state. */
   private IAeProcessStateStorage mStorage;

   /** Process recovery object shared between {@link #prepareToStart} and {@link #start}. */
   private IAeProcessRecovery mProcessRecovery;

   /** # of calls to {@link #saveProcess(AeProcessWrapper)}. */
   private int mSaveProcessCount;

   /** # of calls to {@link #saveProcess(AeProcessWrapper)} averted. */
   private int mSaveAvertedCount;

   /**
    * The number of seconds to wait after a process goes quiescent before
    * releasing the process from memory.
    */
   private int mConfigReleaseLagSeconds;

   /**
    * Maps process ids to instances of {@link commonj.timers.Timer}. Based on a
    * {@link java.util.LinkedHashMap} to allow us to remove processes from the
    * map in LRU order.
    */
   private final Map<Long,Object> mProcessTimerMap = new LinkedHashMap<Long,Object>();

   /** Process state reader for this process manager. */
   private IAeProcessStateReader mProcessStateReader;

   /** Process state writer for this process manager. */
   private IAeProcessStateWriter mProcessStateWriter;

   /**
    * Synchronization object that enforces mutually exclusive execution of
    * recovery and the {@link #stop()} method.
    */ 
   private final Object mRecoveryAndStopMutex = new Object();

   /**
    * Creates a persistent process manager given the manager's engine
    * configuration map.
    *
    * @param aConfig The configuration map.
    */
   public AePersistentProcessManager(Map aConfig) throws Exception
   {
      super(aConfig);
      setProcessWrapperMap(new AeProcessWrapperMap(this));
      setProcessStateReader(new AeProcessStateReader(this));
      setProcessStateWriter(new AeProcessStateWriter(this));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeRecoverableProcessManager#acquireProcessMutex(long)
    */
   public void acquireProcessMutex(long aProcessId)
   {
      // Acquire the process mutex. This matches a call in
      // releaseProcessMutex() that releases the mutex.
      getProcessWrapperWithMutex(aProcessId);
   }

   /**
    * Cancels the release timer for the specified process.
    *
    * @param aProcessId
    */
   protected void cancelReleaseTimer(long aProcessId)
   {
      Timer timer;

      synchronized (getProcessTimerMap())
      {
         timer = (Timer) getProcessTimerMap().remove(aProcessId);
      }

      // If the process had a release timer running, then cancel the timer and
      // decrement the process reference count.
      if (timer != null)
      {
         try
         {
            timer.cancel();
         }
         catch (NullPointerException ignore)
         {
            // This should never happen, but we have observed the WebSphere
            // implementation throwing NullPointerException from within its
            // Timer implementation.
         }
         catch (Throwable t)
         {
            // This should also never happen, but if it does, don't let it keep
            // us from releasing the process reference. We'll report this,
            // because we'll want to know about new failure scenarios.
            AeException.logError(t);
         }

         AeProcessWrapper wrapper = getProcessWrapperWithMutex(aProcessId);

         try
         {
            // Decrement the process reference count. This matches a call in
            // scheduleReleaseTimer() that increments the count.
            wrapper.decrementCount();
         }
         finally
         {
            // If this is the last reference to the process, then
            // releaseProcessWrapperWithMutex() will call
            // reallyReleaseProcess().
            releaseProcessWrapperWithMutex(wrapper);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#create()
    */
   public void create() throws Exception
   {
      setStorage(AeEngineFactory.getStorageFactory().getProcessStateStorage());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#createBusinessProcess(org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public IAeBusinessProcess createBusinessProcess(IAeProcessPlan aProcessPlan) throws AeBusinessProcessException
   {
      QName processName = aProcessPlan.getProcessDef().getQName();

      // Acquire the process wrapper with mutex on temporary id, to ensure we have room for new process.
      AeProcessWrapper wrapper = getProcessWrapperWithMutex();

      // Generate new process instance in storage.
      IAeProcessDeployment deployment = AeProcessDeploymentFactory.getDeploymentForPlan(aProcessPlan);
      int planId = deployment.getPlanId();
      long processId;

      if (deployment.getPersistenceType() != AeProcessPersistenceType.NONE)
      {
         processId = getStorage().createProcess(planId, processName);
      }
      else
      {
         processId = getStorage().getNextProcessId();
      }

      // Create the process from the provided process plan.
      IAeBusinessProcess process = createProcess(processId, deployment);

      // Put the process into the process table.
      setProcessWrapperProcess(wrapper, process);

      // if debugging print out info about creation
      if (isDebug())
         debug(
            "Process {0,number,0}: {1}, persistenceType = {2}, transactionType = {3}", //$NON-NLS-1$
            new Object[] { new Long(processId),
                           processName.getLocalPart(),
                           deployment.getPersistenceType(),
                           deployment.getTransactionType() });

      return process;
   }

   /**
    * Creates process.
    *
    * @param aProcessId
    * @param aDeployment The deployment plan or <code>null</code>.
    */
   protected IAeBusinessProcess createProcess(long aProcessId, IAeProcessDeployment aDeployment) throws AeBusinessProcessException
   {
      IAeProcessDeployment deployment = aDeployment;

      // If caller did not provide a deployment plan, then retrieve it from
      // storage.
      if (deployment == null)
      {
         deployment = getDeploymentPlan(aProcessId);
      }

      IAeBusinessProcess process = AeDefToImplVisitor.createProcess(aProcessId, getEngine(), deployment);
      return process;
   }

   /**
    * Returns process recovery implementation.
    */
   protected IAeProcessRecovery createProcessRecovery()
   {
      return new AePersistentProcessRecovery(this);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#dumpLog(long)
    */
   public Reader dumpLog(long aProcessId) throws AeBusinessProcessException
   {
      return getStorage().dumpLog(aProcessId);
   }

   /**
    * Returns the number of seconds to wait after a process goes quiescent
    * before releasing the process from memory.
    */
   public int getConfigReleaseLagSeconds()
   {
      return mConfigReleaseLagSeconds;
   }

   /**
    * Returns the current process wrapper for the given process id or
    * <code>null</code> if there is no process wrapper for the given process id.
    * Does not increment the wrapper's reference count or acquire the process
    * mutex the way {@link #getProcessWrapperWithMutex(long)} does, so does not
    * require a matching call to {@link
    * #releaseProcessWrapperWithMutex(AeProcessWrapper)}. However, this means
    * that successive calls to {@link #getCurrentProcessWrapper(long)} with the
    * same process id may return different process wrappers.
    *
    * @param aProcessId
    */
   protected AeProcessWrapper getCurrentProcessWrapper(long aProcessId)
   {
      return getProcessWrapperMap().getCurrentWrapper(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getDeadlockTryCount()
    */
   public int getDeadlockTryCount()
   {
      return DEADLOCK_TRY_COUNT;
   }

   /**
    * Returns the deployment plan for the specified process.
    *
    * @param aProcessId
    */
   private IAeProcessDeployment getDeploymentPlan(long aProcessId) throws AeBusinessProcessException
   {
      IAeDeploymentProvider provider = (IAeDeploymentProvider) getPlanManager();
      QName processName = getStorage().getProcessName(aProcessId);

      IAeProcessDeployment deployment = provider.findDeploymentPlan(aProcessId, processName);
      if (deployment == null)
      {
         throw new AeBusinessProcessException(AeMessages.format("AePersistentProcessManager.ERROR_10", aProcessId)); //$NON-NLS-1$
      }

      return deployment;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getEffectiveProcessLimit()
    */
   public int getEffectiveProcessLimit()
   {
      return Math.max(getMaxProcessCount(), getRecoveryProcessCount());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getJournalEntry(long)
    */
   public IAeJournalEntry getJournalEntry(long aJournalId) throws AeBusinessProcessException
   {
      return getStorage().getJournalEntry(aJournalId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getMaxProcessCount()
    */
   public int getMaxProcessCount()
   {
      return mMaxProcessCount;
   }

   /**
    * Returns the plan manager that resolves a process <code>QName</code> to a
    * context WSDL provider for the process.
    */
   protected IAePlanManager getPlanManager()
   {
      return mPlanManager;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcess(long)
    */
   public IAeBusinessProcess getProcess(long aProcessId) throws AeBusinessProcessException
   {
      return getProcess(aProcessId, true);
   }
   
   /**
    * Returns the business process with the specified process id, locking the
    * process into memory. <em>Each call to {@link #getProcess(long, boolean)}
    * must be followed eventually by a matching call to
    * {@link #releaseProcess(IAeBusinessProcess)}</em>.
    *
    * @param aProcessId
    * @param aForUpdate <code>true</code> if the process should be persisted
    *           after it is released from memory.
    */
   protected IAeBusinessProcess getProcess(long aProcessId, boolean aForUpdate) throws AeBusinessProcessException
   {
      long start = System.currentTimeMillis();
      AeProcessWrapper wrapper = getProcessWrapperWithMutex(aProcessId);

      try
      {
         // Cancel the release timer that might be running.
         cancelReleaseTimer(aProcessId);

         IAeBusinessProcess process = wrapper.getProcess();
         if (process == null)
         {
            // Notify listeners that process was loaded from database
            getEngine().fireMonitorEvent(IAeMonitorListener.MONITOR_PROCESS_LOADED, IAeMonitorListener.EVENT_DATA_PROCESS_LOAD_FROM_DB);

            // Process was discarded. Restore from storage.
            process = restoreProcess(aProcessId);
            wrapper.setProcess(process);

            wrapper.setModified(aForUpdate);
         }
         else
         {
            // Notify listeners that process was loaded from cache
            getEngine().fireMonitorEvent(IAeMonitorListener.MONITOR_PROCESS_LOADED, IAeMonitorListener.EVENT_DATA_PROCESS_LOAD_FROM_CACHE);
            if (aForUpdate)
               wrapper.setModified(true);
         }

         if (process != null)
         {
            // Acquire the process mutex. This matches a call in
            // releaseProcess() that releases the mutex.
            getProcessWrapperWithMutex(aProcessId);
         }

         return process;
      }
      finally
      {
         releaseProcessWrapperWithMutex(wrapper);
         
         // Send monitor event indicating time to acquire process
         long loadTime = System.currentTimeMillis() - start;
         getEngine().fireMonitorEvent(IAeMonitorListener.MONITOR_PROCESS_LOAD_TIME, loadTime);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessInstanceDetails(long)
    */
   public AeProcessInstanceDetail getProcessInstanceDetails(long aProcessId)
   {
      // In order to return the correct instant detail snaphot (ie. avoid discrepancies due to
      // latency when getting the data from the storage), first check to see if the process is
      // in memory. If so, get the details from in memory process. Otherwise, return the process
      // instance details directly from the storage layer.

      // get the wrapper to determine if the process is in memory.
      AeProcessWrapper wrapper = getCurrentProcessWrapper(aProcessId);

      AeProcessInstanceDetail result;

      // Careful here. Don't compare wrapper.getProcess() to null and then try
      // to use wrapper.getProcess() inside the body of the if, because the
      // value of wrapper.getProcess() may change from one call to the next. In
      // other words, call wrapper.getProcess() once and hold onto the return
      // value.
      IAeBusinessProcess process = (wrapper != null) ? wrapper.getProcess() : null;
      if (process != null)
      {
         result = getProcessInstanceDetailsFromMemory(process);
      }
      else
      {
         result = getProcessInstanceDetailsFromStorage(aProcessId);
      }

      return result;
   }

   /**
    * Returns the process instance details using the process instance from the memory cache.
    * @param aProcess
    * @return process instance details if available, or null otherwise.
    */
   protected AeProcessInstanceDetail getProcessInstanceDetailsFromMemory(IAeBusinessProcess aProcess)
   {
      AeProcessInstanceDetail detail = null;
      try
      {
         detail = new AeProcessInstanceDetail();
         detail.setName(new AeQName(aProcess.getName()));
         detail.setProcessId(aProcess.getProcessId());
         detail.setState(aProcess.getProcessState());
         detail.setStateReason(aProcess.getProcessStateReason());
         detail.setStarted(aProcess.getStartDate());
         detail.setEnded(aProcess.getEndDate());
      }
      catch(Exception e)
      {
         AeException.logError(e, AeMessages.format("AePersistentProcessManager.ERROR_6", aProcess.getProcessId())); //$NON-NLS-1$
      }
      return detail;
   }

   /**
    * Returns the process instance details from the persistant layer.
    * @param aProcessId
    * @return process instance details if available, or null otherwise.
    */
   protected AeProcessInstanceDetail getProcessInstanceDetailsFromStorage(long aProcessId)
   {
       try
       {
          return getStorage().getProcessInstanceDetail(aProcessId);
       }
       catch (AeStorageException e)
       {
          AeException.logError(e, AeMessages.format("AePersistentProcessManager.ERROR_6", aProcessId)); //$NON-NLS-1$
       }
       return null;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getProcessLog(long)
    */
   public String getProcessLog(long aProcessId) throws AeBusinessProcessException
   {
      return getStorage().getLog(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessNoUpdate(long)
    */
   public IAeBusinessProcess getProcessNoUpdate(long aProcessId) throws AeBusinessProcessException
   {
      return getProcess(aProcessId, false);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessQName(long)
    */
   public QName getProcessQName(long aProcessId)
   {
      // this method is here primarily for the console ui
      // so that the location path can be displayed for
      // the message receiver queue listing
      // the issue is that the queue objects only contain
      // the process id and the location id
      // so we have to get the qname for the process (via
      // its process id) but we don't want to lock the process
      // this method provides direct db access with no
      // locking penalties
      return getProcessInstanceDetails(aProcessId).getName().toQName();
   }

   /**
    * Returns process recovery object shared between
    * <code>prepareToStart()</code> and <code>start()</code>.
    */
   protected IAeProcessRecovery getProcessRecovery()
   {
      if (mProcessRecovery == null)
      {
         mProcessRecovery = createProcessRecovery();
      }

      return mProcessRecovery;
   }

   /**
    * Returns process state reader.
    */
   public IAeProcessStateReader getProcessStateReader()
   {
      return mProcessStateReader;
   }

   /**
    * Returns process state writer.
    */
   protected IAeProcessStateWriter getProcessStateWriter()
   {
      return mProcessStateWriter;
   }

   /**
    * Returns map from process ids to process release timers.
    */
   protected Map<Long,Object> getProcessTimerMap()
   {
      return mProcessTimerMap;
   }

   /**
    * Returns process wrapper map.
    */
   protected IAeProcessWrapperMap getProcessWrapperMap()
   {
      return mProcessWrapperMap;
   }

   /**
    * Returns process wrapper for the a temporary process id to ensure that
    * we have room for this process in memory. The real id is set afterwards.
    * <em>Each call to {@link #getProcessWrapperWithMutex()} must be followed
    * eventually by a matching call to
    * {@link #releaseProcessWrapperWithMutex(AeProcessWrapper)}</em>
    *
    */
   protected AeProcessWrapper getProcessWrapperWithMutex()
   {
      AeProcessWrapper wrapper = getProcessWrapperMap().getWrapper();
      wrapper.acquireMutex();
      return wrapper;
   }

   /**
    * Returns process wrapper for the specified process id and acquires the
    * process mutex. Adds a process wrapper for the process if one does not yet
    * exist. <em>Each call to {@link #getProcessWrapperWithMutex(long)} must be
    * followed eventually by a matching call to {@link
    * #releaseProcessWrapperWithMutex(AeProcessWrapper)}</em>.
    *
    * @param aProcessId
    */
   protected AeProcessWrapper getProcessWrapperWithMutex(long aProcessId)
   {
      AeProcessWrapper wrapper = getProcessWrapperMap().getWrapper(aProcessId);
      wrapper.acquireMutex();
      return wrapper;
   }

   /**
    * Sets the process associated with the process wrapper through the map so the id an be updated
    * @param aWrapper
    * @param aProcess
    */
   protected void setProcessWrapperProcess(AeProcessWrapper aWrapper, IAeBusinessProcess aProcess)
   {
      getProcessWrapperMap().setProcessWrapperProcess(aWrapper, aProcess);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcesses(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public AeProcessListResult getProcesses(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      int tryCount = getDeadlockTryCount();
      AeStorageException firstException = null;

      for (int tries = 0; true; )
      {
         try
         {
            // Successful retrieval of the process list breaks the loop.
            return getStorage().getProcessList(aFilter);
         }
         catch (AeStorageException e)
         {
            // Retry if this is a SQL exception and we haven't exhausted the
            // try count.  
            // TODO (EPW) We need to have a method isRetryableException on the provider factory...
            if ((e.getCause() instanceof SQLException) && (++tries < tryCount))
            {
               if (firstException == null)
               {
                  firstException = e;
               }

               // backoff wait time
               // skew the max wait time by 5 so our range will be 0-128ms up to 0-2048ms
               AeDbUtils.backOffWait(tries + 6, -1, "AePersistentProcessManager.ERROR_GetProcessesRetry"); //$NON-NLS-1$
            }
            // Otherwise, we're done.
            else
            {
               if (firstException != null)
               {
                  AeException.logError(firstException.getCause(), AeMessages.getString("AePersistentProcessManager.ERROR_GetProcessesFirstException")); //$NON-NLS-1$
               }

               throw new AeBusinessProcessException(AeMessages.getString("AePersistentProcessManager.ERROR_7"), e); //$NON-NLS-1$
            }
         }
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessCount(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int getProcessCount(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      try
      {
         return getStorage().getProcessCount(aFilter);
      }
      catch (AeStorageException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AePersistentProcessManager.ERROR_7"), e); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessIds(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public long[] getProcessIds(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      try
      {
         return getStorage().getProcessIds(aFilter);
      }
      catch (AeStorageException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AePersistentProcessManager.ERROR_7"), e); //$NON-NLS-1$
      }
   }   
   
   /**
    * Returns the number of processes required for recovery.
    */
   protected int getRecoveryProcessCount()
   {
      return mRecoveryProcessCount;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeRecoverableProcessManager#getRecoveryAndStopMutex()
    */
   public Object getRecoveryAndStopMutex()
   {
      return mRecoveryAndStopMutex;
   }

   /**
    * Called by {@link #releaseProcess(IAeBusinessProcess)}; returns the number
    * of milliseconds to wait after the process goes quiescent before releasing
    * the process from memory. This implementation returns either
    * <code>0</code> (to release the process immediately) or the value from the
    * engine configuration; subclasses should override this method to tailor
    * the behavior.
    */
   protected long getReleaseLagMillis(AeProcessWrapper aWrapper)
   {
      return (aWrapper.isQuickRelease() || !aWrapper.isModified()) ? 0 : (getConfigReleaseLagSeconds() * MILLIS_PER_SECOND);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getStorage()
    */
   public IAeProcessStateStorage getStorage()
   {
      return mStorage;
   }

   /**
    * @param aStorage The storage to set.
    */
   protected void setStorage(IAeProcessStateStorage aStorage)
   {
      mStorage = aStorage;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#isContainerManaged(long)
    */
   public boolean isContainerManaged(long aProcessId)
   {
      AeProcessWrapper wrapper = getCurrentProcessWrapper(aProcessId);
      return (wrapper != null) && wrapper.isContainerManaged();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#isLoaded(long)
    */
   public boolean isLoaded(long aProcessId)
   {
      AeProcessWrapper wrapper = getCurrentProcessWrapper(aProcessId);
      return (wrapper != null) && (wrapper.getProcess() != null);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#isPersistent(long)
    */
   public boolean isPersistent(long aProcessId)
   {
      AeProcessWrapper wrapper = getCurrentProcessWrapper(aProcessId);

      // The default value is persistent, so if the process is not in memory,
      // then return true. If the process is in memory, then return the value
      // from the wrapper.
      return (wrapper == null) || wrapper.isPersistent();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalEntryDone(long, long)
    */
   public void journalEntryDone(long aProcessId, long aJournalId)
   {
      AeProcessWrapper wrapper = getProcessWrapperWithMutex(aProcessId);

      try
      {
         wrapper.getCompletedJournalIds().add(aJournalId);
      }
      finally
      {
         releaseProcessWrapperWithMutex(wrapper);
      }
   }

   public void journalEntriesDone(long aProcessId, Set<Long> aJournalIds)
   {
      AeProcessWrapper wrapper = getProcessWrapperWithMutex(aProcessId);

      try
      {
         wrapper.getCompletedJournalIds().addAll(aJournalIds);
      }
      finally
      {
         releaseProcessWrapperWithMutex(wrapper);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokeData(long, int, long, org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public long journalInvokeData(long aProcessId, int aLocationId, long aTransmissionId, IAeMessageData aMessageData, Map aProcessProperties) throws AeBusinessProcessException
   {
      return getProcessStateWriter().journalInvokeData(aProcessId, aLocationId, aTransmissionId, aMessageData, aProcessProperties);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokeFault(long, int, long, org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public long journalInvokeFault(long aProcessId, int aLocationId, long aTransmissionId, IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException
   {
      return getProcessStateWriter().journalInvokeFault(aProcessId, aLocationId, aTransmissionId, aFault, aProcessProperties);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInboundReceive(long, int, org.activebpel.rt.bpel.impl.queue.AeInboundReceive)
    */
   public long journalInboundReceive(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive) throws AeBusinessProcessException
   {
      return getProcessStateWriter().journalInboundReceive(aProcessId, aLocationId, aInboundReceive);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalSentReply(long, org.activebpel.rt.bpel.impl.queue.AeReply, java.util.Map)
    */
   public void journalSentReply(long aProcessId, AeReply aSentReply, Map aProcessProperties) throws AeBusinessProcessException
   {
      long journalId = getProcessStateWriter().journalSentReply(aProcessId, aSentReply, aProcessProperties);

      // We can mark the item done as soon as we write it, because replies are
      // only sent from running processes.
      journalEntryDone(aProcessId, journalId);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokeTransmitted(long, int, long)
    */
   public void journalInvokeTransmitted(long aProcessId, int aLocationId,long aTransmissionId) throws AeBusinessProcessException
   {
      long journalId = getProcessStateWriter().journalInvokeTransmitted(aProcessId, aLocationId, aTransmissionId);
      journalEntryDone(aProcessId, journalId);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCompensateSubprocess(long, java.lang.String)
    */
   public long journalCompensateSubprocess(long aProcessId, String aCoordinationId) //throws AeBusinessProcessException
   {
      return getProcessStateWriter().journalCompensateSubprocess(aProcessId, aCoordinationId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokePending(long, int)
    */
   public long journalInvokePending(long aProcessId, int aLocationId) throws AeBusinessProcessException
   {
      return getProcessStateWriter().journalInvokePending(aProcessId, aLocationId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeRecoverableProcessManager#journalEngineFailure(long, int)
    */
   public void journalEngineFailure(long aProcessId, int aDeadEngineId) throws AeBusinessProcessException
   {
      long journalId = getProcessStateWriter().journalEngineFailure(aProcessId, aDeadEngineId);
      journalEntryDone(aProcessId, journalId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#transmissionIdDone(long, long)
    */
   public void transmissionIdDone(long aProcessId, long aTransmissionId)
   {
      AeProcessWrapper wrapper = getProcessWrapperWithMutex(aProcessId);
      try
      {
         wrapper.getCompletedTransmissionIds().add(aTransmissionId);
      }
      finally
      {
         releaseProcessWrapperWithMutex(wrapper);
      }
   }

   /**
    * Overrides method to cancel a process release timer in order to make room
    * for a new process wrapper in the process wrapper map.
    *
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMapCallback#notifyProcessWrapperMapFull()
    */
   public void notifyProcessWrapperMapFull()
   {
      long processId = -1;

      synchronized (getProcessTimerMap())
      {
         // The process timers map is based on a LinkedHashMap, which provides
         // insertion-order iterators. Use an iterator to find the oldest timer
         // in the map.
         Iterator i = getProcessTimerMap().keySet().iterator();
         if (i.hasNext())
         {
            processId = ((Number) i.next()).longValue();
         }
      }

      if (processId >= 0)
      {
         debug("Process {0,number,0}: *** cancelling release timer ***", processId); //$NON-NLS-1$

         // Call cancelReleaseTimer() outside of the synchronized block above
         // to reduce the potential for deadlocks.
         cancelReleaseTimer(processId);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#prepareToStart()
    */
   public void prepareToStart() throws Exception
   {
      super.prepareToStart();

      getProcessRecovery().prepareToRecover();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#processEnded(long)
    */
   public void processEnded(long aProcessId)
   {
      AeProcessWrapper wrapper = getCurrentProcessWrapper(aProcessId);
      if (wrapper != null)
      {
         // Mark the process for quick release by releaseProcess().
         wrapper.setQuickRelease();
      }
   }

   /**
    * Handles the case when we are about to drop our last reference to the specified process.
    *
    * @param aWrapper
    */
   protected void reallyReleaseProcess(AeProcessWrapper aWrapper)
   {
      if (aWrapper.isPersistent())
      {
         try
         {
            // Soon we will have no more references to the process, so this is
            // a good time to save it.
            saveProcess(aWrapper);
         }
         catch (Throwable e)
         {
            AeException.logError(e, AeMessages.format("AePersistentProcessManager.ERROR_8", aWrapper.getProcessId())); //$NON-NLS-1$
         }
      }
      else
      {
         IAeProcessLogger logger = AeEngineFactory.getLogger();

         if (logger instanceof IAePersistentLogger)
         {
            IAeProcessLogEntry entry = ((IAePersistentLogger) logger).getLogEntry(aWrapper.getProcessId());
            if (entry != null)
               entry.clearFromLog();
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#releaseProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void releaseProcess(IAeBusinessProcess aProcess)
   {
      if (aProcess == null)
      {
         return;
      }

      long processId = aProcess.getProcessId();
      AeProcessWrapper wrapper = getProcessWrapperWithMutex(processId);

      try
      {
         releaseProcessWrapperWithMutex(wrapper);

         // If this is the last reference to the process, then schedule a
         // release timer.
         if (wrapper.getCount() == 1)
         {
            // Note that wrapper.getCount() returns an inflated count for
            // non-persistent processes while they are running, so we get here
            // only for persistent processes or for non-persistent processes
            // that have ended.
            long lag = getReleaseLagMillis(wrapper);
            if (lag > 0)
            {
               scheduleReleaseTimer(wrapper, lag);
            }
         }
      }
      finally
      {
         // Release the process mutex. This matches a call in
         // createBusinessProcess() or getProcess() that acquired the mutex.
         //
         // If this is the last reference to the process and we didn't schedule
         // a release timer, then releaseProcessWrapperWithMutex() will call
         // reallyReleaseProcess().
         releaseProcessWrapperWithMutex(wrapper);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeRecoverableProcessManager#releaseProcessMutex(long)
    */
   public void releaseProcessMutex(long aProcessId)
   {
      AeProcessWrapper wrapper = getProcessWrapperWithMutex(aProcessId);

      try
      {
         // Release the process mutex. This matches a call in
         // acquireProcessMutex() that acquired the mutex.
         releaseProcessWrapperWithMutex(wrapper);
      }
      finally
      {
         releaseProcessWrapperWithMutex(wrapper);
      }
   }

   /**
    * Releases a process wrapper allocated by {@link
    * #getProcessWrapperWithMutex(long)} and then calls {@link
    * AeProcessWrapper#releaseMutex()}. If this is the last reference to the
    * process, calls {@link #reallyReleaseProcess(AeProcessWrapper)} before
    * releasing the mutex to make sure the process is persisted.
    *
    * @param aWrapper
    */
   protected void releaseProcessWrapperWithMutex(AeProcessWrapper aWrapper)
   {
      try
      {
         if ((aWrapper.getCount() == 1) && (aWrapper.getProcess() != null))
         {
            try
            {
               reallyReleaseProcess(aWrapper);
            }
            finally
            {
               // The final release of a quick-release process means that the
               // process is leaving memory for the first time since the process
               // completed.
               if (aWrapper.isQuickRelease())
               {
                  fireProcessPurged(aWrapper.getProcessId());
               }
            }
         }
      }
      finally
      {
         try
         {
            // The order here is important. Release the wrapper before
            // releasing the mutex. If we were to release the mutex first, then
            // another thread blocked in getProcessWrapperWithMutex() could
            // conceivably make the following call to releaseWrapper() before
            // this thread does, so that neither thread would see a return
            // value of 1 from the call to getCount() above.
            getProcessWrapperMap().releaseWrapper(aWrapper);
         }
         finally
         {
            aWrapper.releaseMutex();
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#removeProcess(long)
    */
   public void removeProcess(long aProcessId) throws AeBusinessProcessException
   {
      try
      {
         getStorage().removeProcess(aProcessId);
      }
      catch (AeStorageException e)
      {
         throw new AeBusinessProcessException(AeMessages.format("AePersistentProcessManager.ERROR_1", aProcessId), e); //$NON-NLS-1$
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#removeProcesses(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int removeProcesses(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      try
      {
         switch (aFilter.getProcessState())
         {
            case AeProcessFilter.STATE_COMPLETED:
            case AeProcessFilter.STATE_COMPLETED_OR_FAULTED:
            case AeProcessFilter.STATE_FAULTED:
               break;
            default:
               // Only allow deleting of completed or faulted processes.
               aFilter.setProcessState(AeProcessFilter.STATE_COMPLETED_OR_FAULTED);
               break;
         }

         return getStorage().removeProcesses(aFilter);
      }
      catch (AeStorageException e)
      {
         String message = e.getLocalizedMessage();
         throw new AeBusinessProcessException((message != null) ? message : AeMessages.getString("AePersistentProcessManager.ERROR_11"), e); //$NON-NLS-1$
      }
   }

   /**
    * Restores process from process definition and stored process state.
    *
    * @param aProcessId
    */
   protected IAeBusinessProcess restoreProcess(long aProcessId) throws AeBusinessProcessException
   {
      long millis = System.currentTimeMillis();

      // Recreate the process implementation object.
      IAeBusinessProcess process = createProcess(aProcessId, null);

      // Read the process implementation state from storage.
      getProcessStateReader().readProcess(process);

      debug(
         "Process {0,number,0}: restored ({1,number,0} millis)", //$NON-NLS-1$
         new Object[] { new Long(aProcessId),
                        new Long(System.currentTimeMillis() - millis) });

      return process;
   }

   /**
    * Saves the process's state and variables.
    *
    * @param aWrapper
    */
   protected void saveProcess(AeProcessWrapper aWrapper) throws AeBusinessProcessException
   {
      long millis = System.currentTimeMillis();

      // Get ids of journal entries that are done.
      Set<Long> completedJournalIds = aWrapper.getCompletedJournalIds();
      // List of transmission ids that needs to be deleted.
      Set<Long> completedTransmissionIds = aWrapper.getCompletedTransmissionIds();
      // Journal id to set aside for restart.
      long journalIdForRestart = aWrapper.getJournalIdForRestart();

      // If the process has been marked as modified or there is at least one
      // completed journal entry or transmission id or there is a restart
      // journal id, then save the process.
      if (aWrapper.isModified() || !completedJournalIds.isEmpty() || !completedTransmissionIds.isEmpty() || (journalIdForRestart != 0))
      {
         // Save the process and delete the completed journal entries.
         int n = getProcessStateWriter().writeProcess(aWrapper);
   
         // Clear journal entries in wrapper.
         completedJournalIds.clear();
         // Clear completed transmission id collection
         completedTransmissionIds.clear();
         // Clear restart journal id in wrapper.
         aWrapper.setJournalIdForRestart(0);
   
         ++mSaveProcessCount;
   
         if (isDebug())
         {
            int total = mSaveProcessCount + mSaveAvertedCount;
            int percent = (int) (100.0 * mSaveAvertedCount / ((double) total) + 0.5);
   
            debug(
               "Process {0,number,0}: saved ({1,number,0} millis){2,choice,0#|1# with 1 invoke pending|1< with {2,number,0} invokes pending} (averted {3}%)", //$NON-NLS-1$
               new Object[] { new Long(aWrapper.getProcessId()),
                              new Long(System.currentTimeMillis() - millis),
                              new Integer(n),
                              new Integer(percent) });
         }
      }
   }

   /**
    * Schedules a timer to release the specified process in the future.
    *
    * @param aWrapper
    * @param aDelayMillis
    */
   protected void scheduleReleaseTimer(AeProcessWrapper aWrapper, long aDelayMillis)
   {
      ++mSaveAvertedCount;

      // Increment the process reference count to keep the wrapper in the
      // wrapper map. This matches a call in cancelReleaseTimer() that
      // decrements the count.
      aWrapper.incrementCount();

      // Schedule a timer to call cancelReleaseTimer().
      long processId = aWrapper.getProcessId();
      TimerListener listener = new AeProcessReleaseTimerListener(processId);

      synchronized (getProcessTimerMap())
      {
         Timer timer = AeEngineFactory.getTimerManager().schedule(listener, aDelayMillis);
         getProcessTimerMap().put(processId, timer);
      }
   }

   /**
    * Sets configuration.
    */
   protected void setConfig(Map aConfig)
   {
      super.setConfig(aConfig);

      setMaxProcessCount(getConfigInt(CONFIG_PROCESS_COUNT, DEFAULT_PROCESS_COUNT));

      // TODO (MF) should add the pm as a listener on the config to update the lag if it changes
      setConfigReleaseLagSeconds(getConfigInt(CONFIG_RELEASE_LAG, DEFAULT_RELEASE_LAG));

      // Set the AeMutex debug flag.
      AeMutex.setDebug(isDebug());
   }

   /**
    * Sets the number of seconds to wait after a process goes quiescent before
    * releasing the process from memory.
    */
   public void setConfigReleaseLagSeconds(int aConfigReleaseLagSeconds)
   {
      if (aConfigReleaseLagSeconds >= 0)
      {
         mConfigReleaseLagSeconds = aConfigReleaseLagSeconds;
      }
      else
      {
         aConfigReleaseLagSeconds = DEFAULT_RELEASE_LAG;
      }
   }

   /**
    * Sets the maximum number of processes allowed in memory.
    *
    * @param aMaxProcessCount
    */
   protected void setMaxProcessCount(int aMaxProcessCount)
   {
      if (aMaxProcessCount > 0)
      {
         mMaxProcessCount = aMaxProcessCount;
      }
      else
      {
         // Specifying 0 or less means no limit.
         mMaxProcessCount = Integer.MAX_VALUE;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#setPlanManager(org.activebpel.rt.bpel.IAePlanManager)
    */
   public void setPlanManager(IAePlanManager aPlanManager)
   {
      if (aPlanManager instanceof IAeDeploymentProvider)
         mPlanManager = aPlanManager;
      else
         throw new IllegalArgumentException(AeMessages.getString("AePersistentProcessManager.ERROR_37")); //$NON-NLS-1$
   }

   /**
    * Sets process state reader.
    */
   public void setProcessStateReader(IAeProcessStateReader aProcessStateReader)
   {
      mProcessStateReader = aProcessStateReader;
   }

   /**
    * Sets process state writer.
    */
   public void setProcessStateWriter(IAeProcessStateWriter aProcessStateWriter)
   {
      mProcessStateWriter = aProcessStateWriter;
   }

   /**
    * Sets process wrapper map.
    */
   protected void setProcessWrapperMap(IAeProcessWrapperMap aProcessWrapperMap)
   {
      mProcessWrapperMap = aProcessWrapperMap;
   }

   /**
    * Sets the number of processes required for recovery.
    *
    * @see org.activebpel.rt.bpel.server.engine.IAeRecoverableProcessManager#setRecoveryProcessCount(int)
    */
   public void setRecoveryProcessCount(int aRecoveryProcessCount)
   {
      int oldLimit = getEffectiveProcessLimit();

      mRecoveryProcessCount = aRecoveryProcessCount;

      int newLimit = getEffectiveProcessLimit();

      if (newLimit > oldLimit)
      {
         debug("Increasing effective process limit to {0,number,0} for recovery", newLimit); //$NON-NLS-1$
      }
      else if (newLimit < oldLimit)
      {
         debug("Restoring effective process limit to {0,number,0}", newLimit); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#start()
    */
   public void start()
   {
      try
      {
         // Execute the recovery object that we prepared in prepareToStart().
         getProcessRecovery().recover();
      }
      finally
      {
         mProcessRecovery = null;
      }

      debug("Starting with maxProcessCount = {0,number,0}", getMaxProcessCount()); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#stop()
    */
   public void stop()
   {
      // To fix defect 1527, "Getting an IllegalArgumentException when
      // recovery is attempting to be done but the server is in the middle of
      // shutting down," we need to keep the engine from stopping the timer
      // manager while in the middle of recovery. AeBpelEngine stops the timer
      // manager after stopping all the managers, so by synchronizing on
      // AePersistentProcessManager#getRecoveryStopMutex() here we prevent
      // this method from returning while recovery is running.
      synchronized (getRecoveryAndStopMutex())
      {
         // Wait until processes are quiescent.
         waitUntilQuiescent();

         // Print attribute value counts (when enabled in AeProcessImplStateAttributeCounts).
         AeProcessImplStateAttributeCounts.getCounts().printCounts();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getNextJournalId()
    */
   public long getNextJournalId() throws AeBusinessProcessException
   {
      return getProcessStateWriter().getNextJournalId();
   }

   /**
    * Waits until processes are quiescent.
    */
   public void waitUntilQuiescent()
   {
      getProcessWrapperMap().waitUntilEmpty();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalEntryForRestart(long, long)
    */
   public void journalEntryForRestart(long aProcessId, long aJournalId)
   {
      AeProcessWrapper wrapper = getProcessWrapperWithMutex(aProcessId);

      try
      {
         wrapper.setJournalIdForRestart(aJournalId);
      }
      finally
      {
         releaseProcessWrapperWithMutex(wrapper);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#recreateBusinessProcess(long, org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public IAeBusinessProcess recreateBusinessProcess(long aProcessId, IAeProcessPlan aProcessPlan) throws AeBusinessProcessException
   {
      AeProcessWrapper wrapper = getProcessWrapperWithMutex(aProcessId);

      try
      {
         // Cancel the release timer that might be running.
         cancelReleaseTimer(aProcessId);

         // Get process deployment.
         IAeProcessDeployment deployment = AeProcessDeploymentFactory.getDeploymentForPlan(aProcessPlan);

         // Recreate the process.
         IAeBusinessProcess process = createProcess(aProcessId, deployment);

         // Save the process in the wrapper.
         wrapper.setProcess(process);

         // Acquire the process mutex again. This matches a call in
         // releaseProcess() that releases the mutex.
         getProcessWrapperWithMutex(aProcessId);

         debug("Process {0,number,0}: recreated", aProcessId); //$NON-NLS-1$

         return process;
      }
      finally
      {
         releaseProcessWrapperWithMutex(wrapper);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getRestartProcessJournalEntry(long)
    */
   public AeInboundReceiveJournalEntry getRestartProcessJournalEntry(long aProcessId) throws AeBusinessProcessException
   {
      AeProcessWrapper wrapper = getProcessWrapperWithMutex(aProcessId);

      try
      {
         long restartProcessJournalId = wrapper.getJournalIdForRestart();
         AeInboundReceiveJournalEntry entry;

         // If the restart process journal id is still specified in the process
         // wrapper, then the journal entry has not yet been set aside in
         // storage, so fetch it by journal id.
         if (restartProcessJournalId != IAeProcessManager.NULL_JOURNAL_ID)
         {
            entry = (AeInboundReceiveJournalEntry) getStorage().getJournalEntry(restartProcessJournalId);
         }
         // Otherwise, the restart process journal entry has been set aside, so
         // fetch it by type.
         else
         {
            entry = getStorage().getRestartProcessJournalEntry(aProcessId);
         }

         return entry;
      }
      finally
      {
         releaseProcessWrapperWithMutex(wrapper);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#journalAllEntriesDone(long)
    */
   public void journalAllEntriesDone(long aProcessId) throws AeBusinessProcessException
   {
      Map<Long,Integer> map = getStorage().getJournalEntriesLocationIdsMap(aProcessId);
      Set<Long> journalIds = new HashSet(map.keySet());
      journalEntriesDone(aProcessId, journalIds);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCoordinationQueueMessageReceived(long, org.activebpel.rt.bpel.coord.IAeProtocolMessage)
    */
   public long journalCoordinationQueueMessageReceived(long aProcessId, IAeProtocolMessage aMessage)
   {
      return getProcessStateWriter().journalCoordinationQueueMessage(aProcessId, aMessage);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCancelProcess(long)
    */
   public long journalCancelProcess(long aProcessId)
   {
      return getProcessStateWriter().journalCancelProcess(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCancelSubprocessCompensation(long)
    */
   public long journalCancelSubprocessCompensation(long aProcessId)
   {
      return getProcessStateWriter().journalCancelSubprocessCompensation(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalReleaseCompensationResources(long)
    */
   public long journalReleaseCompensationResources(long aProcessId)
   {
      return getProcessStateWriter().journalReleaseCompensationResources(aProcessId);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalNotifyCoordinatorsParticipantClosed(long)
    */
   public long journalNotifyCoordinatorsParticipantClosed(long aProcessId)
   {
      return getProcessStateWriter().journalNotifyCoordinatorsParticipantClosed(aProcessId);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCompensateCallback(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault)
    */
   public long journalCompensateCallback(long aProcessId, String aLocationPath,
         String aCoordinationId, IAeFault aFault)
   {
      return getProcessStateWriter().journalCompensateCallback(aProcessId, aLocationPath, aCoordinationId, aFault);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCoordinatedActivityCompleted(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault)
    */
   public long journalCoordinatedActivityCompleted(long aProcessId,
         String aLocationPath, String aCoordinationId, IAeFault aFault)
   { 
      return getProcessStateWriter().journalCoordinatedActivityCompleted(aProcessId, aLocationPath, aCoordinationId, aFault);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalDeregisterCoordination(long, java.lang.String, java.lang.String)
    */
   public long journalDeregisterCoordination(long aProcessId, String aLocationPath, String aCoordinationId)
   {
      return getProcessStateWriter().journalDeregisterCoordination(aProcessId, aLocationPath, aCoordinationId);
   }

   /**
    * Implements timer listener to release a process from memory.
    */
   protected class AeProcessReleaseTimerListener implements TimerListener
   {
      private final long mProcessId;

      /**
       * Constructs timer listener to release the specified process from memory.
       */
      public AeProcessReleaseTimerListener(long aProcessId)
      {
         mProcessId = aProcessId;
      }

      /**
       * Returns the process id.
       */
      protected long getProcessId()
      {
         return mProcessId;
      }

      /**
       * @see commonj.timers.TimerListener#timerExpired(commonj.timers.Timer)
       */
      public void timerExpired(Timer aTimer)
      {
         --mSaveAvertedCount;

         debug("Process {0,number,0}: *** release timer expired ***", mProcessId); //$NON-NLS-1$

         // Schedule work to release the process.
         // TODO (KR) Use a shared work manager thread.
         Work work = new AeProcessReleaseWork(getProcessId());

         try
         {
            AeEngineFactory.getWorkManager().schedule(work);
         }
         catch (Exception e)
         {
            AeException.logError(e, AeMessages.format("AePersistentProcessManager.ERROR_50", getProcessId())); //$NON-NLS-1$

            // Do the work on this thread.
            work.run();
         }
      }
   }

   /**
    * Implements work to release a process from memory.
    */
   protected class AeProcessReleaseWork extends AeAbstractWork
   {
      /** The process id. */
      private final long mProcessId;

      /**
       * Constructs work for the given process id.
       *
       * @param aProcessId
       */
      public AeProcessReleaseWork(long aProcessId)
      {
         mProcessId = aProcessId;
      }

      /**
       * Returns the process id.
       */
      protected long getProcessId()
      {
         return mProcessId;
      }

      /**
       * @see java.lang.Runnable#run()
       */
      public void run()
      {
         try
         {
            // This handles timer expired, too.
            cancelReleaseTimer(getProcessId());
         }
         catch (Throwable t)
         {
            AeException.logError(t, AeMessages.format("AePersistentProcessManager.ERROR_51", getProcessId())); //$NON-NLS-1$
         }
      }
   }
}
