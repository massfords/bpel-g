//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeWsaHeaderHandler.java,v 1.4 2007/12/11 19:53:51 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import java.util.Iterator;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.axis.bpel.handlers.soap.AeAxisObjectProxyFactory;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.addressing.AeAddressingHeaders;
import org.activebpel.rt.bpel.impl.addressing.AeWsAddressingFactory;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingDeserializer;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPHeaderElement;

/**
 * Axis handler that flags all WS-Addressing headers as understood
 */
public class AeWsaHeaderHandler extends BasicHandler
{
   /**
    * Overrides method to flag all wsa headers as understood 
    * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
    */
   public void invoke(MessageContext aMsgContext) throws AxisFault
   {
      try
      {
         if (aMsgContext.getCurrentMessage() == null)
         {
            return;
         }

         IAeAddressingHeaders wsa = (IAeAddressingHeaders) aMsgContext.getProperty(IAeAddressingHeaders.AE_WSA_HEADERS_PROPERTY);
         SOAPHeader axisHeader = aMsgContext.getCurrentMessage().getSOAPHeader();
         if (wsa == null)
         {
            wsa = deserializeHeaders(axisHeader);
            aMsgContext.setProperty(IAeAddressingHeaders.AE_WSA_HEADERS_PROPERTY, wsa);
         }
         setMustUnderstand(axisHeader);
      }
      catch (SOAPException ex)
      {
         throw new AxisFault(ex.getLocalizedMessage(), ex);
      }
   }
   
   /**
    * Deserializes the WSA headers from SOAPHeader element
    * @param aHeader
    * @throws AxisFault
    * @throws SOAPException
    */
   protected IAeAddressingHeaders deserializeHeaders(SOAPHeader aHeader) throws AxisFault, SOAPException
   {
      /*
       * Note: we proxy the message context here in order to fix some problems with Axis DOM interface 
       * implementations.  Specifically, we need to convert Axis Elements to DOM Elements for
       * persistence.  The Axis Nodes don't import properly (the namespace decls aren't imported).
       * The org.activebpel.rt.axis.bpel.handlers.soap package contains some proxies around the
       * various Axis objects that help with this.
       */
      IAeAddressingDeserializer des = AeWsAddressingFactory.getInstance().getDeserializer(IAeConstants.WSA_NAMESPACE_URI);
      IAeAddressingHeaders wsa = new AeAddressingHeaders(IAeConstants.WSA_NAMESPACE_URI);
      if (aHeader.getChildNodes().getLength() > 0)
      {
         SOAPHeader hdr = AeAxisObjectProxyFactory.getSOAPHeaderProxy((org.apache.axis.message.SOAPHeader) aHeader, SOAPHeader.class);
         try
         {
            wsa = des.deserializeHeaders(hdr, wsa);
         }
         catch (AeBusinessProcessException ex)
         {
            throw new AxisFault(ex.getLocalizedMessage(), ex);
         }
      }
      return wsa;
   }
   
   /**
    * Sets all wsa headers as processed for the mustUnderstand check
    * @param aHeader
    */
   protected void setMustUnderstand(SOAPHeader aHeader)
   {
      IAeAddressingDeserializer des = AeWsAddressingFactory.getInstance().getDeserializer(IAeConstants.WSA_NAMESPACE_URI);
      // Set the mustUnderstand flag
      for (Iterator it = aHeader.getChildElements(); it.hasNext(); )
      {
         SOAPHeaderElement element = (SOAPHeaderElement) it.next();
         if (des.isEndpointHeader(element))
         {
            element.setProcessed(true);
         }
      }
   }

}
