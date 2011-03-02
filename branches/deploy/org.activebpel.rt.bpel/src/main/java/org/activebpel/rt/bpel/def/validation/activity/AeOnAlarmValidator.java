//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeOnAlarmValidator.java,v 1.3 2008/03/20 16:01:31 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.expressions.IAeExpressionModelValidator;

/**
 * provides validation for the onAlarm part of an event handler or pick
 */
public class AeOnAlarmValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeOnAlarmValidator(AeOnAlarmDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.expressions.AeBaseExpressionValidator#validate()
    */
   public void validate()
   {
      super.validate();

      validateAlarmChildren();
   }

   /**
    * Validates the OnAlarm's children.  In this case, the OnAlarm must have a single child that
    * implements the IAeExpressionModel interface.
    */
   protected void validateAlarmChildren()
   {
      IAeExpressionModelValidator child = (IAeExpressionModelValidator) getChild(IAeExpressionModelValidator.class);
      validateAlarmChild(child);
   }

   /**
    * Validates that the child is not null and contains a non-empty expression. The actual expression (if present)
    * is validated elsewhere.
    * @param aChild
    */
   protected void validateAlarmChild(IAeExpressionModelValidator aChild)
   {
      if (isNullOrEmpty(aChild))
      {
         getReporter().reportProblem( BPEL_ALARM_FIELD_MISSING_CODE,
                                       ERROR_FIELD_MISSING,
                                       new String[] { AeOnAlarmDef.TAG_ON_ALARM },
                                       getDefinition() );
      }
   }

   /**
    * Getter for the def
    */
   protected AeOnAlarmDef getDef()
   {
      return (AeOnAlarmDef) getDefinition();
   }
}
 