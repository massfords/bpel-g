// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/AeRecoveredRemoveAlarmItem.java,v 1.4 2007/11/15 21:06:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;

/**
 * Implements a recovered item to remove an alarm.
 */
public class AeRecoveredRemoveAlarmItem extends AeRecoveredLocationIdItem
{
   /** Alarm Id. */
   private int mAlarmId;
   /**
    * Constructs a recovered item to remove an alarm.
    */
   public AeRecoveredRemoveAlarmItem(long aProcessId, int aLocationId, int aAlarmId)
   {
      super(aProcessId, aLocationId);
      setAlarmId(aAlarmId);
   }

   /**
    * @return Returns the alarmId.
    */
   protected int getAlarmId()
   {
      return mAlarmId;
   }

   /**
    * @param aAlarmId The alarmId to set.
    */
   protected void setAlarmId(int aAlarmId)
   {
      mAlarmId = aAlarmId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#queueItem(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void queueItem(IAeBusinessProcessEngineInternal aTargetEngine) throws AeBusinessProcessException
   {
      aTargetEngine.getQueueManager().removeAlarm(getProcessId(), getLocationId(), getAlarmId());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#isRemoval()
    */
   public boolean isRemoval()
   {
      return true;
   }
}