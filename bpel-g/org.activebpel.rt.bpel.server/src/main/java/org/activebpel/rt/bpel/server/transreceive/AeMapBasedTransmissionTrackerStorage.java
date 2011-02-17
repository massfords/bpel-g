package org.activebpel.rt.bpel.server.transreceive;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeTransmissionTrackerStorage;

// useful for in-memory only version of engine
public class AeMapBasedTransmissionTrackerStorage implements
		IAeTransmissionTrackerStorage {

	/** In memory map of entries. */
	private Map<Long,AeTransmissionTrackerEntry> mEntries = new ConcurrentHashMap();
	/** Next transmission id. */
	private AtomicLong mNextId = new AtomicLong(0);

	@Override
	public long getNextTransmissionId() throws AeStorageException {
		return mNextId.incrementAndGet();
	}

	@Override
	public void add(AeTransmissionTrackerEntry aEntry)
			throws AeStorageException {
		mEntries.put(aEntry.getTransmissionId(), aEntry);
	}

	@Override
	public AeTransmissionTrackerEntry get(long aTransmissionId)
			throws AeStorageException {
		return mEntries.get(aTransmissionId);
	}

	@Override
	public void update(AeTransmissionTrackerEntry aEntry)
			throws AeStorageException {
	}

	@Override
	public void remove(long aTransmissionId) throws AeStorageException {
		mEntries.remove(aTransmissionId);
	}

	@Override
	public void remove(Set<Long> aTransmissionIds) throws AeStorageException {
		for(Long key : aTransmissionIds) {
			remove(key);
		}
	}

}
