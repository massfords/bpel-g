//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeMessageReceiverHandler.java,v 1.3 2006/02/10 21:51:13 ewittmann Exp $
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

import org.activebpel.rt.bpel.server.engine.storage.AePersistedMessageReceiver;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLQueueStorageProvider;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * Creates a result set handler that returns an <code>AeSQLMessageReceiver</code> object.
 */
public class AeMessageReceiverHandler implements ResultSetHandler
{
   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public Object handle(ResultSet aResultSet) throws SQLException
   {
      AePersistedMessageReceiver rval = null;
      if (aResultSet.next())
      {
         rval = AeSQLQueueStorageProvider.readSQLMessageReceiver(aResultSet);
      }
      return rval;
   }
}
