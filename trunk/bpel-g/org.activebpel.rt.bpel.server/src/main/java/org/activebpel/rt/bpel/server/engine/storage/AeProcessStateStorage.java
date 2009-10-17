// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeProcessStateStorage.java,v 1.8 2007/09/28 19:48:52 mford Exp $
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
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeRestartProcessJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.util.AeLongMap;

/**
 * A delegating implementation of a process state storage. This class delegates all of the database calls to
 * an instance of IAeProcessStateStorageProvider. The purpose of this class is to encapsulate storage 'logic'
 * so that it can be shared across multiple storage implementations (such as SQL and Tamino).
 */
public class AeProcessStateStorage extends AeAbstractStorage implements IAeProcessStateStorage
{
   /**
    * Default constructor that takes the process state storage provider to use.
    * 
    * @param aProvider
    */
   public AeProcessStateStorage(IAeProcessStateStorageProvider aProvider)
   {
      super(aProvider);
   }

   /**
    * Convenience method to get the storage provider cast to a process state storage provider.
    */
   protected IAeProcessStateStorageProvider getProcessStateStorageProvider()
   {
      return (IAeProcessStateStorageProvider) getProvider();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#createProcess(int, javax.xml.namespace.QName)
    */
   public long createProcess(int aPlanId, QName aProcessName) throws AeStorageException
   {
      return getProcessStateStorageProvider().createProcess(aPlanId, aProcessName);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getConnection(long, boolean)
    */
   public IAeProcessStateConnection getConnection(long aProcessId, boolean aContainerManaged) throws AeStorageException
   {
      IAeProcessStateConnectionProvider provider = getProcessStateStorageProvider().getConnectionProvider(aProcessId, aContainerManaged);
      return new AeProcessStateConnection(provider);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getLog(long)
    */
   public String getLog(long aProcessId) throws AeStorageException
   {
      return getProcessStateStorageProvider().getLog(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#dumpLog(long)
    */
   public Reader dumpLog(long aProcessId) throws AeStorageException
   {
      return getProcessStateStorageProvider().dumpLog(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getNextProcessId()
    */
   public long getNextProcessId() throws AeStorageException
   {
      return getProcessStateStorageProvider().getNextProcessId();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getProcessInstanceDetail(long)
    */
   public AeProcessInstanceDetail getProcessInstanceDetail(long aProcessId) throws AeStorageException
   {
      return getProcessStateStorageProvider().getProcessInstanceDetail(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getProcessList(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public AeProcessListResult getProcessList(AeProcessFilter aFilter) throws AeStorageException
   {
      return getProcessStateStorageProvider().getProcessList(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getProcessCount(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int getProcessCount(AeProcessFilter aFilter) throws AeStorageException
   {
      return getProcessStateStorageProvider().getProcessCount(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getProcessIds(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public long[] getProcessIds(AeProcessFilter aFilter) throws AeStorageException
   {
      return getProcessStateStorageProvider().getProcessIds(aFilter);
   }   
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getProcessName(long)
    */
   public QName getProcessName(long aProcessId) throws AeStorageException
   {
      QName name = getProcessStateStorageProvider().getProcessName(aProcessId);
      if (name == null)
      {
         throw new AeStorageException(MessageFormat.format(AeMessages.getString("AeDelegatingProcessStateStorage.NO_NAME_FOR_PROCESS_ERROR"), //$NON-NLS-1$
               new Object[] { new Long(aProcessId) }));
      }
      return name;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getJournalEntries(long)
    */
   public List getJournalEntries(long aProcessId) throws AeStorageException
   {
      return getProcessStateStorageProvider().getJournalEntries(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getJournalEntriesLocationIdsMap(long)
    */
   public AeLongMap getJournalEntriesLocationIdsMap(long aProcessId) throws AeStorageException
   {
      return getProcessStateStorageProvider().getJournalEntriesLocationIdsMap(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getJournalEntry(long)
    */
   public IAeJournalEntry getJournalEntry(long aJournalId) throws AeStorageException
   {
      return getProcessStateStorageProvider().getJournalEntry(aJournalId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getRecoveryProcessIds()
    */
   public Set getRecoveryProcessIds() throws AeStorageException
   {
      return getProcessStateStorageProvider().getRecoveryProcessIds();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#releaseConnection(org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection)
    */
   public void releaseConnection(IAeProcessStateConnection aConnection) throws AeStorageException
   {
      getProcessStateStorageProvider().releaseConnection(aConnection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#removeProcess(long)
    */
   public void removeProcess(long aProcessId) throws AeStorageException
   {
      IAeStorageConnection connection = getDBConnection();

      try
      {
         removeProcess(aProcessId, connection);
      }
      finally
      {
         connection.close();
      }
   }
      
   /**
    * Removes the process and its variables from the database using the
    * specified database connection.
    *
    * @param aProcessId
    * @param aConnection The database connection to use.
    * @throws AeStorageException
    */
   protected void removeProcess(long aProcessId, IAeStorageConnection aConnection) throws AeStorageException
   {
      getProcessStateStorageProvider().removeProcess(aProcessId, aConnection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#removeProcesses(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int removeProcesses(AeProcessFilter aFilter) throws AeStorageException
   {
      return getProcessStateStorageProvider().removeProcesses(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#writeJournalEntry(long, org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry)
    */
   public long writeJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry) throws AeStorageException
   {
      return getProcessStateStorageProvider().writeJournalEntry(aProcessId, aJournalEntry);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getNextJournalId()
    */
   public long getNextJournalId() throws AeStorageException
   {
      return getProcessStateStorageProvider().getNextJournalId();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage#getRestartProcessJournalEntry(long)
    */
   public AeRestartProcessJournalEntry getRestartProcessJournalEntry(long aProcessId) throws AeStorageException
   {
      return getProcessStateStorageProvider().getRestartProcessJournalEntry(aProcessId);
   }
}
