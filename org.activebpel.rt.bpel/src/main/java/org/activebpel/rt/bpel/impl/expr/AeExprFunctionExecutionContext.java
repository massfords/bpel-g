//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/AeExprFunctionExecutionContext.java,v 1.6 2008/02/15 17:42:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr;

import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.function.IAeFunctionFactory;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * Implements an IAeExpressionContext.  This implementation basically wraps the current expression 
 * <b>runner</b> context.
 */
public class AeExprFunctionExecutionContext implements IAeFunctionExecutionContext
{
   /** The expression runner context. */
   private IAeExpressionRunnerContext mRunnerContext;
   /** The type converter. */
   private IAeExpressionTypeConverter mExpressionTypeConverter;

   /**
    * Constructs an expression context from the given expression runner context.
    * 
    * @param aRunnerContext
    * @param aExpressionTypeConverter
    */
   public AeExprFunctionExecutionContext(IAeExpressionRunnerContext aRunnerContext,
         IAeExpressionTypeConverter aExpressionTypeConverter)
   {
      setRunnerContext(aRunnerContext);
      setExpressionTypeConverter(aExpressionTypeConverter);
   }

   /**
    * @return Returns the runnerContext.
    */
   protected IAeExpressionRunnerContext getRunnerContext()
   {
      return mRunnerContext;
   }

   /**
    * @param aRunnerContext The runnerContext to set.
    */
   protected void setRunnerContext(IAeExpressionRunnerContext aRunnerContext)
   {
      mRunnerContext = aRunnerContext;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getAbstractBpelObject()
    */
   public AeAbstractBpelObject getAbstractBpelObject()
   {
      return getRunnerContext().getAbstractBpelObject();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getEvaluationContext()
    */
   public Object getEvaluationContext()
   {
      return getRunnerContext().getEvaluationContext();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getFunctionContext()
    */
   public IAeFunctionFactory getFunctionFactory()
   {
      return getRunnerContext().getFunctionContext();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getNamespaceContext()
    */
   public IAeNamespaceContext getNamespaceContext()
   {
      return getRunnerContext().getNamespaceContext();
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionExecutionContext#getFaultFactory()
    */
   public IAeFaultFactory getFaultFactory()
   {
      return getRunnerContext().getFaultFactory();
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
