package org.activebpel.rt.bpel;

public enum ProcessEventType {
	   Inactive(-1), 
	   ReadyToExecute(0), 
	   Executing(1), 
	   ExecuteComplete(2), 
	   ExecuteFault(3), 
	   LinkStatus(4), 
	   DeadPathStatus(5),
	   Terminated(6), 
	   Migrated(12), 
	   Suspended(13), 
	   Faulting(14);
	   
	   private final int code;
	   ProcessEventType(int aCode) {
		   this.code = aCode;
	   }
	   public int code() {
		   return this.code;
	   }
   }