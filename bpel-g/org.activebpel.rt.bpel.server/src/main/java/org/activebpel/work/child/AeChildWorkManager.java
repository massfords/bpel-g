// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/child/AeChildWorkManager.java,v 1.2 2007/06/20 19:40:06 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work.child;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.work.factory.IAeWorkManagerFactory;

import commonj.work.Work;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;

/**
 * Implements a work manager that limits the amount of work scheduled to a
 * parent work manager.
 */
public class AeChildWorkManager implements WorkManager, IAeChildWorkCompletedListener
{
   /** <code>WorkItem</code>s waiting to be scheduled to the parent work manager. */
   private final Collection<AeChildWorkItem> mWaitingQueue = new LinkedList<AeChildWorkItem>();

   /** The maximum number of work items to schedule from this work manager to its parent. */
   private int mMaxWorkCount = 5; 

   /** The number of work items currently scheduled in the parent work manager from this work manager. */
   private int mScheduledCount = 0;
   
   private IAeWorkManagerFactory mWorkManagerFactory;
   
   private String mName; 

   /**
    * Decrements the count of work items currently scheduled in the parent work
    * manager.
    */
   protected void decrementScheduledCount()
   {
      if (--mScheduledCount < 0)
      {
         // Reset it to a sane value.
         mScheduledCount = 0;

         throw new IllegalStateException(AeMessages.format("AeChildWorkManager.ERROR_NegativeScheduledCount", "child work manager")); //$NON-NLS-1$
      }
   }

   /**
    * @return the maximum number of work items to schedule from this work manager to its parent
    */
   public int getMaxWorkCount()
   {
      return mMaxWorkCount;
   }

   /**
    * @return the count of work items currently scheduled in the parent work manager from this work manager
    */
   protected int getScheduledCount()
   {
      return mScheduledCount;
   }

   /**
    * @return the <code>Collection</code> of work items waiting to be scheduled to the parent work manager
    */
   protected Collection<AeChildWorkItem> getWaitingQueue()
   {
      return mWaitingQueue;
   }

   /**
    * Increments the count of work items currently scheduled in the parent work
    * manager.
    */
   protected void incrementScheduledCount()
   {
      ++mScheduledCount;
   }
   
   /**
    * @see commonj.work.WorkManager#schedule(commonj.work.Work)
    */
   public WorkItem schedule(Work aWork)
   {
      return schedule(aWork, null);
   }

   /**
    * @see commonj.work.WorkManager#schedule(commonj.work.Work, commonj.work.WorkListener)
    */
   public synchronized WorkItem schedule(Work aWork, WorkListener aWorkListener)
   {
      AeChildWorkItem item = new AeChildWorkItem(aWork, aWorkListener, this);
      item.addChildWorkCompletedListener(this);

      getWaitingQueue().add(item);
      scheduleWaiting();

      return item;
   }

   /**
    * Schedules waiting work to the parent work manager.
    */
   protected synchronized void scheduleWaiting()
   {
      // Iterate through the waiting work items until we have scheduled the
      // maximum possible number of work items.
      for (Iterator<AeChildWorkItem> i = getWaitingQueue().iterator(); i.hasNext() && (getScheduledCount() < getMaxWorkCount()); )
      {
         AeChildWorkItem item = i.next();

         // Schedule the work with its listener to the parent work manager.
         WorkItem scheduledItem = mWorkManagerFactory.getWorkManager().schedule(item.getWork(), item.getWorkListener());

         // Save the actual WorkItem returned by the parent work manager.
         item.setScheduledWorkItem(scheduledItem);

         // And we're off! Remove the work item from the queue and increment the
         // scheduled count.
         i.remove();
         incrementScheduledCount();
      }
   }

   /**
    * Convenience method that calls {@link #scheduleWaiting()} and handles any
    * <code>WorkException</code>s.
    */
   protected void scheduleWaitingNoException()
   {
     scheduleWaiting();
   }

   /**
    * Sets the maximum number of work items to schedule from this child work
    * manager to its parent.
    *
    * @param aMaxWorkCount
    */
   public void setMaxWorkCount(int aMaxWorkCount)
   {
      if (aMaxWorkCount > 0)
      {
         mMaxWorkCount = aMaxWorkCount;
      }
      else
      {
         // Specifying 0 or less means no limit.
         mMaxWorkCount = Integer.MAX_VALUE;
      }

      // The work count may have increased, allowing us to schedule more work.
      scheduleWaitingNoException();
   }

   /**
    * @see commonj.work.WorkManager#waitForAll(java.util.Collection, long)
    */
   public boolean waitForAll(Collection aWorkItems, long aTimeoutMillis) throws InterruptedException, IllegalArgumentException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see commonj.work.WorkManager#waitForAny(java.util.Collection, long)
    */
   public Collection waitForAny(Collection aWorkItems, long aTimeoutMillis) throws InterruptedException, IllegalArgumentException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.work.child.IAeChildWorkCompletedListener#workCompleted(commonj.work.WorkItem)
    */
   public synchronized void workCompleted(WorkItem aWorkItem)
   {
      decrementScheduledCount();

      scheduleWaitingNoException();
   }
   
   public void setWorkManagerFactory(IAeWorkManagerFactory aFactory) {
	   mWorkManagerFactory = aFactory;
   }

public String getName() {
	return mName;
}

public void setName(String aName) {
	mName = aName;
}
}
