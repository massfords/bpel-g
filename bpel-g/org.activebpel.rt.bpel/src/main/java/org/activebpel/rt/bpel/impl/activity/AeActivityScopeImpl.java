// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityScopeImpl.java,v 1.80 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.faults.IAeFaultMatchingStrategy;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeVariablesImpl;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeCompensatableActivity;
import org.activebpel.rt.bpel.impl.IAeFaultHandler;
import org.activebpel.rt.bpel.impl.IAeLink;
import org.activebpel.rt.bpel.impl.activity.support.AeCompInfo;
import org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeCoordinationContainer;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.AeDefaultFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeEventHandlers;
import org.activebpel.rt.bpel.impl.activity.support.AeEventHandlersContainer;
import org.activebpel.rt.bpel.impl.activity.support.AeFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeImplicitCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeImplicitFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeImplicitTerminationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeOnMessage;
import org.activebpel.rt.bpel.impl.activity.support.AeScopeSnapshot;
import org.activebpel.rt.bpel.impl.activity.support.AeTerminationHandler;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.util.AeSequenceIterator;
import org.activebpel.rt.util.AeUtil;

/**
 * Implementation of the bpel scope activity.
 */
public class AeActivityScopeImpl extends AeActivityImpl implements IAeActivityParent, IAeCompensatableActivity
{
   /** flag that indicates the scope has completed normally. Scopes that catch
    * faults w/o rethrowing are not eligible for compensation */
   private boolean mNormalCompletion = true;

   /** maps variable names to variable objects */
   private AeVariablesImpl mVariables;

   /** collection of fault implementation object */
   private Collection mFaultHandlers;

   /** catchAll fault handler */
   private AeDefaultFaultHandler mCatchAll;

   /** maps correlation set name to implementation object */
   private Map mCorrelationSets;

   /** container for the termination handling behavior */
   private AeTerminationHandler mTerminationHandler;

   /** container for the compensation handler */
   private AeCompensationHandler mCompensationHandler;

   /** container for the events */
   private AeEventHandlersContainer mEvents;

   /** child activity */
   private IAeActivity mChild;

   /** contains all of the compensation info for this scope. */
   private AeCompInfo mCompInfo;

   /** currently executing fault handler */
   private IAeFaultHandler mFaultHandler;
   
   /**  Support container for coordinations. */
   private AeCoordinationContainer mCoordinationContainer;

   /** True if the scope has recorded its variables from a previous execution */
   private boolean mSnapshotRecorded = false;

   /** Our map of associated partner links for this process. */
   private Map mPartnerLinks = new HashMap();
   
   /** strategy to handle the termination of scopes */
   private IAeScopeTerminationStrategy mTerminationStrategy;
   
   // TODO (MF) replace the fault matching strategy w/ an impl object for the <faultHandlers> element - leverage the lifecycle events in <faultHandlers> to clear variable state from <catch> impls
   
   /** strategy for matching a fault to a fault handler */
   private IAeFaultMatchingStrategy mFaultMatchingStrategy;

   /**
    * Links that have been deferred because they leave an isolated scope.
    */
   private List mDeferredLinks = new LinkedList();

   /** default constructor for activity */
   public AeActivityScopeImpl(AeActivityScopeDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }

   /**
    * This constructor is here to allow Process to extend Scope.
    * @param aDef
    */
   protected AeActivityScopeImpl(AeBaseDef aDef)
   {
      super(aDef);
   }

   /**
    * Returns true if a variable snapshot has been recorded. If this is true, then
    * a subsequent execution of the scope should clone the local variables to ensure
    * that they are different objects than the ones in the snapshot.
    */
   public boolean isSnapshotRecorded()
   {
      // the previous impl used to clone the variable at the time of the snapshot
      // this flag defers the cloning until the scope executes again (if at all)
      return mSnapshotRecorded;
   }

   /**
    * Setter for the variable snapshot flag
    * @param aBool
    */
   public void setSnapshotRecorded(boolean aBool)
   {
      mSnapshotRecorded = aBool;
   }

   /**
    * Callback from the events container to signal the completion of an alarm.
    * If the child activity for the scope is in a final state then the scope
    * must have been waiting for the completion of the alarm (or some other event)
    * so we'll kick off the disable call again and try to complete the scope.
    * @param aAlarm
    * @throws AeBusinessProcessException
    */
   public void alarmCompleted(AeOnAlarm aAlarm) throws AeBusinessProcessException
   {
      if (isScopeOkToComplete())
      {
         completeEvents();
      }
      else if (!aAlarm.isConcurrent() && aAlarm.getDef().getRepeatEveryDef() != null)
      {
         aAlarm.reschedule();
      }
   }

   /**
    * Callback from the events container to signal the completion of an event.
    * If the child activity for the scope is in a final state and there are no
    * active coordinations, then the scope must have been waiting for the
    * completion of the event (or an alarm) so we'll kick off the disable call
    * again and try to complete the scope. Otherwise, we'll requeue the event.
    * @param aMessage
    * @throws AeBusinessProcessException
    */
   public void eventCompleted(AeOnMessage aMessage) throws AeBusinessProcessException
   {
      if (isScopeOkToComplete())
      {
         completeEvents();
      }
      else
      {
         // If the scope's activity is still executing OR if we're waiting for a
         // coordination event to complete then we should requeue the event
         // If the message is concurrent, then it's already queued so no reason
         // to requeue it.
         if (!aMessage.isConcurrent())
         {
            aMessage.setState(AeBpelState.INACTIVE);
            getProcess().queueObjectToExecute(aMessage);
         }
      }
   }

   /**
    * Returns true if the scope's child activity is in a final state and there are
    * no active coordinations
    */
   protected boolean isScopeOkToComplete()
   {
      // scope is ok to complete if the following conditions are met:
      //
      // it's not already in a final state
      //       AND
      // it's not currently executing a fault handler
      //       AND
      // its child IS in a final state
      //       AND
      // there are no active coordinations
      //       AND
      // it's not currently executing a termination handler
      
      boolean isExecutingTH = mTerminationHandler != null && mTerminationHandler.getState() == AeBpelState.EXECUTING;
      
      return !isExecutingFaultHandler() && !isExecutingTH && !getState().isFinal() && getActivity().getState().isFinal() && !hasActiveCoordinations();
   }

   /**
    * A messageExchange path is the value of the declaring scope's location path
    * plus /aMessageExchangeValue. This method will check this scope and then
    * continuing walking up the enclosing scopes (including the process) until it
    * finds the scope that declares the messageExchange. This method will never
    * return null since a null indicates a problem with the BPEL process that
    * should have been caught during static analysis.
    * @param aMessageExchangeValue
    */
   public String getMessageExchangePath(String aMessageExchangeValue) throws AeBusinessProcessException
   {
      AeScopeDef def = getScopeDef();

      if (def.declaresMessageExchange(aMessageExchangeValue))
      {
         return getLocationPath() + "/" + aMessageExchangeValue; //$NON-NLS-1$
      }
      else
      {
         AeActivityScopeImpl enclosingScope = findEnclosingScope();
         if (enclosingScope == null)
         {
            staticAnalysisFailure(AeMessages.format("AeActivityScopeImpl.NO_DECL_FOR_MESSAGE_EXCHANGE", aMessageExchangeValue)); //$NON-NLS-1$
         }
         return enclosingScope.getMessageExchangePath(aMessageExchangeValue);
      }
   }

   /**
    * Convenience method to avoid having to cast the definition object.
    * <p>
    * Any method that calls <code>getDef()</code> <strong>MUST</strong> be
    * overridden in {@link AeBusinessProcess}, because the definition object for
    * the process is <strong>not</strong> an instance of
    * {@link AeActivityScopeDef}. 
    * </p>
    */
   private AeActivityScopeDef getDef()
   {
      return (AeActivityScopeDef) getDefinition();
   }

   /**
    * Gets the scope definition for this scope.
    */
   protected AeScopeDef getScopeDef()
   {
      // Using getDef() here means that we have to override this method in
      // AeBusinessProcess.
      return getDef().getScopeDef();
   }

   /**
    * Getter for the compensation handler. If there was none defined then we create
    * an instance of the implicit compensation handler.
    */
   public AeCompensationHandler getCompensationHandler()
   {
      if (mCompensationHandler == null)
      {
         mCompensationHandler = new AeImplicitCompensationHandler(this);
         getProcess().addBpelObject(mCompensationHandler.getLocationPath(), mCompensationHandler);
      }
      return mCompensationHandler;
   }
   
   /**
    * Setter for the terminationHandler
    * @param aTerminationHandler
    */
   public void setTerminationHandler(AeTerminationHandler aTerminationHandler)
   {
      mTerminationHandler = aTerminationHandler;
   }
   
   /**
    * Getter for the terminationHandler. If there was none defined then we create
    * an instance of the implicit compensation handler
    */
   public AeTerminationHandler getTerminationHandler()
   {
      if (mTerminationHandler == null)
      {
         mTerminationHandler = new AeImplicitTerminationHandler(this);
      }
      return mTerminationHandler;
   }
   
   /**
    * Returns <code>true</code> if and only if this scope has an implicit
    * termination handler but does not create the termination handler (unlike
    * {@link #getTerminationHandler()}).
    */
   public boolean hasImplicitTerminationHandler()
   {
      return mTerminationHandler instanceof AeImplicitTerminationHandler;
   }

   /**
    * Returns <code>true</code> if and only if this scope has an implicit
    * compensation handler but does not create the compensation handler (unlike
    * {@link #getCompensationHandler()}).
    */
   public boolean hasImplicitCompensationHandler()
   {
      return mCompensationHandler instanceof AeImplicitCompensationHandler;
   }

   /**
    * Returns <code>true</code> if and only if this scope has an explicit
    * compensation handler.
    */
   public boolean hasExplicitCompensationHandler()
   {
      return hasCompensationHandler() && !hasImplicitCompensationHandler();
   }

   /**
    * Returns true if a compensation handler has been created for this activity.
    * @return true if compensationHandler is not null.
    */
   public boolean hasCompensationHandler()
   {
      return mCompensationHandler != null;
   }

   /**
    * Returns true if the scope is currently in the middle of compensating.
    */
   public boolean isCompensating()
   {
      boolean compensating = false;

      if (hasCompensationHandler())
      {
         // comp handler is not in a final state and its not inactive, ergo
         // it must be ready to execute, executing, or faulting
         if (!getCompensationHandler().getState().isFinal() && getCompensationHandler().getState() != AeBpelState.INACTIVE)
         {
            compensating = true;
         }
      }

      return compensating;
   }

   /**
    * Overrides method to terminate the current (if installed) compensation handler if it
    * is in a terminatable state.
    * @see org.activebpel.rt.bpel.impl.IAeCompensatableActivity#terminateCompensationHandler()
    */
   public void terminateCompensationHandler() throws AeBusinessProcessException
   {
      if (mCompensationHandler != null && mCompensationHandler.isTerminatable())
      {
         mCompensationHandler.terminate();
      }
   }

   /**
    * Returns the coordination support container. Creates a new instance if it does not have one.
    * @return Returns the coordinationContainer.
    */
   public AeCoordinationContainer getCoordinationContainer()
   {
      if (mCoordinationContainer == null)
      {
         mCoordinationContainer = new AeCoordinationContainer(this);
      }
      return mCoordinationContainer;
   }

   /**
    * Sets the coordination container.
    * @param aContainer
    */
   public void setCoordinationContainer(AeCoordinationContainer aContainer)
   {
      mCoordinationContainer = aContainer;
   }

   /**
    * Returns true if the scope has any activities under 'active' coordination. An activity
    * that has 'completed' is not considered active.
    * @return Returns true if the scope has any activities under 'active' coordination.
    */
   public boolean hasActiveCoordinations()
   {
      return ( mCoordinationContainer != null && mCoordinationContainer.hasActiveCoordinations() );
   }

   /**
    * Returns true if the scope has any activities under coordination,
    * including completed, and faulted activities.
    *
    * @return Returns true if the scope has any activities under coordination.
    */
   public boolean hasCoordinations()
   {
      return ( mCoordinationContainer != null);
   }

   /**
    * A callback method that signals that all of the scope's active coordinations have
    * completed executing. The scope can now complete.
    */
   public void coordinationCompleted() throws AeBusinessProcessException
   {
      // the check for isExecutingFaultHandler is to avoid illegal state
      // transition of fault handler from executing to dead path

      if (isScopeOkToComplete())
      {
         completeScope();
      }
   }

   /**
    * Completing a scope sets its fault handlers to dead path (since they're no
    * longer reachable and shouldn't have been executed) and then completes
    * the events. If there are events currently executing, then the scope will
    * wait for those events to complete.
    */
   protected void completeScope() throws AeBusinessProcessException
   {
      completeEvents();
   }

   /**
    * If the scope has events then we'll signal the events container that they
    * should be disabled. Otherwise, we'll call the eventsCompleted method which
    * signals the completion of the scope.
    * @throws AeBusinessProcessException
    */
   protected void completeEvents() throws AeBusinessProcessException
   {
      if (hasEvents())
      {
         getEventHandlersContainer().completeEvents();
      }
      else
      {
         eventsCompleted();
      }
   }

   /**
    * Invoked by the coordination framework once the participant under coordination
    * has completed successfully and is compensatable.
    * @param aCoordinationId
    * @param aCompInfo compensation information
    */
   public void coordinatedActivityCompleted(String aCoordinationId, AeCompInfo aCompInfo)
   {
      if (hasCoordinations())
      {
         synchronized( getCoordinationContainer() )
         {
            if (getCoordinationContainer().hasCoordinationId(aCoordinationId))
            {
               // install compensation handler
               enclosedScopeCompleted(this, aCompInfo);
               // move the coordination 'state' from active to completed.
               getCoordinationContainer().coordinationCompleted(aCoordinationId);
            }
         }// synch
      }// if
   }

   /**
    * Invoked by the coordination framework once the participant under coordination
    * has faulted and is not available for compensatable.
    * @param aCoordinationId
    * @param aFault fault.
    */
   public void coordinatedActivityCompletedWithFault(String aCoordinationId, IAeFault aFault)
   {
      if (hasCoordinations())
      {
         synchronized( getCoordinationContainer() )
         {
            if (getCoordinationContainer().hasCoordinationId(aCoordinationId))
            {
               // what happens when fault occurs during compensation?
               getCoordinationContainer().coordinationCompletedWithFault(aCoordinationId);
            }
         }// synch
      }// if
   }


   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * Setter for the compensation handler.
    * @param aHandler
    */
   public void setCompensationHandler(AeCompensationHandler aHandler)
   {
      mCompensationHandler = aHandler;
   }

   /**
    * Getter for the variables impl.
    */
   public IAeVariableContainer getVariableContainer()
   {
      return getVariablesImpl();
   }
   
   /**
    * Getter for the variables impl
    */
   protected AeVariablesImpl getVariablesImpl()
   {
      return mVariables;
   }
   
   /**
    * Setter for the variables impl
    * @param aVariablesImpl
    */
   public void setVariablesImpl(AeVariablesImpl aVariablesImpl)
   {
      mVariables = aVariablesImpl;
   }
   
   /**
    * Looks for the correlation set by its name in this scope only 
    * @param aName
    * @return AeCorrelationSet or null if not defined by this scope
    */
   public AeCorrelationSet getCorrelationSet(String aName)
   {
      return (AeCorrelationSet) getCorrelationSetMap().get(aName);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#findCorrelationSet(java.lang.String)
    */
   public AeCorrelationSet findCorrelationSet(String aName)
   {
      AeCorrelationSet set = getCorrelationSet(aName);
      if (set == null && getParent() != null)
      {
         set = super.findCorrelationSet(aName);
      }
      return set;
   }

   /**
    * Adds a correlation set implementation to the scope.
    * @param aCorrelationSet
    */
   public void addCorrelationSet(AeCorrelationSet aCorrelationSet)
   {
      getCorrelationSetMap().put(aCorrelationSet.getName(), aCorrelationSet);
   }

   /**
    * Getter for the correlation set map. Lazily creates the map.
    */
   protected Map getCorrelationSetMap()
   {
      if (mCorrelationSets == null)
      {
         mCorrelationSets = new HashMap();
      }
      return mCorrelationSets;
   }

   /**
    * Gets the variable by name. Delegates to the parent if it's not handled
    * by this scope.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#findVariable(java.lang.String)
    */
   public IAeVariable findVariable(String aName)
   {
      IAeVariable var = getVariable(aName);
      // added null check here because the parent could be null if this scope
      // is actually a process. could override the method in process but this
      // works fine.
      if (var == null && getParent() != null)
      {
         var = super.findVariable(aName);
      }
      return var;
   }

   /**
    * Gets the variable by name
    * @param aName
    */
   public IAeVariable getVariable(String aName)
   {
      IAeVariable var = null;
      if (getVariableContainer() != null)
      {
         var = (IAeVariable) getVariableContainer().findVariable(aName);
      }
      return var;
   }
   
   
   /**
    * Adds a partner link to the process.
    * 
    * @param aLink
    */
   public void addPartnerLink(AePartnerLink aLink)
   {
      mPartnerLinks.put(aLink.getName(), aLink);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#findPartnerLink(java.lang.String)
    */
   public AePartnerLink findPartnerLink(String aName)
   {
      AePartnerLink plink = (AePartnerLink) mPartnerLinks.get(aName);
      if (plink == null && getParent() != null)
      {
         plink = super.findPartnerLink(aName);
      }
      return plink;
   }
   
   /**
    * Getter for the fault handlers map. Lazily creates the map.
    */
   protected Collection getFaultHandlersCollection()
   {
      if (mFaultHandlers == null)
      {
         mFaultHandlers = new ArrayList();
      }
      return mFaultHandlers;
   }

   /**
    * Helper method to get the iterator of fault handlers or the empty iterator
    * if there are no fault handlers.
    */
   public Iterator getFaultHandlersIterator()
   {
      if (mFaultHandlers == null && mCatchAll == null)
      {
         return Collections.EMPTY_SET.iterator();
      }
      return AeUtil.join(getFaultHandlersCollection().iterator(), mCatchAll);
   }

   /**
    * Adds a fault handler to the scope.
    * @param aFaultHandler
    */
   public void addFaultHandler(AeFaultHandler aFaultHandler)
   {
      getFaultHandlersCollection().add(aFaultHandler);
   }

   /**
    * Sets the <code>catchAll</code> fault handler on this scope.
    */
   public void setDefaultFaultHandler(AeDefaultFaultHandler aCatchAll)
   {
      mCatchAll = aCatchAll;
   }

   /**
    * Getter for the default fault handler (aka catchAll).
    */
   public AeDefaultFaultHandler getDefaultFaultHandler()
   {
      return mCatchAll;
   }

   /**
    * Setter for the events handler container.
    */
   public void setEventHandlersContainer(AeEventHandlersContainer aEvents)
   {
      mEvents = aEvents;
   }

   /**
    * Getter for the events handler container.
    * @return AeEventHandlersContainer or null
    */
   public AeEventHandlersContainer getEventHandlersContainer()
   {
      return mEvents;
   }

   /**
    * Getter for the event handlers.
    * @return AeEventHandlers or null if there are no events associated with
    *          this scope.
    */
   protected AeEventHandlers getEventHandlers()
   {
      if ( ! hasEvents() )
      {
         return null;
      }

      return mEvents.getEventHandlers();
   }

   /**
    * Convenience routine that returns true if the scope has events.
    * @return true if there is a event container present.
    */
   protected boolean hasEvents()
   {
      return mEvents != null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeActivityParent#addActivity(org.activebpel.rt.bpel.IAeActivity)
    */
   public void addActivity(IAeActivity aActivity)
   {
      mChild = aActivity;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityImpl#acquireResourceLocks()
    */
   protected boolean acquireResourceLocks()
   {
      // Using getDef() here means that we have to override this method in
      // AeBusinessProcess.
      if (getDef().isIsolated())
         return getProcess().addExclusiveLock(getConvertedResourceLockPaths(), getLocationPath());
      else
         return getProcess().addSharedLock(getConvertedResourceLockPaths(), getLocationPath());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      clearDeferredLinks();
      // reset the completion flag in case we're being executed multiple times from a loop
      setNormalCompletion(true);
      executeEventHandler();
      getProcess().queueObjectToExecute(getActivity());
   }

   /**
    * Executes the events and alarms for the scope's event handler if present.
    * @throws AeBusinessProcessException
    */
   protected void executeEventHandler() throws AeBusinessProcessException
   {
      if (hasEvents())
      {
         // we still queue the container to execute so it'll transition through
         // its states cleanly.
         getProcess().queueObjectToExecute(getEventHandlersContainer());

         // The container's execute() is a no-op. We're installing the alarms and
         // messages here to ensure that they're present on the execution queue
         // BEFORE any other activities that may get queued as a result of
         // executing the scope's child activity.
         getEventHandlersContainer().installAlarms();
         getEventHandlersContainer().installMessages();
      }
   }

   /**
    * Correlation Sets get re-initialized each time the scope executes.
    */
   private void clearCorrelationSetState(boolean aCloneFlag)
   {
      for (Iterator iter = getCorrelationSetMap().entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry) iter.next();
         AeCorrelationSet corrSet = (AeCorrelationSet) entry.getValue();
         if (aCloneFlag)
         {
            corrSet = (AeCorrelationSet) corrSet.clone();
            entry.setValue(corrSet);
         }
         corrSet.clear();
      }
   }

   /**
    * Partner links get re-initialized each time the scope executes. This method will
    * clear their state 
    * 
    * @param aCloneFlag
    */
   private void clearPartnerLinkState(boolean aCloneFlag) throws AeBusinessProcessException
   {
      if (aCloneFlag)
      {
         for (Iterator iter = getPartnerLinks().entrySet().iterator(); iter.hasNext(); )
         {
            Map.Entry entry = (Map.Entry) iter.next();
            AePartnerLink plink = (AePartnerLink) entry.getValue();
            entry.setValue((AePartnerLink) plink.clone());
         }
      }
   }

   /**
    * Initialize all partner links in the scope.
    */
   protected void initializePartnerLinks() throws AeBusinessProcessException
   {
      for (Iterator iter = getPartnerLinks().values().iterator(); iter.hasNext(); )
      {
         AePartnerLink plink = (AePartnerLink)iter.next();
         getProcess().initPartnerLink(plink);
      }
   }

   /**
    * Initializes the variables if variable initialization logic is present
    * @throws AeBusinessProcessException
    */
   protected void initializeVariables() throws AeBusinessProcessException
   {
      if (getVariableContainer() != null)
         getVariableContainer().initialize();
   }      

   /**
    * Scopes only have a single child, ergo this should mark us as completed.
    * Need to uninstall any events and alarms and then wait for any active
    * events.
    * @param aChild
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childComplete(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      // checking to see if the child is our activity. This is to avoid triggering the
      // complete code for fault handlers that go to dead path
      if (aChild == getActivity() && aChild.getState() != AeBpelState.DEAD_PATH)
      {
         if (isScopeOkToComplete())
         {
            completeScope();
         }
      }
   }
   
   /**
    * Clears the currently executing fault handler reference if the child that was just terminated
    * was the executing fault handler.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#childTerminated(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childTerminated(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      if (aChild == getFaultHandler())
      {
         setFaultHandler(null);
      }
      super.childTerminated(aChild);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childCompleteWithFault(org.activebpel.rt.bpel.impl.IAeBpelObject, org.activebpel.rt.bpel.IAeFault)
    */
   public void childCompleteWithFault(IAeBpelObject aChild, IAeFault aFault)
   throws AeBusinessProcessException
   {
      triggerFaultHandling(aFault);
   }

   /**
    * Returns true if the scope is currently executing a fault handler. This is
    * to signal that the scope is ineligible for executing any other fault handlers.
    */
   public boolean isExecutingFaultHandler()
   {
      return getFaultHandler() != null;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#handleFault()
    */
   protected void handleFault() throws AeBusinessProcessException
   {
      IAeFault fault = getFault();
      setNormalCompletion(false);
      IAeFaultHandler matched = findOrCreateMatchingFaultHandler();
      setFault(null);
      matched.setHandledFault(fault);
      setFaultHandler(matched);
      getProcess().queueObjectToExecute(matched);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#handleTermination()
    */
   protected void handleTermination() throws AeBusinessProcessException
   {
      getTerminationStrategy().onHandleTermination(this);
   }

   /**
    * Called from the termination handler when its done executing
    */
   public void terminationHandlerComplete() throws AeBusinessProcessException
   {
      setState(AeBpelState.TERMINATED);
   }

   /**
    * Callback method from the fault handler. Indicates that the fault handler completed
    * execution w/o rethrowing a fault. The Scope's state remains at faulted, but
    * its links are still evaluated and regular execution continues.
    *
    * @throws AeBusinessProcessException
    */
   public void faultHandlerComplete(IAeFault aFault) throws AeBusinessProcessException
   {
      setFaultHandler(null);
      faultOrphanedIMAs(aFault);

      if (isFaultingOrRetrying() || (getParent() != null && getParent().isTerminating()) || isProcessExiting())
      {
         setState(AeBpelState.TERMINATED);
      }
      else if (hasNewOrphanedIMAs())
      {
         // fixme (MF) Fault immediately.
//         faultOrphanedIMAs(aFault);
         faultHandlerCompleteWithFault(getFaultFactory().getMissingReply());
      }
      else
      {
         objectCompleted();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#isFaultingOrRetrying()
    */
   public boolean isFaultingOrRetrying()
   {
      return super.isFaultingOrRetrying() || getProcess().getFaultingActivityLocationPaths().contains(getLocationPath());
   }

   /**
    * Callback method from the fault handler that signals a rethrowing of a fault.
    * This causes the activity to complete with a fault and propagate upwards.
    * @param aFault
    * @throws AeBusinessProcessException
    */
   public void faultHandlerCompleteWithFault(IAeFault aFault) throws AeBusinessProcessException
   {
      setFaultHandler(null);
      /*
       * At this point, we've already gone through our fault handling code - which
       * is probably obvious from the method name. If we call the base class's impl
       * of objectCompletedWithFault, then we'll take another cycle through the
       * fault handling and termination code which isn't something we want to do.
       * This is why we're calling objectCompletedWithFault on the process and
       * we're the only other class outside of the business process and base class
       * to do such.
       */
      getProcess().objectCompletedWithFault(this, aFault);
   }
   
   /**
    * Return true if this scope handles the fault.
    * @param aFault
    */
   public boolean isFaultHandledByScope( IAeFault aFault )
   {
      // added check for retry in order to consume any faults raised during compensating a scope if its enclosing scope is retrying
      boolean retry = getProcess().getFaultingActivityLocationPaths().contains(getLocationPath());
      return retry || getDefaultFaultHandler() != null || getFaultMatchingStrategy().selectMatchingCatch(getProcess().getProcessPlan(), getFaultHandlersCollection().iterator(), aFault) != null;
   }

   /**
    * Find the matching fault handler by scanning the <code>catch</code> blocks
    * in our definition. If there is no match, then we fall back to the
    * <code>catchAll</code>. If there is no <code>catchAll</code>, then we
    * create the implicit fault handler and return that.
    * @return IAeBpelObject - the fault handler (will never be null)
    * @throws AeBusinessProcessException
    */
   private IAeFaultHandler findOrCreateMatchingFaultHandler()
      throws AeBusinessProcessException
   {
      IAeFaultHandler matched = null;

      // special check for our internal retry fault. This fault should trigger
      // compensation for the eligible scopes but cannot be caught by an fault
      // handler - even a catch all.
      if (getFaultFactory().isRetryFault(getFault()))
      {
         matched = createImplicitFaultHandler(getFault());
      }
      else
      { 
         matched = (IAeFaultHandler) getFaultMatchingStrategy().selectMatchingCatch(getProcess().getProcessPlan(), getFaultHandlersCollection().iterator(), getFault());
         
         if (matched == null)
            matched = getDefaultFaultHandler();
         
         if (matched == null)
            matched = createImplicitFaultHandler(getFault());
      }

      setAllOtherFaultHandlersToDeadPath(matched);

      return matched;
   }

   /**
    * Sets all of the other fault handlers to dead path except for the one that
    * we matched against and are about to execute.
    * @param aMatched
    */
   private void setAllOtherFaultHandlersToDeadPath(IAeBpelObject aMatched) throws AeBusinessProcessException
   {
      setAllOtherToDeadPath(aMatched, getFaultHandlersIterator());
   }

   /**
    * Broken out to allow Process to override since the process terminates on
    * any uncaught fault.
    * @param aFault
    */
   public IAeFaultHandler createImplicitFaultHandler(IAeFault aFault)
   {
      AeImplicitFaultHandler faultHandler = new AeImplicitFaultHandler(this, aFault);
      IAeBusinessProcessInternal process = faultHandler.getProcess();
      process.addBpelObject(faultHandler.getLocationPath(), faultHandler);
      return faultHandler;
   }

   /**
    * A callback method that signals that all of the scope's active events have
    * completed executing. The scope can now complete.
    */
   public void eventsCompleted() throws AeBusinessProcessException
   {
      if (hasNewOrphanedIMAs())
      {
         IAeFault fault = getFaultFactory().getMissingReply();
         setFault(fault);
         handleFault();
      }
      else
      {
         setAllOtherFaultHandlersToDeadPath(null);
         objectCompleted();
      }
   }

   /**
    * Returns the associated main scope activity.
    */
   public IAeActivity getActivity()
   {
      return mChild;
   }

   /**
    * Scopes only have a single child activity but this method includes the fault
    * handlers, default fault handler, and compensation handler because these
    * bpel objects need to have the state change propagated to them.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      AeSequenceIterator seqIterator = new AeSequenceIterator();
      seqIterator.add(getActivity());
      seqIterator.add(mCompensationHandler);
      seqIterator.add(getFaultHandlersIterator());
      seqIterator.add(getEventHandlersContainer());
      seqIterator.add(mTerminationHandler);

      // If the current fault handler is the implicit one, then it hasn't been
      // added to the iterator yet.
      if (getFaultHandler() instanceof AeImplicitFaultHandler)
      {
         seqIterator.add(getFaultHandler());
      }
      // If coordinated, then add the coordination container.
      if (hasCoordinations())
      {
         seqIterator.add(getCoordinationContainer());
      }
      return seqIterator;
   }

   /**
    * If the scope completes normally then it will notify the enclosing scope
    * of the successful completion and of any available compensation information.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#objectCompleted()
    */
   protected void objectCompleted() throws AeBusinessProcessException
   {
      if ( isNormalCompletion() )
      {
         // find the enclosing scope and add notify it that we've completed.
         AeActivityScopeImpl enclosingScope = findEnclosingScope();
         if (enclosingScope != null)
         {
            enclosingScope.enclosedScopeCompleted(this, getCompInfo());
            setSnapshotRecorded(true);
         }
      }

      clearCompInfo();
      super.objectCompleted();
   }

   /**
    * Sets the comp info to null. We clear the reference after we've successfully
    * completed and notified the enclosing scope of our completion. The parent
    * scope is responsible for maintaining our compensation information. This
    * reference is only non-null during the execution of the scope.
    */
   protected void clearCompInfo()
   {
      mCompInfo = null;
   }

   /**
    * Getter for the AeCompInfo object.
    */
   public AeCompInfo getCompInfo()
   {
      if (mCompInfo == null)
      {
         mCompInfo = new AeCompInfo(this);
      }
      return mCompInfo;
   }

   /**
    * Returns <code>true</code>if and only if this scope has compensation info.
    */
   public boolean hasCompInfo()
   {
      return mCompInfo != null;
   }

   /**
    * Records the scope's variable data and corr sets on the snapshot object
    * @param aSnapshot
    */
   public void recordScopeSnapshot(AeScopeSnapshot aSnapshot)
   {
      if (getVariableContainer() != null)
         aSnapshot.addVariables(getVariablesImpl().getMap());
      if (mCorrelationSets != null)
         aSnapshot.addCorrelationSets(getCorrelationSetMap());
      if (mPartnerLinks != null)
         aSnapshot.addPartnerLinks(getPartnerLinks());
   }
   
   /**
    * Returns true if the scope should record a snapshot of its variables since 
    * one or more nested scopes contain explicit compensation behavior
    */
   public boolean isRecordSnapshotEnabled()
   {
      // Using getDef() here means that we have to override this method in
      // AeBusinessProcess.
      return getDef().isRecordSnapshotEnabled();
   }

   /**
    * Returns true if the scope completed normally. An abnormal completion will
    * prevent the compensation handler from being installed for this execution.
    */
   public boolean isNormalCompletion()
   {
      return mNormalCompletion;
   }

   /**
    * Setter for the normal termination flag. A false value will prevent the
    * compensation handler from being installed for this execution of the scope.
    * @param aB
    */
   public void setNormalCompletion(boolean aB)
   {
      mNormalCompletion = aB;
   }

   /**
    * Adds the compensation info object to the scope's existing compensation
    * info. Once this scope completes, it'll notify its enclosing scope through
    * this same method and further propagate the compensation info.
    * @param aCompInfo
    */
   public void enclosedScopeCompleted(AeActivityScopeImpl aScope, AeCompInfo aCompInfo)
   {
      getCompInfo().add(aCompInfo, aScope);
   }

   /**
    * Set by the compensate activity during compensation.
    */
   public void setCompInfo(AeCompInfo aCompInfo)
   {
      mCompInfo = aCompInfo;
   }

   /**
    * Scopes handle terminate calls by generating a <code>bpws:forcedTermination</code>
    * exception and then handling that exception through the regular fault handling
    * process. This will terminate the active child activity as well as any
    * events that have been queued. After the termination completes, the scope
    * will then execute the matched fault handler for the  <code>bpws:forcedTermination</code>
    * fault.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#terminate()
    */
   public void terminate() throws AeBusinessProcessException
   {
      if (isProcessExiting())
      {
         // The call to super here will clear the fault and terminate all of the children.
         super.terminate();
      }
      else
      {
         getTerminationStrategy().onStartTermination(this);
      }
   }
   
   /**
    * Returns the next child available for termination. While Scopes only have a
    * single primary activity, a scope's events (if any) must be terminated when
    * the scope terminated. As such, this method will first return the primary
    * child if it's available for termination and then the event container.
    * @return IAeBpelObject or null if there are no active bpel objects that require termination.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#getChildrenForTermination()
    */
   public Iterator getChildrenForTermination() throws AeBusinessProcessException
   {
      LinkedList childrenToTerminate = new LinkedList();
      childrenToTerminate.add(getActivity());
      
      if (hasEvents() && getEventHandlersContainer().getState().isTerminatable())
      {
         childrenToTerminate.add(getEventHandlersContainer());
      }
      
      if (isExecutingFaultHandler())
      {
         childrenToTerminate.add(getFaultHandler());
      }
      return childrenToTerminate.iterator();
   }

   /**
    * Returns true if the process is exiting
    */
   private boolean isProcessExiting()
   {
      return getProcess().isExiting();
   }

   /**
    * Overriding this in order to clear the variable state for the scope when it
    * is ready to execute. This was previously accomplished by overriding isReadyToExecute()
    * but that method now has to handle serializable scopes and as such will not
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#setState(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void setState(AeBpelState aNewState)
      throws AeBusinessProcessException
   {
      super.setState(aNewState);
      if (aNewState == AeBpelState.READY_TO_EXECUTE)
      {
         boolean makeClones = isSnapshotRecorded();
         if (getVariableContainer() != null)
            getVariablesImpl().clearVariableState(makeClones);
         clearCorrelationSetState(makeClones);
         clearPartnerLinkState(makeClones);

         AeExtensionNotifier.onInitialize(this);
         initializePartnerLinks();
         initializeVariables();
         setSnapshotRecorded(false);
      }
      else if (aNewState == AeBpelState.DEAD_PATH)
      {
         // notify the process of any links that may have gone dead path as a
         // result of the scope having gone dead path. 
         notifyProcessOfLinkChanges();
      }
   }

   /**
    * Returns currently executing fault handler.
    */
   public IAeFaultHandler getFaultHandler()
   {
      return mFaultHandler;
   }

   /**
    * Sets currently executing fault handler.
    *
    * @param aFaultHandler the fault handler to set.
    */
   public void setFaultHandler(IAeFaultHandler aFaultHandler)
   {
      mFaultHandler = aFaultHandler;
   }

   /**
    * @return Returns the partnerLinks.
    */
   public Map getPartnerLinks()
   {
      return mPartnerLinks;
   }
   
   /**
    * Returns the partner link declared on this scope, if one exists.
    * 
    * @param aPartnerLinkName
    */
   public AePartnerLink getPartnerLink(String aPartnerLinkName)
   {
      return (AePartnerLink) getPartnerLinks().get(aPartnerLinkName);
   }

   /**
    * @return Returns the terminationStrategy.
    */
   public IAeScopeTerminationStrategy getTerminationStrategy()
   {
      return mTerminationStrategy;
   }

   /**
    * @param aTerminationStrategy The terminationStrategy to set.
    */
   public void setTerminationStrategy(
         IAeScopeTerminationStrategy aTerminationStrategy)
   {
      mTerminationStrategy = aTerminationStrategy;
   }

   /**
    * @return Returns the faultMatchingStrategy.
    */
   public IAeFaultMatchingStrategy getFaultMatchingStrategy()
   {
      return mFaultMatchingStrategy;
   }

   /**
    * @param aFaultMatchingStrategy The faultMatchingStrategy to set.
    */
   public void setFaultMatchingStrategy(
         IAeFaultMatchingStrategy aFaultMatchingStrategy)
   {
      mFaultMatchingStrategy = aFaultMatchingStrategy;
   }

   /**
    * Clears the list of deferred links.
    */
   protected void clearDeferredLinks()
   {
      getDeferredLinks().clear();
   }

   /**
    * This is a callback to defer notifying the process of the status of the
    * given link until this isolated scope completes.
    *
    * @param aLink The link that has changed its status.
    */
   public void deferLinkStatusChanged(IAeLink aLink)
   {
      getDeferredLinks().add(aLink);
   }
   
   /**
    * Returns the list of links that have been deferred because they leave an
    * isolated scope.
    */
   protected List getDeferredLinks()
   {
      return mDeferredLinks;
   }

   /**
    * Returns <code>true</code> if and only if this is an isolated scope.
    */
   public boolean isIsolatedScope()
   {
      // Using getDef() here means that we have to override this method in
      // AeBusinessProcess.
      return getDef().isIsolated();
   }

   /**
    * Overrides method to notify the process of link state changes for links
    * that were deferred because they leave an isolated scope.
    *
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityImpl#notifyProcessOfLinkChanges()
    */
   protected void notifyProcessOfLinkChanges()
   {
      for (Iterator i = getDeferredLinks().iterator(); i.hasNext(); )
      {
         IAeLink link = (IAeLink) i.next();
         getProcess().linkStatusChanged(link);
      }

      super.notifyProcessOfLinkChanges();
   }

   /**
    * Returns <code>true</code> if and only this scope has any new orphaned
    * IMAs.
    */
   protected boolean hasNewOrphanedIMAs()
   {
      return getProcess().hasNewOrphanedIMAs(this);
   }

   /**
    * Faults and removes any orphaned IMAs.
    */
   protected void faultOrphanedIMAs(IAeFault aFault)
   {
      getProcess().faultOrphanedIMAs(this, aFault);
   }
}