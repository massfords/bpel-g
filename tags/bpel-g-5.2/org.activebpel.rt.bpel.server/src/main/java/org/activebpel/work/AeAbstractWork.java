// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeAbstractWork.java,v 1.2 2004/07/08 13:10:04 ewittmann Exp $
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

/**
 * Base class to help create a Work instance.
 */
abstract public class AeAbstractWork implements Work
{
   /** true if this work */
   boolean mDaemon = false;

   /** no-arg constructor */   
   public AeAbstractWork()
   {
   }
   
   /** accepts the daemon flag */
   public AeAbstractWork(boolean aDaemon)
   {
      mDaemon = aDaemon;
   }

   /**
    * @see commonj.work.Work#release()
    */
   public void release()
   {
   }

   /**
    * A hint to the work manager as to whether the thread for this Work should
    * come from a pool or not.
    * @see commonj.work.Work#isDaemon()
    */
   public boolean isDaemon()
   {
      return mDaemon;
   }
}
