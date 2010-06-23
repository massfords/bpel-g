// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AeBpelEngine.java,v 1.107 2008/02/17 21:38:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeWSDLPolicyHelper;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.impl.AeConflictingRequestException;
import org.activebpel.rt.bpel.impl.AeCorrelationViolationException;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeTimeoutPolicy;
import org.activebpel.rt.bpel.impl.IAeAttachmentManager;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.impl.IAeEnginePartnerLinkStrategy;
import org.activebpel.rt.bpel.impl.IAeLockManager;
import org.activebpel.rt.bpel.impl.IAeManager;
import org.activebpel.rt.bpel.impl.IAeManagerVisitor;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.IAeQueueManager;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.addressing.IAePartnerAddressing;
import org.activebpel.rt.bpel.server.admin.AeEngineDetail;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.coord.AeRegistrationRequest;
import org.activebpel.rt.bpel.server.coord.subprocess.IAeSpCoordinating;
import org.activebpel.rt.bpel.server.deploy.AeProcessDeploymentFactory;
import org.activebpel.rt.bpel.server.deploy.AeRoutingInfo;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeInboundReceiveJournalEntry;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionException;
import org.activebpel.rt.bpel.server.engine.transaction.AeTransactionManager;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.work.AeAbstractWork;
import org.activebpel.work.input.IAeInputMessageWork;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;
import org.activebpel.wsio.receive.AeMessageContext;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Element;

/**
 * The runtime BPEL engine. There are only minor differences between the runtime
 * engine and the simulation engine. This is because most of the behavioral differences
 * are encapsulated in interfaces.
 */
public class AeBpelEngine extends AeAbstractServerEngine
{
   /** current state of the engine */
   private int mState = IAeEngineAdministration.CREATED;

   /** current state info */
   private String mErrorInfo;

   /** A list of managers. */
   private List mManagers = new LinkedList();

   /**
    * Creates a server based business process engine with the passed queue,
    * process, and alarm managers.
    *
    * @param aEngineConfiguration The engine configuration to use for this engine.
    * @param aQueueManager The queue manager to be associated with this engine.
    * @param aProcessManager The process manager to be associated with this engine.
    * @param aLockManager The lock manager to be associated with this engine.
    * @param aAttachmentManager The lock manager to be associated with this engine.
    */
   public AeBpelEngine(
      IAeEngineConfiguration aEngineConfiguration,
      IAeQueueManager aQueueManager,
      IAeProcessManager aProcessManager,
      IAeLockManager aLockManager,
      IAeAttachmentManager aAttachmentManager) throws Exception
   {
      super(aEngineConfiguration, aQueueManager, aProcessManager, aLockManager, aAttachmentManager);

      getManagers().add(aQueueManager);
      getManagers().add(aProcessManager);
      getManagers().add(aLockManager);

      getManagers().add(aAttachmentManager);
      setPartnerLinkStrategy(new AeServerPartnerLinkStrategy());
   }

   /**
    * Sets the coordination manager.
    * @param aCoordinationManager coordination manager.
    */
   public void setCoordinationManager(IAeCoordinationManagerInternal aCoordinationManager)
   {
      super.setCoordinationManager( aCoordinationManager );
      if (aCoordinationManager != null)
      {
         getManagers().add(aCoordinationManager) ;
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal#registerForCoordination(org.activebpel.wsio.receive.IAeMessageContext, org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public boolean registerForCoordination(IAeMessageContext aContext, IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      IAeCoordinationContext coordCtx = null;
      try
      {
         // get the coordination context.
         coordCtx = getCoordinationContext( aContext );
      }
      catch (Throwable t)
      {
         AeException.logError(t, t.getMessage());
         // propagate errors up to the process.
         throw new AeBusinessProcessException(t.getMessage(), t);
      }

      // create a coordination registration request.
      AeRegistrationRequest regRequest = new AeRegistrationRequest();
      regRequest.setCoordinationContext(coordCtx);
      regRequest.setProtocolIdentifier(IAeSpCoordinating.AESP_PARTICIPANT_COMPLETION_PROTOCOL);
      regRequest.setProperty(IAeCoordinating.AE_COORD_PID, String.valueOf(aProcess.getProcessId()) );
      IAeCoordinationManager mgr = getCoordinationManager();
      try
      {
         // register for coordination - assuming the subprocess is good to go.
         mgr.register(regRequest);
         return true;
      }
      catch (AeCoordinationException e)
      {
         // propergate errors up to the process.
         throw new AeBusinessProcessException(e.getMessage(), e);
      }
   }

   /**
    * Create lifecycle of the engine which will create all managers.
    * @throws AeException
    */
   public void create() throws AeException
   {
      try
      {
         createManagers();
      }
      catch (Exception e)
      {
         throw new AeException(AeMessages.getString("AeEngineFactory.ERROR_6"), e); //$NON-NLS-1$
      }
   }

   /**
    * Gets the current engine information.
    */
   public AeEngineDetail getEngineInfo()
   {
      AeEngineDetail detail = new AeEngineDetail();
      detail.setStartTime(getStartDate());
      detail.setState(getState());
      detail.setMonitorStatus(getMonitorStatus());
      if (getState() == IAeEngineAdministration.ERROR)
      {
         detail.setErrorMessage(getErrorInfo());
      }
      return detail;
   }

   /**
    * Creates the managers used by the engine. Any initialization work that the
    * managers must do in order to do their jobs (e.g. init a db connection) should
    * be done here in order to catch any fatal errors.
    */
   protected void createManagers() throws Exception
   {
      visitManagers(IAeManagerVisitor.CREATE);
   }

   /**
    * Starts all of the managers.
    */
   protected void startManagers() throws Exception
   {
      visitManagers(IAeManagerVisitor.START);
   }

   /**
    * Prepares all of the managers to start.
    */
   protected void prepareManagersToStart() throws Exception
   {
      visitManagers(IAeManagerVisitor.PREPARE);
   }

   /**
    * Stops all of the managers.
    */
   protected void stopManagers()
   {
      visitManagersNoException(IAeManagerVisitor.STOP);
   }

   /**
    * Destroys all of the managers.
    */
   protected void destroyManagers()
   {
      visitManagersNoException(IAeManagerVisitor.DESTROY);
   }

   /**
    * Getter for the engine state.
    */
   public int getState()
   {
      return mState;
   }

   /**
    * Setter for the engine state.
    * @param aState
    */
   protected void setState( int aState )
   {
      mState = aState;
   }

   /**
    * Starts the engine instances.  Note this implementation
    * just calls start.
    */
   public void startAll() throws AeBusinessProcessException
   {
      start();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal#start()
    */
   public void start() throws AeBusinessProcessException
   {
      try
      {
         super.start();

         setState(IAeEngineAdministration.STARTING);
         prepareManagersToStart();

         setState(IAeEngineAdministration.RUNNING);
         startManagers();
      }
      catch (Exception e)
      {
         fail(e.getLocalizedMessage());
         throw new AeBusinessProcessException(AeMessages.getString("AeBpelEngine.ERROR_0"), e); //$NON-NLS-1$
      }
   }

   /**
    * Call if start failed.
    * @param aMessage
    */
   public void fail(String aMessage)
   {
      // Stop the managers...
      stopManagers();

      setState(IAeEngineAdministration.ERROR);
      String errMsg = formatErrorMessage( aMessage );
      setErrorInfo( errMsg );
   }

   /**
    * Format error message.
    * @param aLocalizedMessage
    */
   protected String formatErrorMessage( String aLocalizedMessage )
   {
      StringBuffer msgBuffer = new StringBuffer(AeMessages.getString("AeBpelEngine.ERROR_0") + "-"); //$NON-NLS-1$ //$NON-NLS-2$
      if(aLocalizedMessage != null)
      {
         msgBuffer.append( aLocalizedMessage );
      }
      else
      {
         msgBuffer.append( AeMessages.getString("AeBpelEngine.2") );          //$NON-NLS-1$
      }
      return msgBuffer.toString();
   }

   /**
    * Stops the engine. The engine will fault if it receives any inbound messages
    * while stopped.
    */
   public void stop() throws AeBusinessProcessException
   {
      super.stop();

      setState(IAeEngineAdministration.STOPPING);

      stopManagers();

      setState(IAeEngineAdministration.STOPPED);
   }

   /**
    * Shut down the bpel engine. This shuts down managers used by the engine (e.g. timer and work).
    */
   public void shutDown() throws AeBusinessProcessException
   {
      // Stop the engine prior to shutting down if we are currently running
      if (getState() == IAeEngineAdministration.RUNNING)
         stop();

      setState(IAeEngineAdministration.SHUTTINGDOWN);
      AeEngineFactory.shutDownWorkManager();
      AeEngineFactory.shutDownTimerManager();
      destroyManagers();
      setState(IAeEngineAdministration.SHUTDOWN);
   }
   
   /**
    * Overrides method to get timeout from policy if one has been specified 
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#getWebServiceTimeout(org.activebpel.rt.bpel.impl.IAeProcessPlan, java.lang.String)
    */
   protected int getWebServiceTimeout(IAeProcessPlan aPlan, String aPartnerLink)
   {
      IAeServiceDeploymentInfo deployInfo = AeProcessDeploymentFactory.getDeploymentForPlan(aPlan).getServiceInfo(aPartnerLink);
      Element timeoutPolicy = AeWSDLPolicyHelper.getPolicyElement(deployInfo.getPolicies(), AeTimeoutPolicy.TIMEOUT_ID);
      if (timeoutPolicy != null)
         return AeTimeoutPolicy.getTimeoutValue(timeoutPolicy);
      
      return super.getWebServiceTimeout(aPlan, aPartnerLink);
   }

   /**
    * Helper class for executing the call to dispatch receive asynchronously
    *
    * <p><em>Note: This class is now declared as <code>static</code> and
    * <code>Serializable</code> (via {@link IAeInputMessageWork}) to support
    * customers who may want to transfer input message work items from one
    * machine to another.</em></p>
    */
   private static class AeDispatchReceiveRunnableHelper extends AeAbstractWork implements IAeInputMessageWork
   {
      /** process id */
      private long mProcessId;
      /** location id of message receiver */
      private int mLocationId;
      /** data for the receive */
      private transient AeInboundReceive mInboundReceive;
      /** process journal id of inbound message */
      private long mJournalId;
      /** current BPEL engine */
      private transient AeBpelEngine mBpelEngine;

      /**
       * Creates the helper and readies the object for execution
       * @param aProcessId
       * @param aLocationId
       * @param aInboundReceive
       * @param aJournalId
       * @param aBpelEngine
       */
      private AeDispatchReceiveRunnableHelper(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive, long aJournalId, AeBpelEngine aBpelEngine)
      {
         mProcessId = aProcessId;
         mLocationId = aLocationId;
         mInboundReceive = aInboundReceive;
         mJournalId = aJournalId;
         mBpelEngine = aBpelEngine;
      }

      /**
       * @see java.lang.Runnable#run()
       */
      public void run()
      {
         try
         {
            if (mInboundReceive == null)
            {
               mInboundReceive = restoreInboundReceive();
            }

            // call dispatch using 'synchronous' flag since this is on a worker thread.
            getBpelEngine().dispatchReceiveData(mProcessId, mLocationId, mInboundReceive, mJournalId, true /* runSynchronously */);
         }
         catch (Throwable t)
         {
            AeException.logError(t, AeMessages.format("AeBpelEngine.ERROR_4", mProcessId)); //$NON-NLS-1$
         }
      }

      /**
       * Convenience method returns current BPEL engine.
       */
      protected AeBpelEngine getBpelEngine()
      {
         if (mBpelEngine == null)
         {
            mBpelEngine = (AeBpelEngine) AeEngineFactory.getEngine();
         }

         return mBpelEngine;
      }

      /**
       * Restores inbound receive from its journal entry.
       *
       * @throws AeBusinessProcessException
       */
      protected AeInboundReceive restoreInboundReceive() throws AeBusinessProcessException
      {
         IAeJournalEntry entry = getBpelEngine().getJournalEntry(mJournalId);
         return getBpelEngine().deserializeInboundReceive(mProcessId, entry);
      }
   }

   /**
    * Helper class for asynchronous process execution
    *
    * <p><em>Note: This class is now declared as <code>static</code> and
    * <code>Serializable</code> (via {@link IAeInputMessageWork}) to support
    * customers who may want to transfer input message work items from one
    * machine to another.</em></p>
    */
   private static class AeProcessExecutionRunnableHelper extends AeAbstractWork implements IAeInputMessageWork
   {
      /** pid for the process that we want to execute */
      private long mProcessId;
      /** current BPEL engine */
      private transient AeBpelEngine mBpelEngine;

      /**
       * Creates the helper class and preps it for execution
       * @param aProcessId
       * @param aBpelEngine
       */
      private AeProcessExecutionRunnableHelper(long aProcessId, AeBpelEngine aBpelEngine)
      {
         mProcessId = aProcessId;
         mBpelEngine = aBpelEngine;
      }

      /**
       * @see java.lang.Runnable#run()
       */
      public void run()
      {
         try
         {
            getBpelEngine().executeProcess(mProcessId, true);
         }
         catch (Throwable t)
         {
            AeException.logError(t, AeMessages.format("AeBpelEngine.ERROR_3", mProcessId)); //$NON-NLS-1$
         }
      }

      /**
       * Convenience method returns current BPEL engine.
       */
      protected AeBpelEngine getBpelEngine()
      {
         if (mBpelEngine == null)
         {
            mBpelEngine = (AeBpelEngine) AeEngineFactory.getEngine();
         }

         return mBpelEngine;
      }
   }

   /**
    * Overrides method to handle multi-start processes due to the possibility of
    * simultaneous arrival of start messsages.
    *
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#createProcessWithMessage(org.activebpel.rt.bpel.impl.queue.AeInboundReceive, org.activebpel.wsio.IAeMessageAcknowledgeCallback, boolean)
    */
   protected long createProcessWithMessage(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback, boolean aQueueForExecution)
      throws AeBusinessProcessException
   {
      if (aInboundReceive.getProcessPlan().getProcessDef().isMultiStart())
      {
         return doMultiStartCreateProcessWithMessage(aInboundReceive, aAckCallback, aQueueForExecution);
      }
      else
      {
         return super.createProcessWithMessage(aInboundReceive, aAckCallback, aQueueForExecution );
      }
   }
   
   /**
    * Overrides method to create process inside a single transaction.
    *
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#internalCreateProcessWithMessage(org.activebpel.rt.bpel.impl.queue.AeInboundReceive, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   protected IAeBusinessProcess internalCreateProcessWithMessage(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback, long aRestartProcessId) throws AeBusinessProcessException
   {
      IAeBusinessProcess process = null;
      // TODO (KR) Handle case for container-managed transactions.
      AeTransactionManager.getInstance().begin();
      try
      {
         process = super.internalCreateProcessWithMessage(aInboundReceive, aAckCallback, aRestartProcessId);
         AeTransactionManager.getInstance().commit();
      }
      catch(AeBusinessProcessException abe)
      {
         try
         {
            AeTransactionManager.getInstance().rollback();
         }
         catch(AeTransactionException ate)
         {
            // log any rollback errors.
            AeException.logError(ate);
         }
         // bubble up the root cause.
         throw abe;
      }
      catch(Throwable t)
      {
         try
         {
            AeTransactionManager.getInstance().rollback();
         }
         catch(AeTransactionException ate)
         {
            // log any rollback errors.
            AeException.logError(ate);
         }
         throw new AeBusinessProcessException(t.getMessage(), t);
      }      
      return process;
   }
   

   /**
    * Handles the special case of creating a multi-start process. Such a process
    * can be created with the arrival of data for more than one activity. The spec
    * requires these activities to be correlated with the same correlation sets.
    * By the time we've reached this method, we've already determined that there
    * is no existing process that is waiting for the receipt of this data and as
    * such we've decided to create a new one. However, due to the concurrent nature
    * of the engine, we need to synchronize here in order to prevent two processes
    * from being created with the same correlated data.
    *
    * @param aInboundReceive
    * @param aAckCallback
    */
   protected long doMultiStartCreateProcessWithMessage(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback, boolean aQueueForExecution) throws AeBusinessProcessException
   {
      long processId = IAeBusinessProcess.NULL_PROCESS_ID;
      AeMessageReceiver found = null;
      // Acquire a lock for this inbound receive so we don't create multiple processes when two
      // correlated receives come in for a multi-start process.
      IAeLockManager.IAeLock lock = getLockManager().acquireLock(aInboundReceive.getProcessName(),
            calculateCorrelationHash(aInboundReceive.getCorrelation()), 60);
      try
      {
         found = getQueueManager().matchInboundReceive(aInboundReceive, false, aAckCallback);
         if (found == null)
         {
            // if nothing was in the queue, then we're sure we need to create the process.
            // the standard create routine is executed here but since we're still w/in the
            // sync block we'll hold up any other threads trying to create the same type
            // of process. This will give our newly created process a chance to execute
            // which in turn will have the multistart activities within it execute and
            // queue themselves with their correlated data. Once we're out of this sync
            // block, any threads we were holding up will have a chance to check the queue
            // to see if there is a match now as a result of this create, or they'll go on
            // to create their own process instances.
            processId = super.createProcessWithMessage(aInboundReceive, aAckCallback, aQueueForExecution);
         }
      }
      finally
      {
         getLockManager().releaseLock(lock);
      }

      // I found something above in the queue but I didn't want to do the handle within the sync block since
      // there's no point in holding up the creation of other processes of the same type.  Note that at this
      // point the receive has been consumed and is thus no longer in the queue.
      if (found != null)
      {
         processId = found.getProcessId();

         int locationId = found.getMessageReceiverPathId();
         long journalId = found.getJournalId();
         boolean synchronous = !aInboundReceive.isOneway();

         dispatchReceiveData(processId, locationId, aInboundReceive, journalId, synchronous);
      }
      return processId;
   }

   /**
    * Given a correlation map, this method calculates a hash value for the data in the map.
    *
    * @param aCorrelationMap The correlation map.
    * @return A correlation map hash.
    */
   protected int calculateCorrelationHash(Map aCorrelationMap)
   {
      int hash = 0;
      int count = 1;
      // Sort the keys - this ensures that the correlation set hash is always calculated the
      // same, even if the data is in the map in a different order.
      SortedSet ss = new TreeSet(new Comparator()
      {
         public int compare(Object o1, Object o2)
         {
            String str1 = o1.toString();
            String str2 = o2.toString();
            return str1.compareTo(str2);
         }

         public boolean equals(Object obj)
         {
            return false;
         }
      });
      ss.addAll(aCorrelationMap.keySet());
      for (Iterator iter = ss.iterator(); iter.hasNext(); count++)
      {
         QName key = (QName) iter.next();
         Object val = aCorrelationMap.get(key);
         hash += key.hashCode() + val.hashCode() * count++;
      }
      return hash;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#executeProcess(long, boolean)
    */
   protected void executeProcess(long aPid, boolean aReplyWaiting)
      throws AeBusinessProcessException
   {
      if (aReplyWaiting)
      {
         super.executeProcess(aPid, aReplyWaiting);
      }
      else
      {
         IAeInputMessageWork work = new AeProcessExecutionRunnableHelper(aPid, this);
         AeEngineFactory.scheduleInputMessageWork(aPid, work);
      }
   }

   /**
    * Overrides method to dispatch to the message receiver either synchronously
    * (for two-way messages) or asynchronously (for one-way messages).
    *  
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#dispatchReceiveData(long, int, org.activebpel.rt.bpel.impl.queue.AeInboundReceive, long, boolean)
    */
   protected void dispatchReceiveData(long aProcessId, int aLocationId, AeInboundReceive aInboundReceive,
      long aJournalId, boolean aRunSynchronously) throws AeBusinessProcessException
   {
      //dispatch to the process either synchronously or asynchronously.
      if (aRunSynchronously)
      {
         internalDispatchReceiveData(aProcessId, aLocationId, aInboundReceive, aJournalId);
      }
      else
      {
         // invoke a worker thread, which in turn will call dispatchReceiveData(..) (i.e. this same method) from the worker thread with **  aRunSynchronously = true **        
         IAeInputMessageWork work = new AeDispatchReceiveRunnableHelper(aProcessId, aLocationId, aInboundReceive, aJournalId, this);
         AeEngineFactory.scheduleInputMessageWork(aProcessId, work);
      }
   }
   
   /** 
    * Overrides method to journal and perform callback in a transaction. 
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#journalQueueInvokeData(long, int, long, org.activebpel.rt.message.IAeMessageData, java.util.Map, org.activebpel.wsio.IAeMessageAcknowledgeCallback)
    */
   protected long journalQueueInvokeData(long aProcessId, int aLocationId, long aTransmissionId, IAeMessageData aMessageData, 
         Map aProperties, IAeMessageAcknowledgeCallback aAckCallback) throws AeBusinessProcessException
   {
      long journalId = IAeProcessManager.NULL_JOURNAL_ID;
      // TODO (KR) Handle case for container-managed transactions.
      AeTransactionManager.getInstance().begin();
      try
      {
         journalId = super.journalQueueInvokeData(aProcessId, aLocationId, aTransmissionId, aMessageData, aProperties, aAckCallback);
         AeTransactionManager.getInstance().commit();
      }
      catch(AeBusinessProcessException abe)
      {
         try
         {
            AeTransactionManager.getInstance().rollback();
         }
         catch(AeTransactionException ate)
         {
            // log any rollback errors.
            AeException.logError(ate);
         }         
         throw abe;
      }
      catch(Throwable t)
      {
         try
         {
            AeTransactionManager.getInstance().rollback();
         }
         catch(AeTransactionException ate)
         {
            // log any rollback errors.
            AeException.logError(ate);
         }
         throw new AeBusinessProcessException(t.getMessage(), t);
      }       
      return journalId;
   }   
   
   /**
    * Overrides method to journal and perform callback in a transaction.  
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#journalQueueInvokeFault(long, int, long, org.activebpel.rt.bpel.IAeFault, java.util.Map, org.activebpel.wsio.IAeMessageAcknowledgeCallback)
    */
   protected long journalQueueInvokeFault(long aProcessId, int aLocationId, long aTransmissionId, IAeFault aFault, 
         Map aProperties, IAeMessageAcknowledgeCallback aAckCallback) throws AeBusinessProcessException
   {
      long journalId = IAeProcessManager.NULL_JOURNAL_ID;
      // TODO (KR) Handle case for container-managed transactions.
      AeTransactionManager.getInstance().begin();
      try
      {
         journalId = super.journalQueueInvokeFault(aProcessId, aLocationId, aTransmissionId, aFault, aProperties, aAckCallback);
         AeTransactionManager.getInstance().commit();
      }
      catch(AeBusinessProcessException abe)
      {
         AeTransactionManager.getInstance().rollback();
         throw abe;
      }
      catch(Throwable t)
      {
         AeTransactionManager.getInstance().rollback();
         throw new AeBusinessProcessException(t.getMessage(), t);
      } 
      return journalId;
   }   
   
   /**
    * Returns a list of processes based upon the filter specification.
    * @param aFilter the optional filter used to limit the result set.
    */
   protected AeProcessListResult getProcesses(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getProcessManager().getProcesses(aFilter);
   }

   /**
    * Returns a count of the processes that match the filter.
    * 
    * @param aFilter the optional filter used to limit the result set.
    */
   protected int getProcessCount(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getProcessManager().getProcessCount(aFilter);
   }

   /**
    * This method should never return a null since it is used for routing an incoming
    * request. We should throw an exception with some details if we cannot find
    * a plan for the given context.
    *
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#getProcessPlan(org.activebpel.wsio.receive.AeMessageContext)
    */
   public IAeProcessPlan getProcessPlan(AeMessageContext aContext)
         throws AeBusinessProcessException
   {
      validateMessageContext(aContext);

      IAeProcessDeployment deploymentPlan;

      try
      {
         if( aContext.getProcessName() != null )
         {
            deploymentPlan = getDeploymentByProcessName(aContext);
         }
         else
         {
            AeRoutingInfo routingInfo = getRoutingInfo(aContext);
            aContext.setPartnerLink(routingInfo.getServiceData().getPartnerLinkName());
            deploymentPlan = routingInfo.getDeployment();
         }
      }
      catch (AeException e)
      {
         throw new AeBusinessProcessException(e.getMessage(), e);
      }

      if (deploymentPlan == null)
      {
         Object[] args = { aContext.getProcessName().getNamespaceURI(),  aContext.getProcessName().getLocalPart() };
         throw new AeBusinessProcessException(AeMessages.format(
               "AeBpelEngine.NO_PLAN_FOR_PROCESS", args)); //$NON-NLS-1$
      }
      return deploymentPlan;
   }

   /**
    * Delegates the call to the deployment provider. Extracted the method to allow
    * easy override by subclass
    * @param aContext
    * @throws AeException
    */
   protected IAeProcessDeployment getDeploymentByProcessName(AeMessageContext aContext) throws AeException
   {
      return AeEngineFactory.getDeploymentProvider().findCurrentDeployment(aContext.getProcessName());
   }

   /**
    * Delegates the call to the deployment provider. Extracted the method to allow
    * easy override by subclass
    * @param aContext
    * @throws AeException
    */
   protected AeRoutingInfo getRoutingInfo(AeMessageContext aContext) throws AeException
   {
      return AeEngineFactory.getDeploymentProvider().getRoutingInfoByServiceName( aContext.getServiceName());
   }

   /**
    * Validate that only the service name OR the process qname have been set
    * on the message context.
    * @param aContext
    */
   protected void validateMessageContext( IAeMessageContext aContext ) throws AeBusinessProcessException
   {
      if( aContext.getServiceName() != null && aContext.getProcessName() != null )
      {
         throw new AeBusinessProcessException(
               AeMessages.format( "AeBpelEngine.InvalidContext",  //$NON-NLS-1$
                        new Object[] {aContext.getServiceName(), aContext.getProcessName()} ) );
      }
   }

   /**
    * Overrides to check for engine state
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#queueReceiveDataInternal(org.activebpel.rt.bpel.impl.queue.AeInboundReceive, org.activebpel.wsio.IAeMessageAcknowledgeCallback, boolean)
    */
   protected long queueReceiveDataInternal(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback, boolean aQueueForExecution)
      throws AeCorrelationViolationException, AeConflictingRequestException, AeBusinessProcessException
   {
      if (getState() != IAeEngineAdministration.RUNNING)
         throw new AeBusinessProcessException(AeMessages.getString("AeBpelEngine.ERROR_6")); //$NON-NLS-1$
      return super.queueReceiveDataInternal(aInboundReceive, aAckCallback, aQueueForExecution); 
   }

   /**
    * Getter for the managers list
    */
   protected List getManagers()
   {
      return mManagers;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#addCustomManager(java.lang.String, org.activebpel.rt.bpel.impl.IAeManager)
    */
   public void addCustomManager(String aManagerName, IAeManager aManager) throws AeException
   {
      super.addCustomManager(aManagerName, aManager);
      getManagers().add(aManager);
   }

   /**
    * Visit the managers with the specified visitor.
    * @param aVisitor
    */
   protected void visitManagers(IAeManagerVisitor aVisitor) throws Exception
   {
      for (Iterator it=getManagers().iterator(); it.hasNext();)
      {
         IAeManager mgr = (IAeManager) it.next();
         mgr.accept(aVisitor);
      }
   }

   /**
    * Visit the managers with the specified visitor. A convenience method for when
    * the visitor is one that won't throw an exception.
    * @param aVisitor
    */
   protected void visitManagersNoException(IAeManagerVisitor aVisitor)
   {
      try
      {
         visitManagers(aVisitor);
      }
      catch (Exception e)
      {
         throw new RuntimeException(AeMessages.getString("AeBpelEngine.ERROR_7"), e); //$NON-NLS-1$
      }
   }

   /**
    * Returns an error message if the state is ERROR, null otherwise.
    */
   public String getErrorInfo()
   {
      if(getState() == IAeEngineAdministration.ERROR)
      {
         return mErrorInfo;
      }
      else
      {
         return null;
      }
   }

   /**
    * Setter for the error message.
    * @param aError
    */
   protected void setErrorInfo( String aError )
   {
      mErrorInfo = aError;
   }

   /**
    * Removes processes based upon filter specification and returns the number
    * of processes removed.
    *
    * @param aFilter the filter specification
    */
   public int removeProcesses(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getProcessManager().removeProcesses(aFilter);
   }

   /**
    * Implements the partner link strategy for the server business process
    * engine.
    */
   protected static class AeServerPartnerLinkStrategy implements IAeEnginePartnerLinkStrategy
   {
      /**
       * @see org.activebpel.rt.bpel.server.addressing.IAePartnerAddressing#getMyRoleEndpoint(org.activebpel.rt.bpel.server.IAeProcessDeployment, org.activebpel.rt.bpel.def.AePartnerLinkDef, javax.xml.namespace.QName, java.lang.String)
       */
      public void initPartnerLink(AePartnerLink aPartnerLink, IAeProcessPlan aPlan) throws AeBusinessProcessException
      {
         IAeProcessDeployment deployment = AeProcessDeploymentFactory.getDeploymentForPlan(aPlan);
         IAeEndpointReference partnerRef = deployment.getPartnerEndpointRef(aPartnerLink.getDefinition().getLocationPath());
         if (partnerRef != null)
         {
            aPartnerLink.getPartnerReference().setReferenceData(partnerRef);
         }
         // get the myRole endpoint
         IAePartnerAddressing addr = AeEngineFactory.getPartnerAddressing();
         IAeEndpointReference myRef = addr.getMyRoleEndpoint(deployment, aPartnerLink.getDefinition(), aPlan.getProcessDef().getQName(), aPartnerLink.getConversationId());
         if (myRef != null)
         {
            aPartnerLink.getMyReference().setReferenceData(myRef);
         }
      }

      /**
       * @see org.activebpel.rt.bpel.impl.IAeEnginePartnerLinkStrategy#updatePartnerLink(org.activebpel.rt.bpel.impl.AePartnerLink, org.activebpel.rt.bpel.impl.IAeProcessPlan, org.activebpel.wsio.receive.IAeMessageContext)
       */
      public void updatePartnerLink(AePartnerLink aPartnerLink, IAeProcessPlan aProcessPlan, IAeMessageContext aMessageContext) throws AeBusinessProcessException
      {
         IAeProcessDeployment dd = AeProcessDeploymentFactory.getDeploymentForPlan(aProcessPlan);
         dd.updatePartnerLink(aPartnerLink, aMessageContext);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal#isRunning()
    */
   public boolean isRunning()
   {
      return getState() == IAeEngineAdministration.RUNNING;
   }

   /**
    * Returns the persistent process manager for this engine.
    *
    * @throws AeBusinessProcessException
    */
   protected IAePersistentProcessManager getPersistentProcessManager() throws AeBusinessProcessException
   {
      IAeProcessManager pm = getProcessManager();
      if (!(pm instanceof IAePersistentProcessManager))
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeBpelEngine.ERROR_NonPersistentProcessManager")); //$NON-NLS-1$
      }
      
      return (IAePersistentProcessManager) pm;
   }

   /**
    * Returns the journal entry with the given journal id.
    *
    * @param aJournalId
    * @throws AeBusinessProcessException
    */
   protected IAeJournalEntry getJournalEntry(long aJournalId) throws AeBusinessProcessException
   {
      IAeJournalEntry entry = getPersistentProcessManager().getJournalEntry(aJournalId);
      if (entry == null)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeBpelEngine.ERROR_MissingJournalEntry", aJournalId)); //$NON-NLS-1$
      }

      return entry;
   }

   /**
    * Deserializes an inbound receive for the given process from the inbound
    * receive's journal entry.
    *
    * @param aProcessId
    * @param aJournalEntry
    * @throws AeBusinessProcessException
    */
   protected AeInboundReceive deserializeInboundReceive(long aProcessId, IAeJournalEntry aJournalEntry) throws AeBusinessProcessException
   {
      if (!(aJournalEntry instanceof AeInboundReceiveJournalEntry))
      {
         Object[] params = { String.valueOf(aJournalEntry.getJournalId()), String.valueOf(aProcessId) };
         throw new AeBusinessProcessException(AeMessages.format("AeBpelEngine.ERROR_NotJournaledInboundReceive", params)); //$NON-NLS-1$
      }

      AeInboundReceiveJournalEntry entry = (AeInboundReceiveJournalEntry) aJournalEntry;
      IAeBusinessProcess process = getProcessById(aProcessId);

      try
      {
         // Set the process to use for deserializing the inbound receive.
         entry.setProcess(process);

         // Deserialize the inbound receive.
         return entry.getInboundReceive();
      }
      finally
      {
         // Always release the process.
         releaseProcess(process);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#isRestartable(long)
    */
   public boolean isRestartable(long aProcessId) throws AeBusinessProcessException
   {
      if (!getEngineConfiguration().isProcessRestartEnabled())
      {
         return false;
      }
      IAeBusinessProcess process = getProcessById(aProcessId);

      try
      {
         // Conditions for a process to be restartable:
         //  (1) the process is suspended;
         //  (2) the process is not coordinating;
         //  (3) the process is not multi-start;
         //  (4) the process manager persists journal entries; and
         //  (5) the process itself is persistent.
         // TODO (KR) Remove process coordinating restriction?
         // TODO (KR) Allow restart of faulted or completed processes?
         return process.isSuspended()
            && !process.isCoordinating()
            && !process.getProcessPlan().getProcessDef().isMultiStart()
            && (getProcessManager() instanceof IAePersistentProcessManager)
            && ((IAePersistentProcessManager) getProcessManager()).isPersistent(aProcessId)
            ;
      }
      finally
      {
         // Always release the process.
         releaseProcess(process);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcessEngine#restartProcess(long)
    */
   public void restartProcess(long aProcessId) throws AeBusinessProcessException
   {
      IAeBusinessProcess process = getProcessById(aProcessId);

      try
      {
         if (isRestartable(aProcessId))
         {
            // Get the restart process journal entry.
            AeInboundReceiveJournalEntry entry = getPersistentProcessManager().getRestartProcessJournalEntry(aProcessId);

            if (entry == null)
            {
               throw new AeBusinessProcessException(AeMessages.format("AeBpelEngine.ERROR_MissingRestartProcessInboundReceive", String.valueOf(aProcessId))); //$NON-NLS-1$
            }

            // Deserialize the restart process inbound receive.
            AeInboundReceive inboundReceive = deserializeInboundReceive(aProcessId, entry);

            // Terminate process to dequeue outstanding alarms, message
            // receivers, and replies.
            terminateProcess(aProcessId);

            // Now that nothing is queued, clear all existing journal entries.
            getPersistentProcessManager().journalAllEntriesDone(aProcessId);

            // Restart the process.
            createProcessWithMessage(inboundReceive, null, true, aProcessId);
         }
         else
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeBpelEngine.FailedToRestartErrorMessage")); //$NON-NLS-1$
         }
      }
      finally
      {
         // Always release the process.
         releaseProcess(process);
      }
   }
}
