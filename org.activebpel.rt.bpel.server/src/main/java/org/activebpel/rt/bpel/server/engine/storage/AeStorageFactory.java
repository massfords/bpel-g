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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeStorageChangeListener;
import org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.storage.attachment.AePersistentAttachmentStorage;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeAttachmentStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeProcessStateStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.util.AeUtil;

/**
 * A basic storage factory - this class creates the various storage objects.  It uses a storage
 * provider factory to create the providers needed by the storages.
 */
public class AeStorageFactory implements IAeStorageFactory
{
   /** This is a engine config key that determines whether we log any hash collisions we find. */
   protected static final String CONFIG_KEY_LOGCOLLISIONS = "LogHashCollisions"; //$NON-NLS-1$
   /** This is a engine config key for the custom storages section. */
   protected static final String CONFIG_KEY_CUSTOM_STORAGES = "CustomStorages"; //$NON-NLS-1$
   /** This is a engine config key for the provider name. */
   protected static final String CONFIG_KEY_PROVIDER = "Provider"; //$NON-NLS-1$

   /** The cached configuration map. */
   private Map mConfig;
   /** The db resource Config object. */
   protected AeStorageConfig mStorageConfig;

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
   /** The persistent counter source. */
   private IAeCounterStore mCounterStore;
   /** The transmission-receive id manager storage. */

   private IAeTransmissionTrackerStorage mTransmissionTrackerStorage; 
   /** The attachment storage. */
   private IAeAttachmentStorage mAttachmentStorage;

   /** The map of custom storages. */
   private Map mCustomStorages = new HashMap();


   /**
    * Accepts the configuration.  Based on the configuration it creates the
    * datasource and validates the schema.
    *
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#setConfiguration(java.util.Map)
    */
   public void setConfiguration(Map aConfig) throws AeException
   {
      setConfig(aConfig);
      setStorageProviderFactory(createStorageProviderFactory());
      getStorageProviderFactory().init();
      setDBConfig(getStorageProviderFactory().getDBConfig());
      createStorages();

      AeEngineFactory.getEngineConfig().getUpdatableEngineConfig().addStorageChangeListener(new AeStorageConfigUpdater());
   }

   /**
    * Creates the storage provider factory from the config.
    *
    * @throws AeException
    */
   protected IAeStorageProviderFactory createStorageProviderFactory() throws AeException
   {
      Map subConfig = (Map) getConfig().get(IAeEngineConfiguration.STORAGE_PROVIDER_FACTORY);
      if (AeUtil.isNullOrEmpty(subConfig))
      {
         throw new AeException(AeMessages.getString("AeStorageFactory.ERROR_STORAGE_PROVIDER_FACTORY_KEY_MISSING")); //$NON-NLS-1$
      }

      String className = (String) subConfig.get(IAeEngineConfiguration.CLASS_ENTRY);
      if (AeUtil.isNullOrEmpty(className))
      {
         throw new AeException(AeMessages.getString("AeStorageFactory.ERROR_CLASS_MISSING")); //$NON-NLS-1$
      }

      try
      {
         Class clazz = Class.forName(className);
         Constructor constructor = clazz.getConstructor(new Class[] { Map.class });
         return (IAeStorageProviderFactory) constructor.newInstance(new Object[] { subConfig });
      }
      catch (Throwable e)
      {
         // unwrap target exception if invocation exception during construction
         if (e instanceof InvocationTargetException)
            e = ((InvocationTargetException)e).getTargetException();
         if (!(e instanceof AeException))
         {
            e = new AeException(AeMessages.getString("AeStorageFactory.ERROR_CREATING_PROVIDER"), e); //$NON-NLS-1$
         }

         throw (AeException) e;
      }
   }

   /**
    * Creates the various storage objects.
    *
    * @throws AeException
    */
   protected void createStorages() throws AeException
   {
      // Create the queue storage object.
      setQueueStorage(createQueueStorage());
      // Create the process state storage object.
      setProcessStateStorage(createProcessStateStorage());
      // Create the coordination storage object.
      setCoordinationStorage(createCoordinationStorage());
      // Create the URN storage object.
      setURNStorage(createURNStorage());
      // Create the counter storage object.
      setCounterStore(createCounterStorage());
      // TransmissionTracker storage
      setTransmissionTrackerStorage(createTransmissionTrackerStorage());
      // Attachment storage
      setAttachmentStorage(createAttachmentStorage());
      
      createCustomStorages();
   }

   /**
    * Creates the map of custom storages based on engine config info.
    */
   protected void createCustomStorages() throws AeException
   {
      Map storages = (Map) getConfig().get(CONFIG_KEY_CUSTOM_STORAGES);
      if (storages != null)
      {
         for (Iterator iter = storages.keySet().iterator(); iter.hasNext(); )
         {
            String storageName = (String) iter.next();
            Map storageConfig = (Map) storages.get(storageName);
            String providerName = (String) storageConfig.get(CONFIG_KEY_PROVIDER);
            IAeStorageProvider provider = getStorageProviderFactory().createCustomStorageProvider(providerName);
            IAeStorage storage = (IAeStorage) AeConfigurationUtil.createConfigSpecificClass(storageConfig, provider, IAeStorageProvider.class);
            getCustomStorages().put(storageName, storage);
         }
      }

   }

   /**
    * Creates the queue storage object.
    */
   protected IAeQueueStorage createQueueStorage()
   {
      IAeQueueStorageProvider provider = getStorageProviderFactory().createQueueStorageProvider();
      AeQueueStorage storage = new AeQueueStorage(provider);
      boolean logCollisions = "true".equals(getConfig().get(CONFIG_KEY_LOGCOLLISIONS)); //$NON-NLS-1$
      storage.setLogCollisions(logCollisions);
      return storage;
   }

   /**
    * Creates the process state storage object.
    */
   protected IAeProcessStateStorage createProcessStateStorage()
   {
      IAeProcessStateStorageProvider provider = getStorageProviderFactory().createProcessStateStorageProvider();
      return new AeProcessStateStorage(provider);
   }

   /**
    * Creates the coordination storage object.
    */
   protected IAeCoordinationStorage createCoordinationStorage()
   {
      IAeCoordinationStorageProvider provider = getStorageProviderFactory().createCoordinationStorageProvider();
      return new AeCoordinationStorage(provider);
   }

   /**
    * Creates the URN storage object.
    */
   protected IAeURNStorage createURNStorage()
   {
      IAeURNStorageProvider provider = getStorageProviderFactory().createURNStorageProvider();
      return new AeURNStorage(provider);
   }

   /**
    * Creates the transmission manager storage object.
    */
   protected IAeTransmissionTrackerStorage createTransmissionTrackerStorage()
   {
      IAeTransmissionTrackerStorageProvider provider = getStorageProviderFactory().createTransmissionTrackerStorageProvider();
      return new AeTransmissionTrackerStorage(provider);
   }
   
   /**
    * Creates the attachment storage object.
    */
   protected IAeAttachmentStorage createAttachmentStorage()
   {
      IAeAttachmentStorageProvider provider = getStorageProviderFactory().createAttachmentStorageProvider();
      return new AePersistentAttachmentStorage(provider);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getQueueStorage()
    */
   public IAeQueueStorage getQueueStorage()
   {
      return mQueueStorage;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getProcessStateStorage()
    */
   public IAeProcessStateStorage getProcessStateStorage()
   {
      return mProcessStateStorage;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getCoordinationStorage()
    */
   public IAeCoordinationStorage getCoordinationStorage()
   {
      return mCoordinationStorage;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getURNStorage()
    */
   public IAeURNStorage getURNStorage()
   {
      return mURNStorage;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getTransmissionTrackerStorage()
    */
   public IAeTransmissionTrackerStorage getTransmissionTrackerStorage()
   {
      return mTransmissionTrackerStorage;
   }
   
   
   public IAeAttachmentStorage getAttachmentStorage()
   {
      return mAttachmentStorage;
   }

   /**
    * Updates the DB config file with changes from the config.
    */
   protected class AeStorageConfigUpdater implements IAeStorageChangeListener
   {
      /**
       * @see org.activebpel.rt.bpel.config.IAeStorageChangeListener#storageConstantsChanged(java.util.Map)
       */
      public void storageConstantsChanged(Map aMap)
      {
         getDBConfig().reload(aMap);
      }
   }

   /**
    * Initializes the store.  Checks for required upgrades to the schema and performs
    * each upgrade in sequence.
    *
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#init()
    */
   public void init() throws AeException
   {
   }

   /**
    * Creates the counter storage object.
    *
    * @throws AeException
    */
   protected IAeCounterStore createCounterStorage() throws AeException
   {
      Map subConfig = (Map) getConfig().get(IAeEngineConfiguration.COUNTER_STORE_ENTRY);
      if (AeUtil.notNullOrEmpty(subConfig))
      {
         String className = (String) subConfig.get(IAeEngineConfiguration.CLASS_ENTRY);
         if (AeUtil.isNullOrEmpty(className))
         {
            throw new AeException(AeMessages.getString("AeStorageFactory.ERROR_SPF_CLASS_MISSING")); //$NON-NLS-1$
         }

         try
         {
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor(new Class[] { Map.class });
            return (IAeCounterStore) constructor.newInstance(new Object[] { subConfig });
         }
         catch (Exception e)
         {
            if (!(e instanceof AeException))
            {
               e = new AeException(AeMessages.getString("AeStorageFactory.ERROR_CREATING_COUNTER_STORE"), e); //$NON-NLS-1$
            }

            throw (AeException) e;
         }
      }
      else
      {
         return null;
      }
   }

   /**
    * Sets the configuration map.
    */
   protected void setConfig(Map config)
   {
      mConfig = config;
   }

   /**
    * Returns the configuration map.
    */
   protected Map getConfig()
   {
      return mConfig;
   }

   /**
    * Sets the DB configuration.
    */
   protected void setDBConfig(AeStorageConfig aDBConfig)
   {
      mStorageConfig = aDBConfig;
   }

   /**
    * Returns the DB configuration.
    */
   protected AeStorageConfig getDBConfig()
   {
      return mStorageConfig;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getCounterStore()
    */
   public IAeCounterStore getCounterStore()
   {
      return mCounterStore;
   }

   /**
    * Sets persistent counter store.
    *
    * @param aPersistentCounterStorage
    */
   protected void setCounterStore(IAeCounterStore aPersistentCounterStorage)
   {
      mCounterStore = aPersistentCounterStorage;
   }

   /**
    * @param aCoordinationStorage The coordinationStorage to set.
    */
   protected void setCoordinationStorage(IAeCoordinationStorage aCoordinationStorage)
   {
      mCoordinationStorage = aCoordinationStorage;
   }

   /**
    * @param aProcessStateStorage The processStateStorage to set.
    */
   protected void setProcessStateStorage(IAeProcessStateStorage aProcessStateStorage)
   {
      mProcessStateStorage = aProcessStateStorage;
   }

   /**
    * @param aQueueStorage The queueStorage to set.
    */
   protected void setQueueStorage(IAeQueueStorage aQueueStorage)
   {
      mQueueStorage = aQueueStorage;
   }

   /**
    * @param aStorage The uRNStorage to set.
    */
   protected void setURNStorage(IAeURNStorage aStorage)
   {
      mURNStorage = aStorage;
   }

   /**
    * @param aTransmissionTrackerStorage transmission-receive tracker storage.
    */
   protected void setTransmissionTrackerStorage(IAeTransmissionTrackerStorage aTransmissionTrackerStorage)
   {
      mTransmissionTrackerStorage = aTransmissionTrackerStorage;
   }
   
   /**
    * @param aAttachmentStorage attachment storage.
    */
   protected void setAttachmentStorage(IAeAttachmentStorage aAttachmentStorage)
   {
      mAttachmentStorage = aAttachmentStorage;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory#getCustomStorage(java.lang.String)
    */
   public IAeStorage getCustomStorage(String aStorageName)
   {
      IAeStorage storage = (IAeStorage) getCustomStorages().get(aStorageName);
      if (storage == null)
      {
         String errorMessage = AeMessages.format(
               "AeStorageFactory.NoNamedStorageFoundError", new String[] { aStorageName }); //$NON-NLS-1$
         throw new RuntimeException(errorMessage);
      }
      return storage;
   }

   /**
    * @return Returns the storageProviderFactory.
    */
   protected IAeStorageProviderFactory getStorageProviderFactory()
   {
      return mStorageProviderFactory;
   }

   /**
    * @param aStorageProviderFactory The storageProviderFactory to set.
    */
   protected void setStorageProviderFactory(IAeStorageProviderFactory aStorageProviderFactory)
   {
      mStorageProviderFactory = aStorageProviderFactory;
   }

   /**
    * @return Returns the customStorages.
    */
   protected Map getCustomStorages()
   {
      return mCustomStorages;
   }

   /**
    * @param aCustomStorages the customStorages to set
    */
   protected void setCustomStorages(Map aCustomStorages)
   {
      mCustomStorages = aCustomStorages;
   }
}
