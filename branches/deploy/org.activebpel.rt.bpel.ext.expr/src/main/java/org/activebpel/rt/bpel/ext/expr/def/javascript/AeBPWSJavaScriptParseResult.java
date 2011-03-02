//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/javascript/AeBPWSJavaScriptParseResult.java,v 1.2 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.def.javascript;

import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.mozilla.javascript.ScriptOrFnNode;

/**
 * A concrete BPEL 1.1 implementation of a parse result for the javascript language.
 */
public class AeBPWSJavaScriptParseResult extends AeAbstractJavaScriptParseResult
{
   /**
    * Constructor.
    * 
    * @param aExpression
    * @param aRootNode
    * @param aParserContext
    */
   public AeBPWSJavaScriptParseResult(String aExpression, ScriptOrFnNode aRootNode,
         IAeExpressionParserContext aParserContext)
   {
      super(aExpression, aRootNode, aParserContext);
   }
}
