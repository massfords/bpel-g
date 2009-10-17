// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/AeXMLDBProcessStateConnectionProvider.java,v 1.2 2007/09/28 19:55:23 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeLocationVersionSet;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeAbstractXMLDBStorageConnectionProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorage;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers.AeLocationVersionSetResponseHandler;
import org.activebpel.rt.util.AeLongSet;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.w3c.dom.Document;

/**
 * A XMLDB implementation of a process state connection provider.
 */
public class AeXMLDBProcessStateConnectionProvider extends AeAbstractXMLDBStorageConnectionProvider implements
      IAeProcessStateConnectionProvider
{
   /** The storage prefix for this connection class. */
   private static final String STORAGE_PREFIX = "ProcessStateConnection"; //$NON-NLS-1$

   /** The process id. */
   private long mProcessId;

   /**
    * Constructs a xmldb process state connection from the given config.
    * 
    * @param aProcessId
    * @param aContainerManaged
    * @param aConfig
    * @param aStorageImpl
    */
   public AeXMLDBProcessStateConnectionProvider(long aProcessId, boolean aContainerManaged,
         AeXMLDBConfig aConfig, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, STORAGE_PREFIX, aContainerManaged, aStorageImpl);
      setProcessId(aProcessId);
   }

   /**
    * @return Returns the processId.
    */
   protected long getProcessId()
   {
      return mProcessId;
   }

   /**
    * @param aProcessId The processId to set.
    */
   protected void setProcessId(long aProcessId)
   {
      mProcessId = aProcessId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#getProcessDocument()
    */
   public Document getProcessDocument() throws AeStorageException
   {
      Object[] params = { new Long(getProcessId()) };

      return (Document) query(IAeProcessConfigKeys.GET_PROCESS_DOCUMENT, params,
            AeXMLDBResponseHandler.DOCUMENT_RESPONSE_HANDLER, getConnection());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#getProcessVariables()
    */
   public IAeLocationVersionSet getProcessVariables() throws AeStorageException
   {
      Object[] params = { new Long(getProcessId()) };
      IAeXMLDBResponseHandler handler = new AeLocationVersionSetResponseHandler();
      return (IAeLocationVersionSet) query(IAeProcessConfigKeys.GET_PROCESS_VARIABLES, params, handler,
            getConnection());
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

      return (Document) query(IAeProcessConfigKeys.GET_VARIABLE_DOCUMENT, params,
            AeXMLDBResponseHandler.DOCUMENT_RESPONSE_HANDLER, getConnection());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#removeJournalEntries(org.activebpel.rt.util.AeLongSet)
    */
   public void removeJournalEntries(AeLongSet aJournalIds) throws AeStorageException
   {
      deleteLongIdSetDocuments(aJournalIds, IAeProcessConfigKeys.DELETE_JOURNAL_ENTRIES, "$journalid"); //$NON-NLS-1$
   }

   /**
    * Builds a query and deletes the entries referenced in the long id set using the given query key and
    * the parameter needed to build the XMLDB query.
    * @param aQueryKey
    * @param aParamName
    * @throws AeStorageException
    */
   protected void deleteLongIdSetDocuments(AeLongSet aLongIds, String aQueryKey, String aParamName) throws AeStorageException
   {
      if (aLongIds.size() == 0)
      {
         return;
      }
      // Dynamically build the where clause...
      String whereClause = buildLongIdSetWhereClause(aLongIds, aParamName); 
      Object [] params = new Object[] { whereClause };
      deleteDocuments(aQueryKey, params, getConnection());      
   }
      
   /**
    * Creates the WHERE clause for the delete query that will remove all of the entries
    * in the given set. The WHERE clause is to delete journal entries as well as completed transmission ids.
    * 
    * @param aLongIds
    */
   protected String buildLongIdSetWhereClause(AeLongSet aLongIds, String aParamName)
   {
      StringBuffer buff = new StringBuffer();
      synchronized (buff)
      {
         buff.append("where "); //$NON-NLS-1$
         // TODO (EPW): we could possibly improve this query by using the following syntax:
         //       aParamName = (comma separated list of ids)
         // (rather than OR'ing together the clauses
         for (Iterator iter = aLongIds.iterator(); iter.hasNext(); )
         {
            long jid = ((Long) iter.next()).longValue();
            buff.append(aParamName); // eg: $journalid
            buff.append(" = "); //$NON-NLS-1$
            buff.append(jid);
            if (iter.hasNext())
            {
               buff.append(" or "); //$NON-NLS-1$
            }
         }
      }
      return buff.toString();
   }   

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#saveProcess(org.activebpel.rt.bpel.impl.fastdom.AeFastDocument, int, int, java.util.Date, java.util.Date, int)
    */
   public void saveProcess(AeFastDocument aDocument, int aProcessState, int aProcessStateReason, Date aStartDate, Date aEndDate, int aPendingInvokesCount) throws AeStorageException
   {
      Object[] params = {
            new Long(getProcessId()),
            serializeFastDocument(aDocument),
            new Integer(aProcessState),
            new Integer(aProcessStateReason),
            new AeSchemaDateTime(aStartDate),
            new AeSchemaDateTime(new Date()), // ModifiedDate
            new Integer(aPendingInvokesCount)
      };

      updateDocuments(IAeProcessConfigKeys.UPDATE_PROCESS, params, getConnection());

      if (aEndDate != null)
      {
         params = new Object[] { new Long(getProcessId()), new AeSchemaDateTime(aEndDate) };
         updateDocuments(IAeProcessConfigKeys.UPDATE_PROCESS_ENDDATE, params, getConnection());
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#saveVariable(org.activebpel.rt.bpel.impl.fastdom.AeFastDocument, int, int)
    */
   public void saveVariable(AeFastDocument aDocument, int aLocationId, int aVersionNumber) throws AeStorageException
   {
      LinkedHashMap params = new LinkedHashMap(4);
      params.put(IAeProcessElements.PROCESS_ID, new Long(getProcessId()));
      params.put(IAeProcessElements.LOCATION_PATH_ID, new Long(aLocationId));
      params.put(IAeProcessElements.VERSION_NUMBER, new Integer(aVersionNumber));
      if (aDocument == null)
      {
         params.put(IAeProcessElements.VARIABLE_DOCUMENT, IAeXMLDBStorage.NULL_DOCUMENT);
      }
      else
      {
         params.put(IAeProcessElements.VARIABLE_DOCUMENT, aDocument);
      }

      insertDocument(IAeProcessConfigKeys.INSERT_VARIABLE, params, getConnection());
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
      deleteDocuments(IAeProcessConfigKeys.DELETE_VARIABLE, params, getConnection());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#saveLog(java.lang.String, int)
    */
   public void saveLog(String aLogString, int aLineCount) throws AeStorageException
   {
      LinkedHashMap params = new LinkedHashMap(3);
      params.put(IAeProcessElements.PROCESS_ID, new Long(getProcessId()));
      params.put(IAeProcessElements.PROCESS_LOG, aLogString);
      params.put(IAeProcessElements.LINE_COUNT, new Integer(aLineCount));

      insertDocument(IAeProcessConfigKeys.INSERT_PROCESS_LOG, params, getConnection());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider#updateJournalEntryType(long, int)
    */
   public void updateJournalEntryType(long aJournalId, int aEntryType) throws AeStorageException
   {
      Object[] params = { new Long(aJournalId), new Integer(aEntryType) };
      updateDocuments(IAeProcessConfigKeys.UPDATE_JOURNAL_ENTRY_TYPE, params, getConnection());
   }
}
