// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/receivers/AeAxisReceiveHandler.java,v 1.5 2008/02/17 21:29:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.receivers;

import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeDataConverter;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.server.engine.receive.AeExtendedMessageContext;
import org.activebpel.rt.bpel.server.engine.receive.AeSOAPReceiveHandler;
import org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.constants.Use;
import org.apache.axis.message.SOAPEnvelope;
import org.w3c.dom.Document;

/**
 * Base Receive handler for SOAP bindings using Axis 1.x
 */
public abstract class AeAxisReceiveHandler extends AeSOAPReceiveHandler
{
   /**
    * No arg constructor
    *
    */
   public AeAxisReceiveHandler()
   {
   }
   
   /**
    * Checks that we have a single document with a root node of soapenv:Envelope 
    * and that we've passed the Axis message context and Use as context properties 
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.AeAbstractReceiveHandler#validateInputData(org.activebpel.rt.bpel.impl.IAeProcessPlan, IAeExtendedMessageContext, org.w3c.dom.Document[])
    */
   public void validateInputData(IAeProcessPlan aPlan, IAeExtendedMessageContext aContext, Document[] aDocArray) throws AeBusinessProcessException
   {
      validateContextData(aContext);
      super.validateInputData(aPlan, aContext, aDocArray);
   }
   
   /**
    * Checks that we have a single document with a root node of soapenv:Envelope 
    * and that we've passed the Axis message context and Use as context properties 
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.AeAbstractReceiveHandler#validateInputData(org.activebpel.rt.bpel.impl.IAeProcessPlan, IAeExtendedMessageContext, org.w3c.dom.Document[])
    */
   public void validateInputData(IAeProcessPlan aPlan, IAeExtendedMessageContext aContext, IAeWebServiceMessageData aData) throws AeBusinessProcessException
   {
      validateContextData(aContext);
      super.validateInputData(aPlan, aContext, aData);
   }

   /**
    * Validates the message context properties
    * 
    * @param aContext
    * @throws AeBusinessProcessException
    */
   protected void validateContextData(IAeExtendedMessageContext aContext) throws AeBusinessProcessException
   {
      MessageContext axisContext = (MessageContext) aContext.getProperty(MessageContext.class.getName());
      if (axisContext == null)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeAxisReceiveHandler.0", MessageContext.class.getName())); //$NON-NLS-1$
      }

      Use use = (Use) aContext.getProperty(Use.class.getName());
      if (use == null)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeAxisReceiveHandler.0", Use.class.getName())); //$NON-NLS-1$
      }
   }
   
   /**
    * Deserializes a SOAP envelope and optional SOAP Attachments into AeMessageData instance .
    * Input data type: IAeWebServiceMessageData
    * Output data type: soapenv:Envelope
    *
    * The message data within the response will have a type of soapenv:Envelope and a single 
    * element containing the SOAPEnvelope document with a key of javax.xml.soap.SOAPEnvelope. 
    * 
    * @see org.activebpel.rt.bpel.impl.IAeReceiveHandler#handleReceiveData(org.w3c.dom.Document[], org.activebpel.wsio.receive.IAeMessageContext)
    */
   public IAeMessageData mapInputData(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, IAeWebServiceMessageData aData) throws AeBusinessProcessException
   {
      return mapInputFromSOAP(aPlan, aContext, aData);
   }
   
   /**
    * Deserializes a SOAP envelope into AeMessageData instance.
    * Input data type: soapenv:Envelope
    * Output data type: soapenv:Envelope
    *
    * The message data within the response will have a type of soapenv:Envelope and a single 
    * element containing the SOAPEnvelope document with a key of javax.xml.soap.SOAPEnvelope. 
    * 
    * @see org.activebpel.rt.bpel.impl.IAeReceiveHandler#handleReceiveData(org.w3c.dom.Document[], org.activebpel.wsio.receive.IAeMessageContext)
    */
   public IAeMessageData mapInputData(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, Document[] aDocArray) throws AeBusinessProcessException
   {
      try
      {
         MessageContext axisContext = (MessageContext) aContext.getProperty(MessageContext.class.getName());
         org.apache.axis.Message msg = new org.apache.axis.Message(AeXmlUtil.serialize(aDocArray[0].getDocumentElement()));
         msg.setMessageContext(axisContext);
         SOAPEnvelope reqEnv = msg.getSOAPEnvelope();
         
         return mapInputFromSOAP(aPlan, aContext, reqEnv);
      }
      catch (AxisFault af)
      {
         throw new AeBusinessProcessException(af.getLocalizedMessage(), af);
      }
   }

   /**
    * Abstract method that does the actual mapping for RPC or Document styles
    * 
    * @param aPlan
    * @param aContext
    * @param aEnv
    */
   protected abstract IAeMessageData mapInputFromSOAP(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, SOAPEnvelope aEnv) throws AeBusinessProcessException;
   
   /**
    * Wrapper method that adds SOAP attachments as extensions to the IAeMesssageData 
    * 
    * @param aPlan
    * @param aContext
    * @param aData
    */
   protected IAeMessageData mapInputFromSOAP(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, IAeWebServiceMessageData aData) throws AeBusinessProcessException
   {
      // First get the standard SOAP Envelope
      SOAPEnvelope reqEnv = (SOAPEnvelope) aData.getMessageData().get(javax.xml.soap.SOAPEnvelope.class.getName());
      IAeMessageData extendedData = mapInputFromSOAP(aPlan, aContext, reqEnv);
            
      // map attachments
      IAeMessageData attachmentData = AeDataConverter.convert(aData);
      if (attachmentData.hasAttachments())
         extendedData.setAttachmentContainer(attachmentData.getAttachmentContainer());

      return extendedData;
   }
}
