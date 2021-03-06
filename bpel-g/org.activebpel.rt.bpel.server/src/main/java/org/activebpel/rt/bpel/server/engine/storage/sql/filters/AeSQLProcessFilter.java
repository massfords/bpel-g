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

import org.activebpel.rt.bpel.impl.AeSuspendReason;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeProcessColumns;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeProcessSQLKeys;
import org.activebpel.rt.util.AeDate;

import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessStateFilterValueType;
import bpelg.services.processes.types.ProcessStateValueType;

/**
 * Helper class to create a SQL statement that queries the database with the
 * conditions defined by an <code>AeProcessFilter</code>.
 */
public class AeSQLProcessFilter extends AeSQLFilter {
    /**
     * Constructor.
     */
    public AeSQLProcessFilter(ProcessFilterType aFilter, AeSQLConfig aConfig)
            throws AeStorageException {
        super(aFilter.getMaxReturn(), aFilter.getListStart(), aConfig,
                AeSQLProcessStateStorageProvider.PROCESS_STORAGE_PREFIX);
        processFilter(aFilter);
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

    private void processFilter(ProcessFilterType aFilter) {
        clearWhereClause();

        // The static where clause is included on the query if it's been set in
        // the config file.
        appendCondition(getSQLStatement(IAeProcessSQLKeys.GET_PROCESS_LIST_WHERE));

        // Handle process name specified in the filter.
        QName processName = aFilter.getProcessName();
        if (processName != null) {
            String localPart = processName.getLocalPart();
            String namespaceURI = processName.getNamespaceURI();
            appendStringCondition(
                    AeSQLProcessStateStorageProvider.SQL_PROCESS_TABLE_NAME
                            + "." + IAeProcessColumns.PROCESS_NAME, localPart); //$NON-NLS-1$
            appendStringCondition(
                    AeSQLProcessStateStorageProvider.SQL_PROCESS_TABLE_NAME
                            + "." + IAeProcessColumns.PROCESS_NAMESPACE, namespaceURI); //$NON-NLS-1$
        }

        // Handle process state specified in the filter.
        ProcessStateFilterValueType psf = aFilter.getProcessState();
        if (psf == ProcessStateFilterValueType.Completed)
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " = ?", ProcessStateValueType.Complete.value()); //$NON-NLS-1$
        else if (psf == ProcessStateFilterValueType.Running)
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " = ?", ProcessStateValueType.Running.value()); //$NON-NLS-1$
        else if (psf == ProcessStateFilterValueType.Faulted)
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " = ?", ProcessStateValueType.Faulted.value()); //$NON-NLS-1$
        else if (psf == ProcessStateFilterValueType.Suspended)
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " = ?", ProcessStateValueType.Suspended.value()); //$NON-NLS-1$
        else if (psf == ProcessStateFilterValueType.SuspendedFaulting) {
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " = ?", ProcessStateValueType.Suspended.value()); //$NON-NLS-1$
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE_REASON + " = ?", AeSuspendReason.SUSPEND_CODE_AUTOMATIC); //$NON-NLS-1$
        } else if (psf == ProcessStateFilterValueType.SuspendedProgrammatic) {
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " = ?", ProcessStateValueType.Suspended.value()); //$NON-NLS-1$
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE_REASON + " = ?", AeSuspendReason.SUSPEND_CODE_LOGICAL); //$NON-NLS-1$
        } else if (psf == ProcessStateFilterValueType.SuspendedManual) {
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " = ?", ProcessStateValueType.Suspended.value()); //$NON-NLS-1$
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE_REASON + " = ?", AeSuspendReason.SUSPEND_CODE_MANUAL); //$NON-NLS-1$
        } else if (psf == ProcessStateFilterValueType.SuspendedInvokeRecovery) {
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " = ?", ProcessStateValueType.Suspended.value()); //$NON-NLS-1$
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE_REASON + " = ?", AeSuspendReason.SUSPEND_CODE_INVOKE_RECOVERY); //$NON-NLS-1$
        } else if (psf == ProcessStateFilterValueType.CompletedOrFaulted) {
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " IN (?, ?)", new Object[]{ //$NON-NLS-1$
                    ProcessStateValueType.Complete.value(),
                    ProcessStateValueType.Faulted.value()});
        } else if (psf == ProcessStateFilterValueType.Compensatable)
            appendCondition(
                    IAeProcessColumns.PROCESS_STATE + " = ?", ProcessStateValueType.Compensatable.value()); //$NON-NLS-1$
        else if (psf == ProcessStateFilterValueType.RunningOrSuspended) {
            Object[] params = new Object[]{
                    ProcessStateValueType.Running.value(),
                    ProcessStateValueType.Suspended.value()};
            appendCondition(
                    "(" + IAeProcessColumns.PROCESS_STATE + " = ? OR " + IAeProcessColumns.PROCESS_STATE + " = ?)", params); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        // Handle start of process start date range specified in the filter.
        Date processCreateStart = AeDate
                .toDate(aFilter.getProcessCreateStart());
        if (processCreateStart != null) {
            appendCondition(
                    IAeProcessColumns.START_DATE + " >= ?", processCreateStart); //$NON-NLS-1$
        }

        // Handle end of process start date range specified in the filter.
        Date processCreateEnd = AeDate.nextDay(aFilter.getProcessCreateEnd());
        if (processCreateEnd != null) {
            appendCondition(
                    IAeProcessColumns.START_DATE + " < ?", processCreateEnd); //$NON-NLS-1$
        }

        // Handle start of process complete date range specified in the filter.
        Date processCompleteStart = AeDate.toDate(aFilter
                .getProcessCompleteStart());
        if (processCompleteStart != null) {
            appendCondition(
                    IAeProcessColumns.END_DATE + " >= ?", processCompleteStart); //$NON-NLS-1$
        }

        // Handle end of process complete date range specified in the filter.
        Date processCompleteEnd = AeDate.nextDay(aFilter
                .getProcessCompleteEnd());
        if (processCompleteEnd != null) {
            appendCondition(
                    IAeProcessColumns.END_DATE + " < ?", processCompleteEnd); //$NON-NLS-1$
        }

        // Handle the planId specified in the filter
        // FIXME this was never being set in the WS API. What's it doing here?
        // int planId = filter.getPlanId();
        // if ( planId != 0 )
        // {
        //            appendCondition(AeSQLProcessStateStorageProvider.SQL_PROCESS_TABLE_NAME + "." + IAeProcessColumns.PLAN_ID + " = ?", new Integer(planId)); //$NON-NLS-1$ //$NON-NLS-2$
        // }

        // Handle the endDate in the filter, when deletableDate > endDate, it
        // can be deleted, where deletableDate = currentDeleteDate -
        // retentationDays.
        // FIXME this was never being set in the WS API. What's it doing here?
        // Date deletableDate = filter.getDeletableDate();
        // if (deletableDate != null)
        // {
        //            appendCondition(IAeProcessColumns.END_DATE + " < ?", deletableDate); //$NON-NLS-1$
        // }

        // Handle the deleteRange of processes specified in the filter
        if (aFilter.getProcessIdMax() != null
                || aFilter.getProcessIdMin() != null) {
            Long fromIndex = aFilter.getProcessIdMin() == null ? 0 : aFilter
                    .getProcessIdMin();
            Long toIndex = aFilter.getProcessIdMax() == null ? Long.MAX_VALUE
                    : aFilter.getProcessIdMax();
            appendCondition(
                    IAeProcessColumns.PROCESS_ID + " BETWEEN ? AND ? ", new Long[]{fromIndex, toIndex}); //$NON-NLS-1$
        }
    }
}