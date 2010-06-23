// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityWhileImpl.java,v 1.31 2007/11/21 03:22:17 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeAlarmReceiver;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.support.IAeLoopActivity;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Implementation of the bpel while activity.
 */
public class AeActivityWhileImpl extends AeLoopActivity
   implements IAeActivityParent, IAeAlarmReceiver, IAeLoopActivity
{
   private static final String CONFIG_WHILE_ALARM_DELAY      = "WhileAlarmDelay"; //$NON-NLS-1$
   private static final String CONFIG_WHILE_ALARM_ITERATIONS = "WhileAlarmIterations"; //$NON-NLS-1$

   private static final int DEFAULT_WHILE_ALARM_DELAY      = 100; // milliseconds
   private static final int DEFAULT_WHILE_ALARM_ITERATIONS = 1000;

   /** <code>true</code> while this activity has an alarm queued. */
   private boolean mQueued = false;

   /** Number of iterations until next alarm for this activity. */
   private int mAlarmIterations = 0;
   /** Alarm execution instance reference id. */
   private int mAlarmId;

   /** default constructor for activity */
   public AeActivityWhileImpl(AeActivityWhileDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
      setAlarmId(-1);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAlarmReceiver#getGroupId()
    */
   public int getGroupId()
   {
      return getLocationId();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeActivityParent#addActivity(org.activebpel.rt.bpel.IAeActivity)
    */
   public void addActivity(IAeActivity aActivity)
   {
      setChild(aActivity);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return Collections.singleton(getChild()).iterator();
   }

   /**
    * Tests the condition and if true proceeds to execute the child activity.
    * Otherwise it sets itself as complete.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      initAlarmIterations();

      AeConditionDef conditionDef = ((AeActivityWhileDef) getDefinition()).getConditionDef();

      boolean isConditionTrue = executeBooleanExpression(conditionDef);

      // Generate engine info event for debug.
      //
      getProcess().getEngine().fireEvaluationEvent(getProcess().getProcessId(), conditionDef.getExpression(),
            IAeProcessInfoEvent.INFO_WHILE, getLocationPath(), Boolean.toString(isConditionTrue));

      if(isConditionTrue)
      {
         // Queue the activity to execute
         getChild().setState(AeBpelState.INACTIVE);
         getProcess().queueObjectToExecute(getChild());
      }
      else
      {
         // otherwise condition is false so we are done
         objectCompleted();
      }
   }

   /**
    * Initialize the alarm iterations member data.
    */
   protected void initAlarmIterations()
   {
      // Note: mAlarmIterations may be less than 0 if childComplete() fires
      // after process state is restored from persistent storage.
      if (mAlarmIterations <= 0)
      {
         // Initialize number of iterations to execute before queueing an
         // alarm.
         mAlarmIterations = getAlarmIterations();
      }
   }

   /**
    * Handles a normal child completion or continue call by re-executing itself,
    * which will reschedule the child activity if the while condition is still true.
    * Any other kind of child complete (like that from a dead path) is ignored.
    */
   protected void handleLoopCompletion(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      if (!getState().isFinal())
      {
         // If this activity has executed a lot of iterations, then queue an
         // alarm to execute the next iteration.
         //
         // Note: Test was (--mAlarmIterations <= 0), but we're specifically
         // interested in the transition from 1 to 0 (and not in the transition
         // from 0 to -1, which may occur after process state is restored from
         // persistent storage).
         if (--mAlarmIterations == 0)
         {
            queue();
         }
         else
         {
            execute();
         }
      }
   }

   /**
    * Overrides method to extend base in order to dequeue any queued alarm. So if we are
    * terminating or completing with queued alarms they should be dequeued.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#setState(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void setState(AeBpelState aNewState) throws AeBusinessProcessException
   {
      dequeue();
      super.setState(aNewState);
   }

   /**
    * Dequeues the alarm from the process.
    */
   protected void dequeue() throws AeBusinessProcessException
   {
      if (isQueued())
      {
         getProcess().dequeueAlarm(this);
         setQueued(false);
      }
   }

   /**
    * Returns number of milliseconds to delay when queueing an alarm.
    */
   protected int getAlarmDelay()
   {
      return getConfigInt(CONFIG_WHILE_ALARM_DELAY, DEFAULT_WHILE_ALARM_DELAY);
   }

   /**
    * Returns number of iterations to execute before queueing an alarm.
    */
   protected int getAlarmIterations()
   {
      return getConfigInt(CONFIG_WHILE_ALARM_ITERATIONS, DEFAULT_WHILE_ALARM_ITERATIONS);
   }

   /**
    * Returns <code>int</code> value from configuration.
    *
    * @param aKey
    */
   protected int getConfigInt(String aKey, int aDefaultValue)
   {
      return getProcess().getEngine().getEngineConfiguration().getIntegerEntry(aKey, aDefaultValue);
   }

   /**
    * Returns the queued flag.
    */
   public boolean isQueued()
   {
      return mQueued;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAlarmReceiver#onAlarm()
    */
   public void onAlarm() throws AeBusinessProcessException
   {
      if (isQueued())
      {
         setQueued(false);
         execute();
      }
   }

   /**
    * Queues the alarm for this activity.
    */
   protected void queue() throws AeBusinessProcessException
   {
      if (!isQueued())
      {
         setQueued(true);
         getProcess().queueAlarm(this, new Date(System.currentTimeMillis() + getAlarmDelay()));
      }
   }

   /**
    * Sets the queued flag.
    */
   public void setQueued(boolean aQueued)
   {
      mQueued = aQueued;
   }
   
   /**
    * @return Returns the alarmId.
    */
   public int getAlarmId()
   {
      return mAlarmId;
   }

   /**
    * @param aAlarmId The alarmId to set.
    */
   public void setAlarmId(int aAlarmId)
   {
      mAlarmId = aAlarmId;
   }   
}
