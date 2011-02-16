package org.activebpel.rt.bpel.server.logging;

import java.util.Collections;
import java.util.Set;

import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;

public class AeProcessLoggingFilter implements IAeLoggingFilter {

	private Set<Integer> mProcessEventIds = Collections.EMPTY_SET;
	
	@Override
	public boolean accept(IAeProcessEvent aEvent) {
		return mProcessEventIds.contains(aEvent.getEventID());
	}

	@Override
	public boolean accept(IAeProcessInfoEvent aInfoEvent) {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return !mProcessEventIds.isEmpty();
	}

	public Set getProcessEventIds() {
		return mProcessEventIds;
	}

	public void setProcessEventIds(Set aProcessEventIds) {
		mProcessEventIds = aProcessEventIds;
	}

	@Override
	public String getFilterAsString() {
		StringBuilder sb = new StringBuilder();
		String delim = "";
		for(int e : mProcessEventIds) {
			sb.append(e);
			sb.append(delim);
			delim = " ";
		}
		return sb.toString();
	}

	@Override
	public void setFilterAsString(String aStr) {
		mProcessEventIds.clear();
		String[] events = aStr.split(" ");
		for(String e : events) {
			mProcessEventIds.add(Integer.parseInt(e));
		}
	}
}
