// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeMissingStorageDocumentException.java,v 1.1 2005/07/12 00:25:23 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;

/**
 * Implements an exception that reports a missing storage document for a
 * journal entry.
 */
public class AeMissingStorageDocumentException extends AeException
{
   /**
    * Constructs an exception that reports a missing storage document for a
    * journal entry.
    */
   public AeMissingStorageDocumentException()
   {
      super(AeMessages.getString("AeMissingStorageDocumentException.ERROR_0")); //$NON-NLS-1$
   }

   /**
    * Constructs an exception that reports a missing storage document for a
    * journal entry with data from the given location.
    *
    * @param aLocationId
    */
   public AeMissingStorageDocumentException(int aLocationId)
   {
      super(AeMessages.format("AeMissingStorageDocumentException.ERROR_1", aLocationId)); //$NON-NLS-1$
   }
}
