//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityForEachDef.java,v 1.12 2006/09/22 19:52:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.IAeSingleActivityContainerDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition object for the "forEach" activity. This activity provides a way of
 * executing a child scope multiple times in sequence or in parallel.
 */
public class AeActivityForEachDef extends AeActivityDef implements IAeSingleActivityContainerDef, IAeLoopActivityDef
{
   /** optional completion condition for serial and parallel forEach */
   private AeForEachCompletionConditionDef mCompletionCondition;
   /** for each defs can only contain scope activities */
   private AeActivityScopeDef mChildDef;
   /** true if the foreach should execute the scope in parallel or false if sequentially */
   private boolean mParallel;

   /** start def contains expression that evaluates to the starting value used for the loop */
   private AeForEachStartDef mStartDef;
   /** final def contains expression that evaluates to the final value used for the loop */
   private AeForEachFinalDef mFinalDef;

   /** name of the counter variable (which is always an xs:unsignedInt */
   private String mCounterName;

   /**
    * @return Returns the completionCondition.
    */
   public AeForEachCompletionConditionDef getCompletionCondition()
   {
      return mCompletionCondition;
   }

   /**
    * Return true if the def contains the optional completion def
    */
   public boolean hasCompletionCondition()
   {
      return mCompletionCondition != null;
   }

   /**
    * @return Returns the finalDef.
    */
   public AeForEachFinalDef getFinalDef()
   {
      if (mFinalDef == null)
         mFinalDef = new AeForEachFinalDef();
      return mFinalDef;
   }

   /**
    * @param aFinalDef The finalDef to set.
    */
   public void setFinalDef(AeForEachFinalDef aFinalDef)
   {
      mFinalDef = aFinalDef;
   }

   /**
    * @return Returns the startDef.
    */
   public AeForEachStartDef getStartDef()
   {
      if (mStartDef == null)
         mStartDef = new AeForEachStartDef();
      return mStartDef;
   }

   /**
    * @param aStartDef The startDef to set.
    */
   public void setStartDef(AeForEachStartDef aStartDef)
   {
      mStartDef = aStartDef;
   }

   /**
    * @param aCompletionCondition The completionCondition to set.
    */
   public void setCompletionCondition(AeForEachCompletionConditionDef aCompletionCondition)
   {
      mCompletionCondition = aCompletionCondition;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeSingleActivityContainerDef#getActivityDef()
    */
   public AeActivityDef getActivityDef()
   {
      return mChildDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeSingleActivityContainerDef#setActivityDef(org.activebpel.rt.bpel.def.AeActivityDef)
    */
   public void setActivityDef(AeActivityDef aActivity)
   {
      mChildDef = (AeActivityScopeDef) aActivity;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeActivityContainerDef#replaceActivityDef(org.activebpel.rt.bpel.def.AeActivityDef, org.activebpel.rt.bpel.def.AeActivityDef)
    */
   public void replaceActivityDef(AeActivityDef aOldActivityDef, AeActivityDef aNewActivityDef)
   {
      setActivityDef(aNewActivityDef);
   }

   /**
    * @return Returns the parallel.
    */
   public boolean isParallel()
   {
      return mParallel;
   }

   /**
    * @param aParallel The parallel to set.
    */
   public void setParallel(boolean aParallel)
   {
      mParallel = aParallel;
   }

   /**
    * @return Returns the counterName.
    */
   public String getCounterName()
   {
      return mCounterName;
   }

   /**
    * @param aCounterName The counterName to set.
    */
   public void setCounterName(String aCounterName)
   {
      mCounterName = aCounterName;
   }

   /**
    * Convenience method that returns the child scope for the forEach.
    */
   public AeActivityScopeDef getChildScope()
   {
      if (getActivityDef() instanceof AeActivityScopeDef)
      {
         return (AeActivityScopeDef) getActivityDef();
      }
      return null;
   }
}
