//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeMessageReceiverListHandler.java,v 1.2 2006/02/10 21:51:13 ewittmann Exp $
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
import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLQueueStorageProvider;

/**
 * Creates a result set handler that returns a list of matching AeMessageReceivers.
 * Has the ability to filter the selected receivers based on the filter criteria.
 */
public class AeMessageReceiverListHandler extends AeListingResultSetHandler
{
   /**
    * Default constructor - uses a null message receiver filter.
    */
   public AeMessageReceiverListHandler()
   {
      super(AeMessageReceiverFilter.NULL_FILTER);
   }
   
   /**
    * Constructor.
    * 
    * @param aFilter The selection criteria.
    */
   public AeMessageReceiverListHandler(AeMessageReceiverFilter aFilter)
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
      return AeSQLQueueStorageProvider.readSQLMessageReceiver(aResultSet);
   }
}
