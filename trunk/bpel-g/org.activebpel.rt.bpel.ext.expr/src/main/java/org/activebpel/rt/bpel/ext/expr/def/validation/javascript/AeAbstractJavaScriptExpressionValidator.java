//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/validation/javascript/AeAbstractJavaScriptExpressionValidator.java,v 1.3 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.def.validation.javascript;

import org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator;
import org.activebpel.rt.bpel.ext.expr.AeMessages;
import org.activebpel.rt.bpel.ext.expr.def.javascript.AeAbstractJavaScriptParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;

/**
 * This is a base class for JavaScript expression validators.
 */
public abstract class AeAbstractJavaScriptExpressionValidator extends AeAbstractExpressionValidator
{
   /**
    * Default c'tor.
    */
   public AeAbstractJavaScriptExpressionValidator()
   {
      super();
   }
   
   /**
    * Overrides method to do additional validation on the javascript parse tree for join conditions.  This
    * method delegates to the superclass to do most of the validation, but then does a little bit extra.
    * 
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#doJoinConditionValidation(org.activebpel.rt.expr.def.IAeExpressionParseResult, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   protected void doJoinConditionValidation(IAeExpressionParseResult aParseResult,
         AeExpressionValidationResult aValidationResult, IAeExpressionValidationContext aContext)
   {
      super.doJoinConditionValidation(aParseResult, aValidationResult, aContext);
      
      validateJSNodeForJoinCondition(((AeAbstractJavaScriptParseResult) aParseResult).getRootNode(), aParseResult,
            aValidationResult);
   }

   /**
    * Validates that the given node is ok for a join condition.  This specifically looks for 
    * literal nodes that are not contained within function calls.
    * 
    * @param aNode
    * @param aParseResult
    * @param aValidationResult
    */
   protected void validateJSNodeForJoinCondition(Node aNode, IAeExpressionParseResult aParseResult,
         AeExpressionValidationResult aValidationResult)
   {
      if (aNode.getType() == Token.CALL)
      {
         return;
      }
      else if (aNode.getType() == Token.STRING)
      {
         addError(aValidationResult,
               AeMessages.getString("AeJavaScriptExpressionValidator.INVALID_LITERAL_IN_JOINCONDITION_ERROR"),  //$NON-NLS-1$
               new Object [] { aNode.getString(), aParseResult.getExpression() });
         return;
      }
      else if (aNode.getType() == Token.NUMBER)
      {
         addError(aValidationResult,
               AeMessages.getString("AeJavaScriptExpressionValidator.INVALID_LITERAL_IN_JOINCONDITION_ERROR"),  //$NON-NLS-1$
               new Object [] { new Double(aNode.getDouble()), aParseResult.getExpression() });
         return;
      }

      Node child = aNode.getFirstChild();
      while ( child != null)
      {
         validateJSNodeForJoinCondition(child, aParseResult, aValidationResult);
         child = child.getNext();
      }
   }

   /**
    * Overrides method to supply a javascript version of the expression parser.
    * 
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected abstract IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext);

   /**
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#handleNoFunctionsInJoinCondition(org.activebpel.rt.expr.def.IAeExpressionParseResult, org.activebpel.rt.expr.validation.AeExpressionValidationResult)
    */
   protected void handleNoFunctionsInJoinCondition(IAeExpressionParseResult aParseResult, AeExpressionValidationResult aValidationResult)
   {
      // Do nothing - you can have a valid boolean expression in Javascript without function calls.
   }
}
