//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityPickValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;

/**
 * model provides validation for pick model
 */
public class AeActivityPickValidator extends AeActivityValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivityPickValidator(AeActivityPickDef aDef)
   {
      super(aDef);
   }

   /**
    * Validates:
    * 1. if create instance, then no alarm allowed
    * 2. if create instance, record on process
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      // there must be at least 1 onMessage
      if (getChildren(AeOnMessageValidator.class).size() == 0)
      {
         getReporter().reportProblem( BPEL_NO_ONMESSAGE_CODE, ERROR_NO_ONMESSAGE, null, getDefinition() );
      }
      
      // no onAlarm if its create instance
      if ( getDef().isCreateInstance() && getChildren(AeOnAlarmValidator.class).size() > 0 )
      {
         getReporter().reportProblem( BPEL_ALARM_ON_CREATEINSTANCE_CODE, ERROR_ALARM_ON_CREATEINSTANCE, null, getDefinition() );
      }
      
      if (getDef().isCreateInstance())
      {
         getProcessValidator().addCreateInstance(this);
      }
   }
   
   /**
    * Getter for the pick def
    */
   protected AeActivityPickDef getDef()
   {
      return (AeActivityPickDef) getDefinition();
   }
}
 