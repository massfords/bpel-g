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

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * A wrapper class for the condition and activity child defs of the if activity.  This wrapper def
 * is used to make the modelling of bpel 1.1 and 2.0 processes a bit more unified.
 */
public class AeIfDef extends AeElseIfDef
{
   /**
    * Default c'tor.
    */
   public AeIfDef()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.support.AeElseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
