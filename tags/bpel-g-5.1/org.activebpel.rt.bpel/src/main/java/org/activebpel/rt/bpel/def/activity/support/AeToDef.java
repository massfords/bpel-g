// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support;

import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Models the <code>to</code> element in a copy operation. Broke this out as its
 * own class since we want to visit it and to avoid any confusion with the <code>from</code>
 * portion of the copy.
 */
public class AeToDef extends AeVarDef
{
   /**
    * Default constructor
    */
   public AeToDef()
   {
      super();
   }

   /**
    * Returns true if this to is empty.  This is used during validation, since the to portion
    * of an assign's copy child should never be empty.
    */
   public boolean isEmpty()
   {
      return AeUtil.isNullOrEmpty(getVariable()) && AeUtil.isNullOrEmpty(getPartnerLink());
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeQueryDef#getBpelNamespace()
    */
   public String getBpelNamespace()
   {
      return AeDefUtil.getProcessDef(this).getNamespace();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
