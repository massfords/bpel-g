//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/handlers/AeLocationVersionSetResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers;

import org.activebpel.rt.bpel.server.engine.storage.AeLocationVersionSet;
import org.activebpel.rt.bpel.server.engine.storage.IAeLocationVersionSet;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.IAeProcessElements;
import org.activebpel.rt.xmldb.AeMessages;
import org.w3c.dom.Element;

/**
 * Converts a XMLDB response to a set of location id and version number pairs (ie an instance of
 * <code>IAeLocationVersionSet</code>).
 */
public class AeLocationVersionSetResponseHandler extends AeXMLDBResponseHandler
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler#handleResponse(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse)
    */
   public Object handleResponse(IAeXMLDBXQueryResponse aResponse) throws AeXMLDBException
   {
      try
      {
         IAeLocationVersionSet rval = new AeLocationVersionSet();
         while (aResponse.hasNextElement())
         {
            Element element = aResponse.nextElement();
            long locationId = getLongFromElement(element, IAeProcessElements.LOCATION_PATH_ID).longValue();
            int versionNumber = getIntFromElement(element, IAeProcessElements.VERSION_NUMBER).intValue();
            rval.add(locationId, versionNumber);
         }
         return rval;
      }
      catch (Exception ex)
      {
         throw new AeXMLDBException(AeMessages.getString("AeLocationVersionSetResponseHandler.ERROR_HANDLING_LOCVERSET_RESPONSE"), ex); //$NON-NLS-1$
      }
   }
}
