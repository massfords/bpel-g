package org.activebpel.rt.bpel.server.admin;

public enum AeEngineStatus {
	Created(0),
	Starting(1),
	Running(2),
	Stopping(3),
	Stopped(4),
	ShuttingDown(5),
	Shutdown(6),
	Error(7);
	
	private final int code;
	AeEngineStatus(int code) {
		this.code = code;
	}
	public int code() {
		return this.code;
	}
}
