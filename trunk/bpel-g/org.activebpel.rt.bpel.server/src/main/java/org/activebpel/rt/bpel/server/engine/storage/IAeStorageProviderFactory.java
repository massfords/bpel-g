// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAeStorageProviderFactory.java,v 1.6 2007/04/23 23:38:57 jbik Exp $
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
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider;

/**
 * Defines methods needed to create storage providers (Database i/o classes).
 */
public interface IAeStorageProviderFactory
{
   /**
    * Returns the DB config that the storage provider will use.
    */
   public AeStorageConfig getDBConfig();

   /**
    * Called after the storage provider factory has been created.
    * 
    * @throws AeException
    */
   public void init() throws AeException;

   /**
    * Creates the queue storage provider.
    */
   public IAeQueueStorageProvider createQueueStorageProvider();

   /**
    * Creates the process state storage object.
    */
   public IAeProcessStateStorageProvider createProcessStateStorageProvider();
   /**
    * Creates the coordination storage object.
    */
   public IAeCoordinationStorageProvider createCoordinationStorageProvider();
   
   /**
    * Creates the URN storage object.
    */
   public IAeURNStorageProvider createURNStorageProvider();
   
   /**
    * Creates and returns the storage provider for the TransmissionTracker manager store.
    */
   public IAeTransmissionTrackerStorageProvider createTransmissionTrackerStorageProvider();
   
   /**
    * Creates and returns the storage provider for the attachment manager store.
    */
   public IAeAttachmentStorageProvider createAttachmentStorageProvider();
   
   /**
    * Creates a custom storage provider with the given name.
    * 
    * @param aProviderName
    */
   public IAeStorageProvider createCustomStorageProvider(String aProviderName);
}
