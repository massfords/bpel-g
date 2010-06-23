//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/assign/AeFromValidator.java,v 1.11 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.assign; 

import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationDefs;
import org.activebpel.rt.bpel.def.validation.activity.scope.AePartnerLinkValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeBaseExpressionValidator;
import org.activebpel.rt.util.AeUtil;

/**
 * model provides validation for the from part of a copy operaiton
 */
public class AeFromValidator extends AeBaseExpressionValidator
{
   /** variable referenced by the from */
   private AeVariableValidator mVariable;
   
   /**
    * ctor
    * @param aDef
    */
   public AeFromValidator(AeFromDef aDef)
   {
      super((IAeExpressionDef)aDef);
   }
   
   /**
    * Getter for the def
    */
   protected AeFromDef getDef()
   {
      return (AeFromDef) getDefinition();
   }
   
   /**
    * Getter for the variable
    */
   protected AeVariableValidator getVariable()
   {
      return mVariable;
   }
   
   /**
    * Setter for the variable
    * @param aVariable
    */
   protected void setVariable(AeVariableValidator aVariable)
   {
      mVariable = aVariable;
   }

   /**
    * Validates:
    * 1. from conforms to one of the prescribed variants
    * 2. if variable referenced, record variable usage (either message part, message part query, element query, property)
    * 3. if plink referenced, validate that it can be resolved and provides the role we're selecting
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      if (getDef().getStrategyKey() == null && !getDef().isOpaque())
      {
         AeVariableDef varDef = AeDefUtil.getVariableByName(getDef().getVariable(), getDef());

         // the most common error will be an unresolvable variable, check for that and give custom message
         if (AeUtil.notNullOrEmpty(getDef().getVariable()) && varDef == null)
         {
            getReporter().reportProblem(BPEL_FROM_VAR_NOT_FOUND_CODE, IAeValidationDefs.ERROR_VAR_NOT_FOUND, new String[] {getDef().getVariable()}, getDefinition());
         }
         else
         {
            getReporter().reportProblem(BPEL_UNSUPPORTED_COPYOP_FROM_CODE, IAeValidationDefs.ERROR_UNSUPPORTED_COPYOP_FROM, null, getDefinition());
         }
         return;
      }
      
      if ( AeUtil.notNullOrEmpty(getDef().getVariable()) )
      {
         setVariable(getVariableValidator(getDef().getVariable(), null, true, AeVariableValidator.VARIABLE_READ_FROM_SPEC));
         if (getVariable() == null)
         {
            return;
         }
         
         // handles recording the usage based on the pattern of part, query, property
         getVariable().addUsage(this, getDef().getPart(), getDef().getQuery(), getDef().getProperty());
      }
      else if (AeUtil.notNullOrEmpty(getDef().getPartnerLink()))
      {
         AePartnerLinkValidator plinkModel = getPartnerLinkValidator(getDef().getPartnerLink(), true);
         if (plinkModel != null)
         {
            plinkModel.addReference();
            if (getDef().isPartnerRole() && AeUtil.isNullOrEmpty(plinkModel.getDef().getPartnerRole())) 
            {
               Object[] args = {plinkModel.getName()};
               getReporter().reportProblem( BPEL_PLINK_ASSIGN_FROM_PARTNERROLE_CODE, IAeValidationDefs.ERROR_PLINK_ASSIGN_FROM_PARTNERROE, args, getDefinition());
            }
            else if (getDef().isMyRole() && AeUtil.isNullOrEmpty(plinkModel.getDef().getMyRole())) 
            {
               Object[] args = {plinkModel.getName()};
               getReporter().reportProblem(BPEL_PLINK_ASSIGN_FROM_MYROLE_CODE,IAeValidationDefs.ERROR_PLINK_ASSIGN_FROM_MYROE, args, getDefinition());
            }
         }
      }
      
      // Fix for Defect #357 - add check for opaque in non-abstract process.
      // 
      if ( getDef().isOpaque() && !getProcessDef().isAbstractProcess())
         getReporter().reportProblem( BPEL_FROM_OPAQUE_ACTIVITY_NOT_ALLOWED_CODE, ERROR_OPAQUE_NOT_ALLOWED, new String[0], getDefinition() );
      
      // super will validate the expression if present
      super.validate();
   }
}
 