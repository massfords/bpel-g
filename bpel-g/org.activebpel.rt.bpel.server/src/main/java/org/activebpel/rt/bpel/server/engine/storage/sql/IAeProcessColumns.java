//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/IAeProcessColumns.java,v 1.3 2007/01/26 15:05:50 TZhang Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

/**
 * Defines constants for process column names.
 */
public interface IAeProcessColumns
{
   public static final String END_DATE          = "EndDate"; //$NON-NLS-1$
   public static final String PLAN_ID           = "PlanId"; //$NON-NLS-1$
   public static final String PROCESS_ID        = "ProcessId"; //$NON-NLS-1$
   public static final String PROCESS_NAME      = "ProcessName"; //$NON-NLS-1$
   public static final String PROCESS_NAMESPACE = "ProcessNamespace"; //$NON-NLS-1$
   public static final String PROCESS_STATE     = "ProcessState"; //$NON-NLS-1$
   public static final String PROCESS_STATE_REASON = "ProcessStateReason"; //$NON-NLS-1$
   public static final String START_DATE        = "StartDate"; //$NON-NLS-1$
}
