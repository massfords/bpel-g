// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBDefaultNSVisitor.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.impl.fastdom.AeFastAttribute;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeFastNode;
import org.activebpel.rt.bpel.impl.fastdom.AeFastText;
import org.activebpel.rt.bpel.impl.fastdom.AeForeignNode;
import org.activebpel.rt.bpel.impl.fastdom.IAeVisitor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used to visit a Fast Document and forcibly qualify (with a prefix) all elements
 * that are in a default namespace.
 */
public class AeXMLDBDefaultNSVisitor implements IAeVisitor
{
   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastAttribute)
    */
   public void visit(AeFastAttribute aAttribute)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastDocument)
    */
   public void visit(AeFastDocument aDocument)
   {
      AeFastElement element = aDocument.getRootElement();
      if (element != null)
      {
         visit(element);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastElement)
    */
   public void visit(AeFastElement aElement)
   {
      for (Iterator i = aElement.getAttributes().iterator(); i.hasNext(); )
      {
         visit((AeFastAttribute) i.next());
      }

      List children = aElement.getChildNodes();
      for (Iterator i = children.iterator(); i.hasNext(); )
      {
         ((AeFastNode) i.next()).accept(this);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastText)
    */
   public void visit(AeFastText aText)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeForeignNode)
    */
   public void visit(AeForeignNode aForeignNode)
   {
      Node node = aForeignNode.getNode();

      if (node != null)
      {
         removeDefaultNamespace(node);
      }
   }
   
   /**
    * Recursively iterates through the W3C Node and changes any Node that is in a default namespace
    * to instead use a prefix.
    * 
    * @param aNode
    */
   protected void removeDefaultNamespace(Node aNode)
   {
      if (aNode instanceof Element)
      {
         Element elem = (Element) aNode;
         String ns = elem.getNamespaceURI();
         
         // Remove any default NS attribute
         if (elem.hasAttributeNS(IAeConstants.W3C_XMLNS, "xmlns")) //$NON-NLS-1$
            elem.removeAttributeNS(IAeConstants.W3C_XMLNS, "xmlns"); //$NON-NLS-1$
         if (elem.hasAttribute("xmlns")) //$NON-NLS-1$
            elem.removeAttribute("xmlns"); //$NON-NLS-1$

         if (AeUtil.notNullOrEmpty(ns))
         {
            // Set the element prefix.
            String nodeName = elem.getNodeName();
            if (!AeXmlUtil.hasColon(nodeName))
            {
               String prefix = AeXmlUtil.getOrCreatePrefix(elem, ns);
               elem.setPrefix(prefix);
            }
         }

         // Fixup all of this element's children.
         NodeList nl = aNode.getChildNodes();
         for (int i = 0; i < nl.getLength(); i++)
         {
            Node node = nl.item(i);
            removeDefaultNamespace(node);
         }
      }
   }
}
