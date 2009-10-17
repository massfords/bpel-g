//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.xml.schema.IAeSchemaType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A simple type converter that does some basic type conversions that are necessary for all/most
 * languages.
 */
public abstract class AeAbstractExpressionTypeConverter implements IAeExpressionTypeConverter
{
   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter#convertToExpressionType(java.lang.Object)
    */
   public Object convertToExpressionType(Object aEngineType)
   {
      Object rval = null;
      if (aEngineType instanceof IAeSchemaType)
      {
         // Convert our schema types (date, dateTime, time, duration) to Strings
         rval = aEngineType.toString();
      }
      else if (aEngineType instanceof List)
      {
         // Convert all of the items in the list.
         List list = (List) aEngineType;
         List rvalList = new ArrayList();
         if (list.size() > 0)
         {
            for (Iterator iter = list.iterator(); iter.hasNext(); )
            {
               Object item = iter.next();
               if (item instanceof Node)
                  rvalList.add(item);
               else
                  rvalList.add(convertToExpressionType(item));
            }
            rval = rvalList;
         }
      }
      else if (aEngineType instanceof Document)
      {
         // Note: this seems redundant, but is necessary is that the 'instanceof Node' case doesn't 
         // get run.
         rval = aEngineType;
      }
      else if (aEngineType instanceof Node)
      {
         // If it's a Node, wrap it in a List so that it can be used in sub-queries of the form:
         //   getVariableData('var')/sub/xpath/query
         ArrayList list = new ArrayList();
         list.add(aEngineType);
         rval = list;
      }
      else
      {
         rval = aEngineType;
      }
      return rval;
   }
}
