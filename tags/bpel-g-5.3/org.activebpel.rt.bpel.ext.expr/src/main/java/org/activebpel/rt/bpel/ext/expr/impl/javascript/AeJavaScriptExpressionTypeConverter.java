// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.impl.javascript;

import java.util.Date;
import java.util.List;

import org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionTypeConverter;
import org.activebpel.rt.util.AeXmlUtil;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptRuntime;
import org.w3c.dom.Element;

/**
 * A JavaScript implementation of a type converter.
 */
public class AeJavaScriptExpressionTypeConverter extends AeAbstractExpressionTypeConverter
{
   /**
    * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionTypeConverter#convertToExpressionType(java.lang.Object)
    */
   public Object convertToExpressionType(Object aEngineType)
   {
      Object rval = super.convertToExpressionType(aEngineType);
      if (rval instanceof List)
         if (((List) rval).size() == 1)
            rval = ((List) rval).get(0);

      if (rval instanceof Element)
      {
         Element elem = (Element) rval;
         if (AeXmlUtil.getFirstSubElement(elem) == null)
         {
            rval = AeXmlUtil.getText((Element) rval);
         }
      }

      return rval;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter#convertToEngineType(java.lang.Object)
    */
   public Object convertToEngineType(Object aExpressionType)
   {
      // Unwrap the return value if necessary.
      if (aExpressionType instanceof NativeJavaObject)
      {
         return ((NativeJavaObject) aExpressionType).unwrap();
      }
      else if (aExpressionType instanceof IdScriptableObject)
      {
         Context.enter();

         try
         {
            IdScriptableObject idsObj = (IdScriptableObject) aExpressionType;
            // Handle an expression like:  'new Date(85, 2, 10)'
            // which will return a NativeDate object (which is a private rhino class)
            if ("Date".equals(idsObj.getClassName())) //$NON-NLS-1$
            {
               Number number = (Number) idsObj.getDefaultValue(ScriptRuntime.NumberClass);
               Date date = new Date(number.longValue());
               return date;
            }
            else
            {
               // No other special cases are known - just in case this happens, convert to string.
               return idsObj.getDefaultValue(ScriptRuntime.StringClass);
            }
         }
         finally
         {
            Context.exit();
         }
      }
      else
      {
         return aExpressionType;
      }
   }
}
