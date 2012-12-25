// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeWorkerThread.java,v 1.6 2007/06/20 19:40:50 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkListener;

/**
 * A pooled worker thread that executes a Work object.
 */
public class AeWorkerThread extends Thread
{
   /** reference back to the work manager that owns this thread */
   private AeWorkManager mOwner;
   
   /** work that's being executed */
   private Work mWork;
   
   /** listener for receiving information on the work being done */
   private WorkListener mListener;
   
   /** work item that we need to update as the state of the work changes */
   private AeWorkItem mWorkItem;

   /** keeps the thread alive in its run() method. Gets set to false in die() */   
   private boolean mKeepGoing = true;
   
   /**
    * Creates a worker thread
    */
   public AeWorkerThread(String aName)
   {
      super(aName);
      start();
   }
   
   /**
    * clears the work specific state from this thread. 
    */
   public void clear()
   {
      setWork(null);
      setListener(null);
      setOwner(null);
      setWorkItem(null);
   }
   
   /**
    * Getter for the work.
    */
   public Work getWork()
   {
      return mWork;
   }
   
   /**
    * Sets all of the work related data in place and fires the accepted event.
    * @param aWork
    * @param aWorkItem
    * @param aListener
    */
   public synchronized void schedule(Work aWork, AeWorkItem aWorkItem, WorkListener aListener)
   {
      setWork(aWork);
      setListener(aListener);
      setWorkItem(aWorkItem);
      notify();
   }

   /**
    * fires WorkEvent to the WorkListener that we're about to start the work 
    */
   private void fireStarted()
   {
      getWorkItem().setStatus(WorkEvent.WORK_STARTED);
      if (getListener() != null)
         getListener().workStarted(new AeWorkEvent(getWorkItem(), WorkEvent.WORK_STARTED, null));
   }

   /**
    * fires WorkEvent to the WorkListener that the work is complete. 
    * @param aWorkException
    */
   private void fireComplete(WorkException aWorkException)
   {
      getWorkItem().setStatus(WorkEvent.WORK_COMPLETED);
      getWorkItem().setException(aWorkException);

      if (getListener() != null)
         getListener().workCompleted(new AeWorkEvent(getWorkItem(), WorkEvent.WORK_COMPLETED, aWorkException));
   }

   /**
    * Setter for the work.
    * @param aWork
    */
   public void setWork(Work aWork)
   {
      mWork = aWork;
   }
   
   /**
    * waits for work to become available. 
    */
   private synchronized void waitForWork()
   {
      if(getWork() == null)
      {
         try
         {
            wait();
         }
         catch (InterruptedException e)
         {
         }
      }
   }

   /**
    * @see java.lang.Runnable#run()
    */
   public void run()
   {
      while(mKeepGoing)
      {
         waitForWork();
         
         if (getWork() != null)
         {
            fireStarted();
            try
            {
               getWork().run();
               fireComplete(null);
            }
            catch (Throwable t)
            {
               fireComplete(new WorkException(t));
            }
            finally
            {
               getOwner().done(this);
            }
         }
      }
   }
   
   /**
    * Getter for the owning work manager.
    */
   private AeWorkManager getOwner()
   {
      return mOwner;
   }

   /**
    * Setter for the work manager that owns this thread
    * @param aManager
    */
   public void setOwner(AeWorkManager aManager)
   {
      mOwner = aManager;
   }

   /**
    * Getter for the listener
    */
   public WorkListener getListener()
   {
      return mListener;
   }

   /**
    * Setter for the listener
    * @param aListener
    */
   public void setListener(WorkListener aListener)
   {
      mListener = aListener;
   }
   
   /**
    * Getter for the work item
    */
   public AeWorkItem getWorkItem()
   {
      return mWorkItem;
   }

   /**
    * Setter for the work item
    * @param aItem
    */
   public void setWorkItem(AeWorkItem aItem)
   {
      mWorkItem = aItem;
   }

   /**
    * Sets the flag for the thread to die. 
    */
   public synchronized void die()
   {
      mKeepGoing = false;
      notify();
   }
}