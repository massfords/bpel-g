//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeCoordinationManager.java,v 1.10.2.1 2008/04/29 21:36:49 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import commonj.work.Work;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.AeCoordinationFaultException;
import org.activebpel.rt.bpel.coord.AeCoordinationNotFoundException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.coord.IAeCoordinator;
import org.activebpel.rt.bpel.coord.IAeCreateContextRequest;
import org.activebpel.rt.bpel.coord.IAeCreateContextResponse;
import org.activebpel.rt.bpel.coord.IAeParticipant;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.coord.IAeProtocolState;
import org.activebpel.rt.bpel.coord.IAeRegistrationRequest;
import org.activebpel.rt.bpel.coord.IAeRegistrationResponse;
import org.activebpel.rt.bpel.impl.AeManagerAdapter;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.coord.subprocess.AeSpCoordinationStates;
import org.activebpel.rt.bpel.server.coord.subprocess.IAeSpCoordinating;
import org.activebpel.rt.bpel.server.coord.subprocess.IAeSpCoordinator;
import org.activebpel.rt.bpel.server.coord.subprocess.IAeSpProtocolMessage;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.work.AeAbstractWork;

/**
 * Base class of the coordination manager implementation.
 */
public abstract class AeCoordinationManager extends AeManagerAdapter implements IAeCoordinationManagerInternal
{
   /**
    * Default constructor.
    */
   public AeCoordinationManager(Map aConfig)
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#notifyCoordinatorsParticipantClosed(long, long)
    */
   public void notifyCoordinatorsParticipantClosed(long aProcessId, long aJournalId)
   {
      try
      {
         // Find list of coordinations (coordinators or participants) for this process id.
         Iterator it = getCoordinatingIterator(aProcessId);
         List coordinatingObjects = AeUtil.toList(it);

         // for each coordinator, call coord.onProcessComplete(aFault)
         for (Iterator iter = coordinatingObjects.iterator(); iter.hasNext();)
         {
            IAeCoordinating c = (IAeCoordinating) iter.next();
            // should this be done via a message dispatch (async mechanism) ?
            if (c instanceof IAeCoordinator)
            {
               c.onProcessComplete(null, true);
            }
         }
      }
      catch (AeCoordinationNotFoundException cfne)
      {
         // ignore - process was not under coordination.
      }
      catch(Throwable t)
      {
         AeException.logError(t, t.getMessage());
      }
      finally
      {
         
         IAeBusinessProcess process = null;
         try
         {
            // The process must be loaded to ensure that the journal entries will
            // be marked as done. This seems wasteful and it may be better to
            // modify the process manager code to trigger a save of the wrapper
            // even if there isn't a process associated with it.
            // fixme (MF-journaling) add some code to do a quick purge of the completed journal ids
            process = getEngine().getProcessManager().getProcess(aProcessId);
            getEngine().getProcessManager().journalEntryDone(aProcessId, aJournalId);
         }
         catch (AeBusinessProcessException e)
         {
         }
         finally
         {
            if (process != null)
               getEngine().getProcessManager().releaseProcess(process);
         }
      }
   }

   /**
    * Handler for Process Manager events. The process manager will call this 
    * method when a process that participating in a coordination has completed.
    *
    * @param aFaultObject fault object if the process completed with a fault.
    * @param aNormalCompletion indiciates that the process completed normally and is eligible fo compensation.
    */
   public void onProcessCompleted(long aProcessId, IAeFault aFaultObject, boolean aNormalCompletion)
   {
      try
      {
         // Find list of coordinations (coordinators or participants) for this process id.
         Iterator it = getCoordinatingIterator(aProcessId);
         List coordinatingObjects = AeUtil.toList(it);

         // If completed normally and aProcessId has both Coordinator(s) 
         // and Participant, that means aProcessId is a subprocess with a
         // nested subprocess and should only notify my Particpant 
         // reference of the completion.
         boolean skipNotifyingCoordinators = hasCoordinatorsAndParticipant(coordinatingObjects);
         
         // for each coordination, call coord.onProcessComplete(aFault)
         for (Iterator iter = coordinatingObjects.iterator(); iter.hasNext();)
         {
            IAeCoordinating c = (IAeCoordinating) iter.next();
            // should this be done via a message dispatch (async mechanism) ?
            if (c instanceof IAeCoordinator && skipNotifyingCoordinators)
            {
               // skip call to notify process complete
            }
            else
            {
               c.onProcessComplete(aFaultObject, aNormalCompletion);
            }
         }
      }
      catch (AeCoordinationNotFoundException cfne)
      {
         // ignore - process was not under coordination.
      }
      catch(Throwable t)
      {
         AeException.logError(t, t.getMessage());
      }
   }

   /**
    * Returns true if the process coordinating objects contains both coordinator
    * objects and a participant object. This means that the process itself is
    * a subprocess and is also the parent of other subprocesses.
    * @param aCoordinatingObjects
    */
   private boolean hasCoordinatorsAndParticipant(List aCoordinatingObjects)
   {
      boolean isParticipant = false;
      boolean hasCoordinators = false;
      boolean skipNotifyingCoordinators = false;
      for (Iterator iter = aCoordinatingObjects.iterator(); iter.hasNext();)
      {
         IAeCoordinating c = (IAeCoordinating) iter.next();
         isParticipant = isParticipant | c instanceof IAeParticipant;
         hasCoordinators = hasCoordinators | c instanceof IAeCoordinator;
         skipNotifyingCoordinators = isParticipant && hasCoordinators;

         if (skipNotifyingCoordinators)
            break;
      }
      //skipNotifyingCoordinators = isParticipant && hasCoordinators;
      return skipNotifyingCoordinators;
   }

   /**
    * Creates a new coordination context.
    * @param aCtxRequest create context request.
    */
   public IAeCreateContextResponse createCoordinationContext(IAeCreateContextRequest aCtxRequest)
         throws AeCoordinationException
   {
      // check to see if support the requested type 
      if (!IAeCoordinating.AE_SUBPROCESS_COORD_TYPE.equals( aCtxRequest.getCoordinationType()) ) 
      {
         // unsupported type
         throw new AeCoordinationException(AeMessages.format("AeCoordinationManager.UNSUPPORTED_TYPE", aCtxRequest.getCoordinationType())); //$NON-NLS-1$
      }
      
      String pidStr = aCtxRequest.getProperty(IAeCoordinating.AE_COORD_PID);
      if (AeUtil.isNullOrEmpty(pidStr))
      {
         // missing required pid
         throw new AeCoordinationException(AeMessages.getString("AeCoordinationManager.MISSING_PID")); //$NON-NLS-1$  
      }
      String locPath = aCtxRequest.getProperty(IAeCoordinating.AE_COORD_LOCATION_PATH);
      if (AeUtil.isNullOrEmpty(locPath))
      {
         // missing required location path.
         throw new AeCoordinationException(AeMessages.getString("AeCoordinationManager.MISSING_LOCATION_PATH")); //$NON-NLS-1$  
      }      
      // create context
      AeCoordinationContext ctx = createContext(aCtxRequest, null, AeSpCoordinationStates.NONE, IAeCoordinating.COORDINATOR_ROLE);      
      return new AeCreateContextResponse(ctx);
   }
      
   /**
    * Creates a coordinating object (IAeCoordinator or IAeParticipant). 
    * @param aContext context
    * @param aInitState initial state
    * @param aRole either coordinator or participant
    * @return IAeCoordinating implementation of the given role.
    * @throws AeCoordinationException
    */
   protected IAeCoordinating createCoordination(AeCoordinationContext aContext, IAeProtocolState aInitState, int aRole) throws AeCoordinationException
   {
      // delegate to a factory to create the object.
      AeCoordinationFactory factory = AeCoordinationFactory.getInstance();
      IAeCoordinating coordinating = factory.createCoordination(this, aContext, aInitState, aRole);
      return coordinating;
   }
   
   /**
    * Registers an activity for coordination. The coordinator should be activated (via createCoordinationContext)
    * prior to registration.
    * @param aRegRequest coordination registration request.
    */
   public IAeRegistrationResponse register(IAeRegistrationRequest aRegRequest) throws AeCoordinationException
   {
      
      // check if we support the protocol
      if (!IAeSpCoordinating.AESP_PARTICIPANT_COMPLETION_PROTOCOL.equalsIgnoreCase(aRegRequest.getProtocolIdentifier() ))
      {
         // throw
         throw new AeCoordinationFaultException(AeCoordinationFaultException.INVALID_PROTOCOL);
      }
      
      String coordId = aRegRequest.getCoordinationContext().getIdentifier();
            
      // get coordinator  
      IAeSpCoordinator coord = null;
      try
      {
         coord = (IAeSpCoordinator) getCoordinator(coordId) ;
      }
      catch(AeCoordinationNotFoundException e)
      {
         // not activated! throw
         throw new AeCoordinationException(AeMessages.format("AeCoordinationManager.UNKNOWN_COORDINATION", coordId)); //$NON-NLS-1$
      }
      
      // create participant context
      AeCreateContextRequest ctxRequest = new AeCreateContextRequest();
      // copy over context properties.
      ctxRequest.setCoordinationType(coord.getCoordinationContext().getCoordinationType());
      // set the supported protocol. In this case, it is a version of AESP_PARTICIPANT_COMPLETION_PROTOCOL.
      ctxRequest.setProperty(IAeCoordinating.WSCOORD_PROTOCOL, aRegRequest.getProtocolIdentifier());
      // pid
      ctxRequest.setProperty(IAeCoordinating.AE_COORD_PID, aRegRequest.getProperty(IAeCoordinating.AE_COORD_PID));
      // location path is always /process
      ctxRequest.setProperty(IAeCoordinating.AE_COORD_LOCATION_PATH, "/process");  //$NON-NLS-1$
      
      if (! (coord.getCoordinationContext() instanceof AeCoordinationContext) )
      {
         throw new AeCoordinationException( AeMessages.format("AeCoordinationManager.UNSUPPORTED_CONTEXT_CLASS",  //$NON-NLS-1$ 
                  coord.getCoordinationContext().getClass().getName() ) ); 
      }
      AeCoordinationContext coordinatorCtx = (AeCoordinationContext) coord.getCoordinationContext();
      
      // set message destination process id and location path.
      // todo: delegate this procedure to a protocol dependent IRegistrationService impl.

      // for now, set the target process id and location id.
      // for cooordinator:
      coordinatorCtx.setProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_PROCESS_ID, ctxRequest.getProperty(IAeCoordinating.AE_COORD_PID) );
      coordinatorCtx.setProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_LOCATION_PATH, ctxRequest.getProperty(IAeCoordinating.AE_COORD_LOCATION_PATH) );
      // for participant:
      ctxRequest.setProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_PROCESS_ID, coordinatorCtx.getProperty(IAeCoordinating.AE_COORD_PID) );
      ctxRequest.setProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_LOCATION_PATH, coordinatorCtx.getProperty(IAeCoordinating.AE_COORD_LOCATION_PATH) );      
      
      // create context for the participant
      createContext(ctxRequest, coordinatorCtx.getCoordinationId(), AeSpCoordinationStates.ACTIVE, IAeCoordinating.PARTICIPANT_ROLE);  
      
      // let coordinator register this coordination activity (with the process/scope).
      coord.register(); 
      persistState(coord); // persist new state info (from NONE -> ACTIVE )
      persistContext(coord); // update context with info on participant's pid and locPath.
      // create and return response.
      AeRegistrationResponse resp = new AeRegistrationResponse();
      resp.setProtocolIdentifier(IAeSpCoordinating.AESP_PARTICIPANT_COMPLETION_PROTOCOL);
      return resp;
   }
   
   /** 
    * Overrides method to return the first matching context for the given id. 
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#getContext(java.lang.String)
    */
   public IAeCoordinationContext getContext(String aCoordinationId) throws AeCoordinationNotFoundException
   {
      Iterator iter = getCoordinatingIterator(aCoordinationId);
      IAeCoordinating c = (IAeCoordinating) iter.next();      
      IAeCoordinationContext context = c.getCoordinationContext();;
      return context;
   }
   
      
   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#dispatch(org.activebpel.rt.bpel.coord.IAeProtocolMessage, boolean)
    */
   public void dispatch(IAeProtocolMessage aMessage, boolean aViaProcessExeQueue)
   {
      // for now, we only know how to dispatch sub-process protocol messages.
      if (aMessage instanceof IAeSpProtocolMessage)
      {
         Work work = null;
         if (aViaProcessExeQueue)
         {
            // dispatch via process's execution queue
            work = new AeCoordinationMessageProcessEnqueueWork(this, aMessage);
         }
         else
         {
            // dispatch via a simple callback to queueReceive
            work = new AeCoordinationMessageDispatchWork(this, aMessage);
         }
         try
         {
            long processId = ((IAeSpProtocolMessage) aMessage).getProcessId();
            AeEngineFactory.schedule(processId, work);
         }
         catch(Exception e)
         {
            // todo: handle this exception!
            AeException.logError(e,e.getMessage());
         }
      }
   }
      
   
   /**
    * Save the current state information.
    * @param aCoordinating
    */
   public abstract void persistState(IAeCoordinating aCoordinating) throws AeCoordinationException;
   
   /**
    * Saves the state.
    * @param aCoordinating coordinator or participant.
    * @throws AeCoordinationException
    */
   protected abstract void persistContext(IAeCoordinating aCoordinating) throws AeCoordinationException;
   
   
   /**
    * Targets the incoming message to the recipient (a coordinator or participant).
    * @param aMessage
    */
   protected void queueReceiveMessage(IAeProtocolMessage aMessage)
   {      
      // for now, we only know how to handle sub-process type and protocol messages.
      if (aMessage instanceof IAeSpProtocolMessage)
      { 
         // Ideally:
         //  for each coordinator|participant that has matching coordination_id:
         //    coordinator_or_participant.queueReceiveMessages(aMessage)
         
         // todo: need a IAeCoordinationProtocolHandler that knows how to find targeted objects.
         // for now, assume subprocess type and use process id to locate target.
         
         try
         {
            IAeSpProtocolMessage m = (IAeSpProtocolMessage) aMessage;
            long pid = m.getProcessId();
            String coordId = m.getCoordinationId();
            IAeCoordinating c = getCoordinating(coordId, pid);
            c.queueReceiveMessage(aMessage);
         }
         catch(AeCoordinationNotFoundException e)
         {
            AeException.logError(e,e.getMessage());
         }         
         catch(Throwable t)
         {
            AeException.logError(t,t.getMessage());
         }
      } // if     
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#compensateOrCancel(java.lang.String)
    */
   public void compensateOrCancel(String aCoordinationId)
   {      
      try
      {
         IAeCoordinator coordinator = getCoordinator(aCoordinationId);
         coordinator.compensateOrCancel();
      }
      catch(Exception e)
      {
         AeException.logError(e, e.getMessage());            
      }
   }

   /**
    * Returns true if a coordination exists for the given coordination id.
    * @param aCoordinationId
    * @return true if a coordination context exists.
    */
   protected boolean hasCoordinator(String aCoordinationId)
   {
      boolean rVal = false;
      try
      {
         rVal = ( getCoordinator(aCoordinationId) != null );
      }
      catch (AeCoordinationNotFoundException cne)
      {
         // ignore
      }
      catch(Throwable t)
      {
         // should not get here.
         AeException.logError(t, t.getMessage());
      }
      return rVal;
   }
   
   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#cancel(java.lang.String)
    */
   public void cancel(String aCoordinationId) throws AeCoordinationException
   {
      getCoordinator(aCoordinationId).cancel();
   }

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#compensate(java.lang.String)
    */
   public void compensate(String aCoordinationId) throws AeCoordinationException
   {
      getCoordinator(aCoordinationId).compensate();
   }

   /**
    * Returns a coordinator matching the coordination id.
    * @param aCoordinationId
    * @return the coordinator if found or null otherwise.
    */   
   public IAeCoordinator getCoordinator(String aCoordinationId) throws AeCoordinationNotFoundException
   {      
      Iterator it = getCoordinatorIterator(aCoordinationId);
      return (IAeCoordinator) it.next();
   }  

   /**
    * Returns the first participant matching the coordination id.
    * @param aCoordinationId
    * @return the participant if found or null otherwise.
    */
   public IAeParticipant getParticipant(String aCoordinationId) throws AeCoordinationNotFoundException
   {
      // (applies to external coordination types and protcols).
      Iterator it = getParticipantIterator(aCoordinationId);
      return  (IAeParticipant) it.next();
   }  
   
   /**
    * Returns an iterator to a set of coordinators matching the coordination id.
    * @param aCoordinationId
    * @return iterator to coordinators.
    */   
   public Iterator getCoordinatorIterator(String aCoordinationId) throws AeCoordinationNotFoundException
   {
      Set set = new HashSet();
      Iterator it = getCoordinatingIterator(aCoordinationId);
      while (it.hasNext())
      {
         IAeCoordinating c = (IAeCoordinating) it.next();
         if (c instanceof IAeCoordinator)
         {
            set.add(c);
         }
      }
      return getCoordinatingIterator(set, aCoordinationId);
   }     

   /**
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#compensationCompleted(java.lang.String, org.activebpel.rt.bpel.IAeFault)
    */
   public void compensationCompleted(String aCoordinationId, IAeFault aFault) throws AeBusinessProcessException
   {
      try
      {
         if (aFault == null)
            getParticipant(aCoordinationId).compensationComplete();
         else
            getParticipant(aCoordinationId).compensationCompleteWithFault(aFault);
      }
      catch(AeCoordinationNotFoundException e)
      {
         System.out.println("error calling compensation completed"); //$NON-NLS-1$
      }
   }

   /**
    * Returns an iterator to list of participants matching the coordination id.
    * @param aCoordinationId
    * @return iterator to list of participants with matching coordination id.
    */
   protected Iterator getParticipantIterator(String aCoordinationId) throws AeCoordinationNotFoundException
   {
      Set set = new HashSet();
      Iterator it = getCoordinatingIterator(aCoordinationId);
      while (it.hasNext())
      {
         IAeCoordinating c = (IAeCoordinating) it.next();
         if (c instanceof IAeParticipant)
         {
            set.add(c);
         }
      }
      return getCoordinatingIterator(set, aCoordinationId);
   }   
   
   /**
    * Creates and registers the given context given the context. The coordination id is normally null 
    * for Coordinators since this method will generate a new id.  
    * @param aCtxRequest
    * @param aId coordination id. Required for participants.
    * @param aRole coordinator or paticipant role.
    * @return coordination context.
    * @throws AeCoordinationException
    */
   protected abstract  AeCoordinationContext createContext(IAeCreateContextRequest aCtxRequest, IAeCoordinationId aId, 
               IAeProtocolState aInitState, int aRole)
      throws AeCoordinationException;
         
   /**
    * Returns an iterator to coordinating activities matching the coordination id.
    * @param aCoordinationId
    * @return iterator of IAeCoordinating objects.
    * @throws AeCoordinationNotFoundException
    */
   protected abstract Iterator getCoordinatingIterator(String aCoordinationId) throws AeCoordinationNotFoundException;
   
   /**
    * Returns an iterator to coordinating activities matching the process id.
    * @param aProcessId
    * @return iterator of IAeCoordinating objects.
    * @throws AeCoordinationNotFoundException
    */
   protected abstract Iterator getCoordinatingIterator(long aProcessId) throws AeCoordinationNotFoundException;
   
   /**
    * Returns a Coordination object given the process id and the coordination id.
    * @param aPid
    * @param aCoordinationId
    * @throws AeCoordinationNotFoundException
    */
   protected abstract IAeCoordinating getCoordinating(String aCoordinationId, long aPid) throws AeCoordinationNotFoundException;
   
   /**
    * Convenience method to return the iterator to a collection if the collection is not empty.
    * If the collection is empty, then this method will throw <code>AeCoordinationNotFoundException</code>
    * exception.
    * @param aCollection
    * @param aCoordinationId
    * @return Iterator to collection.
    * @throws AeCoordinationNotFoundException
    */
   protected Iterator getCoordinatingIterator(Collection aCollection, String aCoordinationId) throws AeCoordinationNotFoundException
   {
      if (aCollection.size() == 0)
      {
         throw new AeCoordinationNotFoundException(aCoordinationId);
      }
      else
      {
         return aCollection.iterator();
      }      
   }
}

/**
 * Wraps the protcol message in a <code>Work</code> impl so that it can be dispatched asynchronously.
 */
class AeCoordinationMessageDispatchWork extends AeAbstractWork
{
   /** Message to be dispatched. */
   private IAeProtocolMessage mMessage;
   /** Coordination manager. */
   private AeCoordinationManager mManager;
   
   /** Default ctor. */
   public AeCoordinationMessageDispatchWork(AeCoordinationManager aManager, IAeProtocolMessage aMessage)
   {
      mManager = aManager;
      mMessage = aMessage;
   }
   
   /**
    * @return Returns the manager.
    */
   protected AeCoordinationManager getManager()
   {
      return mManager;
   }
   
   /**
    * @return Returns the message.
    */
   protected IAeProtocolMessage getMessage()
   {
      return mMessage;
   }
   
   /** 
    * Overrides method to dispatch a message. Currently this implementation callback back on the
    * Coordination managers's queueReceiveMessages() method. 
    * @see java.lang.Runnable#run()
    */
   public void run()
   {
      getManager().queueReceiveMessage(getMessage());
   }
}

/**
 * Wraps the protcol message in a <code>Work</code> impl so that it can be queued asynchronously
 * to the (sub) process's execution queue.
 */
class AeCoordinationMessageProcessEnqueueWork extends AeCoordinationMessageDispatchWork
{
   /** Default ctor. */
   public AeCoordinationMessageProcessEnqueueWork(AeCoordinationManager aManager, IAeProtocolMessage aMessage)
   {
      super(aManager, aMessage);
   }
   
   /** 
    * Overrides method to dispatch message to the (sub) process's execution queue.
    * @see java.lang.Runnable#run()
    */
   public void run()
   {
      if (getMessage() instanceof IAeSpProtocolMessage)
      {
         IAeSpProtocolMessage m = (IAeSpProtocolMessage) getMessage();
         long pid = m.getProcessId();
         IAeBusinessProcess process = null;
         IAeProcessManager processManager = null;
         try
         {
            IAeBusinessProcessEngineInternal engine = (IAeBusinessProcessEngineInternal)AeEngineFactory.getEngine();
            processManager = engine.getProcessManager();
            process = processManager.getProcess(pid);

            // wrap message around a process queue executable stub. When the process executes this
            // stub, the message is piped into the coordination manager via queueReceiveMessage.
            process.queueObjectToExecute( new AeProcessExecutionQueueMessageWrapper(getManager(), m) );
         }
         catch(AeBusinessProcessException bpe)
         {
            AeException.logError(bpe,bpe.getMessage());
         }
         finally
         {
            processManager.releaseProcess(process);
         }// finally
      }// if
      
   }
}
/**
 * Wraps the protcol message in a Runnable so that it can be enqueued to a process's execution queue.
 */
class AeProcessExecutionQueueMessageWrapper implements Runnable
{
   /** Message to be dispatched. */
   private IAeProtocolMessage mMessage;
   /** Coordination manager. */
   private AeCoordinationManager mManager;
   
   /** Ctor */
   public AeProcessExecutionQueueMessageWrapper(AeCoordinationManager aManager, IAeProtocolMessage aMessage)
   {
      mManager = aManager;
      mMessage = aMessage;
   }
   
   // Runnable interface
   public void run()
   {
      // called from the process' execution
      try
      {
         mManager.queueReceiveMessage(mMessage);         
      }
      catch(Exception e)
      {
         AeException.logError( e, e.getMessage() );
      }
   }
}