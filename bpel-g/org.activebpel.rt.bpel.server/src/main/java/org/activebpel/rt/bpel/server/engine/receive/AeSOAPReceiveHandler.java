// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/receive/AeSOAPReceiveHandler.java,v 1.8 2008/02/29 20:43:27 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.receive;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessageDataFactory;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.addressing.AeWsAddressingFactory;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingSerializer;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.IAeWsAddressingHeaders;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Abstract receive handler for handling input and output data as SOAPEnvelope documents
 */
public abstract class AeSOAPReceiveHandler extends AeAbstractReceiveHandler
{
   /** Key for property which holds the AeBPELExtendedWSDLDef this service operates against */
   public static final String WSDL_DEF_ENTRY = "org.activebpel.rt.axis.WsdlDefEntry"; //$NON-NLS-1$
   
   /** Key for property which specifies the AePartnerLinkDef which this service operates against*/
   public static final String PARTNER_LINK_ENTRY = "org.activebpel.rt.axis.PartnerLinkEntry";  //$NON-NLS-1$

   /** Key for property which holds the port type QName which service operates against*/
   public static final String PORT_TYPE_ENTRY = "org.activebpel.rt.axis.PortTypeEntry"; //$NON-NLS-1$

   /**
    * no arg constructor
    */
   public AeSOAPReceiveHandler()
   {
   }
   
   /**
    * Checks that we have a single document with a root node of soapenv:Envelope
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.AeAbstractReceiveHandler#validateInputData(org.activebpel.rt.bpel.impl.IAeProcessPlan, IAeExtendedMessageContext, org.w3c.dom.Document[])
    */
   public void validateInputData(IAeProcessPlan aPlan, IAeExtendedMessageContext aContext, Document[] aDocArray) throws AeBusinessProcessException
   {
      if (aDocArray.length != 1)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeSOAPReceiveHandler.ERROR_1")); //$NON-NLS-1$
      }
      String namespace = aDocArray[0].getDocumentElement().getNamespaceURI();
      String localname = aDocArray[0].getDocumentElement().getLocalName();
      
      // check for soapenv:Element as the root element
      if (!IAeConstants.SOAP_NAMESPACE_URI.equals(namespace) || !"Envelope".equals(localname) ) //$NON-NLS-1$
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeSOAPReceiveHandler.ERROR_1")); //$NON-NLS-1$
      }
   }

   /**
    * Checks that we have a single data object of type javax.xml.soap.SOAPEnvelope
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.AeAbstractReceiveHandler#validateInputData(org.activebpel.rt.bpel.impl.IAeProcessPlan, IAeExtendedMessageContext, org.w3c.dom.Document[])
    */
   public void validateInputData(IAeProcessPlan aPlan, IAeExtendedMessageContext aContext, IAeWebServiceMessageData aData) throws AeBusinessProcessException
   {
      Object data = aData.getMessageData().get(SOAPEnvelope.class.getName()); 
      {
         if (!(data instanceof SOAPEnvelope))
            throw new AeBusinessProcessException(AeMessages.getString("AeSOAPReceiveHandler.ERROR_1")); //$NON-NLS-1$
      }
   }
   
   /**
    * Sets up a response message envelope with required wsa headers.
    * 
    * @param aResponseEnv
    * @param aInboundHeaders
    * @throws AeBusinessProcessException
    */
   protected void mapResponseAddressing(SOAPEnvelope aResponseEnv, IAeWsAddressingHeaders aInboundHeaders) throws AeBusinessProcessException
   {
      if (aInboundHeaders == null)
         return;
      
      // Setup the response with required WSA info
      if (aInboundHeaders.getReplyTo() != null)
      {
         IAeAddressingHeaders replyAddressing = AeEngineFactory.getPartnerAddressing().getReplyAddressing(aInboundHeaders, aInboundHeaders.getAction());
         IAeAddressingSerializer ser = AeWsAddressingFactory.getInstance().getSerializer(aInboundHeaders.getSourceNamespace());
         ser.serializeHeaders(replyAddressing, aResponseEnv);
      }
   }
   
   /**
    * Creates a IAeWebServiceResponse from the BPEL engine response and a SOAPEnvelope
    * 
    * The returned response will replace the AeMessageData from the engine with 
    * one that contains the SOAPEnvelope with a type of soapenv:Envelope and any attachments from the response.    
    * 
    * @param aResponse
    * @param aResponseEnv
    * @return the response
    */
   protected AeInvokeResponse createWsResponse(IAeWebServiceResponse aResponse, SOAPEnvelope aResponseEnv) throws AeBusinessProcessException
   {
      QName envName = new QName(aResponseEnv.getNamespaceURI(), aResponseEnv.getLocalName());
      AeWebServiceMessageData responseData = new AeWebServiceMessageData(envName);
      responseData.setData(SOAPEnvelope.class.getName(), aResponseEnv); 
      
      AeInvokeResponse response = new AeInvokeResponse();
      response.setMessageData(responseData);
      
      if (aResponse != null)
      {
         response.setErrorString(aResponse.getErrorString());
         response.setErrorDetail(aResponse.getErrorDetail());
         response.setErrorCode(aResponse.getErrorCode());
         response.setBusinessProcessProperties(aResponse.getBusinessProcessProperties());
         response.setEarlyReply(aResponse.isEarlyReply());

         // Copy attachments.
         if (aResponse.getMessageData() != null)
         {
            responseData.setAttachments(aResponse.getMessageData().getAttachments());
         }
      }

      return response;
   }
   
   /**
    * Maps fault data from the BPEL engine to a SOAPEnvelope containing a Fault envelope.
    * 
    * The message data within the response will have a type of soapenv:Envelope and a single 
    * element containing the SOAPEnvelope document. 
    * 
    * @param aContext
    * @param aResponse
    */
   public IAeWebServiceResponse mapFaultData(IAeExtendedMessageContext aContext, IAeWebServiceResponse aResponse) throws AeBusinessProcessException
   {
      try
      {
         SOAPEnvelope env = createEnvelope();
         
         SOAPBody body = env.getBody();
         SOAPFault fault = (SOAPFault) body.addFault();
         QName code = aResponse.getErrorCode();
         fault.setFaultCode(env.createName(code.getLocalPart(), null, code.getNamespaceURI()));
         fault.setFaultString(aResponse.getErrorString());

         if (aResponse.getMessageData() != null)
         {
            Detail detail = fault.addDetail();
            // Only convert the message data, not attachments, this is for convenience
            // to get easy access to the first part
            // note we used to convert the whole message, but that destroys attachments
            // see defect 3261, because once an attachment is read it is closed and can't
            // be used again.  Below we still pass on the original aResponse so it needs
            // its attachments in good shape.
            // used to be this which overwrites attachments: IAeMessageData data = AeDataConverter.convert(aResponse.getMessageData());
            IAeMessageData data = AeMessageDataFactory.instance().createMessageData(aResponse.getMessageData().getMessageType(),
                                                                                    aResponse.getMessageData().getMessageData());

            // there can only be one part name
            String partName = (String) data.getPartNames().next();
            Object partData = data.getData(partName);

            // If data is of type document, ok to set the data
            if(partData instanceof Document)
            {
               Element element = ((Document)partData).getDocumentElement();
               detail.appendChild(detail.getOwnerDocument().importNode(element, true));
            }
            else
            {
               // The WS-I Basic Profile requires that fault messages' single part
               // is defined as a schema element. If we get here then we're going
               // to be replying with a non-compliant fault message.
               // Our fault routing logic extends the WS-I Basic Profile spec to
               // allow for an xsi:type attribute to indicate the type. This is
               // done to account for non-compliant web services.
               SOAPElement detailElement = detail.addChildElement(partName);
               detailElement.addTextNode(partData.toString());
               
               QName faultMessage = data.getMessageType();
               IAeProcessPlan plan = getProcessPlan(AeMessageContext.convert(aContext));
               AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForMsg(plan, faultMessage);
               if (def != null)
               {
                  Message wsdlMessage = def.getMessage(faultMessage);
                  Part part = wsdlMessage.getPart(partName);
                  // It would be strange indeed if the single part we found was an element
                  // and we were in this code. It's possible if they're running with
                  // validation turned off so in order to avoid a NPE, I'll check for null.
                  if (part.getTypeName() != null)
                  {
                     AeXmlUtil.declareXsiType(detailElement, part.getTypeName());
                  }
               }
            }
         }
         
         return createWsResponse(aResponse, env);
      }
      catch (Exception ex)
      {
         throw new AeBusinessProcessException(ex.getLocalizedMessage(), ex); 
      }
   }

   /**
    * Creates a new SOAPEnvelope
    * 
    *
    * @throws AeBusinessProcessException
    */
   protected SOAPEnvelope createEnvelope() throws AeBusinessProcessException
   {
      return getEnvelope(null);
   }
   
   /**
    * converts a Document to a SOAPEnvelope 
    * 
    * @param aDoc
    * @return envelope
    * @throws AeBusinessProcessException
    */
   protected SOAPEnvelope getEnvelope(Document aDoc) throws AeBusinessProcessException
   {
      SOAPEnvelope env = null;
      try
      {
         SOAPMessage msg = null;
         if (aDoc == null)
         {
            msg = AeEngineFactory.getSOAPMessageFactory().createMessage();
         }
         else
         {
            String envString = AeXmlUtil.serialize(aDoc.getDocumentElement());
            msg = AeEngineFactory.getSOAPMessageFactory().createMessage(null, new ByteArrayInputStream(envString.getBytes()));
         }
         
         env = msg.getSOAPPart().getEnvelope();         
      }
      catch (IOException ie)
      {
         throw new AeBusinessProcessException(ie.getMessage(), ie); 
      }
      catch (SOAPException se)
      {
         throw new AeBusinessProcessException(se.getMessage(), se);
      }

      return env;
   }
}
