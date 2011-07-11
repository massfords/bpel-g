//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/transaction/IAeTransaction.java,v 1.3 2006/05/24 23:16:31 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.transaction;

/**
 * Defines interface for a transaction on the current thread.
 */
public interface IAeTransaction
{
   /**
    * Begins a new transaction.
    */
   public void begin() throws AeTransactionException;

   /**
    * Commits the current transaction.
    */
   public void commit() throws AeTransactionException;

   /**
    * Returns <code>true</code> if and only if a transaction is active.
    */
   public boolean isActive();

   /**
    * Rolls back the current transaction.
    */
   public void rollback() throws AeTransactionException;
}
