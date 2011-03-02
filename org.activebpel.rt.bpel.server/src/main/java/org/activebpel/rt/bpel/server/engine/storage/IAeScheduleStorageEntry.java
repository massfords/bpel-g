//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAeScheduleStorageEntry.java,v 1.2 2008/02/17 21:38:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

public interface IAeScheduleStorageEntry
{
   /**
    * Schedule entry id
    *
    */
   public String getId();
   
   /**
    * Schedule trigger.
    *
    */
   public String getTrigger();
   
   /**
    * True if the schedule is enabled.
    *
    */
   public boolean isEnabled();
   
   /**
    * Engine id that has currently locked the entry.
    * @return engine id if locked or 0 otherwise.
    */
   public int getLocked();
   
   /**
    * Deadline in milli seconds.
    * @return deadline or 0 if not scheduled.
    */
   public long getDeadlineMillis();
   
   /**
    * Last known start date.
    * @return start date in ms or 0 if not started.
    */
   public long getStartDateMillis();

   /**
    * Last known end date.
    * @return end date in ms or 0 if not started.
    */   
   public long getEndDateMillis();
   
   /** 
    * @return Classname of implementation.
    */
   public String getClassname();
}
