//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/functions/AeWSBPELFunctionUsedInBPWSValidator.java,v 1.2 2008/01/25 21:01:17 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.expr.functions; 

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidator;

/**
 * Pass through validator that also reports an error message that the function
 * being used isn't appropriate for this version of BPEL.
 */
public class AeWSBPELFunctionUsedInBPWSValidator extends AeDelegatingFunctionValidator
{
   /**
    * Ctor
    * @param aValidator
    */
   public AeWSBPELFunctionUsedInBPWSValidator(IAeFunctionValidator aValidator)
   {
      super(aValidator);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.expr.functions.AeDelegatingFunctionValidator#validate(org.activebpel.rt.expr.def.AeScriptFuncDef, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   public void validate(AeScriptFuncDef aScriptFunction, AeExpressionValidationResult aResult, IAeExpressionValidationContext aContext)
   {
      super.validate(aScriptFunction, aResult, aContext);
      addError(aResult, AeMessages.getString("AeWSBPELFunctionUsedInBPWSValidator.BPEL20_FUNCTION_USED_ERROR"), new Object[] { aScriptFunction.getName() }); //$NON-NLS-1$
   }
}
 
