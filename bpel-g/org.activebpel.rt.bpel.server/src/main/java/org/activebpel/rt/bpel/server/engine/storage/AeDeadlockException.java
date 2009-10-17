// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeDeadlockException.java,v 1.1 2006/02/10 21:51:13 ewittmann Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage;

import org.activebpel.rt.bpel.server.AeMessages;


/**
 * An exception thrown when a database encounters a deadlock.
 */
public class AeDeadlockException extends AeStorageException
{
   /**
    * Construct a new runtime exception.
    */
   public AeDeadlockException()
   {
      super(AeMessages.getString("AeDeadlockException.DEFAULT_DEALOCK_EXCEPTION_MESSAGE")); //$NON-NLS-1$
   }

   /**
    * Construct a new runtime exception with the passed info string.
    * 
    * @see java.lang.Throwable#Throwable(String)
    */
   public AeDeadlockException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Construct with a root exception (used primarily for rethrowing an underlying exception).
    * 
    * @param aRootCause
    */
   public AeDeadlockException(Throwable aRootCause)
   {
      super(aRootCause);
   }

   /**
    * Construct with a root exception (used prinarily for rethrowing an underlying exception).
    * 
    * @param aInfo Informational message for the exception
    * @param aRootCause Root cause of the exception
    */
   public AeDeadlockException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }
}
