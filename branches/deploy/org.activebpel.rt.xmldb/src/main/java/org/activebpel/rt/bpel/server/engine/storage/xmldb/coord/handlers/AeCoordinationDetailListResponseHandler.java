// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/coord/handlers/AeCoordinationDetailListResponseHandler.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb.coord.handlers;

import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.coord.IAeCoordinationElements;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler;
import org.w3c.dom.Element;

/**
 * A XMLDB response handler that will return a list of coordination details.
 */
public class AeCoordinationDetailListResponseHandler extends AeXMLDBListResponseHandler
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      String coordId = getStringFromElement(aElement, IAeCoordinationElements.COORDINATION_ID);
      String state = getStringFromElement(aElement, IAeCoordinationElements.STATE);
      long processId = getLongFromElement(aElement, IAeCoordinationElements.PROCESS_ID).longValue();
      String locationPath = getStringFromElement(aElement, IAeCoordinationElements.LOCATION_PATH);

      return new AeCoordinationDetail(processId, coordId, state, locationPath);  
   }
}
