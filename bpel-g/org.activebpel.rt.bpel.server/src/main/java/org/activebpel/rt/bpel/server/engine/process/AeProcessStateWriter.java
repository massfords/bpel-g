// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/process/AeProcessStateWriter.java,v 1.26 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.process;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.bpel.impl.storage.IAeProcessSnapshot;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.AePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.IAeProcessLogger;
import org.activebpel.rt.bpel.server.engine.recovery.journal.*;
import org.activebpel.rt.bpel.server.engine.recovery.journal.coord.*;
import org.activebpel.rt.bpel.server.engine.storage.*;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeDbUtils;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionException;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager;
import org.activebpel.rt.bpel.server.logging.IAePersistentLogger;
import org.activebpel.rt.bpel.server.logging.IAeProcessLogEntry;
import org.activebpel.rt.message.IAeMessageData;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Writes process state to persistent storage.
 */
public class AeProcessStateWriter implements IAeProcessStateWriter
{
   /** The process manager that owns this process state writer. */
   private final IAePersistentProcessManager mProcessManager;

   /**
    * Constructs the process state writer for the given process manager.
    *
    * @param aProcessManager
    */
   public AeProcessStateWriter(IAePersistentProcessManager aProcessManager)
   {
      mProcessManager = aProcessManager;
   }

   /**
    * Writes debugging output.
    */
   public static void debug(String aMessage)
   {
      if (isDebug())
      {
         System.out.println(aMessage);
      }
   }

   /**
    * Writes formatted debugging output.
    */
   public static void debug(String aPattern, Object[] aArguments)
   {
      if (isDebug()) // test for debugging before formatting
      {
         debug(MessageFormat.format(aPattern, aArguments));
      }
   }

   /**
    * Returns the engine for this process state writer.
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return getProcessManager().getEngine();
   }

   /**
    * Returns log entry for the current portion of the process log.
    *
    * @param aProcessId
    */
   protected IAeProcessLogEntry getProcessLogEntry(long aProcessId)
   {
      IAeProcessLogEntry entry = null;
      IAeProcessLogger logger = AeEngineFactory.getBean(IAeProcessLogger.class);

      if (logger instanceof IAePersistentLogger)
      {
         entry = ((IAePersistentLogger) logger).getLogEntry(aProcessId);
      }

      return entry;
   }

   /**
    * Returns the process manager that owns this process state writer.
    */
   protected IAePersistentProcessManager getProcessManager()
   {
      return mProcessManager;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getStorage()
    */
   public IAeProcessStateStorage getStorage()
   {
      return getProcessManager().getStorage();
   }

   /**
    * @return <code>true</code> if and only if the process manager is in debug
    * mode.
    */
   public static boolean isDebug()
   {
      return AePersistentProcessManager.isDebug();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalAlarm(long, int, int, int)
    */
   public long journalAlarm(long aProcessId, int aLocationId, int aGroupId, int aAlarmId)
   {
      debug(
         "Process {0,number,0}: received alarm for location {1,number,0} with alarmId {2,number,0}", //$NON-NLS-1$
         new Object[] {aProcessId, aLocationId, aAlarmId}
      );
      return writeJournalEntry(aProcessId, new AeAlarmJournalEntry(aLocationId, aGroupId, aAlarmId));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalInboundReceive(long, int, org.activebpel.rt.bpel.impl.queue.AeInboundReceive)
    */
   public long journalInboundReceive(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive)
   {
      debug(
         "Process {0,number,0}: received message for location {1,number,0}", //$NON-NLS-1$
         new Object[] {aProcessId, aLocationId}
      );
      return writeJournalEntry(aProcessId, new AeInboundReceiveJournalEntry(aLocationId, aInboundReceive));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalInvokeData(long, int, long, org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public long journalInvokeData(long aProcessId, int aLocationId, long aTransmissionId, IAeMessageData aMessageData, Map<String,String> aProcessProperties)
   {
      debug(
         "Process {0,number,0}: received data for invoke at location {1,number,0}", //$NON-NLS-1$
         new Object[] {aProcessId, aLocationId}
      );
      return writeJournalEntry(aProcessId, new AeInvokeDataJournalEntry(aLocationId, aTransmissionId, aMessageData, aProcessProperties));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalInvokeFault(long, int, long, org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public long journalInvokeFault(long aProcessId, int aLocationId, long aTransmissionId, IAeFault aFault, Map<String,String> aProcessProperties)
   {
      debug(
         "Process {0,number,0}: received fault for invoke at location {1,number,0}", //$NON-NLS-1$
         new Object[] {aProcessId, aLocationId}
      );
      return writeJournalEntry(aProcessId, new AeInvokeFaultJournalEntry(aLocationId, aTransmissionId, aFault, aProcessProperties));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalSentReply(long, org.activebpel.rt.bpel.impl.queue.AeReply, java.util.Map)
    */
   public long journalSentReply(long aProcessId, AeReply aSentReply, Map aProcessProperties)
   {
      debug(
         "Process {0,number,0}: sent reply id {1,number,0}", //$NON-NLS-1$
         new Object[] {aProcessId, aSentReply.getReplyId()}
      );
      return writeJournalEntry(aProcessId, new AeSentReplyJournalEntry(aSentReply, aProcessProperties));
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalInvokeTransmitted(long, int, long)
    */
   public long journalInvokeTransmitted(long aProcessId, int aLocationId, long aTransmissionId)
   {
      debug(
         "Process {0,number,0}: transmitted invoke at location {1,number,0} with txid {2,number,0}", //$NON-NLS-1$
         new Object[] {aProcessId, aLocationId, aTransmissionId}
      );
      return writeJournalEntry(aProcessId, new AeInvokeTransmittedJournalEntry(aLocationId, aTransmissionId));
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalCompensateSubprocess(long, java.lang.String)
    */
   public long journalCompensateSubprocess(long aProcessId, String aCoordinationId)
   {
      debug(
         "Process {0,number,0}: subprocess compensate for coordination id {1}", //$NON-NLS-1$
         new Object[] {aProcessId, aCoordinationId }
      );
      return writeJournalEntry( aProcessId, new AeCompensateSubprocessJournalEntry(aCoordinationId) );     
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalInvokePending(long, int)
    */
   public long journalInvokePending(long aProcessId, int aLocationId)
   {
      debug(
         "Process {0,number,0}: invoke pending at location {1,number,0}", //$NON-NLS-1$
         new Object[] {aProcessId, aLocationId}
      );
      return writeJournalEntry(aProcessId, new AeInvokePendingJournalEntry(aLocationId));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalEngineFailure(long, int)
    */
   public long journalEngineFailure(long aProcessId, int aEngineId)
   {
      debug(
         "Process {0,number,0}: recovery from engine {1,number,0}", //$NON-NLS-1$
         new Object[] {aProcessId, aEngineId}
      );
      return writeJournalEntry(aProcessId, new AeEngineFailureJournalEntry(aEngineId));
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalCoordinationQueueMessage(long, org.activebpel.rt.bpel.coord.IAeProtocolMessage)
    */
   public long journalCoordinationQueueMessage(long aProcessId, IAeProtocolMessage aMessage)
   {
      debug(
            "Process {0,number,0}: participant message {1}", //$NON-NLS-1$
            new Object[] {aProcessId, aMessage.getSignal() }
         );
      return writeJournalEntry(aProcessId, new AeCoordinationQueueMessageEntry(aProcessId, aMessage));
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalCancelProcess(long)
    */
   public long journalCancelProcess(long aProcessId)
   {
      debug(
            "Process {0,number,0}: journaling cancel process", //$NON-NLS-1$
            new Object[] {aProcessId}
         );
      return writeJournalEntry(aProcessId, new AeCancelProcessEntry());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalCancelSubprocessCompensation(long)
    */
   public long journalCancelSubprocessCompensation(long aProcessId)
   {
      debug(
            "Process {0,number,0}: journaling cancel subprocess compensation", //$NON-NLS-1$
            new Object[] {aProcessId}
         );
      return writeJournalEntry(aProcessId, new AeCancelSubProcessCompensationEntry());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalReleaseCompensationResources(long)
    */
   public long journalReleaseCompensationResources(long aProcessId)
   {
      debug(
            "Process {0,number,0}: journaling release compensation resources", //$NON-NLS-1$
            new Object[] {aProcessId}
         );
      return writeJournalEntry(aProcessId, new AeReleaseCompensationResourcesEntry());
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalNotifyCoordinatorsParticipantClosed(long)
    */
   public long journalNotifyCoordinatorsParticipantClosed(long aProcessId)
   {
      debug(
            "Process {0,number,0}: journaling notification of coordinators that participant closed", //$NON-NLS-1$
            new Object[] {aProcessId}
         );
      return writeJournalEntry(aProcessId, new AeNotifyCoordinatorsParticipantClosedEntry());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalCompensateCallback(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault)
    */
   public long journalCompensateCallback(long aProcessId, String aLocationPath,
         String aCoordinationId, IAeFault aFault)
   {
      debug(
            "Process {0,number,0}: journaling compensation callback", //$NON-NLS-1$
            new Object[] {aProcessId}
         );
      return writeJournalEntry(aProcessId, new AeCompensateCallbackEntry(aLocationPath, aCoordinationId, aFault));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalCoordinatedActivityCompleted(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault)
    */
   public long journalCoordinatedActivityCompleted(long aProcessId,
         String aLocationPath, String aCoordinationId, IAeFault aFault)
   {
      debug(
            "Process {0,number,0}: journaling coordinated activity completed", //$NON-NLS-1$
            new Object[] {aProcessId}
         );
      return writeJournalEntry(aProcessId, new AeCoordinatedActivityCompletedEntry(aLocationPath, aCoordinationId, aFault));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#journalDeregisterCoordination(long, java.lang.String, java.lang.String)
    */
   public long journalDeregisterCoordination(long aProcessId,
         String aLocationPath, String aCoordinationId)
   {
      debug(
            "Process {0,number,0}: journaling deregister of coordination at {1}", //$NON-NLS-1$
            new Object[] {aProcessId, aLocationPath }
         );
      return writeJournalEntry(aProcessId, new AeDeregisterCoordinationEntry(aLocationPath, aCoordinationId));
   }

   /**
    * Rolls back the active transaction manager transaction, consuming any
    * exceptions.
    */
   protected void rollbackTransaction(long aProcessId)
   {
      try
      {
         AeTransactionManager.getInstance().rollback();
      }
      catch (AeTransactionException e)
      {
         AeException.logError(e, AeMessages.format("AeProcessStateWriter.ERROR_RollbackTransaction", aProcessId)); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#writeProcess(org.activebpel.rt.bpel.server.engine.process.AeProcessWrapper)
    */
   public int writeProcess(AeProcessWrapper aProcessWrapper) throws AeBusinessProcessException
   {
      long processId = aProcessWrapper.getProcessId();
      int n; // pending invokes count

      // Get log entry for the current portion of the process log.
      IAeProcessLogEntry logEntry = getProcessLogEntry(processId);

      if (getProcessManager().isContainerManaged(processId))
      {
         IAeProcessStateConnection connection = getStorage().getConnection(processId, true);

         try
         {
            n = writeProcess(connection, aProcessWrapper, logEntry);

            if (n > 0)
            {
               debug(
                  "Process {0,number,0}: *** service flow has {1,choice,1#1 invoke|1<{1,number,0} invokes} pending ***", //$NON-NLS-1$
                  new Object[] {processId, n}
               );
            }
         }
         finally
         {
            getStorage().releaseConnection(connection);
         }
      }
      else
      {
         int tryCount = getProcessManager().getDeadlockTryCount();
         AeStorageException firstException = null;
         
         for (int tries = 0; true; )
         {
            // begin transaction.
            AeTransactionManager.getInstance().begin();
            IAeProcessStateConnection connection = getStorage().getConnection(processId, false);

            try
            {
               n = writeProcess(connection, aProcessWrapper, logEntry);

               // Commit all changes and stop looping.
               AeTransactionManager.getInstance().commit();
               break;
            }
            catch (AeStorageException e)
            {
               // Rollback all changes.
               rollbackTransaction(processId);

               // Retry if this is a SQL exception and we haven't exhausted the
               // try count.  
               // Note(Eric): Tamino will throw a SQLException specifically when a dead lock is detected (i.e. throws a common/well-known exception).               
               // TODO (EPW) We need to have a method isRetryableException on the provider factory...
               if ((e.getCause() instanceof SQLException) && (++tries < tryCount))
               {
                  if (firstException == null)
                  {
                     firstException = e;
                  }

                  // backoff wait time
                  // skew the max wait time by 5 so our range will be 0-128ms up to 0-2048ms
                  AeDbUtils.backOffWait(tries + 6, processId, "AeProcessStateWriter.ERROR_0"); //$NON-NLS-1$
               }
               // Otherwise, we're done.
               else
               {
                  if (firstException != null)
                  {
                     AeException.logError(firstException.getCause(), AeMessages.format("AeProcessStateWriter.ERROR_SaveProcessFirstException", processId)); //$NON-NLS-1$
                  }

                  throw e;
               }
            }
            catch (AeTransactionException e)
            {
               rollbackTransaction(processId);

               throw e;
            }
         } // end for loop
      }

      // If we get to this point, then we can safely clear this log entry's
      // portion from the process log.
      if (logEntry != null)
      {
         logEntry.clearFromLog();
      }

      return n;
   }

   /**
    * Saves given {@link IAeJournalEntry} instance for possible recovery in the
    * event of engine failure.
    */
   protected long writeJournalEntry(long aProcessId, IAeJournalEntry aJournalEntry)
   {
      if (getProcessManager().isPersistent(aProcessId))
      {
         int tryCount = getProcessManager().getDeadlockTryCount();
         AeStorageException firstException = null;

         for (int tries = 0; true; )
         {
            try
            {
               // Successful insertion of the journal entry breaks the loop.
               return getStorage().writeJournalEntry(aProcessId, aJournalEntry);
            }
            catch (AeStorageException e)
            {
               // Retry if this is a SQL exception and we haven't exhausted the
               // try count.  
               // TODO (EPW) We need to have a method isRetryableException on the provider factory...
               if ((e.getCause() instanceof SQLException) && (++tries < tryCount))
               {
                  if (firstException == null)
                  {
                     firstException = e;
                  }

                  AeDbUtils.backOffWait(tries + 6, aProcessId, "AeProcessStateWriter.ERROR_3");
               }
               // Otherwise, we're done.
               else
               {
                  if (firstException != null)
                  {
                     AeException.logError(firstException.getCause(), AeMessages.format("AeProcessStateWriter.ERROR_WriteJournalEntryFirstException", aProcessId)); //$NON-NLS-1$
                  }

                  AeException.logError(e, AeMessages.format("AeProcessStateWriter.ERROR_WriteJournalEntryLastException", aProcessId)); //$NON-NLS-1$

                  // TODO (KR) Should this throw the storage exception rather than break?
                  break;
               }
            }
         }
      }

      return IAeProcessManager.NULL_JOURNAL_ID;
   }

   /**
    * Saves process state and variables to the given storage connection.
    *
    * @param aConnection
    * @param aProcessWrapper
    * @param aLogEntry
    * @return The number of pending invoke activities (for debugging output).
    * @throws AeBusinessProcessException
    */
   protected int writeProcess(IAeProcessStateConnection aConnection, AeProcessWrapper aProcessWrapper, IAeProcessLogEntry aLogEntry)
         throws AeBusinessProcessException
   {
      IAeBusinessProcess process = aProcessWrapper.getProcess();

      // Get a process snapshot.
      IAeProcessSnapshot snapshot = process.getProcessSnapshot();

      // Save the process state.
      AeFastDocument processDocument = snapshot.serializeProcess(true);
      int processState = process.getProcessState().value();
      int processStateReason = process.getProcessStateReason().value();
      Date startDate = process.getStartDate();
      Date endDate = process.getEndDate();
      int pendingInvokesCount = snapshot.getPendingInvokes().size();

      aConnection.saveProcess(processDocument, processState, processStateReason, startDate, endDate, pendingInvokesCount);

      // Build the set of live variables for
      // IAeProcessStateConnection#trimStoredVariables.
      IAeLocationVersionSet liveSet = new AeLocationVersionSet();

      // Iterate through all live variable location paths.
       for (Object o : snapshot.getVariableLocationPaths()) {
           String locationPath = (String) o;
           int locationId = process.getLocationId(locationPath);
           Set versionNumbers = snapshot.getVariableVersionNumbers(locationPath);

           // Iterate through all version numbers for this location path.
           for (Object vn : versionNumbers) {
               int versionNumber = ((Number) vn).intValue();
               liveSet.add(locationId, versionNumber);

               IAeVariable variable = snapshot.getVariable(locationPath, versionNumber);
               if ((variable.hasData() || variable.hasAttachments()) && !aConnection.isStoredVariable(locationId, versionNumber)) {
                   AeFastDocument variableDocument = snapshot.serializeVariable(variable);
                   aConnection.saveVariable(process, variable, variableDocument);
               }
           }
       }

      // Trim the set of stored variables to those that are live now.
      aConnection.trimStoredVariables(liveSet);

      // Save new persistent log data.
      aConnection.saveLog(aLogEntry);

      // Remove journal entries that have been incorporated into the process
      // state.
      aConnection.removeJournalEntries(aProcessWrapper.getCompletedJournalIds());

      // If we need to set aside a journal entry to restart the process, then
      // update the journal entry's entry type.
      long journalIdForRestart = aProcessWrapper.getJournalIdForRestart();
      if (journalIdForRestart != IAeProcessManager.NULL_JOURNAL_ID)
      {
         aConnection.updateJournalEntryType(journalIdForRestart, IAeJournalEntry.JOURNAL_RESTART_PROCESS);
      }

      // Remove completed transmission ids.      
      try
      {
         getProcessManager().getEngine().getTransmissionTracker().remove(aProcessWrapper.getCompletedTransmissionIds());
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(e.getMessage(), e);
      }

      return pendingInvokesCount;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateWriter#getNextJournalId()
    */
   public long getNextJournalId() throws AeBusinessProcessException
   {
      try
      {
         return getStorage().getNextJournalId();
      }
      catch (Throwable t)
      {
         throw new AeBusinessProcessException(t.getMessage(), t);
      }
   }
}
