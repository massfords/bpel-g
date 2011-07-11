// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeQueuedWork.java,v 1.2 2004/07/08 13:10:04 ewittmann Exp $
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
import commonj.work.WorkListener;


/**
 * Holds work that is waiting for a thread to execute it.
 */   
public class AeQueuedWork
{
   /** Work that will be executed */
   private Work mWork;
   /** Optional listener to receive callbacks regarding work progress */
   private WorkListener mListener;
   /** WorkItem that reports the status of the work */
   private AeWorkItem mItem;
   
   /**
    * Creates an entry for work to be queued.
    * @param aWork
    * @param aWorkItem
    * @param aWorkListener
    */
   public AeQueuedWork(Work aWork, AeWorkItem aWorkItem, WorkListener aWorkListener)
   {
      setWork(aWork);
      setItem(aWorkItem);
      setListener(aWorkListener);
   }

   /**
    * Setter for the work
    * @param aWork
    */
   protected void setWork(Work aWork)
   {
      mWork = aWork;
   }

   /**
    * Getter for the work
    */
   protected Work getWork()
   {
      return mWork;
   }

   /**
    * Setter for the listener
    * @param aListener
    */
   protected void setListener(WorkListener aListener)
   {
      mListener = aListener;
   }

   /**
    * Getter for the listener
    */
   protected WorkListener getListener()
   {
      return mListener;
   }

   /**
    * Setter for the item
    * @param aItem
    */
   protected void setItem(AeWorkItem aItem)
   {
      mItem = aItem;
   }

   /**
    * Getter for the item
    */
   protected AeWorkItem getItem()
   {
      return mItem;
   }
}
