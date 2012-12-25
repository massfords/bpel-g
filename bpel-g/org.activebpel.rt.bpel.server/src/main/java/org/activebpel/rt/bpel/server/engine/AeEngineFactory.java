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

import commonj.timers.TimerManager;
import commonj.work.Work;
import commonj.work.WorkManager;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.timer.IAeStoppableTimerManager;
import org.activebpel.work.IAeProcessWorkManager;
import org.activebpel.work.IAeStoppableWorkManager;
import org.activebpel.work.factory.IAeWorkManagerFactory;
import org.activebpel.work.input.IAeInputMessageWork;
import org.activebpel.work.input.IAeInputMessageWorkManager;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Maintains a singleton instance of the engine.
 */
public class AeEngineFactory {
	private static ApplicationContext sContext;

	/**
	 * Start the BPEL engine. Should not be called until all of the expected
	 * deployments have been completed (as previously persisted processes will
	 * assume their resources are available as soon as they start up again).
	 * 
	 * @throws AeException
	 */
	public static void start() throws AeException {
		getEngine().start();
	}

	public static <T> T getBean(Class<T> aClass) {
		if (sContext == null)
			return null;
		return sContext.getBean(aClass);
	}

	/**
	 * Gets a ref to the administration API
	 */
	public static IAeEngineAdministration getEngineAdministration() {
		return getBean(IAeEngineAdministration.class);
	}

	/**
	 * Getter for the engine.
	 */
	public static IAeBusinessProcessEngineInternal getEngine() {
		return getBean(IAeBusinessProcessEngineInternal.class);
	}

	/**
	 * Convenience method that schedules work to be done and translates any work
	 * exceptions into our standard business process exception.
	 * 
	 * @param aWork
	 */
	public static void schedule(Work aWork) {
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
		if (getBean(TimerManager.class) instanceof IAeStoppableTimerManager)
			((IAeStoppableTimerManager) getBean(TimerManager.class)).stop();
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
					"Alarm",
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
		return (WorkManager) sContext.getBean("childWorkManagers", Map.class)
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
	
	public static void setApplicationContext(ApplicationContext aContext) {
		sContext = aContext;
	}
}
