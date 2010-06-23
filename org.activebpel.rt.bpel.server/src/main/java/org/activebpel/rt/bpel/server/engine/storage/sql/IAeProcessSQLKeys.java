// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/IAeProcessSQLKeys.java,v 1.11 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

/**
 * Constants for the Process storage SQL keys (keys into the {@link
 * org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig} object).
 */
public interface IAeProcessSQLKeys
{
   public static final String DELETE_JOURNAL_ENTRIES       = "DeleteJournalEntries"; //$NON-NLS-1$
   public static final String DELETE_JOURNAL_ENTRY         = "DeleteJournalEntry"; //$NON-NLS-1$
   public static final String DELETE_PROCESS               = "DeleteProcess"; //$NON-NLS-1$
   public static final String DELETE_PROCESS_LOG           = "DeleteProcessLog"; //$NON-NLS-1$
   public static final String DELETE_PROCESS_VARIABLES     = "DeleteProcessVariables"; //$NON-NLS-1$
   public static final String DELETE_PROCESSES             = "DeleteProcesses"; //$NON-NLS-1$
   public static final String DELETE_VARIABLE              = "DeleteVariable"; //$NON-NLS-1$
   public static final String GET_JOURNAL_ENTRIES          = "GetJournalEntries"; //$NON-NLS-1$
   public static final String GET_JOURNAL_ENTRIES_LOCATION_IDS = "GetJournalEntriesLocationIds"; //$NON-NLS-1$
   public static final String GET_JOURNAL_ENTRY            = "GetJournalEntry"; //$NON-NLS-1$
   public static final String GET_PROCESS_DOCUMENT         = "GetProcessDocument"; //$NON-NLS-1$
   public static final String GET_PROCESS_INSTANCE_DETAIL  = "GetProcessInstanceDetail"; //$NON-NLS-1$
   public static final String GET_PROCESS_NAME             = "GetProcessName"; //$NON-NLS-1$
   public static final String GET_PROCESS_LIST             = "GetProcessList"; //$NON-NLS-1$
   public static final String GET_PROCESS_IDS              = "GetProcessIds"; //$NON-NLS-1$
   public static final String GET_PROCESS_COUNT            = "GetProcessCount"; //$NON-NLS-1$
   public static final String GET_PROCESS_LIST_WHERE       = "GetProcessWhereClause";  //$NON-NLS-1$
   public static final String GET_PROCESS_VARIABLES        = "GetProcessVariables"; //$NON-NLS-1$
   public static final String GET_RECOVERY_PROCESS_IDS     = "GetRecoveryProcessIds"; //$NON-NLS-1$
   public static final String GET_RESTART_PROCESS_JOURNAL_ENTRY = "GetRestartProcessJournalEntry"; //$NON-NLS-1$
   public static final String GET_VARIABLE_DOCUMENT        = "GetVariableDocument"; //$NON-NLS-1$
   public static final String INSERT_PROCESS               = "InsertProcess"; //$NON-NLS-1$
   public static final String INSERT_PROCESS_LOG           = "InsertProcessLog"; //$NON-NLS-1$
   public static final String INSERT_VARIABLE              = "InsertVariable"; //$NON-NLS-1$
   public static final String UPDATE_JOURNAL_ENTRY_TYPE    = "UpdateJournalEntryType"; //$NON-NLS-1$
   public static final String UPDATE_PROCESS               = "UpdateProcess"; //$NON-NLS-1$
}
