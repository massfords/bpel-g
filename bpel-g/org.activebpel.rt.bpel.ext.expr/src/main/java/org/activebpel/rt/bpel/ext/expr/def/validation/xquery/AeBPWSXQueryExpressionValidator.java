// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/validation/xquery/AeBPWSXQueryExpressionValidator.java,v 1.4 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.validation.xquery;

import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.expr.def.AeScriptVarDef;
import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;

/**
 * A BPEL 1.1 impl of a XQuery expression validator.
 */
public class AeBPWSXQueryExpressionValidator extends AeAbstractXQueryExpressionValidator
{
   /**
    * Default c'tor.
    */
   public AeBPWSXQueryExpressionValidator()
   {
      super();
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
         addError(aValidationResult, AeMessages.getString("AeBPWSXQueryExpressionValidator.ERROR_RESOLVING_XQUERY_VARIABLE"), //$NON-NLS-1$
               new Object[] { varDef.getName() });
      }
   }
}
