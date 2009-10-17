// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/timer/IAeStoppableTimerManager.java,v 1.1 2006/04/18 20:55:06 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.timer;

import commonj.timers.TimerManager;

/**
 * Defines interface for timer managers that can be stopped automatically when
 * the engine shuts down.
 */
public interface IAeStoppableTimerManager extends TimerManager
{
    /**
     * Stops this timer manager.
     */ 
    public void stop();
}
