// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityWaitDef.java,v 1.8 2006/07/20 20:45:05 mford Exp $
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
import org.activebpel.rt.bpel.def.IAeForUntilParentDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel wait activity.
 */
public class AeActivityWaitDef extends AeActivityDef implements IAeTimedDef, IAeForUntilParentDef
{
   /** The 'for' child construct. */
   private AeForDef mForDef;
   /** The 'until' child construct. */
   private AeUntilDef mUntilDef;

   /**
    * Default constructor
    */
   public AeActivityWaitDef()
   {
      super();
   }

   /**
    * Convenience method to get the 'for' expression from the forDef.
    */
   public String getFor()
   {
      if (mForDef != null)
      {
         return mForDef.getExpression();
      }
      else
      {
         return null;
      }
   }

   /**
    * Convenience method to get the until expression from the untilDef.
    */
   public String getUntil()
   {
      if (mUntilDef != null)
      {
         return mUntilDef.getExpression();
      }
      else
      {
         return null;
      }
   }

   /**
    * @return Returns the forDef.
    */
   public AeForDef getForDef()
   {
      return mForDef;
   }

   /**
    * @param aForDef The forDef to set.
    */
   public void setForDef(AeForDef aForDef)
   {
      mForDef = aForDef;
   }

   /**
    * @return Returns the untilDef.
    */
   public AeUntilDef getUntilDef()
   {
      return mUntilDef;
   }

   /**
    * @param aUntilDef The untilDef to set.
    */
   public void setUntilDef(AeUntilDef aUntilDef)
   {
      mUntilDef = aUntilDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeTimedDef#getRepeatEveryDef()
    */
   public AeRepeatEveryDef getRepeatEveryDef()
   {
      return null;
   }
}
