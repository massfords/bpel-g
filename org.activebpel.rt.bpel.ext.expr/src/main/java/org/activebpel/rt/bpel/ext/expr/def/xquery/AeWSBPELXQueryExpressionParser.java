// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeWSBPELXQueryExpressionParser.java,v 1.3 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.xquery;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.XQueryExpression;

import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * A BPEL 2.0 version of an XQuery expression parser.
 */
public class AeWSBPELXQueryExpressionParser extends AeAbstractXQueryExpressionParser
{
   /**
    * Constructs a WS-BPEL xquery expression parser.
    * 
    * @param aParserContext
    */
   public AeWSBPELXQueryExpressionParser(IAeExpressionParserContext aParserContext)
   {
      super(aParserContext);
   }
   
   /**
    * @see org.activebpel.rt.bpel.ext.expr.def.xquery.AeAbstractXQueryExpressionParser#createParseResult(java.lang.String, net.sf.saxon.Configuration, net.sf.saxon.query.XQueryExpression)
    */
   protected IAeExpressionParseResult createParseResult(String aExpression, Configuration aConfig, XQueryExpression aXQueryExpression)
   {
      return new AeWSBPELXQueryParseResult(aExpression, aXQueryExpression.getExpression(), aConfig, getParserContext());
   }
}
