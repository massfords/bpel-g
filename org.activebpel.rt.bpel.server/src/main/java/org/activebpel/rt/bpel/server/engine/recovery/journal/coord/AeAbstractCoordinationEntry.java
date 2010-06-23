//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/coord/AeAbstractCoordinationEntry.java,v 1.1 2008/03/28 01:46:19 mford Exp $
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
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.storage.AeFaultDeserializer;
import org.activebpel.rt.bpel.impl.storage.AeFaultSerializer;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for coordination entries. 
 */
public abstract class AeAbstractCoordinationEntry extends AeAbstractJournalEntry
{
   /** location path of the activity involved in coordination */
   private String mLocationPath;
   /** id of the coordination */
   private String mCoordinationId;
   /** optional fault data associated with coordination recovery */
   private IAeFault mFault;

   /**
    * Ctor that's called by the code that is executing the behavior associated
    * with the journal entry. The values here will be serialized and stored in
    * the db for replay during recovery.
    * @param aEntryType
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    */
   protected AeAbstractCoordinationEntry(int aEntryType, String aLocationPath, String aCoordinationId, IAeFault aFault)
   {
      super(aEntryType, 0);
      setLocationPath(aLocationPath);
      setCoordinationId(aCoordinationId);
      setFault(aFault);
   }
   
   /**
    * Ctor that's called by the journal factory to deserialize the entry from the
    * data stored in the db. The storage document will contained the serialized
    * form of the entry.
    * @param aEntryType
    * @param aJournalId
    * @param aStorageDocument
    */
   protected AeAbstractCoordinationEntry(int aEntryType, long aJournalId, Document aStorageDocument)
   {
      super(aEntryType, 0, aJournalId, aStorageDocument);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalDeserialize(org.w3c.dom.Document)
    */
   protected void internalDeserialize(Document aStorageDocument)
         throws AeBusinessProcessException
   {
      Element root = aStorageDocument.getDocumentElement();
      
      String locationPath = root.getAttribute(STATE_LOC);
      String coordinationId = root.getAttribute(STATE_COORDINATION_ID);
      
      setLocationPath(locationPath);
      setCoordinationId(coordinationId);
      
      if (AeXmlUtil.getFirstSubElement(root) != null)
      {
         AeFaultDeserializer deserializer = new AeFaultDeserializer();
         deserializer.setFaultElement(root);
   
         IAeFault fault = deserializer.getFault();
         setFault(fault);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.AeAbstractJournalEntry#internalSerialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   protected AeFastDocument internalSerialize(AeTypeMapping aTypeMapping)
         throws AeBusinessProcessException
   {
      AeFastDocument result = null;
   
      if (getFault() != null)
      {
         AeFaultSerializer serializer = new AeFaultSerializer();
         serializer.setFault(getFault());
         serializer.setTypeMapping(aTypeMapping);
         result = serializer.getFaultDocument();
      }
      else
      {
         result = new AeFastDocument();
         result.appendChild(new AeFastElement(getTagName()));
      }
   
      result.getRootElement().setAttribute( STATE_LOC, getLocationPath() );
      result.getRootElement().setAttribute( STATE_COORDINATION_ID, getCoordinationId() );
      
      return result;
   }
   
   /**
    * @return the locationPath
    */
   protected String getLocationPath() throws AeBusinessProcessException
   {
      deserialize();
      return mLocationPath;
   }

   /**
    * @param aLocationPath the locationPath to set
    */
   protected void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }

   /**
    * @return the coordinationId
    */
   protected String getCoordinationId() throws AeBusinessProcessException
   {
      deserialize();
      return mCoordinationId;
   }

   /**
    * @param aCoordinationId the coordinationId to set
    */
   protected void setCoordinationId(String aCoordinationId)
   {
      mCoordinationId = aCoordinationId;
   }

   /**
    * @return the fault
    */
   protected IAeFault getFault() throws AeBusinessProcessException
   {
      deserialize();
      return mFault;
   }

   /**
    * @param aFault the fault to set
    */
   protected void setFault(IAeFault aFault)
   {
      mFault = aFault;
   }

   /**
    * Name of the root tag for the serialized entry
    */
   protected abstract String getTagName();
}
 