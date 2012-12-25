//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/tx/AeTxManagerXMLDBConnection.java,v 1.2 2008/02/17 21:59:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.tx;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection;
import org.activebpel.rt.xmldb.AeMessages;

/**
 * A delegate version of a xmldb local tx connection where the close, commit and rollback methods are
 * ignored. The connection is really closed on commit or rollback operations initiated by the
 * TransactionManager.
 */
public class AeTxManagerXMLDBConnection implements IAeXMLDBConnection
{
   /** XML DB connection. */
   private IAeXMLDBConnection mConnection;

   /**
    * Default ctor.
    *
    * @param aConnection
    * @throws AeXMLDBException
    */
   public AeTxManagerXMLDBConnection(IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      setConnection(aConnection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#getNativeConnection()
    */
   public Object getNativeConnection()
   {
      return getConnection().getNativeConnection();
   }

   /**
    * Overrides method to do nothing.
    *
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#close()
    */
   public void close()
   {
      // ignore!
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#rollback()
    */
   public void rollback() throws AeXMLDBException
   {
      throw new AeXMLDBException(AeMessages.getString("AeXMLDBTransaction.ERROR_CANNOT_NEST_TRANSACTIONS")); //$NON-NLS-1$)
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#commit()
    */
   public void commit() throws AeXMLDBException
   {
      throw new AeXMLDBException(AeMessages.getString("AeXMLDBTransaction.ERROR_CANNOT_NEST_TRANSACTIONS")); //$NON-NLS-1$)
   }

   /**
    * Rolls back the underlying connection.
    * @throws AeXMLDBException
    */
   public void reallyRollback() throws AeXMLDBException
   {
      getConnection().rollback();
   }

   /**
    * Commits the underlying connection.
    * @throws AeXMLDBException
    */
   public void reallyCommit() throws AeXMLDBException
   {
      getConnection().commit();
   }

   /**
    * Closes the underlying connection.
    */
   public void reallyClose()
   {
      getConnection().close();
   }

   /**
    * @return Returns the connection.
    */
   protected IAeXMLDBConnection getConnection()
   {
      return mConnection;
   }

   /**
    * @param aConnection the connection to set
    */
   protected void setConnection(IAeXMLDBConnection aConnection)
   {
      mConnection = aConnection;
   }
}
