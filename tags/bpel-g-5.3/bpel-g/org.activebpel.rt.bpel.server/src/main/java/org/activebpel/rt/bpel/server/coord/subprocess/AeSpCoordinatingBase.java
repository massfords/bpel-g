//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/AeSpCoordinatingBase.java,v 1.2 2008/03/28 01:44:27 mford Exp $
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
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.coord.AeCoordinatingBase;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionException;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager;
import org.activebpel.wsio.AeMessageAcknowledgeCallbackAdapter;
import org.activebpel.wsio.AeMessageAcknowledgeException;


/**
 * Base class for subprocess type coordinated activities.
 */
public abstract class AeSpCoordinatingBase extends AeCoordinatingBase
{
   /** process id of participant (or coordinator) where the messages should be sent to. */
   private long mDestinationProcessId = 0;

   /**
    * Default ctor.
    * @param aContext coordination context.
    * @param aCoordinationManager
    */
   public AeSpCoordinatingBase(IAeCoordinationContext aContext, IAeCoordinationManagerInternal aCoordinationManager)
   {
      super(aContext, aCoordinationManager);
   }

   /**
    * Convenience method to create a message given a message 'template'.
    * @param aMessage message template.
    * @return message with the target (destination) pid and location path.
    */
   protected IAeSpProtocolMessage createMessage(IAeProtocolMessage aMessage)
   {
      return createMessage(aMessage, null);
   }   
   
   /**
    * Convenience method to create a message given a message 'template'.
    * @param aMessage message template.
    * @param aFault fault.
    * @return message with the target (destination) pid and location path.
    */   
   protected IAeSpProtocolMessage createMessage(IAeProtocolMessage aMessage, IAeFault aFault)
   {
      IAeSpProtocolMessage msg = new AeSpProtocolMessage(aMessage.getSignal(), 
                  getCoordinationId(), aFault,  getMessageDestinationProcessId(), getMessageDestinationLocationPath(), IAeProcessManager.NULL_JOURNAL_ID, getProcessId());
      return msg;
   }

   /**
    * Returns the process id for which a message should be sent to.
    * This method returns the process id of the participant (subprocess).
    * @return process id of the targeted process.
    */
   protected  long getMessageDestinationProcessId()
   {
      if (mDestinationProcessId == 0)
      {
         try
         {
            mDestinationProcessId = 
               Long.parseLong(getCoordinationContext().getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_PROCESS_ID));
         }
         catch(Exception e)
         {
            // ignore
            AeException.logError(e,e.getMessage());
         }
      }
      return mDestinationProcessId;
      
   }

   /**
    * Returns the location path for which a message should be sent to. This method returns
    * the path for the participant (subprocess) -  "/process".
    * @return process id of the participant (sub) process.
    */   
   protected String getMessageDestinationLocationPath()
   {
      return getCoordinationContext().getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_LOCATION_PATH);
   }
   
   /**
    * Queues a message to the coordination manager. We'll use a transaction here 
    * to journal that we've sent the message, change our state, and also journal 
    * the receipt of the message on behalf of the coordinator/participant in case 
    * there's a failure and we need to recover. 
    * @param aMessage message sent.
    * @param aDispatch if true, dispatches the message.
    */
   protected void queueCoordinationMessage(IAeSpProtocolMessage aMessage, boolean aViaProcessExeQueue)
         throws AeCoordinationException
   {
      changeStateNoPersist(aMessage);
      
      long journalId = IAeProcessManager.NULL_JOURNAL_ID;
      
      try
      {
         AeTransactionManager.getInstance().begin();
   
         // persist current state info
         getCoordinationManager().persistState(this);
         // journal that the participant received the message
         journalId = getCoordinationManager().journalCoordinationQueueMessageReceived(aMessage.getProcessId(), aMessage);
         // commit
         AeTransactionManager.getInstance().commit();
      }
      catch(Throwable t)
      {
         try
         {
            AeTransactionManager.getInstance().rollback();
         }
         catch (AeTransactionException e)
         {
         }
         throw new AeCoordinationException(t);
      }
      
      // dispatch message
      aMessage.setJournalId(journalId);
      dispatchMessage(aMessage, aViaProcessExeQueue);
   }
   
   /**
    * Callback object that persists the state of the coordination when it
    * receives the callback. This ensures that the state is persisted within
    * the transaction established by the coordination manager or other code
    * that is being invoked. 
    */
   protected class AePersistCoordinationStateCallback extends AeMessageAcknowledgeCallbackAdapter
   {
      /**
       * @see org.activebpel.wsio.IAeMessageAcknowledgeCallback#onAcknowledge(java.lang.String)
       */
      public void onAcknowledge(String aMessageId)
            throws AeMessageAcknowledgeException
      {
         try
         {
            getCoordinationManager().persistState(AeSpCoordinatingBase.this);
         }
         catch (AeCoordinationException e)
         {
            throw new AeMessageAcknowledgeException(e);
         }
      }
   }
}

