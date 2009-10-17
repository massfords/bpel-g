// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/AeQueuedLocationIdsCollector.java,v 1.2 2007/11/15 21:06:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeAlarm;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage;
import org.activebpel.rt.util.AeIntMap;
import org.activebpel.rt.util.AeLongMap;
import org.activebpel.rt.util.AeLongSet;
import org.activebpel.rt.util.AeUtil;

/**
 * Determines the location ids for activities that are currently queued in the
 * queue manager or that were queued when we first retrieved journal entries for
 * recovery.
 */
public class AeQueuedLocationIdsCollector
{
   /** The process being recovered. */
   private IAeBusinessProcess mProcess;
   /** The known journal entries that we are processing for recovery. */
   private List mKnownJournalEntries;
   /** The set of location ids for queued activities. */
   private AeLongSet mQueuedLocationIds;
   /** Maps location ids to the alarm object. Supports a one to many mapping. */
   private AeIntMap mAlarms = new AeIntMap();

   /**
    * Default constructor.
    */
   public AeQueuedLocationIdsCollector()
   {
   }

   /**
    * Adds location ids of activities that have been journaled for the given
    * process but not accounted for in the known journal entries. In other
    * words, these are location ids for requests that have been journaled since
    * we first retrieved journal entries for recovery.
    */
   protected void addNewlyJournaledLocationIds() throws AeBusinessProcessException
   {
      IAeProcessManager processManager = getProcess().getEngine().getProcessManager();

      if (processManager instanceof IAePersistentProcessManager)
      {
         IAeProcessStateStorage storage = ((IAePersistentProcessManager) processManager).getStorage();
         long processId = getProcess().getProcessId();
         AeLongMap locationIdsMap = storage.getJournalEntriesLocationIdsMap(processId);

         // Remove map entries corresponding to journal entries that we already
         // have in our list of journal entries.
         for (Iterator i = getKnownJournalEntries().iterator(); i.hasNext(); )
         {
            IAeJournalEntry entry = (IAeJournalEntry) i.next();

            locationIdsMap.remove(entry.getJournalId());
         }

         // Now add the location ids from the remaining members of the map to
         // the given set of location ids.
         getQueuedLocationIds().addAll(locationIdsMap.values());
      }
   }

   /**
    * Adds location ids of alarms that are queued for the process.
    */
   protected void addQueuedAlarmLocationIds() throws AeBusinessProcessException
   {
      AeAlarmFilter filter = new AeAlarmFilter();
      filter.setProcessId(getProcess().getProcessId());

      AeAlarmListResult list = getProcess().getEngine().getQueueManager().getAlarms(filter);
      AeAlarm[] alarms = list.getResults();

      for (int i = 0; i < alarms.length; ++i)
      {
         getQueuedLocationIds().add(alarms[i].getPathId());
         addAlarm(alarms[i]);
      }
   }
   
   /**
    * Adds the alarm to the alarm map
    * @param aAlarm
    */
   protected void addAlarm(AeAlarm aAlarm)
   {
      // Using a list here for legacy reasons. The old restore code was never
      // removing the old alarms. as a result, it's possible to have multiple
      // alarms mapped to a location id, although only one alarm will get
      // recovered.
      // Technically shouldn't need this legacy check but it's possible that 
      // users could do a hard shutdown prior to upgrading and have old journal
      // items left behind.
      List list = (List) getAlarms().get(aAlarm.getPathId());
      if (list == null)
      {
         list = new ArrayList();
         getAlarms().put(aAlarm.getPathId(), list);
      }
      list.add(aAlarm);
   }
   
   /**
    * Returns true if the alarm with the specified path and id is queued.
    * @param aPathId
    * @param aAlarmId
    */
   public boolean isQueued(int aPathId, int aAlarmId)
   {
      List list = (List) getAlarms().get(aPathId);
      if (AeUtil.notNullOrEmpty(list))
      {
         for (Iterator iter = list.iterator(); iter.hasNext();)
         {
            AeAlarm alarm = (AeAlarm) iter.next();
            if (alarm.getAlarmId() == aAlarmId)
               return true;
         }
      }
      return false;
   }

   /**
    * Adds location ids of message receivers that are queued for the process.
    */
   protected void addQueuedReceiverLocationIds() throws AeBusinessProcessException
   {
      AeMessageReceiverFilter filter = new AeMessageReceiverFilter();
      filter.setProcessId(getProcess().getProcessId());

      AeMessageReceiverListResult list = getProcess().getEngine().getQueueManager().getMessageReceivers(filter);
      if (!list.isEmpty())
      {
         AeMessageReceiver[] receivers = list.getResults();

         for (int i = 0; i < receivers.length; ++i)
         {
            getQueuedLocationIds().add(receivers[i].getMessageReceiverPathId());
         }
      }
   }

   /**
    * Returns the known journal entries that we are processing for recovery.
    */
   protected List getKnownJournalEntries()
   {
      return mKnownJournalEntries;
   }

   /**
    * Returns the process being recovered.
    */
   protected IAeBusinessProcess getProcess()
   {
      return mProcess;
   }

   /**
    * Returns the queued location ids set.
    */
   public AeLongSet getQueuedLocationIds()
   {
      return mQueuedLocationIds;
   }
   
   /**
    * Returns the location ids for activities that are or were queued in the
    * queue manager for the given process, excluding location ids corresponding
    * to the given journal entries.
    *
    * @param aProcess
    * @param aKnownJournalEntries
    */
   public AeLongSet getQueuedLocationIds(IAeBusinessProcess aProcess, List aKnownJournalEntries) throws AeBusinessProcessException
   {
      if (getQueuedLocationIds() == null)
      {
         setProcess(aProcess);
         setKnownJournalEntries(aKnownJournalEntries);
         setQueuedLocationIds(new AeLongSet());
         
         // We don't need to wrap the following steps in a database transaction,
         // because if we happen to receive an alarm or message during this
         // period, then the worst that will happen is that we'll see the queued
         // location id and then we'll see the same location id in the journal
         // entries. This requires that we check the queued requests first and
         // then fetch the journal entries.
         addQueuedAlarmLocationIds();
         addQueuedReceiverLocationIds();
         addNewlyJournaledLocationIds();
      }

      return getQueuedLocationIds();
   }

   /**
    * Sets the known journal entries that we are processing for recovery.
    */
   protected void setKnownJournalEntries(List aKnownJournalEntries)
   {
      mKnownJournalEntries = aKnownJournalEntries;
   }

   /**
    * Sets the process being recovered.
    */
   protected void setProcess(IAeBusinessProcess aProcess)
   {
      mProcess = aProcess;
   }

   /**
    * Sets the queued location ids set.
    */
   protected void setQueuedLocationIds(AeLongSet aQueuedLocationIds)
   {
      mQueuedLocationIds = aQueuedLocationIds;
   }

   /**
    * @return the alarms
    */
   protected AeIntMap getAlarms()
   {
      return mAlarms;
   }
}
