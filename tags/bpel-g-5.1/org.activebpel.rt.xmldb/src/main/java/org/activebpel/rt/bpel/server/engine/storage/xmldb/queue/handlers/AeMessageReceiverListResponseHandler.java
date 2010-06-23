//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/queue/handlers/AeMessageReceiverListResponseHandler.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.handlers;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.AeXMLDBQueueStorageProvider;
import org.w3c.dom.Element;


/**
 * A Query Handler that converts a TResponse to a List of AeXMLDBMessageReceiver objects.
 */
public class AeMessageReceiverListResponseHandler extends AeXMLDBListResponseHandler
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      return AeXMLDBQueueStorageProvider.MESSAGE_RECEIVER_HANDLER.readMessageReceiver(aElement);
   }
}