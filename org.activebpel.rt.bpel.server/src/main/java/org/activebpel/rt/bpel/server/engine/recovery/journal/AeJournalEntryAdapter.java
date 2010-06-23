//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeJournalEntryAdapter.java,v 1.1 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;

/**
 * Adapter for the journal entry to simplify subclassing. 
 */
public abstract class AeJournalEntryAdapter extends AeAbstractJournalEntry
{
   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. The values here will be serialized and stored in
    * the db for replay during recovery.
    * @param aEntryType
    */
   public AeJournalEntryAdapter(int aEntryType)
   {
      this(aEntryType, 0);
   }
   
   /**
    * Ctor for entries which only provide an entry type and location id 
    * @param aEntryType
    * @param aLocationId
    */
   public AeJournalEntryAdapter(int aEntryType, int aLocationId)
   {
      this(aEntryType, aLocationId, IAeProcessManager.NULL_JOURNAL_ID, null);
   }
   
   /**
    * Ctor for use by the journal factory when the entry is being deserialized
    * @param aEntryType
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeJournalEntryAdapter(int aEntryType, long aJournalId, Document aStorageDocument)
   {
      this(aEntryType, 0, aJournalId, aStorageDocument);
   }

   /**
    * Ctor for use by the journal factory when the entry is being deserialized
    * @param aEntryType
    * @param aLocationId
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeJournalEntryAdapter(int aEntryType, int aLocationId, long aJournalId, Document aStorageDocument)
   {
      super(aEntryType, aLocationId, aJournalId, aStorageDocument);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalDeserialize(org.w3c.dom.Document)
    */
   protected void internalDeserialize(Document aStorageDocument)
         throws AeBusinessProcessException
   {
      // no-op here
   }

   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping)
         throws AeBusinessProcessException
   {
      // no-op here
      return null;
   }
}
 