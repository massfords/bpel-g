// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.providers;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * The base interface for all storage delegates.  This interface will be extended by the more
 * concrete storage delegates, such as Queue, Process, etc.
 */
public interface IAeStorageProvider
{
   /**
    * Gets an auto-commit DB connection.
    */
   public IAeStorageConnection getDBConnection() throws AeStorageException;

   /**
    * Gets a commit control DB connection.
    */
   public IAeStorageConnection getCommitControlDBConnection() throws AeStorageException;

   /**
    * Gets a transaction DB connection.
    */
   public IAeStorageConnection getTxCommitControlDBConnection() throws AeStorageException;
}
