// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeInMemoryProcessManager.java,v 1.41 2008/03/28 01:41:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessInstanceDetail;
import bpelg.services.processes.types.ProcessList;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAePlanManager;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.def.visitors.AeDefToImplVisitor;
import org.activebpel.rt.bpel.impl.list.AeProcessFilterAdapter;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeDate;

import javax.inject.Singleton;
import javax.xml.namespace.QName;
import java.util.*;

/**
 * Implements a simple in-memory process manager.
 */
@Singleton
public class AeInMemoryProcessManager extends AeAbstractProcessManager
		implements IAeProcessManager {
	public static final String CONFIG_COMPLETED_PROCESS_COUNT = "CompletedProcessCount"; //$NON-NLS-1$

	/** Default number of completed processes to leave in {@link #mProcesses}. */
	private static final int DEFAULT_COMPLETED_PROCESS_COUNT = 25;

	/** Next available process id */
	private long mNextProcessId = 1;

	/** Maps process ids to processes */
	private final Hashtable<Long,IAeBusinessProcess> mProcesses = new Hashtable<>();

	/**
	 * The number of completed process to leave temporarily in
	 * {@link #mProcesses}.
	 */
	private int mCompletedProcessCount = DEFAULT_COMPLETED_PROCESS_COUNT;

	/**
	 * Process ids for completed processes temporarily left in
	 * {@link #mProcesses}.
	 */
	private final List<Long> mCompletedProcessIds = new LinkedList<>();

	/** The next journal ID to use for journaling methods. */
	private long mNextJournalId = 1;

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getNextJournalId()
	 */
	public synchronized long getNextJournalId() {
		return mNextJournalId++;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#createBusinessProcess(org.activebpel.rt.bpel.impl.IAeProcessPlan)
	 */
	public IAeBusinessProcess createBusinessProcess(IAeProcessPlan aPlan)
			throws AeBusinessProcessException {
		long pid = getNextProcessId();
		IAeBusinessProcess process = AeDefToImplVisitor.createProcess(pid,
				getEngine(), aPlan);
		putProcess(pid, process);
		return process;
	}

	/**
	 * Returns process id from a key generated by {@link #getKey(long)}.
	 */
	protected long fromKey(Object aKey) {
		return ((Number) aKey).longValue();
	}

	/**
	 * Returns a key compatible with a Java <code>Map</code> for the specified
	 * process id.
	 */
	protected Long getKey(long aProcessId) {
		return aProcessId;
	}

	/**
	 * Returns next available process id.
	 */
	protected long getNextProcessId() {
		synchronized (mProcesses) {
			return mNextProcessId++;
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcess(long)
	 */
	public IAeBusinessProcess getProcess(long aProcessId) {
		return getProcessMap().get(getKey(aProcessId));
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessNoUpdate(long)
	 */
	public IAeBusinessProcess getProcessNoUpdate(long aProcessId) {
		return getProcess(aProcessId);
	}

	/**
	 * Returns process map.
	 */
	protected Map<Long,IAeBusinessProcess> getProcessMap() {
		return mProcesses;
	}

	/**
	 * Creates a process instance detail for the given process
	 * 
	 * @param aProcess
	 */
	private ProcessInstanceDetail createProcessInstanceDetail(
			IAeBusinessProcess aProcess) {
		ProcessInstanceDetail detail = new ProcessInstanceDetail()
			.withName(aProcess.getName())
			.withProcessId(aProcess.getProcessId())
			.withState(aProcess.getProcessState())
			.withStateReason(aProcess.getProcessStateReason())
			.withStarted(AeDate.toCal(aProcess.getStartDate()))
			.withEnded(AeDate.toCal(aProcess.getEndDate()))
			;
		return detail;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessInstanceDetails(long)
	 */
	public ProcessInstanceDetail getProcessInstanceDetails(long aProcessId) {
		IAeBusinessProcess process = getProcess(aProcessId);
		return (process == null) ? null : createProcessInstanceDetail(process);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.activebpel.rt.bpel.impl.IAeProcessManager#getProcesses(bpelg.services
	 * .processes.types.ProcessFilterType)
	 */
	public ProcessList getProcesses(ProcessFilterType aFilter) {
		ArrayList<ProcessInstanceDetail> results = new ArrayList<>();
		int totalCount = aFilter == null ? 0 : aFilter.getListStart();
		int matches = 0;

		synchronized (getProcessMap()) {
			AeProcessFilterAdapter filter = new AeProcessFilterAdapter(aFilter);

			SortedMap<Long,IAeBusinessProcess> map = new TreeMap<>(new AeReverseComparator());
			map.putAll(getProcessMap());

            for (Long l : map.keySet()) {
                IAeBusinessProcess process = map.get(l);
                if (filter.accept(process)) {
                    totalCount++;
                    if (isWithinRange(aFilter, totalCount)) {
                        results.add(createProcessInstanceDetail(process));
                    }
                    matches++;
                }
            }
		}

		return new ProcessList().withTotalRowCount(totalCount).withProcessInstanceDetail(results)
			.withComplete(true);
	}

	/* (non-Javadoc)
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessCount(bpelg.services.processes.types.ProcessFilterType)
	 */
	public int getProcessCount(ProcessFilterType aFilter)
			throws AeBusinessProcessException {
		return getProcesses(aFilter).getTotalRowCount();
	}

	/* (non-Javadoc)
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessIds(bpelg.services.processes.types.ProcessFilterType)
	 */
	public long[] getProcessIds(ProcessFilterType aFilter)
			throws AeBusinessProcessException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Puts process into process map.
	 * 
	 * @param aProcessId
	 * @param aProcess
	 */
	protected void putProcess(long aProcessId, IAeBusinessProcess aProcess) {
		getProcessMap().put(getKey(aProcessId), aProcess);
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#processEnded(long)
	 */
	public void processEnded(long aProcessId) {
		removeProcess(aProcessId);
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#releaseProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
	 */
	public void releaseProcess(IAeBusinessProcess aProcess) {
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#removeProcess(long)
	 */
	public void removeProcess(long aProcessId) {
		List<Long> purgedProcessIds = new ArrayList<>();

		if (getCompletedProcessCount() <= 0) {
			// If we're not caching completed processes, then just remove the
			// process from the process map.
			Long key = getKey(aProcessId);
			getProcessMap().remove(key);

			purgedProcessIds.add(key);
		} else {
			synchronized (mCompletedProcessIds) {
				// Otherwise, pare the list of cached processes to the cache
				// size.
				while (mCompletedProcessIds.size() >= getCompletedProcessCount()) {
					Long key = mCompletedProcessIds.remove(0);
					getProcessMap().remove(key);

					purgedProcessIds.add(key);
				}

				// And then add the specified process to the cache.
				mCompletedProcessIds.add(getKey(aProcessId));
			}
		}

		// Notify listeners of the purged processes.
        for (Long purgedProcessId : purgedProcessIds) {
            long processId = fromKey(purgedProcessId);
            fireProcessPurged(processId);
        }
	}

	/* (non-Javadoc)
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#removeProcesses(bpelg.services.processes.types.ProcessFilterType)
	 */
	public int removeProcesses(ProcessFilterType aFilter) {
		synchronized (getProcessMap()) {
			List<IAeBusinessProcess> processes = new ArrayList<>(getProcessMap().values());
			AeProcessFilterAdapter filter = new AeProcessFilterAdapter(aFilter);
			int removed = 0;

            for (IAeBusinessProcess process : processes) {
                if (filter.accept(process)) {
                    removeProcess(process.getProcessId());
                    ++removed;
                }
            }

			return removed;
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#setPlanManager(org.activebpel.rt.bpel.IAePlanManager)
	 */
	public void setPlanManager(IAePlanManager aPlanManager) {
		// Don't need plan manager for in-memory process manager.
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessQName(long)
	 */
	public QName getProcessQName(long aProcessId) {
		QName processName = null;
		Object key = getKey(aProcessId);
		IAeBusinessProcess process = getProcessMap().get(
				key);
		if (process != null) {
			processName = process.getName();
		}
		return processName;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalEntryDone(long,
	 *      long)
	 */
	public void journalEntryDone(long aProcessId, long aJournalId) {
		// Nothing to do for in-memory process manager.
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokeData(long,
	 *      int, long, org.activebpel.rt.message.IAeMessageData, java.util.Map)
	 */
	public long journalInvokeData(long aProcessId, int aLocationId,
			long aTransmissionId, IAeMessageData aMessageData,
			Map aProcessProperties) {
		// Don't need to save received invoke data for in-memory process
		// manager.
		return getNextJournalId();
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokeFault(long,
	 *      int, long, org.activebpel.rt.bpel.IAeFault, java.util.Map)
	 */
	public long journalInvokeFault(long aProcessId, int aLocationId,
			long aTransmissionId, IAeFault aFault, Map aProcessProperties) {
		// Don't need to save received invoke fault for in-memory process
		// manager.
		return getNextJournalId();
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInboundReceive(long,
	 *      int, org.activebpel.rt.bpel.impl.queue.AeInboundReceive)
	 */
	public long journalInboundReceive(long aProcessId, int aLocationId,
			AeInboundReceive aInboundReceive) {
		// Don't need to save received message for in-memory process manager.
		return getNextJournalId();
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalSentReply(long,
	 *      org.activebpel.rt.bpel.impl.queue.AeReply, java.util.Map)
	 */
	public void journalSentReply(long aProcessId, AeReply aSentReply,
			Map aProcessProperties) {
		// Don't need to save sent reply for in-memory process manager.
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokeTransmitted(long,
	 *      int, long)
	 */
	public void journalInvokeTransmitted(long aProcessId, int aLocationId,
			long aTransmissionId) throws AeBusinessProcessException {
		// Don't need to save invoke's transmission id for in-memory process
		// manager.
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCompensateSubprocess(long,
	 *      java.lang.String)
	 */
	public long journalCompensateSubprocess(long aProcessId,
			String aCoordinationId) {
		// In memory proc. manager does not save journal data.
		return getNextJournalId();
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokePending(long,
	 *      int)
	 */
	public long journalInvokePending(long aProcessId, int aLocationId) {
		// Don't need to save pending invoke data for in-memory process manager.
		return getNextJournalId();
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#transmissionIdDone(long,
	 *      long)
	 */
	public void transmissionIdDone(long aProcessId, long aTransmissionId) {
		// For in-memory impl., remove from tx tracker.
		try {
			getEngine().getTransmissionTracker().remove(aTransmissionId);
		} catch (Exception e) {
			AeException.logError(e);
		}
	}

	/**
	 * Returns the number of completed process to leave temporarily in the
	 * process map.
	 */
	protected int getCompletedProcessCount() {
		return mCompletedProcessCount;
	}

	/**
	 * Sets the number of completed process to leave temporarily in the process
	 * map.
	 */
	protected void setCompletedProcessCount(int aCompletedProcessCount) {
		mCompletedProcessCount = aCompletedProcessCount;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalEntryForRestart(long,
	 *      long)
	 */
	public void journalEntryForRestart(long aProcessId, long aJournalId) {
		// Nothing to do for in-memory process manager.
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#recreateBusinessProcess(long,
	 *      org.activebpel.rt.bpel.impl.IAeProcessPlan)
	 */
	public IAeBusinessProcess recreateBusinessProcess(long aProcessId,
			IAeProcessPlan aPlan) throws AeBusinessProcessException {
		// This is implemented even though the current implementation of the
		// restart process capability only works with the persistent process
		// manager.
		IAeBusinessProcess process = AeDefToImplVisitor.createProcess(
				aProcessId, getEngine(), aPlan);
		putProcess(aProcessId, process);
		return process;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCoordinationQueueMessageReceived(long,
	 *      org.activebpel.rt.bpel.coord.IAeProtocolMessage)
	 */
	public long journalCoordinationQueueMessageReceived(long aProcessId,
			IAeProtocolMessage aMessage) {
		return IAeProcessManager.NULL_JOURNAL_ID;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCancelProcess(long)
	 */
	public long journalCancelProcess(long aProcessId) {
		return IAeProcessManager.NULL_JOURNAL_ID;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCancelSubprocessCompensation(long)
	 */
	public long journalCancelSubprocessCompensation(long aProcessId) {
		return IAeProcessManager.NULL_JOURNAL_ID;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalReleaseCompensationResources(long)
	 */
	public long journalReleaseCompensationResources(long aProcessId) {
		return IAeProcessManager.NULL_JOURNAL_ID;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalNotifyCoordinatorsParticipantClosed(long)
	 */
	public long journalNotifyCoordinatorsParticipantClosed(long aProcessId) {
		return IAeProcessManager.NULL_JOURNAL_ID;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCompensateCallback(long,
	 *      java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault)
	 */
	public long journalCompensateCallback(long aProcessId,
			String aLocationPath, String aCoordinationId, IAeFault aFault) {
		return IAeProcessManager.NULL_JOURNAL_ID;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCoordinatedActivityCompleted(long,
	 *      java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault)
	 */
	public long journalCoordinatedActivityCompleted(long aProcessId,
			String aLocationPath, String aCoordinationId, IAeFault aFault) {
		return IAeProcessManager.NULL_JOURNAL_ID;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalDeregisterCoordination(long,
	 *      java.lang.String, java.lang.String)
	 */
	public long journalDeregisterCoordination(long aProcessId,
			String aLocationPath, String aCoordinationId) {
		return IAeProcessManager.NULL_JOURNAL_ID;
	}

	/**
	 * Implements a <code>Comparator</code> that reverses the natural order of
	 * <code>Comparable</code> objects.
	 */
	protected static class AeReverseComparator implements Comparator<Comparable> {
	    
        @SuppressWarnings("unchecked")
        @Override
        public int compare(Comparable o1, Comparable o2) {
            return -(o1.compareTo(o2));
        }
	}

	/**
	 * Convenience method that returns true if the value passed in is within the
	 * range of rows that we're looking.
	 * 
	 * @param aRowCount
	 */
	private boolean isWithinRange(ProcessFilterType aFilter, int aRowCount) {
		if (aRowCount > aFilter.getListStart()) {
			// figure out how many rows have actually been returned
			// at this point
			int actualNumberReturned = aRowCount - aFilter.getListStart();

			// we've skipped passed the number of rows we're supposed to
			// we'll accept this row if it's less than our max or if our
			// max is 0
			return aFilter.getMaxReturn() == 0
					|| actualNumberReturned <= aFilter.getMaxReturn();
		}
		return false;
	}
}
