// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/IAeRecoveredItem.java,v 1.2 2007/11/15 21:06:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;

/**
 * Defines the interface for recovered alarm and queue manager items.
 */
public interface IAeRecoveredItem
{
   /**
    * Returns the location id of the activity for the recovered item;
    * may return <code>0</code> if the location id is not relevant.
    */
   public int getLocationId();

   /**
    * Queues the recovered item to the given engine.
    */
   public void queueItem(IAeBusinessProcessEngineInternal aTargetEngine) throws AeBusinessProcessException;
   
   /**
    * Returns true if the recovered item is an instruction to remove an item
    * that was previously queued.
    */
   public boolean isRemoval();
}
