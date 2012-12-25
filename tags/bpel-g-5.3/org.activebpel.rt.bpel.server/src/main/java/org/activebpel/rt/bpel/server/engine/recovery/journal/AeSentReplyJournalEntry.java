// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeSentReplyJournalEntry.java,v 1.4 2007/11/15 21:06:54 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.bpel.impl.storage.AeReplyDeserializer;
import org.activebpel.rt.bpel.impl.storage.AeReplySerializer;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements journal entry for a sent reply.
 */
public class AeSentReplyJournalEntry extends AeAbstractJournalEntry
{
   /** The reply object. */
   private AeReply mReply;

   /** The associated process properties. */
   private Map mProcessProperties;

   /**
    * Constructs sent reply to persist to storage.
    */
   public AeSentReplyJournalEntry(AeReply aReply, Map aProcessProperties)
   {
      // Pass 0 for location id.
      super(JOURNAL_SENT_REPLY, 0);

      mReply = aReply;
      mProcessProperties = aProcessProperties;
   }

   /**
    * Constructs sent reply to restore from storage.
    */
   public AeSentReplyJournalEntry(int aLocationId, long aJournalId, Document aStorageDocument) throws AeMissingStorageDocumentException
   {
      // Pass 0 for location id.
      super(JOURNAL_SENT_REPLY, 0, aJournalId, aStorageDocument);

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
    * Returns the associated process properties.
    */
   public Map getProcessProperties() throws AeBusinessProcessException
   {
      deserialize();
      return mProcessProperties;
   }

   /**
    * Returns the reply object.
    */
   public AeReply getReply() throws AeBusinessProcessException
   {
      deserialize();
      return mReply;
   }

   /**
    * Overrides method to deserialize the storage document to a reply object.
    *
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalDeserialize(org.w3c.dom.Document)
    */
   protected void internalDeserialize(Document aStorageDocument) throws AeBusinessProcessException
   {
      Element root = aStorageDocument.getDocumentElement();

      AeReplyDeserializer deserializer = new AeReplyDeserializer();
      deserializer.setReplyElement(root);

      mReply = deserializer.getReply();
      mProcessProperties = deserializeProcessProperties(root);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalSerialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException
   {
      AeReplySerializer serializer = new AeReplySerializer();
      serializer.setReply(getReply());

      AeFastDocument result = serializer.getReplyDocument();
      serializeProcessProperties(result.getRootElement(), getProcessProperties());

      return result;
   }
}
