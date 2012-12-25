//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeCancelSubProcessCompensationEntry.java,v 1.1 2008/03/28 01:46:19 mford Exp $
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
import org.activebpel.rt.bpel.impl.IAeCompensatableActivity;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeJournalEntryAdapter;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.w3c.dom.Document;

/**
 * Journal entry for the cancellation of a subprocess compensation. 
 */
public class AeCancelSubProcessCompensationEntry extends AeJournalEntryAdapter
{
   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. 
    */
   public AeCancelSubProcessCompensationEntry()
   {
      super(IAeJournalEntry.JOURNAL_CANCEL_SUBPROCESS_COMPENSATION);
   }

   /**
    * Ctor that's called by the journal factory to deserialize the entry from the
    * data stored in the db.
    * @param aJournalEntry
    * @param aStorageDocument
    */
   public AeCancelSubProcessCompensationEntry(long aJournalEntry, Document aStorageDocument)
   {
      super(IAeJournalEntry.JOURNAL_CANCEL_SUBPROCESS_COMPENSATION, aJournalEntry, aStorageDocument);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess)
         throws AeBusinessProcessException
   {
      IAeCompensatableActivity compensatableActivity = (IAeCompensatableActivity) aProcess;
      compensatableActivity.terminateCompensationHandler();
   }
}
 