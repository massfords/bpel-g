// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/xpath/AeWSBPELXPathExpressionAnalyzer.java,v 1.6 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.expr.xpath;

import org.activebpel.rt.expr.def.AePrefixedExpressionAnalyzer;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;

/**
 * A concrete implementation of an expression analyzer for XPath 1.0 (for BPEL 2.0).  This 
 * class helps the Designer perform analysis and manipulation of expressions written in XPath 1.0
 * according to the BPEL 2.0 specification.
 */
public class AeWSBPELXPathExpressionAnalyzer extends AePrefixedExpressionAnalyzer
{
   /**
    * Overrides method to supply a BPEL 2.0 version of the expression parser.
    * 
    * @see org.activebpel.rt.bpel.def.expr.xpath.AeBPWSXPathExpressionAnalyzer#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeWSBPELXPathExpressionParser(aContext);
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
    * Renames variables within an expression.  This method renames variables with the syntactic form
    * of $varName within an XPath expression.
    * 
    * @param aContext
    * @param aExpression
    * @param aOldVarName
    * @param aNewVarName
    */
   public static String renameExpressionVariable(IAeExpressionAnalyzerContext aContext, String aExpression,
         String aOldVarName, String aNewVarName)
   {
      String expression = aExpression;

      // Note: $2 should refer to any character that could conceivably follow the variable reference
      // in an xpath.  Example syntax:  concat('Val:', $variableName)
      String pattern = "(\\$)" + aOldVarName + "([\\.[^" + AeXmlUtil.NCNAME_CHAR_PATTERN + "]])"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      String replacement = "$1" + aNewVarName + "$2"; //$NON-NLS-1$ //$NON-NLS-2$
      String newExpr = expression.replaceAll(pattern, replacement);
      
      // Note: need to check for $varName at the end of the expression because for some reason
      // the above regexp will not match on the end of the line (even using the $ construct)
      if (newExpr.endsWith("$" + aOldVarName)) //$NON-NLS-1$
      {
         newExpr = newExpr.substring(0, newExpr.length() - aOldVarName.length()) + aNewVarName;
      }
      
      return newExpr;
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#parseExpressionToSpec(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public AeExpressionToSpecDetails parseExpressionToSpec(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      return AeWSBPELXPathExpressionToSpecUtil.parseExpressionToSpec(aContext, aExpression);
   }
}
