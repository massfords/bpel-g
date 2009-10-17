// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AePersistentQueueManager.java,v 1.26 2007/11/15 21:06:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.AeInMemoryQueueManager;
import org.activebpel.rt.bpel.server.engine.AePersistentProcessManager;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Implements a persistent queue manager.  This is a version of the bpel engine
 * queue manager that will remember its state even across engine restarts.  The
 * class delegates the work of actually saving state to a persistent queue store.
 */
public class AePersistentQueueManager extends AeInMemoryQueueManager
{
   /** The storage that this manager will use to store and retrieve queue objects. */
   protected IAeQueueStorage mStorage;

   /** 
    * Constructs a persistent queue manager using the given configuration map.
    * 
    * @param aConfig The configuration map for this manager.
    */
   public AePersistentQueueManager(Map aConfig) throws AeException
   {
      super(aConfig);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#create()
    */
   public void create() throws Exception
   {
      super.create();
      setStorage(AePersistentStoreFactory.getInstance().getQueueStorage());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeBaseQueueManager#findMatchForInboundReceive(org.activebpel.rt.bpel.impl.queue.AeInboundReceive, org.activebpel.wsio.IAeMessageAcknowledgeCallback)
    */
   protected AeMessageReceiver findMatchForInboundReceive(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback) throws AeBusinessProcessException
   {
      AeBusinessProcessException rethrownException = null;

      for (int tries = 0; tries < getDeadlockTryCount(); tries++ )
      {
         try
         {
            return getStorage().findMatchingReceive(aInboundReceive, aAckCallback);
         }
         catch (AeStorageException ex)
         {
            // If the root cause is not a SQL exception, break out of the retry loop.
            // TODO (EPW) We need some sort of AeStorageDeadlockException here.
            if (!(ex.getCause() instanceof SQLException))
            {
               throw ex;
            }
            else
            {
               AeException.logError(null, AeMessages.getString("AePersistentQueueManager.INBOUND_RECEIVE_DEADLOCK_ERROR")); //$NON-NLS-1$
               rethrownException = ex;
            }
         }
      }
      throw rethrownException;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.AeBaseQueueManager#addMessageReceiverInternal(org.activebpel.rt.bpel.impl.queue.AeMessageReceiver)
    */
   protected void addMessageReceiverInternal(AeMessageReceiver aMessageReceiver)
   {
      try
      {
         getStorage().storeReceiveObject(aMessageReceiver);
      }
      catch (Exception e)
      {
         AeException.logError(e, AeMessages.getString("AePersistentQueueManager.ERROR_0")); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#removeMessageReceiver(long, int)
    */
   public boolean removeMessageReceiver(long aProcessId, int aMessageReceiverPathId) throws AeBusinessProcessException
   {
      try
      {
         return getStorage().removeReceiveObject(aProcessId, aMessageReceiverPathId) != null;
      }
      catch (AeException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AePersistentQueueManager.ERROR_1"), e); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#getMessageReceivers(org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter)
    */
   public AeMessageReceiverListResult getMessageReceivers(AeMessageReceiverFilter aFilter) throws AeBusinessProcessException
   {
      return getStorage().getQueuedMessageReceivers( aFilter );
   }
   

   /**
    * Called when the manager starts to do the initial loadup of saved alarms.  These saved
    * alarms were scheduled prior to stopping the manager.  Now that it's started up again, 
    * the alarms must be loaded and scheduled.
    */
   protected void doInitialAlarmLoad()
   {
      loadAndScheduleAlarms();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeBaseQueueManager#scheduleAlarm(long, int, int, int, java.util.Date)
    */
   public void scheduleAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId, Date aDeadline)
         throws AeBusinessProcessException
   {
      try
      {
         internalStoreAlarm(aProcessId, aLocationPathId, aGroupId, aAlarmId, aDeadline);
      }
      catch (AeStorageException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AePersistentQueueManager.ERROR_0"), e); //$NON-NLS-1$
      }

      // Delegate the scheduling work to the in-memory alarm manager.
      internalScheduleAlarm(aProcessId, aLocationPathId, aGroupId, aAlarmId, aDeadline);
   }

   /**
    * This method is called to store the alarm in whatever storage facility is being used.  In the
    * case of the simple persistent alarm manager, the alarm is stored in the configured storage
    * object.
    * @param aProcessId The process id of the alarm.
    * @param aLocationPathId The location path of the alarm.
    * @param aGroupId The alarm's group id.
    * @param aAlarmId Alarm id.
    * @param aDeadline The alarm's deadline.
    * 
    * @throws AeStorageException
    */
   protected void internalStoreAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId, Date aDeadline)
         throws AeStorageException
   {
      getStorage().storeAlarm(aProcessId, aLocationPathId, aGroupId, aAlarmId, aDeadline);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeBaseQueueManager#internalRemoveAlarm(long, int, int)
    */
   protected boolean internalRemoveAlarm(long aProcessId, int aLocationPathId, int aAlarmId) throws AeBusinessProcessException
   {
      boolean removed = removeAlarmFromStore(aProcessId, aLocationPathId, aAlarmId);

      // Don't care if the super version of remove worked - the bottom line is:
      // was the alarm removed from the store?
      // The super call just removes the alarm from the Timer service
      super.internalRemoveAlarm(aProcessId, aLocationPathId, aAlarmId);

      // True as long as the alarm was successfully removed from the store.
      return removed;
   }

   /**
    * Convenience method to get the deadlock retry count.
    */
   protected int getDeadlockTryCount()
   {
      return getProcessManager().getDeadlockTryCount();
   }
   
   /**
    * Convenience method to get the persistent process manager.
    */
   protected AePersistentProcessManager getProcessManager()
   {
      return (AePersistentProcessManager) AeEngineFactory.getEngine().getProcessManager();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeBaseQueueManager#removeAlarmForDispatch(long, int, int, int)
    */
   public long removeAlarmForDispatch(long aProcessId, int aGroupId, int aLocationPathId, int aAlarmId)
         throws AeBusinessProcessException
   {
      AeBusinessProcessException rethrownException = null;

      for (int tries = 0; tries < getDeadlockTryCount(); tries++ )
      {
         try
         {
            long journalId = getStorage().removeAlarmForDispatch(aProcessId, aGroupId, aLocationPathId, aAlarmId);
            super.internalRemoveAlarm(aProcessId, aLocationPathId, aAlarmId);
            return journalId;
         }
         catch (AeStorageException ex)
         {
            // If the root cause is not a SQL exception, break out of the retry loop.
            // TODO (EPW) We need some sort of AeStorageDeadlockException here.
            if (!(ex.getCause() instanceof SQLException))
            {
               throw ex;
            }
            else
            {
               String msg = AeMessages.format("AePersistentQueueManager.ALARM_DEADLOCK_ERROR", //$NON-NLS-1$
                     new Object[] { new Long(aProcessId), new Integer(aLocationPathId) });
               AeException.logError(null, msg);
               rethrownException = ex;
            }
         }
      }
      throw rethrownException;
   }
   
   /**
    * Removes the alarm from the persistent store.
    */
   protected boolean removeAlarmFromStore(long aProcessId, int aLocationPathId, int aAlarmId) throws AeBusinessProcessException
   {
      return getStorage().removeAlarm(aProcessId, aLocationPathId, aAlarmId);
   }

   /**
    * Schedule all of the alarms that were loaded on startup.  This will restart
    * any alarms that were 'in progress' when the server was shut down.
    */
   protected void loadAndScheduleAlarms()
   {
      try
      {
         List alarms = loadAlarmsForScheduling();
         scheduleAlarms(alarms);
      }
      catch (Exception e)
      {
         AeException.logError(e, AeMessages.getString("AePersistentQueueManager.ERROR_1")); //$NON-NLS-1$
      }
   }

   /**
    * Does the work of scheduling a list of alarms.
    * 
    * @param aAlarms The list of alarms to schedule.
    */
   protected void scheduleAlarms(List aAlarms) throws AeException
   {
      Iterator iter = aAlarms.iterator();
      while (iter.hasNext())
      {
         IAePersistedAlarm alarm = (IAePersistedAlarm) iter.next();
         internalScheduleAlarm(alarm.getProcessId(), alarm.getLocationPathId(), alarm.getGroupId(), alarm.getAlarmId() ,alarm.getDeadline());
      }
   }

   /**
    * Does the work of actually scheduling an alarm for firing.  This will delegate the work to
    * the superclass, who keeps the list of currently scheduled alarms in memory and utilizes
    * the timer service to handle the work of firing the alarm at the desired time.
    * @param aProcessId The process id of the alarm.
    * @param aLocationPathId The location path of the alarm.
    * @param aGroupId The alarm's group location id.
    * @param aAlarmId alarm id 
    * @param aDeadline The alarm's deadline.
    * 
    * @throws AeBusinessProcessException
    */
   protected void internalScheduleAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId,
         Date aDeadline) throws AeBusinessProcessException
   {
      super.scheduleAlarm(aProcessId, aLocationPathId, aGroupId, aAlarmId, aDeadline);
   }

   /**
    * This method is called whenever the manager needs to schedule a set of previously
    * persisted alarms (for example on startup).
    * 
    * @return A list of stored alarms.
    */
   protected List loadAlarmsForScheduling() throws AeBusinessProcessException
   {
      return getStorage().getAlarms();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#getAlarms(org.activebpel.rt.bpel.impl.list.AeAlarmFilter)
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter) throws AeBusinessProcessException
   {
      return getStorage().getAlarms(aFilter);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.AeInMemoryQueueManager#saveLookup()
    */
   protected void saveLookup()
   {
      // do nothing already persisted.
   }

   /**
    * @return Returns the store.
    */
   protected IAeQueueStorage getStorage()
   {
      return mStorage;
   }
   
   /**
    * @param aStorage The storage to set.
    */
   protected void setStorage(IAeQueueStorage aStorage)
   {
      mStorage = aStorage;
   }
}
