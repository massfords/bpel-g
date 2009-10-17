//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr.bsf/src/org/activebpel/rt/bpel/ext/expr/bsf/impl/AeBSFAbstractExtensionFunctionBean.java,v 1.3 2008/02/15 17:47:11 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.bsf.impl;

import java.util.List;

import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;

/**
 * This is a base class for all of the BSF specific extension function beans.  Each extension
 * function bean provides a way to execute BPEL extension functions, such as bpws:getVariableData()
 * or custom extension functions such as myprfx:myFunction().  This class provides a set of
 * common methods needed by the concrete beans.
 */
public abstract class AeBSFAbstractExtensionFunctionBean
{
   /** The function execution context to use. */
   private IAeFunctionExecutionContext mFunctionExecutionContext;

   /**
    * Constructs the bean with the given function context.
    * 
    * @param aFunctionExecutionContext
    */
   public AeBSFAbstractExtensionFunctionBean(IAeFunctionExecutionContext aFunctionExecutionContext)
   {
      setFunctionExecutionContext(aFunctionExecutionContext);
   }

   /**
    * Returns the namespace used for the bean - must be implemented by children.
    */
   protected abstract String getNamespace();

   /**
    * Calls the given function.
    * 
    * @param aFunctionName
    * @param aArgs
    */
   protected Object callFunction(String aFunctionName, List aArgs)
   {
      String namespace = getNamespace();
      return callFunction(namespace, aFunctionName, aArgs);
   }
   
   /**
    * Calls the given function with the given arguments.  The function is identified by its
    * namespace, and name.
    * 
    * @param aNamespace
    * @param aFunctionName
    * @param aArgs
    */
   protected Object callFunction(String aNamespace, String aFunctionName, List aArgs)
   {
      try
      {
         IAeFunction function = getFunctionExecutionContext().getFunctionFactory().getFunction(aNamespace, aFunctionName);
         Object rval = function.call(getFunctionExecutionContext(), aArgs);
         rval = getFunctionExecutionContext().getTypeConverter().convertToExpressionType(rval);
         return rval;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
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
