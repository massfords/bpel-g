//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/AeFunctionCallException.java,v 1.1 2005/06/08 12:50:28 EWittmann Exp $
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
 * This exception is thrown when an exception is found during execution of a
 * function.
 */
public class AeFunctionCallException extends AeException
{
   /**
    * Construct a new function call exception.
    */
   public AeFunctionCallException()
   {
      super();
   }

   /**
    * Construct a new function call exception with the passed info string.
    * @see java.lang.Throwable#Throwable(String)
    */
   public AeFunctionCallException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Construct with a root exception (used primarily for rethrowing an underlying exception).
    * @param aRootCause
    */
   public AeFunctionCallException(Throwable aRootCause)
   {
      super(aRootCause);
   }

   /**
    * Construct with a root exception (used prinarily for rethrowing an underlying exception).
    * @param aInfo Informational message for the exception
    * @param aRootCause Root cause of the exception
    */
   public AeFunctionCallException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }

}
