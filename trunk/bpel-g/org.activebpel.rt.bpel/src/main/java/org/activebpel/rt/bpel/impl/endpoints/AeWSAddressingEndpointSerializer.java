// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/endpoints/AeWSAddressingEndpointSerializer.java,v 1.25.14.1 2008/04/24 14:49:50 KPease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.endpoints;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Endpoint serializer for WS-Addressing endpoints. 
 */
public class AeWSAddressingEndpointSerializer implements IAeEndpointSerializer
{
   /** singleton instance */
   private static final AeWSAddressingEndpointSerializer sSingleton = new AeWSAddressingEndpointSerializer();
   /** 2004 08 singleton instance */
   private static final AeWSAddressingEndpointSerializer sSingleton_2004_08 = new AeWSAddressingEndpointSerializer(IAeConstants.WSA_NAMESPACE_URI_2004_08);
   /** 2004 03 singleton instance */
   private static final AeWSAddressingEndpointSerializer sSingleton_2004_03 = new AeWSAddressingEndpointSerializer(IAeConstants.WSA_NAMESPACE_URI_2004_03);
   /** 2005 08 singleton instance */
   private static final AeWSAddressingEndpointSerializer sSingleton_2005_08 = new AeWsa2005EndpointSerializer(IAeConstants.WSA_NAMESPACE_URI_2005_08);
   
   private String mNamespace = IAeConstants.WSA_NAMESPACE_URI;
   
   /**
    * Private ctor for singleton pattern 
    */
   private AeWSAddressingEndpointSerializer()
   {
   }
   
   /**
    * ctor for singleton pattern 
    */
   protected AeWSAddressingEndpointSerializer(String aNamespace)
   {
      mNamespace = aNamespace;
   }   
   
   /**
    * Getter for the singleton
    */
   public static AeWSAddressingEndpointSerializer getInstance()
   {
      return sSingleton;
   }

   /**
    * Getter for the singleton
    */
   public static AeWSAddressingEndpointSerializer getInstance(String aNamespace)
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
    * @see org.activebpel.rt.bpel.impl.endpoints.IAeEndpointSerializer#serializeEndpoint(org.activebpel.wsio.IAeWebServiceEndpointReference)
    */
   public Document serializeEndpoint(IAeWebServiceEndpointReference aRef)
   {
      Document doc = AeXmlUtil.newDocument();
      
      Map qnameToPrefixMap = new HashMap();
         
      Element er = AeXmlUtil.addElementNS(doc, mNamespace, "wsa:EndpointReference", null); //$NON-NLS-1$
      
      // prime the pump w/ some popular ns prefix mapping. it'll get added to the doc below
      qnameToPrefixMap.put(mNamespace, "wsa"); //$NON-NLS-1$
      qnameToPrefixMap.put(IAeConstants.W3C_XML_SCHEMA_INSTANCE, "xsi"); //$NON-NLS-1$
      qnameToPrefixMap.put(IAeConstants.SOAP_NAMESPACE_URI, "soapenv"); //$NON-NLS-1$
      qnameToPrefixMap.put(IAeConstants.W3C_XML_SCHEMA, "xsd"); //$NON-NLS-1$

      // Add the address, which is mandatory
      AeXmlUtil.addElementNS(er, mNamespace, "wsa:Address", aRef.getAddress()); //$NON-NLS-1$
      
      // Add reference properties if any were defined
      serializeReferenceProps(aRef, er);

      // Add policies & ext elements, if any were defined
      addMetadata(aRef, er, qnameToPrefixMap);
      
      // add all of the namespaces we've discovered      
      for (Iterator iter = qnameToPrefixMap.entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Entry) iter.next();
         String namespace = (String) entry.getKey();
         String prefix = (String) entry.getValue();
         er.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + prefix, namespace); //$NON-NLS-1$
      }
      
      return doc;
   }

   /**
    * Adds policy and extensibility elements as children of the given element
    * @param aRef
    * @param aElement
    */
   protected void addMetadata(IAeWebServiceEndpointReference aRef, Element aElement, Map aQNamePrefixMap)
   {
      // Add the port type if one was specified
      if (aRef.getPortType() != null)
      {
         String portType = getQNameStr(aRef.getPortType(), aQNamePrefixMap);
         AeXmlUtil.addElementNS(aElement, mNamespace, "wsa:PortType", portType); //$NON-NLS-1$
      }
      
      // Add the service name if one was specified
      if (aRef.getServiceName() != null)
      {
         String serviceName = getQNameStr(aRef.getServiceName(), aQNamePrefixMap);
         Element svcName = AeXmlUtil.addElementNS(aElement, mNamespace, "wsa:ServiceName", serviceName); //$NON-NLS-1$
         
         if (aRef.getServicePort() != null)
            svcName.setAttribute("PortName", aRef.getServicePort()); //$NON-NLS-1$
      }
      
      // Add policies if any were defined
      addPolicies(aRef, aElement);
      
      // Add extensibility elements if any
      addExtElements(aRef, aElement);
   }
   

   /**
    * Adds policy elements as children of the given element
    * @param aRef
    * @param aElement
    */
   protected void addPolicies(IAeWebServiceEndpointReference aRef, Element aElement)
   {
      for(Iterator iter = aRef.getPolicies().iterator(); iter.hasNext(); )
      {
         Element prop = (Element) iter.next();
         addChildElement(prop, aElement);
      }
   }

   /**
    * Adds all reference properties to the element under wsa:ReferenceProperties 
    * @param aRef
    * @param aElement
    */
   protected void serializeReferenceProps(IAeWebServiceEndpointReference aRef, Element aElement)
   {
      if (!aRef.getReferenceProperties().isEmpty())
      {
         Element props = AeXmlUtil.addElementNS(aElement, mNamespace, "wsa:ReferenceProperties", null); //$NON-NLS-1$
         addReferenceProps(aRef, props);
      }
   }
   
   
   /**
    * Adds all reference properties to the element under wsa:ReferenceProperties 
    * @param aRef
    * @param aElement
    */
   protected void addReferenceProps(IAeWebServiceEndpointReference aRef, Element aElement)
   {
      if (!aRef.getReferenceProperties().isEmpty())
      {
         for(Iterator iter= aRef.getReferenceProperties().iterator(); iter.hasNext(); ) 
         {
            Element prop = (Element) iter.next();
            addChildElement(prop, aElement);
         }      
      }
   }

   /**
    * Adds all extensibility elements as direct children of the given element
    * @param aRef
    * @param aElement
    */
   protected void addExtElements(IAeWebServiceEndpointReference aRef, Element aElement)
   {
      for(Iterator iter = aRef.getExtensibilityElements(); iter.hasNext(); )
      {
         Element prop = (Element) iter.next();
         addChildElement(prop, aElement);
      }
   }
   
   /**
    * Adds the source element as a child of the target
    * 
    * @param aSource
    * @param aTarget
    */
   protected void addChildElement(Element aSource, Element aTarget)
   {
      Element elem = AeXmlUtil.cloneElement(aSource, aTarget.getOwnerDocument());
      aTarget.appendChild(elem);
   }
   
   /**
    * Converts a QName to a String using the prefix already found in the map or 
    * it'll generate a new prefix and add it to the map for this QName
    * @param aName
    * @param qnameToPrefixMap
    */
   private String getQNameStr(QName aName, Map qnameToPrefixMap)
   {
      if (!AeUtil.isNullOrEmpty(aName.getNamespaceURI()))
      {
         String prefix = getPrefix(aName.getNamespaceURI(), null, qnameToPrefixMap);
         return prefix + ":" + aName.getLocalPart(); //$NON-NLS-1$
      }
      else
      {
         return aName.getLocalPart();
      }
   }
   
   /**
    * Returns the prefix for the given namespace. If not already defined,
    * it will generate a new prefix and add it to the map. 
    * 
    * @param aNamespace the namespace to search for
    * @param aPrefix the current prefix
    * @param qnameToPrefixMap the map of namespace prefixes
    */
   private String getPrefix(String aNamespace, String aPrefix, Map qnameToPrefixMap)
   {
      String prefix = (String) qnameToPrefixMap.get(aNamespace);
      if (AeUtil.isNullOrEmpty(prefix))
      {
         if (AeUtil.isNullOrEmpty(aPrefix) || qnameToPrefixMap.containsValue(aPrefix))
         {
            // generate new, unused prefix
            int i = 1;
            prefix = "ns" + (qnameToPrefixMap.size() + i); //$NON-NLS-1$
            while (qnameToPrefixMap.containsValue(prefix))
            {
               i++;
               prefix = "ns" + (qnameToPrefixMap.size() + i); //$NON-NLS-1$
            }
         }
         else
         {
            prefix = aPrefix;
         }
            
         qnameToPrefixMap.put(aNamespace, prefix);
      }
      return prefix;
   }
}
