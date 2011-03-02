// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity.support;

import java.util.Iterator;

import org.activebpel.rt.bpel.def.AeBaseContainer;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the 'target' container bpel construct introduced in WS-BPEL 2.0.
 */
public class AeTargetsDef extends AeBaseContainer
{
   /** The optional joinCondition child. */
   private AeJoinConditionDef mJoinCondition;

   /**
    * Default c'tor.
    */
   public AeTargetsDef()
   {
      super();
   }

   /**
    * Adds a target def to the container.
    * 
    * @param aDef
    */
   public void addTargetDef(AeTargetDef aDef)
   {
      add(aDef);
   }
   
   /**
    * Gets an iterator over all of the target defs.
    */
   public Iterator getTargetDefs()
   {
      return getValues();
   }

   /**
    * @return Returns the joinCondition.
    */
   public AeJoinConditionDef getJoinConditionDef()
   {
      return mJoinCondition;
   }

   /**
    * @param aJoinCondition The joinCondition to set.
    */
   public void setJoinConditionDef(AeJoinConditionDef aJoinCondition)
   {
      mJoinCondition = aJoinCondition;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseContainer#isEmpty()
    */
   public boolean isEmpty()
   {
      boolean empty = super.isEmpty();
      if (getJoinConditionDef() != null)
      {
         empty = false;
      }
      return empty;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
