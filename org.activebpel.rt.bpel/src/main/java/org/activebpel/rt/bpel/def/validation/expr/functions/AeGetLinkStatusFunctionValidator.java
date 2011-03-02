//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/functions/AeGetLinkStatusFunctionValidator.java,v 1.2 2008/01/25 21:01:16 dvilaverde Exp $
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
import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;

/**
 * Validates the bpws standard getLinkStatus function
 */
public class AeGetLinkStatusFunctionValidator extends AeAbstractFunctionValidator
{
   /**
    * @see org.activebpel.rt.expr.validation.functions.IAeFunctionValidator#validate(org.activebpel.rt.expr.def.AeScriptFuncDef, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   public void validate(AeScriptFuncDef aScriptFunction, 
         AeExpressionValidationResult aResult,
         IAeExpressionValidationContext aContext)
   {
      int numArgs = aScriptFunction.getArgs().size();
      if (numArgs != 1)
      {
         addError(aResult,
               AeMessages.getString("AeAbstractFunctionValidator.ERROR_INCORRECT_ARGS_NUMBER"), //$NON-NLS-1$
               new Object[] { aScriptFunction.getName(), new Integer(1), new Integer(numArgs), aResult.getParseResult().getExpression() });
      }
      else if ( !(aScriptFunction.getArgument(0) instanceof String) )
      {
         addError(aResult,
               AeMessages.getString("AeAbstractFunctionValidator.ERROR_INCORRECT_ARG_TYPE"), //$NON-NLS-1$
               new Object [] { aScriptFunction.getName(), "", "String", aResult.getParseResult().getExpression() });//$NON-NLS-1$//$NON-NLS-2$
      }
      else
      {
         AeActivityDef def = AeDefUtil.getActivityDef(aContext.getBaseDef());
         String linkName = aScriptFunction.getStringArgument(0);
         if (!def.getTargetLinkNames().contains(linkName))
         {
            addError(aResult,
                  AeMessages.getString("AeGetLinkStatusFunctionValidator.LINK_DOES_NOT_EXIST_ERROR"), //$NON-NLS-1$
                  new Object[] { linkName, aResult.getParseResult().getExpression() });
         }
      }
   }
}
 
