//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/AeFunctionExecutionContext.java,v 1.6 2008/02/15 17:42:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;

import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter;
import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * A simple implementation of a function execution context.
 */
public class AeFunctionExecutionContext implements IAeFunctionExecutionContext
{
   /** An abstract bpel object. */
   private AeAbstractBpelObject mAbstractBpelObject;
   /** The context to use when evaluating an expression. */
   private Object mEvaluationContext;
   /** A generic namespace context. */
   private IAeNamespaceContext mNamespaceContext;
   /** A generic function context. */
   private IAeFunctionFactory mFunctionFactory;
   /** The type converter. */
   private IAeExpressionTypeConverter mExpressionTypeConverter;

   /**
    * Constructs a function execution context.
    * 
    * @param aAbstractBpelObject
    * @param aEvaluationContext
    * @param aNamespaceContext
    * @param aFunctionFactory
    * @param aExpressionTypeConverter
    */
   public AeFunctionExecutionContext(AeAbstractBpelObject aAbstractBpelObject, Object aEvaluationContext,
         IAeNamespaceContext aNamespaceContext, IAeFunctionFactory aFunctionFactory,
         IAeExpressionTypeConverter aExpressionTypeConverter)
   {
      setAbstractBpelObject(aAbstractBpelObject);
      setEvaluationContext(aEvaluationContext);
      setNamespaceContext(aNamespaceContext);
      setFunctionFactory(aFunctionFactory);
      setExpressionTypeConverter(aExpressionTypeConverter);
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getNamespaceContext()
    */
   public IAeNamespaceContext getNamespaceContext()
   {
      return mNamespaceContext;
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getFunctionFactory()
    */
   public IAeFunctionFactory getFunctionFactory()
   {
      return mFunctionFactory;
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getEvaluationContext()
    */
   public Object getEvaluationContext()
   {
      return mEvaluationContext;
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getAbstractBpelObject()
    */
   public AeAbstractBpelObject getAbstractBpelObject()
   {
      return mAbstractBpelObject;
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
      return getFaultFactory().getNamespace();
   }
   
   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getTypeConverter()
    */
   public IAeExpressionTypeConverter getTypeConverter()
   {
      return getExpressionTypeConverter();
   }

   /**
    * @param aAbstractBpelObject The abstractBpelObject to set.
    */
   protected void setAbstractBpelObject(AeAbstractBpelObject aAbstractBpelObject)
   {
      mAbstractBpelObject = aAbstractBpelObject;
   }
   
   /**
    * @param aEvaluationContext The evaluationContext to set.
    */
   protected void setEvaluationContext(Object aEvaluationContext)
   {
      mEvaluationContext = aEvaluationContext;
   }
   
   /**
    * @param aFunctionContext The functionContext to set.
    */
   protected void setFunctionFactory(IAeFunctionFactory aFunctionContext)
   {
      mFunctionFactory = aFunctionContext;
   }
   
   /**
    * @param aNamespaceContext The namespaceContext to set.
    */
   protected void setNamespaceContext(IAeNamespaceContext aNamespaceContext)
   {
      mNamespaceContext = aNamespaceContext;
   }

   /**
    * @return Returns the expressionTypeConverter.
    */
   protected IAeExpressionTypeConverter getExpressionTypeConverter()
   {
      return mExpressionTypeConverter;
   }

   /**
    * @param aExpressionTypeConverter The expressionTypeConverter to set.
    */
   protected void setExpressionTypeConverter(IAeExpressionTypeConverter aExpressionTypeConverter)
   {
      mExpressionTypeConverter = aExpressionTypeConverter;
   }
}
