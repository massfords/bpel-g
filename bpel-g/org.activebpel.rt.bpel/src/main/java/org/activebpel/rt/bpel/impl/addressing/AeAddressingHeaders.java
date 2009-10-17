//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/addressing/AeAddressingHeaders.java,v 1.12 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.addressing;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.impl.AeEndpointReference;
import org.activebpel.rt.bpel.impl.endpoints.AeEndpointFactory;
import org.activebpel.rt.bpel.impl.endpoints.IAeEndpointFactory;
import org.activebpel.rt.util.AeSOAPElementUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.wsio.AeWsAddressingException;
import org.activebpel.wsio.AeWsAddressingHeaders;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.IAeWsAddressingConstants;
import org.activebpel.wsio.IAeWsAddressingHeaders;
import org.w3c.dom.Element;

/**
 *  Holder for the values from a set of WS-Addressing Headers
 */
public class AeAddressingHeaders extends AeWsAddressingHeaders implements IAeAddressingHeaders
{
   /** factory that gives us a means to parse endpoint references from xml */
   private static IAeEndpointFactory sEndpointFactory = new AeEndpointFactory();
   
   /**
    * Constructor 
    * @param aNamespace WS-Addressing namespace URI for this instance
    */
   public AeAddressingHeaders(String aNamespace)
   {
      super(aNamespace);
   }
   
   /**
    * Copy constructor
    * @param aHeaders
    */
   public AeAddressingHeaders(IAeWsAddressingHeaders aHeaders)
   {
      super(aHeaders.getSourceNamespace());
      this.setAction(aHeaders.getAction());
      this.setConversationId(aHeaders.getConversationId());
      this.setFaultTo(aHeaders.getFaultTo());
      this.setFrom(aHeaders.getFrom());
      this.setMessageId(aHeaders.getMessageId());
      this.setRecipient(aHeaders.getRecipient());
      this.setRelatesTo(aHeaders.getRelatesTo());
      this.setReplyTo(aHeaders.getReplyTo());
      this.setTo(aHeaders.getTo());
      try
      {
         this.setReferenceProperties(aHeaders.getReferenceProperties());
      }
      catch (AeWsAddressingException ex)
      {
         AeException.logError(ex);
      }
   }
   
   /**
    * Converts headers to an instance of this class
    * @param aHeaders
    * @return converted headers
    */
   public static AeAddressingHeaders convert(IAeWsAddressingHeaders aHeaders)
   {
      if (aHeaders instanceof AeAddressingHeaders)
      {
         return (AeAddressingHeaders) aHeaders;
      }
      else
      {
         return new AeAddressingHeaders(aHeaders);
      }
   }

   /**
    * Adds a WS-Addressing header element to this instance
    * If the element is a WS-Addressing Header, the element will set the appropriate member variable
    * Adding headers this way prevents accidentally duplicating an addressing header if someone 
    * adds, for example, a ReplyTo element to reference properties
    * 
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders#addHeaderElement(org.w3c.dom.Element)
    */
   public void addHeaderElement(Element aElement) throws AeWsAddressingException
   {
      try
      {
         if (IAePolicyConstants.CONVERSATION_ID_HEADER.getLocalPart().equals(aElement.getLocalName()))
         {
            if (!aElement.hasChildNodes())
               throw new AeWsAddressingException(AeMessages.format("AeWsAddressingHeaders.2", aElement.getLocalName())); //$NON-NLS-1$
            setConversationId(aElement.getFirstChild().getNodeValue());
         }
         else if (IAeAddressingHeaders.WSA_TO.equals(aElement.getLocalName()))
         {
            if (!aElement.hasChildNodes())
            {
               setTo(""); //$NON-NLS-1$
            }
            else
            {
               setTo(aElement.getFirstChild().getNodeValue());
            }
         }
         else if (IAeAddressingHeaders.WSA_FROM.equals(aElement.getLocalName()))
            setFrom(parseEndpoint(aElement));
         else if (IAeAddressingHeaders.WSA_RECIPIENT.equals(aElement.getLocalName()))
            setRecipient(parseEndpoint(aElement));
         else if (IAeAddressingHeaders.WSA_REPLY_TO.equals(aElement.getLocalName()))
            setReplyTo(parseEndpoint(aElement));
         else if (IAeAddressingHeaders.WSA_FAULT_TO.equals(aElement.getLocalName()))
            setFaultTo(parseEndpoint(aElement));
         else if (IAeAddressingHeaders.WSA_ACTION.equals(aElement.getLocalName()))
         {
            setAction(AeXmlUtil.getText(aElement));
         }
         else if (IAeAddressingHeaders.WSA_MESSAGE_ID.equals(aElement.getLocalName()))
         {
            setMessageId(AeXmlUtil.getText(aElement));
         }
         else if (IAeAddressingHeaders.WSA_RELATES_TO.equals(aElement.getLocalName()))
         {
            addRelatesTo(AeXmlUtil.getText(aElement), getReplyRelationshipName()); 
         }
         else
         {
            Element element = AeSOAPElementUtil.convertToDOM(aElement);
            getReferenceProperties().add(element);
         }
      }
      catch (AeBusinessProcessException bpe)
      {
         throw new AeWsAddressingException(AeMessages.getString("AeWsAddressingHeaders.0"), bpe);  //$NON-NLS-1$
      }
   }
   
   /**
    * Sets the wsa:ReplyTo endpoint. Mandatory wsa:MessageID for this request is generated if not already set. 
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setReplyTo(org.activebpel.wsio.IAeWebServiceEndpointReference)
    */
   public void setReplyTo(IAeWebServiceEndpointReference aReplyTo)
   {
      super.setReplyTo(aReplyTo);
      // MessageID is mandatory with a ReplyTo
      if (aReplyTo != null)
      {
         if (getMessageId() == null)
         {
            setMessageId(AeUtil.getNewUUID());
         }
      }
   }
   
   /**
    * Parses an endpoint from the element passed in. 
    * @param aElement
    */
   private IAeEndpointReference parseEndpoint(Element aElement) throws AeBusinessProcessException
   {
      // use endpoint factory to deserialize from element
      IAeEndpointReference ref = null;
      // copy all of the child nodes over to the new endpoint ref element
      try
      {
         ref = sEndpointFactory.getDeserializer(aElement.getNamespaceURI()).deserializeEndpoint(aElement);
      }
      catch (AeException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeWsAddressingHeaders.1"), e); //$NON-NLS-1$
      }
      return ref;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders#getTo()
    */
   public String getTo()
   {
      IAeWebServiceEndpointReference epr = getRecipient();
      if (epr != null)
         return epr.getAddress();
      else
         return null;
   }

   /**
    * Sets the address of the intended recipient.
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setTo(java.lang.String)
    */
   public void setTo(String aTo)
   {
      AeEndpointReference epr = new AeEndpointReference();
      if (getRecipient() != null)
         epr.setReferenceData(getRecipient());
      epr.setAddress(aTo);   
      setRecipient(epr);
   }

   /**
    * Sets the list of additional headers to be included when serializing to a SOAP Envelope
    * If the list contains any embedded WS-Addressing headers, these will be used to set the 
    * appropriate member value of this instance 
    *  
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders#setReferenceProperties(java.util.List)
    */
   public void setReferenceProperties(List aElementList) throws AeWsAddressingException
   {
      // clear old stuff
      List props = getReferenceProperties();
      props.clear();
      addReferenceProperties(aElementList);
   }
   
   /**
    * Adds to the list of additional headers to be included when serializing to a SOAP Envelope
    * If the list contains any embedded WS-Addressing headers, these will be used to set the 
    * appropriate member value of this instance 
    *  
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders#setReferenceProperties(java.util.List)
    */
   public void addReferenceProperties(List aElementList) throws AeWsAddressingException
   {
      try
      {
         // Add all the properties
         for (Iterator it = aElementList.iterator(); it.hasNext();)
         {
            // This method figures out if an element is an 
            // embedded WSA header
            addHeaderElement((Element) it.next());
         }
      }
      catch (Exception e)
      {
         throw new AeWsAddressingException(AeMessages.getString("AeAddressingHeaders.0"), e); //$NON-NLS-1$
      }
   }

   /**
    * Gets the Reply relationship type QName
    * 
    * @return relationship type attribute
    */
   public QName getReplyRelationshipName()
   {
      String namespace = getSourceNamespace();
      if (IAeConstants.WSA_NAMESPACE_URI.equals(namespace))
      {
         return new QName(namespace, IAeWsAddressingConstants.WSA_RESPONSE_RELATION);
      }
      else
      {
         return new QName(namespace, IAeWsAddressingConstants.WSA_REPLY_RELATION);
      }
   }

   /**
    * Returns the fully qualified fault action uri
    * @return fault action uri
    */
   public String getFaultAction()
   {
      return getSourceNamespace().concat(IAeWsAddressingConstants.WSA_FAULT_ACTION_);
   }
   
   /**
    * Returns the WSA anonymous role URI
    * @return anonymous role uri
    */
   public String getAnonymousRole()
   {
      return getSourceNamespace().concat(IAeWsAddressingConstants.WSA_ANONYMOUS_ROLE);
   }
}
