// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeProcessManager.java,v 1.29 2008/03/28 01:41:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAePlanManager;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Defines the interface for process managers.
 */
public interface IAeProcessManager extends IAeManager
{
   /** "Null" value for journal entry id. */
   public static final long NULL_JOURNAL_ID = 0;

   /**
    * Creates a business process from the process plan.
    *
    * @param aProcessPlan
    * @return the process instance
    * @throws AeBusinessProcessException
    */
   public IAeBusinessProcess createBusinessProcess(IAeProcessPlan aProcessPlan) throws AeBusinessProcessException;

   /**
    * Returns the business process with the specified process id, locking the
    * process into memory. <em>Each call to {@link #getProcess(long)} must be
    * followed eventually by a matching call to
    * {@link #releaseProcess(IAeBusinessProcess)}</em>.
    *
    * @param aProcessId
    * @throws AeBusinessProcessException if there is an error restoring the process or acquiring its lock, no need to call releaseProcess in this case 
    */
   public IAeBusinessProcess getProcess(long aProcessId) throws AeBusinessProcessException;

   /**
    * Returns the business process with the specified process id, locking the
    * process into memory. <em>Each call to {@link #getProcessNoUpdate(long)}
    * must be followed eventually by a matching call to
    * {@link #releaseProcess(IAeBusinessProcess)}</em>. Calling
    * {@link #getProcessNoUpdate(long)} instead of {@link #getProcess(long)}
    * asserts that the caller will not modify the process, so that the process
    * manager may discard the process from memory after it is released without
    * updating its persistent representation (if there is one).
    *
    * @param aProcessId
    */
   public IAeBusinessProcess getProcessNoUpdate(long aProcessId) throws AeBusinessProcessException;

   /**
    * Returns the process instance details for the process with the specified process id.
    *
    * @param aProcessId
    * @return AeProcessInstanceDetail
    */
   public AeProcessInstanceDetail getProcessInstanceDetails(long aProcessId);

   /**
    * Returns a result set of processes based upon filter specification.
    *
    * @param aFilter the filter specification used to limit processes returned, may be null.
    */
   public AeProcessListResult getProcesses(AeProcessFilter aFilter) throws AeBusinessProcessException;

   /**
    * Returns process ids that match the given filter.
    *
    * @param aFilter
    * @throws AeBusinessProcessException
    */
   public long[] getProcessIds(AeProcessFilter aFilter) throws AeBusinessProcessException;

   /**
    * Returns the number of processes that match the filter specification.
    *
    * @param aFilter the filter specification used to limit processes returned, may be null.
    */
   public int getProcessCount(AeProcessFilter aFilter) throws AeBusinessProcessException;

   /**
    * Indicates that the engine has processed the given journal entry, so the
    * process manager should delete the journal entry from storage when the 
    * process is next persisted.
    *
    * @param aProcessId
    * @param aJournalId The id of the journal entry to delete.
    */
   public void journalEntryDone(long aProcessId, long aJournalId);

   /**
    * Creates journal entry to recover inbound receive in the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aLocationId The location id of the BPEL object that received the message.
    * @param aInboundReceive The received message.
    */
   public long journalInboundReceive(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive) throws AeBusinessProcessException;

   /**
    * Creates journal entry to recover invoke data in the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aLocationId The location id of the BPEL object that received the message.
    * @param aTransmissionId The invoke activity's transmission id.
    * @param aMessageData The message data received from the invoke.
    * @param aProcessProperties
    */
   public long journalInvokeData(long aProcessId, int aLocationId, long aTransmissionId, IAeMessageData aMessageData, Map aProcessProperties) throws AeBusinessProcessException;

   /**
    * Creates journal entry to recover invoke fault in the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aLocationId The location id of the BPEL object that received the fault.
    * @param aTransmissionId The invoke activity's transmission id.
    * @param aFault The fault received from the invoke.
    * @param aProcessProperties
    */
   public long journalInvokeFault(long aProcessId, int aLocationId, long aTransmissionId, IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException;

   /**
    * Creates journal entry to recover sent reply in the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aSentReply The sent reply.
    * @param aProcessProperties
    */
   public void journalSentReply(long aProcessId, AeReply aSentReply, Map aProcessProperties) throws AeBusinessProcessException;

   /**
    * Creates journal entry to recover durable invoke's transmission id the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aLocationId The location id of the BPEL object that received the message. 
    * @param aTransmissionId The durable invoke transmission id.
    * @throws AeBusinessProcessException
    */
   public void journalInvokeTransmitted(long aProcessId, int aLocationId, long aTransmissionId) throws AeBusinessProcessException;
   
   
   /**
    * Creates journal entry to recover sub process instance compensate in the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aCoordinationId The coordination id.
    * @return journal id.
    */
   public long journalCompensateSubprocess(long aProcessId, String aCoordinationId);

   /**
    * Creates journal entry to recover pending invoke in the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aLocationId
    */
   public long journalInvokePending(long aProcessId, int aLocationId) throws AeBusinessProcessException;

   /**
    * Handles process termination.
    *
    * @param aProcessId
    */
   public void processEnded(long aProcessId);

   /**
    * Releases a process locked into memory by {@link #getProcess(long)}.
    *
    * @param aProcess The process to release.
    */
   public void releaseProcess(IAeBusinessProcess aProcess);

   /**
    * Removes a business process.
    *
    * @param aProcessId
    */
   public void removeProcess(long aProcessId) throws AeBusinessProcessException;

   /**
    * Removes processes based upon filter specification and returns the number
    * of processes removed.
    *
    * @param aFilter the filter specification
    */
   public int removeProcesses(AeProcessFilter aFilter) throws AeBusinessProcessException;

   /**
    * Sets the plan manager that resolves a process <code>QName</code> to a
    * context WSDL provider for the process.
    *
    * @param aPlanManager
    */
   public void setPlanManager(IAePlanManager aPlanManager) throws AeBusinessProcessException;

   /**
    * Return the process qname given the pid WITHOUT locking
    * the process.
    * @param aProcessId
    * @return QName The process QName.
    */
   public QName getProcessQName(long aProcessId) throws AeBusinessProcessException;
   
   /** 
    * @return Returns the next journal id.
    * @throws AeBusinessProcessException
    */
   public long getNextJournalId() throws AeBusinessProcessException;
   
   /**
    * Indicates that the process's invoke activity is no longer using
    * the durable/persistent transmission id and that it may be deleted
    * from the transmission tracker.
    * @param aProcessId
    * @param aTransmissionId The id of the transmission to delete.
    */
   public void transmissionIdDone(long aProcessId, long aTransmissionId);   

   /**
    * Adds the given listener to the list of listeners that are notified when
    * this process manager purges a process.
    *
    * @param aListener
    */
   public void addProcessPurgedListener(IAeProcessPurgedListener aListener);

   /**
    * Removes the given listener from the list of listeners that are notified
    * when this process manager purges a process.
    *
    * @param aListener
    */
   public void removeProcessPurgedListener(IAeProcessPurgedListener aListener);

   /**
    * Indicates that the next time the process is persisted, the process manager
    * should set aside the given journal entry for the purpose of possibly
    * restarting the process. 
    *
    * @param aProcessId
    * @param aJournalId The id of the journal entry to save for restart.
    */
   public void journalEntryForRestart(long aProcessId, long aJournalId);

   /**
    * Recreates a business process from the given process plan.
    *
    * @param aProcessId 
    * @param aProcessPlan
    * @return the process instance
    * @throws AeBusinessProcessException
    */
   public IAeBusinessProcess recreateBusinessProcess(long aProcessId, IAeProcessPlan aProcessPlan) throws AeBusinessProcessException;

   /**
    * Journals the receipt of a message by the coordinator or participant. 
    * This journal entry will be marked as done once the coordinator or participant 
    * processes the message. 
    * @param aProcessId
    * @param aMessage
    */
   public long journalCoordinationQueueMessageReceived(long aProcessId, IAeProtocolMessage aMessage);

   /**
    * Journals the cancellation of a subprocess's compensation
    * @param aProcessId
    */
   public long journalCancelSubprocessCompensation(long aProcessId);

   /**
    * Journals the cancellation of a process.
    * @param aProcessId
    */
   public long journalCancelProcess(long aProcessId);

   /**
    * Journals that the process is releasing its compensation resources owing to
    * the completion of a subprocess coordination. This will transition the 
    * process from compensatable to completed.
    * @param aProcessId
    */
   public long journalReleaseCompensationResources(long aProcessId);

   /**
    * Journals that the process has completed and we need to notify and coordinators
    * attached to this process that the process has reached a final state.
    * @param aProcessId
    */
   public long journalNotifyCoordinatorsParticipantClosed(long aProcessId);

   /**
    * Journals that the compensation of a subprocess has completed.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    */
   public long journalCompensateCallback(long aProcessId, String aLocationPath,
         String aCoordinationId, IAeFault aFault);

   /**
    * Journals that the coordinated activity at the given location path has completed.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    */
   public long journalCoordinatedActivityCompleted(long aProcessId, String aLocationPath, String aCoordinationId, IAeFault aFault);

   /**
    * Journals that the enclosing scope for the location path is being deregistered
    * from the coordination.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    */
   public long journalDeregisterCoordination(long aProcessId, String aLocationPath, String aCoordinationId);
}
