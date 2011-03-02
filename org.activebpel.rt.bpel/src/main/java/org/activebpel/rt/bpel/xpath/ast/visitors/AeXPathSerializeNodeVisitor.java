// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/visitors/AeXPathSerializeNodeVisitor.java,v 1.2 2006/09/07 15:06:26 EWittmann Exp $
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
import java.util.List;

import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathAxisNode;
import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathBooleanNode;
import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathOperatorNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAbsLocPathNode;
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
import org.activebpel.rt.bpel.xpath.ast.AeXPathPathExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathPredicateNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathProcessingInstructionNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathRelationalExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathRelativeLocPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathTextNodeStepNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathUnaryExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathUnionExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode;
import org.activebpel.rt.bpel.xpath.ast.IAeXPathQualifiedNode;
import org.activebpel.rt.util.AeUnsynchronizedCharArrayWriter;
import org.activebpel.rt.util.AeUtil;
import org.jaxen.saxpath.Axis;

/**
 * This class is used to serialize an XPath AST into a string representation of the XPath.  It
 * is not guaranteed that the resulting serialized XPath will be identical to the one that
 * created the AST, but it should be functionally equivalent.
 */
public class AeXPathSerializeNodeVisitor extends AeAbstractTraversingXPathNodeVisitor
{
   private static String [] AXIS_MAPPING = {
      /* INVALID_AXIS       */  "??", //$NON-NLS-1$
      /* CHILD              */  "", //$NON-NLS-1$
      /* DESCENDANT         */  "descendant::", //$NON-NLS-1$
      /* PARENT             */  "parent::", //$NON-NLS-1$
      /* ANCESTOR           */  "ancestor::", //$NON-NLS-1$
      /* FOLLOWING_SIBLING  */  "following-sibling::", //$NON-NLS-1$
      /* PRECEDING_SIBLING  */  "preceding-sibling::", //$NON-NLS-1$
      /* FOLLOWING          */  "following::", //$NON-NLS-1$
      /* PRECEDING          */  "preceding::", //$NON-NLS-1$
      /* ATTRIBUTE          */  "@", //$NON-NLS-1$
      /* NAMESPACE          */  "namespace::", //$NON-NLS-1$
      /* SELF               */  "self::", //$NON-NLS-1$
      /* DESCENDANT_OR_SELF */  "descendant-or-self::", //$NON-NLS-1$
      /* ANCESTOR_OR_SELF   */  "ancestor-or-self" //$NON-NLS-1$
   };

   private static String [] OPERATOR_MAPPING = {
      /* NO_OP               */ "??", //$NON-NLS-1$
      /* EQUALS              */ " = ", //$NON-NLS-1$
      /* NOT_EQUALS          */ " != ", //$NON-NLS-1$
      /* LESS_THAN           */ " < ", //$NON-NLS-1$
      /* LESS_THAN_EQUALS    */ " <= ", //$NON-NLS-1$
      /* GREATER_THAN        */ " > ", //$NON-NLS-1$
      /* GREATER_THAN_EQUALS */ " >= ", //$NON-NLS-1$
      /* ADD                 */ " + ", //$NON-NLS-1$
      /* SUBTRACT            */ " - ", //$NON-NLS-1$
      /* MULTIPLY            */ " * ", //$NON-NLS-1$
      /* MOD                 */ " mod ", //$NON-NLS-1$
      /* DIV                 */ " div ", //$NON-NLS-1$
      /* NEGATIVE            */ "-" //$NON-NLS-1$
   };

   /** A string buffer to hold the serialized xpath. */
   private AeUnsynchronizedCharArrayWriter mBuffer;

   /**
    * Default c'tor.
    */
   public AeXPathSerializeNodeVisitor()
   {
      super();

      setBuffer(new AeUnsynchronizedCharArrayWriter());
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode)
    */
   public void visit(AeXPathFunctionNode aNode)
   {
      appendQualifiedName(aNode);
      getBuffer().write('(');

      visitNodeWithSeparator(aNode, ", "); //$NON-NLS-1$

      getBuffer().write(')');
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode)
    */
   public void visit(AeXPathVariableNode aNode)
   {
      // Variable references start with $
      getBuffer().write('$');
      appendQualifiedName(aNode);

      traverse(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode)
    */
   public void visit(AeXPathLiteralNode aNode)
   {
      Object value = aNode.getValue();
      if (value instanceof String)
      {
         char quoteChar = '\'';
         String strLiteral = String.valueOf(value);
         if (strLiteral.indexOf(quoteChar) != -1)
         {
            quoteChar = '\"';
         }
         getBuffer().write(quoteChar);
         getBuffer().write(strLiteral);
         getBuffer().write(quoteChar);
      }
      else
      {
         getBuffer().write(String.valueOf(value));
      }

      traverse(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathRelativeLocPathNode)
    */
   public void visit(AeXPathRelativeLocPathNode aNode)
   {
      visitPathExpression(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathPathExprNode)
    */
   public void visit(AeXPathPathExprNode aNode)
   {
      visitPathExpression(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAbsLocPathNode)
    */
   public void visit(AeXPathAbsLocPathNode aNode)
   {
      getBuffer().write('/');
      visitPathExpression(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathNameStepNode)
    */
   public void visit(AeXPathNameStepNode aNode)
   {
      appendAxis(aNode);
      appendQualifiedName(aNode);
      traverse(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathOrExprNode)
    */
   public void visit(AeXPathOrExprNode aNode)
   {
      visitBooleanNode(aNode, " or "); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAndExprNode)
    */
   public void visit(AeXPathAndExprNode aNode)
   {
      visitBooleanNode(aNode, " and "); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathUnionExprNode)
    */
   public void visit(AeXPathUnionExprNode aNode)
   {
      visitBooleanNode(aNode, " | "); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathPredicateNode)
    */
   public void visit(AeXPathPredicateNode aNode)
   {
      getBuffer().write('[');
      traverse(aNode);
      getBuffer().write(']');
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathEqualityExprNode)
    */
   public void visit(AeXPathEqualityExprNode aNode)
   {
      appendOperator(aNode);
      traverse(aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathRelationalExprNode)
    */
   public void visit(AeXPathRelationalExprNode aNode)
   {
      appendOperator(aNode);
      traverse(aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAdditiveExprNode)
    */
   public void visit(AeXPathAdditiveExprNode aNode)
   {
      appendOperator(aNode);
      traverse(aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathMultiplicativeExprNode)
    */
   public void visit(AeXPathMultiplicativeExprNode aNode)
   {
      appendOperator(aNode);
      traverse(aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathUnaryExprNode)
    */
   public void visit(AeXPathUnaryExprNode aNode)
   {
      appendOperator(aNode);
      traverse(aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathTextNodeStepNode)
    */
   public void visit(AeXPathTextNodeStepNode aNode)
   {
      appendAxis(aNode);
      getBuffer().write("text()"); //$NON-NLS-1$
      traverse(aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathAllNodeStepNode)
    */
   public void visit(AeXPathAllNodeStepNode aNode)
   {
      if (aNode.getAxis() != Axis.DESCENDANT_OR_SELF)
      {
         appendAxis(aNode);
         getBuffer().write("node()"); //$NON-NLS-1$
      }
      
      traverse(aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathProcessingInstructionNodeStepNode)
    */
   public void visit(AeXPathProcessingInstructionNodeStepNode aNode)
   {
      appendAxis(aNode);
      getBuffer().write("processing-instruction"); //$NON-NLS-1$
      getBuffer().write('(');
      if (AeUtil.notNullOrEmpty(aNode.getName()))
      {
         getBuffer().write('\'');
         getBuffer().write(aNode.getName());
         getBuffer().write('\'');
      }
      getBuffer().write(')');

      traverse(aNode);
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathCommentNodeStepNode)
    */
   public void visit(AeXPathCommentNodeStepNode aNode)
   {
      appendAxis(aNode);
      getBuffer().write("comment()"); //$NON-NLS-1$
      traverse(aNode);
   }

   /**
    * Common visit for nodes that are effectively just paths.  This means that all of the
    * children of the node are simply concat'd using '/' as the separator.
    *
    * @param aNode
    */
   protected void visitPathExpression(AeAbstractXPathNode aNode)
   {
      visitNodeWithSeparator(aNode, "/"); //$NON-NLS-1$
   }
   
   /**
    * Common visit for all boolean nodes.  This method is needed because the tree will look a
    * little weird for the following xpath:
    * 
    * <code>
    *    /path/to/data[@attr1 = 'value1' and @attr2 = 'value2']
    * </code>
    * 
    * In the above instance, the predicate node will have a single child node of type 'and'.  That
    * 'and' child will have three children:  a relative path, an equality expression, and another
    * and expression.
    * 
    * @param aNode
    * @param aSeparator
    */
   protected void visitBooleanNode(AeAbstractXPathBooleanNode aNode, String aSeparator)
   {
      getBuffer().write('(');
      if (aNode.isCreate())
      {
         if (aNode.getChildren().size() == 2)
         {
            visitNodeWithSeparator(aNode, aSeparator);
         }
         else
         {
            visitMultiArgBooleanNode(aNode, aSeparator);
         }
      }
      else
      {
         traverse(aNode);
      }
      getBuffer().write(')');
   }
   
   /**
    * Called to visit a boolean node that has more than 2 children.  See documenation for 
    * <code>visitBooleanNode</code> for details.
    * 
    * @param aNode
    */
   protected void visitMultiArgBooleanNode(AeAbstractXPathBooleanNode aNode, String aSeparator)
   {
      getBuffer().write('(');
      pushNode(aNode);
      List children = aNode.getChildren();
      for (int i = 0; i < children.size() - 1; i++)
      {
         AeAbstractXPathNode child = (AeAbstractXPathNode) children.get(i);
         child.accept(this);
      }
      getBuffer().write(')');
      getBuffer().write(aSeparator);
      AeAbstractXPathNode child = (AeAbstractXPathNode) children.get(children.size() - 1);
      child.accept(this);

      popNode();
   }

   /**
    * Appends/visits all of the node's children using the given separator to separate them.
    *
    * @param aNode
    */
   protected void visitNodeWithSeparator(AeAbstractXPathNode aNode, String aSeparator)
   {
      // Visit all of the node's children (custom traverse) and append the given separator
      // between each node.
      pushNode(aNode);
      for (Iterator iter = aNode.getChildren().iterator(); iter.hasNext(); )
      {
         AeAbstractXPathNode child = (AeAbstractXPathNode) iter.next();
         child.accept(this);
         if (iter.hasNext())
         {
            getBuffer().write(aSeparator);
         }
      }
      popNode();
   }

   /**
    * Appends the qualified name of the node.
    *
    * @param aNode
    */
   protected void appendQualifiedName(IAeXPathQualifiedNode aNode)
   {
      if (AeUtil.notNullOrEmpty(aNode.getPrefix()))
      {
         getBuffer().write(aNode.getPrefix());
         getBuffer().write(':');
      }
      getBuffer().write(aNode.getLocalName());
   }

   /**
    * Appends the axis qualifier of the given axis-qualified node.
    *
    * @param aNode
    */
   protected void appendAxis(AeAbstractXPathAxisNode aNode)
   {
      getBuffer().write(AXIS_MAPPING[aNode.getAxis()]);
   }

   /**
    * Appends the appropriate operator.
    *
    * @param aNode
    */
   protected void appendOperator(AeAbstractXPathOperatorNode aNode)
   {
      getBuffer().write(OPERATOR_MAPPING[aNode.getOperator()]);
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

   /**
    * Returns the serialized string that was created by the visitor.
    */
   public String getSerializedString()
   {
      return getBuffer().toString();
   }
}
