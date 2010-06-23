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
 * A storage connection interface.  This interface exists in order to have a common "face" to the
 * specific database (SQL vs Tamino) connection.  The delegating storages often make multiple calls
 * to their delegate that must all exist in the same transaction.  This connection object is what
 * is acquired and then used for that purpose.
 */
public interface IAeStorageConnection
{
   /**
    * Commits the transaction associated with this connection.
    * 
    * @throws AeStorageException
    */
   public void commit() throws AeStorageException;

   /**
    * Rolls back the transaction associated with this connection.
    * 
    * @throws AeStorageException
    */
   public void rollback() throws AeStorageException;
   
   /**
    * Closes the connection.
    */
   public void close();
}
