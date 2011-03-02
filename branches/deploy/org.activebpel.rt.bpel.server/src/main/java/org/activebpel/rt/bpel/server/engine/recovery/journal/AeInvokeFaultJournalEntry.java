// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeInvokeFaultJournalEntry.java,v 1.6 2007/11/15 21:06:53 mford Exp $
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
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.storage.AeFaultDeserializer;
import org.activebpel.rt.bpel.impl.storage.AeFaultSerializer;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements journal entry for invoke fault.
 */
public class AeInvokeFaultJournalEntry extends AeAbstractJournalEntry
{
   /** The fault object. */
   private IAeFault mFault;

   /** The associated process properties. */
   private Map mProcessProperties;

   /** Invoke activity transmission id. */
   private long mTransmissionId; 
      
   /**
    * Constructs journal entry to persist to storage.
    */
   public AeInvokeFaultJournalEntry(int aLocationId, long aTransmissionId, IAeFault aFault, Map aProcessProperties)
   {
      super(JOURNAL_INVOKE_FAULT, aLocationId);

      mFault = aFault;
      mProcessProperties = aProcessProperties;
      mTransmissionId = aTransmissionId;
   }

   /**
    * Constructs journal entry to restore from storage.
    */
   public AeInvokeFaultJournalEntry(int aLocationId, long aJournalId, Document aStorageDocument) throws AeMissingStorageDocumentException
   {
      super(JOURNAL_INVOKE_FAULT, aLocationId, aJournalId, aStorageDocument);

      if (aStorageDocument == null)
      {
         throw new AeMissingStorageDocumentException(aLocationId); 
      }
   }

   /**
    * Overrides method to dispatch the fault to the specified process through
    * the recovery engine.
    *
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      String locationPath = aProcess.getLocationPath(getLocationId());

      if (locationPath == null)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeInvokeFaultJournalEntry.ERROR_0", getLocationId())); //$NON-NLS-1$
      }

      IAeBusinessProcessEngineInternal engine = aProcess.getEngine();
      long processId = aProcess.getProcessId();
      IAeFault fault = getFault();
      Map processProperties = getProcessProperties();
      engine.queueInvokeFault(processId, locationPath, getTransmissionId(), fault, processProperties);
   }

   /**
    * Returns the fault object.
    */
   protected IAeFault getFault() throws AeBusinessProcessException
   {
      deserialize();
      return mFault;
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
      
      AeFaultDeserializer deserializer = new AeFaultDeserializer();
      deserializer.setFaultElement(root);

      // Note: Don't need to call deserializer.setProcess() here, because an
      // invoke fault won't have a source object yet (the invoke object will
      // become the fault's source).

      mFault = deserializer.getFault();
      mProcessProperties = deserializeProcessProperties(root);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalSerialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException
   {
      if (aTypeMapping == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeInvokeFaultJournalEntry.ERROR_1")); //$NON-NLS-1$
      }

      AeFaultSerializer serializer = new AeFaultSerializer();
      serializer.setFault(getFault());
      serializer.setTypeMapping(aTypeMapping);

      AeFastDocument result = serializer.getFaultDocument();
      serializeProcessProperties(result.getRootElement(), getProcessProperties());
      result.getRootElement().setAttribute( STATE_TRANSMISSION_ID, String.valueOf( getTransmissionId() ) );

      return result;
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
