//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/IAeSchedulerColumns.java,v 1.2 2007/01/29 15:28:25 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

/**
 * SQL table column names for scheduler table.
 */
public interface IAeSchedulerColumns
{
   public static final String SCHEDULER_ID   = "ScheduleId"; //$NON-NLS-1$
   public static final String STATE          = "State"; //$NON-NLS-1$
   public static final String TRIGGER        = "ScheduleTrigger"; //$NON-NLS-1$
   public static final String DEADLINE_MS    = "DeadlineMillis"; //$NON-NLS-1$
   public static final String STARTDATE_MS   = "StartDateMillis"; //$NON-NLS-1$
   public static final String ENDDATE_MS     = "EndDateMillis"; //$NON-NLS-1$
   public static final String LOCKED         = "Locked"; //$NON-NLS-1$
   public static final String CLASSNAME      = "Classname"; //$NON-NLS-1$

   // todo (PJ) create indices for state,locked and trigger cols - when if neccessary
   // todo (PJ) add new column to store clob to store dom instead of className
   // todo (PJ) scheduleId column should be autogen from storage layer.

}
