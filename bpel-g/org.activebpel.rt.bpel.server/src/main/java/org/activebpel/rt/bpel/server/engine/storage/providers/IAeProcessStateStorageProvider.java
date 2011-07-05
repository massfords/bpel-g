// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.providers;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.server.engine.recovery.journal.AeRestartProcessJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection;

import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessInstanceDetail;
import bpelg.services.processes.types.ProcessList;

/**
 * A process storage delegate. This interface defines methods that the
 * delegating process storage will call in order to store/read date in the
 * underlying database.
 */
public interface IAeProcessStateStorageProvider extends IAeStorageProvider
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#createProcess(int, javax.xml.namespace.QName)
    */
   public long createProcess(int aPlanId, QName aProcessName) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getConnection(long, boolean)
    */
   public IAeProcessStateConnectionProvider getConnectionProvider(long aProcessId, boolean aContainerManaged) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getLog(long)
    */
   public String getLog(long aProcessId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#dumpLog(long)
    */
   public Reader dumpLog(long aProcessId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getNextProcessId()
    */
   public long getNextProcessId() throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getProcessInstanceDetail(long)
    */
   public ProcessInstanceDetail getProcessInstanceDetail(long aProcessId) throws AeStorageException;

   public ProcessList getProcessList(ProcessFilterType aFilter) throws AeStorageException;

   public int getProcessCount(ProcessFilterType aFilter) throws AeStorageException;

   /**
    * Returns the processId for a given filter.
    * @param aFilter
    * @throws AeStorageException
    */
   public long[] getProcessIds(ProcessFilterType aFilter) throws AeStorageException;

   /**
    * Returns the process name for the given process ID or null if not found.
    * 
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getProcessName(long)
    */
   public QName getProcessName(long aProcessId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getJournalEntries(long)
    */
   public List<IAeJournalEntry> getJournalEntries(long aProcessId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getJournalEntriesLocationIdsMap(long)
    */
   public Map<Long, Integer> getJournalEntriesLocationIdsMap(long aProcessId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getJournalEntry(long)
    */
   public IAeJournalEntry getJournalEntry(long aJournalId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getRecoveryProcessIds()
    */
   public Set getRecoveryProcessIds() throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#releaseConnection(org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection)
    */
   public void releaseConnection(IAeProcessStateConnection aConnection) throws AeStorageException;

   /**
    * Removes the process and its variables from the database using the
    * specified database connection.
    *
    * @param aProcessId
    * @param aConnection The database connection to use.
    * @throws AeStorageException
    */
   public void removeProcess(long aProcessId, IAeStorageConnection aConnection) throws AeStorageException;

   public int removeProcesses(ProcessFilterType aFilter) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#writeJournalEntry(long, org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry)
    */
   public long writeJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry) throws AeStorageException;
   
   /** 
    * @return Next available journal id.
    * @throws AeStorageException
    */
   public long getNextJournalId() throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getRestartProcessJournalEntry(long)
    */
   public AeRestartProcessJournalEntry getRestartProcessJournalEntry(long aProcessId) throws AeStorageException;
}
