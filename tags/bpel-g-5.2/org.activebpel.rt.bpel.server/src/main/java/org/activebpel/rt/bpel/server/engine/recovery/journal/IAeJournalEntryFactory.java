// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/IAeJournalEntryFactory.java,v 1.1 2005/07/12 00:25:23 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal;

import org.activebpel.rt.AeException;
import org.w3c.dom.Document;

/**
 * Defines factory interface for creating journal entries from storage.
 */
public interface IAeJournalEntryFactory
{
   /**
    * Returns new journal entry constructed with the given type, location id,
    * and storage document.
    *
    * @param aEntryType
    * @param aLocationId
    * @param aJournalId
    * @param aStorageDocument
    */
   public IAeJournalEntry newJournalEntry(int aEntryType, int aLocationId, long aJournalId, Document aStorageDocument) throws AeException;
}
