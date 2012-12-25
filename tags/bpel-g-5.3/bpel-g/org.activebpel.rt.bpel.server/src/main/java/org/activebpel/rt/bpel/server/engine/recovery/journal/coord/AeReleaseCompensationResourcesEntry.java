//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeReleaseCompensationResourcesEntry.java,v 1.1 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal.coord; 

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeJournalEntryAdapter;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.w3c.dom.Document;

/**
 * Journal item to tell the process to resource its compensation resources since
 * the compensation handler is no longer reachable.  
 */
public class AeReleaseCompensationResourcesEntry extends AeJournalEntryAdapter
{

   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. 
    */
   public AeReleaseCompensationResourcesEntry()
   {
      super(IAeJournalEntry.JOURNAL_RELEASE_COMPENSATION_RESOURCES);
   }
   
   /**
    * Ctor used by the journal factory to deserialize the entry
    * @param aJournalEntry
    * @param aStorageDocument
    */
   public AeReleaseCompensationResourcesEntry(long aJournalEntry, Document aStorageDocument)
   {
      super(IAeJournalEntry.JOURNAL_RELEASE_COMPENSATION_RESOURCES, aJournalEntry, aStorageDocument);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess)
   {
      aProcess.releaseCompensationResources();
   }
}
 