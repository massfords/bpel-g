// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/process/AeProcessWrapper.java,v 1.8 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.process;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.AeProcessPersistenceType;
import org.activebpel.rt.bpel.server.deploy.AeProcessTransactionType;
import org.activebpel.rt.bpel.server.engine.AePersistentProcessManager;
import org.activebpel.rt.util.AeLongSet;
import org.activebpel.rt.util.AeMutex;

/**
 * Wraps a process with its reference count and state.
 */
public class AeProcessWrapper
{
   /** The process id. */
   private long mProcessId;

   /** Process mutex. */
   private final AeMutex mMutex = new AeMutex();

   /** Journal entry ids to delete when process state is next saved. */
   private final AeLongSet mCompletedJournalIds = new AeLongSet();

   /** The process. */
   private IAeBusinessProcess mProcess;

   /** Reference count. */
   private int mCount = 0;

   /** <code>true</code> if and only if the process may have been modified. */
   private boolean mModified = true;

   /** <code>true</code> if and only if the process should release quickly. */
   private boolean mQuickRelease = false;

   /** Process persistence type. */
   private AeProcessPersistenceType mPersistenceType;

   /** Process transaction type. */
   private AeProcessTransactionType mTransactionType;
   
   /** Invoke transmission ids to delete when process state is next saved. */
   private final AeLongSet mCompletedTransmissionIds = new AeLongSet();

   /** Journal entry id to set aside for restart when process state is next saved. */
   private long mJournalIdForRestart = IAeProcessManager.NULL_JOURNAL_ID;

   /**
    * Constructs process wrapper.
    *
    * @param aProcessId
    */
   public AeProcessWrapper(long aProcessId)
   {
      mProcessId = aProcessId;
   }

   /**
    * Acquires process mutex, blocking if necessary until the current owner
    * relinquishes ownership.
    */
   public void acquireMutex()
   {
      getMutex().acquireMutex();
   }

   /**
    * A debugging routine that modifies the reference count by the given amount
    * with an interposing call to {@link Thread#yield()}. This makes it easier
    * to shake out problems with the way the reference count is used in a
    * multi-threaded environment.
    */
   protected void adjustCountWithYield(int aDelta)
   {
      int n = mCount;

      Thread.yield();

      mCount = n + aDelta;
   }

   /**
    * Decrements the reference count and returns the new effective reference
    * count.
    *
    * @return int
    */
   public synchronized int decrementCount()
   {
      if (isDebug())
      {
         adjustCountWithYield(-1);
      }
      else
      {
         --mCount;
      }

      if (mCount < 0)
      {
         // Reset it to a sane value.
         mCount = 0;

         throw new IllegalStateException(AeMessages.format("AeProcessWrapper.ERROR_0", getProcessId())); //$NON-NLS-1$
      }

      return getCount();
   }

   /**
    * Returns journal entry ids to delete when process state is next saved.
    */
   public AeLongSet getCompletedJournalIds()
   {
      return mCompletedJournalIds;
   }
   
   /** 
    * @return set of durable/persistent invoke transmission ids to be deleted when the process state is next saved.
    */
   public AeLongSet getCompletedTransmissionIds()
   {
      return mCompletedTransmissionIds;
   }

   /**
    * Returns the current effective reference count, which is the actual
    * reference count plus possibly one more artificial reference to keep
    * non-persistent processes in memory.
    *
    * @return int
    */
   public int getCount()
   {
      int count = mCount;

      // Keep the process in memory if we're not doing full persistence and
      // it is still running.
      if (getPersistenceType() != AeProcessPersistenceType.FULL)
      {
         IAeBusinessProcess process = getProcess();

         if ((process != null) && !process.isFinalState())
         {
            // Keep the process in memory.
            ++count;
         }
      }

      return count;
   }

   /**
    * Returns process mutex.
    */
   protected AeMutex getMutex()
   {
      return mMutex;
   }

   /**
    * Returns the process persistence type.
    */
   protected AeProcessPersistenceType getPersistenceType()
   {
      return mPersistenceType;
   }

   /**
    * Returns the process.
    */
   public IAeBusinessProcess getProcess()
   {
      return mProcess;
   }

   /**
    * Returns the process id.
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * Returns the process transaction type.
    */
   protected AeProcessTransactionType getTransactionType()
   {
      return mTransactionType;
   }

   /**
    * Increments the reference count.
    */
   public synchronized void incrementCount()
   {
      if (isDebug())
      {
         adjustCountWithYield(+1);
      }
      else
      {
         ++mCount;
      }
   }

   /**
    * Returns <code>true</code> if and only if process is container-managed.
    */
   public boolean isContainerManaged()
   {
      return getTransactionType() == AeProcessTransactionType.CONTAINER;
   }

   /**
    * Returns <code>true</code> if and only if the process manager is in debug
    * mode.
    */
   protected static boolean isDebug()
   {
      return AePersistentProcessManager.isDebug();
   }

   /**
    * Returns <code>true</code> if and only if the process may have been
    * modified.
    */
   public boolean isModified()
   {
      return mModified;
   }

   /**
    * Returns <code>true</code> if and only if process is persistent.
    */
   public boolean isPersistent()
   {
      return getPersistenceType() != AeProcessPersistenceType.NONE;
   }

   /**
    * Returns <code>true</code> if and only if process should release quickly.
    */
   public boolean isQuickRelease()
   {
      return mQuickRelease;
   }

   /**
    * Releases process mutex.
    */
   public void releaseMutex()
   {
      getMutex().releaseMutex();
   }

   /**
    * Marks the process as potentially modified or not.
    */
   public void setModified(boolean aModified)
   {
      mModified = aModified;
   }

   /**
    * Sets the process.
    *
    * @param aProcess
    */
   public void setProcess(IAeBusinessProcess aProcess)
   {
      // set the process
      mProcess = aProcess;
      // if non-null process update our id as it may have changed during a create
      if(mProcess != null)
         mProcessId = mProcess.getProcessId();

      IAeProcessDeployment aDeployment = (aProcess == null) ? null : (IAeProcessDeployment) aProcess.getProcessPlan();

      if (aDeployment != null)
      {
         mPersistenceType = aDeployment.getPersistenceType();
         mTransactionType = aDeployment.getTransactionType();
      }
      else
      {
         // If we don't have the process deployment at this point, then the
         // process is being restored and can't be a service flow process.
         mPersistenceType = AeProcessPersistenceType.FULL;
         mTransactionType = AeProcessTransactionType.BEAN;
      }
   }

   /**
    * Marks process to release quickly.
    */
   public void setQuickRelease()
   {
      mQuickRelease = true;
   }

   /**
    * @param aJournalId the journal entry id to set aside for restart when process state is next saved
    */
   public void setJournalIdForRestart(long aJournalId)
   {
      mJournalIdForRestart = aJournalId;
   }

   /**
    * @return the journal entry id to set aside for restart when process state is next saved
    */
   public long getJournalIdForRestart()
   {
      return mJournalIdForRestart;
   }
}
