//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeMonitoredDataSource.java,v 1.1 2007/08/13 17:57:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql; 

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.activebpel.rt.bpel.IAeMonitorListener;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Wraps a DataSource in order to monitor how long it takes to get a connection. 
 */
public class AeMonitoredDataSource extends AeDelegatingDataSource
{
   /**
    * Ctor accepts delegate
    * @param aDelegate
    */
   public AeMonitoredDataSource(DataSource aDelegate)
   {
      super(aDelegate);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeDelegatingDataSource#getConnection()
    */
   public Connection getConnection() throws SQLException
   {
      long time = System.currentTimeMillis();
      Connection conn = super.getConnection();
      reportTime(time);
      return conn;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeDelegatingDataSource#getConnection(java.lang.String, java.lang.String)
    */
   public Connection getConnection(String aUsername, String aPassword) throws SQLException
   {
      long time = System.currentTimeMillis();
      Connection conn = super.getConnection(aUsername, aPassword);
      reportTime(time);
      return conn;
   }
   
   /**
    * Reports how long it took to get a connection.
    * @param aStartTime
    */
   protected void reportTime(long aStartTime)
   {
      long durationInMillis = System.currentTimeMillis() - aStartTime;
      IAeBusinessProcessEngineInternal engine = AeEngineFactory.getEngine();
      // engine could be null during startup only. At that point, we hit the db in order to verify the storage setup.
      if (engine != null)
         engine.fireMonitorEvent(IAeMonitorListener.MONITOR_DB_CONN_TIME, durationInMillis);
   }
}
 