// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeExceptionReportingWorkManager.java,v 1.2 2006/01/12 21:52:12 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import org.activebpel.work.factory.IAeWorkManagerFactory;

import commonj.work.Work;
import commonj.work.WorkItem;
import commonj.work.WorkListener;

/**
 * Extends {@link org.activebpel.work.AeDelegatingWorkManager} to wrap scheduled
 * work in instances of {@link org.activebpel.work.AeExceptionReportingWork}.
 */
public class AeExceptionReportingWorkManager extends AeDelegatingWorkManager implements IAeStoppableWorkManager
{
   /**
    * Constructor.
    *
    * @param aDelegateWorkManager
    */
   public AeExceptionReportingWorkManager(IAeWorkManagerFactory aWorkManagerFactory)
   {
      super(aWorkManagerFactory.getWorkManager());
   }

   /**
    * @see commonj.work.WorkManager#schedule(commonj.work.Work)
    */
   public WorkItem schedule(Work aWork) throws IllegalArgumentException
   {
      return super.schedule(new AeExceptionReportingWork(aWork));
   }

   /**
    * @see commonj.work.WorkManager#schedule(commonj.work.Work, commonj.work.WorkListener)
    */
   public WorkItem schedule(Work aWork, WorkListener aWorkListener) throws IllegalArgumentException
   {
      return super.schedule(new AeExceptionReportingWork(aWork), aWorkListener);
   }

   /**
    * @see org.activebpel.work.IAeStoppableWorkManager#stop() 
    */
   public void stop()
   {
      if (getDelegateWorkManager() instanceof IAeStoppableWorkManager)
      {
         ((IAeStoppableWorkManager) getDelegateWorkManager()).stop();
      }
   }
}