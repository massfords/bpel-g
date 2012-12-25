//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expressions/AeBooleanExpressionValidator.java,v 1.2 2008/01/25 21:01:18 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.expressions; 

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;
import org.activebpel.rt.expr.validation.IAeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;

/**
 * model provides validation of boolean expression defs like conditions or elseifs
 */
public class AeBooleanExpressionValidator extends AeBaseExpressionValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeBooleanExpressionValidator(IAeExpressionDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.expressions.AeBaseExpressionValidator#validateExpression(java.lang.String, org.activebpel.rt.expr.validation.IAeExpressionValidator, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   protected IAeExpressionValidationResult validateExpression(String aExpression, IAeExpressionValidator aValidator, IAeExpressionValidationContext aContext) throws AeException
   {
      return aValidator.validateBooleanExpression(aContext, aExpression);
   }
}
 