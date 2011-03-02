//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/IAeCoordinationManager.java,v 1.6 2008/03/28 01:38:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeManager;


/**
 * Interface for a coordination manager.
 */
public interface IAeCoordinationManager extends IAeManager
{
   
   /**
    * Creates a coordination context.
    * @param aCtxRequest
    * @return Create context response.
    * @throws AeCoordinationException
    */
   public IAeCreateContextResponse createCoordinationContext(IAeCreateContextRequest aCtxRequest) throws AeCoordinationException;
   
   /**
    * Registers the given context for coordination.
    * @param aRegRequest registration request.
    * @return registration response.
    * @throws AeCoordinationException
    */
   public IAeRegistrationResponse register(IAeRegistrationRequest aRegRequest) throws AeCoordinationException;
   
   /**
    * Returns the coordination context given the coordination id, or returns null if not found.
    * @param aCoordinationId
    * @return coordination context if found.
    * @throws AeCoordinationNotFoundException
    */
   public IAeCoordinationContext getContext(String aCoordinationId) throws AeCoordinationNotFoundException;
   
   /**
    * Returns a coordinator matching the coordination id.
    * @param aCoordinationId
    * @return the coordinator if found or null otherwise.
    */
   public IAeCoordinator getCoordinator(String aCoordinationId) throws AeCoordinationNotFoundException;
   
   /**
    * Returns a participant matching the coordination id.
    * @param aCoordinationId
    * @return the participant if found or null otherwise.
    */
   public IAeParticipant getParticipant(String aCoordinationId) throws AeCoordinationNotFoundException;

   /**
    * Returns the parent (coordinator) coordination detail given a child (participant) process id.
    * @param aChildProcessId
    * @throws AeCoordinationNotFoundException
    */
   public AeCoordinationDetail getCoordinatorDetail(long aChildProcessId) throws AeCoordinationNotFoundException;
   
   /**
    * Returns a list of participants (children) given parent process (coordinator) id.
    * @param aParentProcessId
    * @return list containing AeCoordinationDetail objects
    * @throws AeCoordinationNotFoundException
    */
   public List getParticipantDetail(long aParentProcessId) throws AeCoordinationNotFoundException;

   /**
    * Sends compensate signal from coordinator to participant
    * @param aCoordinationId
    * @throws AeCoordinationException 
    */
   public void compensate(String aCoordinationId) throws AeCoordinationException;

   /**
    * Cancels a coordination.
    * @param aCoordinationId
    * @throws AeCoordinationException 
    */
   public void cancel(String aCoordinationId) throws AeCoordinationException;

   /**
    * Callback to indicate that the compensation of a coordination has completed.
    * @param aCoordinationId
    * @param aFault optional fault indicates failure completion.
    * @throws AeBusinessProcessException
    */
   public void compensationCompleted(String aCoordinationId, IAeFault aFault) throws AeBusinessProcessException;
}
