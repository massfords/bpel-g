//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/validation/IAeExpressionValidator.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
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
 * This interface defines a common API for classes that validate expressions. The BPEL 1.0 default expression
 * language is XPath 1.0, so there will be at least one class that implements this interface
 * (AeXPathExpressionValidator).
 */
public interface IAeExpressionValidator
{
   /**
    * This method is called to validate a generic expression. It returns a
    * <code>IAeExpressionValidationResult</code> that will be interrogated for the result of the validation.
    * This method should throw an exception only for errors that it did not expect to find. It should,
    * however, internally handle the case where the expression syntax is invalid.
    * @param aContext The expression validation context
    * @param aExpression The expression to validate.
    * 
    * @throws AeException
    */
   public IAeExpressionValidationResult validateExpression(IAeExpressionValidationContext aContext,
         String aExpression) throws AeException;

   /**
    * This method is called to validate an expression that is expected to return a Boolean. The class
    * implementing this interface (which will be expression language specific) can then provide additional
    * logic (when possible) to determine if the expression is a valid boolean expression. It is expected that
    * some languages may not be able to do any type checking at validation (untyped or dynamically typed
    * languages, for example). Such languages should simply do generic expression validation in that case.
    * @param aContext The expression validation context
    * @param aExpression
    * 
    * @throws AeException
    */
   public IAeExpressionValidationResult validateBooleanExpression(IAeExpressionValidationContext aContext,
         String aExpression) throws AeException;

   /**
    * This method is called to validate an expression that is expected to return a deadline. The class
    * implementing this interface (which will be expression language specific) can then provide additional
    * logic (when possible) to determine if the expression is a valid deadline expression. It is expected that
    * some languages may not be able to do any type checking at validation (untyped or dynamically typed
    * languages, for example). Such languages should simply do generic expression validation in that case.
    * @param aContext The expression validation context
    * @param aExpression
    * 
    * @throws AeException
    */
   public IAeExpressionValidationResult validateDeadlineExpression(IAeExpressionValidationContext aContext,
         String aExpression) throws AeException;

   /**
    * This method is called to validate an expression that is expected to return a duration. The class
    * implementing this interface (which will be expression language specific) can then provide additional
    * logic (when possible) to determine if the expression is a valid duration expression. It is expected that
    * some languages may not be able to do any type checking at validation (untyped or dynamically typed
    * languages, for example). Such languages should simply do generic expression validation in that case.
    * @param aContext The expression validation context
    * @param aExpression
    * 
    * @throws AeException
    */
   public IAeExpressionValidationResult validateDurationExpression(IAeExpressionValidationContext aContext,
         String aExpression) throws AeException;

   /**
    * This method is called to validate a join condition expression. Join conditions are subject to a set of
    * relatively strict rules. For example, only boolean operators and the bpws:getLinkStatus() function may
    * be used in the expression. In addition, the result must be a Boolean. Please refer to the BPEL
    * specification for more details.
    * @param aContext The expression validation context
    * @param aExpression
    * 
    * @throws AeException
    */
   public IAeExpressionValidationResult validateJoinConditionExpression(IAeExpressionValidationContext aContext,
         String aExpression) throws AeException;
   
   /**
    * This method is called to validate an expression that is expected to return an unsignedInt. The class
    * implementing this interface (which will be expression language specific) can then provide additional
    * logic (when possible) to determine if the expression is a valid unsignedInt expression. It is expected that
    * some languages may not be able to do any type checking at validation (untyped or dynamically typed
    * languages, for example). Such languages should simply do generic expression validation in that case.
    * @param aContext The expression validation context
    * @param aExpression
    * @throws AeException 
    */
   public IAeExpressionValidationResult validateUnsignedIntExpression(IAeExpressionValidationContext aContext, String aExpression) throws AeException;

}