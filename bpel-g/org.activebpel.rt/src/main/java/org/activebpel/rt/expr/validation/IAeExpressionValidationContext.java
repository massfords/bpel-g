//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/validation/IAeExpressionValidationContext.java,v 1.3 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.validation;

import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Defines a context that is passed to the expression validator when an expression is being validated.
 */
public interface IAeExpressionValidationContext
{
   /**
    * Returns the base def object.
    */
   public AeBaseXmlDef getBaseDef();
   
   /**
    * @return the functionFactory
    */
   public IAeFunctionValidatorFactory getFunctionFactory();
   
   /**
    * Returns the namespace of the bpel process.
    * 
    */
   public String getBpelNamespace();
   
}
