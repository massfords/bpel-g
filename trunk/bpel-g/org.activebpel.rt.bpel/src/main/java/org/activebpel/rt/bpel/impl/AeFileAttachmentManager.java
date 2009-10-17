// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeFileAttachmentManager.java,v 1.2 2007/05/24 00:51:37 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.attachment.AeFileAttachmentStorage;
import org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage;
import org.activebpel.rt.util.AeLongMap;
import org.activebpel.rt.util.AeLongSet;

/**
 * This class implements a file-based attachment manager.
 */
public class AeFileAttachmentManager extends AeAbstractAttachmentManager implements IAeProcessPurgedListener
{
   /** The file-based storage object. */
   private AeFileAttachmentStorage mFileStorage;

   /** The set of process ids that are waiting to be purged. */
   private AeLongSet mDeferredPurges;

   /** Maps process ids to the number of pending responses for each process. */
   private AeCounterMap mPendingResponses;

   /**
    * Constructs a new file-based attachment manager.
    * 
    * @param aConfig The configuration map for this manager.
    */
   public AeFileAttachmentManager(Map aConfig)
   {
      super(aConfig);
   }

   /**
    * Returns the file-based storage object.
    */
   protected AeFileAttachmentStorage getFileStorage()
   {
      if (mFileStorage == null)
      {
         mFileStorage = new AeFileAttachmentStorage();
      }

      return mFileStorage;
   }

   /**
    * Returns the set of process ids that are waiting to be purged.
    */
   protected AeLongSet getDeferredPurges()
   {
      if (mDeferredPurges == null)
      {
         mDeferredPurges = new AeLongSet(Collections.synchronizedSet(new HashSet()));
      }

      return mDeferredPurges;
   }

   /**
    * Returns the map of process ids to the number of responses pending for each
    * process.
    */
   protected AeCounterMap getPendingResponses()
   {
      if (mPendingResponses == null)
      {
         mPendingResponses = new AeCounterMap();
      }

      return mPendingResponses;
   }

   /**
    * Overrides method to return file-based storage implementation.
    * 
    * @see org.activebpel.rt.bpel.impl.AeAbstractAttachmentManager#getStorage()
    */
   protected IAeAttachmentStorage getStorage() throws AeBusinessProcessException
   {
      return getFileStorage();
   }

   /**
    * Overrides method to add this attachment manager as a process purged
    * listener.
    * 
    * @see org.activebpel.rt.bpel.impl.AeAbstractAttachmentManager#prepareToStart()
    */
   public void prepareToStart() throws Exception
   {
      super.prepareToStart();

      getEngine().getProcessManager().addProcessPurgedListener(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessPurgedListener#processPurged(long)
    */
   public void processPurged(long aProcessId)
   {
      // If there are responses pending for this process, then defer process
      // purging.
      if (getPendingResponses().getCount(aProcessId) > 0)
      {
         getDeferredPurges().add(aProcessId);
      }
      // Otherwise, purge the process now.
      else
      {
         removeProcess(aProcessId);
      }
   }

   /**
    * Removes the given process and its attachments from file storage.
    *
    * @param aProcessId
    */
   protected void removeProcess(long aProcessId)
   {
      getFileStorage().removeProcess(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAttachmentManager#responsePending(long)
    */
   public void responsePending(long aProcessId)
   {
      if (aProcessId == IAeBusinessProcess.NULL_PROCESS_ID)
      {
         AeException.logError(new IllegalStateException(AeMessages.getString("AeFileAttachmentManager.ERROR_InvalidProcessId"))); //$NON-NLS-1$
      }
      else
      {
         // This method gets called to prevent the attachment manager from
         // purging a process before the engine can deserialize the process's
         // attachments.
         getPendingResponses().increment(aProcessId);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAttachmentManager#responseFilled(long)
    */
   public void responseFilled(long aProcessId)
   {
      // If there are no more pending responses for the given process, and the
      // process purge was defered, then remove the process from storage now.
      int count = getPendingResponses().decrement(aProcessId);
      if ((count == 0) && getDeferredPurges().remove(aProcessId))
      {
         removeProcess(aProcessId);
      }
   }

   /**
    * Implements a map of from <code>long</code> keys to positive
    * <code>int</code> counter values.
    */
   protected static class AeCounterMap
   {
      /** The underlying map from <code>long</code> keys to counter objects. */
      private AeLongMap mLongMap = new AeLongMap();

      /**
       * Decrements the count associated with the given key and returns the new
       * count value. If the new count value is not positive, then removes the
       * counter from the underlying map.
       *
       * @param aKey
       * @return decremented count value
       */
      public synchronized int decrement(long aKey)
      {
         AeIntCounter counter = (AeIntCounter) getLongMap().get(aKey);
         int count = (counter == null) ? -1 : --counter.mCount;

         if (count <= 0)
         {
            getLongMap().remove(aKey);
         }

         return count;
      }

      /**
       * Returns the count currently associated with the given key.
       * 
       * @param aKey
       * @return current count value
       */
      public synchronized int getCount(long aKey)
      {
         AeIntCounter counter = (AeIntCounter) getLongMap().get(aKey);
         return (counter == null) ? 0 : counter.mCount;
      }

      /**
       * Returns the underlying map from <code>long</code> keys to counters.
       */
      protected AeLongMap getLongMap()
      {
         return mLongMap;
      }

      /**
       * Increments the count associated with the given key and returns the new
       * count value.
       *
       * @param aKey
       * @return incremented count value
       */
      public synchronized int increment(long aKey)
      {
         AeIntCounter counter = (AeIntCounter) getLongMap().get(aKey);
         if (counter == null)
         {
            counter = new AeIntCounter();
            getLongMap().put(aKey, counter);
         }
         return ++counter.mCount;
      }

      /**
       * Implements a simple counter that holds an <code>int</code> count field.
       */
      protected static class AeIntCounter
      {
         /** The current count for this counter. */
         private int mCount;
      }
   }
}
