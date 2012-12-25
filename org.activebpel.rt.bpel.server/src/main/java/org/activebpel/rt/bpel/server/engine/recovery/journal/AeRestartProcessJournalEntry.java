// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeRestartProcessJournalEntry.java,v 1.3 2007/11/15 21:06:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.w3c.dom.Document;

/**
 * Extends {@link AeInboundReceiveJournalEntry} to hold the inbound receive that
 * created a process but overrides
 * {@link #dispatchToProcess(IAeBusinessProcess)} to do nothing.
 */
public class AeRestartProcessJournalEntry extends AeInboundReceiveJournalEntry
{
   /**
    * Constructs journal entry to persist to storage.
    */
   public AeRestartProcessJournalEntry(int aLocationId, AeInboundReceive aInboundReceive)
   {
      super(JOURNAL_RESTART_PROCESS, aLocationId, aInboundReceive);
   }

   /**
    * Constructs journal entry to restore from persisted document.
    */
   public AeRestartProcessJournalEntry(int aLocationId, long aJournalId, Document aStorageDocument) throws AeMissingStorageDocumentException
   {
      super(JOURNAL_RESTART_PROCESS, aLocationId, aJournalId, aStorageDocument);
   }

   /**
    * Overrides method to do nothing.
    *
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeInboundReceiveJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      // Do not dispatch to process.
   }
}
