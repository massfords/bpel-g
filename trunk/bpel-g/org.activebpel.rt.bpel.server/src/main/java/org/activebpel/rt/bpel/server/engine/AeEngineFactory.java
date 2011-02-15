//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AeEngineFactory.java,v 1.93 2008/02/17 21:38:49 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;

import javax.naming.InitialContext;
import javax.xml.soap.MessageFactory;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEngineListener;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.registry.AeEngineConfigExtensionRegistry;
import org.activebpel.rt.bpel.def.visitors.AeDefVisitorFactory;
import org.activebpel.rt.bpel.function.AeFunctionContextContainer;
import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionContext;
import org.activebpel.rt.bpel.impl.AeSOAPMessageFactory;
import org.activebpel.rt.bpel.impl.IAeAttachmentManager;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.impl.IAeLockManager;
import org.activebpel.rt.bpel.impl.IAeManager;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.IAeQueueManager;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.addressing.IAePartnerAddressing;
import org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerAddressingProvider;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.admin.rdebug.server.AeRemoteDebugImpl;
import org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin;
import org.activebpel.rt.bpel.server.catalog.IAeCatalog;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandlerFactory;
import org.activebpel.rt.bpel.server.deploy.IAePolicyMapper;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory;
import org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory;
import org.activebpel.rt.bpel.server.engine.transaction.IAeTransactionManagerFactory;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLoggerFactory;
import org.activebpel.rt.bpel.server.security.IAeSecurityProvider;
import org.activebpel.rt.bpel.urn.IAeURNResolver;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.timer.AeTimerManager;
import org.activebpel.timer.IAeStoppableTimerManager;
import org.activebpel.work.AeExceptionReportingWorkManager;
import org.activebpel.work.IAeProcessWorkManager;
import org.activebpel.work.IAeStoppableWorkManager;
import org.activebpel.work.child.AeConfigAwareChildWorkManager;
import org.activebpel.work.factory.AeDefaultWorkManagerFactory;
import org.activebpel.work.factory.IAeWorkManagerFactory;
import org.activebpel.work.input.IAeInputMessageWork;
import org.activebpel.work.input.IAeInputMessageWorkManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import commonj.timers.TimerManager;
import commonj.work.Work;
import commonj.work.WorkManager;

/**
 * Maintains a singleton instance of the engine.
 */
public class AeEngineFactory
{
   /** The singleton engine instance */
   private static AeBpelEngine sEngine;

   /** The logger for creating process log files */
   private static IAeProcessLogger sProcessLogger;

   /** WorkManager impl for asynchronous work */
   private static WorkManager sWorkManager;

   /** Timer Manager impl for scheduling alarms */
   private static TimerManager sTimerManager;

   /** The current configuration settings */
   private static IAeEngineConfiguration sConfig;

   /** Deployment handler factory. */
   private static IAeDeploymentHandlerFactory sDeploymentHandlerFactory;
  

   /** Global catalog. */
   private static IAeCatalog sCatalog;

   /** Deployment logger factory. */
   private static IAeDeploymentLoggerFactory sDeploymentLoggerFactory;

   /** Flag indicating the persistent store is available in the configuration. */
   private static boolean sPersistentStoreConfiguration;

   /** String indicating the error message from the persistent store if it is not ready for use. */
   private static String sPersistentStoreError;

   /** The singleton remote debug engine instance */
   private static IAeBpelAdmin sRemoteDebugImpl;

   /** Policy Mapper to create handler chains from policy assertions. */
   private static IAePolicyMapper sPolicyMapper;

   /** Work manager for per-process work requests */
   private static IAeProcessWorkManager sProcessWorkManager;

   /** Flag indicating if using internal WorkManager version */
   private static boolean sInternalWorkManager;

   /** Transmission and receive/reply manager. */
   private static IAeTransmissionTracker sTransmissionTracker;

   /** Child work managers indexed by name. */
   private static Map sChildWorkManagers = new HashMap();

   /** Input message work manager. */
   private static IAeInputMessageWorkManager sInputMessageWorkManager;
   
   private static ApplicationContext sContext;
   
   /**
    * Pre-initialize the engine to set up storage work, policy mappers and timer managers.
    * @param aConfig
    * @throws AeException
    */
   public static void preInit(IAeEngineConfiguration aConfig) throws AeException
   {
      // Load the logging handler if one was specified
      Map logMap = aConfig.getMapEntry(IAeEngineConfiguration.LOG_HANDLER_ENTRY);
      if (logMap != null)
      {
         for (Iterator iter = logMap.keySet().iterator(); iter.hasNext();)
         {
            String handlerClass = (String)logMap.get(iter.next());
            try
            {
               Class handlerClazz = Class.forName(handlerClass);
               Handler logHandler = (Handler)handlerClazz.newInstance();
               AeException.getLogger().addHandler(logHandler);
            }
            catch(Throwable t)
            {
               t.printStackTrace();
            }
         }
      }

      setEngineConfig(aConfig);
      
      sContext = new ClassPathXmlApplicationContext("bpelg-applicationContext.xml");

      // Initialize the work manager
      initializeWorkManager();

      // Initialize the timer manager
      initializeTimerManager();

      // Initialize the policy mapper
      initializePolicyMapper();
   }

   /**
    * Initialize the BPEL engine.
    *
    * @throws AeException
    */
   public static void init() throws AeException
   {
      // create the managers
      IAeProcessManager processManager       = createProcessManager();
      IAeQueueManager queueManager           = sContext.getBean(IAeQueueManager.class);
      IAeLockManager lockManager             = sContext.getBean(IAeLockManager.class);
      IAeAttachmentManager attachmentManager = sContext.getBean(IAeAttachmentManager.class);

      // Create the remote debug engine implementation instance.
      sRemoteDebugImpl = createRemoteDebugImpl( getEngineConfig().getMapEntry(IAeEngineConfiguration.REMOTE_DEBUG_ENTRY) );

      // Use the managers to create the bpel engine.
      // The class name for the bpel engine can be supplied dynamically, but this
      // factory assumes that it's derived from AeBpelEngine
      sEngine = createNewEngine(queueManager, processManager, lockManager, attachmentManager, sContext.getBean(IAeExpressionLanguageFactory.class));
      sEngine.setFunctionValidatorFactory(sContext.getBean(IAeFunctionValidatorFactory.class));

      IAeEngineListener engineListener = createEngineListener();
      if (engineListener != null)
         sEngine.addEngineListener(engineListener);

      // set the coordination manager before calling the engine's create() (and hence initialization of all managers)
      sEngine.setCoordinationManager( getCoordinationManager());

      // create transmission/receive id tracker.
      sTransmissionTracker = createTransmissionTracker();
      sEngine.setTransmissionTracker(sTransmissionTracker);
      
      // create any custom managers that are defined in the config
      createCustomManagers();

      // create engine and init its managers.
      sEngine.create();

      sEngine.setPlanManager(getDeploymentProvider());
      processManager.setPlanManager(getDeploymentProvider());

      // Create the process logger
      sProcessLogger = createProcessLogger();
      sProcessLogger.setEngine(getEngine());

      sCatalog = (IAeCatalog)createConfigObject( IAeEngineConfiguration.CATALOG_ENTRY );
      sDeploymentLoggerFactory = (IAeDeploymentLoggerFactory)createConfigObject( IAeEngineConfiguration.DEPLOYMENT_LOG_ENTRY );
      sDeploymentHandlerFactory = (IAeDeploymentHandlerFactory)createConfigObject( IAeEngineConfiguration.DEPLOYMENT_HANDLER_ENTRY );

      // Create the work manager for per-process work requests.
      sProcessWorkManager = createProcessWorkManager();

      // create urn resolver
      IAeURNResolver urnResolver = createURNResolver();
      sEngine.setURNResolver(urnResolver);

      // create SOAP Message factory
      AeSOAPMessageFactory.setSOAPMessageFactory(createSOAPMessageFactory());
      
      // install extension registry
      try
      {
         AeEngineConfigExtensionRegistry registry = new AeEngineConfigExtensionRegistry((AeDefaultEngineConfiguration) AeEngineFactory.getEngineConfig());
         AeDefVisitorFactory.setExtensionRegistry(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, registry);
      }
      catch (ClassNotFoundException ex)
      {
         // DO Nothing if can not find or install an extension registry
      }
   }

   /**
    * Create an object instance from config params.
    * @param aKey Key to top level entry with sub-entry that specifies the class.
    * @throws AeException
    */
   protected static Object createConfigObject( String aKey ) throws AeException
   {
      Map entryParams = getEngineConfig().getMapEntry(aKey);
      if (AeUtil.isNullOrEmpty(entryParams))
      {
         throw new AeException(AeMessages.getString("AeEngineFactory.ERROR_0") + aKey ); //$NON-NLS-1$
      }
      return createConfigSpecificClass( entryParams );
   }

   /**
    * Start the BPEL engine.  Should not be called until all of
    * the expected deployments have been completed (as previously persisted
    * processes will assume their resources are available as soon as they
    * start up again).
    * @throws AeException 
    */
   public static void start() throws AeException
   {
      if (isEngineStorageReadyRetest())
      {
         // Start only this engine - startAll() is not necessary on startup - each node will
         // call their own start method on startup.
         sEngine.start();
      }
      else
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeEngineFactory.ERROR_1")); //$NON-NLS-1$
      }
   }

   /**
    * Creates the class that listens for process events and logs them.
    */
   protected static IAeProcessLogger createProcessLogger() throws AeException
   {
      Map configMap = getEngineConfig().getMapEntry(IAeEngineConfiguration.PROCESS_LOGGER_ENTRY);
      return (IAeProcessLogger) createConfigSpecificClass(configMap);
   }

   /**
    * Creates the new remote debug engine instance.
    * @param aMap
    * @return IAeBPelAdmin
    * @throws AeException
    */
   protected static IAeBpelAdmin createRemoteDebugImpl(Map aMap) throws AeException
   {
      try
      {
         String debugClassName;
         String eventLocatorClass;

         // Defaults if values not configured in the given map.
         String defaultRDebugClass = AeRemoteDebugImpl.class.getName();
         String defaultEventLocatorClass = "org.activebpel.rt.axis.bpel.rdebug.client.AeEventHandlerLocator"; //$NON-NLS-1$

         if ( aMap == null || aMap.isEmpty() )
         {
            debugClassName = defaultRDebugClass;
            eventLocatorClass = defaultEventLocatorClass;
         }
         else
         {
            // Get the remote debug impl class name.
            debugClassName = (String) aMap.get(IAeEngineConfiguration.REMOTE_DEBUG_IMPL_ENTRY);
            if ( AeUtil.isNullOrEmpty(debugClassName) )
               debugClassName = defaultRDebugClass;

            // Get the handler locator class names.
            eventLocatorClass = (String) aMap.get(IAeEngineConfiguration.EVENT_HANDLER_LOCATOR_ENTRY);
            if ( AeUtil.isNullOrEmpty(eventLocatorClass) )
               eventLocatorClass = defaultEventLocatorClass;
         }

         Class c = Class.forName(debugClassName);
         Constructor constructor = c.getConstructor( new Class[] { String.class } );
         return (IAeBpelAdmin) constructor.newInstance( new Object[] {eventLocatorClass} );
      }
      catch (Exception e)
      {
         throw new AeException(AeMessages.getString("AeEngineFactory.ERROR_5"), e); //$NON-NLS-1$
      }
   }

   /**
    * Creates the durable transmit/receive manager.
    *
    * @throws AeException
    */
   protected static IAeTransmissionTracker createTransmissionTracker() throws AeException
   {
      Map configMap = getEngineConfig().getMapEntry(IAeEngineConfiguration.TRANSMISSION_TRACKER_ENTRY);
      return (IAeTransmissionTracker) createConfigSpecificClass(configMap);
   }

   /**
    * Creates managers for each of the managers list in the custom
    * "Managers" section of the engine config.
    * 
    * @throws AeException
    */
   protected static void createCustomManagers() throws AeException
   {
      Map configMap = getEngineConfig().getMapEntry(IAeEngineConfiguration.CUSTOM_MANAGERS_ENTRY);
      if (AeUtil.notNullOrEmpty(configMap))
      {
         for (Iterator iter = configMap.keySet().iterator(); iter.hasNext(); )
         {
            String managerName = (String) iter.next();
            Map managerMap = (Map) configMap.get(managerName);
            IAeManager manager = (IAeManager) createConfigSpecificClass(managerMap);
            sEngine.addCustomManager(managerName, manager);
         }
      }
   }

   /**
    * Creates the engine listener specified in the config file
    * @throws AeException
    */
   protected static IAeEngineListener createEngineListener() throws AeException
   {
      Map configMap = getEngineConfig().getMapEntry(IAeEngineConfiguration.ENGINE_LISTENER);
      return configMap != null? (IAeEngineListener) createConfigSpecificClass(configMap) : null;
   }

   /**
    * Creates the new engine instance. This provides a means of changing the underlying
    * engine class based on the config file which is something that we do in order
    * to easily swap in our clustered version of the engine.
    * @param aQueueManager
    * @param aProcessManager
    * @param aLockManager
    * @param aAttachmentManager
    */
   protected static AeBpelEngine createNewEngine(
         IAeQueueManager aQueueManager,
         IAeProcessManager aProcessManager,
         IAeLockManager aLockManager,
         IAeAttachmentManager aAttachmentManager,
         IAeExpressionLanguageFactory aFactory)
   throws AeException
   {
      String engineClass = getEngineConfig().getEntry(
            IAeEngineConfiguration.ENGINE_IMPL_ENTRY, AeBpelEngine.class.getName());

      try
      {
         Class clazz = Class.forName(engineClass);
         Constructor cons =
            clazz.getConstructor(
                  new Class[] {
                        IAeEngineConfiguration.class,
                        IAeQueueManager.class,
                        IAeProcessManager.class,
                        IAeLockManager.class,
                        IAeAttachmentManager.class,
                        IAeExpressionLanguageFactory.class});
         return (AeBpelEngine) cons.newInstance(new Object[] { getEngineConfig(), aQueueManager, aProcessManager, aLockManager, aAttachmentManager, aFactory});
      }
      catch (Exception e)
      {
         throw new AeException(AeMessages.getString("AeEngineFactory.ERROR_6"), e); //$NON-NLS-1$
      }

   }

   /**
    * This method constructs the work manager used by the engine through the
    * configured work manager factory.
    */
   protected static void initializeWorkManager() throws AeException
   {
      IAeWorkManagerFactory factory = null;

      // Try to construct the work manager factory specified in the work manager
      // configuration.
      Map workManagerMap = getEngineConfig().getMapEntry(IAeEngineConfiguration.WORK_MANAGER_ENTRY);
      if (!AeUtil.isNullOrEmpty(workManagerMap))
      {
         Map factoryMap = (Map) workManagerMap.get(IAeEngineConfiguration.FACTORY_ENTRY);
         if (!AeUtil.isNullOrEmpty(factoryMap))
         {
            String className = (String) factoryMap.get(IAeEngineConfiguration.CLASS_ENTRY);
            if (!AeUtil.isNullOrEmpty(className))
            {
               try
               {
                  Class clazz = Class.forName(className);
                  factory = (IAeWorkManagerFactory) clazz.newInstance();
               }
               catch (Exception e)
               {
                  AeException.logError(e, AeMessages.format("AeEngineFactory.ERROR_WorkManagerFactoryClass", className)); //$NON-NLS-1$
               }
            }
         }
      }

      // If the work manager factory was not specified or was invalid, then use
      // the default factory.
      if (factory == null)
      {
         factory = new AeDefaultWorkManagerFactory();
      }

      factory.init(workManagerMap);

      sWorkManager = factory.getWorkManager();
      sInternalWorkManager = factory.isInternalWorkManager();
      sInputMessageWorkManager = factory.getInputMessageWorkManager();

      // Wrap the chosen work manager with one that reports unhandled exceptions.
      sWorkManager = new AeExceptionReportingWorkManager(sWorkManager);

      // Initialize child work managers from WorkManager config map.
      initializeChildWorkManagers(workManagerMap);
   }

   /**
    * Initializes child work managers.
    *
    * @param aWorkManagerConfig
    */
   protected static void initializeChildWorkManagers(Map aWorkManagerConfig)
   {
      // Names of child work managers.
      Set names = new HashSet();

      // Make sure the set includes the Alarm child work manager.
      names.add(IAeEngineConfiguration.ALARM_CHILD_WORK_MANAGER_ENTRY);

      // Scan entries for child work managers.
      if (!AeUtil.isNullOrEmpty(aWorkManagerConfig))
      {
         Map childWorkManagersConfig = (Map) aWorkManagerConfig.get(IAeEngineConfiguration.CHILD_WORK_MANAGERS_ENTRY);
         if (!AeUtil.isNullOrEmpty(childWorkManagersConfig))
         {
            names.addAll(childWorkManagersConfig.keySet());
         }
      }

      // Each instance of AeConfigAwareChildWorkManager will pick up its max
      // work count from the engine configuration. However, if the child work
      // manager is not mentioned in the engine configuration (like, possibly,
      // the Alarm child work manager), then use the default max work count.
      int childMaxWorkCount = IAeEngineConfiguration.DEFAULT_CHILD_MAX_WORK_COUNT;

      // Create each defined child work manager.
      for (Iterator i = names.iterator(); i.hasNext(); )
      {
         try
         {
            String name = (String) i.next();
            
            sChildWorkManagers.put(name, new AeConfigAwareChildWorkManager(name, childMaxWorkCount, getWorkManager()));
         }
         catch (Throwable t)
         {
            AeException.logError(t);
         }
      }
   }

   /**
    * This method initializes the timer manager used by the engine. We will first attempt
    * to lookup the timer manager from the JNDI location specified in the engine config
    * file. If not specified or unable to load, then we will use default timer manager.
    */
   protected static void initializeTimerManager()
   {
      // Make sure we initialize to null, or may not behave properly during servlet hot deploy
      sTimerManager = null;

      Map timerMgrConfigMap = getEngineConfig().getMapEntry(IAeEngineConfiguration.TIMER_MANAGER_ENTRY);
      if (! AeUtil.isNullOrEmpty(timerMgrConfigMap))
      {
         String timerMgrLocation = (String)timerMgrConfigMap.get(IAeEngineConfiguration.TM_JNDI_NAME_ENTRY);
         if (! AeUtil.isNullOrEmpty(timerMgrLocation))
         {
            try
            {
               // Lookup the timer manager from the JNDI location specified in engine config.
               InitialContext ic = new InitialContext();
               sTimerManager = (TimerManager)ic.lookup(timerMgrLocation);
               AeException.info(AeMessages.getString("AeEngineFactory.ERROR_16") + timerMgrLocation); //$NON-NLS-1$
            }
            catch (Exception e)
            {
               AeException.info(AeMessages.getString("AeEngineFactory.ERROR_17") + timerMgrLocation); //$NON-NLS-1$
            }
         }
      }

      // The JNDI location was missing or invalid, so try to construct a timer
      // manager from an explicitly specified class name.
      if ((sTimerManager == null) && !AeUtil.isNullOrEmpty(timerMgrConfigMap))
      {
         String className = (String) timerMgrConfigMap.get(IAeEngineConfiguration.CLASS_ENTRY);
         if (!AeUtil.isNullOrEmpty(className))
         {
            try
            {
               Class clazz = Class.forName(className);
               sTimerManager = (TimerManager) clazz.newInstance();
            }
            catch (Exception e)
            {
               AeException.logError(e, AeMessages.format("AeEngineFactory.ERROR_Instantiation", className)); //$NON-NLS-1$
            }
         }
      }

      // Timer manager not specified or invalid JNDI location given, use default
      if (sTimerManager == null)
      {
         AeException.info(AeMessages.getString("AeEngineFactory.18")); //$NON-NLS-1$
         sTimerManager = new AeTimerManager();
      }
   }

   /**
    * This method initializes the policy mapper used by the engine.
    */
   protected static void initializePolicyMapper() throws AeException
   {
      // Make sure we initialize to null, or may not behave properly during servlet hot deploy
      sPolicyMapper = null;
      // get the main policy mapper
      IAeEngineConfiguration config = getEngineConfig();
      sPolicyMapper = (IAePolicyMapper) createConfigSpecificClass(config.getMapEntry(IAeEngineConfiguration.POLICY_MAPPER));
   }

   /**
    * Factory method for creating the process manager for the engine.  The type
    * of manager to use will be determined based on information found in the
    * engine configuration.
    *
    * @return A process manager.
    */
   protected static IAeProcessManager createProcessManager() throws AeException
   {
      Map configMap = getEngineConfig().getMapEntry(IAeEngineConfiguration.PROCESS_MANAGER_ENTRY);
      return (IAeProcessManager) createConfigSpecificClass(configMap);
   }

   /**
    * Factory method for creating the urn resolver.
    */
   private static IAeURNResolver createURNResolver() throws AeException
   {
      Map configMap = getEngineConfig().getMapEntry(IAeEngineConfiguration.URN_RESOLVER_ENTRY);
      return (IAeURNResolver) createConfigSpecificClass(configMap);
   }

   /**
    * This method takes a configuration map for a manager and instantiates that
    * manager.  This involves some simple java reflection to find the proper
    * constructor and then calling that constructor.
    *
    * @param aConfig The engine configuration map for the manager.
    * @return An engine manager (alert, queue, etc...).
    */
   public static Object createConfigSpecificClass(Map aConfig) throws AeException
   {
      if (AeUtil.isNullOrEmpty(aConfig))
      {
         throw new AeException(AeMessages.getString("AeEngineFactory.ERROR_10")); //$NON-NLS-1$
      }
      return AeConfigurationUtil.createConfigSpecificClass(aConfig);
   }

   /**
    * Gets the process logger.
    */
   public static IAeProcessLogger getLogger()
   {
      return sProcessLogger;
   }

   /**
    * Gets the partner addressing
    */
   public static IAePartnerAddressing getPartnerAddressing()
   {
      return sContext.getBean(IAePartnerAddressing.class);
   }

   /**
    * Gets the policy mapper
    */
   public static IAePolicyMapper getPolicyMapper()
   {
      return sPolicyMapper;
   }

   /**
    * Gets a ref to the administration API
    */
   public static IAeEngineAdministration getEngineAdministration()
   {
       if (sContext == null)
           return null;
      return sContext.getBean(IAeEngineAdministration.class);
   }

   /**
    * Getter for the deployment descriptor.
    */
   public static IAeDeploymentProvider getDeploymentProvider()
   {
      return sContext.getBean(IAeDeploymentProvider.class);
   }

   /**
    * Getter for the engine.
    */
   public static IAeBusinessProcessEngineInternal getEngine()
   {
      return sEngine;
   }

   /**
    * Gets the installed work manager.
    */
   public static WorkManager getWorkManager()
   {
      return sWorkManager;
   }

   /**
    * Gets the installed timer manager.
    */
   public static TimerManager getTimerManager()
   {
      return sTimerManager;
   }

   /**
    * Convenience method that schedules work to be done and translates any work exceptions
    * into our standard business process exception.
    * @param aWork
    */
   public static void schedule(Work aWork) throws AeBusinessProcessException
   {
     getWorkManager().schedule(aWork);
   }

   /**
    * Convenience method for stopping the work manager.
    */
   public static void shutDownWorkManager()
   {
      // Stop is only available in our default implementation of the work manager
      if (getWorkManager() instanceof IAeStoppableWorkManager)
         ((IAeStoppableWorkManager)getWorkManager()).stop();

      // Notify the input message work manager that we're shutting down.
      getInputMessageWorkManager().stop();
   }

   /**
    * Convenience method for stopping the timer manager.
    */
   public static void shutDownTimerManager()
   {
      // Stop is only available in our default implementation of the timer manager
      if (getTimerManager() instanceof IAeStoppableTimerManager)
         ((IAeStoppableTimerManager) getTimerManager()).stop();
   }

   /**
    * Set the engine configuration settings.
    */
   protected static void setEngineConfig(IAeEngineConfiguration aConfig)
   {
      sConfig = aConfig;
   }

   /**
    * Accessor for engine configuration settings.
    */
   public static IAeEngineConfiguration getEngineConfig()
   {
      return sConfig;
   }

   /**
    * Convenience method for getting the function validator factory.
    * 
    *
    * @throws AeException
    */
   public static IAeFunctionValidatorFactory getFunctionValidatorFactory() throws AeException
   {
      return getEngine().getFunctionValidatorFactory();
   }

   /**
    * Accessor for the partner addressing provider.
    */
   public static IAePartnerAddressingProvider getPartnerAddressProvider()
   {
      return sContext.getBean(IAePartnerAddressingProvider.class);
   }

   /**
    * Accessor for the global catalog.
    */
   public static IAeCatalog getCatalog()
   {
      return sCatalog;
   }

   /**
    * Accessor for deployment logger factory.
    */
   public static IAeDeploymentLoggerFactory getDeploymentLoggerFactory()
   {
      return sDeploymentLoggerFactory;
   }

   /**
    * Access the deployment handler factory.
    */
   public static IAeDeploymentHandlerFactory getDeploymentHandlerFactory()
   {
      return sDeploymentHandlerFactory;
   }

   /**
    * Returns the coordination manager instance.
    */
   public static IAeCoordinationManagerInternal getCoordinationManager()
   {
      return sContext.getBean(IAeCoordinationManagerInternal.class);
   }

   /**
    * Returns the transmission-receive id tracker instance.
    */
   public static IAeTransmissionTracker getTransmissionTracker()
   {
      return sTransmissionTracker;
   }

   /**
    * @return Storage transaction manager factory.
    */
   public static IAeTransactionManagerFactory getTransactionManagerFactory()
   {
      return sContext.getBean(IAeTransactionManagerFactory.class);
   }

   /**
    * Returns true if the current configuration contains a persistent store.
    */
   public static boolean isPersistentStoreConfiguration()
   {
      return sPersistentStoreConfiguration;
   }

   /**
    * Returns true if the persistent storage is ready for use.
    */
   public static boolean isPersistentStoreReadyForUse()
   {
      return sContext.getBean(IAeStorageFactory.class).isReady();
   }

   /**
    * If engine storage is not already in ready state then we will
    * check it again before returning status.
    */
   public static boolean isEngineStorageReadyRetest() throws AeException
   {
      try
      {
         if(! isEngineStorageReady()) {
             sContext.getBean(IAeStorageProviderFactory.class).init();
         }
         return isEngineStorageReady();
      }
      catch (AeStorageException ex)
      {
         AeException.logWarning(""); //$NON-NLS-1$
         ex.logError();
         AeException.logWarning(""); //$NON-NLS-1$
         return false;
      }
   }

   /**
    * Returns true if the engine storage system is ready, either not persistent
    * or the persistent and the storage is ready.
    */
   public static boolean isEngineStorageReady()
   {
      if(! isPersistentStoreConfiguration())
         return true;
      return isPersistentStoreReadyForUse();
   }

   /**
    * Returns the error message if the persistent store is not ready for use.
    */
   public static String getPersistentStoreError()
   {
      return sPersistentStoreError;
   }

   /**
    * Sets the error message if the persistent store is not ready for use.
    */
   public static void setPersistentStoreError(String aString)
   {
      sPersistentStoreError = aString;
   }

   /**
    * Gets the remote debug engine instance.
    * @return IAeBpelAdmin
    */
   public static IAeBpelAdmin getRemoteDebugImpl()
   {
      return sRemoteDebugImpl;
   }

   /**
    * Gets the URN manager that is used to resolve URN to URL
    */
   public static IAeURNResolver getURNResolver()
   {
      return getEngine().getURNResolver();
   }

   /**
    * Constructs the work manager for per-process work requests.
    */
   protected static IAeProcessWorkManager createProcessWorkManager() throws AeException
   {
      Map config = getEngineConfig().getMapEntry(IAeEngineConfiguration.PROCESS_WORK_MANAGER_ENTRY);
      return (IAeProcessWorkManager) createConfigSpecificClass(config);
   }

   /**
    * Returns the work manager for per-process work requests.
    */
   public static IAeProcessWorkManager getProcessWorkManager()
   {
      return sProcessWorkManager;
   }

   /**
    * Schedules per-process work for the given process.
    *
    * @param aProcessId
    * @param aWork
    */
   public static void schedule(long aProcessId, Work aWork) throws AeBusinessProcessException
   {
      getProcessWorkManager().schedule(aProcessId, aWork);
   }

   /**
    * Returns True if using internal WorkManager implementation or False if using server version.
    */
   public static boolean isInternalWorkManager()
   {
      return sInternalWorkManager;
   }

   /**
    * @return configured instance of SOAP MessageFactory.
    * If no entry defined in AeEngineConfig or if there is an exception creating the instance,
    * the default for the server is used.
    * @throws AeBusinessProcessException
    */
   private static MessageFactory createSOAPMessageFactory() //throws AeBusinessProcessException
   {
      Map config = getEngineConfig().getMapEntry(IAeEngineConfiguration.SOAP_MESSAGE_FACTORY);

      if (!AeUtil.isNullOrEmpty(config))
      {
         String factoryName = (String) config.get(IAeEngineConfiguration.CLASS_ENTRY);
         if (!AeUtil.isNullOrEmpty(factoryName))
         {
            try
            {
               return (MessageFactory) Class.forName(factoryName).newInstance();
            }
            catch (Throwable ae)
            {
               // if we can't create the class, fall through to the default
               AeException.logWarning(AeMessages.format("AeEngineFactory.8", factoryName)); //$NON-NLS-1$
            }
         }
      }

      try
      {
         // default for app server
         return MessageFactory.newInstance();
      }
      catch (Throwable ae)
      {
         // FIXME this should really be loaded through a manager and not through the engine. Move it!
         //throw new AeBusinessProcessException(ae.getMessage(), ae);
          return null;
      }
   }

   /**
    * Returns the SOAP Message factory used by the engine
    */
   public static MessageFactory getSOAPMessageFactory()
   {
      return AeSOAPMessageFactory.getSOAPMessageFactory();
   }
   
   /**
    * Returns the security provider.
    */
   public static IAeSecurityProvider getSecurityProvider()
   {
      return sContext.getBean(IAeSecurityProvider.class);
   }

   /**
    * Schedules work on the alarm child work manager.
    *
    * @param aWork
    */
   public static void scheduleAlarmWork(Work aWork) throws AeBusinessProcessException
   {
      try
      {
         scheduleChildWork(IAeEngineConfiguration.ALARM_CHILD_WORK_MANAGER_ENTRY, aWork);
      }
      catch (AeBusinessProcessException e)
      {
         AeException.logError(e);

         // This should never happen, but let's guarantee that we always
         // schedule alarm work.
         schedule(aWork);
      }
   }

   /**
    * Schedules work on a child work manager.
    *
    * @param aName
    * @param aWork
    */
   public static void scheduleChildWork(String aName, Work aWork) throws AeBusinessProcessException
   {
      WorkManager childWorkManager = getChildWorkManager(aName);
      if (childWorkManager == null)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeEngineFactory.ERROR_NoChildWorkManager", aName)); //$NON-NLS-1$
      }

      try
      {
         childWorkManager.schedule(aWork);
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeEngineFactory.ERROR_15"), e); //$NON-NLS-1$
      }
   }

   /**
    * @return the child work manager with the given name
    */
   public static WorkManager getChildWorkManager(String aName)
   {
      return (WorkManager) sChildWorkManagers.get(aName);
   }

   /**
    * Schedules input message work on the configured input message work manager.
    *
    * @param aProcessId
    * @param aInputMessageWork
    */
   public static void scheduleInputMessageWork(long aProcessId, IAeInputMessageWork aInputMessageWork) throws AeBusinessProcessException
   {
      getInputMessageWorkManager().schedule(aProcessId, aInputMessageWork);
   }
   
   /**
    * @return the input message work manager configured for this engine
    */
   public static IAeInputMessageWorkManager getInputMessageWorkManager()
   {
      return sInputMessageWorkManager;
   }

    public static IAeExpressionLanguageFactory getExpressionLanguageFactory() {
        return sContext.getBean(IAeExpressionLanguageFactory.class);
    }

    public IAeFunction getFunction(String aFunctionName, String aNamespaceUri) throws AeUnresolvableException
    {
       IAeFunctionContext context = sContext.getBean(AeFunctionContextContainer.class).getFunctionContext(aNamespaceUri);
       if (context == null)
       {
          return null;
       }

       try
       {
          return context.getFunction(aFunctionName);
       }
       catch( AeUnresolvableException ure )
       {
          AeException.logError(ure, ure.getLocalizedMessage());
          throw new AeUnresolvableException(ure.getLocalizedMessage());
       }
    }
    
    public static IAeStorageFactory getStorageFactory() {
        return sContext.getBean(IAeStorageFactory.class);
    }
}
