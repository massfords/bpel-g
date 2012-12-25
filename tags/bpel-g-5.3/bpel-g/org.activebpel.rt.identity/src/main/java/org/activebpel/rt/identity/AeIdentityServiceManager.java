//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/AeIdentityServiceManager.java,v 1.9 2008/03/03 20:02:17 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeConfigChangeListener;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpel.impl.AeManagerAdapter;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.identity.search.IAeIdentitySearch;
import org.activebpel.rt.util.AeUtil;

/**
 * Manager for providing access to the Identity search service.
 */
public class AeIdentityServiceManager extends AeManagerAdapter implements IAeIdentityServiceManager, IAeConfigChangeListener
{
   /** Indicates if the manager was started. */
   private boolean mStarted;
   /** Indicates if the service is enabled. */
   private boolean mEnabled;
   /** Indicates if the service provider data is configured. */
   private boolean mConfigured;   
   /** Identity search instance.*/
   private IAeIdentitySearch mSearchInstance;
   
   /**
    * Default constructor.
    */
   public AeIdentityServiceManager(Map aConfig)
   {
      super();      
   }

   /**
    * @return the started
    */
   protected boolean isStarted()
   {
      return mStarted;
   }

   /**
    * @param aStarted the started to set
    */
   protected void setStarted(boolean aStarted)
   {
      mStarted = aStarted;
   }
   
   /**
    * @return the enabled
    */
   protected boolean isEnabled()
   {
      return mEnabled;
   }

   /**
    * @param aEnabled the enabled to set
    */
   protected void setEnabled(boolean aEnabled)
   {
      mEnabled = aEnabled;
   }
   
   /**
    * @return the configured
    */
   protected boolean isConfigured()
   {
      return mConfigured;
   }

   /**
    * @param aConfigured the configured to set
    */
   protected void setConfigured(boolean aConfigured)
   {
      mConfigured = aConfigured;
   }

   /**
    * @return the searchInstance
    */
   protected IAeIdentitySearch getSearchInstance()
   {
      return mSearchInstance;
   }

   /**
    * @param aSearchInstance the searchInstance to set
    */
   protected void setSearchInstance(IAeIdentitySearch aSearchInstance)
   {
      mSearchInstance = aSearchInstance;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#start()
    */
   public void start() throws Exception
   {
      AeEngineFactory.getEngineConfig().getUpdatableEngineConfig().addConfigChangeListener( this );
      IAeUpdatableEngineConfig config = (IAeUpdatableEngineConfig) AeEngineFactory.getEngineConfig().getUpdatableEngineConfig();
      AeIdentityConfig providerConfig = AeIdentityConfig.getFromConfig(config);
      if (providerConfig.isConfigured() && providerConfig.isEnabled())
      {
         initSearchInstance();
      }
      setStarted(true);      
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#stop()
    */
   public void stop()
   {
      AeEngineFactory.getEngineConfig().getUpdatableEngineConfig().removeConfigChangeListener( this );
      setStarted(false);   
   }   
   
   /**
    * @see org.activebpel.rt.bpel.config.IAeConfigChangeListener#updateConfig(org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig)
    */
   public void updateConfig(IAeUpdatableEngineConfig aConfig)
   {
      try
      {
         initSearchInstance();
      }
      catch (AeIdentityException ie)
      {
         if (ie.getCode() == AeIdentityException.CONFIGURATION_EXCEPTION)
         {
            AeException.info(ie.getMessage());
         }
         else
         {
            AeException.logWarning(ie.getMessage());
         }
      }
      catch(Throwable t)
      {         
         AeException.logWarning(t.getMessage());
      }
   }
   
   /**
    * Creates and deploys the search web service.
    * @throws Exception
    */
   protected void initSearchInstance() throws Exception
   {
      try
      {
         setSearchInstance( createSearchInstance() );
      }
      catch(Throwable t)
      {  
         setSearchInstance(null);         
         if (t instanceof Exception)
         {
            throw (Exception) t;
         }
         else
         {
            throw new Exception(t);
         }
      }      
   }
   
   /**
    * @see org.activebpel.rt.identity.IAeIdentityServiceManager#getIdentitySearch()
    */
   public IAeIdentitySearch getIdentitySearch() throws AeIdentityException
   {
      if (!isStarted())
      {
         AeIdentityException ex = new AeIdentityException(AeMessages.getString("AeIdentityServiceManager.MANAGER_NOT_STARTED")); //$NON-NLS-1$
         ex.setCode(AeIdentityException.UNINITIALIZED_EXCEPTION);
         throw ex;
      }
      else if ( !isEnabled() && isConfigured() && getSearchInstance() != null )
      {
         AeIdentityException ex = new AeIdentityException(AeMessages.getString("AeIdentityServiceManager.MANAGER_NOT_ENABLED")); //$NON-NLS-1$
         ex.setCode(AeIdentityException.UNINITIALIZED_EXCEPTION);
         throw ex;         
      }      
      else if ( !isConfigured() || getSearchInstance() == null )
      {
         AeIdentityException ex = new AeIdentityException(AeMessages.getString("AeIdentityServiceManager.MANAGER_NOT_CONFIGURED")); //$NON-NLS-1$
         ex.setCode(AeIdentityException.UNINITIALIZED_EXCEPTION);
         throw ex;         
      }
      else
      {
         return getSearchInstance();
      }
      
   }
   
   /**
    * Creates a IAeIdentitySearch implementation.
    * @return AeIdentitySearch
    * @throws AeIdentityException
    */
   protected IAeIdentitySearch createSearchInstance() throws AeIdentityException
   {
      IAeUpdatableEngineConfig config = (IAeUpdatableEngineConfig) AeEngineFactory.getEngineConfig().getUpdatableEngineConfig();
      AeIdentityConfig providerConfig = AeIdentityConfig.getFromConfig(config);
      setEnabled( providerConfig.isEnabled() );
      setConfigured( providerConfig.isConfigured() );
      Map customManagersMap = config.getMapEntry(IAeEngineConfiguration.CUSTOM_MANAGERS_ENTRY);
      if (AeUtil.isNullOrEmpty(customManagersMap))
      {
         AeIdentityException ex = createConfigNotFoundException(IAeEngineConfiguration.CUSTOM_MANAGERS_ENTRY);
         throw ex;
      }
      
      Map identityServiceManagerMap = (Map) customManagersMap.get(AeIdentityConfig.IDENTITY_SERVICE_MANAGER_ENTRY);
      if (AeUtil.isNullOrEmpty(identityServiceManagerMap))
      {
         String entryPath = IAeEngineConfiguration.CUSTOM_MANAGERS_ENTRY + "/" + AeIdentityConfig.IDENTITY_SERVICE_MANAGER_ENTRY; //$NON-NLS-1$
         AeIdentityException ex = createConfigNotFoundException(entryPath);
         throw ex;
      }
      
      Map identitySearchMap =  (Map) identityServiceManagerMap.get(AeIdentityConfig.IDENTITY_SEARCH_ENTRY);
      if (AeUtil.isNullOrEmpty(identitySearchMap))
      {
         String entryPath = IAeEngineConfiguration.CUSTOM_MANAGERS_ENTRY + "/" + AeIdentityConfig.IDENTITY_SERVICE_MANAGER_ENTRY //$NON-NLS-1$
               + "/" + AeIdentityConfig.IDENTITY_SEARCH_ENTRY; //$NON-NLS-1$
         AeIdentityException ex = createConfigNotFoundException(entryPath);
         throw ex;
      } 
      
      try
      {
         return AeIdentityFactory.createIdentitySearch(identitySearchMap);
      }
      catch(AeIdentityException aie)
      {
         throw aie;
      }
      catch(Throwable t)
      {
         throw new AeIdentityException(t);
      }
   }
   
   /**
    * Convinience method to create formatted message exception.
    * @param aConfigPath
    */
   protected AeIdentityException createConfigNotFoundException(String aConfigPath)
   {
      AeIdentityException ex = new AeIdentityException(AeMessages.format("AeIdentityServiceManager.MANAGER_CONFIGURATION_ENTRY_NOT_FOUND", aConfigPath) ); //$NON-NLS-1$
      ex.setCode(AeIdentityException.UNINITIALIZED_EXCEPTION);
      return ex;               
   }
}
