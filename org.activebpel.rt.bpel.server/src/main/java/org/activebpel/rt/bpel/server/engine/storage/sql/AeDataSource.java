// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeDataSource.java,v 1.23 2008/04/01 13:44:06 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionException;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager;
import org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction;
import org.activebpel.rt.bpel.server.engine.transaction.sql.IAeSQLTransaction;

/**
 * Convenience class to centralize access to JDBC data sources. There is a
 * static member (e.g., <code>MAIN</code>) for each data source in the system.
 * Each such instance is a subclass of <code>AeDataSource</code> that
 * constructs the real JDBC data source as needed.
 */
public abstract class AeDataSource implements DataSource
{
   /** Delegates the main data source. */
   public static AeDataSource MAIN = null;

   /** The name of this data source. */
   private final String mName;

   /** The SQL configuration. */
   private final AeSQLConfig mSQLConfig;

   /** The underlying delegate JDBC data source. */
   private DataSource mDelegate;

   /** Whether to set transaction isolation level on connections. */
   private boolean mSetTransactionIsolationLevel;

   /**
    * Constructor.
    *
    * @param aName
    * @param aSQLConfig The SQL configuration.
    */
   protected AeDataSource(String aName, AeSQLConfig aSQLConfig)
   {
      mName = aName;
      mSQLConfig = aSQLConfig;

      // set derby home in case we want to use embedded driver
      setupDerbySystemHomeProperty();

      // Get the transaction isolation level parameter once when we initialize
      // the data source instead of every time we get a connection.
      try
      {
         mSetTransactionIsolationLevel = mSQLConfig.getParameterBoolean(
            AeSQLConfig.PARAMETER_SET_TRANSACTION_ISOLATION_LEVEL
         );
      }
      catch (AeStorageException e)
      {
         AeException.logError(e, AeMessages.getString("AeDataSource.ERROR_0")); //$NON-NLS-1$

         // As of 2/14/2006, the default value is false, because we no longer
         // need to override the default transaction isolation level to avoid
         // deadlocks.
         mSetTransactionIsolationLevel = false;
      }
   }

   /**
    * Configures the transaction isolation level (and possibly other settings)
    * for the specified connection.
    *
    * @param aConnection
    * @throws SQLException
    */
   protected Connection configureConnection(Connection aConnection) throws SQLException
   {
      if (getSetTransactionIsolationLevel())
      {
         boolean wasAutoCommit = aConnection.getAutoCommit();

         // Per ckeller: We don't need a higher transaction isolation level,
         // because we implement much of our own concurrency control.
         // Furthermore, this level eliminates deadlock problems that we have
         // with SQL Server.
         //
         // However, Oracle does not support read uncommitted, which is why we
         // skip this if the database type is oracle.
         aConnection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

         // setTransactionIsolation() has been known to disable auto-commit
         // (which should be enabled by default). However, we can't blithely
         // call setAutoCommit() unless we're reasonably certain that we're not
         // running in a container-managed transaction. So, call
         // setAutoCommit() only if setTransactionIsolation() actually changed
         // auto-commit from its default setting.
         if (wasAutoCommit && !aConnection.getAutoCommit())
         {
            aConnection.setAutoCommit(true);
         }
      }

      return aConnection;
   }
   
   /**
    * This method will ALWAYS get a new connection irrespective of whether or not
    * there is a transaction in place.
    * 
    * @return Gets a new connection from the delegate DataSource
    * @throws SQLException
    */
   protected Connection getNewConnection() throws SQLException
   {
      return configureConnection(getDelegate().getConnection());
   }

   /**
    * Creates the underlying JDBC data source.
    *
    * @return DataSource The datasource we will be using to connect to storage through.
    * @throws AeStorageException
    */
   protected abstract DataSource createDelegate() throws AeStorageException;

   /**
    * Returns connection with auto-commit disabled. Callers must explicitly
    * call {@link java.sql.Connection#commit} to persist.
    */
   public Connection getCommitControlConnection() throws SQLException
   {
      return getCommitControlConnection(false);
   }

   /**
    * Returns connection with auto-commit disabled. Callers must explicitly
    * call {@link java.sql.Connection#commit} to persist.
    *
    * @param aVerifyFlag Verify that {@link java.sql.Connection#commit} or {@link java.sql.Connection#rollback()} is called.
    */
   public Connection getCommitControlConnection( boolean aVerifyFlag ) throws SQLException
   {
      Connection connection = getConnection();
      connection.setAutoCommit(false);

      // Wrap the connection with a proxy that restores auto-commit setting to
      // default value (true) on close() to account for connection pool
      // implementations that don't do so automatically.
      InvocationHandler handler = AeConnectionInvocationHandlerFactory.getInstance().getCommitControlHandler(connection, aVerifyFlag);
      connection = AeConnectionProxyFactory.getConnectionProxy(connection, handler);
      return connection;
   }

   /**
    * Returns a connection from the transaction manager if the current thread is already participating
    * in a transaction. Otherwise returns a new connection or a commit control connection.
    * @param aCommitControlOnFallback if the current thread is not participating in a transaction, then return a commit control connection instead of a new connection.
    * @param aVerifyFlag Verify that {@link java.sql.Connection#commit()} or {@link java.sql.Connection#rollback()} is called for commit control connections.
    * @throws SQLException
    */
   public Connection getTransactionManagerConnection(boolean aCommitControlOnFallback,  boolean aVerifyFlag) throws SQLException
   {
      IAeTransaction transaction = getTransaction();

      // If the current thread has started a transaction in the transaction
      // manager, then return the transaction's database connection.
      if (transaction.isActive())
      {
         if (!(transaction instanceof IAeSQLTransaction))
         {
            throw new SQLException(AeMessages.getString("AeDataSource.ERROR_5")); //$NON-NLS-1$
         }

         return ((IAeSQLTransaction) transaction).getConnection();
      }
      // since the current thread is not part of an active transaction, then
      // return either a commit control connection or a normal connection.
      if (aCommitControlOnFallback)
      {
         return getCommitControlConnection(aVerifyFlag);
      }
      else
      {
         return getConnection();
      }
   }
   
   /**
    * Convenience method to return transaction for current thread, translating
    * <code>AeTransactionException</code> to <code>SQLException</code>.
    */
   protected IAeTransaction getTransaction() throws SQLException
   {
      try
      {
         return AeTransactionManager.getInstance().getTransaction();
      }
      catch (AeTransactionException e)
      {
         String message = AeMessages.getString("AeDataSource.ERROR_3"); //$NON-NLS-1$

         if (e.getLocalizedMessage() != null)
         {
            message += ": " + e.getLocalizedMessage(); //$NON-NLS-1$
         }

         throw new SQLException(message);
      }
   }   
   
   /**
    * Returns a connection directly from the container data source.
    *
    * @throws SQLException
    */
   public Connection getContainerManagedConnection() throws SQLException
   {
      return getDelegate().getConnection();
   }

   /**
    * Returns a connection directly from the container data source.
    *
    * @throws SQLException
    */
   public Connection getContainerManagedConnection(String aUsername, String aPassword) throws SQLException
   {
      return getDelegate().getConnection(aUsername, aPassword);
   }

   /**
    * Returns the underlying delegate JDBC data source.
    *
    * @return DataSource
    * @throws SQLException
    */
   protected final DataSource getDelegate() throws SQLException
   {
      if (mDelegate == null)
      {
         try
         {
            mDelegate = new AeMonitoredDataSource(createDelegate());
         }
         catch (AeStorageException e)
         {
            String message = AeMessages.getString("AeDataSource.ERROR_1"); //$NON-NLS-1$
            if(e.getLocalizedMessage() != null)
               message += ": " + e.getLocalizedMessage(); //$NON-NLS-1$
            throw new SQLException(message);
         }
      }

      return mDelegate;
   }

   /**
    * Returns the name of this data source object.
    */
   public String getName()
   {
      return mName;
   }

   /**
    * Returns value of parameter that controls whether to set transaction
    * isolation level on connections. The default is <code>true</code>, which
    * is overridden for Oracle, because Oracle does not support the read
    * uncommitted transaction isolation level.
    */
   public boolean getSetTransactionIsolationLevel()
   {
      return mSetTransactionIsolationLevel;
   }

   /**
    * Returns the SQL configuration.
    */
   public AeSQLConfig getSQLConfig()
   {
      return mSQLConfig;
   }

   /*======================================================================
    * javax.sql.DataSource methods
    *======================================================================
    */

   /**
    * @see javax.sql.DataSource#getConnection()
    */
   public Connection getConnection() throws SQLException
   {
      return getNewConnection();
   }

   /**
    * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
    */
   public Connection getConnection(String aUsername, String aPassword) throws SQLException
   {
      return configureConnection(getDelegate().getConnection(aUsername, aPassword));
   }

   /**
    * @see javax.sql.DataSource#getLoginTimeout()
    */
   public int getLoginTimeout() throws SQLException
   {
      return getDelegate().getLoginTimeout();
   }

   /**
    * @see javax.sql.DataSource#getLogWriter()
    */
   public PrintWriter getLogWriter() throws SQLException
   {
      return getDelegate().getLogWriter();
   }

   /**
    * @see javax.sql.DataSource#setLoginTimeout(int)
    */
   public void setLoginTimeout(int aSeconds) throws SQLException
   {
      getDelegate().setLoginTimeout(aSeconds);
   }

   /**
    * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
    */
   public void setLogWriter(PrintWriter aWriter) throws SQLException
   {
      getDelegate().setLogWriter(aWriter);
   }

   /**
    * This sets the derby.system.home system property if it has not been set.  This
    * allows users to use relative database locations in their embedded derby connection url.
    * Note this will be set to catalina.home if it is not already set and catalina.home
    * is available. 
    */
   protected void setupDerbySystemHomeProperty()
   {
      // used for derby system home, for database locations
      String DERBY = "derby"; //$NON-NLS-1$

      // if we are using derby make sure that we set the derby system home if it isn't set already
      if(DERBY.equals( getSQLConfig().getDatabaseType() ) )
      {
         // used for derby system home, for database locations
         String DERBY_SYSTEM_HOME = "derby.system.home"; //$NON-NLS-1$
         // We can use embedded derby with tomcat, so catalina.home will get us the installation directory
         String CATALINA_HOME = "catalina.home"; //$NON-NLS-1$
         
         String derbyHome = System.getProperty(DERBY_SYSTEM_HOME);
         if(derbyHome == null)
         {
            String catalinaHome = System.getProperty(CATALINA_HOME);
            if(catalinaHome != null)
               System.setProperty(DERBY_SYSTEM_HOME, catalinaHome);
         }
      }
   }

}
