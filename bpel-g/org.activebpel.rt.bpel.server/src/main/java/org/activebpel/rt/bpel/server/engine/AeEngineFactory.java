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

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Handler;

import javax.naming.InitialContext;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.registry.AeEngineConfigExtensionRegistry;
import org.activebpel.rt.bpel.def.visitors.AeDefVisitorFactory;
import org.activebpel.rt.bpel.function.AeFunctionContextContainer;
import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionContext;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory;
import org.activebpel.rt.bpel.server.engine.storage.IAeStorageProviderFactory;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.timer.AeTimerManager;
import org.activebpel.timer.IAeStoppableTimerManager;
import org.activebpel.work.IAeProcessWorkManager;
import org.activebpel.work.IAeStoppableWorkManager;
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
public class AeEngineFactory {
	/** The singleton engine instance */
	private static AeBpelEngine sEngine;

	/** Timer Manager impl for scheduling alarms */
	private static TimerManager sTimerManager;

	/** The current configuration settings */
	private static IAeEngineConfiguration sConfig;

	/** Flag indicating the persistent store is available in the configuration. */
	private static boolean sPersistentStoreConfiguration;

	/**
	 * String indicating the error message from the persistent store if it is
	 * not ready for use.
	 */
	private static String sPersistentStoreError;

	private static ApplicationContext sContext;

	/**
	 * Pre-initialize the engine to set up storage work, policy mappers and
	 * timer managers.
	 * 
	 * @param aConfig
	 * @throws AeException
	 */
	public static void preInit(IAeEngineConfiguration aConfig)
			throws AeException {
		// Load the logging handler if one was specified
		Map logMap = aConfig
				.getMapEntry(IAeEngineConfiguration.LOG_HANDLER_ENTRY);
		if (logMap != null) {
			for (Iterator iter = logMap.keySet().iterator(); iter.hasNext();) {
				String handlerClass = (String) logMap.get(iter.next());
				try {
					Class handlerClazz = Class.forName(handlerClass);
					Handler logHandler = (Handler) handlerClazz.newInstance();
					AeException.getLogger().addHandler(logHandler);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}

		setEngineConfig(aConfig);

		sContext = new ClassPathXmlApplicationContext(
				"bpelg-applicationContext.xml");

		// Initialize the timer manager
		initializeTimerManager();
	}

	/**
	 * Initialize the BPEL engine.
	 * 
	 * @throws AeException
	 */
	public static void init() throws AeException {
		// Use the managers to create the bpel engine.
		// The class name for the bpel engine can be supplied dynamically, but
		// this
		// factory assumes that it's derived from AeBpelEngine
		sEngine = sContext.getBean(AeBpelEngine.class);

		// create engine and init its managers.
		sEngine.create();

		// install extension registry
		try {
			AeEngineConfigExtensionRegistry registry = new AeEngineConfigExtensionRegistry(
					(AeDefaultEngineConfiguration) AeEngineFactory
							.getEngineConfig());
			AeDefVisitorFactory.setExtensionRegistry(
					IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, registry);
		} catch (ClassNotFoundException ex) {
			// DO Nothing if can not find or install an extension registry
		}
	}

	/**
	 * Start the BPEL engine. Should not be called until all of the expected
	 * deployments have been completed (as previously persisted processes will
	 * assume their resources are available as soon as they start up again).
	 * 
	 * @throws AeException
	 */
	public static void start() throws AeException {
		if (isEngineStorageReadyRetest()) {
			// Start only this engine - startAll() is not necessary on startup -
			// each node will
			// call their own start method on startup.
			sEngine.start();
		} else {
			throw new AeBusinessProcessException(
					AeMessages.getString("AeEngineFactory.ERROR_1")); //$NON-NLS-1$
		}
	}

	/**
	 * This method initializes the timer manager used by the engine. We will
	 * first attempt to lookup the timer manager from the JNDI location
	 * specified in the engine config file. If not specified or unable to load,
	 * then we will use default timer manager.
	 */
	protected static void initializeTimerManager() {
		// Make sure we initialize to null, or may not behave properly during
		// servlet hot deploy
		sTimerManager = null;

		Map timerMgrConfigMap = getEngineConfig().getMapEntry(
				IAeEngineConfiguration.TIMER_MANAGER_ENTRY);
		if (!AeUtil.isNullOrEmpty(timerMgrConfigMap)) {
			String timerMgrLocation = (String) timerMgrConfigMap
					.get(IAeEngineConfiguration.TM_JNDI_NAME_ENTRY);
			if (!AeUtil.isNullOrEmpty(timerMgrLocation)) {
				try {
					// Lookup the timer manager from the JNDI location specified
					// in engine config.
					InitialContext ic = new InitialContext();
					sTimerManager = (TimerManager) ic.lookup(timerMgrLocation);
					AeException
							.info(AeMessages
									.getString("AeEngineFactory.ERROR_16") + timerMgrLocation); //$NON-NLS-1$
				} catch (Exception e) {
					AeException
							.info(AeMessages
									.getString("AeEngineFactory.ERROR_17") + timerMgrLocation); //$NON-NLS-1$
				}
			}
		}

		// The JNDI location was missing or invalid, so try to construct a timer
		// manager from an explicitly specified class name.
		if ((sTimerManager == null) && !AeUtil.isNullOrEmpty(timerMgrConfigMap)) {
			String className = (String) timerMgrConfigMap
					.get(IAeEngineConfiguration.CLASS_ENTRY);
			if (!AeUtil.isNullOrEmpty(className)) {
				try {
					Class clazz = Class.forName(className);
					sTimerManager = (TimerManager) clazz.newInstance();
				} catch (Exception e) {
					AeException.logError(e, AeMessages.format(
							"AeEngineFactory.ERROR_Instantiation", className)); //$NON-NLS-1$
				}
			}
		}

		// Timer manager not specified or invalid JNDI location given, use
		// default
		if (sTimerManager == null) {
			AeException.info(AeMessages.getString("AeEngineFactory.18")); //$NON-NLS-1$
			sTimerManager = new AeTimerManager();
		}
	}

	/**
	 * This method takes a configuration map for a manager and instantiates that
	 * manager. This involves some simple java reflection to find the proper
	 * constructor and then calling that constructor.
	 * 
	 * @param aConfig
	 *            The engine configuration map for the manager.
	 * @return An engine manager (alert, queue, etc...).
	 */
	public static Object createConfigSpecificClass(Map aConfig)
			throws AeException {
		if (AeUtil.isNullOrEmpty(aConfig)) {
			throw new AeException(
					AeMessages.getString("AeEngineFactory.ERROR_10")); //$NON-NLS-1$
		}
		return AeConfigurationUtil.createConfigSpecificClass(aConfig);
	}

	public static <T> T getBean(Class<T> aClass) {
		return sContext.getBean(aClass);
	}

	/**
	 * Gets a ref to the administration API
	 */
	public static IAeEngineAdministration getEngineAdministration() {
		if (sContext == null)
			return null;
		return sContext.getBean(IAeEngineAdministration.class);
	}

	/**
	 * Getter for the engine.
	 */
	public static IAeBusinessProcessEngineInternal getEngine() {
		return sEngine;
	}

	/**
	 * Gets the installed timer manager.
	 */
	public static TimerManager getTimerManager() {
		return sTimerManager;
	}

	/**
	 * Convenience method that schedules work to be done and translates any work
	 * exceptions into our standard business process exception.
	 * 
	 * @param aWork
	 */
	public static void schedule(Work aWork) throws AeBusinessProcessException {
		getBean(WorkManager.class).schedule(aWork);
	}

	/**
	 * Convenience method for stopping the work manager.
	 */
	public static void shutDownWorkManager() {
		// Stop is only available in our default implementation of the work
		// manager
		if (getBean(WorkManager.class) instanceof IAeStoppableWorkManager)
			((IAeStoppableWorkManager) getBean(WorkManager.class)).stop();

		// Notify the input message work manager that we're shutting down.
		getInputMessageWorkManager().stop();
	}

	/**
	 * Convenience method for stopping the timer manager.
	 */
	public static void shutDownTimerManager() {
		// Stop is only available in our default implementation of the timer
		// manager
		if (getTimerManager() instanceof IAeStoppableTimerManager)
			((IAeStoppableTimerManager) getTimerManager()).stop();
	}

	/**
	 * Set the engine configuration settings.
	 */
	protected static void setEngineConfig(IAeEngineConfiguration aConfig) {
		sConfig = aConfig;
	}

	/**
	 * Accessor for engine configuration settings.
	 */
	public static IAeEngineConfiguration getEngineConfig() {
		return sConfig;
	}

	/**
	 * Returns true if the current configuration contains a persistent store.
	 */
	public static boolean isPersistentStoreConfiguration() {
		return sPersistentStoreConfiguration;
	}

	/**
	 * Returns true if the persistent storage is ready for use.
	 */
	public static boolean isPersistentStoreReadyForUse() {
		return sContext.getBean(IAeStorageFactory.class).isReady();
	}

	/**
	 * If engine storage is not already in ready state then we will check it
	 * again before returning status.
	 */
	public static boolean isEngineStorageReadyRetest() throws AeException {
		try {
			if (!isEngineStorageReady()) {
				sContext.getBean(IAeStorageProviderFactory.class).init();
			}
			return isEngineStorageReady();
		} catch (AeStorageException ex) {
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
	public static boolean isEngineStorageReady() {
		if (!isPersistentStoreConfiguration())
			return true;
		return isPersistentStoreReadyForUse();
	}

	/**
	 * Returns the error message if the persistent store is not ready for use.
	 */
	public static String getPersistentStoreError() {
		return sPersistentStoreError;
	}

	/**
	 * Sets the error message if the persistent store is not ready for use.
	 */
	public static void setPersistentStoreError(String aString) {
		sPersistentStoreError = aString;
	}

	/**
	 * Schedules per-process work for the given process.
	 * 
	 * @param aProcessId
	 * @param aWork
	 */
	public static void schedule(long aProcessId, Work aWork)
			throws AeBusinessProcessException {
		getBean(IAeProcessWorkManager.class).schedule(aProcessId, aWork);
	}

	/**
	 * Returns True if using internal WorkManager implementation or False if
	 * using server version.
	 */
	public static boolean isInternalWorkManager() {
		return sContext.getBean(IAeWorkManagerFactory.class)
				.isInternalWorkManager();
	}

	/**
	 * Schedules work on the alarm child work manager.
	 * 
	 * @param aWork
	 */
	public static void scheduleAlarmWork(Work aWork)
			throws AeBusinessProcessException {
		try {
			scheduleChildWork(
					IAeEngineConfiguration.ALARM_CHILD_WORK_MANAGER_ENTRY,
					aWork);
		} catch (AeBusinessProcessException e) {
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
	public static void scheduleChildWork(String aName, Work aWork)
			throws AeBusinessProcessException {
		WorkManager childWorkManager = getChildWorkManager(aName);
		if (childWorkManager == null) {
			throw new AeBusinessProcessException(AeMessages.format(
					"AeEngineFactory.ERROR_NoChildWorkManager", aName)); //$NON-NLS-1$
		}

		try {
			childWorkManager.schedule(aWork);
		} catch (Exception e) {
			throw new AeBusinessProcessException(
					AeMessages.getString("AeEngineFactory.ERROR_15"), e); //$NON-NLS-1$
		}
	}

	/**
	 * @return the child work manager with the given name
	 */
	public static WorkManager getChildWorkManager(String aName) {
		return (WorkManager) sContext.getBean("ChildWorkManagers", Map.class)
				.get(aName);
	}

	/**
	 * Schedules input message work on the configured input message work
	 * manager.
	 * 
	 * @param aProcessId
	 * @param aInputMessageWork
	 */
	public static void scheduleInputMessageWork(long aProcessId,
			IAeInputMessageWork aInputMessageWork)
			throws AeBusinessProcessException {
		getInputMessageWorkManager().schedule(aProcessId, aInputMessageWork);
	}

	/**
	 * @return the input message work manager configured for this engine
	 */
	public static IAeInputMessageWorkManager getInputMessageWorkManager() {
		return sContext.getBean(IAeWorkManagerFactory.class)
				.getInputMessageWorkManager();
	}
}
