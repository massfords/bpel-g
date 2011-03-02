//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/transreceive/handlers/AeTransmissionTrackerResponseHandler.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.transreceive.handlers;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.transreceive.IAeTransmissionTrackerElements;
import org.activebpel.rt.bpel.server.transreceive.AeTransmissionTrackerEntry;
import org.w3c.dom.Element;

/**
 * Handler to create a AeTransmissionTrackerEntry object from the XMLDB response.
 *
 */
public class AeTransmissionTrackerResponseHandler extends AeXMLDBSingleObjectResponseHandler
{
   /**
    * Overrides method to create and return a  <code>AeTransmissionTrackerEntry</code> object.
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      long transmissionId = getLongFromElement(aElement, IAeTransmissionTrackerElements.TRANSMISSION_ID).longValue();
      int state  = getIntFromElement(aElement, IAeTransmissionTrackerElements.STATE).intValue();
      String messageId = getStringFromElement(aElement, IAeTransmissionTrackerElements.MESSAGE_ID);
      return new AeTransmissionTrackerEntry(transmissionId, state, messageId);
   }

}
