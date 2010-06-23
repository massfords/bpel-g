// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityImpl.java,v 1.50 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.AeProcessInfoEvent;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.support.AeLink;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.util.AeUtil;

/**
 * Base class for bpel activity implementations.
 */
abstract public class AeActivityImpl extends AeAbstractBpelObject implements IAeActivity
{
   /** storage for this activity's source links (aka outbound links) */
   private Collection mSourceLinks;
   /** storage for this activity's target links (aka inbound links) */
   private Collection mTargetLinks;
   /** collection of extension objects that were installed because of an extension attribute or element */
   private Collection mExtensions;
   
   /** default constructor for activity */
   public AeActivityImpl(AeActivityDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }
   
   /**
    * This constructor is here because it enabled Process to extend Scope.
    * @param aDef
    */
   protected AeActivityImpl(AeBaseDef aDef)
   {
      super(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      AeExtensionNotifier.onExecute(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#objectCompletedWithFault(org.activebpel.rt.bpel.IAeFault)
    */
   public void objectCompletedWithFault(IAeFault aFault)
         throws AeBusinessProcessException
   {
      AeExtensionNotifier.onFault(this, aFault);
      super.objectCompletedWithFault(aFault);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#terminate()
    */
   public void terminate() throws AeBusinessProcessException
   {
      AeExtensionNotifier.onTerminate(this);
      super.terminate();
   }

   /**
    * Terminates the activity as a result of the early termination of a looping
    * container. The object terminates all of its enclosed children but completes
    * normally.
    *
    * @see org.activebpel.rt.bpel.IAeActivity#terminateEarly()
    */
   public void terminateEarly() throws AeBusinessProcessException
   {
      // the absence of a fault and terminating flag will cause the object to
      // complete normally once all of its children have been terminated.
      if (getState().canTransitionToDeadPath())
      {
         setState(AeBpelState.DEAD_PATH);
      }
      else
      {

         // report the early termination
         getProcess().getEngine().fireInfoEvent(
               new AeProcessInfoEvent(getProcess().getProcessId(),
                                      getLocationPath(),
                                      IAeProcessInfoEvent.INFO_EARLY_TERMINATION));
         IAeFault fault = getFaultFactory().getEarlyTerminationFault();
         triggerFaultHandling(fault);
      }
   }

   /**
    * Overrides method to acquire shared locks on the resources that this
    * activity uses.
    * 
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#acquireResourceLocks()
    */
   protected boolean acquireResourceLocks()
   {
      return getProcess().addSharedLock(getConvertedResourceLockPaths(), getLocationPath());
   }

   /**
    * If the activity is nested within a parallel forEach then we may need
    * to convert one or more of its variable paths to include instance
    * information if those variables are themselves declared within a parallel forEach.
    */
   protected Set getConvertedResourceLockPaths()
   {
      AeActivityDef def = (AeActivityDef) getDefinition();
      Set resourcePathsUsed = def.getResourcesUsed();
      return customizeResourcePaths(resourcePathsUsed);
   }

   /**
    * Returns true if join condition is true.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#isNotDeadPath()
    */
   public boolean isNotDeadPath() throws AeBusinessProcessException
   {
      return isJoinConditionTrue();
   }

   /**
    * Returns true if the join condition evaluates to true. If the join condition
    * doesn't evaluate to true and we're not suppressing errors then we'll get a fault.
    */
   protected boolean isJoinConditionTrue() throws AeBusinessProcessException
   {
      boolean result = true;
      if(hasTargetLinks())
      {
         AeActivityDef activityDef = (AeActivityDef) getDefinition();
         AeJoinConditionDef joinCondDef = activityDef.getJoinConditionDef();
         if (joinCondDef != null && AeUtil.notNullOrEmpty(joinCondDef.getExpression()))
         {
            result = executeJoinConditionExpression(joinCondDef);

            // Generate engine info event for debug.
            //
            getProcess().getEngine().fireEvaluationEvent(getProcess().getProcessId(),
                  joinCondDef.getExpression(), IAeProcessInfoEvent.INFO_JOIN, getLocationPath(),
                  Boolean.toString(result));
         }
         else
         {
            // implicit 'OR' so start as false first true will break loop
            result = false;
            for (Iterator iter = getTargetLinksColl().iterator(); result == false && iter.hasNext();)
            {
               AeLink link = (AeLink) iter.next();
               result = link.getStatus();
            }
         }

         // if join condition is false;
         if (result == false)
         {
            // check if the failure should fault or propagate to source links
            if( ! isSuppressJoinConditionFailure() && !getParent().isTerminating())
            {
               throw new AeBpelException(AeMessages.getString("AeActivityImpl.ERROR_0"), getFaultFactory().getJoinFailure()); //$NON-NLS-1$
            }
         }
      }
      return result;
   }

   /**
    * Returns true if the status of all incoming links is known.
    * @return true if all link statuses are known, false if at least 1 is unknown
    */
   protected boolean isStatusOfIncomingLinksKnown()
   {
      // if we have target links check their status
      if(hasTargetLinks())
      {
         for (Iterator iter = getTargetLinksColl().iterator(); iter.hasNext();)
         {
            AeLink link = (AeLink) iter.next();
            if ( ! link.isStatusKnown())
            {
               return false;
            }
         }
      }
      return true;
   }

   /**
    * Finds the target link for the given link name or returns null if not found .
    * @param aLinkName the name of the link we are interested in.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#findTargetLink(java.lang.String)
    */
   public AeLink findTargetLink(String aLinkName)
   {
      for (Iterator iter = getTargetLinksColl().iterator(); iter.hasNext();)
      {
         AeLink link = (AeLink)iter.next();
         if (link.getName().equals(aLinkName))
            return link;
      }

      return null;
   }

   /**
    * The link is stored in a collection of source links that will be
    * evaluated when this activity completes.
    * @param aLink
    */
   public void addSourceLink(AeLink aLink)
   {
      getSourceLinksColl().add(aLink);
   }

   /**
    * The link is stored in a collection of target links that will be evaluated
    * before this activity is allowed to start. The status of all of an activities
    * incoming links must be known before an activity is allowed to start.
    * @param aLink
    */
   public void addTargetLink(AeLink aLink)
   {
      getTargetLinksColl().add(aLink);
   }

   /**
    * Setter for the source links collection
    * @param aSourceLinks
    */
   protected void setSourceLinks(Collection aSourceLinks)
   {
      mSourceLinks = aSourceLinks;
   }

   /**
    * Getter for the source links collection.
    */
   protected Collection getSourceLinksColl()
   {
      if (mSourceLinks == null)
      {
         mSourceLinks = new ArrayList();
      }
      return mSourceLinks;
   }

   /**
    * Returns an iterator on the source links collection.
    */
   public Iterator getSourceLinks()
   {
      if (mSourceLinks == null)
      {
         return Collections.EMPTY_SET.iterator();
      }
      return getSourceLinksColl().iterator();
   }

   /**
    * Getter for the target links collection
    */
   protected Collection getTargetLinksColl()
   {
      if (mTargetLinks == null)
      {
         mTargetLinks = new ArrayList();
      }
      return mTargetLinks;
   }

   /**
    * Setter for the target links collection
    * @param aCollection
    */
   protected void setTargetLinksColl(Collection aCollection)
   {
      mTargetLinks = aCollection;
   }

   /**
    * Returns true if we have target links.
    */
   protected boolean hasTargetLinks()
   {
      return (mTargetLinks != null && mTargetLinks.size() > 0);
   }

   /**
    * Checks the activity def to see if it defines the suppress attribute flag.
    * If not, it'll default to the super's impl.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#isSuppressJoinConditionFailure()
    */
   public boolean isSuppressJoinConditionFailure()
   {
      AeActivityDef def = (AeActivityDef) getDefinition();
      Boolean flag = def.getSuppressFailure();
      if (flag != null)
      {
         return flag.booleanValue();
      }
      return super.isSuppressJoinConditionFailure();
   }

   /**
    * Processes any outgoing links (aka source links) and then signals the engine
    * that this activity is complete which should in turn notify its parent.
    */
   protected void objectCompleted() throws AeBusinessProcessException
   {
      getProcess().objectCompleted(this);

      IAeFault fault = evaluateLinks();
      if (fault == null)
      {
         // then we have the links notify the process of their state change
         notifyParentOfCompletion();
         notifyProcessOfLinkChanges();
      }
      else
      {
         // Any faults that occur during link evaulation should be reported to the
         // object's parent.
         AeAbstractBpelObject parent = (AeAbstractBpelObject) getParent();
         parent.triggerFaultHandling(fault);

         // We want to notify the process of the link changes to
         // ensure that any activities that were the targets of the link have a
         // chance to either complete or go deadpath. The process will ignore any
         // notification of link state changes after it's complete so if the
         // previous calls above resulted in the process completing (or faulting)
         // then this call will be ignored. Without this call, we risk leaving
         // an activity in an inactive state and the process won't complete.
         notifyProcessOfLinkChanges();
      }
   }

   /**
    * Evaluate all of our links. The first part of this evaluation is done
    * prior to our state changing since we want to be able to be able to handle
    * any transitionCondition errors as errors on the source activity as opposed
    * to the target activity.
    */
   protected IAeFault evaluateLinks() throws AeBusinessProcessException
   {
      IAeFault fault = null;
      for (Iterator iter = getSourceLinks(); iter.hasNext();)
      {
      	// Evaluating all of the links instead of stopping after the first failure
      	//	This is discussed in Issue 169
         AeLink link = (AeLink) iter.next();
         try
         {
            link.evaluate();
         }
         catch(Throwable t)
         {
            link.setStatus(false);

            if (fault == null)
            {
               if(t instanceof AeBpelException)
                  fault = ((AeBpelException)t).getFault();
               if(fault == null)
                  fault = AeFaultFactory.getSystemErrorFault(t);
            }

            getProcess().getEngine().fireInfoEvent(
                  new AeProcessInfoEvent(getProcess().getProcessId(),
                                         getLocationPath(),
                                         IAeProcessInfoEvent.INFO_LINK_XTN,
                                         fault.getFaultName().getLocalPart(),
                                         AeMessages.getString("AeActivityImpl.ERROR_IN_TRANSITION_CONDITION"))); //$NON-NLS-1$
         }
      }
      return fault;
   }

   /**
    * Executes a join condition expression that we expect to return a Boolean. Any other
    * return value from the evaluation will result in an exception being thrown
    * since there is something terribly wrong.
    * 
    * @param aExpressionDef
    * @throws AeBusinessProcessException
    */
   protected boolean executeJoinConditionExpression(IAeExpressionDef aExpressionDef) throws AeBusinessProcessException
   {
      String expressionLanguage = getExpressionLanguage(aExpressionDef);
      try
      {
         IAeExpressionLanguageFactory factory = getProcess().getExpressionLanguageFactory();
         IAeExpressionRunner runner = factory.createExpressionRunner(aExpressionDef.getBpelNamespace(), expressionLanguage);
         return runner.executeJoinConditionExpression(createExpressionRunnerContext(expressionLanguage, null), aExpressionDef.getExpression()).booleanValue();
      }
      catch (AeBusinessProcessException e)
      {
         throw e;
      }
      catch (AeExpressionException expe)
      {
         throw expe.getWrappedException();
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Notifies the process of the link state change (if any). In the event that
    * a link became true then its target activity will execute or at least becomes
    * eligible to execute. If the link is false then the target will go dead path.
    */
   protected void notifyProcessOfLinkChanges()
   {
      // now have the links notify the process that they've changed
      for (Iterator iter = getSourceLinks(); iter.hasNext();)
      {
         AeLink link = (AeLink) iter.next();
         link.notifyProcess();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return Collections.EMPTY_SET.iterator();
   }

   /**
    * Overridden here to provide the additional behavior of setting all of the
    * outbound link statuses to false if the state we're moving to is the dead path.
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#setState(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void setState(AeBpelState aNewState) throws AeBusinessProcessException
   {
      AeBpelState oldState = getState();

      // record whether this object is faulting/retrying before changing its state
      // we need to know test before changing the state since the object's state could
      // change if it is a scope being retried. The call to super.setState(xxx) will
      // signal that the scope has completed terminating and is ready to be retried.
      boolean updateLinks = isBeingRetried(aNewState, oldState);

      super.setState(aNewState);

      // if this activity is being terminated because it (or its parent)
      // is being retried, then we do not want to update its
      // outgoing links - just set the state and move on
      if( !updateLinks )
      {
         updateLinks();
      }
   }

   /**
    * Return true if this activity (or one of its parents) is being retried.
    * @param aNewState
    * @param aOldState
    */
   protected boolean isBeingRetried( AeBpelState aNewState, AeBpelState aOldState )
   {
      boolean isBeingRetried = false;
      if( AeBpelState.TERMINATED == aNewState || AeBpelState.DEAD_PATH == aNewState)
      {
         isBeingRetried = isFaultingOrRetrying() || isOneOfMyAnscestorsFaultingOrRetrying();
      }
      return isBeingRetried;
   }

   /**
    * Return true if an ancestor is in the faulting state.
    */
   protected boolean isOneOfMyAnscestorsFaultingOrRetrying()
   {
      IAeBpelObject parent = getParent();
      while( parent != null )
      {
         if( parent.isFaultingOrRetrying() )
         {
            return true;
         }
         parent = parent.getParent();
      }
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#setFaultedState(org.activebpel.rt.bpel.IAeFault)
    */
   public void setFaultedState(IAeFault aFault)
      throws AeBusinessProcessException
   {
      super.setFaultedState(aFault);
      updateLinks();
   }

   /**
    * Some states result in an activity's links automatically getting set to false.
    * For example, a faulted state or terminated state.
    */
   private void updateLinks()
   {
      if (getState().linksBecomeFalse())
      {
         for(Iterator iter=getSourceLinksColl().iterator(); iter.hasNext(); )
         {
            AeLink link = (AeLink)iter.next();
            link.setStatus(false);
         }
      }
   }

   /**
    * Returns true if all of the activity's child objects are done executing or
    * are dead paths. This method relies on getChildrenForStateChange() to return
    * an Iterator for all of the child objects that we want to check.
    */
   protected boolean childrenAreDone()
   {
      for (Iterator iter = getChildrenForStateChange(); iter.hasNext();)
      {
         IAeBpelObject child = (IAeBpelObject) iter.next();
         if ( ! child.getState().isFinal())
         {
            return false;
         }
         // very subtle issue here....
         //
         // this method is called from the childComplete() callback method for
         // flow and switch. Based on how we ordered the setting of state to
         // dead path and the event propagation, it's possible to get that callback
         // for completed BEFORE getting the callback for the child faulted.
         // This check avoids us running into trouble by changing that logic
         // later since we'll never complete normally when we have a faulted
         // child.
         else if (child.getState() == AeBpelState.FAULTED)
         {
            return false;
         }
      }
      return true;
   }

   /**
    * Walks the Iterator setting all of the bpel objects to dead path except for
    * the one passed in. This is used by activities that allow only a single
    * child to execute and set the others to dead path (like switch's cases)
    * @param aChild
    * @param aIterator
    * @throws AeBusinessProcessException
    */
   protected void setAllOtherToDeadPath(IAeBpelObject aChild, Iterator aIterator)
      throws AeBusinessProcessException
   {
      while (aIterator.hasNext())
      {
         IAeBpelObject bipple = (IAeBpelObject) aIterator.next();
         if (bipple != aChild)
         {
            bipple.setState(AeBpelState.DEAD_PATH);
         }
      }
   }

   /**
    * Convenience accessor for the <code>IAeEngineConfiguration</code>.
    */
   protected IAeEngineConfiguration getEngineConfiguration()
   {
      return getProcess().getEngine().getEngineConfiguration();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#getExtensions()
    */
   public Collection getExtensions()
   {
      return mExtensions;
   }

   /**
    * @param aExtensionObjects the extensionObjects to set
    */
   public void setExtensions(Collection aExtensionObjects)
   {
      mExtensions = aExtensionObjects;
   }
   
   /**
    * Adds an extension adapter to the collection of extensions. The adapter
    * will be called back immediately to inform it that it has been installed.
    * @param aAdapter
    */
   public void addExtension(IAeExtensionLifecycleAdapter aAdapter)
   {
      if (getExtensions() == null)
         setExtensions(new LinkedList());
      getExtensions().add(aAdapter);
      
      aAdapter.onInstalled(this);
   }
}
