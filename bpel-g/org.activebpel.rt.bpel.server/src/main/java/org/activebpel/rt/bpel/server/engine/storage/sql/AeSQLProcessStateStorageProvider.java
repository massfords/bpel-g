// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLProcessStateStorageProvider.java,v 1.11 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeRestartProcessJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AeCounter;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.sql.filters.AeSQLProcessFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeJournalEntriesLocationIdsResultSetHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeJournalEntriesResultSetHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeSQLProcessIdsResultSetHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeSQLProcessInstanceResultSetHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeSQLProcessListResultSetHandler;
import org.activebpel.rt.bpel.server.logging.AeLogReader;
import org.activebpel.rt.bpel.server.logging.AeSequentialClobStream;
import org.activebpel.rt.util.AeCloser;
import org.apache.commons.dbutils.ResultSetHandler;

import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessInstanceDetail;
import bpelg.services.processes.types.ProcessList;
import bpelg.services.processes.types.ProcessStateValueType;
import bpelg.services.processes.types.SuspendReasonType;

/**
 * A SQL version of a process state storage delegate.
 */
public class AeSQLProcessStateStorageProvider extends AeAbstractSQLStorageProvider implements
      IAeProcessStateStorageProvider
{
   public static final String PROCESS_STORAGE_PREFIX = "ProcessStorage."; //$NON-NLS-1$
   public static final String SQL_PROCESS_TABLE_NAME = "AeProcess"; //$NON-NLS-1$
   public static final String SQL_ORDER_BY_PROCESSID = " ORDER BY " + IAeProcessColumns.PROCESS_ID ; //$NON-NLS-1$
   public static final String SQL_ORDER_BY_START_DATE_PROCESSID = " ORDER BY " + IAeProcessColumns.START_DATE + " DESC, " + IAeProcessColumns.PROCESS_ID + " DESC"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

   protected static final AeSQLProcessInstanceResultSetHandler PROC_INSTANCE_HANDLER = new AeSQLProcessInstanceResultSetHandler();
   protected static final ResultSetHandler<List<IAeJournalEntry>> JOURNAL_ENTRIES_RESULT_SET_HANDLER = new AeJournalEntriesResultSetHandler();
   protected static final ResultSetHandler<Map<Long, Integer>> JOURNAL_ENTRIES_LOCATION_IDS_RESULT_SET_HANDLER = new AeJournalEntriesLocationIdsResultSetHandler();

   /** The journal storage. */
   private AeSQLJournalStorage mJournalStorage;
   private AeCounter mCounter;
   
   /**
    * Constructs a SQL based process state storage delegate with the given SQL
    * config instance.
    */
   public AeSQLProcessStateStorageProvider()
   {
      setPrefix(PROCESS_STORAGE_PREFIX);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#createProcess(int, javax.xml.namespace.QName)
    */
   public long createProcess(int aPlanId, QName aProcessName) throws AeStorageException
   {
      long processId = getNextProcessId();

      // Insert a row for the new process.
      Object[] params = new Object[]
      {
         new Long(processId),
         new Integer(aPlanId),
         aProcessName.getNamespaceURI(),
         aProcessName.getLocalPart(),
         new Integer(ProcessStateValueType.Running.value()),
         new Integer(SuspendReasonType.None.value()), // StateReason is undefined unless dealing with a suspended state 
         new Date() // specify StartDate, so that process will sort correctly in Active Processes list
      };

      // note: when calling update, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.      
      Connection conn = null;
      try
      {
         conn = getTransactionConnection();
         update( conn, IAeProcessSQLKeys.INSERT_PROCESS, params);
      }
      finally
      {
         AeCloser.close(conn);
      }      
      
      return processId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getConnectionProvider(long, boolean)
    */
   public IAeProcessStateConnectionProvider getConnectionProvider(long aProcessId, boolean aContainerManaged) throws AeStorageException
   {
      return new AeSQLProcessStateConnectionProvider(aProcessId, aContainerManaged, getSQLConfig());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getLog(long)
    */
   public String getLog(long aProcessId) throws AeStorageException
   {
      try
      {
         return new AeLogReader(aProcessId, getSQLConfig()).readLog();
      }
      catch (SQLException e)
      {
         throw new AeStorageException(e);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#dumpLog(long)
    */
   public Reader dumpLog(long aProcessId) throws AeStorageException
   {
      return new AeSequentialClobStream(getSQLConfig(), getQueryRunner(), aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getNextProcessId()
    */
   public long getNextProcessId() throws AeStorageException
   {
      return getCounter().getNextValue();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getProcessInstanceDetail(long)
    */
   public ProcessInstanceDetail getProcessInstanceDetail(long aProcessId) throws AeStorageException
   {
      return query(IAeProcessSQLKeys.GET_PROCESS_INSTANCE_DETAIL, getProcessInstanceResultSetHandler(), aProcessId);
   }

   /**
    * Creates the result set handler that will be used to create the process instance detail.
    */
   protected AeSQLProcessInstanceResultSetHandler getProcessInstanceResultSetHandler()
   {
      return PROC_INSTANCE_HANDLER;
   }

   /**
	* @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getProcessList(bpelg.services.processes.types.ProcessFilterType)
	*/
   public ProcessList getProcessList(ProcessFilterType aFilter) throws AeStorageException
   {
      try
      {
         AeSQLProcessFilter filter = createFilter(aFilter);
         String sql = filter.getSelectStatement();
         Object[] params = filter.getParams();

         // Construct a ResultSetHandler that converts the ResultSet to an AeProcessListResult.
         AeSQLProcessListResultSetHandler handler = createProcessListResultSetHandler(aFilter);

         // Run the query.
         return getQueryRunner().query(sql, handler, params);
      }
      catch (SQLException ex)
      {
         throw new AeStorageException(ex);
      }
   }
   
   public int getProcessCount(ProcessFilterType aFilter) throws AeStorageException
   {
      try
      {
         AeSQLProcessFilter filter = createFilter(aFilter);
         String sql = filter.getCountStatement();
         Object[] params = filter.getParams();

         // Run the query.
         return (getQueryRunner().query(sql, AeResultSetHandlers.getIntegerHandler(), params)).intValue();
      }
      catch (SQLException ex)
      {
         throw new AeStorageException(ex);
      }
   }

   /**
    * Get processIds for for the specified filter. ProcessIds needs to be ordered when remove between processId ranges
    */
   public long[] getProcessIds(ProcessFilterType aFilter) throws AeStorageException
   {
      try
      {
         AeSQLProcessFilter filter = createFilter(aFilter);
         filter.setSelectClause(filter.getSQLStatement(IAeProcessSQLKeys.GET_PROCESS_IDS));
         filter.setOrderBy(AeSQLProcessStateStorageProvider.SQL_ORDER_BY_PROCESSID);
         String sql = filter.getSelectStatement();
         Object[] params = filter.getParams();         
         AeSQLProcessIdsResultSetHandler handler = createProcessIdsResultSetHandler(aFilter);
         return getQueryRunner().query(sql, handler, params);
      }
      catch (SQLException ex)
      {
         throw new AeStorageException(ex);
      }
   }

   /**
    * Creates a sql filter for which is responsible for creating where clause based on the passed
    * filters criteria.
    * 
    * @param aFilter
    * @return the class which will produce the sql which applies the filter.
    * @throws AeStorageException
    */
   protected AeSQLProcessFilter createFilter(ProcessFilterType aFilter) throws AeStorageException
   {
      return new AeSQLProcessFilter(aFilter, getSQLConfig());
   }

   /**
    * Creates the result set handler used to extract the process list from the SQL result set.
    */
   protected AeSQLProcessListResultSetHandler createProcessListResultSetHandler(ProcessFilterType aFilter)
   {
      return new AeSQLProcessListResultSetHandler(aFilter);
   }

   /**
    * Creates the result set handler used to extract the process ids from the SQL result set.
    */
   protected AeSQLProcessIdsResultSetHandler createProcessIdsResultSetHandler(ProcessFilterType aFilter)
   {
      return new AeSQLProcessIdsResultSetHandler(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getProcessName(long)
    */
   public QName getProcessName(long aProcessId) throws AeStorageException
   {
      return query(IAeProcessSQLKeys.GET_PROCESS_NAME, AeResultSetHandlers.getQNameHandler(), aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getJournalEntries(long)
    */
   public List<IAeJournalEntry> getJournalEntries(long aProcessId) throws AeStorageException
   {
      return (List<IAeJournalEntry>) query(IAeProcessSQLKeys.GET_JOURNAL_ENTRIES, JOURNAL_ENTRIES_RESULT_SET_HANDLER, aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getJournalEntriesLocationIdsMap(long)
    */
   public Map<Long,Integer> getJournalEntriesLocationIdsMap(long aProcessId) throws AeStorageException
   {
      return query(IAeProcessSQLKeys.GET_JOURNAL_ENTRIES_LOCATION_IDS, JOURNAL_ENTRIES_LOCATION_IDS_RESULT_SET_HANDLER, aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getJournalEntry(long)
    */
   public IAeJournalEntry getJournalEntry(long aJournalId) throws AeStorageException
   {
      List list = query(IAeProcessSQLKeys.GET_JOURNAL_ENTRY, JOURNAL_ENTRIES_RESULT_SET_HANDLER, aJournalId);

      return ((list != null) && (list.size() > 0)) ? (IAeJournalEntry) list.get(0) : null;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getRecoveryProcessIds()
    */
   public Set getRecoveryProcessIds() throws AeStorageException
   {
      return (Set) query(IAeProcessSQLKeys.GET_RECOVERY_PROCESS_IDS, null, AeResultSetHandlers.getLongSetHandler());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#releaseConnection(org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection)
    */
   public void releaseConnection(IAeProcessStateConnection aConnection) throws AeStorageException
   {
      aConnection.close();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#removeProcess(long, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public void removeProcess(long aProcessId, IAeStorageConnection aConnection) throws AeStorageException
   {
      Connection connection = getSQLConnection(aConnection);

      Object[] params = new Object[] { new Long(aProcessId) };

      if (!getSQLConfig().getParameterBoolean(AeSQLConfig.PARAMETER_HAS_CASCADING_DELETES))
      {
         // Scrub process from dependent tables.
         update(connection, IAeProcessSQLKeys.DELETE_PROCESS_LOG, params);
         update(connection, IAeProcessSQLKeys.DELETE_PROCESS_VARIABLES, params);
         update(connection, IAeProcessSQLKeys.DELETE_JOURNAL_ENTRIES, params);
         //TODO: (JB) remove attachments here for databases which don't support cascading deletes
      }

      // Delete process.
      update(connection, IAeProcessSQLKeys.DELETE_PROCESS, params);
   }
   
   public int removeProcesses(ProcessFilterType aFilter) throws AeStorageException
   {
      try
      {
         if ( !getSQLConfig().getParameterBoolean(AeSQLConfig.PARAMETER_HAS_CASCADING_DELETES) )
         {
            throw new AeStorageException(AeMessages.getString("AeSQLProcessStateStorage.ERROR_2")); //$NON-NLS-1$
         }

         AeSQLProcessFilter filter = createFilter(aFilter);
         String sql = filter.getDeleteStatement();
         Object[] params = filter.getParams();
         return getQueryRunner().update(sql, params);
      }
      catch (SQLException e)
      {
         String errorMessage = AeMessages.getString("AeSQLProcessStateStorage.ERROR_3"); //$NON-NLS-1$
         String causeMessage = e.getLocalizedMessage();
         throw new AeStorageException((causeMessage != null) ? (errorMessage + ": " + causeMessage) : errorMessage, e); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#writeJournalEntry(long, org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry)
    */
   public long writeJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry) throws AeStorageException
   {
      Connection connection = getTransactionConnection();

      try
      {
         return getJournalStorage().writeJournalEntry(aProcessId, aJournalEntry, connection);
      }
      finally
      {
         AeCloser.close(connection);
      }
   }

   /**
    * Returns next available journal id.
    *
    * @throws AeStorageException
    */
   public long getNextJournalId() throws AeStorageException
   {
      return getJournalStorage().getNextJournalId();
   }
   
   /**
    * @return Returns the journalStorage.
    */
   public AeSQLJournalStorage getJournalStorage()
   {
      return mJournalStorage;
   }
   
   /**
    * @param aJournalStorage The journalStorage to set.
    */
   public void setJournalStorage(AeSQLJournalStorage aJournalStorage)
   {
      mJournalStorage = aJournalStorage;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getRestartProcessJournalEntry(long)
    */
   public AeRestartProcessJournalEntry getRestartProcessJournalEntry(long aProcessId) throws AeStorageException
   {
      List list = query(IAeProcessSQLKeys.GET_RESTART_PROCESS_JOURNAL_ENTRY, JOURNAL_ENTRIES_RESULT_SET_HANDLER, aProcessId);

      return ((list != null) && (list.size() > 0)) ? (AeRestartProcessJournalEntry) list.get(0) : null;
   }

   /**
    * @return
    */
   public AeCounter getCounter() {
      return mCounter;
   }

   /**
    * @param aCounter
    */
   public void setCounter(AeCounter aCounter) {
      mCounter = aCounter;
   }
}
