// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeExceptionReportingWork.java,v 1.4 2008/02/17 21:38:54 mford Exp $
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

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeMonitorListener;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Extends {@link org.activebpel.work.AeDelegatingWork} to report any
 * exceptions thrown when the <code>Work</code> runs.  Additionally
 * it interacts with the engine monitor system to report on the time it 
 * took from construction to running (i.e. long times indicate that
 * the work manager thread pool is not large enough).
 */
public class AeExceptionReportingWork extends AeDelegatingWork implements Work
{
   /** The time the work was constructed. */
   private long mStartTime = System.currentTimeMillis(); 

   /**
    * Constructor.
    *
    * @param aDelegateWork
    */
   public AeExceptionReportingWork(Work aDelegateWork)
   {
      super(aDelegateWork);
   }

   /**
    * @see org.activebpel.work.AeDelegatingWork#run()
    */
   public void run()
   {
      try
      {
         // If engine has not yet started, do not try and send monitor events 
         if (getEngine() != null)
         {
            // Notify monitor listeners that the work took current - start amount of time to execute
            long delay = System.currentTimeMillis() - mStartTime;
            getEngine().fireMonitorEvent(IAeMonitorListener.MONITOR_WM_START_TIME, delay);
         }
         
         // now run the original work
         super.run();
      }
      catch (Throwable t)
      {
         AeException.logError(t, AeMessages.getString("AeExceptionReportingWork.ERROR_UnhandledException")); //$NON-NLS-1$
      }
   }
   
   /** 
    * Convenince method for accessing the engine.
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return AeEngineFactory.getEngine();
   }
}