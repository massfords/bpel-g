// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAeQueueStorage.java,v 1.16.12.1 2008/04/21 16:12:45 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Date;
import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;


/**
 * This interface defines the required methods for a persistent queue store.  A
 * persistent queue store is used by the persistent version of the Active BPEL
 * queue manager.
 */
public interface IAeQueueStorage extends IAeStorage
{
   /**
    * Finds and returns a persisted receive object that matches the inbound 
    * receive.  Whether a receive object matches an inbound receive is determined
    * by the following:
    * <br/>
    * 1) If the inbound receive is correlated, then the <code>correlatesTo</code>
    * method is used to determine if the receive matches.<br/>
    * 2) If the inbound receive is not correlated, then the <code>matches</code>
    * method is used to determine if the receive matches.<br/>
    * <br/>
    * If this method returns a non-null value, then a successful match has been 
    * made AND the inbound receive has been successfully journalled.  This means that
    * the caller can assume that they have exclusive rights to the message receiver.
    * 
    * @param aInboundReceive The inbound receive object.
    * @param aAckCallback durable invoke message acknowledge callback.
    * @return A matching message receiver or null if not found.
    * @throws AeStorageException
    */
   public AeMessageReceiver findMatchingReceive(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback)
         throws AeStorageException;

   /**
    * Stores a receive object in the persistent store.
    * 
    * @param aReceiveObject A BPEL receive object.
    */
   public void storeReceiveObject(AeMessageReceiver aReceiveObject) throws AeStorageException;

   /**
    * Removes a receive object from the persistent store.
    * 
    * @param aProcessId The ID of the owning process.
    * @param aMessageReceiverPathId The message receiver path id (xpath to a BPEL object).
    * @return The receive queue object or <code>null</code> if not found.
    * @throws AeStorageException
    */
   public AeMessageReceiver removeReceiveObject(long aProcessId, int aMessageReceiverPathId) throws AeStorageException;

   /**
    * Returns a selection of message receivers based on the filter criteria.
    * @param aFilter Specifies matching criteria for receiver selection.
    * @return The matching receivers.  The <code>AeMessageReceiverListResult</code> will be 
    * empty if no matching rows are found.
    * @throws AeStorageException
    */
   public AeMessageReceiverListResult getQueuedMessageReceivers(AeMessageReceiverFilter aFilter)
         throws AeStorageException;

   /**
    * Returns a list of all alarms that are saved in the store.  This is 
    * used by the persistent alarm manager to initialize the alarms at engine
    * startup.
    * 
    * @return The list of stored alarms as <code>IAePersistedAlarm</code> objects.
    */
   public List getAlarms() throws AeStorageException;

   /**
    * Returns a list of alarms matching the passe filter.
    * @param aFilter Filter for alarms to find.
    * @return List of alarms matching the passed filter.
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter) throws AeStorageException;
   
   /**
    * Stores an alarm in the store object.  The alarm will be saved to some
    * sort of medium, typically a database or other secondary storage.
    * 
    * @param aProcessId The ID of the process this alarm should be sent to.
    * @param aLocationPathId
    * @param aGroupId The group id of the alarm.
    * @param aAlarmId Alarm id.
    * @param aDeadline The Date when this alarm should be triggered.
    * @throws AeStorageException
    */
   public void storeAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId, Date aDeadline) throws AeStorageException;

   /**
    * Deletes an alarm from the persistent storage.  This will typically happen
    * when the alarm is cancelled, but also when the alarm fires.
    * 
    * @param aProcessId The ID of the process that owns the alarm.
    * @param aLocationPathId The location path id of the BPEL object that owns the alarm.
    * @param aAlarmId alarm execution referene id.    
    * @return True if the alarm was successfully deleted.
    * @throws AeStorageException
    */
   public boolean removeAlarm(long aProcessId, int aLocationPathId, int aAlarmId) throws AeStorageException;
   
   /**
    * Deletes an alarm from the storage for the purposes of dispatching the alarm to a process.
    * This amounts to:<br/>
    * <ol>
    *   <li>Removing the alarm</li>
    *   <li>Removing all message receivers in the same group</li>
    *   <li>Journaling the alarm</li>
    *   <li>Returning the journal id</li>
    * </ol>
    * Returns the journal id of the journaled alarm (or NULL_JOURNAL_ID if the remove failed).
    * 
    * @param aProcessId
    * @param aGroupId
    * @param aLocationPathId
    * @param aAlarmId
    * @throws AeStorageException
    */
   public long removeAlarmForDispatch(long aProcessId, int aGroupId, int aLocationPathId, int aAlarmId) throws AeStorageException;
   
}
