//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/filters/AeSQLAlarmFilter.java,v 1.4 2006/02/24 16:37:31 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.filters;

import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLQueueStorageProvider;
import org.activebpel.rt.util.AeUtil;


/**
 * Wraps the AeAlarmFilter and uses its selection
 * criteria to format the select statement and the matching
 * criteria parameters.
 */
public class AeSQLAlarmFilter extends AeSQLFilter
{
   /** Query key. */
   private static final String SQL_GET_ALARMS = "GetAlarmsJoinProcess"; //$NON-NLS-1$
   /** Where clause that always gets appended to the query if its not empty from the sql config file. */
   private static final String SQL_GET_ALARMS_WHERE_CLAUSE = "GetAlarmsWhereClause"; //$NON-NLS-1$
   /** Group By clause. */
   private static final String SQL_ALARMS_ORDER_BY = "GetAlarmsOrderBy"; //$NON-NLS-1$
   /** Column constants. */
   private static final String SQL_PROCESS_ID = "AeAlarm.ProcessId"; //$NON-NLS-1$
   /** Column constants. */
   private static final String SQL_PROCESS_NAME = "AeProcess.ProcessName"; //$NON-NLS-1$
   /** Column constants. */
   private static final String SQL_PROCESS_NAMESPACE = "AeProcess.ProcessNamespace"; //$NON-NLS-1$
   /** Column constants. */
   private static final String SQL_DEADLINE_MILLIS = "AeAlarm.DeadlineMillis"; //$NON-NLS-1$

   /**
    * Constructor.
    * @param aFilter The selection criteria.
    */
   public AeSQLAlarmFilter( AeAlarmFilter aFilter, AeSQLConfig aConfig ) throws AeStorageException
   {
      super( aFilter, aConfig, AeSQLQueueStorageProvider.SQLSTATEMENT_PREFIX );
      setSelectClause(getSQLStatement(SQL_GET_ALARMS));
      setOrderBy(getSQLStatement(SQL_ALARMS_ORDER_BY));
   }

   /**
    * Builds the sql statement.
    */
   protected void processFilter( )
   {
      AeAlarmFilter filter = (AeAlarmFilter) getFilter();

      // the static where clause is included as part of the query if it's been
      // set in the sql config class
      appendCondition(getSQLStatement(SQL_GET_ALARMS_WHERE_CLAUSE));

      if( filter != null )
      {
         if( !filter.isNullProcessId() )
         {
            appendCondition( SQL_PROCESS_ID + " = ?", new Long( filter.getProcessId() ) ); //$NON-NLS-1$
         }

         if( filter.getProcessName() != null )
         {
            if( ! AeUtil.isNullOrEmpty(filter.getProcessName().getLocalPart()) )
               appendCondition( SQL_PROCESS_NAME + " = ?", filter.getProcessName().getLocalPart() ); //$NON-NLS-1$
            if( ! AeUtil.isNullOrEmpty(filter.getProcessName().getNamespaceURI()) )
               appendCondition( SQL_PROCESS_NAMESPACE + " = ?", filter.getProcessName().getNamespaceURI() ); //$NON-NLS-1$
         }

         if( filter.getAlarmFilterStart() != null )
         {
            appendCondition( SQL_DEADLINE_MILLIS + " >= ?", new Long(filter.getAlarmFilterStart().getTime()) ); //$NON-NLS-1$
         }

         if( filter.getAlarmFilterEnd() != null )
         {
            appendCondition( SQL_DEADLINE_MILLIS + " <= ?", new Long(filter.getAlarmFilterEnd().getTime()) ); //$NON-NLS-1$
         }
      }
   }
}