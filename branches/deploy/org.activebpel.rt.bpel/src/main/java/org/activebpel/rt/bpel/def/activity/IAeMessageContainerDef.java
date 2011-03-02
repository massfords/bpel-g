// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity;

import java.util.Iterator;

import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;

/**
 * Interface for &lt;pick&gt;.
 */
public interface IAeMessageContainerDef extends IAeAlarmParentDef
{
   /**
    * Gets an iterator over the onMessage defs.
    */
   public Iterator getOnMessageDefs();

   /**
    * Adds the message
    * @param aMessage
    */
   public void addOnMessageDef(AeOnMessageDef aMessage);
}
