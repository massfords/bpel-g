// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/config/IAeEngineConfiguration.java,v 1.48 2008/02/26 01:47:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.config;

import java.util.Map;

/**
 * Interface representing the static configuration settings for the BPEL engine.
 */
public interface IAeEngineConfiguration
{
   //////////////////////////////////////////////////////////////
   // Standard named elements for values
   //////////////////////////////////////////////////////////////
   
   /** Name of entry for determining if the allowedRoles attribute on services is enforced for unauthenticated requests */
   public static final String ALLOWED_ROLES_ENFORCED = "AllowedRolesEnforced"; //$NON-NLS-1$

   /** Name of entry for describing the configuration. */
   public static final String CONFIG_DESCRIPTION_ENTRY = "Description"; //$NON-NLS-1$
   /** Name of entry for setting the max number of correlation combinations to query for when matching an inbound receive to IMA's with join style correlations */
   public static final String MAX_CORRELATION_COMBINATIONS = "MaxCorrelationCombinations"; //$NON-NLS-1$
   /** Name of entry for setting the unmatched correlated receive timeout */
   public static final String UNMATCHED_RECEIVE_TIMEOUT_ENTRY = "UnmatchedReceiveTimeout"; //$NON-NLS-1$
   /** Name of entry for setting the min number of threads for the work manager */
   public static final String WORKMANAGER_THREAD_MIN_ENTRY = "DefaultWorkManager.ThreadPool.Min"; //$NON-NLS-1$
   /** Name of entry for setting the max number of threads for the work manager */
   public static final String WORKMANAGER_THREAD_MAX_ENTRY = "DefaultWorkManager.ThreadPool.Max"; //$NON-NLS-1$
   /** Name of entry for turning on/off ValidateServiceMessages */
   public static final String VALIDATE_SERVICE_MESSAGES_ENTRY = "ValidateServiceMessages"; //$NON-NLS-1$
   /** Name of entry for turning on/off AllowEmptyQuerySelection */
   public static final String ALLOW_EMPTY_QUERY_SELECTION_ENTRY = "AllowEmptyQuerySelection"; //$NON-NLS-1$
   /** Name of entry for turning on/off AllowCreateXPath */
   public static final String ALLOW_CREATE_XPATH_ENTRY = "AllowCreateXPath"; //$NON-NLS-1$
   /** Name of entry for the setting the catalog cache size. */
   public static final String RESOURCE_FACTORY_CACHE_SIZE_ENTRY = "cache.max"; //$NON-NLS-1$
   /** Name of entry for loading class related entries*/
   public static final String CLASS_ENTRY = "Class"; //$NON-NLS-1$
   /** Name of entry for loading factory related entries*/
   public static final String FACTORY_ENTRY = "Factory"; //$NON-NLS-1$
   /** Name of entry for turning logging on */
   public static final String LOGGING_ENTRY = "Logging"; //$NON-NLS-1$
   /** Name of the entry for specifying a logging dir */
   public static final String LOGGING_DIR_ENTRY = "Logging.Dir"; //$NON-NLS-1$
   /** Name of entry for the setting the catalog factory */
   public static final String CATALOG_ENTRY = "Catalog"; //$NON-NLS-1$
   /** Name of entry for the setting the replace wsdl flag */
   public static final String REPLACE_EXISTING_ENTRY = "replace.existing"; //$NON-NLS-1$
   /** Name of entry for persistent store/database type. */
   public static final String DATABASE_TYPE_ENTRY = "DatabaseType"; //$NON-NLS-1$
   /** Name of entry for persistent store/version. */
   public static final String PERSISTENT_VERSION_ENTRY = "Version"; //$NON-NLS-1$
   /** Name of entry for persistent store/datasource map. */
   public static final String DATASOURCE_ENTRY = "DataSource"; //$NON-NLS-1$
   /** Name of entry for setting the datasource/jndi location for data source lookups */
   public static final String DS_JNDI_NAME_ENTRY = "JNDILocation"; //$NON-NLS-1$
   /** Name of entry for setting the datasource/user name for data source lookups */
   public static final String DS_USERNAME_ENTRY = "Username"; //$NON-NLS-1$
   /** Name of entry for setting the datasource/password for data source lookups */
   public static final String DS_PASSWORD_ENTRY = "Password"; //$NON-NLS-1$
   /** Name of entry for work manager map. */
   public static final String WORK_MANAGER_ENTRY = "WorkManager"; //$NON-NLS-1$
   /** Name of entry for timer manager map. */
   public static final String TIMER_MANAGER_ENTRY = "TimerManager"; //$NON-NLS-1$
   /** Name of entry for setting the timer manager jndi location */
   public static final String TM_JNDI_NAME_ENTRY = "JNDILocation"; //$NON-NLS-1$
   /** Name of entry for suspending processes on uncaught faults. */
   public static final String SUSPEND_PROCESS_ON_UNCAUGHT_FAULT_ENTRY = "SuspendProcessOnUncaughtFault"; //$NON-NLS-1$
   /** Name of entry for suspending processes on invoke recovery. */
   public static final String SUSPEND_PROCESS_ON_INVOKE_RECOVERY_ENTRY = "SuspendProcessOnInvokeRecovery"; //$NON-NLS-1$
   /** Name of entry for enabling process restart */
   public static final String RESTART_SUSPENDED_PROCESS = "RestartSuspendedProcess"; //$NON-NLS-1$
   /** Name of entry for maximum number of work requests to schedule per-process. */
   public static final String PROCESS_WORK_COUNT_ENTRY = "ProcessWorkCount"; //$NON-NLS-1$
   /** Name of entry for the storage provider factory map. */
   public static final String STORAGE_PROVIDER_FACTORY = "StorageProviderFactory"; //$NON-NLS-1$
   /** Name of entry for the web service timeouts for invokes, this remains for legacy reasons. */
   public static final String WEB_SERVICE_TIMEOUT = "WebServiceTimeout"; //$NON-NLS-1$
   /** Name of entry for the web service timeouts for receives */
   public static final String WEB_SERVICE_RECEIVE_TIMEOUT = "WebServiceReceiveTimeout"; //$NON-NLS-1$
   /** Name of entry for scheduler manager. */
   public static final String SCHEDULE_MANAGER_ENTRY = "ScheduleManager"; //$NON-NLS-1$
   /** Name of entry for child work manager maximum work count. */
   public static final String MAX_WORK_COUNT_ENTRY = "MaxWorkCount"; //$NON-NLS-1$
   /** Name of the child work manager for alarm work. */
   public static final String ALARM_CHILD_WORK_MANAGER_ENTRY = "Alarm"; //$NON-NLS-1$
   /** Default maximum work count for child work managers. */
   public static final int DEFAULT_CHILD_MAX_WORK_COUNT = 5;
   
      
   /**
    * Returns a description for this engine configuration.
    */
   public String getDescription();

   /**
    * Return the base directory for all application log files. 
    */
   public String getLoggingBaseDir();

   /**
    * Get a string entry in the config with the passed name.
    * Returning the passed default if entry is unavailable.
    */
   public String getEntry(String aName, String aDefault);
   
   /**
    * Returns the entry given a path
    * @param aPath
    */
   public Object getEntryByPath(String aPath);
   
   /**
    * Returns the int value of the given entry or the default value if the entry
    * didn't exist or produced a NumberFormatException.
    * @param aPath
    * @param aDefault
    */
   public int getIntEntryByPath(String aPath, int aDefault);

   /**
    * Get a mapped entry in the config with the passed name.
    * Returns null if no map entry found or entry is not a map.
    */
   public Map getMapEntry(String aName);

   /**
    * Get a boolean entry in the config with the passed name.
    * Returning the passed default if entry is unavailable.
    */
   public boolean getBooleanEntry(String aName, boolean aDefault);

   /**
    * Get an integer entry in the config with the passed name.
    * Returning the passed default if entry is unavailable.
    */
   public int getIntegerEntry(String aName, int aDefault);

   /**
    * Accessor for the mutable engine configuration settings.
    */
   public IAeUpdatableEngineConfig getUpdatableEngineConfig();
   
   /**
    * If true the bpel engine should automatically create XPath nodes that don't exist.
    * @return boolean true to create false indicates engine should cause selectionFailure fault.
    */
   public boolean allowCreateXPath();
   
   /**
    * If true the bpel engine should allow xpath queries which return 0 nodes.
    * @return boolean false indicates 0 node selection querys should cause selectionFailure fault.
    */
   public boolean allowEmptyQuerySelection();
   
   /**
    * gets the value for the level of logging that is enabled (one of: full, execution, none)
    */
   public String getLoggingFilter();
   
   /**
    * Returns true if engine should validate all inbound and outbound messages.
    * These are the messages used in receive, reply, invoke and onMessage.
    */
   public boolean validateServiceMessages();

   /**
    * Gets the minimum number of threads to have in the work manager's thread pool.
    */
   public int getWorkManagerThreadPoolMin();

   /**
    * Gets the maximum number of threads to have in the work manager's thread pool
    */
   public int getWorkManagerThreadPoolMax();
   
   /**
    * Returns the time the engine will hold onto unmatched correlated receives
    * before rejecting them. This is to address the race condition that is possible
    * with one-way invokes and a resulting asynchronous callback. Briefly, it's
    * possible for a response from a one-way invoke to arrive before the intended
    * receive activity has executed. The spec says that the implementation needs
    * to support that receive arriving prior to the execution of the invoke.
    * The details can be found in Issue 33 of the WS-BPEL issues list.
    *
    * @return int Time in seconds before unmatched correlated receive is timed out
    */
   public int getUnmatchedCorrelatedReceiveTimeout();
   
   /**
    * Returns the max number of correlation combinations that will be queried for
    * when trying to match an inbound receive to IMA's that were queued with multiple
    * join style correlations. With join style correlations, it's difficult to know
    * which correlationSets would have been initiated when trying to dispatch the message
    * so we'll make multiple passes in order to account for the different combinations
    * of some set being initiated and some not. The number of combinations amounts to
    * 2^n so the more combinations you allow the more queries will be run. 
    * 
    * @return int max number of combinations
    */
   public int getMaxCorrelationCombinations();
   
   /**
    * Accessor for resource cache max size.
    */
   public int getResourceCacheMax();
   
   /**
    * Returns true if our custom authentication/authorization handlers for Axis
    * should allow unauthenticated requests to pass through. 
    */
   public boolean isAllowedRolesEnforced();

   /**
    * Return true if existing resources (resources mapped to the same location hint
    * via the catalog) should be replaced on a subsequent deployment.
    */
   public boolean isResourceReplaceEnabled();
   
   /**
    * Return true if non-service flow processes should be suspened if they
    * encounter a uncaught fault.
    */
   public boolean isSuspendProcessOnUncaughtFault();

   /**
    * Return <code>true</code> if and only if a process should be suspended if
    * it has a non-durable invoke pending during process recovery.
    */
   public boolean isSuspendProcessOnInvokeRecovery();

   /**
    * Returns the maximum number of work requests to schedule per-process.
    */
   public int getProcessWorkCount();
   
   /**
    * Returns the amount of time in seconds that the engine will wait for a request-response
    * message to be completed. 
    * @return amount of time in seconds to wait
    */
   public int getWebServiceInvokeTimeout();

   /**
    * Returns the amount of time in seconds that the engine will wait for an inbound message activity.
    * @return amount of time in seconds to wait
    */
   public int getWebServiceReceiveTimeout();
   
   /**
    * Returns true if process restart is enabled. If enabled, users will be able
    * to restart suspended processes with their create instance message.
    * 
    * This feature is disabled by default since it has some additional overhead
    * and the current implementation of it simply terminates the process prior
    * to replaying it. The issue with the termination is that it doesn't execute
    * any termination or compensation handlers. A future version of this will 
    * address these two concerns and likely remove this flag from the config.
    */
   public boolean isProcessRestartEnabled();
}
