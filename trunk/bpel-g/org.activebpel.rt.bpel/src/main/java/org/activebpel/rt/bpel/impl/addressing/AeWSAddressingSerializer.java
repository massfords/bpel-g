// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/addressing/AeWSAddressingSerializer.java,v 1.15 2007/12/07 21:23:14 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.addressing;

import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.endpoints.AeEndpointFactory;
import org.activebpel.rt.bpel.impl.endpoints.IAeEndpointSerializer;
import org.activebpel.rt.util.AeSOAPElementUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.IAeWsAddressingConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * WS-Addressing serializer to add addressing headers to a SOAPHeader or DOM element. 
 */
public class AeWSAddressingSerializer implements IAeAddressingSerializer
{
   /** singleton instance */
   private static final AeWSAddressingSerializer sSingleton = new AeWSAddressingSerializer();
   /** 2004 08 singleton instance */
   private static final AeWSAddressingSerializer sSingleton_2004_08 = new AeWSAddressingSerializer(IAeConstants.WSA_NAMESPACE_URI_2004_08);
   /** 2004 03 singleton instance */
   private static final AeWSAddressingSerializer sSingleton_2004_03 = new AeWSAddressingSerializer(IAeConstants.WSA_NAMESPACE_URI_2004_03);
   /** 2005 08 singleton instance */
   private static final AeWSAddressingSerializer sSingleton_2005_08 = new AeWSAddressingSerializer(IAeConstants.WSA_NAMESPACE_URI_2005_08);

   /** Factory for getting endpoint serializers */ 
   private static final AeEndpointFactory sFactory = new AeEndpointFactory();
   
   /** Namespace of this serializer */
   private String mNamespace = IAeConstants.WSA_NAMESPACE_URI;
   
   /**
    * Private ctor for singleton pattern using default namespace 
    */
   private AeWSAddressingSerializer()
   {
   }
   
   /**
    * Private ctor for singleton pattern for a specific namespace
    */
   private AeWSAddressingSerializer(String aNamespace)
   {
      mNamespace = aNamespace;
   }   
   
   /**
    * Getter for the default singleton
    */
   public static AeWSAddressingSerializer getInstance()
   {
      return sSingleton;
   }

   /**
    * Getter for the singleton for a specific namespace
    */
   public static AeWSAddressingSerializer getInstance(String aNamespace)
   {
      if (IAeConstants.WSA_NAMESPACE_URI_2004_08.equals(aNamespace))
      {
         return sSingleton_2004_08;
      }
      else if (IAeConstants.WSA_NAMESPACE_URI_2004_03.equals(aNamespace))
      {
         return sSingleton_2004_03;
      }
      else if (IAeConstants.WSA_NAMESPACE_URI_2005_08.equals(aNamespace))
      {
         return sSingleton_2005_08;
      }
      else
      {
         throw new IllegalArgumentException(AeMessages.getString("AeWSAddressingDeserializer.1") + aNamespace); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingSerializer#serializeToDocument(org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders)
    */
   public Document serializeToDocument(IAeAddressingHeaders aRef) throws AeBusinessProcessException
   {
      return serializeHeadersInternal(aRef);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingSerializer#serializeHeaders(org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders, javax.xml.soap.SOAPEnvelope)
    */
   public SOAPHeader serializeHeaders(IAeAddressingHeaders aReference, SOAPEnvelope aEnv) throws AeBusinessProcessException
   {
      try
      {
         Document doc = serializeToDocument(aReference);
         SOAPHeader header = aEnv.getHeader();
         AeSOAPElementUtil.copyToSOAP(doc.getDocumentElement(), (SOAPElement) header);
         return header;
      }
      catch (SOAPException ex)
      {
         throw new AeBusinessProcessException(ex.getLocalizedMessage(), ex);
      }
   }

   /**
    * Internal method that performs the serialization of addressing headers to a DOM element
    * on behalf of the various overloaded methods.
    * 
    * @param aRef 
    * @return Serialized headers as a DOM element
    * @throws AeBusinessProcessException
    */
   protected Document serializeHeadersInternal(IAeAddressingHeaders aRef) throws AeBusinessProcessException
   {
      Document doc = AeXmlUtil.newDocument();
      Element headers = doc.createElementNS(IAeConstants.SOAP_NAMESPACE_URI, "soap:Header"); //$NON-NLS-1$
      headers.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:soap", IAeConstants.SOAP_NAMESPACE_URI); //$NON-NLS-1$
      headers.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:wsa", mNamespace); //$NON-NLS-1$
      headers.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:abx", IAeConstants.ABX_NAMESPACE_URI); //$NON-NLS-1$
      doc.appendChild(headers);
      
      if (aRef == null)
      {
         return doc;
      }

      if (!AeUtil.isNullOrEmpty(aRef.getConversationId()))
         addTextHeader(headers, ABX_CONVERSATION_ID_NAME, IAeConstants.ABX_NAMESPACE_URI, aRef.getConversationId());
      // Empty is an allowable value for Action, To, and MessageId, so we only check for null
      if (aRef.getAction() != null)
         addTextHeader(headers, WSA_ACTION_NAME, aRef.getAction());
      if (aRef.getMessageId() != null)
         addTextHeader(headers, WSA_MESSAGE_ID_NAME, aRef.getMessageId());
      if (aRef.getTo() != null)
      {
         addTextHeader(headers, WSA_TO_NAME, aRef.getTo());
      }
      if (aRef.getFrom() != null)
         addEndpointHeader(headers, WSA_FROM_NAME, aRef.getFrom());
      if (aRef.getReplyTo() != null)
         addEndpointHeader(headers, WSA_REPLY_TO_NAME, aRef.getReplyTo());
      if (aRef.getFaultTo() != null)
         addEndpointHeader(headers, WSA_FAULT_TO_NAME, aRef.getFaultTo());
      if (!AeUtil.isNullOrEmpty(aRef.getRelatesTo()))
      {
          Map relatesTo = aRef.getRelatesTo();
          for (Iterator it = relatesTo.keySet().iterator(); it.hasNext();)
          {
             QName name = (QName) it.next();
             addRelatesToHeader(headers, IAeWsAddressingConstants.WSA_NS_PREFIX + ":" + name.getLocalPart(), (String) relatesTo.get(name));  //$NON-NLS-1$
          }
      }
      if (!AeUtil.isNullOrEmpty(aRef.getReferenceProperties()))
      {
         for (Iterator it = aRef.getReferenceProperties().iterator(); it.hasNext();)
         {
            addHeaderElement(headers, (Element) it.next());
         }
      }
      return doc;
   }
   
   /**
    * Adds adds a DOM element to a SOAPEnvelope as a header
    * 
    * @param aParent the target parent
    * @param aValue the Header to add
    * @return the Element added
    */
   private Element addHeaderElement(Element aParent, Element aValue) 
   {
      return (Element) aParent.appendChild(aParent.getOwnerDocument().importNode(aValue, true));
   }

   /**
    * Adds adds a header element to a SOAPEnvelope for a name/value pair 
    * @param aParent the target parent
    * @param aName the header element name
    * @param aValue the header element value
    * @return the new Header Element
    */
   private Element addTextHeader(Element aParent, String aName, String aValue) 
   {
      return AeXmlUtil.addElementNS(aParent, mNamespace, aName, aValue);
   }

   /**
    * Adds adds a header element to a SOAPEnvelope for a name/value pair 
    * @param aParent the target parent
    * @param aName the header element name
    * @param aNamespace a specific namespace to use
    * @param aValue the header element value
    * @return the new header Element
    */
   private Element addTextHeader(Element aParent, String aName, String aNamespace, String aValue) 
   {
      return AeXmlUtil.addElementNS(aParent, aNamespace, aName, aValue);
   }
   
   /**
    * Adds a wsa:RelatesTo header 
    * @param aParent the target parent
    * @param aRelation the local part of the RelationshipType attribute
    * @param aValue the message id this header is relating to
    * @return the header added
    */
   private Element addRelatesToHeader(Element aParent, String aRelation, String aValue) 
   {
      Element wsaHeader = AeXmlUtil.addElementNS(aParent, mNamespace, WSA_RELATES_TO_NAME, aValue);
      wsaHeader.setAttributeNS(mNamespace, WSA_RELATIONSHIP_TYPE_NAME, aRelation);
      return wsaHeader;
   }   

   /**
    * Serializes an Endpoint Reference object to a SOAPHeader 
    * @param aParent the target parent
    * @param aName the endpoint header element name
    * @param aEndpoint the endpoint reference
    * @return the new header element
    */
   private Element addEndpointHeader(Element aParent, String aName, IAeWebServiceEndpointReference aEndpoint) 
   {
      Element wsaHeader = AeXmlUtil.addElementNS(aParent, mNamespace, aName);
      IAeEndpointSerializer ser = sFactory.getSerializer(mNamespace);
      Document doc = ser.serializeEndpoint(aEndpoint);
      AeXmlUtil.copyNodeContents(doc.getDocumentElement(), wsaHeader);
      return wsaHeader;
   }
}
