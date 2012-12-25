// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/javascript/AeWSBPELJavaScriptParseResult.java,v 1.4 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.javascript;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.expr.AeExpressionLanguageUtil;
import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.mozilla.javascript.ScriptOrFnNode;

/**
 * A concrete BPEL 2.0 implementation of a parse result for the javascript language.
 */
public class AeWSBPELJavaScriptParseResult extends AeAbstractJavaScriptParseResult
{
   /** The getVariableData QName (for BPEL 2.0 JavaScript expressions). */
   public static final QName GET_VARIABLE_DATA_FUNC_NAME = new QName(
         IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, AeExpressionLanguageUtil.VAR_DATA_FUNC_NAME);
   /** The getLinkStatus QName (for BPEL 2.0 JavaScript expressions). */
   private static final QName GET_LINK_STATUS_FUNC_NAME = new QName(
         IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, AeExpressionLanguageUtil.LINK_STATUS_FUNC_NAME);

   /**
    * Constructor.
    * 
    * @param aExpression
    * @param aRootNode
    * @param aParserContext
    */
   public AeWSBPELJavaScriptParseResult(String aExpression, ScriptOrFnNode aRootNode,
         IAeExpressionParserContext aParserContext)
   {
      super(aExpression, aRootNode, aParserContext);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.expr.AeAbstractExpressionParseResult#isGetLinkStatusFunction(org.activebpel.rt.expr.def.AeScriptFuncDef)
    */
   protected boolean isGetLinkStatusFunction(AeScriptFuncDef aFunction)
   {
      return GET_LINK_STATUS_FUNC_NAME.equals(aFunction.getQName()) || super.isGetLinkStatusFunction(aFunction);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.expr.AeAbstractExpressionParseResult#isGetVariableDataFunction(org.activebpel.rt.expr.def.AeScriptFuncDef)
    */
   protected boolean isGetVariableDataFunction(AeScriptFuncDef aFunction)
   {
      return GET_VARIABLE_DATA_FUNC_NAME.equals(aFunction.getQName()) || super.isGetVariableDataFunction(aFunction);
   }
}
