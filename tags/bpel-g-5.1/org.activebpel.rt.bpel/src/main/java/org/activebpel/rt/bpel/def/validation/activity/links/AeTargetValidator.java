//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/links/AeTargetValidator.java,v 1.2 2006/09/25 01:34:39 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.links; 

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for the target element
 */
public class AeTargetValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeTargetValidator(AeTargetDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the target def
    */
   protected AeTargetDef getDef()
   {
      return (AeTargetDef) getDefinition();
   }

   /**
    * Validate: records the link with the link validator
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      getProcessValidator().getLinkValidator().addTarget(getDef(), (AeActivityDef) getParentActivityModel().getDefinition() );
   }
}
 