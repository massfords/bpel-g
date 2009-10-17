//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeSQLProcessInstanceResultSetHandler.java,v 1.4 2007/02/16 20:04:10 rnaylor Exp $
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

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeDbUtils;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeProcessColumns;
import org.apache.commons.dbutils.ResultSetHandler;


/**
 * Implements a <code>ResultSetHandler</code> that converts the next row of
 * a <code>ResultSet</code> to an <code>AeProcessInstanceDetail</code>.
 */
public class AeSQLProcessInstanceResultSetHandler implements ResultSetHandler
{
   /**
    * Converts the current row of the specified <code>ResultSet</code> to an
    * instance of <code>AeProcessInstanceDetail</code>.
    *
    * @param aResultSet
    */
   protected static AeProcessInstanceDetail createProcessInstanceDetail(ResultSet aResultSet) throws SQLException
   {
      // Remember to retrieve columns in left-to-right order for maximum
      // compatibility with JDBC drivers.

      AeProcessInstanceDetail result = new AeProcessInstanceDetail();
      
      populate(result, aResultSet);

      return result;
   }
   
   /**
    * Populates the detail object with data read from the ResultSet. This is broken
    * out as a separate method in order to allow some re-use in the versioning platform.
    * 
    * @param aProcessInstanceDetail
    * @param aResultSet
    * @throws SQLException
    */
   protected static void populate(AeProcessInstanceDetail aProcessInstanceDetail, ResultSet aResultSet) throws SQLException
   {
      long processId = aResultSet.getLong(IAeProcessColumns.PROCESS_ID);
      String namespaceURI = aResultSet.getString(IAeProcessColumns.PROCESS_NAMESPACE);
      String localPart = aResultSet.getString(IAeProcessColumns.PROCESS_NAME);
      QName processName = new QName(namespaceURI, localPart);

      int processState = aResultSet.getInt(IAeProcessColumns.PROCESS_STATE);
      if (aResultSet.wasNull())
      {
         // The process state may be null if the process hasn't been saved yet.
         processState = IAeBusinessProcess.PROCESS_LOADED;
      }

      int processStateReason = aResultSet.getInt(IAeProcessColumns.PROCESS_STATE_REASON);
      Date startDate = AeDbUtils.getDate(aResultSet, IAeProcessColumns.START_DATE);
      Date endDate = AeDbUtils.getDate(aResultSet, IAeProcessColumns.END_DATE);

      if (startDate == null)
      {
         // Always return a non-null start date. The start date will be null
         // if the process hasn't been saved yet.
         startDate = new Date();
      }

      aProcessInstanceDetail.setProcessId(processId);
      aProcessInstanceDetail.setName(processName);
      aProcessInstanceDetail.setState(processState);
      aProcessInstanceDetail.setStateReason(processStateReason);
      aProcessInstanceDetail.setStarted(startDate);
      aProcessInstanceDetail.setEnded(endDate);
   }

   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public Object handle(ResultSet aResultSet) throws SQLException
   {
      return aResultSet.next() ? createProcessInstanceDetail(aResultSet) : null;
   }
}