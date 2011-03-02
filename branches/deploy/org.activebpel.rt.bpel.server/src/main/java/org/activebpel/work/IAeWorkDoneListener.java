// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/IAeWorkDoneListener.java,v 1.2 2004/07/08 13:10:04 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

/**
 * Listener interface to receive notification of when a WorkItem completes. This
 * is used to implement the WorkManager.waitForAll() and WorkManager.waitForAny()
 * calls.
 */
public interface IAeWorkDoneListener
{
   public void workDone();
}
