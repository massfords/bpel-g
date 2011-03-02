//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/transreceive/AePersistentTransmissionTracker.java,v 1.3 2007/01/25 21:38:12 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.transreceive;

import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyFactory;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory;
import org.activebpel.rt.bpel.server.engine.storage.IAeTransmissionTrackerStorage;
import org.activebpel.wsio.invoke.IAeTransmission;

/**
 * Implements the persistent version of a transmission manager which is required
 * for durable invokes and durable replies.
 * 
 */
public class AePersistentTransmissionTracker implements IAeTransmissionTracker {
	/** Durable reply factory. */
	private IAeDurableReplyFactory mDurableReplyFactory;
	private IAeStorageFactory mStorageFactory;

	/**
	 * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getDurableReplyFactory()
	 */
	public IAeDurableReplyFactory getDurableReplyFactory() {
		return mDurableReplyFactory;
	}

	/**
	 * @param aDurableReplyFactory
	 *            the durableReplyFactory to set
	 */
	public void setDurableReplyFactory(
			IAeDurableReplyFactory aDurableReplyFactory) {
		mDurableReplyFactory = aDurableReplyFactory;
	}

	/**
	 * @return Returns the storage.
	 */
	protected IAeTransmissionTrackerStorage getStorage() {
		return getStorageFactory().getTransmissionTrackerStorage();
	}

	/**
	 * Overrides method to return id from the storage layer.
	 * 
	 * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getNextId()
	 */
	public long getNextId() {
		try {
			return getStorage().getNextTransmissionId();
		} catch (Exception e) {
			AeException.logError(e);
			return IAeTransmissionTracker.NULL_TRANSREC_ID;
		}
	}

	/**
	 * Overrides method to return entry from storage layer.
	 * 
	 * @see org.activebpel.rt.bpel.server.transreceive.AeInMemoryTransmissionTracker#getEntry(long)
	 */
	protected AeTransmissionTrackerEntry getEntry(long aTransmissionId)
			throws AeStorageException {
		return getStorage().get(aTransmissionId);
	}

	/**
	 * Adds the message id with the given state. This method returns a unique
	 * transmission id.
	 * 
	 * @param aTransmissionId
	 * @param aMessageId
	 *            Invoke handler dependent message id.
	 * @param aState
	 *            transmitted or received state.
	 * @throws AeException
	 */
	public void add(long aTransmissionId, String aMessageId, int aState)
			throws AeException {
		AeTransmissionTrackerEntry entry = new AeTransmissionTrackerEntry(
				aTransmissionId, aState, aMessageId);
		getStorage().add(entry);
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#update(long,
	 *      int)
	 */
	public void update(long aTransmissionId, int aState) throws AeException {
		AeTransmissionTrackerEntry entry = new AeTransmissionTrackerEntry(
				aTransmissionId, aState);
		getStorage().update(entry);
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#remove(long)
	 */
	public void remove(long aTransmissionId) throws AeException {
		getStorage().remove(aTransmissionId);
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#remove(org.activebpel.rt.util.AeLongSet)
	 */
	public void remove(Set<Long> aTransmissionIds) throws AeException {
		getStorage().remove(aTransmissionIds);
	}

	/**
	 * Returns true if the given transmission id and state already exists.
	 * 
	 * @param aTransmissionId
	 *            transmission id.
	 * @return true if id exists.
	 * @throws AeException
	 */
	public boolean exists(long aTransmissionId) throws AeException {
		return getEntry(aTransmissionId) != null;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#exists(long,
	 *      int)
	 */
	public boolean exists(long aTransmissionId, int aState) throws AeException {
		AeTransmissionTrackerEntry entry = getEntry(aTransmissionId);
		return (entry != null && entry.getState() == aState);
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getState(long)
	 */
	public int getState(long aTransmissionId) throws AeException {
		AeTransmissionTrackerEntry entry = getEntry(aTransmissionId);
		if (entry != null) {
			return entry.getState();
		} else {
			return IAeTransmissionTracker.NULL_STATE;
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getMessageId(long)
	 */
	public String getMessageId(long aTransmissionId) throws AeException {
		AeTransmissionTrackerEntry entry = getEntry(aTransmissionId);
		if (entry != null) {
			return entry.getMessageId();
		} else {
			return null;
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#isTransmitted(long)
	 */
	public boolean isTransmitted(long aTxId) throws AeException {
		// Check if this invoke has already been (reliably) delivered based on
		// the existence of
		// the transmission id in the storage layer.
		// Perform this check only if the transmission id is positive
		// (persistent/durable invoke transmission id)
		return (aTxId > IAeTransmissionTracker.NULL_TRANSREC_ID)
				&& exists(aTxId);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.transreceive.AeNoopTransmissionTracker#assignTransmissionId(org.activebpel.wsio.invoke.IAeTransmission,
	 *      long, int)
	 */
	public void assignTransmissionId(IAeTransmission aTransmission,
			long aProcessId, int aLocationId) throws AeException {
		// Assign a new id only if a durable/persistent id (positive #) has not
		// already been assigned.
		if (aTransmission.getTransmissionId() <= IAeTransmissionTracker.NULL_TRANSREC_ID) {
			// get the next tranmission id.
			long txId = getNextId();
			// set the tx id in the process state
			aTransmission.setTransmissionId(txId);
			// journal this action.
			AeEngineFactory.getEngine().getProcessManager()
					.journalInvokeTransmitted(aProcessId, aLocationId, txId);
		}
	}
	
	public void setStorageFactory(IAeStorageFactory aFactory) {
	    mStorageFactory = aFactory;
	}
	
	public IAeStorageFactory getStorageFactory() {
	    return mStorageFactory;
	}

}
