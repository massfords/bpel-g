// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/endpoints/AeWSAddressingEndpointDeserializer.java,v 1.21 2008/02/17 21:37:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.endpoints;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.AeEndpointReference;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates an IAeEndpointReference instance based on the xml format defined in
 * the WS-Addressing spec. 
 */
public class AeWSAddressingEndpointDeserializer implements IAeEndpointDeserializer
{
   /** default singleton instance */
   private static final AeWSAddressingEndpointDeserializer sSingleton = new AeWSAddressingEndpointDeserializer();
   private static final AeWSAddressingEndpointDeserializer sSingleton_2004_08 = new AeWSAddressingEndpointDeserializer(IAeConstants.WSA_NAMESPACE_URI_2004_08);   
   private static final AeWSAddressingEndpointDeserializer sSingleton_2004_03 = new AeWSAddressingEndpointDeserializer(IAeConstants.WSA_NAMESPACE_URI_2004_03);
   private static final AeWSAddressingEndpointDeserializer sSingleton_2005_08 = new AeWSAddressingEndpointDeserializer(IAeConstants.WSA_NAMESPACE_URI_2005_08);   
   
   private String mNamespace = IAeConstants.WSA_NAMESPACE_URI;
   
   /**
    * Private ctor for singleton pattern 
    */
   private AeWSAddressingEndpointDeserializer()
   {
   }

   /**
    * ctor for singleton pattern
    */
   protected AeWSAddressingEndpointDeserializer(String aNamespace)
   {
      mNamespace = aNamespace;
   }
   
   /**
    * Getter for the singleton instance
    */
   public static AeWSAddressingEndpointDeserializer getInstance()
   {
      return sSingleton;
   }
   
   /**
    * Getter for the singleton instance
    */
   public static AeWSAddressingEndpointDeserializer getInstance(String aNamespace)
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
         return sSingleton;
      }
   }   
   
   /**
    * @see org.activebpel.rt.bpel.impl.endpoints.IAeEndpointDeserializer#deserializeEndpoint(org.w3c.dom.Element)
    */
   public IAeEndpointReference deserializeEndpoint(Element aData)
      throws AeBusinessProcessException
   {
      return deserializeEndpoint(aData, null);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.endpoints.IAeEndpointDeserializer#deserializeEndpoint(org.w3c.dom.Element, org.activebpel.rt.bpel.IAeEndpointReference)
    */
   public IAeEndpointReference deserializeEndpoint(Element aData, IAeEndpointReference aRef)
      throws AeBusinessProcessException
   {
      IAeEndpointReference ref = aRef == null ? new AeEndpointReference() : aRef;
      
      ref.setSourceNamespace(mNamespace);
      
      NodeList children = aData.getChildNodes();
      for (int i=0, len=children.getLength(); i < len; i++)
      {
         Node child = children.item(i);
         if (child.getNodeType() != Node.ELEMENT_NODE)
            continue;
         deserializeChildElement(ref, (Element) child);
      }
      return ref;
   }

   /**
    * Determines the EPR property for a given child element
    * @param aRef
    * @param aChild
    * @throws AeBusinessProcessException
    */
   protected void deserializeChildElement(IAeEndpointReference aRef, Element aChild) throws AeBusinessProcessException
   {
      if (mNamespace.equals(aChild.getNamespaceURI()))
      {
         if ("Address".equals(aChild.getLocalName())) //$NON-NLS-1$
         {
            String address = AeXmlUtil.getText((Element) aChild); 
            aRef.setAddress(address.trim());
         }
         else if ("PortType".equals(aChild.getLocalName())) //$NON-NLS-1$
            aRef.setPortType(extractQNameData((Element)aChild));
         else if ("ReferenceProperties".equals(aChild.getLocalName())) //$NON-NLS-1$
         {
            deserializeProperties(aRef, aChild.getChildNodes());
         }
         else if ("ReferenceParameters".equals(aChild.getLocalName())) //$NON-NLS-1$
         {
            deserializeProperties(aRef, aChild.getChildNodes());
         }
         else if ("ServiceName".equals(aChild.getLocalName())) //$NON-NLS-1$
         {
            String portName = ((Element)aChild).getAttribute("PortName"); //$NON-NLS-1$
            if (! AeUtil.isNullOrEmpty(portName))
               aRef.setServicePort(portName);
            
            aRef.setServiceName(extractQNameData((Element)aChild));
         }
         else if ("Metadata".equals(aChild.getLocalName())) //$NON-NLS-1$
         {
            NodeList children = aChild.getChildNodes();
            for (int i=0, len=children.getLength(); i < len; i++)
            {
               Node child = children.item(i);
               if (child.getNodeType() == Node.ELEMENT_NODE)
                  deserializeChildElement(aRef, (Element) child);
            }
         }
         else
         {
            // Extensibility element
            aRef.addExtensibilityElement((Element)aChild);
         }
      }
      else if (IAeBPELConstants.WSP_NAMESPACE_URI.equals(aChild.getNamespaceURI()))
      {
         if ("Policy".equals(aChild.getLocalName())) //$NON-NLS-1$
            aRef.addPolicyElement((Element)aChild);
      }
      else
      {
         // Extensibility element
         aRef.addExtensibilityElement((Element)aChild);
      }
   }
   
   /**
    * This will deserialize a list of Nodes to ReferenceProperties
    * @param aRef
    * @param aList
    */
   protected void deserializeProperties(IAeEndpointReference aRef, NodeList aList)
   {
      for (int j=0, length=aList.getLength(); j < length; j++)
      {

         Node prop = aList.item(j);                  
         
         if (prop.getNodeType() == Node.ELEMENT_NODE)
         {
            deserializeProperty(aRef, (Element) prop);
         }
      }
   }
   
   /**
    * This will deserialize a property to ReferenceProperties
    * @param aRef
    * @param aProp
    */
   protected void deserializeProperty(IAeEndpointReference aRef, Element aProp)
   {
      // add element to reference properties for header propagation
      aRef.addReferenceProperty((Element) aProp);
   }

   
   /**
    * Extracts the text value from this element which is expected to be a QName. 
    * @param aElement
    * @throws AeBusinessProcessException
    */
   protected QName extractQNameData(Element aElement) throws AeBusinessProcessException
   {
      String qname  = AeXmlUtil.getText(aElement);
      if (AeUtil.isNullOrEmpty(qname) && aElement instanceof SOAPElement)
         qname = ((SOAPElement)aElement).getValue();
      
      // check for standard {namespace}name syntax
      if (qname.charAt(0) == '{')
      {
         return QName.valueOf(qname);
      }
      
      String prefix = AeXmlUtil.extractPrefix(qname);
      String nsURI  = null;
      if (prefix != null)
      {
         if (aElement instanceof SOAPElement)
         {
            SOAPElement soapEl = (SOAPElement) aElement;
            nsURI = soapEl.getNamespaceURI(prefix);
         }
         else
         {
            nsURI = AeXmlUtil.getNamespaceForPrefix(aElement, prefix);
         }
      }
      else
      {
         nsURI = AeXmlUtil.findDefaultNamespace(aElement); 
      }
      
      return new QName(nsURI, AeXmlUtil.extractLocalPart(qname));
   }
}
