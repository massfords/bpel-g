//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeSOAPElementUtil.java,v 1.1 2007/05/11 15:18:16 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.activebpel.rt.IAeConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility methods for converting SOAPElements to DOM elements and vice-versa
 */
public class AeSOAPElementUtil
{
   /**
    * Creates a copy of an Element as a DOM element.  
    * If the source is a SOAPElement, it is converted. Otherwise, it is copied with no conversion. 
    * 
    * @param aElement Element to copy
    * @return DOM element
    */
   public static Element convertToDOM(Element aElement)
   {
      if (aElement instanceof SOAPElement)
      {
         return convertToDOM((SOAPElement) aElement, null);
      }
      else
      {
         return AeXmlUtil.cloneElement(aElement);
      }
   }

   /**
    * Creates a copy of a SOAPElement as a DOM element. The primary function of this method is 
    * to ensure that the namespace declarations are handled properly and the resulting element 
    * will import correctly.
    *  
    * @param aSoapElement source element to copy
    * @param aParent parent element of resulting copy, if null element is created in a new document
    * @return copy of element as a child of the parent
    */
   public static Element convertToDOM(SOAPElement aSoapElement, Element aParent)
   {
      Element srcElement = (Element) aSoapElement;
      String namespace = aSoapElement.getElementName().getURI();
      String name = aSoapElement.getElementName().getQualifiedName();
      
      Node parent = aParent;
      if (aParent == null)
      {
         // create element in a new document
         Document doc = AeXmlUtil.newDocument();
         parent = doc;
      }
      
      Element element = AeXmlUtil.addElementNS(parent, namespace, name, null);
      
      // add any new or conflicting prefixes
      addNewDOMPrefixes(aSoapElement, element);
      
      // add attributes
      if (srcElement.hasAttributes())
      {
         NamedNodeMap attributes = srcElement.getAttributes();
         for (int i = 0; i < attributes.getLength(); i++)
         {
            Node attr = attributes.item(i);
            // skip namespace decls since we got them already
            if (attr.getNodeName().startsWith("xmlns")) //$NON-NLS-1$
            {
               continue;
            }
            
            namespace = attr.getNamespaceURI();
            name = attr.getLocalName();
            
            if (AeUtil.isNullOrEmpty(namespace))
            {
               name = attr.getNodeName();
               element.setAttribute(name, attr.getNodeValue());
            }
            else
            {
               String prefix = attr.getPrefix();
               if (AeUtil.notNullOrEmpty(prefix))
               {
                  name = prefix + ":" + name; //$NON-NLS-1$
               }
               element.setAttributeNS(namespace, name, attr.getNodeValue());
            }
         }
      }
      
      // add children
      if (srcElement.hasChildNodes())
      {
         NodeList nodes = srcElement.getChildNodes();
         for (int i = 0; i < nodes.getLength(); i++)
         {
            Node child = nodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
               convertToDOM((SOAPElement) child, element);
            }
            else
            {
               Node newChild = element.getOwnerDocument().importNode(child, true);
               element.appendChild(newChild);
            }
         }
      }
      
      return element;
   }
   
   /**
    * Adds the contents of a DOM element to a newly created SOAPElement 
    * 
    * @param aSource element to copy
    * @param aSoapElement target element
    * @throws SOAPException
    */
   public static SOAPElement copyToSOAP(Element aSource, SOAPElement aSoapElement) throws SOAPException
   {
      String namespace = aSoapElement.getElementName().getURI();
      String name = aSoapElement.getElementName().getQualifiedName();
      
      Element element = (Element) aSoapElement;
      
      // add any new declarations
      addNewSOAPPrefixes(aSource, aSoapElement);
      
      // add attributes
      if (aSource.hasAttributes())
      {
         NamedNodeMap attributes = aSource.getAttributes();
         for (int i = 0; i < attributes.getLength(); i++)
         {
            Node attr = attributes.item(i);
            // skip namespace decls since we got them already
            if (attr.getNodeName().startsWith("xmlns")) //$NON-NLS-1$
            {
               continue;
            }
            
            namespace = attr.getNamespaceURI();
            name = attr.getLocalName();
            
            if (AeUtil.isNullOrEmpty(namespace))
            {
               name = attr.getNodeName();
               element.setAttribute(name, attr.getNodeValue());
            }
            else
            {
               String prefix = attr.getPrefix();
               if (AeUtil.notNullOrEmpty(prefix))
               {
                  name = prefix + ":" + name; //$NON-NLS-1$
               }
               element.setAttributeNS(namespace, name, attr.getNodeValue());
            }
         }
      }
      
      // add children
      if (aSource.hasChildNodes())
      {
         NodeList nodes = aSource.getChildNodes();
         for (int i = 0; i < nodes.getLength(); i++)
         {
            Node child = nodes.item(i);
            element.appendChild(element.getOwnerDocument().importNode(child, true));
         }
      }
      
      return (SOAPElement) element;
   }
   
   /**
    * Finds all the namespaces and prefixes in effect for a SOAPElement
    * using SAAJ interface methods
    *  
    * @param aElement element 
    * @param aPrefixMap map of prefixes to namespaces
    */
   public static void getDeclaredNamespaces(SOAPElement aElement, Map aPrefixMap)
   {
      for(SOAPElement element = aElement; element != null; element = element.getParentElement())
      {
         for (Iterator it = element.getNamespacePrefixes(); it.hasNext();)
         {
            String prefix = (String) it.next();
            String ns = element.getNamespaceURI(prefix);
            if (!aPrefixMap.containsKey(prefix))
            {
               aPrefixMap.put(prefix, ns);
            }
         }
      }
   }
   
   /**
    * Finds any prefix declarations that are new or different 
    * and adds a local declaration to the DOM element
    * 
    * @param aSource
    * @param aTarget
    */
   private static void addNewDOMPrefixes(SOAPElement aSource, Element aTarget)
   {
      Map sourcePrefixes = new HashMap();
      getDeclaredNamespaces(aSource, sourcePrefixes);
      Map targetPrefixes = new HashMap();
      AeXmlUtil.getDeclaredNamespaces(aTarget, targetPrefixes);
      
      for (Iterator it = sourcePrefixes.entrySet().iterator(); it.hasNext();)
      {
         Map.Entry entry = (Map.Entry) it.next();
         if (AeUtil.notNullOrEmpty((String) entry.getValue()))
         {
            String prefixToCheck = (String) entry.getKey();
            String sourceNamespace = (String) entry.getValue();
                        
            // find prefix in the target
            String targetNamespace = (String) targetPrefixes.get(prefixToCheck);
            if (!sourceNamespace.equals(targetNamespace))
            {
               if (AeUtil.notNullOrEmpty(prefixToCheck))
               {
                  aTarget.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + prefixToCheck, sourceNamespace); //$NON-NLS-1$
               }
               else
               {
                  aTarget.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns", sourceNamespace); //$NON-NLS-1$
               }
            }
         }
      }
   }

   /**
    * Finds any prefix declarations that are new or different
    * and adds a local declaration to the SOAPElement
    * 
    * @param aSource
    * @param aTarget
    */
   private static void addNewSOAPPrefixes(Element aSource, SOAPElement aTarget) throws SOAPException
   {
      Map sourcePrefixes = new HashMap();
      AeXmlUtil.getDeclaredNamespaces(aSource, sourcePrefixes);
      Map targetPrefixes = new HashMap();
      getDeclaredNamespaces(aTarget, targetPrefixes);
      
      for (Iterator it = sourcePrefixes.entrySet().iterator(); it.hasNext();)
      {
         Map.Entry entry = (Map.Entry) it.next();
         if (AeUtil.notNullOrEmpty((String) entry.getValue()))
         {
            String prefixToCheck = (String) entry.getKey();
            String sourceNamespace = (String) entry.getValue();
                        
            // find prefix in the target
            String targetNamespace = (String) targetPrefixes.get(prefixToCheck);
            if (!sourceNamespace.equals(targetNamespace))
            {
               if (AeUtil.notNullOrEmpty(prefixToCheck))
               {
                  aTarget.addNamespaceDeclaration(prefixToCheck, sourceNamespace);
               }
            }
         }
      }
   }
   
}
