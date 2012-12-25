// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/input/IAeInputMessageWorkManager.java,v 1.1 2007/07/31 20:53:47 KRoe Exp $
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

/**
 * Defines the interface for a work manager that schedules instances of
 * {@link IAeInputMessageWork}.
 */
public interface IAeInputMessageWorkManager
{
   /**
    * Schedules input message work for the given process id.
    *
    * @param aProcessId
    * @param aInputMessageWork
    */
   public void schedule(long aProcessId, IAeInputMessageWork aInputMessageWork) throws AeBusinessProcessException;

   /**
    * Signals the input message work manager to stop. 
    */
   public void stop();
}
