//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeProcessCoordination.java,v 1.7 2008/03/28 01:40:04 mford Exp $
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
import org.activebpel.rt.bpel.coord.IAeCoordinator;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Interface that is responsible for handling coordination operations related to the process.
 */
public interface IAeProcessCoordination
{
   /**
    * Registers the coordination id with process invoke activity's enclosing scope.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @throws AeCoordinationException
    */
   public void registerCoordinationId(long aProcessId, String aLocationPath, String aCoordinationId) throws AeCoordinationException;

   /**
    * Deregisters the coordination id from process invoke activity's enclosing scope.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aCallback
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   public void deregisterCoordinationId(long aProcessId, String aLocationPath, String aCoordinationId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException;

   /**
    * Indicates the coordinated activity faulted.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    * @param aCallback
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   public void activityFaulted(long aProcessId, String aLocationPath, String aCoordinationId, IAeFault aFault, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException;

   /**
    * Installs a AeCoordinatorCompInfo object into the enclosing scope.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aCoordinator
    * @param aCallback
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   public void installCompensationHandler(long aProcessId, String aLocationPath, String aCoordinationId, IAeCoordinator aCoordinator, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException;

   /**
    * Handles the callback when a coordinated compensation has completed.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aJournalId
    * @param aCallback
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   public void compensationCompletedCallback(long aProcessId, String aLocationPath, String aCoordinationId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException;

   /**
    * Handles the callback when a coordinated compensation has faulted.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    * @param aJournalId
    * @param aCallback
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   public void compensationCompletedWithFaultCallback(long aProcessId, String aLocationPath, String aCoordinationId, IAeFault aFault, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException;

   /**
    * Compensates a sub process (participant).
    * @param aProcessId sub process id
    * @param aCoordinationId
    * @param aJournalId journalid if the compensate acitivity was journaled.
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   public void compensateSubProcess(long aProcessId, String aCoordinationId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException;

   /**
    * Terminates the subprocess's compensation handler if it is currently executing.
    * @param aProcessId
    * @param aJournalId
    * @param aCallback
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   public void cancelSubProcessCompensation(long aProcessId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException;

   /**
    * Cancels (terminates) process.
    * @param aProcessId
    * @param aJournalId
    * @param aCallback
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   public void cancelProcess(long aProcessId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException;

   /**
    * Signals that the sub process coordination has ended. The process identified by the
    * process id will not be compensated.
    * @param aProcessId
    * @param aJournalId
    * @param aCallback
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   public void subprocessCoordinationEnded(long aProcessId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException;

}
