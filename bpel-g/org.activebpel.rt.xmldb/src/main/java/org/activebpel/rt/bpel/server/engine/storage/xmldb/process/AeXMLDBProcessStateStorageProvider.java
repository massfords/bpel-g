// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/AeXMLDBProcessStateStorageProvider.java,v 1.2 2007/09/28 19:55:23 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process;

import java.io.Reader;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeRestartProcessJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageUtil;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeAbstractXMLDBStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBQueryBuilder;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorage;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.journal.AeXMLDBJournalStorage;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers.AeJournalEntriesLocationIdsResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers.AeJournalEntriesResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers.AeProcessIDFilteredListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers.AeProcessInstanceDetailFilteredListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers.AeProcessInstanceDetailResponseHandler;
import org.activebpel.rt.util.AeLongMap;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.activebpel.rt.xmldb.AeMessages;

/**
 * A XMLDB implementation of a process state storage provider.
 */
public class AeXMLDBProcessStateStorageProvider extends AeAbstractXMLDBStorageProvider implements
      IAeProcessStateStorageProvider
{
   /** The prefix into the xmldb config that this storage object uses. */
   protected static final String CONFIG_PREFIX = "ProcessStorage"; //$NON-NLS-1$
   /** The chunk size used for deleting processes. */
   private static final int PURGE_CHUNK_SIZE = 50;

   /** A response handler that returns a list of process instance detail objects. */
   protected static final AeProcessInstanceDetailFilteredListResponseHandler PROCESS_LIST_RESPONSE_HANDLER = new AeProcessInstanceDetailFilteredListResponseHandler();
   /** A process detail instance handler. */
   protected static final AeProcessInstanceDetailResponseHandler PROC_INSTANCE_HANDLER = new AeProcessInstanceDetailResponseHandler();
   /** A journal entries response handler. */
   protected static final AeJournalEntriesResponseHandler JOURNAL_ENTRIES_HANDLER = new AeJournalEntriesResponseHandler();
   /** A journal entries location ids response handler. */
   protected static final AeJournalEntriesLocationIdsResponseHandler JOURNAL_ENTRIES_LOCATION_IDS_HANDLER = new AeJournalEntriesLocationIdsResponseHandler();

   /** The journal storage. */
   private AeXMLDBJournalStorage mJournalStorage;

   /**
    * Constructs a xmldb process state storage delegate.
    * 
    * @param aConfig
    * @param aStorageImpl
    */
   public AeXMLDBProcessStateStorageProvider(AeXMLDBConfig aConfig, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, CONFIG_PREFIX, aStorageImpl);
      setJournalStorage(createJournalStorage());
   }

   /**
    * Creates the journal storage.
    */
   protected AeXMLDBJournalStorage createJournalStorage()
   {
      return new AeXMLDBJournalStorage(getXMLDBConfig(), getStorageImpl());
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#createProcess(int, javax.xml.namespace.QName)
    */
   public long createProcess(int aPlanId, QName aProcessName) throws AeStorageException
   {
      LinkedHashMap params = new LinkedHashMap(10);
      params.put(IAeProcessElements.PLAN_ID, new Integer(aPlanId));
      params.put(IAeProcessElements.PROCESS_NAME, aProcessName);
      params.put(IAeProcessElements.PROCESS_DOCUMENT, IAeXMLDBStorage.NULL_DOCUMENT);
      params.put(IAeProcessElements.PROCESS_STATE, new Integer(IAeBusinessProcess.PROCESS_RUNNING));
      params.put(IAeProcessElements.PROCESS_STATE_REASON, IAeXMLDBStorage.NULL_INT);
      params.put(IAeProcessElements.START_DATE, new AeSchemaDateTime(new Date()));
      params.put(IAeProcessElements.END_DATE, IAeXMLDBStorage.NULL_DATETIME);
      params.put(IAeProcessElements.PENDING_INVOKES_COUNT, new Integer(0));
      params.put(IAeProcessElements.MODIFIED_DATE, IAeXMLDBStorage.NULL_DATETIME);
      params.put(IAeProcessElements.MODIFIED_COUNT, IAeXMLDBStorage.NULL_INT);
      
      // get (tx) connection and close it after use (in case this thread was not participating in a tx)
      IAeXMLDBConnection txConn = null; 
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         return insertDocument(IAeProcessConfigKeys.INSERT_PROCESS, params, txConn);
      }
      finally
      {
         txConn.close();
      }      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getConnectionProvider(long, boolean)
    */
   public IAeProcessStateConnectionProvider getConnectionProvider(long aProcessId, boolean aContainerManaged)
         throws AeStorageException
   {
      return new AeXMLDBProcessStateConnectionProvider(aProcessId, aContainerManaged, getXMLDBConfig(), getStorageImpl());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getLog(long)
    */
   public String getLog(long aProcessId) throws AeStorageException
   {
      return new AeXMLDBLogReader(aProcessId, getXMLDBConfig(), getStorageImpl()).readLog();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#dumpLog(long)
    */
   public Reader dumpLog(long aProcessId) throws AeStorageException
   {
      return new AeXMLDBLogStreamReader(getXMLDBConfig(), aProcessId, getStorageImpl());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getNextProcessId()
    */
   public long getNextProcessId() throws AeStorageException
   {
      // XMLDB doesn't use counters, so to mimic "getNextProcessId" we insert and then immediately
      // remove a process record.  This gives us the ino:id that was created for that doc instance.

      // Note:  this is not transactional because process recovery has a mechanism that removes hanging process instances (ones with no state document)
      long procId = createProcess(0, new QName("urn:ae:notused", "notused")); //$NON-NLS-1$ //$NON-NLS-2$
      removeProcessInternal(procId);
      return procId;
   }

   /**
    * Removes a process.  Returns true if the process was successfully removed.
    * 
    * @param aProcessId
    * @throws AeStorageException
    */
   protected boolean removeProcessInternal(long aProcessId) throws AeStorageException
   {
      IAeXMLDBConnection connection = getNewConnection();

      try
      {
         return removeProcessInternal(aProcessId, connection);
      }
      finally
      {
         connection.close();
      }
   }

   /**
    * Removes a process.  Returns true if the process was successfully removed.
    * 
    * @param aProcessId
    * @param aConnection
    * @throws AeStorageException
    */
   protected boolean removeProcessInternal(long aProcessId, IAeXMLDBConnection aConnection) throws AeStorageException
   {
      Object[] params = { new Long(aProcessId) };
      return deleteDocuments(IAeProcessConfigKeys.DELETE_PROCESS, params, aConnection) > 0;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getProcessInstanceDetail(long)
    */
   public AeProcessInstanceDetail getProcessInstanceDetail(long aProcessId) throws AeStorageException
   {
      Object[] params = { new Long(aProcessId) };
      return (AeProcessInstanceDetail) query(IAeProcessConfigKeys.GET_PROCESS_INSTANCE_DETAIL, params, 
            getProcessInstanceDetailResponseHandler());
   }

   /**
    * Creates the process instance detail response handler to use.
    */
   protected AeProcessInstanceDetailResponseHandler getProcessInstanceDetailResponseHandler()
   {
      return PROC_INSTANCE_HANDLER;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getProcessList(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public AeProcessListResult getProcessList(AeProcessFilter aFilter) throws AeStorageException
   {
      AeXMLDBQueryBuilder queryBuilder = createProcessListQueryBuilder(aFilter);
      AeProcessInstanceDetailFilteredListResponseHandler handler = createProcessListResponseHandler();
      List processes = (List) query(queryBuilder, handler);
      return new AeProcessListResult(handler.getRowCount(), processes, handler.isCompleteRowCount());
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getProcessIds(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public long[] getProcessIds(AeProcessFilter aFilter) throws AeStorageException
   {
      AeXMLDBFilteredProcessListQueryBuilder queryBuilder = createProcessListQueryBuilder(aFilter);
      queryBuilder.setConfigKey(IAeProcessConfigKeys.GET_PROCESS_IDS);
      AeProcessIDFilteredListResponseHandler handler = new AeProcessIDFilteredListResponseHandler();
      return (long []) query(queryBuilder, handler);
   }   
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getProcessCount(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int getProcessCount(AeProcessFilter aFilter) throws AeStorageException
   {
      AeXMLDBQueryBuilder queryBuilder = createProcessListQueryBuilder(aFilter);
      queryBuilder.setCountOnlyFlag(true);
      return ((Integer) query(queryBuilder, AeXMLDBResponseHandler.INTEGER_RESPONSE_HANDLER)).intValue();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getProcessName(long)
    */
   public QName getProcessName(long aProcessId) throws AeStorageException
   {
      Object[] params = { new Long(aProcessId) };
      QName qname = (QName) query(IAeProcessConfigKeys.GET_PROCESS_NAME, params,
            AeXMLDBResponseHandler.QNAME_RESPONSE_HANDLER);
      if (qname == null)
      {
         throw new AeStorageException(AeMessages.format("AeXMLDBProcessStateStorage.0", new Long(aProcessId))); //$NON-NLS-1$
      }
      return qname;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getJournalEntries(long)
    */
   public List getJournalEntries(long aProcessId) throws AeStorageException
   {
      Object[] params = { new Long(aProcessId) };
      return (List) query(IAeProcessConfigKeys.GET_JOURNAL_ENTRIES, params, JOURNAL_ENTRIES_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getJournalEntriesLocationIdsMap(long)
    */
   public AeLongMap getJournalEntriesLocationIdsMap(long aProcessId) throws AeStorageException
   {
      Object[] params = { new Long(aProcessId) };
      return (AeLongMap) query(IAeProcessConfigKeys.GET_JOURNAL_ENTRIES_LOCATION_IDS, params,
            JOURNAL_ENTRIES_LOCATION_IDS_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getJournalEntry(long)
    */
   public IAeJournalEntry getJournalEntry(long aJournalId) throws AeStorageException
   {
      Object[] params = { new Long(aJournalId) };
      List list = (List) query(IAeProcessConfigKeys.GET_JOURNAL_ENTRY, params, JOURNAL_ENTRIES_HANDLER);

      return ((list != null) && (list.size() > 0)) ? (IAeJournalEntry) list.get(0) : null;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getRecoveryProcessIds()
    */
   public Set getRecoveryProcessIds() throws AeStorageException
   {
      return (Set) query(IAeProcessConfigKeys.GET_RECOVERY_PROCESS_IDS, AeXMLDBResponseHandler.LONGSET_RESPONSE_HANDLER);
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
      removeProcessInternal(aProcessId, getXMLDBConnection(aConnection));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#removeProcesses(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int removeProcesses(AeProcessFilter aFilter) throws AeStorageException
   {
      // TODO The following two queries could be improved to only return the min/max process ID for processes that match the filter.
      Integer maxProcId = (Integer) query(IAeProcessConfigKeys.GET_MAX_PROCESSID, AeXMLDBResponseHandler.INTEGER_RESPONSE_HANDLER);
      Integer minProcId = (Integer) query(IAeProcessConfigKeys.GET_MIN_PROCESSID, AeXMLDBResponseHandler.INTEGER_RESPONSE_HANDLER);
      int totalRemoved = 0;
      // If there are NO records in the DB, minProcId will be null.
      if (minProcId != null)
      {
         AeXMLDBFilteredProcessListQueryBuilder queryBuilder = createProcessListQueryBuilder(aFilter);
         int currProcId = minProcId.intValue();
         while (currProcId <= maxProcId.intValue())
         {
            queryBuilder.setMinProcessID(currProcId);
            queryBuilder.setMaxProcessID(currProcId + PURGE_CHUNK_SIZE);
            totalRemoved += deleteDocuments(queryBuilder);
            currProcId += PURGE_CHUNK_SIZE + 1;
         }
      }
      return totalRemoved;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#writeJournalEntry(long, org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry)
    */
   public long writeJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry) throws AeStorageException
   {
      IAeXMLDBConnection txConn = null;
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         return getJournalStorage().writeJournalEntry(aProcessId, aJournalEntry, txConn);
      }
      finally
      {
         txConn.close();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getNextJournalId()
    */
   public long getNextJournalId() throws AeStorageException
   {
      // create an empty journal entry and delete it to grab the journal id.
      IAeStorageConnection conn = getCommitControlDBConnection();
      try
      {
         long journalId = getJournalStorage().saveJournalEntry(0, null, null, getXMLDBConnection(conn));
         getJournalStorage().deleteJournalEntry(journalId, getXMLDBConnection(conn));
         conn.commit();
         return journalId;
      }
      catch(AeStorageException ase)
      {
         AeStorageUtil.rollback(conn);
         throw ase;
      }
      finally
      {
         conn.close();
      }
   }   
   
   /**
    * Creates the process list query builder to use when getting a filtered list of processes.
    * 
    * @param aFilter
    */
   protected AeXMLDBFilteredProcessListQueryBuilder createProcessListQueryBuilder(AeProcessFilter aFilter)
   {
      return new AeXMLDBFilteredProcessListQueryBuilder(aFilter, getXMLDBConfig(), getStorageImpl());
   }

   /**
    * Creates the response handler to use for the response list.  This method exists so that it
    * can be overridden.
    */
   protected AeProcessInstanceDetailFilteredListResponseHandler createProcessListResponseHandler()
   {
      return PROCESS_LIST_RESPONSE_HANDLER;
   }

   /**
    * @return Returns the journalStorage.
    */
   protected AeXMLDBJournalStorage getJournalStorage()
   {
      return mJournalStorage;
   }

   /**
    * @param aJournalStorage The journalStorage to set.
    */
   protected void setJournalStorage(AeXMLDBJournalStorage aJournalStorage)
   {
      mJournalStorage = aJournalStorage;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider#getRestartProcessJournalEntry(long)
    */
   public AeRestartProcessJournalEntry getRestartProcessJournalEntry(long aProcessId) throws AeStorageException
   {
      Object[] params = { new Long(aProcessId) };
      List list = (List) query(IAeProcessConfigKeys.GET_JOURNAL_ENTRY, params, JOURNAL_ENTRIES_HANDLER);

      return ((list != null) && (list.size() > 0)) ? (AeRestartProcessJournalEntry) list.get(0) : null;
   }
}
