//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/assign/AeAssignCopyValidator.java,v 1.4 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.assign; 

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.io.readers.def.IAeFromStrategyNames;
import org.activebpel.rt.bpel.def.io.readers.def.IAeToStrategyNames;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for a copy operation within an assign
 */
public class AeAssignCopyValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeAssignCopyValidator(AeAssignCopyDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      AeFromValidator from = (AeFromValidator) getChild(AeFromValidator.class);
      AeToValidator to = (AeToValidator) getChild(AeToValidator.class);

      if (from == null)
      {
         getReporter().reportProblem(BPEL_ASSIGNCOPY_MISSING_FROM_TO_CODE, AeMessages.getString("AeDefValidationVisitor.ASSIGNCOPY_WITH_MISSING_FROM"), null, getDefinition()); //$NON-NLS-1$
      }
      if (to == null)
      {
         getReporter().reportProblem(BPEL_ASSIGNCOPY_MISSING_FROM_TO_CODE, AeMessages.getString("AeDefValidationVisitor.ASSIGNCOPY_WITH_MISSING_TO"), null, getDefinition()); //$NON-NLS-1$
      }
      
      if (from != null && to != null && from.getDef().getStrategyKey() != null && to.getDef().getStrategyKey() != null)
      {
         // validate that the strategies make sense
         String fromStrategyName = from.getDef().getStrategyKey().getStrategyName();
         String toStrategyName = to.getDef().getStrategyKey().getStrategyName();
         if ((fromStrategyName.equals(IAeFromStrategyNames.FROM_VARIABLE_MESSAGE) && !toStrategyName.equals(IAeToStrategyNames.TO_VARIABLE_MESSAGE))
               || (!fromStrategyName.equals(IAeFromStrategyNames.FROM_VARIABLE_MESSAGE) && toStrategyName.equals(IAeToStrategyNames.TO_VARIABLE_MESSAGE)))
         {
            getReporter().reportProblem(BPEL_MISMATCHED_ASSIGNMENT_FAILURE_CODE, ERROR_MISMATCHED_ASSIGNMENT_FAILURE, null, getDefinition());
         }
      }
   }
}
 