//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeConnectionInvocationHandlerFactory.java,v 1.4 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * Provides factory access to <code>InvocationHandler</code> impls.
 */
public class AeConnectionInvocationHandlerFactory
{
   // internal constants for connection methods
   private static final String CLOSE    = "close"   ; //$NON-NLS-1$
   private static final String COMMIT   = "commit"  ; //$NON-NLS-1$
   private static final String ROLLBACK = "rollback"; //$NON-NLS-1$

   /** Singleton instance. */
   private static final AeConnectionInvocationHandlerFactory sInstance = new AeConnectionInvocationHandlerFactory();

   /**
    * The {@link java.lang.reflect.Method} instance representing the {@link
    * java.sql.Connection#close} method.
    */
   protected Method mCloseMethod;

   /**
    * The {@link java.lang.reflect.Method} instance representing the {@link
    * java.sql.Connection#commit} method.
    */
   protected Method mCommitMethod;

   /**
    * The {@link java.lang.reflect.Method} instance representing the {@link
    * java.sql.Connection#rollback()} method.
    */
   protected Method mRollbackMethod;
   
   /**
    * Returns singleton instance of the invocation handler factory.
    */
   public static AeConnectionInvocationHandlerFactory getInstance()
   {
      return sInstance;
   }
   
   /**
    * Return the <code>InvocationHandler</code> implementation for the given
    * JDBC connection.  Minimally, the handler will ensure that the autoCommit
    * property is set to true before {@link java.sql.Connection#close} is called.
    */
   public InvocationHandler getAutoCommitHandler( Connection aConnection )
   {
      return new AeRestoreAutoCommitHandler( aConnection );
   }
   
   /**
    * Return the <code>InvocationHandler</code> implementation for the given
    * JDBC connection.  Minimally, the handler will ensure that the autoCommit
    * property is set to true before {@link java.sql.Connection#close} is called.
    * If the verify flag is true, then the handler will verify that either
    * {@link java.sql.Connection#commit} or {@link java.sql.Connection#rollback()}
    * have been called before the connection is closed.
    * @param aConnection
    * @param aVerifyFlag
    */
   public InvocationHandler getCommitControlHandler( Connection aConnection, boolean aVerifyFlag )
   {
      if( aVerifyFlag )
      {
         return new AeVerifyCommitOrRollbackInvocationHandler( aConnection );
      }
      else
      {
         return new AeRestoreAutoCommitHandler( aConnection );
      }
   }
   
   /**
    * Return the <code>InvocationHandler</code> implementation for the given
    * JDBC connection. This invocation handler passes all method calls
    * except <code>commit(), rollback() and close()</code> to an underlying JDBC connection.
    */
   public InvocationHandler getIgnoreCommitCloseHandler( Connection aConnection )
   {
      return new AeIgnoreCommitCloseConnectionProxyInvocationHandler( aConnection );
   }   
   
   /**
    * Returns the {@link java.lang.reflect.Method} instance representing the
    * {@link java.sql.Connection#close} method.
    */
   protected Method getCloseMethod()
   {
      if (mCloseMethod == null)
      {
         mCloseMethod = getConnectionMethod(CLOSE);
      }

      return mCloseMethod;
   }

   /**
    * Returns the {@link java.lang.reflect.Method} instance representing the
    * {@link java.sql.Connection#commit} method.
    */
   protected Method getCommitMethod()
   {
      if (mCommitMethod == null)
      {
         mCommitMethod = getConnectionMethod(COMMIT);
      }

      return mCommitMethod;
   }
   
   /**
    * Returns the {@link java.lang.reflect.Method} instance representing the
    * {@link java.sql.Connection#rollback()} method.
    */
   protected Method getRollbackMethod()
   {
      if (mRollbackMethod == null)
      {
         mRollbackMethod = getConnectionMethod(ROLLBACK);
      }

      return mRollbackMethod;
   }

   /**
    * Returns the {@link java.lang.reflect.Method} instance representing the
    * specified method in the {@link java.sql.Connection} interface.
    */
   protected Method getConnectionMethod(String aName)
   {
      try
      {
         return Connection.class.getMethod(aName, (Class[])null);
      }
      catch (SecurityException | NoSuchMethodException e)
      {
         // This shouldn't happen, but if it does something is seriously wrong.
         throw new RuntimeException(MessageFormat.format(AeMessages.getString("AeVerifyCommitOrRollbackConnectionProxyFactory.ERROR_1"), //$NON-NLS-1$
                 aName), e);
      }
   }

   /**
    * Implements a method invocation handler that passes all method calls to an
    * underlying JDBC connection but ensures that  {@link
    * java.sql.Connection#setAutoCommit} is set to true before 
    * {@link java.sql.Connection#close} is called.
    */
   private class AeRestoreAutoCommitHandler implements InvocationHandler
   {
      
      /** The underlying JDBC connection. */
      private final Connection mConnection;
            
      /**
       * Constructor.
       * @param aConnection
       */
      public AeRestoreAutoCommitHandler( Connection aConnection )
      {
         mConnection = aConnection;
      }
      
      /**
       * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
       */
      public Object invoke(Object aProxy, Method aMethod, Object[] args) throws Throwable
      {
         if( aMethod.equals( getCloseMethod() ) )
         {
            setAutoCommitToTrue();
         }
         return aMethod.invoke(getConnection(), args);
      }
      
      /**
       * Ensure that autoCommit is set to true.
       */
      protected void setAutoCommitToTrue() throws SQLException
      {
         getConnection().setAutoCommit( true );
      }
      
      /**
       * Returns the underlying JDBC connection.
       */
      protected Connection getConnection()
      {
         return mConnection;
      }
   }
   
   /**
    * Implements a method invocation handler that passes all method calls to an
    * underlying JDBC connection but verifies that either {@link
    * java.sql.Connection#commit} or {@link java.sql.Connection#rollback} is
    * called before {@link java.sql.Connection#close}.
    */
   private class AeVerifyCommitOrRollbackInvocationHandler extends AeRestoreAutoCommitHandler
   {
      /**
       * Flag that records whether it is okay to close the connection without
       * an error message.
       */
      private boolean mIsOkayToClose;

      /**
       * Constructs the method invocation handler for the specified JDBC
       * connection.
       */
      public AeVerifyCommitOrRollbackInvocationHandler(Connection aConnection)
      {
         super( aConnection );
      }

      /**
       * Invokes the specified method on the underlying JDBC connection.
       *
       * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
       */
      public Object invoke(Object aProxy, Method aMethod, Object[] args) throws Throwable
      {
         if (aMethod.equals(getCloseMethod()))
         {
            if (!mIsOkayToClose)
            {
               new AeStorageException(AeMessages.getString("AeVerifyCommitOrRollbackConnectionProxyFactory.ERROR_3")).logError(); //$NON-NLS-1$
            }
         }
         else if (aMethod.equals(getCommitMethod()))
         {
            mIsOkayToClose = true;
         }
         else if (aMethod.equals(getRollbackMethod()))
         {
            mIsOkayToClose = true;
         }
         
         return super.invoke(aProxy, aMethod, args);
      }
   }
   
   /**
    * Implements a method invocation handler that passes all method calls
    * except <code>commit()</code> , <code>rollback()</code>  and <code>close()</code> to an underlying JDBC connection.
    */
   private class AeIgnoreCommitCloseConnectionProxyInvocationHandler extends AeRestoreAutoCommitHandler
   {
      /**
       * Constructs the method invocation handler for the specified JDBC
       * connection.
       */
      public AeIgnoreCommitCloseConnectionProxyInvocationHandler(Connection aConnection)
      {
         super( aConnection );
      }
      /**
       * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
       */
      public Object invoke(Object aProxy, Method aMethod, Object[] args) throws Throwable
      {
         if( aMethod.equals( getCloseMethod() ) )        
         {
            // ignore close
            return null;
         }
         else if( aMethod.equals( getRollbackMethod() ) || aMethod.equals( getCommitMethod() ) )        
         {
            // cannot nest transactions.
            throw new AeStorageException(AeMessages.getString("AeSQLTransaction.ERROR_0")); //$NON-NLS-1$
         }
         return aMethod.invoke(getConnection(), args);
      }      
   }
}
