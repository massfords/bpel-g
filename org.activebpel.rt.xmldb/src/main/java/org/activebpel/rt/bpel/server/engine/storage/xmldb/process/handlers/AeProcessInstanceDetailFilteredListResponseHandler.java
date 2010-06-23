//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/handlers/AeProcessInstanceDetailFilteredListResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers;

import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBFilteredListResponseHandler;
import org.w3c.dom.Element;

/**
 * A response handler that creates a List of AeProcessInstanceDetail objects.
 */
public class AeProcessInstanceDetailFilteredListResponseHandler extends AeXMLDBFilteredListResponseHandler
{
   /**
    * Constructs a response handler.
    */
   public AeProcessInstanceDetailFilteredListResponseHandler()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      AeProcessInstanceDetail detail = createProcessInstanceDetail();
      AeProcessInstanceDetailResponseHandler.extractProcessInstanceDetailInto(aElement, detail);
      return detail;
   }

   /**
    * Creates the process instance detail object.
    */
   protected AeProcessInstanceDetail createProcessInstanceDetail()
   {
      return new AeProcessInstanceDetail();
   }

}
