// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeCompensationHandler.java,v 1.23 2006/11/10 04:00:21 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import java.util.Set;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Models the <code>compensateHandler</code> that is part of a scope or process.
 */
public class AeCompensationHandler extends AeFCTHandler
{
   /** Reference back to the object that invoked the compensation handler */
   private IAeCompensationCallback mCallback;

   /** reference to the current set of compensation information. If a scope
    * was executed in a loop then it'll likely be compensated for each iteration
    * through the loop. The compensate activity will call into the handler
    * to execute the compensation behavior once for each time the scope
    * reported its execution as complete. Each time through, the comp info
    * object will be set in place so the comp handler will have access to the
    * correct variable snapshot  */
   private AeCompInfo mCompInfo;

   /**
    * Requires the definition object and the scope impl.
    * @param aContainerDef
    * @param aParent
    */
   public AeCompensationHandler(AeCompensationHandlerDef aContainerDef,
                                 AeActivityScopeImpl aParent)
   {
      super(aContainerDef, aParent);
   }

   /**
    * Explicit compensation handlers run only the CompInfo objects specified by
    * Compensate activities. After running all of the activities with in this handler,
    * any remaining coordinatated activites (invokes) must also be (implicitly) compensated.
    * This method returns true so that the coordinated activities are also compensated.
    *
    * @return Returns true if the coordinated activities (invokes)
    * should also be compensated after executing the normal list of CompInfo object.
    */
   protected boolean isEnableCoordinatedActivityCompensation()
   {
      // Explicit compensation handlers should always run the comp for the coord. activities
      // after running the normal list of comps.
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * Overridden here so we can pull the variable data we need from the snapshot.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#findVariable(java.lang.String)
    */
   public IAeVariable findVariable(String aName)
   {
      AeCompInfo compInfo = getCompInfo();
      IAeVariable var = compInfo.getVariable(aName);
      if (var == null)
         var = super.findVariable(aName);
      return var;
   }

   /**
    * Overridden here so we can pull the correlation set we need from the snapshot.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#findCorrelationSet(java.lang.String)
    */
   public AeCorrelationSet findCorrelationSet(String aName)
   {
      AeCorrelationSet corrSet = getCompInfo().getCorrelationSet(aName);
      if (corrSet == null)
         corrSet = super.findCorrelationSet(aName);
      return corrSet;
   }

   /**
    * Overridden here so we can pull the partner link we need from the snapshot.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#findPartnerLink(java.lang.String)
    */
   public AePartnerLink findPartnerLink(String aName)
   {
      AePartnerLink plink = getCompInfo().getPartnerLink(aName);
      if (plink == null)
         plink = super.findPartnerLink(aName);
      return plink;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      getProcess().queueObjectToExecute(getActivity());
   }

   /**
    * notifies the callback that the compensation is complete
    * @see org.activebpel.rt.bpel.impl.activity.support.AeFCTHandler#notifyScopeOfCompletion()
    */
   protected void notifyScopeOfCompletion() throws AeBusinessProcessException
   {
      if (hasNewOrphanedIMAs())
      {
         // fixme (MF) Fault immediately.
//         faultOrphanedIMAs();
         notifyScopeOfFaultedCompletion(getFaultFactory().getMissingReply());
      }
      else
      {
         mCallback.compensationComplete(this);
      }
   }

   /**
    * notifies the callback that the compensation completed with a fault
    * @see org.activebpel.rt.bpel.impl.activity.support.AeFCTHandler#notifyScopeOfFaultedCompletion(org.activebpel.rt.bpel.IAeFault)
    */
   protected void notifyScopeOfFaultedCompletion(IAeFault aFault) throws AeBusinessProcessException
   {
      mCallback.compensationCompleteWithFault(this, aFault );
   }

   /**
    * Called by the compensate activity during compensation. This will install
    * the compInfo object and provide access to the variable snapshot data
    * @param aCompInfo
    */
   public void setCompInfo(AeCompInfo aCompInfo)
   {
      mCompInfo = aCompInfo;
   }

   /**
    * Getter for the compensation info object
    */
   public AeCompInfo getCompInfo()
   {
      return mCompInfo;
   }

   /**
    * Sets the callback reference so when we complete we'll know where to
    * resume execution.
    * @param aCallback
    */
   public void setCallback(IAeCompensationCallback aCallback)
   {
      mCallback = aCallback;
   }

   /**
    * Getter for the compensation callback reference.
    */
   public IAeCompensationCallback getCallback()
   {
      return mCallback;
   }

   /**
    * Overrides method to call the base class <code>noMoreChildrenToTerminate()</code>
    * followed by invoking the <code>IAeCompensationCallback</code>'s <code>compensationTerminated</code>
    * method.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#noMoreChildrenToTerminate()
    */
   protected void noMoreChildrenToTerminate() throws AeBusinessProcessException
   {
      super.noMoreChildrenToTerminate();
      // noMoreChildrenToTerminate() calls the process objectCompletedWithFault (moving up the parent-child hierarchy).
      // In the case of the compensationHandler, we also have to call back "laterally" i.e the compensation
      // handlers's callback - which is typically a CompensateActivity (explicit or implicit).
      if (getCallback() != null && getState().isFinal())
      {
         getCallback().compensationTerminated(this);
      }
   }

   /**
    * compensation handler is allowed to rethrow
    * @see org.activebpel.rt.bpel.impl.activity.support.AeFCTHandler#isAllowedToRethrow()
    */
   public boolean isAllowedToRethrow()
   {
      return true;
   }

   /**
    * Returns <code>true</code> if and only this scope has any new orphaned
    * IMAs.
    */
   protected boolean hasNewOrphanedIMAs()
   {
      return getProcess().hasNewOrphanedIMAs((AeActivityScopeImpl) getParent());
   }

   /**
    * Faults and removes any orphaned IMAs.
    */
   protected void faultOrphanedIMAs()
   {
      getProcess().faultOrphanedIMAs((AeActivityScopeImpl) getParent(), null);
   }

   /**
    * Overrides method to acquire exclusive locks on resources if this
    * compensation handler belongs to an isolated scope.
    * 
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#acquireResourceLocks()
    */
   protected boolean acquireResourceLocks()
   {
      return !isIsolated() || getProcess().addExclusiveLock(getConvertedResourceLockPaths(), getLocationPath());
   }

   /**
    * Returns <code>true</code> if and only if this compensation handler belongs
    * to an isolated scope.
    */
   protected boolean isIsolated()
   {
      AeActivityScopeImpl scope = (AeActivityScopeImpl) getParent();
      return scope.isIsolatedScope();
   }
   
   /**
    * Returns paths of resources used by this compensation handler. The paths
    * may be customized with instance information.
    */
   protected Set getConvertedResourceLockPaths()
   {
      AeActivityScopeImpl scope = (AeActivityScopeImpl) getParent();
      AeActivityScopeDef scopeDef = (AeActivityScopeDef) scope.getDefinition();
      Set resourcesUsed = scopeDef.getResourcesUsedByCompensationHandler();
      return customizeResourcePaths(resourcesUsed);
   }
}
