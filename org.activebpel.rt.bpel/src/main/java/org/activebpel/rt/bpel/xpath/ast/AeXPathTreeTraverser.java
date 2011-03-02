// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/AeXPathTreeTraverser.java,v 1.1 2006/07/21 16:03:32 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.xpath.ast;

import java.util.Iterator;

/**
 * Used to traverse the XPath AST.
 */
public class AeXPathTreeTraverser
{
   /** The xpath node visitor. */
   private IAeXPathNodeVisitor mVisitor;

   /**
    * Simple constructor.
    * 
    * @param aVisitor
    */
   public AeXPathTreeTraverser(IAeXPathNodeVisitor aVisitor)
   {
      setVisitor(aVisitor);
   }

   /**
    * Traverse the entire tree using the given visitor.
    * 
    * @param aXPathNode
    */
   public void traverse(AeAbstractXPathNode aXPathNode)
   {
      // Traverse the given node.
      aXPathNode.accept(getVisitor());

      // Traverse all of that node's children.
      for (Iterator iter = aXPathNode.getChildren().iterator(); iter.hasNext(); )
      {
         AeAbstractXPathNode child = (AeAbstractXPathNode) iter.next();
         traverse(child);
      }
   }

   /**
    * @return Returns the visitor.
    */
   protected IAeXPathNodeVisitor getVisitor()
   {
      return mVisitor;
   }

   /**
    * @param aVisitor The visitor to set.
    */
   protected void setVisitor(IAeXPathNodeVisitor aVisitor)
   {
      mVisitor = aVisitor;
   }
}
