//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/providers/IAeTransmissionTrackerStorageProvider.java,v 1.3 2006/12/14 15:18:40 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.providers;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.transreceive.AeTransmissionTrackerEntry;
import org.activebpel.rt.util.AeLongSet;

/**
 * Defines the storage provider interface for managing transmission ids.
 */
public interface IAeTransmissionTrackerStorageProvider extends IAeStorageProvider
{
   /**
    * Returns the next (unique) id. 
    * @return next transmission id.
    * @throws AeStorageException
    */
   public long getNextTransmissionId() throws AeStorageException;
   
   /**
    * Adds the given entry to the storage.
    * @param aEntry
    * @throws AeStorageException
    */
   public void add(AeTransmissionTrackerEntry aEntry) throws AeStorageException;
   
   /**
    * Retrieves the entry given the entry id.
    * @param aTransmissionId
    * @return entry or <code>null</code> if not found.
    * @throws AeStorageException
    */
   public AeTransmissionTrackerEntry get(long aTransmissionId) throws AeStorageException;
   
   /**
    * Updates the state and or message id.
    * @param aEntry
    * @throws AeStorageException
    */
   public void update(AeTransmissionTrackerEntry aEntry) throws AeStorageException;
   
   /**
    * Deletes a collection of entries.
    * @param aTransmissionIds
    * @throws AeStorageException
    */
   public void remove(AeLongSet aTransmissionIds) throws AeStorageException;
}
