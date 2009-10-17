// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAeProcessStateStorage.java,v 1.25 2008/02/17 21:38:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.io.Reader;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeRestartProcessJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.util.AeLongMap;

/**
 * Defines interface for managing process state and variables in storage.
 */
public interface IAeProcessStateStorage extends IAeStorage
{
   /**
    * Creates a process in the storage.
    *
    * @param aPlanId The id for the plan the process was created from.
    * @param aProcessName The process name.
    * @return long The process id for the new process.
    * @throws AeStorageException
    */
   public long createProcess(int aPlanId, QName aProcessName) throws AeStorageException;

   /**
    * Returns a connection for storing and retrieving state and variable
    * documents for the specified process.
    *
    * @param aProcessId
    * @param aContainerManaged <code>true</code> to respect container-managed transaction boundaries.
    * @return IAeProcessStateConnection
    * @throws AeStorageException
    */
   public IAeProcessStateConnection getConnection(long aProcessId, boolean aContainerManaged) throws AeStorageException;

   /**
    * Returns uncleared journal entries for the specified process as a list of
    * {@link org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry}
    * instances in the order that they were saved.
    *
    * @param aProcessId
    * @return List
    * @throws AeStorageException
    */
   public List getJournalEntries(long aProcessId) throws AeStorageException;

   /**
    * Returns location ids of journal entries for the given process as a map
    * from journal entry ids to location ids.
    *
    * @param aProcessId
    * @return AeLongMap
    * @throws AeStorageException
    */
   public AeLongMap getJournalEntriesLocationIdsMap(long aProcessId) throws AeStorageException;

   /**
    * Returns the journal entry with the given journal id.
    */
   public IAeJournalEntry getJournalEntry(long aJournalId) throws AeStorageException;

   /**
    * Returns next available process id.
    *
    * @throws AeStorageException
    */
   public long getNextProcessId() throws AeStorageException;

   /**
    * Returns next available journal id.
    *
    * @throws AeStorageException
    */
   public long getNextJournalId() throws AeStorageException;
   
   /**
    * Returns process instance detail for the process with the specified process id.
    *
    * @param aProcessId
    * @return AeProcessInstanceDetail
    * @throws AeStorageException
    */
   public AeProcessInstanceDetail getProcessInstanceDetail(long aProcessId) throws AeStorageException;

   /**
    * Returns a list of processes based upon filter specification.
    *
    * @param aFilter The filter specification to limit the set of processes, or <code>null</code>.
    * @return AeProcessListResult
    * @throws AeStorageException
    */
   public AeProcessListResult getProcessList(AeProcessFilter aFilter) throws AeStorageException;

   /**
    * Returns the number of processes that match the filter specification.
    *
    * @param aFilter The filter specification to limit the set of processes, or <code>null</code>.
    * @return AeProcessListResult
    * @throws AeStorageException
    */
   public int getProcessCount(AeProcessFilter aFilter) throws AeStorageException;

   /**
    * Returns the processIds that match the filter specification.
    * @param aFilter
    * @return long[]
    * @throws AeStorageException
    */   
   public long[] getProcessIds(AeProcessFilter aFilter) throws AeStorageException;

   /**
    * Returns the <code>QName</code> for a process.
    *
    * @param aProcessId
    * @return QName
    * @throws AeStorageException
    */
   public QName getProcessName(long aProcessId) throws AeStorageException;

   /**
    * Returns the set of ids for processes with work to be recovered.
    *
    * @return Set
    * @throws AeStorageException
    */
   public Set getRecoveryProcessIds() throws AeStorageException;

   /**
    * Releases any resources associated with the connection.
    *
    * @param aConnection
    * @throws AeStorageException
    */
   public void releaseConnection(IAeProcessStateConnection aConnection) throws AeStorageException;

   /**
    * Removes a process and its variables from storage.
    *
    * @param aProcessId
    * @throws AeStorageException
    */
   public void removeProcess(long aProcessId) throws AeStorageException;
   
   /**
    * Removes processes based upon filter specification.
    *
    * @param aFilter the filter specification
    */
   public int removeProcesses(AeProcessFilter aFilter) throws AeStorageException;

   /**
    * Writes a journal entry for possible recovery in the event of engine failure.
    *
    * 
    * @param aProcessId
    * @param aJournalEntry
    * @throws AeStorageException
    */
   public long writeJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry) throws AeStorageException;

   /**
    * Gets the log from storage
    */
   public String getLog(long aProcessId) throws AeStorageException;

   /**
    * Returns a Reader to the process log.
    * @param aProcessId
    * @throws AeStorageException
    */
   public Reader dumpLog(long aProcessId) throws AeStorageException;

   /**
    * Returns the restart process journal entry for the given process.
    *
    * @param aProcessId
    * @throws AeStorageException
    */
   public AeRestartProcessJournalEntry getRestartProcessJournalEntry(long aProcessId) throws AeStorageException;
}
