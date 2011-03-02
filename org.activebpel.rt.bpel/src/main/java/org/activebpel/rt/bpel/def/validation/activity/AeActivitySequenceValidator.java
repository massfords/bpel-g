//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivitySequenceValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef;

/**
 * model provides validation for the sequence activity
 */
public class AeActivitySequenceValidator extends AeActivityValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivitySequenceValidator(AeActivitySequenceDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the def
    */
   protected AeActivitySequenceDef getDef()
   {
      return (AeActivitySequenceDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if (getChildren(AeActivityValidator.class).size() == 0)
      {
         getReporter().reportProblem( BPEL_SEQ_ACTIVITY_MISSING_CODE, ERROR_ACTIVITY_MISSING,
               new String[] { getDef().getLocationPath() },
               getDef() );
         
      }
   }
}
 