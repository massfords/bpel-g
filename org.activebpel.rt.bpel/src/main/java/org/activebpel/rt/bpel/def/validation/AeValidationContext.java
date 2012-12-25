//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeValidationContext.java,v 1.5 2008/03/20 16:00:22 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation;

import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * A simple validation context impl.
 */
public class AeValidationContext implements IAeValidationContext
{
   /** An error reporter. */
   private IAeValidationProblemReporter mErrorReporter;
   /** A context WSDL provider. */
   private IAeContextWSDLProvider mContextWSDLProvider;
   /** An expression validator. */
   private IAeExpressionLanguageFactory mExpressionFactory;
   /** factory for validating functions */
   private IAeFunctionValidatorFactory mFunctionFactory;

   /**
    * Constructs a simple validation context.
    * 
    * @param aErrorReporter
    * @param aContextWSDLProvider
    * @param aExpressionFactory
    */
   public AeValidationContext(IAeValidationProblemReporter aErrorReporter, IAeContextWSDLProvider aContextWSDLProvider, 
         IAeExpressionLanguageFactory aExpressionFactory, IAeFunctionValidatorFactory aFunctionFactory)
   {
      setErrorReporter(aErrorReporter);
      setContextWSDLProvider(aContextWSDLProvider);
      setExpressionFactory(aExpressionFactory);
      setFunctionValidatorFactory(aFunctionFactory);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeValidationContext#getFunctionValidatorFactory()
    */
   public IAeFunctionValidatorFactory getFunctionValidatorFactory()
   {
      return mFunctionFactory;
   }
   
   /**
    * Setter for function validator factory
    * @param aFactory
    */
   protected void setFunctionValidatorFactory(IAeFunctionValidatorFactory aFactory)
   {
      mFunctionFactory = aFactory;
   }

   /**
    * Gets the base error reporter to use during validation.
    */
   public IAeValidationProblemReporter getErrorReporter()
   {
      return mErrorReporter;
   }

   /**
    * Gets the context WSDL provider to use during validation.
    */
   public IAeContextWSDLProvider getContextWSDLProvider()
   {
      return mContextWSDLProvider;
   }

   /**
    * Gets the expression language factory to use during validation.
    */
   public IAeExpressionLanguageFactory getExpressionLanguageFactory()
   {
      return mExpressionFactory;
   }

   /**
    * @param aContextWSDLProvider The contextWSDLProvider to set.
    */
   protected void setContextWSDLProvider(IAeContextWSDLProvider aContextWSDLProvider)
   {
      mContextWSDLProvider = aContextWSDLProvider;
   }

   /**
    * @param aErrorReporter The errorReporter to set.
    */
   protected void setErrorReporter(IAeValidationProblemReporter aErrorReporter)
   {
      mErrorReporter = aErrorReporter;
   }

   /**
    * @param aExpressionValidator The expressionValidator to set.
    */
   protected void setExpressionFactory(IAeExpressionLanguageFactory aExpressionValidator)
   {
      mExpressionFactory = aExpressionValidator;
   }

}
