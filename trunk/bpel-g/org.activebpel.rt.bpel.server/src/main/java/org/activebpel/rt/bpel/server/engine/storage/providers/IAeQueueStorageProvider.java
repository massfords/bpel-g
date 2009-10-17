// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/providers/IAeQueueStorageProvider.java,v 1.7 2008/02/17 21:38:54 mford Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.providers;

import java.util.Date;
import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.engine.storage.AePersistedMessageReceiver;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * A queue storage delegate. This interface defines methods that the delegating queue storage will call in
 * order to store/read date in the underlying database.
 */
public interface IAeQueueStorageProvider extends IAeStorageProvider
{
   /**
    * Stores a single receive object in the database.
    * 
    * @param aReceiveObject
    * @throws AeStorageException
    */
   public void storeReceiveObject(AeMessageReceiver aReceiveObject) throws AeStorageException;

   /**
    * Stores a single alarm in the database.
    * 
    * @param aProcessId
    * @param aLocationPathId
    * @param aGroupId
    * @param aAlarmId
    * @param aDeadline
    * @throws AeStorageException
    */
   public void storeAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId, Date aDeadline)
         throws AeStorageException;

   /**
    * Removes all receive objects in the given group. This removes all rows in the receive table by their
    * GroupID.
    * 
    * @param aProcessId
    * @param aGroupId
    * @param aLocationPathId
    * @param aConnection
    * @throws AeStorageException
    */
   public int removeReceiveObjectsInGroup(long aProcessId, int aGroupId, int aLocationPathId,
         IAeStorageConnection aConnection) throws AeStorageException;

   /**
    * Removes a single receive object by its queued receive id.
    * 
    * @param aQueuedReceiveId
    * @throws AeStorageException
    */
   public boolean removeReceiveObjectById(int aQueuedReceiveId) throws AeStorageException;


   /**
    * Removes a single alarm from the database by its process ID and location ID.
    * 
    * @param aProcessId
    * @param aLocationPathId
    * @param aAlarmId
    * @param aConnection
    * @throws AeStorageException
    */ 
   public boolean removeAlarm(long aProcessId, int aLocationPathId, int aAlarmId,  IAeStorageConnection aConnection)
         throws AeStorageException;

   /**
    * Removes all alarms in the given group. This will remove all rows in the alarm table by their GroupID.
    * 
    * @param aProcessId
    * @param aGroupId
    * @param aConnection
    * @throws AeStorageException
    */
   public int removeAlarmsInGroup(long aProcessId, int aGroupId, IAeStorageConnection aConnection)
         throws AeStorageException;

   /**
    * Gets a single receive object from the DB, identified by process ID and location ID.
    * 
    * @param aProcessId
    * @param aMessageReceiverPathId
    */
   public AePersistedMessageReceiver getReceiveObject(long aProcessId, int aMessageReceiverPathId)
         throws AeStorageException;

   /**
    * Gets a list of receives that match the given match and correlation hashes.
    * 
    * @param aMatchHash
    * @param aCorrelatesHash
    * @throws AeStorageException
    */
   public List getReceives(int aMatchHash, int aCorrelatesHash) throws AeStorageException;

   /**
    * Gets a list of message receivers that match the given filter.
    * 
    * @param aFilter
    */
   public AeMessageReceiverListResult getQueuedMessageReceivers(AeMessageReceiverFilter aFilter)
         throws AeStorageException;

   /**
    * Gets a list of all alarms in the database.
    */
   public List getAlarms() throws AeStorageException;

   /**
    * Gets a list of all alarm in the database that match the given filter.
    * 
    * @param aFilter
    * @throws AeStorageException
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter) throws AeStorageException;

   /**
    * Journals an inbound receive by putting a record of it in the database and associating attachments to the process.
    * 
    * @param aProcessId
    * @param aLocationId
    * @param aInboundReceive
    * @param aConnection
    * @throws AeStorageException
    */
   public long journalInboundReceive(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive,
         IAeStorageConnection aConnection) throws AeStorageException;

   /**
    * Journals an alarm in the database.
    * 
    * @param aProcessId
    * @param aGroupId
    * @param aLocationPathId
    * @param aAlarmId
    * @param aConnection
    * @throws AeStorageException
    */
   public long journalAlarm(long aProcessId, int aGroupId, int aLocationPathId, int aAlarmId, IAeStorageConnection aConnection)
         throws AeStorageException;

   /**
    * Increments the hash collision counter.
    */
   public void incrementHashCollisionCounter();
}
