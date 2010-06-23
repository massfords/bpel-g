//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeAlarmListQueryHandler.java,v 1.3 2006/09/18 17:59:53 PJayanetti Exp $
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
import java.util.LinkedList;

import org.activebpel.rt.bpel.server.engine.storage.AePersistedAlarm;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeQueueColumns;
import org.apache.commons.dbutils.ResultSetHandler;


/**
 * Implements a query handler that can transform a result set into a list of
 * persisted alarm objects.
 */
public class AeAlarmListQueryHandler implements ResultSetHandler
{
   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public Object handle(ResultSet aResultSet) throws SQLException
   {
      LinkedList list = new LinkedList();
      while (aResultSet.next())
      {
         long processId = aResultSet.getLong(IAeQueueColumns.PROCESS_ID);
         int locationPathId = aResultSet.getInt(IAeQueueColumns.LOCATION_PATH_ID);
         Date deadline = new Date(aResultSet.getLong(IAeQueueColumns.DEADLINE_MILLIS));
         int groupId = aResultSet.getInt(IAeQueueColumns.GROUP_ID);
         int alarmId = aResultSet.getInt(IAeQueueColumns.ALARM_ID);
         list.add(new AePersistedAlarm(processId, locationPathId, deadline, groupId, alarmId));
      }

      return list;
   }
}