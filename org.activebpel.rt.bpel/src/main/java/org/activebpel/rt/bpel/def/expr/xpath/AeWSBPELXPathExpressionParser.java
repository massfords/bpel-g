// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/xpath/AeWSBPELXPathExpressionParser.java,v 1.3 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.expr.xpath;

import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * An implementation of an expression parser for the xpath language for BPEL 2.0.
 */
public class AeWSBPELXPathExpressionParser extends AeAbstractXPathExpressionParser
{
   /**
    * Constructs an xpath parser given the context.
    * 
    * @param aParserContext
    */
   public AeWSBPELXPathExpressionParser(IAeExpressionParserContext aParserContext)
   {
      super(aParserContext);
   }

   /**
    * @see org.activebpel.rt.bpel.def.expr.xpath.AeAbstractXPathExpressionParser#createParseResult(java.lang.String, org.activebpel.rt.bpel.def.expr.xpath.AeXPathParseHandler)
    */
   protected IAeExpressionParseResult createParseResult(String aExpression, AeXPathParseHandler aHandler)
   {
      return new AeWSBPELXPathParseResult(aExpression, aHandler.getAST(), aHandler.getErrors(), getParserContext());
   }
}
