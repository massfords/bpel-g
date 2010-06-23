// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/AeExpressionResultConverter.java,v 1.1 2008/02/27 20:41:55 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.expr;

import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;


/**
 * Utility used to convert expression results to specific expected types.
 */
public class AeExpressionResultConverter
{
   /**
    * Converts the given String value to a non negative number.
    * 
    * @param aValue
    */
   public static int toNonNegativeInt(Object aValue) throws IllegalArgumentException
   {
      Object value = aValue;
      if (value instanceof Element)
         value = AeXmlUtil.getText((Element) value);
      if (value instanceof Attr)
         value = ((Attr) value).getValue();
      
      double result = -1;

      if (value instanceof Number)
      {
         result = ((Number) value).doubleValue();
      }
      else if (value instanceof String)
      {
         try
         {
            result = Double.parseDouble(value.toString());
         }
         catch (NumberFormatException e)
         {
            // we'll throw below
            result = -1;
         }
      }

      // check to make sure that the value is >=0 AND that there is no floating point number
      if (result >= 0 && Double.compare(result, Math.ceil(result)) == 0)
      {
         return (int) result;
      }

      throw new IllegalArgumentException();
   }
}
