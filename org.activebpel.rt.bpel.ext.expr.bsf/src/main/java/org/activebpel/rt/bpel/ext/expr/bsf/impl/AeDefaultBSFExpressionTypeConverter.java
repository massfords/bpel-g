//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.bsf.impl;

import java.util.List;

import org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter;
import org.w3c.dom.Node;

/**
 * Implements a default BSF type converter.  This converter knows nothing about any particular langage,
 * and so is basically just a pass-through converter.  It passes through common types and converts
 * unknown types to String.
 */
public class AeDefaultBSFExpressionTypeConverter implements IAeExpressionTypeConverter
{
   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter#convertToEngineType(java.lang.Object)
    */
   public Object convertToEngineType(Object aExpressionType)
   {
      if (aExpressionType instanceof Integer || aExpressionType instanceof Double || aExpressionType instanceof String || aExpressionType instanceof Boolean)
      {
         return aExpressionType;
      }
      if (aExpressionType instanceof Node || aExpressionType instanceof List)
      {
         return aExpressionType;
      }
      else
      {
         return aExpressionType.toString();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionTypeConverter#convertToExpressionType(java.lang.Object)
    */
   public Object convertToExpressionType(Object aEngineType)
   {
      return aEngineType;
   }
}
