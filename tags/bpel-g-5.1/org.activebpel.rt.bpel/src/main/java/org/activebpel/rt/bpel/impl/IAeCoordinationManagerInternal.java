//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeCoordinationManagerInternal.java,v 1.4 2008/03/28 01:39:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;

/**
 * Extension to the coordination manager implementation.
 */
public interface IAeCoordinationManagerInternal extends IAeCoordinationManager
{
   /**
    * Callback from the process manager when a process completes.
    * @param aProcessId
    * @param aFaultObject fault object if the process completed with a fault.
    * @param aNormalCompletion indiciates that the process completed normally and is eligible fo compensation.
    */
   public void onProcessCompleted(long aProcessId, IAeFault aFaultObject, boolean aNormalCompletion);

   /**
    * Dispatches the message to the destination.
    * @param aMessage
    * @param aViaProcessExeQueue
    */
   public void dispatch(IAeProtocolMessage aMessage, boolean aViaProcessExeQueue);

   /**
    * Save the current state information.
    * @param aCoordinating
    */
   public void persistState(IAeCoordinating aCoordinating) throws AeCoordinationException;

   /**
    * Sends a COMPENSATE_OR_COMPENSATE signal to participants.
    * @param aCoordinationId id
    */
   public void compensateOrCancel(String aCoordinationId);

   /**
    * Notifies the coordinators that a subprocess particpant has closed and 
    * therefore the coordinators are no longer reachable through compensation.
    * @param aProcessId
    * @param aJournalId
    */
   public void notifyCoordinatorsParticipantClosed(long aProcessId, long aJournalId);

   /**
    * Journals the receipt of a message by the coordination manager. This journal 
    * entry will be marked as done once the coordinator or participant processes 
    * the message and dispatches the behavior to the process. 
    * @param aProcessId
    * @param aMessage
    */
   public long journalCoordinationQueueMessageReceived(long aProcessId, IAeProtocolMessage aMessage);

   /**
    * Journals the notification that gets sent to 
    * @param aProcessId
    */
   public long journalNotifyCoordinatorsParticipantClosed(long aProcessId);
}
