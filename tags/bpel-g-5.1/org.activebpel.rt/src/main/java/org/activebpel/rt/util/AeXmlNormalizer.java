// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeXmlNormalizer.java,v 1.3 2008/03/27 15:11:25 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * An XML normalizer - this class takes one or more XML documents
 * and "normalizes" them for the purposes of display or compare.  
 * This amounts to removing unneeded whitespace and using consistent 
 * prefixes for all qualified elements and attributes.
 */
public class AeXmlNormalizer
{
   private static final String COLON = ":"; //$NON-NLS-1$
   private static final String XMLNS_PREFIX = "xmlns"; //$NON-NLS-1$

   /**
    * Normalizes the given Element.
    *
    * @param aElement
    */
   public static Element normalize(Element aElement)
   {
      List list = normalize(Collections.singletonList(aElement));
      return (Element) list.get(0);
   }

   /**
    * Normalizes a list of Elements.
    *
    * @param aElements
    */
   public static List normalize(List aElements)
   {
      AeXmlNormalizer normalizer = new AeXmlNormalizer();
      return normalizer.normalizeElements(aElements);
   }

   /** The namespace manager to use when normalizing. */
   private AeNamespaceManager mNamespaceManager;
   /** The document to use when creating nodes during normalization. */
   private Document mDocument;

   /**
    * C'tor.
    */
   public AeXmlNormalizer()
   {
      setNamespaceManager(new AeNamespaceManager());
   }

   /**
    * Normalizes a list of elements.  Returns a new list of elements
    * with the newly created (normalized) results.
    *
    * @param aElements
    */
   protected List normalizeElements(List aElements)
   {
      // First, configure the namespace manager by adding in all
      // namespaces from all elements being normalized.
      for (Iterator iter = aElements.iterator(); iter.hasNext(); )
      {
         Element element = (Element) iter.next();
         getNamespaceManager().addNamespaces(element);
      }

      List newElements = new ArrayList();
      // Now normalize each element using the configured namespace manager.
      for (Iterator iter = aElements.iterator(); iter.hasNext(); )
      {
         Element element = (Element) iter.next();
         setDocument(AeXmlUtil.newDocument());
         Element newElement = normalizeElement(element);
         // Add in the namespace declarations to the root node only.
         for (Iterator iter2 = getNamespaceManager().getPrefixToNamespaceMap().entrySet().iterator(); iter2.hasNext(); )
         {
            Map.Entry entry = (Map.Entry) iter2.next();
            String prefix = (String) entry.getKey();
            String namespace = (String) entry.getValue();
            newElement.setAttributeNS(IAeConstants.W3C_XMLNS, XMLNS_PREFIX + COLON + prefix, namespace);
         }
         newElements.add(newElement);
      }

      return newElements;
   }

   /**
    * Normalize a single element.
    *
    * @param aElement
    */
   protected Element normalizeElement(Element aElement)
   {
      Element newElement = null;

      // create the new element
      String namespace = aElement.getNamespaceURI();
      String elemPrefix = getNamespaceManager().getPrefixForNamespace(namespace);
      newElement = getDocument().createElementNS(namespace, elemPrefix + COLON + aElement.getLocalName());

      // Now normalize all children (attributes, elements, etc) and add them
      // to the new element.
      List childNodes = selectNodes(aElement);

      // Now iterate through all children.
      for (Iterator iter = childNodes.iterator(); iter.hasNext(); )
      {
         Node child = (Node) iter.next();
         switch (child.getNodeType())
         {
            case  Node.ELEMENT_NODE:
               newElement.appendChild(normalizeElement((Element) child));
               break;
            case Node.TEXT_NODE:
               if (AeUtil.notNullOrEmpty(child.getNodeValue()))
               {
                  Node txtNode = getDocument().createTextNode(child.getNodeValue());
                  newElement.appendChild(txtNode);
               }
               break;
            case Node.CDATA_SECTION_NODE:
               Node cdataNode = getDocument().createCDATASection(child.getNodeValue());
               newElement.appendChild(cdataNode);
               break;
            case Node.ATTRIBUTE_NODE:
               Attr attribute = (Attr) child;
               String namespaceURI = attribute.getNamespaceURI();
               // Case where the attribute is unqualified
               if (AeUtil.isNullOrEmpty(namespaceURI))
               {
                  newElement.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
               }
               // Case where the attribute is qualified (except namespace decls)
               else if (!IAeConstants.W3C_XMLNS.equals(namespaceURI))
               {
                  String localPart = attribute.getLocalName();
                  String attrPrefix = getNamespaceManager().getPrefixForNamespace(namespaceURI);
                  String qualifiedName = attrPrefix + COLON + localPart;
                  newElement.setAttributeNS(namespaceURI, qualifiedName, attribute.getNodeValue());
               }
               break;
            default:
               break;
         }
      }

      return newElement;
   }

   /**
    * Selects some nodes by xpath and suppresses any exceptions.
    * 
    * @param aElement
    */
   protected List selectNodes(Element aElement)
   {
      try
      {
         return AeXPathUtil.selectNodes(aElement, "@*|node()"); //$NON-NLS-1$
      }
      catch (AeException ex)
      {
         AeException.logError(ex);
         return Collections.EMPTY_LIST;
      }
   }

   /**
    * @return Returns the namespaceManager.
    */
   protected AeNamespaceManager getNamespaceManager()
   {
      return mNamespaceManager;
   }

   /**
    * @param aNamespaceManager the namespaceManager to set
    */
   protected void setNamespaceManager(AeNamespaceManager aNamespaceManager)
   {
      mNamespaceManager = aNamespaceManager;
   }

   /**
    * @return Returns the currentDocument.
    */
   protected Document getDocument()
   {
      return mDocument;
   }

   /**
    * @param aDocument the currentDocument to set
    */
   protected void setDocument(Document aDocument)
   {
      mDocument = aDocument;
   }

   /**
    * Namespace manager - keeps mappings of prefix to namespace and
    * namespace to prefix.  This manager is populated when normalizing
    * documents.
    */
   protected static class AeNamespaceManager
   {
      /** Map of prefix to namespace. */
      private Map mPrefixToNamespaceMap = new HashMap();
      /** Map of namespace to prefix. */
      private Map mNamespaceToPrefixMap = new HashMap();

      /**
       * C'tor.
       */
      public AeNamespaceManager()
      {
         // Add implicit mapping from "xml" prefix to the W3C namespace
         mNamespaceToPrefixMap.put(IAeConstants.W3C_XML_NAMESPACE, "xml"); //$NON-NLS-1$
      }
      
      /**
       * Adds namespace mappings for all namespace declarations in
       * the given element (anywhere in the element, including all
       * children).
       *
       * @param aElement
       */
      public void addNamespaces(Element aElement)
      {
         try
         {
            List nodes = AeXPathUtil.selectNodes(aElement, "//*"); //$NON-NLS-1$
            for (Iterator iter = nodes.iterator(); iter.hasNext(); )
            {
               Element elem = (Element) iter.next();
               readNamespaceDeclarations(elem);
            }
         }
         catch (AeException ex)
         {
            AeException.logError(ex);
         }
      }

      /**
       * Reads all of the namespace declarations in the given element
       * and adds mappings for them.
       *
       * @param aCurrentDefObj
       */
      protected void readNamespaceDeclarations(Element aElement)
      {
         if (aElement.hasAttributes())
         {
            // Loop through and add all attributes which are part of the xmlns to the map
            NamedNodeMap attrNodes = aElement.getAttributes();
            for (int i = 0, length = attrNodes.getLength(); i < length; i++)
            {
               Attr attr = (Attr) attrNodes.item(i);
               if (IAeConstants.W3C_XMLNS.equals(attr.getNamespaceURI()))
               {
                  String prefix = attr.getLocalName();
                  String namespaceURI = attr.getNodeValue();
                  if (XMLNS_PREFIX.equals(prefix))
                     prefix = "def"; //$NON-NLS-1$
                  addNamespace(prefix, namespaceURI);
               }
            }
         }
      }

      /**
       * Adds a prefix to namespace mapping to the manager.  If the
       * prefix is already mapped, a new prefix is created and added.
       * The resulting prefix is returned.  If another prefix is already
       * mapped to this namespace, then no mappings are made and that
       * prefix is returned.
       *
       * @param aPrefix
       * @param aNamespace
       */
      public String addNamespace(String aPrefix, String aNamespace)
      {
         if (mNamespaceToPrefixMap.containsKey(aNamespace))
            return (String) mNamespaceToPrefixMap.get(aNamespace);

         String prefix = aPrefix;
         if (mPrefixToNamespaceMap.containsKey(prefix))
            prefix = AeUtil.generateUniqueName(prefix, mPrefixToNamespaceMap.keySet());
         mPrefixToNamespaceMap.put(prefix, aNamespace);
         mNamespaceToPrefixMap.put(aNamespace, prefix);
         return prefix;
      }

      /**
       * Gets the namespace mapped to the given prefix.
       *
       * @param aPrefix
       */
      public String getNamespaceForPrefix(String aPrefix)
      {
         return (String) mPrefixToNamespaceMap.get(aPrefix);
      }

      /**
       * Gets the prefix mapped to the given namespace.
       *
       * @param aNamespace
       */
      public String getPrefixForNamespace(String aNamespace)
      {
         return (String) mNamespaceToPrefixMap.get(aNamespace);
      }

      /**
       * Gets the map of prefix to namespace.
       */
      public Map getPrefixToNamespaceMap()
      {
         return mPrefixToNamespaceMap;
      }
   }
}
