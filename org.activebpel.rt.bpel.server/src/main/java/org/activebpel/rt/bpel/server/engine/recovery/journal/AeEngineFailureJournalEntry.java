// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeEngineFailureJournalEntry.java,v 1.2 2007/11/15 21:06:53 mford Exp $
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
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;

/**
 * Implements journal entry for engine failure.
 */
public class AeEngineFailureJournalEntry extends AeAbstractJournalEntry
{
   private static final String ENGINE = "engine"; //$NON-NLS-1$
   private static final String ENGINE_ID = "engine-id"; //$NON-NLS-1$

   /** The engine id. */
   private int mEngineId;

   /**
    * Constructs journal entry to persist to storage.
    */
   public AeEngineFailureJournalEntry(int aEngineId)
   {
      // Pass 0 for location id.
      super(JOURNAL_ENGINE_FAILURE, 0);

      setEngineId(aEngineId);
   }

   /**
    * Constructs journal entry to restore from storage.
    */
   public AeEngineFailureJournalEntry(int aLocationId, long aJournalId, Document aStorageDocument) throws AeMissingStorageDocumentException
   {
      super(JOURNAL_ENGINE_FAILURE, aLocationId, aJournalId, aStorageDocument);

      if (aStorageDocument == null)
      {
         throw new AeMissingStorageDocumentException();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      // Do nothing.
   }
   
   /**
    * Returns the engine id.
    */
   public int getEngineId() throws AeBusinessProcessException
   {
      deserialize();
      return mEngineId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalDeserialize(org.w3c.dom.Document)
    */
   protected void internalDeserialize(Document aStorageDocument) throws AeBusinessProcessException
   {
      try
      {
         int engineId = Integer.parseInt(aStorageDocument.getDocumentElement().getAttribute(ENGINE_ID));
         setEngineId(engineId);
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeEngineFailureJournalEntry.ERROR_InvalidEngineId", getJournalId()), e); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalSerialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException
   {
      AeFastElement element = new AeFastElement(ENGINE);
      element.setAttribute(ENGINE_ID, String.valueOf(getEngineId()));
      return new AeFastDocument(element);
   }
   
   /**
    * Sets the engine id.
    */
   protected void setEngineId(int aGroupId)
   {
      mEngineId = aGroupId;
   }
}
