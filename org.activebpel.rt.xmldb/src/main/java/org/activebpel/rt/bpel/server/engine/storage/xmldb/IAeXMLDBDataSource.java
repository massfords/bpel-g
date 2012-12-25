//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/IAeXMLDBDataSource.java,v 1.2 2008/02/17 21:59:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;


/**
 * An interface that defines the set of methods needed to give access to the XMLDB database.
 */
public interface IAeXMLDBDataSource
{
   /**
    * Gets a new auto-commit Exist connection.
    * 
    */
   public IAeXMLDBConnection getNewConnection() throws AeXMLDBException;

   /**
    * Gets a new Exist connection.
    * 
    */
   public IAeXMLDBConnection getNewConnection(boolean aAutoCommit) throws AeXMLDBException;

   /**
    * Returns the native data source for this database.  The return type 
    * will be implementation dependent.
    */
   public Object getNativeDataSource();

   /**
    * Destroys the data source, cleaning up any resouces associated with it.
    * This is really only useful for testing purposes where the data source is
    * destroyed between each test run and is only implemented for a pooled data 
    * source. 
    */
   public void destroy();
}
