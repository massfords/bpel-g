// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeScopeDef.java,v 1.17 2007/01/02 20:45:35 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel scope.
 * fixme can we get rid of scopedef?
 */
public class AeScopeDef extends AeNamedDef implements IAeSingleActivityContainerDef,
      IAeMessageExchangesParentDef, IAePartnerLinksParentDef, IAeCorrelationSetsParentDef,
      IAeVariablesParentDef, IAeFaultHandlersParentDef, IAeCompensationHandlerParentDef,
      IAeEventHandlersParentDef, IAeTerminationHandlerParentDef
{
   /** contains events and alarms */
   private AeEventHandlersDef mEventHandlers;
   /** the single child activity for the scope */
   private AeActivityDef mActivity;
   /** container for the variables */
   private AeVariablesDef mVariables;
   /** container for the correlation sets */
   private AeCorrelationSetsDef mCorrelationSets;
   /** container for the fault handlers */
   private AeFaultHandlersDef mFaultHandlers;
   /** container for the compensation handler */
   private AeCompensationHandlerDef mCompensationHandlerDef;
   /** container for the message exchanges */
   private AeMessageExchangesDef mMessageExchangesDef;
   /** Partner links container. */
   private AePartnerLinksDef mPartnerLinks;
   /** Termination handler. */
   private AeTerminationHandlerDef mTerminationHandler;

   /**
    * Default constructor
    */
   public AeScopeDef()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#getLocationPath()
    */
   public String getLocationPath()
   {
      // The scope def is the base class for AeProcessDef and
      // a member of AeActivityScopeDef. If this def is the
      // process, then it'll have a location path. If it's
      // contained within a scope activity, then use the activity's
      // path for its path.
      //
      // This came up with the addition of messageExchanges since we
      // need to calculate the fully qualified path of the message
      // exchange value when matching receives and replies.
      if (super.getLocationPath()==null && getParent() != null)
         return getParent().getLocationPath();
      else
         return super.getLocationPath();
   }

   /**
    * Gets the set of message exchanges found in this scope.
    */
   public Set getMessageExchanges()
   {
      Set exchanges = Collections.EMPTY_SET;
      if (getMessageExchangesDef() != null)
      {
         exchanges = getMessageExchangesDef().getMessageExchangeValues();
      }
      return exchanges;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeMessageExchangesParentDef#getMessageExchangesDef()
    */
   public AeMessageExchangesDef getMessageExchangesDef()
   {
      return mMessageExchangesDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeMessageExchangesParentDef#setMessageExchangesDef(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void setMessageExchangesDef(AeMessageExchangesDef aMessageExchangesDef)
   {
      mMessageExchangesDef = aMessageExchangesDef;
   }

   /**
    * Returns true if the scope has the messageExchange explicitly declared.
    * @param aMessageExchangeValue
    */
   public boolean declaresMessageExchange(String aMessageExchangeValue)
   {
      return (getMessageExchangesDef() != null)
            && getMessageExchangesDef().declaresMessageExchange(aMessageExchangeValue);
   }

   /**
    * Returns true if the scope defines one or more messageExchange values.
    */
   public boolean hasMessageExchanges()
   {
      return mMessageExchangesDef != null && getMessageExchangesDef().getSize() > 0;
   }

   /**
    * Provide a list of the fault handler objects for the user to iterate .
    *
    * @return iterator of AeFaultHandlerDef objects
    */
   public Iterator getCatchDefs()
   {
      if (mFaultHandlers == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return getFaultHandlersDef().getCatchDefs();
   }

   /**
    * Getter for the <code>catchAll</code> def.
    */
   public AeCatchAllDef getCatchAllDef()
   {
      if (mFaultHandlers == null)
         return null;

      return getFaultHandlersDef().getCatchAllDef();
   }

   /**
    * Utility method to determine if a scoped activity has any fault handlers
    * @return true if has fault handlers false otherwise
    */
   public boolean hasFaultHandlers()
   {
      if (mFaultHandlers == null)
      {
         return false;
      }
      return getFaultHandlersDef().hasCatchOrCatchAll();
   }

   /**
    * Obtains the current activity associated with this object.
    *
    * @return an activity associated with this object
    * @see AeActivityDef
    */
   public AeActivityDef getActivityDef()
   {
      return mActivity;
   }

   /**
    * Set the activity to execute if this scope is called
    *
    * @param aActivity activity to set
    * @see AeActivityDef
    */
   public void setActivityDef(AeActivityDef aActivity)
   {
      mActivity = aActivity;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeActivityContainerDef#replaceActivityDef(org.activebpel.rt.bpel.def.AeActivityDef, org.activebpel.rt.bpel.def.AeActivityDef)
    */
   public void replaceActivityDef(AeActivityDef aOldActivityDef, AeActivityDef aNewActivityDef)
   {
      setActivityDef(aNewActivityDef);
   }

   /**
    * Obtains the compensation handling activity associated with this object.
    *
    * @return an activity associated with this object
    * @see AeActivityDef
    */
   public AeActivityDef getCompensationHandlerActivityDef()
   {
      if (mCompensationHandlerDef == null)
      {
         return null;
      }
      return getCompensationHandlerDef().getActivityDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCompensationHandlerParentDef#getCompensationHandlerDef()
    */
   public AeCompensationHandlerDef getCompensationHandlerDef()
   {
      return mCompensationHandlerDef;
   }

   /**
    * Returns true if the scope has a compensation handler defined.
    */
   public boolean hasCompensationHandlerActivity()
   {
      return getCompensationHandlerActivityDef() != null;
   }

   /**
    * Returns true if the evnt handlers object is not empty.
    */
   public boolean hasEventHandlers()
   {
      boolean hasEventHandler = false;
      if (mEventHandlers != null)
         hasEventHandler = mEventHandlers.hasEventHandler();
      return hasEventHandler;
   }
   
   /**
    * Returns true if the scope has a termination handler defined.
    */
   public boolean hasTerminationHandler()
   {
      return mTerminationHandler != null;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeEventHandlersParentDef#getEventHandlersDef()
    */
   public AeEventHandlersDef getEventHandlersDef()
   {
      return mEventHandlers;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeEventHandlersParentDef#setEventHandlers(org.activebpel.rt.bpel.def.AeEventHandlersDef)
    */
   public void setEventHandlers(AeEventHandlersDef aEventHandlers)
   {
      mEventHandlers = aEventHandlers;
   }

   /**
    * Get a specific variable from the variable collection.
    *
    * @param aVariableName the name of the variable we are searching for.
    *
    * @return a variable object reference or null if not found
    */
   public AeVariableDef getVariableDef(String aVariableName)
   {
      if (getVariablesDef() == null)
         return null;
      else
         return getVariablesDef().getVariableDef(aVariableName);
   }

   /**
    * Get a list of Variables which are part of this process.
    *
    * @return an iterator of the variable collection
    */
   public Iterator getVariableDefs()
   {
      if (getVariablesDef() == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return getVariablesDef().getValues();
   }

   /**
    * Get a list of correlation sets which are part of this process.
    *
    * @return an iterator of the correlation set collection
    */
   public Iterator getCorrelationSetDefs()
   {
      if (getCorrelationSetsDef() == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return getCorrelationSetsDef().getValues();
   }

   /**
    * Returns true if there are variables defined within the scope.
    */
   public boolean hasVariables()
   {
      boolean hasVars = false;
      if (mVariables != null)
         hasVars = (mVariables.getSize() != 0);
      return hasVars;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeVariablesParentDef#getVariablesDef()
    */
   public AeVariablesDef getVariablesDef()
   {
      return mVariables;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeVariablesParentDef#setVariablesDef(org.activebpel.rt.bpel.def.AeVariablesDef)
    */
   public void setVariablesDef(AeVariablesDef aDef)
   {
      mVariables = aDef;
   }

   /**
    * Returns true if there are correlatiuon sets defined within the scope.
    */
   public boolean hasCorrelationSets()
   {
      boolean hasCorrSets = false;
      if (mCorrelationSets != null)
         hasCorrSets = (mCorrelationSets.getSize() != 0);
      return hasCorrSets;
   }

   /**
    * Returns true if the scope defines any partner links.
    */
   public boolean hasPartnerLinks()
   {
      boolean hasPartnerLinks = false;
      if (mPartnerLinks != null)
         hasPartnerLinks = (mPartnerLinks.getSize() != 0);
      return hasPartnerLinks;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationSetsParentDef#getCorrelationSetsDef()
    */
   public AeCorrelationSetsDef getCorrelationSetsDef()
   {
      return mCorrelationSets;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationSetsParentDef#setCorrelationSetsDef(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
    */
   public void setCorrelationSetsDef(AeCorrelationSetsDef aDef)
   {
      mCorrelationSets = aDef;
   }

   /**
    * @return Returns the terminationHandler.
    */
   public AeTerminationHandlerDef getTerminationHandlerDef()
   {
      return mTerminationHandler;
   }

   /**
    * @param aTerminationHandler The terminationHandler to set.
    */
   public void setTerminationHandlerDef(AeTerminationHandlerDef aTerminationHandler)
   {
      mTerminationHandler = aTerminationHandler;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCompensationHandlerParentDef#setCompensationHandlerDef(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void setCompensationHandlerDef(AeCompensationHandlerDef aCompensationHandlerDef)
   {
      mCompensationHandlerDef = aCompensationHandlerDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAePartnerLinksParentDef#getPartnerLinksDef()
    */
   public AePartnerLinksDef getPartnerLinksDef()
   {
      return mPartnerLinks;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAePartnerLinksParentDef#setPartnerLinksDef(org.activebpel.rt.bpel.def.AePartnerLinksDef)
    */
   public void setPartnerLinksDef(AePartnerLinksDef aDef)
   {
      mPartnerLinks = aDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAePartnerLinksParentDef#getPartnerLinkDefs()
    */
   public Iterator getPartnerLinkDefs()
   {
      if (getPartnerLinksDef() == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return getPartnerLinksDef().getPartnerLinkDefs();
   }

   /**
    * Gets the fault handler container. Will create one if it's null.
    */
   public AeFaultHandlersDef getFaultHandlersDef()
   {
      return mFaultHandlers;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFaultHandlersParentDef#setFaultHandlersDef(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
    */
   public void setFaultHandlersDef(AeFaultHandlersDef aDef)
   {
      mFaultHandlers = aDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAePartnerLinksParentDef#getPartnerLinkDef(java.lang.String)
    */
   public AePartnerLinkDef getPartnerLinkDef(String aPartnerLinkName)
   {
      if (getPartnerLinksDef() == null)
         return null;
   
      return getPartnerLinksDef().getPartnerLinkDef(aPartnerLinkName);
   }
}
