// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/xpath/AeWSBPELXPathExpressionValidator.java,v 1.5 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.expr.xpath;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.expr.xpath.AeWSBPELXPathExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;

/**
 * Implements an expression validator for the XPath 1.0 expression language.  This is the default languge used
 * for BPEL 2.0 (when no expression language is specified).
 */
public class AeWSBPELXPathExpressionValidator extends AeAbstractXPathExpressionValidator
{
   /**
    * Default c'tor.
    */
   public AeWSBPELXPathExpressionValidator()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeWSBPELXPathExpressionParser(aContext);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#handleNoFunctionsInJoinCondition(org.activebpel.rt.expr.def.IAeExpressionParseResult, org.activebpel.rt.expr.validation.AeExpressionValidationResult)
    */
   protected void handleNoFunctionsInJoinCondition(IAeExpressionParseResult aParseResult, AeExpressionValidationResult aValidationResult)
   {
      if (aParseResult.getVarNames().size() == 0)
      {
         addError(aValidationResult,
               AeMessages.getString("AeWSBPELXPathExpressionValidator.InvalidJoinConditionError"),  //$NON-NLS-1$
               new Object[] { aParseResult.getExpression() });
      }
   }
}
