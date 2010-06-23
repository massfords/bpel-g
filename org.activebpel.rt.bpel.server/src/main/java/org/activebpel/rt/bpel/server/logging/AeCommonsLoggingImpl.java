package org.activebpel.rt.bpel.server.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AeCommonsLoggingImpl implements IAeLogWrapper {
	
	private Log mLog;
	
	public AeCommonsLoggingImpl(Class aClass) {
		mLog = LogFactory.getLog(aClass);
	}
	
	public AeCommonsLoggingImpl(Log aLog) {
	    mLog = aLog;
	}

	public void logDebug(String aMessage) {
		mLog.debug(aMessage);
	}

	public void logError(String aMessage, Throwable aProblem) {
		mLog.error(aMessage, aProblem);
	}

	public void logInfo(String aMessage) {
		mLog.info(aMessage);
	}

}
