// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeAbstractStorageProviderFactory.java,v 1.2 2007/08/17 00:23:09 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageProvider;
import org.activebpel.rt.config.AeConfigurationUtil;

/**
 * Base class for storage provider factories.
 */
public abstract class AeAbstractStorageProviderFactory implements IAeStorageProviderFactory
{
   /** This is a engine config key for the 'custom providers' section. */
   protected static final String CONFIG_KEY_CUSTOM_PROVIDERS = "CustomProviders"; //$NON-NLS-1$

   /** The config. */
   private Map mConfig;

   /**
    * C'tor.
    * 
    * @param aConfig
    * @throws AeException
    */
   public AeAbstractStorageProviderFactory(Map aConfig) throws AeException
   {
      setConfiguration(aConfig);
   }
   
   /**
    * Sets the engine configuration.  This is called from the constructor
    * and is intended to be overridden.
    * 
    * @param aConfig
    * @throws AeException
    */
   protected void setConfiguration(Map aConfig) throws AeException
   {
      setConfig(aConfig);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory#createCustomStorageProvider(java.lang.String)
    */
   public IAeStorageProvider createCustomStorageProvider(String aProviderName)
   {
      Map customProviderConfig = getCustomProviderConfig(aProviderName);
      if (customProviderConfig == null)
      {
         String errorMessage = AeMessages.format(
               "AeAbstractStorageProviderFactory.NoNamedProviderFoundError", new String[] { aProviderName }); //$NON-NLS-1$
         throw new RuntimeException(errorMessage);
      }
      
      try
      {
         return createCustomStorageProvider(customProviderConfig);
      }
      catch (AeException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Creates the custom storage provider.
    * 
    * @param aCustomProviderConfig
    * @throws AeException
    */
   protected IAeStorageProvider createCustomStorageProvider(Map aCustomProviderConfig) throws AeException
   {
      return (IAeStorageProvider) AeConfigurationUtil.createConfigSpecificClass(aCustomProviderConfig,
            getProviderCtorArg(), AeStorageConfig.class);
   }
   
   /**
    * Gets the config for a given provider name.
    * 
    * @param aProviderName
    */
   protected Map getCustomProviderConfig(String aProviderName)
   {
      Map map = (Map) getConfig().get(CONFIG_KEY_CUSTOM_PROVIDERS);
      if (map == null)
         return null;
      
      return (Map) map.get(aProviderName);
   }

   /**
    * Returns the argument that gets passed to the provider instance.
    */
   protected abstract AeStorageConfig getProviderCtorArg();

   /**
    * @return Returns the config.
    */
   protected Map getConfig()
   {
      return mConfig;
   }

   /**
    * @param aConfig the config to set
    */
   protected void setConfig(Map aConfig)
   {
      mConfig = aConfig;
   }
}
