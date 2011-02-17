// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/test/AeMirroringProcessManager.java,v 1.4 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.AePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.process.AeProcessStateWriter;
import org.activebpel.rt.bpel.server.engine.process.AeProcessWrapper;
import org.activebpel.rt.bpel.server.engine.recovery.AeRecoveryEngineFactory;
import org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveryEngine;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeSentReplyJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.work.AeAbstractWork;

import commonj.work.Work;
import commonj.work.WorkManager;

/**
 * Implements a process manager that mirrors the execution of each process to a
 * new process by replaying the journal entries for the process.
 */
public class AeMirroringProcessManager extends AeDelegatingPersistentProcessManager implements IAePersistentProcessManager
{
   /** Maps process ids to maps of captured journal entries. */
   private final Map<Long,Object> mCapturedItemsMapsMap = Collections.synchronizedMap(new HashMap());

   /** The recovery engine. */
   private IAeRecoveryEngine mRecoveryEngine;

   /**
    * Constructs mirroring process manager from the given configuration map.
    *
    * @param aConfig
    * @throws AeException
    */
   public AeMirroringProcessManager(Map aConfig) throws AeException
   {
      super(aConfig);

      // Replace default process writer with one that captures journal entries.
      AePersistentProcessManager base = (AePersistentProcessManager) getBasePersistentProcessManager();
      base.setProcessStateWriter(new AeCapturingProcessStateWriter(base));
   }

   /**
    * Captures journal entries for the given process before they are deleted by
    * process saving.
    */
   protected void captureJournalEntries(long aProcessId) throws AeStorageException
   {
      Map<Long,Object> capturedEntriesMap = (Map<Long, Object>) getCapturedEntriesMapsMap().get(aProcessId);
      if (capturedEntriesMap != null)
      {
         List latestEntries = getStorage().getJournalEntries(aProcessId);

         // Add the latest entries to the map of captured entries. We use a
         // map, so that we can be sure that we have only one instance of each
         // journal entry, since a journal entry that is in storage now may not
         // be deleted from storage until the next time the process is saved.
         for (Iterator i = latestEntries.iterator(); i.hasNext(); )
         {
            IAeJournalEntry entry = (IAeJournalEntry) i.next();
            capturedEntriesMap.put(entry.getJournalId(), entry);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#createBusinessProcess(org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public IAeBusinessProcess createBusinessProcess(IAeProcessPlan aProcessPlan) throws AeBusinessProcessException
   {
      // Create the process.
      IAeBusinessProcess process = super.createBusinessProcess(aProcessPlan);
      long processId = process.getProcessId();

      if (isPersistent(processId))
      {
         // Map the process id to an empty journal entries map. This marks the
         // process for mirroring. Note that the journal entries map is based
         // on a LinkedHashMap to preserve order of insertions.
         getCapturedEntriesMapsMap().put(processId, new LinkedHashMap());
      }

      return process;
   }

   /**
    * Returns map from process ids to maps of journal entries.
    */
   protected Map<Long,Object> getCapturedEntriesMapsMap()
   {
      return mCapturedItemsMapsMap;
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
    * Replays the journal entries for the specified process to a new process.
    *
    * @param aProcessId
    */
   protected synchronized void replayCapturedEntries(long aProcessId, IAeProcessPlan aProcessPlan) throws AeBusinessProcessException
   {
      // The whole method is synchronized, because we can only recover one
      // process at a time, and we don't want to fill up the process table with
      // processes waiting to be replayed.
      Map<Long,Object> capturedEntriesMap = (Map<Long, Object>) getCapturedEntriesMapsMap().remove(aProcessId);
      if (capturedEntriesMap != null)
      {
         // Call super.createBusinessProcess() and not createBusinessProcess(),
         // so that we don't try to mirror a mirror process.
         IAeBusinessProcess mirror = super.createBusinessProcess(aProcessPlan);

         try
         {
            long mirrorId = mirror.getProcessId();

            // Release the mirror process quickly from memory when we're done.
            processEnded(mirrorId);

            debug(
               "Process {0,number,0}: replaying process {1,number,0}", //$NON-NLS-1$
               new Object[] { new Long(mirrorId), new Long(aProcessId) }
            );

            List capturedEntries = new LinkedList(capturedEntriesMap.values());
            capturedEntries = transferSentReplies(capturedEntries, mirrorId);

            List recoveredItems = getRecoveryEngine().recover(mirror, capturedEntries, false);

            int n = recoveredItems.size();
            if (n > 0)
            {
               AeException.logWarning(AeMessages.format("AeMirroringProcessManager.WARNING_RECOVERED_ITEMS", //$NON-NLS-1$
                  new Object[] { new Long(mirrorId), new Integer(n) }));
            }
         }
         finally
         {
            releaseProcess(mirror);
         }
      }
   }

   /**
    * Sets the process id of each sent reply to the process id of the mirror
    * process.
    */
   protected List transferSentReplies(List aJournalEntries, long aMirrorId) throws AeBusinessProcessException
   {
      for (Iterator i = aJournalEntries.iterator(); i.hasNext(); )
      {
         IAeJournalEntry entry = (IAeJournalEntry) i.next();
         if (entry instanceof AeSentReplyJournalEntry)
         {
            AeReply sentReply = ((AeSentReplyJournalEntry) entry).getReply();
            sentReply.setProcessId(aMirrorId);
         }
      }

      return aJournalEntries;
   }

   /**
    * Implements a process state writer that captures the journal entries for a
    * process before saving its state.
    */
   protected class AeCapturingProcessStateWriter extends AeProcessStateWriter
   {
      /**
       * Constructs a process state writer that captures journal entries.
       *
       * @param aProcessManager
       */
      public AeCapturingProcessStateWriter(IAePersistentProcessManager aProcessManager)
      {
         super(aProcessManager);
      }

      /**
       * @see org.activebpel.rt.bpel.server.engine.process.AeProcessStateWriter#writeProcess(org.activebpel.rt.bpel.server.engine.process.AeProcessWrapper)
       */
      public int writeProcess(AeProcessWrapper aProcessWrapper) throws AeBusinessProcessException
      {
         long processId = aProcessWrapper.getProcessId();

         // Capture journal entries before they are deleted by process saving.
         try
         {
            captureJournalEntries(processId);
         }
         catch (Throwable t)
         {
            AeException.logError(t, AeMessages.format("AeMirroringProcessManager.ERROR_0", processId)); //$NON-NLS-1$
         }

         IAeBusinessProcess process = aProcessWrapper.getProcess();

         // If the process has ended, then schedule work to replay the journal
         // entries.
         if (process.isFinalState() && getCapturedEntriesMapsMap().containsKey(processId))
         {
            // Get the process plan now, so that we don't have to restore the
            // process just to get its plan.
            Work work = new AeMirroringWork(processId, process.getProcessPlan());

            try
            {
               AeEngineFactory.getBean(WorkManager.class).schedule(work);
            }
            catch (Exception e)
            {
               AeException.logError(e, AeMessages.format("AeMirroringProcessManager.ERROR_1", processId)); //$NON-NLS-1$

               // Do the work on this thread.
               work.run();
            }
         }

         // Now save the process state normally.
         return super.writeProcess(aProcessWrapper);
      }
   }

   /**
    * Implements work to replay the journal entries for a process.
    */
   protected class AeMirroringWork extends AeAbstractWork
   {
      /** The process id. */
      private final long mProcessId;

      /** The process plan. */
      private final IAeProcessPlan mProcessPlan;

      /**
       * Constructs work for the given process id and process plan.
       *
       * @param aProcessId
       * @param aProcessPlan
       */
      public AeMirroringWork(long aProcessId, IAeProcessPlan aProcessPlan)
      {
         mProcessId = aProcessId;
         mProcessPlan = aProcessPlan;
      }

      /**
       * Returns the process id.
       */
      protected long getProcessId()
      {
         return mProcessId;
      }

      /**
       * Returns the process plan.
       */
      protected IAeProcessPlan getProcessPlan()
      {
         return mProcessPlan;
      }

      /**
       * @see java.lang.Runnable#run()
       */
      public void run()
      {
         try
         {
            replayCapturedEntries(getProcessId(), getProcessPlan());
         }
         catch (Throwable t)
         {
            AeException.logError(t, AeMessages.format("AeMirroringProcessManager.ERROR_2", getProcessId())); //$NON-NLS-1$
         }
      }
   }
}
