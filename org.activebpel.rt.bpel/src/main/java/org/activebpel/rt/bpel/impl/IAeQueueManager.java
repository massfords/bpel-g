// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeQueueManager.java,v 1.39 2007/01/26 22:38:23 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.bpel.impl.reply.AeMissingReplyReceiverException;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Defines the methods required to queue incoming data and activities that have
 * executed and are awaiting data.
 */
public interface IAeQueueManager extends IAeManager
{
   /**
    * Matches an inbound receive with a waiting message receiver.
    *
    * If a match is found...
    *    The reply, if not null, is added to the reply queue so we can track that
    *    the process actually replies. A process MUST reply to all of its outstanding
    *    replies in order to complete successfully. We then return the message receiver
    *    that was waiting for the message to arrive.
    * If a match is not found and the queued flag is true and the inbound receive contains correlation data...
    *    We will attempt to queue the inbound receive in hopes that an activity
    *    will soon execute and consume the data. The inbound receive will remain in the
    *    queue for as long as the engine's timeout period. If the receive has a reply
    *    waiting then it will also be added to our unmatched reply collection.
    * If a match is not found and the queued flag is true but the inbound receive DOES NOT contain correlation data...
    *    We throw a correlation violation.
    * If a match is not found and the queued flag is false...
    *    Simply return null. This case only applies when the caller knows that the inbound receive
    *    is capable of creating a process instance. If a match isn't found, then we don't want to
    *    queue the inbound receive, rather we'll create a new process instance to deal with it.
    * 
    * @param aInboundReceive A message that is targeting either an existing activity or capable of creating a new process
    * @param aQueueIfNotFoundFlag If true, then the inbound receive will be queued if no match is found
    * @param aAckCallback optional callback interface used in durable receives.
    * @throws AeCorrelationViolationException if the unmatched receive isn't correlated
    * @throws AeConflictingRequestException if matching this receive will result in two queued replies with the same partnerlink, porttype, operation, and pid
    */
   public AeMessageReceiver matchInboundReceive(AeInboundReceive aInboundReceive,
         boolean aQueueIfNotFoundFlag,
         IAeMessageAcknowledgeCallback aAckCallback) throws AeCorrelationViolationException, AeConflictingRequestException,
         AeBusinessProcessException;

   /**
    * Finds a matching message queue entry for the receiver and dequeues it.
    * @param aProcessId process Id for the message receiver
    * @param aMessageReceiverPathId The path id to the receiver
    * @return boolean True if the matching element was found and dequeued.
    */
   public boolean removeMessageReceiver(long aProcessId, int aMessageReceiverPathId) throws AeBusinessProcessException;

   /**
    * Queues a message receiver. This message receiver (e.g. receive, onMessage)
    * will stay queued until a message arrives for it from the outside world
    * or until it is explicity dequeued by the process.
    * @param aMessageReceiver The entry to add.
    */
   public void addMessageReceiver(AeMessageReceiver aMessageReceiver) throws AeBusinessProcessException;

   /**
    * The invoke queue object contains all of the necessary information to perform
    * an invocation on a target endpoint. The actual invoke operation is handled
    * at a different layer asynchronously.
    * @param aPlan may contain additional information from the deployment layer like invokeHandler or addressing information
    * @param aInvokeQueueObject The entry to add.
    */
   public void addInvoke(IAeProcessPlan aPlan, IAeInvokeInternal aInvokeQueueObject) throws AeBusinessProcessException;
  
   /**
    * Removes the matching <code>AeReply</code> from the queue.
    * @param aReplyQueueObject
    * @return queued AeReply that was removed.
    */
   public AeReply removeReply(AeReply aReplyQueueObject);
   
   /**
    * Sends the data to the response receiver that's waiting for the reply.
    * @param aReplyObject Identifies the reply object
    * @param aData data to send, if not <code>null</code> then the reply is a message
    * @param aFault fault to send, if not <code>null</code> then the reply is a fault
    * @param aProcessProperties The business process properties.
    * @throws AeMissingReplyReceiverException if no reply was found to be waiting
    * @throws AeBusinessProcessException if there is some communication error executing the reply
    */
   public void sendReply(AeReply aReplyObject, IAeMessageData aData, IAeFault aFault, Map aProcessProperties ) throws AeBusinessProcessException;

   /**
    * Returns an iterator over a read only collection of unmatched AeQueueReceiveObjects.
    */
   public Iterator getUnmatchedReceivesIterator();

   /**
    * Returns a listing of matching AeMessageReceivers based on
    * the filter criteria.
    * @param aFilter The selection criteria or null to select all.
    */
   public AeMessageReceiverListResult getMessageReceivers(AeMessageReceiverFilter aFilter) throws AeBusinessProcessException;
   
   /**
    * Queues a reply object. Once queued the reply must be dequeued in order for a
    * process to complete successfully.  If a reply is a durable reply, then the implementation
    * ignores the reply (i.e. short return) since durable replies are maintained and handled by 
    * the IAeBusinessProcess.
    * 
    * @param aReply Queue object for the reply
    * @param aMessageReceiver Previously queued receive that matches with this reply. Will be null in a create instance scenario.
    * @throws AeConflictingRequestException thrown if there is already a queued reply for this pid, partnerlink, porttype, and operation
    */
   public void addNonDurableReply(AeReply aReply, AeMessageReceiver aMessageReceiver) throws AeConflictingRequestException;
 
   /**
    * Schedules an alarm for execution. 
    * @param aProcessId The ID of the BPEL process this alarm is associated with.
    * @param aLocationPathId The location path id of the alarm.
    * @param aGroupId The group id of the alarm.
    * @param aAlarmId alarm id.
    * @param aDeadline The alarm deadline.
    */
   public void scheduleAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId, Date aDeadline)
         throws AeBusinessProcessException;

   /**
    * Removes an alarm.  The removal of an alarm requires it to be unscheduled (cancelled)
    * in addition to whatever other bookkeeping must be done by the alarm manager 
    * implementation.  Note that <code>removeAlarm</code> will be called by the 
    * engine when an alarm should be cancelled (not fired) as well as when the alarm
    * was fired and must now be removed from whatever internal storage the alarm manager
    * implementation is using.
    * 
    * @param aProcessId The ID of the BPEL process this alarm is associated with.
    * @param aLocationPathId The location path id of the alarm receiver.
    * @author aAlarmId alarm id.
    * @return True if the alarm was successfully removed.
    */
   public boolean removeAlarm(long aProcessId, int aLocationPathId, int aAlarmId) throws AeBusinessProcessException;

   /**
    * Removes an alarm for purposes of dispatching it to a process.  The alarm will be 
    * removed from storage and added to the process journal.  The journal id will be 
    * returned.  If the NULL journal id is returned, then the alarm was NOT deleted, and
    * therefore should not be dispatched to the process.
    * 
    * @param aProcessId
    * @param aGroupId
    * @param aLocationPathId
    * @param aAlarmId
    * @throws AeBusinessProcessException
    */
   public long removeAlarmForDispatch(long aProcessId, int aGroupId, int aLocationPathId, int aAlarmId) throws AeBusinessProcessException;

   /**
    * Returns a list of scheduled alarms matching the passed filter.
    * @param aFilter filter for alarms to be returned.
    * @return list of alarms matching the passed filter.
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter) throws AeBusinessProcessException;

   /**
    * Returns the configured receive handler for the protocol
    *  
    * @param aProtocol
    */
   public IAeReceiveHandler getReceiveHandler(String aProtocol) throws AeBusinessProcessException;
}
