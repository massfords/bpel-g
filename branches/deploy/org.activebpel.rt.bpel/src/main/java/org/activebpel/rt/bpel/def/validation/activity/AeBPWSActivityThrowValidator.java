//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeBPWSActivityThrowValidator.java,v 1.3 2008/03/20 16:01:31 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivityThrowDef;

public class AeBPWSActivityThrowValidator extends AeActivityThrowValidator
{

   /**
    * Ctor accepts def
    * @param aDef
    */
   public AeBPWSActivityThrowValidator(AeActivityThrowDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityThrowValidator#validateVariable()
    */
   protected void validateVariable()
   {
      if (!getVariable().getDef().isMessageType())
      {
         getReporter().reportProblem( BPWS_FAULT_MESSAGETYPE_REQUIRED_CODE,
                              ERROR_FAULT_MESSAGETYPE_REQUIRED,
                              new String[] { getVariable().getDef().getName() },
                              getDef() );
      }
   }
}
 