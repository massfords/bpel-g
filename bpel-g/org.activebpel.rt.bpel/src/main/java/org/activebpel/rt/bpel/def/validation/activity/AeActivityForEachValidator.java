//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityForEachValidator.java,v 1.7 2008/03/20 16:01:31 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.util.AeUtil;

/**
 * model for validating a forEach activity
 */
public class AeActivityForEachValidator extends AeActivityValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivityForEachValidator(AeActivityForEachDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if ( isUndefined(getDef().getCounterName()))
      {
         getReporter().reportProblem( BPEL_FOREACH_FIELD_MISSING_CODE, ERROR_FIELD_MISSING, new String[] { AeActivityForEachDef.TAG_FOREACH_COUNTERNAME }, getDefinition() );
      }
      else
      {
         // no need to check for null since if there was a counterName then we would have created a variable for the 
         // counter, unless one was already there, at which point we'll produce an error message since it's illegal to 
         // have an explicit def for an implicit variable
         AeVariableValidator variableModel = getCounterVariableModel();
         if (variableModel != null && !variableModel.getDef().isImplicit())
         {
            getReporter().reportProblem( BPEL_FOREACH_IMPLICIT_VARIABLE_EXPLICITLY_DEFINED_CODE, ERROR_IMPLICIT_VARIABLE_EXPLICITLY_DEFINED,
                  new String[] { getDefinition().getLocationPath(), variableModel.getDef().getName(), variableModel.getDef().getLocationPath() },
                  getDefinition() );
         }
      }

      if (isNullOrEmpty(getDef().getStartDef()))
      {
         getReporter().reportProblem( BPEL_EMPTY_START_EXPRESSION_CODE, ERROR_EMPTY_START_EXPRESSION, null, getDefinition() ); 
      }
      
      if (isNullOrEmpty(getDef().getFinalDef()))
      {
         getReporter().reportProblem( BPEL_EMPTY_FINAL_EXPRESSION_CODE, ERROR_EMPTY_FINAL_EXPRESSION, null, getDefinition() ); 
      }
      
      if (getDef().getCompletionCondition() != null && AeUtil.isNullOrEmpty(getDef().getCompletionCondition().getExpression()))
      {
         getReporter().reportProblem( BPEL_EMPTY_COMPLETION_CONDITION_EXPRESSION_CODE, ERROR_EMPTY_COMPLETION_CONDITION_EXPRESSION, null, getDefinition() ); 
      }
   }

   /**
    * Gets the implicit variable model in the child scope 
    */
   protected AeVariableValidator getCounterVariableModel()
   {
      AeActivityScopeValidator scope = (AeActivityScopeValidator) getChild(AeActivityScopeValidator.class);
      if (scope != null)
      {
         return scope.getVariableValidator(getDef().getCounterName(), AeVariableValidator.VARIABLE_IMPLICIT);
      }
      return null;
   }
   
   /**
    * Getter for the def
    */
   protected AeActivityForEachDef getDef()
   {
      return (AeActivityForEachDef) getDefinition();
   }

}
 