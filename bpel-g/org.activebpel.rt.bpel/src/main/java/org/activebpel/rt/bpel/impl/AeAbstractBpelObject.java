// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeAbstractBpelObject.java,v 1.89 2008/02/17 21:37:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeStaticAnalysisException;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.util.AeLocationPathUtils;
import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionFactory;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.AeLink;
import org.activebpel.rt.bpel.impl.expr.AeExpressionRunnerContext;
import org.activebpel.rt.bpel.impl.expr.AeExpressionRunnerVariableResolver;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext;
import org.activebpel.rt.bpel.impl.visitors.IAeVisitable;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.activebpel.rt.xml.def.AeXmlDefUtil;
import org.activebpel.rt.xml.schema.AeSchemaDuration;

/**
 * Base class for BPEL objects. Provides access to a parent object and 
 * base definition object. Subclasses will include Activities as well as supporting
 * objects like those that model <code>case</code>s, <code>catch</code>...etc.
 */
abstract public class AeAbstractBpelObject implements IAeExecutableBpelObject, IAeVisitable,
      IAeNamespaceContext, IAeFunctionFactory
{
   /** non-null when we're in the middle of handling a fault and need to terminate our child activities asynchronously */
   private IAeFault mFault;

   /** The parent object. */
   private IAeBpelObject mParent;

   /** The process object. */
   private IAeBusinessProcessInternal mProcess;

   /** Base definition object. */
   private AeBaseDef mDef;

   /** The state of the bpel object. */
   private AeBpelState mState = AeBpelState.INACTIVE;

   /**
    * flag that indicates we're in the process of terminating. Gets set to true
    * in terminate method and then set back to false once the object's state changes. 
    */
   private boolean mTerminating;

   /** stores the custom location path */
   private String mLocationPath;

   /** stores the custom location id */
   private int mLocationId;
   
   /** 
    * non-persistent variable which is set to true while we're starting the termination of this object. 
    * Used to avoid notifying parent of completion while we're terminating 
    */
   private boolean mStartingTermination = false;

   /**
    * Requires the base definition object and
    * @param aDef
    * @param aParent
    */
   public AeAbstractBpelObject(AeBaseDef aDef, IAeBpelObject aParent)
   {
      mParent = (IAeBpelObject) aParent;
      mDef = aDef;
   }

   /**
    * Returns true if we're suppressing the join condition failure, false
    * otherwise.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#isSuppressJoinConditionFailure()
    */
   public boolean isSuppressJoinConditionFailure()
   {
      return getParent().isSuppressJoinConditionFailure();
   }

   /**
    * Getter for the definition
    */
   public AeBaseDef getDefinition()
   {
      return mDef;
   }

   /**
    * Returns the location path associated with the definition object.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getLocationPath()
    */
   public String getLocationPath()
   {
      if (hasCustomLocationPath())
      {
         return mLocationPath;
      }
      else
      {
         return getDefinition().getLocationPath();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#setLocationPath(java.lang.String)
    */
   public void setLocationPath(String aPath)
   {
      mLocationPath = aPath;
   }

   /**
    * Returns the location id assoicated with the definition object.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getLocationId()
    */
   public int getLocationId()
   {
      if (hasCustomLocationPath())
      {
         return mLocationId;
      }
      else
      {
         return getDefinition().getLocationId();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#setLocationId(int)
    */
   public void setLocationId(int aId)
   {
      mLocationId = aId;
   }

   /**
    * Returns true if the implementation object's location path differs from the
    * definition object. This will be true in cases where we create multiple
    * implementation objects from a single definition object (parallel forEach)
    */
   public boolean hasCustomLocationPath()
   {
      return mLocationPath != null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#hasLocationId()
    */
   public boolean hasLocationId()
   {
      return true;
   }

   /**
    * Getter for the parent object.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getParent()
    */
   public IAeBpelObject getParent()
   {
      return mParent;
   }

   /**
    * Base implementation which returns null. Activities should extend this to
    * return the appropriate link.
    * @param aLinkName the name of the link we are interested in.
    */
   public AeLink findTargetLink(String aLinkName)
   {
      return null;
   }

   /**
    * Delegates the call to the parent.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#findVariable(java.lang.String)
    */
   public IAeVariable findVariable(String aName)
   {
      return getParent().findVariable(aName);
   }

   /**
    * Delegates the call to the parent.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#findPartnerLink(java.lang.String)
    */
   public AePartnerLink findPartnerLink(String aName)
   {
      return getParent().findPartnerLink(aName);
   }

   /**
    * Delegates the call to the parent and then caches.
    * TODO we may want to set this automatically when constructed as it is called a lot.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getProcess()
    */
   public IAeBusinessProcessInternal getProcess()
   {
      if (mProcess == null)
         mProcess = getParent().getProcess();
      return mProcess;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#findCorrelationSet(java.lang.String)
    */
   public AeCorrelationSet findCorrelationSet(String aName)
   {
      return getParent().findCorrelationSet(aName);
   }

   /**
    * Does nothing, if the derived class has no children it need not override.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childComplete(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      // do nothing
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childTerminated(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childTerminated(IAeBpelObject aChild)
         throws AeBusinessProcessException
   {
      if (!isStartingTermination())
      {
         if (allEligibleChildrenAreTerminated())
         {
            noMoreChildrenToTerminate();
         }
         // This is a legacy check.
         // Previously saved processes that were in the middle of catching a fault or
         // terminating activities get restored with their terminating flag cleared.
         // The result is that when a child terminates we'll hit this code which will
         // trigger any remaining activities that need to be terminated.
         else if (!isTerminating())
         {
            startTermination();
         }
      }
   }

   /**
    * Passes child completion fault to parent for handling.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childCompleteWithFault(org.activebpel.rt.bpel.impl.IAeBpelObject, org.activebpel.rt.bpel.IAeFault)
    */
   public void childCompleteWithFault(IAeBpelObject aChild, IAeFault aFaultObject) throws AeBusinessProcessException
   {
      objectCompletedWithFault(aFaultObject);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childIsFaulting(org.activebpel.rt.bpel.impl.IAeBpelObject, org.activebpel.rt.bpel.IAeFault)
    */
   public void childIsFaulting(IAeBpelObject aChild, IAeFault aFaultObject)
   {
      setFault(aFaultObject);
   }

   /**
    * Indicates that the object has completed. Sets the objects state to
    * completed and then propagates the call through the process.
    * @throws AeBusinessProcessException
    */
   protected void objectCompleted() throws AeBusinessProcessException
   {
      getProcess().objectCompleted(this);
      notifyParentOfCompletion();
   }

   /**
    * Notifies the parent of our completion
    */
   protected void notifyParentOfCompletion() throws AeBusinessProcessException
   {
      if (getParent() != null && !getParent().getState().isFinal())
      {
         ((IAeExecutableBpelObject) getParent()).childComplete(this);
      }
   }

   /**
    * Determines is process should be suspended due to an uncaught fault or
    * should the process procees with normal fault completion.
    * 
    * @param aFault
    * @throws AeBusinessProcessException
    */
   public void objectCompletedWithFault(IAeFault aFault) throws AeBusinessProcessException
   {
      // fixme (MF) This method should be protected but it is public  
      //            This is due to execution queue's interface for executable items. 
      //            Should create an adapter object for bpel objects in the 
      //            queue to avoid exposing this method.
      if( isSuspendable(aFault) )
      {
         setFault( aFault );
         setState( AeBpelState.FAULTING );
         getProcess().suspend(new AeSuspendReason(AeSuspendReason.SUSPEND_CODE_AUTOMATIC, getLocationPath(), null));
      }
      else
      {
         objectCompletedWithFaultInternal(aFault);
      }
   }

   /**
    * Indicates that the object has completed with a fault. Sets the objects state 
    * to faulted and propagates the fault through the process. If the exitOnStandardFault
    * is enabled, the process will exit immediately.
    * @param aFault
    * @throws AeBusinessProcessException
    */
   protected void objectCompletedWithFaultInternal(IAeFault aFault) throws AeBusinessProcessException
   {
      if ( isStdFaultAndExitEnabled(aFault) )
      {
         getProcess().terminate();
      }
      else
      {
         triggerFaultHandling(aFault);
      }
   }

   /**
    * Return true if the fault is a standard fault and the existOnStandardFault is set to yes
    * @param aFault
    */
   protected boolean isStdFaultAndExitEnabled(IAeFault aFault)
   {
      boolean standardFaultForExit = getFaultFactory().isStandardFaultForExit(aFault);
      return standardFaultForExit
            && AeDefUtil.isExitOnStandardFaultEnabled(getDefinition());
   }

   /**
    * Return true if the fault is uncaught and the associated process instance
    * should be suspended if it encounters an uncaught fault.
    */
   protected boolean isSuspendable(IAeFault aFault)
   {
      // Note: broke out into local variables for easier debugging, except for the isFaultCaught which has to walk the process
      boolean suspendable = aFault.isSuspendable();
      boolean suspendProcessOnUncaughtFaultEnabled = getProcess().getProcessPlan().isSuspendProcessOnUncaughtFaultEnabled();
      boolean stdFaultAndExitEnabled = isStdFaultAndExitEnabled(aFault);
      // fixme should I add a check for an uncaught fault within a termination handler. The fault will not propagate but the user may still want to debug the process
      return suspendable &&  suspendProcessOnUncaughtFaultEnabled && (stdFaultAndExitEnabled || isFaultUncaught( aFault, false, findEnclosingScope() ));
   }

   /**
    * Return true if the enclosing scope does not handle the
    * given fault.
    * @param aFault
    */
   protected boolean isFaultUncaught(IAeFault aFault, boolean aIsHandled, AeActivityScopeImpl aScope )
   {
      if (aScope != null)
      {
         aIsHandled = aScope.isFaultHandledByScope(aFault);

         if (!aIsHandled)
         {
            aIsHandled = !isFaultUncaught( aFault, aIsHandled, aScope.findEnclosingScope() );
         }
      }
      return !aIsHandled;
   }

   /**
    * Walks up the object hierarchy until it finds the enclosing scope for this
    * object.
    */
   public AeActivityScopeImpl findEnclosingScope()
   {
      for (IAeBpelObject parent = getParent(); parent != null; parent = parent.getParent())
      {
         if (parent instanceof AeActivityScopeImpl)
         {
            return (AeActivityScopeImpl) parent;
         }
      }

      // should never get here since we'll hit the process at some point
      // unless of course we ARE the process at...
      return null;
   }

   /**
    * Propagates the terminate call to all of its child objects.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#terminate()
    */
   public void terminate() throws AeBusinessProcessException
   {
      // The activity may be in a state where it has a fault due to processing
      // a fault raised by one of its children. If we have a fault present and
      // are asked to terminate, then we should clear the fault and proceed with
      // our standard termination routine. This will result in all of the child
      // nodes being terminated and the container then reporting its terminated
      // state to its parent. One case where this arises is a fault during DPE
      // where an uncaught bpws:joinFailure bubbles up to a top level fault handler
      // or the implicit one.
      triggerFaultHandling(null);
   }

   /**
    * Called when an activity is faulting and the user opts to retry the activity.
    * The activity will terminate and at the end of its termination, the process
    * will transition the activity back into an executing state.
    * @throws AeBusinessProcessException
    */
   public void terminateForRetry() throws AeBusinessProcessException
   {
      IAeFault retryFault = getFaultFactory().getRetryFault();
      triggerFaultHandling(retryFault);
   }
   
   /**
    * Convenience method for setting a fault and triggering the fault handling
    * behavior for the object.
    * @param aFault
    * @throws AeBusinessProcessException 
    */
   public void triggerFaultHandling(IAeFault aFault) throws AeBusinessProcessException
   {
      setFault(aFault);
      startTermination();
   }
   
   /**
    * Starts the process of termination by terminating all of the children that are eligible for termination.
    */
   public void startTermination() throws AeBusinessProcessException
   {
      setTerminating(true);
      setStartingTermination(true);
      try
      {
         for (Iterator it = getChildrenForTermination(); !getState().isFinal() && it.hasNext();)
         {
            IAeBpelObject child = (IAeBpelObject) it.next();
            // If the child can transition to dead path then we'll do that here.
            // This includes children that are currently queued by their parent,
            // ready to execute, or inactive. Setting them to dead path here will
            // ensure that they enter a final state and have their children go dead path
            // and any links go to false.
            if (child.getState().canTransitionToDeadPath())
            {
               child.setState(AeBpelState.DEAD_PATH);
            }
            else if (child.isTerminatable())
            {
               ((IAeExecutableBpelObject) child).terminate();
            }
         }
      }
      finally
      {
         setStartingTermination(false);
      }
      
      if(allEligibleChildrenAreTerminated() && !getState().isFinal())
         noMoreChildrenToTerminate();
   }
   
   /**
    * Setter for the startingTermination flag
    * @param aBool
    */
   private void setStartingTermination(boolean aBool)
   {
      mStartingTermination = aBool;
   }
   
   /**
    * Getter for the startingTermination flag
    */
   private boolean isStartingTermination()
   {
      return mStartingTermination;
   }

   /**
    * Returns true if all of the children are terminated.
    * 
    * This extra check is necessary to handle the situation where a scope is asked
    * to terminate and it's in the middle of executing a fault handler. The scope's
    * parent will have asked the scope to terminate but the scope will ignore the request
    * since it's currently in the middle of handling a fault. As such, the parent
    * will move on thinking that the scope has been terminated. This check was
    * added as a way of preventing the parent from reporting that all of its 
    * children were terminated until the catch had a chance to finish executing.
    * 
    * When the catch finishes executing, it'll notify the scope (which was asked
    * to terminate) and the scope will in turn notify its parent that it's been
    * terminated. The parent will then execute terminateNextChild() again, but this
    * time make it through this extra check and then call noMoreChildrenToTerminate()
    * @throws AeBusinessProcessException 
    */
   protected boolean allEligibleChildrenAreTerminated() throws AeBusinessProcessException
   {
      for (Iterator iter = getChildrenForTermination(); iter.hasNext();)
      {
         IAeBpelObject child = (IAeBpelObject) iter.next();
         if (!child.getState().isFinal())
         {
            return false;
         }
      }
      return true;
   }

   /**
    * Gets called from the termination routine when there are no more children
    * to terminate. If our terminate call originated from a fault, then we're
    * holding onto a fault that needs to be propagated to our parent.
    * If this is false, then the terminate originated from our parent and we
    * should change our state to terminated since all of our children are terminated
    */
   protected void noMoreChildrenToTerminate() throws AeBusinessProcessException
   {
      setTerminating(false);
      /*
       * reasons for terminating children
       * + fault handling: handleFault(fault) [getProcess().objectCompletedWithFault() for all but scope]
       * + early termination: objectCompleted()
       * + termination: runTerminationHandler() [setState(TERMINATED) for all but scope]
       * + process termination: no fault handling: setState(TERMINATED) 
       */
      if (getProcess().isExiting())
      {
         // set our state to terminated which will trigger a callback to our parent
         // notifiying them that we've terminated
         setState(AeBpelState.TERMINATED);
      }
      else if (isEarlyTermination())
      {
         setFault(null);
         objectCompleted();
      }
      else if (getFault() != null)
      {
         handleFault();
      }
      else
      {
         // It's possible that we're already in a final state by way of a callback
         // from our terminate work and subsequent firing of link changes.
         if (!getState().isFinal())
         {
            if (getState().canTransitionToDeadPath())
            {
               setState(AeBpelState.DEAD_PATH);
            }
            else
            {
               handleTermination();
            }
         }
      }
   }

   /**
    * Returns true if the object's termination was due to an early termination signal from
    * a loop control like break/continue.
    */
   protected boolean isEarlyTermination()
   {
      if (getFault() != null)
      {
         return getFaultFactory().isEarlyTerminationFault(getFault());
      }
      else
      {
         // TODO (MF) this handles the legacy case where we weren't using a fault to indicate early termination
         // If we were asked to terminate w/o any fault and our parent isn't terminating, then the only explanation
         // is that it was an early termination.
         return getParent() != null && !getParent().isTerminating();
      }
   }
   
   /**
    * Sets the object's state to terminated which will propagate a state change event to the parent
    */
   protected void handleTermination() throws AeBusinessProcessException
   {
      setState(AeBpelState.TERMINATED);
   }
   
   /**
    * Notifies the process that we've faulted. This will change our state and then report the
    * change to our parent
    */
   protected void handleFault() throws AeBusinessProcessException
   {
      IAeFault fault = getFault();
      if (isFaultingOrRetrying())
      {
         setFault(null);
         setState(AeBpelState.TERMINATED);
      }
      else
      {
         setFault(null);
         getProcess().objectCompletedWithFault(this, fault);
      }
   }

   /**
    * Returns the next child eligible for termination or null if there are no
    * child objects that need to be terminated.
    */
   public Iterator getChildrenForTermination() throws AeBusinessProcessException
   {
      return getChildrenForStateChange();
   }

   /**
    * Returns true if the bpel object has been queued by its parent. There is an
    * additional check here to prevent bpel activities that are in the process
    * of terminating from executing.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#isReadyToExecute()
    */
   public boolean isReadyToExecute()
   {
      boolean ready = !isTerminating() && (getState() == AeBpelState.QUEUED_BY_PARENT) && isStatusOfIncomingLinksKnown();
      if (ready)
      {
         ready = acquireResourceLocks();
         if (!ready)
         {
            // fire info event if we're waiting for a variable lock
            getProcess().getEngine().fireInfoEvent(new AeProcessInfoEvent(getProcess().getProcessId(),
                  getLocationPath(), IAeProcessInfoEvent.INFO_WAITING_FOR_LOCK));
         }
      }
      return ready;
   }

   /**
    * Base returns <code>true</code>; activities and compensation handlers
    * should override.
    */
   protected boolean acquireResourceLocks()
   {
      return true;
   }

   /**
    * Base returns <code>true</code>; activities should override.
    */
   protected boolean isStatusOfIncomingLinksKnown()
   {
      return true;
   }

   /**
    * Convenience method for activities and compensation handlers to incorporate
    * instance information into resource paths.
    */
   protected Set customizeResourcePaths(Set aResourcePaths)
   {
      Set resourcePaths = aResourcePaths;

      if (!AeUtil.isNullOrEmpty(resourcePaths) && hasCustomLocationPath())
      {
         resourcePaths = AeLocationPathUtils.addInstanceInfo(aResourcePaths, getLocationPath());
      }
   
      return resourcePaths;
   }
   
   /**
    * Base returns true, activities should override.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#isNotDeadPath()
    */
   public boolean isNotDeadPath() throws AeBusinessProcessException
   {
      return true;
   }

   /**
    * Creates an expression runner context when running an expression.
    * 
    * @param aLanguageURI
    * @param aContext
    */
   protected IAeExpressionRunnerContext createExpressionRunnerContext(String aLanguageURI, Object aContext)
   {
      return new AeExpressionRunnerContext(this, aContext, aLanguageURI, this, this, new AeExpressionRunnerVariableResolver(this));
   }

   /**
    * Creates an expression runner context when running an expression.
    * @param aLanguageURI
    * @param aContext
    * @param aFunctionContext
    */
   protected IAeExpressionRunnerContext createExpressionRunnerContext(String aLanguageURI, Object aContext, IAeFunctionFactory aFunctionContext)
   {
      return new AeExpressionRunnerContext(this, aContext, aLanguageURI, this, aFunctionContext, new AeExpressionRunnerVariableResolver(this));
   }

   /**
    * Gets the expression language that should be used when executing the expression found
    * in the given IAeExpressionDef object.
    * 
    * @param aExpressionDef
    */
   protected String getExpressionLanguage(IAeExpressionDef aExpressionDef)
   {
      return AeDefUtil.getExpressionLanguage(aExpressionDef, getProcess().getProcessDefinition());
   }

   /**
    * Gets the expression runner from the expression language factory based on the expression language
    * @param aExpressionDef
    * @throws AeBusinessProcessException
    */
   protected IAeExpressionRunner getExpressionRunner(IAeExpressionDef aExpressionDef) throws AeBusinessProcessException
   {
      String expressionLanguage = getExpressionLanguage(aExpressionDef);
      IAeExpressionLanguageFactory factory;
      try
      {
         factory = getProcess().getExpressionLanguageFactory();
         IAeExpressionRunner runner = factory.createExpressionRunner(aExpressionDef.getBpelNamespace(), expressionLanguage);
         return runner;
      }
      catch (AeException ex)
      {
         throw new AeBusinessProcessException(ex.getLocalizedMessage(), ex);
      }
   }
   
   /**
    * Executes the given expression.
    * 
    * @param aExpressionDef
    * @throws AeBusinessProcessException
    */
   public Object executeExpression(IAeExpressionDef aExpressionDef) throws AeBusinessProcessException
   {
      return executeExpression(aExpressionDef, null);
   }

   /**
    * Executes an expression and returns the result as an Object.
    * 
    * @param aExpressionDef
    * @param aContext
    * @throws AeBusinessProcessException
    */
   public Object executeExpression(IAeExpressionDef aExpressionDef, Object aContext) throws AeBusinessProcessException
   {
      return executeExpression(aExpressionDef, aContext, this);
   }

   /**
    * Executes an expression and returns the result as an Object.
    * @param aExpressionDef
    * @param aContext
    * @param aFunctionContext
    * @throws AeBusinessProcessException
    */
   public Object executeExpression(IAeExpressionDef aExpressionDef, Object aContext, IAeFunctionFactory aFunctionContext) throws AeBusinessProcessException
   {
      try
      {
         String expressionLanguage = getExpressionLanguage(aExpressionDef);
         IAeExpressionRunner runner = getExpressionRunner(aExpressionDef);
         return runner.executeExpression(createExpressionRunnerContext(expressionLanguage, aContext, aFunctionContext), aExpressionDef.getExpression());
      }
      catch (AeBusinessProcessException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Executes a duration expression and returns the result as an AeSchemaDuration.
    * @param aExpressionDef
    * @param aContext
    * @param aFunctionContext
    * @throws AeBusinessProcessException
    */
   public AeSchemaDuration executeDurationExpression(IAeExpressionDef aExpressionDef, Object aContext, IAeFunctionFactory aFunctionContext) throws AeBusinessProcessException
   {
      try
      {
         String expressionLanguage = getExpressionLanguage(aExpressionDef);
         IAeExpressionRunner runner = getExpressionRunner(aExpressionDef);
         return runner.executeDurationExpression(createExpressionRunnerContext(expressionLanguage, aContext, aFunctionContext), aExpressionDef.getExpression());
      }
      catch (AeBusinessProcessException e)
      {
         throw e;
      }
      catch (AeException ex)
      {
         throw new AeBusinessProcessException(ex.getLocalizedMessage(), ex);
      }
   }
   
   /**
    * Executes a deadline expression and returns the result as a Date.
    * @param aExpressionDef
    * @param aContext
    * @param aFunctionContext
    */
   public Date executeDeadlineExpression(IAeExpressionDef aExpressionDef, Object aContext, IAeFunctionFactory aFunctionContext) throws AeBusinessProcessException
   {
      try
      {
         String expressionLanguage = getExpressionLanguage(aExpressionDef);
         IAeExpressionRunner runner = getExpressionRunner(aExpressionDef);
         return runner.executeDeadlineExpression(createExpressionRunnerContext(expressionLanguage, aContext, aFunctionContext), aExpressionDef.getExpression());
      }
      catch (AeBusinessProcessException e)
      {
         throw e;
      }
      catch (AeException ex)
      {
         throw new AeBusinessProcessException(ex.getLocalizedMessage(), ex);
      }
   }
   
   /**
    * Executes an XPath expression that we expect to return a Boolean. Any other
    * return value from the evaluation will result in an exception being thrown
    * since there is something terribly wrong.
    * 
    * @param aExpressionDef
    * @throws AeBusinessProcessException
    */
   public boolean executeBooleanExpression(IAeExpressionDef aExpressionDef) throws AeBusinessProcessException
   {
      try
      {
         String expressionLanguage = getExpressionLanguage(aExpressionDef);
         IAeExpressionRunner runner = getExpressionRunner(aExpressionDef);
         return runner.executeBooleanExpression(createExpressionRunnerContext(expressionLanguage, null), aExpressionDef.getExpression()).booleanValue();
      }
      catch (AeBusinessProcessException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Returns the namespace associated with the prefix from the associated model.
    * 
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#translateNamespacePrefixToUri(java.lang.String)
    */
   public String translateNamespacePrefixToUri(String aPrefix)
   {
      return AeXmlDefUtil.translateNamespacePrefixToUri(getDefinition(), aPrefix);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getState()
    */
   public AeBpelState getState()
   {
      return mState;
   }

   /**
    * Sets the state of the object <i>without</i> reporting the change to the process.
    * This is used when restoring the state of a process from its serialized
    * representation.
    * 
    * @param aNewState The new state to set
    */
   public void setRawState(AeBpelState aNewState)
   {
      mState = aNewState;
   }

   /**
    * Sets the state on the object and reports the change to the process.
    * In order to properly log the details of the fault object, you need to call
    * setFaultedState(IAeFault).
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#setState(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void setState(AeBpelState aNewState) throws AeBusinessProcessException
   {
      if (aNewState == AeBpelState.FAULTED)
      {
         throw new IllegalArgumentException(AeMessages.getString("AeAbstractBpelObject.ERROR_41")); //$NON-NLS-1$
      }

      IAeStateChangeDetail details;
      // If Faulting, provide the causal fault's name.
      if (aNewState == AeBpelState.FAULTING)
      {
         String faultName = getFault() == null ? "" : getFault().getFaultName().getLocalPart(); //$NON-NLS-1$
         String faultInfo = getFault() == null ? "" : getFault().getInfo(); //$NON-NLS-1$
         details = new AeStateChangeDetail(faultName, faultInfo);
      }
      else
         details = AeStateChangeDetail.NONE;

      setState(aNewState, details);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#setFaultedState(org.activebpel.rt.bpel.IAeFault)
    */
   public void setFaultedState(IAeFault aFault) throws AeBusinessProcessException
   {
      String faultName = aFault.getFaultName() != null ? aFault.getFaultName().getLocalPart() : "[unnamed]"; //$NON-NLS-1$
      String info = (aFault.getSource() != null && aFault.getSource() != this) ? "..." : aFault.getInfo(); //$NON-NLS-1$
      setState(AeBpelState.FAULTED, new AeStateChangeDetail(faultName, info));
   }

   /**
    * Private overloaded setter for the state which takes an additional param to
    * convey any extra information associated with this state change. For the
    * time being this is limited to the fault object that comes with entering the
    * faulted state but this could conceiveably be expanded to include other
    * values (perhaps the originating node in a dead path chain?).
    * @param aNewState
    * @param aDetailsObject
    */
   private void setState(AeBpelState aNewState, IAeStateChangeDetail aDetailsObject) throws AeBusinessProcessException
   {
      AeBpelState oldState = getState();

      if (oldState != aNewState)
      {
         validateStateChange(aNewState);

         mState = aNewState;

         // any kind of state change means that we're no longer in the process of terminating         
         setTerminating(false);

         if (mState.isPropagable())
         {
            mState.propagate(this);
         }

         getProcess().objectStateChanged(this, oldState, aDetailsObject);
      }
   }

   /**
    * Validates the state change to the new state is allowable.  This class
    * calls the state to determine if the change is allowable derived classes
    * may override.
    * @param aNewState The state we have been asked to change to.
    * @throws AeBusinessProcessException
    */
   protected void validateStateChange(AeBpelState aNewState) throws AeBusinessProcessException
   {
      AeBpelState.validateStateChange(this, aNewState);
   }

   /**
    * Returns true if the state of this object is faulted.
    */
   protected boolean isFaulted()
   {
      return getState() == AeBpelState.FAULTED;
   }

   /**
    * Setter for terminating flag
    * @param aTerminatingFlag
    */
   public void setTerminating(boolean aTerminatingFlag)
   {
      mTerminating = aTerminatingFlag;
   }

   /**
    * Returns true if this object is in the process of terminating its child activities.
    * An object may be terminating due to the process exiting, fault handling, or
    * through some extension like process exception mangement (retrying activities) or
    * early termination through a loop control
    */
   public boolean isTerminating()
   {
      return mTerminating;
   }
   
   /**
    * Returns true if this object is currently executing.
    */
   public boolean isExecuting()
   {
      return getState() == AeBpelState.EXECUTING;
   }

   /**
    * Gets called when we're in the middle of terminating our child activity in
    * order to process the fault. Terminating the child's execution will result
    * in a callback to the parent process once the termination is complete. We're
    * storing the fault that triggered the termination so we can run the proper
    * fault handling code after the callback is received.
    * @param aFault
    */
   public void setFault(IAeFault aFault)
   {
      if (aFault != null)
      {
         // We don't want to overwrite a fault if one is already set.
         if (getFault() == null)
            mFault = aFault;
      }
      else
      {
         mFault = aFault;
      }
   }

   /**
    * Getter for the fault that we're storing. This gets called from the childTerminated
    * callback since we know that the scope's only child has terminated its execution
    * and we're now ready to execute the fault handling code.
    */
   public IAeFault getFault()
   {
      return mFault;
   }

   /**
    * Call this method in order to report a failure that should have been caught
    * during static analysis of the bpel process.
    * @param aMessage
    * @throws AeStaticAnalysisException
    */
   protected void staticAnalysisFailure(String aMessage) throws AeStaticAnalysisException
   {
      throw new AeStaticAnalysisException(aMessage);
   }

   /**
    * Returns true if the object isn't currently terminating and its state is
    * one that can be terminated.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#isTerminatable()
    */
   public boolean isTerminatable()
   {
      if (!getState().isTerminatable())
         return false;
      if (getProcess().isExiting())
         return true;
      return !isTerminating();
   }
   
   /** 
    * Overrides method to 
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#isCompletable()
    */
   public boolean isCompletable()
   {
      if (!getState().isTerminatable())
         return false;
      if (((AeBusinessProcess) getProcess()).isExiting())
         return true;
      return !isTerminating();
   }   

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#isFaultingOrRetrying()
    */
   public boolean isFaultingOrRetrying()
   {
      return getFault() != null && getFault().isSuspendable() && getState() == AeBpelState.FAULTING;
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolvePrefixToNamespace(java.lang.String)
    */
   public String resolvePrefixToNamespace(String aPrefix)
   {
      return AeXmlDefUtil.translateNamespacePrefixToUri(getDefinition(), aPrefix);
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolveNamespaceToPrefixes(java.lang.String)
    */
   public Set resolveNamespaceToPrefixes(String aNamespace)
   {
      return getDefinition().findPrefixesForNamespace(aNamespace);
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionFactory#getFunction(java.lang.String, java.lang.String)
    */
   public IAeFunction getFunction(String aNamespaceURI, String aLocalName) throws AeUnresolvableException
   {
      return getProcess().getEngine().getEngineConfiguration().getFunction(aLocalName, aNamespaceURI);
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionFactory#getFunctionContextNamespaceList()
    */
   public Set getFunctionContextNamespaceList()
   {
      return getProcess().getEngine().getEngineConfiguration().getFunctionContextNamespaceList();
   }
   
   /**
    * Gets the namespace for the version of BPEL we're working with
    */
   public String getBPELNamespace()
   {
      return getProcess().getProcessDefinition().getNamespace();
   }
   
   /**
    * Getter for the factory
    */
   public IAeFaultFactory getFaultFactory()
   {
      return AeFaultFactory.getFactory(getBPELNamespace());
   }
   
   /**
    * Overrides method to set the activity state to COMPLETED. 
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#exceptionManagementCompleteActivity()
    */
   public void exceptionManagementCompleteActivity() throws AeBusinessProcessException
   {
      // Change the state of the object here before touching any of the children
      // this is done to avoid an invalid state transition from ready to execute 
      // to finished
      if(getState() == AeBpelState.READY_TO_EXECUTE )
      {
         setState( AeBpelState.EXECUTING );
      }
      // Walk all of the children and set them to dead path or complete them as
      // well.
      for (Iterator it = getChildrenForCompletion(); it.hasNext();)
      {
         IAeBpelObject child = (IAeBpelObject) it.next();
         if (child.getState().canTransitionToDeadPath())
         {
            child.setState(AeBpelState.DEAD_PATH);
         }
         else if (child.isCompletable()) 
         {
            ((IAeExecutableBpelObject) child).exceptionManagementCompleteActivity();
         }
      }
      
      // If we had some children that we completed or set to deadpath then we're
      // likely already in a final state. If we didn't have any children then
      // we change our state to completed here and we're done.
      if (!getState().isFinal())
         objectCompleted();
   }
   
   /** 
    * @return Returns list of children that eligle for process exception management's Complete Activity operation.
    */
   protected Iterator getChildrenForCompletion()
   {
      return getChildrenForStateChange();
   }

   /** 
    * Overrides method to completed the activity with the fault. 
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#exceptionManagementResumeUncaughtFault(org.activebpel.rt.bpel.IAeFault)
    */
   public void exceptionManagementResumeUncaughtFault(IAeFault aUncaughtFault) throws AeBusinessProcessException
   {
      aUncaughtFault.setSuspendable(false);
      objectCompletedWithFault(aUncaughtFault);     
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getExtensions()
    */
   public Collection getExtensions()
   {
      return Collections.EMPTY_LIST;
   }
}
