// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/child/AeChildWorkItem.java,v 1.2 2007/06/20 19:40:06 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work.child;

import commonj.work.*;
import org.activebpel.rt.AeException;
import org.activebpel.work.AeDelegatingWork;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements a <code>WorkItem</code> for a {@link AeChildWorkManager}.
 * Implements the <code>RemoteWorkItem</code> interface instead of just the
 * <code>WorkItem</code> interface, because a <code>WorkManager</code> must
 * return a <code>RemoteWorkItem</code> when a <code>Serializable</code>
 * <code>Work</code> object is scheduled.
 */
public class AeChildWorkItem implements RemoteWorkItem
{
   /**
    * Next available item id.
    *
    * @see #mItemId
    */
   private static long sNextItemId = 0L;

   /** The <code>Work</code> to schedule. */
   private final Work mWork;

   /** Associated <code>WorkListener</code>; may be <code>null</code>. */
   private final WorkListener mWorkListener;

   /** The <code>WorkManager</code> executing the scheduled <code>Work</code>. */
   private final WorkManager mPinnedWorkManager;

   /** Item id for {@link #equals(Object)}, {@link #hashCode()}, and {@link #compareTo(Object)}. */
   private final Long mItemId;

   /**
    * Work completed listeners. Generally, the only listener will be the child
    * work manager that created this item.
    */
   private final Collection<IAeChildWorkCompletedListener> mWorkCompletedListeners = new ArrayList<>(1);

   /**
    * <code>true</code> if and only if the associated work is completed.
    */
   private boolean mIsWorkCompleted;

   /**
    * The scheduled <code>WorkItem</code> or <code>null</code> if the
    * <code>Work</code> has not yet been scheduled.
    */
   private WorkItem mScheduledWorkItem;

   /**
    * Constructor.
    *
    * @param aWork
    * @param aPinnedWorkManager
    */
   public AeChildWorkItem(Work aWork, WorkListener aWorkListener, WorkManager aPinnedWorkManager)
   {
      mWork = new AeChildWork(aWork);
      mWorkListener = aWorkListener;
      mPinnedWorkManager = aPinnedWorkManager;
      mItemId = getNextItemId();
   }

   /**
    * Adds a work completed listener.
    *
    * @param aListener
    */
   public void addChildWorkCompletedListener(IAeChildWorkCompletedListener aListener)
   {
      synchronized (mWorkCompletedListeners)
      {
         mWorkCompletedListeners.add(aListener);
      }
   }

   /**
    * @return this item's id
    */
   protected Long getItemId()
   {
      return mItemId;
   }

   /**
    * @return the next available item id
    *
    * @see #getItemId()
    */
   protected synchronized static Long getNextItemId()
   {
      return sNextItemId++;
   }

   /**
    * @return the scheduled <code>WorkItem</code> or <code>null</code> if the <code>Work</code> has not yet been scheduled
    */
   protected WorkItem getScheduledWorkItem()
   {
      return mScheduledWorkItem;
   }

   /**
    * @return the <code>Work</code> to schedule
    */
   protected Work getWork()
   {
      return mWork;
   }

   /**
    * @return associated <code>WorkListener</code>, which may be <code>null</code>
    */
   protected WorkListener getWorkListener()
   {
      return mWorkListener;
   }

   /**
    * @return <code>true</code> if and only if the associated work is completed
    */
   public boolean isWorkCompleted()
   {
      return mIsWorkCompleted;
   }

   /**
    * Notifies all work completed listeners that the work has completed.
    */
   protected void notifyChildWorkCompletedListeners()
   {
      synchronized (mWorkCompletedListeners)
      {
          for (IAeChildWorkCompletedListener l : mWorkCompletedListeners) {
              try {
                  l.workCompleted(this);
              } catch (Throwable t) {
                  AeException.logError(t);
              }
          }
      }
   }

   /**
    * Sets the actual <code>WorkItem</code> returned by a
    * <code>WorkManager</code> when this item's <code>Work</code> is scheduled.
    */
   protected void setScheduledWorkItem(WorkItem aScheduledWorkItem)
   {
      mScheduledWorkItem = aScheduledWorkItem;
   }

   /**
    * Called when the work is completed. 
    */
   protected void workCompleted()
   {
      mIsWorkCompleted = true;
      
      notifyChildWorkCompletedListeners();
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      return this == aOther;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getItemId().hashCode();
   }

   /**
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Object aOther)
   {
      Long otherItemId = ((AeChildWorkItem) aOther).getItemId();
      return getItemId().compareTo(otherItemId);
   }

   /**
    * @see commonj.work.WorkItem#getResult()
    */
   public Work getResult()
   {
      Work result = (getScheduledWorkItem() == null) ? null : getScheduledWorkItem().getResult();

      if (result instanceof AeChildWork)
      {
         result = ((AeChildWork) result).getDelegateWork();
      }

      return result;
   }

   /**
    * @see commonj.work.WorkItem#getStatus()
    */
   public int getStatus()
   {
      return (getScheduledWorkItem() == null) ? WorkEvent.WORK_ACCEPTED : getScheduledWorkItem().getStatus();
   }

   /**
    * @see commonj.work.RemoteWorkItem#getPinnedWorkManager()
    */
   public WorkManager getPinnedWorkManager()
   {
      return mPinnedWorkManager;
   }

   /**
    * @see commonj.work.RemoteWorkItem#release()
    */
   public void release()
   {
      getWork().release();
   }

   /**
    * Extends {@link AeDelegatingWork} to notify this child work item when the
    * <code>Work</code> has completed. We use this <code>Work</code> wrapper
    * instead of a <code>WorkListener</code> to guarantee notification even if
    * the parent work manager implementation is not absolutely reliable about
    * firing work completed events.
    */
   protected class AeChildWork extends AeDelegatingWork implements Work
   {
      /**
       * Constructor.
       *
       * @param aDelegateWork
       */
      public AeChildWork(Work aDelegateWork)
      {
         super(aDelegateWork);
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
            // Notify the work item that the work is completed.
            workCompleted();
         }
      }
   }
}
