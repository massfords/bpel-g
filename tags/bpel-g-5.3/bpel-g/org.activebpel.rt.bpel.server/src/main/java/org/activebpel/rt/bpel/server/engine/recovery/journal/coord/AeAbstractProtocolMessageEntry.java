//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeAbstractProtocolMessageEntry.java,v 1.1 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal.coord; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.coord.AeProtocolMessageIO;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;

/**
 * Base class for journal entries that contain protocol messages. 
 */
public abstract class AeAbstractProtocolMessageEntry extends AeAbstractJournalEntry
{
   /** message the was journaled */
   private IAeProtocolMessage mMessage;

   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. The values here will be serialized and stored in
    * the db for replay during recovery.
    * @param aEntryType
    * @param aProcessId
    * @param aMessage
    */
   public AeAbstractProtocolMessageEntry(int aEntryType, long aProcessId, IAeProtocolMessage aMessage)
   {
      super(aEntryType, 0);
      setMessage(aMessage);
   }
   
   /**
    * Ctor that's called by the journal factory to deserialize the entry from the
    * data stored in the db. The storage document will contained the serialized
    * form of the entry.
    */
   public AeAbstractProtocolMessageEntry(int aEntryType, long aJournalId, Document aStorageDocument)
   {
      super(aEntryType, 0, aJournalId, aStorageDocument);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalDeserialize(org.w3c.dom.Document)
    */
   protected void internalDeserialize(Document aStorageDocument)
         throws AeBusinessProcessException
   {
      IAeProtocolMessage message = AeProtocolMessageIO.deserialize(aStorageDocument);
      setMessage(message);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalSerialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping)
         throws AeBusinessProcessException
   {
      AeFastDocument fastDoc = AeProtocolMessageIO.serialize(getMessage(), aTypeMapping);
      return fastDoc;
   }

   /**
    * @return the message
    */
   public IAeProtocolMessage getMessage() throws AeBusinessProcessException
   {
      deserialize();
      return mMessage;
   }

   /**
    * @param aMessage the message to set
    */
   public void setMessage(IAeProtocolMessage aMessage)
   {
      mMessage = aMessage;
   }
}
 