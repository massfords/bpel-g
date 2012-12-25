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

import org.activebpel.rt.bpel.def.activity.support.AeFromDef;

/**
 * Defs that can have a 'from' child will implement this interface.
 */
public interface IAeFromParentDef
{
   /**
    * Accessor method to obtain the From def.
    */
   public AeFromDef getFromDef();

   /**
    * Mutator method to set the From def.
    * @param aFrom the From def
    */
   public void setFromDef(AeFromDef aFrom);
}
