// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLQueueStorageProvider.java,v 1.13 2008/02/17 21:38:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeAlarmJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeInboundReceiveJournalEntry;
import org.activebpel.rt.bpel.server.engine.storage.AeCounter;
import org.activebpel.rt.bpel.server.engine.storage.AePersistedMessageReceiver;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageUtil;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.sql.filters.AeSQLAlarmFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.filters.AeSQLReceiverFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeAlarmListHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeAlarmListQueryHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeMessageReceiverHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeMessageReceiverListHandler;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * This is a SQL Queue Storage provider (an implementation of IAeQueueStorageDelegate).
 */
public class AeSQLQueueStorageProvider extends AeAbstractSQLStorageProvider implements IAeQueueStorageProvider
{
   /** The SQL statement prefix for all SQL statements used in this class. */
   public static final String SQLSTATEMENT_PREFIX = "QueueStorage."; //$NON-NLS-1$

   /** A SQL Result Set Handler that returns a list of Message Receivers. */
   private static final AeMessageReceiverListHandler MESSAGE_RECEIVER_LIST_HANDLER = new AeMessageReceiverListHandler();
   /** A SQL Result Set Handler that returns a single Message Receivers. */
   private static final AeMessageReceiverHandler MESSAGE_RECEIVER_HANDLER = new AeMessageReceiverHandler();
   /** A SQL Result Set Handler that returns a list of Alarms. */
   private static final ResultSetHandler ALARM_LIST_HANDLER = new AeAlarmListQueryHandler();

   /** The number of times to retry deadlocked transactions. */
   private static final int DEADLOCK_TRY_COUNT = 5;

   /** The journal storage. */
   private AeSQLJournalStorage mJournalStorage;
   
   /**
    * Constructs a SQL Queue storage delegate given a SQL config object.
    * 
    * @param aConfig
    */
   public AeSQLQueueStorageProvider(AeSQLConfig aConfig)
   {
      super(SQLSTATEMENT_PREFIX, aConfig);
      setJournalStorage(createJournalStorage());
   }
   
   /**
    * Returns next available queued receive object id.
    * @throws AeStorageException
    */
   protected long getNextQueuedReceiveId() throws AeStorageException
   {
      return AeCounter.QUEUED_RECEIVE_ID_COUNTER.getNextValue();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#storeReceiveObject(org.activebpel.rt.bpel.impl.queue.AeMessageReceiver)
    */
   public void storeReceiveObject(AeMessageReceiver aReceiveObject) throws AeStorageException
   {
      long receiveId = getNextQueuedReceiveId();

      Object[] params = new Object[]
      {
         new Long(receiveId),
         new Long(aReceiveObject.getProcessId()),
         new Integer(aReceiveObject.getMessageReceiverPathId()),
         aReceiveObject.getOperation(),
         aReceiveObject.getPartnerLinkOperationKey().getPartnerLinkName(),
         AeUtil.getSafeString(aReceiveObject.getPortType().getNamespaceURI()),
         aReceiveObject.getPortType().getLocalPart(),
         AeStorageUtil.getCorrelationProperties(aReceiveObject),
         new Integer(AeStorageUtil.getReceiveMatchHash(aReceiveObject)),
         new Integer(AeStorageUtil.getReceiveCorrelatesHash(aReceiveObject)),
         new Integer(aReceiveObject.getGroupId()),
         new Integer(aReceiveObject.getPartnerLinkOperationKey().getPartnerLinkId()),
         new Integer(AeDbUtils.convertBooleanToInt(aReceiveObject.isConcurrent()))
      };
      update(IAeQueueSQLKeys.INSERT_QUEUED_RECEIVE, params);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#storeAlarm(long, int, int, int, java.util.Date)
    */
   public void storeAlarm(long aProcessId, int aLocationPathId, int aGroupId, int aAlarmId, Date aDeadline)
         throws AeStorageException
   {
      Object[] params = new Object[] {
            new Long(aProcessId),
            new Integer(aLocationPathId),
            new Timestamp(aDeadline.getTime()),
            new Long(aDeadline.getTime()),
            new Integer(aGroupId),
            new Integer(aAlarmId)
      };
      
      update(IAeQueueSQLKeys.INSERT_ALARM, params);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#removeReceiveObjectsInGroup(long, int, int, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public int removeReceiveObjectsInGroup(long aProcessId, int aGroupId, int aLocationPathId,
         IAeStorageConnection aConnection) throws AeStorageException
   {
      Connection connection = getSQLConnection(aConnection);
      
      Object[] params = { new Long(aProcessId), new Integer(aGroupId) };
      int count = update(connection, IAeQueueSQLKeys.DELETE_QUEUED_RECEIVES_BY_GROUP, params);

      // Now, for backwards compatibility, remove a single queued receive if none were found in the
      // above delete.
      if (count == 0 && aLocationPathId != -1)
      {
         Object[] params2 = { new Long(aProcessId), new Integer(aLocationPathId) };
         count = update(connection, IAeQueueSQLKeys.DELETE_QUEUED_RECEIVES_BY_LOCID, params2);
      }

      return count;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#removeReceiveObjectById(int)
    */
   public boolean removeReceiveObjectById(int aQueuedReceiveId) throws AeStorageException
   {
      // Next, delete the queued receive from the QueuedReceive table.
      return update(IAeQueueSQLKeys.DELETE_QUEUED_RECEIVE_BYID, new Integer(aQueuedReceiveId)) == 1;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#removeAlarm(long, int, int, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public boolean removeAlarm(long aProcessId, int aLocationPathId, int aAlarmId, IAeStorageConnection aConnection)
         throws AeStorageException
   {
      Object[] params = new Object[] { new Long(aProcessId), new Integer(aLocationPathId), new Integer(aAlarmId) };
      return update(getSQLConnection(aConnection), IAeQueueSQLKeys.DELETE_ALARM, params) == 1;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#removeAlarmsInGroup(long,
    *      int, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public int removeAlarmsInGroup(long aProcessId, int aGroupId, IAeStorageConnection aConnection)
         throws AeStorageException
   {
      Object[] params = new Object[] { new Long(aProcessId), new Integer(aGroupId) };
      return update(getSQLConnection(aConnection), IAeQueueSQLKeys.DELETE_ALARMS_IN_GROUP, params);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getReceiveObject(long,
    *      int)
    */
   public AePersistedMessageReceiver getReceiveObject(long aProcessId, int aMessageReceiverPathId)
         throws AeStorageException
   {
      Object[] params = new Object[] { new Long(aProcessId), new Integer(aMessageReceiverPathId) };
      return (AePersistedMessageReceiver) query(IAeQueueSQLKeys.GET_QUEUED_RECEIVE, params,
            MESSAGE_RECEIVER_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getReceives(int, int)
    */
   public List getReceives(int aMatchHash, int aCorrelatesHash) throws AeStorageException
   {
      for (int tries = 0; true; )
      {
         try
         {
            // Stop looping when successful.
            Object[] params = new Object[] { new Integer(aMatchHash), new Integer(aCorrelatesHash) };
            return (List) query(IAeQueueSQLKeys.GET_CORRELATED_RECEIVES, params,
                  MESSAGE_RECEIVER_LIST_HANDLER);
         }
         catch (AeStorageException e)
         {
            // Abort if this is not an SQL exception or we've already tried
            // getDeadlockTryCount() times.
            if (!(e.getCause() instanceof SQLException) || (++tries >= getDeadlockTryCount()))
            {
               throw e;
            }

            AeException.logError(null, AeMessages.getString("AeSQLQueueStorage.DEADLOCK_RETRY")); //$NON-NLS-1$
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getQueuedMessageReceivers(org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter)
    */
   public AeMessageReceiverListResult getQueuedMessageReceivers(AeMessageReceiverFilter aFilter)
         throws AeStorageException
   {
      AeSQLReceiverFilter filter = createSQLFilter( aFilter );
      String sql = filter.getSelectStatement();
      Object[] params = filter.getParams();
      return getFilteredReceives( sql, params, aFilter );
   }

   /**
    * Creates the filter wrapper used to format the sql statement.
    * @param aFilter The selection criteria.
    */
   protected AeSQLReceiverFilter createSQLFilter(AeMessageReceiverFilter aFilter) throws AeStorageException
   {
      return new AeSQLReceiverFilter(aFilter, getSQLConfig());
   }
   

   /**
    * Extracts the data from the ResultSet and returns an AeSQLMessageReceiver.
    * 
    * @param aResultSet
    * @throws SQLException
    */
   public static AePersistedMessageReceiver readSQLMessageReceiver(ResultSet aResultSet) throws SQLException
   {
      AePersistedMessageReceiver rval = null;
      int queuedReceiveId = aResultSet.getInt(IAeQueueColumns.QUEUED_RECEIVE_ID);
      long processId = aResultSet.getLong(IAeQueueColumns.PROCESS_ID);
      int locationPathId = aResultSet.getInt(IAeQueueColumns.LOCATION_PATH_ID);
      String operation = aResultSet.getString(IAeQueueColumns.OPERATION);
      String plinkName = aResultSet.getString(IAeQueueColumns.PARTNER_LINK_NAME);
      // TODO (MF) I want to drop the port type from this table
      String portTypeNamespace = aResultSet.getString(IAeQueueColumns.PORT_TYPE_NAMESPACE);
      String portTypeLocalPart = aResultSet.getString(IAeQueueColumns.PORT_TYPE_LOCALPART);
      Reader corrPropsReader = aResultSet.getClob(IAeQueueColumns.CORRELATION_PROPERTIES).getCharacterStream();
      int groupId = aResultSet.getInt(IAeQueueColumns.GROUP_ID);
      int partnerLinkId = aResultSet.getInt(IAeQueueColumns.PARTNER_LINK_ID);
      AePartnerLinkOpKey plDefKey = new AePartnerLinkOpKey(plinkName, partnerLinkId, operation);

      QName processName = new QName(aResultSet.getString(IAeProcessColumns.PROCESS_NAMESPACE), aResultSet.getString(IAeProcessColumns.PROCESS_NAME));
      boolean allowsConcurrency = AeDbUtils.convertIntToBoolean(aResultSet.getInt(IAeQueueColumns.ALLOWS_CONCURRENCY));

      try
      {
         QName portType = new QName(portTypeNamespace, portTypeLocalPart);
         Map info = AeStorageUtil.deserializeCorrelationProperties(corrPropsReader);
         rval =
            new AePersistedMessageReceiver(
                  queuedReceiveId,
                  processId,
                  processName,
                  plDefKey,
                  portType,
                  info,
                  locationPathId,
                  groupId,
                  allowsConcurrency);
      }
      catch (Exception e)
      {
         AeException.logError(e, AeMessages.getString("AeSQLQueueStorage.ERROR_13")); //$NON-NLS-1$
      }
      return rval;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getAlarms()
    */
   public List getAlarms() throws AeStorageException
   {
      return (List) query(IAeQueueSQLKeys.GET_ALARMS, null, ALARM_LIST_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#getAlarms(org.activebpel.rt.bpel.impl.list.AeAlarmFilter)
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter) throws AeStorageException
   {
      AeSQLAlarmFilter filter = createSQLAlarmFilter(aFilter);
      String sql = filter.getSelectStatement();
      Object[] params = filter.getParams();
      return getFilteredAlarms(sql, params, aFilter);
   }

   /**
    * Creates the filter wrapper used to format the sql statement.
    * 
    * @param aFilter The selection criteria.
    */
   protected AeSQLAlarmFilter createSQLAlarmFilter(AeAlarmFilter aFilter) throws AeStorageException
   {
      return new AeSQLAlarmFilter(aFilter, getSQLConfig());
   }

   /**
    * Generic convenience method for getting a list of alarms from the database,
    * given the SQL query and parameters.
    *
    * @param aSQLQuery The SQL query that will return a list of receives.
    * @param aParams The parameters to the SQL query.
    * @return The AeAlarmListResult.
    * @throws AeStorageException
    */
   protected AeAlarmListResult getFilteredAlarms(String aSQLQuery, Object[] aParams, AeAlarmFilter aFilter)
         throws AeStorageException
   {
      Connection connection = getConnection();

      try
      {
         AeAlarmListHandler handler = new AeAlarmListHandler(aFilter);
         List matches = (List) getQueryRunner().query(connection, aSQLQuery, aParams, handler);
         return new AeAlarmListResult(handler.getRowCount(), matches);
      }
      catch (SQLException ex)
      {
         throw new AeStorageException(ex);
      }
      finally
      {
         AeCloser.close(connection);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#journalInboundReceive(long, int, org.activebpel.rt.bpel.impl.queue.AeInboundReceive, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public long journalInboundReceive(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive,
         IAeStorageConnection aConnection) throws AeStorageException
   {
      return getJournalStorage().writeJournalEntry(aProcessId,
            new AeInboundReceiveJournalEntry(aLocationId, aInboundReceive), 
            getSQLConnection(aConnection));
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#journalAlarm(long,
    *      int, int, int, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public long journalAlarm(long aProcessId, int aGroupId, int aLocationPathId, int aAlarmId, IAeStorageConnection aConnection)
         throws AeStorageException
   {
      return getJournalStorage().writeJournalEntry(aProcessId,
            new AeAlarmJournalEntry(aLocationPathId, aGroupId, aAlarmId), getSQLConnection(aConnection));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeQueueStorageProvider#incrementHashCollisionCounter()
    */
   public void incrementHashCollisionCounter()
   {
      try
      {
         AeCounter.HASH_COLLISION_COUNTER.getNextValue();
      }
      catch (Exception e) 
      {
         AeException.logError(e, AeMessages.getString("AeSQLQueueStorage.ERROR_11")); //$NON-NLS-1$
      }
   }

   /**
    * Generic convenience method for getting a list of receives from the database,
    * given the SQL query and parameters.
    * 
    * @param aSQLQuery The SQL query that will return a list of receives.
    * @param aParams The parameters to the SQL query.
    * @return The AeMessageReceiverListResult.
    * @throws AeStorageException
    */
   protected AeMessageReceiverListResult getFilteredReceives(String aSQLQuery, Object[] aParams,
         AeMessageReceiverFilter aFilter) throws AeStorageException
   {
      Connection connection = getConnection();
      try
      {
         AeMessageReceiverListHandler handler = new AeMessageReceiverListHandler(aFilter);
         List matches = (List) getQueryRunner().query(connection, aSQLQuery, aParams, handler);
         return new AeMessageReceiverListResult(handler.getRowCount(), matches);
      }
      catch (SQLException ex)
      {
         throw new AeStorageException(ex);
      }
      finally
      {
         AeCloser.close(connection);
      }
   }

   /**
    * Creates the journal storage.
    */
   protected AeSQLJournalStorage createJournalStorage()
   {
      return new AeSQLJournalStorage(getSQLConfig());
   }

   /**
    * @return Returns the journalStorage.
    */
   protected AeSQLJournalStorage getJournalStorage()
   {
      return mJournalStorage;
   }
   
   /**
    * @param aJournalStorage The journalStorage to set.
    */
   protected void setJournalStorage(AeSQLJournalStorage aJournalStorage)
   {
      mJournalStorage = aJournalStorage;
   }

   /**
    * Returns number of times to retry deadlocked transactions.
    */
   protected int getDeadlockTryCount()
   {
      return DEADLOCK_TRY_COUNT;
   }
}
