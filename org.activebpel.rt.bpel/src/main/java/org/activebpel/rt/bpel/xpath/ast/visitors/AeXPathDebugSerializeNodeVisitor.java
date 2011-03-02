// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/visitors/AeXPathDebugSerializeNodeVisitor.java,v 1.1 2006/07/21 16:03:33 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.xpath.ast.visitors;

import java.text.MessageFormat;
import java.util.Stack;

import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathAxisNode;
import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathBooleanNode;
import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathOperatorNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAdditiveExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAllNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAndExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathCommentNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathEqualityExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathMultiplicativeExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathNameStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathOrExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathProcessingInstructionNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathRelationalExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathTextNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathUnaryExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathUnionExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode;
import org.activebpel.rt.util.AeUnsynchronizedCharArrayWriter;

/**
 * This visitor is used to serialize the XPath AST to a String for debugging purposes.
 */
public class AeXPathDebugSerializeNodeVisitor extends AeAbstractTraversingXPathNodeVisitor
{
   /** A string buffer. */
   private AeUnsynchronizedCharArrayWriter mBuffer;

   /**
    * Default c'tor.
    */
   public AeXPathDebugSerializeNodeVisitor()
   {
      setBuffer(new AeUnsynchronizedCharArrayWriter());
      setNodeStack(new Stack());
   }

   /**
    * Default visit for nodes that are specifically handled.
    *
    * @param aNode
    */
   protected void visitBaseXPathNode(AeAbstractXPathNode aNode)
   {
      appendIndentString();
      getBuffer().write(aNode.getType());
      getBuffer().write('\n');

      traverse(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode)
    */
   public void visit(AeXPathFunctionNode aNode)
   {
      appendIndentString();
      String msg = MessageFormat.format("Function: {0}()\n", new Object[] { aNode.getFunctionQName().getLocalPart() }); //$NON-NLS-1$
      getBuffer().write(msg);

      traverse(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode)
    */
   public void visit(AeXPathVariableNode aNode)
   {
      appendIndentString();
      String msg = MessageFormat.format("VarReference: {0}\n", new Object[] { aNode.getVariableQName().getLocalPart() }); //$NON-NLS-1$
      getBuffer().write(msg);

      traverse(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode)
    */
   public void visit(AeXPathLiteralNode aNode)
   {
      appendIndentString();
      String msg = MessageFormat.format("Literal[{0}]\n", new Object[] { aNode.getValue().toString() }); //$NON-NLS-1$
      getBuffer().write(msg);

      traverse(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathNameStepNode)
    */
   public void visit(AeXPathNameStepNode aNode)
   {
      appendIndentString();
      String msg = MessageFormat.format(
            "NameStep[axis={0}, {1}:{2}]\n",  //$NON-NLS-1$
            new Object[] { new Integer(aNode.getAxis()), aNode.getPrefix(), aNode.getLocalName() });
      getBuffer().write(msg);

      traverse(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAllNodeStepNode)
    */
   public void visit(AeXPathAllNodeStepNode aNode)
   {
      visitAxisNode((AeAbstractXPathAxisNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathCommentNodeStepNode)
    */
   public void visit(AeXPathCommentNodeStepNode aNode)
   {
      visitAxisNode((AeAbstractXPathAxisNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathProcessingInstructionNodeStepNode)
    */
   public void visit(AeXPathProcessingInstructionNodeStepNode aNode)
   {
      appendIndentString();
      String msg = MessageFormat.format(
            "{0}[axis={1} name={2}]\n",  //$NON-NLS-1$
            new Object[] { aNode.getType(), new Integer(aNode.getAxis()), aNode.getName() });
      getBuffer().write(msg);

      traverse(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathTextNodeStepNode)
    */
   public void visit(AeXPathTextNodeStepNode aNode)
   {
      visitAxisNode((AeAbstractXPathAxisNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAndExprNode)
    */
   public void visit(AeXPathAndExprNode aNode)
   {
      visitBooleanNode((AeAbstractXPathBooleanNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathOrExprNode)
    */
   public void visit(AeXPathOrExprNode aNode)
   {
      visitBooleanNode((AeAbstractXPathBooleanNode) aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathUnionExprNode)
    */
   public void visit(AeXPathUnionExprNode aNode)
   {
      visitBooleanNode((AeAbstractXPathBooleanNode) aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAdditiveExprNode)
    */
   public void visit(AeXPathAdditiveExprNode aNode)
   {
      visitOperatorNode((AeAbstractXPathOperatorNode) aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathEqualityExprNode)
    */
   public void visit(AeXPathEqualityExprNode aNode)
   {
      visitOperatorNode((AeAbstractXPathOperatorNode) aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathMultiplicativeExprNode)
    */
   public void visit(AeXPathMultiplicativeExprNode aNode)
   {
      visitOperatorNode((AeAbstractXPathOperatorNode) aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathRelationalExprNode)
    */
   public void visit(AeXPathRelationalExprNode aNode)
   {
      visitOperatorNode((AeAbstractXPathOperatorNode) aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathUnaryExprNode)
    */
   public void visit(AeXPathUnaryExprNode aNode)
   {
      visitOperatorNode((AeAbstractXPathOperatorNode) aNode);
   }

   /**
    * Generic visit method for boolean nodes.
    * 
    * @param aNode
    */
   protected void visitBooleanNode(AeAbstractXPathBooleanNode aNode)
   {
      appendIndentString();
      String msg = MessageFormat.format(
            "{0}[create={1}]\n",  //$NON-NLS-1$
            new Object[] { aNode.getType(), new Boolean(aNode.isCreate()) });
      getBuffer().write(msg);

      traverse(aNode);
   }
   
   /**
    * Generic visit method for operator nodes.
    * 
    * @param aNode
    */
   protected void visitOperatorNode(AeAbstractXPathOperatorNode aNode)
   {
      appendIndentString();
      String msg = MessageFormat.format(
            "{0}[operator={1}]\n",  //$NON-NLS-1$
            new Object[] { aNode.getType(), new Integer(aNode.getOperator()) });
      getBuffer().write(msg);

      traverse(aNode);
   }
   
   /**
    * Generic visit method for axis nodes.
    * 
    * @param aNode
    */
   protected void visitAxisNode(AeAbstractXPathAxisNode aNode)
   {
      appendIndentString();
      String msg = MessageFormat.format(
            "{0}[axis={1}]\n",  //$NON-NLS-1$
            new Object[] { aNode.getType(), new Integer(aNode.getAxis()) });
      getBuffer().write(msg);

      traverse(aNode);
   }

   /**
    * Gets the String value created by the visitor.
    */
   public String getString()
   {
      return getBuffer().toString();
   }

   /**
    * Gets the current indentation string based on the current depth.
    */
   protected void appendIndentString()
   {
      for (int i = 0; i < getNodeStack().size(); i++)
      {
         getBuffer().write("  "); //$NON-NLS-1$
      }
   }

   /**
    * @return Returns the buffer.
    */
   protected AeUnsynchronizedCharArrayWriter getBuffer()
   {
      return mBuffer;
   }

   /**
    * @param aBuffer The buffer to set.
    */
   protected void setBuffer(AeUnsynchronizedCharArrayWriter aBuffer)
   {
      mBuffer = aBuffer;
   }
}
