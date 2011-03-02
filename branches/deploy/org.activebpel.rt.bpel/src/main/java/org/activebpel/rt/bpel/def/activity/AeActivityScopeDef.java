// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityScopeDef.java,v 1.14 2007/03/03 02:45:33 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinksDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.IAeCompensationHandlerParentDef;
import org.activebpel.rt.bpel.def.IAeCorrelationSetsParentDef;
import org.activebpel.rt.bpel.def.IAeEventHandlersParentDef;
import org.activebpel.rt.bpel.def.IAeFaultHandlersParentDef;
import org.activebpel.rt.bpel.def.IAeMessageExchangesParentDef;
import org.activebpel.rt.bpel.def.IAePartnerLinksParentDef;
import org.activebpel.rt.bpel.def.IAeSingleActivityContainerDef;
import org.activebpel.rt.bpel.def.IAeTerminationHandlerParentDef;
import org.activebpel.rt.bpel.def.IAeUncrossableLinkBoundary;
import org.activebpel.rt.bpel.def.IAeVariablesParentDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel scope activity.
 */
public class AeActivityScopeDef extends AeActivityDef 
      implements IAeSingleActivityContainerDef,
      IAeMessageExchangesParentDef, IAePartnerLinksParentDef, 
      IAeVariablesParentDef, IAeFaultHandlersParentDef, 
      IAeCompensationHandlerParentDef, IAeCorrelationSetsParentDef, 
      IAeEventHandlersParentDef, IAeTerminationHandlerParentDef,
      IAeUncrossableLinkBoundary
{
   /** Flag indicating if the scope is isolated. */
   private boolean mIsolated;
   /** The exit on standard fault override. */
   private Boolean mExitOnStandardFault;
   /** The scope def. */
   private AeScopeDef mScope = new AeScopeDef();
   /** optimization point for scope compensation. If this scope and none of its descendants have explicit 
    * compensation behavior then there's no reason to record a snapshot at the scope's completion. */
   private boolean mRecordSnapshot;

   /**
    * Set of resources used by this scope's compensation handler isolation
    * domain. Will be <code>null</code> if this is not an isolated scope.
    */
   private Set mResourcesUsedByCompensationHandler;

   /**
    * Default constructor
    */
   public AeActivityScopeDef()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeVariablesParentDef#getVariableDef(java.lang.String)
    */
   public AeVariableDef getVariableDef(String aVariableName)
   {
      if(getVariablesDef() != null)
         return getVariablesDef().getVariableDef(aVariableName);
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeMessageExchangesParentDef#getMessageExchangesDef()
    */
   public AeMessageExchangesDef getMessageExchangesDef()
   {
      return getScopeDef().getMessageExchangesDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeMessageExchangesParentDef#setMessageExchangesDef(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void setMessageExchangesDef(AeMessageExchangesDef aDef)
   {
      getScopeDef().setMessageExchangesDef(aDef);
   }

   /**
    * Mutator method to set the flag indicating if variable access is serializable.
    *
    * @param aVariableAccessSerializable boolean flag indicating if variable
    *         access is serializable
    */
   public void setVariableAccessSerializable(boolean aVariableAccessSerializable)
   {
      setIsolated(aVariableAccessSerializable);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAePartnerLinksParentDef#setPartnerLinksDef(org.activebpel.rt.bpel.def.AePartnerLinksDef)
    */
   public void setPartnerLinksDef(AePartnerLinksDef aDef)
   {
      getScopeDef().setPartnerLinksDef(aDef);
   }

   /**
    * Returns the underlying scope definition.
    * @return AeScopeDef
    */
   public AeScopeDef getScopeDef()
   {
      return mScope;
   }

   /**
    * Obtains the current activity associated with this object.
    *
    * @return an activity associated with this object
    * @see AeActivityDef
    */
   public AeActivityDef getActivityDef()
   {
      return getScopeDef().getActivityDef();
   }

   /**
    * Set the activity to execute if this scope is called
    *
    * @param aActivity activity to set
    * @see AeActivityDef
    */
   public void setActivityDef(AeActivityDef aActivity)
   {
      getScopeDef().setActivityDef(aActivity);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeActivityContainerDef#replaceActivityDef(org.activebpel.rt.bpel.def.AeActivityDef, org.activebpel.rt.bpel.def.AeActivityDef)
    */
   public void replaceActivityDef(AeActivityDef aOldActivityDef, AeActivityDef aNewActivityDef)
   {
      getScopeDef().replaceActivityDef(aOldActivityDef, aNewActivityDef);
   }

   /**
    * @return fault handler container
    */
   public AeFaultHandlersDef getFaultHandlersDef()
   {
      return getScopeDef().getFaultHandlersDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFaultHandlersParentDef#setFaultHandlersDef(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
    */
   public void setFaultHandlersDef(AeFaultHandlersDef aDef)
   {
      getScopeDef().setFaultHandlersDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAePartnerLinksParentDef#getPartnerLinkDef(java.lang.String)
    */
   public AePartnerLinkDef getPartnerLinkDef(String aPartnerLinkName)
   {
      return getScopeDef().getPartnerLinkDef(aPartnerLinkName);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAePartnerLinksParentDef#getPartnerLinksDef()
    */
   public AePartnerLinksDef getPartnerLinksDef()
   {
      return getScopeDef().getPartnerLinksDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAePartnerLinksParentDef#getPartnerLinkDefs()
    */
   public Iterator getPartnerLinkDefs()
   {
      return getScopeDef().getPartnerLinkDefs();
   }

   /**
    * @return Returns the isolated.
    */
   public boolean isIsolated()
   {
      return mIsolated;
   }

   /**
    * @param aIsolated The isolated to set.
    */
   public void setIsolated(boolean aIsolated)
   {
      mIsolated = aIsolated;
   }

   /**
    * Gets the termination handler def.
    */
   public AeTerminationHandlerDef getTerminationHandlerDef()
   {
      return getScopeDef().getTerminationHandlerDef();
   }

   /**
    * Sets the termination handler.
    *
    * @param aDef
    */
   public void setTerminationHandlerDef(AeTerminationHandlerDef aDef)
   {
      getScopeDef().setTerminationHandlerDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeVariablesParentDef#getVariablesDef()
    */
   public AeVariablesDef getVariablesDef()
   {
      return getScopeDef().getVariablesDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeVariablesParentDef#setVariablesDef(org.activebpel.rt.bpel.def.AeVariablesDef)
    */
   public void setVariablesDef(AeVariablesDef aDef)
   {
      getScopeDef().setVariablesDef(aDef);
   }

   /**
    * Convenience method for getting the scope's catch defs.
    */
   public Iterator getCatchDefs()
   {
      return getScopeDef().getCatchDefs();
   }

   /**
    * Convenience method for getting the scope's catchAll def.
    */
   public AeCatchAllDef getCatchAllDef()
   {
      return getScopeDef().getCatchAllDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCompensationHandlerParentDef#getCompensationHandlerDef()
    */
   public AeCompensationHandlerDef getCompensationHandlerDef()
   {
      return getScopeDef().getCompensationHandlerDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCompensationHandlerParentDef#setCompensationHandlerDef(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void setCompensationHandlerDef(AeCompensationHandlerDef aDef)
   {
      getScopeDef().setCompensationHandlerDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationSetsParentDef#setCorrelationSetsDef(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
    */
   public void setCorrelationSetsDef(AeCorrelationSetsDef aDef)
   {
      getScopeDef().setCorrelationSetsDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationSetsParentDef#getCorrelationSetsDef()
    */
   public AeCorrelationSetsDef getCorrelationSetsDef()
   {
      return getScopeDef().getCorrelationSetsDef();
   }

   /**
    * @return Returns the exitOnStandardFault.
    */
   public Boolean getExitOnStandardFault()
   {
      return mExitOnStandardFault;
   }

   /**
    * @param aExitOnStandardFault The exitOnStandardFault to set.
    */
   public void setExitOnStandardFault(Boolean aExitOnStandardFault)
   {
      mExitOnStandardFault = aExitOnStandardFault;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeEventHandlersParentDef#getEventHandlersDef()
    */
   public AeEventHandlersDef getEventHandlersDef()
   {
      return getScopeDef().getEventHandlersDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeEventHandlersParentDef#setEventHandlers(org.activebpel.rt.bpel.def.AeEventHandlersDef)
    */
   public void setEventHandlers(AeEventHandlersDef aEventHandlers)
   {
      getScopeDef().setEventHandlers(aEventHandlers);
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
   
   /**
    * Getter for the record snapshot flag
    */
   public boolean isRecordSnapshotEnabled()
   {
      return mRecordSnapshot;
   }
   
   /**
    * Setter for the record snapshot flag
    * @param aBool
    */
   public void setRecordSnapshotEnabled(boolean aBool)
   {
      mRecordSnapshot = aBool;
   }

   /**
    * Returns the set of resources used by this scope's compensation handler
    * isolation domain.
    */
   public Set getResourcesUsedByCompensationHandler()
   {
      return mResourcesUsedByCompensationHandler;
   }

   /**
    * Sets the set of resources used by this scope's compensation handler
    * isolation domain.
    */
   public void setResourcesUsedByCompensationHandler(Set aResourcesUsedByCompensationHandler)
   {
      mResourcesUsedByCompensationHandler = aResourcesUsedByCompensationHandler;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeUncrossableLinkBoundary#canCrossInbound()
    */
   public boolean canCrossInbound()
   {
      return AeDefUtil.isBPWS(this)? !isIsolated() : true;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeUncrossableLinkBoundary#canCrossOutbound()
    */
   public boolean canCrossOutbound()
   {
      return canCrossInbound();
   }
}
