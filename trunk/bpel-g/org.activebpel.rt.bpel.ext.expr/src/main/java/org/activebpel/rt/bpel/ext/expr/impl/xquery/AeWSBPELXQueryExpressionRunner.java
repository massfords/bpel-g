// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeWSBPELXQueryExpressionRunner.java,v 1.4 2008/02/17 21:38:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import net.sf.saxon.variables.VariableResolver;

import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver;

/**
 * A WS-BPEL implementation of an XQuery expression runner.
 */
public class AeWSBPELXQueryExpressionRunner extends AeAbstractXQueryExpressionRunner
{
   /**
    * Default c'tor.
    */
   public AeWSBPELXQueryExpressionRunner()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.ext.expr.impl.xquery.AeAbstractXQueryExpressionRunner#createLinkVariableResolver(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver)
    */
   protected VariableResolver createLinkVariableResolver(IAeFunctionExecutionContext aFunctionExecContext, IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      return new AeWSBPELXQueryLinkVariableResolver(aFunctionExecContext, aVariableResolver);
   }

   /**
    * @see org.activebpel.rt.bpel.ext.expr.impl.xquery.AeAbstractXQueryExpressionRunner#createVariableResolver(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver)
    */
   protected VariableResolver createVariableResolver(IAeFunctionExecutionContext aFunctionExecContext, IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      return new AeWSBPELXQueryVariableResolver(aFunctionExecContext, aVariableResolver);
   }
}
