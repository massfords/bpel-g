//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/validation/AeExpressionValidationContext.java,v 1.2 2008/02/13 00:23:31 dvilaverde Exp $
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
 * A simple implementation of an expression validation context.
 */
public class AeExpressionValidationContext implements IAeExpressionValidationContext
{
   /** The base def. */
   private AeBaseXmlDef mDef;
   /** Factory used to create validators for functions found in the expression */
   private IAeFunctionValidatorFactory mFunctionFactory;
   /** the bpel namespace */
   private String mBpelNamespace;
   /**
    * Simple constructor.
    * 
    * @param aDef
    */
   public AeExpressionValidationContext(AeBaseXmlDef aDef, IAeFunctionValidatorFactory aFactory, String aBpelNamespace)
   {
      setDef(aDef);
      setFunctionFactory(aFactory);
      setBpelNamespace(aBpelNamespace);
   }

   /**
    * @see org.activebpel.rt.expr.validation.IAeExpressionValidationContext#getBaseDef()
    */
   public AeBaseXmlDef getBaseDef()
   {
      return getDef();
   }
   
   /**
    * @return Returns the def.
    */
   protected AeBaseXmlDef getDef()
   {
      return mDef;
   }
   
   /**
    * @param aDef The def to set.
    */
   protected void setDef(AeBaseXmlDef aDef)
   {
      mDef = aDef;
   }

   /**
    * @return the functionFactory
    */
   public IAeFunctionValidatorFactory getFunctionFactory()
   {
      return mFunctionFactory;
   }

   /**
    * @param aFunctionFactory the functionFactory to set
    */
   protected void setFunctionFactory(IAeFunctionValidatorFactory aFunctionFactory)
   {
      mFunctionFactory = aFunctionFactory;
   }

   /**
    * @return Returns the bpelNamespace.
    */
   public String getBpelNamespace()
   {
      return mBpelNamespace;
   }

   /**
    * @param aBpelNamespace the bpelNamespace to set
    */
   protected void setBpelNamespace(String aBpelNamespace)
   {
      mBpelNamespace = aBpelNamespace;
   }

}
