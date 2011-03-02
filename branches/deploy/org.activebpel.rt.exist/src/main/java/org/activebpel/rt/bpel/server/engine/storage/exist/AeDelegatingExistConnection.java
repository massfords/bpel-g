// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/AeDelegatingExistConnection.java,v 1.3 2008/02/17 21:49:22 mford Exp $
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
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;


/**
 * Delegating implementation of an exist connection - delegates all calls
 * to the delegate connection.
 */
public abstract class AeDelegatingExistConnection implements IAeExistConnection
{
   /** The delegate connection. */
   private IAeExistConnection mConnection;

   /**
    * C'tor.
    *
    * @param aConnection
    */
   public AeDelegatingExistConnection(IAeExistConnection aConnection)
   {
      setConnection(aConnection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#insertDocument(java.lang.String)
    */
   public long insertDocument(String aXMLContent) throws AeXMLDBException
   {
      return getConnection().insertDocument(aXMLContent);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#xquery(java.lang.String, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler)
    */
   public Object xquery(String aQuery, IAeXMLDBResponseHandler aResponseHandler) throws AeXMLDBException
   {
      return getConnection().xquery(aQuery, aResponseHandler);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#updateDocuments(java.lang.String)
    */
   public int updateDocuments(String aQuery) throws AeXMLDBException
   {
      return getConnection().updateDocuments(aQuery);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#deleteDocuments(java.lang.String)
    */
   public int deleteDocuments(String aQuery) throws AeXMLDBException
   {
      return getConnection().deleteDocuments(aQuery);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#close()
    */
   public void close()
   {
      getConnection().close();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#rollback()
    */
   public void rollback() throws AeXMLDBException
   {
      getConnection().rollback();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#commit()
    */
   public void commit() throws AeXMLDBException
   {
      getConnection().commit();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#getNativeConnection()
    */
   public Object getNativeConnection()
   {
      return getConnection().getNativeConnection();
   }

   /**
    * @return Returns the connection.
    */
   protected IAeExistConnection getConnection()
   {
      return mConnection;
   }

   /**
    * @param aConnection the connection to set
    */
   protected void setConnection(IAeExistConnection aConnection)
   {
      mConnection = aConnection;
   }
}
