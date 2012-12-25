package org.activebpel.rt.bpel;

public enum AeEngineEventType {
	ProcessCreated(0),
	ProcessTerminated(1),
	ProcessSuspended(2),
	ProcessResumed(3),
	ProcessStarted(4),
	ProcessRecreated(5);
	
	private final int code;
	AeEngineEventType(int code) {
		this.code = code;
	}
	public int code() {
		return code;
	}
}
