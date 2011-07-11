//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeCancelProcessEntry.java,v 1.1 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal.coord; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeJournalEntryAdapter;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.w3c.dom.Document;

/**
 * Journal entry for the cancel process signal 
 */
public class AeCancelProcessEntry extends AeJournalEntryAdapter
{
   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. 
    */
   public AeCancelProcessEntry()
   {
      super(IAeJournalEntry.JOURNAL_CANCEL_PROCESS);
   }
   
   /**
    * Ctor that's called by the journal factory to deserialize the entry from the
    * data stored in the db. The storage document will contained the serialized
    * form of the entry.
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeCancelProcessEntry(long aJournalId, Document aStorageDocument)
   {
      super(IAeJournalEntry.JOURNAL_CANCEL_PROCESS, aJournalId, aStorageDocument);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess)
         throws AeBusinessProcessException
   {
      aProcess.cancelProcess();
   }
}
 