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
public abstract class AeTransactionManager implements IAeTransactionManager {
    /**
     * Per-thread storage for transaction reference.
     */
    private final ThreadLocal<IAeTransaction> mTransactionThreadLocal = new ThreadLocal<>();

    /**
     * Protected constructor for singleton instance.
     */
    protected AeTransactionManager() {
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#begin()
     */
    public void begin() throws AeTransactionException {
        getTransaction().begin();
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#commit()
     */
    public void commit() throws AeTransactionException {
        try {
            getTransaction().commit();
        } finally {
            setTransaction(null);
        }
    }

    /**
     * Returns a new transaction.
     */
    protected abstract IAeTransaction createTransaction()
            throws AeTransactionException;

    /**
     * Returns the singleton instance, constructing it if necessary.
     */
    public static IAeTransactionManager getInstance() {
        return AeEngineFactory.getBean(IAeTransactionManager.class);
    }

    /**
     * Overrides method to return transaction from thread local storage,
     * creating the transaction if necessary.
     *
     * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#getTransaction()
     */
    public IAeTransaction getTransaction() throws AeTransactionException {
        IAeTransaction transaction = mTransactionThreadLocal.get();

        if (transaction == null) {
            // Construct a new transaction.
            transaction = createTransaction();
            setTransaction(transaction);
        }

        return transaction;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManager#rollback()
     */
    public void rollback() throws AeTransactionException {
        try {
            getTransaction().rollback();
        } finally {
            setTransaction(null);
        }
    }

    /**
     * Sets the transaction for the current thread.
     */
    protected void setTransaction(IAeTransaction aTransaction) {
        mTransactionThreadLocal.set(aTransaction);
    }
}
