// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLCounterStore.java,v 1.5 2006/12/15 20:10:58 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeCounterStore;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;

/**
 * Implements persistent counter store in the database.
 */
public class AeSQLCounterStore extends AeSQLObject implements IAeCounterStore
{
   private static final String SQL_GET_COUNTER_VALUE    = "GetCounterValue"; //$NON-NLS-1$
   private static final String SQL_UPDATE_COUNTER_VALUE = "UpdateCounterValue"; //$NON-NLS-1$
   private static final String SQL_INSERT_COUNTER       = "InsertCounter"; //$NON-NLS-1$

   private static final int DEADLOCK_TRY_COUNT = 5;

   /**
    * Default constructor.
    */
   public AeSQLCounterStore()
   {
      this(Collections.EMPTY_MAP);
   }

   /**
    * Constructs persistent source for counter values.
    *
    * @param aConfig The configuration map.
    */
   public AeSQLCounterStore(Map aConfig)
   {
   }

   /**
    * Returns number of times to retry deadlocked transactions.
    */
   protected int getDeadlockTryCount()
   {
      return DEADLOCK_TRY_COUNT;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCounterStore#getNextValues(java.lang.String, int)
    */
   public long getNextValues(String aCounterName, int aBlockSize) throws AeStorageException
   {
      int tryCount = getDeadlockTryCount();
      SQLException firstException = null;

      for (int tries = 0; true; )
      {
         try
         {
            // We're getting a new connection here since we could already be in 
            // the middle of a transaction and we don't want the current transaction
            // to commit for the sake of the counter increment. 
            Connection connection = getCommitControlConnection();

            try
            {
               if (getDataSource().getSetTransactionIsolationLevel())
               {
                  // Counters require a higher isolation level than the default
                  // level of TRANSACTION_READ_UNCOMMITTED that we set in
                  // AeDataSource.
                  //
                  // However, not only is this not necessary for Oracle, but
                  // also trying to set the transaction isolation level at this
                  // point fails with the Oracle driver, which is why we skip
                  // this if the database type is oracle.
                  connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
               }

               // Update this counter in the database to the end of a new block of values.
               if (!updateCounter(connection, aCounterName, aBlockSize))
               {
                  createCounter(connection, aCounterName, aBlockSize);
               }

               // Return first new value in block.
               long result = getValue(connection, aCounterName) - aBlockSize + 1;

               connection.commit();
               return result;
            }
            finally
            {
               AeCloser.close(connection);
            }
         }
         catch (SQLException e)
         {
            // Retry if we haven't exhausted the try count.
            if (++tries < tryCount)
            {
               if (firstException == null)
               {
                  firstException = e;
               }

               AeException.logError(null, AeMessages.format("AeSQLCounterStore.ERROR_RetryGetNextValues", aCounterName)); //$NON-NLS-1$
            }
            // Otherwise, we're done.
            else
            {
               if (firstException != null)
               {
                  AeException.logError(firstException, AeMessages.format("AeSQLCounterStore.ERROR_GetNextValuesFirstException", aCounterName)); //$NON-NLS-1$
               }

               throw new AeStorageException(AeMessages.format("AeSQLCounterStore.ERROR_GetNextValues", aCounterName), e); //$NON-NLS-1$
            }
         }
      }
   }

   /**
    * Returns a SQL statement from the SQL configuration object. This
    * convenience method prepends the name of the statement with
    * "Counter.".
    *
    * @param aStatementName The name of the statement, such as "GetCounterValue".
    * @return The SQL statement found for that name.
    * @throws AeStorageException If the SQL statement is not found.
    */
   protected String getSQLStatement(String aStatementName) throws AeStorageException
   {
      String key = "Counter." + aStatementName; //$NON-NLS-1$
      String sql = getDataSource().getSQLConfig().getSQLStatement(key);

      if (AeUtil.isNullOrEmpty(sql))
      {
         throw new AeStorageException(AeMessages.format("AeSQLCounterStore.ERROR_2", key)); //$NON-NLS-1$
      }

      return sql;
   }

   /**
    * Returns current value for counter using the specified database connection.
    *
    * @param aConnection The database connection to use.
    * @return long
    * @throws AeStorageException
    * @throws SQLException
    */
   protected long getValue(Connection aConnection, String aCounterName) throws AeStorageException, SQLException
   {
      String sql = getSQLStatement(SQL_GET_COUNTER_VALUE);
      Object param = aCounterName;

      Long value = (Long) getQueryRunner().query(aConnection, sql, param, AeResultSetHandlers.getLongHandler());
      if (value == null)
      {
         throw new AeStorageException(AeMessages.format("AeSQLCounterStore.ERROR_3", aCounterName)); //$NON-NLS-1$
      }

      return value.longValue();
   }

   /**
    * Updates counter to next block of values using the specified database connection.
    *
    * @param aConnection The database connection to use.
    * @throws AeStorageException
    * @throws SQLException
    */
   protected boolean updateCounter(Connection aConnection, String aCounterName, int aBlockSize) throws AeStorageException, SQLException
   {
      // Update counter in the database to the end of a new block of values.
      String sql = getSQLStatement(SQL_UPDATE_COUNTER_VALUE);
      Object[] params = new Object[]
      {
         new Integer(aBlockSize),
         aCounterName
      };

      return getQueryRunner().update(aConnection, sql, params) > 0;
   }

   /**
    * Creates counter initialized to next block of values using the specified database connection.
    *
    * @param aConnection The database connection to use.
    * @throws AeStorageException
    * @throws SQLException
    */
   protected void createCounter(Connection aConnection, String aCounterName, int aBlockSize) throws AeStorageException, SQLException
   {
      String sql = getSQLStatement(SQL_INSERT_COUNTER);
      Object[] params = new Object[]
      {
         aCounterName,
         new Integer(aBlockSize)
      };

      getQueryRunner().update(aConnection, sql, params);
   }
}
