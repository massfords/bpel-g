// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeInvokeDataJournalEntry.java,v 1.8 2008/03/20 19:31:21 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeInvokeActivity;
import org.activebpel.rt.bpel.impl.AeMessageDataDeserializer;
import org.activebpel.rt.bpel.impl.AeMessageDataSerializer;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements journal entry for invoke data.
 */
public class AeInvokeDataJournalEntry extends AeAbstractJournalEntry
{
   /** XML tag name for serialization. */
   private static final String TAG_RECEIVED_INVOKE_DATA = "receivedInvokeData"; //$NON-NLS-1$

   /** The message data. */
   private IAeMessageData mMessageData;

   /** The associated process properties. */
   private Map mProcessProperties;

   /** The invoke activity implementation object. */
   private IAeInvokeActivity mInvoke;

   /** The process to use to deserialize the storage document. */
   private IAeBusinessProcess mProcess;
   
   /** Invoke activity transmissiond id. */
   private long mTransmissionId; 

   /**
    * Constructs journal entry to persist to storage.
    */
   public AeInvokeDataJournalEntry(int aLocationId, long aTransmissionId, IAeMessageData aMessageData, Map aProcessProperties)
   {
      super(JOURNAL_INVOKE_DATA, aLocationId);

      mMessageData = aMessageData;
      mProcessProperties = aProcessProperties;
      mTransmissionId = aTransmissionId;
   }

   /**
    * Constructs journal entry to restore from storage.
    */
   public AeInvokeDataJournalEntry(int aLocationId, long aJournalId, Document aStorageDocument)
   {
      super(JOURNAL_INVOKE_DATA, aLocationId, aJournalId, aStorageDocument);
   }

   /**
    * Overrides method to dispatch the invoke data to the specified process
    * through the recovery engine.
    *
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      // Set process to use to deserialize the storage document.
      setProcess(aProcess);
      
      // If we have a journal item for an invoke (one-way or request-response) then
      // we should dispatch that message to the invoke activity. The previous impl
      // of this code was not doing the dispatch for a one-way invoke and instead
      // relied on the one-way invoke re-executing. The problem with this is that
      // any downstream invokes or activities (alarms or receives) that had journal
      // entries would fail to get their journal entries dispatched since the invoke's
      // state would remain in the executing state.
      IAeBusinessProcessEngineInternal engine = aProcess.getEngine();
      long processId = aProcess.getProcessId();
      String locationPath = getInvoke().getLocationPath();
      IAeMessageData messageData = getMessageData();
      Map processProperties = getProcessProperties();
      engine.queueInvokeData(processId, locationPath,  getTransmissionId() , messageData, processProperties);
   }

   /**
    * Returns the invoke activity implementation object.
    */
   protected IAeInvokeActivity getInvoke() throws AeBusinessProcessException
   {
      if (mInvoke == null)
      {
         IAeBpelObject object = getProcess().findBpelObject(getLocationId());

         if (!(object instanceof IAeInvokeActivity))
         {
            throw new AeBusinessProcessException(AeMessages.format("AeInvokeDataJournalEntry.ERROR_0", getLocationId())); //$NON-NLS-1$
         }

         mInvoke = (IAeInvokeActivity) object;
      }

      return mInvoke;
   }

   /**
    * Returns the message data.
    */
   protected IAeMessageData getMessageData() throws AeBusinessProcessException
   {
      deserialize();
      return mMessageData;
   }

   /**
    * Returns the process to use to deserialize the storage document.
    */
   protected IAeBusinessProcess getProcess()
   {
      if (mProcess == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeInvokeDataJournalEntry.ERROR_1")); //$NON-NLS-1$
      }

      return mProcess;
   }

   /**
    * Returns the associated process properties.
    */
   protected Map getProcessProperties() throws AeBusinessProcessException
   {
      deserialize();
      return mProcessProperties;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalDeserialize(org.w3c.dom.Document)
    */
   protected void internalDeserialize(Document aStorageDocument) throws AeBusinessProcessException
   {
      Element root = aStorageDocument.getDocumentElement();
      String txIdString = root.getAttribute( STATE_TRANSMISSION_ID );
      mTransmissionId = 0;
      if ( AeUtil.notNullOrEmpty(txIdString) )
      {
         try
         {
            mTransmissionId = Long.parseLong(txIdString); 
         }
         catch(Exception e)
         {            
         }
      }
      Element messageDataElement = AeXmlUtil.findSubElement(root, STATE_MESSAGEDATA);

      if (messageDataElement == null)
      {
         mMessageData = null;
      }
      else
      {
         // Deserialize the message data.
         AeMessageDataDeserializer deserializer = new AeMessageDataDeserializer();
         deserializer.setMessageDataElement(messageDataElement);
         // no need to set variable or type mapping on deserializer since the invoke will use type mappings when the message is consumed.

         mMessageData = deserializer.getMessageData();
      }

      mProcessProperties = deserializeProcessProperties(root);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalSerialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException
   {
      if (aTypeMapping == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeInvokeDataJournalEntry.ERROR_2")); //$NON-NLS-1$
      }

      AeFastElement root = new AeFastElement(TAG_RECEIVED_INVOKE_DATA);
      root.setAttribute( STATE_TRANSMISSION_ID, String.valueOf( getTransmissionId() ) );
      serializeProcessProperties(root, getProcessProperties());

      if (getMessageData() != null)
      {
         AeMessageDataSerializer serializer = new AeMessageDataSerializer(aTypeMapping);
         serializer.setMessageData(getMessageData());

         AeFastElement messageDataElement = serializer.getMessageDataElement();
         root.appendChild(messageDataElement);
      }

      return new AeFastDocument(root);
   }

   /**
    * Sets the process to use to deserialize the storage document.
    */
   protected void setProcess(IAeBusinessProcess aProcess)
   {
      mProcess = aProcess;
   }

   /**
    * @return Returns the transmission id.
    */
   protected long getTransmissionId() throws AeBusinessProcessException
   {
      deserialize();
      return mTransmissionId;
   }
   
}
