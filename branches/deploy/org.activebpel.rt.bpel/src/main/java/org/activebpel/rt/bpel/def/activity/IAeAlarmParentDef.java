// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity;

import java.util.Iterator;

import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;

/**
 * Defs that can parent an onAlarm construct should implement this interface.
 */
public interface IAeAlarmParentDef
{
   /**
    * Gets an iterator over the alarm defs.
    */
   public Iterator getAlarmDefs();

   /**
    * Adds the alarm.
    * 
    * @param aAlarm
    */
   public void addAlarmDef(AeOnAlarmDef aAlarm);
}
