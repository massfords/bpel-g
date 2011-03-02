//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/xpath/AeXPathParseHandler.java,v 1.7 2006/09/27 19:58:41 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.expr.xpath;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAST;
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
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathHandler;

/**
 * This class listens to a Jaxen parse of an XPath and constructs a list of functions that are used in the
 * expression.
 */
public class AeXPathParseHandler implements XPathHandler
{
   /** The namespace resolver to use while parsing. */
   private IAeNamespaceContext mNamespaceContext;
   /** The node stack. */
   private Stack mNodeStack = new Stack();
   /** The full AST - set when endXPath() is called. */
   private AeXPathAST mAbstractSyntaxTree;
   /** A list of errors found while handling the jaxen parse. */
   private List mErrors = new ArrayList();

   /**
    * Constructor.
    *
    * @param aNamespaceContext
    */
   public AeXPathParseHandler(IAeNamespaceContext aNamespaceContext)
   {
      setNamespaceContext(aNamespaceContext);
   }

   /**
    * Returns the full parse tree.  This method is only valid once the parse is complete.
    */
   public AeXPathAST getAST()
   {
      return mAbstractSyntaxTree;
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startAbsoluteLocationPath()
    */
   public void startAbsoluteLocationPath() throws SAXPathException
   {
      pushNode(new AeXPathAbsLocPathNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startAdditiveExpr()
    */
   public void startAdditiveExpr() throws SAXPathException
   {
      pushNode(new AeXPathAdditiveExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startAllNodeStep(int)
    */
   public void startAllNodeStep(int aAxis) throws SAXPathException
   {
      pushNode(new AeXPathAllNodeStepNode(aAxis));
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startAndExpr()
    */
   public void startAndExpr() throws SAXPathException
   {
      pushNode(new AeXPathAndExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startCommentNodeStep(int)
    */
   public void startCommentNodeStep(int aAxis) throws SAXPathException
   {
      pushNode(new AeXPathCommentNodeStepNode(aAxis));
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startEqualityExpr()
    */
   public void startEqualityExpr() throws SAXPathException
   {
      pushNode(new AeXPathEqualityExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startFilterExpr()
    */
   public void startFilterExpr() throws SAXPathException
   {
      pushNode(new AeXPathFilterExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startFunction(java.lang.String, java.lang.String)
    */
   public void startFunction(String aPrefix, String aFunctionName) throws SAXPathException
   {
      String ns = getNamespaceContext().resolvePrefixToNamespace(aPrefix);
      if (ns == null && AeUtil.notNullOrEmpty(aPrefix))
      {
         String msg = MessageFormat.format(AeMessages.getString("AeXPathParseHandler.PREFIX_NOT_DECLARED_FUNC_ERROR"), //$NON-NLS-1$
               new Object [] { aPrefix, aPrefix, ":", aFunctionName }); //$NON-NLS-1$
         getErrors().add(msg);
      }
      AeXPathFunctionNode node = new AeXPathFunctionNode(aPrefix, ns, aFunctionName);
      pushNode(node);
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startMultiplicativeExpr()
    */
   public void startMultiplicativeExpr() throws SAXPathException
   {
      pushNode(new AeXPathMultiplicativeExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startNameStep(int, java.lang.String, java.lang.String)
    */
   public void startNameStep(int aAxis, String aPrefix, String aLocalName) throws SAXPathException
   {
      String ns = getNamespaceContext().resolvePrefixToNamespace(aPrefix);
      if (ns == null && AeUtil.notNullOrEmpty(aPrefix))
      {
         String msg = MessageFormat.format(AeMessages.getString("AeXPathParseHandler.PREFIX_NOT_DECLARED_ERROR"), //$NON-NLS-1$
               new Object [] { aPrefix, aPrefix, ":", aLocalName  }); //$NON-NLS-1$
         getErrors().add(msg);
      }
      pushNode(new AeXPathNameStepNode(aAxis, aPrefix, ns, aLocalName));
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startOrExpr()
    */
   public void startOrExpr() throws SAXPathException
   {
      pushNode(new AeXPathOrExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startPathExpr()
    */
   public void startPathExpr() throws SAXPathException
   {
      pushNode(new AeXPathPathExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startPredicate()
    */
   public void startPredicate() throws SAXPathException
   {
      pushNode(new AeXPathPredicateNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startProcessingInstructionNodeStep(int, java.lang.String)
    */
   public void startProcessingInstructionNodeStep(int aAxis, String aName) throws SAXPathException
   {
      pushNode(new AeXPathProcessingInstructionNodeStepNode(aAxis, aName));
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startRelationalExpr()
    */
   public void startRelationalExpr() throws SAXPathException
   {
      pushNode(new AeXPathRelationalExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startRelativeLocationPath()
    */
   public void startRelativeLocationPath() throws SAXPathException
   {
      pushNode(new AeXPathRelativeLocPathNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startTextNodeStep(int)
    */
   public void startTextNodeStep(int aAxis) throws SAXPathException
   {
      pushNode(new AeXPathTextNodeStepNode(aAxis));
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startUnaryExpr()
    */
   public void startUnaryExpr() throws SAXPathException
   {
      pushNode(new AeXPathUnaryExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startUnionExpr()
    */
   public void startUnionExpr() throws SAXPathException
   {
      pushNode(new AeXPathUnionExprNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#startXPath()
    */
   public void startXPath() throws SAXPathException
   {
      pushNode(new AeXPathRootXpathNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endAbsoluteLocationPath()
    */
   public void endAbsoluteLocationPath() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endAdditiveExpr(int)
    */
   public void endAdditiveExpr(int additiveOperator) throws SAXPathException
   {
      AeXPathAdditiveExprNode addNode = (AeXPathAdditiveExprNode) popNode();
      addNode.setOperator(additiveOperator);
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endAllNodeStep()
    */
   public void endAllNodeStep() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endAndExpr(boolean)
    */
   public void endAndExpr(boolean aCreate) throws SAXPathException
   {
      AeXPathAndExprNode andNode = (AeXPathAndExprNode) popNode();
      andNode.setCreate(aCreate);
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endCommentNodeStep()
    */
   public void endCommentNodeStep() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endEqualityExpr(int)
    */
   public void endEqualityExpr(int aEqualityOperator) throws SAXPathException
   {
      AeXPathEqualityExprNode equalNode = (AeXPathEqualityExprNode) popNode();
      equalNode.setOperator(aEqualityOperator);
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endFilterExpr()
    */
   public void endFilterExpr() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endFunction()
    */
   public void endFunction() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endMultiplicativeExpr(int)
    */
   public void endMultiplicativeExpr(int aMultiplicativeOperator) throws SAXPathException
   {
      AeXPathMultiplicativeExprNode multNode = (AeXPathMultiplicativeExprNode) popNode();
      multNode.setOperator(aMultiplicativeOperator);
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endNameStep()
    */
   public void endNameStep() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endOrExpr(boolean)
    */
   public void endOrExpr(boolean aCreate) throws SAXPathException
   {
      AeXPathOrExprNode orNode = (AeXPathOrExprNode) popNode();
      orNode.setCreate(aCreate);
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endPathExpr()
    */
   public void endPathExpr() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endPredicate()
    */
   public void endPredicate() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endProcessingInstructionNodeStep()
    */
   public void endProcessingInstructionNodeStep() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endRelationalExpr(int)
    */
   public void endRelationalExpr(int aRelationalOperator) throws SAXPathException
   {
      AeXPathRelationalExprNode relNode = (AeXPathRelationalExprNode) popNode();
      relNode.setOperator(aRelationalOperator);
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endRelativeLocationPath()
    */
   public void endRelativeLocationPath() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endTextNodeStep()
    */
   public void endTextNodeStep() throws SAXPathException
   {
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endUnaryExpr(int)
    */
   public void endUnaryExpr(int aUnaryOperator) throws SAXPathException
   {
      AeXPathUnaryExprNode unaryNode = (AeXPathUnaryExprNode) popNode();
      unaryNode.setOperator(aUnaryOperator);
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endUnionExpr(boolean)
    */
   public void endUnionExpr(boolean aCreate) throws SAXPathException
   {
      AeXPathUnionExprNode unionNode = (AeXPathUnionExprNode) popNode();
      unionNode.setCreate(aCreate);
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#endXPath()
    */
   public void endXPath() throws SAXPathException
   {
      mAbstractSyntaxTree = new AeXPathAST(popNode());
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#literal(java.lang.String)
    */
   public void literal(String aLiteral) throws SAXPathException
   {
      AeXPathLiteralNode node = new AeXPathLiteralNode(aLiteral);
      pushNode(node);
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#number(double)
    */
   public void number(double aNumber) throws SAXPathException
   {
      AeXPathLiteralNode node = new AeXPathLiteralNode(new Double(aNumber));
      pushNode(node);
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#number(int)
    */
   public void number(int aNumber) throws SAXPathException
   {
      AeXPathLiteralNode node = new AeXPathLiteralNode(new Integer(aNumber));
      pushNode(node);
      popNode();
   }

   /**
    * @see org.jaxen.saxpath.XPathHandler#variableReference(java.lang.String, java.lang.String)
    */
   public void variableReference(String aPrefix, String aVariableName) throws SAXPathException
   {
      String ns = getNamespaceContext().resolvePrefixToNamespace(aPrefix);
      if (ns == null && AeUtil.notNullOrEmpty(aPrefix))
      {
         String msg = MessageFormat.format(AeMessages.getString("AeXPathParseHandler.PREFIX_NOT_DECLARED_VAR_ERROR"), //$NON-NLS-1$
               new Object [] { aPrefix, aPrefix, ":", aVariableName  }); //$NON-NLS-1$
         getErrors().add(msg);
      }

      AeXPathVariableNode node = new AeXPathVariableNode(aPrefix, ns, aVariableName);
      pushNode(node);
      popNode();
   }

   /**
    * Pushes a node onto the stack.  This also adds the node as a child of the node currently at the
    * top of the stack.
    *
    * @param aNode
    */
   protected void pushNode(AeAbstractXPathNode aNode)
   {
      if (mNodeStack.size() > 0)
      {
         AeAbstractXPathNode parent = (AeAbstractXPathNode) mNodeStack.peek();
         parent.addChild(aNode);
         aNode.setParent(parent);
      }
      mNodeStack.push(aNode);
   }

   /**
    * Pops a node from the node stack.
    */
   protected AeAbstractXPathNode popNode()
   {
      return (AeAbstractXPathNode) mNodeStack.pop();
   }

   /**
    * @return Returns the namespaceContext.
    */
   protected IAeNamespaceContext getNamespaceContext()
   {
      return mNamespaceContext;
   }

   /**
    * @param aNamespaceContext The namespaceContext to set.
    */
   protected void setNamespaceContext(IAeNamespaceContext aNamespaceContext)
   {
      mNamespaceContext = aNamespaceContext;
   }

   /**
    * @return Returns the errors.
    */
   public List getErrors()
   {
      return mErrors;
   }

   /**
    * @param aErrors The errors to set.
    */
   protected void setErrors(List aErrors)
   {
      mErrors = aErrors;
   }
}
