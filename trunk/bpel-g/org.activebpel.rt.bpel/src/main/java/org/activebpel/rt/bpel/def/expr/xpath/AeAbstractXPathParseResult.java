// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/xpath/AeAbstractXPathParseResult.java,v 1.2 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.expr.xpath;

import java.util.List;
import java.util.Set;

import org.activebpel.rt.bpel.def.expr.AeAbstractExpressionParseResult;
import org.activebpel.rt.bpel.def.expr.xpath.ast.visitors.AeXPathFunctionNodeVisitor;
import org.activebpel.rt.bpel.def.expr.xpath.ast.visitors.AeXPathVariableNodeVisitor;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAST;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * A base implementation for XPath parse results.
 */
public abstract class AeAbstractXPathParseResult extends AeAbstractExpressionParseResult
{
   /** The AST representation of the parsed XPath. */
   private AeXPathAST mXPathAST;
   /** Cached list of functions. */
   private Set mFunctions;
   /** Cached list of variables. */
   private Set mVariableReferences;

   /**
    * Creates the xpath parse result.
    * 
    * @param aExpression
    * @param aXPathAST
    * @param aErrors
    * @param aParserContext
    */
   public AeAbstractXPathParseResult(String aExpression, AeXPathAST aXPathAST, List aErrors,
         IAeExpressionParserContext aParserContext)
   {
      super(aExpression, aParserContext);
      setXPathAST(aXPathAST);
      setErrors(aErrors);
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionParseResult#getFunctions()
    */
   public Set getFunctions()
   {
      if (mFunctions == null)
         setFunctions(findFunctions());
      return mFunctions;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionParseResult#getVariableReferences()
    */
   public Set getVariableReferences()
   {
      if (mVariableReferences == null)
         setVariableReferences(findVariableReferences());
      return mVariableReferences;
   }
   
   /**
    * Visits the xpath AST in order to find the functions.
    */
   protected Set findFunctions()
   {
      AeXPathFunctionNodeVisitor visitor = new AeXPathFunctionNodeVisitor();
      getXPathAST().visitAll(visitor);
      return visitor.getFunctions();
   }
   
   /**
    * Visits the xpath AST in order to find the variable references.
    */
   protected Set findVariableReferences()
   {
      AeXPathVariableNodeVisitor visitor = new AeXPathVariableNodeVisitor();
      getXPathAST().visitAll(visitor);
      return visitor.getVariableReferences();
   }

   /**
    * @return Returns the xPathAST.
    */
   public AeXPathAST getXPathAST()
   {
      return mXPathAST;
   }

   /**
    * @param aPathAST The xPathAST to set.
    */
   protected void setXPathAST(AeXPathAST aPathAST)
   {
      mXPathAST = aPathAST;
   }

   /**
    * @param aVariableReferences The variableReferences to set.
    */
   protected void setVariableReferences(Set aVariableReferences)
   {
      mVariableReferences = aVariableReferences;
   }

   /**
    * @param aFunctions The functions to set.
    */
   protected void setFunctions(Set aFunctions)
   {
      mFunctions = aFunctions;
   }
}
