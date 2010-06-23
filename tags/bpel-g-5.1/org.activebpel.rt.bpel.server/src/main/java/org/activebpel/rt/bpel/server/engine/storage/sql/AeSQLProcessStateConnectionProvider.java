// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLProcessStateConnectionProvider.java,v 1.9 2008/02/17 21:38:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeLocationVersionSet;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeLocationVersionResultSetHandler;
import org.apache.commons.dbutils.ResultSetHandler;
import org.w3c.dom.Document;

/**
 * A SQL implementation of a process state connection provider.
 */
public class AeSQLProcessStateConnectionProvider extends AeAbstractSQLStorageProvider implements
      IAeProcessStateConnectionProvider
{
   // DELETE_JOURNAL_ENTRIES_LENGTH was 10, because we were "batching" deletes
   // of journal entries in groups of 10 with a
   // JournalId IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) test, but this leads to
   // deadlocks on SQL Server, which handles this kind of "batched" query with
   // a clustered index scan. So, for the time being delete one at a time using
   // a JournalId = ? test that SQL Servers handles with a direct clustered
   // index delete.
   /** The number of journal entries that can be deleted per statement. */
   private static final int DELETE_JOURNAL_ENTRIES_LENGTH = 1;

   /** Value to use to fill in remaining parameters for delete journal entries statement. */
   private static final Object DELETE_JOURNAL_ENTRIES_FILL = AeQueryRunner.NULL_BIGINT;

   /** A location version set result set handler. */
   private static final ResultSetHandler LOCATION_VERSION_SET_HANDLER = new AeLocationVersionResultSetHandler();

   /** The process id. */
   private final long mProcessId;
   /** <code>true</code> to respect container-managed transaction boundaries. */
   private final boolean mContainerManaged;
   /** The shared JDBC <code>Connection</code>. */
   private Connection mSharedConnection;

   /**
    * Constructs a process state connection for the specified process.
    *
    * @param aProcessId
    * @param aContainerManaged
    * @param aSQLConfig
    */
   protected AeSQLProcessStateConnectionProvider(long aProcessId, boolean aContainerManaged,
         AeSQLConfig aSQLConfig)
   {
      super(AeSQLProcessStateStorageProvider.PROCESS_STORAGE_PREFIX, aSQLConfig);

      mProcessId = aProcessId;
      mContainerManaged = aContainerManaged;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#close()
    */
   public void close() throws AeStorageException
   {
      if (mSharedConnection != null)
      {
         try
         {
            mSharedConnection.close();
         }
         catch (SQLException e)
         {
            throw new AeStorageException(e);
         }
         finally
         {
            mSharedConnection = null;
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#commit()
    */
   public void commit() throws AeStorageException
   {
      if (!isContainerManaged())
      {
         try
         {
            getConnection().commit();
         }
         catch (SQLException e)
         {
            throw new AeStorageException(AeMessages.getString("AeSQLProcessStateConnection.ERROR_1"), e); //$NON-NLS-1$
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#getProcessDocument()
    */
   public Document getProcessDocument() throws AeStorageException
   {
      Object[] param = { new Long(getProcessId()) };
      return (Document) query(getConnection(), IAeProcessSQLKeys.GET_PROCESS_DOCUMENT, param,
            AeResultSetHandlers.getDocumentHandler());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#getProcessVariables()
    */
   public IAeLocationVersionSet getProcessVariables() throws AeStorageException
   {
      Object[] params = { new Long(getProcessId()) };
      return (IAeLocationVersionSet) query(getConnection(), IAeProcessSQLKeys.GET_PROCESS_VARIABLES, params,
            LOCATION_VERSION_SET_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#getVariableDocument(long, int)
    */
   public Document getVariableDocument(long aLocationId, int aVersionNumber) throws AeStorageException
   {
      Object[] params = { 
            new Long(getProcessId()), 
            new Long(aLocationId),
            new Integer(aVersionNumber) 
      };

      return (Document) query(getConnection(), IAeProcessSQLKeys.GET_VARIABLE_DOCUMENT, params,
            AeResultSetHandlers.getDocumentHandler());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#removeJournalEntries(org.activebpel.rt.util.AeLongSet)
    */
   public void removeJournalEntries(Set<Long> aJournalIds) throws AeStorageException
   {
      removeLongSet(aJournalIds, IAeProcessSQLKeys.DELETE_JOURNAL_ENTRY, IAeProcessSQLKeys.DELETE_JOURNAL_ENTRIES);
   }
   
   /**
    * Removes a set of entries based on the given long set and the query key.
    * @param aLongIds
    * @param aSingleParamQueryKey
    * @param aBatchParamQueryKey
    * @throws AeStorageException
    */
   protected void removeLongSet(Set<Long> aLongIds, String aSingleParamQueryKey, String aBatchParamQueryKey) throws AeStorageException
   {
      // Sort the ids to delete journal (or transmissiond id) entries in a consistent order. This
      // prevents deadlocks on DB2 between transactions that are deleting
      // multiple entries, because DB2 creates "next key" locks along
      // the way.
      List ids = new ArrayList(aLongIds);
      Collections.sort(ids);

      Object[] params = new Object[DELETE_JOURNAL_ENTRIES_LENGTH];

      if (params.length == 1)
      {
         for (Iterator i = ids.iterator(); i.hasNext(); )
         {
            params[0] = i.next();

            update(getConnection(), aSingleParamQueryKey, params);
         }
      }
      else
      {
         for (Iterator i = ids.iterator(); i.hasNext(); )
         {
            int k = 0;
   
            while ((k < params.length) && i.hasNext())
            {
               params[k++] = i.next();
            }
   
            while (k < params.length)
            {
               params[k++] = DELETE_JOURNAL_ENTRIES_FILL;
            }
   
            update(getConnection(), aBatchParamQueryKey, params);
         }
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#rollback()
    * 
    * Note: When <code>isDebugReleaseConnection()</code> is <code>true</code>,
    * either <code>commit()</code> or <code>rollback()</code> is required or
    * the system will log an error message.
    */
   public void rollback() throws AeStorageException
   {
      if (!isContainerManaged())
      {
         try
         {
            getConnection().rollback();
         }
         catch (SQLException e)
         {
            throw new AeStorageException(AeMessages.getString("AeSQLProcessStateConnection.ERROR_2"), e); //$NON-NLS-1$
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#saveProcess(org.activebpel.rt.bpel.impl.fastdom.AeFastDocument,
    *      int, int, java.util.Date, java.util.Date, int)
    */
   public void saveProcess(AeFastDocument aDocument, int aProcessState, int aProcessStateReason,
         Date aStartDate, Date aEndDate, int aPendingInvokesCount) throws AeStorageException
   {
      // TODO (KR) Optionally compress process document.
      Object[] params = {
         (aDocument  == null) ? (Object) AeQueryRunner.NULL_CLOB : aDocument,
         new Integer(aProcessState),
         new Integer(aProcessStateReason),
         getDateOrSqlNull(aStartDate),
         getDateOrSqlNull(aEndDate),
         new Integer(aPendingInvokesCount),
         new Date(), // ModifiedDate
         new Long(getProcessId())
      };

      update(getConnection(), IAeProcessSQLKeys.UPDATE_PROCESS, params);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#saveVariable(org.activebpel.rt.bpel.impl.fastdom.AeFastDocument, int, int)
    */
   public void saveVariable(AeFastDocument aDocument, int aLocationId, int aVersionNumber)
         throws AeStorageException
   {
      // TODO (KR) Optionally compress variable document.
      Object[] params = {
         (aDocument == null) ? (Object) AeQueryRunner.NULL_CLOB : aDocument,
         new Long(getProcessId()),
         new Long(aLocationId),
         new Integer(aVersionNumber)
      };

      update(getConnection(), IAeProcessSQLKeys.INSERT_VARIABLE, params);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#trimStoredVariable(long, int)
    */
   public void trimStoredVariable(long aLocationId, int aVersionNumber) throws AeStorageException
   {
      Object[] params = {
         new Long(getProcessId()),
         new Long(aLocationId),
         new Integer(aVersionNumber)
      };

      update(getConnection(), IAeProcessSQLKeys.DELETE_VARIABLE, params);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#saveLog(java.lang.String, int)
    */
   public void saveLog(String aLogString, int aLineCount) throws AeStorageException
   {
      // TODO (KR) Optionally compress process log.
      Object[] params = {
         new Long(getProcessId()),
         aLogString.toCharArray(),
         new Integer(aLineCount)
      };

      update(getConnection(), IAeProcessSQLKeys.INSERT_PROCESS_LOG, params);
   }

   /**
    * Returns the shared JDBC <code>Connection</code>.
    *
    */
   protected Connection getConnection() throws AeStorageException
   {
      if (mSharedConnection == null)
      {
         Connection connection;

         if (isContainerManaged())
         {
            connection = super.getContainerManagedConnection();
         }
         else
         {
            connection = super.getTransactionConnection(true, isDebugReleaseConnection()); 
         }

         mSharedConnection = connection;
      }

      return mSharedConnection;
   }

   /**
    * Returns <code>true</code> if the class should debug release of
    * connections to verify that they are either commited or rollback.
    * Currently delegates to persistent process manager debug flag.
    */
   protected boolean isDebugReleaseConnection()
   {
      return AePersistentProcessManager.isDebug() && !isContainerManaged();
   }

   /**
    * @return Returns the processId.
    */
   protected long getProcessId()
   {
      return mProcessId;
   }

   /**
    * @return Returns the sharedConnection.
    */
   protected Connection getSharedConnection()
   {
      return mSharedConnection;
   }

   /**
    * @return Returns the containerManaged.
    */
   protected boolean isContainerManaged()
   {
      return mContainerManaged;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#updateJournalEntryType(long, int)
    */
   public void updateJournalEntryType(long aJournalId, int aEntryType) throws AeStorageException
   {
      Object[] params = { new Integer(aEntryType), new Long(aJournalId) };
      update(getConnection(), IAeProcessSQLKeys.UPDATE_JOURNAL_ENTRY_TYPE, params);
   }
}
