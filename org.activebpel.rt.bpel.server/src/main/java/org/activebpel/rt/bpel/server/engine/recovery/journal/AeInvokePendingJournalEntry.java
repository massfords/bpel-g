//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeInvokePendingJournalEntry.java,v 1.2 2007/11/15 21:06:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;

/**
 * Implements journal entry for pending invokes.
 */
public class AeInvokePendingJournalEntry extends AeAbstractJournalEntry
{
   /**
    * Constructs journal entry to persist to storage.
    */
   public AeInvokePendingJournalEntry(int aLocationId)
   {
      super(JOURNAL_INVOKE_PENDING, aLocationId);
   }

   /**
    * Constructs journal entry to restore from storage.
    */
   public AeInvokePendingJournalEntry(int aLocationId, long aJournalId, Document aStorageDocument)
   {
      super(JOURNAL_INVOKE_PENDING, aLocationId, aJournalId, aStorageDocument);
   }
   
   /** 
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalDeserialize(org.w3c.dom.Document)
    */
   protected void internalDeserialize(Document aStorageDocument) throws AeBusinessProcessException
   {
      // Nothing to deserialize.
   }

   /** 
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalSerialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException
   {
      // Nothing to serialize.
      return null;
   }

   /** 
    * Overrides method to set the journal id in the invoke activity implementation.
    * 
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      if (aProcess == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeInvokeQueuedJournalEntry.MISSING_PROCESS")); //$NON-NLS-1$
      }

      IAeBpelObject object = aProcess.findBpelObject(getLocationId());

      if (!(object instanceof AeActivityInvokeImpl))
      {            
         throw new AeBusinessProcessException(AeMessages.format("AeInvokeQueuedJournalEntry.NO_MATCHING_INVOKE", getLocationId())); //$NON-NLS-1$
      }

      ((AeActivityInvokeImpl) object).setJournalId(getJournalId());
   }
}
