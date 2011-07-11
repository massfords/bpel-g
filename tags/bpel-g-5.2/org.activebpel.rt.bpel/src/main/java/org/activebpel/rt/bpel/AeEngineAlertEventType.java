package org.activebpel.rt.bpel;

public enum AeEngineAlertEventType {
	ProcessSuspended(1000),
	ProcessFaulting(1001),
	ProcessInvokeRecovery(1002);
	
	private final int code;
	AeEngineAlertEventType(int code) {
		this.code = code;
	}
	public int code() {
		return code;
	}
}
