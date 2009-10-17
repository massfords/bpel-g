//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/handlers/AeProcessInstanceDetailResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers;

import java.util.Date;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.process.IAeProcessElements;
import org.w3c.dom.Element;

/**
 * Implements a response handler that return a <code>AeProcessInstanceDetail</code>.
 */
public class AeProcessInstanceDetailResponseHandler extends AeXMLDBSingleObjectResponseHandler
{
   /**
    * Extracts the information found in the element into the proc instance detail object.
    * 
    * @param aElement
    * @param aDetail
    */
   public static void extractProcessInstanceDetailInto(Element aElement, AeProcessInstanceDetail aDetail)
   {
      long processId = getLongFromElement(aElement, IAeProcessElements.PROCESS_ID).longValue();
      QName processName = getQNameFromElement(aElement, IAeProcessElements.PROCESS_NAME);
      Integer processState = getIntFromElement(aElement, IAeProcessElements.PROCESS_STATE);
      if (processState == null)
      {
         processState = new Integer(IAeBusinessProcess.PROCESS_LOADED);
      }
      Integer processStateReason = getIntFromElement(aElement, IAeProcessElements.PROCESS_STATE_REASON);
      if (processStateReason == null)
      {
         processStateReason = new Integer(IAeBusinessProcess.PROCESS_REASON_NONE);
      }

      Date startDate = getDateTimeFromElement(aElement, IAeProcessElements.START_DATE);
      Date endDate = getDateTimeFromElement(aElement, IAeProcessElements.END_DATE);

      if (startDate == null)
      {
         // Always return a non-null start date. The start date will be null
         // if the process hasn't been saved yet.
         startDate = new Date();
      }

      aDetail.setProcessId(processId);
      aDetail.setName(processName);
      aDetail.setState(processState.intValue());
      aDetail.setStateReason(processStateReason.intValue());
      aDetail.setStarted(startDate);
      aDetail.setEnded(endDate);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      AeProcessInstanceDetail detail = createProcessInstanceDetail();
      extractProcessInstanceDetailInto(aElement, detail);
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
