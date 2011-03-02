//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/IAeSchedulerSQLKeys.java,v 1.2 2007/02/05 17:10:02 PJayanetti Exp $
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
 * Scheduler SQL query keys.
 */
public interface IAeSchedulerSQLKeys
{
   public static final String INSERT_SCHEDULE           = "InsertSchedule"; //$NON-NLS-1$
   public static final String UPDATE_SCHEDULE           = "UpdateSchedule"; //$NON-NLS-1$
   public static final String GET_ENTRY                 = "GetEntry"; //$NON-NLS-1$
   public static final String GET_ENABLED_ENTRY         = "GetEnabledEntry"; //$NON-NLS-1$
   public static final String LIST_ENABLED_ENTRIES      = "ListEnabledEntries"; //$NON-NLS-1$
   public static final String LIST_UNSCHEDULED_ENTRIES  = "ListUnscheduledEntries"; //$NON-NLS-1$
   public static final String LIST_LOCKED_ENTRIES       = "ListLockedEntries"; //$NON-NLS-1$
   public static final String UPDATE_DEADLINE           = "UpdateDeadline"; //$NON-NLS-1$
   public static final String UPDATE_ENDDATE            = "UpdateEnddate"; //$NON-NLS-1$
   public static final String CANCEL_ENTRY              = "CancelEntry"; //$NON-NLS-1$
   public static final String LOCK_ENTRY                = "LockEntry"; //$NON-NLS-1$
   public static final String UNLOCK_ENTRY              = "UnLockEntry"; //$NON-NLS-1$
   public static final String LOCK_ENTRY_FOR_SCHEDULED_EXECUTION  = "LockEntryForScheduledExecution"; //$NON-NLS-1$
   public static final String TRANSFER_LOCK             = "TransferLock"; //$NON-NLS-1$   
   public static final String LIST_LOCKED_ENTRIES_BY_ENGINE = "ListLockedEntriesByEngineId"; //$NON-NLS-1$   
}
