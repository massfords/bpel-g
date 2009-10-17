//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/transaction/sql/AeSQLTransaction.java,v 1.9 2007/09/07 20:52:13 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.transaction.sql;

import java.lang.reflect.InvocationHandler;
import java.sql.Connection;
import java.sql.SQLException;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeConnectionInvocationHandlerFactory;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeConnectionProxyFactory;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeDataSource;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionException;

/**
 * Implements a transaction over a JDBC connection. The <code>begin()</code>
 * sets the transaction as active and the first call to the <code>getConnection</code>
 * method opens a JDBC connection and sets the connection's autocommit setting
 * to <code>false</code>, so that database activities on the connection will
 * occur in a single database transaction. Additionally the <code>getConnection()</code>
 * method returns a proxy for this connection that ignores attempts to close
 * the connection. Ultimately, either <code>commit()</code> or
 * <code>rollback()</code> are called to commit or rollback the database
 * transaction and close the database connection.
 */
public class AeSQLTransaction implements IAeSQLTransaction
{
   
   // TODO (PJ) refactor this class and make it abstract so that common code can be share between SQL and Tamino impls.
   
   /** Base JDBC connection for the active transaction. */
   private Connection mBaseConnection;

   /** Proxy JDBC connection for the active transaction. */
   private Connection mProxyConnection;

   /** True if begin has been called and no commit or rollback. */
   private boolean mActive;

   /**
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction#begin()
    */
   public void begin() throws AeTransactionException
   {
      if (isActive())
      {
         throw new AeTransactionException(AeMessages.getString("AeSQLTransaction.ERROR_0")); //$NON-NLS-1$
      }      
      setActive(true);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction#commit()
    */
   public void commit() throws AeTransactionException
   {
      if (!isActive())
      {
         throw new AeTransactionException(AeMessages.getString("AeSQLTransaction.ERROR_2")); //$NON-NLS-1$
      }

      try
      {
         try
         {
            getBaseConnection().commit();
         }
         finally
         {
            getBaseConnection().close();
         }
      }
      // catching Throwable here because we're using a Proxy in some cases and the exceptions coming from the Proxy won't be SQLExceptions
      catch (Throwable t)
      {
         throw new AeTransactionException(AeMessages.getString("AeSQLTransaction.ERROR_3"), t); //$NON-NLS-1$
      }
      finally
      {
         // Make this transaction inactive.
         setBaseConnection(null);
         setActive(false);
      }
   }

   /**
    * Returns base JDBC connection for the active transaction.
    */
   protected Connection getBaseConnection() throws SQLException
   {

      if(mBaseConnection == null)
         mBaseConnection = getDataSource().getCommitControlConnection();
      return mBaseConnection;
   }

   /**
    * Returns database connection for the active transaction.
    * @see org.activebpel.rt.bpel.server.engine.transaction.sql.IAeSQLTransaction#getConnection()
    */
   public Connection getConnection() throws SQLException
   {
      if (mProxyConnection == null)
      {
         InvocationHandler handler = AeConnectionInvocationHandlerFactory.getInstance().getIgnoreCommitCloseHandler( getBaseConnection() );
         mProxyConnection = AeConnectionProxyFactory.getConnectionProxy(getBaseConnection(), handler);         
      }
      return mProxyConnection;
   }

   /**
    * Returns the <code>DataSource</code> to use for SQL operations.
    */
   protected AeDataSource getDataSource()
   {
      return AeDataSource.MAIN;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction#isActive()
    */
   public boolean isActive()
   {
      return mActive;
   }

   /**
    * Sets the active state to the passed boolean.
    */
   protected void setActive(boolean aActive)
   {
      mActive = aActive;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction#rollback()
    */
   public void rollback() throws AeTransactionException
   {
      if (!isActive())
      {
         throw new AeTransactionException(AeMessages.getString("AeSQLTransaction.ERROR_4")); //$NON-NLS-1$
      }

      try
      {
         try
         {
            getBaseConnection().rollback();
         }
         finally
         {
            getBaseConnection().close();
         }
      }
      // catching Throwable here because we're using a Proxy in some cases and the exceptions coming from the Proxy won't be SQLExceptions
      catch (Throwable t)
      {
         throw new AeTransactionException(AeMessages.getString("AeSQLTransaction.ERROR_3"), t); //$NON-NLS-1$
      }
      finally
      {
         // Make this transaction inactive.
         setBaseConnection(null);
         setActive(false);
      }
   }

   /**
    * Sets base JDBC connection for active transaction.
    */
   protected void setBaseConnection(Connection aBaseConnection)
   {
      mBaseConnection = aBaseConnection;

      // The proxy connection is invalid when the base connection changes.
      setProxyConnection(null);
   }

   /**
    * Sets proxy JDBC connection for active transaction.
    */
   protected void setProxyConnection(Connection aProxyConnection)
   {
      mProxyConnection = aProxyConnection;
   }
   
}

