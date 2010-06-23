// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeBlockingQueue.java,v 1.4 2004/08/03 21:09:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of a blocking queue.  
 */
public class AeBlockingQueue
{
   /** list of objects for our queue */
   private List mQueue = new LinkedList();
   
   /**
    * Adds a new object to the queue, calling notify() on this object.
    * @param aObject
    */
   public synchronized void add(Object aObject)
   {
      mQueue.add(aObject);
      notify();
   }
   
   /**
    * Waits for the queue to not be empty. 
    */
   public synchronized void waitForObject()
   {
      while(mQueue.isEmpty())
      {
         try
         {
            wait();
         }
         catch (InterruptedException e)
         {
            // interrupted means we are shutting down
            break;
         }
      }
   }
   
   /**
    * Gets the first object from the queue
    */
   public synchronized Object getNextObjectOrWait()
   {
      waitForObject();
      if (!mQueue.isEmpty())
      {
         return mQueue.remove(0);
      }
      return null;
   }
}
