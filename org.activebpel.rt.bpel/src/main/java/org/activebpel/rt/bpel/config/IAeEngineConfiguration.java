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
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;

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
   /** Name of entry for map of overrides for sql config files */
   public static final String SQL_CONSTANTS = "SQLConfigConstants"; //$NON-NLS-1$
   /** Name of entry for the setting the catalog cache size. */
   public static final String RESOURCE_FACTORY_CACHE_SIZE_ENTRY = "cache.max"; //$NON-NLS-1$
   /** Name of entry which contains the engine admin implementation. */
   public static final String ENGINE_ADMIN_IMPL_ENTRY = "EngineAdminImpl"; //$NON-NLS-1$
   /** Name of entry which contains the engine implementation. */
   public static final String ENGINE_IMPL_ENTRY = "EngineImpl"; //$NON-NLS-1$
   /** Name of entry where custom function contexts are specified. */
   public static final String FUNCTION_CONTEXT_LOCATOR_ENTRY = "FunctionContextLocator"; //$NON-NLS-1$ 
   /** Name of entry where custom function contexts are specified. */
   public static final String STANDARD_FUNCTION_CONTEXTS_ENTRY = "StandardFunctionContexts"; //$NON-NLS-1$ 
   /** Name of entry for loading external xpath function contexts */
   public static final String FUNCTION_CONTEXTS_ENTRY = "FunctionContexts"; //$NON-NLS-1$
   /** Name of entry for loading external xpath function context namespace */
   public static final String FUNCTION_CONTEXT_NAMESPACE_ENTRY = "Namespace"; //$NON-NLS-1$
   /** Attribute for loading external xpath function context class path */
   public static final String FUNCTION_CONTEXT_CLASSPATH_ENTRY = "Classpath"; //$NON-NLS-1$
   /** Attribute indicating type of custom function */
   public static final String FUNCTION_CONTEXT_TYPE = "FunctionType"; //$NON-NLS-1$
   /** Name of entry for loading standard bpel function context. */
   public static final String BPEL_FUNCTION_CONTEXT = "BpelFunctionContext"; //$NON-NLS-1$
   /** Name of entry for loading standard bpel function context. */
   public static final String BPEL20_FUNCTION_CONTEXT = "Bpel20FunctionContext"; //$NON-NLS-1$
   /** Name of entry for loading bpel extension function context. */
   public static final String BPEL_EXT_FUNCTION_CONTEXT = "BpelExtFunctionContext"; //$NON-NLS-1$
   /** Name of entry for loading class related entries*/
   public static final String CLASS_ENTRY = "Class"; //$NON-NLS-1$
   /** Name of entry for loading factory related entries*/
   public static final String FACTORY_ENTRY = "Factory"; //$NON-NLS-1$
   /** Name of entry for turning logging on */
   public static final String LOGGING_ENTRY = "Logging"; //$NON-NLS-1$
   /** Name of the entry for specifying a logging dir */
   public static final String LOGGING_DIR_ENTRY = "Logging.Dir"; //$NON-NLS-1$
   /** Name of entry for the setting the partner definition factory */
   public static final String PDEF_FACTORY_ENTRY = "PartnerDefinitionFactory"; //$NON-NLS-1$
   /** Name of entry for the setting the catalog factory */
   public static final String CATALOG_ENTRY = "Catalog"; //$NON-NLS-1$
   /** Name of entry for the setting the replace wsdl flag */
   public static final String REPLACE_EXISTING_ENTRY = "replace.existing"; //$NON-NLS-1$
   /** Name of entry for setting the Lock manager. */
   public static final String LOCK_MANAGER_ENTRY = "LockManager"; //$NON-NLS-1$
   /** Name of entry for setting the Attachment manager. */
   public static final String ATTACHMENT_MANAGER_ENTRY = "AttachmentManager"; //$NON-NLS-1$
   /** Name of entry for setting the URN resolver */
   public static final String URN_RESOLVER_ENTRY = "URNResolver"; //$NON-NLS-1$
   /** Name of entry for setting the process manager. */
   public static final String PROCESS_MANAGER_ENTRY = "ProcessManager"; //$NON-NLS-1$
   /** Name of entry for Queue Manager map. */
   public static final String QUEUE_MANAGER_ENTRY = "QueueManager"; //$NON-NLS-1$
   /** Name of entry for setting the auto-start flag */
   public static final String AUTO_START_ENTRY = "AutoStart"; //$NON-NLS-1$
   /** Name of entry for persistent store map. */
   public static final String PERSISTENT_STORE_ENTRY = "PersistentStore"; //$NON-NLS-1$
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
   /** Name of entry for deployment factory. */
   public static final String DEPLOYMENT_FACTORY_ENTRY = "DeploymentFactory"; //$NON-NLS-1$
   /** Name of entry for deployment log. */
   public static final String DEPLOYMENT_LOG_ENTRY = "DeploymentLog"; //$NON-NLS-1$
   /** Name of entry for process logger. */
   public static final String PROCESS_LOGGER_ENTRY = "ProcessLogger"; //$NON-NLS-1$
   /** Name of entry for deployment handler. */
   public static final String DEPLOYMENT_HANDLER_ENTRY = "DeploymentHandler"; //$NON-NLS-1$
   /** Name of entry for deployment provider. */
   public static final String DEPLOYMENT_PROVIDER = "DeploymentProvider"; //$NON-NLS-1$
   /** Name of entry which contains the remote debug engine map. */
   public static final String REMOTE_DEBUG_ENTRY = "RemoteDebug"; //$NON-NLS-1$
   /** Name of entry which contains the remote debug engine implementation. */
   public static final String REMOTE_DEBUG_IMPL_ENTRY = "RemoteDebugImpl"; //$NON-NLS-1$
   /** Name of entry which contains the event handler locator. */
   public static final String EVENT_HANDLER_LOCATOR_ENTRY = "EventHandlerLocator"; //$NON-NLS-1$
   /** Name of entry which contains the breakpoint handler locator. */
   public static final String BREAKPOINT_HANDLER_LOCATOR_ENTRY = "BreakpointHandlerLocator"; //$NON-NLS-1$
   /** Name of entry for the invoke handler factory */
   public static final String INVOKE_HANDLER_FACTORY = "InvokeHandlerFactory"; //$NON-NLS-1$
   /** Name of entry for the receive handler factory */
   public static final String RECEIVE_HANDLER_FACTORY = "ReceiveHandlerFactory"; //$NON-NLS-1$
   /** Name of entry for work manager map. */
   public static final String WORK_MANAGER_ENTRY = "WorkManager"; //$NON-NLS-1$
   /** Name of entry for setting the work manager jndi location */
   public static final String WM_JNDI_NAME_ENTRY = "JNDILocation"; //$NON-NLS-1$
   /** Name of entry for timer manager map. */
   public static final String TIMER_MANAGER_ENTRY = "TimerManager"; //$NON-NLS-1$
   /** Name of entry for setting the timer manager jndi location */
   public static final String TM_JNDI_NAME_ENTRY = "JNDILocation"; //$NON-NLS-1$
   /** Name of entry for transaction manager factory. */
   public static final String TRANSACTION_MANAGER_FACTORY_ENTRY = "TransactionManagerFactory"; //$NON-NLS-1$
   /** Name of entry for persistent counter store. */
   public static final String COUNTER_STORE_ENTRY = "CounterStore"; //$NON-NLS-1$
   /** Name of entry for counter store JNDI location. */
   public static final String CS_JNDI_NAME_ENTRY = "JNDILocation"; //$NON-NLS-1$
   /** Name of entry for the expression language factory. */
   public static final String EXPRESSION_FACTORY = "ExpressionLanguageFactory"; //$NON-NLS-1$
   /** Name of entry for the function validator factory. */
   public static final String FUNCTION_VALIDATORS = "FunctionValidators"; //$NON-NLS-1$
   /** Name of entry for suspending processes on uncaught faults. */
   public static final String SUSPEND_PROCESS_ON_UNCAUGHT_FAULT_ENTRY = "SuspendProcessOnUncaughtFault"; //$NON-NLS-1$
   /** Name of entry for suspending processes on invoke recovery. */
   public static final String SUSPEND_PROCESS_ON_INVOKE_RECOVERY_ENTRY = "SuspendProcessOnInvokeRecovery"; //$NON-NLS-1$
   /** Name of entry for enabling process restart */
   public static final String RESTART_SUSPENDED_PROCESS = "RestartSuspendedProcess"; //$NON-NLS-1$
   /** Name of entry for setting the coordination manager. */
   public static final String COORDINATION_MANAGER_ENTRY = "CoordinationManager"; //$NON-NLS-1$ 
   /** Name of entry for specifying logger handlers */
   public static final String LOG_HANDLER_ENTRY = "LogHandler"; //$NON-NLS-1$
   /** Name of entry for setting an engine listener */
   public static final String ENGINE_LISTENER = "EngineListener"; //$NON-NLS-1$
   /** Name of entry for setting a policy mapper */
   public static final String POLICY_MAPPER = "PolicyMapper"; //$NON-NLS-1$
   /** Name of entry for process work manager map. */
   public static final String PROCESS_WORK_MANAGER_ENTRY = "ProcessWorkManager"; //$NON-NLS-1$
   /** Name of entry for maximum number of work requests to schedule per-process. */
   public static final String PROCESS_WORK_COUNT_ENTRY = "ProcessWorkCount"; //$NON-NLS-1$
   /** Name of entry for the storage provider factory map. */
   public static final String STORAGE_PROVIDER_FACTORY = "StorageProviderFactory"; //$NON-NLS-1$
   /** Name of entry for transmit, receive/reply tracker. */
   public static final String TRANSMISSION_TRACKER_ENTRY = "TransmissionTracker"; //$NON-NLS-1$
   /** Name of entry which contains list of durable reply receiver factories. */
   public static final String DURABLE_REPLY_FACTORIES = "ReplyFactories"; //$NON-NLS-1$
   /** Name of entry for the web service timeouts for invokes, this remains for legacy reasons. */
   public static final String WEB_SERVICE_TIMEOUT = "WebServiceTimeout"; //$NON-NLS-1$
   /** Name of entry for the web service timeouts for receives */
   public static final String WEB_SERVICE_RECEIVE_TIMEOUT = "WebServiceReceiveTimeout"; //$NON-NLS-1$
   /** Name of entry for the SOAP Message factory */
   public static final String SOAP_MESSAGE_FACTORY = "SOAPMessageFactory"; //$NON-NLS-1$
   /** Name of entry for scheduler manager. */
   public static final String SCHEDULE_MANAGER_ENTRY = "ScheduleManager"; //$NON-NLS-1$
   /** Name of entry for security provider map. */
   public static final String SECURITY_PROVIDER_ENTRY = "SecurityProvider"; //$NON-NLS-1$
   /** Name of entry for custom managers. */
   public static final String CUSTOM_MANAGERS_ENTRY = "CustomManagers"; //$NON-NLS-1$
   /** Name of entry for child work managers map. */
   public static final String CHILD_WORK_MANAGERS_ENTRY = "ChildWorkManagers"; //$NON-NLS-1$
   /** Name of entry for child work manager maximum work count. */
   public static final String MAX_WORK_COUNT_ENTRY = "MaxWorkCount"; //$NON-NLS-1$
   /** Name of the child work manager for alarm work. */
   public static final String ALARM_CHILD_WORK_MANAGER_ENTRY = "Alarm"; //$NON-NLS-1$
   /** Default maximum work count for child work managers. */
   public static final int DEFAULT_CHILD_MAX_WORK_COUNT = 5;
   
      
   /**
    * Returns true if the engine should start immediately after being created or
    * if it should wait for someone to start it manually (ie through the admin page). 
    */
   public boolean isAutoStart();
   
   /**
    * Returns a description for this engine configuration.
    */
   public String getDescription();

   /**
    * Returns the function associated with the passed namespace.
    * 
    * @param aFunctionName
    * @param aNamespace
    * @return IAeExpressionFunction Returns the associated function.
    * @throws AeUnresolvableException Thrown if no function is found.
    */
   public IAeFunction getFunction(String aFunctionName, String aNamespace)
      throws AeUnresolvableException;

   /**
    * Gets a list of namespaces for all configured function contexts.
    */
   public Set getFunctionContextNamespaceList();
   
   /**
    * Gets the expression language factory configured in the engine config.  Defaults to the standard
    * AeExpressionLanguageFactory which includes support for XPath 1.0 as required by the BPEL spec.
    * 
    * @throws AeException
    */
   public IAeExpressionLanguageFactory getExpressionLanguageFactory() throws AeException;
   
   /**
    * Gets the function validator factory with any configured extensions defined in the engine config.
    * 
    * @throws AeException
    */
   public IAeFunctionValidatorFactory getFunctionValidatorFactory() throws AeException;

   /**
    * Return the class name of the IAePartnerAddressingFactory impl.
    */
   public String getPartnerAddressingFactoryClassName();

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
    * Returns the class name of the process manager.
    */
   public String getProcessManagerClassName();
   
   /**
    * Accessor for catalog factory class name.
    */
   public String getCatalogFactoryClassName();
   
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
