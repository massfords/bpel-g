//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityForEachImpl.java,v 1.13 2008/02/27 20:50:28 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.Collections;
import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.expr.AeExpressionResultConverter;

/**
 * Provides the ability to loop over a list of document elements through the use
 * of a start and final counter value which are evaluated from expressions at
 * execution time.
 */
public class AeActivityForEachImpl extends AeLoopActivity implements IAeActivityParent
{
   /** starting value for the counter */
   private int mStartValue = -1;
   /** final value for the counter */
   private int mFinalValue = -1;
   /** current value of the counter */
   private int mCounterValue = -1;
   /** optional early completion condition calculated once during execution or -1 if the expression wasn't set */
   private int mCompletionCondition = -1;
   /** number of completed iterations or -1 if the forEach lacks the optional completionCondition expression */
   private int mCompletionCount = -1;

   /**
    * @param aForEachDef
    * @param aParent
    */
   public AeActivityForEachImpl(AeActivityForEachDef aForEachDef, IAeActivityParent aParent)
   {
      super(aForEachDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeActivityParent#addActivity(org.activebpel.rt.bpel.IAeActivity)
    */
   public void addActivity(IAeActivity aActivity)
   {
      setChild(aActivity);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      int startValue = evaluateCounterExpression(getDef().getStartDef());

      setStartValue(startValue);
      setCounterValue(getStartValue());

      // report the start value evaluation
      getProcess().getEngine().fireEvaluationEvent(getProcess().getProcessId(),
            getDef().getStartDef().getExpression(), IAeProcessInfoEvent.INFO_FOREACH_START_VALUE,
            getLocationPath(), String.valueOf(getStartValue()));

      int finalValue = evaluateCounterExpression(getDef().getFinalDef());
      setFinalValue(finalValue);

      // report the final value evaluation
      getProcess().getEngine().fireEvaluationEvent(getProcess().getProcessId(),
            getDef().getFinalDef().getExpression(), IAeProcessInfoEvent.INFO_FOREACH_FINAL_VALUE,
            getLocationPath(), String.valueOf(getFinalValue()));

      if (getDef().hasCompletionCondition())
      {
         int completionCondition = evaluateCompletionConditionExpression();
         setCompletionCondition(completionCondition);
         setCompletionCount(0);

         // report the completion condition value evaluation
         getProcess().getEngine().fireEvaluationEvent(getProcess().getProcessId(),
               getDef().getCompletionCondition().getExpression(), 
               IAeProcessInfoEvent.INFO_FOREACH_COMPLETION_CONDITION_VALUE, getLocationPath(),
               String.valueOf(getCompletionCondition()));
      }

      startLoop();
   }

   /**
    * Evaluates the start/final counter expressions
    * @param aExpressionDef
    * @throws AeBusinessProcessException (probably a bpws:forEachCounterError)
    */
   protected int evaluateCounterExpression(IAeExpressionDef aExpressionDef) throws AeBusinessProcessException
   {
      try
      {
         return evaluateUnsignedIntegerExpression(aExpressionDef);
      }
      catch (AeUnsignedIntException e)
      {
         Object[] args = {e.getExpression(), e.getValue()}; 
         throw new AeBpelException(AeMessages.format("AeActivityForEachImpl.CounterExpressionError", args), getFaultFactory().getForEachCounterError()); //$NON-NLS-1$
      }
   }

   /**
    * Evaluates the optional completion condition expression
    * @throws AeBusinessProcessException
    */
   protected int evaluateCompletionConditionExpression() throws AeBusinessProcessException
   {
      int completionCondition;
      try
      {
         completionCondition = evaluateUnsignedIntegerExpression(getDef().getCompletionCondition());
      }
      catch (AeUnsignedIntException e)
      {
         Object[] args = {e.getExpression(), e.getValue()}; 
         throw new AeBpelException(AeMessages.format("AeActivityForEachImpl.InvalidBranchCondition_Negative", args), getFaultFactory().getInvalidBranchCondition()); //$NON-NLS-1$
      }
      
      // completion condition tells us that we can stop processing the
      // forEach after M out of N iterations.
      // If this value is greater than the number of iterations, then we must fault
      if (completionCondition > ((getFinalValue()-getStartValue())+1))
      {
         Object[] args = {
               getDef().getCompletionCondition().getExpression(),
               new Integer(completionCondition),
               new Integer(getStartValue()),
               new Integer(getFinalValue())
         };
         throw new AeBpelException(AeMessages.format("AeActivityForEachImpl.InvalidBranchCondition_RangeError", args), getFaultFactory().getInvalidBranchCondition()); //$NON-NLS-1$
      }

      return completionCondition;
   }

   /**
    * Executes the expression using the language specified. This expression is expected
    * to return an unsigned int. Any other return value will result in an exception being thrown.
    * @param aExpressionDef
    * @throws AeBusinessProcessException
    * @throws AeBpelException
    */
   protected int evaluateUnsignedIntegerExpression(IAeExpressionDef aExpressionDef) throws AeBusinessProcessException, AeUnsignedIntException
   {
      Object result = executeExpression(aExpressionDef);
      try
      {
         return AeExpressionResultConverter.toNonNegativeInt(result);
      }
      catch (IllegalArgumentException ex)
      {
         throw new AeUnsignedIntException(aExpressionDef.getExpression(), result);
      }
   }
   
   /**
    * internal exception thrown from <code>evaluateUnsignedIntegerExpression</code>
    */
   protected static class AeUnsignedIntException extends Exception
   {
      /** result of executing the expression */
      private Object mValue;
      /** expression */
      private String mExpression;
      
      /**
       * @param aValue
       */
      public AeUnsignedIntException(String aExpression, Object aValue)
      {
         mValue = aValue;
         mExpression = aExpression;
      }

      /**
       * @return Returns the value.
       */
      public Object getValue()
      {
         return mValue;
      }

      /**
       * @return Returns the expression.
       */
      public String getExpression()
      {
         return mExpression;
      }
   }

   /**
    * convenience method that gets our definition object
    */
   protected AeActivityForEachDef getDef()
   {
      return (AeActivityForEachDef) getDefinition();
   }

   /**
    * Executes the loop while the counter value is less than or equal to the final
    * value. If the counter value exceeds the final value then the object is completed.
    */
   protected void startLoop() throws AeBusinessProcessException
   {
      if (isCounterWithinRange())
      {
         AeActivityScopeImpl scope = getChildScope();

         // clear any previous state from the child for this next iteration.
         scope.setState(AeBpelState.INACTIVE);

         // Update the value of the implicit counter variable within the forEach's
         // scope. If the scope has previously executed and recorded snapshot info
         // then we need to operate on a clone of the variable to avoid changing the
         // value of the variable in the scope's previously recorded snapshot.
         // Note: scopes were previously cloning the variables at the time of the
         //       snapshot's creation but this has been deferred until a clone is
         //       actually needed. Now, the scope clears its variables and creates
         //       clones during its change to READY_TO_EXECUTE but this special
         //       handling is needed for implicit variables like the forEach's counter.
         IAeVariable variable = (IAeVariable) scope.getVariableContainer().findVariable(getDef().getCounterName());
         IAeVariable clone = scope.isSnapshotRecorded()? (IAeVariable) variable.clone() : variable;
         clone.setTypeData(new Integer(getCounterValue()));
         scope.getVariableContainer().addVariable(clone);

         getProcess().queueObjectToExecute(scope);
      }
      else
      {
         completionConditionMet();
      }
   }

   /**
    * Returns the child activity cast as a scope
    */
   protected AeActivityScopeImpl getChildScope()
   {
      return (AeActivityScopeImpl) getChild();
   }

   /**
    * Returns true if the counter is within the
    */
   protected boolean isCounterWithinRange()
   {
      return getCounterValue() <= getFinalValue();
   }

   /**
    * Overrides the basic loop handling to check completion condition.
    * @see org.activebpel.rt.bpel.impl.activity.AeLoopActivity#handleLoopCompletion(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   protected void handleLoopCompletion(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      if (shouldIncrementCompletionCount((AeActivityScopeImpl) aChild))
      {
         setCompletionCount(getCompletionCount() + 1);
      }

      if (isCompletionConditionMet())
      {
         // log that the forEach's completion condition has been met
         getProcess().getEngine().fireEvaluationEvent(
            getProcess().getProcessId(), getDef().getCompletionCondition().getExpression(), IAeProcessInfoEvent.INFO_FOREACH_COMPLETION_CONDITION_MET, getLocationPath(), String.valueOf(getCompletionCount()));

         completionConditionMet();
      }
      else if (isCompletionConditionFailure())
      {
         completionConditionFailure();
      }
      else
      {
         completedIteration();
      }
   }

   /**
    * Called when an iteration completes.
    */
   protected void completedIteration() throws AeBusinessProcessException
   {
      setCounterValue(getCounterValue() + 1);
      startLoop();
   }

   /**
    * Called after determining that there are not enough iterations remaining in
    * order to meet the completion condition. The forEach should complete with a bpws:completionConditionFailure
    */
   protected void completionConditionFailure() throws AeBusinessProcessException
   {
      objectCompletedWithFault(getFaultFactory().getCompletionConditionFailure());
   }

   /**
    * Called when the completion condition is met. The forEach will stop iterating and complete.
    */
   protected void completionConditionMet() throws AeBusinessProcessException
   {
      objectCompleted();
   }

   /**
    * Returns if the optional completion condition is present and it is determined that there
    * are not enough iterations remaining in the loop for the condition to have been met.
    */
   protected boolean isCompletionConditionFailure()
   {
      boolean failure = false;
      if (getDef().hasCompletionCondition())
      {
         int numberOfIterationsRequiredToComplete = getCompletionCondition() - getCompletionCount();
         int numberOfIterationsRemaining = getNumberOfIterationsRemaining();
         failure = numberOfIterationsRequiredToComplete > numberOfIterationsRemaining;
      }
      return failure;
   }

   /**
    * Returns the number of iterations remaining in the loop
    */
   protected int getNumberOfIterationsRemaining()
   {
      int numberOfIterationsRemaining = getFinalValue() - getCounterValue();
      return numberOfIterationsRemaining;
   }

   /**
    * Returns true if the recently completed iteration should increment the completion count.
    * The optional completion condition can specify that only normally completed scopes (those
    * that didn't catch a fault) count towards the completion count.
    * @param aChild
    */
   protected boolean shouldIncrementCompletionCount(AeActivityScopeImpl aChild)
   {
      boolean increment = false;
      if (getDef().hasCompletionCondition())
      {
         increment = aChild.isNormalCompletion() || !getDef().getCompletionCondition().isCountCompletedBranchesOnly();
      }
      return increment;
   }

   /**
    * Returns true if the number of iterations completed satifies the optional
    * completion condition. If none set, then the forEach will loop from start
    * to final.
    */
   protected boolean isCompletionConditionMet() throws AeBpelException
   {
      boolean conditionMet = false;
      if (getDef().hasCompletionCondition())
      {
         conditionMet = getCompletionCount() == getCompletionCondition();
      }
      return conditionMet;
   }

   /**
    * @return Returns the counterValue.
    */
   public int getCounterValue()
   {
      return mCounterValue;
   }

   /**
    * @param aCounterValue The counterValue to set.
    */
   public void setCounterValue(int aCounterValue)
   {
      mCounterValue = aCounterValue;
   }

   /**
    * @return Returns the finalValue.
    */
   public int getFinalValue()
   {
      return mFinalValue;
   }

   /**
    * @param aFinalValue The finalValue to set.
    */
   public void setFinalValue(int aFinalValue)
   {
      mFinalValue = aFinalValue;
   }

   /**
    * @return Returns the startValue.
    */
   public int getStartValue()
   {
      return mStartValue;
   }

   /**
    * @param aStartValue The startValue to set.
    */
   public void setStartValue(int aStartValue)
   {
      mStartValue = aStartValue;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return Collections.singleton(getChild()).iterator();
   }

   /**
    * @return Returns the completionCondition.
    */
   public int getCompletionCondition()
   {
      return mCompletionCondition;
   }

   /**
    * @param aCompletionCondition The completionCondition to set.
    */
   public void setCompletionCondition(int aCompletionCondition)
   {
      mCompletionCondition = aCompletionCondition;
   }

   /**
    * @return Returns the completionCount.
    */
   public int getCompletionCount()
   {
      return mCompletionCount;
   }

   /**
    * @param aCompletionCount The completionCount to set.
    */
   public void setCompletionCount(int aCompletionCount)
   {
      mCompletionCount = aCompletionCount;
   }
}