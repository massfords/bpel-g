//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeTerminationHandlerValidator.java,v 1.5 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for termination handler
 */
public class AeTerminationHandlerValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeTerminationHandlerValidator(AeTerminationHandlerDef aDef)
   {
      super(aDef);
   }

   /**
    * Getter for the def
    */
   protected AeTerminationHandlerDef getDef()
   {
      return (AeTerminationHandlerDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      if (getDef().getActivityDef() == null)
         getReporter().reportProblem(BPEL_TERM_EMPTY_CONTAINER_CODE,
                                    ERROR_EMPTY_CONTAINER, 
                                    new String[] { getDef().getLocationPath() },
                                    getDefinition());
   }
}
 