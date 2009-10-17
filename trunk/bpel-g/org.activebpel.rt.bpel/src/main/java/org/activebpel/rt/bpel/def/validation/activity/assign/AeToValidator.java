//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/assign/AeToValidator.java,v 1.9 2008/03/20 16:01:32 dvilaverde Exp $
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
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationDefs;
import org.activebpel.rt.bpel.def.validation.activity.scope.AePartnerLinkValidator;
import org.activebpel.rt.bpel.def.validation.expressions.AeBaseExpressionValidator;
import org.activebpel.rt.util.AeUtil;

/**
 * model provides validation for the to def
 */
public class AeToValidator extends AeBaseExpressionValidator
{
   /** adapter interface used to validate the query */
   private IAeExpressionDef mDefAdapter;

   /**
    * ctor
    * @param aDef
    */
   public AeToValidator(AeToDef aDef)
   {
      super((IAeExpressionDef) aDef);
   }

   /**
    * Getter for the def
    */
   protected AeToDef getDef()
   {
      return (AeToDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.expressions.AeBaseExpressionValidator#getExpressionDef()
    */
   public IAeExpressionDef getExpressionDef()
   {
      if (mDefAdapter == null && AeUtil.notNullOrEmpty(getDef().getQuery()))
      {
         mDefAdapter = new IAeExpressionDef()
         {
            /**
             * @see org.activebpel.rt.bpel.def.IAeExpressionDef#getExpression()
             */
            public String getExpression()
            {
               return getDef().getQuery();
            }

            /**
             * @see org.activebpel.rt.bpel.def.IAeExpressionDef#getExpressionLanguage()
             */
            public String getExpressionLanguage()
            {
               // TODO (EPW) should create a different path for executing/analyzing/validating queries to be used in the future.
               return getValidationContext().getExpressionLanguageFactory().getBpelDefaultLanguage(getBpelNamespace());
            }
            
            /**
             * @see org.activebpel.rt.bpel.def.IAeExpressionDef#getBpelNamespace()
             */
            public String getBpelNamespace()
            {
               return AeDefUtil.getProcessDef(getDef()).getNamespace();
            }

            /**
             * @see org.activebpel.rt.bpel.def.IAeExpressionDef#setExpression(java.lang.String)
             */
            public void setExpression(String aExpression)
            {
               // no-op
            }
            
            /**
             * @see org.activebpel.rt.bpel.def.IAeExpressionDef#setExpressionLanguage(java.lang.String)
             */
            public void setExpressionLanguage(String aLanguageURI)
            {
               // no-op
            }
         };
      }
      return mDefAdapter;
   }

   /**
    * Validates:
    * 1. to conforms to one of the prescribed variants
    * 2. if variable is provided, validate that it can be resolved and record usage (message part, message part query, element query, property)
    * @see org.activebpel.rt.bpel.def.validation.expressions.AeBaseExpressionValidator#validate()
    */
   public void validate()
   {
      if (getDef().getStrategyKey() == null)
      {
         AeVariableDef varDef = AeDefUtil.getVariableByName(getDef().getVariable(), getDef());

         // the most common error will be an unresolvable variable, check for that and give custom message
         if (AeUtil.notNullOrEmpty(getDef().getVariable()) && varDef == null)
         {
            getReporter().reportProblem(BPEL_TO_VAR_NOT_FOUND_CODE,IAeValidationDefs.ERROR_VAR_NOT_FOUND, new String[] {getDef().getVariable()}, getDefinition());
         }
         else
         {
            getReporter().reportProblem(BPEL_UNSUPPORTED_COPYOP_TO_CODE,IAeValidationDefs.ERROR_UNSUPPORTED_COPYOP_TO, null, getDefinition());
         }
         return;
      }

      if (AeUtil.notNullOrEmpty(getDef().getVariable()))
      {
         AeVariableValidator variableValidator = getVariableValidator(getDef().getVariable(), null, true, AeVariableValidator.VARIABLE_WRITE_TO_SPEC);
         if (variableValidator == null)
         {
            return;
         }

         // handles recording the usage based on the pattern of part, query, property
         variableValidator.addUsage(this, getDef().getPart(), getDef().getQuery(), getDef().getProperty());
      }
      // record partner link usage
      else if (AeUtil.notNullOrEmpty(getDef().getPartnerLink()))
      {
         AePartnerLinkValidator plink = getPartnerLinkValidator(getDef().getPartnerLink(), true);
         if (plink != null)
         {
            plink.addReference();
            
            if (AeUtil.isNullOrEmpty(plink.getDef().getPartnerRole()))
            {
               Object[] args = {plink.getName()};
               getReporter().reportProblem(BPEL_PLINK_ASSIGN_TO_CODE, IAeValidationDefs.ERROR_PLINK_ASSIGN_TO, args, getDefinition());
            }
         }
      }

      // call to super will validate the query
      super.validate();
   }
}
