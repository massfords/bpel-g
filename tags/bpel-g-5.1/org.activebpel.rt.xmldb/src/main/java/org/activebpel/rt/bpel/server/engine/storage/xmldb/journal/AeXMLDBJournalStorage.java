//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/journal/AeXMLDBJournalStorage.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.journal;

import java.util.LinkedHashMap;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeAbstractXMLDBStorage;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorage;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.activebpel.rt.xmldb.AeMessages;

/**
 * A XMLDB implementation of the Journal storage.
 */
public class AeXMLDBJournalStorage extends AeAbstractXMLDBStorage
{
   /** The prefix into the xmldb config that this storage object uses. */
   protected static final String CONFIG_PREFIX = "JournalStorage"; //$NON-NLS-1$

   /**
    * Constructs the XMLDB Journal Storage given the XMLDB config.
    * 
    * @param aConfig
    */
   public AeXMLDBJournalStorage(AeXMLDBConfig aConfig, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, CONFIG_PREFIX, aStorageImpl);
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
   public long writeJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry, IAeXMLDBConnection aConnection)
         throws AeStorageException
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
      IAeXMLDBConnection connection = getNewConnection();

      try
      {
         return saveJournalEntry(aProcessId, aJournalEntry, aTypeMapping, connection);
      }
      finally
      {
         connection.close();
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
         IAeXMLDBConnection aConnection) throws AeStorageException
   {
      try
      {
         AeFastDocument document = (aJournalEntry != null) ? aJournalEntry.serialize(aTypeMapping) : null;
         LinkedHashMap params = new LinkedHashMap(4);
         params.put(IAeJournalElements.PROCESS_ID, new Long(aProcessId));
         params.put(IAeJournalElements.LOCATION_PATH_ID, (aJournalEntry == null) ? new Integer(0): new Integer(aJournalEntry.getLocationId()) );
         params.put(IAeJournalElements.ENTRY_TYPE, (aJournalEntry == null) ? new Integer(0) : new Integer(aJournalEntry.getEntryType()));
         params.put(IAeJournalElements.ENTRY_DOCUMENT, (document == null) ? (Object) IAeXMLDBStorage.NULL_DOCUMENT : document);

         // Insert the document and return the journal id.
         return insertDocument(IAeJournalConfigKeys.INSERT_JOURNAL_ENTRY, params, aConnection);
      }
      catch (AeBusinessProcessException e)
      {
         throw new AeStorageException(AeMessages.getString("AeXMLDBJournalStorage.ERROR_SERIALIZING_JOURNAL_ENTRY"), e); //$NON-NLS-1$
      }
   }
   
   /**
    * Deletes journal id, based on a new connection.
    * @param aJournalId
    * @throws AeStorageException
    */
   public boolean deleteJournalEntry(long aJournalId) throws AeStorageException
   {
      IAeXMLDBConnection connection = getNewConnection();
      try
      {
         return deleteJournalEntry(aJournalId, connection);
      }
      finally
      {
         connection.close();
      }      
   }
   
   /**
    * Deletes a journal entry given journal id and connection.
    * @param aJournalId
    * @param aConnection
    * @throws AeStorageException
    */
   public boolean deleteJournalEntry(long aJournalId, IAeXMLDBConnection aConnection) throws AeStorageException
   {
      try
      {
         Object[] params = { new Long(aJournalId) };
         return deleteDocuments(IAeJournalConfigKeys.DELETE_JOURNAL_ENTRY, params, aConnection) > 0;         
      }
      catch (Exception e)
      {
         throw new AeStorageException(AeMessages.getString("AeXMLDBJournalStorage.ERROR_DELETING_JOURNAL_ENTRY"), e); //$NON-NLS-1$
      }      
   }   
}
