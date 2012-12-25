package org.activebpel.rt.bpel.server.engine.transaction;

/**
    * Implements a do-nothing transaction for non-persistent engine
    * configurations.
    */
   public class AeNoopTransaction implements IAeTransaction
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