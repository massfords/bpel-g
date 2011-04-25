// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeProcessFilterAdapter.java,v 1.10 2008/02/14 20:52:34 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.util.AeDate;
import org.activebpel.rt.util.AeUtil;

import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessStateFilterValueType;
import bpelg.services.processes.types.ProcessStateValueType;

/**
 * Wraps the AeProcessFilter to encapsulate selection criteria. Since this
 * version is only used for the AeInMemoryProcessManager, we will get the entire
 * collection of processes in the system.
 */
public class AeProcessFilterAdapter {
	/** Process filter instance. */
	protected ProcessFilterType mFilter;
	/** Current row. */
	protected int mCurrentRow;
	/** Mapping of filter state to process states */
	// FIXME disappointing that these values don't match up
	private static Map<ProcessStateFilterValueType, ProcessStateValueType> sMappings = new HashMap();

	static {
		sMappings.put(ProcessStateFilterValueType.Completed,
				ProcessStateValueType.Complete);
		sMappings.put(ProcessStateFilterValueType.Running,
				ProcessStateValueType.Running);
		sMappings.put(ProcessStateFilterValueType.Faulted,
				ProcessStateValueType.Faulted);
		sMappings.put(ProcessStateFilterValueType.Suspended,
				ProcessStateValueType.Suspended);
		sMappings.put(ProcessStateFilterValueType.SuspendedFaulting,
				ProcessStateValueType.Suspended);
		sMappings.put(ProcessStateFilterValueType.SuspendedProgrammatic,
				ProcessStateValueType.Suspended);
		sMappings.put(ProcessStateFilterValueType.SuspendedManual,
				ProcessStateValueType.Suspended);
		sMappings.put(ProcessStateFilterValueType.SuspendedInvokeRecovery,
				ProcessStateValueType.Suspended);
		sMappings.put(ProcessStateFilterValueType.Compensatable,
				ProcessStateValueType.Compensatable);
	}

	/**
	 * Constructor.
	 * 
	 * @param aFilter
	 *            The process filter instance.
	 */
	public AeProcessFilterAdapter(ProcessFilterType aFilter) {
		mFilter = aFilter;
	}

	/**
	 * Returns true if the process is selectable based on the filter criteria.
	 * 
	 * @param aProcess
	 */
	public boolean accept(IAeBusinessProcess aProcess) {
		boolean match = true;
		if (getFilter() != null) {
			if (isStartRowOrAbove()) {
				match = isMatch(aProcess);
			} else {
				match = false;
			}
			incrementCurrentRow();
		}
		return match;
	}

	/**
	 * Increments the current row count.
	 */
	protected void incrementCurrentRow() {
		mCurrentRow++;
	}

	/**
	 * Returns true if the current row should be examined.
	 */
	protected boolean isStartRowOrAbove() {
		return mCurrentRow >= getFilter().getListStart();
	}

	/**
	 * Returns true if the process meets the filter selection criteria.
	 * 
	 * @param aProcess
	 */
	protected boolean isMatch(IAeBusinessProcess aProcess) {
		return isPIDMatch(aProcess) && isStateMatch(aProcess)
				&& isQNameMatch(aProcess) && isAfterCreationStartDate(aProcess)
				&& isBeforeCreationEndDate(aProcess)
				&& isAfterCompletionStartDate(aProcess)
				&& isBeforeCompletionEndDate(aProcess);
	}

	/**
	 * Returns true if we're filtering on the PID and this process has a PID
	 * within our range.
	 * 
	 * @param aProcess
	 */
	protected boolean isPIDMatch(IAeBusinessProcess aProcess) {
		boolean match = true;
		if (getFilter().getProcessIdMin() != null) {
			match = aProcess.getProcessId() >= getFilter().getProcessIdMin();
		}
		if (match && getFilter().getProcessIdMax() != null) {
			match = aProcess.getProcessId() <= getFilter().getProcessIdMax();
		}
		return match;
	}

	/**
	 * Return the int representing the process state from the process filter
	 * state.
	 * 
	 * @param aFilter
	 */
	protected static ProcessStateValueType getRequestedState(ProcessFilterType aFilter) {
		ProcessStateFilterValueType key = aFilter.getProcessState();
		return sMappings.get(key);
	}

	/**
	 * Compare the process state with the filter state settings.
	 * 
	 * @param aProcess
	 * @return boolean True if the state settings are a match.
	 */
	protected boolean isStateMatch(IAeBusinessProcess aProcess) {
		// issue here is that we are comparing state ints from
		// AeProcessFilter against state ints from IAeBusinessProcess
		// @todo - replace with enum
		if (getFilter().getProcessState() == ProcessStateFilterValueType.Any) {
			return true;
		}

		if (getFilter().getProcessState() == ProcessStateFilterValueType.CompletedOrFaulted) {
			return (aProcess.getProcessState() == ProcessStateValueType.Complete)
					|| (aProcess.getProcessState() == ProcessStateValueType.Faulted);
		}

		return aProcess.getProcessState() == getRequestedState(getFilter());
	}

	/**
	 * Compare the process state QName with the filter settings.
	 * 
	 * @param aProcess
	 * @return boolean True if the QName is a match.
	 */
	protected boolean isQNameMatch(IAeBusinessProcess aProcess) {
		if (getFilter().getProcessName() != null) {
			return isNamespaceMatch(aProcess) && isLocalNameMatch(aProcess);
		} else {
			return true;
		}
	}

	/**
	 * Returns true if filter has no ns criteria otherwise returns true if the
	 * ns strings match.
	 * 
	 * @param aProcess
	 */
	protected boolean isNamespaceMatch(IAeBusinessProcess aProcess) {
		if (!AeUtil.isNullOrEmpty(getFilter().getProcessName()
				.getNamespaceURI())) {
			return AeUtil.compareObjects(getFilter().getProcessName()
					.getNamespaceURI(), aProcess.getName().getNamespaceURI());
		} else {
			return true;
		}
	}

	/**
	 * Returns true if the local part for the filter and the process are a
	 * match.
	 * 
	 * @param aProcess
	 */
	protected boolean isLocalNameMatch(IAeBusinessProcess aProcess) {
		return getFilter().getProcessName().getLocalPart()
				.equals(aProcess.getName().getLocalPart());
	}

	/**
	 * Returns true if the filter has no creation start date criteria,
	 * otherwise, returns true if the process start date is after the filter
	 * creation start date.
	 * 
	 * @param aProcess
	 */
	protected boolean isAfterCreationStartDate(IAeBusinessProcess aProcess) {
		Date processDate = aProcess.getStartDate();
		Date filterDate = toDate(getFilter().getProcessCreateStart());

		return (filterDate == null) || !processDate.before(filterDate);
	}

	Date toDate(XMLGregorianCalendar aCal) {
		return AeDate.toDate(aCal);
	}

	/**
	 * Returns true if the filter has no creation end date criteria, otherwise,
	 * returns true if the process start date is before the filter creation end
	 * date.
	 * 
	 * @param aProcess
	 */
	protected boolean isBeforeCreationEndDate(IAeBusinessProcess aProcess) {
		Date processDate = aProcess.getStartDate();
		Date filterDate = getNextDay(toDate(getFilter().getProcessCreateEnd()));

		return (filterDate == null) || processDate.before(filterDate);
	}

	/**
	 * Returns <code>true</code> if the filter has no completion date start
	 * criterion. If criteria is set it returns <code>false</code> if the
	 * process has no end date. Otherwise, returns <code>true</code> if the
	 * process end date is after the filter completion start date.
	 * 
	 * @param aProcess
	 */
	protected boolean isAfterCompletionStartDate(IAeBusinessProcess aProcess) {
		Date filterDate = toDate(getFilter().getProcessCompleteStart());
		boolean accept = filterDate == null;
		if (!accept) {
			Date processDate = aProcess.getEndDate();
			accept = processDate != null && !processDate.before(filterDate);
		}
		return accept;
	}

	/**
	 * Returns <code>true</code> if the filter has no completion end date
	 * criterion. If criteria is set it returns <code>false</code> if the
	 * process has no end date. Otherwise, returns <code>true</code> if the
	 * process end date is before the filter completion end date.
	 * 
	 * @param aProcess
	 */
	protected boolean isBeforeCompletionEndDate(IAeBusinessProcess aProcess) {
		Date filterDate = getNextDay(toDate( getFilter().getProcessCompleteEnd() ));
		boolean accept = filterDate == null;
		if (!accept) {
			Date processDate = aProcess.getEndDate();
			accept = (processDate != null) && processDate.before(filterDate);
		}
		return accept;
	}

	/**
	 * Getter for the process filter.
	 */
	protected ProcessFilterType getFilter() {
		return mFilter;
	}

	/**
	 * Returns the start of the day following the specified date.
	 */
	protected Date getNextDay(Date aDate) {
		return AeDate.getStartOfNextDay(aDate);
	}

}
