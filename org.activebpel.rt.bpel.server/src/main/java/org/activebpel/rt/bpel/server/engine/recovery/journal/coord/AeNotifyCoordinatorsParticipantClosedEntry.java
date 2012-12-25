//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeNotifyCoordinatorsParticipantClosedEntry.java,v 1.2 2008/04/03 21:55:10 mford Exp $
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
 * Journal entry to notify the any coordinators tied to a participant that the
 * participant has closed. This is necessary to in case you have multiple 
 * subprocesses chained together. 
 * 
 * i.e. Consider processes A, B, C, D where A invokes B, B invokes C, and C invokes
 * D. Assume that all invokes are subprocess invokes. If B were to reach a final
 * state, then we need to notify C that it has closed.  
 */
public class AeNotifyCoordinatorsParticipantClosedEntry extends AeJournalEntryAdapter
{

   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. 
    */
   public AeNotifyCoordinatorsParticipantClosedEntry()
   {
      super(IAeJournalEntry.JOURNAL_NOTIFY_COORDINATORS);
   }
   
   /**
    * Ctor used to by the journal factory to deserialize the entry from the db
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeNotifyCoordinatorsParticipantClosedEntry(long aJournalId, Document aStorageDocument)
   {
      super(IAeJournalEntry.JOURNAL_NOTIFY_COORDINATORS, aJournalId, aStorageDocument);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess)
         throws AeBusinessProcessException
   {
      aProcess.getEngine().getCoordinationManager().notifyCoordinatorsParticipantClosed(aProcess.getProcessId(), getJournalId());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#canDispatch(int)
    */
   public boolean canDispatch(int aProcessState)
   {
      return true;
   }
}
