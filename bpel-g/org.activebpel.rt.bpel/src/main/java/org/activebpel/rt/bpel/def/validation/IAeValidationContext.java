//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/IAeValidationContext.java,v 1.5 2008/03/20 16:01:15 dvilaverde Exp $
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
 * This interface defines a context that will be used by the def validation visitor during
 * validation.  It provides access to various objects required during validation.
 */
public interface IAeValidationContext
{
   /**
    * Gets the base error reporter to use during validation.
    */
   public IAeValidationProblemReporter getErrorReporter();

   /**
    * Gets the context WSDL provider to use during validation.
    */
   public IAeContextWSDLProvider getContextWSDLProvider();
   
   /**
    * Gets the expression language factory to use during validation.
    */
   public IAeExpressionLanguageFactory getExpressionLanguageFactory();
   
   /**
    * Gets the function validator factory to use when validating function calls
    * found in expressions or queries.
    */
   public IAeFunctionValidatorFactory getFunctionValidatorFactory();
}
