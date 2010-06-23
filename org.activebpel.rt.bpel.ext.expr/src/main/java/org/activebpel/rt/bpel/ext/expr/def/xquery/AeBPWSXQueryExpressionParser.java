//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeBPWSXQueryExpressionParser.java,v 1.2 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.def.xquery;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.XQueryExpression;

import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * An implementation of an expression parser for the xquery language.
 */
public class AeBPWSXQueryExpressionParser extends AeAbstractXQueryExpressionParser
{
   /**
    * Constructs an xquery parser given the context.
    * 
    * @param aParserContext
    */
   public AeBPWSXQueryExpressionParser(IAeExpressionParserContext aParserContext)
   {
      super(aParserContext);
   }

   /**
    * @see org.activebpel.rt.bpel.ext.expr.def.xquery.AeAbstractXQueryExpressionParser#createParseResult(java.lang.String, net.sf.saxon.Configuration, net.sf.saxon.query.XQueryExpression)
    */
   protected IAeExpressionParseResult createParseResult(String aExpression, Configuration aConfig, XQueryExpression aXQueryExpression)
   {
      return new AeBPWSXQueryParseResult(aExpression, aXQueryExpression.getExpression(), aConfig, getParserContext());
   }
}
