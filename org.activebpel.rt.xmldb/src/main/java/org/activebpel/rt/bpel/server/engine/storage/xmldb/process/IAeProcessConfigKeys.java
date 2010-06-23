// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/IAeProcessConfigKeys.java,v 1.2 2007/09/28 19:55:23 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process;

/**
 * An interface that simply holds some static constants which are the names of keys in the XMLDB config
 * object.
 */
public interface IAeProcessConfigKeys
{
   /* Used in the process state storage. */
   public static final String INSERT_PROCESS               = "InsertProcess"; //$NON-NLS-1$
   public static final String GET_PROCESS_INSTANCE_DETAIL  = "GetProcessInstanceDetail"; //$NON-NLS-1$
   public static final String GET_PROCESS_NAME             = "GetProcessName"; //$NON-NLS-1$
   public static final String GET_RECOVERY_PROCESS_IDS     = "GetRecoveryProcessIds"; //$NON-NLS-1$
   public static final String GET_PROCESS_LIST             = "GetProcessList"; //$NON-NLS-1$
   public static final String GET_PROCESS_IDS              = "GetProcessIds"; //$NON-NLS-1$
   public static final String DELETE_PROCESS               = "DeleteProcess"; //$NON-NLS-1$
   public static final String GET_MAX_PROCESSID            = "GetMaxProcessID"; //$NON-NLS-1$
   public static final String GET_MIN_PROCESSID            = "GetMinProcessID"; //$NON-NLS-1$
   public static final String GET_JOURNAL_ENTRIES          = "GetJournalEntries"; //$NON-NLS-1$
   public static final String GET_JOURNAL_ENTRY            = "GetJournalEntry"; //$NON-NLS-1$
   public static final String GET_JOURNAL_ENTRIES_LOCATION_IDS = "GetJournalEntriesLocationIds"; //$NON-NLS-1$
   public static final String GET_RESTART_PROCESS_JOURNAL_ENTRY = "GetRestartProcessJournalEntry"; //$NON-NLS-1$

   /* Used in the process state connection. */
   public static final String DELETE_JOURNAL_ENTRIES       = "DeleteJournalEntries"; //$NON-NLS-1$
   public static final String INSERT_PROCESS_LOG           = "InsertProcessLog"; //$NON-NLS-1$
   public static final String INSERT_VARIABLE              = "InsertVariable"; //$NON-NLS-1$
   public static final String GET_PROCESS_DOCUMENT         = "GetProcessDocument"; //$NON-NLS-1$
   public static final String GET_PROCESS_VARIABLES        = "GetProcessVariables"; //$NON-NLS-1$
   public static final String GET_VARIABLE_DOCUMENT        = "GetVariableDocument"; //$NON-NLS-1$
   public static final String DELETE_VARIABLE              = "DeleteVariable"; //$NON-NLS-1$
   public static final String UPDATE_PROCESS               = "UpdateProcess"; //$NON-NLS-1$
   public static final String UPDATE_PROCESS_ENDDATE       = "UpdateProcessEndDate"; //$NON-NLS-1$
   public static final String UPDATE_JOURNAL_ENTRY_TYPE    = "UpdateJournalEntryType"; //$NON-NLS-1$

   /* Used for accessing process log related statements. */
   public static final String GET_HEAD           = "GetLogHead"; //$NON-NLS-1$
   public static final String GET_TAIL           = "GetLogTail"; //$NON-NLS-1$
   public static final String GET_LOG_ENTRIES    = "GetLogEntries"; //$NON-NLS-1$
   public static final String GET_SMALL_LOG      = "GetSmallLog"; //$NON-NLS-1$
   public static final String GET_LOG_IDS        = "GetLogIDs"; //$NON-NLS-1$
   public static final String GET_LOG            = "GetLog"; //$NON-NLS-1$
}
