//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeCoordinatedActivityCompletedEntry.java,v 1.1 2008/03/28 01:46:19 mford Exp $
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
import org.activebpel.rt.bpel.coord.AeCoordinationNotFoundException;
import org.activebpel.rt.bpel.coord.IAeCoordinator;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.w3c.dom.Document;

/**
 * Journal entry that sends a message into the process to report that the 
 * coordination for a specific activity has completed. This entry handles both
 * normal completion and faulted completion depending on the presence of the
 * optional fault data.
 * 
 */
public class AeCoordinatedActivityCompletedEntry extends AeAbstractCoordinationEntry
{
   /** tag to use fo the root element */
   private static final String TAG_COORD_ACTIVITY_COMPLETED = "coordinatedActivityCompleted"; //$NON-NLS-1$
   
   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. The values here will be serialized and stored in
    * the db for replay during recovery.
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    */
   public AeCoordinatedActivityCompletedEntry(String aLocationPath, String aCoordinationId, IAeFault aFault)
   {
      super(IAeJournalEntry.JOURNAL_COORDINATED_ACTIVITY_COMPLETED, aLocationPath, aCoordinationId, aFault);
   }
   
   /**
    * Ctor that's called by the journal factory to deserialize the entry from the
    * data stored in the db. The storage document will contained the serialized
    * form of the entry.
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeCoordinatedActivityCompletedEntry(long aJournalId, Document aStorageDocument)
   {
      super(IAeJournalEntry.JOURNAL_COORDINATED_ACTIVITY_COMPLETED, aJournalId, aStorageDocument);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess)
         throws AeBusinessProcessException
   {
      IAeBusinessProcessInternal process = (IAeBusinessProcessInternal) aProcess;
      try
      {
         IAeCoordinator coordinator = null;
         if (getFault() == null)
         {
            coordinator = process.getEngine().getCoordinationManager().getCoordinator(getCoordinationId());
         }
         process.coordinatedActivityCompleted(getLocationPath(), getCoordinationId(), coordinator, getFault());
      }
      catch (AeCoordinationNotFoundException e)
      {
         throw new AeBusinessProcessException(e.getMessage(), e);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeAbstractCoordinationEntry#getTagName()
    */
   protected String getTagName()
   {
      return TAG_COORD_ACTIVITY_COMPLETED;
   }
}
 