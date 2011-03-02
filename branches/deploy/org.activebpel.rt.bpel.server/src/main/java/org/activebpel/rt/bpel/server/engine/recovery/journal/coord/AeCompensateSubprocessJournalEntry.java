//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeCompensateSubprocessJournalEntry.java,v 1.1 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal.coord;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Journal entry for subprocess compensation.
 */
public class AeCompensateSubprocessJournalEntry extends AeAbstractJournalEntry
{
   /** XML tag name for serialization. */
   private static final String TAG_COMPENSATE_SUBPROCESS = "compensateSubprocess"; //$NON-NLS-1$
   /** Coordination id. */
   private String mCoordinationId;

   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. The values here will be serialized and stored in
    * the db for replay during recovery.
    * @param aCoordinationId
    */
   public AeCompensateSubprocessJournalEntry(String aCoordinationId)
   {
      super(JOURNAL_COMPENSATE_SUBPROCESS, 0);
      setCoordinationId(aCoordinationId);
   }

   /**
    * Ctor that's called by the journal factory to deserialize the entry from the
    * data stored in the db. The storage document will contained the serialized
    * form of the entry.
    * @param aLocationId
    * @param aJournalId
    * @param aStorageDocument
    */
   public AeCompensateSubprocessJournalEntry( int aLocationId, long aJournalId, Document aStorageDocument)
   {
      super(JOURNAL_COMPENSATE_SUBPROCESS, aLocationId, aJournalId, aStorageDocument);
   }

   /**
    * @return Returns the coordinationId.
    */
   protected String getCoordinationId() throws AeBusinessProcessException
   {
      deserialize();
      return mCoordinationId;
   }

   /**
    * @param aCoordinationId The coordinationId to set.
    */
   protected void setCoordinationId(String aCoordinationId)
   {
      mCoordinationId = aCoordinationId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalDeserialize(org.w3c.dom.Document)
    */
   protected void internalDeserialize(Document aStorageDocument) throws AeBusinessProcessException
   {
      Element root = aStorageDocument.getDocumentElement();
      setCoordinationId(  root.getAttribute( STATE_COORDINATION_ID)  );
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalSerialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException
   {
      if (aTypeMapping == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeCompensateSubprocessJournalEntry.MISSING_TYPE_MAPPING")); //$NON-NLS-1$
      }
      AeFastElement root = new AeFastElement(TAG_COMPENSATE_SUBPROCESS);
      root.setAttribute( STATE_COORDINATION_ID, String.valueOf( getCoordinationId() ) );
      return new AeFastDocument(root);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#dispatchToProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      try
      {
         IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal) aProcess.getEngine();
         engine.getProcessCoordination().compensateSubProcess( aProcess.getProcessId(), getCoordinationId(), getJournalId(), null, IAeProcessManager.NULL_JOURNAL_ID);
      }
      catch(AeCoordinationException ace)
      {
        throw new AeBusinessProcessException(ace.getMessage(), ace);  
      }
   }
}
