//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivitySuspendValidator.java,v 1.3 2007/09/28 21:45:39 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;

/**
 * model provides validation for the suspend activity
 */
public class AeActivitySuspendValidator extends AeActivityValidator
{
   /** variable referenced by the variable */
   private AeVariableValidator mVariable;

   /**
    * ctor
    * @param aDef
    */
   public AeActivitySuspendValidator(AeActivitySuspendDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the def
    */
   protected AeActivitySuspendDef getDef()
   {
      return (AeActivitySuspendDef) getDefinition();
   }
   
   /**
    * Getter for the variable
    */
   public AeVariableValidator getVariable()
   {
      return mVariable;
   }

   /**
    * Validates:
    * 1. variable reference if provided
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      // Validate optional variable.
      if ( !isUndefined( getDef().getVariable()) )
      {
         mVariable = getVariableValidator( getDef().getVariable(), AeActivitySuspendDef.TAG_VARIABLE, true, AeVariableValidator.VARIABLE_READ_OTHER );
      }
   }
}
 