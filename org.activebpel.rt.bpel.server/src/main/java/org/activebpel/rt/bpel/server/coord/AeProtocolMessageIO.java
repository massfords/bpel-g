//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeProtocolMessageIO.java,v 1.2 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.storage.AeFaultDeserializer;
import org.activebpel.rt.bpel.impl.storage.AeFaultSerializer;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.coord.subprocess.AeSpProtocolMessage;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility to serialize/deserialize protocol messages to XML
 */
public class AeProtocolMessageIO
{
   private static final String TAG_COORDMESSAGE = "spCoordinationMessage"; //$NON-NLS-1$
   private static final String ATTR_PROCESSID = "processId"; //$NON-NLS-1$
   private static final String ATTR_COORDID = "coordinationId"; //$NON-NLS-1$
   private static final String ATTR_LOCATION_PATH = "locationPath"; //$NON-NLS-1$
   private static final String ATTR_SIGNAL = "signal"; //$NON-NLS-1$
   private static final String ATTR_JOURNALID = "journalId"; //$NON-NLS-1$
   private static final String ATTR_SOURCE_PID = "sourceProcessId"; //$NON-NLS-1$
   
   
   /**
    * private ctor
    */
   private AeProtocolMessageIO()
   {
   }
   
   /**
    * Serializes the message to xml
    * @param aMessage
    * @param aTypeMapping
    * @throws AeBusinessProcessException
    */
   public static AeFastDocument serialize(IAeProtocolMessage aMessage, AeTypeMapping aTypeMapping) throws AeBusinessProcessException
   {
      if (!(aMessage instanceof AeSpProtocolMessage))
      {
         throw new UnsupportedOperationException(AeMessages.getString("AeProtocolMessageIO.0")); //$NON-NLS-1$
      }
      
      AeSpProtocolMessage spMessage = (AeSpProtocolMessage) aMessage;
      
      AeFastDocument fastDoc = new AeFastDocument();
      AeFastElement element = new AeFastElement(TAG_COORDMESSAGE);
      element.setAttribute(ATTR_PROCESSID, String.valueOf(spMessage.getProcessId()));
      element.setAttribute(ATTR_COORDID, String.valueOf(spMessage.getCoordinationId()));
      element.setAttribute(ATTR_LOCATION_PATH, spMessage.getLocationPath());
      element.setAttribute(ATTR_SIGNAL, spMessage.getSignal());
      element.setAttribute(ATTR_JOURNALID, String.valueOf(spMessage.getJournalId()));
      element.setAttribute(ATTR_SOURCE_PID, String.valueOf(spMessage.getSourceProcessId()));
      
      if (aMessage.getFault() != null)
      {
         AeFaultSerializer faultSerializer = new AeFaultSerializer();
         faultSerializer.setFault(aMessage.getFault());
         faultSerializer.setTypeMapping(aTypeMapping);
         AeFastElement faultElement = faultSerializer.getFaultElement();
         element.appendChild(faultElement);
      }
      
      fastDoc.appendChild(element);
      return fastDoc;
   }
   
   /**
    * Deserializes the message from a document
    * @param aDocument
    * @throws AeBusinessProcessException
    */
   public static IAeProtocolMessage deserialize(Document aDocument) throws AeBusinessProcessException
   {
      Element element = aDocument.getDocumentElement();
      String signal = element.getAttribute(ATTR_SIGNAL);
      String coordinationId = element.getAttribute(ATTR_COORDID);
      long pid = Long.parseLong(element.getAttribute(ATTR_PROCESSID));
      long journalId = Long.parseLong(element.getAttribute(ATTR_JOURNALID));
      long sourcePid = Long.parseLong(element.getAttribute(ATTR_SOURCE_PID));
      String locationPath = element.getAttribute(ATTR_LOCATION_PATH);
      Element faultElement = AeXmlUtil.getFirstSubElement(element);
      IAeFault fault = null;
      if (faultElement != null)
      {
         AeFaultDeserializer deserializer = new AeFaultDeserializer();
         deserializer.setFaultElement(faultElement);
         fault = deserializer.getFault();
      }
      AeSpProtocolMessage spMessage = new AeSpProtocolMessage(signal, coordinationId, fault, pid, locationPath, journalId, sourcePid);
      return spMessage;
   }
}
 