// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/config/AeDefaultEngineConfiguration.java,v 1.41.4.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeExpressionLanguageFactory;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.config.AeConfiguration;
import org.activebpel.rt.config.AeConfigurationUtil;

/**
 * Default engine configuration implementation.
 */
public class AeDefaultEngineConfiguration extends AeConfiguration implements IAeEngineConfiguration,
      IAeUpdatableEngineConfig, Cloneable
{
   /** Default name for static engine config file. */
   public static final String DEFAULT_CONFIG_FILE = "aeEngineConfig.xml"; //$NON-NLS-1$
   //////////////////////////////////////////////////////
   // default values for some of the main entries.
   //////////////////////////////////////////////////////
   /** max number of join style correlationSet combinations that get computed for dispatching inbound receives (persistence only) */
   private static final int DEFAULT_MAX_JOIN_COMBINATIONS = 3;
   /** Default description of the configuration. */
   private static final String DEFAULT_CONFIG_DESCRIPTION = ""; //$NON-NLS-1$
   /** default timeout value for unmatched correlated receives */
   public static final int UNMATCHED_RECEIVE_TIMEOUT_DEFAULT = 30;
   /** default timeout value for web service operations */
   public static final int WEB_SERVICE_TIMEOUT_DEFAULT = 600; // 10 minutes
   /** default min number of threads for the work manager's thread pool */
   public static final int WORKMANAGER_THREAD_MIN_DEFAULT = 1;
   /** default max number of threads for the work manager's thread pool */
   public static final int WORKMANAGER_THREAD_MAX_DEFAULT = 10;
   /** default maximum number of work requests to schedule per-process */
   public static final int PROCESS_WORK_COUNT_DEFAULT = 10;
   /** default resource cache maximum value */
   private static final int DEFAULT_RESOURCE_CACHE_MAX = 100;
   /** Default logging dir - defaults to {user.home}/AeBpelEngine*/
   private static final String LOGGING_DIR_DEFAULT = new File(System.getProperty("user.home"), "AeBpelEngine").getPath(); //$NON-NLS-1$ //$NON-NLS-2$
   /** Configuration change listeners. */
   protected List mListeners = new ArrayList();
   /** Storage listeners  */
   protected List mStorageListeners = new ArrayList();

   /**
    * Creates the default expression language factory when none is found in the configuration
    * file.
    */
   protected IAeExpressionLanguageFactory createDefaultExpressionLanguageFactory()
   {
      return new AeExpressionLanguageFactory();
   }

   /**
    * Load the engine configuration from the xml in the passed stream.
    * @param aConfigStream a stream containing the configuration xml.
    * @param aClassLoader the classloader for function contexts.
    * @return the config created from the passed stream.
    */
   public static AeDefaultEngineConfiguration loadConfig(InputStream aConfigStream, ClassLoader aClassLoader)
   {
      AeDefaultEngineConfiguration config = new AeDefaultEngineConfiguration();
      loadConfig(config, aConfigStream, aClassLoader);
      return config;
   }

   /**
    * Load the engine configuration from the xml in the passed stream.
    * @param aDefaultConfig the config to load
    * @param aConfigStream a stream containing the configuration xml.
    * @param aClassLoader the classloader for function contexts.
    */
   public static void loadConfig(AeDefaultEngineConfiguration aDefaultConfig, InputStream aConfigStream, ClassLoader aClassLoader)
   {
      if (aConfigStream == null)
         return;

      try
      {
         Map entries = AeConfigurationUtil.loadConfig(new InputStreamReader(aConfigStream));
         aDefaultConfig.setEntries( entries );
      }
      catch(Throwable ex)
      {
         AeException.logError(ex, AeMessages.getString("AeDefaultEngineConfiguration.ERROR_16")); //$NON-NLS-1$
      }

      return;
   }

   /**
    * Setter for the logging base dir.
    * @param aBaseDir The base directory for all system log files.
    */
   protected void setLoggingBaseDir( String aBaseDir )
   {
      setEntry( LOGGING_DIR_ENTRY, aBaseDir );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getLoggingBaseDir()
    */
   public String getLoggingBaseDir()
   {
      return getEntry( LOGGING_DIR_ENTRY, LOGGING_DIR_DEFAULT );
   }

   /**
    * Sets the description for this configuration.
    */
   public void setDescription(String aDescription)
   {
      setEntry( CONFIG_DESCRIPTION_ENTRY, aDescription );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getDescription()
    */
   public String getDescription()
   {
      return getEntry( CONFIG_DESCRIPTION_ENTRY, DEFAULT_CONFIG_DESCRIPTION );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getMaxCorrelationCombinations()
    */
   public int getMaxCorrelationCombinations()
   {
      return getIntegerEntry("MaxCorrelationCombinations", DEFAULT_MAX_JOIN_COMBINATIONS); //$NON-NLS-1$
   }

   //----------[ MUTABLE CONFIGURATION SETTINGS ]-------------------------------

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getUnmatchedCorrelatedReceiveTimeout()
    */
   public int getUnmatchedCorrelatedReceiveTimeout()
   {
      return getIntegerEntry(UNMATCHED_RECEIVE_TIMEOUT_ENTRY, UNMATCHED_RECEIVE_TIMEOUT_DEFAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getWebServiceInvokeTimeout()
    */
   public int getWebServiceInvokeTimeout()
   {
      return getIntegerEntry(WEB_SERVICE_TIMEOUT, WEB_SERVICE_TIMEOUT_DEFAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setWebServiceInvokeTimeout(int)
    */
   public void setWebServiceInvokeTimeout(int aTimeout)
   {
      setIntegerEntry(WEB_SERVICE_TIMEOUT, aTimeout);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getWebServiceReceiveTimeout()
    */
   public int getWebServiceReceiveTimeout()
   {
      return getIntegerEntry(WEB_SERVICE_RECEIVE_TIMEOUT, WEB_SERVICE_TIMEOUT_DEFAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setWebServiceReceiveTimeout(int)
    */
   public void setWebServiceReceiveTimeout(int aTimeout)
   {
      setIntegerEntry(WEB_SERVICE_RECEIVE_TIMEOUT, aTimeout);
   }

   /**
    * Sets the timeout value for unmatched correlated receives.
    * @param aTimeoutValue
    */
   public void setUnmatchedCorrelatedReceiveTimeout(int aTimeoutValue)
   {
      setIntegerEntry(UNMATCHED_RECEIVE_TIMEOUT_ENTRY, aTimeoutValue);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getWorkManagerThreadPoolMax()
    */
   public int getWorkManagerThreadPoolMax()
   {
      Map workMgrMap = getMapEntry(WORK_MANAGER_ENTRY);
      return getIntegerEntryInternal( workMgrMap, WORKMANAGER_THREAD_MAX_ENTRY, WORKMANAGER_THREAD_MAX_DEFAULT );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getWorkManagerThreadPoolMin()
    */
   public int getWorkManagerThreadPoolMin()
   {
      Map workMgrMap = getMapEntry(WORK_MANAGER_ENTRY);
      return getIntegerEntryInternal( workMgrMap, WORKMANAGER_THREAD_MIN_ENTRY, WORKMANAGER_THREAD_MIN_DEFAULT );
   }

   /**
    * Setter for the work manager thread pool max thread count
    * @param aInt
    */
   public void setWorkManagerThreadPoolMax(int aInt)
   {
      Map workMgrMap = getMapEntry(WORK_MANAGER_ENTRY, true);
      workMgrMap.put(WORKMANAGER_THREAD_MAX_ENTRY, Integer.toString(aInt));
   }

   /**
    * Setter for the work manager thread pool min thread count
    * @param aInt
    */
   public void setWorkManagerThreadPoolMin(int aInt)
   {
      Map workMgrMap = getMapEntry(WORK_MANAGER_ENTRY, true);
      workMgrMap.put(WORKMANAGER_THREAD_MIN_ENTRY, Integer.toString(aInt));
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#allowEmptyQuerySelection()
    */
   public boolean allowEmptyQuerySelection()
   {
      return getBooleanEntry(ALLOW_EMPTY_QUERY_SELECTION_ENTRY, false);
   }

   /**
    * Sets the allow empty query selection results on or off.
    */
   public void setAllowEmptyQuerySelection(boolean aBoolean)
   {
      setBooleanEntry(ALLOW_EMPTY_QUERY_SELECTION_ENTRY, aBoolean);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#allowCreateXPath()
    */
   public boolean allowCreateXPath()
   {
      return getBooleanEntry(ALLOW_CREATE_XPATH_ENTRY, false);
   }

   /**
    * Sets the allow create xpath option on or off.
    */
   public void setAllowCreateXPath(boolean aBoolean)
   {
      setBooleanEntry(ALLOW_CREATE_XPATH_ENTRY, aBoolean);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#validateServiceMessages()
    */
   public boolean validateServiceMessages()
   {
      return getBooleanEntry(VALIDATE_SERVICE_MESSAGES_ENTRY, false);
   }

   /**
    * Sets whether inbound and outbound messages are validated.
    */
   public void setValidateServiceMessages(boolean aBoolean)
   {
      setBooleanEntry(VALIDATE_SERVICE_MESSAGES_ENTRY, aBoolean);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getLoggingFilter()
    */
   public String getLoggingFilter()
   {
      return getEntry(LOGGING_ENTRY);
   }

   /**
    * Setter for logging property.
    * @param aFilterName
    */
   public void setLoggingFilter(String aFilterName)
   {
      setEntry(LOGGING_ENTRY, aFilterName);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setResourceCacheMax(int)
    */
   public void setResourceCacheMax(int aMax)
   {
      Map params = getMapEntry(CATALOG_ENTRY);
      params.put( RESOURCE_FACTORY_CACHE_SIZE_ENTRY, String.valueOf(aMax) );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getResourceCacheMax()
    */
   public int getResourceCacheMax()
   {
      Map params = getMapEntry( CATALOG_ENTRY );
      return getIntegerEntryInternal( params, RESOURCE_FACTORY_CACHE_SIZE_ENTRY, DEFAULT_RESOURCE_CACHE_MAX );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isAllowedRolesEnforced()
    */
   public boolean isAllowedRolesEnforced()
   {
      return getBooleanEntry(ALLOWED_ROLES_ENFORCED, true);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setAllowedRolesEnforced(boolean)
    */
   public void setAllowedRolesEnforced(boolean aFlag)
   {
      setBooleanEntry(ALLOWED_ROLES_ENFORCED, aFlag);
   }

   //----------[ CHANGE LISTENER METHODS ]--------------------------------------

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#update()
    */
   public void update()
   {
      notifyListeners();
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#addConfigChangeListener(org.activebpel.rt.bpel.config.IAeConfigChangeListener)
    */
   public synchronized void addConfigChangeListener(IAeConfigChangeListener aListener)
   {
      mListeners.add( aListener );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#removeConfigChangeListener(org.activebpel.rt.bpel.config.IAeConfigChangeListener)
    */
   public synchronized void removeConfigChangeListener(IAeConfigChangeListener aListener)
   {
      mListeners.remove(aListener);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#addStorageChangeListener(org.activebpel.rt.bpel.config.IAeStorageChangeListener)
    */
   public synchronized void addStorageChangeListener(IAeStorageChangeListener aListener)
   {
      mStorageListeners.add( aListener );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#removeStorageChangeListener(org.activebpel.rt.bpel.config.IAeStorageChangeListener)
    */
   public synchronized void removeStorageChangeListener(IAeStorageChangeListener aListener)
   {
      mStorageListeners.remove(aListener);
   }

   /**
    * Notify any registered listeners of a config change.
    */
   protected void notifyListeners()
   {
      List listeners = null;
      synchronized( mListeners )
      {
         if( !mListeners.isEmpty() )
         {
            listeners = new ArrayList(mListeners);
         }
      }

      if( listeners != null )
      {
         for( Iterator iter = listeners.iterator(); iter.hasNext(); )
         {
            IAeConfigChangeListener listener = (IAeConfigChangeListener) iter.next();
            listener.updateConfig( this );
         }
      }
   }

   /**
    * Walk all of the listeners and notify them of the change.
    *
    * @param aMap
    */
   protected void notifyStorageListeners(Map aMap)
   {
      List listeners = null;
      synchronized( mStorageListeners )
      {
         if( !mStorageListeners.isEmpty() )
         {
            listeners = new ArrayList(mStorageListeners);
         }
      }

      if( listeners != null )
      {
         for( Iterator iter = listeners.iterator(); iter.hasNext(); )
         {
            IAeStorageChangeListener listener = (IAeStorageChangeListener) iter.next();
            listener.storageConstantsChanged( aMap );
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getUpdatableEngineConfig()
    */
   public IAeUpdatableEngineConfig getUpdatableEngineConfig()
   {
      return this;
   }

   /**
    * This clone simply returns a config that contains the same map entries. It
    * does not include the listeners or the function context container.
    *
    * @see java.lang.Object#clone()
    */
   public Object clone()
   {
      AeDefaultEngineConfiguration copy = new AeDefaultEngineConfiguration();
      HashMap map = new HashMap(getEntries());
      copy.setEntries(map);
      return copy;
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isResourceReplaceEnabled()
    */
   public boolean isResourceReplaceEnabled()
   {
      Map wsdlParams = getMapEntry( CATALOG_ENTRY );
      return getBooleanEntryInternal( wsdlParams, REPLACE_EXISTING_ENTRY, false );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setResourceReplaceEnabled(boolean)
    */
   public void setResourceReplaceEnabled(boolean aFlag)
   {
      Map params = getMapEntry(CATALOG_ENTRY);
      params.put( REPLACE_EXISTING_ENTRY, Boolean.toString(aFlag) );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isSuspendProcessOnUncaughtFault()
    */
   public boolean isSuspendProcessOnUncaughtFault()
   {
      return getBooleanEntry(SUSPEND_PROCESS_ON_UNCAUGHT_FAULT_ENTRY, false);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setSuspendProcessOnUncaughtFault(boolean)
    */
   public void setSuspendProcessOnUncaughtFault(boolean aFlag)
   {
      setBooleanEntry(SUSPEND_PROCESS_ON_UNCAUGHT_FAULT_ENTRY, aFlag);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getProcessWorkCount()
    */
   public int getProcessWorkCount()
   {
	   return getIntegerEntry(PROCESS_WORK_COUNT_ENTRY, PROCESS_WORK_COUNT_DEFAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setProcessWorkCount(int)
    */
   public void setProcessWorkCount(int aProcessWorkCount)
   {
	   setIntegerEntry(PROCESS_WORK_COUNT_ENTRY, aProcessWorkCount);
   }

   /**
    * This method takes a configuration map for a config class and instantiates that
    * class.  This involves some simple java reflection to find the proper
    * constructor and then calling that constructor.  Any class can use this method,
    * as long as the class in question has a constructor that takes the configuration
    * map.  Returns null if no entry is found.
    *
    * @param aConfigName The name of a MapEntry in the config.
    * @throws AeException
    * @return A config class.
    */
   public Object createConfigSpecificClass(String aConfigName) throws AeException
   {
      return AeConfigurationUtil.createConfigSpecificClass(getMapEntry(aConfigName));
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isSuspendProcessOnInvokeRecovery()
    */
   public boolean isSuspendProcessOnInvokeRecovery()
   {
      return getBooleanEntry(SUSPEND_PROCESS_ON_INVOKE_RECOVERY_ENTRY, false);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setSuspendProcessOnInvokeRecovery(boolean)
    */
   public void setSuspendProcessOnInvokeRecovery(boolean aFlag)
   {
      setBooleanEntry(SUSPEND_PROCESS_ON_INVOKE_RECOVERY_ENTRY, aFlag);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isProcessRestartEnabled()
    */
   public boolean isProcessRestartEnabled()
   {
      return getBooleanEntry(RESTART_SUSPENDED_PROCESS, false);
   }
}
