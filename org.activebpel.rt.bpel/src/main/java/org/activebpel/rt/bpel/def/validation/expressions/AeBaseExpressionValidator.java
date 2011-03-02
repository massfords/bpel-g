//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expressions/AeBaseExpressionValidator.java,v 1.10 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.expressions; 

import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.util.AeVariableData;
import org.activebpel.rt.bpel.def.util.AeVariableProperty;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.expr.validation.AeExpressionValidationContext;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;
import org.activebpel.rt.expr.validation.IAeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;
import org.activebpel.rt.util.AeUtil;

/**
 * Base class for models that contain expressions that need to be validated.
 */
public abstract class AeBaseExpressionValidator extends AeBaseValidator implements IAeExpressionModelValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeBaseExpressionValidator(IAeExpressionDef aDef)
   {
      super((AeBaseDef) aDef);
   }
   
   /**
    * Ctor for subclasses
    * 
    * @param aDef
    */
   protected AeBaseExpressionValidator(AeBaseDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.expressions.IAeExpressionModelValidator#getExpressionDef()
    */
   public IAeExpressionDef getExpressionDef()
   {
      return (IAeExpressionDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();

      if (getExpressionDef() != null)
         validateExpression(getExpressionDef().getExpression(), getExpressionLanguage());
   }

   /**
    * Validates the given expression and language
    * @param aExpression
    * @param aExpressionLanguage
    */
   protected void validateExpression(String aExpression, String aExpressionLanguage)
   {
      if (AeUtil.isNullOrEmpty(aExpression))
         return;

      try
      {
         IAeExpressionValidator validator = getValidationContext().getExpressionLanguageFactory().createExpressionValidator(getBpelNamespace(), aExpressionLanguage);
         
         IAeFunctionValidatorFactory functionFactory = getValidationContext().getFunctionValidatorFactory();
         IAeExpressionValidationContext context = new AeExpressionValidationContext(getDefinition(), functionFactory, getBpelNamespace());
         IAeExpressionValidationResult vResult = validateExpression(aExpression, validator, context);
         processExpressionValidationResult(vResult);
      }
      catch (Throwable t)
      {
         getReporter().reportProblem(BPEL_INVALID_EXPRESSION_CODE, ERROR_INVALID_EXPRESSION,
               new String[] { getExpressionDef().getExpression(), t.getLocalizedMessage() }, getDefinition());
      }
   }

   /**
    * Gets the expression language to use when validating the given expression def.
    */
   protected String getExpressionLanguage()
   {
      return AeDefUtil.getExpressionLanguage(getExpressionDef(), getProcessDef());
   }

   /**
    * Process the expression validation result.  This amounts to extracting any errors or warnings from
    * the result object, as well as asking it for lists of AeVariableData and AeVariableProperty objects
    * in order to do various additional validation tasks.
    *
    * @param aResult
    */
   protected void processExpressionValidationResult(IAeExpressionValidationResult aResult)
   {
      // Process any info messages found by the validator.
      for ( Iterator iter = aResult.getInfoList().iterator() ; iter.hasNext() ; )
      {
         String info = (String) iter.next();
         getReporter().reportProblem(BPEL_EXPRESSION_INFO_CODE, info, null, getDefinition());
      }

      // Process any errors found by the validator.
      for ( Iterator iter = aResult.getErrors().iterator() ; iter.hasNext() ; )
      {
         String error = (String) iter.next();
         getReporter().reportProblem(BPEL_EXPRESSION_ERROR_CODE, error, null, getDefinition());
      }

      // Process any warnings found by the validator.
      for ( Iterator iter = aResult.getWarnings().iterator() ; iter.hasNext() ; )
      {
         String warning = (String) iter.next();
         getReporter().reportProblem(BPEL_EXPRESSION_WARNING_CODE, warning, null, getDefinition());
      }
      
      if (aResult.getParseResult() != null)
      {
         recordVariablesInExpression(aResult);
      }
   }

   /**
    * Resolves all of the variables found in the expression.
    * @param aResult
    */
   protected void recordVariablesInExpression(IAeExpressionValidationResult aResult)
   {
      // walk all of the variables found and add a reference
      for(Iterator it = aResult.getParseResult().getVarNames().iterator(); it.hasNext(); )
      {
         String name = (String) it.next();
         // Getting the variable will record the reference
         /*AeVariableValidator varModel =*/ getVariableValidator(name, null, true, AeVariableValidator.VARIABLE_READ_FROM_SPEC);
      }

      // Check variable data list.
      for (Iterator iter = aResult.getParseResult().getVarDataList().iterator(); iter.hasNext();)
      {
         AeVariableData varData = (AeVariableData) iter.next();
         AeVariableValidator varModel = getVariableValidator(varData.getVarName(), null, true, AeVariableValidator.VARIABLE_READ_FROM_SPEC);
         if (varModel != null)
         {
            if (varModel.getDef().isElement())
            {
               // There is a legacy issue here with getVariableData(). Our prev versions
               // allowed for a variant of getVariableData that selected an element variable
               // along with a query expression. 
               // In this format, the AeVariableData class will store the query as the
               // part name since it wasn't aware of this variation from the spec.
               varModel.addUsage(this, null, varData.getPart(), null);
            }
            else
            {
               varModel.addUsage(this, varData.getPart(), varData.getQuery(), null);
            }
            if (varModel.getDef().isMessageType() && AeUtil.isNullOrEmpty(varData.getPart()))
            {
               getReporter().reportProblem(BPEL_MISSING_EXPRESSION_PART_CODE,
                     AeMessages.getString("AeBaseExpressionModel.MissingPartInExpressionError"),  //$NON-NLS-1$
                     new Object[] { varModel.getDef().getName() }, 
                     getDefinition());
            }
         }
      }

      // Check variable property combos.
      for (Iterator iter = aResult.getParseResult().getVarPropertyList().iterator(); iter.hasNext();)
      {
         AeVariableProperty varProp = (AeVariableProperty) iter.next();
         AeVariableValidator varModel = getVariableValidator(varProp.getVarName(), null, true, AeVariableValidator.VARIABLE_READ_FROM_SPEC);
         if (varModel != null && varProp.getProperty() != null)
         {
            varModel.addUsage(this, null, null, varProp.getProperty());
         }
      }
}

   /**
    * Validates the expression. Subclasses will validate the specific type of the expression by calling different
    * methods on the validator like validateBoolean, validateDuration ...etc.
    * @param aExpression
    * @param aValidator
    * @param aContext
    * @throws AeException
    */
   protected IAeExpressionValidationResult validateExpression(String aExpression, IAeExpressionValidator aValidator, IAeExpressionValidationContext aContext) throws AeException
   {
      return aValidator.validateExpression(aContext, aExpression);
   }
}
 