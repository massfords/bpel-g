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

import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;

/**
 * Defs that can have 'for' and 'until' children will implement this interface.
 */
public interface IAeForUntilParentDef
{
   /**
    * Gets the 'for' def.
    */
   public AeForDef getForDef();
   
   /**
    * Sets the 'for' def.
    * 
    * @param aDef
    */
   public void setForDef(AeForDef aDef);
   
   /**
    * Gets the 'until' def.
    */
   public AeUntilDef getUntilDef();
   
   /**
    * Sets the 'until' def.
    * 
    * @param aDef
    */
   public void setUntilDef(AeUntilDef aDef);
}
