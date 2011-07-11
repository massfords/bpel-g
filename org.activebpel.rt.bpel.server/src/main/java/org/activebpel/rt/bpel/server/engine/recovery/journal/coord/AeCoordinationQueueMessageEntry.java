//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeCoordinationQueueMessageEntry.java,v 1.2 2008/04/03 21:55:10 mford Exp $
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
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.w3c.dom.Document;

/**
 * Journal entry for a coordination message that is queued through the coordination
 * manager. 
 */
public class AeCoordinationQueueMessageEntry extends AeAbstractProtocolMessageEntry
{
   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. The values here will be serialized and stored in
    * the db for replay during recovery.
    * @param aProcessId
    * @param aMessage
    */
   public AeCoordinationQueueMessageEntry(long aProcessId, IAeProtocolMessage aMessage)
   {
      super(IAeJournalEntry.JOURNAL_COORDINATION_QUEUE_MESSAGE, aProcessId, aMessage);
   }

   /**
    * Ctor that's called by the journal factory to deserialize the entry from the
    * data stored in the db. The storage document will contained the serialized
    * form of the entry.
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeCoordinationQueueMessageEntry(long aJournalId, Document aStorageDocument)
   {
      super(IAeJournalEntry.JOURNAL_COORDINATION_QUEUE_MESSAGE, aJournalId, aStorageDocument);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess)
         throws AeBusinessProcessException
   {
      IAeCoordinationManagerInternal manager = aProcess.getEngine().getCoordinationManager();
      IAeProtocolMessage message = getMessage();
      manager.dispatch(message, false);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#canDispatch(int)
    */
   public boolean canDispatch(int aProcessState)
   {
      return true;
   }
}
 