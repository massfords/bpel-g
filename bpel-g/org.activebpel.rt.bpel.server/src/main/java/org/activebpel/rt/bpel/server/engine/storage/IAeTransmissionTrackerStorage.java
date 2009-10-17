//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAeTransmissionTrackerStorage.java,v 1.3 2007/04/03 20:54:31 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import org.activebpel.rt.bpel.server.transreceive.AeTransmissionTrackerEntry;
import org.activebpel.rt.util.AeLongSet;

/**
 * Interface for the transmission, receive manager storage layer.
 */
public interface IAeTransmissionTrackerStorage extends IAeStorage
{
   /**
    * Returns the next available transmission id. The id is unique across the engine or cluster.
    * @return next transmission id.
    * @throws AeStorageException
    */
   public long getNextTransmissionId() throws AeStorageException;
   
   /**
    * Adds a new entry.
    * @param aEntry
    * @throws AeStorageException
    */
   public void add(AeTransmissionTrackerEntry aEntry) throws AeStorageException;
   
   /**
    * Returns the entry given id.
    * @param aTransmissionId
    * @return entry or <code>null</code> if not found.
    * @throws AeStorageException
    */
   public AeTransmissionTrackerEntry get(long aTransmissionId) throws AeStorageException;
   
   /**
    * Updates entry state and or message id.
    * @param aEntry
    * @throws AeStorageException
    */
   public void update(AeTransmissionTrackerEntry aEntry) throws AeStorageException;
   
   /**
    * Removes entry.
    * @param aTransmissionId
    * @throws AeStorageException
    */
   public void remove(long aTransmissionId) throws AeStorageException;
   
   /**
    * Removes entries given the set of transmission ids.
    * @param aTransmissionIds
    * @throws AeStorageException
    */
   public void remove(AeLongSet aTransmissionIds) throws AeStorageException;
}
