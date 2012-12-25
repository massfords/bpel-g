//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/urn/handlers/AeURNMappingResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.urn.handlers;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBMapResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.urn.IAeURNElements;
import org.w3c.dom.Element;

/**
 * A response handler that returns a mapping of URN to URL.
 */
public class AeURNMappingResponseHandler extends AeXMLDBMapResponseHandler
{
   /**
    * Default constructor.
    */
   public AeURNMappingResponseHandler()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBMapResponseHandler#getKey(org.w3c.dom.Element)
    */
   protected Object getKey(Element aElement) throws AeXMLDBException
   {
      return getStringFromElement(aElement, IAeURNElements.URN);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBMapResponseHandler#getValue(org.w3c.dom.Element)
    */
   protected Object getValue(Element aElement) throws AeXMLDBException
   {
      return getStringFromElement(aElement, IAeURNElements.URL);
   }
}
