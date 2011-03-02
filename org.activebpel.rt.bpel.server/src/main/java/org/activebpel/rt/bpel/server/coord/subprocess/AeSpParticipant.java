//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/AeSpParticipant.java,v 1.13 2008/03/28 01:45:13 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess;

import commonj.work.Work;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
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
import org.activebpel.work.AeAbstractWork;
import org.activebpel.wsio.AeMessageAcknowledgeException;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Implementation of a participant, based on the AE subprocess coordination protocol.
 */
public class AeSpParticipant extends AeSpCoordinatingBase implements IAeSpParticipant , IAeSpCoordinating
{
   /**
    * Constructs a participant.
    * @param aContext
    * @param aCoordinationManager
    */
   public AeSpParticipant(IAeCoordinationContext aContext, IAeCoordinationManagerInternal aCoordinationManager)
   {
      super(aContext, aCoordinationManager);
   }

   /**
    * Overrides method to return a state transition table based on the BPEL Business Activity protocol.
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
    * Overrides method to handle protocol messages.
    * @see org.activebpel.rt.bpel.coord.IAeCoordinating#queueReceiveMessage(org.activebpel.rt.bpel.coord.IAeProtocolMessage)
    */
   public void queueReceiveMessage(IAeProtocolMessage aMessage) throws AeCoordinationException
   {
      IAeSpProtocolMessage spMessage = (IAeSpProtocolMessage) aMessage;
      
      IAeProtocolState currState = getState();
      if (AeSpCoordinationMessages.COMPENSATE_OR_CANCEL.getSignal().equals(spMessage.getSignal()))
      {
         // translate this message based on current state. E.g map to COMPENSATE if process has COMPLETED
         spMessage = mapCompensateOrCancelMessage(spMessage);
      }

      IAeProtocolState nextState = getTable().getNextState(currState, spMessage);

      if (nextState == null)
      {
         // invalid state change!for now, just return.
         Object params[] = { String.valueOf(currState), String.valueOf(spMessage) };
         AeException.logWarning(AeMessages.format("AeCoordinatingBase.INVALID_STATE_CHANGE",params)); //$NON-NLS-1$
         return;
      }
      else if (nextState.equals(currState))
      {
         // state has not changed - but we may want to handle this case. for now, just return.
         return;
      }
      
      if (AeSpCoordinationMessages.CANCEL.equalsSignal(spMessage))
      {
         onMessageCancel(spMessage);
      }
      else if (AeSpCoordinationMessages.CLOSE.equalsSignal(spMessage))
      {
         onMessageClose(spMessage);
      }
      else if (AeSpCoordinationMessages.COMPENSATE.equalsSignal(spMessage))
      {
         onMessageCompensate(spMessage);
      }
      else if (AeSpCoordinationMessages.FORGET.equalsSignal(spMessage))
      {
         onMessageForget(spMessage);
      }
      else
      {
         // should not get here based on the state table.
         AeException.info(AeMessages.format("AeCoordinatingBase.UNKNOWN_MESSAGE", String.valueOf(aMessage)));//$NON-NLS-1$
      }
   }

   /**
    * Maps a COMPENSTATE_OR_CANCEL message to either CANCEL, COMPENSATE, FORGET or CLOSE message.
    * @param aMessage
    */
   protected IAeSpProtocolMessage mapCompensateOrCancelMessage(IAeSpProtocolMessage aMessage)
   {
      IAeSpProtocolMessage rVal = null;
      IAeProtocolState currState = getState();

      if (AeSpCoordinationStates.ACTIVE.equals(currState))
      {
         rVal = AeSpCoordinationMessages.create(AeSpCoordinationMessages.CANCEL.getSignal(), aMessage);
      }
      else if (AeSpCoordinationStates.FAULTED_ACTIVE.equals(currState) || AeSpCoordinationStates.FAULTED_COMPENSATING.equals(currState))
      {
         rVal = AeSpCoordinationMessages.create(AeSpCoordinationMessages.FORGET.getSignal(), aMessage);
      }
      else if (AeSpCoordinationStates.COMPLETED.equals(currState))
      {
         rVal = AeSpCoordinationMessages.create( AeSpCoordinationMessages.COMPENSATE.getSignal(), aMessage);
      }
      else
      {
         rVal = aMessage;
      }
      return rVal;
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinating#onProcessComplete(org.activebpel.rt.bpel.IAeFault, boolean)
    */
   public void onProcessComplete(IAeFault aFault, boolean aNormalCompletion)
   {
      IAeProtocolState currState = getState();

      if (AeSpCoordinationStates.ENDED.equals(currState) || AeSpCoordinationStates.CANCELING.equals(currState))
      {
         // If the subprocess is currently cancelling or has ended, then ignore this message.
         // (ref: AeAxisUnitTest::SimpleSubprocessInvokeEarlyTermination).
         return;
      }

      IAeSpProtocolMessage msg = null;
      if (aFault != null && AeSpCoordinationStates.COMPENSATING.equals(currState) )
      {
         // faulted during compensation.
         msg = createMessage(AeSpCoordinationMessages.FAULTED_COMPENSATING, aFault);
      }
      else if (aFault != null)
      {
         // faulted in active (or other ?)state
         msg = createMessage(AeSpCoordinationMessages.FAULTED_ACTIVE, aFault);
      }
      else if (aNormalCompletion)
      {
         // process completed normally and eligible for compensation.
         msg = createMessage(AeSpCoordinationMessages.COMPLETED, null);
      }
      else
      {
         // process completed abnormally - so, it should not be eligible for compensation.
         // (simply exit from the coordination).
         msg = createMessage(AeSpCoordinationMessages.EXITED, null);
      }

      // change to next state and send off message.
      try
      {
         queueCoordinationMessage(msg, false);      
      }
      catch (AeCoordinationException e)
      {
         AeException.logWarning(e.getMessage());
      };
   }
   
   /**
    * Called when the participant compensation is complete. This callback is made
    * by the ParticipantCompensator.
    */
   public void compensationComplete() throws AeBusinessProcessException
   {
      // if the coordination has issued a terminate/cancel command, then ignore the callback.
      if (ignoreCompensationCallback())
      {
         return;
      }

      // send message to coordinator: Compensated with out errors
      IAeSpProtocolMessage msg = createMessage(AeSpCoordinationMessages.COMPENSATED, null);

      // change to next state (compensated) and send off message.
      try
      {
         queueCoordinationMessage(msg, false);      
      }
      catch (AeCoordinationException e)
      {
         AeException.logError(e, e.getMessage());
      }
   }

   /**
    * Called when the participant compensation was interrupted by a fault. This callback is made
    * by the ParticipantCompensator.
    * @param aFault
    */
   public void compensationCompleteWithFault(IAeFault aFault) throws AeBusinessProcessException
   {
      // if the coordination has issued a termincate/cancel command, then ignore the callback.
      if (ignoreCompensationCallback())
      {
         return;
      }

      // send message to coordinator: Faulted while compensating
      IAeSpProtocolMessage msg = createMessage(AeSpCoordinationMessages.FAULTED_COMPENSATING, aFault);

      // change to next state (faulted-compensating) and send off message.
      try
      {
         queueCoordinationMessage(msg, false);
      }
      catch (AeCoordinationException e)
      {
         AeException.logError(e, e.getMessage());
      }
   }

   /**
    * Returns true if the compensation callback should be ignored.
    */
   protected boolean ignoreCompensationCallback()
   {
      // ignore if the current state is cancelling.
      if ( AeSpCoordinationStates.CANCELING.equals( getState() ) )
      {
         // the main process (coordinator) may have initiated a cancel/terminate.
         // (e.g. when compensationHandler is terminated). In this case, we ignore
         // the subprocess (participant) compensation callback.
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Handle CANCEL message from coordinator.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageCancel(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      IAeProtocolState currState = getState();
      if (AeSpCoordinationStates.COMPLETED.equals(currState)
            || AeSpCoordinationStates.ENDED.equals(currState)
            || AeSpCoordinationStates.CANCELING.equals(currState))
      {
         // BPEL 1.1 specs, Appendix C (page 115), section I:
         // In the case there is a race condition between between COMPLETED/ENDED/CANCELING state and a CANCEL
         // signal from the coordinator, COMPLETED/ENDED/CANCELING state wins.
         return;
      }

      // cancel compensation handler instead of process?
      boolean cancelCompensationHandler = AeSpCoordinationStates.COMPENSATING.equals( getState() );

      changeStateNoPersist(aMessage); // should transition to 'canceling'.
      // next message to be sent the coordinator: canceled
      IAeSpProtocolMessage msg = createMessage(AeSpCoordinationMessages.CANCELED, null);
      // Terminate process and dispatch message on a separate thread.
      Work work = new AeProcessTerminateWork(this, msg, cancelCompensationHandler);
      try
      {
         AeEngineFactory.schedule(getProcessId(), work);
      }
      catch(Exception e)
      {
         AeException.logError(e);
      }
   }

   /**
    * Handle CLOSE message.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageClose(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      // should transition to 'closing'.
      changeStateNoPersist(aMessage);
      
      IAeSpProtocolMessage msg = createMessage(AeSpCoordinationMessages.CLOSED, null);
      IAeMessageAcknowledgeCallback callback = new AeMessageClosedAcknowledgement(msg);
      
      IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
      engine.getProcessCoordination().subprocessCoordinationEnded( getProcessId(), IAeProcessManager.NULL_JOURNAL_ID, callback, aMessage.getJournalId() );

      // change to next state and send off message.
      // send message to coordinator: closed
      dispatchMessage(msg, false);
   }
   
   /**
    * Special ack for the closed message. Persists the state of the participant
    * and then journals a message to be sent to the coordination manager indicating
    * that it is closed.
    */
   private class AeMessageClosedAcknowledgement extends AePersistCoordinationStateCallback
   {
      /** message we want to journal */
      private IAeSpProtocolMessage mMessage;
      
      /**
       * Ctor
       * @param aMessage
       */
      public AeMessageClosedAcknowledgement(IAeSpProtocolMessage aMessage)
      {
         setMessage(aMessage);
      }

      /**
       * @see org.activebpel.wsio.AeMessageAcknowledgeCallbackAdapter#onAcknowledge(java.lang.String)
       */
      public void onAcknowledge(String aMessageId)
            throws AeMessageAcknowledgeException
      {
         super.onAcknowledge(aMessageId);
         long journalId = getCoordinationManager().journalCoordinationQueueMessageReceived(getMessage().getProcessId(), getMessage());
         getMessage().setJournalId(journalId);
      }

      /**
       * @return the message
       */
      protected IAeSpProtocolMessage getMessage()
      {
         return mMessage;
      }

      /**
       * @param aMessage the message to set
       */
      protected void setMessage(IAeSpProtocolMessage aMessage)
      {
         mMessage = aMessage;
      }
   }
   
   /**
    * Compensates the subprocess.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageCompensate(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      changeStateNoPersist(aMessage); // should transition to 'compensating'.
      // begin compensating the subprocess.
      IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
      IAeMessageAcknowledgeCallback callback = new AePersistCoordinationStateCallback();
      engine.getProcessCoordination().compensateSubProcess( getProcessId(), getCoordinationId(), IAeProcessManager.NULL_JOURNAL_ID, callback, aMessage.getJournalId() );
   }
   
   /**
    * Handles the FORGET mesage by transitioning to ENDED state.
    * @param aMessage
    * @throws AeCoordinationException
    */
   private void onMessageForget(IAeSpProtocolMessage aMessage) throws AeCoordinationException
   {
      changeStateNoPersist(aMessage); // should transition to 'ended'.
      
      IAeMessageAcknowledgeCallback callback = new AePersistCoordinationStateCallback();

      IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
      engine.getProcessCoordination().subprocessCoordinationEnded( getProcessId(), IAeProcessManager.NULL_JOURNAL_ID, callback, aMessage.getJournalId() );
   }

   /**
    * Terminates the process and dispatches the given message.
    */
   protected void cancelProcess(IAeProtocolMessage aMessage, boolean aCancelCompensationHandler)
   {
      try
      {
         changeStateNoPersist(aMessage);
         IAeSpProtocolMessage message = (IAeSpProtocolMessage) aMessage;
         IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
         AeCancelProcessAcknowledge callback = new AeCancelProcessAcknowledge(message);
         if (aCancelCompensationHandler)
         {
            engine.getProcessCoordination().cancelSubProcessCompensation( getProcessId(), IAeProcessManager.NULL_JOURNAL_ID, callback, message.getJournalId() );
         }
         else
         {
            engine.getProcessCoordination().cancelProcess( getProcessId(), IAeProcessManager.NULL_JOURNAL_ID, callback, message.getJournalId() );
         }
      }
      catch(Throwable t)
      {
         AeException.logError(t,t.getMessage());
      }
      // dispatch message
      if (aMessage != null)
      {
         try
         {
            dispatchMessage(aMessage, false);
         }
         catch(Exception e)
         {
            AeException.logError(e,e.getMessage());
         }
      }
   }

   /**
    * Special ack for the cancel operation. Persists the state of the participant
    * and then journals a message to the coordination manager indicating that
    * it has been cancelled.  
    */
   private class AeCancelProcessAcknowledge extends AePersistCoordinationStateCallback
   {
      private IAeSpProtocolMessage mMessage;
      
      public AeCancelProcessAcknowledge(IAeSpProtocolMessage aMessage)
      {
         setMessage(aMessage);
      }
      
      /**
       * @see org.activebpel.rt.bpel.server.coord.subprocess.AeSpCoordinatingBase.AePersistCoordinationStateCallback#onAcknowledge(java.lang.String)
       */
      public void onAcknowledge(String aMessageId)
            throws AeMessageAcknowledgeException
      {
         super.onAcknowledge(aMessageId);
         long journalId = getCoordinationManager().journalCoordinationQueueMessageReceived(getMessage().getProcessId(), getMessage());
         getMessage().setJournalId(journalId);
      }

      /**
       * @return the message
       */
      protected IAeSpProtocolMessage getMessage()
      {
         return mMessage;
      }

      /**
       * @param aMessage the message to set
       */
      protected void setMessage(IAeSpProtocolMessage aMessage)
      {
         mMessage = aMessage;
      }
      
   }
}

/**
 * Wraps the process termination code in a <code>Work</code> impl so that it can be run asynchronously.
 */
class AeProcessTerminateWork extends AeAbstractWork
{
   /** Message to be dispatched. */
   private IAeProtocolMessage mMessage;
   /** Particiant. */
   private AeSpParticipant mParticipant;
   /** Flag to indicate that the process's compensation handler should be canceled. */
   private boolean mCancelCompensationHandler;

   /** Default ctor. */
   public AeProcessTerminateWork(AeSpParticipant aParticipant, IAeProtocolMessage aMessage, boolean aCancelCompensationHandler)
   {
      mParticipant = aParticipant;
      mMessage = aMessage;
      mCancelCompensationHandler = aCancelCompensationHandler;
   }

   /**
    * Overrides method to callback terminateProcess method.
    * Coordination managers's queueReceiveMessages() method.
    * @see java.lang.Runnable#run()
    */
   public void run()
   {
      // callback to terminate and change to next state and send off message.
      mParticipant.cancelProcess(mMessage, mCancelCompensationHandler);
   }
}