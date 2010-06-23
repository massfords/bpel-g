//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeFCTHandler.java,v 1.4 2008/03/28 01:41:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeFCTHandler;
import org.activebpel.rt.bpel.impl.activity.AeActivityCompensateImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;

/**
 * Base class for Fault, Compensation, and Termination handlers.
 */
public abstract class AeFCTHandler extends AeActivityParent implements IAeFCTHandler
{
   /**
    * Implicit compensator activity for compensating coordinated activities.
    */
   private AeActivityCompensateImpl mCoordinatedCompensator;
      
   /**
    * Child activity fault object.
    */
   private IAeFault mChildFaultObject;
   
   /**
    * Default construct.
    * @param aDef
    * @param aParent
    */
   public AeFCTHandler(AeBaseDef aDef, IAeBpelObject aParent)
   {
      super(aDef, aParent);   
   }
      
   /**
    * @return Returns the childFaultObject.
    */
   protected IAeFault getChildFaultObject()
   {
      return mChildFaultObject;
   }
      
   /** 
    * @return true if there is an implicit compensator created for coordinated activities.
    */
   public boolean hasCoordinatedCompensator()
   {
      return (mCoordinatedCompensator != null);
   }
   
   /** 
    * Returns a compensator with coordinated CompInfo objects. This method creates one
    * if the current member variable is null.
    * @return Returns a compensator with coordinated CompInfo objects.
    */
   public AeActivityCompensateImpl getCoordinatedActivityCompensator()
   {
      if (mCoordinatedCompensator == null)
      {
         mCoordinatedCompensator = AeActivityCompensateImpl.createImplicitCompensation(this, getDefinition(), true);
      }
      return mCoordinatedCompensator;
   }
   
   /**
    * @return Returns true if the coordinated activities (invokes) 
    * should also be compensated after executing the normal list of CompInfo objects.
    */
   protected abstract boolean isEnableCoordinatedActivityCompensation();
 
   /** 
    * Overrides method to 
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      List list = new ArrayList();
      list.add(getActivity());
      if (hasCoordinatedCompensator())
      {
         list.add(getCoordinatedActivityCompensator());
      }
      return list.iterator();
   }   

   /**
    * Queues an implicit compensate activity to compensate coordinated CompInfo objects.
    * @param aChild
    * @param aChildFaultObject
    * @return true if an implicit compensate activity was queued.
    * @throws AeBusinessProcessException
    */
   protected boolean queueCoordinatedCompensator(IAeBpelObject aChild, IAeFault aChildFaultObject) throws AeBusinessProcessException
   {
      boolean rVal = false;
      
      // "install" compensator for coordinated activities if the aChild is
      // the child of this handler; and the compensation for coordinated activites is enabled.
      
      if ( getActivity() == aChild )
      {  
         // save the child fault
         if (aChildFaultObject != null)
            setFault(aChildFaultObject);
         
         // get the enclosing scope
         AeActivityScopeImpl scope = findEnclosingScope();
         // queue implicit compensator iff scope has coordinated activities
         if (scope != null && scope.hasCoordinations())
         {
            // FIXMEPJ The call to compensateOrCancel should behave the same way as the AeCoordinatorCompensationHandler does. 
            //       We should get a callback indicating that the participant was 
            //       cancelled or compensated prior to moving on.
            //       see particpantRepliesWithFault.bunit as an example of the problem
            // comp or cancel active coordinations
            scope.getCoordinationContainer().cancelActiveCoordinations();
            // queue implicit compensate activity if needed for the activities which are eligible for compensation.
            // Note: we queue up an implicit compensate activity only if the coordinated comp info objects
            // have not been run before to avoid repeated-comp-fault [scope.getCompInfo().hasEnabledCoordinatedChildren()]            
            if ( isEnableCoordinatedActivityCompensation() && !hasCoordinatedCompensator() 
                  && scope.getCompInfo().hasEnabledCoordinatedChildren())
            {
               getProcess().queueObjectToExecute(getCoordinatedActivityCompensator());
               rVal = true;
            }
         }
         else if (scope == null)
         {
            staticAnalysisFailure(AeMessages.getString("AeBaseHandler.ENCLOSING_SCOPE_NOT_FOUND")); //$NON-NLS-1$  
         }                    
      }
      return rVal;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public final void childComplete(IAeBpelObject aChild)
      throws AeBusinessProcessException
   {
      if (queueCoordinatedCompensator(aChild, null))
      {
         return;
      }
      
      childComplete();
   }

   /**
    * Changes state to finished and notifies scope of completion
    */
   protected void childComplete() throws AeBusinessProcessException
   {
      if (getActivity().getState() != AeBpelState.DEAD_PATH)
      {
         setState(AeBpelState.FINISHED);
         notifyScopeOfCompletion();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childCompleteWithFault(org.activebpel.rt.bpel.impl.IAeBpelObject, org.activebpel.rt.bpel.IAeFault)
    */
   public final void childCompleteWithFault(
      IAeBpelObject aChild,
      IAeFault aFaultObject)
      throws AeBusinessProcessException
   {
      if (queueCoordinatedCompensator(aChild, aFaultObject))
      {
         return;
      }
      
      if ( isAllowedToRethrow() )
      {
         IAeFault fault = getFault();
         setFaultedState( fault );
         // need to clear the fault once it is propagated to the compensate
         // if not done, then the comp handler will get saved w/ a fault
         setFault(null);
         notifyScopeOfFaultedCompletion(fault);
      }
      else
      {
         childComplete();
      }
   }

   /**
    * Returns true if the handler is allowed to rethrow any faults raised during its execution.
    */
   public abstract boolean isAllowedToRethrow();

   /**
    * Notifies the scope that the handler has completed normally. 
    */
   protected abstract void notifyScopeOfCompletion() throws AeBusinessProcessException;
   
   /**
    * Notifies the scope that the handler has completed with a fault
    * @param aFault
    */
   protected abstract void notifyScopeOfFaultedCompletion(IAeFault aFault) throws AeBusinessProcessException;   
}