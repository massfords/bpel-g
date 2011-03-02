//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeEventHandlersValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for the event handlers def
 */
public class AeEventHandlersValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeEventHandlersValidator(AeEventHandlersDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Gets the event handlers def.
    */
   protected AeEventHandlersDef getDef()
   {
      return (AeEventHandlersDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      if (!getDef().hasEventHandler())
         getReporter().reportProblem( BPEL_EMPTY_EVENT_HANDLER_CODE,
                                 ERROR_EMPTY_EVENT_HANDLER, 
                                 new String[]{ getDef().getLocationPath() }, 
                                 getDefinition() );

      super.validate();
   }
}
 