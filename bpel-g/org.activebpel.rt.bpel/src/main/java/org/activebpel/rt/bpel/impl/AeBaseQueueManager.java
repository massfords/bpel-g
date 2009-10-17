// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeBaseQueueManager.java,v 1.37.4.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import commonj.timers.Timer;
import commonj.timers.TimerListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeMonitorListener;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilterManager;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilterManager;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeAlarm;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.bpel.impl.reply.AeQueueManagerReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * An in-memory implementation of the queue manager. All of the queues are implemented
 * with java.util.Collection implementations.
 */
public abstract class AeBaseQueueManager extends AeManagerAdapter implements IAeQueueManager
{
   public static final String CONFIG_UNMATCHED_RECEIVES_COUNT = "UnmatchedReceivesCount"; //$NON-NLS-1$
   public static final int DEFAULT_UNMATCHED_RECEIVES_COUNT = 50;

   /** inbound receives that have not been matched to activities */
   private List mUnmatchedReceives = Collections.synchronizedList(new LinkedList());
   /** activites that are waiting to receive data */
   private Collection mMessageReceivers = Collections.synchronizedSet(new TreeSet(sMessageReceiverComparator));
   /** contains the reply portion of an inbound receive that has been matched and is waiting to receive its response from its call */
   private Map mReplies = Collections.synchronizedMap(new HashMap());
   /** next id for unmatched receives */
   private long mQueueId = 0;
   /** comparator used to keep the message receiver list sorted by # of correlated properties */
   private static final Comparator sMessageReceiverComparator = new AeMessageReceiverComparator();

   /** the maximum number of unmatched receives */
   private int mMaxUnmatchedReceivesCount;
   /** flag that let's us know if we've been started */
   private boolean mStarted = false;
   /** Maps AeAlarm objects to the scheduled task's timer id. */
   private Map mLookup = new HashMap();
   /** Saved map for in-memory starts and stops. */
   private Map mSavedLookup;
   /** Next journal id for non-persitent implementation of the queue manager. */
   private static long NONPERSISTENT_QUEUEMANAGER_NEXT_ID = IAeProcessManager.NULL_JOURNAL_ID + 1;

   /**
    * Constructs an in-memory queue manager.
    *
    * @param aConfig The configuration map for this manager.
    */
   public AeBaseQueueManager(Map aConfig)
   {
      setConfig(aConfig);
   }

   /**
    * Gets the next journal id.
    */
   protected long getNextJournalId()
   {
      return NONPERSISTENT_QUEUEMANAGER_NEXT_ID;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#getUnmatchedReceivesIterator()
    */
   public Iterator getUnmatchedReceivesIterator()
   {
      synchronized(getUnmatchedReceives())
      {
         return Collections.unmodifiableList( getUnmatchedReceives() ).iterator();
      }
   }

   /**
    * Attempts to find a match for the message receiver among the unmatched
    * inbound receives. If the message receiver doesn't have any correlation data
    * then we're only matching on the partnerlink, port type, and operation. Otherwise,
    * we include the correlation information in the check. If there was no match
    * then we return null.
    * @param aMessageReceiver
    */
   protected AeUnmatchedReceive findMatchForMessageReceiver(AeMessageReceiver aMessageReceiver)
   {
      // if the message receiver is correlated, then look for an exact match
      // among the inbound receives
      if (aMessageReceiver.isCorrelated())
      {
         for (Iterator iter = getUnmatchedReceives().iterator(); iter.hasNext();)
         {
            AeUnmatchedReceive unmatchedReceive = (AeUnmatchedReceive) iter.next();
            if (aMessageReceiver.correlatesTo(unmatchedReceive.getInboundReceive()))
            {
               iter.remove();
               return unmatchedReceive;
            }
         }
      }
      else
      {
         // if the message receiver is not correlated, then we only need to match
         // on the partner link, port type, and operation.
         for (Iterator iter = getUnmatchedReceives().iterator(); iter.hasNext();)
         {
            AeUnmatchedReceive unmatchedReceive = (AeUnmatchedReceive) iter.next();
            if (aMessageReceiver.matches(unmatchedReceive.getInboundReceive()))
            {
               iter.remove();
               return unmatchedReceive;
            }
         }
      }
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#addMessageReceiver(org.activebpel.rt.bpel.impl.queue.AeMessageReceiver)
    */
   public void addMessageReceiver(AeMessageReceiver aMessageReceiver) throws AeBusinessProcessException
   {
      // check for a match before adding
      synchronized(getUnmatchedReceives())
      {
         synchronized(getMessageReceivers())
         {
            // Always queue the message receiver.
            addMessageReceiverInternal(aMessageReceiver);

            // This will look to see if we already have an inbound message waiting that matches the activity.
            // If we already have a message waiting, we will dispatch that message to the process via
            // the engine's dispatchReceiveData() method - this allows us to route ALL 'incoming' messages
            // to the engine in the same way.

            // Note that the inbound receive (if found) is also removed from the inbound receive queue, so it
            // MUST be properly handled here.
            AeUnmatchedReceive foundMatch = null;
            do
            {
               foundMatch = findMatchForMessageReceiver(aMessageReceiver);
               if (foundMatch != null)
               {
                  queueReceiveDataInEngine(foundMatch.getInboundReceive(), foundMatch.getAckCallback());
               }
            }
            // keep pulling all of the matched messages if the receiver supports concurrency
            while(aMessageReceiver.isConcurrent() && foundMatch != null);
         }
      }
   }

   /**
    * Queues the receive data in the business process engine.  In addition, this method will send a fault to
    * the reply if an exception is thrown when queueing the receive data.
    *
    * @param aFoundMatch
    */
   protected void queueReceiveDataInEngine(AeInboundReceive aFoundMatch, IAeMessageAcknowledgeCallback aAckCallback)
   {
      try
      {
         getEngine().queueReceiveData(aFoundMatch, aAckCallback, true);
      }
      catch (Throwable t)
      {
         IAeFault fault = AeFaultFactory.getSystemErrorFault(t);
         replyFault(aFoundMatch, aAckCallback, aFoundMatch.getContext().getBusinessProcessProperties(), fault, t);
      }
   }

   /**
    * Convenience method to call back the optional <code>IAeMessageAcknowledgeCallback</code>
    * and reply with a fault.
    * @param aInboundReceive
    * @param aAckCallback
    * @param aFault fault.
    * @param aReason optional reason for faulting.
    */
   protected void replyFault(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback,
         Map aProcessProperties, IAeFault aFault, Throwable aReason)
   {
      // If a durable messaging callback is given then, callback.
      if (aAckCallback != null)
      {
         try
         {
            // ack/notAck based on request type.
            if (aInboundReceive.isOneway())
            {
               if (aReason == null && aFault != null)
               {
                  aReason = new Exception(aFault.getInfo());
               }
               // always not-ack for one-way messages. (e.g. use - unmatched receive timeout).
               aAckCallback.onNotAcknowledge(aReason);
            }
            else
            {
               // for two-way message, do normal ack, since a reply with a fault will follow.
               aAckCallback.onAcknowledge(null);
            }
         }
         catch(Throwable t)
         {
            AeException.logError(t, AeMessages.getString("AeBaseQueueManager.ERROR_3")); //$NON-NLS-1$
         }
      }

      // for two-way message, always reply with the fault.
      if (aInboundReceive.getReplyReceiver() != null)
      {
         try
         {
            aInboundReceive.getReplyReceiver().onFault(aFault, aProcessProperties);
         }
         catch (Throwable t)
         {
            AeException.logError(t, AeMessages.getString("AeBaseQueueManager.ERROR_3")); //$NON-NLS-1$
         }
      }
   }

   /**
    * This method is called to put a receive (message receiver) on the receive queue.
    *
    * @param aMessageReceiver The receive to put on the queue.
    */
   protected void addMessageReceiverInternal(AeMessageReceiver aMessageReceiver)
   {
      // the caller will have sync'd on the message receivers coll so it's safe to add/sort
      getMessageReceivers().add(aMessageReceiver);
   }

   /**
    * Attempts to add an unmatched receive which will get matched at a later point.
    * This implementation will reject an inbound receive that is lacking correlation
    * data, opting to throw a bpws:correlationViolation fault instead.
    * @param aInboundReceive An inbound receive that didn't match up to a waiting message receiver
    * @param aAckCallback optional durable invoke callback interface.
    * @throws AeCorrelationViolationException
    */
   protected void addUnmatchedReceive(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback) throws AeCorrelationViolationException
   {
      if (aInboundReceive.isCorrelated())
      {
         AeUnmatchedReceive unmatchedReceive = new AeUnmatchedReceive(aInboundReceive, aAckCallback);
         getUnmatchedReceives().add(unmatchedReceive);
         if (unmatchedReceive.getQueueId() == null)
         {
            unmatchedReceive.setQueueId(getNextQueueId());
         }
         // Limit the size of the unmatched receives list.
         if (getUnmatchedReceives().size() > getMaxUnmatchedReceivesCount())
         {
            // Notify monitor that a correlated msg was discarded because cache was full
            getEngine().fireMonitorEvent(IAeMonitorListener.MONITOR_CORR_MSG_DISCARD, 1);
            
            AeUnmatchedReceive first = (AeUnmatchedReceive) getUnmatchedReceives().get(0);
            removeUnmatchedReceive(first.getQueueId());

            // Make sure it's really gone from the list at this point;
            // otherwise, we could be trying to remove the first one over and
            // over again.
            getUnmatchedReceives().remove(first);
         }
      }
      else
      {
         throw new AeUnmatchedRequestException(aInboundReceive.getProcessPlan().getProcessDef().getNamespace());
      }
   }

   /**
    * Gets the next id to use for queued objects.
    */
   private String getNextQueueId()
   {
      mQueueId++;
      return String.valueOf(mQueueId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#matchInboundReceive(org.activebpel.rt.bpel.impl.queue.AeInboundReceive, boolean, org.activebpel.wsio.IAeMessageAcknowledgeCallback)
    */
   public AeMessageReceiver matchInboundReceive(AeInboundReceive aInboundReceive, boolean aQueueIfNotFoundFlag, IAeMessageAcknowledgeCallback aAckCallback)
                   throws AeCorrelationViolationException, AeConflictingRequestException, AeBusinessProcessException
   {
      synchronized(getUnmatchedReceives())
      {
         synchronized(getMessageReceivers())
         {
            // The findMatchForInboundReceive(..) attempts to find a waiting message receiver.
            // If a receiver is found, then the message is consumed.
            //
            // Consuming of the message involves:
            // 1. Remove Message Receiver from queue
            // 2. Remove alarms
            // 3. Journal inbound receive and assign journal id to the message receiver.
            // 4. If there is a msg callback, the do the call back.
            //  (Persistent versions does the above steps with in an active transaction.

            AeMessageReceiver found = findMatchForInboundReceive(aInboundReceive, aAckCallback);

            if (found == null && aQueueIfNotFoundFlag)
            {
               // message receiver (onMessage, Receive activity not found: hold onto this inbound receive in case a message receiver executes and consumes it).
               addUnmatchedReceive(aInboundReceive, aAckCallback);
            }
            else if (found != null)
            {
               // A matching message receiver was found. If this is two-way message, add the reply
               // to the collection.
               //
               // Use the journal id as the reply id. If a journal id has not been assigned (e.g. persistence = None), then assign one.
               //
               if (aInboundReceive.getReplyReceiver() != null)
               {
                  // Note: This must match the way that
                  // AeInboundReceiveJournalEntry#dispatchToProcess recovers the
                  // reply id. In other words, if you change the reply id to be
                  // something other than the journal id, then you must update
                  // AeInboundReceiveJournalEntry#dispatchToProcess.
                  long replyId = found.getJournalId();
                  if (replyId == IAeProcessManager.NULL_JOURNAL_ID)
                  {
                     replyId = getEngine().getProcessManager().getNextJournalId();
                  }
                  // keep a reference to the reply id in the inbound rec. since it will be needed for the process's AeOpenMessageActivityInfo
                  // specifically, in the AeBusinessProcess::queueMessageReceiver(...)
                  aInboundReceive.setReplyId(replyId);
                  if (aInboundReceive.getReplyReceiver().getDurableReplyInfo() == null )
                  {
                     addNonDurableReply( new AeReply(found.getProcessId(), replyId, aInboundReceive.getReplyReceiver()), null);
                  }
               }
            }
            return found;
         }
      }
   }

   /**
    * Consumes the inbound receive to the matched message receiver.
    * @param aInboundReceive
    * @param aMatchedReceiver
    * @param aAckCallback
    * @throws AeBusinessProcessException
    */
   protected void consumeInboundReceive(AeInboundReceive aInboundReceive, AeMessageReceiver aMatchedReceiver, IAeMessageAcknowledgeCallback aAckCallback) throws AeBusinessProcessException
   {
      long processId = aMatchedReceiver.getProcessId();
      int locationId = aMatchedReceiver.getMessageReceiverPathId();

      // Consume by:
      // 1. Remove Message Receiver from queue
      // 2. Remove alarm
      // 3. Journal inbound receive and assign jounal id.
      // 4. If there is a callback, the do the call back.

      if (!aMatchedReceiver.isConcurrent())
      {
         int groupId = aMatchedReceiver.getGroupId();

         // 1. Remove Message Receivers from queue (all in the same group)
         removeMessageReceiversInGroup(processId, groupId, locationId);
         // 2. Remove alarms
         removeAlarmsInGroup(processId, groupId);
      }

      IAeMessageData messageData = aInboundReceive.getMessageData();
      if (messageData != null && messageData.hasAttachments())
      {
         // 3. Associate inbound attachments with process.
         getEngine().getAttachmentManager().associateProcess(messageData.getAttachmentContainer(), processId);
      }

      // 4. Journal inbound receive and assign jounal id.
      long journalId = getEngine().getProcessManager().journalInboundReceive(processId, locationId, aInboundReceive);
      aMatchedReceiver.setJournalId(journalId);

      // 5. callback
      if (aAckCallback != null)
      {
         try
         {
            aAckCallback.onAcknowledge(null);
         }
         catch(Throwable t)
         {
            throw new AeBusinessProcessException(t.getMessage(), t);
         }
      }
   }

   /**
    * Finds a matching queued message receiver for the given inbound receive. If matching
    * receiver is found, then the message is journaled and consumed. The journal id of the
    * inbound receive is assigned to the returned AeMessageReceiver. The consumption of the
    * message should also acknowledge that the message has been journaled and delivered via
    * optional durable invoke callback interface.
    *
    * <br/>
    * Note that the caller is responsible for any necessary synchronization.  This
    * method assumes that its work will always be done atomically (at least within
    * the same VM).
    *
    * @param aInboundReceive An inbound receive.
    * @param aAckCallback durable invoke message acknowledge callback.
    * @return A matching queued receive, or null if not found.
    */
   protected AeMessageReceiver findMatchForInboundReceive(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback) throws AeBusinessProcessException
   {
      AeMessageReceiver found = null;

      // Find an exact match first
      if (aInboundReceive.isCorrelated())
      {
         for (Iterator iter = getMessageReceivers().iterator(); found == null && iter.hasNext();)
         {
            AeMessageReceiver queuedReceive = (AeMessageReceiver) iter.next();
            if (queuedReceive.correlatesTo(aInboundReceive))
            {
               found = queuedReceive;
            }
         }
      }

      // Find partial match among uncorrelated message receivers
      if (found == null)
      {
         for (Iterator iter = getMessageReceivers().iterator(); found == null && iter.hasNext();)
         {
            AeMessageReceiver queuedReceive = (AeMessageReceiver) iter.next();

            if (!queuedReceive.isCorrelated() && queuedReceive.matches(aInboundReceive))
            {
               found = queuedReceive;
            }
         }
      }

      // if found, then consume it and acknowledge callback.
      if (found != null)
      {
         consumeInboundReceive(aInboundReceive, found, aAckCallback);
      }
      return found;
   }

   /**
    * Removes a previously queued unmatched receive. If the unmatched receive is found
    * then we'll check to see if it also had a reply waiting. If so, this reply
    * is notified with a fault.
    * @param aQueueId Id that was assigned to the previously unmatched receive
    */
   protected void removeUnmatchedReceive(String aQueueId)
   {
      // search the unmatched receive collection given aQueueId.
      AeUnmatchedReceive found = null;
      synchronized(getUnmatchedReceives())
      {
         for (Iterator iter = getUnmatchedReceives().iterator(); found == null && iter.hasNext();)
         {
            AeUnmatchedReceive queuedUnmatchedReceive = (AeUnmatchedReceive) iter.next();
            if (aQueueId.equals(queuedUnmatchedReceive.getQueueId()))
            {
               iter.remove();
               found = queuedUnmatchedReceive;
            }
         }
      }
      if (found != null)
      {
         // reply with correlation violation fault.
         IAeFault fault = AeFaultFactory.getFactory(found.getInboundReceive().getProcessPlan().getProcessDef().getNamespace()).getUnmatchedRequest();

         // Note: Business process properties are not used here because fault is
         // correlation violation - so no interaction with business process took place
         replyFault(found.getInboundReceive(), found.getAckCallback(), null, fault, null);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#removeMessageReceiver(long, int)
    */
   public boolean removeMessageReceiver(long aProcessId, int aMessageReceiverPathId) throws AeBusinessProcessException
   {
      synchronized(getMessageReceivers())
      {
         for(Iterator iter = getMessageReceivers().iterator(); iter.hasNext(); )
         {
            AeMessageReceiver entry = (AeMessageReceiver)iter.next();
            if (aProcessId == entry.getProcessId() && aMessageReceiverPathId == entry.getMessageReceiverPathId())
            {
               iter.remove();
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Removes all message receivers in the group.
    *
    * @param aProcessId
    * @param aGroupId
    * @param aLocationPathId
    * @throws AeBusinessProcessException
    */
   protected int removeMessageReceiversInGroup(long aProcessId, int aGroupId, int aLocationPathId)
         throws AeBusinessProcessException
   {
      int count = 0;
      synchronized(getMessageReceivers())
      {
         for(Iterator iter = getMessageReceivers().iterator(); iter.hasNext(); )
         {
            AeMessageReceiver entry = (AeMessageReceiver)iter.next();
            if (aProcessId == entry.getProcessId() && aGroupId == entry.getGroupId())
            {
               iter.remove();
               count++;
            }
         }
      }

      // For backwards compatibility, try to remove the specific message receiver if removing the
      // group didn't turn up any items.
      if (count == 0 && aLocationPathId != -1)
      {
         if (removeMessageReceiver(aProcessId, aLocationPathId))
         {
            count++;
         }
      }

      return count;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#removeReply(org.activebpel.rt.bpel.impl.queue.AeReply)
    */
   public AeReply removeReply(AeReply aReplyQueueObject)
   {
      AeReply reply  = (AeReply) getReplies().remove( new Long(aReplyQueueObject.getReplyId()) );
      return reply;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#sendReply(org.activebpel.rt.bpel.impl.queue.AeReply, org.activebpel.rt.message.IAeMessageData, org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public void sendReply(AeReply aReplyObject, IAeMessageData aData, IAeFault aFault, Map aProcessProperties ) throws AeBusinessProcessException
   {
      if ((aData != null) && (aFault != null))
      {
         throw new IllegalArgumentException(AeMessages.getString("AeBaseQueueManager.ERROR_AMBIGUOUS_REPLY")); //$NON-NLS-1$
      }

      // The IAeReplyReceiver referenced by the reply object is  either a durable reply receiver
      // (created by the process), or null. In the later case, the (non durable) reply receiver
      // is found in the queue manager's AeReply list via AeQueueManagerReplyReceiver delegate.

      IAeReplyReceiver replyReceiver = null;
      if (aReplyObject.getReplyReceiver() != null)
      {
         // if there is a reply receiver, then use it (applicable to durable replies; created at the process level)
         replyReceiver = aReplyObject.getReplyReceiver();
      }
      else
      {
         // create delegate which looks up the internal collection.
         replyReceiver = new AeQueueManagerReplyReceiver( getEngine().getQueueManager(), aReplyObject);
      }

      if (aFault != null)
      {
         replyReceiver.onFault(aFault, aProcessProperties );
      }
      else
      {
         replyReceiver.onMessage(aData, aProcessProperties);
      }
   }

   /**
    * Getter for the unmatched receives collection
    */
   protected List getUnmatchedReceives()
   {
      return mUnmatchedReceives;
   }

   /**
    * Getter for the message receivers collection
    */
   protected Collection getMessageReceivers()
   {
      return mMessageReceivers;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#getMessageReceivers(org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter)
    */
   public AeMessageReceiverListResult getMessageReceivers(AeMessageReceiverFilter aFilter)
         throws AeBusinessProcessException
   {
      AeMessageReceiverFilter filter = aFilter == null ? AeMessageReceiverFilter.NULL_FILTER : aFilter;
      synchronized( getMessageReceivers() )
      {
         return AeMessageReceiverFilterManager.filter( filter, getMessageReceivers() );
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#addNonDurableReply(org.activebpel.rt.bpel.impl.queue.AeReply, org.activebpel.rt.bpel.impl.queue.AeMessageReceiver)
    */
   public void addNonDurableReply(AeReply aReply, AeMessageReceiver aMessageReceiver) throws AeConflictingRequestException
   {
      // This method is normally called when an inbound message is consumed such as the engine's create process with message
      // and queue manager's matchInboundReceive method.

      // Durable replies should *not* be added to this (in memory) list since durable replies are handled
      // at the business process layer via open message activity info.
      // Since only non-durable reply should be here, if the reply is durable, thru IllegalArg exception (as a runtime check)
      if (aReply.getReplyReceiver().getDurableReplyInfo() != null)
      {
         throw new IllegalArgumentException(AeMessages.getString("AeBaseQueueManager.NON_DURABLE_REPLY_EXPECTED")); //$NON-NLS-1$
      }
      // non-durable replies are maintained in the queue manager.
      synchronized(getReplies())
      {
         getReplies().put( new Long(aReply.getReplyId()), aReply);
      }
   }

   /**
    * Getter for the replies collection set.
    */
   protected Map getReplies()
   {
      return mReplies;
   }

   /**
    * Returns <code>int</code> value from configuration <code>Map</code>.
    */
   protected static int getConfigInt(Map aConfig, String aKey, int aDefaultValue)
   {
      String value = (String) aConfig.get(aKey);
      if (!AeUtil.isNullOrEmpty(value))
      {
         try
         {
            return Integer.parseInt(value);
         }
         catch (NumberFormatException e)
         {
            AeException.logError(e, AeMessages.format("AeBaseQueueManager.ERROR_BAD_CONFIG_KEY_INT", new Object[] { value, aKey })); //$NON-NLS-1$
         }
      }

      return aDefaultValue;
   }

   /**
    * Returns maximum number of unmatched receives.
    */
   protected int getMaxUnmatchedReceivesCount()
   {
      return mMaxUnmatchedReceivesCount;
   }

   /**
    * Sets configuration.
    */
   protected void setConfig(Map aConfig)
   {
      setMaxUnmatchedReceivesCount(getConfigInt(aConfig, CONFIG_UNMATCHED_RECEIVES_COUNT, DEFAULT_UNMATCHED_RECEIVES_COUNT));
   }

   /**
    * Sets maximum number of unmatched receives.
    */
   protected void setMaxUnmatchedReceivesCount(int aMaxUnmatchedReceivesCount)
   {
      if (aMaxUnmatchedReceivesCount > 0)
      {
         mMaxUnmatchedReceivesCount = aMaxUnmatchedReceivesCount;
      }
      else
      {
         // Specifying 0 or less means no limit.
         mMaxUnmatchedReceivesCount = Integer.MAX_VALUE;
      }
   }

   /**
    * Returns true if the manager has been started. This is to avoid scheduling
    * an alarm while the manager is stopped - something that is possible in some
    * conditions during a shutdown.
    */
   protected boolean isStarted()
   {
      return mStarted;
   }

   /**
    * Used to actually perform the scheduling of the alarm. Derived classes should
    * implement this to schedule with their timer implementation.
    * @param aListener the listener to be scheduled
    * @param aDeadline the date this alarm should execute
    * @return Timer object created by the scheduler
    */
   protected abstract Timer schedule(TimerListener aListener, Date aDeadline);

   /**
    * Creates the alarm listener which encapsulates the alarm work to be performed.
    * Derived classes should implement this such that the work is performed in a
    * separate thread.
    * @param aAlarm The alarm definition
    */
   protected abstract TimerListener createAlarmListener(AeAlarm aAlarm);

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#scheduleAlarm(long, int, int, int,java.util.Date)
    */
   public void scheduleAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId, Date aDeadline)
         throws AeBusinessProcessException
   {
      AeAlarm alarm = new AeAlarm(aProcessId, aLocationPathId, aGroupId, aAlarmId,  aDeadline);

      // By synchronizing on the Hashtable, the entry can be added before a fired alarm
      // goes to remove it.  This will avoid the case where the alarm manager immediately
      // fires the scheduled alarm prior to it being added to the table.
      synchronized (mLookup)
      {
         // Make sure we don't double-schedule the alarm.
         if (! mLookup.containsKey(alarm))
         {
            Timer timer = schedule(createAlarmListener(alarm), aDeadline);
            if (timer != null)
               mLookup.put(alarm, timer);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#removeAlarmForDispatch(long, int, int, int)
    */
   public long removeAlarmForDispatch(long aProcessId, int aGroupId, int aLocationPathId, int aAlarmId)
         throws AeBusinessProcessException
   {
      // Synchronize on the message receiver queue - the 'find match for inbound receive' will also
      // synchronize on the message receiver queue.  This eliminates a race condition where
      // an alarm fires at the same instant as an inbound receive is handled.
      synchronized (getMessageReceivers())
      {
         // Remove and unschedule the alarm.
         if (internalRemoveAlarm(aProcessId, aLocationPathId, aAlarmId))
         {
            // Remove any message receivers in this group.
            removeMessageReceiversInGroup(aProcessId, aGroupId, aLocationPathId);
            // Pretend to journal the alarm in the process journal.
            return getNextJournalId();
         }
         else
         {
            return IAeProcessManager.NULL_JOURNAL_ID;
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#removeAlarm(long, int, int)
    */
   public boolean removeAlarm(long aProcessId, int aLocationPathId, int aAlarmId) throws AeBusinessProcessException
   {
      return internalRemoveAlarm(aProcessId, aLocationPathId, aAlarmId);
   }

   /**
    * Removes all of the alarms in the group.
    *
    * @param aProcessId
    * @param aGroupId
    * @throws AeBusinessProcessException
    */
   protected int removeAlarmsInGroup(long aProcessId, int aGroupId) throws AeBusinessProcessException
   {
      int count = 0;
      synchronized (mLookup)
      {
         for (Iterator iter = mLookup.entrySet().iterator(); iter.hasNext(); )
         {
            Map.Entry entry = (Map.Entry) iter.next();
            AeAlarm alarm = (AeAlarm) entry.getKey();
            if (alarm.getProcessId() == aProcessId && alarm.getGroupId() == aGroupId)
            {
               count++;
               Timer timer = (Timer) entry.getValue();
               timer.cancel();
               iter.remove();
            }
         }
      }
      return count;
   }

   /**
    * This method removes the alarm from its internal representation.  In the case of the
    * in-memory alarm manager, this means removing it from the cache.  Once removed, this
    * method returns the Timer object that will be used to cancel the alarm from the
    * timer service.
    *
    * @param aProcessId The process id of the alarm.
    * @param aLocationPathId The location path of the alarm.
    * @param aAlarmId alarm id.
    * @return True if the alarm was successfully removed.
    * todo should this get refactored to just be part of removeAlarm
    */
   protected boolean internalRemoveAlarm(long aProcessId, int aLocationPathId, int aAlarmId) throws AeBusinessProcessException
   {
      Timer timer = null;
      synchronized (mLookup)
      {
         timer = (Timer)mLookup.remove(new AeAlarm(aProcessId, aLocationPathId, aAlarmId));
      }

      if (timer == null)
         return false;
      else
      {
         try
         {
            // Cancel the timer in case it is executing, then return true indicating it has been removed
            timer.cancel();
         }
         catch (NullPointerException ignore)
         {
            // This should never happen, but we have observed the WebSphere implementation
            // throwing NullPointerException from within its Timer implementation.
         }
         catch (Throwable t)
         {
            // This should also never happen, but if it does, don't let it keep
            // us from releasing the alarm reference. We'll report this,
            // because we'll want to know about new failure scenarios.
            AeException.logError(t);
         }

         return true;
      }

   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#start()
    */
   public void start() throws Exception
   {
      super.start();
      mStarted = true;
      doInitialAlarmLoad();
   }

   /**
    * Called when the manager starts to do the initial loadup of saved alarms.  These saved
    * alarms were scheduled prior to stopping the manager.  Now that it's started up again,
    * the alarms must be loaded and scheduled.
    */
   protected void doInitialAlarmLoad() throws AeBusinessProcessException
   {
      if(mSavedLookup != null)
      {
         for(Iterator iter = mSavedLookup.values().iterator(); iter.hasNext(); )
         {
            AeAlarm alarm = (AeAlarm)iter.next();
            scheduleAlarm(alarm.getProcessId(), alarm.getPathId(), alarm.getGroupId(), alarm.getAlarmId(), alarm.getDeadline());
         }
         mSavedLookup = null;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#stop()
    */
   public void stop()
   {
      super.stop();

      mStarted = false;

      synchronized (mLookup)
      {
         for (Iterator iter = mLookup.values().iterator(); iter.hasNext();)
            ((Timer)iter.next()).cancel();

         saveLookup();
         mLookup.clear();
      }
   }

   /**
    * Saves lookup for in-memory start and stops.
    */
   protected void saveLookup()
   {
      mSavedLookup = new HashMap(mLookup);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#destroy()
    */
   public void destroy()
   {
      stop();

      super.destroy();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#getAlarms(org.activebpel.rt.bpel.impl.list.AeAlarmFilter)
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter) throws AeBusinessProcessException
   {
      AeAlarmFilter filter = aFilter == null ? AeAlarmFilter.NULL_FILTER : aFilter;
      synchronized( mLookup )
      {
         // The keyset is a collection of AeAlarm objects
         ArrayList list = new ArrayList(mLookup.keySet());
         return AeAlarmFilterManager.filter(getEngine(), filter, list);
      }
   }

   /**
    * Comparator that brings the correlated message receivers with the most
    * properties to the top so we can match against those first.
    */
   private static class AeMessageReceiverComparator implements Comparator
   {
      public int compare(Object aO1, Object aO2)
      {
         AeMessageReceiver one = (AeMessageReceiver) aO1;
         AeMessageReceiver two = (AeMessageReceiver) aO2;

         int twoSize = two.isCorrelated() ? two.getCorrelation().size() : 0;
         int oneSize = one.isCorrelated() ? one.getCorrelation().size() : 0;

         int result = twoSize - oneSize;

         if (result == 0)
         {
            // if they have the same number of correlations, then compare using
            // their hash values
            result = two.hashCode() - one.hashCode();
         }

         return result;
      }
   }
}
