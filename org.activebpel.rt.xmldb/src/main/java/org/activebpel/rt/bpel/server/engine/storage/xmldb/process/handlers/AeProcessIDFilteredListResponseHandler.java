// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/handlers/AeProcessIDFilteredListResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers;

import java.util.List;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBFilteredListResponseHandler;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;

/**
 * Response handler for converting the XMLDB result set to an array
 * of process IDs (long[]).
 */
public class AeProcessIDFilteredListResponseHandler extends AeXMLDBFilteredListResponseHandler
{
   /**
    * Constructs a response handler.
    */
   public AeProcessIDFilteredListResponseHandler()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBCollectionResponseHandler#handleResponse(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse)
    */
   public Object handleResponse(IAeXMLDBXQueryResponse aResponse) throws AeXMLDBException
   {
      List list = (List) super.handleResponse(aResponse);
      return convertToArray(list);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      String processId = AeXmlUtil.getText(aElement);
      return new Long(processId);
   }
   
   /**
    * Convert the resulting List of Long objects into a long array.
    * 
    * @param aList
    */
   protected long [] convertToArray(List aList)
   {
      long [] rval = new long[aList.size()];
      for (int idx = 0; idx < aList.size(); idx++)
      {
         Long l = (Long) aList.get(idx);
         rval[idx] = l.longValue();
      }
      return rval;
   }
}
