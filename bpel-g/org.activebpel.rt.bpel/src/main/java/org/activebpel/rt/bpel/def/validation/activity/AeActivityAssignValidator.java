//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityAssignValidator.java,v 1.2 2008/03/20 16:01:31 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.validation.activity.assign.AeAssignCopyValidator;

/**
 * Performs validation for the assign activity
 */
public class AeActivityAssignValidator extends AeActivityValidator
{
   /**
    * ctor 
    * @param aDef
    */
   public AeActivityAssignValidator(AeActivityAssignDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      validateAssignOperations();
   }

   /**
    * validates that the assign has children
    */
   protected void validateAssignOperations()
   {
      if ( getChildren(AeAssignCopyValidator.class).size() == 0 )
         getReporter().reportProblem( BPEL_NO_COPY_CODE, ERROR_NO_COPY, null, getDefinition() );
   }
}
 