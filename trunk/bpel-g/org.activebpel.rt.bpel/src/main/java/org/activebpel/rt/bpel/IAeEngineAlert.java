// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeEngineAlert.java,v 1.3 2007/09/28 19:37:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * Event for reporting alert events to engine listeners. These events will
 * include processes that are suspended due to the suspend activity and any
 * uncaught faults in a process which would lead to its termination.
 */
public interface IAeEngineAlert extends IAeEvent {
    /**
     * Returns the process id for the engine event.
     */
    public long getPID();

    /**
     * Returns the namespace qualified name of the process this event
     * represents.
     */
    public QName getProcessName();

    /**
     * Location of the activity generating the alert.
     */
    public String getLocation();

    /**
     * Alert events (like those from the suspend activity) may have some extra
     * details in the form of an xml document.
     */
    public Document getDetails();

    /**
     * Gets the fault name.
     */
    public QName getFaultName();

    public AeEngineAlertEventType getAlertType();
}
