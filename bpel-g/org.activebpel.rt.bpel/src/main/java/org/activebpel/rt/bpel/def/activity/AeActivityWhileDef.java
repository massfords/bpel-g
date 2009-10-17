// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityWhileDef.java,v 1.8 2007/03/03 02:45:33 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.IAeConditionParentDef;
import org.activebpel.rt.bpel.def.IAeSingleActivityContainerDef;
import org.activebpel.rt.bpel.def.IAeUncrossableLinkBoundary;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition bpel while activity.
 */
public class AeActivityWhileDef extends AeActivityDef 
   implements IAeSingleActivityContainerDef, IAeLoopActivityDef, 
              IAeConditionParentDef, IAeUncrossableLinkBoundary
{
   /** The while activity's 'condition' child construct. */
   private AeConditionDef mCondition;
   /** The while activity's child activity. */
   private AeActivityDef mActivity;

   /**
    * Default constructor
    */
   public AeActivityWhileDef()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeConditionParentDef#getConditionDef()
    */
   public AeConditionDef getConditionDef()
   {
      return mCondition;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeConditionParentDef#setConditionDef(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
    */
   public void setConditionDef(AeConditionDef aCondition)
   {
      mCondition = aCondition;
   }

   /**
    * Accessor method to get the activity associated with the while loop.
    * 
    * @return activity associated with while loop
    */
   public AeActivityDef getActivityDef()
   {
      return mActivity;
   }

   /**
    * Mutator method to set the activity associated with the while loop.
    * 
    * @param aActivity the activity to be set
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
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeUncrossableLinkBoundary#canCrossInbound()
    */
   public boolean canCrossInbound()
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeUncrossableLinkBoundary#canCrossOutbound()
    */
   public boolean canCrossOutbound()
   {
      return false;
   }
}
