//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/invokers/AeDocumentStyleInvoker.java,v 1.7 2007/12/11 19:53:51 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.invokers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Part;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPHeaderElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Calls a document style endpoint.
 */
public class AeDocumentStyleInvoker extends AeSOAPInvoker
{

   
   /**
    * @see org.activebpel.rt.axis.bpel.invokers.IAeInvoker#invoke(org.activebpel.rt.axis.bpel.invokers.AeAxisInvokeContext)
    */
   public void invoke( AeAxisInvokeContext aContext ) throws AeException, RemoteException
   {
      invokeDocumentCall( aContext );
   }
   
   /**
    * Calls a document style endpoint on behalf of passed invoke context.
    * 
    * @param aInvokeContext
    * @throws AeException
    * @throws RemoteException
    */
   protected void invokeDocumentCall( AeAxisInvokeContext aInvokeContext ) throws AeException, RemoteException
   {
      // this document gets used to create element placeholders for the arguments
      // if they're rpc style and not already documents
      Document simpleTypeDoc = null;
      
      List orderedParts = aInvokeContext.getOperation().getInput().getMessage().getOrderedParts(null);
      
      ArrayList list = new ArrayList();
      Map messageData = getMessageData(aInvokeContext);
      List outboundAttachments = addAttachments(aInvokeContext);
      AeWebServiceMessageData outputMsg;
      Vector elems;
      try
      {
         for (Iterator iter = orderedParts.iterator(); iter.hasNext();)
         {
            Part part = (Part) iter.next();
            Object obj = messageData.get(part.getName());
            
            // don't add the part to the body if it is supposed to be a header
            if (aInvokeContext.isInputHeader(part.getName()) && obj instanceof Document)
            {
                Document doc = (Document) obj;
                aInvokeContext.getCall().addHeader(new SOAPHeaderElement(doc.getDocumentElement()));
            }
            else
            {
               if (obj instanceof Document)
               {
                  Element root = ((Document) obj).getDocumentElement();
                  list.add(new SOAPBodyElement(root));
               }
               else
               {
                  if (simpleTypeDoc == null)
                  {
                     simpleTypeDoc = AeXmlUtil.newDocument();
                  }
                  Element e = simpleTypeDoc.createElement(part.getName());
                  e.appendChild(simpleTypeDoc.createTextNode(obj.toString()));
                  SOAPBodyElement body = new SOAPBodyElement(e);
                  list.add(body);
               }
            }
         }
         
         // for document style we receive a vector of body elements from return
         elems = (Vector) aInvokeContext.getCall().invoke(list.toArray());
         
         // outputMsg will be created for request/response only
         outputMsg = createOutputMessageData(aInvokeContext);
      }
      finally
      {
         closeAttachmentStreams(outboundAttachments);
      }
      
      receiveAttachments(aInvokeContext,outputMsg);
      
   
      // if we got a return and expect one then process the body elements
      if (!aInvokeContext.getInvoke().isOneWay())
      {
         if (elems != null)
         {
            int i = 0;               
            for(Iterator iter = aInvokeContext.getOperation().getOutput().getMessage().getOrderedParts(null).iterator(); iter.hasNext(); )
            {
               Part part = (Part)iter.next();
               if (!aInvokeContext.isOutputHeader(part.getName()))
               {
                  SOAPBodyElement elem = (SOAPBodyElement) elems.get(i++);
                  Document doc;
                  try
                  {
                     doc = elem.getAsDocument();
                  }
                  catch (Exception ex)
                  {
                     throw new AeBusinessProcessException(AeMessages.getString("AeInvokeHandler.ERROR_1"), ex); //$NON-NLS-1$
                  }
                  Element root = doc.getDocumentElement();
                  if(root != null)
                  {
                     if(isSimpleType(root))
                        outputMsg.setData(part.getName(), AeXmlUtil.getText(root));
                     else
                        outputMsg.setData(part.getName(), doc);
                  }
                  else
                  {
                     outputMsg.setData(part.getName(), null);
                  }
               }
            }
         }
         
         extractPartsFromHeader(aInvokeContext, outputMsg);
      }
      

      // Return the message to the awaiting callback 
      aInvokeContext.getResponse().setMessageData( outputMsg );
   }
   
   /**
    * Helper method that checks to see if the passed elements data is a simple type
    * or complex type.
    * @param aElement The element to check the contents of.
    */
   protected boolean isSimpleType(Element aElement)
   {
      boolean simple = false;
      // TODO Simple check for now, a complex type will have attributes and/or child elements.
      if(AeUtil.isNullOrEmpty(aElement.getNamespaceURI()) && AeXmlUtil.getFirstSubElement(aElement) == null)
      {
         simple = true;
         if(aElement.hasAttributes())
         {
            NamedNodeMap attrs = aElement.getAttributes();
            for(int i=0; i < attrs.getLength(); ++i)
            {
               String nsURI = attrs.item(i).getNamespaceURI();
               if(! IAeBPELConstants.W3C_XMLNS.equals(nsURI) &&
                  ! IAeBPELConstants.W3C_XML_SCHEMA_INSTANCE.equals(nsURI))
               {
                  simple = false;
                  break;
               }
            }
         }
      }
      return simple;
   }
}
