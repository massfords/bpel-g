package org.activebpel.rt.bpel;

public enum ProcessInfoEventType {
	
	InfoEvents(1000),
	InfoOnAlarm(1001),
	InfoWait(1002),
	InfoJoin(1003),
	InfoWhile(1004),
	InfoCase(1005),
	InfoLinkTransition(1006),
	InfoForEachStart(1007),
	InfoForEachFinal(1008),
	InfoForEachCompletionCondition(1009),
	InfoForEachCompletionConditionMet(1010),
	InfoEarlyTermination(1011),
	InfoWaitingForLock(1012),
	InfoProcessCompensationStarted(1013),
	InfoProcessCompensationFinished(1014),
	InfoProcessCompensationFaulted(1015),
	InfoProcessCompensationTerminated(1016),
	InfoRepeatUntil(1017),
	InfoIf(1018),
	InfoElseIf(1019),
	GenericInfoEvent(1500),
	WarnEvents(2000),
	ErrorEvents(3000),
	ErrorEventNotHandled(3001),
	ErrorRestartActivity(3002),
	ErrorAssignActivity(3003),
	ErrorOnEventValidation(3004),
	LastErrorEventId(4000),
	EventIdAndInfoFormat(4001);
	
	
	private final int code; 
	ProcessInfoEventType(int code) {
		this.code = code;
	}
	
	public int code() {
		return this.code;
	}
}
