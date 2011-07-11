//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/xpath/AeXPathExpressionTypeConverter.java,v 1.2 2006/07/10 16:32:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr.xpath;

import org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionTypeConverter;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;

/**
 * An implementation of a type converter for jaxen.  This class is necessary because Jaxen doesn't like
 * certain Java types.  So we convert those types to other types that Jaxen DOES like.
 */
public class AeXPathExpressionTypeConverter extends AeAbstractExpressionTypeConverter
{
   /** The XPath Helper to use. */
   private AeXPathHelper mXPathHelper;

   /**
    * Constructor.
    * 
    * @param aXPathHelper
    */
   public AeXPathExpressionTypeConverter(AeXPathHelper aXPathHelper)
   {
      setXPathHelper(aXPathHelper);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter#convertToExpressionType(java.lang.Object)
    */
   public Object convertToExpressionType(Object aEngineType)
   {
      Object type = super.convertToExpressionType(aEngineType);
      if (type instanceof Number)
      {
         // jaxen likes doubles, but not floats, big integers, so convert to double here
         type = new Double(((Number) aEngineType).doubleValue());
      }
      return type;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter#convertToEngineType(java.lang.Object)
    */
   public Object convertToEngineType(Object aExpressionType)
   {
      return getXPathHelper().unwrapXPathValue(aExpressionType);
   }

   /**
    * @return Returns the xPathHelper.
    */
   protected AeXPathHelper getXPathHelper()
   {
      return mXPathHelper;
   }

   /**
    * @param aPathHelper The xPathHelper to set.
    */
   protected void setXPathHelper(AeXPathHelper aPathHelper)
   {
      mXPathHelper = aPathHelper;
   }
}
