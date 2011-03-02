//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/decision/AeElseValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.decision; 

import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for the else condition of an if or switch activity
 */
public class AeElseValidator extends AeBaseValidator
{
   /** value for the tag name, used for error reporting only ("otherwise" or "else") */
   private String mTagName;
   
   /**
    * ctor
    * @param aDef
    */
   public AeElseValidator(AeElseDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the def
    */
   protected AeElseDef getDef()
   {
      return (AeElseDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();

      // No empty otherwises allowed.
      //
      if ( getDef().getActivityDef() == null )
      {
         getReporter().reportProblem( BPEL_ELSE_ACTIVITY_MISSING_CODE,
               ERROR_ACTIVITY_MISSING,
               new String[] { getTagName() },
               getDef() );
      }
   }
   
   /**
    * Getter for the tag name
    */
   protected String getTagName()
   {
      return mTagName;
   }
   
   /**
    * Setter for the tag name
    * @param aTagName
    */
   public void setTagName(String aTagName)
   {
      mTagName = aTagName;
   }
}
 