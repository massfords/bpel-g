// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeWorkManager.java,v 1.12 2008/03/28 01:45:58 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import org.activebpel.rt.bpel.AePreferences;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeBlockingQueue;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;

/**
 * Implementation of the WorkManager for Application Servers spec from IBM/BEA.
 * Provides a facility for scheduling asynchronous operations in the form of
 * Work objects.
 */
public class AeWorkManager implements WorkManager, Runnable,
		PreferenceChangeListener, IAeStoppableWorkManager {
	/** work waiting to be executing because of a lack of threads */
	private AeBlockingQueue mQueuedWork = new AeBlockingQueue();
	/** our pool of threads */
	private AeThreadPool mPool = new AeThreadPool();
	/** our dispatch thread */
	private Thread mDispatchThread;
	/** flag to keep the dispatch thread running */
	private boolean mKeepGoing = true;

	/**
	 * Creates the workmanager and starts its dispatch thread.
	 */
	public AeWorkManager() {
		AePreferences.workManager().addPreferenceChangeListener(this);
		mDispatchThread = new Thread(this, "AeWorkManager-DispatchThread"); //$NON-NLS-1$
		mDispatchThread.start();
	}

	/**
	 * Accepts the min and max values to use for the thread pool.
	 * 
	 * @param aMinPoolSize
	 * @param aMaxPoolSize
	 */
	public AeWorkManager(int aMinPoolSize, int aMaxPoolSize) {
		setThreadPoolSize(aMinPoolSize, aMaxPoolSize);
	}

	private void updateConfig() {
		int min = AePreferences.getThreadPoolMin();
		int max = AePreferences.getThreadPoolMax();
		setThreadPoolSize(min, max);
	}

	/**
	 * Sets the size of the thread pool used by the work manager
	 * 
	 * @param aMin
	 * @param aMax
	 */
	public void setThreadPoolSize(int aMin, int aMax) {
		mPool.setPoolSize(aMin, aMax);
	}

	/**
	 * @see org.activebpel.work.IAeStoppableWorkManager#stop()
	 */
	public void stop() {
		mKeepGoing = false;
		if (mDispatchThread.isAlive()) {
			mDispatchThread.interrupt();
		}
		mPool.killAllThreads();
	}

	/**
	 * Callback method from the worker thread for when it's done executing its
	 * work. Each time a thread completes we walk the collection of waiting
	 * objects to see if any of them are able to continue.
	 * 
	 * @param aThread
	 */
	public void done(AeWorkerThread aThread) {
		AeWorkItem item = aThread.getWorkItem();

		aThread.clear();
		item.notifyListeners();
		mPool.returnThread(aThread);
	}

	/**
	 * The runnable target for the dispatch thread. The run method will execute
	 * until the workmanager is shutdown.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (keepGoing()) {
			// wait for some work to get added to the queue
			mQueuedWork.waitForObject();
			// if we are not shutting down
			if (keepGoing()) {
				// wait for a thread to become available from our pool
				AeWorkerThread thread = mPool.waitForThread(this);
				// thread can be null if thread was interrupted while waiting
				// for new thread
				if (thread != null) {
					AeQueuedWork queuedWork = (AeQueuedWork) mQueuedWork
							.getNextObjectOrWait();
					// queued work can be null if thread was interrupted while
					// waiting for next object
					if (queuedWork != null) {
						thread.schedule(queuedWork.getWork(),
								queuedWork.getItem(), queuedWork.getListener());
					}
				}
			}
		}
	}

	/**
	 * Returns false if our dispatch thread should stop execution.
	 */
	private boolean keepGoing() {
		return mKeepGoing;
	}

	/**
	 * @see commonj.work.WorkManager#schedule(commonj.work.Work)
	 */
	public WorkItem schedule(Work aWork) throws IllegalArgumentException {
		return schedule(aWork, null);
	}

	/**
	 * Schedules the work and returns the work item immediately. The dispatch
	 * thread for the work manager will assign a thread to the newly queued work
	 * when one becomes available.
	 * 
	 * @see commonj.work.WorkManager#schedule(commonj.work.Work,
	 *      commonj.work.WorkListener)
	 */
	public WorkItem schedule(Work aWork, WorkListener aListener)
			throws IllegalArgumentException {
		if (isStopped())
			throw new IllegalArgumentException(
					AeMessages.getString("AeWorkManager.WorkManagerStopped")); //$NON-NLS-1$
		AeWorkItem workItem = createWorkItem(aWork);

		fireAccepted(workItem, aListener);

		addWorkToQueue(new AeQueuedWork(aWork, workItem, aListener));

		return workItem;
	}

	/**
	 * Returns true if the work manager has been stopped.
	 */
	protected boolean isStopped() {
		return !mKeepGoing;
	}

	/**
	 * Adds work to the queue.
	 * 
	 * @param aQueuedWork
	 */
	private void addWorkToQueue(AeQueuedWork aQueuedWork) {
		synchronized (mQueuedWork) {
			mQueuedWork.add(aQueuedWork);
		}
	}

	/**
	 * Updates the work item's status and notifies listener that the work has
	 * been accepted.
	 * 
	 * @param aWorkItem
	 * @param aWorkListener
	 */
	private void fireAccepted(AeWorkItem aWorkItem, WorkListener aWorkListener) {
		aWorkItem.setStatus(WorkEvent.WORK_ACCEPTED);
		if (aWorkListener != null) {
			aWorkListener.workAccepted(new AeWorkEvent(aWorkItem,
					WorkEvent.WORK_ACCEPTED, null));
		}
	}

	/**
	 * Creates a work item based on the type of work being done. If it's a
	 * serializable work instance then we'll create a remote work item,
	 * otherwise it's a regular work item.
	 * 
	 * @param aWork
	 */
	private AeWorkItem createWorkItem(Work aWork) {
		if (aWork instanceof Serializable) {
			return new AeRemoteWorkItem(this, aWork);
		}
		return new AeWorkItem(aWork);
	}

	/**
	 * @see commonj.work.WorkManager#waitForAll(java.util.Collection, long)
	 */
	public boolean waitForAll(Collection aWorkItems, long aTimeout) {
		AeWaitForAll waiter = new AeWaitForAll(aWorkItems);
		return wait(waiter, aTimeout);
	}

	/**
	 * @see commonj.work.WorkManager#waitForAny(java.util.Collection, long)
	 */
	public Collection waitForAny(Collection aWorkItems, long aTimeout) {
		AeWaitForAny waiter = new AeWaitForAny(aWorkItems);
		wait(waiter, aTimeout);
		return waiter.getCompletedItems();
	}

	/**
	 * Waits for the waiter to be done.
	 * 
	 * @param aWaiter
	 * @param aTimeout
	 */
	private boolean wait(AeWaitForAll aWaiter, long aTimeout) {
		boolean done = aWaiter.isDone();

		if (aTimeout == WorkManager.IMMEDIATE || done) {
			return done;
		}

		// now sync on the waiter so we can put it to sleep
		aWaiter.doWait(aTimeout);

		return aWaiter.isDone();
	}

	/**
	 * Waits for one or all of the work items within its collection to finish
	 * before calling its notify() method which will notify the calling thread
	 * from either waitForAll or waitForAny.
	 */
	protected class AeWaitForAll implements IAeWorkDoneListener {
		/** set of work items we're waiting to complete */
		protected Collection mColl;

		/**
		 * Creates a waiter that will notify its waiting objects when the
		 * WorkItems in its set have completed.
		 * 
		 * @param aColl
		 */
		public AeWaitForAll(Collection aColl) {
			mColl = copyWorkItems(aColl);

			for (Iterator iter = mColl.iterator(); iter.hasNext();) {
				AeWorkItem item = (AeWorkItem) iter.next();
				item.addWorkDoneListener(this);
			}
		}

		/**
		 * We want to work off of a copy of the work items collections which
		 * would normally be a single call to clone(), but the collection passed
		 * in may contain objects other than WorkItems and these need to be
		 * skipped.
		 * 
		 * @param aWorkItems
		 */
		private Collection copyWorkItems(Collection aWorkItems) {
			Collection coll = new LinkedList();
			for (Iterator iter = aWorkItems.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof WorkItem) {
					coll.add(o);
				}
			}
			return coll;
		}

		/**
		 * If the waiting is not done then we'll go into a wait
		 * 
		 * @param aTimeout
		 */
		public synchronized void doWait(long aTimeout) {
			if (!isDone()) {
				try {
					wait(aTimeout);
				} catch (InterruptedException e) {
				}
			}
		}

		/**
		 * Notifies the waiting objects if our notification criteria is met.
		 * We're either waiting for all objects to complete or waiting for any
		 * in the collection to complete.
		 */
		public synchronized void checkIfDone() {
			if (isDone()) {
				notifyAll();
			}
		}

		/**
		 * returns true if the criteria for waiting has been met and we no
		 * longer need to wait.
		 */
		protected boolean isDone() {
			int completeCount = getCompleteCount();
			return completeCount == mColl.size();
		}

		/**
		 * Returns the number of completed items within the collection
		 */
		protected int getCompleteCount() {
			int i = 0;
			for (Iterator iter = mColl.iterator(); iter.hasNext();) {
				WorkItem workItem = (WorkItem) iter.next();
				if (workItem.getStatus() == WorkEvent.WORK_COMPLETED) {
					i++;
				}
			}
			return i;

		}

		/**
		 * @see org.activebpel.work.IAeWorkDoneListener#workDone()
		 */
		public void workDone() {
			checkIfDone();
		}
	}

	/**
	 * Extends the wait for all routine to notify when any of the objects within
	 * the coll have completed.
	 */
	protected class AeWaitForAny extends AeWaitForAll {
		/**
		 * Creates a waiter that will notify its waiting objects when any one of
		 * the WorkItems in its set have completed.
		 * 
		 * @param aColl
		 */
		public AeWaitForAny(Collection aColl) {
			super(aColl);
		}

		/**
		 * @see org.activebpel.work.AeWorkManager.AeWaitForAll#isDone()
		 */
		protected boolean isDone() {
			return getCompleteCount() > 0;
		}

		/**
		 * Gets all of the completed items or null if there are none.
		 */
		protected Collection getCompletedItems() {
			List list = null;
			for (Iterator iter = mColl.iterator(); iter.hasNext();) {
				WorkItem item = (WorkItem) iter.next();
				if (item.getStatus() == WorkEvent.WORK_COMPLETED) {
					list = addToList(item, list);
				}
			}

			// 1.1 version of spec requires empty list instead of null
			if (list == null)
				list = Collections.EMPTY_LIST;
			return list;
		}

		/**
		 * Adds the object to the list, creating the list if the one passed in
		 * was null.
		 * 
		 * @param aItem
		 *            Object to add to list
		 * @param aList
		 *            List or null to return a new one.
		 */
		private List addToList(WorkItem aItem, List aList) {
			List list = aList == null ? new ArrayList() : aList;
			list.add(aItem);
			return list;
		}
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent aEvt) {
		updateConfig();
	}
}