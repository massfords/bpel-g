//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeCompensationHandlerValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.IAeFCTHandlerDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for the compensationHandler def
 */
public class AeCompensationHandlerValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeCompensationHandlerValidator(AeCompensationHandlerDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the def
    */
   protected AeCompensationHandlerDef getDef()
   {
      return (AeCompensationHandlerDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      if ( getDef().getActivityDef() == null )
         getReporter().reportProblem( BPEL_COMP_EMPTY_CONTAINER_CODE,
                                       ERROR_EMPTY_CONTAINER, 
                                       new String[]{getDef().getLocationPath()}, 
                                       getDefinition() );
      
      // report an error if the compensation handler exists on a scope that is
      // the root scope for an FCT handler. These scopes are never reachable.
      if (getScopeParent() != null && getScopeParent().getParent() instanceof IAeFCTHandlerDef)
      {
         getReporter().reportProblem( BPEL_ROOT_SCOPE_FCT_HANDLER_CODE,
                                       ERROR_ROOT_SCOPE_FCT_HANDLER, 
                                       new String[]{}, 
                                       getDefinition() );
      }
   }

   /**
    * Returns the def for the compensation handler's scope activity
    */
   private AeBaseDef getScopeParent()
   {
      return getDef().getParent().getParent();
   }
}
 