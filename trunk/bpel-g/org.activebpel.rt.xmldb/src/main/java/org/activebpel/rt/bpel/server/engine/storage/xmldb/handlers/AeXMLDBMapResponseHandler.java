// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/handlers/AeXMLDBMapResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse;
import org.activebpel.rt.xmldb.AeMessages;
import org.w3c.dom.Element;

/**
 * An abstract base class that returns a Map of objects.
 */
public abstract class AeXMLDBMapResponseHandler extends AeXMLDBResponseHandler
{
   /**
    * Default c'tor.
    */
   public AeXMLDBMapResponseHandler()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler#handleResponse(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse)
    */
   public Object handleResponse(IAeXMLDBXQueryResponse aResponse) throws AeXMLDBException
   {
      try
      {
         Map map = createMap();

         while (aResponse.hasNextElement())
         {
            Element docInst = aResponse.nextElement();
            Object key = getKey(docInst);
            Object value = getValue(docInst);
            map.put(key, value);
         }

         return map;
      }
      catch (Exception e)
      {
         throw new AeXMLDBException(AeMessages.getString("AeXMLDBListResponseHandler.ERROR_ITERATING_THROUGH_XMLDB_RESULT"), e); //$NON-NLS-1$
      }
   }
   
   /**
    * Gets the key to use for the map from the given XMLDB response Element.
    * 
    * @param aElement
    * @throws AeXMLDBException
    */
   protected abstract Object getKey(Element aElement) throws AeXMLDBException;

   /**
    * Gets the value of the Map entry from the given XMLDB response Element.
    * 
    * @param aElement
    * @throws AeXMLDBException
    */
   protected abstract Object getValue(Element aElement) throws AeXMLDBException;

   /**
    * Creates the Map to use for the return value.
    */
   protected Map createMap()
   {
      return new HashMap();
   }
}
