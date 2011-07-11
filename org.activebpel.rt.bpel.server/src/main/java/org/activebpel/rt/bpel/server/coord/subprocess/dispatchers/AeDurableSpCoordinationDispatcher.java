//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/dispatchers/AeDurableSpCoordinationDispatcher.java,v 1.1 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess.dispatchers; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Dispatches a coordination signal from the coordination manager into the
 * process. This class uses a transaction and journal entries to ensure that
 * the message is reliably delivered and will be replayed during recovery.
 */
public abstract class AeDurableSpCoordinationDispatcher
{
   private IAeProcessManager mProcessManager;
   private long mProcessId;
   private long mJournalId;
   private IAeMessageAcknowledgeCallback mCallback;
   private long mCallbackJournalId;
   private String mLocationPath;
   private String mCoordinationId;
   
   /**
    * @param aProcessManager
    * @param aProcessId
    * @param aJournalId
    * @param aCallback
    * @param aCallbackJournalId
    */
   public AeDurableSpCoordinationDispatcher(IAeProcessManager aProcessManager,
         long aProcessId, long aJournalId,
         IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId,
         String aLocationPath, String aCoordinationId)
   {
      setProcessManager(aProcessManager);
      setProcessId(aProcessId);
      setJournalId(aJournalId);
      setCallback(aCallback);
      setCallbackJournalId(aCallbackJournalId);
      setLocationPath(aLocationPath);
      setCoordinationId(aCoordinationId);
   }

   /**
    * Template method for dispatching the coordination signal.
    * @throws AeCoordinationException
    */
   public void dispatch() throws AeCoordinationException
   {
      IAeBusinessProcessInternal process = null;
      IAeProcessManager processManager = getProcessManager();
      try
      {
         long journalId = IAeProcessManager.NULL_JOURNAL_ID;

         // 1. Lock process. 
         //      Important to do this first since it helps avoid deadlocks where 
         //      there are no db connections left but someone with a db connection is 
         //      trying to get the lock on this process. We'll also need the process 
         //      wrapper lock in order to mark its journal entry as done so we may as well
         //      grab it here even if deadlock weren't an issue. 
         process = (IAeBusinessProcessInternal) processManager.getProcess(getProcessId());

         try
         {
            // 2. create TX
            AeTransactionManager.getInstance().begin();
            
            // 3. create new journal entry for call to process
            //      The recovery engine will replay this journal entry during recovery. This
            //      will target a specific method on the process as opposed to going through
            //      the whole coordination framework again. We're essentially accepting 
            //      responsibility for the delivery of the coordination message into the process
            journalId = journalDispatchBehavior();

            if (getCallback() != null)
            {
               // 4. mark callback journal entry as done
               processManager.journalEntryDone(getProcessId(), getCallbackJournalId());
               
               // 5. create auto-done journal entry to indicate that the message has been transmitted
               //      This ensures that we won't retransmit the message. The concern would be 
               //      the situation where we recover with journal entries as follows:
               //              - journal for queue coordination message
               //              - journal for process behavior
               //      Without the transmitted journal entry, we'll replay the first coordination
               //      message and get an illegal state change on the coordination since the coordination
               //      has already persisted its state as a result of handing the call over to the
               //      process. Recovery should complete normally but it would be best to not
               //      have stacktraces or error messages in the console during recovery.
               // fixme (MF-coord) add new journal item here
   
               // 6. issue acknowledgement to callback (this persists the state of the coord)
               //      This callback enables the caller to include one or more calls as part 
               //      of the transaction established in this method
               getCallback().onAcknowledge(String.valueOf(journalId));
            }
            
            // 7. end TX
            AeTransactionManager.getInstance().commit();
         }
         catch(Throwable t)
         {
            AeTransactionManager.getInstance().rollback();
         }
         
         // synchronize on the process for the in-memory version which requires 
         // synchronization.
         synchronized (process)
         {
            // 8. invoke method on process to do coordination action
            dispatchBehavior(process);
            // 9. mark newly created journal as done
            processManager.journalEntryDone(getProcessId(), journalId);
         }
      }
      catch(Exception e)
      {
         throw new AeCoordinationException(e);
      }
      finally
      {
         // 10. release process
         processManager.releaseProcess(process);
      }   
   }
   
   /**
    * journals the behavior for the message we're dispatching
    */
   protected abstract long journalDispatchBehavior();
   
   /**
    * invokes the method on the process that performs the behavior associated
    * with this signal.
    * @param aProcess
    * @throws AeBusinessProcessException
    */
   protected abstract void dispatchBehavior(IAeBusinessProcessInternal aProcess) throws AeBusinessProcessException;
   
   /**
    * @return the processManager
    */
   protected IAeProcessManager getProcessManager()
   {
      return mProcessManager;
   }
   
   /**
    * @param aProcessManager the processManager to set
    */
   protected void setProcessManager(IAeProcessManager aProcessManager)
   {
      mProcessManager = aProcessManager;
   }
   
   /**
    * @return the processId
    */
   protected long getProcessId()
   {
      return mProcessId;
   }
   
   /**
    * @param aProcessId the processId to set
    */
   protected void setProcessId(long aProcessId)
   {
      mProcessId = aProcessId;
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
    * @return the callback
    */
   protected IAeMessageAcknowledgeCallback getCallback()
   {
      return mCallback;
   }
   
   /**
    * @param aCallback the callback to set
    */
   protected void setCallback(IAeMessageAcknowledgeCallback aCallback)
   {
      mCallback = aCallback;
   }
   
   /**
    * @return the callbackJournalId
    */
   protected long getCallbackJournalId()
   {
      return mCallbackJournalId;
   }
   
   /**
    * @param aCallbackJournalId the callbackJournalId to set
    */
   protected void setCallbackJournalId(long aCallbackJournalId)
   {
      mCallbackJournalId = aCallbackJournalId;
   }

   /**
    * @return the locationPath
    */
   protected String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * @param aLocationPath the locationPath to set
    */
   protected void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }

   /**
    * @return the coordinationId
    */
   protected String getCoordinationId()
   {
      return mCoordinationId;
   }

   /**
    * @param aCoordinationId the coordinationId to set
    */
   protected void setCoordinationId(String aCoordinationId)
   {
      mCoordinationId = aCoordinationId;
   }
}
 