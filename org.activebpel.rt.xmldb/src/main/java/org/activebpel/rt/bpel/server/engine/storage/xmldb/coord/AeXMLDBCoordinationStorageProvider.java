// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/coord/AeXMLDBCoordinationStorageProvider.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb.coord;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeAbstractXMLDBStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorage;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.coord.handlers.AeCoordinatingListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.coord.handlers.AeCoordinatingResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.coord.handlers.AeCoordinationDetailListResponseHandler;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;

/**
 * A XMLDB version of a coordination storage provider.
 */
public class AeXMLDBCoordinationStorageProvider extends AeAbstractXMLDBStorageProvider implements
      IAeCoordinationStorageProvider
{
   /** A XMLDB response handler that returns a list of coordination detail objects. */
   private static final IAeXMLDBResponseHandler COORDINATION_DETAIL_LIST_RESPONSE_HANDLER = new AeCoordinationDetailListResponseHandler();

   /** The prefix into the xmldb config that this storage object uses. */
   protected static final String CONFIG_PREFIX = "CoordinationStorage"; //$NON-NLS-1$

   /** Coordination manager. */
   private IAeCoordinationManager mCoordinationManager;
   /** The next coordination id. */
   private long mNextCoordinationId = System.currentTimeMillis();
   /** The cached coordinating response handler. */
   private IAeXMLDBResponseHandler mCoordinatingResponseHandler;
   /** The cached coordinating list response handler. */
   private IAeXMLDBResponseHandler mCoordinatingListResponseHandler;

   /**
    * Constructs a XMLDB coordination storage delegate with the given XMLDB config.
    */
   public AeXMLDBCoordinationStorageProvider(AeXMLDBConfig aConfig, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, CONFIG_PREFIX, aStorageImpl);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getNextCoordinationId()
    */
   public synchronized String getNextCoordinationId() throws AeStorageException
   {
      return String.valueOf(mNextCoordinationId++);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#setCoordinationManager(org.activebpel.rt.bpel.coord.IAeCoordinationManager)
    */
   public void setCoordinationManager(IAeCoordinationManager aManager)
   {
      mCoordinationManager = aManager;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#insertContext(java.lang.String, int, java.lang.String, java.lang.String, long, java.lang.String, org.activebpel.rt.bpel.impl.fastdom.AeFastDocument)
    */
   public void insertContext(String aState, int aRole, String aIdentifier, String aCoordinationType, long aProcessId, 
                             String aLocationPath, AeFastDocument aContextDocument) throws AeStorageException
   {
      LinkedHashMap params = new LinkedHashMap();
      params.put(IAeCoordinationElements.COORDINATION_TYPE, aCoordinationType);
      params.put(IAeCoordinationElements.COORDINATION_ROLE, new Integer(aRole));
      params.put(IAeCoordinationElements.COORDINATION_ID, aIdentifier);
      params.put(IAeCoordinationElements.STATE, aState);
      params.put(IAeCoordinationElements.PROCESS_ID, new Long(aProcessId));
      params.put(IAeCoordinationElements.LOCATION_PATH, aLocationPath);
      if (aContextDocument == null)
         params.put(IAeCoordinationElements.COORDINATION_DOC, IAeXMLDBStorage.NULL_DOCUMENT);
      else
         params.put(IAeCoordinationElements.COORDINATION_DOC, aContextDocument);
      params.put(IAeCoordinationElements.START_DATE, new AeSchemaDateTime(new Date()));
      params.put(IAeCoordinationElements.MODIFIED_DATE, new AeSchemaDateTime(new Date()));

      // get (tx) connection and insert document and close it (in case the connection was not a tx).
      IAeXMLDBConnection txConn = null; 
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         insertDocument(IAeCoordinationConfigKeys.INSERT_CONTEXT, params, txConn);
      }
      finally
      {
         txConn.close();
      }      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getCoordination(java.lang.String, long)
    */
   public IAeCoordinating getCoordination(String aCoordinationId, long aProcessId) throws AeStorageException
   {
      Object[] params = { aCoordinationId, new Long(aProcessId) };
      IAeXMLDBResponseHandler handler = createCoordinatingResponseHandler();            
      IAeXMLDBConnection txConn = null; 
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         return (IAeCoordinating) query(IAeCoordinationConfigKeys.LOOKUP_COORDINATION, params, handler, txConn);
      }
      finally
      {
         txConn.close();
      }      
      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getCoordinationsByProcessId(long)
    */
   public List getCoordinationsByProcessId(long aProcessId) throws AeStorageException
   {
      Object[] params = { new Long(aProcessId) };
      IAeXMLDBResponseHandler handler = createCoordinatingListResponseHandler();      
      IAeXMLDBConnection txConn = null; 
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         return (List) query(IAeCoordinationConfigKeys.LIST_BY_PROCESS_ID, params, handler, txConn);
      }
      finally
      {
         txConn.close();
      }  
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getCoordinations(java.lang.String)
    */
   public List getCoordinations(String aCoordinationId) throws AeStorageException
   {
      Object[] params = { aCoordinationId };
      IAeXMLDBResponseHandler handler = createCoordinatingListResponseHandler();      
      IAeXMLDBConnection txConn = null; 
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         return (List) query(IAeCoordinationConfigKeys.LIST_BY_COORDINATION_ID, params, handler, txConn);
      }
      finally
      {
         txConn.close();
      }      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#updateCoordinationState(org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId, java.lang.String)
    */
   public void updateCoordinationState(AePersistentCoordinationId aCoordinationId, String aState)
         throws AeStorageException
   {
      Object[] params = {
            aCoordinationId.getIdentifier(),
            new Long(aCoordinationId.getProcessId()),
            aState,
            new AeSchemaDateTime(new Date()).toString()
      };      
            
      IAeXMLDBConnection txConn = null; 
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         updateDocuments(IAeCoordinationConfigKeys.UPDATE_STATE, params, txConn);
      }
      finally
      {
         txConn.close();
      }      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#updateCoordinationContext(org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId, org.activebpel.rt.bpel.impl.fastdom.AeFastDocument)
    */
   public void updateCoordinationContext(AePersistentCoordinationId aCoordinationId,
         AeFastDocument aContextDocument) throws AeStorageException
   {
      Object[] params = new Object[] {
            aCoordinationId.getIdentifier(),
            new Long(aCoordinationId.getProcessId()),
            serializeFastDocument(aContextDocument)
      };      
      
      IAeXMLDBConnection txConn = null; 
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         updateDocuments(IAeCoordinationConfigKeys.UPDATE_CONTEXT, params, txConn);
      }
      finally
      {
         txConn.close();
      }      
      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getCoordinatorDetail(long)
    */
   public List getCoordinatorDetail(long aChildProcessId) throws AeStorageException
   {
      Object[] params = { new Long(aChildProcessId) };
      
      IAeXMLDBConnection txConn = null; 
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         return (List) query(IAeCoordinationConfigKeys.LIST_COORDINATORS_FOR_PID, params,
               COORDINATION_DETAIL_LIST_RESPONSE_HANDLER, txConn);
      }
      finally
      {
         txConn.close();
      }      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider#getParticipantDetail(long)
    */
   public List getParticipantDetail(long aParentProcessId) throws AeStorageException
   {
      Object[] params = { new Long(aParentProcessId) };
      IAeXMLDBConnection txConn = null; 
      try
      {
         txConn = getXMLDBTransactionManagerConnection(); 
         return (List) query(IAeCoordinationConfigKeys.LIST_PARTICIPANTS_FOR_PID, params,
               COORDINATION_DETAIL_LIST_RESPONSE_HANDLER, txConn);
      }
      finally
      {
         txConn.close();
      }      
   }

   /**
    * @return Returns the coordinationManager.
    */
   protected IAeCoordinationManager getCoordinationManager()
   {
      return mCoordinationManager;
   }

   /**
    * @return Resultset handler to create IAeCoordinating object.
    */
   protected IAeXMLDBResponseHandler createCoordinatingResponseHandler()
   {
      return new AeCoordinatingResponseHandler(getCoordinationManager());
   }

   /**
    * @return Resultset handler to create a list of IAeCoordinating objects.
    */   
   protected IAeXMLDBResponseHandler createCoordinatingListResponseHandler()
   {
      return new AeCoordinatingListResponseHandler(getCoordinationManager());
   }


   /**
    * @return Returns the coordinatingResponseHandler.
    */
   protected IAeXMLDBResponseHandler getCoordinatingResponseHandler()
   {
      return mCoordinatingResponseHandler;
   }

   /**
    * @param aCoordinatingResponseHandler The coordinatingResponseHandler to set.
    */
   protected void setCoordinatingResponseHandler(IAeXMLDBResponseHandler aCoordinatingResponseHandler)
   {
      mCoordinatingResponseHandler = aCoordinatingResponseHandler;
   }

   /**
    * @return Returns the coordinatingListResponseHandler.
    */
   protected IAeXMLDBResponseHandler getCoordinatingListResponseHandler()
   {
      return mCoordinatingListResponseHandler;
   }

   /**
    * @param aCoordinatingListResponseHandler The coordinatingListResponseHandler to set.
    */
   protected void setCoordinatingListResponseHandler(IAeXMLDBResponseHandler aCoordinatingListResponseHandler)
   {
      mCoordinatingListResponseHandler = aCoordinatingListResponseHandler;
   }
}
