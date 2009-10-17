//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/validation/xquery/AeAbstractXQueryExpressionValidator.java,v 1.3 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.def.validation.xquery;

import java.util.Iterator;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.FunctionCall;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.BooleanValue;

import org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator;
import org.activebpel.rt.bpel.ext.expr.AeMessages;
import org.activebpel.rt.bpel.ext.expr.def.xquery.AeBPWSXQueryExpressionParser;
import org.activebpel.rt.bpel.ext.expr.def.xquery.AeAbstractXQueryParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;

/**
 * Implements an expression validator for the XQuery 1.0 expression language.
 */
public abstract class AeAbstractXQueryExpressionValidator extends AeAbstractExpressionValidator
{
   /**
    * Constructs an xquery expression validator.
    */
   public AeAbstractXQueryExpressionValidator()
   {
   }

   /**
    * Overrides method to do additional validation on the xquery parse tree for join conditions.  This
    * method delegates to the superclass to do most of the validation, but then does a little bit extra.
    * 
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#doJoinConditionValidation(org.activebpel.rt.expr.def.IAeExpressionParseResult, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   protected void doJoinConditionValidation(IAeExpressionParseResult aParseResult,
         AeExpressionValidationResult aValidationResult, IAeExpressionValidationContext aContext)
   {
      super.doJoinConditionValidation(aParseResult, aValidationResult, aContext);
      
      Expression node = ((AeAbstractXQueryParseResult) aParseResult).getXQueryExpression();
      validateXQueryNode(node, aParseResult, aValidationResult);
   }

   /**
    * Validates that the given node is ok for a join condition.  This specifically looks for 
    * literal nodes that are not contained within function calls.
    * 
    * @param aNode
    * @param aParseResult
    * @param aValidationResult
    */
   protected void validateXQueryNode(Expression aNode, IAeExpressionParseResult aParseResult, 
         AeExpressionValidationResult aValidationResult)
   {
      if (aNode instanceof FunctionCall)
      {
         return;
      }
      else if (aNode instanceof AtomicValue && !(aNode instanceof BooleanValue))
      {
         AtomicValue value = (AtomicValue) aNode;
         addError(aValidationResult, AeMessages.getString("AeXQueryExpressionValidator.INVALID_LITERAL_IN_JOINCONDITION_ERROR"),  //$NON-NLS-1$
               new Object [] { value.getStringValue(), aParseResult.getExpression() });
         return;
      }

      // Now process all of the children.
      for (Iterator iter = aNode.iterateSubExpressions(); iter.hasNext(); )
      {
         Expression child = (Expression) iter.next();
         validateXQueryNode(child, aParseResult, aValidationResult);
      }
   }

   /**
    * Overrides method to supply the xpath expression parser.
    * 
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeBPWSXQueryExpressionParser(aContext);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#handleNoFunctionsInJoinCondition(org.activebpel.rt.expr.def.IAeExpressionParseResult, org.activebpel.rt.expr.validation.AeExpressionValidationResult)
    */
   protected void handleNoFunctionsInJoinCondition(IAeExpressionParseResult aParseResult, AeExpressionValidationResult aValidationResult)
   {
      // Do nothing, you can have a valid boolean XQuery expression without using functions.
   }
}
