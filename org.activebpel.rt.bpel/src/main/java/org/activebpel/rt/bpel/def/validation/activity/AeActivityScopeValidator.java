//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityScopeValidator.java,v 1.6 2006/10/03 19:35:13 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;

/**
 * validator provides validation for the scope activity
 */
public abstract class AeActivityScopeValidator extends AeBaseScopeValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivityScopeValidator(AeActivityScopeDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if (getDef().isIsolated())
      {
         validateIsolatedScope();
      }
   }

   /**
    * Executes the validation code for an isolated scope 
    */
   protected abstract void validateIsolatedScope();
   
   /**
    * Getter for the scope def
    */
   protected AeActivityScopeDef getDef()
   {
      return (AeActivityScopeDef) getDefinition();
   }
}
 