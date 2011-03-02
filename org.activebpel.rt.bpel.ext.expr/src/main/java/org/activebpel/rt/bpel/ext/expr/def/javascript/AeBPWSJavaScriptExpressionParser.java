//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/javascript/AeBPWSJavaScriptExpressionParser.java,v 1.2 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.def.javascript;

import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.mozilla.javascript.ScriptOrFnNode;

/**
 * A BPEL 1.1 JavaScript implementation of an expression parse.
 */
public class AeBPWSJavaScriptExpressionParser extends AeAbstractJavaScriptExpressionParser
{
   /**
    * Constructs a javascript parser given the context.
    * 
    * @param aParserContext
    */
   public AeBPWSJavaScriptExpressionParser(IAeExpressionParserContext aParserContext)
   {
      super(aParserContext);
   }

   /**
    * @see org.activebpel.rt.bpel.ext.expr.def.javascript.AeAbstractJavaScriptExpressionParser#createParseResult(java.lang.String, org.mozilla.javascript.ScriptOrFnNode)
    */
   protected IAeExpressionParseResult createParseResult(String aExpression, ScriptOrFnNode aTree)
   {
      return new AeBPWSJavaScriptParseResult(aExpression, aTree, getParserContext());
   }
}
