//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/AeUnresolvableException.java,v 1.1 2005/06/08 12:50:28 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;

import org.activebpel.rt.AeException;

/**
 * Thrown if <code>IAeFunctionContext</code> impl is unable to find the 
 * appropriate <code>IAeExpressionFunction</code>.
 */
public class AeUnresolvableException extends AeException
{
   /**
    * Constructor.
    * @param aInfo
    */
   public AeUnresolvableException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Constructor.
    * @param aInfo
    * @param aRootCause
    */
   public AeUnresolvableException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }
   
   /**
    * Constructor.
    * @param aRootCause
    */
   public AeUnresolvableException(Throwable aRootCause)
   {
      super(aRootCause);
   }
}
