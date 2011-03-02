// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/AeWSResourceXPathGenerator.java,v 1.1 2007/12/17 16:41:42 ckeller Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Used to generate an xpath for a problem. This generator takes a DOM Node and
 * generates an xpath to that Node suitable for problem reporting. Later, the
 * generated xpath will be used to identify from where in the source file a
 * problem originated.
 * 
 *  FIXMEQ (builders) use the XPath nodes/models here - then serialize?
 */
public class AeWSResourceXPathGenerator
{
   /** The standard namespaces to use when generating XPath. */
   private IAeWSResourceStandardNamespaces mNamespaces;

   /**
    *
    */
   public AeWSResourceXPathGenerator(IAeWSResourceStandardNamespaces aNamespaces)
   {
      setNamespaces(aNamespaces);
   }

   /**
    * Generates the xpath.
    *
    * @param aNode
    */
   public String generateXPath(Node aNode)
   {
      List steps = new ArrayList();

      Node node = aNode;

      while (node != null)
      {
         switch (node.getNodeType())
         {
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
               steps.add(0, "text()"); //$NON-NLS-1$
               break;
            case Node.ELEMENT_NODE:
               Element elem = (Element) node;
               String elemName = getElementName(elem);
               Integer elemPos = new Integer(getElementPosition(elem));
               steps.add(0, MessageFormat.format(
                     "{0}{1,choice,1#|1<[{1,number,integer}]}", new Object[] { elemName, elemPos })); //$NON-NLS-1$
               break;
            case Node.ATTRIBUTE_NODE:
               Attr attr = (Attr) node;
               String attrName = getAttributeName(attr);
               steps.add(0, "@" + attrName); //$NON-NLS-1$
               break;
         }
         node = node.getParentNode();
      }

      return "/" + AeUtil.joinToStringObjects(steps, "/"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Returns the name of the attribute.
    *
    * @param aAttr
    */
   protected String getAttributeName(Attr aAttr)
   {
      if (AeUtil.notNullOrEmpty(aAttr.getNamespaceURI()))
      {
         String prefix = getNamespaces().resolveNamespaceToPrefix(aAttr.getNamespaceURI());
         String localName = aAttr.getLocalName();
         return prefix + ":" + localName; //$NON-NLS-1$
      }
      else
      {
         return aAttr.getName();
      }
   }

   /**
    * Returns the element position.
    *
    * @param aElem
    */
   protected int getElementPosition(Element aElem)
   {
      int position = 1;
      if (aElem.getParentNode() != null)
      {
         NodeList nl = aElem.getParentNode().getChildNodes();
         for (int i = 0; i < nl.getLength(); i++)
         {
            Node node = nl.item(i);
            if (node == aElem)
               break;
            if (node.getNodeType() == Node.ELEMENT_NODE && elementNamesEqual(aElem, (Element) node))
               position++;
         }
      }
      return position;
   }

   /**
    * Compare two elements to see if their names are equal.
    *
    * @param aElement1
    * @param aElement2
    */
   protected boolean elementNamesEqual(Element aElement1, Element aElement2)
   {
      return AeUtil.compareObjects(aElement1.getNamespaceURI(), aElement2.getNamespaceURI())
            && AeUtil.compareObjects(aElement1.getLocalName(), aElement2.getLocalName());
   }

   /**
    * Returns the name of the element.
    *
    * @param aElem
    */
   protected String getElementName(Element aElem)
   {
      if (AeUtil.notNullOrEmpty(aElem.getNamespaceURI()))
      {
         String prefix = getNamespaces().resolveNamespaceToPrefix(aElem.getNamespaceURI());
         String localName = aElem.getLocalName();
         return prefix + ":" + localName; //$NON-NLS-1$
      }
      else
      {
         return aElem.getNodeName();
      }
   }

   /**
    * @return Returns the namespaces.
    */
   protected IAeWSResourceStandardNamespaces getNamespaces()
   {
      return mNamespaces;
   }

   /**
    * @param aNamespaces the namespaces to set
    */
   protected void setNamespaces(IAeWSResourceStandardNamespaces aNamespaces)
   {
      mNamespaces = aNamespaces;
   }
}
