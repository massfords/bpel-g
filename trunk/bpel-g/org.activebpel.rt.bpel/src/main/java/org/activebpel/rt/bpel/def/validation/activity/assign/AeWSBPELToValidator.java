// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/assign/AeWSBPELToValidator.java,v 1.8 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.activity.assign;

import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.io.readers.def.AeExpressionSpecStrategyKey;
import org.activebpel.rt.bpel.def.io.readers.def.IAeToStrategyKeys;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationDefs;

/**
 * A WS-BPEL 2.0 implementation of the ToModel.  This class overrides the base class in 
 * order to do some additional testing for the Query to-spec variant introduced in BPEL 2.0.
 */
public class AeWSBPELToValidator extends AeToValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeWSBPELToValidator(AeToDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.assign.AeToValidator#validate()
    */
   public void validate()
   {
      if (getDef().getStrategyKey() == IAeToStrategyKeys.KEY_TO_EXPRESSION)
      {
         getReporter().reportProblem(WSBPEL_INVALID_TO_FORMAT_EXPRESSION_CODE, 
                                    IAeValidationDefs.ERROR_TO_EXPR_FORMAT_INVALID, 
                                    new Object[] { getDef().getExpression() }, 
                                    getDefinition());
         return;
      }
      else if (getDef().getStrategyKey() instanceof AeExpressionSpecStrategyKey)
      {
         AeExpressionSpecStrategyKey expressionStrategy = (AeExpressionSpecStrategyKey) getDef().getStrategyKey();
         String varName = expressionStrategy.getVariableName();
         String partName = expressionStrategy.getPartName();
         String query = expressionStrategy.getQuery();
         AeVariableValidator variableValidator = getVariableValidator(varName, null, true, AeVariableValidator.VARIABLE_WRITE_TO_SPEC);
         if (variableValidator != null)
         {
            variableValidator.addUsage(this, partName, query, null);
         }
      }
      else
      {
         // Just do super.validate() - this will cause the Query to be validated as-is, which will give us
         // error messages like "could not parse query" etc...
         super.validate();
      }
   }
}
