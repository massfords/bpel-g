// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeStorageException.java,v 1.1 2004/07/31 00:46:59 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * Standard exception for the storage package.
 */
public class AeStorageException extends AeBusinessProcessException
{
   /** Constructor. */
   public AeStorageException(String aInfo)
   {
      super(aInfo);
   }

   /** Constructor. */
   public AeStorageException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }

   /** Constructor. */
   public AeStorageException(Throwable aRootCause)
   {
      super(aRootCause.getLocalizedMessage(), aRootCause);
   }
}
