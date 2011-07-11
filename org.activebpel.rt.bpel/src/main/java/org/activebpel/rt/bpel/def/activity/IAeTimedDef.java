// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/IAeTimedDef.java,v 1.4 2006/07/20 20:45:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;

/**
 * Interface contains the getter methods for getting the duration or deadline
 * for a timed event like a wait or an alarm.
 */
public interface IAeTimedDef
{
   /**
    * Returns the for def, will be null if the def uses the deadline model.
    */
   public AeForDef getForDef();

   /**
    * Returns the until def, will be null if the def uses the duration model.
    */
   public AeUntilDef getUntilDef();
   
   /**
    * Returns the optional repeatEvery def
    */
   public AeRepeatEveryDef getRepeatEveryDef();
}
