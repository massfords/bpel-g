//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/functions/AeDelegatingFunctionValidator.java,v 1.2 2008/01/25 21:01:17 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.expr.functions; 

import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.expr.validation.AeExpressionValidationResult;
import org.activebpel.rt.expr.validation.IAeExpressionValidationContext;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidator;

/**
 * Delegating function validator. Passes validate call through to a delegate
 * validator. 
 */
public class AeDelegatingFunctionValidator extends AeAbstractFunctionValidator
{
   /** delegate that does actual validation */
   private IAeFunctionValidator mDelegate;
   
   /**
    * Ctor
    * @param aDelegate
    */
   public AeDelegatingFunctionValidator(IAeFunctionValidator aDelegate)
   {
      setDelegate(aDelegate);
   }

   /**
    * Pass through to delegate
    * 
    * @see org.activebpel.rt.expr.validation.functions.IAeFunctionValidator#validate(org.activebpel.rt.expr.def.AeScriptFuncDef, org.activebpel.rt.expr.validation.AeExpressionValidationResult, org.activebpel.rt.expr.validation.IAeExpressionValidationContext)
    */
   public void validate(AeScriptFuncDef aScriptFunction,
         AeExpressionValidationResult aResult, IAeExpressionValidationContext aContext)
   {
      getDelegate().validate(aScriptFunction, aResult, aContext);
   }

   /**
    * @return the delegate
    */
   public IAeFunctionValidator getDelegate()
   {
      return mDelegate;
   }

   /**
    * @param aDelegate the delegate to set
    */
   public void setDelegate(IAeFunctionValidator aDelegate)
   {
      mDelegate = aDelegate;
   }

}
 
