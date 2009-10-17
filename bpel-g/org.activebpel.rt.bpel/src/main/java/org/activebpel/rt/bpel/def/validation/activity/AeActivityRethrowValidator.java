//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityRethrowValidator.java,v 1.3 2008/03/20 16:01:31 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.IAeCatchParentDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef;

/**
 * model provides validation for the rethrow activity
 */
public class AeActivityRethrowValidator extends AeActivityValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivityRethrowValidator(AeActivityRethrowDef aDef)
   {
      super(aDef);
   }

   /**
    * Validates:
    * 1. we're enclosed within a catch or catchAll
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      // TODO (MF) add an interface for AeCatchDef and AeCatchAllDef instead of checking for the <faultHandlers>
      if (!enclosedWithinDef(IAeCatchParentDef.class))
      {
         getReporter().reportProblem( BPEL_MISPLACED_RETHROW_CODE, ERROR_MISPLACED_RETHROW, null, getDefinition() );
      }
   }
}
 