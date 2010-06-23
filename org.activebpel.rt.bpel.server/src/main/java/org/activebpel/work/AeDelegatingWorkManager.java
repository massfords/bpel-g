// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeDelegatingWorkManager.java,v 1.1 2006/01/07 00:05:34 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import java.util.Collection;

import commonj.work.Work;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;

/**
 * Implements a <code>WorkManager</code> that delegates to another
 * <code>WorkManager</code>.
 */
public class AeDelegatingWorkManager implements WorkManager
{
   /** The delegate <code>WorkManager</code>. */
   private final WorkManager mDelegateWorkManager;

   /**
    * Constructor.
    *
    * @param aDelegateWorkManager
    */
   public AeDelegatingWorkManager(WorkManager aDelegateWorkManager)
   {
      mDelegateWorkManager = aDelegateWorkManager;
   }

   /**
    * Returns the delegate <code>WorkManager</code>.
    */
   protected WorkManager getDelegateWorkManager()
   {
      return mDelegateWorkManager;
   }

   /**
    * @see commonj.work.WorkManager#schedule(commonj.work.Work)
    */
   public WorkItem schedule(Work aWork) throws IllegalArgumentException
   {
      return getDelegateWorkManager().schedule(aWork);
   }

   /**
    * @see commonj.work.WorkManager#schedule(commonj.work.Work, commonj.work.WorkListener)
    */
   public WorkItem schedule(Work aWork, WorkListener aWorkListener) throws IllegalArgumentException
   {
      return getDelegateWorkManager().schedule(aWork, aWorkListener);
   }

   /**
    * @see commonj.work.WorkManager#waitForAll(java.util.Collection, long)
    */
   public boolean waitForAll(Collection aWorkItems, long aTimeoutMillis) throws InterruptedException, IllegalArgumentException
   {
      return getDelegateWorkManager().waitForAll(aWorkItems, aTimeoutMillis);
   }

   /**
    * @see commonj.work.WorkManager#waitForAny(java.util.Collection, long)
    */
   public Collection waitForAny(Collection aWorkItems, long aTimeoutMillis) throws InterruptedException, IllegalArgumentException
   {
      return getDelegateWorkManager().waitForAny(aWorkItems, aTimeoutMillis);
   }
}