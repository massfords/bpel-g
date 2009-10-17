// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.impl.xquery;

/**
 * A runtime exception that can be thrown when there is a problem converting a type in the XQuery
 * type conversion layer.
 */
public class AeXQueryTypeConversionException extends RuntimeException
{
   /**
    * Constructs the type conversion exception.
    * 
    * @param aException
    */
   public AeXQueryTypeConversionException(Exception aException)
   {
      super(aException);
   }
}
