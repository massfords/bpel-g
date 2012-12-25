// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeInvokeTransmittedJournalEntry.java,v 1.7 2008/03/11 03:09:29 mford Exp $
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
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.activebpel.wsio.invoke.IAeTransmission;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Journal entry for durable invokes.
 */
public class AeInvokeTransmittedJournalEntry extends AeAbstractJournalEntry
{
   /** XML tag name for serialization. */
   private static final String TAG_INVOKE_TRANSMISSION = "invokeTransmission"; //$NON-NLS-1$

   /** Durable invoke transmission id. */
   private long mTransmissionId;
   
   /**
    * Constructs a journal entry for seialization.
    * @param aLocationId
    * @param aTransmissionId
    */
   public AeInvokeTransmittedJournalEntry(int aLocationId, long aTransmissionId)
   {
      super(JOURNAL_INVOKE_TRANSMITTED, aLocationId);
      setTransmissionId(aTransmissionId);
   }

   /**
    * Constructs a journal entry for deserialization.
    * @param aLocationId
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeInvokeTransmittedJournalEntry(int aLocationId, long aJournalId,
         Document aStorageDocument)
   {
      super(JOURNAL_INVOKE_TRANSMITTED, aLocationId, aJournalId, aStorageDocument);
   }
   
   /**
    * @return Returns the transmissionId.
    */
   public long getTransmissionId() throws AeBusinessProcessException
   {
      deserialize();
      return mTransmissionId;
   }

   /**
    * @param aTransmissionId The transmissionId to set.
    */
   protected void setTransmissionId(long aTransmissionId)
   {
      mTransmissionId = aTransmissionId;
   }

   /** 
    * Overrides method to extract the transmission id from the document.
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalDeserialize(org.w3c.dom.Document)
    */
   protected void internalDeserialize(Document aStorageDocument) throws AeBusinessProcessException
   {
      Element root = aStorageDocument.getDocumentElement();
      setTransmissionId( Long.parseLong( root.getAttribute( STATE_TRANSMISSION_ID) ) );
   }

   /** 
    * Overrides method to create a invokeTransmission element with a attribute to contain the transmission id. 
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalSerialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException
   {
      AeFastElement root = new AeFastElement(TAG_INVOKE_TRANSMISSION);
      root.setAttribute( STATE_TRANSMISSION_ID, String.valueOf( getTransmissionId() ) );
      return new AeFastDocument(root);
   }

   /** 
    * Overrides method to set the transmission id in the invoke activity implementation. 
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      if (aProcess == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeInvokeTransmittedJournalEntry.MISSING_PROCESS")); //$NON-NLS-1$
      }

      IAeBpelObject invoke = aProcess.findBpelObject(getLocationId());

      if (!(invoke instanceof IAeTransmission))
      {            
         throw new AeBusinessProcessException(AeMessages.format("AeInvokeTransmittedJournalEntry.NO_MATCHING_INVOKE", getLocationId())); //$NON-NLS-1$
      }

      ((IAeTransmission) invoke).setTransmissionId(getTransmissionId());
   }
}
