// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeJournalEntryFactory.java,v 1.6 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeCancelProcessEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeCancelSubProcessCompensationEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeCompensateCallbackEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeCompensateSubprocessJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeCoordinatedActivityCompletedEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeCoordinationQueueMessageEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeDeregisterCoordinationEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeNotifyCoordinatorsParticipantClosedEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.AeReleaseCompensationResourcesEntry;
import org.w3c.dom.Document;

/**
 * Implements factory to construct instances of {@link IAeJournalEntry} from
 * the entry's persisted data: entry type, location id, journal entry id, and
 * storage document.
 */
public class AeJournalEntryFactory implements IAeJournalEntryFactory
{
   /** Singleton instance. */
   private static IAeJournalEntryFactory sInstance = new AeJournalEntryFactory();

   /**
    * Private constructor for singleton instance.
    */
   private AeJournalEntryFactory()
   {
   }

   /**
    * Returns singleton instance.
    */
   public static IAeJournalEntryFactory getInstance()
   {
      return sInstance;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntryFactory#newJournalEntry(int, int, long, org.w3c.dom.Document)
    */
   public IAeJournalEntry newJournalEntry(int aEntryType, int aLocationId, long aJournalId, Document aStorageDocument) throws AeException
   {
      IAeJournalEntry entry = null;

      switch (aEntryType)
      {
         case IAeJournalEntry.JOURNAL_ALARM:
            entry = new AeAlarmJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;

         case IAeJournalEntry.JOURNAL_INVOKE_DATA:
            entry = new AeInvokeDataJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;

         case IAeJournalEntry.JOURNAL_INVOKE_FAULT:
            entry = new AeInvokeFaultJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;

         case IAeJournalEntry.JOURNAL_INBOUND_RECEIVE:
            entry = new AeInboundReceiveJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;

         case IAeJournalEntry.JOURNAL_SENT_REPLY:
            entry = new AeSentReplyJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;
            
         case IAeJournalEntry.JOURNAL_INVOKE_TRANSMITTED:
            entry = new AeInvokeTransmittedJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;
            
         case IAeJournalEntry.JOURNAL_COMPENSATE_SUBPROCESS:
            entry = new AeCompensateSubprocessJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;

         case IAeJournalEntry.JOURNAL_ENGINE_FAILURE:
            entry = new AeEngineFailureJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;

         case IAeJournalEntry.JOURNAL_INVOKE_PENDING:
            entry = new AeInvokePendingJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;

         case IAeJournalEntry.JOURNAL_RESTART_PROCESS:
            entry = new AeRestartProcessJournalEntry(aLocationId, aJournalId, aStorageDocument);
            break;
            
         case IAeJournalEntry.JOURNAL_COORDINATION_QUEUE_MESSAGE:
            entry = new AeCoordinationQueueMessageEntry(aJournalId, aStorageDocument);
            break;
         
         case IAeJournalEntry.JOURNAL_CANCEL_PROCESS:
            entry = new AeCancelProcessEntry(aJournalId, aStorageDocument);
            break;
            
         case IAeJournalEntry.JOURNAL_CANCEL_SUBPROCESS_COMPENSATION:
            entry = new AeCancelSubProcessCompensationEntry(aJournalId, aStorageDocument);
            break;
            
         case IAeJournalEntry.JOURNAL_RELEASE_COMPENSATION_RESOURCES:
            entry = new AeReleaseCompensationResourcesEntry(aJournalId, aStorageDocument);
            break;
            
         case IAeJournalEntry.JOURNAL_NOTIFY_COORDINATORS:
            entry = new AeNotifyCoordinatorsParticipantClosedEntry(aJournalId, aStorageDocument);
            break;
            
         case IAeJournalEntry.JOURNAL_COMPENSATE_CALLBACK:
            entry = new AeCompensateCallbackEntry(aJournalId, aStorageDocument);
            break;
            
         case IAeJournalEntry.JOURNAL_COORDINATED_ACTIVITY_COMPLETED:
            entry = new AeCoordinatedActivityCompletedEntry(aJournalId, aStorageDocument);
            break;
            
         case IAeJournalEntry.JOURNAL_DEREGISTER_COORDINATION:
            entry = new AeDeregisterCoordinationEntry(aJournalId, aStorageDocument);
            break;

         default:
            throw new AeUnknownEntryTypeException(aEntryType, aLocationId);
      }

      return entry;
   }

   /**
    * Implements an exception that reports an unknown entry type.
    */
   protected static class AeUnknownEntryTypeException extends AeException
   {
      /**
       * Constructs an exception that reports an unknown entry type.
       */
      public AeUnknownEntryTypeException(int aEntryType, int aLocationId)
      {
         super(AeMessages.format("AeUnknownEntryTypeException.ERROR_UNKNOWN_TYPE", new Object[] { new Integer(aEntryType), new Integer(aLocationId) })); //$NON-NLS-1$
      }
   }
}
