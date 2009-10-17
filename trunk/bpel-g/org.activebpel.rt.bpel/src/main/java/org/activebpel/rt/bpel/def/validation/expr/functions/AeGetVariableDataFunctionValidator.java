//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/functions/AeGetVariableDataFunctionValidator.java,v 1.2 2008/01/25 21:01:17 dvilaverde Exp $
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

/**
 * Validates the standard bpws getVariableData() function 
 */
public class AeGetVariableDataFunctionValidator extends AeAbstractFunctionValidator
{
   /**
    * @see org.activebpel.rt.expr.validation.functions.IAeFunctionValidator#validate(org.activebpel.rt.expr.def.AeScriptFuncDef, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   public void validate(AeScriptFuncDef aFunction, 
                        AeExpressionValidationResult aResult,
                        IAeExpressionValidationContext aContext)
   {
      int numArgs = aFunction.getArgs().size();

      if (numArgs < 1 || numArgs > 3)
      {
         addError(aResult, AeMessages.getString("AeGetVariableDataFunctionValidator.INVALID_GETVARIABLEDATA_PARAMS"),  //$NON-NLS-1$
               new Object[] { new Integer(numArgs), aResult.getParseResult().getExpression() });
      }
      else
      {
         if (!aFunction.isStringArgument(0))
         {
            addWarning(aResult,
                  AeMessages.getString("AeGetVariableDataFunctionValidator.NON_CONSTANT_REF_IN_GETVARDATA_WARNING"), //$NON-NLS-1$
                  new String[] {});
         }
      }
   }
}
 
