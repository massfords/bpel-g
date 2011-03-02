// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.expr;

import org.activebpel.rt.bpel.impl.AeBpelException;

/**
 * Runtime exception that wraps an AeException.  This is used when we want to throw an exception
 * from a place where it would not normally be allowed (for example, in a Jaxen VariableContext
 * implementation).
 */
public class AeExpressionException extends RuntimeException
{
   /** The wrapped AeException. */
   private AeBpelException mWrappedException;

   /**
    * Constructor.
    */
   public AeExpressionException(AeBpelException aWrappedException)
   {
      setWrappedException(aWrappedException);
   }

   /**
    * @return Returns the wrappedException.
    */
   public AeBpelException getWrappedException()
   {
      return mWrappedException;
   }

   /**
    * @param aWrappedException The wrappedException to set.
    */
   protected void setWrappedException(AeBpelException aWrappedException)
   {
      mWrappedException = aWrappedException;
   }
}
