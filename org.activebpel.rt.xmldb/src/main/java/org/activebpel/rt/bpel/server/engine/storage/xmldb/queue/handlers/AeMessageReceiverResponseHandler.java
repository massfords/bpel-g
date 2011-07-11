//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/queue/handlers/AeMessageReceiverResponseHandler.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.handlers;

import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.engine.storage.AePersistedMessageReceiver;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageUtil;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.IAeQueueElements;
import org.w3c.dom.Element;


/**
 * A simple query handler that returns a single message receiver from the result set.
 */
public class AeMessageReceiverResponseHandler extends AeXMLDBSingleObjectResponseHandler
{
   /**
    * Converts the given XML element in a message receiver.
    * 
    * @param aElement
    */
   public AeMessageReceiver readMessageReceiver(Element aElement) throws AeXMLDBException
   {
      try
      {
         int queuedReceiveId = getIntFromElement(aElement, IAeQueueElements.QUEUED_RECEIVE_ID).intValue();
         long processId = getLongFromElement(aElement, IAeQueueElements.PROCESS_ID).longValue();
         int locationPathId = getIntFromElement(aElement, IAeQueueElements.LOCATION_PATH_ID).intValue();
         int groupId = getIntFromElement(aElement, IAeQueueElements.GROUP_ID).intValue();
         String operation = getStringFromElement(aElement, IAeQueueElements.OPERATION);
         String plinkName = getStringFromElement(aElement, IAeQueueElements.PARTNER_LINK_NAME);
         QName portType = getQNameFromElement(aElement, IAeQueueElements.PORT_TYPE);
         Element corrPropsElem = getElementFromElement(aElement, IAeQueueElements.CORRELATION_PROPERTIES);
         Map corrProps = AeStorageUtil.deserializeCorrelationProperties(corrPropsElem);
         QName processName = getQNameFromElement(aElement, IAeQueueElements.PROCESS_NAME);
         boolean allowsConcurrency = getBoolFromElement(aElement, IAeQueueElements.ALLOWS_CONCURRENCY).booleanValue();

         // Note: legacy issue here - the partner link id may be null for old message receivers.
         Integer plinkIdInt = getIntFromElement(aElement, IAeQueueElements.PARTNER_LINK_ID);
         if (plinkIdInt == null)
            plinkIdInt = new Integer(-1);

         int partnerLinkId = plinkIdInt.intValue();
         AePartnerLinkOpKey plOpKey = new AePartnerLinkOpKey(plinkName, partnerLinkId, operation);

         return new AePersistedMessageReceiver(queuedReceiveId, processId, processName, plOpKey, portType,
               corrProps, locationPathId, groupId, allowsConcurrency);
      }
      catch (Exception e)
      {
         throw new AeXMLDBException(e);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      return readMessageReceiver(aElement);
   }
}
