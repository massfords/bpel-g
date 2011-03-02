// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeWSBPELXQueryLinkVariableResolver.java,v 1.4 2008/02/15 17:46:48 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.value.StringValue;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.function.IAeFunctionFactory;
import org.activebpel.rt.bpel.impl.activity.support.AeLink;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver;
import org.activebpel.rt.util.AeUtil;

/**
 * A WS-BPEL Xquery variable resolver.  This class resolves XQuery variables (of the form $bpelLinkName)
 * to XQuery expressions that, when executed, return the value of the named BPEL Link.
 */
public class AeWSBPELXQueryLinkVariableResolver extends AeWSBPELXQueryVariableResolver
{
   /**
    * Constructor.
    * 
    * @param aContext
    */
   public AeWSBPELXQueryLinkVariableResolver(IAeFunctionExecutionContext aContext, IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      super(aContext, aVariableResolver);
   }
   
   /**
    * @see org.activebpel.rt.bpel.ext.expr.impl.xquery.AeWSBPELXQueryVariableResolver#hasVariable(int, java.lang.String, java.lang.String)
    */
   public boolean hasVariable(int aNameCode, String aUri, String aLocal)
   {
      if (AeUtil.notNullOrEmpty(aUri))
      {
         return false;
      }
      
      AeLink link = getFunctionExecutionContext().getAbstractBpelObject().findTargetLink(aLocal);
      return link != null;
   }

   /**
    * Create the expression that will be executed for the variable reference.
    * 
    * @param aLinkName
    */
   protected Expression createVariableExpression(String aLinkName)
   {
      try
      {
         IAeFunctionFactory funcContext = getFunctionExecutionContext().getFunctionFactory();
         IAeFunction function = funcContext.getFunction(IAeBPELConstants.BPWS_NAMESPACE_URI, "getLinkStatus"); //$NON-NLS-1$
         AeXQueryFunction func = new AeXQueryFunction(function, getFunctionExecutionContext());
         Expression [] args = new Expression[] { StringValue.makeStringValue(aLinkName) };
         func.setArguments(args);
         return func;
      }
      catch (AeUnresolvableException ex)
      {
         throw new RuntimeException("Error: could not find getVariableData()."); //$NON-NLS-1$
      }
   }
}
