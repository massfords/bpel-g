//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/javascript/AeBPWSJavaScriptExpressionAnalyzer.java,v 1.3 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.def.javascript;

import org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * A concrete implementation of an expression analyzer for JavaScript (BPEL 1.1).  This class helps the 
 * Designer perform analysis and manipulation of expressions written in JavaScript.
 */
public class AeBPWSJavaScriptExpressionAnalyzer extends AeAbstractJavaScriptExpressionAnalyzer
{
   /**
    * Default c'tor.
    */
   public AeBPWSJavaScriptExpressionAnalyzer()
   {
      super();
   }

   /**
    * Overrides method to supply a javascript impl for the expression parser.
    * 
    * @see org.activebpel.rt.expr.def.AeAbstractExpressionAnalyzer#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeBPWSJavaScriptExpressionParser(aContext);
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#parseExpressionToSpec(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public AeExpressionToSpecDetails parseExpressionToSpec(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      throw new UnsupportedOperationException();
   }
}
