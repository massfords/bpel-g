//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeTransmissionTrackerResultSetHandler.java,v 1.1 2006/05/24 23:16:35 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.activebpel.rt.bpel.server.engine.storage.sql.IAeTransmissionTrackerColumns;
import org.activebpel.rt.bpel.server.transreceive.AeTransmissionTrackerEntry;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * Creates a <code>AeTransmissionTrackerEntry</code> from the SQL result set.
 */
public class AeTransmissionTrackerResultSetHandler implements ResultSetHandler
{
   /**
    * 
    * Overrides method to create and return a AeTransmissionTrackerEntry object.
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public Object handle(ResultSet aResultSet) throws SQLException
   {
      AeTransmissionTrackerEntry rval = null;
      if (aResultSet.next())
      {         
         int state = aResultSet.getInt(IAeTransmissionTrackerColumns.STATE);
         long transmissionId = aResultSet.getLong(IAeTransmissionTrackerColumns.TRANSMISSION_ID);
         String messageId = aResultSet.getString(IAeTransmissionTrackerColumns.MESSAGE_ID);   
         if (aResultSet.wasNull())
         {
            messageId = null;
         }
         rval = new AeTransmissionTrackerEntry(transmissionId, state, messageId);
      }
      return rval;   
   }

}
