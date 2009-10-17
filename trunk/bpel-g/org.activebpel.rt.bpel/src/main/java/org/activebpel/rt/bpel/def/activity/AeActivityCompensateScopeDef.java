// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for the 'compensateScope' activity.
 */
public class AeActivityCompensateScopeDef extends AeActivityCompensateDef
{
   /** The name of the scope to compensate (the value of the 'target' attribute). */
   private String mTarget;  // Note: also models the BPEL 1.1 'scope' attribute of the 'compensate' activity.

   /**
    * Default constructor
    */
   public AeActivityCompensateScopeDef()
   {
      super();
   }

   /**
    * @return Returns the target.
    */
   public String getTarget()
   {
      return mTarget;
   }

   /**
    * @param aTarget The target to set.
    */
   public void setTarget(String aTarget)
   {
      mTarget = aTarget;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
