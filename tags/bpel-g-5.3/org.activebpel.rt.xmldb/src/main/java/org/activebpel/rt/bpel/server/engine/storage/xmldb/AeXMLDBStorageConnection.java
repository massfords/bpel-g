// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBStorageConnection.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;

/**
 * A XMLDB version of the DB Connection wrapper interface.
 */
public class AeXMLDBStorageConnection implements IAeStorageConnection
{
   /** The XMLDB Connection. */
   private IAeXMLDBConnection mConnection;

   /**
    * Constructs a SQL DB Connection that will delegate to the given SQL Connection.
    * 
    * @param aConnection
    */
   protected AeXMLDBStorageConnection(IAeXMLDBConnection aConnection)
   {
      setConnection(aConnection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection#commit()
    */
   public void commit() throws AeStorageException
   {
      getConnection().commit();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection#rollback()
    */
   public void rollback() throws AeStorageException
   {
      getConnection().rollback();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection#close()
    */
   public void close()
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
    * @param aConnection The connection to set.
    */
   protected void setConnection(IAeXMLDBConnection aConnection)
   {
      mConnection = aConnection;
   }
}
