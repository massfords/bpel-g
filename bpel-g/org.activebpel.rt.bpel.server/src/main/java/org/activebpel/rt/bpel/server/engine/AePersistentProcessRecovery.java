// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AePersistentProcessRecovery.java,v 1.18 2008/02/19 14:20:09 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.AeSuspendReason;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.recovery.AeInvokeRecoveryEvaluator;
import org.activebpel.rt.bpel.server.engine.recovery.AeRecoveryEngineFactory;
import org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveryEngine;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeInboundReceiveJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeRestartProcessJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage;
import org.activebpel.rt.util.AeLongSet;
import org.activebpel.rt.util.AeUtil;

/**
 * Implements recovering processes from persistent process storage.
 */
public class AePersistentProcessRecovery implements IAeProcessRecovery
{
   /** Mutex object for critical section in <code>prepareToRecover()</code>. */
   private static final Object sPrepareMutex = new Object();

   /** The recoverable process manager. */
   private final IAeRecoverableProcessManager mProcessManager;

   /** <code>true</code> if and only if <code>prepareToRecover</code> has been called. */
   private boolean mPrepared;

   /** The set of ids of processes that have been prepared for recovery. */
   private AeLongSet mPreparedProcessIds;

   /** The recovery engine. */
   private IAeRecoveryEngine mRecoveryEngine;

   /**
    * Constructs recovery implementation for the specified process manager.
    *
    * @param aProcessManager
    */
   public AePersistentProcessRecovery(IAeRecoverableProcessManager aProcessManager)
   {
      mProcessManager = aProcessManager;
   }

   /**
    * Writes debugging output.
    */
   protected void debug(String aMessage)
   {
      if (getProcessManager() instanceof AePersistentProcessManager)
      {
         ((AePersistentProcessManager) getProcessManager()).debug(aMessage);
      }
   }

   /**
    * Returns the business process engine.
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return getProcessManager().getEngine();
   }

   /**
    * Returns uncleared journal entries for the given process as a list of
    * {@link IAeJournalEntry} instances in the order that they were saved.
    *
    * @param aProcessId
    */
   protected List getJournalEntries(long aProcessId)
   {
      try
      {
         return getStorage().getJournalEntries(aProcessId);
      }
      catch (AeStorageException e)
      {
         AeException.logError(e, AeMessages.format("AePersistentProcessRecovery.ERROR_0", aProcessId)); //$NON-NLS-1$
         return Collections.EMPTY_LIST;
      }
   }

   /**
    * Returns the journal entry ids from the given journal entries. Filters out
    * the create instance journal item if present.
    *
    * @param aJournalEntries
    */
   protected AeLongSet getDoneJournalIds(List aJournalEntries)
   {
      AeLongSet result = new AeLongSet();
      boolean restartEnabled = getEngine().getEngineConfiguration().isProcessRestartEnabled();
      for (Iterator i = aJournalEntries.iterator(); i.hasNext(); )
      {
         IAeJournalEntry entry = (IAeJournalEntry) i.next();
         if (restartEnabled && isCreateInstance(entry))
         {
            // skip over the entry if it's the create instance one
            // we flip this to a restart item instead of marking it as done.
         }
         else
         {
         result.add(entry.getJournalId());
      }
      }

      return result;
   }

   /**
    * Returns invokes pending for the specified process.
    *
    * @param aProcess
    */
   protected Set getPendingInvokes(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      return aProcess.getProcessSnapshot().getPendingInvokes();
   }

   /**
    * Returns the set of ids of processes that have been prepared for recovery.
    */
   protected AeLongSet getPreparedProcessIds()
   {
      if (mPreparedProcessIds == null)
      {
         mPreparedProcessIds = new AeLongSet(new TreeSet());
      }

      return mPreparedProcessIds;
   }

   /**
    * Returns the process with the specified process id.
    *
    * @param aProcessId
    */
   protected IAeBusinessProcess getProcess(long aProcessId) throws AeBusinessProcessException
   {
      return getProcessManager().getProcess(aProcessId);
   }

   /**
    * Returns the recoverable process manager.
    */
   protected IAeRecoverableProcessManager getProcessManager()
   {
      return mProcessManager;
   }

   /**
    * Returns the recovery engine.
    */
   protected IAeRecoveryEngine getRecoveryEngine()
   {
      if (mRecoveryEngine == null)
      {
         mRecoveryEngine = AeRecoveryEngineFactory.getInstance().newRecoveryEngine();
      }

      return mRecoveryEngine;
   }

   /**
    * Returns the set of ids for processes with work to be recovered.
    */
   protected Set getRecoveryProcessIds() throws AeStorageException
   {
      return getStorage().getRecoveryProcessIds();
   }

   /**
    * Convenience method that returns the process state storage.
    */
   protected IAeProcessStateStorage getStorage()
   {
      return getProcessManager().getStorage();
   }

   /**
    * Returns <code>true</code> if and only if the engine is running.
    */
   protected static boolean isEngineRunning()
   {
      return AeEngineFactory.getEngine().isRunning();
   }

   /**
    * Returns <code>true</code> if and only if <code>prepareToRecover</code>
    * has been called.
    */
   protected boolean isPrepared()
   {
      return mPrepared;
   }

   /**
    * Returns the location path for an invoke that has the suspend on recovery
    * policy enabled which causes the process to suspend in lieu of re-executing
    * the invoke. 
    *
    * @param aPendingInvokes
    * @return String location path for the invoke or null if none found
    */
   protected String getPathForSuspendingInvoke(Set aPendingInvokes) throws AeBusinessProcessException
   {
      AeInvokeRecoveryEvaluator evaluator = new AeInvokeRecoveryEvaluator();

      for (Iterator i = aPendingInvokes.iterator(); i.hasNext(); )
      {
         AeActivityInvokeImpl invoke = (AeActivityInvokeImpl) i.next();
         evaluator.setInvoke(invoke);

         if (evaluator.isSuspendProcessRequired())
         {
            return invoke.getLocationPath();
         }
      }

      return null;
   }

   /**
    * Overrides method to load processes that require recovery.
    *
    * @see org.activebpel.rt.bpel.server.engine.IAeProcessRecovery#prepareToRecover()
    */
   public void prepareToRecover()
   {
      mPrepared = true;

      Set ids;

      try
      {
         ids = getRecoveryProcessIds();
      }
      catch (AeStorageException e)
      {
         AeException.logError(e, AeMessages.getString("AePersistentProcessRecovery.ERROR_2")); //$NON-NLS-1$
         ids = Collections.EMPTY_SET;
      }

      // This is synchronized globally to prevent one recovery from clobbering
      // the process count required for another concurrent recovery.
      synchronized (sPrepareMutex)
      {
         // Make sure that the process manager has enough room to load the
         // process wrappers.
         getProcessManager().setRecoveryProcessCount(ids.size());

         try
         {
            // Grab mutexes for the processes that require recovery. We release
            // the mutexes in recover() as we recover individual processes.
            for (Iterator i = ids.iterator(); i.hasNext(); )
            {
               long processId = ((Number) i.next()).longValue();

               getProcessManager().acquireProcessMutex(processId);
               getPreparedProcessIds().add(processId);
            }
         }
         finally
         {
            getProcessManager().setRecoveryProcessCount(0);
         }
      }
   }

   /**
    * Overrides method to call {@link #recoverUnderMutex()} after synchronizing
    * on the process manager's
    * {@link IAeRecoverableProcessManager#getRecoveryAndStopMutex()} object.
    *  
    * @see org.activebpel.rt.bpel.server.engine.IAeProcessRecovery#recover()
    */
   public void recover()
   {
      // To fix defect 1527, "Getting an IllegalArgumentException when
      // recovery is attempting to be done but the server is in the middle of
      // shutting down," we need to keep the engine from stopping the timer
      // manager while in the middle of recovery. AeBpelEngine stops the timer
      // manager after stopping all the managers, so by synchronizing on
      // AePersistentProcessManager#getRecoveryStopMutex() here we prevent
      // AePersistentProcessManager#stop() from returning until we return.
      synchronized (getProcessManager().getRecoveryAndStopMutex())
      {
         recoverUnderMutex();
      }
   }

   /**
    * Recovers pending work. Calls <code>prepareToRecover()</code> if not yet prepared.
    */
   protected void recoverUnderMutex()
   {
      if (!isPrepared())
      {
         prepareToRecover();
      }

      AeLongSet ids = getPreparedProcessIds();
      long millis = System.currentTimeMillis();

      int n = ids.size();
      debug("Recovering " + n + " process" + ((n == 1) ? "" : "es")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

      // Iterate over process ids that have been prepared for recovery.
      for (Iterator i = ids.iterator(); i.hasNext(); )
      {
         long processId = ((Number) i.next()).longValue();

         try
         {
            IAeBusinessProcess process;

            try
            {
               // Fix defect 1527, "Getting an IllegalArgumentException when
               // recovery is attempting to be done but the server is in the
               // middle of shutting down," by performing recovery only while
               // the engine is running. When someone tries to shut down the
               // engine, AeBpelEngine transitions first to the state
               // IAeEngineAdministration.STOPPING, which is not a running
               // state. This gives us a chance to get out of recovery before
               // the engine stops the timer manager (because we are running
               // under a mutex that keeps the process manager from stopping
               // while we are in this loop).
               //
               // This test also addresses defect 1475, "You cannot stop the
               // ActiveBPEL engine while it is in the middle of recovery,"
               // because recovery stops as soon as the engine starts to shut
               // down.
               if (isEngineRunning())
               {
                  process = getProcess(processId);
               }
               else
               {
                  process = null;
               }
            }
            finally
            {
               // Make sure to release the process mutex that we acquired in
               // prepareToRecover(), but remember that releaseProcess() has to
               // be the last call in order to persist the recovered state.
               getProcessManager().releaseProcessMutex(processId);
            }

            if (process != null)
            {
               try
               {
                  recover(process);
               }
               finally
               {
                  releaseProcess(process);
               }
            }
         }
         // Catch even null pointer and other runtime exceptions here to
         // prevent problems recovering one process from affecting other
         // processes or engine startup.
         catch (Throwable t)
         {
            AeException.logError(t, AeMessages.format("AePersistentProcessRecovery.ERROR_12", processId)); //$NON-NLS-1$
         }
      }

      long elapsed = System.currentTimeMillis() - millis;
      debug("Recovery done (" + elapsed + " millis)"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Recovers pending work for the specified process.
    *
    * @param aProcess
    */
   protected void recover(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      // Dispatch journal entries anew.
      //
      // Note: Dispatch journal entries before restarting pending invokes,
      // because some of the journal entries may be received data or faults for
      // pending invokes (in which case the invokes will no longer be pending
      // after recovery).
      long processId = aProcess.getProcessId();
      List journalEntries = getJournalEntries(processId);

      // Remove the restart process journal entry.
      journalEntries = removeRestartProcessJournalEntry(journalEntries);

      // Recover the process state and queue recovered requests.
      getRecoveryEngine().recover(aProcess, journalEntries, true);
      
      AeLongSet doneJournalIds = getDoneJournalIds(journalEntries);

      // TODO (KR) Administrator should have a way to purge process and its journal entries if recovery fails.
      getProcessManager().journalEntriesDone(processId, doneJournalIds);
      
      // Need to handle the case where there was no restart journal but restart 
      // is enabled. In this case, we want to flip the type for the create 
      // instance journal id to be a restart.
      if (getEngine().getEngineConfiguration().isProcessRestartEnabled())
      {
         long createInstanceJournalId = getCreateInstanceJournalId(journalEntries);
         if (createInstanceJournalId != IAeProcessManager.NULL_JOURNAL_ID)
         {
            getProcessManager().journalEntryForRestart(processId, createInstanceJournalId);
         }
      }

      // Get pending invokes. This includes invokes that were pending before
      // recovery and invokes that became pending during recovery.
      Set pendingInvokes = getPendingInvokes(aProcess);

      String locationPath = getPathForSuspendingInvoke(pendingInvokes);

      // Check for a pending invoke that requires suspending the process.
      if (locationPath != null)
      {
         suspendProcess(aProcess, locationPath);
      }
      else
      {
         // Restart pending invokes.
         restartPendingInvokes(aProcess, pendingInvokes);
      }
   }

   /**
    * Return the journal id for the create instance journal entry. If present,
    * this entry will be the first entry in the list. This means that the 
    * engine crashed prior to a full state save so it didn't have a chance to
    * flip the initial create instance receive to be a restart item.
    * 
    * @param aJournalEntries
    */
   private long getCreateInstanceJournalId(List aJournalEntries)
   {
      if (AeUtil.notNullOrEmpty(aJournalEntries))
      {
         // only need to check the first one
         IAeJournalEntry entry = (IAeJournalEntry) aJournalEntries.get(0);
         
         if (isCreateInstance(entry))
         {
            return entry.getJournalId();
         }
      }
      return IAeProcessManager.NULL_JOURNAL_ID;
   }
   
   /**
    * Returns true if the journal entry is the create instance journal entry.
    * @param aEntry
    */
   private boolean isCreateInstance(IAeJournalEntry aEntry)
   {
      if (aEntry instanceof AeInboundReceiveJournalEntry)
      {
         AeInboundReceiveJournalEntry inboundReceive = (AeInboundReceiveJournalEntry) aEntry;
         return inboundReceive.isCreateInstance();
      }
      return false;
   }

   /**
    * Releases a process locked into memory by <code>getProcess</code>.
    *
    * @param aProcess The process to release.
    */
   protected void releaseProcess(IAeBusinessProcess aProcess)
   {
      getProcessManager().releaseProcess(aProcess);
   }

   /**
    * Restarts the pending invokes for the given process.
    *
    * @param aProcess
    * @param aPendingInvokes
    * @throws AeBusinessProcessException
    */
   protected void restartPendingInvokes(IAeBusinessProcess aProcess, Set aPendingInvokes) throws AeBusinessProcessException
   {
      for (Iterator i = aPendingInvokes.iterator(); i.hasNext(); )
      {
         AeActivityInvokeImpl invoke = (AeActivityInvokeImpl) i.next();
         if (invoke.isPending())
         {
            try
            {
               invoke.queueInvoke(null);
            }
            catch (AeBusinessProcessException e)
            {
               long processId = aProcess.getProcessId();
               int locationId = invoke.getLocationId();
               Object[] params = new Object[] { String.valueOf(processId), String.valueOf(locationId) };

               // Report the problem.
               AeException.logError(e, AeMessages.format("AePersistentProcessRecovery.ERROR_RestartInvoke", params)); //$NON-NLS-1$

               // Give the process a chance to handle the failed invoke.
               aProcess.handleExecutableItemException(invoke, e);
            }
         }
      }
   }

   /**
    * Suspends the given process.
    *
    * @param aProcess
    * @throws AeBusinessProcessException 
    */
   protected void suspendProcess(IAeBusinessProcess aProcess, String aLocationPath) throws AeBusinessProcessException
   {
      aProcess.suspend(new AeSuspendReason(AeSuspendReason.SUSPEND_CODE_INVOKE_RECOVERY, aLocationPath, null));
   }

   /**
    * Removes the restart process journal entry from the given list of journal
    * entries. The restart process journal entry must be removed from the list
    * of journal entries used for recovery, because journal entries consumed
    * by process recovery are cleared (deleted) when recovery finishes.
    *
    * @param aEntries
    */
   protected List removeRestartProcessJournalEntry(List aEntries)
   {
      List entries = new ArrayList(aEntries.size());

      // Add all but the restart process journal entry.
      for (Iterator i = aEntries.iterator(); i.hasNext(); )
      {
         IAeJournalEntry entry = (IAeJournalEntry) i.next();

         if (!(entry instanceof AeRestartProcessJournalEntry))
         {
            entries.add(entry);
         }
      }

      return entries;
   }
}
