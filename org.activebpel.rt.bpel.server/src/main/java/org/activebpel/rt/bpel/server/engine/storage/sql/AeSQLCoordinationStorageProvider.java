// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLCoordinationStorageProvider.java,v 1.5 2007/02/06 14:39:59 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId;
import org.activebpel.rt.bpel.server.engine.storage.AeCounter;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeCoordinationDetailListResultSetHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeSQLCoordinatingListResultSetHandler;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeSQLCoordinatingResultSetHandler;
import org.activebpel.rt.util.AeCloser;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * A SQL version of a coordination storage provider.
 */
public class AeSQLCoordinationStorageProvider extends AeAbstractSQLStorageProvider implements
      IAeCoordinationStorageProvider
{
   /** Config prefix. */
   protected static final String COORDINATION_STORAGE_PREFIX = "CoordinationStorage."; //$NON-NLS-1$

   /** A result set handler for returning  */
   private static final ResultSetHandler COORDINATION_DETAIL_LIST_RESULT_SET_HANDLER = new AeCoordinationDetailListResultSetHandler();

   /** Coordination manager. */
   private IAeCoordinationManager mCoordinationManager;
   /** The cached coordinating response handler. */
   private ResultSetHandler mCoordinatingResultSetHandler;
   /** The cached coordinating list response handler. */
   private ResultSetHandler mCoordinatingListResultSetHandler;

   /**
    * Constructs a sql coordination storage delegate with the given SQL config.
    * 
    * @param aSQLConfig
    */
   public AeSQLCoordinationStorageProvider(AeSQLConfig aSQLConfig)
   {
      super(COORDINATION_STORAGE_PREFIX, aSQLConfig);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#insertContext(java.lang.String, int, java.lang.String, java.lang.String, long, java.lang.String, org.activebpel.rt.bpel.impl.fastdom.AeFastDocument)
    */
   public void insertContext(String aState, int aRole, String aIdentifier, String aCoordinationType, long aProcessId, 
                             String aLocationPath, AeFastDocument aContextDocument) throws AeStorageException
   {
      Object contextClob = aContextDocument == null ? AeQueryRunner.NULL_CLOB : (Object) aContextDocument;

      long pk = AeCounter.COORDINATION_PK_COUNTER.getNextValue();
      Object[] params = new Object[] {
            new Long(pk),
            aCoordinationType,
            new Integer(aRole),
            aIdentifier,
            aState,
            new Long(aProcessId),
            aLocationPath.toCharArray(),
            contextClob,
            new Date(), // start date
            new Date()  // modified date
      };
      // note: when calling update, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.
      Connection conn = null;
      try
      {
         conn = getTransactionConnection();
         update(conn, IAeCoordinationSQLKeys.INSERT_CONTEXT, params);
      }
      finally
      {
         AeCloser.close(conn);
      }      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getCoordination(java.lang.String, long)
    */
   public IAeCoordinating getCoordination(String aCoordinationId, long aProcessId) throws AeStorageException
   {
      Object[] params = new Object[] {
            aCoordinationId,
            new Long(aProcessId)
      };
      // Construct a ResultSetHandler that converts the first row of the ResultSet to an IAeCoordinating.
      ResultSetHandler handler = getCoordinatingResultSetHandler();
      // Run the query.
      // note: when calling query, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.
      Connection conn = null;
      try
      {
         conn = getTransactionConnection();
         return (IAeCoordinating) query( conn,IAeCoordinationSQLKeys.LOOKUP_COORDINATION, params, handler);
      }
      finally
      {
         AeCloser.close(conn);
      }      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getCoordinationsByProcessId(long)
    */
   public List getCoordinationsByProcessId(long aProcessId) throws AeStorageException
   {
      Object[] params = { new Long(aProcessId) };
      ResultSetHandler handler = getCoordinatingListResultSetHandler();
      // note: when calling query, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.      
      Connection conn = null;
      try
      {
         conn = getTransactionConnection();
         return (List) query(conn, IAeCoordinationSQLKeys.LIST_BY_PROCESS_ID, params, handler);
      }
      finally
      {
         AeCloser.close(conn);
      }      
      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getCoordinations(java.lang.String)
    */
   public List getCoordinations(String aCoordinationId) throws AeStorageException
   {
      Object[] params = {new Long(aCoordinationId)};
      ResultSetHandler handler = createCoordinatingListResultSetHandler();
      // note: when calling query, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.      
      Connection conn = null;
      try
      {
         conn = getTransactionConnection();
         return (List) query( conn,IAeCoordinationSQLKeys.LIST_BY_COORDINATION_ID, params, handler);
      }
      finally
      {
         AeCloser.close(conn);
      }            
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#updateCoordinationState(org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId, java.lang.String)
    */
   public void updateCoordinationState(AePersistentCoordinationId aCoordinationId, String aState) throws AeStorageException
   {
      Object[] params = new Object[] {
            aState,
            new Date(), // modified date
            aCoordinationId.getIdentifier(),
            new Long(aCoordinationId.getProcessId())
      };
      // note: when calling update, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.
      Connection conn = null;
      try
      {
         conn = getTransactionConnection();
         update( conn, IAeCoordinationSQLKeys.UPDATE_STATE, params);
      }
      finally
      {
         AeCloser.close(conn);
      }      
      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#updateCoordinationContext(org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId, org.activebpel.rt.bpel.impl.fastdom.AeFastDocument)
    */
   public void updateCoordinationContext(AePersistentCoordinationId aCoordinationId,
         AeFastDocument aContextDocument) throws AeStorageException
   {
      Object contextClob = aContextDocument == null ? AeQueryRunner.NULL_CLOB : (Object) aContextDocument;

      Object[] params = new Object[] {
            contextClob,
            aCoordinationId.getIdentifier(),
            new Long(aCoordinationId.getProcessId())
      };
      // note: when calling update, we also pass the aClose=true to close the connection in case the connection is not from the TxManager.       
      Connection conn = null;
      try
      {
         conn = getTransactionConnection();
         update( conn, IAeCoordinationSQLKeys.UPDATE_CONTEXT, params);
      }
      finally
      {
         AeCloser.close(conn);
      }      
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getCoordinatorDetail(long)
    */
   public List getCoordinatorDetail(long aChildProcessId) throws AeStorageException
   {
      Object param = new Long(aChildProcessId);
      return (List) query(IAeCoordinationSQLKeys.LIST_COORDINATORS_FOR_PID, param, COORDINATION_DETAIL_LIST_RESULT_SET_HANDLER);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getParticipantDetail(long)
    */
   public List getParticipantDetail(long aParentProcessId) throws AeStorageException
   {
      Object param = new Long(aParentProcessId);
      return (List) query(IAeCoordinationSQLKeys.LIST_PARTICIPANTS_FOR_PID, param, COORDINATION_DETAIL_LIST_RESULT_SET_HANDLER);
   }

   /**
    * @return Resultset handler to create IAeCoordinating object.
    */
   protected ResultSetHandler createCoordinatingResultSetHandler()
   {
      return new AeSQLCoordinatingResultSetHandler(getCoordinationManager());
   }

   /**
    * @return Resultset handler to create a list of IAeCoordinating objects.
    */   
   protected ResultSetHandler createCoordinatingListResultSetHandler()
   {
      return new AeSQLCoordinatingListResultSetHandler(getCoordinationManager());
   }

   /**
    * @return Returns the coordinatingResultSetHandler.
    */
   protected ResultSetHandler getCoordinatingResultSetHandler()
   {
      if (mCoordinatingResultSetHandler == null)
      {
         setCoordinatingResultSetHandler(createCoordinatingResultSetHandler());
      }
      return mCoordinatingResultSetHandler;
   }

   /**
    * @param aCoordinatingResultSetHandler The coordinatingResultSetHandler to set.
    */
   protected void setCoordinatingResultSetHandler(ResultSetHandler aCoordinatingResultSetHandler)
   {
      mCoordinatingResultSetHandler = aCoordinatingResultSetHandler;
   }

   /**
    * @return Returns the coordinatingListResultSetHandler.
    */
   protected ResultSetHandler getCoordinatingListResultSetHandler()
   {
      if (mCoordinatingListResultSetHandler == null)
      {
         setCoordinatingListResultSetHandler(createCoordinatingListResultSetHandler());
      }
      return mCoordinatingListResultSetHandler;
   }

   /**
    * @param aCoordinatingListResultSetHandler The coordinatingListResultSetHandler to set.
    */
   protected void setCoordinatingListResultSetHandler(ResultSetHandler aCoordinatingListResultSetHandler)
   {
      mCoordinatingListResultSetHandler = aCoordinatingListResultSetHandler;
   }

   /**
    * @return Returns the coordinationManager.
    */
   protected IAeCoordinationManager getCoordinationManager()
   {
      return mCoordinationManager;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#setCoordinationManager(org.activebpel.rt.bpel.coord.IAeCoordinationManager)
    */
   public void setCoordinationManager(IAeCoordinationManager aManager)
   {
      mCoordinationManager = aManager;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getNextCoordinationId()
    */
   public String getNextCoordinationId() throws AeStorageException
   {
      return Long.toString(AeCounter.COORDINATION_ID_COUNTER.getNextValue());
   }
}
