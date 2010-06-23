//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/functions/AeDoXslTransformFunctionValidator.java,v 1.2 2008/01/25 21:01:17 dvilaverde Exp $
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
 * Validates the bpel:doXslTransform() function
 * 
 * doXslTransform('string', $var/my/xpath, 'name', 'value', 'n2', $x, 'n3', count($y/some/path) )
 * 
 */
public class AeDoXslTransformFunctionValidator extends AeAbstractFunctionValidator
{
   /**
    * @see org.activebpel.rt.expr.validation.functions.IAeFunctionValidator#validate(org.activebpel.rt.expr.def.AeScriptFuncDef, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   public void validate(AeScriptFuncDef aScriptFunction,
         AeExpressionValidationResult aResult,
         IAeExpressionValidationContext aContext)
   {
      int numArgs = aScriptFunction.getArgs().size();
      if (numArgs < 2 || (numArgs % 2) != 0)
      {
         addError(aResult, AeMessages.getString("AeDoXslTransformFunctionValidator.INCORRECT_NUM_ARGS_TO_DOXSLTRANSFORM_ERROR"), //$NON-NLS-1$
               new Object[] { new Integer(numArgs) });
      }
      else
      {
         if ( !aScriptFunction.isStringArgument(0) )
         {
            addError(aResult, AeMessages.getString("AeDoXslTransformFunctionValidator.INVALID_ARG0_IN_DOXSLTRANSFORM_ERROR"), null); //$NON-NLS-1$
         }
         if (!(aScriptFunction.isExpressionArgument(1)))
         {
            addError(aResult, AeMessages.getString("AeDoXslTransformFunctionValidator.INVALID_ARG1_IN_DOXSLTRANSFORM_ERROR"), null); //$NON-NLS-1$
         }
         // Check the optional (key, value) arguments - each key must be a string literal
         for (int i = 2; i < aScriptFunction.getArgs().size(); i += 2)
         {
            if ( !aScriptFunction.isStringArgument(i) )
            {
               addError(
                     aResult,
                     AeMessages.getString("AeDoXslTransformFunctionValidator.INVALID_PARAM_NAME_IN_DOXSLTRANSFORM_ERROR"), new Object[] { new Integer(i + 1) }); //$NON-NLS-1$
            }
         }
      }
   }
 
}