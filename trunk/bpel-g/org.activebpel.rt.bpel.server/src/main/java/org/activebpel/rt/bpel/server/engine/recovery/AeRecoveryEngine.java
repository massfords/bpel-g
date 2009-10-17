// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/AeRecoveryEngine.java,v 1.23 2008/04/03 21:55:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeAlarmReceiver;
import org.activebpel.rt.bpel.impl.IAeAttachmentManager;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeEnginePartnerLinkStrategy;
import org.activebpel.rt.bpel.impl.IAeLockManager;
import org.activebpel.rt.bpel.impl.IAeManager;
import org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeAbstractServerEngine;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeEngineFailureJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeInvokeTransmittedJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeSentReplyJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.recovered.AeRecoveredRemoveAlarmItem;
import org.activebpel.rt.bpel.server.engine.recovery.recovered.AeRecoveredRemoveReceiverItem;
import org.activebpel.rt.bpel.server.engine.recovery.recovered.AeRecoveredScheduleAlarmItem;
import org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem;
import org.activebpel.rt.util.AeLongSet;
import org.activebpel.rt.util.AeUtil;

/**
 * Implements a business process engine that recovers the state of a process
 * from a list of
 * {@link org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry}
 * instances.
 */
public class AeRecoveryEngine extends AeAbstractServerEngine implements IAeRecoveryEngine
{
   private int mEngineId;

   /**
    * Constructs a recovery engine.
    *
    * @param aEngineConfiguration
    * @param aRecoveryQueueManager
    * @param aRecoveryProcessManager
    * @param aRecoveryLockManager
    * @param aRecoveryAttachmentManager
    * @param aPartnerLinkStrategy
    * @param aTransmissionTracker
    * @param aCustomManagersMap
    * @param aEngineId
    */
   public AeRecoveryEngine(
      IAeEngineConfiguration aEngineConfiguration,
      IAeRecoveryQueueManager aRecoveryQueueManager,
      IAeRecoveryProcessManager aRecoveryProcessManager,
      IAeLockManager aRecoveryLockManager,
      IAeAttachmentManager aRecoveryAttachmentManager,
      IAeEnginePartnerLinkStrategy aPartnerLinkStrategy,
      IAeRecoveryCoordinationManager aCoordinationManager,
      IAeTransmissionTracker aTransmissionTracker,
      Map aCustomManagersMap,
      int aEngineId)
   {
      super(aEngineConfiguration, aRecoveryQueueManager, aRecoveryProcessManager, aRecoveryLockManager, aRecoveryAttachmentManager);

      setPartnerLinkStrategy(aPartnerLinkStrategy);
      setCoordinationManager(aCoordinationManager);
      setTransmissionTracker(aTransmissionTracker);
      Map managersMap = createRecoveryVersionsOfCustomManagers(aCustomManagersMap);
      setCustomManagers(managersMap);
      // Since recovery engine extends BusiProcEngine, engine id will always be 1.
      // We need to get the engine id from the underlying engine.      
      mEngineId = aEngineId;
   }

   /**
    * Walks the map of custom managers and creates recovery versions of whatever
    * managers provide adapters for {@link IAeRecoveryAwareManager}.
    * @param aCustomManagersMap
    */
   protected Map createRecoveryVersionsOfCustomManagers(Map aCustomManagersMap)
   {
      Map managersMap = new HashMap(aCustomManagersMap);
      for (Iterator it = managersMap.entrySet().iterator(); it.hasNext();)
      {
         Map.Entry entry = (Map.Entry) it.next();
         
         IAeManager manager = (IAeManager) entry.getValue();
         IAeRecoveryAwareManager recoveryAwareManager = (IAeRecoveryAwareManager) manager.getAdapter(IAeRecoveryAwareManager.class);
         if (recoveryAwareManager != null)
         {
            entry.setValue(recoveryAwareManager);
         }
      }
      return managersMap;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal#getEngineId()
    */
   public int getEngineId()
   {
      return mEngineId;
   }   
   
   /**
    * Adds recovered items to the given list that will remove queued requests
    * that are now stale (meaning that the corresponding activities are no
    * longer executing in the process).
    *
    * @param aRecoveredItems
    * @param aProcess
    * @param aStaleLocationIds
    */
   protected void addStaleRequestRemovalItems(List aRecoveredItems, IAeBusinessProcess aProcess, Set aStaleLocationIds)
   {
      long processId = aProcess.getProcessId();

      for (Iterator i = aStaleLocationIds.iterator(); i.hasNext(); )
      {
         int locationId = ((Number) i.next()).intValue();
         IAeBpelObject aImpl = aProcess.findBpelObject(locationId);
         
         if (aImpl instanceof IAeAlarmReceiver)
         {
            IAeAlarmReceiver alarmRec = (IAeAlarmReceiver) aImpl;
            IAeRecoveredItem removeAlarmItem = new AeRecoveredRemoveAlarmItem(processId, locationId, alarmRec.getAlarmId());
            aRecoveredItems.add(removeAlarmItem);
         }
         else if (aImpl instanceof IAeMessageReceiverActivity)
         {
            IAeRecoveredItem removeReceiverItem = new AeRecoveredRemoveReceiverItem(processId, locationId);
            aRecoveredItems.add(removeReceiverItem);
         }
      }
   }

   /**
    * Dispatches the journal entries to the given process.
    *
    * @param aJournalEntries
    * @param aProcess
    * @throws AeBusinessProcessException
    */
   protected void dispatchJournalEntries(List aJournalEntries, IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      // Loop until we dispatch all of the journal entries or the process
      // completes.
      Iterator i = aJournalEntries.iterator();

      while (i.hasNext())
      {
         IAeJournalEntry entry = (IAeJournalEntry) i.next();
         
         // This code used to stop looping as soon as the process reached a final
         // state but it was changed to ask the journal entry if it should be
         // dispatched given the state of the process.
         // This change was necessary to support the recovery of the coordination
         // messages. Our recovery is process-centric but in the case we're really
         // recovering work for the coordination manager. As such, there could
         // be journal entries which should be dispatched despite the process
         // being in a final state.
         if (entry.canDispatch(aProcess.getProcessState()))
         {
            entry.dispatchToProcess(aProcess);
         }
         else
         {
            // put the entry back into the iterator
            i = AeUtil.join(entry, i);
            break;
         }
      }

      // If there are still journal entries to consume, then the process
      // probably faulted. Count the number of remaining entries and emit a
      // warning.
      if (i.hasNext())
      {
         int remaining = 0;

         while (i.hasNext())
         {
            IAeJournalEntry entry = (IAeJournalEntry) i.next();

            if (entry instanceof AeEngineFailureJournalEntry)
            {
               // This doesn't count as an entry that the process would consume.
            }
            else if (entry instanceof AeSentReplyJournalEntry)
            {
               // This doesn't count as an entry that the process would consume.
            }
            else
            {
               ++remaining;
            }
         }

         if (remaining > 0)
         {
            AeException.logWarning(AeMessages.format("AeRecoveryEngine.WARNING_ENTRIES_REMAINING", //$NON-NLS-1$
                                                     new Object[] { new Long(aProcess.getProcessId()), new Integer(remaining) }));
         }
      }
   }

   /**
    * Returns the location ids for activities that are currently executing in
    * the given process.
    */
   protected AeLongSet getExecutingLocationIds(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      AeQueuedExecutingLocationIdsCollector collector = new AeQueuedExecutingLocationIdsCollector();
      return collector.getExecutingLocationIds(aProcess);
   }

   /**
    * Returns the process manager to use during recovery.
    * @see org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveryEngine#getRecoveryProcessManager()
    */
   public IAeRecoveryProcessManager getRecoveryProcessManager()
   {
      return (IAeRecoveryProcessManager) getProcessManager();
   }

   /**
    * Returns the queue manager to use during recovery.
    */
   protected IAeRecoveryQueueManager getRecoveryQueueManager()
   {
      return (IAeRecoveryQueueManager) getQueueManager();
   }

   /**
    * Returns location ids of requests that are currently queued but should no
    * longer be queued, because the corresponding activity is not executing.
    */
   protected Set getStaleLocationIds(Set aQueuedLocationIds, Set aExecutingLocationIds)
   {
      Set staleLocationIds = new HashSet(aQueuedLocationIds);
      staleLocationIds.removeAll(aExecutingLocationIds);

      return staleLocationIds;
   }

   /**
    * Queues the recovered items to the given engine.
    *
    * @param aRecoveredItems
    * @param aProcess
    */
   protected void queueRecoveredItems(List aRecoveredItems, IAeBusinessProcess aProcess)
   {
      for (Iterator i = aRecoveredItems.iterator(); i.hasNext(); )
      {
         IAeRecoveredItem recoveredItem = (IAeRecoveredItem) i.next();

         try
         {
            recoveredItem.queueItem(aProcess.getEngine());
         }
         catch (AeBusinessProcessException e)
         {
            // Report the name of the recovered item's class.
            String name = recoveredItem.getClass().getName();

            // Report just the name without the package.
            int n = name.lastIndexOf('.');
            if (n > 0)
            {
               name = name.substring(n + 1);
            }

            Object[] params = new Object[] { name, new Long(aProcess.getProcessId()), new Integer(recoveredItem.getLocationId()) };
            AeException.logError(e, AeMessages.format("AeRecoveryEngine.ERROR_QUEUE_RECOVERED_ITEM", params)); //$NON-NLS-1$
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveryEngine#recover(org.activebpel.rt.bpel.IAeBusinessProcess, java.util.List, boolean)
    */
   public List recover(IAeBusinessProcess aProcess, List aJournalEntries, boolean aQueueRecoveredItems) throws AeBusinessProcessException
   {
      // Extract sent reply objects.
      List sentReplies = getSentReplies(aJournalEntries);

      // Extract invoke transmitted journal entries.
      List invokeTransmittedEntries = getInvokeTransmittedEntries(aJournalEntries);

      // Recover the process state and generate a complete list of recovered
      // alarm and queue manager requests.
      List recoveredItems = recover(aProcess, aJournalEntries, sentReplies, invokeTransmittedEntries);

      // Get location ids for requests that are already queued.
      AeQueuedLocationIdsCollector collector = new AeQueuedLocationIdsCollector();
      collector.getQueuedLocationIds(aProcess, aJournalEntries);

      // Remove recovered items that are already queued.
      removeQueuedItems(recoveredItems, collector);

      // Get location ids for executing activities.
      AeLongSet executingLocationIds = getExecutingLocationIds(aProcess);

      // Get location ids for queued requests that should be removed.
      Set staleLocationIds = getStaleLocationIds(collector.getQueuedLocationIds(), executingLocationIds);

      // Add recovered items to remove stale requests.
      addStaleRequestRemovalItems(recoveredItems, aProcess, staleLocationIds);

      // Queue the recovered items if requested.
      if (aQueueRecoveredItems)
      {
         queueRecoveredItems(recoveredItems, aProcess);
      }

      return recoveredItems;
   }
   
   /**
    * Recovers the state of the specified process from the given list of {@link
    * org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry}
    * instances.
    *
    * @param aProcess
    * @param aJournalEntries
    * @param aSentReplies
    * @param aInvokeTransmittedEntries
    * @return A list of {@link org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem} instances.
    * @throws AeBusinessProcessException
    */
   protected synchronized List recover(IAeBusinessProcess aProcess, List aJournalEntries, List aSentReplies, List aInvokeTransmittedEntries) throws AeBusinessProcessException
   {
      // The whole method is synchronized, because we can only recover one
      // process at a time.
      setRecoveryProcess(aProcess);

      // Create a set to hold the recovered alarm and queue manager requests.
      IAeRecoveredItemsSet recoveredItemsSet = new AeRecoveredItemsSet();
      
      // Prepare the queue manager to match recovered send reply requests
      // against replies that were already sent by the process.
      getRecoveryQueueManager().setSentReplies(aSentReplies);

      // Prepare the queue manager to match invoke location ids to transmission
      // ids.
      getRecoveryQueueManager().setInvokeTransmittedEntries(aInvokeTransmittedEntries);
      
      setRecoveryDataOnManagers(recoveredItemsSet, aInvokeTransmittedEntries);

      // Switch the process from its current engine to this recovery engine.
      IAeBusinessProcessEngineInternal oldEngine = aProcess.getEngine();
      aProcess.setEngine(this);
      try
      {
         // Restore queued items so we can match correlated requests
         restoreQueuedItems(aProcess);

         // Dispatch the journal entries to the process.
         dispatchJournalEntries(aJournalEntries, aProcess);
      }
      finally
      {
         // Restore the original engine for the process.
         aProcess.setEngine(oldEngine);
      }

      // Return the list of recovered alarm and queue manager requests.
      return recoveredItemsSet.getRecoveredItems();
   }

   /**
    * Sets all of the recovery data on the recovery aware managers
    * @param aRecoveredItemsSet
    * @param aInvokeTransmittedEntries
    */
   protected void setRecoveryDataOnManagers(IAeRecoveredItemsSet aRecoveredItemsSet, List aInvokeTransmittedEntries)
   {
      getRecoveryQueueManager().setRecoveredItemsSet(aRecoveredItemsSet);
      
      for(Iterator it=AeUtil.join(getCustomManagers().values().iterator(), getCoordinationManager()); it.hasNext();)
      {
         IAeManager manager = (IAeManager) it.next();
         if (manager instanceof IAeRecoveryAwareManager)
         {
            // fixme (MF-recovery) introduce some kind of context interface here
            IAeRecoveryAwareManager recoveryManager = (IAeRecoveryAwareManager) manager;
            recoveryManager.setRecoveredItemsSet(aRecoveredItemsSet);
            recoveryManager.setInvokeTransmittedEntries(aInvokeTransmittedEntries);
         }
      }
   }

   /**
    * Removes recovered items from the given list for requests that are already
    * queued.
    *
    * @param aRecoveredItems
    * @param aCollector
    */
   protected void removeQueuedItems(List aRecoveredItems, AeQueuedLocationIdsCollector aCollector)
   {
      AeLongSet queuedLocationIds = aCollector.getQueuedLocationIds();
      
      // loop over all of the recovered items and see which ones we should
      // remove because they've already been queued.
      for (Iterator i = aRecoveredItems.iterator(); i.hasNext(); )
      {
         IAeRecoveredItem recoveredItem = (IAeRecoveredItem) i.next();
         int locationId = recoveredItem.getLocationId();
         
         boolean alreadyQueued = false;
         
         if (recoveredItem.isRemoval())
         {
            // Skip over the removal items. 
            alreadyQueued = false;
         }
         else if (recoveredItem instanceof AeRecoveredScheduleAlarmItem)
         {
            // fixme (MF-Defect-3260) The real fix here is to add some journaling for alarms.
            // The lack of journaling (Defect 3260) means that we don't know if 
            // the alarm was really scheduled. This is why we're forced to 
            // requeue the alarms during recovery. The result of this requeuing
            // is needing to do some special checks like here as well as having
            // duration based times slip as the duration is essentially reset 
            // during recovery.

            AeRecoveredScheduleAlarmItem recoveredAlarm = (AeRecoveredScheduleAlarmItem) recoveredItem;
            alreadyQueued = aCollector.isQueued(locationId, recoveredAlarm.getAlarmId());
         }
         // Note that some recovered items (namely, invokes and replies) report
         // 0 for location id to indicate that they don't have a location id.
         else if ((locationId > 0) && queuedLocationIds.contains(locationId))
         {
            alreadyQueued = true;
         }
         
         if (alreadyQueued)
            i.remove();
      }
   }

   /**
    * Restores the executing items that are flagged as queued in the recovery queue manager
    * so we can match correlated requests
    *  
    * @param aProcess
    * @throws AeBusinessProcessException
    */
   protected void restoreQueuedItems(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      // Get location ids for executing items 
      Set executingLocationIds = getExecutingLocationIds(aProcess);
      for (Iterator it = executingLocationIds.iterator(); it.hasNext();)
      {
         int locationId = ((Long) it.next()).intValue();
         IAeBpelObject item = aProcess.findBpelObject(locationId);
         
         // re-queue any previously queued message or alarm receivers
         if (item instanceof IAeMessageReceiverActivity)
         {
            IAeMessageReceiverActivity receiver = (IAeMessageReceiverActivity) item;
            if (receiver.isQueued())
            {
               receiver.requeue();
            }
         }
         // we reset the state before re-queue to suppress a state transition warning that is 
         // entirely appropriate in the normal engine, but not during recovery
         else if (item instanceof IAeAlarmReceiver)
         {
            IAeAlarmReceiver receiver = (IAeAlarmReceiver) item;
            if (receiver.isQueued())
            {  
               item.setState(AeBpelState.INACTIVE);
               aProcess.queueObjectToExecute(item);
            }
         }
      }
   }

   /**
    * Returns sent reply objects from the sent reply journal entries in the
    * given list of journal entries.
    */
   protected List getSentReplies(List aJournalEntries) throws AeBusinessProcessException
   {
      List sentReplies = new LinkedList();

      for (Iterator i = aJournalEntries.iterator(); i.hasNext(); )
      {
         IAeJournalEntry item = (IAeJournalEntry) i.next();

         if (item instanceof AeSentReplyJournalEntry)
         {
            // Unwrap the actual reply object.
            AeReply sentReply = ((AeSentReplyJournalEntry) item).getReply();
            sentReplies.add(sentReply);
         }
      }

      return sentReplies;
   }

   /**
    * Sets the process to recover on all the managers.
    *
    * @param aProcess
    */
   protected void setRecoveryProcess(IAeBusinessProcess aProcess)
   {
      getRecoveryProcessManager().setRecoveryProcess(aProcess);
      getRecoveryQueueManager().setRecoveryProcess(aProcess);
   }
   
   /**
    * Returns invoke transmitted journal entries from the given list of journal
    * entries.
    */
   protected List getInvokeTransmittedEntries(List aJournalEntries) throws AeBusinessProcessException
   {
      List transmittedInvokeEntries = new LinkedList();

      for (Iterator i = aJournalEntries.iterator(); i.hasNext(); )
      {
         IAeJournalEntry entry = (IAeJournalEntry) i.next();

         if (entry instanceof AeInvokeTransmittedJournalEntry)
         {
            transmittedInvokeEntries.add(entry);
         }
      }

      return transmittedInvokeEntries;
   }
}
