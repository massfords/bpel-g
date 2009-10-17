//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/receivers/AeMSGReceiveHandler.java,v 1.2 2007/12/27 17:58:05 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.receivers;

import java.util.Iterator;
import java.util.Vector;

import javax.wsdl.Part;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.server.engine.receive.AeDefaultReceiveHandler;
import org.activebpel.rt.bpel.server.engine.receive.AeExtendedMessageContext;
import org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.w3c.dom.Document;

/**
 * Receive handler for Document Literal SOAP binding
 */
public class AeMSGReceiveHandler extends AeAxisReceiveHandler
{
   /**
    * No arg constructor
    *
    */
   public AeMSGReceiveHandler()
   {
      
   }

   /**
    * Deserializes a SOAP envelope into AeMessageData instance.
    * 
    * Input data type: soapenv:Envelope
    * Output data type: soapenv:Envelope
    * 
    * A document style binding will attempt to match the operation based on a signature match. 
    *
    * The message data within the response will have a type of soapenv:Envelope and a single 
    * element containing the SOAPEnvelope document with a key of "javax.xml.soap.SOAPEnvelope".
    *  
    * @see org.activebpel.rt.axis.bpel.receivers.AeAxisReceiveHandler#mapInputFromSOAP(org.activebpel.rt.bpel.impl.IAeProcessPlan, org.activebpel.rt.bpel.server.engine.receive.AeExtendedMessageContext, org.apache.axis.message.SOAPEnvelope)
    */
   protected IAeMessageData mapInputFromSOAP(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, SOAPEnvelope aEnv) throws AeBusinessProcessException
   {
      String opName = "";  //$NON-NLS-1$

      try
      {
         Vector bodyElements = aEnv.getBodyElements();
         Document[] data = new Document[bodyElements.size()];
         for (int i = 0; i < data.length; i++)
         {
            SOAPBodyElement bodyElement = (SOAPBodyElement)bodyElements.get(i);
            Document doc = bodyElement.getAsDocument();
            data[i] = doc; 
         }
         
         AeDefaultReceiveHandler bpelHandler = new AeDefaultReceiveHandler();
         return bpelHandler.mapInputData(aPlan, aContext, data);
      }
      catch (Exception e)
      {
         AeException.logError(e, AeMessages.format("AeBpelDocumentHandler.ERROR_4", opName)); //$NON-NLS-1$
         throw new AeBusinessProcessException(e.getLocalizedMessage());
      }
   }

   /**
    * Returned response data consists of a single element 
    * containing the entire SOAP envelope for the response.
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.AeDefaultReceiveHandler#mapOutputData(org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext, org.activebpel.wsio.IAeWebServiceResponse)
    */
   public IAeWebServiceResponse mapOutputData(IAeExtendedMessageContext aContext, IAeWebServiceResponse aResponse) throws AeBusinessProcessException
   {
      try
      {
         MessageContext axisContext = (MessageContext) aContext.getProperty(MessageContext.class.getName());

         // If we don't have a response message, make sure we set one up
         // with the appropriate versions of SOAP and Schema
         SOAPEnvelope resEnv;
         if (axisContext.getResponseMessage() != null)
         {
            resEnv = axisContext.getResponseMessage().getSOAPEnvelope();
         }
         else
         {
            resEnv = new SOAPEnvelope(axisContext.getSOAPConstants(), axisContext.getSchemaVersion());
            axisContext.setResponseMessage(new Message(resEnv));
         }
         
         // Get the WSDL definition for the output message
         AeBPELExtendedWSDLDef def = (AeBPELExtendedWSDLDef) aContext.getProperty(AE_CONTEXT_KEY_WSDL_OUTPUT);
         if (def == null)
         {
            IAeProcessPlan plan = getProcessPlan(AeMessageContext.convert(aContext));
            def = AeWSDLDefHelper.getWSDLDefinitionForMsg(plan, aResponse.getMessageData().getMessageType());
         }
         javax.wsdl.Message outputMessage = def.getMessage(aResponse.getMessageData().getMessageType());
         
         // Loop through all parts for the output message and add an output param to the response body
         for (Iterator iter=outputMessage.getOrderedParts(null).iterator(); iter.hasNext();)
         {
            Part part = (Part) iter.next();

            // Get the data itself, if Document we want to use the Document Element
            Object partData = aResponse.getMessageData().getMessageData().get(part.getName());
            if(partData instanceof Document)
            {
               resEnv.addBodyElement(new SOAPBodyElement(((Document)partData).getDocumentElement()));
            }
            else if (partData != null)
            {
               Name name = resEnv.createName(part.getName());
               SOAPBodyElement soapBody = new SOAPBodyElement(name);
               soapBody.addTextNode(partData.toString());
               resEnv.addBodyElement(soapBody);
            }
         }
         
         mapResponseAddressing(resEnv, aContext.getWsAddressingHeaders());
         
         return createWsResponse(aResponse, resEnv);
      }
      catch (AxisFault e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeBpelDocumentHandler.ERROR_2"), e); //$NON-NLS-1$
      }
      catch (SOAPException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeBpelDocumentHandler.ERROR_3"), e); //$NON-NLS-1$
      }
   }
   
}
