// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeAbstractStorage.java,v 1.4 2007/01/25 21:38:12 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage;

import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionException;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager;

/**
 * An abstract base class that is extended by all delegating storage impls.
 */
public abstract class AeAbstractStorage
{
   /** The storage delegate. */
   private IAeStorageProvider mProvider;

   /**
    * Construct the delegating storage with the given delegate.
    * 
    * @param aProvider
    */
   protected AeAbstractStorage(IAeStorageProvider aProvider)
   {
      setProvider(aProvider);
   }
   
   /**
    * @return Returns the provider.
    */
   protected IAeStorageProvider getProvider()
   {
      return mProvider;
   }

   /**
    * @param aProvider The provider to set.
    */
   protected void setProvider(IAeStorageProvider aProvider)
   {
      mProvider = aProvider;
   }
   
   /**
    * Gets an auto-commit DB connection.
    */
   protected IAeStorageConnection getDBConnection() throws AeStorageException
   {
      return getProvider().getDBConnection();
   }

   /**
    * Gets a commit control DB connection.
    */
   protected IAeStorageConnection getCommitControlDBConnection() throws AeStorageException
   {
      return getProvider().getCommitControlDBConnection();
   }
   
   /**
    * Gets a transaction based DB connection.
    * 
    * @throws AeStorageException
    */
   protected IAeStorageConnection getTxCommitControlDBConnection() throws AeStorageException
   {
      return getProvider().getTxCommitControlDBConnection();
   }

   /**
    * Starts a transaction.
    * 
    * @throws AeStorageException
    */
   protected void beginTransaction() throws AeStorageException
   {
      try
      {
         AeTransactionManager.getInstance().begin();
      }
      catch (AeTransactionException ex)
      {
         throw new AeStorageException(ex);
      }
   }

   /**
    * Rolls back the current transaction, wrapping the exception in a storage
    * exception.
    * 
    * @throws AeStorageException
    */
   protected void rollbackTransaction() throws AeStorageException
   {
      try
      {
         AeTransactionManager.getInstance().rollback();
      }
      catch (AeTransactionException ex)
      {
         throw new AeStorageException(ex);
      }
   }
}
