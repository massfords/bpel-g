//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityExitValidator.java,v 1.2 2008/03/20 16:01:31 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivityExitDef;

/**
 * model for validating the exit activity
 */
public class AeActivityExitValidator extends AeActivityValidator
{

   /**
    * ctor 
    * @param aDef
    */
   public AeActivityExitValidator(AeActivityExitDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if ( getProcessDef().isAbstractProcess() )
      {
         getReporter().reportProblem( BPEL_TERM_NOT_ALLOWED_CODE, ERROR_TERM_NOT_ALLOWED, null, getDefinition());
      }
   }
}
 