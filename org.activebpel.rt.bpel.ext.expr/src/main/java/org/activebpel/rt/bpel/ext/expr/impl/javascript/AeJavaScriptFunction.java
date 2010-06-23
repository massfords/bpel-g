//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/javascript/AeJavaScriptFunction.java,v 1.4 2006/09/25 21:18:09 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.impl.javascript;

import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;


/**
 * This class implements a Rhino Function.
 */
public class AeJavaScriptFunction extends AeScriptable implements Function
{
   /** The generic function to delegate to. */
   private IAeFunction mDelegate;
   /** The function execution context. */
   private IAeFunctionExecutionContext mFunctionExecutionContext;

   /**
    * Constructs this xpath function given the generic delegate function.
    * 
    * @param aParentScope
    * @param aFunction
    * @param aFunctionExecutionContext
    */
   public AeJavaScriptFunction(Scriptable aParentScope, IAeFunction aFunction, IAeFunctionExecutionContext aFunctionExecutionContext)
   {
      super(aParentScope);
      setDelegate(aFunction);
      setFunctionExecutionContext(aFunctionExecutionContext);
   }

   /**
    * Overrides method to delegate the call to the underlying IAeExpressionFunction object.
    * 
    * @see org.mozilla.javascript.Function#call(org.mozilla.javascript.Context, org.mozilla.javascript.Scriptable, org.mozilla.javascript.Scriptable, java.lang.Object[])
    */
   public Object call(Context aCtx, Scriptable aScope, Scriptable aThisObj,
         Object[] aArgs)
   {
      List args = new ArrayList();
      for (int i = 0; i < aArgs.length; i++)
      {
         args.add(getFunctionExecutionContext().getTypeConverter().convertToEngineType(aArgs[i]));
      }
      try
      {
         // Call the function and convert the result to something Rhino will like.
         Object rval = getDelegate().call(getFunctionExecutionContext(), args);
         return getFunctionExecutionContext().getTypeConverter().convertToExpressionType(rval);
      }
      catch (AeFunctionCallException fce)
      {
         throw new RuntimeException(fce.getLocalizedMessage());
      }
   }

   /**
    * @see org.mozilla.javascript.Function#construct(org.mozilla.javascript.Context, org.mozilla.javascript.Scriptable, java.lang.Object[])
    */
   public Scriptable construct(Context cx, Scriptable scope, Object[] args)
   {
      throw new RuntimeException("Attempted to call the constructor on a function - invalid."); //$NON-NLS-1$
   }

   /**
    * @return Returns the delegate.
    */
   protected IAeFunction getDelegate()
   {
      return mDelegate;
   }
   
   /**
    * @param aDelegate The delegate to set.
    */
   protected void setDelegate(IAeFunction aDelegate)
   {
      mDelegate = aDelegate;
   }

   /**
    * @return Returns the functionExecutionContext.
    */
   protected IAeFunctionExecutionContext getFunctionExecutionContext()
   {
      return mFunctionExecutionContext;
   }

   /**
    * @param aFunctionExecutionContext The functionExecutionContext to set.
    */
   protected void setFunctionExecutionContext(IAeFunctionExecutionContext aFunctionExecutionContext)
   {
      mFunctionExecutionContext = aFunctionExecutionContext;
   }
}
