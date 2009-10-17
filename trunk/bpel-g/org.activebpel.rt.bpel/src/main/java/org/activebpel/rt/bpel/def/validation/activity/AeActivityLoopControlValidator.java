//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityLoopControlValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.activity.IAeLoopActivityDef;

/**
 * base model for activities that must be nested within a loop control (break, continue)
 */
public class AeActivityLoopControlValidator extends AeActivityValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivityLoopControlValidator(AeBaseDef aDef)
   {
      super(aDef);
   }
   
   /**
    * validates:
    * 1. activity is nested within a loop control
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      validateLoopControl();
   }

   /**
    * Ensures that the loop control exists within a loop container.
    */
   protected void validateLoopControl()
   {
      if(!enclosedWithinDef(IAeLoopActivityDef.class))
      {
         getReporter().reportProblem(BPEL_INVALID_LOOP_LOCATION_CODE,
                                 AeMessages.getString("IAeValidationDefs.InvalidLocationForLoopControl"), //$NON-NLS-1$
                                 new String[] {getDefinition().getLocationPath()}, 
                                 getDefinition()); 
      }
   }
}
 