//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/decision/AeElseIfValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.decision; 

import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for the else if condition of a an if activity
 */
public class AeElseIfValidator extends AeBaseValidator
{

   /**
    * ctor
    * @param aDef
    */
   public AeElseIfValidator(AeElseIfDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Gets the def
    */
   protected AeElseIfDef getDef()
   {
      return (AeElseIfDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      // No empty cases allowed.
      //
      if ( getDef().getActivityDef() == null )
         getReporter().reportProblem( BPEL_ELSEIF_ACTIVITY_MISSING_CODE,
                              ERROR_ACTIVITY_MISSING,
                              new String[] { getDef().getLocationPath() },
                              getDef() );

      // Needs a condition expression.
      //
      if ( getDef().getConditionDef() == null )
      {
         getReporter().reportProblem( BPEL_ELSEIF_FIELD_MISSING_CODE, ERROR_FIELD_MISSING, new String[] { AeElseIfDef.TAG_CONDITION }, getDef() ); 
      }
      super.validate();
   }
} 