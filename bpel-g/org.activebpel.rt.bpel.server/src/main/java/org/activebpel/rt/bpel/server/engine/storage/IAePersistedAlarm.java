// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAePersistedAlarm.java,v 1.4 2006/09/18 17:59:53 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Date;

/**
 * Represents an alarm that was stored in the persistent store.  This class
 * provides access to the details about the alarm that were persisted.
 */
public interface IAePersistedAlarm
{
   /**
    * Gets the process ID associated with this alarm.
    * 
    * @return The process ID.
    */
   public long getProcessId();

   /**
    * Gets the location path ID associated with this alarm.
    * 
    * @return The location path ID.
    */
   public int getLocationPathId();

   /**
    * Gets the alarm's deadline.
    * 
    * @return The alarm's deadline.
    */
   public Date getDeadline();
   
   /**
    * Gets the alarm's group id.
    * 
    * @return The alarm's group id.
    */
   public int getGroupId();
   
   /** 
    * @return The alarm id.
    */
   public int getAlarmId();
}
