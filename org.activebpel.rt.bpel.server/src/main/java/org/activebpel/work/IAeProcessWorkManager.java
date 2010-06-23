// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/IAeProcessWorkManager.java,v 1.1 2006/01/07 00:05:08 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import org.activebpel.rt.bpel.AeBusinessProcessException;

import commonj.work.Work;

/**
 * Defines the interface for a work manager that limits the amount of work
 * scheduled per process.
 */
public interface IAeProcessWorkManager
{
   /**
    * Schedules the given work for the given process.
    *
    * @param aProcessId
    * @param aWork
    */
   public void schedule(long aProcessId, Work aWork) throws AeBusinessProcessException;
}
