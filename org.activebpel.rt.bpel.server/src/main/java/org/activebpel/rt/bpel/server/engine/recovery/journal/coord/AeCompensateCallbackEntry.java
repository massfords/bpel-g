//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeCompensateCallbackEntry.java,v 1.1 2008/03/28 01:46:20 mford Exp $
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
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.w3c.dom.Document;

/**
 * Journal entry to signal the completion of a compensation 
 */
public class AeCompensateCallbackEntry extends AeAbstractCoordinationEntry
{
   /** tag to use for root element  */
   private static final String TAG_COMPENSATE = "compensateCallback"; //$NON-NLS-1$
   
   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. The values here will be serialized and stored in
    * the db for replay during recovery.
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    */
   public AeCompensateCallbackEntry(String aLocationPath, String aCoordinationId, IAeFault aFault)
   {
      super(IAeJournalEntry.JOURNAL_COMPENSATE_CALLBACK, aLocationPath, aCoordinationId, aFault);
      setLocationPath(aLocationPath);
      setCoordinationId(aCoordinationId);
      setFault(aFault);
   }
   
   /**
    * Ctor that's called by the journal factory to deserialize the entry from the
    * data stored in the db. The storage document will contained the serialized
    * form of the entry.
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeCompensateCallbackEntry(long aJournalId, Document aStorageDocument)
   {
      super(IAeJournalEntry.JOURNAL_COMPENSATE_CALLBACK, aJournalId, aStorageDocument);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess)
         throws AeBusinessProcessException
   {
      IAeBusinessProcessInternal process = ((IAeBusinessProcessInternal) aProcess);
      String path = getLocationPath();
      String coordId = getCoordinationId();
      IAeFault fault = getFault();
      process.compensationCompletedCallback(path, coordId, fault);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeAbstractCoordinationEntry#getTagName()
    */
   protected String getTagName()
   {
      return TAG_COMPENSATE;
   }
}
 