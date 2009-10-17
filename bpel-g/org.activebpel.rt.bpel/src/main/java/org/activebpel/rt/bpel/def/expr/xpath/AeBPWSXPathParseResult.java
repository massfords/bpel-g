//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/xpath/AeBPWSXPathParseResult.java,v 1.2 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.expr.xpath;

import java.util.List;

import org.activebpel.rt.bpel.xpath.ast.AeXPathAST;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * A concrete implementation of a parse result for the xpath language for BPEL 1.1 processes.
 */
public class AeBPWSXPathParseResult extends AeAbstractXPathParseResult
{
   /**
    * Creates the xpath parse result.
    * 
    * @param aExpression
    * @param aXPathAST
    * @param aErrors
    * @param aParserContext
    */
   public AeBPWSXPathParseResult(String aExpression, AeXPathAST aXPathAST, List aErrors,
         IAeExpressionParserContext aParserContext)
   {
      super(aExpression, aXPathAST, aErrors, aParserContext);
   }
}
