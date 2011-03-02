// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/process/IAeProcessStateWriter.java,v 1.10 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.process;

import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Defines interface for writing process state to storage.
 */
public interface IAeProcessStateWriter
{
   /**
    * Creates journal entry to recover alarm in the event of engine failure.
    *
    * @param aProcessId
    * @param aLocationId The location id of the BPEL object that received the alarm.
    * @param aGroupId The group id of the alarm.
    * @param aAlarmId alarm execution reference id.
    */
   public long journalAlarm(long aProcessId, int aLocationId, int aGroupId, int aAlarmId);

   /**
    * Creates journal entry to recover inbound receive in the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aLocationId The location id of the BPEL object that received the message.
    * @param aInboundReceive The received message.
    */
   public long journalInboundReceive(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive);

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
   public long journalInvokeData(long aProcessId, int aLocationId, long aTransmissionId, IAeMessageData aMessageData, Map aProcessProperties);

   /**
    * Creates journal entry to recover invoke fault in the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aLocationId The location id of the BPEL object that received the fault.
    * @param aTransmissionId The invoke activity's tranmission id.
    * @param aFault The fault received from the invoke.
    * @param aProcessProperties
    */
   public long journalInvokeFault(long aProcessId, int aLocationId, long aTransmissionId, IAeFault aFault, Map aProcessProperties);

   /**
    * Creates journal entry to recover sent reply in the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aSentReply The sent reply.
    * @param aProcessProperties
    */
   public long journalSentReply(long aProcessId, AeReply aSentReply, Map aProcessProperties);
   
   /**
    * Creates journal entry to recover durable invoke's transmission id the event of engine
    * failure.
    *
    * @param aProcessId
    * @param aLocationId The location id of the BPEL object that received the message. 
    * @param aTransmissionId The durable invoke transmission id.
    * @return journal id.
    */
   public long journalInvokeTransmitted(long aProcessId, int aLocationId, long aTransmissionId);
   
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
   public long journalInvokePending(long aProcessId, int aLocationId);

   /**
    * Creates journal entry to recover an engine failure in the event that the
    * current recovery engine itself fails. 
    *
    * @param aProcessId
    * @param aDeadEngineId
    */
   public long journalEngineFailure(long aProcessId, int aDeadEngineId);

   /**
    * Writes the state of the given process to storage.
    *
    * @param aProcessWrapper
    * @return The number of pending invoke activities (for debugging output).
    */ 
   public int writeProcess(AeProcessWrapper aProcessWrapper) throws AeBusinessProcessException;
   
   /** 
    * @return returns next journal id.
    * @throws AeBusinessProcessException
    */
   public long getNextJournalId() throws AeBusinessProcessException;

   /**
    * Journals a message that needs to be requeued with the coordination manager
    * @param aProcessId
    * @param aMessage
    */
   public long journalCoordinationQueueMessage(long aProcessId, IAeProtocolMessage aMessage);

   /**
    * Journals the cancel signal for a process
    * @param aProcessId
    */
   public long journalCancelProcess(long aProcessId);

   /**
    * Journals the cancel subprocess compensation signal for a process
    * @param aProcessId
    */
   public long journalCancelSubprocessCompensation(long aProcessId);

   /**
    * Journals the signal to release compensation resources for a process.
    * @param aProcessId
    */
   public long journalReleaseCompensationResources(long aProcessId);

   /**
    * Journals the signal to child-coordinators that their parent has been closed.
    * @param aProcessId
    */
   public long journalNotifyCoordinatorsParticipantClosed(long aProcessId);

   /**
    * Journals the callback signal that compesation has completed.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    */
   public long journalCompensateCallback(long aProcessId, String aLocationPath,
         String aCoordinationId, IAeFault aFault);

   /**
    * Journals the signal that the coordinated activity has completed.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    */
   public long journalCoordinatedActivityCompleted(long aProcessId,
         String aLocationPath, String aCoordinationId, IAeFault aFault);

   /**
    * Journals the signal to deregister the location path's enclosing scope
    * from the coordination.
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    */
   public long journalDeregisterCoordination(long aProcessId,
         String aLocationPath, String aCoordinationId);
}
