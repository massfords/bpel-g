// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityWaitImpl.java,v 1.22 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.Date;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeAlarmReceiver;
import org.activebpel.rt.bpel.impl.activity.support.AeAlarmCalculator;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Implementation of the bpel wait activity.
 */
public class AeActivityWaitImpl extends AeActivityImpl implements IAeAlarmReceiver
{
   /** True while the wait activity is queued */
   private boolean mQueued;
   
   /** Alarm execution instance reference id. */
   private int mAlarmId;
   
   
   /** default constructor for activity */
   public AeActivityWaitImpl(AeActivityWaitDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
      setAlarmId(-1);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAlarmReceiver#getGroupId()
    */
   public int getGroupId()
   {
      return getLocationId();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      Date deadline = AeAlarmCalculator.calculateDeadline(this, getDef(), IAeProcessInfoEvent.INFO_WAIT);
      setQueued(true);
      getProcess().queueAlarm(this, deadline);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAlarmReceiver#onAlarm()
    */
   public void onAlarm() throws AeBusinessProcessException
   {
      if (isQueued())
      {
         setQueued(false);
         objectCompleted();
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
    * Dequeues the wait from the process.
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
    * Setter for the queued flag
    * @param aBool
    */
   public void setQueued(boolean aBool)
   {
      mQueued = aBool;
   }

   /**
    * Getter for the queued flag
    */
   public boolean isQueued()
   {
      return mQueued;
   }
   
   /**
    * Getter for the def
    */
   protected AeActivityWaitDef getDef()
   {
      return (AeActivityWaitDef) getDefinition();
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
