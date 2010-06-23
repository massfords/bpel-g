// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAeStorageFactory.java,v 1.8 2007/05/08 19:21:00 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage;

/**
 * This interface defines an Active BPEL engine store factory. The storage factory is responsible for
 * creating storage objects for the generic persistent versions of the engine managers.
 */
public interface IAeStorageFactory
{
   /**
    * Sets the configuration information for this persistent store factory.
    * This method is guaranteed to be called prior to any other method in the
    * interface.  Implementers of this interface should save the passed-in
    * configuration map if there is any configuration information needed to
    * create the persistent stores.
    *
    * @param aConfig The engine configuration map.
    */
   public void setConfiguration(Map aConfig) throws AeException;

   /**
    * The init method is called after the store factory has been created.  The factory
    * should do any initialization of its internal values in the init method.
    *
    * @throws AeException
    */
   public void init() throws AeException;

   /**
    * Gets the queue storage.
    */
   public IAeQueueStorage getQueueStorage();

   /**
    * Gets the process state storage.
    */
   public IAeProcessStateStorage getProcessStateStorage();

   /**
    * Gets the coordination storage.
    */
   public IAeCoordinationStorage getCoordinationStorage();

   /**
    * Gets the URN storage.
    */
   public IAeURNStorage getURNStorage();

   /**
    * Returns persistent counter store.
    */
   public IAeCounterStore getCounterStore();

   /**
    * Returns persistent transmission manager store.
    */
   public IAeTransmissionTrackerStorage getTransmissionTrackerStorage();
     
   /**
    * Returns attachment storage. 
    */
   public IAeAttachmentStorage getAttachmentStorage();
   
   
   /**
    * Returns a custom storage object with the given name.
    * 
    * @param aStorageName
    */
   public IAeStorage getCustomStorage(String aStorageName);

}
