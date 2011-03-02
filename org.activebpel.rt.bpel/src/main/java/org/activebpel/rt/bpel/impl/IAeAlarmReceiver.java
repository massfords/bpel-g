// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeAlarmReceiver.java,v 1.6 2006/09/18 17:55:33 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * Defines an onAlarm callback.  An alarm receiver is used when an bpel 
 * executable object wishes to be called back when an alarm time has been reached.
 */
public interface IAeAlarmReceiver
{
   /**
    * Callback when an alarm time has been reached.
    * @throws AeBusinessProcessException Allows the receiver to throw an exception.
    */
   public void onAlarm() throws AeBusinessProcessException;

   /**
    * Returns the unique location path within the process for this receiver.
    */
   public String getLocationPath();

   /**
    * Returns the unique location id within the process for this receiver.
    */
   public int getLocationId();

   /**
    * Returns the location path of the group this alarm is a part of.  An alarm's group is
    * determined by the activity it is associated with.  This could be a Wait activity, While,
    * or Pick.
    */
   public int getGroupId();
   
   /** 
    * @return The alarm execution instance id.
    */
   public int getAlarmId();

   /** 
    * Sets the alarm execution instance id.
    * @param aAlarmId alarm id.
    */
   public void setAlarmId(int aAlarmId);
   
   /**
    * Returns <code>true</code> if and only if this object is expecting to
    * receive an alarm.
    */
   public boolean isQueued();
   
}
