//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/timer/AeTimerManager.java,v 1.4 2006/05/01 19:50:50 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.timer;

import commonj.timers.Timer;
import commonj.timers.TimerListener;
import commonj.timers.TimerManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.timersvc.AeTimerService;

/**
 * Implementation of the TimerManager for Application Servers spec from CommonJ.
 * Provides a facility for scheduling timer operations.
 * 
 * Reference: http://dev2dev.bea.com/technologies/commonj/twm/index.jsp 
 */
public class AeTimerManager implements TimerManager, IAeStoppableTimerManager
{
   /** Timer Manager state indicating that we are running */
   protected static final short RUNNING = 0; 
   /** Timer Manager state indicating that we are suspending */
   protected static final short SUSPENDING = 1; 
   /** Timer Manager state indicating that we are suspended */
   protected static final short SUSPENDED = 2; 
   /** Timer Manager state indicating that we are stopping */
   protected static final short STOPPING = 3; 
   /** Timer Manager state indicating that we are stopped */
   protected static final short STOPPED = 4;
   
   /** Maximum time to wait (in milliseconds) to suspend or stop the Timer Manager is 1 minute */
   protected static final long MAXIMUM_WAIT = 60 * 1000;
   
   /** Current state of the timer manager, initially stopped */
   private short mTimerManagerState = STOPPED; 
   
   /** A list of timers which have been deferred. They will fire when timer manager is resumed. */
   private ArrayList mPendingTimers = new ArrayList();
   /** A count of timers which are currently executing. */
   private int mExecutingTimers;
   /** Synchronizes {@link #stop()} with {@link #waitForStop(long)}. */
   private final Object mWaitForStopMutex = new Object();
   
   /**
    * Constructor for the timer manager which is responsible for starting the 
    * timer manager service.
    */
   public AeTimerManager()
   {
      getTimerService().startUp();
      setState(RUNNING);
   }
   
   /**
    * @see commonj.timers.TimerManager#suspend()
    */
   public void suspend()
   {
      try
      {
         // The specification does not place an upper limit on amount of time to wait, but we will
         waitForSuspend(MAXIMUM_WAIT);
      }
      catch(InterruptedException ie)
      {
         // Thread has been interrupted, no longer need to wait
      }
   }

   /**
    * @see commonj.timers.TimerManager#isSuspending()
    */
   public boolean isSuspending() throws IllegalStateException
   {
      if (isStopped())
         throw new IllegalStateException(AeMessages.getString("AeTimerManager.ERROR_AlreadyStopped")); //$NON-NLS-1$
         
      return (getState() == SUSPENDED || getState() == SUSPENDING);
   }

   /**
    * @see commonj.timers.TimerManager#isSuspended()
    */
   public boolean isSuspended() throws IllegalStateException
   {
      return (getState() == SUSPENDED);
   }

   /**
    * @see commonj.timers.TimerManager#waitForSuspend(long)
    */
   public boolean waitForSuspend(long aTimeout_ms) throws InterruptedException, IllegalStateException, IllegalArgumentException
   {
      boolean suspendCompleted = false;
      
      // Indicate that we are attempting to suspend the Timer Manager
      setState(SUSPENDING);

      // It is illegal to stop the timer service when it is already stopped
      if (isStopped())
         throw new IllegalStateException(AeMessages.getString("AeTimerManager.ERROR_AlreadyStopped")); //$NON-NLS-1$
      if (aTimeout_ms < 0)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidTimeout")); //$NON-NLS-1$

      try
      {
         suspendCompleted = waitForExecutingTimers(aTimeout_ms);
      }
      finally
      {
         // We should be suspended no matter what happens
         setState(SUSPENDED);
      }
      
      return suspendCompleted;
   }

   /**
    * @see commonj.timers.TimerManager#resume()
    */
   public void resume() throws IllegalStateException
   {
      // Set the timer manager as running and release any pending timers
      setState(RUNNING);
      
      synchronized(getPendingTimers())
      {
         for (Iterator iter=getPendingTimers().iterator(); iter.hasNext();)
         {
            Timer timer = (Timer)iter.next();
            timer.getTimerListener().timerExpired(timer);
         }
         
         // Remove all timers from list
         getPendingTimers().clear();
      }
   }

   /**
    * @see commonj.timers.TimerManager#stop()
    */
   public void stop() throws IllegalStateException
   {
      // It is illegal to stop the timer service when it is already stopped
      if (isStopping())
         throw new IllegalStateException(AeMessages.getString("AeTimerManager.ERROR_AlreadyStopped")); //$NON-NLS-1$

      // Indicate that we are attempting to stop the TimerManager
      setState(STOPPING);

      try
      {
         waitForExecutingTimers(MAXIMUM_WAIT);
      }
      catch (InterruptedException e)
      {
         AeException.logError(e);
      }
      finally
      {
         // We should be stopped no matter what happens
         setState(STOPPED);

         synchronized (mWaitForStopMutex)
         {
            mWaitForStopMutex.notifyAll();
         }

         getTimerService().shutDown();         
      }
   }

   /**
    * @see commonj.timers.TimerManager#isStopped()
    */
   public boolean isStopped()
   {
      return (getState() == STOPPED);
   }

   /** 
    * @see commonj.timers.TimerManager#isStopping()
    */
   public boolean isStopping()
   {
      return (getState() == STOPPED || getState() == STOPPING);
   }

   /**
    * @see commonj.timers.TimerManager#waitForStop(long)
    */
   public boolean waitForStop(long aTimeout_ms) throws InterruptedException, IllegalArgumentException
   {
      if (aTimeout_ms < 0)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidTimeout")); //$NON-NLS-1$

      synchronized (mWaitForStopMutex)
      {
         if (getState() != STOPPED)
         {
            mWaitForStopMutex.wait(aTimeout_ms);
         }
      }
      
      return getState() == STOPPED;
   }

   /**
    * Delegates through to the AeTimerService to schedule operation.
    * @see commonj.timers.TimerManager#schedule(commonj.timers.TimerListener, java.util.Date)
    */
   public Timer schedule(TimerListener aListener, Date aTime) throws IllegalArgumentException, IllegalStateException
   {
      if (aListener == null)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidTimerListener")); //$NON-NLS-1$
      
      if (aTime == null)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidDate")); //$NON-NLS-1$
         
      if (isStopping())
         throw new IllegalStateException(AeMessages.getString("AeTimerManager.ERROR_TimerStopped")); //$NON-NLS-1$
      
      return getTimerService().schedule(new AeTimerListener(aListener), aTime);
   }

   /**
    * Delegates through to the AeTimerService to schedule operation.
    * @see commonj.timers.TimerManager#schedule(commonj.timers.TimerListener, long)
    */
   public Timer schedule(TimerListener aListener, long aDelay) throws IllegalArgumentException, IllegalStateException
   {
      if (aListener == null)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidTimerListener")); //$NON-NLS-1$

      if (isStopping())
         throw new IllegalStateException(AeMessages.getString("AeTimerManager.ERROR_TimerStopped")); //$NON-NLS-1$
      
      return getTimerService().schedule(new AeTimerListener(aListener), aDelay);
   }

   /**
    * Delegates through to the AeTimerService to schedule operation.
    * @see commonj.timers.TimerManager#schedule(commonj.timers.TimerListener, java.util.Date, long)
    */
   public Timer schedule(TimerListener aListener, Date aFirstTime, long aPeriod) throws IllegalArgumentException, IllegalStateException
   {
      if (aListener == null)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidTimerListener")); //$NON-NLS-1$

      if (aFirstTime == null)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidDate")); //$NON-NLS-1$
         
      if (isStopping())
         throw new IllegalStateException(AeMessages.getString("AeTimerManager.ERROR_TimerStopped")); //$NON-NLS-1$
      
      return getTimerService().schedule(new AeTimerListener(aListener), aFirstTime, aPeriod);
   }

   /**
    * Delegates through to the AeTimerService to schedule operation.
    * @see commonj.timers.TimerManager#schedule(commonj.timers.TimerListener, long, long)
    */
   public Timer schedule(TimerListener aListener, long aDelay, long aPeriod) throws IllegalArgumentException, IllegalStateException
   {
      if (aListener == null)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidTimerListener")); //$NON-NLS-1$

      if (isStopping())
         throw new IllegalStateException(AeMessages.getString("AeTimerManager.ERROR_TimerStopped")); //$NON-NLS-1$
      
      return getTimerService().schedule(new AeTimerListener(aListener), aDelay, aPeriod);
   }

   /**
    * Delegates through to the AeTimerService to schedule operation.
    * @see commonj.timers.TimerManager#scheduleAtFixedRate(commonj.timers.TimerListener, java.util.Date, long)
    */
   public Timer scheduleAtFixedRate(TimerListener aListener, Date aFirstTime, long aPeriod) throws IllegalArgumentException, IllegalStateException
   {
      if (aListener == null)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidTimerListener")); //$NON-NLS-1$

      if (aFirstTime == null)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidDate")); //$NON-NLS-1$
         
      if (isStopping())
         throw new IllegalStateException(AeMessages.getString("AeTimerManager.ERROR_TimerStopped")); //$NON-NLS-1$
      
      return getTimerService().scheduleAtFixedRate(new AeTimerListener(aListener), aFirstTime, aPeriod);
   }

   /**
    * Delegates through to the AeTimerService to schedule operation.
    * @see commonj.timers.TimerManager#scheduleAtFixedRate(commonj.timers.TimerListener, long, long)
    */
   public Timer scheduleAtFixedRate(TimerListener aListener, long aDelay, long aPeriod) throws IllegalArgumentException, IllegalStateException
   {
      if (aListener == null)
         throw new IllegalArgumentException(AeMessages.getString("AeTimerManager.ERROR_InvalidTimerListener")); //$NON-NLS-1$

      if (isStopping())
         throw new IllegalStateException(AeMessages.getString("AeTimerManager.ERROR_TimerStopped")); //$NON-NLS-1$
      
      return getTimerService().scheduleAtFixedRate(new AeTimerListener(aListener), aDelay, aPeriod);
   }
   
   /**
    * Used to defer execution of a timer when timer manager has been suspended.
    * The timer will execute when the timer manager is resumed.
    * @param aTimer The timer which is to be deferred
    */
   protected void deferTimerExecution(Timer aTimer)
   {
      synchronized(getPendingTimers())
      {
         getPendingTimers().add(aTimer);
      }      
   }
   
   /**
    * Marks the given timer as currently executing.
    * @param aTimer The timer which started execution
    */
   protected synchronized void timerExecutionStart(Timer aTimer)
   {
      ++mExecutingTimers;
   }

   /**
    * Marks the given timer as having completed executiion.
    * @param aTimer The timer to be removed.
    */
   protected synchronized void timerExecutionStop(Timer aTimer)
   {
      --mExecutingTimers;
   }
   
   /**
    * Returns the number of timers which are currently executing.
    */
   protected synchronized int getExecutingTimerCount()
   {
      return mExecutingTimers;
   }

   /**
    * Waits for the given amount of time for all currently executing timers to complete.
    * @param aTimeout_ms The maximum amount of time to wait for
    * @return True if all timers have completed, False otherwise
    * 
    * @throws InterruptedException
    */
   protected boolean waitForExecutingTimers(long aTimeout_ms) throws InterruptedException
   {
      long sleepInterval = (Math.min(aTimeout_ms /5, 250));
      long expiration = System.currentTimeMillis() + aTimeout_ms;

      // Poll waiting for in-flight timers to complete
      while (System.currentTimeMillis() < expiration)
      {
         if (getExecutingTimerCount() <= 0)
            return true;
         
         Thread.sleep(sleepInterval);
      }
      
      return false;
   }
   
   /**
    * Returns the list of timers waiting to execute.
    */
   protected ArrayList getPendingTimers()
   {
      return mPendingTimers;
   }
   
   /**
    * Returns the timer service which we delegate timer calls through.
    */
   protected AeTimerService getTimerService()
   {
      return AeTimerService.getInstance();
   }   
   
   /**
    * Returns the current state of the timer manager.
    */
   protected short getState()
   {
      return mTimerManagerState;
   }
   
   /**
    * Sets the current state of the timer manager.
    * @param aState The timerManagerState to set.
    */
   protected void setState(short aState)
   {
      mTimerManagerState = aState;
   }

   /**
    * Inner class used to place a wrapper around TimerListeners so that we may defer their
    * execution in the event that the timer manager is suspended. All deferred timers will 
    * execute when timer manager is resumed.
    */
   class AeTimerListener implements TimerListener
   {
      /** The timer listener we are wrappering */
      private TimerListener mListener;
      
      /**
       * Constructor which takes a timer listener as input.
       * @param aListener The listener we are creating a wrapper for
       */
      public AeTimerListener(TimerListener aListener)
      {
         mListener = aListener;
      }
      
      /**
       * Implements method so we may defer execution of timers when the timer manager has been
       * suspended or is suspending. Otherwise timer will fire on schedule through the delegate.
       * @see commonj.timers.TimerListener#timerExpired(commonj.timers.Timer)
       */
      public void timerExpired(Timer aTimer)
      {
         if (isSuspending())
            deferTimerExecution(aTimer);
         else
         {
            try
            {
               // Indicate that timer has begun execution and expire the timer
               timerExecutionStart(aTimer);
               mListener.timerExpired(aTimer);
            }
            catch (Throwable th)
            {
               AeException.logError(th, AeMessages.getString("AeTimerManager.ERROR_UnexpectedTimerWorkError")); //$NON-NLS-1$
            }
            finally
            {
               timerExecutionStop(aTimer);
            }
         }
      }
   }
}