// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/IAePersistentProcessManager.java,v 1.10 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.io.Reader;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeInboundReceiveJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage;

/**
 * Persistent version of the process manager.
 */
public interface IAePersistentProcessManager extends IAeProcessManager
{
   /**
    * Gets a Reader to the process's log.
    * @param aProcessId
    * @throws AeBusinessProcessException
    */
   public Reader dumpLog(long aProcessId) throws AeBusinessProcessException;

   /**
    * Returns number of times to retry deadlocked transactions.
    */
   public int getDeadlockTryCount();

   /**
    * Returns the effective process limit, which is the greater of the
    * configured maximum process count or the number of processes required for
    * recovery.
    */
   public int getEffectiveProcessLimit();

   /**
    * Returns the journal entry with the given journal id.
    */
   public IAeJournalEntry getJournalEntry(long aJournalId) throws AeBusinessProcessException;

   /**
    * Returns the maximum number of processes allowed in memory.
    */
   public int getMaxProcessCount();

   /**
    * Gets the process log for the given pid.
    * @param aProcessId
    */
   public String getProcessLog(long aProcessId) throws AeBusinessProcessException;

   /**
    * Returns process state storage.
    */
   public IAeProcessStateStorage getStorage();

   /**
    * Returns <code>true</code> if and only if process is container-managed.
    */
   public boolean isContainerManaged(long aProcessId);

   /**
    * Returns <code>true</code> if and only if the given process id currently
    * resident in memory.
    *
    * @param aProcessId
    */
   public boolean isLoaded(long aProcessId);
   
   /**
    * Returns <code>true</code> if and only if process is persistent.
    */
   public boolean isPersistent(long aProcessId);

   /**
    * Returns the restart process journal entry for the given process.
    *
    * @param aProcessId
    * @throws AeBusinessProcessException
    */
   public AeInboundReceiveJournalEntry getRestartProcessJournalEntry(long aProcessId) throws AeBusinessProcessException;

   /**
    * Indicates that the process manager should delete all existing journal
    * entries for the given process when the process is next persisted.
    *
    * @param aProcessId
    * @throws AeBusinessProcessException
    */
   public void journalAllEntriesDone(long aProcessId) throws AeBusinessProcessException;
}
