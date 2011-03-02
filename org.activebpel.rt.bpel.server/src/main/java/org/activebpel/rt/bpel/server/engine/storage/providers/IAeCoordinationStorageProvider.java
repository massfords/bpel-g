// $Header$
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.providers;

import java.util.List;

import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * A coordination storage delegate. This interface defines methods that the delegating coordination storage
 * will call in order to store/read date in the underlying database.
 */
public interface IAeCoordinationStorageProvider extends IAeStorageProvider
{
   /**
    * Gets the next Coordination ID.
    * 
    * @throws AeStorageException
    */
   public String getNextCoordinationId() throws AeStorageException;

   /**
    * Sets the Coordination Manager.
    * 
    * @param aManager
    */
   public void setCoordinationManager(IAeCoordinationManager aManager);

   /**
    * Inserts a Context into the database.
    * 
    * @param aState
    * @param aRole
    * @param aIdentifier
    * @param aCoordinationType
    * @param aProcessId
    * @param aLocationPath
    * @param aContextDocument
    * @throws AeStorageException
    */
   public void insertContext(String aState, int aRole, String aIdentifier,
         String aCoordinationType, long aProcessId, String aLocationPath, AeFastDocument aContextDocument)
         throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getCoordination(java.lang.String, long)
    */
   public IAeCoordinating getCoordination(String aCoordinationId, long aProcessId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getCoordinationsByProcessId(long)
    */
   public List getCoordinationsByProcessId(long aProcessId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getCoordinations(java.lang.String)
    */
   public List getCoordinations(String aCoordinationId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#updateCoordinationState(org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId, java.lang.String)
    */
   public void updateCoordinationState(AePersistentCoordinationId aCoordinationId, String aState)
         throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#updateCoordinationContext(org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId, org.activebpel.rt.bpel.server.coord.AeCoordinationContext)
    */
   public void updateCoordinationContext(AePersistentCoordinationId aCoordinationId,
         AeFastDocument aContextDocument) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getCoordinatorDetail(long)
    */
   public List getCoordinatorDetail(long aChildProcessId) throws AeStorageException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeCoordinationStorage#getParticipantDetail(long)
    */
   public List getParticipantDetail(long aParentProcessId) throws AeStorageException;
}
