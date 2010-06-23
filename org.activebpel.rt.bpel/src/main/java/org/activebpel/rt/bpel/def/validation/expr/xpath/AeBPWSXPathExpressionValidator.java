// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/xpath/AeBPWSXPathExpressionValidator.java,v 1.4 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.expr.xpath;

import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.expr.def.AeScriptVarDef;
import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;

/**
 * Implements an expression validator for the XPath 1.0 expression language.  This is the default languge used
 * for BPEL 1.1 (when no expression language is specified).
 */
public class AeBPWSXPathExpressionValidator extends AeAbstractXPathExpressionValidator
{
   /**
    * Default c'tor.
    */
   public AeBPWSXPathExpressionValidator()
   {
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#doCommonExpressionValidation(org.activebpel.rt.expr.def.IAeExpressionParseResult, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   protected void doCommonExpressionValidation(IAeExpressionParseResult aParseResult, AeExpressionValidationResult aValidationResult, IAeExpressionValidationContext aContext)
   {
      super.doCommonExpressionValidation(aParseResult, aValidationResult, aContext);
      
      checkExpressionVariableReferences(aParseResult, aValidationResult);
   }

   /**
    * Checks for variable references in the expression.
    * 
    * @param aParseResult
    * @param aValidationResult
    */
   protected void checkExpressionVariableReferences(IAeExpressionParseResult aParseResult, AeExpressionValidationResult aValidationResult)
   {
      Set variables = aParseResult.getVariableReferences();
      for (Iterator iter = variables.iterator(); iter.hasNext(); )
      {
         AeScriptVarDef varDef = (AeScriptVarDef) iter.next();
         addError(aValidationResult, AeMessages.getString("AeBPEL4WSXPathExpressionValidator.ERROR_RESOLVING_XPATH_VARIABLE"), //$NON-NLS-1$
               new Object[] { varDef.getName() });
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#handleNoFunctionsInJoinCondition(org.activebpel.rt.expr.def.IAeExpressionParseResult, org.activebpel.rt.expr.validation.AeExpressionValidationResult)
    */
   protected void handleNoFunctionsInJoinCondition(IAeExpressionParseResult aParseResult, AeExpressionValidationResult aValidationResult)
   {
      addError(aValidationResult,
            AeMessages.getString("AeBPWSXPathExpressionValidator.INVALID_JOIN_CONDITION1_ERROR"),  //$NON-NLS-1$
            new Object[] { aParseResult.getExpression() });
   }
}
