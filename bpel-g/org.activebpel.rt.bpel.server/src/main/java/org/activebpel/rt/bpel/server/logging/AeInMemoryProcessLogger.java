// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeInMemoryProcessLogger.java,v 1.9 2007/02/16 14:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import org.activebpel.rt.bpel.AePreferences;
import org.activebpel.rt.bpel.AeProcessEventType;
import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.engine.AeBPELProcessEventFormatter;
import org.activebpel.rt.bpel.server.engine.IAeProcessLogger;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * Maintains a StringBuffer of formatted process events for each process created
 * and executed by the engine. This class is intended to be a base class from
 * which to either persist the process log to a file or db.
 */
public class AeInMemoryProcessLogger implements IAeProcessLogger, PreferenceChangeListener {
    private static final Log sLog = LogFactory.getLog(AeInMemoryProcessLogger.class);

    /** Reference to the engine that we're listening to. */
    private IAeBusinessProcessEngineInternal mEngine;

    /** maps the process id to the string buffer */
    protected Map<Long, StringBuffer> mPidToBuffer = Collections.synchronizedMap(new HashMap<Long, StringBuffer>());

    /** used to filter out some log events */
    protected IAeLoggingFilter mFilter = null;
    
    public AeInMemoryProcessLogger() {
    	AePreferences.logEvents().addPreferenceChangeListener(this);
    }

    /**
     * @see org.activebpel.rt.bpel.IAeProcessListener#handleProcessEvent(org.activebpel.rt.bpel.IAeProcessEvent)
     */
    public boolean handleProcessEvent(IAeProcessEvent aEvent) {
        if (getLoggingFilter().accept(aEvent)) {
            String line = formatEvent(aEvent);
            try {
                appendToLog(aEvent.getPID(), line);
                if (isCloseEvent(aEvent))
                    closeLog(aEvent.getPID());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @see org.activebpel.rt.bpel.IAeProcessListener#handleProcessInfoEvent(org.activebpel.rt.bpel.IAeProcessInfoEvent)
     */
    public void handleProcessInfoEvent(IAeProcessInfoEvent aEvent) {
        if (getLoggingFilter().accept(aEvent)) {
            String line = formatEvent(aEvent);
            appendToLog(aEvent.getPID(), line);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#getAbbreviatedLog(long)
     */
    public String getAbbreviatedLog(long aPid) throws Exception {
        String log = ""; //$NON-NLS-1$
        StringBuffer buffer = getBuffer(aPid, false);
        if (buffer != null) {
            log = buffer.toString();
        }

        return log;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#getFullLog(long)
     */
    public Reader getFullLog(long aProcessId) throws Exception {
        return new StringReader(getAbbreviatedLog(aProcessId));
    }

    /**
     * Appends the formatted line to the process's StringBuffer
     * 
     * @param aPid
     * @param aLine
     */
    protected void appendToLog(long aPid, String aLine) {
        if (AeUtil.notNullOrEmpty(aLine)) {
            sLog.debug(aLine);
            StringBuffer buffer = getBuffer(aPid, true);
            synchronized (buffer) {
                buffer.append(aLine);
                buffer.append("\r\n"); //$NON-NLS-1$
            }
        }
    }

    /**
     * Gets the buffer for the specified process.
     * 
     * @param aPid
     * @param aCreateIfNotFound
     */
    protected StringBuffer getBuffer(long aPid, boolean aCreateIfNotFound) {
        StringBuffer buffer = getBufferMap().get(aPid);
        if (buffer == null && aCreateIfNotFound) {
            synchronized (getBufferMap()) {
                buffer = getBufferMap().get(aPid);
                if (buffer == null) {
                    buffer = new StringBuffer();
                    getBufferMap().put(aPid, buffer);
                }
            }
        }
        return buffer;
    }

    /**
     * Returns true if the event signals that the process has ended
     * 
     * @param aEvent
     */
    protected boolean isCloseEvent(IAeProcessEvent aEvent) {
        return "/process".equals(aEvent.getNodePath()) && (aEvent.getEventType() == AeProcessEventType.ExecuteComplete || aEvent.getEventType() == AeProcessEventType.ExecuteFault); //$NON-NLS-1$
    }

    /**
     * Getter for the buffer map.
     */
    protected Map<Long, StringBuffer> getBufferMap() {
        return mPidToBuffer;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#setEngine(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
     */
    public void setEngine(IAeBusinessProcessEngineInternal aEngine) {
        mEngine = aEngine;
    }

    /**
     * Getter for the engine.
     */
    protected IAeBusinessProcessEngineInternal getEngine() {
        return mEngine;
    }

    /**
     * Default impl does nothing. Called when the process completes and no more
     * log events will be fired.
     */
    protected void closeLog(long aPid) throws IOException {
    }

    /**
     * Formats the event using the MessageFormat based
     * <code>AeBPELProcessEventFormatter</code>.
     * 
     * @param aEvent
     */
    protected String formatEvent(IAeProcessEvent aEvent) {
        return AeBPELProcessEventFormatter.getInstance().formatEvent(aEvent);
    }

    /**
     * Formats the event using the MessageFormat based
     * <code>AeBPELProcessEventFormatter</code>.
     * 
     * @param aEvent
     */
    protected String formatEvent(IAeProcessInfoEvent aEvent) {
        return AeBPELProcessEventFormatter.getInstance().formatEvent(aEvent);
    }

    /**
     * Getter for the logging filter.
     */
    public IAeLoggingFilter getLoggingFilter() {
        return mFilter;
    }

    /**
     * Setter for the logging filter
     * 
     * @param aFilter
     */
    public void setLoggingFilter(IAeLoggingFilter aFilter) {
        mFilter = aFilter;
    }

	@Override
	public void preferenceChange(PreferenceChangeEvent aEvt) {
		init();
	}

	// FIXME config test
	public void init() {
		Set<AeProcessEventType> enabledEvents = AePreferences.getEnabledLogEvents();
		getLoggingFilter().setEnabledEventTypes(enabledEvents);
		if (getLoggingFilter().isEnabled()) {
			getEngine().addProcessListener(this);
		} else {
			getEngine().removeProcessListener(this);
		}
	}
}
