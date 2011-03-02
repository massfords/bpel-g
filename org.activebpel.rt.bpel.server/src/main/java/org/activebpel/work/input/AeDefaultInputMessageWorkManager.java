// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/input/AeDefaultInputMessageWorkManager.java,v 1.1 2007/07/31 20:53:47 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work.input;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Implements an {@link IAeInputMessageWorkManager} that delegates to
 * {@link AeEngineFactory#schedule(long, commonj.work.Work)}.
 */
public class AeDefaultInputMessageWorkManager implements IAeInputMessageWorkManager
{
   /**
    * Overrides method to delegate to
    * {@link AeEngineFactory#schedule(long, commonj.work.Work)} thus treating
    * the given input message work just like any other work scheduled for the
    * given process.
    *
    * @see org.activebpel.work.input.IAeInputMessageWorkManager#schedule(long, org.activebpel.work.input.IAeInputMessageWork)
    */
   public void schedule(long aProcessId, IAeInputMessageWork aInputMessageWork) throws AeBusinessProcessException
   {
      AeEngineFactory.schedule(aProcessId, aInputMessageWork);
   }

   /**
    * Overrides method to ignore stop request.
    *  
    * @see org.activebpel.work.input.IAeInputMessageWorkManager#stop()
    */
   public void stop()
   {
      // Nothing to do.
   }
}