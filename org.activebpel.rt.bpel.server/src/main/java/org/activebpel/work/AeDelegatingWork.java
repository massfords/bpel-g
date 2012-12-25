// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeDelegatingWork.java,v 1.2 2007/06/20 19:40:27 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import commonj.work.Work;

/**
 * Implements a <code>Work</code> object that delegates to another
 * <code>Work</code> object.
 */
public class AeDelegatingWork implements Work
{
   /** The delegate <code>Work</code> object. */
   private final Work mDelegateWork;

   /**
    * Constructor.
    *
    * @param aDelegateWork
    */
   public AeDelegatingWork(Work aDelegateWork)
   {
      mDelegateWork = aDelegateWork;
   }

   /**
    * Returns the delegate <code>Work</code> object.
    */
   public Work getDelegateWork()
   {
      return mDelegateWork;
   }

   /**
    * @see commonj.work.Work#isDaemon()
    */
   public boolean isDaemon()
   {
      return getDelegateWork().isDaemon();
   }

   /**
    * @see commonj.work.Work#release()
    */
   public void release()
   {
      getDelegateWork().release();
   }

   /**
    * @see java.lang.Runnable#run()
    */
   public void run()
   {
      getDelegateWork().run();
   }
}