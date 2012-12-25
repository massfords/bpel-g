// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/javascript/AeWSBPELJavaScriptExpressionRunner.java,v 1.2 2006/09/18 20:08:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.impl.javascript;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.mozilla.javascript.Scriptable;

/**
 * A BPEL 2.0 implementation of a JavaScript expression runner.
 */
public class AeWSBPELJavaScriptExpressionRunner extends AeAbstractJavaScriptExpressionRunner
{
   /**
    * Default c'tor.
    */
   public AeWSBPELJavaScriptExpressionRunner()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.ext.expr.impl.javascript.AeAbstractJavaScriptExpressionRunner#createFunctionContainer(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, org.mozilla.javascript.Scriptable, java.lang.String)
    */
   protected AeJavaScriptFunctionContainer createFunctionContainer(
         IAeFunctionExecutionContext aFuncExecContext, Scriptable aScope, String aFunctionNamespace)
   {
      if (!IAeBPELConstants.BPWS_NAMESPACE_URI.equals(aFunctionNamespace))
      {
         return new AeWSBPELJavaScriptFunctionContainer(aFunctionNamespace, aScope, aFuncExecContext);
      }
      else
      {
         return super.createFunctionContainer(aFuncExecContext, aScope, aFunctionNamespace);
      }
   }
}
