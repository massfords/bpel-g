//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/xpath/AeBPWSXPathExpressionParser.java,v 1.2 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.expr.xpath;

import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * An implementation of an expression parser for the xpath language.
 */
public class AeBPWSXPathExpressionParser extends AeAbstractXPathExpressionParser
{
   /**
    * Constructs an xpath parser given the context.
    * 
    * @param aParserContext
    */
   public AeBPWSXPathExpressionParser(IAeExpressionParserContext aParserContext)
   {
      super(aParserContext);
   }

   /**
    * @see org.activebpel.rt.bpel.def.expr.xpath.AeAbstractXPathExpressionParser#createParseResult(java.lang.String, org.activebpel.rt.bpel.def.expr.xpath.AeXPathParseHandler)
    */
   protected IAeExpressionParseResult createParseResult(String aExpression, AeXPathParseHandler aHandler)
   {
      return new AeBPWSXPathParseResult(aExpression, aHandler.getAST(), aHandler.getErrors(), getParserContext());
   }
}
