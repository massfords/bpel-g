// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/IAeXPathNodeVisitor.java,v 1.1 2006/07/21 16:03:32 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.xpath.ast;

/**
 * This interface is implemented by visitors of the XPath AST.  Each node in the AST will be
 * visited by the visitor.
 */
public interface IAeXPathNodeVisitor
{
   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathFunctionNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathVariableNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathLiteralNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathAbsLocPathNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathAdditiveExprNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathAllNodeStepNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathAndExprNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathCommentNodeStepNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathEqualityExprNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathFilterExprNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathMultiplicativeExprNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathNameStepNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathOrExprNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathPathExprNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathPredicateNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathProcessingInstructionNodeStepNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathRelationalExprNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathRelativeLocPathNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathRootXpathNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathTextNodeStepNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathUnaryExprNode aNode);

   /**
    * Visit the given xpath AST node.
    *
    * @param aNode
    */
   public void visit(AeXPathUnionExprNode aNode);
}
