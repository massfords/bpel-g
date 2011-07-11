//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.bsf.impl.python;

import java.util.List;

import org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionTypeConverter;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.IAeSchemaType;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.w3c.dom.Element;

/**
 * Implements a python/Jython type converter.  This class converts values returned by Jython into 
 * native Java types.
 */
public class AeBSFPythonExpressionTypeConverter extends AeAbstractExpressionTypeConverter
{
   /**
    * @see org.activebpel.rt.bpel.impl.expr.xpath.AeXPathExpressionTypeConverter#convertToEngineType(java.lang.Object)
    */
   public Object convertToEngineType(Object aExpressionType)
   {
      if (aExpressionType instanceof PyInteger)
      {
         return new Integer(((PyInteger) aExpressionType).getValue());
      }
      else if (aExpressionType instanceof PyFloat)
      {
         return new Float(((PyFloat) aExpressionType).getValue());
      }
      else if (aExpressionType instanceof PyLong)
      {
         return new Double(((PyLong) aExpressionType).doubleValue());
      }
      else if (aExpressionType instanceof PyObject)
      {
         return aExpressionType.toString();
      }
      else
      {
         return aExpressionType;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.xpath.AeXPathExpressionTypeConverter#convertToExpressionType(java.lang.Object)
    */
   public Object convertToExpressionType(Object aEngineType)
   {
      Object rval = super.convertToExpressionType(aEngineType);
      if (rval instanceof Number)
      {
         rval = new Double(((Number) aEngineType).doubleValue());
      }

      // Unwrap the return value if necessary.
      if (aEngineType instanceof List)
      {
         if (((List) aEngineType).size() == 1)
         {
            Object val = ((List) aEngineType).get(0);
            if (val instanceof Element)
            {
               rval = AeXmlUtil.getText((Element) val);
            }
            else
            {
               rval = val;
            }
         }
      }
      else if (aEngineType instanceof IAeSchemaType)
      {
         rval = aEngineType.toString();
      }

      return rval;
   }
}
