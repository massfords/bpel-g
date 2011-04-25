package org.activebpel.rt.bpel.server.services;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessManager;

import bpelg.services.processes.AeProcessManager;
import bpelg.services.processes.StorageErrorMessage;
import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessList;

public class AeProcessManagerService implements AeProcessManager {
	
	private IAeProcessManager mProcessManager;

	public IAeProcessManager getProcessManager() {
		return mProcessManager;
	}

	public void setProcessManager(IAeProcessManager aProcessManager) {
		mProcessManager = aProcessManager;
	}

	@Override
	public int removeProcessByQuery(ProcessFilterType aFilter)
			throws StorageErrorMessage {
		try {
			return getProcessManager().removeProcesses(aFilter);
		} catch(AeBusinessProcessException e) {
			throw new StorageErrorMessage(e.getMessage(), e);
		}
	}

	@Override
	public ProcessList getProcesses(ProcessFilterType aBody)
			throws StorageErrorMessage {
		try {
			return getProcessManager().getProcesses(aBody);
		} catch (AeBusinessProcessException e) {
			throw new StorageErrorMessage(e.getMessage(), e);
		}
	}

}
