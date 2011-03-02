// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/AeOnAlarmDef.java,v 1.9 2006/07/20 20:45:04 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support;

import org.activebpel.rt.bpel.def.AeSingleActivityParentBaseDef;
import org.activebpel.rt.bpel.def.IAeForUntilParentDef;
import org.activebpel.rt.bpel.def.IAeSingleActivityContainerDef;
import org.activebpel.rt.bpel.def.activity.IAeTimedDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;


/** The onAlarm element used within the Pick activity */
public class AeOnAlarmDef extends AeSingleActivityParentBaseDef implements IAeSingleActivityContainerDef, IAeTimedDef, IAeForUntilParentDef
{
   /** The 'for' child construct. */
   private AeForDef mForDef;
   /** The 'until' child construct. */
   private AeUntilDef mUntilDef;
   /** The 'repeatEvery' child construct. */
   private AeRepeatEveryDef mRepeatEvery;

   /**
    * Default constructor
    */
   public AeOnAlarmDef()
   {
      super();
   }

   /**
    * Accessor method to obtain the For attribute.
    * 
    * @return name of For attribute
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
    * Accessor method to obtain the Until attribute.
    * 
    * @return name of Until attribute
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
    * @return Returns the repeatEvery.
    */
   public AeRepeatEveryDef getRepeatEveryDef()
   {
      return mRepeatEvery;
   }

   /**
    * @param aRepeatEvery The repeatEvery to set.
    */
   public void setRepeatEveryDef(AeRepeatEveryDef aRepeatEvery)
   {
      mRepeatEvery = aRepeatEvery;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
