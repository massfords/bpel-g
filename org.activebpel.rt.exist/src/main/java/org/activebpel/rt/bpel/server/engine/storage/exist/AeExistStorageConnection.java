// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/AeExistStorageConnection.java,v 1.2 2007/08/17 00:59:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.exist;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;

/**
 * A Exist version of the DB Connection wrapper interface.
 */
public class AeExistStorageConnection implements IAeStorageConnection
{
   /** The Exist Connection. */
   private IAeExistConnection mConnection;

   /**
    * Constructs a SQL DB Connection that will delegate to the given SQL Connection.
    * 
    * @param aConnection
    */
   protected AeExistStorageConnection(IAeExistConnection aConnection)
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
   protected IAeExistConnection getConnection()
   {
      return mConnection;
   }

   /**
    * @param aConnection The connection to set.
    */
   protected void setConnection(IAeExistConnection aConnection)
   {
      mConnection = aConnection;
   }
}
