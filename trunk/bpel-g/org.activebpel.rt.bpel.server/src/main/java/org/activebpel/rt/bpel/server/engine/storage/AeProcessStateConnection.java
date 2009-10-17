// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeProcessStateConnection.java,v 1.6 2008/02/17 21:38:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Date;
import java.util.Iterator;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateConnectionProvider;
import org.activebpel.rt.bpel.server.logging.IAeProcessLogEntry;
import org.activebpel.rt.util.AeLongSet;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Document;

/**
 * A delegating implementation of a process state connection. This class delegates all of the database calls
 * to an instance of IAeProcessStateConnectionDelegate. The purpose of this class is to encapsulate storage
 * 'logic' so that it can be shared across multiple storage implementations (such as SQL and Tamino).
 */
public class AeProcessStateConnection extends AeAbstractStorage implements IAeProcessStateConnection
{
   /** Set of stored variables. */
   private IAeLocationVersionSet mStoredVariablesSet;

   /**
    * Default constructor that takes the process state connection provider to use.
    * 
    * @param aProvider
    */
   public AeProcessStateConnection(IAeProcessStateConnectionProvider aProvider)
   {
      super(aProvider);
   }

   /**
    * Convenience method to get the storage provider cast to a process state connection provider.
    */
   protected IAeProcessStateConnectionProvider getProcessStateConnectionProvider()
   {
      return (IAeProcessStateConnectionProvider) getProvider();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#close()
    */
   public void close() throws AeStorageException
   {
      getProcessStateConnectionProvider().close();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#commit()
    */
   public void commit() throws AeStorageException
   {
      getProcessStateConnectionProvider().commit();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#getProcessDocument()
    */
   public Document getProcessDocument() throws AeStorageException
   {
      return getProcessStateConnectionProvider().getProcessDocument();
   }

   /**
    * Returns set of stored variable location ids.
    *
    */
   protected IAeLocationVersionSet getStoredVariablesSet() throws AeStorageException
   {
      if (mStoredVariablesSet == null)
      {
         // Run the query and save the set.
         mStoredVariablesSet = getProcessStateConnectionProvider().getProcessVariables();
      }

      return mStoredVariablesSet;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#getVariableDocument(long, int)
    */
   public Document getVariableDocument(long aLocationId, int aVersionNumber) throws AeStorageException
   {
      return getProcessStateConnectionProvider().getVariableDocument(aLocationId, aVersionNumber);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#isStoredVariable(long, int)
    */
   public boolean isStoredVariable(long aLocationId, int aVersionNumber) throws AeStorageException
   {
      return getStoredVariablesSet().contains(aLocationId, aVersionNumber);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#removeJournalEntries(org.activebpel.rt.util.AeLongSet)
    */
   public void removeJournalEntries(AeLongSet aJournalIds) throws AeStorageException
   {
      getProcessStateConnectionProvider().removeJournalEntries(aJournalIds);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#rollback()
    */
   public void rollback() throws AeStorageException
   {
      getProcessStateConnectionProvider().rollback();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#saveProcess(org.activebpel.rt.bpel.impl.fastdom.AeFastDocument, int, int,  java.util.Date, java.util.Date, int)
    */
   public void saveProcess(AeFastDocument aDocument, int aProcessState, int aProcessStateReason, Date aStartDate, Date aEndDate, int aPendingInvokesCount) throws AeStorageException
   {
      getProcessStateConnectionProvider().saveProcess(aDocument, aProcessState, aProcessStateReason, aStartDate, aEndDate, aPendingInvokesCount);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#saveVariable(org.activebpel.rt.bpel.IAeBusinessProcess, org.activebpel.rt.bpel.IAeVariable, org.activebpel.rt.bpel.impl.fastdom.AeFastDocument)
    */
   public void saveVariable(IAeBusinessProcess aProcess, IAeVariable aVariable, AeFastDocument aDocument) throws AeStorageException
   {
      int locationId = aVariable.getLocationId();
      int versionNumber = aVariable.getVersionNumber();
      if (isStoredVariable(locationId, versionNumber))
      {
         Object[] errorParams = {
               new Long(aProcess.getProcessId()),
               new Long(locationId),
               new Integer(versionNumber)
         };
         throw new AeStorageException(AeMessages.format("AeDelegatingProcessStateConnection.VARIABLE_ALREADY_EXISTS_ERROR", //$NON-NLS-1$
               errorParams));
      }

      getProcessStateConnectionProvider().saveVariable(aDocument, locationId, versionNumber);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#trimStoredVariables(org.activebpel.rt.bpel.server.engine.storage.IAeLocationVersionSet)
    */
   public void trimStoredVariables(IAeLocationVersionSet aKeepSet) throws AeStorageException
   {
      for (Iterator i = getStoredVariablesSet().iterator(); i.hasNext(); )
      {
         IAeLocationVersionSet.IEntry entry = (IAeLocationVersionSet.IEntry) i.next();
         if (!aKeepSet.contains(entry))
         {
            getProcessStateConnectionProvider().trimStoredVariable(entry.getLocationId(), entry.getVersionNumber());
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#saveLog(org.activebpel.rt.bpel.server.logging.IAeProcessLogEntry)
    */
   public void saveLog(IAeProcessLogEntry aLogEntry) throws AeStorageException
   {
      if ((aLogEntry != null) && AeUtil.notNullOrEmpty(aLogEntry.getLog()))
      {
         getProcessStateConnectionProvider().saveLog(aLogEntry.getLog(), aLogEntry.getLineCount());
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection#updateJournalEntryType(long, int)
    */
   public void updateJournalEntryType(long aJournalId, int aEntryType) throws AeStorageException
   {
      getProcessStateConnectionProvider().updateJournalEntryType(aJournalId, aEntryType);
   }
}
