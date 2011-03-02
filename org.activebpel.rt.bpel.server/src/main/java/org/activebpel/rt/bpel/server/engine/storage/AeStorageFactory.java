// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeStorageFactory.java,v 1.9 2007/05/08 19:21:00 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage;
import org.activebpel.rt.bpel.server.engine.storage.attachment.AePersistentAttachmentStorage;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider;

/**
 * A basic storage factory - this class creates the various storage objects. It
 * uses a storage provider factory to create the providers needed by the
 * storages.
 */
public class AeStorageFactory implements IAeStorageFactory {
    /** The storage provider factory. */
    private IAeStorageProviderFactory mStorageProviderFactory;

    /** The queue storage object. */
    private IAeQueueStorage mQueueStorage;
    /** The process state storage object. */
    private IAeProcessStateStorage mProcessStateStorage;
    /** The coordination storage object. */
    private IAeCoordinationStorage mCoordinationStorage;
    /** The URN storage object. */
    private IAeURNStorage mURNStorage;
    /** The transmission-receive id manager storage. */
    private IAeTransmissionTrackerStorage mTransmissionTrackerStorage;
    /** The attachment storage. */
    private IAeAttachmentStorage mAttachmentStorage;

    /**
     * Creates the queue storage object.
     */
    protected IAeQueueStorage createQueueStorage() {
        IAeQueueStorageProvider provider = getStorageProviderFactory().createQueueStorageProvider();
        AeQueueStorage storage = new AeQueueStorage();
        storage.setProvider(provider);
        return storage;
    }

    /**
     * Creates the process state storage object.
     */
    protected IAeProcessStateStorage createProcessStateStorage() {
        IAeProcessStateStorageProvider provider = getStorageProviderFactory().createProcessStateStorageProvider();
        AeProcessStateStorage storage = new AeProcessStateStorage();
        storage.setProvider(provider);
        return storage;
    }

    /**
     * Creates the coordination storage object.
     */
    protected IAeCoordinationStorage createCoordinationStorage() {
        IAeCoordinationStorageProvider provider = getStorageProviderFactory().createCoordinationStorageProvider();
        AeCoordinationStorage storage = new AeCoordinationStorage();
        storage.setProvider(provider);
        return storage;
    }

    /**
     * Creates the URN storage object.
     */
    protected IAeURNStorage createURNStorage() {
        IAeURNStorageProvider provider = getStorageProviderFactory().createURNStorageProvider();
        AeURNStorage storage = new AeURNStorage();
        storage.setProvider(provider);
        return storage;
    }

    /**
     * Creates the transmission manager storage object.
     */
    protected IAeTransmissionTrackerStorage createTransmissionTrackerStorage() {
        IAeTransmissionTrackerStorageProvider provider = getStorageProviderFactory().createTransmissionTrackerStorageProvider();
        AeTransmissionTrackerStorage storage = new AeTransmissionTrackerStorage();
        storage.setProvider(provider);
        return storage;
    }

    /**
     * Creates the attachment storage object.
     */
    protected IAeAttachmentStorage createAttachmentStorage() {
        IAeAttachmentStorageProvider provider = getStorageProviderFactory().createAttachmentStorageProvider();
        AePersistentAttachmentStorage storage = new AePersistentAttachmentStorage();
        storage.setProvider(provider);
        return storage;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getQueueStorage()
     */
    public IAeQueueStorage getQueueStorage() {
        return mQueueStorage;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getProcessStateStorage()
     */
    public IAeProcessStateStorage getProcessStateStorage() {
        return mProcessStateStorage;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getCoordinationStorage()
     */
    public IAeCoordinationStorage getCoordinationStorage() {
        return mCoordinationStorage;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getURNStorage()
     */
    public IAeURNStorage getURNStorage() {
        return mURNStorage;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getTransmissionTrackerStorage()
     */
    public IAeTransmissionTrackerStorage getTransmissionTrackerStorage() {
        return mTransmissionTrackerStorage;
    }

    public IAeAttachmentStorage getAttachmentStorage() {
        return mAttachmentStorage;
    }

    /**
     * Initializes the store. Checks for required upgrades to the schema and
     * performs each upgrade in sequence.
     * 
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#init()
     */
    public void init() throws AeException {
        setQueueStorage(createQueueStorage());
        setProcessStateStorage(createProcessStateStorage());
        setAttachmentStorage(createAttachmentStorage());
        setCoordinationStorage(createCoordinationStorage());
        setTransmissionTrackerStorage(createTransmissionTrackerStorage());
        setURNStorage(createURNStorage());
    }

    /**
     * @param aCoordinationStorage
     *            The coordinationStorage to set.
     */
    public void setCoordinationStorage(IAeCoordinationStorage aCoordinationStorage) {
        mCoordinationStorage = aCoordinationStorage;
    }

    /**
     * @param aProcessStateStorage
     *            The processStateStorage to set.
     */
    public void setProcessStateStorage(IAeProcessStateStorage aProcessStateStorage) {
        mProcessStateStorage = aProcessStateStorage;
    }

    /**
     * @param aQueueStorage
     *            The queueStorage to set.
     */
    public void setQueueStorage(IAeQueueStorage aQueueStorage) {
        mQueueStorage = aQueueStorage;
    }

    /**
     * @param aStorage
     *            The uRNStorage to set.
     */
    public void setURNStorage(IAeURNStorage aStorage) {
        mURNStorage = aStorage;
    }

    /**
     * @param aTransmissionTrackerStorage
     *            transmission-receive tracker storage.
     */
    public void setTransmissionTrackerStorage(
            IAeTransmissionTrackerStorage aTransmissionTrackerStorage) {
        mTransmissionTrackerStorage = aTransmissionTrackerStorage;
    }

    /**
     * @param aAttachmentStorage
     *            attachment storage.
     */
    public void setAttachmentStorage(IAeAttachmentStorage aAttachmentStorage) {
        mAttachmentStorage = aAttachmentStorage;
    }

    /**
     * @return Returns the storageProviderFactory.
     */
    public IAeStorageProviderFactory getStorageProviderFactory() {
        return mStorageProviderFactory;
    }

    /**
     * @param aStorageProviderFactory
     *            The storageProviderFactory to set.
     */
    public void setStorageProviderFactory(IAeStorageProviderFactory aStorageProviderFactory) {
        mStorageProviderFactory = aStorageProviderFactory;
    }

    @Override
    public boolean isReady() {
        // FIXME db - need to impl
        return false;
    }
}
