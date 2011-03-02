//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/AeSpCoordinator.java,v 1.8 2008/04/03 21:55:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.coord.IAeProtocolState;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.coord.IAeProtocolStateTable;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.wsio.AeMessageAcknowledgeCallbackAdapter;
import org.activebpel.wsio.AeMessageAcknowledgeException;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Implementation of a AE subprocess protcol coordinator. The aesp protocol is loosely based on
 * Business Agreement protocol described in the BPEL-4WS 1.1, appendix C.
 */
public class AeSpCoordinator extends AeSpCoordinatingBase implements IAeSpCoordinator
{
   /**
    * Default constructor.
    */
   public AeSpCoordinator(IAeCoordinationContext aContext, IAeCoordinationManagerInternal aCoordinationManager)
   {
      super(aContext, aCoordinationManager);
   }

   /**
    * Overrides method to create and return an instance of AeSpProtocolTable.
    * @see org.activebpel.rt.bpel.server.coord.AeCoordinatingBase#createProtocolTable()
    */
   protected IAeProtocolStateTable createProtocolTable()
   {
      return new AeSpProtocolTable();
   }


   /**
    * Returns true if the message is a valid message to be dispatched in the current state.
    * @param aMessage
    * @return true if the message is valid for the given state.
    */
   protected boolean canDispatch(IAeProtocolMessage aMessage)
   {
      // cannot dispatch message when in ENDED state.
      return !( AeSpCoordinationStates.ENDED.equals(getState()) );
   }

   /**
    * Overrides method to handle incoming protocol messages.
    * @see org.activebpel.rt.bpel.coord.IAeCoordinating#queueReceiveMessage(org.activebpel.rt.bpel.coord.IAeProtocolMessage)
    */
   public void queueReceiveMessage(IAeProtocolMessage aMessage) throws AeCoordinationException
   {
      // coordinators view of the participant's current state
      IAeProtocolState currState = getState();
      // coordinators view of the participant's next state
      IAeProtocolState nextState = getTable().getNextState(currState, aMessage);

      if (nextState == null)
      {
         // invalid state change!
         Object params[] = { String.valueOf(currState), String.valueOf(aMessage) };
         AeException.logWarning(AeMessages.format("AeCoordinatingBase.INVALID_STATE_CHANGE",params)); //$NON-NLS-1$
         return;
      }
      else if (nextState.equals(currState))
      {
         // state has not changed - but we may want to handle this case.
         // for now, just return.
         return;
      }
      
      IAeSpProtocolMessage msg = (IAeSpProtocolMessage) aMessage;
      
      if (AeSpCoordinationStates.COMPENSATING_OR_CANCELING.equals( getState() ))
      {
         onCompensatingOrCanceling(msg);
      }
      // Handle following messages sent by the participant (subprocess):
      // canceled, completed, closed, compensated, exited, faulted/active, faulted/compensating.
      else if (AeSpCoordinationMessages.CANCELED.equalsSignal(msg))
      {
         onMessageCanceled(msg);
      }
      else if (AeSpCoordinationMessages.COMPLETED.equalsSignal(msg))
      {
         onMessageCompleted(msg);
      }
      else if (AeSpCoordinationMessages.CLOSED.equalsSignal(msg))
      {
         onMessageClosed(msg);
      }
      else if (AeSpCoordinationMessages.COMPENSATED.equalsSignal(msg))
      {
         onMessageCompensated(msg);
      }
      else if (AeSpCoordinationMessages.EXITED.equalsSignal(msg))
      {
         onMessageExited(msg);
      }
      else if (AeSpCoordinationMessages.FAULTED_ACTIVE.equalsSignal(msg))
      {
         onMessageFaultedActive(msg);
      }
      else if (AeSpCoordinationMessages.FAULTED_COMPENSATING.equalsSignal(msg))
      {
         onMessageFaultedCompensating(msg);
      }
      else
      {
         // unknown message - ignore for now.
         AeException.logWarning(AeMessages.format("AeCoordinatingBase.UNKNOWN_MESSAGE",String.valueOf(aMessage))); //$NON-NLS-1$
      }
   }

   /**
    * Callback via the process manager when coordinator process is complete.
    *
    * @param aFaultObject fault object if the process completed with a fault.
    * @param aNormalCompletion indiciates that the process completed normally and is eligible fo compensation.
    */
   public void onProcessComplete(IAeFault aFaultObject, boolean aNormalCompletion)
   {
      // what to do do if the parent process (coordinator) completed or faulted.
      // for now terminate the sub process?

      // curr view of the participant.
      IAeProtocolState currState = getState();
      if (AeSpCoordinationStates.ENDED.equals(currState))
      {
         // participant/subprocess is already done!
         return;
      }
      // create a message to cancel or compensate the sub process.
      else if (AeSpCoordinationStates.ACTIVE.equals(currState))
      {
         // Defect 2268 race condition. Process A completes while Process B is still active
         // or before Process B gets a chance to reliabaly xmt the COMPLETED message to A.
         // For example, a participant (Proc_B) may have completed and indicated this (asynchronously)
         // to the coordinator (Proc_A). In the meantime, Proc_A completes before receiving the
         // COMPLETED msg from Proc_B. At this point Proc_A assumes Proc_B coord state is still ACTIVE.
         //
         // In this case,  we need to signal Proc_B to either CANCEL (if it is still running/ACTIVE)
         // or COMPENSATE (if Proc_B has COMPLETED).
         try
         {
            compensateOrCancel();
         }
         catch(Exception e)
         {
            AeException.logError(e,e.getMessage());
         }
         return;
      }

      IAeSpProtocolMessage msg = null;
      if (AeSpCoordinationStates.COMPLETED.equals(currState))
      {
         msg = createMessage(AeSpCoordinationMessages.CLOSE);
      }
      else if (AeSpCoordinationStates.FAULTED_ACTIVE.equals(currState)
            || AeSpCoordinationStates.FAULTED_COMPENSATING.equals(currState))
      {
         msg = createMessage(AeSpCoordinationMessages.FORGET);
      }

      // change to next state and send off message.
      if (msg != null)
      {
         try
         {
            queueCoordinationMessage(msg, false);
         }
         catch (AeCoordinationException e)
         {
            AeException.logError(e,e.getMessage());
         }
      }
   }

   /**
    * Registers the coordination id with process invoke activity's enclosing scope.
    *
    * @see org.activebpel.rt.bpel.coord.IAeCoordinator#register()
    */
   public void register() throws AeCoordinationException
   {
      // This is already happening in a transaction.
      
      // set the current state to active.
      setState(AeSpCoordinationStates.ACTIVE);

      // add coordination id to enclosing scope.
      IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
      engine.getProcessCoordination().registerCoordinationId( getProcessId(), getLocationPath(), getCoordinationId());
   }

   /**
    * Deregisters the current coordination with the invoke activity's enclosing scope.
    * (The enclosing scope is not complete until all participants (subprocesses) have
    * ended and deregistered.)
    * @param aCallback
    * @param aJournalId
    * @throws AeCoordinationException
    */
   private void deregister(IAeMessageAcknowledgeCallback aCallback, long aJournalId) throws AeCoordinationException
   {
      // remove coordination id from enclosing scope.
      IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
      engine.getProcessCoordination().deregisterCoordinationId( getProcessId(), getLocationPath(), getCoordinationId(), aCallback, aJournalId);
   }

   /**
    * Overrides method to begin the compensation process by dispatching a COMPENSATE message
    * to the participant.
    * @see org.activebpel.rt.bpel.server.coord.subprocess.IAeSpCoordinator#compensate()
    */
   public void compensate() throws AeCoordinationException
   {
      // should a check be made to see if the participant is in COMPLETED state?
      IAeSpProtocolMessage msg = createMessage(AeSpCoordinationMessages.COMPENSATE);
      queueCoordinationMessage(msg, false);
   }
   
   /**
    * Signals the activity under coordination (ie - the participant) to either compensate (if it has
    * completed) or cancel (it if is still running).
    *
    * This method is normally send to participants in 'active' state during fault/comp handler execution.
    */
   public void compensateOrCancel() throws AeCoordinationException
   {
      IAeSpProtocolMessage msg = createMessage(AeSpCoordinationMessages.COMPENSATE_OR_CANCEL); // fire and forget
      // change state and dispatch via (sub) process's execution queue.
      queueCoordinationMessage(msg, true);
   }

   /**
    * Overrides method to signal the participant to cancel.
    * @see org.activebpel.rt.bpel.coord.IAeCoordinator#cancel()
    */
   public void cancel() throws AeCoordinationException
   {
      IAeSpProtocolMessage msg = createMessage(AeSpCoordinationMessages.CANCEL);
      // change state and dispatch via (sub) process's execution queue.
      queueCoordinationMessage(msg, true);
   }

   /**
    * If the participants were told to either cancel or compensate (equivalent to fire and forget),
    * then next expect messag is canceled, closed,  compensated faulted. Since the current
    * state COMPENSATING_OR_CANCELING is fire and forget, change state to ended and deregister
    * coordination.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onCompensatingOrCanceling(IAeSpProtocolMessage aMessage)
         throws AeCoordinationException
   {
      changeStateNoPersist(aMessage);
      deregister(new AePersistCoordinationStateCallback(), aMessage.getJournalId());
   }

   /**
    * Handle the COMPLETED message sent by the participant. On receiving the COMPLETED message,
    * a compensation handler on behalf of the participant is installed into the invoke activity's
    * enclosing scope.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageCompleted(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      // participant has completed and is eligible for compensation.
      // update coordinator's view of the participant state to COMPLETED.
      changeStateNoPersist(aMessage);

      AePersistCoordinationStateCallback callback = new AePersistCoordinationStateCallback();
      
      // install compensation handler.
      IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
      engine.getProcessCoordination().installCompensationHandler( getProcessId(), getLocationPath(), getCoordinationId(), this, callback, aMessage.getJournalId());
   }

   /**
    * Handle the EXITED message sent by the participant.  The exited message indicates that
    * the participant have transitioned to the ENDED state, hence this coordination will be
    * removed (deregistered) from the invoke activity's enclosing scope.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageExited(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      // participant has exited the process.
      // update coordinator's view of the participant state to ENDED.
      changeStateNoPersist(aMessage);
      deregister(new AePersistCoordinationStateCallback(), aMessage.getJournalId());
   }

   /**
    * Handle the CANCLED message sent by the participant.  The cancled message indicates that
    * the participant have transitioned to the ENDED state, hence this coordination will be
    * removed (deregistered) from the invoke activity's enclosing scope.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageCanceled(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      // participant has completed the cancel request.
      // update coordinator's view of the participant state to ENDED.
      changeStateNoPersist(aMessage);
      deregister(new AePersistCoordinationStateCallback(), aMessage.getJournalId());
   }

   /**
    * Handle the CLOSED message sent by the participant.  The closed message indicates that
    * the participant transitioned to the ENDED state, hence this coordination will be
    * removed (deregistered) from the invoke activity's enclosing scope.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageClosed(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      // participant has completed the close request.
      // update coordinator's view of the participant state to ENDED.
      changeStateNoPersist(aMessage);
      
      AeMessageClosedAcknowledgement callback = new AeMessageClosedAcknowledgement(aMessage.getSourceProcessId());
      deregister(callback, aMessage.getJournalId());
      
      getCoordinationManager().notifyCoordinatorsParticipantClosed(aMessage.getSourceProcessId(), callback.getJournalId());
   }
   
   /**
    * Special acknowledgement for the message closed signal. This ack will 
    * persist the state and create a new journal item to notify the coordination
    * manager that the participant has been closed. 
    */
   protected class AeMessageClosedAcknowledgement extends AeMessageAcknowledgeCallbackAdapter
   {
      /** id of the journal entry */
      private long mJournalId;
      /** id for the process */
      private long mSourceProcessId;
      
      /**
       * Ctor
       * @param aSourceProcessId
       */
      public AeMessageClosedAcknowledgement(long aSourceProcessId)
      {
         setSourceProcessId(aSourceProcessId);
      }
      
      /**
       * @see org.activebpel.wsio.AeMessageAcknowledgeCallbackAdapter#onAcknowledge(java.lang.String)
       */
      public void onAcknowledge(String aMessageId) throws AeMessageAcknowledgeException
      {
         try
         {
            getCoordinationManager().persistState(AeSpCoordinator.this);
            long journalId = getCoordinationManager().journalNotifyCoordinatorsParticipantClosed(getSourceProcessId());
            setJournalId(journalId);
         }
         catch (AeCoordinationException e)
         {
            throw new AeMessageAcknowledgeException(e);
         }
      }

      /**
       * @return the journalId
       */
      protected long getJournalId()
      {
         return mJournalId;
      }

      /**
       * @param aJournalId the journalId to set
       */
      protected void setJournalId(long aJournalId)
      {
         mJournalId = aJournalId;
      }

      /**
       * @return the processId
       */
      protected long getSourceProcessId()
      {
         return mSourceProcessId;
      }

      /**
       * @param aProcessId the processId to set
       */
      protected void setSourceProcessId(long aSourceProcessId)
      {
         mSourceProcessId = aSourceProcessId;
      }
      
   }

   /**
    * Handle the COMPENSATED message sent by the participant.  The compensated message indicates that
    * the participant have successfully compensated itself and have transitioned to the ENDED state.
    * The installed compensation handler will be signaled that it completed.
    * This coordination will be removed (deregistered) from the invoke activity's enclosing scope.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageCompensated(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      // participant has completed the compensate request.
      // update coordinator's view of the participant state to ENDED.
      changeStateNoPersist(aMessage);

      // signal the compHandler that it's done.
      IAeMessageAcknowledgeCallback callback = new AePersistCoordinationStateCallback();
      IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
      engine.getProcessCoordination().compensationCompletedCallback( getProcessId(), getLocationPath(), getCoordinationId(), IAeProcessManager.NULL_JOURNAL_ID,  callback, aMessage.getJournalId());
   }

   /**
    * Handle the FAULTED_COMPENSATED message sent by the participant.  This message indicates that
    * the participant faulted while compensating. This method signals the installed compensationHandler
    * that it completedWithFault.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageFaultedCompensating(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      // participant has faulted during compensation.
      // update coordinator's view of the participant state to FAULTED_COMPENSATING.
      changeStateNoPersist(aMessage);
      
      IAeMessageAcknowledgeCallback callback = new AePersistCoordinationStateCallback();

      // signal the compHandler that it 'completed with fault'.
      IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
      engine.getProcessCoordination().compensationCompletedWithFaultCallback( getProcessId(), getLocationPath(), getCoordinationId(), aMessage.getFault(), IAeProcessManager.NULL_JOURNAL_ID, callback, aMessage.getJournalId() );
   }

   /**
    * Handle the FAULTED_ACTIVE message sent by the participant.  This message indicates that
    * the participant have faulted.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageFaultedActive(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      // participant has faulted during normal activity. It is not eligible for compensation!
      // update coordinator's view of the participant state to FAULTED_ACTIVE.
      changeStateNoPersist(aMessage);
      
      IAeMessageAcknowledgeCallback callback = new AePersistCoordinationStateCallback();
      
      //Fault the enclosing scope
      IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
      engine.getProcessCoordination().activityFaulted( getProcessId(), getLocationPath(), getCoordinationId(), aMessage.getFault(), callback, aMessage.getJournalId() );
   }

}
