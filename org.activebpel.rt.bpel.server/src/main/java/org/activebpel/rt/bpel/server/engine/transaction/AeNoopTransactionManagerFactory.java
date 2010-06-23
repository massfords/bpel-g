//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/transaction/AeNoopTransactionManagerFactory.java,v 1.3 2006/05/24 23:16:32 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.transaction;

import java.util.Map;

/**
 * Implements a factory for a do-nothing transaction manager for non-persistent
 * engine configurations.
 */
public class AeNoopTransactionManagerFactory implements IAeTransactionManagerFactory
{
   /**
    * Constructs transaction manager factory with the specified configuration.
    *
    * @param aConfigMap
    */
   public AeNoopTransactionManagerFactory(Map aConfigMap)
   {
      // No configuration required.
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManagerFactory#createTransactionManager()
    */
   public IAeTransactionManager createTransactionManager()
   {
      return new AeNoopTransactionManager();
   }

   /**
    * Implements a do-nothing transaction for non-persistent engine
    * configurations.
    */
   private static class AeNoopTransaction implements IAeTransaction
   {
      /**
       * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction#begin()
       */
      public void begin() throws AeTransactionException
      {
      }

      /**
       * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction#commit()
       */
      public void commit() throws AeTransactionException
      {
      }

      /**
       * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction#isActive()
       */
      public boolean isActive()
      {
         return false;
      }

      /**
       * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction#rollback()
       */
      public void rollback() throws AeTransactionException
      {
      }
   }

   /**
    * Implements a do-nothing transaction manager for non-persistent engine
    * configurations.
    */
   private static class AeNoopTransactionManager implements IAeTransactionManager
   {
      /** Do-nothing transaction. */
      private IAeTransaction mNoopTransaction = new AeNoopTransaction();

      /**
       * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#begin()
       */
      public void begin() throws AeTransactionException
      {
      }

      /**
       * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#commit()
       */
      public void commit() throws AeTransactionException
      {
      }

      /**
       * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#getTransaction()
       */
      public IAeTransaction getTransaction() throws AeTransactionException
      {
         return mNoopTransaction;
      }

      /**
       * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#rollback()
       */
      public void rollback() throws AeTransactionException
      {
      }
   }
}

