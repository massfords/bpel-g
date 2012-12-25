//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/handlers/AeSmallLogHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.IAeProcessElements;
import org.activebpel.rt.xmldb.AeMessages;
import org.w3c.dom.Element;


/**
 * A ResponseHandler that's responsible for walking the entire result set
 * and returning a String that represents the concatenation of all of the logs.
 * As its name implies, this is designed for reading a small number of logs since
 * it reads the entire contents of the logs into memory.
 */
public class AeSmallLogHandler extends AeXMLDBResponseHandler
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler#handleResponse(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse)
    */
   public Object handleResponse(IAeXMLDBXQueryResponse aResponse) throws AeXMLDBException
   {
      try
      {
         StringBuffer sb = new StringBuffer();

         while (aResponse.hasNextElement())
         {
            Element elem = aResponse.nextElement();
            String log = getStringFromElement(elem, IAeProcessElements.PROCESS_LOG);
            sb.ensureCapacity((int) (sb.length() + log.length()));
            sb.append(log);
         }

         return sb.toString();
      }
      catch (Exception ex)
      {
         throw new AeXMLDBException(AeMessages.getString("AeXMLDBLogReader.ERROR_FOUND_IN_LOG_HANDLER"), ex); //$NON-NLS-1$
      }
   }
}
