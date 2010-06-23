//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityIfValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.validation.activity.decision.AeIfValidator;

/**
 * model provides validation for the 1.1 switch and 2.0 if activities
 */
public class AeActivityIfValidator extends AeActivityValidator
{
   /** error for reporting missing condition */
   private String mMissingConditionError;
   
   /**
    * ctor
    * @param aDef
    */
   public AeActivityIfValidator(AeActivityIfDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Validates that the if contains at least one conditional check
    * @see org.activebpel.rt.bpel.def.validation.IAeValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      AeIfValidator child = (AeIfValidator)getChild(AeIfValidator.class);
      if(child == null)
      {
         getReporter().reportProblem( BPEL_MISSING_CONDITION_CODE, getMissingConditionError(), null, getDefinition() );
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#checkForMissingChildActivity()
    */
   protected void checkForMissingChildActivity()
   {
      // no-op here, the child activity will be nested within an if def
   }

   /**
    * Error message for missing condition
    */
   public String getMissingConditionError()
   {
      return mMissingConditionError;
   }

   /**
    * Setter for missing condition error message
    * @param aMissingConditionError
    */
   public void setMissingConditionError(String aMissingConditionError)
   {
      mMissingConditionError = aMissingConditionError;
   }
}
 