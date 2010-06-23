// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeLink.java,v 1.22 2007/04/05 01:39:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import java.util.Collections;
import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeLink;
import org.activebpel.rt.bpel.impl.activity.AeActivityFlowImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Models the <code>link</code>s that are defined within a <code>flow</code>.
 * A link has a single source activity and a single target activity. The
 * use of a link ensures that the target activity will not execute until the
 * status of the link is known and it's transition condition is evaluated.
 *
 * The link implementation keeps a reference to the target activity so it can
 * notify the engine when the link's status changes.
 */
public class AeLink extends AeAbstractBpelObject implements IAeLink
{
   /** the link's status, null if unknown */
   private Boolean mStatus;

   /** target activity for this link */
   private IAeActivity mTargetActivity;

   /** source activity for this link */
   private IAeActivity mSourceActivity;

   /** transition condition for the source */
   private AeTransitionConditionDef mTransitionConditionDef;

   /** flag controls whether or not we notify the process upon our state change */
   private boolean mNotifyProcess = true;

   /**
    * The isolated scope that this link leaves, or <code>null</code> if the
    * link does not leave an isolated scope.
    */
   private AeActivityScopeImpl mSourceIsolatedScope;

   /** <code>true</code> if and only if {@link #mSourceIsolatedScope} has been initialized. */
   private boolean mSourceIsolatedScopeInited;

   /**
    * Creates the link from the def object and flow.
    * @param aDef
    * @param aParent
    */
   public AeLink(AeLinkDef aDef, AeActivityFlowImpl aParent)
   {
      super(aDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * Gets the name of the link from the def.
    * @return String
    */
   public String getName()
   {
      AeLinkDef def = (AeLinkDef) getDefinition();
      return def.getName();
   }

   /**
    * Returns true if the status of this link is known.
    */
   public boolean isStatusKnown()
   {
      return (mStatus != null) && isIsolatedStatusKnown();
   }

   /**
    * Returns <code>true</code> if and only if this link does not leave an
    * isolated scope or the link leaves an isolated scope and the isolated scope
    * completed.
    */
   protected boolean isIsolatedStatusKnown()
   {
      return !hasSourceIsolatedScope() || getSourceIsolatedScope().getState().isFinal();
   }

   /**
    * Returns the status. If the status of this link is unknown then it'll
    * generate an exception since the spec dictates that you don't evaluate join
    * conditions until the status of all inbound links is known.
    * @return true if transition condition met, false if not, exception if not known
    */
   public boolean getStatus()
   {
      if ( ! isStatusKnown() )
      {
         throw new IllegalStateException("call isStatusKnown() before checking status"); //$NON-NLS-1$
      }

      return mStatus.booleanValue() && getIsolatedStatus();
   }

   /**
    * Returns <code>true</code> if and only if this link does not leave an
    * isolated scope or the link leaves an isolated scope and the isolated scope
    * completed successfully.
    */
   protected boolean getIsolatedStatus()
   {
      return !hasSourceIsolatedScope() || ((getSourceIsolatedScope().getState() == AeBpelState.FINISHED) && getSourceIsolatedScope().isNormalCompletion());
   }

   /**
    * Returns raw status for serialization.
    */
   public Boolean getRawStatus()
   {
      return mStatus;
   }

   /**
    * Sets status <i>without</i> notifying the process. This is used when
    * restoring the state of a process from its serialized representation.
    *
    * @param aNewStatus the new status to set
    */
   public void setRawStatus(boolean aNewStatus)
   {
      mStatus = new Boolean(aNewStatus);
      mNotifyProcess = false;
   }

   /**
    * Setter for the status. The links status gets set after its source activity
    * completes.
    * @param aBool
    */
   public void setStatus(boolean aBool)
   {
      // status is immutable
      if(mStatus == null)
      {
         mStatus = new Boolean(aBool);
         if (mNotifyProcess)
            notifyProcess();
      }
   }

   /**
    * Clears the status of the link.  This is used by flow to reset links
    * before execute.
    * TODO that we may need to trigger an event so listeners can reflact this unknown state.
    */
   public void clearStatus()
   {
      mStatus = null;
      mNotifyProcess = true;
   }

   /**
    * Links are evaluated when their source activity completes.
    */
   public void evaluate() throws AeBusinessProcessException
   {
      mNotifyProcess = false;
      if (getTransitionConditionDef() == null)
      {
         setStatus(true);
      }
      else
      {
         boolean result = ((AeAbstractBpelObject) getSourceActivity()).executeBooleanExpression(getTransitionConditionDef());
         getProcess().getEngine().fireEvaluationEvent(getProcess().getProcessId(),
               getTransitionConditionDef().getExpression(), IAeProcessInfoEvent.INFO_LINK_XTN,
               getLocationPath(), Boolean.toString(result));
         setStatus( result );
      }
   }

   /**
    * Notifies the process of the link's state change.
    */
   public void notifyProcess()
   {
      if (hasSourceIsolatedScope())
      {
         getSourceIsolatedScope().deferLinkStatusChanged(this);
      }
      else
      {
         getProcess().linkStatusChanged(this);
      }
   }

   /**
    * Getter for the target activity.
    */
   public IAeActivity getTargetActivity()
   {
      return mTargetActivity;
   }

   /**
    * Setter for the target activity.
    * @param aActivity
    */
   public void setTargetActivity(IAeActivity aActivity)
   {
      mTargetActivity = aActivity;
   }

   /**
    * Getter for the transition condition def.
    */
   public AeTransitionConditionDef getTransitionConditionDef()
   {
      return mTransitionConditionDef;
   }

   /**
    * Setter for the transition condition def.
    * @param aDef
    */
   public void setTransitionConditionDef(AeTransitionConditionDef aDef)
   {
      mTransitionConditionDef = aDef;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return Collections.EMPTY_SET.iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeLink#getSourceActivity()
    */
   public IAeActivity getSourceActivity()
   {
      return mSourceActivity;
   }

   /**
    * Setter for the source activity.
    */
   public void setSourceActivity(IAeActivity aActivity)
   {
      mSourceActivity = aActivity;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      // no-op
   }

   /**
    * Searches for an isolated scope that encloses the given activity and
    * returns that isolated scope if there is one; otherwise, returns
    * <code>null</code>.
    */
   protected AeActivityScopeImpl findIsolatedScope(AeActivityImpl aActivity)
   {
      AeActivityScopeImpl scope = aActivity.findEnclosingScope();
      
      while ((scope != getProcess()) && (scope != null))
      {
         if (scope.isIsolatedScope())
         {
            return scope;
         }

         scope = scope.findEnclosingScope();
      }

      return null;
   }

   /**
    * Returns the isolated scope that this link leaves, or <code>null</code>
    * if the link does not leave an isolated scope.
    */
   protected AeActivityScopeImpl getSourceIsolatedScope()
   {
      if (!mSourceIsolatedScopeInited)
      {
         mSourceIsolatedScopeInited = true;

         // TODO (KR) We can optimize this slightly in the definition layer to determine whether the link leaves an isolated scope.

         // If the link goes from an activity within an isolated scope...
         AeActivityScopeImpl sourceIsolatedScope = findIsolatedScope((AeActivityImpl) getSourceActivity());

         if (sourceIsolatedScope != null)
         {
            AeActivityScopeImpl targetIsolatedScope = findIsolatedScope((AeActivityImpl) getTargetActivity());

            // ... to an activity that is not within an isolated scope or that
            // is within a different isolated scope, then save the source
            // isolated scope.
            if (sourceIsolatedScope != targetIsolatedScope)
            {
               mSourceIsolatedScope = sourceIsolatedScope;
            }
         }
      }

      return mSourceIsolatedScope;
   }
   
   /**
    * Returns <code>true</code> if this link goes from an activity enclosed by
    * an isolated scope to an activity outside of the isolated scope.
    */
   protected boolean hasSourceIsolatedScope()
   {
      return getSourceIsolatedScope() != null;
   }
}
