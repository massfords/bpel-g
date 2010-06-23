// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/IAeStoppableWorkManager.java,v 1.1 2006/01/12 21:52:12 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import commonj.work.WorkManager;

/**
 * Defines interface for work managers that can be stopped automatically when
 * the engine shuts down.
 */
public interface IAeStoppableWorkManager extends WorkManager
{
   /**
    * Signals the work manager to stop. If the dispatch thread is currently running
    * it'll be interrupted. 
    */
   public void stop();
}