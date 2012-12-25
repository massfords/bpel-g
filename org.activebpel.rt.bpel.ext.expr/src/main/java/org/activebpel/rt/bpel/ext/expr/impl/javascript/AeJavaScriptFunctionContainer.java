//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/javascript/AeJavaScriptFunctionContainer.java,v 1.7 2008/02/15 17:46:48 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.impl.javascript;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.function.IAeFunctionFactory;
import org.activebpel.rt.util.AeUtil;
import org.mozilla.javascript.Scriptable;

/**
 * This class implements the Rhino Scriptable interface in order to expose a set of custom extension
 * functions.  A single instance of this class exposes the functions from a single function context
 * to the JavaScript expression.
 */
public class AeJavaScriptFunctionContainer extends AeScriptable
{
   /** The function exec context to use during evaluation. */
   private IAeFunctionExecutionContext mFunctionExecutionContext;
   /** The namespace associated with this function container. */
   private String mNamespace;

   /**
    * Constructs the function container with the given parent scope.
    * 
    * @param aNamespace
    * @param aParentScope
    * @param aFunctionExecutionContext
    */
   public AeJavaScriptFunctionContainer(String aNamespace, Scriptable aParentScope,
         IAeFunctionExecutionContext aFunctionExecutionContext)
   {
      super(aParentScope);
      setNamespace(aNamespace);
      setFunctionExecutionContext(aFunctionExecutionContext);
   }

   /**
    * @see org.mozilla.javascript.Scriptable#getClassName()
    */
   public String getClassName()
   {
      return AeJavaScriptFunctionContainer.class.getName();
   }

   /**
    * @see org.mozilla.javascript.Scriptable#get(java.lang.String, org.mozilla.javascript.Scriptable)
    */
   public Object get(String aName, Scriptable aStart)
   {
      try
      {
         IAeFunction function = findFunction(aName);
         return new AeJavaScriptFunction(getParentScope(), function, getFunctionExecutionContext());
      }
      catch (AeUnresolvableException e)
      {
         AeException.logError(e, e.getLocalizedMessage());
         throw new RuntimeException(e);
      }
   }

   /**
    * @see org.mozilla.javascript.Scriptable#has(java.lang.String, org.mozilla.javascript.Scriptable)
    */
   public boolean has(String aName, Scriptable aStart)
   {
      try
      {
         IAeFunction function = findFunction(aName);
         return function != null;
      }
      catch (AeUnresolvableException e)
      {
         return false;
      }
   }

   /**
    * @see org.mozilla.javascript.Scriptable#hasInstance(org.mozilla.javascript.Scriptable)
    */
   public boolean hasInstance(Scriptable aInstance)
   {
      if (aInstance instanceof AeJavaScriptFunctionContainer)
      {
         AeJavaScriptFunctionContainer other = (AeJavaScriptFunctionContainer) aInstance;
         return AeUtil.compareObjects(getNamespace(), other.getNamespace());
      }
      return false;
   }
   
   /**
    * Finds and returns the function with the given name.
    * 
    * @param aName
    */
   protected IAeFunction findFunction(String aName) throws AeUnresolvableException
   {
      IAeFunctionFactory funcCtx = getFunctionExecutionContext().getFunctionFactory();
      IAeFunction function = funcCtx.getFunction(getNamespace(), aName);
      return function;
   }

   /**
    * @return Returns the namespace.
    */
   protected String getNamespace()
   {
      return mNamespace;
   }
   
   /**
    * @param aNamespace The namespace to set.
    */
   protected void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
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
