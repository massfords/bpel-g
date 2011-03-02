// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/filters/AeSQLProcessFilter.java,v 1.9 2008/02/07 18:44:38 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.filters;

import java.util.Date;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.AeSuspendReason;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeProcessColumns;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeProcessSQLKeys;

/**
 * Helper class to create a SQL statement that queries the database with the
 * conditions defined by an <code>AeProcessFilter</code>.
 */
public class AeSQLProcessFilter extends AeSQLFilter
{
   /**
    * Constructor.
    */
   public AeSQLProcessFilter(AeProcessFilter aFilter, AeSQLConfig aConfig)
    throws AeStorageException
   {
      super(aFilter, aConfig, AeSQLProcessStateStorageProvider.PROCESS_STORAGE_PREFIX);

      setDeleteClause(getSQLStatement(IAeProcessSQLKeys.DELETE_PROCESSES));
      setOrderBy(AeSQLProcessStateStorageProvider.SQL_ORDER_BY_START_DATE_PROCESSID);

      // Here we could generate SELECT TOP <rows> ... for SQL Server the way
      // we use LIMIT for MySQL, but doing this doesn't seem to be
      // necessary: the SQL Server driver streams results as we fetch them
      // whereas the MySQL driver brings the entire result set into memory
      // (the MySQL driver can also optionally stream results but locks the
      // table when asked to do so).
      setSelectClause(getSQLStatement(IAeProcessSQLKeys.GET_PROCESS_LIST));
      setCountClause(getSQLStatement(IAeProcessSQLKeys.GET_PROCESS_COUNT));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLFilter#processFilter()
    */
   protected void processFilter()
   throws AeStorageException
   {
      clearWhereClause();

      // The static where clause is included on the query if it's been set in
      // the config file.
      appendCondition(getSQLStatement(IAeProcessSQLKeys.GET_PROCESS_LIST_WHERE));

      if (getFilter() != null)
      {
         AeProcessFilter filter = (AeProcessFilter) getFilter();
         // Handle process name specified in the filter.
         QName processName = filter.getProcessName();
         if (processName != null)
         {
            String localPart = processName.getLocalPart();
            String namespaceURI = processName.getNamespaceURI();
            appendStringCondition(AeSQLProcessStateStorageProvider.SQL_PROCESS_TABLE_NAME + "." + IAeProcessColumns.PROCESS_NAME, localPart); //$NON-NLS-1$ 
            appendStringCondition(AeSQLProcessStateStorageProvider.SQL_PROCESS_TABLE_NAME + "." + IAeProcessColumns.PROCESS_NAMESPACE, namespaceURI); //$NON-NLS-1$ 
         }

         // Handle process state specified in the filter.
         switch (filter.getProcessState())
         {
            case AeProcessFilter.STATE_COMPLETED:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " = ?", new Integer(IAeBusinessProcess.PROCESS_COMPLETE)); //$NON-NLS-1$
               break;
            case AeProcessFilter.STATE_RUNNING:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " = ?", new Integer(IAeBusinessProcess.PROCESS_RUNNING)); //$NON-NLS-1$
               break;
            case AeProcessFilter.STATE_FAULTED:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " = ?", new Integer(IAeBusinessProcess.PROCESS_FAULTED)); //$NON-NLS-1$
               break;
            case AeProcessFilter.STATE_SUSPENDED:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " = ?", new Integer(IAeBusinessProcess.PROCESS_SUSPENDED)); //$NON-NLS-1$
               break;
            case AeProcessFilter.STATE_SUSPENDED_FAULTING:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " = ?", new Integer(IAeBusinessProcess.PROCESS_SUSPENDED)); //$NON-NLS-1$
               appendCondition(IAeProcessColumns.PROCESS_STATE_REASON + " = ?", new Integer(AeSuspendReason.SUSPEND_CODE_AUTOMATIC)); //$NON-NLS-1$
               break;
            case AeProcessFilter.STATE_SUSPENDED_PROGRAMMATIC:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " = ?", new Integer(IAeBusinessProcess.PROCESS_SUSPENDED)); //$NON-NLS-1$
               appendCondition(IAeProcessColumns.PROCESS_STATE_REASON + " = ?", new Integer(AeSuspendReason.SUSPEND_CODE_LOGICAL)); //$NON-NLS-1$
               break;
            case AeProcessFilter.STATE_SUSPENDED_MANUAL:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " = ?", new Integer(IAeBusinessProcess.PROCESS_SUSPENDED)); //$NON-NLS-1$
               appendCondition(IAeProcessColumns.PROCESS_STATE_REASON + " = ?", new Integer(AeSuspendReason.SUSPEND_CODE_MANUAL)); //$NON-NLS-1$
               break;
            case AeProcessFilter.STATE_SUSPENDED_INVOKE_RECOVERY:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " = ?", new Integer(IAeBusinessProcess.PROCESS_SUSPENDED)); //$NON-NLS-1$
               appendCondition(IAeProcessColumns.PROCESS_STATE_REASON + " = ?", new Integer(AeSuspendReason.SUSPEND_CODE_INVOKE_RECOVERY)); //$NON-NLS-1$
               break;
            case AeProcessFilter.STATE_COMPLETED_OR_FAULTED:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " IN (?, ?)", new Object[] { //$NON-NLS-1$
                  new Integer(IAeBusinessProcess.PROCESS_COMPLETE),
                  new Integer(IAeBusinessProcess.PROCESS_FAULTED)});
               break;
            case AeProcessFilter.STATE_COMPENSATABLE:
               appendCondition(IAeProcessColumns.PROCESS_STATE + " = ?", new Integer(IAeBusinessProcess.PROCESS_COMPENSATABLE)); //$NON-NLS-1$
               break;
            case AeProcessFilter.STATE_RUNNING_OR_SUSPENDED:
               Object [] params = new Object[] {
                     new Integer(IAeBusinessProcess.PROCESS_RUNNING),
                     new Integer(IAeBusinessProcess.PROCESS_SUSPENDED)
               };
               appendCondition("(" + IAeProcessColumns.PROCESS_STATE + " = ? OR " + IAeProcessColumns.PROCESS_STATE + " = ?)", params); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
               break;
            default:
               break;
         }

         // Handle start of process start date range specified in the filter.
         Date processCreateStart = filter.getProcessCreateStart();
         if (processCreateStart != null)
         {
            appendCondition(IAeProcessColumns.START_DATE + " >= ?", processCreateStart); //$NON-NLS-1$
         }

         // Handle end of process start date range specified in the filter.
         Date processCreateEnd = filter.getProcessCreateEndNextDay();
         if (processCreateEnd != null)
         {
            appendCondition(IAeProcessColumns.START_DATE + " < ?", processCreateEnd); //$NON-NLS-1$
         }

         // Handle start of process complete date range specified in the filter.
         Date processCompleteStart = filter.getProcessCompleteStart();
         if (processCompleteStart != null)
         {
            appendCondition(IAeProcessColumns.END_DATE + " >= ?", processCompleteStart); //$NON-NLS-1$
         }

         // Handle end of process complete date range specified in the filter.
         Date processCompleteEnd = filter.getProcessCompleteEndNextDay();
         if (processCompleteEnd != null)
         {
            appendCondition(IAeProcessColumns.END_DATE + " < ?", processCompleteEnd); //$NON-NLS-1$
         }

         // Handle the planId specified in the filter
         int planId = filter.getPlanId();
         if ( planId != 0 )
         {
            appendCondition(AeSQLProcessStateStorageProvider.SQL_PROCESS_TABLE_NAME + "." + IAeProcessColumns.PLAN_ID + " = ?", new Integer(planId)); //$NON-NLS-1$ //$NON-NLS-2$
         }
         
         // Handle the endDate in the filter, when deletableDate > endDate, it can be deleted, where deletableDate = currentDeleteDate - retentationDays.
         Date deletableDate = filter.getDeletableDate();
         if (deletableDate != null)
         {
            appendCondition(IAeProcessColumns.END_DATE + " < ?", deletableDate); //$NON-NLS-1$
         }

         // Handle the deleteRange of processes specified in the filter
         long[] processIdRange = filter.getProcessIdRange();
         if ( processIdRange != null )
         {
            Long fromIndex = new Long(processIdRange[0]);
            Long toIndex = new Long(processIdRange[1]);
            appendCondition(IAeProcessColumns.PROCESS_ID + " BETWEEN ? AND ? ", new Long[] {fromIndex, toIndex}); //$NON-NLS-1$
         }
      }
   }
}