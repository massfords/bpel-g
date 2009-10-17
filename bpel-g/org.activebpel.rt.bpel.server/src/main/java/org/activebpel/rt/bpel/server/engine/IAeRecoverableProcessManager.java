// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/IAeRecoverableProcessManager.java,v 1.4 2006/11/08 18:17:40 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage;
import org.activebpel.rt.util.AeLongSet;

/**
 * Recoverable version of the process manager.
 */
public interface IAeRecoverableProcessManager extends IAeProcessManager
{
   /**
    * Acquires the process mutex for the given process.
    *
    * @param aProcessId
    */
   public void acquireProcessMutex(long aProcessId);

   /**
    * Returns synchronization object that enforces mutually exclusive execution
    * of recovery and the {@link AePersistentProcessManager#stop()} method.
    */
   public Object getRecoveryAndStopMutex();

   /**
    * Returns process state storage.
    */
   public IAeProcessStateStorage getStorage();

   /**
    * Adds the given journal entry ids to the set of journal entries to delete
    * when the process state is next saved.
    */
   public void journalEntriesDone(long aProcessId, AeLongSet aJournalIds);

   /**
    * Creates journal entry to recover an engine failure in the event that the
    * current recovery engine itself fails. 
    *
    * @param aProcessId
    * @param aDeadEngineId
    */
   public void journalEngineFailure(long aProcessId, int aDeadEngineId) throws AeBusinessProcessException;

   /**
    * Releases the process mutex for the given process.
    *
    * @param aProcessId
    */
   public void releaseProcessMutex(long aProcessId);

   /**
    * Sets the number of processes allowed in memory for recovery.
    *
    * @param aRecoveryProcessCount
    */
   public void setRecoveryProcessCount(int aRecoveryProcessCount);
}
