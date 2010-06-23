// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/timersvc/AeTimerService.java,v 1.18 2005/09/02 17:49:00 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.timersvc;

import commonj.timers.Timer;
import commonj.timers.TimerListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;

/**
 * A Simple Timer service.  This class uses a standard Java <code>java.util.Timer</code>
 * object to manage timer events.
 */
public class AeTimerService
{
   /** This is the singleton instance of the timer service. */
   private static AeTimerService instance = null;

   /** Get the singleton instance of the timer service. */
   public static AeTimerService getInstance()
   {
      if (instance == null)
         instance = new AeTimerService();

      return instance;
   }

   /** Holds the next task id. */
   private long mNextId = 1;

   /** The java timer used by this manager. */
   private java.util.Timer mTimer;

   /** The map of currently running alarm manager tasks, keyed by request ID. */
   private HashMap mTimerTasks = new HashMap();

   /**
    * Constructs a new timer service.
    */
   private AeTimerService()
   {
   }

   /**
    * Schedule and record a timing request. Deadline version.
    * 
    * @param aTimerListener The timer listener callback.
    * @param aDeadline The instant at which the client wants the event to fire.
    * 
    * @return The Timer object created by the scheduling request.
    */
   public Timer schedule(TimerListener aTimerListener, Date aDeadline)
   {
      AeTimerTask task = new AeTimerTask(aTimerListener);
      synchronized (getTimerTasks())
      {
         getTimerTasks().put(task.getId(), task);
      }
      mTimer.schedule(task, aDeadline);
      
      return task;
   }

   /**
    * Schedule and record a timing request. Duration version. Granularity is 1 millisecond.
    * 
    * @param aTimerListener The timer listener callback.
    * @param aDelay The number of milliseconds to wait.
    * @return The Timer object created by the scheduling request.
    */
   public Timer schedule(TimerListener aTimerListener, long aDelay)
   {
      AeTimerTask task = new AeTimerTask(aTimerListener);
      synchronized (getTimerTasks())
      {
         getTimerTasks().put(task.getId(), task);
      }
      mTimer.schedule(task, aDelay);
      
      return task;
   }

   /**
    * Schedule and record a repeating timing request.
    * 
    * @param aTimerListener The timer listener callback.
    * @param aDelay The time to wait before executing task in milliseconds. 
    * @param aPeriod The interval at which this task recurs in milliseconds.
    * @return The Timer object created by the scheduling request.
    */
   public Timer schedule(TimerListener aTimerListener, long aDelay, long aPeriod)
   {
      AeTimerTask task = new AeTimerTask(aTimerListener, aPeriod);
      synchronized (getTimerTasks())
      {
         getTimerTasks().put(task.getId(), task);
      }
      mTimer.schedule(task, aDelay, aPeriod);
      
      return task;
   }

   /**
    * Schedule and record a repeating timing request.
    * 
    * @param aTimerListener The timer listener callback.
    * @param aFirstTime First time at which task is to be executed.
    * @param aPeriod The interval at which this task recurs in miliseconds.
    * @return The Timer object created by the scheduling request.
    */
   public Timer schedule(TimerListener aTimerListener, Date aFirstTime, long aPeriod)
   {
      AeTimerTask task = new AeTimerTask(aTimerListener, aPeriod);
      synchronized (getTimerTasks())
      {
         getTimerTasks().put(task.getId(), task);
      }
      mTimer.schedule(task, aFirstTime, aPeriod);
      
      return task;
   }

   /**
    * Schedules the specified task for repeated fixed-rate execution, beginning after the specified delay. 
    * Subsequent executions take place at approximately regular intervals, separated by the specified period.
    * 
    * @param aTimerListener The timer listener callback.
    * @param aFirstTime First time at which task is to be executed.
    * @param aPeriod The interval at which this task recurs in miliseconds.
    * @return The Timer object created by the scheduling request.
    */
   public Timer scheduleAtFixedRate(TimerListener aTimerListener, Date aFirstTime, long aPeriod)
   {
      AeTimerTask task = new AeTimerTask(aTimerListener, aPeriod);
      synchronized (getTimerTasks())
      {
         getTimerTasks().put(task.getId(), task);
      }
      mTimer.schedule(task, aFirstTime, aPeriod);
      
      return task;
   }

   /**
    * Schedules the specified task for repeated fixed-rate execution, beginning at the specified time. 
    * Subsequent executions take place at approximately regular intervals, separated by the specified period.
    * 
    * @param aTimerListener The timer listener callback.
    * @param aDelay The time to wait before executing task in miliseconds 
    * @param aPeriod The interval at which this task recurs in miliseconds.
    * @return The Timer object created by the scheduling request.
    */
   public Timer scheduleAtFixedRate(TimerListener aTimerListener, long aDelay, long aPeriod)
   {
      AeTimerTask task = new AeTimerTask(aTimerListener, aPeriod);
      synchronized (getTimerTasks())
      {
         getTimerTasks().put(task.getId(), task);
      }
      mTimer.schedule(task, aDelay, aPeriod);
      
      return task;
   }
   
   /**
    * Cancel a scheduled request given the timer object created from the scheduling request.
    *
    * @param aTimer the timer object, returned previously by schedule();
    * @return True if request was cancelled, false otherwise
    * @see java.util.Timer#cancel()
    */
   public boolean cancel(Timer aTimer)
   {
      boolean cancelled = true;
      synchronized (getTimerTasks())
      {
         Long key = ((AeTimerTask)aTimer).getId();
         AeTimerTask task = (AeTimerTask)getTimerTasks().get(key);
         if (task == null)
            cancelled = false;
         else
         {
            task.cancel();
         }
      }
      
      return cancelled;
   }

   /**
    * Shut down the alarm manager.  This will cancel all currently running notification requests.
    */
   public void shutDown()
   {
      // Cancel all currently running timer tasks.
      synchronized (getTimerTasks())
      {
         // Iterate over a copy of the timer tasks collection, so that we don't
         // get a concurrent modification exception when AeTimerTask#cancel()
         // removes a task from the timer tasks map.
         Collection tasks = new ArrayList(getTimerTasks().values());

         for (Iterator iter = tasks.iterator(); iter.hasNext(); )
            ((AeTimerTask)iter.next()).cancel();
      }

      // Cancel myself.
      mTimer.cancel();
      mTimer = null;
   }

   /**
    * On startUp of the timer service, create a java timer.
    */
   public void startUp()
   {
      // Test to see if there is already a timer, if so then startup was already called.
      if (mTimer == null)
         mTimer = new java.util.Timer();
   }

   /**
    * Gets the next available id for a scheduled timer task.
    */
   protected synchronized long getNextId()
   {
      return mNextId++;
   }

   /**
    * Returns the collection of timer events which are currently scheduled. 
    */
   protected HashMap getTimerTasks()
   {
      return mTimerTasks;
   }

   /**
    * Inner class to provide specialized timer task functionality.
    */
   public class AeTimerTask extends TimerTask implements Timer
   {
      /** The timer service handler that will receive the timer event callback. */
      private TimerListener mTimerListener;
      /** The timer ID for this task. */
      private Long mTimerId;
      /** The period to use for computing a repeating interval if this is a repeating timer */
      private long mPeriod;

      /**
       * Constructor to create a timer task object given the listener.
       * @param aTimerListener The client to be notified when the task triggers.
       */
      public AeTimerTask(TimerListener aTimerListener)
      {
         mTimerListener = aTimerListener ;
         mTimerId = new Long(getNextId()) ;
      }

      /**
       * Constructor to create a timer task object given the listener and schedule info.
       * @param aTimerListener The client to be notified when the task triggers.
       * @param aPeriod The period to use for computing a repeating interval if this is a repeating timer
       */
      public AeTimerTask(TimerListener aTimerListener, long aPeriod)
      {
         this(aTimerListener);
         mPeriod = aPeriod;
      }

      /** 
       * Returns the internal id associated with this timer task
       */
      public Long getId()
      {
         return mTimerId;
      }

      /**
       * Notify the client that the timer event is triggered.
       * 
       * @see java.lang.Runnable#run()
       */      
      public void run()
      {
         try
         {
            mTimerListener.timerExpired(this);
         }
         finally
         {
            if (mPeriod == 0)
            {
               cancel();
            }
         }
      }

      /**
       * @see commonj.timers.Timer#getTimerListener()
       */
      public TimerListener getTimerListener()
      {
         return mTimerListener;
      }

      /**
       * @see commonj.timers.Timer#getScheduledExecutionTime()
       */
      public long getScheduledExecutionTime() throws IllegalStateException
      {
         return scheduledExecutionTime();
      }

      /**
       * @see commonj.timers.Timer#getPeriod()
       */
      public long getPeriod()
      {
         return mPeriod;
      }

      /**
       * Overrides method to remove this timer task from timer service map.
       *
       * @see java.util.TimerTask#cancel()
       */
      public boolean cancel()
      {
         synchronized (getTimerTasks())
         {
            getTimerTasks().remove(getId());
         }

         return super.cancel();
      }
   }
}