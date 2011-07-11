//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/invokers/AeSOAPInvoker.java,v 1.8 2008/02/17 21:29:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.invokers; 

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.bpel.handlers.AeAttachmentUtil;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceAttachment;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.message.SOAPHeaderElement;
import org.w3c.dom.Document;

/**
 * Base class for RPC and DOC invokers. 
 */
public abstract class AeSOAPInvoker implements IAeInvoker
{

	private MessageFactory mMessageFactory;
   
   /**
    * Extracts message parts from the output message 
    * @param aContext
    * @param outputMsg
    * @throws AeException
    */
   protected void extractPartsFromHeader(AeAxisInvokeContext aContext, AeWebServiceMessageData outputMsg) throws AeException
   {
      try
      {
         for (Iterator<String> iter = aContext.getOutputHeaderParts().iterator(); iter.hasNext();)
         {
            String partName = iter.next();
            Part part = aContext.getOperation().getOutput().getMessage().getPart(partName);
            QName elementQName = part.getElementName();
            if (elementQName != null)
            {
               for(@SuppressWarnings("unchecked")
            		   Iterator<SOAPHeaderElement> it = aContext.getCall().getResponseMessage().getSOAPHeader().examineAllHeaderElements(); it.hasNext(); )
               {
                  SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
                  if (headerElement.getQName().equals(elementQName))
                  {
                     Document doc = headerElement.getAsDOM().getOwnerDocument();
                     outputMsg.setData(part.getName(), doc);
                     break;
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         throw new AeException(e.getLocalizedMessage(), e);
      }
   }
   
   /**
    * Creates the container for the response or null if it's a one-way
    * @param aContext
    */
   protected AeWebServiceMessageData createOutputMessageData(AeAxisInvokeContext aContext)
   {
      if (!aContext.getInvoke().isOneWay())
      {
         QName outMsgQName = aContext.getOperation().getOutput().getMessage().getQName();
         return new AeWebServiceMessageData(outMsgQName); 
      }
      return null;
   }
   
   /**
    * Returns the map of message parts
    * 
    * @param aContext
    * @throws AeException
    */
   protected Map<String,Object> getMessageData(AeAxisInvokeContext aContext) throws AeException
   {
      return aContext.getInvoke().getInputMessageData().getMessageData();
   }
   
   /**
    * Adds attachments to the invoke context soap message for delivery
    * 
    * @param aInvokeContext
    * @return List of attachments added
    * @throws AeException
    */
   protected List<IAeWebServiceAttachment> addAttachments(AeAxisInvokeContext aInvokeContext) throws AeException
   {
      IAeWebServiceMessageData inputMessageData = aInvokeContext.getInvoke().getInputMessageData();

      List<IAeWebServiceAttachment> attachments = inputMessageData.getAttachments();
      
      
      if (attachments != null)
      {
         SOAPMessage msg;
         try
         {
            msg = getMessageFactory().createMessage();
         }
         catch (SOAPException ex1)
         {
           throw new AeException(ex1);
         }
         
         for (Iterator<IAeWebServiceAttachment> itr = attachments.iterator();itr.hasNext();)
         {
        	IAeWebServiceAttachment attachment = itr.next();
            DataHandler dh = new DataHandler(new AeAttachmentDataSource((IAeWebServiceAttachment)attachment));
            AttachmentPart ap = (AttachmentPart)msg.createAttachmentPart(dh);
            ap.setContentId(((IAeWebServiceAttachment)attachment).getContentId());
            aInvokeContext.getCall().addAttachmentPart(ap);
         }
      }
      
      return attachments;
   }
   
   /**
    * Close attachment streams
    */
   protected void closeAttachmentStreams(List<IAeWebServiceAttachment> aAttachments)
   {
      if(aAttachments != null)
      {
         // close attachment streams of the message sent.
         for (Iterator<IAeWebServiceAttachment> itr = aAttachments.iterator();itr.hasNext();)
         {
            AeCloser.close(itr.next().getContent()); 
         }  
      }
   }
   
   /**
    * Populates the response message with attachments received in the response soap message
    * 
    * @param aInvokeContext
    * @param responseMessage
    * @throws AeException
    */
   protected void receiveAttachments(AeAxisInvokeContext aInvokeContext, AeWebServiceMessageData responseMessage) throws AeException
   {
      try
      {
         if(responseMessage == null) return;
         
         // get the attachments of the response message
         responseMessage.setAttachments(AeAttachmentUtil.soap2wsioAttachments(aInvokeContext.getCall().getResponseMessage()));
      }
      catch (Exception ex1)
      {
        throw new AeException(ex1);
      }
   }

public MessageFactory getMessageFactory() {
	return mMessageFactory;
}

public void setMessageFactory(MessageFactory aMessageFactory) {
	mMessageFactory = aMessageFactory;
}
}
 