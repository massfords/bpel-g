//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityValidateValidator.java,v 1.6 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import java.util.Iterator;

import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;

/**
 * model provides validation for the validate activity
 */
public class AeActivityValidateValidator extends AeActivityValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivityValidateValidator(AeActivityValidateDef aDef)
   {
      super(aDef);
   }

   /**
    * Getter for the def
    */
   protected AeActivityValidateDef getDef()
   {
      return (AeActivityValidateDef) getDefinition();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if (getDef().getVariablesCount() == 0)
      {
         getReporter().reportProblem(BPEL_EMPTY_VALIDATE_CODE, ERROR_EMPTY_VALIDATE, null, getDefinition());
      }
      else
      {
         // TODO (MF) Add test case to make sure variables are in scope
         // Validate that all of the variables referenced can be resolved

         for (Iterator iter=getDef().getVariables(); iter.hasNext(); )
            getVariableValidator((String)iter.next(), "variable", true, AeVariableValidator.VARIABLE_READ_VALIDATION);  //$NON-NLS-1$
      }
      
   }
} 