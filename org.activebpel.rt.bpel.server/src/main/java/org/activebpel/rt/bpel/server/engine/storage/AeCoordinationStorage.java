// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeCoordinationStorage.java,v 1.4 2007/02/06 14:39:59 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage;

import java.util.List;

import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.coord.AeCoordinationContext;
import org.activebpel.rt.bpel.server.coord.AeCreateContextRequest;
import org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId;
import org.activebpel.rt.bpel.server.coord.IAeCoordinationId;
import org.activebpel.rt.bpel.server.coord.subprocess.IAeSpCoordinating;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeCoordinationStorageProvider;
import org.activebpel.rt.util.AeUtil;

/**
 * A delegating implementation of a coordination storage.  This class delegates all of the database
 * calls to an instance of IAeCoordinationStorageProvider.  The purpose of this class is to encapsulate
 * storage 'logic' so that it can be shared across multiple storage implementations (such as SQL
 * and Tamino).
 */
public class AeCoordinationStorage extends AeAbstractStorage implements IAeCoordinationStorage
{
   /**
    * Default constructor that takes the queue storage provider to use.
    * 
    * @param aProvider
    */
   public AeCoordinationStorage(IAeCoordinationStorageProvider aProvider)
   {
      super(aProvider);
   }

   /**
    * Convenience method to get the storage provider cast to a coordination storage provider.
    */
   protected IAeCoordinationStorageProvider getCoordinationStorageProvider()
   {
      return (IAeCoordinationStorageProvider) getProvider();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#setCoordinationManager(org.activebpel.rt.bpel.coord.IAeCoordinationManager)
    */
   public void setCoordinationManager(IAeCoordinationManager aManager)
   {
      getCoordinationStorageProvider().setCoordinationManager(aManager);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#createContext(org.activebpel.rt.bpel.server.coord.AeCreateContextRequest, org.activebpel.rt.bpel.server.coord.IAeCoordinationId, java.lang.String, int)
    */
   public AeCoordinationContext createContext(AeCreateContextRequest aCtxRequest, IAeCoordinationId aCoordinationId, String aState, int aRole) throws AeStorageException
   {
      // primary key for this row (identity/seq)
      String identifier = null;
      // used the coord-id if given, else create a new one.
      if (aCoordinationId != null)
      {
        identifier = aCoordinationId.getIdentifier();
      }
      else
      { 
         // create a new coordination id.
         identifier = getCoordinationStorageProvider().getNextCoordinationId();
      }
      
      String coordType = aCtxRequest.getCoordinationType();
      long pid = aCtxRequest.getProcessId();
      String locPath = aCtxRequest.getLocationPath();
      IAeCoordinationId coordId = new AePersistentCoordinationId(pid, identifier);
      
      AeCoordinationContext ctx = new AeCoordinationContext(coordId);
      // set the coordination type. In this case, its AE subprocess.
      ctx.setProperty(IAeCoordinating.WSCOORD_TYPE, coordType);
      // set the supported protocol. In this case, it is a version of AESP_PARTICIPANT_COMPLETION_PROTOCOL.
      ctx.setProperty(IAeCoordinating.WSCOORD_PROTOCOL, IAeSpCoordinating.AESP_PARTICIPANT_COMPLETION_PROTOCOL);
      // pid
      ctx.setProperty(IAeCoordinating.AE_COORD_PID, String.valueOf(pid));
      // location path of invoke (or scope).
      ctx.setProperty(IAeCoordinating.AE_COORD_LOCATION_PATH, locPath);
      
      if (AeUtil.notNullOrEmpty(aCtxRequest.getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_PROCESS_ID) ) )
      {
         ctx.setProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_PROCESS_ID, aCtxRequest.getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_PROCESS_ID) );
      }
      if (AeUtil.notNullOrEmpty(aCtxRequest.getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_LOCATION_PATH) ) )
      {
         ctx.setProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_LOCATION_PATH, aCtxRequest.getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_LOCATION_PATH) );
      }      

      AeFastDocument contextDoc = AeStorageUtil.createCoordinationContextDocument(ctx);

      getCoordinationStorageProvider().insertContext(aState, aRole, identifier, coordType, pid, locPath, contextDoc);      
      
      return ctx;      
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getCoordination(java.lang.String, long)
    */
   public IAeCoordinating getCoordination(String aCoordinationId, long aProcessId) throws AeStorageException
   {
      return getCoordinationStorageProvider().getCoordination(aCoordinationId, aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getCoordinationsByProcessId(long)
    */
   public List getCoordinationsByProcessId(long aProcessId) throws AeStorageException
   {
      return getCoordinationStorageProvider().getCoordinationsByProcessId(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getCoordinations(java.lang.String)
    */
   public List getCoordinations(String aCoordinationId) throws AeStorageException
   {
      return getCoordinationStorageProvider().getCoordinations(aCoordinationId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#updateCoordinationState(org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId, java.lang.String)
    */
   public void updateCoordinationState(AePersistentCoordinationId aCoordinationId, String aState) throws AeStorageException
   {
      getCoordinationStorageProvider().updateCoordinationState(aCoordinationId, aState);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#updateCoordinationContext(org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId, org.activebpel.rt.bpel.server.coord.AeCoordinationContext)
    */
   public void updateCoordinationContext(AePersistentCoordinationId aCoordinationId,
         AeCoordinationContext aContext) throws AeStorageException
   {
      AeFastDocument contextDoc = AeStorageUtil.createCoordinationContextDocument(aContext);
      
      getCoordinationStorageProvider().updateCoordinationContext(aCoordinationId, contextDoc);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getCoordinatorDetail(long)
    */
   public List getCoordinatorDetail(long aChildProcessId) throws AeStorageException
   {
      return getCoordinationStorageProvider().getCoordinatorDetail(aChildProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getParticipantDetail(long)
    */
   public List getParticipantDetail(long aParentProcessId) throws AeStorageException
   {
      return getCoordinationStorageProvider().getParticipantDetail(aParentProcessId);
   }
}
