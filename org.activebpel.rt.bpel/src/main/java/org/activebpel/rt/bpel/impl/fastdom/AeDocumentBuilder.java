// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/fastdom/AeDocumentBuilder.java,v 1.2 2005/01/28 20:23:10 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.fastdom;

import java.util.Iterator;
import java.util.LinkedList;

import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Builds a standard XML <code>Document</code> from an
 * <code>AeFastDocument</code>.
 */
public class AeDocumentBuilder implements IAeVisitor
{
   /** The standard XML <code>Document</code> that this builder is building. */
   private Document mDocument;

   /** Stack of nodes pending construction. */
   private final LinkedList mNodeStack = new LinkedList();

   /**
    * Builds a standard XML <code>Document</code> from the specified
    * <code>AeFastDocument</code>.
    *
    * @param aDocument
    */
   public Document build(AeFastDocument aDocument)
   {
      visit(aDocument);

      return getDocument();
   }

   /**
    * Returns the current node at the top of the stack.
    */
   protected Node getCurrentNode()
   {
      return (Node) getNodeStack().getFirst();
   }

   /**
    * Returns the standard XML <code>Document</code> that this builder is
    * building.
    */
   public Document getDocument()
   {
      if (mDocument == null)
      {
         AeXMLParserBase parser = new AeXMLParserBase();
         parser.setNamespaceAware(false);
         parser.setValidating(false);

         mDocument = parser.createDocument();
      }

      return mDocument;
   }

   /**
    * Returns the stack of nodes pending construction.
    */
   protected LinkedList getNodeStack()
   {
      return mNodeStack;
   }

   /**
    * Pops the <code>Node</code> at the top of the node stack.
    */
   protected Node popNode()
   {
      return (Node) getNodeStack().removeFirst();
   }

   /**
    * Pushes the specified <code>Node</code> onto the node stack.
    *
    * @param aNode
    */
   protected void pushNode(Node aNode)
   {
      getNodeStack().addFirst(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastAttribute)
    */
   public void visit(AeFastAttribute aAttribute)
   {
      Element element = (Element) getCurrentNode();
      element.setAttribute(aAttribute.getName(), aAttribute.getValue());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastDocument)
    */
   public void visit(AeFastDocument aDocument)
   {
      AeFastElement element = aDocument.getRootElement();
      if (element != null)
      {
         pushNode(getDocument());
         visit(element);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastElement)
    */
   public void visit(AeFastElement aElement)
   {
      // Add a new Element to the node at the top of the stack.
      Element element = getDocument().createElement(aElement.getName());
      getCurrentNode().appendChild(element);

      // Push the new Element onto the stack.
      pushNode(element);

      try
      {
         for (Iterator i = aElement.getAttributes().iterator(); i.hasNext(); )
         {
            visit((AeFastAttribute) i.next());
         }

         for (Iterator i = aElement.getChildNodes().iterator(); i.hasNext(); )
         {
            ((AeFastNode) i.next()).accept(this);
         }
      }
      finally
      {
         // Always pop the Element from the stack.
         popNode();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastText)
    */
   public void visit(AeFastText aText)
   {
      Text text = getDocument().createTextNode(aText.getValue());
      getCurrentNode().appendChild(text);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeForeignNode)
    */
   public void visit(AeForeignNode aForeignNode)
   {
      Node node = aForeignNode.getNode();

      if (node != null)
      {
         getCurrentNode().appendChild(getDocument().importNode(node, true));
      }
   }
}
