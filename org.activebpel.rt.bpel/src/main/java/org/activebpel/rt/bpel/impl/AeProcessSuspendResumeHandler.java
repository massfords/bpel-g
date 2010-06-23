// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeProcessSuspendResumeHandler.java,v 1.12 2008/02/17 21:37:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeEngineEvent;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeMonitorListener;
import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.visitors.AeCreateInstanceVisitor;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity;

/**
 * Handles the suspensd/resume and process exception management logic for the
 * business process.
 */
public class AeProcessSuspendResumeHandler
{
   /** the business process object */
   private AeBusinessProcess mProcess;
   
   /**
    * Constructor.
    * @param aProcess
    */
   public AeProcessSuspendResumeHandler( AeBusinessProcess aProcess )
   {
      mProcess = aProcess;
   }
   
   //----------[ process suspension methods ]-----------------------------------
   
   /**
    * Suspend the process.
    */
   public void suspend()
   {
      if( getProcess().isRunning() )
      {
         getExecutionQueue().suspend();
         setProcessState( IAeBusinessProcess.PROCESS_SUSPENDED );
         fireEngineEvent(IAeEngineEvent.PROCESS_SUSPENDED);
         getProcess().fireEvent(getProcess().getLocationPath(), IAeProcessEvent.SUSPENDED, "");          //$NON-NLS-1$
      }
   }
   
   /**
    * Suspend the process due to an uncaught fault condition.
    * @param aLocationPath
    * @param aUncaughtFault
    */
   public void suspendBecauseOfUncaughtFault(String aLocationPath,
         IAeFault aUncaughtFault)
   {
      getProcess().getFaultingActivityLocationPaths().add( aLocationPath );

      getExecutionQueue().suspend();
      setProcessState(IAeBusinessProcess.PROCESS_SUSPENDED);
      fireEngineEvent(IAeEngineEvent.PROCESS_SUSPENDED);
      getProcess().getEngine().fireMonitorEvent(IAeMonitorListener.MONITOR_PROCESS_FAULT, IAeMonitorListener.EVENT_DATA_PROCESS_FAULTING);
      getProcess().fireEvent( aLocationPath, IAeProcessEvent.SUSPENDED, 
                              aUncaughtFault.getFaultName().getLocalPart(), 
                              aUncaughtFault.getDetailedInfo() );
   }
   
   /**
    * Suspends the process due to non-durable invoke pending during process
    * recovery.
    *
    * @param aLocationPath location path of pending invoke activity
    */
   public void suspendBecauseOfInvokeRecovery(String aLocationPath)
   {
      getExecutionQueue().suspend();
      setProcessState(IAeBusinessProcess.PROCESS_SUSPENDED);
      fireEngineEvent(IAeEngineEvent.PROCESS_SUSPENDED);
      getProcess().fireEvent(aLocationPath, IAeProcessEvent.SUSPENDED, ""); //$NON-NLS-1$
   }

   /**
    * Suspends the process due to retry failure with onFailure set to "suspend"
    *
    * @param aLocationPath location path of pending invoke activity
    */
   public void suspendBecauseOfInvokeRetry(String aLocationPath, IAeFault aFault)
   {
      getProcess().getFaultingActivityLocationPaths().add( aLocationPath );

      getExecutionQueue().suspend();
      setProcessState(IAeBusinessProcess.PROCESS_SUSPENDED);
      fireEngineEvent(IAeEngineEvent.PROCESS_SUSPENDED);
      getProcess().getEngine().fireMonitorEvent(IAeMonitorListener.MONITOR_PROCESS_FAULT, IAeMonitorListener.EVENT_DATA_PROCESS_FAULTING);
      getProcess().fireEvent( aLocationPath, IAeProcessEvent.SUSPENDED, 
                              aFault.getFaultName().getLocalPart(), 
                              aFault.getDetailedInfo() );
   }
   
   //----------[ process resume methods ]---------------------------------------
   
   /**
    * Check the suspended execution queue and resume the object represented
    * by the passed path.
    */
   public void resume(String aLocation) throws AeBusinessProcessException
   {
      if( getProcess().isSuspended() )
      {
         getExecutionQueue().resume(aLocation);
      }
   }

   /**
    * Resumes the business process.
    * @param aExecute
    * @throws AeBusinessProcessException
    */
   public void resume( boolean aExecute ) throws AeBusinessProcessException
   {
      if( getProcess().isSuspended() )
      {
         setProcessState( IAeBusinessProcess.PROCESS_RUNNING );
         if( !getProcess().getFaultingActivityLocationPaths().isEmpty())
         {
            resumeFaultingObject();
         }
         fireEngineEvent( IAeEngineEvent.PROCESS_RESUMED );
         getExecutionQueue().resume(aExecute);
      }
   }
   
   /**
    * Continue to termination.
    * @throws AeBusinessProcessException
    */
   protected void resumeFaultingObject() throws AeBusinessProcessException
   {
      // someone hit resume...
      
      // walk all of the faulting location paths to see if one of the activities 
      // is executing as opposed to faulting. This will indicate that there is
      // a retry from the scope level executing and we should allow it to finish
      for (Iterator iter = getFaultingActivityLocationPaths().iterator(); iter.hasNext();)
      {
         String path = (String) iter.next();
         AeAbstractBpelObject faultingBpelObj = (AeAbstractBpelObject)findBpelObject( path );
         if (faultingBpelObj.getState() != AeBpelState.FAULTING)
         {
            return;
         }
      }
      
      // otherwise
      // if the caller hit resume, we should take the first entry
      // in the suspensionData list (the activity that actually
      // suspended the process) and resume it - which will ultimately
      // result in process termination (this is an uncaught fault)
      // we can safely discard the rest of the entries in the 
      // suspension data list
      String locationPath = (String)getFaultingActivityLocationPaths().remove(0);
      getFaultingActivityLocationPaths().clear();

      // get the faulting bpel object
      AeAbstractBpelObject faultingBpelObj = (AeAbstractBpelObject)findBpelObject( locationPath );
      IAeFault uncaughtFault = faultingBpelObj.getFault();
      
      // clear the fault from the bpel obj
      faultingBpelObj.setFault( null );
      
      // this will add the object back to queue in a wrapper that will
      // call the objectCompletedWithFault method so that fault processing
      // can resume as normal
      getExecutionQueue().addFaultingObject( faultingBpelObj, uncaughtFault );
   }

   /**
    * Retry the current activity (or its enclosing scope).
    * @param aLocationPath
    * @param aRetryFromEnclosingScope
    * @throws AeBusinessProcessException
    */
   public void retryActivity( String aLocationPath, boolean aRetryFromEnclosingScope ) throws AeBusinessProcessException
   {
      if( getProcess().isSuspended() )
      {
         AeAbstractBpelObject suspendedObject = findBpelObject( aLocationPath );
         
         if( suspendedObject == null )
         {
            String errMsg = AeMessages.format( "AeProcessSuspendResumeHandler.INVALID_LOCATION_PATH", aLocationPath ); //$NON-NLS-1$
            throw new AeBusinessProcessException( errMsg );
         }

         // object must be in faulting or executing state
         validateObjectCanBeRetried( suspendedObject, aRetryFromEnclosingScope );
         
         suspendedObject.setFault( null );

         // if the user wants to retry the enclosing scope, find it
         if( aRetryFromEnclosingScope )
         {
            // we're going to be retrying the enclosing scope, so remove the
            // suspended object's path from the faulting activity paths.
            getFaultingActivityLocationPaths().remove(suspendedObject.getLocationPath());
            AeActivityScopeImpl scope = suspendedObject.findEnclosingScope();
            suspendedObject = scope;
         }
         
         // clear any nested faulting locations from the process
         for (Iterator iter = getFaultingActivityLocationPaths().iterator(); iter.hasNext();)
         {
            String path = (String) iter.next();
            if (path.startsWith(suspendedObject.getLocationPath()))
            {
               iter.remove();
            }
         }
         
         // kick off the terminate sequence
         // then "wait" for the object's noMoreChildrenToTerminate 
         // method to be called 
         if( !getFaultingActivityLocationPaths().contains( suspendedObject.getLocationPath() ) )
         {
            getFaultingActivityLocationPaths().add( suspendedObject.getLocationPath() );
         }
         
         suspendedObject.terminateForRetry();
         if (suspendedObject instanceof AeActivityScopeImpl)
         {
            // if it's a scope, give the process a chance to execute the scope's retry fault handler
            resume(true);
         }
      }
   }

   /**
    * Called by the process when the faulting activity has completed its termination and is
    * ready for its retry attempt.
    * @param aSuspendedObject
    * @throws AeBusinessProcessException
    */
   public void terminationComplete(AeAbstractBpelObject aSuspendedObject) throws AeBusinessProcessException
   {
      aSuspendedObject.setState( AeBpelState.INACTIVE );
      aSuspendedObject.setState( AeBpelState.QUEUED_BY_PARENT );
      aSuspendedObject.setState( AeBpelState.READY_TO_EXECUTE );

      getFaultingActivityLocationPaths().remove( aSuspendedObject.getLocationPath() );
      
      suspend();
      
      getExecutionQueue().add( aSuspendedObject );
      
      // step
      resume( aSuspendedObject.getLocationPath() );
   }
   
   /**
    * Validate that the bpel object is in a state where it can be retried
    * (either FAULTING or EXECUTING).
    * @param aBpelObject
    * @throws AeBusinessProcessException Thrown if the object is not in a valid retry state.
    */
   protected void validateObjectCanBeRetried( AeAbstractBpelObject aBpelObject, boolean aRetryFromEnclosingScope )
   throws AeBusinessProcessException
   {
      // can't retry the root process
      // can retry faulting activity
      // can retry executing scope
      boolean okToRetry = aBpelObject.getParent() != null && (aBpelObject.getState() == AeBpelState.FAULTING || 
         (aBpelObject instanceof AeActivityScopeImpl && aBpelObject.getState() == AeBpelState.EXECUTING));
      
      if( !okToRetry)
      {
         Object[] errMsgParams = { aBpelObject.getLocationPath(), aBpelObject.getState() };
         throw new AeBusinessProcessException( AeMessages.format("AeProcessSuspendResumeHandler.INVALID_RETRY", errMsgParams) ); //$NON-NLS-1$
      }
      else if (aRetryFromEnclosingScope && aBpelObject.findEnclosingScope().getParent() == null || retryInvolvesCreateInstance(aBpelObject, aRetryFromEnclosingScope))
      {
         // throw if we're trying to retry at the scope level and that would mean retrying the process
         Object[] errMsgParams = { aBpelObject.getLocationPath() };
         throw new AeBusinessProcessException( AeMessages.format("AeProcessSuspendResumeHandler.INVALID_RETRY_AT_SCOPE", errMsgParams) ); //$NON-NLS-1$
      }
   }
   
   /**
    * We currently don't support retrying of a process, only activities within the process. As such, we will
    * not allow retrying of a create instance activity. 
    * @param aBpelObject
    * @param aRetryFromEnclosingScope
    */
   protected boolean retryInvolvesCreateInstance(AeAbstractBpelObject aBpelObject, boolean aRetryFromEnclosingScope)
   {
      // TODO (MF) Consider lifting this restriction and offering a "replay" of the create instance message or perhaps simply allowing the retry of create instance activities. 
      if (aBpelObject instanceof IAeMessageReceiverActivity && ((IAeMessageReceiverActivity)aBpelObject).canCreateInstance())
      {
         return true;
      }
      
      AeBaseDef def = null;
      if (aRetryFromEnclosingScope)
      {
         def = aBpelObject.findEnclosingScope().getDefinition();
      }
      else
      {
         def = aBpelObject.getDefinition();
      }
      
      // check to see if the enclosing scope contains any createInstance activities
      AeCreateInstanceVisitor createInstanceVisitor = new AeCreateInstanceVisitor();
      def.accept(createInstanceVisitor);
      return createInstanceVisitor.isCreateInstanceFound();
   }
   
   /**
    * Mark the suspended bpel object as complete and step.
    * The idea here is that the user will have manually fixed the process so
    * that it is in a state to continue.
    * @param aLocationPath
    * @throws AeBusinessProcessException
    */
   public void completeActivity( String aLocationPath ) throws AeBusinessProcessException
   {
      if( getProcess().isSuspended() )
      {
         AeAbstractBpelObject completeMe = findBpelObject( aLocationPath );
         
         if( completeMe == null )
         {
            String errMsg = AeMessages.format( "AeProcessSuspendResumeHandler.INVALID_LOCATION_PATH", aLocationPath ); //$NON-NLS-1$
            throw new AeBusinessProcessException( errMsg );
         }

         validateObjectCanBeCompleted( completeMe );
         
         completeMe.setFault( null );
         getFaultingActivityLocationPaths().remove( aLocationPath );
         
         // add it to the execution queue (where it will be wrapped
         // in a stub that simply calls objectCompleted
         getExecutionQueue().addCompletedObject( completeMe );
         
         // step
         resume( completeMe.getLocationPath() );
      }
   }
   
   /**
    * @param aBpelObject
    * @throws AeBusinessProcessException
    */
   protected void validateObjectCanBeCompleted( AeAbstractBpelObject aBpelObject )
   throws AeBusinessProcessException
   {
      boolean okToComplete = aBpelObject.getState() == AeBpelState.FAULTING ||
         aBpelObject.getState() == AeBpelState.EXECUTING ||
         aBpelObject.getState() == AeBpelState.READY_TO_EXECUTE;
      
      if( !okToComplete )
      {
         Object[] errMsgParams = { aBpelObject.getLocationPath(), aBpelObject.getState() };
         throw new AeBusinessProcessException( AeMessages.format("AeProcessSuspendResumeHandler.INVALID_COMPLETE", errMsgParams) ); //$NON-NLS-1$
      }
   }
   
   
   //----------[ utility methods ]----------------------------------------------

   /**
    * Find the <code>AeAbstractBpelObject</code> given its location path.
    * @param aLocationPath
    */
   protected AeAbstractBpelObject findBpelObject( String aLocationPath )
   {
      return (AeAbstractBpelObject)getProcess().findBpelObject( aLocationPath );
   }
   
   /**
    * Get the list of faulting activity location paths.
    */
   protected List getFaultingActivityLocationPaths()
   {
      return getProcess().getFaultingActivityLocationPaths();
   }
   
   /**
    * Set the process state.
    * @param aProcessState
    */
   protected void setProcessState( int aProcessState )
   {
      getProcess().setProcessState( aProcessState );
   }
   
   /**
    * Signal the process to fire an engine event with the given type.
    * @param aEventType
    */
   protected void fireEngineEvent( int aEventType )
   {
      getProcess().getEngine().fireEngineEvent(new AeEngineEvent(getProcessId(), aEventType, getProcessName()));
   }
   
   /**
    * Getter for the process' <code>AeExecutionQueue</code> object.
    */
   protected AeExecutionQueue getExecutionQueue()
   {
      return getProcess().getExecutionQueue();
   }
   
   /**
    * Getter for the process id.
    */
   protected long getProcessId()
   {
      return getProcess().getProcessId();
   }
   
   /**
    * Getter for the process <code>QName</code>.
    */
   protected QName getProcessName()
   {
      return getProcess().getName();
   }
   
   /**
    * Find the uncaugh fault given its location path.
    * @param aLocationPath
    */
   protected IAeFault getUncaughtFault( String aLocationPath )
   {
      AeAbstractBpelObject faultingObj = findBpelObject( aLocationPath );
      return faultingObj.getFault();
   }
   
   /**
    * @return Returns the process.
    */
   public AeBusinessProcess getProcess()
   {
      return mProcess;
   }
}
