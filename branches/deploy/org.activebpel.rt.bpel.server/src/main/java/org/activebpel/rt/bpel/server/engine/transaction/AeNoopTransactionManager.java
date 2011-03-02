package org.activebpel.rt.bpel.server.engine.transaction;

/**
    * Implements a do-nothing transaction manager for non-persistent engine
    * configurations.
    */
   public class AeNoopTransactionManager implements IAeTransactionManager
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