//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/AeServerProcessCoordination.java,v 1.1 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.IAeCoordinator;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.impl.IAeProcessCoordination;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.coord.subprocess.dispatchers.AeCancelProcess;
import org.activebpel.rt.bpel.server.coord.subprocess.dispatchers.AeCancelSubprocessCompensation;
import org.activebpel.rt.bpel.server.coord.subprocess.dispatchers.AeCompensateSubprocess;
import org.activebpel.rt.bpel.server.coord.subprocess.dispatchers.AeCompensationCompleted;
import org.activebpel.rt.bpel.server.coord.subprocess.dispatchers.AeCoordinationCompleted;
import org.activebpel.rt.bpel.server.coord.subprocess.dispatchers.AeDeregisterCoordination;
import org.activebpel.rt.bpel.server.coord.subprocess.dispatchers.AeSubprocessCoordinationEnded;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Handles the coordination related operations for a process.
 * 
 * This impl uses the transaction manager and journal entries in order to
 * ensure that the coordination messages are delivered reliably. The basic
 * pattern for the methods here is as follows:
 *
 * 1. Lock process. 
 *      Important to do this first since it helps avoid deadlocks where 
 *      there are no db connections left but someone with a db connection is 
 *      trying to get the lock on this process. We'll also need the process 
 *      wrapper lock in order to mark its journal entry as done so we may as well
 *      grab it here even if deadlock weren't an issue. 
 * 2. create TX
 * 3. create new journal entry for call to process
 *      The recovery engine will replay this journal entry during recovery. This
 *      will target a specific method on the process as opposed to going through
 *      the whole coordination framework again. We're essentially accepting 
 *      responsibility for the delivery of the coordination message into the process
 * 4. mark callback journal entry as done
 * 5. create auto-done journal entry to indicate that the message has been transmitted
 *      This ensures that we won't retransmit the message. The concern would be 
 *      the situation where we recover with journal entries as follows:
 *              - journal for queue coordination message
 *              - journal for process behavior
 *      Without the transmitted journal entry, we'll replay the first coordination
 *      message and get an illegal state change on the coordination since the coordination
 *      has already persisted its state as a result of handing the call over to the
 *      process. Recovery should complete normally but it would be best to not
 *      have stacktraces or error messages in the console during recovery. 
 * 6. issue acknowledgement to callback (this persists the state of the coord)
 *      This callback enables the caller to include one or more calls as part 
 *      of the transaction established in this method
 * 7. end TX
 * 8. invoke method on process to do coordination action
 * 9. mark newly created journal as done
 * 10. release process
 *   
 */
public class AeServerProcessCoordination implements IAeProcessCoordination
{
   /**
    * Business process engine.
    */
   private IAeBusinessProcessEngineInternal mEngine;
   
   /**
    * Default ctor.
    */
   public AeServerProcessCoordination(IAeBusinessProcessEngineInternal aEngine)
   {
      setEngine(aEngine);
   }
     
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#registerCoordinationId(long, java.lang.String, java.lang.String)
    */
   public void registerCoordinationId(long aProcessId, String aLocationPath, String aCoordinationId)
         throws AeCoordinationException
   {
      // no need for TX here since this is already called from within a TX
      IAeBusinessProcessInternal process = null;
      IAeProcessManager processManager = getProcessManager();
      try
      {
         process = (IAeBusinessProcessInternal) processManager.getProcess(aProcessId);
         //  need to be synchronized (for in-memory process manager).
         process.registerCoordination(aLocationPath, aCoordinationId);
      }
      catch(AeCoordinationException ce)
      {
         throw ce;
      }
      catch(Throwable t)
      {
         throw new AeCoordinationException(t);
      }
      finally
      {
         processManager.releaseProcess(process);
      }      
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#deregisterCoordinationId(long, java.lang.String, java.lang.String, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void deregisterCoordinationId(long aProcessId, String aLocationPath, String aCoordinationId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId)
         throws AeCoordinationException
   {
      IAeProcessManager processManager = getProcessManager();
      AeDeregisterCoordination dispatch = new AeDeregisterCoordination(processManager, aProcessId, IAeProcessManager.NULL_JOURNAL_ID, aCallback, aCallbackJournalId, aLocationPath, aCoordinationId);
      dispatch.dispatch();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#activityFaulted(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void activityFaulted(long aProcessId, String aLocationPath, String aCoordinationId, IAeFault aFault, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId)
         throws AeCoordinationException
   {
      // fixme (MF-coord) combine into one
      IAeProcessManager processManager = getProcessManager();
      
      AeCoordinationCompleted dispatcher = new AeCoordinationCompleted(processManager, aProcessId, IAeProcessManager.NULL_JOURNAL_ID, aCallback, aCallbackJournalId, aLocationPath, aCoordinationId);
      dispatcher.setFault(aFault);
      dispatcher.dispatch();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#installCompensationHandler(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.coord.IAeCoordinator, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void installCompensationHandler(long aProcessId, String aLocationPath, String aCoordinationId,
         IAeCoordinator aCoordinator, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      // fixme (MF-coord) combine into one
      IAeProcessManager processManager = getProcessManager();
      
      AeCoordinationCompleted dispatcher = new AeCoordinationCompleted(processManager, aProcessId, IAeProcessManager.NULL_JOURNAL_ID, aCallback, aCallbackJournalId, aLocationPath, aCoordinationId);
      dispatcher.setCoordinator(aCoordinator); 
      dispatcher.dispatch();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#compensationCompletedCallback(long, java.lang.String, java.lang.String, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void compensationCompletedCallback(long aProcessId, String aLocationPath, String aCoordinationId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      // fixme (MF-coord) combine into one
      internalCompensationCompletedCallback(aProcessId, aLocationPath, aCoordinationId, null, aJournalId, aCallback, aCallbackJournalId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#compensationCompletedWithFaultCallback(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void compensationCompletedWithFaultCallback(long aProcessId, String aLocationPath,
         String aCoordinationId, IAeFault aFault, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      internalCompensationCompletedCallback(aProcessId, aLocationPath, aCoordinationId, aFault, aJournalId, aCallback, aCallbackJournalId);
   }

   /**
    * Handles the compensation callback either completed normally or with faulted (if aFault is not null).
    * 
    * @param aProcessId
    * @param aLocationPath
    * @param aCoordinationId
    * @param aFault
    * @param aCallback
    * @param aCallbackJournalId
    * @throws AeCoordinationException
    */
   protected void internalCompensationCompletedCallback(long aProcessId, String aLocationPath, String aCoordinationId, IAeFault aFault, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      IAeProcessManager processManager = getProcessManager();
      
      AeCompensationCompleted dispatcher = new AeCompensationCompleted(processManager, aProcessId, aJournalId, aCallback, aCallbackJournalId, aLocationPath, aCoordinationId);
      dispatcher.setFault(aFault);
      dispatcher.dispatch();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#compensateSubProcess(long, java.lang.String, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback)
    */
   public void compensateSubProcess(long aProcessId, String aCoordinationId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {            
      IAeProcessManager processManager = getProcessManager();
      
      AeCompensateSubprocess dispatcher = new AeCompensateSubprocess(processManager, aProcessId, aJournalId, aCallback, aCallbackJournalId, null, aCoordinationId);
      dispatcher.dispatch();
   }

   /** 
    * Overrides method to cancel subprocess's compensation handler if it is currently executing. 
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#cancelSubProcessCompensation(long, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void cancelSubProcessCompensation(long aProcessId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      IAeProcessManager processManager = getProcessManager();
      
      AeCancelSubprocessCompensation dispatcher = new AeCancelSubprocessCompensation(processManager, aProcessId, aJournalId, aCallback, aCallbackJournalId);
      dispatcher.dispatch();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#cancelProcess(long, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void cancelProcess(long aProcessId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId)
         throws AeCoordinationException
   {
      IAeProcessManager processManager = getProcessManager();
      
      AeCancelProcess dispatcher = new AeCancelProcess(processManager, aProcessId, aJournalId, aCallback, aCallbackJournalId);
      dispatcher.dispatch();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#subprocessCoordinationEnded(long, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void subprocessCoordinationEnded(long aProcessId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      IAeProcessManager processManager = getProcessManager();
      
      AeSubprocessCoordinationEnded dispatcher = new AeSubprocessCoordinationEnded(processManager, aProcessId, aJournalId, aCallback, aCallbackJournalId);
      dispatcher.dispatch();
   }
   
   /**
    * @return Returns the engine.
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return mEngine;
   }
         
   /**
    * @param aEngine The engine to set.
    */
   protected void setEngine(IAeBusinessProcessEngineInternal aEngine)
   {
      mEngine = aEngine;
   }
   
   /** 
    * @return Returns the coordination manager.
    */
   protected IAeCoordinationManagerInternal getCoordinationManager()
   {
      return getEngine().getCoordinationManager();
   }
   
   /** 
    * @return Returns the process manager.
    */
   protected IAeProcessManager getProcessManager()
   {
      return getEngine().getProcessManager();
   }
}