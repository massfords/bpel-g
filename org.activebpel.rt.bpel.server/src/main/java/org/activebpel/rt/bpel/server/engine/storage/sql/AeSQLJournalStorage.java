//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLJournalStorage.java,v 1.5 2008/02/17 21:38:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AeCounter;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.xml.schema.AeTypeMapping;

/**
 * A SQL journal storage.  Used to write journal entries to a SQL database.
 */
public class AeSQLJournalStorage extends AeAbstractSQLStorage
{
   /** The SQL statement prefix for all SQL statements used in this class. */
   public static final String SQLSTATEMENT_PREFIX = "JournalStorage."; //$NON-NLS-1$

   /**
    * Constructs the journal storage given the SQL config to use to resolve SQL statements.
    * 
    * @param aConfig
    */
   protected AeSQLJournalStorage(AeSQLConfig aConfig)
   {
      super(SQLSTATEMENT_PREFIX, aConfig);
   }

   /**
    * Returns next available journal id.
    *
    * @throws AeStorageException
    */
   public long getNextJournalId() throws AeStorageException
   {
      return AeCounter.JOURNAL_ID_COUNTER.getNextValue();
   }

   /**
    * Returns the engine for this process state writer.
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return AeEngineFactory.getEngine();
   }

   /**
    * Saves given {@link IAeJournalEntry} instance for possible recovery in the
    * event of engine failure.
    * 
    * @param aProcessId
    * @param aJournalEntry
    * @param aConnection
    */
   public long writeJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry, Connection aConnection) throws AeStorageException
   {
      return saveJournalEntry(aProcessId, aJournalEntry, getEngine().getTypeMapping(), aConnection);
   }

   /**
    * Saves given {@link IAeJournalEntry} instance for possible recovery in the
    * event of engine failure.
    * 
    * @param aProcessId
    * @param aJournalEntry
    */
   public long saveJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry, AeTypeMapping aTypeMapping)
         throws AeStorageException
   {
      Connection connection = getConnection();
      
      try
      {
         return saveJournalEntry(aProcessId, aJournalEntry, aTypeMapping, connection);
      }
      finally
      {
         AeCloser.close(connection);
      }
   }

   /**
    * Saves the journal entry to the DB.
    * 
    * @param aProcessId
    * @param aJournalEntry
    * @param aTypeMapping
    * @param aConnection
    * @throws AeStorageException
    */
   public long saveJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry, AeTypeMapping aTypeMapping,
         Connection aConnection) throws AeStorageException
   {
      long journalId = getNextJournalId();
      AeFastDocument document;
      
      try
      {
         document = aJournalEntry.serialize(aTypeMapping);
      }
      catch (AeBusinessProcessException e)
      {
         throw new AeStorageException(AeMessages.getString(AeMessages.getString("AeSQLJournalStorage.ERROR_SERIALIZING_JOURNAL_ENTRY")), e); //$NON-NLS-1$
      }

      Object[] params = new Object[]
      {
         new Long(journalId),
         new Long(aProcessId),
         new Integer(aJournalEntry.getEntryType()),
         new Integer(aJournalEntry.getLocationId()),
         (document == null) ? (Object) AeQueryRunner.NULL_CLOB : document 
      };

      update(aConnection, IAeJournalSQLKeys.INSERT_JOURNAL_ENTRY, params);
      return journalId;
   }

}
