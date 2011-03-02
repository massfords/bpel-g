// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def;


/**
 * A base class for any def that has a single activity as its child.
 */
public abstract class AeSingleActivityParentBaseDef extends AeBaseDef implements IAeSingleActivityContainerDef
{
   /** Activity to execute if this fault handler is called */
   private AeActivityDef mActivity;

   /**
    * Default c'tor.
    */
   public AeSingleActivityParentBaseDef()
   {
      super();
   }

   /**
    * Accessor method to obtain the activity to be executed upon for
    * the fault condition.
    * 
    * @return the activity associated with the fault condition
    */
   public AeActivityDef getActivityDef()
   {
      return mActivity;
   }

   /**
    * Mutator method to set the activity which will be executed for 
    * the fault condition.
    * 
    * @param aActivity the activity to be executed
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
}
