// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/IAeExistConnection.java,v 1.4 2008/02/17 21:49:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.exist;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;


/**
 * Connection to the eXist database, similar to a JDBC or TConnection.
 */
public interface IAeExistConnection extends IAeXMLDBConnection
{
   /**
    * Insert document content into the eXist database.
    * 
    * @param aXMLContent
    * @throws AeXMLDBException
    */
   public long insertDocument(String aXMLContent) throws AeXMLDBException;

   /**
    * Query the database using the given XQuery.
    * 
    * @param aQuery
    * @param aResponseHandler
    */
   public Object xquery(String aQuery, IAeXMLDBResponseHandler aResponseHandler) throws AeXMLDBException;

   /**
    * Update some documents using the given XQuery.
    * 
    * @param aQuery
    */
   public int updateDocuments(String aQuery) throws AeXMLDBException;

   /**
    * Delete some documents using the given XQuery.
    * 
    * @param aQuery
    */
   public int deleteDocuments(String aQuery) throws AeXMLDBException;
   
   /**
    * Close the connection.
    * 
    */
   public void close();
   
   /**
    * Commit the 'transaction'.
    * 
    * @throws AeXMLDBException
    */
   public void commit() throws AeXMLDBException;
   
   /**
    * Roll back the transaction.
    * 
    * @throws AeXMLDBException
    */
   public void rollback() throws AeXMLDBException;
}
