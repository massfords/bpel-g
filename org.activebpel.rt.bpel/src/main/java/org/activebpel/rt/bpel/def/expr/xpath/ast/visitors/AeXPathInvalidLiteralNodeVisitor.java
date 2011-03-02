// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.expr.xpath.ast.visitors;

import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode;
import org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor;

/**
 * This visitor will visit all of the nodes in the xpath AST and return a list of literals that
 * are not contained within functions.  This is used specifically for validating joing 
 * conditions, where literals cannot be used unless they are params to a function.
 */
public class AeXPathInvalidLiteralNodeVisitor extends AeAbstractXPathNodeVisitor
{
   /** The list of invalid literals found during the visit. */
   private List mLiterals;
   
   /**
    * Default c'tor.
    */
   public AeXPathInvalidLiteralNodeVisitor()
   {
      setLiterals(new LinkedList());
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode)
    */
   public void visit(AeXPathLiteralNode aNode)
   {
      if (!hasFunctionParent(aNode))
         getLiterals().add(aNode);
   }
   
   /**
    * Walks up the tree and returns true if it finds an instance of AeXPathFunctionNode.
    * 
    * @param aNode
    */
   protected boolean hasFunctionParent(AeAbstractXPathNode aNode)
   {
      AeAbstractXPathNode node = aNode.getParent();
      
      while (node != null)
      {
         if (node instanceof AeXPathFunctionNode)
            return true;
         else
            node = node.getParent();
      }
      
      return false;
   }

   /**
    * @return Returns the literals.
    */
   public List getLiterals()
   {
      return mLiterals;
   }

   /**
    * @param aLiterals The literals to set.
    */
   protected void setLiterals(List aLiterals)
   {
      mLiterals = aLiterals;
   }
}
