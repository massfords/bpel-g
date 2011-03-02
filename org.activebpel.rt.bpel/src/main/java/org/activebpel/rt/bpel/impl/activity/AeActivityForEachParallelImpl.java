//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityForEachParallelImpl.java,v 1.14 2007/06/19 15:28:37 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * An extension of the forEach activity that executes all of its iterations
 * in parallel.
 */
public class AeActivityForEachParallelImpl extends AeActivityForEachImpl implements IAeDynamicScopeParent
{
   /** list of child scope instances created during execute routine */
   private List mChildren = new ArrayList();

   /** list of child scope instances that have been restored for compensation purposes */
   private List mCompensatableChildren = new ArrayList();

   /** value for the next scope instance created for this parallel forEach */
   private int mInstanceValue = 1;
   
   /**
    * @param aForEachDef
    * @param aParent
    */
   public AeActivityForEachParallelImpl(AeActivityForEachDef aForEachDef, IAeActivityParent aParent)
   {
      super(aForEachDef, aParent);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor)
         throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeActivityParent#addActivity(org.activebpel.rt.bpel.IAeActivity)
    */
   public void addActivity(IAeActivity aActivity)
   {
      getChildren().add(aActivity);
   }
   
   /**
    * Getter for the collection of children
    */
   public List getChildren()
   {
      return mChildren;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      // Record the compensatable scopes locally within a separate collection.
      // This ensures that the process state document will contain any references
      // to compensating scopes. The use case is a process state document getting 
      // built after the forEach has executed multiple times but before any state
      // restoration. The result is a state document which won't contain any info
      // for the currently compensating scope. By adding the compensatable scopes
      // to the separate collection, they'll get persisted. 
      for (Iterator iter = getChildren().iterator(); iter.hasNext();)
      {
         AeActivityScopeImpl scope = (AeActivityScopeImpl) iter.next();
         if (scope.isNormalCompletion())
         {
            // TODO (KR) Drop this collection.
            getCompensatableChildren().add(scope);
         }
      }
      getChildren().clear();
      super.execute();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl#startLoop()
    */
   protected void startLoop() throws AeBusinessProcessException
   {
      // we only have work to do if the counter value is w/in range
      if (isCounterWithinRange())
      {
         // creates all of the children
         int startInstance = getInstanceValue();
         int finalInstance = startInstance + (getFinalValue() - getStartValue());
         List scopes = AeDynamicScopeCreator.create(true, this, startInstance, finalInstance);
         getChildren().addAll(scopes);
         
         // set the instance value for the next time we execute
         setInstanceValue(finalInstance + 1);
         setCounterValue(getFinalValue() + 1);

         for (Iterator it=getChildren().iterator(); it.hasNext();)
         {
            getProcess().queueObjectToExecute((IAeBpelObject) it.next());
         }
      }
      else
      {
         objectCompleted();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childComplete(IAeBpelObject aChild)
         throws AeBusinessProcessException
   {
      handleLoopCompletion(aChild);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl#completedIteration()
    */
   protected void completedIteration() throws AeBusinessProcessException
   {
      if (childrenAreDone())
      {
         objectCompleted();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl#getNumberOfIterationsRemaining()
    */
   protected int getNumberOfIterationsRemaining()
   {
      int stillExecuting = 0;
      for (Iterator iter = getChildren().iterator(); iter.hasNext();)
      {
         AeActivityScopeImpl scope = (AeActivityScopeImpl) iter.next();
         if (!scope.getState().isFinal())
         {
            stillExecuting++;
         }
      }
      return stillExecuting;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl#completionConditionFailure()
    */
   protected void completionConditionFailure() throws AeBusinessProcessException
   {
      IAeFault fault = getFaultFactory().getCompletionConditionFailure();
      triggerFaultHandling(fault);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl#completionConditionMet()
    */
   protected void completionConditionMet() throws AeBusinessProcessException
   {
      runEarlyTerminationOnChildren();
   }

   /**
    * A continue call causes the parent instance of the loop control to terminate
    * but does not affect any other instances.
    * 
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeLoopActivity#onContinue(org.activebpel.rt.bpel.impl.activity.IAeLoopControl)
    */
   public void onContinue(IAeLoopControl aControl)
         throws AeBusinessProcessException
   {
      AeActivityScopeImpl impl = (AeActivityScopeImpl) findScopeInstance(aControl);
      
      impl.terminateEarly();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeLoopActivity#onBreak(org.activebpel.rt.bpel.impl.activity.IAeLoopControl)
    */
   public void onBreak(IAeLoopControl aControl)
         throws AeBusinessProcessException
   {
      runEarlyTerminationOnChildren();
   }

   /**
    * Walks all of the children and calls <code>terminateEarly</code> on all that
    * are eligible. This is called from the break and when the optional completionCondition
    * evaluates to true.
    */
   protected void runEarlyTerminationOnChildren() throws AeBusinessProcessException
   {
      boolean terminatedChild = false;
      for (Iterator iter = getChildren().iterator(); iter.hasNext();)
      {
         AeActivityScopeImpl impl = (AeActivityScopeImpl) iter.next();
         if (!impl.getState().isFinal())
         {
            impl.terminateEarly();
            terminatedChild = true;
         }
      }
      
      // if we didn't actually terminate any children, then we should just
      // signal that we're done.
      if (!terminatedChild)
      {
         objectCompleted();
      }
   }
   
   /**
    * Finds the enclosing scope for the object that is also our child instance.
    * 
    * @param aObject
    */
   protected IAeBpelObject findScopeInstance(IAeBpelObject aObject)
   {
      if (aObject.getParent() == this)
      {
         return aObject;
      }
      else
      {
         return findScopeInstance(aObject.getParent());
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return getChildren().iterator();
   }
   
   /**
    * Getter for the list of children that are compensatable
    */
   public List getCompensatableChildren()
   {
      return mCompensatableChildren;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeDynamicScopeParent#getInstanceValue()
    */
   public int getInstanceValue()
   {
      return mInstanceValue;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeDynamicScopeParent#setInstanceValue(int)
    */
   public void setInstanceValue(int aInstanceValue)
   {
      mInstanceValue = aInstanceValue;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeDynamicScopeParent#getChildScopeDef()
    */
   public AeActivityScopeDef getChildScopeDef()
   {
      return getDef().getChildScope();
   }
}
 