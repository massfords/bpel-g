// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/visitors/AeAbstractTraversingXPathNodeVisitor.java,v 1.1 2006/07/21 16:03:33 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.xpath.ast.visitors;

import java.util.Iterator;
import java.util.Stack;

import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;

/**
 * This class is a simple traversing xpath node visitor.  It simply adds some convenience 
 * methods to aid in traversing the nodes.
 */
public abstract class AeAbstractTraversingXPathNodeVisitor extends AeAbstractXPathNodeVisitor
{
   /** The node stac. */
   private Stack mNodeStack;

   /**
    * Default c'tor.
    */
   protected AeAbstractTraversingXPathNodeVisitor()
   {
      super();
      
      setNodeStack(new Stack());
   }

   /**
    * @return Returns the nodeStack.
    */
   protected Stack getNodeStack()
   {
      return mNodeStack;
   }

   /**
    * @param aNodeStack The nodeStack to set.
    */
   protected void setNodeStack(Stack aNodeStack)
   {
      mNodeStack = aNodeStack;
   }

   /**
    * Pushes a node onto the stack.
    * 
    * @param aNode
    */
   protected void pushNode(AeAbstractXPathNode aNode)
   {
      getNodeStack().push(aNode);
   }

   /**
    * Pops a node off the stack.
    */
   protected void popNode()
   {
      getNodeStack().pop();
   }

   /**
    * Traverse the node's children.
    * 
    * @param aNode
    */
   protected void traverse(AeAbstractXPathNode aNode)
   {
      pushNode(aNode);
      for (Iterator iter = aNode.getChildren().iterator(); iter.hasNext(); )
      {
         AeAbstractXPathNode child = (AeAbstractXPathNode) iter.next();
         child.accept(this);
      }
      popNode();
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visitBaseXPathNode(org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode)
    */
   protected void visitBaseXPathNode(AeAbstractXPathNode aNode)
   {
      traverse(aNode);
   }
}
