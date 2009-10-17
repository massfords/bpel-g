// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAeProcessStateConnection.java,v 1.19 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Date;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.logging.IAeProcessLogEntry;
import org.activebpel.rt.util.AeLongSet;
import org.w3c.dom.Document;

/**
 * Defines interface for storing and retrieving process state and variable
 * documents.
 */
public interface IAeProcessStateConnection
{
   /**
    * Called to close the connection.
    * 
    * @throws AeStorageException
    */
   public void close() throws AeStorageException;

   /**
    * Makes all changes permanent and releases any database locks currently
    * held by this object.
    *
    * @throws AeStorageException
    */
   public void commit() throws AeStorageException;

   /**
    * Returns the stored state document for a process.
    *
    * @return Document
    * @throws AeStorageException
    */
   public Document getProcessDocument() throws AeStorageException;

   /**
    * Returns the stored state document for a variable.
    *
    * @param aLocationId
    * @param aVersionNumber
    * @return Document
    * @throws AeStorageException
    */
   public Document getVariableDocument(long aLocationId, int aVersionNumber) throws AeStorageException;

   /**
    * Returns <code>true</code> if and only if the specified variable is
    * already stored.
    *
    * @param aLocationId
    * @param aVersionNumber
    * @return boolean
    * @throws AeStorageException
    */
   public boolean isStoredVariable(long aLocationId, int aVersionNumber) throws AeStorageException;

   /**
    * Removes the journal entries with the given ids.
    *
    * @throws AeStorageException
    */
   public void removeJournalEntries(AeLongSet aJournalIds) throws AeStorageException;

   /**
    * Undoes all changes made in the current transaction and releases any
    * database locks currently held by this object.
    */
   public void rollback() throws AeStorageException;

   /**
    * Saves process to storage.
    *
    * @param aDocument The process state document.
    * @param aProcessState
    * @param aProcessStateReason
    * @param aStartDate
    * @param aEndDate
    * @param aPendingInvokesCount
    * @throws AeStorageException
    */
   public void saveProcess(AeFastDocument aDocument, int aProcessState, int aProcessStateReason, Date aStartDate, Date aEndDate, int aPendingInvokesCount) throws AeStorageException;

   /**
    * Saves a process variable to storage.
    *
    * @param aProcess 
    * @param aVariable
    * @param aDocument
    * @throws AeStorageException
    */
   public void saveVariable(IAeBusinessProcess aProcess, IAeVariable aVariable, AeFastDocument aDocument) throws AeStorageException;

   /**
    * Trims the variables stored for the process to the specified set.
    *
    * @param aKeepSet
    * @throws AeStorageException
    */
   public void trimStoredVariables(IAeLocationVersionSet aKeepSet) throws AeStorageException;

   /**
    * Saves the log to storage.
    * @throws AeStorageException
    */
   public void saveLog(IAeProcessLogEntry aLogEntry) throws AeStorageException;

   /**
    * Updates the entry type of the given journal entry.
    *
    * @param aJournalId
    * @param aEntryType
    */
   public void updateJournalEntryType(long aJournalId, int aEntryType) throws AeStorageException;
}
