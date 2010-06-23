// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/addressing/AeWSAddressingDeserializer.java,v 1.5 2007/01/17 17:48:06 KPease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.addressing;

import java.util.HashSet;
import java.util.Set;

import javax.xml.soap.SOAPHeader;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.wsio.AeWsAddressingException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Extracts WS-Addressing Header values from XML based on the WS-Addressing spec. 
 */
public class AeWSAddressingDeserializer implements IAeAddressingDeserializer
{
   /** default singleton instances */
   private static final AeWSAddressingDeserializer sSingleton = new AeWSAddressingDeserializer();
   private static final AeWSAddressingDeserializer sSingleton_2004_08 = new AeWSAddressingDeserializer(IAeConstants.WSA_NAMESPACE_URI_2004_08);   
   private static final AeWSAddressingDeserializer sSingleton_2004_03 = new AeWSAddressingDeserializer(IAeConstants.WSA_NAMESPACE_URI_2004_03);
   private static final AeWSAddressingDeserializer sSingleton_2005_08 = new AeWSAddressingDeserializer(IAeConstants.WSA_NAMESPACE_URI_2005_08);   
   
   /** WSA Namespace to use */ 
   private String mNamespace = IAeConstants.WSA_NAMESPACE_URI;

   /** set of element names that may contain WS-Addressing information */
   private static Set mWSASoapHeaderElementNames = new HashSet();
   
   static
   {
     mWSASoapHeaderElementNames.add(IAeAddressingHeaders.WSA_TO);       
     mWSASoapHeaderElementNames.add(IAeAddressingHeaders.WSA_FROM); 
     mWSASoapHeaderElementNames.add(IAeAddressingHeaders.WSA_REPLY_TO);
     mWSASoapHeaderElementNames.add(IAeAddressingHeaders.WSA_FAULT_TO);
     mWSASoapHeaderElementNames.add(IAeAddressingHeaders.WSA_ACTION); 
     mWSASoapHeaderElementNames.add(IAeAddressingHeaders.WSA_MESSAGE_ID);
     mWSASoapHeaderElementNames.add(IAeAddressingHeaders.WSA_RELATES_TO);     
     mWSASoapHeaderElementNames.add(IAeAddressingHeaders.WSA_RECIPIENT);
     mWSASoapHeaderElementNames.add(IAeAddressingHeaders.ABX_CONVERSATION_ID);     
   }
   
   
   /**
    * Private ctor for singleton pattern that uses the default namespace
    */
   private AeWSAddressingDeserializer()
   {
   }

   /**
    * Private ctor for singleton pattern with specific namespace
    */
   private AeWSAddressingDeserializer(String aNamespace)
   {
      mNamespace = aNamespace;
      
   }
   
   /**
    * Getter for the default namespace singleton instance
    */
   public static AeWSAddressingDeserializer getInstance()
   {
      return sSingleton;
   }
   
   /**
    * Getter for the singleton instance in a specific namespace
    */
   public static AeWSAddressingDeserializer getInstance(String aNamespace)
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
      else if (IAeConstants.WSA_NAMESPACE_URI.equals(aNamespace))
      {
         return sSingleton;
      }
      else
      {
         throw new IllegalArgumentException(AeMessages.getString("AeWSAddressingDeserializer.1") + aNamespace); //$NON-NLS-1$
      }
   }   
   
   /**
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingDeserializer#deserializeHeaders(javax.xml.soap.SOAPHeader)
    */
   public IAeAddressingHeaders deserializeHeaders(SOAPHeader aData)
      throws AeBusinessProcessException
   {
      return deserializeHeaders(aData, null);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingDeserializer#deserializeHeaders(javax.xml.soap.SOAPHeader, org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders)
    */
   public IAeAddressingHeaders deserializeHeaders(Element aData, IAeAddressingHeaders aRef)
      throws AeBusinessProcessException
   {
      IAeAddressingHeaders ref = aRef == null ? new AeAddressingHeaders(mNamespace) : aRef;
      
      ref.setSourceNamespace(mNamespace);

      // just use defaults if null header
      if (aData == null)
      {
         return ref;
      }
      
      try
      {
         NodeList nodes = aData.getChildNodes();
         for (int i = 0; i < nodes.getLength(); i++)
         {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
               Element e = (Element) nodes.item(i);
               if (isEndpointHeader(e))
               {
                  ref.addHeaderElement(e);
               }
            }
         }
      }
      catch (AeWsAddressingException ae)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeWSAddressingDeserializer.0"), ae); //$NON-NLS-1$
      }
      
      return ref;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingDeserializer#deserializeHeaders(org.w3c.dom.Element)
    */
   public IAeAddressingHeaders deserializeHeaders(Element aElement) throws AeBusinessProcessException
   {
      return deserializeHeaders(aElement, null);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.addressing.IAeAddressingDeserializer#deserializeHeaders(javax.xml.soap.SOAPHeader, org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders)
    */
   public IAeAddressingHeaders deserializeHeaders(SOAPHeader aHeader, IAeAddressingHeaders aRef) throws AeBusinessProcessException
   {
      return deserializeHeaders((Element) aHeader, aRef);
   }
   
   /**
    * Returns true if the element is one that contains a wsa header
    * @param aElement
    */
   public boolean isEndpointHeader(Element aElement)
   {
      return (IAeConstants.WSA_NAMESPACE_URI.equals(aElement.getNamespaceURI()) || 
            IAeConstants.WSA_NAMESPACE_URI_2004_03.equals(aElement.getNamespaceURI()) ||
            IAeConstants.WSA_NAMESPACE_URI_2005_08.equals(aElement.getNamespaceURI()) ||
            IAeConstants.ABX_NAMESPACE_URI.equals(aElement.getNamespaceURI()) ||
            IAeConstants.WSA_NAMESPACE_URI_2004_08.equals(aElement.getNamespaceURI())) &&
            mWSASoapHeaderElementNames.contains(aElement.getLocalName());
   }

}
