// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/test/AeDelegatingPersistentProcessManager.java,v 1.5 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.test;

import java.io.Reader;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeInboundReceiveJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage;

/**
 * Implements a persistent process manager that delegates all method calls to
 * an underlying delegate persistent process manager.
 */
public class AeDelegatingPersistentProcessManager extends AeDelegatingProcessManager implements IAePersistentProcessManager
{
   /**
    * Constructs a persistent process manager that delegates all method calls
    * to a delegate process manager constructed from the given configuration.
    *
    * @param aConfig
    */
   public AeDelegatingPersistentProcessManager(Map aConfig) throws AeException
   {
      super(aConfig);
   }

   /**
    * Returns the base persistent process manager.
    */
   protected IAePersistentProcessManager getBasePersistentProcessManager()
   {
      return (IAePersistentProcessManager) getBaseProcessManager();
   }

   /*=========================================================================
    * org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager methods
    *=========================================================================
    */

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#dumpLog(long)
    */
   public Reader dumpLog(long aProcessId) throws AeBusinessProcessException
   {
      return getBasePersistentProcessManager().dumpLog(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getDeadlockTryCount()
    */
   public int getDeadlockTryCount()
   {
      return getBasePersistentProcessManager().getDeadlockTryCount();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getEffectiveProcessLimit()
    */
   public int getEffectiveProcessLimit()
   {
      return getBasePersistentProcessManager().getEffectiveProcessLimit();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getJournalEntry(long)
    */
   public IAeJournalEntry getJournalEntry(long aJournalId) throws AeBusinessProcessException
   {
      return getBasePersistentProcessManager().getJournalEntry(aJournalId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getMaxProcessCount()
    */
   public int getMaxProcessCount()
   {
      return getBasePersistentProcessManager().getMaxProcessCount();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getProcessLog(long)
    */
   public String getProcessLog(long aProcessId) throws AeBusinessProcessException
   {
      return getBasePersistentProcessManager().getProcessLog(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getStorage()
    */
   public IAeProcessStateStorage getStorage()
   {
      return getBasePersistentProcessManager().getStorage();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#isContainerManaged(long)
    */
   public boolean isContainerManaged(long aProcessId)
   {
      return getBasePersistentProcessManager().isContainerManaged(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#isLoaded(long)
    */
   public boolean isLoaded(long aProcessId)
   {
      return getBasePersistentProcessManager().isLoaded(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#isPersistent(long)
    */
   public boolean isPersistent(long aProcessId)
   {
      return getBasePersistentProcessManager().isPersistent(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getRestartProcessJournalEntry(long)
    */
   public AeInboundReceiveJournalEntry getRestartProcessJournalEntry(long aProcessId) throws AeBusinessProcessException
   {
      return getBasePersistentProcessManager().getRestartProcessJournalEntry(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#journalAllEntriesDone(long)
    */
   public void journalAllEntriesDone(long aProcessId) throws AeBusinessProcessException
   {
      getBasePersistentProcessManager().journalAllEntriesDone(aProcessId);
   }
}
