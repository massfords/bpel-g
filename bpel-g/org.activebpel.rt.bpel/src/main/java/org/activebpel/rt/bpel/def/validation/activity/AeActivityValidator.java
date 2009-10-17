//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityValidator.java,v 1.2 2008/03/20 16:01:31 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeMultipleActivityContainerDef;
import org.activebpel.rt.bpel.def.IAeSingleActivityContainerDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * Base model for all activity subclasses
 */
public class AeActivityValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   protected AeActivityValidator(AeBaseDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      validateNCName(false);

      checkForMissingChildActivity();
   }

   /**
    * checks for a missing child activity
    */
   protected void checkForMissingChildActivity()
   {
      if (getDefinition() instanceof IAeSingleActivityContainerDef || getDefinition() instanceof IAeMultipleActivityContainerDef)
      {
         if (getChildren(AeActivityValidator.class).isEmpty())
         {
               getReporter().reportProblem( BPEL_ACTIVITY_MISSING_CODE, 
                                    ERROR_ACTIVITY_MISSING,
                                    new String[] { getDefinition().getLocationPath() },
                                    getDefinition() );
         }
      }
   }
}
 