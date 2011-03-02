//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityCompensateValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.IAeCompensateParentDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef;

/**
 * Model for validating the compensate activity
 */
public class AeActivityCompensateValidator extends AeActivityValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivityCompensateValidator(AeActivityCompensateDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the def
    */
   protected AeActivityCompensateDef getDef()
   {
      return (AeActivityCompensateDef) getDefinition();
   }

   /**
    * Validates that the compensate activity is properly nested.
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();

      if (!enclosedWithinDef(IAeCompensateParentDef.class))
      {
         getReporter().reportProblem( BPEL_MISPLACED_COMPENSATE_CODE, ERROR_MISPLACED_COMPENSATE, null, getDefinition() );
      }
   }
}
 