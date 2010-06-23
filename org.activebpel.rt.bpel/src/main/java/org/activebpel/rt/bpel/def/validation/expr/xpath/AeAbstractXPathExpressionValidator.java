//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.expr.xpath;

import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.expr.xpath.AeAbstractXPathParseResult;
import org.activebpel.rt.bpel.def.expr.xpath.AeBPWSXPathExpressionParser;
import org.activebpel.rt.bpel.def.expr.xpath.ast.visitors.AeXPathInvalidLiteralNodeVisitor;
import org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator;
import org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode;
import org.activebpel.rt.expr.def.IAeExpressionParseResult;
import org.activebpel.rt.expr.def.IAeExpressionParser;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;

/**
 * Implements an expression validator for the XPath 1.0 expression language.  This class must be
 * extended by the BPEL 1.1 and BPEL 2.0 specific implementations.
 */
public abstract class AeAbstractXPathExpressionValidator extends AeAbstractExpressionValidator
{
   /**
    * Constructs an xpath expression validator.
    */
   public AeAbstractXPathExpressionValidator()
   {
   }

   /**
    * Overrides method to do additional validation on the xpath parse tree for join conditions.  This
    * method delegates to the superclass to do most of the validation, but then does a little bit extra.
    * 
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#doJoinConditionValidation(org.activebpel.rt.expr.def.IAeExpressionParseResult, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   protected void doJoinConditionValidation(IAeExpressionParseResult aParseResult,
         AeExpressionValidationResult aValidationResult, IAeExpressionValidationContext aContext)
   {
      super.doJoinConditionValidation(aParseResult, aValidationResult, aContext);

      validateXPathASTForJoinCondition(aParseResult, aValidationResult);
   }
   
   /**
    * Validates the XPath parse result (using the xpath-only AST structure) for additional problems
    * that might exist in the join condition, specifically invalid literal usage.
    * 
    * @param aParseResult
    * @param aValidationResult
    */
   protected void validateXPathASTForJoinCondition(IAeExpressionParseResult aParseResult,
         AeExpressionValidationResult aValidationResult)
   {
      AeXPathInvalidLiteralNodeVisitor visitor = new AeXPathInvalidLiteralNodeVisitor();
      ((AeAbstractXPathParseResult) aParseResult).getXPathAST().visitAll(visitor);
      List invalidLiterals = visitor.getLiterals();
      
      for (Iterator iter = invalidLiterals.iterator(); iter.hasNext(); )
      {
         AeXPathLiteralNode literal = (AeXPathLiteralNode) iter.next();
         addError(aValidationResult,
               AeMessages.getString("AeXPathExpressionValidator.INVALID_LITERAL_IN_JOINCONDITION_ERROR"),  //$NON-NLS-1$
               new Object [] { literal.getValue(), aParseResult.getExpression() });
         
      }
   }

   /**
    * Overrides method to supply the xpath expression parser.
    * 
    * @see org.activebpel.rt.bpel.def.validation.expr.AeAbstractExpressionValidator#createExpressionParser(org.activebpel.rt.expr.def.IAeExpressionParserContext)
    */
   protected IAeExpressionParser createExpressionParser(IAeExpressionParserContext aContext)
   {
      return new AeBPWSXPathExpressionParser(aContext);
   }
}
