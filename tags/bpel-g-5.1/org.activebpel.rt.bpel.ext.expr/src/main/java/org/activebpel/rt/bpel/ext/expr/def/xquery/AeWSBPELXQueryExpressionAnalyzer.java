// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeWSBPELXQueryExpressionAnalyzer.java,v 1.5 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.xquery;

import org.activebpel.rt.bpel.def.expr.xpath.AeWSBPELXPathExpressionAnalyzer;
import org.activebpel.rt.bpel.def.expr.xpath.AeWSBPELXPathExpressionToSpecUtil;
import org.activebpel.rt.expr.def.AePrefixedExpressionAnalyzer;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.activebpel.rt.util.AeUtil;

/**
 * A WS-BPEL implementation of a XQuery expression analyser.
 */
public class AeWSBPELXQueryExpressionAnalyzer extends AePrefixedExpressionAnalyzer
{
   /**
    * Default c'tor.
    */
   public AeWSBPELXQueryExpressionAnalyzer()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.ext.expr.def.xquery.AeBPWSXQueryExpressionAnalyzer#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeWSBPELXQueryExpressionParser(aContext);
   }

   /**
    * @see org.activebpel.rt.expr.def.AeAbstractExpressionAnalyzer#renameVariable(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String, java.lang.String, java.lang.String)
    */
   public String renameVariable(IAeExpressionAnalyzerContext aContext, String aExpression, String aOldVarName, String aNewVarName)
   {
      if (AeUtil.isNullOrEmpty(aExpression))
         return null;
      
      String expression = super.renameVariable(aContext, aExpression, aOldVarName, aNewVarName);
      if (expression == null)
         expression = aExpression;
      
      // Use the same logic as the XPath impl for renaming $varName variables.
      String newExpr = AeWSBPELXPathExpressionAnalyzer.renameExpressionVariable(aContext, expression, aOldVarName, aNewVarName);
      
      if (aExpression.equals(newExpr))
      {
         return null;
      }
      else
      {
         return newExpr;
      }
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#parseExpressionToSpec(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public AeExpressionToSpecDetails parseExpressionToSpec(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      return AeWSBPELXPathExpressionToSpecUtil.parseExpressionToSpec(aContext, aExpression);
   }
}
