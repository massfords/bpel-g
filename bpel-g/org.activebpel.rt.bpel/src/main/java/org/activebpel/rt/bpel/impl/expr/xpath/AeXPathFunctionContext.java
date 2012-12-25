//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/xpath/AeXPathFunctionContext.java,v 1.3 2008/02/15 17:42:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr.xpath;

import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.jaxen.Function;
import org.jaxen.FunctionContext;
import org.jaxen.UnresolvableException;
import org.jaxen.XPathFunctionContext;

/**
 * This class implements a jaxen function context from an ActiveBPEL function context.  This class
 * basically wraps a generic ActiveBPEL function context and delegates calls to it.
 */
public class AeXPathFunctionContext implements FunctionContext
{
   /** The expression runner context. */
   private IAeFunctionExecutionContext mFunctionExecContext;
   /** The default Jaxen function context to use. */
   private FunctionContext mDefaultContext;

   /**
    * Constructs an xpath function context from the given the expression runner context.
    * 
    * @param aFuncExecContext
    */
   public AeXPathFunctionContext(IAeFunctionExecutionContext aFuncExecContext)
   {
      setFunctionExecContext(aFuncExecContext);
      setDefaultContext(XPathFunctionContext.getInstance());
   }

   /**
    * @see org.jaxen.FunctionContext#getFunction(java.lang.String, java.lang.String, java.lang.String)
    */
   public Function getFunction(String aNamespaceURI, String aPrefix, String aLocalName)
         throws UnresolvableException
   {
      try
      {
         IAeFunction function = getFunctionExecContext().getFunctionFactory().getFunction(aNamespaceURI, aLocalName);
         if (function != null)
         {
            return new AeXPathFunction(function, getFunctionExecContext());
         }
      }
      catch (AeUnresolvableException aException)
      {
         aException.logError();
         throw new UnresolvableException(aException.getLocalizedMessage());
      }

      return getDefaultContext().getFunction(aNamespaceURI, aPrefix, aLocalName);
   }

   /**
    * @return Returns the defaultContext.
    */
   protected FunctionContext getDefaultContext()
   {
      return mDefaultContext;
   }

   /**
    * @param aDefaultContext The defaultContext to set.
    */
   protected void setDefaultContext(FunctionContext aDefaultContext)
   {
      mDefaultContext = aDefaultContext;
   }

   /**
    * @return Returns the runnerContext.
    */
   protected IAeFunctionExecutionContext getFunctionExecContext()
   {
      return mFunctionExecContext;
   }

   /**
    * @param aFuncExecContext The function execution context to set.
    */
   protected void setFunctionExecContext(IAeFunctionExecutionContext aFuncExecContext)
   {
      mFunctionExecContext = aFuncExecContext;
   }
}
