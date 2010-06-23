// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeBPWSXQueryParseResult.java,v 1.2 2008/01/25 21:28:25 dvilaverde Exp $
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
import net.sf.saxon.expr.Expression;

import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * A concrete implementation of a xquery parse result for BPEL 1.1.
 */
public class AeBPWSXQueryParseResult extends AeAbstractXQueryParseResult
{
   /**
    * Constructs the BPWS xquery parse result.
    */
   public AeBPWSXQueryParseResult(String aExpression, Expression aXQueryExpression, Configuration aConfiguration,
         IAeExpressionParserContext aParserContext)
   {
      super(aExpression, aXQueryExpression, aConfiguration, aParserContext);
   }
}
