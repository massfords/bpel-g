//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeAlarmListHandler.java,v 1.3 2006/09/18 17:59:53 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.list.AeAlarmExt;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeQueueColumns;


/**
 * Creates a result set handler that returns a list of matching AeAlarms.
 * Has the ability to filter the selected alarms based on the filter criteria.
 */
public class AeAlarmListHandler extends AeListingResultSetHandler
{
   /**
    * Default constructor.
    */
   public AeAlarmListHandler()
   {
      this( AeAlarmFilter.NULL_FILTER );
   }

   /**
    * Constructor.
    * @param aFilter The selection criteria.
    */
   public AeAlarmListHandler( AeAlarmFilter aFilter )
   {
      super(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler#convertToType(java.util.List)
    */
   protected Object convertToType(List aResults)
   {
      return aResults;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler#readRow(java.sql.ResultSet)
    */
   protected Object readRow(ResultSet aResultSet) throws SQLException
   {
      long processId = aResultSet.getLong(IAeQueueColumns.PROCESS_ID);
      String processName = aResultSet.getString(IAeQueueColumns.PROCESS_NAME);
      String processNamespace = aResultSet.getString(IAeQueueColumns.PROCESS_NAMESPACE);
      int locationPathId = aResultSet.getInt(IAeQueueColumns.LOCATION_PATH_ID);
      Date deadline = new Date(aResultSet.getLong(IAeQueueColumns.DEADLINE_MILLIS));
      QName processQName = new QName(processNamespace, processName);
      int groupId = aResultSet.getInt(IAeQueueColumns.GROUP_ID);
      int alarmId = aResultSet.getInt(IAeQueueColumns.ALARM_ID);
      return new AeAlarmExt(processId, locationPathId, groupId, alarmId, deadline, processQName);
   }
}