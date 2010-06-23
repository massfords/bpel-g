// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeQueueStorage.java,v 1.18 2008/02/17 21:38:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeCorrelationCombinations;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionException;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * A delegating implementation of a queue storage.  This class delegates all of the database
 * calls to an instance of IAeQueueStorageProvider.  The purpose of this class is to encapsulate
 * storage 'logic' so that it can be shared across multiple storage implementations (such as SQL
 * and Tamino).
 */
public class AeQueueStorage extends AeAbstractStorage implements IAeQueueStorage
{
   /** A flag indicating whether to log hash collisions. */
   private boolean mLogCollisions;

   /** max number of combinations to produce when searching for join style correlations */
   private int mMaxCorrelationCombinations = AeEngineFactory.getEngineConfig().getMaxCorrelationCombinations();

   /**
    * Default constructor that takes the queue storage provider to use.
    *
    * @param aProvider
    */
   public AeQueueStorage(IAeQueueStorageProvider aProvider)
   {
      super(aProvider);
   }

   /**
    * Convenience method to get the storage provider cast to a queue storage provider.
    */
   protected IAeQueueStorageProvider getQueueStorageProvider()
   {
      return (IAeQueueStorageProvider) getProvider();
   }

   /**
    * Gets the max number of correlation combinations to produce when routing an inbound receive w/ correlation data to IMA's with join style correlations
    */
   protected int getMaxCorrelationCombinations()
   {
      return mMaxCorrelationCombinations;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeQueueStorage#findMatchingReceive(org.activebpel.rt.bpel.impl.queue.AeInboundReceive, org.activebpel.wsio.IAeMessageAcknowledgeCallback)
    */
   public AeMessageReceiver findMatchingReceive(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback)
         throws AeStorageException
   {
      // Logic:
      //  - Find a match (may be correlated or not)
      //  - If found:
      //     - Delete the receiver by group id
      //     - If deleted:
      //        - Delete alarms by group id
      //        - Journal match
      //        - acknowledge of message delivary via ack callback.
      //        - Return match
      //     - If not deleted:
      //        - Loop to "Find a match"
      //  - If not found:
      //     - Return null
      boolean done = false;
      AePersistedMessageReceiver found = null;

      while (!done)
      {
         // If the inbound receive is correlated, look for an exact match first.
         if (aInboundReceive.isCorrelated())
         {
            AeCorrelationCombinations combos = aInboundReceive.getProcessPlan().getProcessDef().getCorrelationProperties(aInboundReceive.getPartnerLinkOperationKey());

            // combos could be null if the correlationSets were all initiate="yes". In this case, none of the message
            // receivers would have been queued with a hash for correlated properties.
            if (combos != null)
            {
               AeCorrelationCombinations.AeCorrelatedProperties corrProps = combos.getPropertyCombinations(getMaxCorrelationCombinations());
               switch (corrProps.getStyle())
               {
                  case AeCorrelationCombinations.AeCorrelatedProperties.INITIATED:
                  case AeCorrelationCombinations.AeCorrelatedProperties.INITIATED_AND_JOIN:
                     found = findCorrelatedMatch(aInboundReceive, corrProps);
                     // stop searching regardless of whether we found a match since this strategy accounts for all possible combinations of correlationSets
                     done = true;
                     break;

                  case AeCorrelationCombinations.AeCorrelatedProperties.JOIN:
                  case AeCorrelationCombinations.AeCorrelatedProperties.INITIATED_AND_JOIN_OVER_MAX:
                     found = findCorrelatedMatch(aInboundReceive, corrProps);
                     break;

                  case AeCorrelationCombinations.AeCorrelatedProperties.JOIN_OVER_MAX:
                     break;
               }
            }
            else
            {
               found = findEngineCorrelatedMatch(aInboundReceive);
            }
         }

         // If it's not yet found, search for uncorrelated partial matches.
         if (found == null && !done)
         {
            // Get all message receivers that match the inbound receive.
            List receives = getMatchingReceives(aInboundReceive, null);

            // check for the correlated receives first
            if (aInboundReceive.isCorrelated())
            {
               for (Iterator iter = receives.iterator(); iter.hasNext() && found == null; )
               {
                  AePersistedMessageReceiver queuedReceive = (AePersistedMessageReceiver) iter.next();
                  if (queuedReceive.isCorrelated() && queuedReceive.correlatesTo(aInboundReceive))
                  {
                     // If the queued receive IS correlated, but it has 'duplicate receives'
                     // which prevent the correlation hash technique from working.
                     found = queuedReceive;
                  }
               }
            }

            // fallback and match uncorrelated if not previously found
            for (Iterator iter = receives.iterator(); iter.hasNext() && found == null; )
            {
               AePersistedMessageReceiver queuedReceive = (AePersistedMessageReceiver) iter.next();
               if (!queuedReceive.isCorrelated())
               {
                  found = queuedReceive;
               }
            }
         }

         // Didn't find a match?  Then we're done.
         if (found == null)
         {
            done = true;
         }
         // If we DID find a match, then we must 'consume' it for it to be official.
         else
         {
            // Consuming the message receiver will:
            //  1) delete it from the message receiver queue
            //  2) add it to the process journal
            //  3) return the journal id of the new journal entry
            long journalId = consumeMessageReceiver(found, aInboundReceive, aAckCallback);

            // If we fail to consume the message receiver, then we need to loop again to look
            // for another match.
            if (journalId != IAeProcessManager.NULL_JOURNAL_ID)
            {
               done = true;
            }
         }
      }

      return found;
   }

   /**
    * This method is called to 'consume' a matched receive.  A matched receive is consumed
    * if it can be transferred from the QueuedReceive table to the Journal table.  This is
    * done by deleting it from the QueuedReceive table and subsequently adding the inbound
    * receive to the journal.  Note that the message receiver is deleted from the queued
    * receive table by its group id.  At the same time, any Alarms that have been queued
    * with the same group id will be removed from the alarm queue table.
    *
    * @param aMessageReceiver
    * @param aInboundReceive
    * @param aAckCallback durable invoke message acknowledge callback.
    * @return the journal id if consumed, or <code>IAeProcessManager.NULL_JOURNAL_ID</code> if not consumed
    * @throws AeStorageException
    */
   protected long consumeMessageReceiver(AePersistedMessageReceiver aMessageReceiver,
         AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback)
         throws AeStorageException
   {
      beginTransaction();
      try
      {
         // Do the actual consume of the message.
         long rval = consumeMessageReceiverInternal(aMessageReceiver, aInboundReceive, aAckCallback);
         AeTransactionManager.getInstance().commit();
         return rval;
      }
      catch (AeTransactionException txe)
      {
         rollbackTransaction();
         throw new AeStorageException(txe);
      }
      catch (AeStorageException t)
      {
         rollbackTransaction();
         throw t;
      }
   }

   /**
    * Do the actual work of consuming the message receiver.
    * 
    * @param aMessageReceiver
    * @param aInboundReceive
    * @param aAckCallback
    * @throws AeStorageException
    */
   protected long consumeMessageReceiverInternal(AePersistedMessageReceiver aMessageReceiver,
         AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback)
         throws AeStorageException
   {
      // NOTE: this method may be called with-in an active transaction.
      IAeStorageConnection connection = getTxCommitControlDBConnection();

      // Steps to consume are:
      // 1) delete message receivers in group
      // 2) delete alarms in group
      // 3) journal the inbound receive

      long procId = aMessageReceiver.getProcessId();
      int groupId = aMessageReceiver.getGroupId();
      int locId = aMessageReceiver.getMessageReceiverPathId();
      long journalId = IAeProcessManager.NULL_JOURNAL_ID;
      try
      {
         // We consumed the message receiver only if we were the thread that successfully
         // deleted it.
         if (aMessageReceiver.isConcurrent())
         {
            associateAttachments(aInboundReceive, procId);

            journalId = getQueueStorageProvider().journalInboundReceive(procId, locId, aInboundReceive, connection);
            // do message ack callback.
            if (aAckCallback != null)
            {
               aAckCallback.onAcknowledge(null);
            }
         }
         else if (getQueueStorageProvider().removeReceiveObjectsInGroup(procId, groupId, locId, connection) > 0)
         {
            // Remove alarms in the group.
            getQueueStorageProvider().removeAlarmsInGroup(procId, groupId, connection);

            associateAttachments(aInboundReceive, procId);

            journalId = getQueueStorageProvider().journalInboundReceive(procId, locId, aInboundReceive, connection);
            // do message ack callback.
            if (aAckCallback != null)
            {
               aAckCallback.onAcknowledge(null);
            }
         }
         else
         {
            // Nothing to be done.
         }

         aMessageReceiver.setJournalId(journalId);
         return journalId;
      }
      catch(AeStorageException se)
      {
         throw se;
      }
      catch(Throwable t)
      {
         AeStorageException se = new AeStorageException(t);
         throw se;
      }
   }

   /**
    * Associates any attachments with the given process.
    *
    * @param aInboundReceive
    * @param aProcessId
    * @throws AeBusinessProcessException
    */
   protected void associateAttachments(AeInboundReceive aInboundReceive, long aProcessId) throws AeBusinessProcessException
   {
      IAeMessageData messageData = aInboundReceive.getMessageData();
      if (messageData != null && messageData.hasAttachments())
         AeEngineFactory.getEngine().getAttachmentManager().associateProcess(messageData.getAttachmentContainer(), aProcessId);
   }

   /**
    * Executes a search for each combination of property values until it finds a match.
    *
    * @param aInboundReceive
    * @param aCorrProps
    * @throws AeStorageException
    */
   protected AePersistedMessageReceiver findCorrelatedMatch(AeInboundReceive aInboundReceive, AeCorrelationCombinations.AeCorrelatedProperties aCorrProps) throws AeStorageException
   {
      AePersistedMessageReceiver found = null;
      List receives = null;

      for (Iterator it = aCorrProps.getCollection().iterator(); found == null && it.hasNext();)
      {
         Set propsForMap = new HashSet((Set) it.next());
         if (aInboundReceive.getContext().getWsAddressingHeaders().getConversationId() != null)
         {
            // add engine managed correlation property to the set
            propsForMap.add(IAePolicyConstants.CONVERSATION_ID_HEADER);
         }
         Map map = new HashMap(aInboundReceive.getCorrelation());
         map.keySet().retainAll(propsForMap);

         receives = getMatchingReceives(aInboundReceive, map);
         for (Iterator iter = receives.iterator(); iter.hasNext();)
         {
            AePersistedMessageReceiver queuedReceive = (AePersistedMessageReceiver) iter.next();
            if (queuedReceive.correlatesTo(aInboundReceive))
            {
               found = queuedReceive;
               break;
            }
         }
      }

      return found;
   }

   /**
    * Executes a search for an engine managed correlation match.
    *
    * @param aInboundReceive
    * @throws AeStorageException
    */
   protected AePersistedMessageReceiver findEngineCorrelatedMatch(AeInboundReceive aInboundReceive) throws AeStorageException
   {
      AePersistedMessageReceiver found = null;
      List receives = null;

      Set propsForMap = new HashSet();
      propsForMap.add(IAePolicyConstants.CONVERSATION_ID_HEADER);
      Map map = new HashMap(aInboundReceive.getCorrelation());
      map.keySet().retainAll(propsForMap);

      receives = getMatchingReceives(aInboundReceive, map);
      for (Iterator iter = receives.iterator(); iter.hasNext();)
      {
         AePersistedMessageReceiver queuedReceive = (AePersistedMessageReceiver) iter.next();
         if (queuedReceive.correlatesTo(aInboundReceive))
         {
            found = queuedReceive;
            break;
         }
      }
      return found;
   }



   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeQueueStorage#removeReceiveObject(long, int)
    */
   public AeMessageReceiver removeReceiveObject(long aProcessId, int aMessageReceiverPathId) throws AeStorageException
   {
      try
      {
         AePersistedMessageReceiver rval = getQueueStorageProvider().getReceiveObject(aProcessId, aMessageReceiverPathId);

         if (rval != null)
         {
            if (!getQueueStorageProvider().removeReceiveObjectById(rval.getQueuedReceiveId()))
            {
               // Null out the rval if we failed to delete the receive.
               rval = null;
            }
         }

         return rval;
      }
      catch (Exception e)
      {
         throw new AeStorageException(e);
      }
   }

   /**
    * Returns a list of receives that match the given inbound receive.  In
    * addition, the caller can specify whether to find correlated receives or
    * not.
    *
    * @param aInboundReceive The inbound receive to match.
    * @param aCorrelationMap The map that contains the correlated info
    * @return A list of matching receives.
    */
   protected List getMatchingReceives(AeInboundReceive aInboundReceive, Map aCorrelationMap) throws AeStorageException
   {
      int matchHash = AeStorageUtil.getReceiveMatchHash(aInboundReceive);
      int correlatesHash = 0;

      if (aCorrelationMap != null)
      {
         correlatesHash = AeStorageUtil.getReceiveCorrelatesHash(aCorrelationMap);
      }

      List list = getQueueStorageProvider().getReceives(matchHash, correlatesHash);

      // If we get more than 1 receive, we might want to log that (hash collision detected).
      // Only care about correlated collisions, since it's entirely possible to get uncorrelated
      // collisions.
      if (list.size() > 1 && aCorrelationMap != null && isLogCollisions())
      {
         getQueueStorageProvider().incrementHashCollisionCounter();
      }

      LinkedList rval = new LinkedList();
      for (Iterator iter = list.iterator(); iter.hasNext(); )
      {
         AePersistedMessageReceiver queuedReceive = (AePersistedMessageReceiver) iter.next();
         if (queuedReceive.matches(aInboundReceive))
         {
            rval.add(queuedReceive);
         }
      }

      return rval;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeQueueStorage#getQueuedMessageReceivers(org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter)
    */
   public AeMessageReceiverListResult getQueuedMessageReceivers(AeMessageReceiverFilter aFilter) throws AeStorageException
   {
      return getQueueStorageProvider().getQueuedMessageReceivers(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeQueueStorage#getAlarms()
    */
   public List getAlarms() throws AeStorageException
   {
      return getQueueStorageProvider().getAlarms();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeQueueStorage#storeAlarm(long, int, int, int, java.util.Date)
    */
   public void storeAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId, Date aDeadline)
         throws AeStorageException
   {
      getQueueStorageProvider().storeAlarm(aProcessId, aLocationPathId, aGroupId, aAlarmId, aDeadline);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeQueueStorage#removeAlarm(long, int, int)
    */
   public boolean removeAlarm(long aProcessId, int aLocationPathId, int aAlarmId) throws AeStorageException
   {
      IAeStorageConnection connection = getDBConnection();

      try
      {
         return removeAlarm(aProcessId, aLocationPathId, aAlarmId, connection);
      }
      finally
      {
         connection.close();
      }
   }

   /**
    * Removes an alarm from the storage.  This version will use the given connection to execute the SQL
    * statement.
    *
    * @param aProcessId
    * @param aLocationPathId
    * @param aAlarmId
    * @param aConnection
    * @throws AeStorageException
    */
   protected boolean removeAlarm(long aProcessId, int aLocationPathId, int aAlarmId, IAeStorageConnection aConnection) throws AeStorageException
   {
      return getQueueStorageProvider().removeAlarm(aProcessId, aLocationPathId, aAlarmId, aConnection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeQueueStorage#removeAlarmForDispatch(long, int, int, int)
    */
   public long removeAlarmForDispatch(long aProcessId, int aGroupId, int aLocationPathId, int aAlarmId) throws AeStorageException
   {
      IAeStorageConnection connection = getCommitControlDBConnection();
      long journalId = IAeProcessManager.NULL_JOURNAL_ID;
      try
      {
         // First, remove any message receivers in the group.
         getQueueStorageProvider().removeReceiveObjectsInGroup(aProcessId, aGroupId, aLocationPathId, connection);
         // Next, remove the alarm
         if (removeAlarm(aProcessId, aLocationPathId, aAlarmId, connection))
         {
            journalId = getQueueStorageProvider().journalAlarm(aProcessId, aGroupId, aLocationPathId, aAlarmId, connection);
            connection.commit();
         }
         else
         {
            connection.rollback();
         }
         return journalId;
      }
      catch(AeStorageException se)
      {
         connection.rollback();
         throw se;
      }
      catch(Throwable t)
      {
         connection.rollback();
         AeStorageException se = new AeStorageException(t);
         throw se;
      }
      finally
      {
         connection.close();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeQueueStorage#getAlarms(org.activebpel.rt.bpel.impl.list.AeAlarmFilter)
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter) throws AeStorageException
   {
      return getQueueStorageProvider().getAlarms(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeQueueStorage#storeReceiveObject(org.activebpel.rt.bpel.impl.queue.AeMessageReceiver)
    */
   public void storeReceiveObject(AeMessageReceiver aReceiveObject) throws AeStorageException
   {
      getQueueStorageProvider().storeReceiveObject(aReceiveObject);
   }

   /**
    * @return Returns the logCollisions.
    */
   protected boolean isLogCollisions()
   {
      return mLogCollisions;
   }

   /**
    * @param aLogCollisions The logCollisions to set.
    */
   public void setLogCollisions(boolean aLogCollisions)
   {
      mLogCollisions = aLogCollisions;
   }
}
