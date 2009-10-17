//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/xpath/AeXPathContext.java,v 1.6 2008/02/15 17:42:50 EWittmann Exp $
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
import org.activebpel.rt.bpel.function.IAeFunctionFactory;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.jaxen.Context;

/**
 * Provides a runtime evaluation context for calling expression functions.  It wraps the jaxen 
 * runtime function context.
 */
public class AeXPathContext implements IAeFunctionExecutionContext
{
   /** The jaxen context. */
   private Context mContext;
   /** The proxied function exec context. */
   private IAeFunctionExecutionContext mFunctionExecContext;

   /**
    * Constructs an xpath context from a jaxen function context.
    * 
    * @param aContext
    */
   public AeXPathContext(Context aContext, IAeFunctionExecutionContext aFunctionExecContext)
   {
      setContext(aContext);
      setFunctionExecContext(aFunctionExecContext);
   }

   /**
    * @return Returns the context.
    */
   public Context getContext()
   {
      return mContext;
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getNamespaceContext()
    */
   public IAeNamespaceContext getNamespaceContext()
   {
      return getFunctionExecContext().getNamespaceContext();
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getFunctionFactory()
    */
   public IAeFunctionFactory getFunctionFactory()
   {
      return getFunctionExecContext().getFunctionFactory();
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getEvaluationContext()
    */
   public Object getEvaluationContext()
   {
      return getFunctionExecContext().getEvaluationContext();
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getAbstractBpelObject()
    */
   public AeAbstractBpelObject getAbstractBpelObject()
   {
      return getFunctionExecContext().getAbstractBpelObject();
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getFaultFactory()
    */
   public IAeFaultFactory getFaultFactory()
   {
      return getAbstractBpelObject().getFaultFactory();
   }
   
   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getBpelNamespace()
    */
   public String getBpelNamespace()
   {
      return getFunctionExecContext().getBpelNamespace();
   }
   
   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getTypeConverter()
    */
   public IAeExpressionTypeConverter getTypeConverter()
   {
      return getFunctionExecContext().getTypeConverter();
   }

   /**
    * @param aContext The context to set.
    */
   protected void setContext(Context aContext)
   {
      mContext = aContext;
   }
   
   /**
    * @return Returns the functionExecContext.
    */
   protected IAeFunctionExecutionContext getFunctionExecContext()
   {
      return mFunctionExecContext;
   }
   
   /**
    * @param aFunctionExecContext The functionExecContext to set.
    */
   protected void setFunctionExecContext(IAeFunctionExecutionContext aFunctionExecContext)
   {
      mFunctionExecContext = aFunctionExecContext;
   }
}
