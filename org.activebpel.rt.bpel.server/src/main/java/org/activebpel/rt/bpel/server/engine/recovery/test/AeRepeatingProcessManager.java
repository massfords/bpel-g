// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/test/AeRepeatingProcessManager.java,v 1.4 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.test;

import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.AeProcessDeploymentFactory;
import org.activebpel.rt.bpel.server.engine.AePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.process.AeProcessStateWriter;
import org.activebpel.rt.bpel.server.engine.process.AeProcessWrapper;
import org.activebpel.rt.bpel.server.engine.process.IAeProcessStateReader;
import org.activebpel.rt.bpel.server.engine.recovery.AeRecoveryEngineFactory;
import org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveryEngine;

/**
 * Implements a process manager that repeats the execution of a process through
 * its journal entries each time the process is saved.
 */
public class AeRepeatingProcessManager extends AeDelegatingPersistentProcessManager implements IAePersistentProcessManager
{
   /** The process state reader from the underlying persistent process manager. */
   private IAeProcessStateReader mProcessStateReader;

   /** The recovery engine. */
   private IAeRecoveryEngine mRecoveryEngine;

   /**
    * Constructs a repeating process manager from the given configuration map.
    */
   public AeRepeatingProcessManager(Map aConfig) throws Exception
   {
      super(aConfig);

      // Replace default process writer with one that repeats process
      // execution.
      AePersistentProcessManager base = (AePersistentProcessManager) getBasePersistentProcessManager();
      base.setProcessStateWriter(new AeRepeatingProcessStateWriter(base));
   }

   /**
    * Returns the process state reader from the underlying persistent process
    * manager.
    */
   protected IAeProcessStateReader getProcessStateReader()
   {
      if (mProcessStateReader == null)
      {
         AePersistentProcessManager base = (AePersistentProcessManager) getBasePersistentProcessManager();
         mProcessStateReader = base.getProcessStateReader(); 
      }

      return mProcessStateReader;
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
    * Repeats the most recent execution of the given process by replaying its
    * journal entries.
    */
   protected void repeatProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      long processId = aProcess.getProcessId();
      IAeProcessDeployment deployment = AeProcessDeploymentFactory.getDeploymentForPlan(aProcess.getProcessPlan());

      // Create a new instance of the process.
      IAeBusinessProcess clone = AeDefToImplVisitor.createProcess(processId, getEngine(), deployment);

      // Restore its state from storage.
      getProcessStateReader().readProcess(clone);

      // Get the journal entries from storage.
      List journalEntries = getStorage().getJournalEntries(processId);

      // Replay the journal entries to the clone.
      getRecoveryEngine().recover(clone, journalEntries, false);
   }

   /**
    * Implements a process state writer that repeats process execution before
    * saving its state.
    */
   protected class AeRepeatingProcessStateWriter extends AeProcessStateWriter
   {
      /**
       * Constructs a process state writer that repeats process execution.
       *
       * @param aProcessManager
       */
      public AeRepeatingProcessStateWriter(IAePersistentProcessManager aProcessManager)
      {
         super(aProcessManager);
      }

      /**
       * @see org.activebpel.rt.bpel.server.engine.process.AeProcessStateWriter#writeProcess(org.activebpel.rt.bpel.server.engine.process.AeProcessWrapper)
       */
      public int writeProcess(AeProcessWrapper aProcessWrapper) throws AeBusinessProcessException
      {
         try
         {
            repeatProcess(aProcessWrapper.getProcess());
         }
         catch (Throwable t)
         {
            AeException.logError(t, AeMessages.format("AeRepeatingProcessManager.ERROR_REPEAT", aProcessWrapper.getProcessId())); //$NON-NLS-1$
         }

         // Now save the process state.
         return super.writeProcess(aProcessWrapper);
      }
   }
}
