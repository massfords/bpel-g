//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeBaseLoopingActivityValidator.java,v 1.4 2008/03/20 16:01:31 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.validation.expressions.IAeExpressionModelValidator;

/**
 * Base class for validating looping activities like while and repeatUntil 
 */
public class AeBaseLoopingActivityValidator extends AeActivityValidator
{
   /**
    * Ctor
    * @param aDef
    */
   protected AeBaseLoopingActivityValidator(AeBaseDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();

      IAeExpressionModelValidator expression = (IAeExpressionModelValidator)getChild(IAeExpressionModelValidator.class);
      if (isNullOrEmpty(expression))
      {
         getReporter().reportProblem( BPEL_LOOP_FIELD_MISSING_CODE,
                                       ERROR_FIELD_MISSING, 
                                       new String[] { AeBaseDef.TAG_CONDITION }, 
                                       getDefinition() ); 
      }
   }
}
 