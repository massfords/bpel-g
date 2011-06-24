// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeBusinessProcess.java,v 1.84 2008/03/11 03:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import java.util.Date;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeSuspendReason;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeExecutableQueueItem;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.storage.IAeProcessSnapshot;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.AeWebServiceAttachment;
import org.w3c.dom.Document;

import bpelg.services.processes.types.ProcessStateValueType;
import bpelg.services.processes.types.SuspendReasonType;

/** Describes the interface used for interacting with business processes */
public interface IAeBusinessProcess
{
   /** Value of an uninitialized process id. */
   public static long NULL_PROCESS_ID = 0;
   
   /** Default process initiator value which is 'anonymous'. */
   public static final String DEFAULT_INITIATOR = "anonymous"; //$NON-NLS-1$

   /**
    * Return the partnerRole endpoint reference or null if none is found.
    * @param aPartnerLinkPath
    * @throws AeBusinessProcessException
    */
   public IAeEndpointReference getPartnerRoleEndpointReference(String aPartnerLinkPath) throws AeBusinessProcessException;
   
   /** 
    * Returns true if this process is participating in an coordinated activity either as a
    * coordinator or as a participant.
    * @return true if participating in a coordinated activity.
    */
   public boolean isCoordinating();
     
   /**
    * Sets the flag participant flag.
    * @param aParticipant
    */
   public void setParticipant(boolean aParticipant);   
   
   /**
    * Get the state of the process
    */
   public ProcessStateValueType getProcessState();

   /**
    * Get the reason code of process state
    */
   public SuspendReasonType getProcessStateReason();

   /**
    * Get the process id for this process.
    */
   public long getProcessId();

   /**
    * The engine this process is running inside of.
    */
   public IAeBusinessProcessEngineInternal getEngine();

   /**
    * Suspend the business process.
    */
   public void suspend(AeSuspendReason aReasonCode) throws AeBusinessProcessException;

   /**
    * Returns correlation data on the process instance.
    * @param aLocationPath Identifies the correlation set within the process.
    * @return CorrelationData map
    */
   public Map<QName, String> getCorrelationData(String aLocationPath) throws AeBusinessProcessException;

   /**
    * Set correlation data on the process instance.
    * @param aLocationPath Identifies the correlation set within the process.
    * @param aCorrelationData
    */
   public void setCorrelationData( String aLocationPath, Map<QName, String> aCorrelationData )
   throws AeBusinessProcessException;

   /**
    * Set the partnerRole partner link endpoint reference data.
    * @param aIsPartnerRole
    * @param aLocationPath
    * @param aPartnerEndpointRef
    * @throws AeBusinessProcessException
    */
   public void setPartnerLinkData( boolean aIsPartnerRole, String aLocationPath, Document aPartnerEndpointRef )
   throws AeBusinessProcessException;

   /**
    * Resumes the business process.
    *
    * @param aExecute <code>true</code> to execute the process immediately or
    * <code>false</code> to put the process in a running state without
    * executing immediately.
    */
   public void resume(boolean aExecute) throws AeBusinessProcessException;

   /**
    * Resume the business process for the suspended object associated
    * with the passed location path
    * @param aLocation the location path of the object to resume.
    */
   public void resume(String aLocation) throws AeBusinessProcessException;

   /**
    * Retry the activity associated with the passed location path or its
    * enclosing scope.
    * @param aLocation
    * @param aAtScope
    * @throws AeBusinessProcessException
    */
   public void retryActivity( String aLocation, boolean aAtScope ) throws AeBusinessProcessException;

   /**
    * Step resume the process and mark the activity associated with passed location
    * path as complete.  The idea here is that the user has manually corrected
    * any issues with the suspended activity or its enclosing scope and has put
    * the process in a state where it can continue (without actually executing
    * suspended the activity).
    * @param aLocation
    * @throws AeBusinessProcessException
    */
   public void completeActivity( String aLocation ) throws AeBusinessProcessException;

   /**
    * Terminates the business process.
    */
   public void terminate() throws AeBusinessProcessException;

   /**
    * Cancels the process by throwing a forcedTerminatation fault.
    */
   public void cancelProcess() throws AeBusinessProcessException;

   /**
    * This method allows a parent to queue a child activity to execute.
    * @param aObject The object that a parent says is ready to execute, links
    *                 may still prevent this object from immediately executing.
    */
   public void queueObjectToExecute(IAeBpelObject aObject) throws AeBusinessProcessException;

   /**
    * This method allows a runnable object to be queued for execution.
    * @param aRunnable
    */
   public void queueObjectToExecute(Runnable aRunnable) throws AeBusinessProcessException;

   /**
    * Queues the process itself for execution.
    */
   public void queueProcessToExecute() throws AeBusinessProcessException;

   /**
    * Returns the variable being referenced in the activity location path.
    * An optional part may be specified when dealing with a message type variable.
    * @param aActivityLoc the location path for the activity which references this variable
    * @param aVarName the name of the variable being referenced
    * @return The variable or null if not found 
    */
   public IAeVariable getVariable(String aActivityLoc, String aVarName);

   /**
    * Sets the message that created the process. The process will keep this until
    * its start activity executes at which time it'll be consumed by that
    * activity.
    * @param aReceiveQueueObject
    */
   public void setCreateMessage(AeInboundReceive aReceiveQueueObject);

   /**
    * Get the date/time that the process instance was started.
    */
   public Date getStartDate();

   /**
    * Gets the date/time that the process ended, either normally, through a fault,
    * or via termination.
    */
   public Date getEndDate();

   /**
    * Gets the qualified name of the process.
    */
   public QName getName();

   /**
    * Serialize and return the process state.
    * @param aForPersistence <code>true</code> to serialize for persistence.
    * @return Document
    * @throws AeBusinessProcessException
    */
   public Document serializeState(boolean aForPersistence) throws AeBusinessProcessException;

   /**
    * Serialize and return a process variable.
    * @param aLocationPath The location XPath of the variable's enclosing scope.
    * @return Document
    * @throws AeBusinessProcessException
    */
   public Document serializeVariable( String aLocationPath ) throws AeBusinessProcessException;

   /**
    * Set this process's state from a previously serialized process state document.
    *
    * @param aDocument
    * @throws AeBusinessProcessException
    */
   public void setProcessData(Document aDocument) throws AeBusinessProcessException;

   /**
    * Set a variable from a previously serialized variable document.
    *
    * @param aLocationPath
    * @param aDocument
    * @param aValidate
    * @throws AeBusinessProcessException
    */
   public void setVariableData(String aLocationPath, Document aDocument, boolean aValidate) throws AeBusinessProcessException;
   
   /**
    * Add an attachment to the variable.
    *
    * @param aLocationPath
    * @param aWsioAttachment AeWebServiceAttachment object
    * @return IAeAttachmentItem - the attachment item added
    * @throws AeBusinessProcessException
    */
   public IAeAttachmentItem addVariableAttachment(String aLocationPath, AeWebServiceAttachment aWsioAttachment) throws AeBusinessProcessException;
   
   /**
    * Remove attachments from the variable.
    *
    * @param aLocationPath
    * @param aAttachmentItemNumbers
    * @throws AeBusinessProcessException
    */
   public void removeVariableAttachments(String aLocationPath, int[] aAttachmentItemNumbers ) throws AeBusinessProcessException;

   /**
    * Dispatches an alarm by location path id.
    *
    * @param aLocationPathId The location path id.
    */
   public void dispatchAlarm(int aLocationPathId, int aAlarmId);

   /**
    * Dispatches message data to a queued invoke.
    *
    * @param aLocationPath The path to the location awaiting the response
    * @param aData The data we have received from invoke.
    */
   public void dispatchInvokeData(String aLocationPath, IAeMessageData aData, Map<String, String> aBusinessProcessProperties);

   /**
    * Dispatches a fault to a queued invoke.
    *
    * @param aLocationPath The path to the location awaiting the response
    * @param aFault The fault we received from invoke.
    */
   public void dispatchInvokeFault(String aLocationPath, IAeFault aFault, Map<String, String> aBusinessProcessProperties);

   /**
    * Dispatches message data to a message receiver (by location path id).
    *
    * @param aLocationPathId The location path id to the location awaiting the message
    * @param aInboundReceive Contains the data and other properties for the inbound receive
    * @param aReplyId queue manager replyId 
    */
   public void dispatchReceiveData(int aLocationPathId, AeInboundReceive aInboundReceive, long aReplyId);

   /**
    * Returns location id corresponding to a location path or <code>-1</code> if not found.
    */
   public int getLocationId(String aLocationPath);

   /**
    * Returns location path corresponding to a location id or <code>null</code> if not found.
    */
   public String getLocationPath(int aLocationId);

   /**
    * Returns a process snapshot that contains all variables and correlation
    * sets that are live for normal or compensation processing.
    *
    * @throws AeBusinessProcessException
    */
   public IAeProcessSnapshot getProcessSnapshot() throws AeBusinessProcessException;

   /**
    * Gets the plan that this process was created with.
    */
   public IAeProcessPlan getProcessPlan();

   /**
    * Returns the Bpel implementation object specified by the location id.
    * @param aLocationId the location id of the Bpel object
    * @return the Bpel impl object or null if not found
    */
   public IAeBpelObject findBpelObject(int aLocationId);

   /**
    * Returns the Bpel implementation object specified by the location path string.
    * @param aLocationPath the XPath location of the Bpel object
    * @return the Bpel impl object or null if not found
    */
   public IAeBpelObject findBpelObject(String aLocationPath);
   
   /**
    * Returns a partner link specified by the location.
    * 
    * @param aLocationPath
    * @return the partner link or null if not found
    */
   public AePartnerLink findProcessPartnerLink(String aLocationPath);

   /**
    * Returns a variable specified by the location.
    * 
    * @param aLocationPath
    * @return the variable or null if not found
    */
   public IAeVariable findProcessVariable(String aLocationPath);
   
   /**
    * Sets the engine this process is running inside of.
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine);

   /**
    * Returns true if the process has been suspended.
    */
   public boolean isSuspended();

   /**
    * Starts compensation for the process
    * @param aCallback
    * @throws AeBusinessProcessException
    */
   public void compensate(IAeCompensationCallback aCallback) throws AeBusinessProcessException;

   /**
    * Releases any resources tied to process level compensation. Called when a compensatable process is
    * no longer compensatable (likely owing to its coordination ending without having called compensate).
    */
   public void releaseCompensationResources();
   
   /**
    * Getter for the BPEL namespace
    */
   public String getBPELNamespace();

   /**
    * Returns the process fault or <code>null</code> if the process did not
    * fault.
    */
   public IAeFault getFault();
   
   /**
    * Returns <code>true</code> if and only if the process is in a final state.
    */
   public boolean isFinalState();

   /**
    * Returns <code>true</code> if and only if the process completed normally.
    */
   public boolean isNormalCompletion();

   /**
    * Notifies the process that an executable item threw an exception.
    *
    * @param aExecutable
    * @param aThrowable
    */
   public void handleExecutableItemException(IAeExecutableQueueItem aExecutable, Throwable aThrowable) throws AeBusinessProcessException;
   
   /**
    * Returns the process initiator (such as the username token or principal token).
    * @return initiator name or DEFAULT_INITIATOR ('anonymous') if not given.
    */
   public String getProcessInitiator();
}
