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

import org.activebpel.rt.bpel.def.IAeConditionParentDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the 'elseIf' construct in WS-BPEL 2.0.  Note that for bpel 1.1 processes, this class
 * models the switch's case child.
 */
public class AeElseIfDef extends AeElseDef implements IAeConditionParentDef
{
   /** The elseif's condition. */
   private AeConditionDef mCondition;

   /**
    * Default c'tor.
    */
   public AeElseIfDef()
   {
      super();
   }

   /**
    * @return Returns the condition.
    */
   public AeConditionDef getConditionDef()
   {
      return mCondition;
   }

   /**
    * @param aCondition The condition to set.
    */
   public void setConditionDef(AeConditionDef aCondition)
   {
      mCondition = aCondition;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.support.AeElseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
