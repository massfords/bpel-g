// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeCompareXML.java,v 1.17 2008/02/27 20:41:55 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.activebpel.rt.AeMessages;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Compare two xml elements for match (use setting of MatchOrder to force
 * order checking).
 */
public class AeCompareXML
{
   /** The namespace used for attribute directives in the expected document. */
   private static final String COMPARE_XML_DIRECTIVE_NS = "urn:active-endpoints:AeCompareXML:directive"; //$NON-NLS-1$
   /** name of the 'skipContents' attribute optionally found on an expected document. */
   private static final String ATTR_SKIP_CONTENTS = "skipContents"; //$NON-NLS-1$

   /** list of errors */
   private List mErrors = new ArrayList();
   /** flag for matching exact order of elements */
   private boolean mMatchOrder = false;
   /** Flag indicating whether wildcards are supported. */
   private boolean mSupportWildcards = false;

   /**
    * Convenience method for comparing to DOM elements.
    * @param aElement1
    * @param aElement2
    */
   public static boolean compare(Element aElement1, Element aElement2)
   {
      AeCompareXML comp = new AeCompareXML();
      return comp.compareBothElements(aElement1, aElement2, "/"); //$NON-NLS-1$
   }

   /**
    * A main entry point for compare it first parses the passed documents and then
    * calls compareBothElements.  If either parse fails false is returned.
    * @return boolean true if documents match
    */
   public boolean compareBothDocuments(String aStr1, String aStr2)
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase(true, false);
         Document doc1 = parser.loadDocumentFromString(aStr1, null);
         Document doc2 = parser.loadDocumentFromString(aStr2, null);

         return compareBothElements(doc1.getDocumentElement(), doc2.getDocumentElement(), "/"); //$NON-NLS-1$
      }
      catch (Exception ex)
      {
         return false;
      }
   }

   /**
    * Main entry point for compare it calls compareElement for 1 and 2 and then
    * 2 and 1 to make sure that neither has extra elements or attributes.
    * @return boolean true if elements match
    */
   public boolean compareBothElements(Element aElement1, Element aElement2, String aPath)
   {
      return compareElements(aElement1, aElement2, aPath) &&
              compareElements(aElement2, aElement1, aPath);
   }

   /**
    * Only considers the elements namespaces if at least one of the elements
    * have a namespace, otherwise it just compares the node names.
    * @param aOne
    * @param aTwo
    */
   protected boolean elementNamesMatch(Element aOne, Element aTwo)
   {
      if (AeUtil.isNullOrEmpty(aOne.getNamespaceURI()) && AeUtil.isNullOrEmpty(aTwo.getNamespaceURI()))
         return AeUtil.compareObjects(aOne.getNodeName(), aTwo.getNodeName());
      else
         return AeUtil.compareObjects(aOne.getLocalName(), aTwo.getLocalName()) &&
                AeUtil.compareObjects(aOne.getNamespaceURI(), aTwo.getNamespaceURI());
   }

   /**
    * Compares that element1's name, attributes and child elements are all in
    * element2, but ignores the order of elements if match order is false
    * @param aElement1
    * @param aElement2
    * @param aPath
    * @return boolean
    */
   public boolean compareElements(Element aElement1, Element aElement2, String aPath)
   {
      HashMap usedChildren2 = new HashMap();
      String path = aPath + "/" + aElement1.getNodeName(); //$NON-NLS-1$

      // check if element name matches first
      if (!elementNamesMatch(aElement1, aElement2))
      {
         error(aElement1, aElement2, AeMessages.getString("AeCompareXML.1")); //$NON-NLS-1$
         return false;
      }

      // check for attributes matching
      if (!compareAttributes(aElement1, aElement2, path))
      {
         // attributes didn't match return false
         error(aElement1, aElement2, AeMessages.getString("AeCompareXML.4")); //$NON-NLS-1$
         return false;
      }

      // should we skip the element comparison?
      if (isSkipElementContents(aElement1) || isSkipElementContents(aElement2))
      {
         return true;
      }

      // ensure that they have the same children
      for(int i= 0; i < aElement1.getChildNodes().getLength(); ++i)
      {
         Node lNode1 = aElement1.getChildNodes().item(i);
         if(isSkip(lNode1))
            continue;
         // set flag saying not found yet (in case match any order)
         boolean bFound = false;
         for(int j = 0; j < aElement2.getChildNodes().getLength(); ++j)
         {
            // if we have already matched this child against an item then skip
            Integer lNext = new Integer(j);
            if(usedChildren2.get(lNext) != null)
               continue;

            Node lNode2 = aElement2.getChildNodes().item(j);
            // check if skipping node type
            if(isSkip(lNode2))
            {
               usedChildren2.put(lNext, lNext);
               continue;
            }

            // check if node data matches
            if(lNode1.getNodeType() == lNode2.getNodeType())
            {
               if(lNode1.getNodeType() == Node.TEXT_NODE)
               {
                  String node1Value = lNode1.getNodeValue().trim();
                  String node2Value = lNode2.getNodeValue().trim();
                  if(compareNodeValues(node1Value, node2Value))
                  {
                     usedChildren2.put(lNext, lNext);
                     bFound = true;
                     break;
                  }
               }
               else if(lNode1.getNodeType() == Node.ELEMENT_NODE)
               {
                  if(compareElements((Element)lNode1, (Element)lNode2, path))
                  {
                     usedChildren2.put(lNext, lNext);
                     bFound = true;
                     break;
                  }
               }
               else // shouldn't be here, but ignore
               {
                  usedChildren2.put(lNext, lNext);
                  bFound = true;
                  break;
               }
            }

            // if not matching order and nothing found then return false
            if(getMatchOrder())
            {
               error(lNode1, AeMessages.getString("AeCompareXML.2")); //$NON-NLS-1$
               return false;
            }
         }
         // none found in whole array then return false
         if(bFound == false)
         {
            error(lNode1, AeMessages.getString("AeCompareXML.2")); //$NON-NLS-1$
            return false;
         }
      }

      // element name, attributes and children matched so return true
      return true;
   }

   /**
    * Returns true if the element contents should be skipped during
    * comparison.
    *
    * @param aElement
    */
   protected boolean isSkipElementContents(Element aElement)
   {
      // If we aren't supporting wildcards, then we can never skip element contents.
      if (!isSupportWildcards())
         return false;

      // If the element has the 'skipContents' flag set, obey it
      if (aElement.hasAttributeNS(COMPARE_XML_DIRECTIVE_NS, ATTR_SKIP_CONTENTS))
         return "true".equals(aElement.getAttributeNS(COMPARE_XML_DIRECTIVE_NS, ATTR_SKIP_CONTENTS)); //$NON-NLS-1$

      // No any reason to skip contents
      return false;
   }

   /**
    * Compare two node values with or without wildcards.
    *
    * @param aNode1Value
    * @param aNode2Value
    */
   protected boolean compareNodeValues(String aNode1Value, String aNode2Value)
   {
      return compareValues(aNode1Value, aNode2Value);
   }

   /**
    * Compare two attribute values with or without wildcards.
    *
    * @param aAttr1Value
    * @param aAttr2Value
    */
   protected boolean compareAttributeValues(String aAttr1Value, String aAttr2Value)
   {
      return compareValues(aAttr1Value, aAttr2Value);
   }
   
   /**
    * Compares two values with or without wildcards.
    * 
    * @param aValue1
    * @param aValue2
    */
   protected boolean compareValues(String aValue1, String aValue2)
   {
      if (aValue1.equals(aValue2))
         return true;

      if (!isSupportWildcards())
         return false;

      return "*".equals(aValue1) || "*".equals(aValue2); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Geter for the errors
    */
   public List getErrors()
   {
      return mErrors;
   }

   /**
    * @param aElement1
    * @param aElement2
    * @param aString
    */
   private void error(Element aElement1, Element aElement2, String aString)
   {
      StringBuffer bugger = new StringBuffer();
      addNodeDetail(aElement1, bugger);
      addNodeDetail(aElement2, bugger);
      mErrors.add(aString + "\n" + bugger.toString()); //$NON-NLS-1$
   }

   /**
    * Records an error message for this node
    * @param aNode
    * @param aMessage
    */
   private void error(Node aNode, String aMessage)
   {
      StringBuffer buffer = new StringBuffer();
      addNodeDetail(aNode, buffer);
      mErrors.add(aMessage + "\n" + buffer.toString()); //$NON-NLS-1$
   }

   /**
    * Records an error message for this attribute
    * @param aAttrib
    * @param aMessage
    */
   private void error(Attr aAttrib, String aMessage)
   {
      mErrors.add(aMessage + " " + aAttrib.getNodeName()+"='"+aAttrib.getNodeValue()+"'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
   }

   /**
    * Adds a detail message for this node
    * @param aNode
    * @param aBuffer
    */
   private void addNodeDetail(Node aNode, StringBuffer aBuffer)
   {
      if (aNode instanceof Element)
      {
         aBuffer.append(AeXMLParserBase.documentToString(aNode)).append('\n');
      }
      else if (aNode.getNodeType() == Node.TEXT_NODE)
      {
         aBuffer.append("text node value=" + aNode.getNodeValue()); //$NON-NLS-1$
      }
      else
      {
         aBuffer.append(aNode.getNamespaceURI());
         aBuffer.append(':');
         aBuffer.append(aNode.getNodeName());
         aBuffer.append('\n');
      }
   }

   /**
    * Return true if thise node should be skipped, only returns true for non-
    * blank text and elements.
    * @param aNode
    * @return boolean
    */
   private boolean isSkip(Node aNode)
   {
      if(aNode.getNodeType() != Node.ELEMENT_NODE)
      {
         if(aNode.getNodeType() == Node.TEXT_NODE)
         {
            if(AeUtil.getSafeString(aNode.getNodeValue()).trim().length() > 0)
               return false;
         }
         // must be comment or blank text (or unknown node type)
         return true;
      }
      return false;
   }

   /**
    * Checks that all attributes set in element1 are in element2 with the same
    * value. Note that it doesn't check the reverse.
    * @param aElement1
    * @param aElement2
    * @param aPath
    * @return boolean
    */
   protected boolean compareAttributes(Element aElement1, Element aElement2, String aPath)
   {
      NamedNodeMap element1Attrs = aElement1.getAttributes();

      for(int i =0; i < element1Attrs.getLength(); ++i)
      {
         Attr attr = (Attr)element1Attrs.item(i);
         if(isSkippedAttribute(attr))
            continue;

         String element1AttrValue = attr.getNodeValue();
         String element2AttrValue = getAttributeValue(attr, aElement2);
         if(element2AttrValue != null && element1AttrValue != null)
         {
            if(! compareAttributeValues(element1AttrValue, element2AttrValue))
            {
               error(attr, AeMessages.getString("AeCompareXML.11")); //$NON-NLS-1$
               return false;
            }
         }
         else if (element2AttrValue != element1AttrValue)
         {
            error(attr, AeMessages.getString("AeCompareXML.11")); //$NON-NLS-1$
            return false;
         }
      }

      return true;
   }

   /**
    * Returns true if the attribute should be skipped during comparison.
    *
    * @param aAttribute
    */
   protected boolean isSkippedAttribute(Attr aAttribute)
   {
      if (aAttribute.getNodeName().startsWith("xmlns")) //$NON-NLS-1$
         return true;
      if (AeUtil.compareObjects(COMPARE_XML_DIRECTIVE_NS, aAttribute.getNamespaceURI()))
         return true;
      return false;
   }

   /**
    * Attempts to get the attribute by its namespace and local name and then
    * falls back to local name if not found.
    * @param aAttribute
    * @param aElement
    */
   protected String getAttributeValue(Attr aAttribute, Element aElement)
   {
      String namespace = aAttribute.getNamespaceURI();
      String localName = aAttribute.getLocalName();
      String value = aElement.getAttributeNS(namespace, localName);
      if (AeUtil.isNullOrEmpty(value))
      {
         if (localName == null)
            localName = aAttribute.getName();
         value = aElement.getAttribute(localName);
      }
      return value;
   }

   /**
    * Set match order to true if the order of elements is important.
    * @param matchOrder
    */
   public void setMatchOrder(boolean matchOrder)
   {
      mMatchOrder = matchOrder;
   }

   /**
    * Returns the current setting of match order which determines if the order
    * of elements is important.
    */
   public boolean getMatchOrder()
   {
      return mMatchOrder;
   }

   /**
    * @return Returns the supportWildcards.
    */
   public boolean isSupportWildcards()
   {
      return mSupportWildcards;
   }

   /**
    * @param aSupportWildcards the supportWildcards to set
    */
   public void setSupportWildcards(boolean aSupportWildcards)
   {
      mSupportWildcards = aSupportWildcards;
   }
}
