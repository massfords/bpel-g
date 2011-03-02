//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeDeregisterCoordinationEntry.java,v 1.1 2008/03/28 01:46:20 mford Exp $
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
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.w3c.dom.Document;

/**
 * Journal entry for deregistering an activity from the coordination protocol.
 * This happens when the participant reaches a final state and the coordinator
 * can move beyond the scope that contained the activity under coord. 
 */
public class AeDeregisterCoordinationEntry extends AeAbstractCoordinationEntry
{
   /** name for the root element */
   private static final String TAG = "deregisterCoordination"; //$NON-NLS-1$

   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. The values here will be serialized and stored in
    * the db for replay during recovery.
    * @param aLocationPath
    * @param aCoordinationId
    */
   public AeDeregisterCoordinationEntry(String aLocationPath, String aCoordinationId)
   {
      super(IAeJournalEntry.JOURNAL_DEREGISTER_COORDINATION, aLocationPath, aCoordinationId, null);
   }

   /**
    * Ctor that's called by the journal factory to deserialize the entry from the
    * data stored in the db. The storage document will contained the serialized
    * form of the entry.
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeDeregisterCoordinationEntry(long aJournalId, Document aStorageDocument)
   {
      super(IAeJournalEntry.JOURNAL_DEREGISTER_COORDINATION, aJournalId, aStorageDocument);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess)
         throws AeBusinessProcessException
   {
      IAeBusinessProcessInternal process = (IAeBusinessProcessInternal) aProcess;
      process.deregisterCoordination(getLocationPath(), getCoordinationId());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeAbstractCoordinationEntry#getTagName()
    */
   protected String getTagName()
   {
      return TAG;
   }
}
 