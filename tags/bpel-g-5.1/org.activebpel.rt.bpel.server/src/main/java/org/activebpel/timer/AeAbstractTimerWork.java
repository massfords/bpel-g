//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/timer/AeAbstractTimerWork.java,v 1.2 2005/02/24 23:31:18 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.timer;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.work.AeAbstractWork;

import commonj.timers.Timer;
import commonj.timers.TimerListener;

/**
 * This abstract class is an adapter used to facilitate scheduling work using the timer facility.
 */
public abstract class AeAbstractTimerWork extends AeAbstractWork implements TimerListener 
{
   /**
    * Called when the timer we scheduled has expired so that we may perform the appropriate work. 
    * @see commonj.timers.TimerListener#timerExpired(commonj.timers.Timer)
    */
   public void timerExpired(Timer aTimer)
   {
     AeEngineFactory.getWorkManager().schedule(this);
   }
   
   /** 
    * The actual timer work to be performed.  
    * Note this work is executed via the engine's work manager.
    */
   abstract public void run();
}