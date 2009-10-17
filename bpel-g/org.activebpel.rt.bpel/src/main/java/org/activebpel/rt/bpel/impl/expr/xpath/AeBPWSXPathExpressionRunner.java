//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/xpath/AeBPWSXPathExpressionRunner.java,v 1.3 2008/02/17 21:37:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr.xpath;

import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver;
import org.jaxen.VariableContext;


/**
 * Implements an XPath 1.0 expression runner. This implementation is capable of executing 
 * expression that conform to the XPath 1.0 specification.  This implementation is specific to
 * BPEL 1.1 (BPEL4WS).
 */
public class AeBPWSXPathExpressionRunner extends AeAbstractXPathExpressionRunner
{
   /**
    * Default constructor.
    */
   public AeBPWSXPathExpressionRunner()
   {
   }

   /**
    * BPEL 1.1 processes don't support $varName syntax.
    * 
    * @see org.activebpel.rt.bpel.impl.expr.xpath.AeAbstractXPathExpressionRunner#createVariableContext(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver)
    */
   protected VariableContext createVariableContext(IAeFunctionExecutionContext aContext, IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      return null;
   }

   /**
    * BPEL 1.1 processes don't support $linkName syntax.
    * 
    * @see org.activebpel.rt.bpel.impl.expr.xpath.AeAbstractXPathExpressionRunner#createJoinConditionVariableContext(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext)
    */
   protected VariableContext createJoinConditionVariableContext(IAeFunctionExecutionContext aContext)
   {
      return null;
   }
}
