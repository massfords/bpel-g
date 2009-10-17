// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/queue/AeXMLDBQueueStorageProvider.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.queue;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeAlarmJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeInboundReceiveJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AePersistedMessageReceiver;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageUtil;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeAbstractXMLDBStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBQueryBuilder;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.journal.AeXMLDBJournalStorage;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.handlers.AeAlarmListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.handlers.AeFilteredAlarmListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.handlers.AeFilteredMessageReceiverListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.handlers.AeMessageReceiverListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.handlers.AeMessageReceiverResponseHandler;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;

/**
 * A XMLDB implementation of a queue storage provider.
 */
public class AeXMLDBQueueStorageProvider extends AeAbstractXMLDBStorageProvider implements
      IAeQueueStorageProvider
{
   /** The prefix into the xmldb config that this storage object uses. */
   protected static final String CONFIG_PREFIX = "QueueStorage"; //$NON-NLS-1$

   /** A handler for message receivers. */
   public static final AeMessageReceiverResponseHandler MESSAGE_RECEIVER_HANDLER = new AeMessageReceiverResponseHandler();
   /** A filtered message receiver list response handler. */
   protected static final AeFilteredMessageReceiverListResponseHandler FILTERED_MESSAGE_RECEIVER_LIST_RESPONSE_HANDLER = new AeFilteredMessageReceiverListResponseHandler();
   /** A message receiver XMLDB response handler. */
   protected static final AeMessageReceiverListResponseHandler MESSAGE_RECEIVER_LIST_RESPONSE_HANDLER = new AeMessageReceiverListResponseHandler();
   /** A query handler for lists of Alarms. */
   protected static final IAeXMLDBResponseHandler ALARM_LIST_HANDLER = new AeAlarmListResponseHandler();
   /** A query handler for filtered lists of Alarms. */
   protected static final AeFilteredAlarmListResponseHandler ALARM_LIST_FILTERED_HANDLER = new AeFilteredAlarmListResponseHandler();

   /** The journal storage. */
   private AeXMLDBJournalStorage mJournalStorage;

   /**
    * Creates the queue storage object with the given XMLDB config object.
    * 
    * @param aConfig
    * @param aStorageImpl
    */
   public AeXMLDBQueueStorageProvider(AeXMLDBConfig aConfig, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, CONFIG_PREFIX, aStorageImpl);
      setJournalStorage(createJournalStorage());
   }
   
   /**
    * Creates the journal storage.
    */
   protected AeXMLDBJournalStorage createJournalStorage()
   {
      return new AeXMLDBJournalStorage(getXMLDBConfig(), getStorageImpl());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#storeReceiveObject(org.activebpel.rt.bpel.impl.queue.AeMessageReceiver)
    */
   public void storeReceiveObject(AeMessageReceiver aReceiveObject) throws AeStorageException
   {
      LinkedHashMap params = new LinkedHashMap(10);
      params.put(IAeQueueElements.PROCESS_ID, new Long(aReceiveObject.getProcessId()));
      params.put(IAeQueueElements.LOCATION_PATH_ID, new Integer(aReceiveObject.getMessageReceiverPathId()));
      params.put(IAeQueueElements.GROUP_ID, new Integer(aReceiveObject.getGroupId()));
      params.put(IAeQueueElements.PARTNER_LINK_NAME, aReceiveObject.getPartnerLinkOperationKey().getPartnerLinkName());
      params.put(IAeQueueElements.PORT_TYPE, aReceiveObject.getPortType());
      params.put(IAeQueueElements.OPERATION, aReceiveObject.getOperation());
      params.put(IAeQueueElements.CORRELATION_PROPERTIES, AeStorageUtil.getCorrelationProperties(aReceiveObject));
      params.put(IAeQueueElements.MATCH_HASH, new Integer(AeStorageUtil.getReceiveMatchHash(aReceiveObject)));
      params.put(IAeQueueElements.CORRELATE_HASH, new Integer(AeStorageUtil.getReceiveCorrelatesHash(aReceiveObject)));
      params.put(IAeQueueElements.PARTNER_LINK_ID, new Integer(aReceiveObject.getPartnerLinkOperationKey().getPartnerLinkId()));
      params.put(IAeQueueElements.ALLOWS_CONCURRENCY, new Boolean(aReceiveObject.isConcurrent()));

      insertDocument(IAeQueueConfigKeys.INSERT_QUEUED_RECEIVE, params);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#storeAlarm(long, int, int, int, java.util.Date)
    */
   public void storeAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId, Date aDeadline)
         throws AeStorageException
   {
      Object[] params = {
            new Long(aProcessId),
            new Integer(aLocationPathId),
            new Integer(aGroupId),
            new Integer(aAlarmId),
            new AeSchemaDateTime(aDeadline)
      };
      insertDocument(IAeQueueConfigKeys.INSERT_ALARM, params);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#removeReceiveObjectsInGroup(long, int, int, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public int removeReceiveObjectsInGroup(long aProcessId, int aGroupId, int aLocationPathId,
         IAeStorageConnection aConnection) throws AeStorageException
   {
      Object [] params = {
            new Long(aProcessId),
            new Integer(aGroupId)
      };
      return deleteDocuments(IAeQueueConfigKeys.DELETE_QUEUED_RECEIVES_BY_GROUP, params,
            getXMLDBConnection(aConnection));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#removeReceiveObjectById(int)
    */
   public boolean removeReceiveObjectById(int aQueuedReceiveId) throws AeStorageException
   {
      Object[] params = { new Integer(aQueuedReceiveId) };
      return deleteDocuments(IAeQueueConfigKeys.DELETE_QUEUED_RECEIVE_BYID, params) > 0;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#removeAlarm(long, int, int, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public boolean removeAlarm(long aProcessId, int aLocationPathId, int aAlarmId, IAeStorageConnection aConnection)
         throws AeStorageException
   {
      Object[] params = { new Long(aProcessId), new Integer(aLocationPathId), new Integer(aAlarmId) };
      return deleteDocuments(IAeQueueConfigKeys.DELETE_ALARM, params, getXMLDBConnection(aConnection)) > 0;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#removeAlarmsInGroup(long, int, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public int removeAlarmsInGroup(long aProcessId, int aGroupId, IAeStorageConnection aConnection)
         throws AeStorageException
   {
      Object[] params = { new Long(aProcessId), new Integer(aGroupId) };
      return deleteDocuments(IAeQueueConfigKeys.DELETE_ALARMS_BY_GROUPID, params,
            getXMLDBConnection(aConnection));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getReceiveObject(long, int)
    */
   public AePersistedMessageReceiver getReceiveObject(long aProcessId, int aMessageReceiverPathId)
         throws AeStorageException
   {
      Object[] params = new Object[] {
            new Long(aProcessId),
            new Integer(aMessageReceiverPathId)
      };
      return (AePersistedMessageReceiver) query(IAeQueueConfigKeys.GET_QUEUED_RECEIVE, params,
            MESSAGE_RECEIVER_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getReceives(int, int)
    */
   public List getReceives(int aMatchHash, int aCorrelatesHash) throws AeStorageException
   {
      Object[] params = { new Integer(aMatchHash), new Integer(aCorrelatesHash) };
      return (List) query(IAeQueueConfigKeys.GET_CORRELATED_RECEIVES, params,
            MESSAGE_RECEIVER_LIST_RESPONSE_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getQueuedMessageReceivers(org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter)
    */
   public AeMessageReceiverListResult getQueuedMessageReceivers(AeMessageReceiverFilter aFilter)
         throws AeStorageException
   {
      AeXMLDBQueryBuilder queryBuilder = new AeXMLDBFilteredMessageReceiverListQueryBuilder(aFilter, getXMLDBConfig(), getStorageImpl());
      AeFilteredMessageReceiverListResponseHandler handler = FILTERED_MESSAGE_RECEIVER_LIST_RESPONSE_HANDLER;
      List receives = (List) query(queryBuilder, handler);
      return new AeMessageReceiverListResult(handler.getRowCount(), receives);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getAlarms()
    */
   public List getAlarms() throws AeStorageException
   {
      return (List) query(IAeQueueConfigKeys.GET_ALARMS, ALARM_LIST_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getAlarms(org.activebpel.rt.bpel.impl.list.AeAlarmFilter)
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter) throws AeStorageException
   {
      AeXMLDBQueryBuilder queryBuilder = new AeXMLDBFilteredAlarmListQueryBuilder(aFilter, getXMLDBConfig(), getStorageImpl());
      AeFilteredAlarmListResponseHandler handler = ALARM_LIST_FILTERED_HANDLER;
      List alarms = (List) query(queryBuilder, handler);
      return new AeAlarmListResult(handler.getRowCount(), alarms);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#journalInboundReceive(long, int, org.activebpel.rt.bpel.impl.queue.AeInboundReceive, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public long journalInboundReceive(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive,
         IAeStorageConnection aConnection) throws AeStorageException
   {
      return getJournalStorage().writeJournalEntry(aProcessId,
            new AeInboundReceiveJournalEntry(aLocationId, aInboundReceive),
            getXMLDBConnection(aConnection));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#journalAlarm(long,
    *      int, int, int, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public long journalAlarm(long aProcessId, int aGroupId, int aLocationPathId, int aAlarmId, IAeStorageConnection aConnection)
         throws AeStorageException
   {
      return getJournalStorage().writeJournalEntry(aProcessId,
            new AeAlarmJournalEntry(aLocationPathId, aGroupId, aAlarmId), getXMLDBConnection(aConnection));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#incrementHashCollisionCounter()
    */
   public void incrementHashCollisionCounter()
   {
      // TODO (EPW) Support this some day...
   }

   /**
    * @return Returns the journalStorage.
    */
   protected AeXMLDBJournalStorage getJournalStorage()
   {
      return mJournalStorage;
   }

   /**
    * @param aJournalStorage The journalStorage to set.
    */
   protected void setJournalStorage(AeXMLDBJournalStorage aJournalStorage)
   {
      mJournalStorage = aJournalStorage;
   }
}
