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

import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;

/**
 * All constructs that can have a condition child should implement this interface.
 */
public interface IAeConditionParentDef
{
   /**
    * @return Returns the condition.
    */
   public AeConditionDef getConditionDef();

   /**
    * @param aCondition The condition to set.
    */
   public void setConditionDef(AeConditionDef aCondition);
}
