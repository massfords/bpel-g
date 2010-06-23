// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeThreadPool.java,v 1.5 2005/02/01 19:56:36 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Simple thread pool
 */
public class AeThreadPool
{
   /** minimum number of free threads to keep in pool */
   private int mMinPoolSize = 1;
   /** maximum number of free and working threads */
   private int mMaxPoolSize = 10;
   /** pooled threads that are available for work */
   private Collection mFreeThreads = new LinkedList();
   /** threads that are currently busy working */
   private Set mWorkingThreads = new HashSet();
   /** increments with each thread we create, used for the thread name */
   private long mThreadCounter = 1;
   
   /**
    * Kill all threads in pool as work manager is shutting down.
    */
   public synchronized void killAllThreads()
   {
      for(Iterator iter = mFreeThreads.iterator(); iter.hasNext(); )
         ((AeWorkerThread)iter.next()).die();
      for(Iterator iter = mWorkingThreads.iterator(); iter.hasNext(); )
         ((AeWorkerThread)iter.next()).die();
      mWorkingThreads.clear();
   }
   
   /**
    * Returns the thread to the pool
    * @param aThread
    */
   public synchronized void returnThread(AeWorkerThread aThread)
   {
      mWorkingThreads.remove(aThread);

      if (getTotalPoolSize() < getMaxPoolSize() && mFreeThreads.size() < mMinPoolSize)
      {
         mFreeThreads.add(aThread);
         notify();
      }
      else
      {
         // we don't need this thread anymore
         aThread.die();
      }
   }
   
   /**
    * borrows a thread from the pool or returns null if the thread count is too high 
    */
   public synchronized AeWorkerThread borrowThread(AeWorkManager aManager)
   {
      AeWorkerThread free = null;
      if (!mFreeThreads.isEmpty())
      {
         free = (AeWorkerThread) mFreeThreads.iterator().next();
         mFreeThreads.remove(free);
      }
      
      if (free == null && canCreate())
      {
         free = createThread();
      }
      
      if (free != null)
      {
         free.setOwner(aManager);
         mWorkingThreads.add(free);
      }
      return free;
   }

   /**
    * Convenience method that creates a new worker thread.
    */
   private AeWorkerThread createThread()
   {
      return new AeWorkerThread("AeWorkManager-WorkerThread-"+mThreadCounter++); //$NON-NLS-1$
   }
   
   /**
    * Waits for a thread to become available from the pool.
    * @param aManager
    */
   public synchronized AeWorkerThread waitForThread(AeWorkManager aManager)
   {
      AeWorkerThread thread = null;
      while( (thread = borrowThread(aManager)) == null)
      {
         try
         {
            wait();
         }
         catch (InterruptedException e)
         {
         }
      }
      return thread;
   }
   
   /**
    * Returns true if the pool can create another thread.
    */
   private boolean canCreate()
   {
      if (getMaxPoolSize() == -1)
      {
         return true;
      }
      
      return getTotalPoolSize() < getMaxPoolSize();  
   }

   /**
    * Gets the total size of the thread pool
    */
   private int getTotalPoolSize()
   {
      return (mFreeThreads.size() + mWorkingThreads.size());
   }
   
   /**
    * Gets the max pool size
    */
   public int getMaxPoolSize()
   {
      return mMaxPoolSize;
   }

   /**
    * Gets the min pool size
    */
   public int getMinPoolSize()
   {
      return mMinPoolSize;
   }
   
   /**
    * Sets the pool size and ensures that there are the minimum number of threads
    * available based on the new min value.
    * @param aMin
    * @param aMax
    */
   public synchronized void setPoolSize(int aMin, int aMax)
   {
      mMinPoolSize = aMin;
      mMaxPoolSize = aMax;
      
      if(getMinPoolSize() > 0)
      {
         while (getTotalPoolSize() < getMinPoolSize())
         {
            mFreeThreads.add(createThread());
         }
      }
   }
}
