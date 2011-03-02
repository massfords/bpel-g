//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/validation/functions/IAeFunctionValidator.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.validation.functions; 

import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;

/**
 * Interface for code that validates the syntax of a function, adding any errors
 * to the {@link AeExpressionValidationResult} passed in. 
 */
public interface IAeFunctionValidator
{
   /**
    * Validates the syntax of the function, adding any errors to the result
    * passed in.
    * @param aScriptFunction - function to validate
    * @param aResult - report errors on the result
    * @param aValidationContext - context for the enclosing expression
    */
   public void validate(AeScriptFuncDef aScriptFunction, 
                        AeExpressionValidationResult aResult, 
                        IAeExpressionValidationContext aValidationContext);
}
 
