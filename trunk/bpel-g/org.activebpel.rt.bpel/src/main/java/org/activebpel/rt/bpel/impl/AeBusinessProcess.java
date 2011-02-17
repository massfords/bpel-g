// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeBusinessProcess.java,v 1.185.2.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.IAeEngineAlert;
import org.activebpel.rt.bpel.IAeEngineEvent;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeInvokeActivity;
import org.activebpel.rt.bpel.IAeLocatableObject;
import org.activebpel.rt.bpel.IAeMonitorListener;
import org.activebpel.rt.bpel.IAePartnerLink;
import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinator;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.util.AeLocationPathUtils;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher;
import org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity;
import org.activebpel.rt.bpel.impl.activity.support.AeCompInfo;
import org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeCoordinatorCompInfo;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.AeFault;
import org.activebpel.rt.bpel.impl.activity.support.AeOpenMessageActivityInfo;
import org.activebpel.rt.bpel.impl.activity.support.AeProcessCompensationCallbackWrapper;
import org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback;
import org.activebpel.rt.bpel.impl.activity.support.IAeIMACorrelations;
import org.activebpel.rt.bpel.impl.fastdom.AeDocumentBuilder;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.lock.AeNoopVariableLocker;
import org.activebpel.rt.bpel.impl.lock.AeVariableLocker;
import org.activebpel.rt.bpel.impl.lock.IAeVariableLockCallback;
import org.activebpel.rt.bpel.impl.lock.IAeVariableLocker;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeInvoke;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.impl.queue.AeReply;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.impl.storage.AeFaultSerializer;
import org.activebpel.rt.bpel.impl.storage.AeProcessSnapshot;
import org.activebpel.rt.bpel.impl.storage.AeRestoreImplState;
import org.activebpel.rt.bpel.impl.storage.IAeProcessSnapshot;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.bpel.impl.visitors.IAeVisitable;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.AeWebServiceAttachment;
import org.activebpel.wsio.invoke.IAeTransmission;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

/**
 * Describes the interface used for interacting with business processes
 */
public class AeBusinessProcess extends AeActivityScopeImpl implements IAeBusinessProcessInternal, IAeVariableLockCallback, IAeVisitable
{
   /**
    * The message that started the process. Gets set during startup and then
    * consumed by the start activity.
    */
   private AeInboundReceive mCreateMessage;
   /** The engine we are running within. */
   private IAeBusinessProcessEngineInternal mEngine;
   /** Our instances process id within the engine. */
   private long mProcessId;
   /** Our process state (running, suspended, etc.) */
   private int mProcessState;
   /** Map of source location for the Bpel implementation objects. */
   private Map mBpelObjects = new HashMap();
   /** Map of source locations to process variables. */
   private Map mProcessVariables = new HashMap();
   /** Map of source locations to partner links. */
   private Map mPartnerLinkMap = new HashMap();
   /**
    * Queue used to execute objects. Prevents multiple executes from happening
    * concurrently
    */
   private final AeExecutionQueue mExecutionQueue = new AeExecutionQueue(this);
   /**
    * Used to control concurrent access to variables when there are isolated
    * scopes present
    */
   private IAeVariableLocker mVariableLocker;
   /** The date/time that the process was started */
   private Date mDateStarted = new Date();
   /** The date/time that the process was ended */
   private Date mDateEnded;
   /** process plan */
   protected IAeProcessPlan mProcessPlan;
   /** business process instance properties */
   private Map mBusinessProcessProperties;
   /** utility class for handling resume/suspend logic */
   private AeProcessSuspendResumeHandler mProcessAdministrator;
   /** location paths for any activities that are in the faulting state */
   private List mFaultingActivityLocationPaths;
   /** maps location id to location path */
   private Map<Integer,String> mLocationIdToPath = new HashMap<Integer,String>();
   /** maps location path to location id */
   private Map mLocationPathToId = new HashMap();
   /**
    * max location id used for creating new bpel objects dynamically as in
    * parallel forEach
    */
   private int mMaxLocationId = -1;
   /** counter used to store the next version number to use for a variable */
   private int mNextVariableVersionNumber = 1;
   /** Reason code for process state, default to none */
   private int mProcessStateReason = IAeBusinessProcess.PROCESS_REASON_NONE;
   /** List of open activity info objects. */
   private List mOpenMessageActivityInfoList;
   /** Indicates if the process is a parent in an coordinated  activity */
   private boolean mCoordinator;
   /** Indicates if the process is a child  in an coordinated  activity */
   private boolean mParticipant;
   /** Process initiator name. */
   private String mProcessInitiator = DEFAULT_INITIATOR;
   /**
    * set to true when the process is asked to terminate from the admin console
    * or when the process encounters an &lt;exit&gt; activity
    */
   private boolean mExiting;
   /** Next non-durable/non-persistent transmission id for invoke objects. */
   private long mInvokeId;
   /** Next alarm execution id. */
   private int mAlarmId;
   /** Map of queuedReceiveKeys to message receivers (receive, onMessage, onEvent) */
   private Map mQueuedReceives = new HashMap();

   /** maps state change values to their engine event values.  */
   private static final int[] STATE_TO_EVENT_MAPPING =
   {
      /*  Inactive  */        IAeProcessEvent.INACTIVE,
      /*  Ready     */        IAeProcessEvent.READY_TO_EXECUTE,
      /*  Executing */        IAeProcessEvent.EXECUTING,
      /*  Finished  */        IAeProcessEvent.EXECUTE_COMPLETE,
      /*  Faulted   */        IAeProcessEvent.EXECUTE_FAULT,
      /*  Dead Path */        IAeProcessEvent.DEAD_PATH_STATUS,
      /*  Queued    */        IAeProcessEvent.INACTIVE, // we're skipping over this
      /*  Terminated */       IAeProcessEvent.TERMINATED,
      /*  Faulting   */       IAeProcessEvent.FAULTING
   };

   /**
    * Construct a new business process from a definition object and associated
    * process id.
    * @param aPid The process id to associate with this process
    * @param aEngine The engine this process will be owned by
    * @param aPlan The process plan.
    */
   protected AeBusinessProcess(long aPid, IAeBusinessProcessEngineInternal aEngine, IAeProcessPlan aPlan)
   {
      super(aPlan.getProcessDef());
      if(aPlan.getProcessDef().containsSerializableScopes())
         mVariableLocker = new AeVariableLocker();
      else
         mVariableLocker = new AeNoopVariableLocker();
      setProcessId(aPid);
      setEngine(aEngine);
      setProcessState(IAeBusinessProcess.PROCESS_LOADED);
      mProcessPlan = aPlan;
      mFaultingActivityLocationPaths = new ArrayList();
      mProcessAdministrator = new AeProcessSuspendResumeHandler( this );
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getProcessInitiator()
    */
   public String getProcessInitiator()
   {
      return mProcessInitiator;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#setProcessInitiator(java.lang.String)
    */
   public void setProcessInitiator(String aInitiator)
   {
      if (AeUtil.notNullOrEmpty(aInitiator))
      {
         mProcessInitiator = aInitiator;
      }
      else
      {
         mProcessInitiator = DEFAULT_INITIATOR;
      }      
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#getNextVersionNumber()
    */
   public int getNextVersionNumber()
   {
      return mNextVariableVersionNumber;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#setNextVersionNumber(int)
    */
   public void setNextVersionNumber(int aId)
   {
      mNextVariableVersionNumber = aId;
   }

   /**
    * Returns the invoke id.
    */
   public long getInvokeId()
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      return mInvokeId;
   }

   /**
    * Sets the invoke id.
    *
    * @param aInvokeId
    */
   public void setInvokeId(long aInvokeId)
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      mInvokeId = aInvokeId;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#getNextInvokeId()
    */
   public long getNextInvokeId()
   {
      long id = getInvokeId() - 1;
      setInvokeId (id);
      return id;
   }

   /**
    * @return the execution reference id for the next alarm.
    */
   public int getAlarmId()
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      return mAlarmId;
   }

   /**
    * Sets the alarm exectution reference id.
    *
    * @param aAlarmId
    */
   public void setAlarmId(int aAlarmId)
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      mAlarmId = aAlarmId;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#getNextAlarmId()
    */
   public int getNextAlarmId()
   {
      int id = getAlarmId() + 1;
      setAlarmId (id);
      return id;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getProcessState()
    */
   public int getProcessState()
   {
      return mProcessState;
   }

   /**
    * Sets the process state reason.
    */
   public void setProcessStateReason(int aProcessStateReason)
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      mProcessStateReason = aProcessStateReason;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getProcessStateReason()
    */
   public int getProcessStateReason()
   {
      return mProcessStateReason;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#setProcessState(int)
    */
   public void setProcessState(int aState)
   {
      mProcessState = aState;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#isSuspended()
    */
   public boolean isSuspended()
   {
      return getProcessState() == PROCESS_SUSPENDED;
   }

   /**
    * Helper method to check if process is running.
    */
   protected boolean isRunning()
   {
      return getProcessState() == PROCESS_RUNNING;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#isCoordinating()
    */
   public boolean isCoordinating()
   {
      // returns true only if the process is either a coordinator or a participant
      // in one or more coordinated activities.
      return isCoordinator() || isParticipant();
   }

   /**
    * Returns <code>true</code> if this process is coordinating (parent) one or
    * more child (sub) processes.
    */
   public boolean isCoordinator()
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      return mCoordinator;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#setCoordinator(boolean)
    */
   public void setCoordinator(boolean aCoordinator)
   {
      // Normally set via the AeCoordinationContainer::register(..) method
      // (when a child joins a coordinated activity) and via process state
      // restoration.
      mCoordinator = aCoordinator;
   }

   /**
    * Returns <code>true</code> if this process is a participant (child/sub)
    * processes.
    */
   public boolean isParticipant()
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      // support legacy case:
      if (!mParticipant)
      {
         mParticipant = AeUtil.notNullOrEmpty( getBusinessProcessProperty(IAeCoordinating.WSCOORD_ID) );
      }
      return mParticipant;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#setParticipant(boolean)
    */
   public void setParticipant(boolean aParticipant)
   {
      //
      // This method is normally called during createProcessWithMessage
      // (for child/subprocess) (i.e. in the initializeCoordination method).
      // and process state restoration.
      //
      mParticipant = aParticipant;
   }


   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#getProcessDefinition()
    */
   public AeProcessDef getProcessDefinition()
   {
      return (AeProcessDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getProcessId()
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * Setter for the process id
    * @param aProcessId
    */
   protected void setProcessId(long aProcessId)
   {
      mProcessId = aProcessId;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      setProcessState(PROCESS_RUNNING);
      getEngine().fireEngineEvent(new AeEngineEvent(getProcessId(), IAeEngineEvent.PROCESS_STARTED, getName()));
      // events will get started after the start message has been consumed.
      getProcess().queueObjectToExecute(getActivity());
   }


   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getEngine()
    */
   public IAeBusinessProcessEngineInternal getEngine()
   {
      return mEngine;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#setEngine(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine)
   {
      mEngine = aEngine;
   }

   /**
    * Overrides method to return <code>this</code>.
    *
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getProcess()
    */
   public IAeBusinessProcessInternal getProcess()
   {
      return this;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#queueObjectToExecute(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public synchronized void queueObjectToExecute(IAeBpelObject aObject) throws AeBusinessProcessException
   {
      aObject.setState(AeBpelState.QUEUED_BY_PARENT);
      if(aObject.isReadyToExecute())
      {
         readyToExecuteObject(aObject);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#queueObjectToExecute(java.lang.Runnable)
    */
   public synchronized void queueObjectToExecute(Runnable aRunnable) throws AeBusinessProcessException
   {
      if (aRunnable != null)
      {
         getExecutionQueue().addRunnable(aRunnable);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#queueProcessToExecute()
    */
   public synchronized void queueProcessToExecute() throws AeBusinessProcessException
   {
      queueObjectToExecute(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#linkStatusChanged(org.activebpel.rt.bpel.impl.IAeLink)
    */
   public void linkStatusChanged(IAeLink aLink)
   {
      // fire link status change event
      String linkPath = aLink.getLocationPath();
      fireEvent(linkPath, IAeProcessEvent.LINK_STATUS, Boolean.toString(aLink.getStatus()));

      // process target activity
      IAeActivity activity = aLink.getTargetActivity();
      if( activity.isReadyToExecute())
      {
         try
         {
            readyToExecuteObject(activity);
         }
         catch(AeBpelException e)
         {
            propagateFault(activity, e.getFault());
         }
         catch(AeBusinessProcessException bpe)
         {
            AeException.logError(bpe, bpe.getLocalizedMessage());
            String msg = MessageFormat.format(AeMessages.getString("AeBusinessProcess.ERROR_EXECUTING_ACTIVITY_ERROR"),  //$NON-NLS-1$
                  new Object [] { activity.getLocationPath(), bpe.getLocalizedMessage() });
            IAeFault fault = AeFaultFactory.getSystemErrorFault(bpe, msg);
            propagateFault(activity, fault);
         }
         catch(Throwable ex)
         {
            // TODO should we not catch everything, for example maybe throwable,
            // i.e. non-planned bpel exceptions  should suspend the process
            // rather than fault
         }
      }
   }

   /**
    * Propagates the fault to the object passed in. Broke this out from
    * linkStatusChanged to avoid having the ugliness of a nested try/catch
    * block. An object that says it's ready to execute but throws a fault
    * @param aBpelObject
    * @param aFault
    */
   private void propagateFault(IAeBpelObject aBpelObject, IAeFault aFault)
   {
      try
      {
         objectCompletedWithFault(aBpelObject, aFault);
      }
      catch (AeBusinessProcessException e)
      {
         e.logError();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#isSuppressJoinConditionFailure()
    */
   public boolean isSuppressJoinConditionFailure()
   {
      AeProcessDef def = (AeProcessDef) getDefinition();
      return def.getSuppressJoinFailure();
   }

   /**
    * If the passed object is not associated with a dead path it will execute
    * the bpel object passed after firing the ready to execute event.
    * If after the event the process is suspened the activity is queued
    * otherwise it is passed to executObject method.
    * @throws AeBusinessProcessException - either a fatal error or an
    *         unsuppressed join failure
    */
   protected void readyToExecuteObject(IAeBpelObject aObject) throws AeBusinessProcessException
   {
      if(aObject.isNotDeadPath())
      {
         aObject.setState(AeBpelState.READY_TO_EXECUTE);
         executeObject(aObject);
      }
      else
      {
         // we may be in a terminated state, don't want to move from dead path
         // to terminated
         aObject.setState(AeBpelState.DEAD_PATH);
      }
   }

   /**
    * Getter for the execution queue.
    */
   protected AeExecutionQueue getExecutionQueue()
   {
      return mExecutionQueue;
   }

   /**
    * We're only going to execute one object at a time. This will simplify our
    * execution by avoiding recursive calls to execute and hopefully avoid
    * any subtle bugs with the ordering of event firing and state changes.
    */
   protected void executeObject(IAeBpelObject aObject) throws AeBusinessProcessException
   {
      // add the object to our execution queue. If the queue is being executed,
      // the our addition will be queued, otherwise it'll execute immediately
      getExecutionQueue().add((IAeExecutableBpelObject) aObject);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#suspend(org.activebpel.rt.bpel.impl.AeSuspendReason)
    */
   public synchronized void suspend(AeSuspendReason aReason) throws AeBusinessProcessException
   {
      // Only suspend if the process is running
      if (!isRunning())
      {
         return;
      }

      // get the reason for suspend (default is manual)
      int code = AeSuspendReason.SUSPEND_CODE_MANUAL;
      if (aReason != null)
         code = aReason.getReasonCode();

      switch(code)
      {
         case AeSuspendReason.SUSPEND_CODE_MIGRATE:
            // if migrate requested then only change reason if we are not
            // already supended
            if(! isSuspended())
            {
               setProcessStateReason(AeSuspendReason.SUSPEND_CODE_MIGRATE);
               getProcessSuspendResumeHandler().suspend();
            }
         break;

         case AeSuspendReason.SUSPEND_CODE_MANUAL:
            setProcessStateReason(AeSuspendReason.SUSPEND_CODE_MANUAL);
            getProcessSuspendResumeHandler().suspend();
         break;

         case AeSuspendReason.SUSPEND_CODE_AUTOMATIC:
            setProcessStateReason(AeSuspendReason.SUSPEND_CODE_AUTOMATIC);
            IAeFault uncaughtFault = ((AeAbstractBpelObject)findBpelObjectOrThrow(aReason.getLocationPath())).getFault();
            getProcessSuspendResumeHandler().suspendBecauseOfUncaughtFault(aReason.getLocationPath(), uncaughtFault );
            alertFaulting(aReason.getLocationPath(), uncaughtFault);
         break;

         case AeSuspendReason.SUSPEND_CODE_LOGICAL:
            setProcessStateReason(AeSuspendReason.SUSPEND_CODE_LOGICAL);
            getProcessSuspendResumeHandler().suspend();
            AeEngineAlert alert = createAlert(IAeEngineAlert.PROCESS_ALERT_SUSPENDED, aReason.getLocationPath(), aReason.getVariable());
            getEngine().fireEngineAlert(alert);
         break;

         case AeSuspendReason.SUSPEND_CODE_INVOKE_RECOVERY:
            setProcessStateReason(AeSuspendReason.SUSPEND_CODE_INVOKE_RECOVERY);
            getProcessSuspendResumeHandler().suspendBecauseOfInvokeRecovery(aReason.getLocationPath());
            alertInvokeRecovery(aReason.getLocationPath());
            break;

         case AeSuspendReason.SUSPEND_CODE_INVOKE_RETRY:
            setProcessStateReason(AeSuspendReason.SUSPEND_CODE_INVOKE_RETRY);
            IAeFault retryFault = ((AeAbstractBpelObject)findBpelObjectOrThrow(aReason.getLocationPath())).getFault();
            getProcessSuspendResumeHandler().suspendBecauseOfInvokeRetry(aReason.getLocationPath(), retryFault);
            alertFaulting(aReason.getLocationPath(), retryFault);
            break;
            
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#resume(boolean)
    */
   public synchronized void resume(boolean aExecute) throws AeBusinessProcessException
   {
      // Only resume if the process is suspended
      if (isSuspended())
      {
      getProcessSuspendResumeHandler().resume( aExecute );
   }
   }

   /**
    * Check the suspended execution queue and resume the object represented
    * by the passed path.
    *
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#resume(java.lang.String)
    */
   public synchronized void resume(String aLocation) throws AeBusinessProcessException
   {
      // Only resume if the process is suspended
      if (isSuspended())
      {
      getProcessSuspendResumeHandler().resume( aLocation );
   }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#retryActivity(java.lang.String, boolean)
    */
   public synchronized void retryActivity( String aLocation, boolean aAtScope ) throws AeBusinessProcessException
   {
      // Only resume if the process is suspended
      if (isSuspended())
      {
         getProcessSuspendResumeHandler().retryActivity( aLocation, aAtScope );
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#completeActivity(java.lang.String)
    */
   public synchronized void completeActivity(String aLocation) throws AeBusinessProcessException
   {
      // Only resume if the process is suspended
      if (isSuspended())
      {
         getProcessSuspendResumeHandler().completeActivity( aLocation );
      }
   }

   /**
    * Note: Bpel activities shouldn't call this method directly. Doing so will
    * bypass the behavior in the base class that handles the link processing.
    *
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#objectCompleted(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void objectCompleted(IAeBpelObject aObject) throws AeBusinessProcessException
   {
      if (aObject == this)
      {
         processEnded(null);
      }
      // if dead path we don't want to declare as finished in a normal way
      else if (! aObject.getState().isFinal())
      {
         aObject.setState(AeBpelState.FINISHED);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#objectCompletedWithFault(org.activebpel.rt.bpel.impl.IAeBpelObject, org.activebpel.rt.bpel.IAeFault)
    */
   public void objectCompletedWithFault(IAeBpelObject aObject, IAeFault aFaultObject) throws AeBusinessProcessException
   {
      if(aObject != this && !aObject.getParent().getState().isFinal())
      {
         ((IAeExecutableBpelObject)aObject.getParent()).childIsFaulting(aObject, aFaultObject);
      }

      aObject.setFaultedState(aFaultObject);

      if ( aFaultObject != null && aFaultObject.getSource() == null )
      {
         aFaultObject.setSource( aObject );
      }

      if(aObject != this && !aObject.getParent().getState().isFinal())
      {
         ((IAeExecutableBpelObject)aObject.getParent()).childCompleteWithFault(aObject, aFaultObject);
      }

      if (aObject == this)
      {
         processEnded(aFaultObject);
      }
   }

   /**
    * Overrides method to so that the compInfo is not cleared at the process
    * level.
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl#clearCompInfo()
    */
   protected void clearCompInfo()
   {
     // The compInfo for compensation at the process (marked for
     // enableInstanceCompensation) level should not be cleared.
   }

   /**
    * Overrides method to return <code>AeCompInfo</code> and creates the
    * variable/correlationSet snapshot for the data at the process level
    * (needed during process instance compensation).
    *
    * @see org.activebpel.rt.bpel.impl.IAeCompensatableActivity#getCompInfo()
    */
   public AeCompInfo getCompInfo()
   {
      AeCompInfo compInfo = super.getCompInfo();
      // check if a snapshot is created -  if not create it.
      if (compInfo.getSnapshot() == null)
      {
         // Note: calling AeCompInfo::recordSnapshot() creates a new snapshot.
         compInfo.recordSnapshot(this);
      }
      return compInfo;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#compensate(org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback)
    */
   public synchronized void compensate(IAeCompensationCallback aCallback) throws AeBusinessProcessException
   {
      if (getProcessState() != IAeBusinessProcess.PROCESS_COMPENSATABLE)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeBusinessProcess.NotCompensatable", String.valueOf(getProcessId()))); //$NON-NLS-1$
      }

      setProcessState(IAeBusinessProcess.PROCESS_RUNNING);

      getExecutionQueue().resume(true);

      AeCompensationHandler compHandler = getCompensationHandler();
      // use a wrapper for the callback to ensure that our state changes to complete after compensation is done.
      compHandler.setCallback(new AeProcessCompensationCallbackWrapper(aCallback));
      compHandler.setCompInfo(getCompInfo());

      AeProcessInfoEvent event = new AeProcessInfoEvent(getProcessId(), getLocationPath(), IAeProcessInfoEvent.INFO_PROCESS_COMPENSATION_STARTED);
      getEngine().fireInfoEvent(event);
      queueObjectToExecute(compHandler);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#releaseCompensationResources()
    */
   public synchronized void releaseCompensationResources()
   {
      if (getProcessState() == IAeBusinessProcess.PROCESS_COMPENSATABLE)
      {
         // TODO (MF) optimization here to trim any remaining comp info objects from state.
         setProcessState(IAeBusinessProcess.PROCESS_COMPLETE);
      }
   }

   /**
    * Overrides method in scope to always return true. The snapshot at the
    * process level is really a live state of the variables so no performance
    * optimization necessary here.
    *
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl#isRecordSnapshotEnabled()
    */
   public boolean isRecordSnapshotEnabled()
   {
      return true;
   }

   /**
    * Overload method for engine events with no fault to report.
    */
   protected void fireEvent(String aPath, int aEventId, String aOtherInfo)
   {
      AeProcessEvent event = new AeProcessEvent(getProcessId(), aPath, aEventId, "", aOtherInfo, getName()); //$NON-NLS-1$
      getEngine().fireEvent(event);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#isReceiveDataQueued(org.activebpel.rt.bpel.def.AePartnerLinkOpKey)
    */
   public boolean isReceiveDataQueued(AePartnerLinkOpKey aPartnerLinkOpKey)
   {
      if (getCreateMessage() != null)
      {
         AePartnerLinkOpKey activityPLOKey = aPartnerLinkOpKey;
         AePartnerLinkOpKey startMsgPLOKey = getCreateMessage().getPartnerLinkOperationKey();

         return AeUtil.compareObjects(activityPLOKey, startMsgPLOKey);
      }
      return false;
   }

   /**
    * Report engine events - includes fault parameter.
    */
   protected void fireEvent(String aPath, int aEventId, String aFault, String aOtherInfo)
   {
      AeProcessEvent event = new AeProcessEvent(getProcessId(), aPath, aEventId, aFault, aOtherInfo, getName());
      getEngine().fireEvent(event);
   }

   /**
    * populates correlation values for engine managed correlation.
    *
    * @param aMessageReceiver The receive or onMessage activity
    * @param aCorrelation - optional map to store the correlation values
    */
   protected void getEngineManagedCorrelationData(IAeMessageReceiverActivity aMessageReceiver, Map aCorrelation)
   {
      AePartnerLink plink = findProcessPartnerLink(aMessageReceiver.getPartnerLinkOperationImplKey().getPartnerLinkLocationPath());
      IAeEndpointReference myRef = plink.getMyReference();
      if (myRef == null)
         return;

      String correlationId = (String) myRef.getProperties().get(IAePolicyConstants.CONVERSATION_ID_HEADER);
      if (correlationId != null)
      {
         aCorrelation.put(IAePolicyConstants.CONVERSATION_ID_HEADER, plink.getConversationId());
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#queueMessageReceiver(org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity, int)
    */
   public void queueMessageReceiver(IAeMessageReceiverActivity aMessageReceiver, int aGroupId)
         throws AeBusinessProcessException
   {
      IAeIMACorrelations receiveCorrelations = aMessageReceiver.getCorrelations();
      Map correlation = receiveCorrelations != null? receiveCorrelations.getInitiatedProperties() : new HashMap();

      int receiverPathId = getLocationId(aMessageReceiver.getLocationPath());
      AePartnerLinkOpImplKey plOpImplKey = aMessageReceiver.getPartnerLinkOperationImplKey();
      AePartnerLink plink = findProcessPartnerLink(plOpImplKey.getPartnerLinkLocationPath());
      getEngineManagedCorrelationData(aMessageReceiver, correlation);
      AeMessageReceiver messageQueueObject = new AeMessageReceiver(getProcessId(), getName(), plOpImplKey,
            plink.getDefinition().getMyRolePortType(), correlation, receiverPathId, aGroupId, aMessageReceiver.isConcurrent());

      // If the start message is still present, then the executing activity
      // MUST be a create instance activity (although it doesn't necessarily
      // have to match against the start message).
      if (getCreateMessage() != null)
      {
         // If it's not a create instance, then throw an exception because we're
         // dealing with bad bpel
         if (!aMessageReceiver.canCreateInstance())
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeBusinessProcess.ERROR_1")); //$NON-NLS-1$
         }
         // check to see if it matches the start message.
         // it should match because we don't allow other receives in a
         // multi-start bpel to queue unless they're the start activity
         else if (messageQueueObject.matches( getCreateMessage() ))
         {
            // clear the create message since we are consuming it now
            AeInboundReceive createMessage = getCreateMessage();
            setCreateMessage(null);

            // it's safe to start the events running now since they won't
            // execute until after the receive is done executing.
            executeEventHandler();

            IAeMessageData messageData = createMessage.getMessageData();

            // dispatch the message to the message receiver using its dispatcher
            IAeMessageDispatcher dispatcher = aMessageReceiver.createDispatcher(createMessage.getContext());
            // record an opemIMA if there is a reply receiver
            AeOpenMessageActivityInfo oma = createOpenMessageActivityInfo(dispatcher, createMessage.getReplyReceiver(), createMessage.getReplyId());
            if (oma != null)
               addOpenMessageActivityInfo(oma);
            dispatcher.onMessage(messageData);
         }
         // We should never get here since the multi-start code avoids executing
         // the activity unless it's the one that created the process. To reach
         // here indicates that the multi-start code failed somehow. This is bad
         // because the activity would enter the queue without its correlation
         // data set and would likely never get matched.
         else
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeBusinessProcess.ERROR_2")); //$NON-NLS-1$
         }
      }
      else
      {
         addReceiverKeyForConflictingReceives(aMessageReceiver);
         getEngine().addMessageReceiver(messageQueueObject);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#dequeueMessageReceiver(org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity)
    */
   public void dequeueMessageReceiver(IAeMessageReceiverActivity aMessageReceiver) throws AeBusinessProcessException
   {
      removeReceiverKeyForConflictingReceives(aMessageReceiver);

      int locationPathId = getLocationId(aMessageReceiver.getLocationPath());
      getEngine().removeMessageReceiver(getProcessId(), locationPathId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#queueAlarm(org.activebpel.rt.bpel.impl.IAeAlarmReceiver, java.util.Date)
    */
   public void queueAlarm(IAeAlarmReceiver aAlarm, Date aDeadline) throws AeBusinessProcessException
   {
      // assign a new alarm id iff needed.
      if (aAlarm.getAlarmId() == -1)
      {
         aAlarm.setAlarmId( getNextAlarmId() );
      }
      getEngine().scheduleAlarm(getProcessId(), aAlarm.getLocationId(), aAlarm.getGroupId(), aAlarm.getAlarmId(), aDeadline);
   }

   /**
    * Dequeues an alarm set in the engine.
    *
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#dequeueAlarm(org.activebpel.rt.bpel.impl.IAeAlarmReceiver)
    */
   public void dequeueAlarm(IAeAlarmReceiver aAlarm) throws AeBusinessProcessException
   {
      int alarmId = aAlarm.getAlarmId();
      aAlarm.setAlarmId(-1);
      getEngine().removeAlarm(getProcessId(), aAlarm.getLocationId(), alarmId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#queueReply(org.activebpel.rt.message.IAeMessageData, javax.xml.namespace.QName, org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey, java.lang.String)
    */
   public void queueReply(IAeMessageData aInputMessage, QName aFaultName,
         AePartnerLinkOpImplKey aPartnerLinkKey, String aMessageExchange) throws AeBusinessProcessException
   {
      // find matching open message activity info.
      AeOpenMessageActivityInfo info = findMatchingOpenMessageActivityInfo(aPartnerLinkKey, aMessageExchange);
      Map propertiesSnapshot = cloneBusinessProcessProperties();
      try
      {
         if (aFaultName == null)
         {
            sendReply(info, aInputMessage, (IAeFault) null, propertiesSnapshot);
         }
         else
         {
            sendReply(info, null, new AeFault(aFaultName, aInputMessage), propertiesSnapshot);
         }
      }
      finally
      {
         //Remove open MA from list since it is now closed.
         getOpenMessageActivityInfoList().remove(info);
      }
   }

   /**
    * Queues the reply data to the queue manager.
    * @param aOpenMessageActivity
    * @param aInputMessage
    * @param aFault
    * @param aBusinessProperties
    * @throws AeBusinessProcessException
    */
   protected void sendReply(AeOpenMessageActivityInfo aOpenMessageActivity,
         IAeMessageData aInputMessage, IAeFault aFault, Map aBusinessProperties) throws AeBusinessProcessException
   {
      if (aOpenMessageActivity == null)
      {
         // reply not found.
         throw new AeBpelException(AeMessages.getString("AeBusinessProcess.MissingRequest"), getFaultFactory().getMissingRequest()); //$NON-NLS-1$
      }

      IAeReplyReceiver replyReceiver = aOpenMessageActivity.getDurableReplyReceiver();
      AeReply replyObject = new AeReply(getProcessId(), aOpenMessageActivity.getReplyId(), replyReceiver);
      replyObject.setMessageData(aInputMessage);
      replyObject.setFault(aFault);
      replyObject.setReceiverPath(aOpenMessageActivity.getReceiverPath());
      getEngine().sendReply(replyObject, aInputMessage, aFault, aBusinessProperties);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#queueInvoke(org.activebpel.rt.bpel.IAeInvokeActivity, org.activebpel.rt.message.IAeMessageData, org.activebpel.rt.bpel.IAePartnerLink, org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey)
    */
   public void queueInvoke(IAeInvokeActivity aInvoke, IAeMessageData aInputMessage,
         IAePartnerLink aPartnerLink, AePartnerLinkOpImplKey aPartnerLinkKey)
         throws AeBusinessProcessException
   {
      AeInvoke messageQueueObject = createInvokeQueueObject(aInvoke, aInputMessage, aPartnerLink, aPartnerLinkKey);
      getEngine().addInvoke(getProcessPlan(), messageQueueObject);
   }

   /**
    * Creates an invoke queue object that will be added to the queue manager.
    *
    * @param aInvoke
    * @param aInputMessage
    * @param aPartnerLink
    * @param aPartnerLinkKey
    * @throws AeBusinessProcessException
    */
   protected AeInvoke createInvokeQueueObject(IAeInvokeActivity aInvoke,
         IAeMessageData aInputMessage, IAePartnerLink aPartnerLink, AePartnerLinkOpImplKey aPartnerLinkKey)
      throws AeBusinessProcessException
   {
      Map propertiesSnapshot = cloneBusinessProcessProperties();

      AeInvoke messageQueueObject =
            new AeInvoke(
               getProcessId(),
               getName(),
               aPartnerLink,
               aPartnerLinkKey,
               aInputMessage,
               aInvoke,
               propertiesSnapshot );

      messageQueueObject.setOneWay(aInvoke.isOneWay());
      messageQueueObject.setInvokeActivity(aInvoke);
      messageQueueObject.setProcessInitiator(getProcessInitiator());
      return messageQueueObject;
   }

   /**
    * Shallow clone of the properties map.
    */
   protected Map cloneBusinessProcessProperties()
   {
      return new HashMap(getBusinessProcessPropertiesMap());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#addBpelObject(java.lang.String, org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void addBpelObject(String aLocationPath, IAeBpelObject aObject)
   {
      if (aLocationPath != null && aObject != null)
      {
         mBpelObjects.put(aLocationPath, aObject);

         addMappings(aObject);
      }
   }

   /**
    * Records the mapping of the location path to the location id if this object
    * has a custom location path set on it. In this case, we can't rely on the
    * definition to provide the mapping.
    * @param aObject
    */
   protected void addMappings(IAeLocatableObject aObject)
   {
      if (aObject.hasCustomLocationPath())
      {
         if (aObject.getLocationId() != -1)
         {
            mLocationIdToPath.put(aObject.getLocationId(), aObject.getLocationPath());
            mLocationPathToId.put(aObject.getLocationPath(), new Integer(aObject.getLocationId()));
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#addVariableMapping(org.activebpel.rt.bpel.IAeVariable)
    */
   public void addVariableMapping(IAeVariable aVariable)
   {
      if (aVariable != null)
      {
         mProcessVariables.put(aVariable.getLocationPath(), aVariable);
         addMappings(aVariable);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#addPartnerLinkMapping(org.activebpel.rt.bpel.impl.AePartnerLink)
    */
   public void addPartnerLinkMapping(AePartnerLink aPartnerLink)
   {
      mPartnerLinkMap.put(aPartnerLink.getLocationPath(), aPartnerLink);
      addMappings(aPartnerLink);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#findBpelObject(java.lang.String)
    */
   public synchronized IAeBpelObject findBpelObject(String aLocationPath)
   {
      return (IAeBpelObject)mBpelObjects.get(aLocationPath);
   }

   /**
    * Finds the bpel object by path or throws an exception
    *
    * @param aLocationPath
    * @throws AeBusinessProcessException
    */
   public IAeBpelObject findBpelObjectOrThrow(String aLocationPath) throws AeBusinessProcessException
   {
      // Note: This non-interface method is public for AeBaseRestoreVisitor.
      IAeBpelObject obj = findBpelObject(aLocationPath);
      if (obj == null)
      {
         Object[] errMsgParams = { aLocationPath, getName().toString(), String.valueOf( getProcessId() ) };
         throw new AeBusinessProcessException(AeMessages.format("AeBusinessProcess.ERROR_LOCATING_BPEL_OBJECT", errMsgParams)); //$NON-NLS-1$
      }
      return obj;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#findBpelObject(int)
    */
   public synchronized IAeBpelObject findBpelObject(int aLocationId)
   {
      String locationPath = getLocationPath(aLocationId);
      return findBpelObject(locationPath);
   }

   /**
    * Returns the variable specified by the location path string.
    * @param aLocationPath the XPath location of the Bpel object
    * @return the variable impl or null if not found
    */
   public IAeVariable findProcessVariable(String aLocationPath)
   {
      return (IAeVariable)mProcessVariables.get(aLocationPath);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#findProcessPartnerLink(java.lang.String)
    */
   public synchronized AePartnerLink findProcessPartnerLink(String aLocationPath)
   {
      return (AePartnerLink) mPartnerLinkMap.get(aLocationPath);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getVariable(java.lang.String, java.lang.String)
    */
   public synchronized IAeVariable getVariable(String aActivityLoc, String aVarName)
   {
      IAeBpelObject activity = findBpelObject(aActivityLoc);
      if (activity != null)
      {
         IAeVariable var = activity.findVariable(aVarName);
         if (var != null)
            return (IAeVariable)var.clone();
      }

      return null;
   }

   /**
    * Fires an event that the object's status is changing. If the object is a
    * dead path then we'll notify the parent. This is really only for Sequence
    * which needs to move onto the next child in the sequence if one reaches a
    * dead path.
    *
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#objectStateChanged(org.activebpel.rt.bpel.impl.IAeBpelObject, org.activebpel.rt.bpel.impl.AeBpelState, org.activebpel.rt.bpel.impl.IAeStateChangeDetail)
    */
   public void objectStateChanged(IAeBpelObject aObject, AeBpelState aOldState, IAeStateChangeDetail aDetailsObject) throws AeBusinessProcessException
   {
      if (aObject.getState() != AeBpelState.QUEUED_BY_PARENT)
      {
         int code = STATE_TO_EVENT_MAPPING[aObject.getState().getCode()];
         fireEvent(aObject.getLocationPath(), code, aDetailsObject.getFaultName(), aDetailsObject.getAdditionalInfo());
      }

      boolean objectIsBeingRetried = isBeingRetried(aObject);

      // Special handling of an object that is being retried from the Exception Management API
      if (aObject.getState() == AeBpelState.TERMINATED && objectIsBeingRetried)
      {
         getProcessSuspendResumeHandler().terminationComplete((AeAbstractBpelObject) aObject);
      }
      else
      {
         // if the object is in a final state then we release its locks
         if (aObject.getState().isFinal())
         {
            releaseLocks(aObject.getLocationPath());
         }

         /*
          * todo Sequence and Flow both need to know about a child activity
          * going dead path. The other container activities don't care. In fact,
          * some of the other containers require a special check in order to
          * prevent them from misinterpreting this childComplete as a normal
          * child completion vs dead path. Perhaps we're back to considering
          * adding a childCompleteWithDeadPath() call ?
          */
         if (aObject.getState() == AeBpelState.DEAD_PATH && !aObject.getParent().getState().isFinal() && !aObject.getParent().isTerminating())
         {
            ((IAeExecutableBpelObject)aObject.getParent()).childComplete(aObject);
         }

         // if the termination is the result of a retry, we don't
         // need to notify the parent - just retry the terminated activity
         if( aObject.getState() == AeBpelState.TERMINATED )
         {
            if (aObject.getParent() != null && !aObject.getParent().getState().isFinal())
            {
               ((IAeExecutableBpelObject)aObject.getParent()).childTerminated(aObject);
            }
         }
      }
   }

   /**
    * Return true if we DO NOT need to notify the parent of a terminate state
    * change (because of activity retry).
    * @param aObject
    */
   protected boolean isBeingRetried( IAeBpelObject aObject )
   {
      // was checking for isSuspended but needed to allow the process to resume
      // in order to execute compensation handlers for scopes being retried
      return getFaultingActivityLocationPaths().contains( aObject.getLocationPath() );
   }

   /**
    * Overriding setState to catch our transition to terminated.
    *
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl#setState(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void setState(AeBpelState aNewState) throws AeBusinessProcessException
   {
      if (aNewState == AeBpelState.TERMINATED)
      {
         terminationComplete();
      }
      else
      {
         super.setState(aNewState);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#setCreateMessage(org.activebpel.rt.bpel.impl.queue.AeInboundReceive)
    */
   public synchronized void setCreateMessage(AeInboundReceive aReceiveQueueObject)
   {
      mCreateMessage = aReceiveQueueObject;
      addBusinessProcessProperties(aReceiveQueueObject);
      // set process intiator. (note: aReceiveQueueObject maybe null e.g. during state restore)
      if (aReceiveQueueObject != null)
      {
         setProcessInitiator( aReceiveQueueObject.getContext().getPrincipal() );
      }
      
   }

   /**
    * @see org.activebpel.rt.bpel.impl.lock.IAeVariableLockCallback#variableLocksAcquired(java.lang.String)
    */
   public void variableLocksAcquired(String aOwnerPath) throws AeBusinessProcessException
   {
      readyToExecuteObject(findBpelObjectOrThrow(aOwnerPath));
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#getVariableLocker()
    */
   public IAeVariableLocker getVariableLocker()
   {
      return mVariableLocker;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#addExclusiveLock(java.util.Set, java.lang.String)
    */
   public boolean addExclusiveLock(Set aSetOfVariablePaths, String aOwnerXPath)
   {
      return getVariableLocker().addExclusiveLock(aSetOfVariablePaths, aOwnerXPath, this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#addSharedLock(java.util.Set, java.lang.String)
    */
   public boolean addSharedLock(Set aSetOfVariablePaths, String aOwnerXPath)
   {
      return getVariableLocker().addSharedLock(aSetOfVariablePaths, aOwnerXPath, this);
   }

   /**
    * Releases any variable locks that were acquired with this location path.
    * @param aOwnerXPath
    */
   private void releaseLocks(String aOwnerXPath) throws AeBusinessProcessException
   {
      getVariableLocker().releaseLocks(aOwnerXPath);
   }

   /**
    * Overridden as a side effect of extending scope. The process never needs to
    * lock variables, that's left to the individual activities to worry about.
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityImpl#acquireResourceLocks()
    */
   protected boolean acquireResourceLocks()
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getStartDate()
    */
   public Date getStartDate()
   {
      return mDateStarted;
   }

   /**
    * Handles any processing that needs to happen as a result of the process
    * ending.
    */
   protected void processEnded(IAeFault aFault) throws AeBusinessProcessException
   {
      // TODO (MF) This code is run once when the process completes its normal execution. Should revist this to account for the process completing from its compensation handler.
      if (aFault == null)
      {
         setState(AeBpelState.FINISHED);
      }
      else
      {
         setFault(aFault);
      }

      setEndDate(new Date());

      if (getFault() != null)
      {
         setProcessState(IAeBusinessProcess.PROCESS_FAULTED);

         // Mark all open IMAs as orphaned.
         hasNewOrphanedIMAs(this);

         // Fault all open IMAs.
         faultOrphanedIMAs(this, getFault());
         
         // Fire an event to let listeners of monitor events know that a process has faulted
         getEngine().fireMonitorEvent(IAeMonitorListener.MONITOR_PROCESS_FAULT, IAeMonitorListener.EVENT_DATA_PROCESS_FAULTED);
      }
      else if (isNormalCompletion() && isParticipant() && getCompensationHandler().getState() == AeBpelState.INACTIVE)
      {
         // if it's a sub process and it is compensatable, then record that as
         // the process state. This process will remain compensatable until it
         // gets compensated or until the parent process completes normally.
         setProcessState(IAeBusinessProcess.PROCESS_COMPENSATABLE);
      }
      else
      {
         setProcessState(IAeBusinessProcess.PROCESS_COMPLETE);
      }
      getEngine().fireEngineEvent(new AeEngineEvent(getProcessId(), IAeEngineEvent.PROCESS_TERMINATED, getName()));
      getEngine().getEngineCallback().processEnded(this);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getEndDate()
    */
   public Date getEndDate()
   {
      return mDateEnded;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getName()
    */
   public QName getName()
   {
      AeProcessDef def = (AeProcessDef) getDefinition();
      return def.getQName();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl#terminate()
    */
   public synchronized void terminate() throws AeBusinessProcessException
   {
      // Only terminate if the process is not completed
      if (isFinalState())
      {
         return;
      }

      // Clear any faulting locations to avoid process interpreting the
      // termination of a faulting activity as a signal to retry the activity
      getFaultingActivityLocationPaths().clear();

      // terminate process iff process's bpel state is *not* final. Otherwise,
      // there may be race conditions where the process has completed and
      // unwinding when the termination is applied.
      if ( getState().isTerminatable() )
      {
         setExiting(true);
         startTermination();
         // in case we were suspended when asked to terminate
         resume(true);
      }
      else if ( getState().isFinal() )
      {
         // Coordinated Subprocess's instance compensation:
         // If this activity was executed in a process level compensation
         // handler (process instance compensation), the process will not
         // terminate since at this point the process has already completed.
         // (i.e. process is not terminatable. The thread then unwinds - but
         // without the AeCompensationHandler object/child completing. Hence,
         // the compHandler callback does not get notified and therefore the
         // coordinator will not get the call back that the subprocess is done
         // with the instance compensation. At this point, we should check if a
         // process instance compensantion handler is running, and if so, fault
         // it so that that call back is invoked.
         AeCompensationHandler compHandler = getCompensationHandler();
         if (compHandler != null && compHandler.getCallback() != null && compHandler.getState().isTerminatable())
         {
            compHandler.terminate();
            // TODO (PJ/Mark): defect 1558: if a subprocess is terminated while its compensating, the process state is "Completed"  - it should be "Faulted". (will be resolved in BPEL 2.0)
         }
      }
   }

   /**
    * Cancel proecss is used by coordination managers to cancel a process because
    * the external coordination has been canceled (faulted). This
    * implementation calls the termination strategy to start termination as
    * BWS and WSBPEL behave differently.
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#cancelProcess()
    */
   public synchronized void cancelProcess() throws AeBusinessProcessException
   {
      getTerminationStrategy().onStartTermination(this);
   }

   /**
    * Serializes the process state to an instance of <code>AeFastDocument</code>.
    *
    * @param aForPersistence <code>true</code> to serialize for persistence.
    * @return AeFastDocument
    * @throws AeBusinessProcessException
    */
   public AeFastDocument fastSerializeState(boolean aForPersistence) throws AeBusinessProcessException
   {
      // Note: This non-interface method is public for AeProcessSnapshot.
      AeProcessImplState serializer = new AeProcessImplState();
      serializer.setForPersistence(aForPersistence);
      serializer.setProcess(this);
      return serializer.getProcessDocument();
   }

   /**
    * Serializes the specified variable to an instance of
    * <code>AeFastDocument</code>.
    *
    * @return AeFastDocument
    * @throws AeBusinessProcessException
    */
   public AeFastDocument fastSerializeVariable(IAeVariable aVariable) throws AeBusinessProcessException
   {
      // Note: This non-interface method is public for AeProcessSnapshot.
      AeVariableSerializer serializer = new AeVariableSerializer(getEngine().getTypeMapping());
      serializer.setVariable(aVariable);
      return serializer.getVariableDocument();
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#serializeState(boolean)
    */
   public synchronized Document serializeState(boolean aForPersistence) throws AeBusinessProcessException
   {
      AeFastDocument document = fastSerializeState(aForPersistence);
      return new AeDocumentBuilder().build(document);
   }

   /**
    * Returns an XML <code>Document</code> that represents the specified
    * variable.
    *
    * @param aVariable
    * @throws AeBusinessProcessException
    */
   protected Document serializeVariable(IAeVariable aVariable) throws AeBusinessProcessException
   {
      AeFastDocument document = fastSerializeVariable(aVariable);
      return new AeDocumentBuilder().build(document);
   }

   /**
    * Serializes the specified fault to an instance of
    * <code>AeFastElement</code>.
    *
    * @param aFault
    * @throws AeBusinessProcessException
    */
   public AeFastElement fastSerializeFault(IAeFault aFault) throws AeBusinessProcessException
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      AeFaultSerializer serializer = new AeFaultSerializer();
      serializer.setFault(aFault);
      serializer.setTypeMapping(getEngine().getTypeMapping());

      AeFastElement faultElement = serializer.getFaultElement();
      return faultElement;
   }

   /**
    * Serializes the specified variable to an instance of
    * <code>AeFastElement</code>.
    * @param aFault
    * @throws AeBusinessProcessException
    */
   protected Document serializeFault(IAeFault aFault) throws AeBusinessProcessException
   {
      AeFastElement element = fastSerializeFault(aFault);
      AeFastDocument doc = new AeFastDocument(element);
      return new AeDocumentBuilder().build(doc);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#serializeVariable(java.lang.String)
    */
   public synchronized Document serializeVariable(String aLocationPath) throws AeBusinessProcessException
   {
      IAeVariable variable = findProcessVariable(aLocationPath);

      if ( variable == null)
         throw new AeBusinessProcessException(
            MessageFormat.format(AeMessages.getString("AeBusinessProcess.ERROR_FINDING_PROCESS_VARIABLE"),  //$NON-NLS-1$
               new Object [] { aLocationPath })
         );

      return serializeVariable(variable);
   }

   /**
    * Called when the process has completed its termination handling.
    */
   protected void terminationComplete() throws AeBusinessProcessException
   {
      IAeFault fault = getFaultFactory().getProcessTerminated();
      setFaultedState(fault);
      processEnded(fault);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#isExiting()
    */
   public boolean isExiting()
   {
      return mExiting;
   }

   /**
    * @param aExiting The exiting to set.
    */
   public void setExiting(boolean aExiting)
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      mExiting = aExiting;
   }

   /**
    * Returns a list of the location paths of the object's in this process's
    * execution queue.
    */
   public List getExecutionQueuePaths()
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      return getExecutionQueue().getLocationPaths();
   }

   /**
    * Returns serialization for this process's variable locker.
    *
    * @throws AeBusinessProcessException
    */
   public DocumentFragment getVariableLockerData() throws AeBusinessProcessException
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      return getVariableLocker().getLockerData(this);
   }

   /**
    * Sets the process's end date.
    */
   public void setEndDate(Date aEndDate)
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      mDateEnded = aEndDate;
   }

   /**
    * Sets the process's execution queue from a list of implementation object
    * location paths.
    *
    * @param aSuspended whether the execution queue is suspended
    * @param aExecutionQueuePaths the list of implementation object location paths
    * @throws AeBusinessProcessException
    */
   public void setExecutionQueue(boolean aSuspended, List aExecutionQueuePaths) throws AeBusinessProcessException
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      List queue = new LinkedList();

      // Convert location paths to implementation objects.
      for (Iterator i = aExecutionQueuePaths.iterator(); i.hasNext(); )
      {
         String locationPath = (String) i.next();
         IAeBpelObject impl = findBpelObjectOrThrow(locationPath);

         queue.add(impl);
      }

      getExecutionQueue().setQueueData(aSuspended, queue);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#setProcessData(org.w3c.dom.Document)
    */
   public void setProcessData(Document aDocument) throws AeBusinessProcessException
   {
      // Not synchronized, because this is called only by the persistent process
      // manager before the process is generally available.
      AeRestoreImplState state = new AeRestoreImplState();
      state.setProcess(this);
      state.restoreState(aDocument);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#setVariableData(java.lang.String, org.w3c.dom.Document, boolean)
    */
   public void setVariableData(String aLocationPath, Document aDocument, boolean aValidate) throws AeBusinessProcessException
   {
      // Not synchronized, because this is called only by the persistent process
      // manager before the process is generally available.
      IAeVariable variable = findProcessVariable(aLocationPath);

      if( variable == null )
      {
         Object [] errMsgParams = new Object[] { aLocationPath, getName().toString(), String.valueOf( getProcessId() ) };
         throw new AeBusinessProcessException(MessageFormat.format( AeMessages.getString("AeBusinessProcess.NO_VARIABLE_FOR_PATH"), errMsgParams)); //$NON-NLS-1$
      }

      AeVariableDeserializer variableSetter = new AeSetVariableHandler( getEngine(), aValidate );
      variableSetter.setVariable(variable);
      variableSetter.restoreVariable(aDocument);
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#addVariableAttachment(java.lang.String, org.activebpel.wsio.AeWebServiceAttachment)
    */
   public synchronized IAeAttachmentItem addVariableAttachment(String aLocationPath, AeWebServiceAttachment aWsioAttachment) throws AeBusinessProcessException
   {
      // TODO (JB) transaction needed ?? synchronize
      IAeVariable variable = findProcessVariable(aLocationPath);

      if( variable == null )
      {
         Object [] errMsgParams = new Object[] { aLocationPath, getName().toString(), String.valueOf( getProcessId() ) };
         throw new AeBusinessProcessException(MessageFormat.format( AeMessages.getString("AeBusinessProcess.NO_VARIABLE_FOR_PATH"), errMsgParams)); //$NON-NLS-1$
      }
      
      IAeAttachmentManager attachmentManager = getEngine().getAttachmentManager();
     
      // Assign a default Content-Id if none set
      if (AeUtil.isNullOrEmpty(aWsioAttachment.getContentId()))
      {
         aWsioAttachment.getMimeHeaders().put(AeMimeUtil.CONTENT_ID_ATTRIBUTE, AeMimeUtil.AE_DEFAULT_REMOTE_CONTENT_ID);
      }
      
      List newAttachmentList = new LinkedList();
      newAttachmentList.add(aWsioAttachment);
      IAeAttachmentContainer tempContainer = attachmentManager.wsio2bpel(newAttachmentList);
      attachmentManager.storeAttachments(tempContainer, null);
      attachmentManager.associateProcess(tempContainer, getProcessId());
      // Add new stored attachment to the variable container.
      variable.getAttachmentData().copy(tempContainer);
      
      return (IAeAttachmentItem)tempContainer.get(0);
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#removeVariableAttachments(java.lang.String, int[])
    */
   public synchronized void removeVariableAttachments(String aLocationPath, int[] aAttachmentItemNumbers)  throws AeBusinessProcessException
   {
      IAeVariable variable = findProcessVariable(aLocationPath);

      if( variable == null )
      {
         Object [] errMsgParams = new Object[] { aLocationPath, getName().toString(), String.valueOf( getProcessId() ) };
         throw new AeBusinessProcessException(MessageFormat.format( AeMessages.getString("AeBusinessProcess.NO_VARIABLE_FOR_PATH"), errMsgParams)); //$NON-NLS-1$
      }
      
      IAeAttachmentManager attachmentManager = getEngine().getAttachmentManager();
      for(int i=aAttachmentItemNumbers.length -1; i >= 0; i--)
      {
         IAeAttachmentItem item = (IAeAttachmentItem)variable.getAttachmentData().get(aAttachmentItemNumbers[i]);
         attachmentManager.removeAttachment(item.getAttachmentId());
         variable.getAttachmentData().remove(aAttachmentItemNumbers[i]);
      }
   }
   
   /**
    * Sets the process's start date.
    */
   public void setStartDate(Date aStartDate)
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      mDateStarted = aStartDate;
   }

   /**
    * Sets this process's variable locker state from a serialized variable
    * locker document.
    */
   public void setVariableLockerData(Node aNode) throws AeBusinessProcessException
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      getVariableLocker().setLockerData(aNode, this);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#dispatchAlarm(int, int)
    */
   public synchronized void dispatchAlarm(int aLocationPathId, int aAlarmId)
   {
      dispatchAlarm(getLocationPath(aLocationPathId), aAlarmId);
   }

   /**
    * Dispatches an alarm by location path.
    *
    * @param aLocationPath
    * @param aAlarmId
    */
   protected void dispatchAlarm(String aLocationPath, int aAlarmId)
   {
      try
      {
         IAeAlarmReceiver receiver = (IAeAlarmReceiver) findBpelObjectOrThrow(aLocationPath);
         if (receiver.isQueued())
         {
            try
            {
               getExecutionQueue().dispatchAlarm(receiver, aAlarmId);
            }
            catch (AeBusinessProcessException e)
            {
               objectCompletedWithFault((IAeBpelObject) receiver, AeFaultFactory.getSystemErrorFault(e));
            }
         }
      }
      catch (AeBusinessProcessException e)
      {
         e.logError();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#dispatchInvokeData(java.lang.String, org.activebpel.rt.message.IAeMessageData, java.util.Map)
    */
   public synchronized void dispatchInvokeData(String aLocationPath, IAeMessageData aData, Map aBusinessProcessProperties)
   {
      try
      {
         // find the receiver and if the receiver is not found then throw an
         // exception
         IAeMessageReceiver receiver = (IAeMessageReceiver) findBpelObjectOrThrow(aLocationPath);
         addBusinessProcessProperties(aBusinessProcessProperties);
         // dispatch the invoke return if throws an exception while processing then fault the bpel object
         try
         {
            getExecutionQueue().dispatchInvokeData(receiver, aData);
         }
         catch (AeBusinessProcessException e)
         {
            objectCompletedWithFault((IAeBpelObject) receiver, AeFaultFactory.getSystemErrorFault(e));
         }
      }
      catch (AeBusinessProcessException e)
      {
         AeBusinessProcessException.logError(e, AeMessages.format("AeBusinessProcess.ERROR_DISPATCHING_INVOKE", new Object[] {aLocationPath, Long.toString(getProcessId()) }) ); //$NON-NLS-1$
      }
      catch (Throwable t)
      {
         AeException.logError(t);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#dispatchInvokeFault(java.lang.String, org.activebpel.rt.bpel.IAeFault, java.util.Map)
    */
   public synchronized void dispatchInvokeFault(String aLocationPath, IAeFault aFault, Map aBusinessProcessProperties)
   {
      try
      {
         IAeMessageReceiver receiver = (IAeMessageReceiver) findBpelObjectOrThrow(aLocationPath);
         addBusinessProcessProperties(aBusinessProcessProperties);
         try
         {
            getExecutionQueue().dispatchInvokeFault(receiver, aFault);
         }
         catch (AeBusinessProcessException e)
         {
            objectCompletedWithFault((IAeBpelObject) receiver, AeFaultFactory.getSystemErrorFault(e));
         }
      }
      catch (AeBusinessProcessException e)
      {
         e.logError();
      }
   }

   /**
    * The process will locate the activity at the given path and attempt to
    * dispatch the data in the inbound receive to the activity.
    *
    * This process could fail for the following reasons:
    *   1. The activity is no longer queued and therefore is not expecting a
    *      message.
    *       - the activity was queued at the time the message was received but
    *         must have been terminated due to a fault or early termination
    *         through a break/continue/completionCondition.
    *   2. The message no longer correlates to the activity.
    *       - the message matched the correlationSets for the activity at the
    *         time it was matched by the queue manager but the correlationSets
    *         have since changed due to the declaring scopes having re-executed
    *   3. bpel:ambiguousReceive The inbound receive correlates to more than one
    *      queued receive (This is WS-BPEL 2.0 behavior only)
    *       - this is possible if more than one IMA is queued for the same
    *         plink/operation but different correlation sets. Even with
    *         different correlation sets, it is possible that the message could
    *         correlate to multiple IMA's.
    *   4. bpel:conflictingRequest: thrown if more than one openIMA exists for
    *      the same plink/operation/messageExchange
    *
    * In order to detect some of the above errors, we need to create a dispatch
    * object from the message receiver which is used to detect and dispatch the
    * fault or simply dispatch the message if there are no faults.
    *
    * @param aLocationPath
    * @param aInboundReceive
    * @param aReplyId
    */
   protected void dispatchReceiveData(String aLocationPath, AeInboundReceive aInboundReceive, long aReplyId)
   {
      try
      {
         IAeMessageReceiverActivity messageReceiver =
            (IAeMessageReceiverActivity) findBpelObjectOrThrow(aLocationPath);

         // Check that the message still correlates.
         // The problem is that the message dispatch in the queue manager
         // happens without a process lock. While we're reasonably sure that the
         // message correlates to the message receiver at the time we record the
         // match, it's possible that the message receiver's state could change
         // in the time it takes us to acquire the process lock and dispatch the
         // message.
         // The first phase of the message dispatch would not make any changes
         // to the queue storage. It would simply identify the message receiver
         // and then issue the dispatch. The code in AeBusinessProcess would
         // have to verify that the message still correlates to the receiver and
         // then consume it and dispatch it. If the message didn't correlate
         // to the receiver, then we would requeue the message in the engine and
         // give it a chance to match to someone else or possibly timeout with
         // no match.
         boolean messageStillCorrelates =
               messageCorrelates(aInboundReceive, messageReceiver);
         if (!messageStillCorrelates)
         {
            handleUnmatchedRequest(aLocationPath, aInboundReceive);
            return;
         }

         // update process properties
         addBusinessProcessProperties(aInboundReceive);

         // Dispatcher is used to dispatch the message or fault to the
         // message receiver. This approach enables the onEvent to defer the
         // creation of its child scope until the message arrives
         IAeMessageDispatcher dispatcher = messageReceiver.createDispatcher(aInboundReceive.getContext());

         if (dispatcher.isPartnerLinkReadyForUpdate())
         {
            // The onEvent will defer the update of the plink until its child
            // scope executes. This avoids the issue of the local plink being
            // updated prior to the scope's execution and therefore having its
            // state cleared as part of the normal state transition.
            AePartnerLinkOpImplKey plo = messageReceiver.getPartnerLinkOperationImplKey();
            AePartnerLink plink = findProcessPartnerLink(plo.getPartnerLinkLocationPath());
            getEngine().getPartnerLinkStrategy().updatePartnerLink(plink, getProcessPlan(), aInboundReceive.getContext());
         }

         // The spec implies that we detect an ambiguousReceive prior to
         // detecting a conflictingRequest. The conflictingRequest fault isn't
         // thrown until the receive starts executing so it seems that the
         // ambiguousReceive takes precedence.
         List list = findAmbiguousReceives(aInboundReceive, messageReceiver);
         if (!list.isEmpty())
         {
            handleAmbiguousReceives(dispatcher, list, aInboundReceive);
            return;
         }

         if (aInboundReceive.getReplyReceiver() != null)
         {
            AeOpenMessageActivityInfo openIMA = createOpenMessageActivityInfo(dispatcher, aInboundReceive.getReplyReceiver(), aReplyId);

            //
            // Check for conflicting requests.
            //
            // Applies to two-way messages only (i.e. aReplyReceiver != null).
            //
            // check to see if already in list
            if ( findMatchingOpenMessageActivityInfo(openIMA) != null)
            {
               // handle conflicting request
               handleConflictingRequest(dispatcher, openIMA);
               return;
            }

            // Save open message activity info as part of process state.
            addOpenMessageActivityInfo(openIMA);
         }

         //
         // Dispatch the data to the message receiver.
         //
         try
         {
            getExecutionQueue().dispatchReceiveData(dispatcher, aInboundReceive.getMessageData());
         }
         catch (AeBusinessProcessException e)
         {
            objectCompletedWithFault(dispatcher.getTarget(), AeFaultFactory.getSystemErrorFault(e));
         }
      }
      catch (AeBusinessProcessException e)
      {
         e.logError();
      }
   }


   /**
    * Faults the receiver's dispatcher we had selected and all of the message
    * receivers that caused the dispatch to be ambiguous
    *
    * @param aDispatcher
    * @param aList
    * @param aInboundReceive
    */
   private void handleAmbiguousReceives(IAeMessageDispatcher aDispatcher, List aList, AeInboundReceive aInboundReceive) throws AeBusinessProcessException
   {
      // create list of dispatchers
      List dispatchers = new ArrayList(aList.size() + 1);
      dispatchers.add(aDispatcher);

      // create a dispatcher for each of the message receivers
      for (Iterator iter = aList.iterator(); iter.hasNext();)
      {
         IAeMessageReceiverActivity receiver = (IAeMessageReceiverActivity) iter.next();
         dispatchers.add(receiver.createDispatcher(aInboundReceive.getContext()));
      }

      // fault each of the dispatchers. Note: we check that the dispatcher's
      // bpel object is still in non-final state since it's possible that the
      // first fault that gets dispatched could cause all of the other receives
      // to be terminated.
      for (Iterator iter = dispatchers.iterator(); iter.hasNext();)
      {
         IAeMessageDispatcher dispatcher = (IAeMessageDispatcher) iter.next();
         IAeBpelObject target = dispatcher.getTarget();
         if (!target.getState().isFinal())
         {
            // any exception from this call will break us out of the loop.
            // I don't think there are any recoverable faults here so
            // propagating the fault seems ok.
            dispatcher.onFault(getFaultFactory().getAmbiguousReceive());
         }
      }

      if (aInboundReceive.getReplyReceiver() != null)
      {
         AeReply replyObject = new AeReply(getProcessId(), aInboundReceive.getReplyId(), aInboundReceive.getReplyReceiver());
         getEngine().sendReply(replyObject, null, getFaultFactory().getExternalAmbiguousReceive(), null);
      }
   }

   /**
    * Walks all of the queued receives in order to check if the inbound receive
    * correlates to more than one message receiver.
    * @param aInboundReceive
    * @param aMessageReceiver
    * @return List of ambiguous receives, all of which need to be faulted in
    *         addition to the original message receiver
    */
   private List findAmbiguousReceives(AeInboundReceive aInboundReceive, IAeMessageReceiverActivity aMessageReceiver)
   {
      // there is no fault for BPWS
      if (!getFaultFactory().isAmbiguousReceiveFaultSupported())
         return Collections.EMPTY_LIST;

      List list = null;

      for(Iterator it=getQueuedReceives().values().iterator(); it.hasNext();)
      {
         IAeMessageReceiverActivity receiver = (IAeMessageReceiverActivity) it.next();
         if (receiver != aMessageReceiver)
         {
            // start with the plink + operation
            boolean match = aInboundReceive.getPartnerLinkOperationKey().equals(receiver.getPartnerLinkOperationImplKey());
            if (match)
            {
               // move on to test the correlation data
               if (messageCorrelates(aInboundReceive, receiver))
               {
                  // we've found an ambiguousReceive, add it to the list
                  if (list == null)
                     list = new ArrayList();
                  list.add(receiver);
               }
            }
         }
      }

      if (list == null)
         list = Collections.EMPTY_LIST;
      return list;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#dispatchReceiveData(int, org.activebpel.rt.bpel.impl.queue.AeInboundReceive, long)
    */
   public synchronized void dispatchReceiveData(int aLocationPathId, AeInboundReceive aInboundReceive, long aReplyId)
   {
      String locationPath = getLocationPath(aLocationPathId);
      dispatchReceiveData(locationPath, aInboundReceive, aReplyId);
   }

   /**
    * Returns true if the message still correlates to the activity
    * @param aInboundReceive
    * @param aActivity
    */
   protected boolean messageCorrelates(AeInboundReceive aInboundReceive, IAeMessageReceiverActivity aActivity)
   {
      boolean correlates = aActivity.isQueued();
      if (correlates)
      {
         try
         {
            Map inboundProperties = new HashMap(aInboundReceive.getCorrelation());
            Map activityCorrelations = aActivity.getCorrelations() != null ? aActivity.getCorrelations().getInitiatedProperties() : new HashMap();

            // add any engine managed correlation
            getEngineManagedCorrelationData(aActivity, activityCorrelations);

            // remove any extra props from the inbound map. There could be extra
            // if there are multiple receives in this process for the same
            // operation but with different correlation sets
            inboundProperties.keySet().retainAll(activityCorrelations.keySet());

            correlates = inboundProperties.equals(activityCorrelations);
         }
         catch (AeCorrelationViolationException e)
         {
            // Should never happen. The reason being that if the activity is
            // still queued then it must have been able to produce a map of its
            // initiated properties as part of being queued.
            e.logError();
            correlates = false;
         }
      }
      return correlates;
   }

   /**
    * The message no longer correlates to the message receiver.
    * @param aLocationPath
    * @param aInboundReceive
    */
   protected void handleUnmatchedRequest(String aLocationPath, AeInboundReceive aInboundReceive) throws AeBusinessProcessException
   {
      // TODO (MF) change to requeue the message if it's not matched
      IAeReplyReceiver replyReceiver = aInboundReceive.getReplyReceiver();
      if (replyReceiver != null)
      {
         AeReply replyObject = new AeReply(getProcessId(), aInboundReceive.getReplyId(), replyReceiver);
         getEngine().sendReply(replyObject, null, getFaultFactory().getUnmatchedRequest(), null);
      }

      // fire an info event so the process has a record of the error
      AeProcessInfoEvent infoEvent = new AeProcessInfoEvent(
            getProcessId(),
            aLocationPath,
            IAeProcessInfoEvent.GENERIC_INFO_EVENT,
            null,
            AeMessages.format("AeBusinessProcess.UNMATCHED_REQUEST", new Object[] {aLocationPath})); //$NON-NLS-1$
      getEngine().fireInfoEvent(infoEvent);
   }

   /**
    * Notifies the sender of the message and the process of the conflicting
    * request fault.
    *
    * @param aMessageDispatcher
    * @param aOpenMessageActivity
    * @throws AeBusinessProcessException
    */
   protected void handleConflictingRequest(IAeMessageDispatcher aMessageDispatcher, AeOpenMessageActivityInfo aOpenMessageActivity) throws AeBusinessProcessException
   {
      // reply fault.
      IAeFault fault = getFaultFactory().getConflictingRequest();
      sendReply(aOpenMessageActivity, null, fault, null);
      // fault the message receiver.
      aMessageDispatcher.onFault(fault);
   }

   /**
    * Returns the message that created the process or <code>null</code> if the
    * start activity has already consumed the message.
    *
    * @return AeInboundReceive
    */
   public AeInboundReceive getCreateMessage()
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      return mCreateMessage;
   }

   /**
    * The process will attempt to resolve the location path to an id using its
    * def object and then fallback to the instance mappings which contain the
    * dynamically created objects.
    *
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getLocationId(java.lang.String)
    */
   public int getLocationId(String aLocationPath)
   {
      AeProcessDef def = (AeProcessDef) getDefinition();
      int id = def.getLocationId(aLocationPath);
      if (id == -1)
      {
         synchronized (this)
         {
         Integer eyeD = (Integer) mLocationPathToId.get(aLocationPath);
         if (eyeD != null)
            id = eyeD.intValue();
      }
      }
      return id;
   }

   /**
    * Checks with the definition object first to see if it has a mapping for the
    * location id and then falls back to the instance mappings which contain the
    * dynamically created objects.
    *
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getLocationPath(int)
    */
   public String getLocationPath(int aLocationId)
   {
      AeProcessDef def = (AeProcessDef) getDefinition();
      String path = def.getLocationPath(aLocationId);
      if (path == null)
      {
         synchronized (this)
         {
         path = (String) mLocationIdToPath.get(aLocationId);
      }
      }
      return path;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getProcessSnapshot()
    */
   public IAeProcessSnapshot getProcessSnapshot() throws AeBusinessProcessException
   {
      // Not synchronized, because this is called only by the persistent process
      // manager when persisting the process.
      return new AeProcessSnapshot(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#isStartMessageAvailable()
    */
   public boolean isStartMessageAvailable()
   {
      return getCreateMessage() != null;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getProcessPlan()
    */
   public IAeProcessPlan getProcessPlan()
   {
      return mProcessPlan;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#getExpressionLanguageFactory()
    */
   public IAeExpressionLanguageFactory getExpressionLanguageFactory() throws AeException
   {
      return getEngine().getExpressionLanguageFactory();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#getBusinessProcessPropertiesMap()
    */
   public Map getBusinessProcessPropertiesMap()
   {
      if( mBusinessProcessProperties == null )
      {
         mBusinessProcessProperties = new HashMap();
      }
      return mBusinessProcessProperties;
   }

   /**
    * Add all of the properties in the map to the business process.  The arg
    * map should only contain <code>String</code> name/value pairs.
    *
    * @param aMap
    */
   public void addBusinessProcessProperties( Map aMap )
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      if (aMap != null)
      {
         getBusinessProcessPropertiesMap().putAll( aMap );
      }
   }

   /**
    * Adds process properties from the inbound receive.
    *
    * @param aInboundReceive
    */
   protected void addBusinessProcessProperties(AeInboundReceive aInboundReceive)
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      if ((aInboundReceive != null) && (aInboundReceive.getContext() != null))
      {
         addBusinessProcessProperties(aInboundReceive.getContext().getBusinessProcessProperties());
      }
   }

   /**
    * Retrieve the business process property or null if none is found.
    *
    * @param aName
    */
   public String getBusinessProcessProperty( String aName )
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      Object value = getBusinessProcessPropertiesMap().get(aName);
      return (value instanceof String) ? (String) value : null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#getFaultingActivityLocationPaths()
    */
   public List getFaultingActivityLocationPaths()
   {
      return mFaultingActivityLocationPaths;
   }

   /**
    * @return Returns the processAdministrator.
    */
   protected AeProcessSuspendResumeHandler getProcessSuspendResumeHandler()
   {
      return mProcessAdministrator;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#setPartnerLinkData(boolean, java.lang.String, org.w3c.dom.Document)
    */
   public synchronized void setPartnerLinkData( boolean aIsPartnerRole, String aPartnerLinkPath, Document aPartnerEndpointRef )
   throws AeBusinessProcessException
   {
      AePartnerLink partnerLink = findProcessPartnerLink(aPartnerLinkPath);
      if( partnerLink != null )
      {
         if(aIsPartnerRole)
            partnerLink.getPartnerReference().setReferenceData( aPartnerEndpointRef.getDocumentElement() );
         else
            partnerLink.getMyReference().setReferenceData( aPartnerEndpointRef.getDocumentElement() );
      }
      else
      {
         Object[] errMsgParams = { aPartnerLinkPath, getName().toString(), String.valueOf( getProcessId() ) };
         throw new AeBusinessProcessException( AeMessages.format("AeBusinessProcess.NO_PARTNERLINK_FOR_PATH", errMsgParams ) ); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getPartnerRoleEndpointReference(java.lang.String)
    */
   public synchronized IAeEndpointReference getPartnerRoleEndpointReference(String aPartnerLinkPath)
   throws AeBusinessProcessException
   {
      AePartnerLink partnerLink = findProcessPartnerLink(aPartnerLinkPath);

      if( partnerLink != null && partnerLink.getPartnerReference() != null )
      {
         return partnerLink.getPartnerReference();
      }
      else
      {
         Object[] errMsgParams = { aPartnerLinkPath, getName().toString(), String.valueOf( getProcessId() ) };
         throw new AeBusinessProcessException( AeMessages.format("AeBusinessProcess.NO_PARTNERLINK_FOR_PATH", errMsgParams ) ); //$NON-NLS-1$
      }
   }

   /**
    * Finds the correlationset given the location path.
    */
   protected AeCorrelationSet findCorrelationSetByPath( String aLocationPath) throws AeBusinessProcessException
   {
      AeCorrelationSet correlationSet = null;
      String parentPath = AeLocationPathUtils.getCorrelationParent(aLocationPath);
      if (parentPath != null)
      {
         String csName = AeLocationPathUtils.getCorrelationSetName(aLocationPath);
         if (AeLocationPathUtils.isProcessPath(parentPath))
         {
            // pull from process
            correlationSet = getCorrelationSet(csName);
         }
         else
         {
            AeActivityScopeImpl scope = (AeActivityScopeImpl) findBpelObjectOrThrow( parentPath );
            correlationSet = scope.getCorrelationSet(csName);
         }
      }

      if (correlationSet == null)
      {
         Object[] errMsgParams = { aLocationPath, getName().toString(), String.valueOf( getProcessId() ) };
         throw new AeBusinessProcessException( AeMessages.format("AeBusinessProcess.NO_CORRELATION_SET_FOR_PATH", errMsgParams ) ); //$NON-NLS-1$
      }

      return correlationSet;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getCorrelationData(java.lang.String)
    */
   public synchronized Map getCorrelationData( String aLocationPath) throws AeBusinessProcessException
   {
      AeCorrelationSet correlationSet = findCorrelationSetByPath( aLocationPath );
      return new HashMap(correlationSet.getPropertyValues());
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#setCorrelationData(java.lang.String, java.util.Map)
    */
   public synchronized void setCorrelationData( String aLocationPath, Map aCorrelationData )
   throws AeBusinessProcessException
   {
      AeCorrelationSet correlationSet = findCorrelationSetByPath( aLocationPath );
      correlationSet.setPropertyValues( aCorrelationData );
   }

   /**
    * Getter for the max location id. If no objects have been created outside of
    * the initial visitation then this will simply return the value from the def.
    * Otherwise, we'll have a record locally of the max location id.
    */
   public int getMaxLocationId()
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      if (mMaxLocationId == -1)
      {
         return ((AeProcessDef)getDefinition()).getMaxLocationId();
      }
      return mMaxLocationId;
   }

   /**
    * Called after we've created some objects dynamically (ie through parallel
    * forEach execution).
    *
    * @param aMaxValue
    */
   public void setMaxLocationId(int aMaxValue)
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      mMaxLocationId = aMaxValue;
   }

   /**
    * Gets the scope definition for this scope.
    */
   protected AeScopeDef getScopeDef()
   {
      return (AeScopeDef) getDefinition();
   }

   /**
    * Informs the alert manager that the process is faulting.
    * @param aLocationPath
    * @param aFault
    */
   protected void alertFaulting(String aLocationPath, IAeFault aFault)
   {
      AeEngineAlert alert = createAlert(IAeEngineAlert.PROCESS_ALERT_FAULTING, aLocationPath, aFault);
      getEngine().fireEngineAlert(alert);
   }

   /**
    * Informs the alert manager that process recovery determined that the
    * process has a non-durable invoke pending.
    *
    * @param aLocationPath
    */
   protected void alertInvokeRecovery(String aLocationPath)
   {
      AeEngineAlert alert = createAlert(IAeEngineAlert.PROCESS_ALERT_INVOKE_RECOVERY, aLocationPath, null);
      getEngine().fireEngineAlert(alert);
   }

   /**
    * Creates an AeEngineAlert with the info provided.
    * @param aReason either faulting or suspend constant
    * @param aLocationPath location of the activity causing the alert
    * @param aDetails optional details of the alert
    */
   protected AeEngineAlert createAlert(int aReason, String aLocationPath, Object aDetails)
   {
      QName faultName = null;
      Document doc = null;
      try
      {
         if (aDetails instanceof IAeVariable)
         {
            IAeVariable variable = (IAeVariable) aDetails;
            // TODO (MF) defer serialization until we know someone is listening
            if (variable != null && variable.hasData())
            {
               doc = serializeVariable(variable);
            }
         }
         else if (aDetails instanceof IAeFault)
         {
            IAeFault fault = (IAeFault) aDetails;
            faultName = fault.getFaultName();
            if (fault.getMessageData() != null)
            {
               doc = serializeFault(fault);
            }
         }
      }
      catch(AeException e)
      {
         e.logError();
      }
      return new AeEngineAlert(getProcessId(), aReason, getName(), aLocationPath, faultName, doc );
   }

   /**
    * Adds the message receiver to our collection of receivers. We track the
    * receivers within the process to prevent multiple concurrent receivers for
    * the same tupple of partner link, operation, and correlation sets.
    *
    * @param aMessageReceiver
    * @throws AeConflictingReceiveException
    */
   public void addReceiverKeyForConflictingReceives(IAeMessageReceiverActivity aMessageReceiver)
      throws AeConflictingReceiveException
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      AeQueuedReceiveKey key = new AeQueuedReceiveKey(aMessageReceiver);
      Object conflict = getQueuedReceives().put(key, aMessageReceiver);
      if (conflict != null)
      {
         // put the old one back
         getQueuedReceives().put(key, conflict);
         throw new AeConflictingReceiveException(getBPELNamespace());
      }
   }

   /**
    * Removes the message receiver from the collection of executing receivers.
    *
    * @param aMessageReceiver
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#removeReceiverKeyForConflictingReceives(org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity)
    */
   public void removeReceiverKeyForConflictingReceives(IAeMessageReceiverActivity aMessageReceiver)
   {
      AeQueuedReceiveKey key = new AeQueuedReceiveKey(aMessageReceiver);
      getQueuedReceives().remove(key);
   }

   /**
    * @return Returns the queuedReceives.
    */
   protected Map getQueuedReceives()
   {
      return mQueuedReceives;
   }

   /**
    * Adds an inbound open message activity which needs a response to the
    * process.
    *
    * @param aInfo message activity info that is currently open.
    */
   public void addOpenMessageActivityInfo(AeOpenMessageActivityInfo aInfo)
   {
      // Note: This non-interface method is public for AeRestoreImplStateVisitor.
      if (aInfo.getReplyId() == IAeReplyReceiver.NULL_REPLY_ID)
      {
         AeException.logWarning(AeMessages.format("AeBusinessProcess.MISSING_REPLY_ID", aInfo.getMessageExchangePath() )); //$NON-NLS-1$
      }
      getOpenMessageActivityInfoList().add(aInfo);
   }

   /**
    * Creates a open message activity struct given the message dispatcher and
    * the reply receiver. Only durable reply receivers are included as part of
    * the open message activity.
    * @param aMessageDispatcher
    * @param aReplyReceiver
    * @param aReplyId
    * @return open message activity.
    */
   protected AeOpenMessageActivityInfo createOpenMessageActivityInfo(IAeMessageDispatcher aMessageDispatcher, IAeReplyReceiver aReplyReceiver, long aReplyId) throws AeBusinessProcessException
   {
      AeOpenMessageActivityInfo oma = null;

      if (aReplyReceiver != null)
      {
         // Save open message activity info only there is a reply waiting
         // (i.e. not a one-way message). Req-Resp style message will have a
         // reply receiver.
         if (aReplyId == IAeReplyReceiver.NULL_REPLY_ID)
         {
            throw new AeBusinessProcessException( AeMessages.format("AeBusinessProcess.MISSING_REPLY_ID", aMessageDispatcher.getLocationPath() ) ); //$NON-NLS-1$
         }

         // The a reply receiver with this oma should be associated iff the
         // reply receiver is durable. (otherwise, the "in memory" reply
         // receivers are maintained by the queue manager).
         IAeReplyReceiver omaReplyReceiver = aReplyReceiver.getDurableReplyInfo() != null ? aReplyReceiver : null;
         oma = new AeOpenMessageActivityInfo(aMessageDispatcher.getPartnerLinkOperationImplKey(),
                  aMessageDispatcher.getMessageExchangePathForOpenIMA(), aReplyId, omaReplyReceiver);
         oma.setReceiverPath(aMessageDispatcher.getTarget().getLocationPath());
      }
      return oma;
   }


   /**
    * @return List containing open inbound message activity info.
    */
   public List getOpenMessageActivityInfoList()
   {
      // Note: This non-interface method is public for AeSaveImplStateVisitor.
      if (mOpenMessageActivityInfoList == null)
      {
         mOpenMessageActivityInfoList = new LinkedList();
      }
      return mOpenMessageActivityInfoList;
   }

   /**
    * Finds and returns the matching open message activity. Returns
    * <code>null</code> if not found.
    * @param aPartnerLinkOpImplKey
    * @param aMessageExchangePath
    * @return matching open message activity.
    */
   protected AeOpenMessageActivityInfo findMatchingOpenMessageActivityInfo(AePartnerLinkOpImplKey aPartnerLinkOpImplKey, String aMessageExchangePath)
   {
      // create prototype
      AeOpenMessageActivityInfo prototype = new AeOpenMessageActivityInfo(aPartnerLinkOpImplKey, aMessageExchangePath, 0);
      return findMatchingOpenMessageActivityInfo(prototype);
   }

   /**
    * Finds and returns the matching open message activity.
    * @param aPrototype
    */
   protected AeOpenMessageActivityInfo findMatchingOpenMessageActivityInfo(AeOpenMessageActivityInfo aPrototype)
   {
      // no need to synchronized on open message activity list since only one
      // thread has access to a process.
      for (Iterator i = getOpenMessageActivityInfoList().iterator(); i.hasNext(); )
      {
         AeOpenMessageActivityInfo oma = (AeOpenMessageActivityInfo) i.next();
         if (oma.equals(aPrototype))
         {
            return oma;
         }
      }

      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl#initializeVariables()
    */
   protected void initializeVariables() throws AeBusinessProcessException
   {
      try
      {
         super.initializeVariables();
      }
      catch(AeBpelException e)
      {
         String message = AeMessages.getString("AeBusinessProcess.ErrorInitializingProcessLevelVariable"); //$NON-NLS-1$

         // fire an info event so the process has a record of the error
         AeProcessInfoEvent infoEvent = new AeProcessInfoEvent(
               getProcessId(),
               getLocationPath(),
               IAeProcessInfoEvent.GENERIC_INFO_EVENT,
               null,
               message);
         getEngine().fireInfoEvent(infoEvent);

         objectCompletedWithFault(e.getFault());
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#objectCompletedWithFaultInternal(org.activebpel.rt.bpel.IAeFault)
    */
   protected void objectCompletedWithFaultInternal(IAeFault aFault) throws AeBusinessProcessException
   {
      setExiting(true);
      super.objectCompletedWithFaultInternal(aFault);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#initPartnerLink(org.activebpel.rt.bpel.impl.AePartnerLink)
    */
   public void initPartnerLink(AePartnerLink aPartnerLink) throws AeBusinessProcessException
   {
      aPartnerLink.clear();
      getEngine().getPartnerLinkStrategy().initPartnerLink(aPartnerLink, getProcessPlan());

      // Update the partner link if it matches the start message.  In this
      // special case, we need to specifically update the partner link because
      // it couldn't be done when the message was received (which is when it
      // would normally occur).
      AeInboundReceive startMessage = getCreateMessage();
      if (startMessage != null)
      {
         AePartnerLinkDefKey startMessagePLKey = startMessage.getPartnerLinkOperationKey();
         AePartnerLinkDefKey plKey = new AePartnerLinkDefKey(aPartnerLink.getDefinition());
         if (startMessagePLKey.equals(plKey))
         {
            getEngine().getPartnerLinkStrategy().updatePartnerLink(aPartnerLink, getProcessPlan(), startMessage.getContext());
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#isAllowCreateTargetXPath()
    */
   public boolean isAllowCreateTargetXPath()
   {
      AeProcessDef processDef = getProcessDefinition();
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(processDef.getNamespace()))
      {
         return getEngine().getEngineConfiguration().getUpdatableEngineConfig().allowCreateXPath();
      }
      else
      {
         return processDef.isCreateTargetXPath();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#isDisableSelectionFailure()
    */
   public boolean isDisableSelectionFailure()
   {
      AeProcessDef processDef = getProcessDefinition();
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(processDef.getNamespace()))
      {
         return getEngine().getEngineConfiguration().getUpdatableEngineConfig().allowEmptyQuerySelection();
      }
      else
      {
         return processDef.isDisableSelectionFailure();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#hasNewOrphanedIMAs(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   public boolean hasNewOrphanedIMAs(AeActivityScopeImpl aScope)
   {
      String scopePath = aScope.getLocationPath();
      boolean found = false;

      for (Iterator i = getOpenMessageActivityInfoList().iterator(); i.hasNext(); )
      {
         AeOpenMessageActivityInfo oma = (AeOpenMessageActivityInfo) i.next();

         // Skip previously orphaned IMAs.
         if (!oma.isOrphaned())
         {
            String messageExchangePath = AeUtil.getSafeString(oma.getMessageExchangePath());
            String partnerLinkPath = AeUtil.getSafeString(oma.getPartnerLinkLocationPath());

            // Check if the message exchange or partner link is defined somewhere
            // within the given scope.
            if (messageExchangePath.startsWith(scopePath) || partnerLinkPath.startsWith(scopePath))
            {
               // Mark this one as orphaned.
               oma.setOrphaned(true);

               // Found at least one.
               found = true;
            }
         }
      }

      return found;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#faultOrphanedIMAs(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl, org.activebpel.rt.bpel.IAeFault)
    */
   public void faultOrphanedIMAs(AeActivityScopeImpl aScope, IAeFault aFault)
   {
      String scopePath = aScope.getLocationPath();
      Map properties = null;

      // Legacy behavior: fault with missingReply if the IMA was not orphaned
      // by some other fault (i.e., if aFault == null).
      //
      // fixme (MF-3.1) Change this to missingReply or custom AE fault
      IAeFault fault = (aFault == null) ? getFaultFactory().getMissingReply() : aFault;

      for (Iterator i = getOpenMessageActivityInfoList().iterator(); i.hasNext(); )
      {
         AeOpenMessageActivityInfo oma = (AeOpenMessageActivityInfo) i.next();

         // Fault only previously orphaned IMAs.
         if (oma.isOrphaned())
         {
            String messageExchangePath = AeUtil.getSafeString(oma.getMessageExchangePath());
            String partnerLinkPath = AeUtil.getSafeString(oma.getPartnerLinkLocationPath());

            // Check if the message exchange or partner link is defined
            // somewhere within the given scope.
            if (messageExchangePath.startsWith(scopePath) || partnerLinkPath.startsWith(scopePath))
            {
               if (properties == null)
               {
                  properties = cloneBusinessProcessProperties();
               }

               try
               {
                  sendReply(oma, null, fault, properties);
               }
               catch (AeBusinessProcessException e)
               {
                  AeException.logError(e, AeMessages.getString("AeBusinessProcess.ERROR_FAULT_ORPHANED_REPLY")); //$NON-NLS-1$
               }
               finally
               {
                  i.remove();
               }
            }
         }
      }
   }

   /**
    * Overrides method to make it clear how this {@link IAeBusinessProcess}
    * method is implemented.
    *
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getBPELNamespace()
    */
   public String getBPELNamespace()
   {
      return super.getBPELNamespace();
   }

   /**
    * Overrides method to make it clear how this {@link IAeBusinessProcess}
    * method is implemented.
    *
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#getFault()
    */
   public IAeFault getFault()
   {
      return super.getFault();
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#isFinalState()
    */
   public boolean isFinalState()
   {
      switch (getProcessState())
      {
         case IAeBusinessProcess.PROCESS_COMPLETE:
         case IAeBusinessProcess.PROCESS_COMPENSATABLE:
         case IAeBusinessProcess.PROCESS_FAULTED:
            return true;

         default:
            return false;
      }
   }

   /**
    * Overrides method to make it clear how this {@link IAeBusinessProcess}
    * method is implemented.
    *
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#isNormalCompletion()
    */
   public boolean isNormalCompletion()
   {
      return super.isNormalCompletion();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#removeReply(org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey, java.lang.String)
    */
   public void removeReply(AePartnerLinkOpImplKey aPartnerLinkKey, String aMessageExchange) throws AeBusinessProcessException
   {
      AeOpenMessageActivityInfo info = findMatchingOpenMessageActivityInfo(aPartnerLinkKey, aMessageExchange);
      if (info != null)
      {
         try
         {
            AeReply replyObject = new AeReply(getProcessId(), info.getReplyId());
            getEngine().removeReply(replyObject);
         }
         finally
         {
            getOpenMessageActivityInfoList().remove(info);
         }
      }
   }

   /**
    * Overrides method to always return <code>false</code>.
    *
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl#isIsolatedScope()
    */
   public boolean isIsolatedScope()
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBusinessProcess#handleExecutableItemException(org.activebpel.rt.bpel.impl.IAeExecutableQueueItem, java.lang.Throwable)
    */
   public void handleExecutableItemException(IAeExecutableQueueItem aExecutable, Throwable aThrowable) throws AeBusinessProcessException
   {
      IAeFault fault = null;
      
      if (aThrowable instanceof AeBpelException)
      {
         fault = ((AeBpelException) aThrowable).getFault();
      }
      
      if (fault == null)
      {
         fault = AeFaultFactory.getSystemErrorFault(aThrowable);
      }
      
      if (fault != null)
      {
         String message = aThrowable.getLocalizedMessage();
         if (AeUtil.isNullOrEmpty(message))
         {
            message = AeMessages.getString("AeBusinessProcess.NO_ADDITIONAL_INFO"); //$NON-NLS-1$
         }

         fault.setInfo(message);
      }

      aExecutable.objectCompletedWithFault(fault);
   }

   /**
    * Assigns the transmission id so we'll avoid re-executing if we've already
    * performed the behavior attached to this transmission id
    * @param aTransmission
    */
   public void assignTransmissionId(IAeTransmission aTransmission)
   {
      // Set the next non-durable/persistent transmission id if one has not been assigned already.
      // This id is used to match up the invoke when the invoke message data or faulted return is
      // dispatched to the activity via the engine. Per defect 1852, if the ref id associated with
      // the response data/fault does not match the current id, then the response data is 
      // dropped/ignored.
      //
      // A new id is assigned during execution (i.e. here) only if one has not been already
      // assigned. The only time you should see a id (id > 0) "pre-assigned" at this
      // point is during playback of durable invoke journals or during state restoration.
      // Assign invoke/tx id iff current tx id is null.
      if (aTransmission.getTransmissionId() == IAeTransmissionTracker.NULL_TRANSREC_ID)
      {
         // Assign a new  non-durable/non-persistent transmission id since the invoke does not have a valid xmt id
         // i.e. this is not a playback of a durable invoke journal. (journaled invokes would already
         // have a (positive) transmission id set via journal playback).
         aTransmission.setTransmissionId( getProcess().getNextInvokeId() );
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#compensationCompletedCallback(java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault)
    */
   public synchronized void compensationCompletedCallback(String aLocationPath,
         String aCoordinationId, IAeFault aFault)
         throws AeBusinessProcessException
   {
      AeActivityScopeImpl scope = findEnclosingScope(aLocationPath);
      // get the compensation handler for the coord-id.
      AeCompensationHandler compHandler = scope.getCoordinationContainer().getCompensationHandler(aCoordinationId);
      if (compHandler != null && compHandler instanceof IAeCompensationCallback)
      {
         // Note: AeCoordinatorCompensationHandler also implements IAeCompensationCallback
         IAeCompensationCallback compCallback = (IAeCompensationCallback)compHandler;
         if (aFault == null)
         {
            compCallback.compensationComplete(compHandler);
         }
         else
         {
            compCallback.compensationCompleteWithFault(compHandler, aFault);
         }
      }
      else
      {
         // error - should get not here.
         AeException.logWarning(AeMessages.format("AeBusinessProcess.UNSUPPORTED_COMP_HANDLER",String.valueOf(compHandler))); //$NON-NLS-1$
      }
      scope.getCoordinationContainer().deregisterCoordinationId( aCoordinationId );
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#coordinatedActivityCompleted(java.lang.String, java.lang.String, org.activebpel.rt.bpel.coord.IAeCoordinator, org.activebpel.rt.bpel.IAeFault)
    */
   public synchronized void coordinatedActivityCompleted(String aLocationPath,
         String aCoordinationId, IAeCoordinator aCoordinator, final IAeFault aFault) 
   {
      final AeActivityScopeImpl scope = findEnclosingScope(aLocationPath);
      if (scope == null)
         return;
      
      if (aFault != null)
      {
         // defect #1367
         // if subprocess was terminated or faulted before it replied to the invoke,
         // then the Invoke would have faulted and gone thru the standard error handling
         // code. For example, the invoke would have faulted and hence then enclosing scope
         // would have also faulted (via normal fault handling code in the engine).
         // Therefore, the coordination framework should not fault the enclosing scope (i.e. second time)
         
         // Also defect: #2579 - coordination framework should not double
         // fault the enclosing scope(which could be the process) if the scope/process is already running its fault handlers.
         
         if (!scope.getState().isFinal() && !scope.isExecutingFaultHandler())
         {
            try
            {
               getExecutionQueue().add(new AeExecutionQueue.AeExecutableObjectStub()
               {
                  protected IAeBpelObject getFaultObject()
                  {
                     return scope;
                  }
   
                  public void execute() throws AeBusinessProcessException
                  {
                     scope.triggerFaultHandling(aFault);
                  }
               });
            }
            catch (AeBusinessProcessException e)
            {
            }
         }
      }
      else
      {
         // install compensation handler!
         scope.coordinatedActivityCompleted( aCoordinationId, new AeCoordinatorCompInfo(scope, aCoordinator) );
      }
   }
   
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#deregisterCoordination(java.lang.String, java.lang.String)
    */
   public synchronized void deregisterCoordination(String aLocationPath, String aCoordinationId)
   {
      AeActivityScopeImpl scope = findEnclosingScope(aLocationPath);
      //  remove the coordination id from the scope's coordination container activity.
      if (scope != null)
         scope.getCoordinationContainer().deregisterCoordinationId( aCoordinationId );
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal#registerCoordination(java.lang.String, java.lang.String)
    */
   public synchronized void registerCoordination(String aLocationPath, String aCoordinationId)
         throws AeCoordinationException
   {
         AeActivityScopeImpl scope = findEnclosingScope(aLocationPath);
         // add the coordination id to the scope's coordination container activity.
         if (scope != null)
            scope.getCoordinationContainer().registerCoordinationId( aCoordinationId );
   }

   /**
    * Finds the enclosing scope given the process and the locationPath
    * @param aProcess
    * @param aLocationPath 
    * @return enclosing scope or null if not found.
    */
   protected AeActivityScopeImpl findEnclosingScope(String aLocationPath)
   {
      IAeBpelObject bpel = findBpelObject(aLocationPath);
      if (bpel != null)
      {
         IAeBpelObject parent = bpel.getParent();
         while (parent != null)
         {
            if (parent instanceof AeActivityScopeImpl)
            {
               return (AeActivityScopeImpl)parent;
            }
            parent = parent.getParent();
         } // while                   
      }// if
      return null;
   }   


   /**
    * Value used for the key in a map of currently executing receives. This map
    * is used to prevent receives with the same partner link,
    */
   protected class AeQueuedReceiveKey
   {
      /** The PLO key. */
      private AePartnerLinkOpImplKey mPartnerLinkOpImplKey;
      /**
       * set comprised of location paths for the correlation sets used for the
       * receive
       */
      private Set mCorrelationSets = new HashSet();
      /** cached value of the hashcode */
      private int mHashcode;
      /** the location id for the activity */
      private int mLocationId;

      /**
       * Constructor accepts all of the values for the key
       *
       * @param aMessageReceiver
       */
      public AeQueuedReceiveKey(IAeMessageReceiverActivity aMessageReceiver)
      {
         IAeIMACorrelations receiveCorrelations = aMessageReceiver.getCorrelations();
         Set csPaths = receiveCorrelations != null? receiveCorrelations.getCSPathsForConflictingReceives() : Collections.EMPTY_SET;

         mLocationId = AeBusinessProcess.this.getLocationId(aMessageReceiver.getLocationPath());
         mPartnerLinkOpImplKey = aMessageReceiver.getPartnerLinkOperationImplKey();
         mCorrelationSets.addAll(csPaths);
         mHashcode = mPartnerLinkOpImplKey.hashCode() + mCorrelationSets.hashCode();
      }

      /**
       * @return Returns the correlationSets.
       */
      protected Set getCorrelationSets()
      {
         return mCorrelationSets;
      }

      /**
       * @return Returns the hashcode.
       */
      protected int getHashcode()
      {
         return mHashcode;
      }

      /**
       * @return Returns the PLO key.
       */
      protected AePartnerLinkOpImplKey getPartnerLinkOpImplKey()
      {
         return mPartnerLinkOpImplKey;
      }

      /**
       * @see java.lang.Object#equals(java.lang.Object)
       */
      public boolean equals(Object aObj)
      {
         if (aObj instanceof AeQueuedReceiveKey)
         {
            AeQueuedReceiveKey other = (AeQueuedReceiveKey) aObj;
            return AeUtil.compareObjects(getPartnerLinkOpImplKey(), other.getPartnerLinkOpImplKey()) &&
                   AeUtil.compareObjects(getCorrelationSets(), other.getCorrelationSets());
         }
         return false;
      }

      /**
       * Getter for the location id
       */
      public int getLocationId()
      {
         return mLocationId;
      }

      /**
       * @see java.lang.Object#hashCode()
       */
      public int hashCode()
      {
         return mHashcode;
      }
   }
}
