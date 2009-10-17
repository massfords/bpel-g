//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeSQLCoordinatingListResultSetHandler.java,v 1.3 2006/02/24 16:37:30 EWittmann Exp $
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
import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.apache.commons.dbutils.ResultSetHandler;


/**
 * Resultset handler which creates and returns a list of IAeCoordinating objects.
 */
public class AeSQLCoordinatingListResultSetHandler implements ResultSetHandler
{
   /** The coordination manager. */
   private IAeCoordinationManager mManager;

   /**
    * Constructor.
    *
    * @param aManager
    */
   public AeSQLCoordinatingListResultSetHandler(IAeCoordinationManager aManager)
   {
      mManager = aManager;
   }

   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public Object handle(ResultSet aResultSet) throws SQLException
   {
      List results = new ArrayList();
      // Iterate through rows
      while (aResultSet.next())
      {
         results.add(readRow(aResultSet));
      }      
      return results;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeListingResultSetHandler#readRow(java.sql.ResultSet)
    */
   protected Object readRow(ResultSet aResultSet) throws SQLException
   {
      return AeSQLCoordinatingResultSetHandler.createCoordinating(aResultSet, mManager);
   }

}