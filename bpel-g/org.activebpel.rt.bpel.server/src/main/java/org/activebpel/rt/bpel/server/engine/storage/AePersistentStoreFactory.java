// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AePersistentStoreFactory.java,v 1.10 2005/11/16 16:48:11 EWittmann Exp $
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

import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * This persistent store factory provides access to a concrete, singleton 
 * implementation of a persistent store factory.  The singleton persistent
 * store factory is used to create instances of the various persistent stores
 * needed by the engine managers.
 */
public class AePersistentStoreFactory
{
   /** The singleton persistent store factory instance. */
   private static IAeStorageFactory sInstance = null;

   /**
    * Returns the singleton persistent store factory instance.
    */
   public static IAeStorageFactory getInstance() throws AeStorageException
   {
      if (sInstance == null)
      {
         throw new AeStorageException(AeMessages.getString("AePersistentStoreFactory.ERROR_0")); //$NON-NLS-1$
      }
      return sInstance;
   }

   /**
    * The Active BPEL engine calls this method in order to initialize the 
    * factory.  The factory is initialized with an engine configuration map.
    * 
    * @param aConfig The engine configuration map.
    */
   public static void init(Map aConfig) throws AeStorageException
   {
      Map factoryMap = (Map) aConfig.get(IAeEngineConfiguration.FACTORY_ENTRY);
      if (AeUtil.isNullOrEmpty(factoryMap))
      {
         throw new AeStorageException(AeMessages.getString("AePersistentStoreFactory.ERROR_1")); //$NON-NLS-1$
      }
      String className = (String) factoryMap.get(IAeEngineConfiguration.CLASS_ENTRY);
      if (className == null)
      {
         throw new AeStorageException(AeMessages.getString("AePersistentStoreFactory.ERROR_2")); //$NON-NLS-1$
      }

      try
      {
         Class clazz = Class.forName(className);
         sInstance = (IAeStorageFactory) clazz.newInstance();
         sInstance.setConfiguration(factoryMap);
         sInstance.init();
      }
      catch (AeStorageException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new AeStorageException(AeMessages.getString("AePersistentStoreFactory.ERROR_3"), e); //$NON-NLS-1$
      }
   }
}
