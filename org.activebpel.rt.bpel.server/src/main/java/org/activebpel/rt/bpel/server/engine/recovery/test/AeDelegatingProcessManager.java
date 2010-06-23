// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/test/AeDelegatingProcessManager.java,v 1.14 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAePlanManager;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.IAeProcessPurgedListener;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Implements a process manager that delegates all method calls to an
 * underlying delegate process manager.
 */
public class AeDelegatingProcessManager extends AeDelegatingManager implements IAeProcessManager
{
   public static final String CONFIG_CLASS          = "Class"; //$NON-NLS-1$
   public static final String CONFIG_DEBUG          = "Debug"; //$NON-NLS-1$
   public static final String CONFIG_DELEGATE_CLASS = "DelegateClass"; //$NON-NLS-1$

   /** Configuration for this process manager. */
   private Map mConfig;

   /** <code>true</code> if and only if showing debug output. */
   private boolean mDebug = false;

   /**
    * Constructs a process manager that delegates all method calls to a
    * delegate process manager constructed from the given configuration.
    *
    * @param aConfig
    */
   public AeDelegatingProcessManager(Map aConfig) throws AeException
   {
      super(createDelegateProcessManager(aConfig));

      setConfig(aConfig);
   }

   /**
    * Constructs the delegate process manager from the specified configuration
    * map.
    *
    * @param aConfig
    * @throws AeException
    */
   protected static IAeProcessManager createDelegateProcessManager(Map aConfig) throws AeException
   {
      // Clone the configuration, replacing the value of the Class entry with
      // the value of the DelegateClass entry.
      Map delegateConfig = new HashMap(aConfig);
      String delegateClass = (String) delegateConfig.get(CONFIG_DELEGATE_CLASS);
      delegateConfig.put(CONFIG_CLASS, delegateClass);

      // Construct the delegate process manager.
      return (IAeProcessManager) AeEngineFactory.createConfigSpecificClass(delegateConfig);
   }

   /**
    * Writes formatted debugging output.
    */
   protected void debug(String aPattern, long aArgument)
   {
      debug(aPattern, new Object[] { new Long(aArgument) });
   }

   /**
    * Writes formatted debugging output.
    */
   protected void debug(String aPattern, Object[] aArguments)
   {
      if (isDebug())
      {
         System.out.println(MessageFormat.format(aPattern, aArguments));
      }
   }

   /**
    * Returns the base process manager.
    */
   protected IAeProcessManager getBaseProcessManager()
   {
      return (IAeProcessManager) getBaseManager();
   }

   /**
    * Returns configuration <code>Map</code>.
    */
   protected Map getConfig()
   {
      return mConfig;
   }

   /**
    * Returns value from configuration <code>Map</code>.
    *
    * @param aKey
    */
   protected String getConfig(String aKey)
   {
      return (String) getConfig().get(aKey);
   }

   /**
    * @return <code>true</code> if and only if showing debug output.
    */
   protected boolean isDebug()
   {
      return mDebug;
   }

   /**
    * Sets configuration.
    */
   protected void setConfig(Map aConfig)
   {
      mConfig = aConfig;
      mDebug = "true".equals(getConfig(CONFIG_DEBUG)); //$NON-NLS-1$
   }

   /*======================================================================
    * org.activebpel.rt.bpel.impl.IAeProcessManager methods
    *======================================================================
    */

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#createBusinessProcess(org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public IAeBusinessProcess createBusinessProcess(IAeProcessPlan aProcessPlan) throws AeBusinessProcessException
   {
      return getBaseProcessManager().createBusinessProcess(aProcessPlan);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcess(long)
    */
   public IAeBusinessProcess getProcess(long aProcessId) throws AeBusinessProcessException
   {
      return getBaseProcessManager().getProcess(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessNoUpdate(long)
    */
   public IAeBusinessProcess getProcessNoUpdate(long aProcessId) throws AeBusinessProcessException
   {
      return getBaseProcessManager().getProcessNoUpdate(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcesses(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public AeProcessListResult getProcesses(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getBaseProcessManager().getProcesses(aFilter);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessCount(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int getProcessCount(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getBaseProcessManager().getProcessCount(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessIds(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public long[] getProcessIds(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getBaseProcessManager().getProcessIds(aFilter);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessInstanceDetails(long)
    */
   public AeProcessInstanceDetail getProcessInstanceDetails(long aProcessId)
   {
      return getBaseProcessManager().getProcessInstanceDetails(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getProcessQName(long)
    */
   public QName getProcessQName(long aProcessId) throws AeBusinessProcessException
   {
      return getBaseProcessManager().getProcessQName(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#processEnded(long)
    */
   public void processEnded(long aProcessId)
   {
      getBaseProcessManager().processEnded(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalEntryDone(long, long)
    */
   public void journalEntryDone(long aProcessId, long aJournalId)
   {
      getBaseProcessManager().journalEntryDone(aProcessId, aJournalId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#releaseProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void releaseProcess(IAeBusinessProcess aProcess)
   {
      getBaseProcessManager().releaseProcess(aProcess);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#removeProcess(long)
    */
   public void removeProcess(long aProcessId) throws AeBusinessProcessException
   {
      getBaseProcessManager().removeProcess(aProcessId);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#removeProcesses(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int removeProcesses(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getBaseProcessManager().removeProcesses(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokeData(long, int,long, org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public long journalInvokeData(long aProcessId, int aLocationId, long aTransmissionId, IAeMessageData aMessageData, Map aProcessProperties) throws AeBusinessProcessException
   {
      return getBaseProcessManager().journalInvokeData(aProcessId, aLocationId, aTransmissionId, aMessageData, aProcessProperties);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokeFault(long, int, long, org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public long journalInvokeFault(long aProcessId, int aLocationId, long aTransmissionId, IAeFault aFault, Map aProcessProperties) throws AeBusinessProcessException
   {
      return getBaseProcessManager().journalInvokeFault(aProcessId, aLocationId, aTransmissionId, aFault, aProcessProperties);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInboundReceive(long, int, org.activebpel.rt.bpel.impl.queue.AeInboundReceive)
    */
   public long journalInboundReceive(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive) throws AeBusinessProcessException
   {
      return getBaseProcessManager().journalInboundReceive(aProcessId, aLocationId, aInboundReceive);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalSentReply(long, org.activebpel.rt.bpel.impl.queue.AeReply, java.util.Map)
    */
   public void journalSentReply(long aProcessId, AeReply aSentReply, Map aProcessProperties) throws AeBusinessProcessException
   {
      getBaseProcessManager().journalSentReply(aProcessId, aSentReply, aProcessProperties);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokeTransmitted(long, int, long)
    */
   public void journalInvokeTransmitted(long aProcessId, int aLocationId, long aTransmissionId) throws AeBusinessProcessException
   {
      getBaseProcessManager().journalInvokeTransmitted(aProcessId, aLocationId, aTransmissionId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCompensateSubprocess(long, java.lang.String)
    */
   public long journalCompensateSubprocess(long aProcessId, String aCoordinationId)
   {
      return getBaseProcessManager().journalCompensateSubprocess(aProcessId, aCoordinationId);
   }   
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalInvokePending(long, int)
    */
   public long journalInvokePending(long aProcessId, int aLocationId) throws AeBusinessProcessException
   {
      return getBaseProcessManager().journalInvokePending(aProcessId, aLocationId);
   }   
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#setPlanManager(org.activebpel.rt.bpel.IAePlanManager)
    */
   public void setPlanManager(IAePlanManager aPlanManager) throws AeBusinessProcessException
   {
      getBaseProcessManager().setPlanManager(aPlanManager);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#getNextJournalId()
    */
   public long getNextJournalId() throws AeBusinessProcessException
   {
      return getBaseProcessManager().getNextJournalId();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#transmissionIdDone(long, long)
    */
   public void transmissionIdDone(long aProcessId, long aTransmissionId)
   {
      getBaseProcessManager().transmissionIdDone(aProcessId, aTransmissionId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#addProcessPurgedListener(org.activebpel.rt.bpel.impl.IAeProcessPurgedListener)
    */
   public void addProcessPurgedListener(IAeProcessPurgedListener aListener)
   {
      getBaseProcessManager().addProcessPurgedListener(aListener);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#removeProcessPurgedListener(org.activebpel.rt.bpel.impl.IAeProcessPurgedListener)
    */
   public void removeProcessPurgedListener(IAeProcessPurgedListener aListener)
   {
      getBaseProcessManager().removeProcessPurgedListener(aListener);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalEntryForRestart(long, long)
    */
   public void journalEntryForRestart(long aProcessId, long aJournalId)
   {
      getBaseProcessManager().journalEntryForRestart(aProcessId, aJournalId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#recreateBusinessProcess(long, org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public IAeBusinessProcess recreateBusinessProcess(long aProcessId, IAeProcessPlan aProcessPlan) throws AeBusinessProcessException
   {
      return getBaseProcessManager().recreateBusinessProcess(aProcessId, aProcessPlan);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCoordinationQueueMessageReceived(long, org.activebpel.rt.bpel.coord.IAeProtocolMessage)
    */
   public long journalCoordinationQueueMessageReceived(long aProcessId,
         IAeProtocolMessage aMessage)
   {
      return getBaseProcessManager().journalCoordinationQueueMessageReceived(aProcessId, aMessage);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCancelProcess(long)
    */
   public long journalCancelProcess(long aProcessId)
   {
      return getBaseProcessManager().journalCancelProcess(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCancelSubprocessCompensation(long)
    */
   public long journalCancelSubprocessCompensation(long aProcessId)
   {
      return getBaseProcessManager().journalCancelSubprocessCompensation(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalReleaseCompensationResources(long)
    */
   public long journalReleaseCompensationResources(long aProcessId)
   {
      return getBaseProcessManager().journalReleaseCompensationResources(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalNotifyCoordinatorsParticipantClosed(long)
    */
   public long journalNotifyCoordinatorsParticipantClosed(long aProcessId)
   {
      return getBaseProcessManager().journalNotifyCoordinatorsParticipantClosed(aProcessId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCompensateCallback(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault)
    */
   public long journalCompensateCallback(long aProcessId, String aLocationPath,
         String aCoordinationId, IAeFault aFault)
   {
      return getBaseProcessManager().journalCompensateCallback(aProcessId, aLocationPath, aCoordinationId, aFault);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalCoordinatedActivityCompleted(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault)
    */
   public long journalCoordinatedActivityCompleted(long aProcessId,
         String aLocationPath, String aCoordinationId, IAeFault aFault)
   {
      return getBaseProcessManager().journalCoordinatedActivityCompleted(aProcessId, aLocationPath, aCoordinationId, aFault);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#journalDeregisterCoordination(long, java.lang.String, java.lang.String)
    */
   public long journalDeregisterCoordination(long aProcessId,
         String aLocationPath, String aCoordinationId)
   {
      return getBaseProcessManager().journalDeregisterCoordination(aProcessId, aLocationPath, aCoordinationId);
   }
}
