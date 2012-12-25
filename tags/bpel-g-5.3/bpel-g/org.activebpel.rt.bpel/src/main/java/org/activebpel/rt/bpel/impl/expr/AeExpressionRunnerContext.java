//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/AeExpressionRunnerContext.java,v 1.8 2007/11/15 22:31:11 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr;

import org.activebpel.rt.bpel.function.IAeFunctionFactory;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * This is a standard expression runner context. It will be passed to the expression runner when an expression
 * is going to be executed.
 */
public class AeExpressionRunnerContext implements IAeExpressionRunnerContext
{
   /** An abstract bpel object. */
   private AeAbstractBpelObject mAbstractBpelObject;
   /** The context to use when evaluating an expression. */
   private Object mEvaluationContext;
   /** A generic namespace context. */
   private IAeNamespaceContext mNamespaceContext;
   /** A generic function context. */
   private IAeFunctionFactory mFunctionContext;
   /** The expression language URI. */
   private String mLanguageURI;
   /** Variable resolver. */
   private IAeExpressionRunnerVariableResolver mVariableResolver;

   /**
    * Constructs an expression runner context.
    * 
    * @param aAbstractBpelObject
    * @param aEvaluationContext
    * @param aLanguageURI
    * @param aNamespaceContext
    * @param aFunctionContext
    * @param aVariableResolver
    */
   public AeExpressionRunnerContext(AeAbstractBpelObject aAbstractBpelObject, Object aEvaluationContext,
         String aLanguageURI, IAeNamespaceContext aNamespaceContext, 
         IAeFunctionFactory aFunctionContext, IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      setAbstractBpelObject(aAbstractBpelObject);
      setEvaluationContext(aEvaluationContext);
      setLanguageURI(aLanguageURI);
      setNamespaceContext(aNamespaceContext);
      setFunctionContext(aFunctionContext);
      setVariableResolver(aVariableResolver);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getAbstractBpelObject()
    */
   public AeAbstractBpelObject getAbstractBpelObject()
   {
      return mAbstractBpelObject;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getNamespaceContext()
    */
   public IAeNamespaceContext getNamespaceContext()
   {
      return mNamespaceContext;
   }

   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getEvaluationContext()
    */
   public Object getEvaluationContext()
   {
      return mEvaluationContext;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getFunctionContext()
    */
   public IAeFunctionFactory getFunctionContext()
   {
      return mFunctionContext;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getFaultFactory()
    */
   public IAeFaultFactory getFaultFactory()
   {
      if (getAbstractBpelObject() != null)
         return getAbstractBpelObject().getFaultFactory();
      return null;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getBpelNamespace()
    */
   public String getBpelNamespace()
   {
      return getFaultFactory().getNamespace();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getLanguageURI()
    */
   public String getLanguageURI()
   {
      return mLanguageURI;
   }

   /**
    * @param aAbstractBpelObject The abstractBpelObject to set.
    */
   protected void setAbstractBpelObject(AeAbstractBpelObject aAbstractBpelObject)
   {
      mAbstractBpelObject = aAbstractBpelObject;
   }

   /**
    * @param aFunctionContext The functionContext to set.
    */
   protected void setFunctionContext(IAeFunctionFactory aFunctionContext)
   {
      mFunctionContext = aFunctionContext;
   }

   /**
    * @param aNamespaceContext The namespaceContext to set.
    */
   protected void setNamespaceContext(IAeNamespaceContext aNamespaceContext)
   {
      mNamespaceContext = aNamespaceContext;
   }

   /**
    * @param aEvaluationContext The evaluationContext to set.
    */
   protected void setEvaluationContext(Object aEvaluationContext)
   {
      mEvaluationContext = aEvaluationContext;
   }

   /**
    * @param aLanguageURI The languageURI to set.
    */
   protected void setLanguageURI(String aLanguageURI)
   {
      mLanguageURI = aLanguageURI;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext#getVariableResolver()
    */
   public IAeExpressionRunnerVariableResolver getVariableResolver()
   {
      return mVariableResolver;
   }
   
   /**
    * Sets the variable resolver.
    * @param aResolver
    */
   protected void setVariableResolver(IAeExpressionRunnerVariableResolver aResolver)
   {
      mVariableResolver = aResolver;
   }
}
