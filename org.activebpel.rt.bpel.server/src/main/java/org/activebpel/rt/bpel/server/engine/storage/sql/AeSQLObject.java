// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLObject.java,v 1.25 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.apache.commons.dbutils.QueryRunner;
import org.w3c.dom.Document;

/**
 * Base class for objects that access the database.
 */
public class AeSQLObject
{
   /** Executes SQL queries. */
   private final QueryRunner mQueryRunner = new AeQueryRunner(getDataSource());

   /**
    * Returns database connection.
    *
    * @throws AeStorageException
    */
   protected Connection getConnection() throws AeStorageException
   {
      try
      {
         return getDataSource().getConnection();
      }
      catch (SQLException e)
      {
         throw new AeStorageException(AeMessages.getString("AeSQLObject.ERROR_0"), e); //$NON-NLS-1$
      }
   }

   /**
    * Returns a new database connection, outside of the current transaction if
    * one is active.
    *
    * @throws AeStorageException
    */
   protected Connection getNewConnection() throws AeStorageException
   {
      try
      {
         return getDataSource().getNewConnection();
      }
      catch (SQLException e)
      {
         throw new AeStorageException(AeMessages.getString("AeSQLObject.ERROR_0"), e); //$NON-NLS-1$
      }
   }

   /**
    * Accessor for connection with auto-commit flag set to false.
    * Callers must explicitly call commit to persist.
    * @throws AeStorageException
    */
   protected Connection getCommitControlConnection() throws AeStorageException
   {
      return getCommitControlConnection(false);
   }

   /**
    * Accessor for connection with auto-commit flag set to false.
    * Callers must explicitly call commit to persist.
    *
    * @param aVerifyFlag Verify that {@link java.sql.Connection#commit()} or {@link java.sql.Connection#rollback()} is called.
    * @throws AeStorageException
    */
   protected Connection getCommitControlConnection( boolean aVerifyFlag ) throws AeStorageException
   {
      try
      {
         return getDataSource().getCommitControlConnection(aVerifyFlag);
      }
      catch (SQLException e)
      {
         throw new AeStorageException(AeMessages.getString("AeSQLObject.ERROR_1"), e); //$NON-NLS-1$
      }
   }

   /**
    * Accessor for a transaction manager based connection. If the current thread is not
    * participating in a transaction, then a new (normal) connection is return instead
    * of a commit control connection.
    *
    * @throws AeStorageException
    */
   protected Connection getTransactionConnection() throws AeStorageException
   {
      return getTransactionConnection(false, false);
   }

   /**
    * Accessor for a transaction manager based connection. If the current thread is not
    * participating in a transaction, then a either a normal connection or a commit control
    * connection is returned.
    *
    * @param aCommitControlOnFallback if true, returns a commit control connection if the thread is not part of a active transaction.
    * @param aVerifyFlag Verify that {@link java.sql.Connection#commit()} or {@link java.sql.Connection#rollback()} is called for commit control connections.
    * @throws AeStorageException
    */
   protected Connection getTransactionConnection(boolean aCommitControlOnFallback, boolean aVerifyFlag) throws AeStorageException
   {
      try
      {
         return getDataSource().getTransactionManagerConnection(aCommitControlOnFallback, aVerifyFlag);
      }
      catch (SQLException e)
      {
         throw new AeStorageException(AeMessages.getString("AeSQLObject.ERROR_1"), e); //$NON-NLS-1$
      }
   }

   /**
    * Returns database connection directly from container data source.
    *
    */
   protected Connection getContainerManagedConnection() throws AeStorageException
   {
      try
      {
         return getDataSource().getContainerManagedConnection();
      }
      catch (SQLException e)
      {
         throw new AeStorageException(AeMessages.getString("AeSQLObject.ERROR_2"), e); //$NON-NLS-1$
      }
   }

   /**
    * Returns the <code>DataSource</code> to use for SQL operations.
    */
   protected AeDataSource getDataSource()
   {
      return AeDataSource.MAIN;
   }

   /**
    * Returns the <code>QueryRunner</code> to use for queries and updates.
    */
   protected QueryRunner getQueryRunner()
   {
      return mQueryRunner;
   }

   /**
    * Convenience method that returns the date passed in or the sql type null for
    * timestamp.
    *
    * @param aDate
    */
   protected Object getDateOrSqlNull(Date aDate)
   {
      if( aDate == null )
      {
         return AeQueryRunner.NULL_TIMESTAMP;
      }
      return aDate;
   }

   /**
    * Convenience method that returns the date passed as a Long (time in millis) or a
    * NULL_BIGINT if the date is null.
    *
    * @param aDate
    */
   protected Object getDateAsLongOrSqlNull(Date aDate)
   {
      if (aDate == null)
      {
         return AeQueryRunner.NULL_BIGINT;
      }
      return new Long(aDate.getTime());
   }

   /**
    * Convenience method that returns the schema date passed as a Long (time in millis) or a
    * NULL_BIGINT if the schema date is null.
    * @param aSchemaDateTime
    *
    */
   protected Object getDateAsLongOrSqlNull(AeSchemaDateTime aSchemaDateTime)
   {
      return getDateAsLongOrSqlNull(aSchemaDateTime != null? aSchemaDateTime.toDate() : null);
   }

   /**
    * Convenience method that returns the Long or the NULL_BIGINT
    * value.
    *
    * @param aLong
    */
   protected Object getLongOrSqlNull(Long aLong)
   {
      if (aLong == null)
         return AeQueryRunner.NULL_BIGINT;
      return aLong;
   }

   /**
    * Convenience method that returns the String or the NULL_VARCHAR
    * value.
    *
    * @param aString
    */
   protected Object getStringOrSqlNullVarchar(String aString)
   {
      if (aString == null)
         return AeQueryRunner.NULL_VARCHAR;
      return aString;
   }

   /**
    * Gets the Document or the SQL NULL for CLOB
    * @param aDocument
    */
   protected Object getDocumentOrSqlNull(Document aDocument)
   {
      if (aDocument == null)
         return AeQueryRunner.NULL_CLOB;
      return aDocument;
   }

   /**
    * Convenience method that returns the String or the NULL_VARCHAR
    * value.  If the String is null or empty string, then null varchar
    * is returned.
    *
    * @param aString
    */
   protected Object getStringOrSqlNullVarcharNoEmpty(String aString)
   {
      if (AeUtil.isNullOrEmpty(aString))
         return AeQueryRunner.NULL_VARCHAR;
      return aString;
   }
}
