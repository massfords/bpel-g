// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/visitors/AeAbstractXPathNodeVisitor.java,v 1.1 2006/07/21 16:03:33 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.xpath.ast.visitors;

import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAbsLocPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAdditiveExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAllNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAndExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathCommentNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathEqualityExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathFilterExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathMultiplicativeExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathNameStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathOrExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathPathExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathPredicateNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathProcessingInstructionNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathRelationalExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathRelativeLocPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathRootXpathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathTextNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathUnaryExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathUnionExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode;
import org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor;

/**
 * An abstract base class that xpath AST visitors can extend.  This implementation provides
 * no-op impls of all of the visitor methods.  This is particularly useful for classes that
 * only need to do something for a small subset of the node types.
 */
public abstract class AeAbstractXPathNodeVisitor implements IAeXPathNodeVisitor
{
   /**
    * Default c'tor.
    */
   protected AeAbstractXPathNodeVisitor()
   {
   }
   
   /**
    * Called by all of the impls in this class, this method can be overridden by visitors that
    * need to do the same basic actions for many node types.
    * 
    * @param aNode
    */
   protected void visitBaseXPathNode(AeAbstractXPathNode aNode)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode)
    */
   public void visit(AeXPathFunctionNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode)
    */
   public void visit(AeXPathVariableNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode)
    */
   public void visit(AeXPathLiteralNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAbsLocPathNode)
    */
   public void visit(AeXPathAbsLocPathNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAdditiveExprNode)
    */
   public void visit(AeXPathAdditiveExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAllNodeStepNode)
    */
   public void visit(AeXPathAllNodeStepNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAndExprNode)
    */
   public void visit(AeXPathAndExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathCommentNodeStepNode)
    */
   public void visit(AeXPathCommentNodeStepNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathEqualityExprNode)
    */
   public void visit(AeXPathEqualityExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathFilterExprNode)
    */
   public void visit(AeXPathFilterExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathMultiplicativeExprNode)
    */
   public void visit(AeXPathMultiplicativeExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathNameStepNode)
    */
   public void visit(AeXPathNameStepNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathOrExprNode)
    */
   public void visit(AeXPathOrExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathPathExprNode)
    */
   public void visit(AeXPathPathExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathPredicateNode)
    */
   public void visit(AeXPathPredicateNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathProcessingInstructionNodeStepNode)
    */
   public void visit(AeXPathProcessingInstructionNodeStepNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathRelationalExprNode)
    */
   public void visit(AeXPathRelationalExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathRelativeLocPathNode)
    */
   public void visit(AeXPathRelativeLocPathNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathRootXpathNode)
    */
   public void visit(AeXPathRootXpathNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathTextNodeStepNode)
    */
   public void visit(AeXPathTextNodeStepNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathUnaryExprNode)
    */
   public void visit(AeXPathUnaryExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathUnionExprNode)
    */
   public void visit(AeXPathUnionExprNode aNode)
   {
      visitBaseXPathNode((AeAbstractXPathNode) aNode);
   }
}
