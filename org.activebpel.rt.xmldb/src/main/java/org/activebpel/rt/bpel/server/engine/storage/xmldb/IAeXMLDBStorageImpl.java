// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/IAeXMLDBStorageImpl.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import java.io.InputStream;
import java.io.Reader;

/**
 * Instances of this interface must implement the parts of the XMLDB storage
 * framework that are implementation specific.
 */
public interface IAeXMLDBStorageImpl
{
   /**
    * Gets the data source for the xml database.
    */
   public IAeXMLDBDataSource getXMLDBDataSource();

   /**
    * Inserts a document into the XML database.
    * 
    * @param aReader
    * @param aConnection
    */
   public long insertDocument(Reader aReader, IAeXMLDBConnection aConnection) throws AeXMLDBException;

   /**
    * Insert the non-xml content in the xml database.
    * 
    * @param aInputStream
    * @param aConnection
    */
   public long insertNonXMLDocument(InputStream aInputStream, IAeXMLDBConnection aConnection)
         throws AeXMLDBException;

   /**
    * Runs the xquery and returns the result.
    * 
    * @param aQuery
    * @param aResponseHandler
    * @param aConnection
    */
   public Object xquery(String aQuery, IAeXMLDBResponseHandler aResponseHandler,
         IAeXMLDBConnection aConnection) throws AeXMLDBException;

   /**
    * Retrieves the non-xml content from the xml database.
    * 
    * @param aDocumentId
    * @param aConnection
    */
   public InputStream retrieveNonXMLDocument(long aDocumentId, IAeXMLDBConnection aConnection)
         throws AeXMLDBException;

   /**
    * Updates the documents in the XML database using the given XQuery and
    * returns the number of documents modified.
    * 
    * @param aQuery
    * @param aConnection
    */
   public int updateDocuments(String aQuery, IAeXMLDBConnection aConnection) throws AeXMLDBException;

   /**
    * Deletes the documents matching the given xquery. The aDeleteDocType param
    * is a string representing the doc type we are deleting. Even if multiple
    * doc types are being deleted, only a count of documents with this name will
    * be returned. Not all XML databases will use this information.
    * 
    * @param aQuery
    * @param aDeleteDocType
    * @param aConnection
    */
   public int deleteDocuments(String aQuery, String aDeleteDocType, IAeXMLDBConnection aConnection)
         throws AeXMLDBException;

   /**
    * Delets the non-xml content with the given ID from the xml database.
    * 
    * @param aDocumentId
    * @param aConnection
    */
   public void deleteNonXMLDocument(long aDocumentId, IAeXMLDBConnection aConnection) throws AeXMLDBException;
}
