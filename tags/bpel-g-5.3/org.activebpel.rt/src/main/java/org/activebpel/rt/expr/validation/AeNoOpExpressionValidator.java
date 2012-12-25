//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/validation/AeNoOpExpressionValidator.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.validation;

import org.activebpel.rt.AeException;

/**
 * A no-op expression validator.  This implementation essentially returns "true" for any expression
 * passed to it.
 */
public class AeNoOpExpressionValidator implements IAeExpressionValidator
{
   /**
    * Overrides method to provide a no-op implementation.  This class can be used as the expression
    * language validator if the language can not be statically validated.  Typically, however, this
    * class will be used during development of support for a particular expression language before
    * a real expression validator implementation has been written.
    * 
    * @see org.activebpel.rt.expr.validation.IAeExpressionValidator#validateExpression(org.activebpel.rt.expr.validation.IAeExpressionValidationContext, java.lang.String)
    */
   public IAeExpressionValidationResult validateExpression(IAeExpressionValidationContext aContext,
         String aExpression) throws AeException
   {
      return new AeExpressionValidationResult();
   }

   /**
    * @see org.activebpel.rt.expr.validation.IAeExpressionValidator#validateBooleanExpression(org.activebpel.rt.expr.validation.IAeExpressionValidationContext, java.lang.String)
    */
   public IAeExpressionValidationResult validateBooleanExpression(IAeExpressionValidationContext aContext, String aExpression)
         throws AeException
   {
      return validateExpression(aContext, aExpression);
   }

   /**
    * @see org.activebpel.rt.expr.validation.IAeExpressionValidator#validateDeadlineExpression(org.activebpel.rt.expr.validation.IAeExpressionValidationContext, java.lang.String)
    */
   public IAeExpressionValidationResult validateDeadlineExpression(IAeExpressionValidationContext aContext, String aExpression)
         throws AeException
   {
      return validateExpression(aContext, aExpression);
   }

   /**
    * @see org.activebpel.rt.expr.validation.IAeExpressionValidator#validateDurationExpression(org.activebpel.rt.expr.validation.IAeExpressionValidationContext, java.lang.String)
    */
   public IAeExpressionValidationResult validateDurationExpression(IAeExpressionValidationContext aContext, String aExpression)
         throws AeException
   {
      return validateExpression(aContext, aExpression);
   }

   /**
    * @see org.activebpel.rt.expr.validation.IAeExpressionValidator#validateJoinConditionExpression(org.activebpel.rt.expr.validation.IAeExpressionValidationContext, java.lang.String)
    */
   public IAeExpressionValidationResult validateJoinConditionExpression(IAeExpressionValidationContext aContext, String aExpression)
         throws AeException
   {
      return validateExpression(aContext, aExpression);
   }

   /**
    * @see org.activebpel.rt.expr.validation.IAeExpressionValidator#validateUnsignedIntExpression(org.activebpel.rt.expr.validation.IAeExpressionValidationContext, java.lang.String)
    */
   public IAeExpressionValidationResult validateUnsignedIntExpression(IAeExpressionValidationContext aContext, String aExpression) throws AeException
   {
      return validateExpression(aContext, aExpression);
   }
}
