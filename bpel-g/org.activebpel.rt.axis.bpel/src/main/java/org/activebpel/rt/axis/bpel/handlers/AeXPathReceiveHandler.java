//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeXPathReceiveHandler.java,v 1.10 2008/03/18 16:18:53 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import java.util.HashMap;
import java.util.Iterator;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.jaxen.XPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Maps SOAP Headers into process variable properties
 */
public class AeXPathReceiveHandler extends AeXPathHandler
{
   /**
    * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
    */
   public void invoke(MessageContext aMsgContext) throws AxisFault
   {
      try
      {
         // Get xpaths
         HashMap xpaths = getXpaths(aMsgContext);
         // Get the message
         Message msg = aMsgContext.getCurrentMessage();
         if (msg == null)
            return;
         if (AeUtil.isNullOrEmpty(msg.getSOAPPartAsString()))
            return;
         
         // get the SOAPEnvelope
         SOAPEnvelope reqEnv = msg.getSOAPEnvelope();
         Document domReq = reqEnv.getAsDocument();

         // See if we need to create an element for mapped headers
         Element headers = (Element) aMsgContext.getProperty(AE_CONTEXT_MAPPED_PROPERTIES);
         if (headers == null)
         {
            Document headerDoc = AeXmlUtil.newDocument();
            headers = headerDoc.createElementNS(IAeConstants.ABX_NAMESPACE_URI, "abx:Headers"); //$NON-NLS-1$
            headers.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:abx", IAeConstants.ABX_NAMESPACE_URI ); //$NON-NLS-1$
            // get passthrough option
            headers.setAttribute("passthrough", (String) getOption("passthrough")); //$NON-NLS-1$ //$NON-NLS-2$
            headerDoc.appendChild(headers);
         }
         // Add selected nodes to operation element
         for (Iterator it = xpaths.keySet().iterator(); it.hasNext();)
         {
            String key = (String) it.next();
            XPath xpath = (XPath) xpaths.get(key);
            Element selected = (Element) xpath.selectSingleNode(domReq);
            if (selected != null)
            {
               Element target = headers.getOwnerDocument().createElementNS(selected.getNamespaceURI(), selected.getNodeName());
               AeXmlUtil.copyNodeContents(selected, target);
               headers.appendChild(headers.getOwnerDocument().importNode(target, true));
            }
         }
         aMsgContext.setProperty(AE_CONTEXT_MAPPED_PROPERTIES, headers);
      }
      catch (Exception e)
      {
         throw new AxisFault(AeMessages.getString("AeXPathReceiveHandler.Error_0"), e); //$NON-NLS-1$
      }
   }
}
