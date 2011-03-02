//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/transaction/IAeTransactionManager.java,v 1.3 2006/05/24 23:16:32 PJayanetti Exp $
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
 * Defines interface for managing instances of
 * {@link org.activebpel.rt.bpel.server.engine.transaction.IAeTransaction}.
 */
public interface IAeTransactionManager
{
   /**
    * Begins a new transaction on the current thread.
    */
   public void begin() throws AeTransactionException;

   /**
    * Commits the transaction on the current thread.
    */
   public void commit() throws AeTransactionException;

   /**
    * Returns the transaction for the current thread.
    */
   public IAeTransaction getTransaction() throws AeTransactionException;

   /**
    * Rolls back the transaction on the current thread.
    */
   public void rollback() throws AeTransactionException;
}

