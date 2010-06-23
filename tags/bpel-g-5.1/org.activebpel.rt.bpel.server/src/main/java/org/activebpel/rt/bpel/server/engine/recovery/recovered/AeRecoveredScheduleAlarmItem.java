// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/AeRecoveredScheduleAlarmItem.java,v 1.5 2007/11/15 21:06:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered;

import java.util.Date;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.AeMessages;

/**
 * Implements a recovered item to schedule an alarm.
 */
public class AeRecoveredScheduleAlarmItem extends AeRecoveredLocationIdItem
{
   /** The alarm's group id. */
   private final int mGroupId;
   /** The alarm deadline. */
   private final Date mDeadline;
   /** The alarm execution id. */
   private final int mAlarmId;

   /**
    * Constructs a recovered item to schedule an alarm.
    */
   public AeRecoveredScheduleAlarmItem(long aProcessId, int aLocationId, int aGroupId, int aAlarmId, Date aDeadline)
   {
      super(aProcessId, aLocationId);
      mGroupId = aGroupId;
      mDeadline = aDeadline;
      mAlarmId = aAlarmId;
   }

   /**
    * Constructs a recovered item to match another schedule alarm item by
    * location id.
    */
   public AeRecoveredScheduleAlarmItem(int aLocationId, int aAlarmId)
   {
      this(0, aLocationId, 0, aAlarmId, null);
   }

   /**
    * Returns the alarm deadline;
    */
   protected Date getDeadline()
   {
      return mDeadline;
   }

   /**
    * Returns the alarm's group id.
    */
   protected int getGroupId()
   {
      return mGroupId;
   }
   
   /**
    * Returns the alarm id.
    */
   public int getAlarmId()
   {
      return mAlarmId;
   }   
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#queueItem(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void queueItem(IAeBusinessProcessEngineInternal aTargetEngine) throws AeBusinessProcessException
   {
      if (getDeadline() == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeRecoveredScheduleAlarmItem.ERROR_0")); //$NON-NLS-1$
      }
      aTargetEngine.scheduleAlarm(getProcessId(), getLocationId(), getGroupId(), getAlarmId(), getDeadline());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#isRemoval()
    */
   public boolean isRemoval()
   {
      return false;
   }
}