//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeRepeatableOnAlarm.java,v 1.2 2006/11/06 23:40:30 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeDynamicScopeCreator;
import org.activebpel.rt.bpel.impl.activity.IAeEventParent;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.xml.schema.AeSchemaDuration;

/**
 * Implementation of a repeatable alarm for use when a scope onAlarm contains
 * the repeatEvery syntax. 
 */
public class AeRepeatableOnAlarm extends AeOnAlarm implements IAeDynamicScopeParent
{
   // TODO (MF) refactor the event classes to enable some code reuse between the repeatable alarms and onEvent. Perhaps make all events support the notion of concurrency but only enable for some?  
   
   /** list of child scope instances created during onAlarm routines */
   private List mChildren = new ArrayList();

   /** 
    * list of child scope instances that have been restored for compensation 
    * purposes 
    */
   private List mCompensatableChildren = new ArrayList();

   /** value for the next scope instance created for this onAlarm */
   private int mInstanceValue = 1;
   
   /** calculated interval for the repeatEvery expression */
   private AeSchemaDuration mRepeatEveryDuration;
   
   /**
    * Ctor accepts def and parent
    * 
    * @param aDef
    * @param aParent
    */
   public AeRepeatableOnAlarm(AeOnAlarmDef aDef, IAeEventParent aParent)
   {
      super(aDef, aParent);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      if (isConcurrent())
      {
         for (Iterator iter = getChildren().iterator(); iter.hasNext();)
         {
            AeActivityScopeImpl scope = (AeActivityScopeImpl) iter.next();
            if (scope.isNormalCompletion())
            {
               getCompensatableChildren().add(scope);
            }
         }
         getChildren().clear();
      }
      
      // calculate repeatEvery duration and record as part of our state
      // TODO (MF) may change with the resolution of Issue R30
      setRepeatEveryDuration(AeAlarmCalculator.calculateRepeatInterval(this, getDef().getRepeatEveryDef()));
      if (getRepeatEveryDuration().isNegative() || getRepeatEveryDuration().isZero())
      {
         objectCompletedWithFault(getFaultFactory().getInvalidExpressionValue(IAeFaultFactory.TYPE_DURATION));
      }
      else
      {
         super.execute();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm#onAlarm()
    */
   public void onAlarm() throws AeBusinessProcessException
   {
      if (isConcurrent())
      {
         reschedule();
      }

      AeActivityScopeImpl scope = createScopeInstance();

      getProcess().queueObjectToExecute(scope);
   }

   /**
    * Creates the child scope instance
    */
   protected AeActivityScopeImpl createScopeInstance()
   {
      // get and increment the instance value
      int instance = getInstanceValue();
      setInstanceValue(getInstanceValue() + 1);
      
      // create a dynamic scope
      List scopes = AeDynamicScopeCreator.create(true, this, instance, instance);
      AeActivityScopeImpl scope = (AeActivityScopeImpl) scopes.get(0);
      
      // add to our list of children
      getChildren().add(scope);
      return scope;
   }

   /**
    * If there are no more children executing then the event will notify the 
    * scope that it is quiescent and give the scope a chance to complete.
    * 
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childComplete(IAeBpelObject aChild)
         throws AeBusinessProcessException
   {
      if (isConcurrent())
      {
         for (Iterator iter = getChildrenForStateChange(); iter.hasNext();)
         {
            AeActivityScopeImpl scope = (AeActivityScopeImpl) iter.next();
            if (!scope.getState().isFinal())
            {
               return;
            }
         }
         findEnclosingScope().alarmCompleted(this);
      }
      else
      {
         super.childComplete(aChild);
      }
   }

   /**
    * Reschedules the repeatEvery alarm
    * @throws AeBusinessProcessException
    */
   public void reschedule() throws AeBusinessProcessException
   {
      Date deadline = getRepeatEveryDuration().toDeadline(); 
      queueAlarm(deadline);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeDynamicScopeParent#getChildren()
    */
   public List getChildren()
   {
      return mChildren;
   }

   /**
    * @param aChildren the children to set
    */
   protected void setChildren(List aChildren)
   {
      mChildren = aChildren;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeDynamicScopeParent#getCompensatableChildren()
    */
   public List getCompensatableChildren()
   {
      return mCompensatableChildren;
   }

   /**
    * @param aCompensatableChildren the compensatableChildren to set
    */
   protected void setCompensatableChildren(List aCompensatableChildren)
   {
      mCompensatableChildren = aCompensatableChildren;
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
    * @see org.activebpel.rt.bpel.impl.activity.support.AeActivityParent#addActivity(org.activebpel.rt.bpel.IAeActivity)
    */
   public void addActivity(IAeActivity aActivity)
   {
      getChildren().add(aActivity);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeActivityParent#getActivity()
    */
   public IAeActivity getActivity()
   {
      // TODO (MF) remove after changing base class
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeActivityParent#setActivity(org.activebpel.rt.bpel.IAeActivity)
    */
   protected void setActivity(IAeActivity aActivity)
   {
      // TODO (MF) remove after changing base class
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm#isConcurrent()
    */
   public boolean isConcurrent()
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeActivityParent#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return getChildren().iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeDynamicScopeParent#getChildScopeDef()
    */
   public AeActivityScopeDef getChildScopeDef()
   {
      return (AeActivityScopeDef) getDef().getActivityDef();
   }

   /**
    * @return the repeatEveryDuration
    */
   public AeSchemaDuration getRepeatEveryDuration()
   {
      return mRepeatEveryDuration;
   }

   /**
    * @param aRepeatEveryDuration the repeatEveryDuration to set
    */
   public void setRepeatEveryDuration(AeSchemaDuration aRepeatEveryDuration)
   {
      mRepeatEveryDuration = aRepeatEveryDuration;
   }
}
