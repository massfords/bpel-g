//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeInvalidFunctionContextException.java,v 1.1 2005/06/08 12:50:30 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import org.activebpel.rt.AeException;

/**
 * Generated when the addition of a function context fails.
 */
public class AeInvalidFunctionContextException extends AeException
{
   /**
    * Constructor. 
    */
   public AeInvalidFunctionContextException()
   {
      super();
   }

   /**
    * Constructor. 
    * @param aInfo
    */
   public AeInvalidFunctionContextException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Constructor. 
    * @param aInfo
    * @param aRootCause
    */
   public AeInvalidFunctionContextException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }

   /**
    * Constructor. 
    * @param aRootCause
    */
   public AeInvalidFunctionContextException(Throwable aRootCause)
   {
      super(aRootCause);
   }
}
