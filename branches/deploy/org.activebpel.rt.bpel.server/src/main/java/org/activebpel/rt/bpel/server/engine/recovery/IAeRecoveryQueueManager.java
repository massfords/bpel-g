// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/IAeRecoveryQueueManager.java,v 1.3 2007/07/09 16:28:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import java.util.List;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeQueueManager;

/**
 * Extends {@link org.activebpel.rt.bpel.impl.IAeQueueManager} to define the
 * interface for a queue manager used by an instance of {@link
 * org.activebpel.rt.bpel.server.engine.recovery.AeRecoveryEngine} for
 * recovery.
 */
public interface IAeRecoveryQueueManager extends IAeQueueManager
{
   /**
    * Sets the list of invoke transmitted journal entries to be used to restore
    * invoke transmission ids.
    */
   public void setInvokeTransmittedEntries(List aInvokeTransmittedEntries);

   /**
    * Sets container for alarm and queue manager items generated during
    * recovery.
    */
   public void setRecoveredItemsSet(IAeRecoveredItemsSet aRecoveredItemsSet);
   
   /**
    * Gets container for alarm and queue manager items generated during 
    * recovery.
    */
   public IAeRecoveredItemsSet getRecoveredItemsSet();

   /**
    * Sets the process that is being recovered.
    */
   public void setRecoveryProcess(IAeBusinessProcess aRecoveryProcess);

   /**
    * Sets the sent replies.
    */
   public void setSentReplies(List aSentReplies);
}
