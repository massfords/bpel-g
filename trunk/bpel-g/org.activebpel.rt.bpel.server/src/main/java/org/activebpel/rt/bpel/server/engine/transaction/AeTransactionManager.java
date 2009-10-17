//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/transaction/AeTransactionManager.java,v 1.9 2007/09/07 20:52:13 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.transaction;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Implements abstract base class for managing instances of
 * {@link org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction}.
 */
public abstract class AeTransactionManager implements IAeTransactionManager
{
   /** Singleton instance. */
   private static IAeTransactionManager sInstance;

   /** Per-thread storage for transaction reference. */
   private ThreadLocal mTransactionThreadLocal = new ThreadLocal();

   /**
    * Protected constructor for singleton instance.
    */
   protected AeTransactionManager()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#begin()
    */
   public void begin() throws AeTransactionException
   {
      getTransaction().begin();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#commit()
    */
   public void commit() throws AeTransactionException
   {
      try
      {
         getTransaction().commit();
      }
      finally
      {
         setTransaction(null);
      }
   }

   /**
    * Returns a new transaction.
    */
   protected abstract IAeTransaction createTransaction() throws AeTransactionException;

   /**
    * Returns the singleton instance, constructing it if necessary.
    */
   public static IAeTransactionManager getInstance()
   {
      // fixme (double-check) remove in favor of inline init
      if (sInstance == null)
      {
         synchronized(AeTransactionManager.class)
         {
            if (sInstance == null)
            {
               sInstance = getTransactionManagerFactory().createTransactionManager();
            }
         }
      }
      return sInstance;
   }

   /**
    * Shuts down the transaction manager.
    */
   public static void shutdown()
   {
      sInstance = null;
   }

   /**
    * Overrides method to return transaction from thread local storage,
    * creating the transaction if necessary.
    *
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#getTransaction()
    */
   public IAeTransaction getTransaction() throws AeTransactionException
   {
      IAeTransaction transaction = (IAeTransaction) mTransactionThreadLocal.get();

      if (transaction == null)
      {
         // Construct a new transaction.
         transaction = createTransaction();
         setTransaction(transaction);
      }

      return transaction;
   }

   /**
    * Returns the singleton transaction manager factory.
    */
   protected static IAeTransactionManagerFactory getTransactionManagerFactory()
   {
      return AeEngineFactory.getTransactionManagerFactory();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#rollback()
    */
   public void rollback() throws AeTransactionException
   {
      try
      {
         getTransaction().rollback();
      }
      finally
      {
         setTransaction(null);
      }

   }

   /**
    * Sets the transaction for the current thread.
    */
   protected void setTransaction(IAeTransaction aTransaction)
   {
      mTransactionThreadLocal.set(aTransaction);
   }
}
