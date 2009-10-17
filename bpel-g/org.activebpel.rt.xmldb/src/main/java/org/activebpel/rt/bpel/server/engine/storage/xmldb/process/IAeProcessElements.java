//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/IAeProcessElements.java,v 1.1 2007/08/17 00:40:56 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process;

/**
 * Constants that define the Process element names.
 */
public interface IAeProcessElements
{
   /* AeProcess doc type elements. */
   public static final String PROCESS_ID        = "ProcessID"; //$NON-NLS-1$
   public static final String PROCESS_NAME      = "ProcessName"; //$NON-NLS-1$
   public static final String PROCESS_STATE     = "ProcessState"; //$NON-NLS-1$
   public static final String PROCESS_STATE_REASON = "ProcessStateReason"; //$NON-NLS-1$
   public static final String START_DATE        = "StartDate"; //$NON-NLS-1$
   public static final String END_DATE          = "EndDate"; //$NON-NLS-1$
   public static final String PLAN_ID           = "PlanID"; //$NON-NLS-1$
   public static final String PROCESS_DOCUMENT  = "ProcessDocument"; //$NON-NLS-1$
   public static final String PENDING_INVOKES_COUNT = "PendingInvokesCount"; //$NON-NLS-1$
   public static final String MODIFIED_DATE     = "ModifiedDate"; //$NON-NLS-1$
   public static final String MODIFIED_COUNT    = "ModifiedCount"; //$NON-NLS-1$

   /* AeReceivedItem doc type elements. */
   public static final String LOCATION_PATH_ID    = "LocationPathID"; //$NON-NLS-1$
   public static final String MESSAGE_DOCUMENT    = "MessageDocument"; //$NON-NLS-1$

   /* AeVariable doc type elements. */
   public static final String VERSION_NUMBER    = "VersionNumber"; //$NON-NLS-1$
   public static final String VARIABLE_DOCUMENT = "VariableDocument"; //$NON-NLS-1$

   /* AeProcessLog doc type elements. */
   public static final String LOG_ID            = "LogID"; //$NON-NLS-1$
   public static final String LINE_COUNT        = "LineCount"; //$NON-NLS-1$
   public static final String PROCESS_LOG       = "ProcessLog"; //$NON-NLS-1$
}
