// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/xpath/AeWSBPELXPathExpressionToSpecUtil.java,v 1.3 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.expr.xpath;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAST;
import org.activebpel.rt.bpel.xpath.ast.AeXPathPathExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathRelativeLocPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer.AeExpressionToSpecDetails;
import org.activebpel.rt.util.AeUtil;

/**
 * A helper class for parsing the expression form of the to-spec for xpath expressions.  This
 * is used by the WSBPEL XPath expression analyzer and the XQuery 1.0 expression analyzer in
 * order to parse an expression and return the expression's component parts (variable name,
 * part name, query).
 */
public class AeWSBPELXPathExpressionToSpecUtil
{
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#parseExpressionToSpec(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public static AeExpressionToSpecDetails parseExpressionToSpec(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      AeExpressionToSpecDetails toSpecDetails = null;
      AeXPathAST xpathAST = createXPathAST(aContext, aExpression);
      // The AST will be null if there is a parse error.  Any parse errors will be 
      // reported later during validation, so just return null.
      if (xpathAST != null)
      {
         if (xpathAST.getRootNode() instanceof AeXPathVariableNode)
         {
            toSpecDetails = getExpressionToSpecDetailsFromVariableNode((AeXPathVariableNode) xpathAST.getRootNode());
         }
         else if (xpathAST.getRootNode() instanceof AeXPathPathExprNode)
         {
            toSpecDetails = getExpressionToSpecDetailsFromPathNode((AeXPathPathExprNode) xpathAST.getRootNode());
         }
      }
      return toSpecDetails;
   }

   /**
    * Creates the XPath AST (returns null if the AST could not be created).
    * 
    * @param aContext
    * @param aExpression
    */
   protected static AeXPathAST createXPathAST(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      try
      {
         return AeXPathAST.createXPathAST(aExpression, aContext.getNamespaceContext());
      }
      catch (AeException ex)
      {
         return null;
      }
   }

   /**
    * Given an XPath variable node, returns the expression to-spec details.  In the case of a 
    * variable node, the only components that will exist are the variable name and optionally
    * the part name.
    * 
    * This method will be called when the expression is of one of the following forms:
    * 
    * $elementVar
    * $simpleVar
    * $messageVar.partName
    * 
    * @param aVariableNode
    */
   protected static AeExpressionToSpecDetails getExpressionToSpecDetailsFromVariableNode(AeXPathVariableNode aVariableNode)
   {
      AeExpressionToSpecDetails details = null;

      QName varQName = aVariableNode.getVariableQName();
      // Not a valid format if the variable reference is qualified
      if (AeUtil.isNullOrEmpty(varQName.getNamespaceURI()))
      {
         String varName = varQName.getLocalPart();
         AeXPathVariableReference ref = new AeXPathVariableReference(varName);
         details = new AeExpressionToSpecDetails(ref.getVariableName(), ref.getPartName(),
               IAeBPELConstants.WSBPEL_EXPR_LANGUAGE_URI, null);
      }
      
      return details;
   }

   /**
    * Given an XPath PathExpr node, returns the expression to-spec details.  The path node 
    * should have two children: a variable reference and a relative path.  If this is not the 
    * case, then the expression is invalid and we will return null.
    * 
    * This method will be called when the expression is of one of the following forms:
    * 
    * $elementVar/path/to/data
    * $typeVar/path/to/data
    * $messageVar.partName/path/to/data
    * 
    * @param aPathNode
    */
   protected static AeExpressionToSpecDetails getExpressionToSpecDetailsFromPathNode(AeXPathPathExprNode aPathNode)
   {
      AeExpressionToSpecDetails details = null;

      if (aPathNode.getChildren().size() == 2 && aPathNode.getChildren().get(0) instanceof AeXPathVariableNode && aPathNode.getChildren().get(1) instanceof AeXPathRelativeLocPathNode)
      {
         AeXPathVariableNode variableNode = (AeXPathVariableNode) aPathNode.getChildren().get(0);
         AeXPathRelativeLocPathNode relativePathNode = (AeXPathRelativeLocPathNode) aPathNode.getChildren().get(1);

         QName varQName = variableNode.getVariableQName();
         // Not a valid format if the variable reference is qualified
         if (AeUtil.isNullOrEmpty(varQName.getNamespaceURI()))
         {
            String varName = varQName.getLocalPart();
            AeXPathVariableReference ref = new AeXPathVariableReference(varName);
            details = new AeExpressionToSpecDetails(ref.getVariableName(), ref.getPartName(), 
                  IAeBPELConstants.WSBPEL_EXPR_LANGUAGE_URI, relativePathNode.serialize());
         }
      }

      return details;
   }
}
