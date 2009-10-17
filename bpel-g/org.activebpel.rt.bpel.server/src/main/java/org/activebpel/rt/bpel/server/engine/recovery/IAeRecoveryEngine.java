// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/IAeRecoveryEngine.java,v 1.3 2007/11/15 21:06:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;

/**
 * Extends {@link org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal}
 * to define the interface for a business process engine that can recover the
 * state of a process from a list of
 * {@link org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry}
 * instances.
 */
public interface IAeRecoveryEngine extends IAeBusinessProcessEngineInternal
{
   /**
    * Recovers the state of the specified process from the given list of {@link
    * org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry}
    * instances and returns a list of {@link
    * org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem}
    * instances representing recovered requests. Queues the recovered items if
    * <code>aQueueRecoveredItems</code> is <code>true</code>.
    * 
    * <p>Note that the list of recovered items does not include invoke requests.
    * Pending invokes are handled separately, because Suspend Process on Invoke
    * Recovery may require suspending the process instead of restarting the
    * invokes.</p>
    *
    * @param aProcess
    * @param aJournalEntries
    * @param aQueueRecoveredItems
    * @return A list of {@link org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem} instances.
    * @throws AeBusinessProcessException
    */
   public List recover(IAeBusinessProcess aProcess, List aJournalEntries, boolean aQueueRecoveredItems) throws AeBusinessProcessException;

   /**
    * Type safe getter for the specialized recovery process manager
    */
   public IAeRecoveryProcessManager getRecoveryProcessManager();
}
