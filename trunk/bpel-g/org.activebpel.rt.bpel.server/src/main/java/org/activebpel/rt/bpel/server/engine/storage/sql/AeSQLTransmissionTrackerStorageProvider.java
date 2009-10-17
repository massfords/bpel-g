//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLTransmissionTrackerStorageProvider.java,v 1.4 2007/04/03 20:54:32 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;
import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.storage.AeCounter;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeTransmissionTrackerResultSetHandler;
import org.activebpel.rt.bpel.server.transreceive.AeTransmissionTrackerEntry;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeLongSet;

/**
 * SQL implementation of the storage provider for the transmission - receive manager.
 */
public class AeSQLTransmissionTrackerStorageProvider extends AeAbstractSQLStorageProvider implements
      IAeTransmissionTrackerStorageProvider
{
   /** Config prefix. */
   protected static final String TRANSMISSION_TRACKER_STORAGE_PREFIX = "TransmissionTrackerStorage."; //$NON-NLS-1$
   
   /**
    * Default ctor.
    * @param aConfig SQL configuration.
    */
   public AeSQLTransmissionTrackerStorageProvider(AeSQLConfig aConfig)
   {
      super(TRANSMISSION_TRACKER_STORAGE_PREFIX, aConfig);
   }

   /**  
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider#getNextTransmissionId()
    */
   public long getNextTransmissionId() throws AeStorageException
   {
      return AeCounter.TRANSMISSION_ID_COUNTER.getNextValue();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider#add(org.activebpel.rt.bpel.server.transreceive.AeTransmissionTrackerEntry)
    */
   public void add(AeTransmissionTrackerEntry aEntry) throws AeStorageException
   { 
      Object[] params = new Object[] {
            new Long(aEntry.getTransmissionId()),
            new Integer(aEntry.getState()),
            getStringOrSqlNullVarchar( aEntry.getMessageId() )
      };
      // note: when calling update, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.
      Connection conn = null;
      try
      {
         conn = getTransactionConnection();
         update(conn, IAeTransmissionTrackerSQLKeys.INSERT_ENTRY, params);
      }
      catch(Throwable t)
      {
         AeException.logError(t);
         throw new AeStorageException(t);
      }
      finally
      {
         AeCloser.close(conn);
      }      
      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider#get(long)
    */
   public AeTransmissionTrackerEntry get(long aTransmissionId) throws AeStorageException
   {
      Object param = new Long(aTransmissionId);
      return (AeTransmissionTrackerEntry) query(IAeTransmissionTrackerSQLKeys.GET_ENTRY, param, new AeTransmissionTrackerResultSetHandler());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider#update(org.activebpel.rt.bpel.server.transreceive.AeTransmissionTrackerEntry)
    */
   public void update(AeTransmissionTrackerEntry aEntry) throws AeStorageException
   {
      // Note: This method updates both - message id and state.
      Object[] params = new Object[] {
            new Integer(aEntry.getState()),
            getStringOrSqlNullVarchar( aEntry.getMessageId() ),
            new Long(aEntry.getTransmissionId())
      };
      // note: when calling update, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.      
      Connection conn = null;
      try
      {
         conn = getTransactionConnection();
         update(conn, IAeTransmissionTrackerSQLKeys.UPDATE_ENTRY, params);
      }
      finally
      {
         AeCloser.close(conn);
      }            
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeTransmissionTrackerStorageProvider#remove(org.activebpel.rt.util.AeLongSet)
    */
   public void remove(AeLongSet aTransmissionIds) throws AeStorageException
   {     
      if (!aTransmissionIds.isEmpty())
      {                  
         Connection conn = null;
         // note: when calling update, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.
         try
         {
            Iterator it = aTransmissionIds.iterator();
            conn = getTransactionConnection();         
            while (it.hasNext()) 
            {
               Object[] params = new Object[] {(Long) it.next() };
               update(conn, IAeTransmissionTrackerSQLKeys.DELETE_ENTRY, params);
            }// while
         }
         finally
         {
            AeCloser.close(conn);
         }                     
      }
   }
}
