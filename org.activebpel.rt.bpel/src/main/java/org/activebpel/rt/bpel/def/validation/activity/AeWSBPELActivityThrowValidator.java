//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeWSBPELActivityThrowValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
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

/**
 * Validates the throw activity for WS-BPEL 2.0
 */
public class AeWSBPELActivityThrowValidator extends AeActivityThrowValidator
{

   /**
    * Ctor accepts def
    * @param aDef
    */
   public AeWSBPELActivityThrowValidator(AeActivityThrowDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityThrowValidator#validateVariable()
    */
   protected void validateVariable()
   {
      if (getVariable().getDef().isType())
      {
         getReporter().reportProblem( WSBPEL_INVALID_FAULT_TYPE_CODE, 
               ERROR_FAULT_TYPE,
               new String[] { getVariable().getDef().getName() },
               getDef() );
      }
   }
}
 