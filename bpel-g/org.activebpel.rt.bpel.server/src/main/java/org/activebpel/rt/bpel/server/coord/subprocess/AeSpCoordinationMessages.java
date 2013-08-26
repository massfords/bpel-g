//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/AeSpCoordinationMessages.java,v 1.4 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess;

import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.server.coord.AeProtocolMessage;

/**
 * Coordination messages used in the AE subprocess Participant Completion coordination protocol.
 */
public class AeSpCoordinationMessages {
    //
    // Coordinator generated:
    //
    /**
     * Close message.
     */
    public static final IAeProtocolMessage CLOSE = new AeProtocolMessage("aesp:Close");  //$NON-NLS-1$

    /**
     * Cancel message
     */
    public static final IAeProtocolMessage CANCEL = new AeProtocolMessage("aesp:Cancel");  //$NON-NLS-1$

    /**
     * Compensate message
     */
    public static final IAeProtocolMessage COMPENSATE = new AeProtocolMessage("aesp:Compensate");  //$NON-NLS-1$

    /**
     * Compensate (if completed) or Cancel (if active) message
     */
    public static final IAeProtocolMessage COMPENSATE_OR_CANCEL = new AeProtocolMessage("aesp:CompensateOrCancel");  //$NON-NLS-1$

    /**
     * FORGET message
     */
    public static final IAeProtocolMessage FORGET = new AeProtocolMessage("aesp:Forget");  //$NON-NLS-1$

    //
    // generated by either the coordinate or participant:
    //

    /**
     * Faulted message.
     */
    public static final IAeProtocolMessage FAULTED = new AeProtocolMessage("aesp:Faulted");  //$NON-NLS-1$

    // generated by participant
    /**
     * EXITED message
     */
    public static final IAeProtocolMessage EXITED = new AeProtocolMessage("aesp:Exited");  //$NON-NLS-1$

    /**
     * Process canceled message.
     */
    public static final IAeProtocolMessage CANCELED = new AeProtocolMessage("aesp:Canceled");  //$NON-NLS-1$

    /**
     * Process completed message.
     */
    public static final IAeProtocolMessage COMPLETED = new AeProtocolMessage("aesp:Completed");  //$NON-NLS-1$

    /**
     * Process closed message.
     */
    public static final IAeProtocolMessage CLOSED = new AeProtocolMessage("aesp:Closed");  //$NON-NLS-1$

    /**
     * Compensated messaged.
     */
    public static final IAeProtocolMessage COMPENSATED = new AeProtocolMessage("aesp:Compensated");  //$NON-NLS-1$

    /**
     * Faulted while active message
     */
    public static final IAeProtocolMessage FAULTED_ACTIVE = new AeProtocolMessage("aesp:FaultedActive");  //$NON-NLS-1$

    /**
     * Faulted while compensating message.
     */
    public static final IAeProtocolMessage FAULTED_COMPENSATING = new AeProtocolMessage("aesp:FaultedCompensating");  //$NON-NLS-1$

    // todo: Need messages for wsba:GetStatus and wsba:Status.


    /**
     * Convenience method to create new message signal based on the data of the another message.
     *
     * @param aSignal  new signal
     * @param aMessage original message (source).
     */
    public static IAeSpProtocolMessage create(String aSignal, IAeSpProtocolMessage aMessage) {
        IAeSpProtocolMessage rVal = new AeSpProtocolMessage(aSignal, aMessage.getCoordinationId(), aMessage.getFault(), aMessage.getProcessId(), aMessage.getLocationPath(), aMessage.getJournalId(), aMessage.getSourceProcessId());
        return rVal;
    }
}
