//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/functions/AeGetMyRolePropertyFunctionValidator.java,v 1.3 2008/03/15 22:13:09 dvilaverde Exp $
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
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.validation.AeValidateIMAVisitor;
import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;

/**
 * Validates the ActiveBPEL extension function getMyRoleProperty(partnerLinkName, operationName, [,path]) 
 */
public class AeGetMyRolePropertyFunctionValidator extends AeAbstractActiveBpelExtensionFunctionValidator
{
   /**
    * @see org.activebpel.rt.expr.validation.functions.IAeFunctionValidator#validate(org.activebpel.rt.expr.def.AeScriptFuncDef, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   public void validate(AeScriptFuncDef aScriptFunction,
         AeExpressionValidationResult aResult,
         IAeExpressionValidationContext aContext)
   {
      super.validate(aScriptFunction, aResult, aContext);
      
      int numArgs = aScriptFunction.getArgs().size();
      if (numArgs < 2 || numArgs > 3)
      {
         addError(aResult,
               AeMessages.getString("AeGetMyRolePropertyFunctionValidator.ERROR_INCORRECT_ARGS_NUMBER"), //$NON-NLS-1$
               new Object[] { aScriptFunction.getName(), null, new Integer(numArgs), aResult.getParseResult().getExpression() });
      }
      else
      {   
         AePartnerLinkDef foundPartnerLink = null;
         
         // validate argument at index 0 to ensure it is a string an to make sure the partner link is resolved
         if ( !aScriptFunction.isStringArgument(0) )
         {
            addError(aResult,
                  AeMessages.getString("AeAbstractActiveBpelExtensionFunctionValidator.ERROR_INCORRECT_ARG_TYPE"), //$NON-NLS-1$
                  new Object [] { aScriptFunction.getName(), "1", "String", aResult.getParseResult().getExpression() });//$NON-NLS-1$//$NON-NLS-2$
         }
         else
         {
            // if the argument is a string ensure that it can be resolved.
            foundPartnerLink = AeDefUtil.findPartnerLinkDef((AeBaseDef) aContext.getBaseDef(), aScriptFunction.getStringArgument(0));
            
            if (foundPartnerLink == null)
            {
               Object[] args = new Object[]{aScriptFunction.getStringArgument(0), aScriptFunction.getName()};
               addError(aResult, AeMessages.getString("AeGetMyRolePropertyFunctionValidator.PARTNERLINK_NOT_RESOLVED"), args); //$NON-NLS-1$
            }
         }
         
         // validate argument at index 1 to ensure it is a string an to make sure the operation is resolved
         if ( !aScriptFunction.isStringArgument(1) )
         {
            addError(aResult,
                  AeMessages.getString("AeAbstractActiveBpelExtensionFunctionValidator.ERROR_INCORRECT_ARG_TYPE"), //$NON-NLS-1$
                  new Object [] { aScriptFunction.getName(), "2", "String", aResult.getParseResult().getExpression() });//$NON-NLS-1$//$NON-NLS-2$
         }
         else
         {
            if (foundPartnerLink != null)
            {
               AeValidateIMAVisitor visitor = new AeValidateIMAVisitor(aScriptFunction.getStringArgument(1), foundPartnerLink);
               AeDefUtil.getProcessDef(aContext.getBaseDef()).accept(visitor);
               
               if (!visitor.foundIMA())
               {
                  Object[] args = new Object[] {aScriptFunction.getStringArgument(1), aScriptFunction.getStringArgument(0), aScriptFunction.getName()};
                  addError(aResult, AeMessages.getString("AeGetMyRolePropertyFunctionValidator.OPERATION_NOT_RESOLVED"), args); //$NON-NLS-1$
               }
            }
         }
      }
   }

}
 
