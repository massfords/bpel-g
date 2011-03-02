// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeRemoteWorkItem.java,v 1.3 2005/02/10 23:00:43 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import commonj.work.RemoteWorkItem;
import commonj.work.Work;
import commonj.work.WorkManager;

/**
 * Remote work items are returned to the caller when the work that was scheduled
 * implements Serializable. The current work manager implementation doesn't support
 * scheduling work in other jvms but it's still required to return a remote
 * work item when something Serializable is scheduled.  
 */
public class AeRemoteWorkItem extends AeWorkItem implements RemoteWorkItem
{
   /** work manager that executed the work */
   private WorkManager mWorkManager;
   
   /**
    * Constructor accepts the work manager and the work to be done.
    * @param aWorkManager
    * @param aWork
    */
   public AeRemoteWorkItem(WorkManager aWorkManager, Work aWork)
   {
      super(aWork);
      mWorkManager = aWorkManager;
   }

   /**
    * @see commonj.work.RemoteWorkItem#release()
    */
   public void release()
   {
      getWork().release();
   }

   /**
    * @see commonj.work.RemoteWorkItem#getPinnedWorkManager()
    */
   public WorkManager getPinnedWorkManager()
   {
      return mWorkManager;
   }
}