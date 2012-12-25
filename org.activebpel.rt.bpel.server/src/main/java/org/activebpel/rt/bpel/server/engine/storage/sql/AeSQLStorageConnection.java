// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.util.AeCloser;

/**
 * A SQL implementation of the delegating DB connection interface.  This class is created with 
 * a SQL connection that it will delegate its calls to.
 */
public class AeSQLStorageConnection implements IAeStorageConnection
{
   /** The SQL Connection. */
   private Connection mConnection;

   /**
    * Constructs a SQL DB Connection that will delegate to the given SQL Connection.
    * 
    * @param aConnection
    */
   public AeSQLStorageConnection(Connection aConnection)
   {
      setConnection(aConnection);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection#commit()
    */
   public void commit() throws AeStorageException
   {
      try
      {
         getConnection().commit();
      }
      catch (SQLException ex)
      {
         throw new AeStorageException(ex);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection#rollback()
    */
   public void rollback() throws AeStorageException
   {
      try
      {
         getConnection().rollback();
      }
      catch (SQLException ex)
      {
         throw new AeStorageException(ex);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection#close()
    */
   public void close()
   {
      AeCloser.close(getConnection());
   }

   /**
    * @return Returns the connection.
    */
   public Connection getConnection()
   {
      return mConnection;
   }

   /**
    * @param aConnection The connection to set.
    */
   protected void setConnection(Connection aConnection)
   {
      mConnection = aConnection;
   }

}
