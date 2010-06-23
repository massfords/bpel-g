//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/AeRecoveryCoordinationManager.java,v 1.2 2008/04/02 01:35:56 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery; 

import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.AeCoordinationNotFoundException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.coord.IAeCoordinator;
import org.activebpel.rt.bpel.coord.IAeCreateContextRequest;
import org.activebpel.rt.bpel.coord.IAeCreateContextResponse;
import org.activebpel.rt.bpel.coord.IAeParticipant;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.coord.IAeRegistrationRequest;
import org.activebpel.rt.bpel.coord.IAeRegistrationResponse;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.server.engine.recovery.recovered.coord.AeRecoveredCancelItem;
import org.activebpel.rt.bpel.server.engine.recovery.recovered.coord.AeRecoveredCompensateItem;
import org.activebpel.rt.bpel.server.engine.recovery.recovered.coord.AeRecoveredCompensateOrCancelItem;
import org.activebpel.rt.bpel.server.engine.recovery.test.AeDelegatingManager;

/**
 * Recovery version of the coordination manager that creates recovered items
 * in order to replay them through the coordination manager after the process
 * has been completely recovered.
 */
public class AeRecoveryCoordinationManager extends AeDelegatingManager implements IAeRecoveryCoordinationManager
{
   /** The set of alarm and queue manager items generated during recovery. */
   private IAeRecoveredItemsSet mRecoveredItemsSet;
   private IAeBusinessProcessEngineInternal mEngine;
   
   /**
    * Ctor
    * @param aBaseManager
    */
   public AeRecoveryCoordinationManager(IAeCoordinationManagerInternal aBaseManager)
   {
      super(aBaseManager);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#compensateOrCancel(java.lang.String)
    */
   public void compensateOrCancel(String aCoordinationId)
   {
      try
      {
         getRecoveredItemsSet().addRecoveredItem(new AeRecoveredCompensateOrCancelItem(aCoordinationId));
      }
      catch (AeRecoveryConflictingRequestException e)
      {
      }
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#cancel(java.lang.String)
    */
   public void cancel(String aCoordinationId) throws AeCoordinationException
   {
      try
      {
         getRecoveredItemsSet().addRecoveredItem(new AeRecoveredCancelItem(aCoordinationId));
      }
      catch (AeRecoveryConflictingRequestException e)
      {
      }
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#compensate(java.lang.String)
    */
   public void compensate(String aCoordinationId)
         throws AeCoordinationException
   {
      try
      {
         getRecoveredItemsSet().addRecoveredItem(new AeRecoveredCompensateItem(aCoordinationId));
      }
      catch (AeRecoveryConflictingRequestException e)
      {
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#compensationCompleted(java.lang.String, org.activebpel.rt.bpel.IAeFault)
    */
   public void compensationCompleted(String aCoordinationId, IAeFault aFault)
         throws AeBusinessProcessException
   {
      getDelegate().compensationCompleted(aCoordinationId, aFault);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveryAwareManager#setInvokeTransmittedEntries(java.util.List)
    */
   public void setInvokeTransmittedEntries(List aInvokeTransmittedEntries)
   {
      // don't care
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveryAwareManager#setRecoveredItemsSet(org.activebpel.rt.bpel.server.engine.recovery.IAeRecoveredItemsSet)
    */
   public void setRecoveredItemsSet(IAeRecoveredItemsSet aRecoveredItemsSet)
   {
      mRecoveredItemsSet = aRecoveredItemsSet;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#dispatch(org.activebpel.rt.bpel.coord.IAeProtocolMessage, boolean)
    */
   public void dispatch(IAeProtocolMessage aMessage, boolean aViaProcessExeQueue)
   {
      getDelegate().dispatch(aMessage, aViaProcessExeQueue);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#journalCoordinationQueueMessageReceived(long, org.activebpel.rt.bpel.coord.IAeProtocolMessage)
    */
   public long journalCoordinationQueueMessageReceived(long aProcessId,
         IAeProtocolMessage aMessage)
   {
      return getDelegate().journalCoordinationQueueMessageReceived(aProcessId, aMessage);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#journalNotifyCoordinatorsParticipantClosed(long)
    */
   public long journalNotifyCoordinatorsParticipantClosed(long aProcessId)
   {
      return getDelegate().journalNotifyCoordinatorsParticipantClosed(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#notifyCoordinatorsParticipantClosed(long, long)
    */
   public void notifyCoordinatorsParticipantClosed(long aProcessId, long aJournalId)
   {
      getDelegate().notifyCoordinatorsParticipantClosed(aProcessId, aJournalId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#onProcessCompleted(long, org.activebpel.rt.bpel.IAeFault, boolean)
    */
   public void onProcessCompleted(long aProcessId, IAeFault aFaultObject,
         boolean aNormalCompletion)
   {
      getDelegate().onProcessCompleted(aProcessId, aFaultObject, aNormalCompletion);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#persistState(org.activebpel.rt.bpel.coord.IAeCoordinating)
    */
   public void persistState(IAeCoordinating aCoordinating)
         throws AeCoordinationException
   {
      getDelegate().persistState(aCoordinating);
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#createCoordinationContext(org.activebpel.rt.bpel.coord.IAeCreateContextRequest)
    */
   public IAeCreateContextResponse createCoordinationContext(
         IAeCreateContextRequest aCtxRequest) throws AeCoordinationException
   {
      return getDelegate().createCoordinationContext(aCtxRequest);
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#getContext(java.lang.String)
    */
   public IAeCoordinationContext getContext(String aCoordinationId)
         throws AeCoordinationNotFoundException
   {
      return getDelegate().getContext(aCoordinationId);
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#getCoordinator(java.lang.String)
    */
   public IAeCoordinator getCoordinator(String aCoordinationId)
         throws AeCoordinationNotFoundException
   {
      return getDelegate().getCoordinator(aCoordinationId);
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#getCoordinatorDetail(long)
    */
   public AeCoordinationDetail getCoordinatorDetail(long aChildProcessId)
         throws AeCoordinationNotFoundException
   {
      return getDelegate().getCoordinatorDetail(aChildProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#getParticipant(java.lang.String)
    */
   public IAeParticipant getParticipant(String aCoordinationId)
         throws AeCoordinationNotFoundException
   {
      return getDelegate().getParticipant(aCoordinationId);
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#getParticipantDetail(long)
    */
   public List getParticipantDetail(long aParentProcessId)
         throws AeCoordinationNotFoundException
   {
      return getDelegate().getParticipantDetail(aParentProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#register(org.activebpel.rt.bpel.coord.IAeRegistrationRequest)
    */
   public IAeRegistrationResponse register(IAeRegistrationRequest aRegRequest)
         throws AeCoordinationException
   {
      return getDelegate().register(aRegRequest);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.test.AeDelegatingManager#getEngine()
    */
   public IAeBusinessProcessEngineInternal getEngine()
   {
      return mEngine;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.test.AeDelegatingManager#setEngine(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine)
   {
      mEngine = aEngine;
   }

   /**
    * Getter for the delegate manager
    */
   protected IAeCoordinationManagerInternal getDelegate()
   {
      return (IAeCoordinationManagerInternal) getBaseManager();
   }
   
   /**
    * Setter for the delegate manager
    */
   protected IAeRecoveredItemsSet getRecoveredItemsSet()
   {
      return mRecoveredItemsSet;
   }
}
 