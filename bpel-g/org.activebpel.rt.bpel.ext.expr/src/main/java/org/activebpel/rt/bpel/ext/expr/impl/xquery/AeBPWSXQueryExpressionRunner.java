//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeBPWSXQueryExpressionRunner.java,v 1.3 2008/02/17 21:38:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import net.sf.saxon.variables.VariableResolver;

import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver;

/**
 * Implements an XQuery 1.0 expression runner. This implementation is capable of executing expression that
 * conform to the XQuery 1.0 specification for BPEL 1.1 processes.
 */
public class AeBPWSXQueryExpressionRunner extends AeAbstractXQueryExpressionRunner
{
   /**
    * Default constructor.
    */
   public AeBPWSXQueryExpressionRunner()
   {
   }

   /**
    * There is no $varName support in BPEL 1.1.
    * 
    * @see org.activebpel.rt.bpel.ext.expr.impl.xquery.AeAbstractXQueryExpressionRunner#createVariableResolver(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver)
    */
   protected VariableResolver createVariableResolver(IAeFunctionExecutionContext aFunctionExecContext, IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      return null;
   }

   /**
    * There is no $linkName support in BPEL 1.1.
    * @see org.activebpel.rt.bpel.ext.expr.impl.xquery.AeAbstractXQueryExpressionRunner#createLinkVariableResolver(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver)
    */
   protected VariableResolver createLinkVariableResolver(IAeFunctionExecutionContext aFunctionExecContext, IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      return null;
   }
}
