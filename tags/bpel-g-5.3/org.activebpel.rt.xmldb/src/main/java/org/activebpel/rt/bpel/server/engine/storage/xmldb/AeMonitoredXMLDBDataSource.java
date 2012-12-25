//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeMonitoredXMLDBDataSource.java,v 1.2 2008/02/17 21:59:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb; 

import org.activebpel.rt.bpel.IAeMonitorListener;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Reports how long it took to acquire a connection
 */
public class AeMonitoredXMLDBDataSource implements IAeXMLDBDataSource
{
   /** source that we delegate to */
   private IAeXMLDBDataSource mDelegate;
   
   /**
    * Ctor
    * @param aSource
    */
   public AeMonitoredXMLDBDataSource(IAeXMLDBDataSource aSource)
   {
      setDelegate(aSource);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource#getNativeDataSource()
    */
   public Object getNativeDataSource()
   {
      return getDelegate().getNativeDataSource();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource#destroy()
    */
   public void destroy()
   {
      getDelegate().destroy();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource#getNewConnection()
    */
   public IAeXMLDBConnection getNewConnection() throws AeXMLDBException
   {
      long time = System.currentTimeMillis();
      IAeXMLDBConnection conn = getDelegate().getNewConnection();
      reportTime(time);
      return conn;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource#getNewConnection(boolean)
    */
   public IAeXMLDBConnection getNewConnection(boolean aAutoCommit) throws AeXMLDBException
   {
      long time = System.currentTimeMillis();
      IAeXMLDBConnection conn = getDelegate().getNewConnection(aAutoCommit);
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

   /**
    * @return the delegate
    */
   protected IAeXMLDBDataSource getDelegate()
   {
      return mDelegate;
   }

   /**
    * @param aDelegate the delegate to set
    */
   protected void setDelegate(IAeXMLDBDataSource aDelegate)
   {
      mDelegate = aDelegate;
   }
}
 