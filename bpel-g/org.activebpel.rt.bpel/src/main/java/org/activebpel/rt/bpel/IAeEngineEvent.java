// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeEngineEvent.java,v 1.16 2007/09/28 19:37:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import javax.xml.namespace.QName;

/**
 * Interface for engine events
 */
public interface IAeEngineEvent extends IAeEvent {
    /**
     * Returns the process id for the engine event.
     */
    public long getPID();

    /**
     * Returns the namespace qualified name of the process this event represents.
     */
    public QName getProcessName();

    public AeEngineEventType getEventType();
}
