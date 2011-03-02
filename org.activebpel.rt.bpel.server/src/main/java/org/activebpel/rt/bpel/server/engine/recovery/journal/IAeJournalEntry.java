// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/IAeJournalEntry.java,v 1.8 2008/04/03 21:55:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.xml.schema.AeTypeMapping;

/**
 * Defines interface for process journal entries.
 */
public interface IAeJournalEntry
{
   public static final int JOURNAL_ALARM                 = 1;
   public static final int JOURNAL_INBOUND_RECEIVE       = 2;
   public static final int JOURNAL_INVOKE_DATA           = 3;
   public static final int JOURNAL_INVOKE_FAULT          = 4;
   public static final int JOURNAL_SENT_REPLY            = 5;
   public static final int JOURNAL_INVOKE_TRANSMITTED    = 6;
   public static final int JOURNAL_COMPENSATE_SUBPROCESS = 7;
   public static final int JOURNAL_ENGINE_FAILURE        = 8;
   public static final int JOURNAL_INVOKE_PENDING        = 9;
   public static final int JOURNAL_COORDINATOR_MESSAGE_SENT        = 10;
   public static final int JOURNAL_COORDINATION_QUEUE_MESSAGE        = 11;
   public static final int JOURNAL_CANCEL_PROCESS        = 12;
   public static final int JOURNAL_CANCEL_SUBPROCESS_COMPENSATION        = 13;
   public static final int JOURNAL_RELEASE_COMPENSATION_RESOURCES        = 14;
   public static final int JOURNAL_NOTIFY_COORDINATORS        = 15;
   public static final int JOURNAL_COMPENSATE_CALLBACK      = 16;
   public static final int JOURNAL_COORDINATED_ACTIVITY_COMPLETED = 17;
   public static final int JOURNAL_DEREGISTER_COORDINATION = 18;

   // Assign 0 to JOURNAL_RESTART_PROCESS to position it at one end of the
   // range of entry type values. This allows us to use a > test instead of <>
   // test to exclude restart process journal entries in SQL (some SQL databases
   // handle <> or NOT queries with a table or index scan).
   public static final int JOURNAL_RESTART_PROCESS       = 0;

   /**
    * Dispatches the journal entry's data to the specified process.
    */
   public void dispatchToProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException;

   /**
    * Returns one of {@link #JOURNAL_ALARM}, {@link #JOURNAL_INBOUND_RECEIVE},
    * {@link #JOURNAL_INVOKE_DATA}, {@link #JOURNAL_INVOKE_FAULT},
    * {@link #JOURNAL_SENT_REPLY}, {@link #JOURNAL_INVOKE_TRANSMITTED},
    * {@link #JOURNAL_COMPENSATE_SUBPROCESS}, {@link #JOURNAL_ENGINE_FAILURE},
    * {@link #JOURNAL_INVOKE_PENDING}, or {@link #JOURNAL_RESTART_PROCESS}
    */
   public int getEntryType();

   /**
    * Returns entry's id from storage.
    */
   public long getJournalId();

   /**
    * Returns the location id of the activity that received the entry's data.
    */
   public int getLocationId();

   /**
    * Serializes entry for persistent storage; may return <code>null</code>.
    */
   public AeFastDocument serialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException;
   
   /**
    * Returns true if the journal item can be dispatched to the process given the 
    * process's current state.
    * @param aProcessState
    */
   public boolean canDispatch(int aProcessState);
}
