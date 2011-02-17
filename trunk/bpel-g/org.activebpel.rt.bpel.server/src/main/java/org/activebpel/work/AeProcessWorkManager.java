// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeProcessWorkManager.java,v 1.1 2006/01/07 00:05:08 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeConfigChangeListener;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

import commonj.work.Work;

/**
 * Limits the amount of work scheduled per process.
 */
public class AeProcessWorkManager implements IAeProcessWorkManager, IAeConfigChangeListener
{
   /** The maximum number of work requests to schedule per process. */
   private int mProcessWorkCount = AeDefaultEngineConfiguration.PROCESS_WORK_COUNT_DEFAULT;

   /** Maps process id to an instance of {@link AeProcessWorkQueue} for the process. */
   private Map<Long,AeProcessWorkQueue> mProcessWorkMap = new HashMap();

   /**
    * Returns the maximum number of work requests to schedule per-process.
    */
   public int getProcessWorkCount()
   {
      return mProcessWorkCount;
   }

   /**
    * Returns the map from process ids to instances of {@link AeProcessWorkQueue}.
    */
   protected Map<Long,AeProcessWorkQueue> getProcessWorkQueueMap()
   {
      return mProcessWorkMap;
   }

   /**
    * Initializes configuration.
    */
   public void init()
   {
      IAeUpdatableEngineConfig config = AeEngineFactory.getEngineConfig().getUpdatableEngineConfig();
      config.addConfigChangeListener(this);
      updateConfig(config);
   }
   
   /**
    * @see org.activebpel.work.IAeProcessWorkManager#schedule(long, commonj.work.Work)
    */
   public void schedule(long aProcessId, Work aWork) throws AeBusinessProcessException
   {
      AeProcessWorkQueue queue;

      synchronized (getProcessWorkQueueMap())
      {
         queue = getProcessWorkQueueMap().get(aProcessId);
         if (queue == null)
         {
            queue = new AeProcessWorkQueue(aProcessId);
            getProcessWorkQueueMap().put(aProcessId, queue);
         }
      }

      synchronized (queue)
      {
         queue.addWaiting(new AeProcessWork(aProcessId, aWork));
         scheduleWaiting(queue);
      }
   }

   /**
    * Schedules waiting work in the given process work queue.
    *
    * @param aQueue
    */
   protected void scheduleWaiting(AeProcessWorkQueue aQueue) throws AeBusinessProcessException
   {
      synchronized (aQueue)
      {
         // Iterate through the waiting work until the process has enough work
         // scheduled.
         for (Iterator i = aQueue.waitingIterator(); i.hasNext() && (aQueue.getScheduledCount() < getProcessWorkCount()); )
         {
            Work work = (Work) i.next();
            AeEngineFactory.schedule(work);

            // And we're off! Remove the work from its queue and increment the
            // scheduled count.
            i.remove();
            aQueue.incrementScheduledCount();
         }
      }
   }

   /**
    * Convenience method that calls {@link #scheduleWaiting(AeProcessWorkQueue)}
    * and handles any {@link AeBusinessProcessException} exceptions.
    *
    * @param aQueue
    */
   protected void scheduleWaitingNoException(AeProcessWorkQueue aQueue)
   {
      try
      {
         scheduleWaiting(aQueue);
      }
      catch (AeBusinessProcessException e)
      {
         AeException.logError(e, AeMessages.format("AeProcessWorkManager.ERROR_ScheduleWaiting", aQueue.getProcessId())); //$NON-NLS-1$
      }
   }

   /**
    * Sets the maximum number of work requests to schedule per-process.
    *
    * @param aProcessWorkCount
    */
   public void setProcessWorkCount(int aProcessWorkCount)
   {
      if (aProcessWorkCount > 0)
      {
         mProcessWorkCount = aProcessWorkCount;
      }
      else
      {
         // Specifying 0 or less means no limit.
         mProcessWorkCount = Integer.MAX_VALUE;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeConfigChangeListener#updateConfig(org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig)
    */
   public void updateConfig(IAeUpdatableEngineConfig aConfig)
   {
      // Save the old effective process work count.
      int oldCount = getProcessWorkCount();

      // Set the new process work count.
      setProcessWorkCount(aConfig.getProcessWorkCount());

      // If the effective process work count increased, then schedule more
      // waiting work.
      if (getProcessWorkCount() > oldCount)
      {
         synchronized (getProcessWorkQueueMap())
         {
            for (Iterator i = getProcessWorkQueueMap().values().iterator(); i.hasNext(); )
            {
               AeProcessWorkQueue queue = (AeProcessWorkQueue) i.next();
               scheduleWaitingNoException(queue);
            }
         }
      }
   }

   /**
    * Handles the completion of work for the given process.
    *
    * @param aProcessId
    */
   protected void workCompleted(long aProcessId)
   {
      synchronized (getProcessWorkQueueMap())
      {
         AeProcessWorkQueue queue = (AeProcessWorkQueue) getProcessWorkQueueMap().get(aProcessId);
         if (queue == null)
         {
            throw new IllegalStateException(AeMessages.format("AeProcessWorkManager.ERROR_MissingProcessWorkQueue", aProcessId)); //$NON-NLS-1$
         }
   
         synchronized (queue)
         {
            // We have to hold the locks for both the process work queue map
            // and the process queue here, so that we don't remove the queue
            // just as another thread schedules more work for the same process.
            queue.decrementScheduledCount();
   
            if (queue.isEmpty())
            {
               getProcessWorkQueueMap().remove(aProcessId);
            }
            else
            {
               scheduleWaitingNoException(queue);
            }
         }
      }
   }

   /**
    * Extends {@link AeDelegatingWork} to notify the process work manager when
    * the work has finished running.
    */
   public class AeProcessWork extends AeDelegatingWork implements Work
   {
      /** The process id. */
      private final long mProcessId;

      /**
       * Constructor.
       *
       * @param aProcessId
       * @param aDelegateWork
       */
      public AeProcessWork(long aProcessId, Work aDelegateWork)
      {
         super(aDelegateWork);

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
            super.run();
         }
         finally
         {
            // Notify the process work manager.
            workCompleted(getProcessId());
         }
      }
   }
}
