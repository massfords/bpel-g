// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeBusinessProcessEngine.java,v 1.61 2008/02/21 19:57:57 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import java.util.Date;
import java.util.Map;

import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.impl.AeConflictingRequestException;
import org.activebpel.rt.bpel.impl.AeCorrelationViolationException;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.activebpel.wsio.AeWebServiceAttachment;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Document;

/** Describes the interface used for interacting with a business process engine */
public interface IAeBusinessProcessEngine
{
   /** Monitor status indicating engine is running normal */
   public static final int MONITOR_NORMAL  = 0; 
   /** Monitor status indicating engine is running with one or more monitor warnings */
   public static final int MONITOR_WARNING = 1; 
   /** Monitor status indicating engine is running with one or more error warnings */
   public static final int MONITOR_ERROR   = 2; 
   
   /**
    * Sets the monitor health status of the engine
    * @param aStatus The status to be set (MONITOR_NORMAL, MONITOR_WARNING, MONITOR_ERROR)
    */
   public void setMonitorStatus(int aStatus);
   
   /**
    * Returns the monitor health status of the engine 
    */
   public int getMonitorStatus();
   
   /**
    * Return the partner role endpoint reference for the partnerLink 
    * identified by the path or null if none is found.
    * @param aPid
    * @param aPartnerLinkPath
    * @throws AeBusinessProcessException
    */
   public IAeEndpointReference getPartnerRoleEndpointReference( long aPid, String aPartnerLinkPath ) 
   throws AeBusinessProcessException;
   
   /**
    * Set the variable data for the given suspended process and variable path.
    * @param aPid
    * @param aVariablePath
    * @param aVariableData
    * @throws AeBusinessProcessException
    */
   public void setVariableData( long aPid, String aVariablePath, Document aVariableData )
   throws AeBusinessProcessException;
  
   /**
    * Add the variable attachment for the given suspended process and variable path.
    * @param aPid
    * @param aVariablePath
    * @param aWsioAttachment
    * @throws AeBusinessProcessException
    */
   public IAeAttachmentItem addVariableAttachment( long aPid, String aVariablePath, AeWebServiceAttachment aWsioAttachment )
   throws AeBusinessProcessException;
   
  /**
   * Remove the variable attachments for the given suspended process and variable path.
   * @param aPid
   * @param aVariablePath
   * @param aAttachmentItemNumbers
   * @throws AeBusinessProcessException
   */
  public void removeVariableAttachments( long aPid, String aVariablePath, int[] aAttachmentItemNumbers )
  throws AeBusinessProcessException;

   
   /**
    * Returns the correlation data for the given process and correlation path.
    * @param aPid
    * @param aCorrsetPath
    * @throws AeBusinessProcessException
    */
   public Map getCorrelationData( long aPid, String aCorrsetPath)
   throws AeBusinessProcessException;
   
   /**
    * Set the correlation data for the given suspended process and correlation path.
    * @param aPid
    * @param aCorrsetPath
    * @param aCorrelationData
    * @throws AeBusinessProcessException
    */
   public void setCorrelationData( long aPid, String aCorrsetPath, Map aCorrelationData )
   throws AeBusinessProcessException;
   
   /**
    * Set the endpoint ref data for a partnerRole partner link.
    * @param aPid
    * @param aIsPartnerRole
    * @param aPartnerLinkPath
    * @param aPartnerEndpointRef
    * @throws AeBusinessProcessException
    */
   public void setPartnerLinkData( long aPid, boolean aIsPartnerRole, String aPartnerLinkPath, Document aPartnerEndpointRef )
   throws AeBusinessProcessException;
   
   /**
    * Suspends the business process identified by the passed pid.
    * @param aPid The process id of the process to suspend.
    * @throws AeBusinessProcessException
    */
   public void suspendProcess(long aPid) throws AeBusinessProcessException;

   /**
    * Resumes the business process identified by the passed pid.
    * @param aPid The process id of the process to resume.
    * @throws AeBusinessProcessException
    */
   public void resumeProcess(long aPid) throws AeBusinessProcessException;

   /**
    * Resumes the business process identified by the passed pid for the passed
    * suspended location.
    * @param aPid The process id of the process to resume.
    * @param aLocation A location path for a suspended bpel object.
    * @throws AeBusinessProcessException
    */
   public void resumeProcessObject(long aPid, String aLocation) throws AeBusinessProcessException;
   
   /**
    * Step resumes the business process identified by the passed pid. The activity
    * identified by the location arg (or its enclosing scope) will be re-executed.
    * @param aPid The process id of the process to resume.
    * @param aLocation A location path for a suspended bpel object.
    * @throws AeBusinessProcessException
    */
   public void retryActivity( long aPid, String aLocation, boolean aAtScope )
   throws AeBusinessProcessException;
   
   /**
    * Step resumes the business process identified by the passed pid. The activity
    * identified by the location arg (or its enclosing scope) will be treated as
    * if it had completed normally.  
    * It is up to the use to ensure that the process is a "good state" before
    * executing this method. 
    * @param aPid
    * @param aLocation
    * @throws AeBusinessProcessException
    */
   public void completeActivity( long aPid, String aLocation )
   throws AeBusinessProcessException;
   

   /**
    * Terminates the business process identified by the passed pid.
    * @param aPid The process id of the process to terminate.
    * @throws AeBusinessProcessException
    */
   public void terminateProcess(long aPid) throws AeBusinessProcessException;

   /**
    * Add a listener for engine notification events.
    * @param aListener The listener to be added
    */
   public void addEngineListener(IAeEngineListener aListener);
   
   /**
    * Remove the given listener from receiving engine notification events.
    * @param aListener The listener to be added
    */
   public void removeEngineListener(IAeEngineListener aListener);

   /**
    * Add a listener for monitor notification events.
    * @param aListener The listener to be added
    */
   public void addMonitorListener(IAeMonitorListener aListener);
   
   /**
    * Remove the given listener from receiving monitor notification events.
    * @param aListener The listener to be removed
    */
   public void removeMonitorListener(IAeMonitorListener aListener);
   
   /**
    * Add a listener to those notified of process events for all executing processes.
    * @param aListener The listener instance to add.
    */
   public void addProcessListener(IAeProcessListener aListener);

   /**
    * Add a listener to those notified of process events for the given process ID.
    * @param aListener The listener instance to add.
    * @param aPid the process id we are being installed for
    */
   public void addProcessListener(IAeProcessListener aListener, long aPid);

   /**
    * Removes the passed listener from list of those notified of process events
    * for all executing processes.
    * @param aListener The listener instance to remove.
    */
   public void removeProcessListener(IAeProcessListener aListener);

   /**
    * Removes the passed listener from list of those notified of process events
    * for the given process ID.
    * @param aListener The listener instance to remove.
    * @param aPid the process id we are being removed for
    */
   public void removeProcessListener(IAeProcessListener aListener, long aPid);

   /**
    * Accepts an inbound message which will either create a new process instance or get dispatched to
    * an existing message receiver from an already executing process.
    * If a new process is created, it will be queued for execution.
    *  
    * @param aProcessPlan
    * @param aMessageData
    * @param aReply
    * @param aContext
    * @return the PID the receive was routed to
    * @throws AeBusinessProcessException
    */
   public long queueReceiveData(IAeProcessPlan aProcessPlan, IAeMessageData aMessageData, IAeReplyReceiver aReply, IAeMessageContext aContext)
      throws AeBusinessProcessException;

   /**
    * Accepts an inbound message which will either create a new process instance or get dispatched to
    * an existing message receiver from an already executing process.  
    * If a new process was created, then this method returns the newly created process id
    * otherwise, returns <code>IAeBusinessProcess.NULL_PROCESS_ID</code>.
    * 
    * If the <code>aQueueForExecution</code> was false, then the newly created process is not
    * queued for execution.
    *  
    * @param aInboundReceive
    * @param aAckCallback message acknowledgement callback.
    * @param aQueueForExecution if true and if a process was created, the process is queued for execution.
    * @throws AeCorrelationViolationException
    * @throws AeConflictingRequestException
    * @throws AeBusinessProcessException
    * @return If a process was created, then the process id is returned, else returns <code>IAeBusinessProcess.NULL_PROCESS_ID</code>.
    */
   public long queueReceiveData(AeInboundReceive aInboundReceive, IAeMessageAcknowledgeCallback aAckCallback, boolean aQueueForExecution)
         throws AeCorrelationViolationException, AeConflictingRequestException, AeBusinessProcessException;

   /**
    * Primary interface for dispatching messages from web services.
    * 
    * @param aContext Provides contextual information about what process and operation are being targeted.
    * @param aData The data for the operation being invoked
    * @throws AeBusinessProcessException
    * @throws AeTimeoutException
    */
   public IAeWebServiceResponse queueReceiveData(IAeMessageContext aContext, IAeWebServiceMessageData aData) 
      throws AeBusinessProcessException;
   
   /**
    * Specialized doc-literal invocation method for the engine. At a minimum, the user must pass
    * a context that contains the service name for the invoke and the engine will determine the
    * process, partnerlink, and operation by using the data to find the matching signature
    * for the port type.
    * 
    * @param aContext Provides contextual information about what process and operation are being targeted.
    * @param aDataArray The data for the operation being invoked
    * @throws AeTimeoutException
    * @throws AeBusinessProcessException
    */
   public IAeWebServiceResponse queueReceiveData(IAeMessageContext aContext, Document[] aDataArray) throws AeBusinessProcessException;
   
   /**
    * Allows an externally invoked operation data to dispatch to a queued invoke.
    * @param aProcessId The id of the process expecting the response from the invoke
    * @param aLocationPath The path to the location awaiting the response
    * @param aTransmissionId invoke activity's transmission id.
    * @param aMessageData The data we have received from invoke.
    * @param aProcessProperties Any string name/value pairs we received back from the invoke.
    * @throws AeBusinessProcessException Thrown if error occurs setting the receiver.
    */
   public void queueInvokeData(long aProcessId, String aLocationPath, long aTransmissionId, IAeMessageData aMessageData, Map aProcessProperties)
         throws AeBusinessProcessException;
   
   /**
    * Allows an externally invoked operation data to dispatch to a queued invoke.
    * @param aProcessId The id of the process expecting the response from the invoke
    * @param aLocationPath The path to the location awaiting the response
    * @param aTransmissionId invoke activity's transmission id.  
    * @param aMessageData The data we have received from invoke.
    * @param aProcessProperties Any string name/value pairs we received back from the invoke.
    * @param aAckCallback optional callback to acknowledge the receipt of data.
    * @throws AeBusinessProcessException Thrown if error occurs setting the receiver.
    */
   public void queueInvokeData(long aProcessId, String aLocationPath, long aTransmissionId, IAeMessageData aMessageData, Map aProcessProperties, IAeMessageAcknowledgeCallback aAckCallback)
         throws AeBusinessProcessException;


   /**
    * Allows an externally invoked operation fault to dispatch to a queued invoke.
    * @param aProcessId The process that's expecting the invoke response
    * @param aLocationPath The path to the location awaiting the response
    * @param aTransmissionId invoke activity's transmission id. 
    * @param aFault The fault we received from invoke.
    * @param aProcessProperties Any string name/value pairs we received back from the invoke.
    * @throws AeBusinessProcessException Thrown if error occurs setting the receiver.
    */   
   public void queueInvokeFault(long aProcessId, String aLocationPath, long aTransmissionId, IAeFault aFault, Map aProcessProperties)
      throws AeBusinessProcessException;
   
   /**
    * Allows an externally invoked operation fault to dispatch to a queued invoke.
    * @param aProcessId The process that's expecting the invoke response
    * @param aLocationPath The path to the location awaiting the response
    * @param aTransmissionId invoke activity's transmission id.
    * @param aFault The fault we received from invoke.
    * @param aProcessProperties Any string name/value pairs we received back from the invoke.
    * @param aAckCallback optional callback to acknowledge the receipt of data.
    * @throws AeBusinessProcessException Thrown if error occurs setting the receiver.
    */   
   public void queueInvokeFault(long aProcessId, String aLocationPath, long aTransmissionId, IAeFault aFault, Map aProcessProperties, IAeMessageAcknowledgeCallback aAckCallback)
      throws AeBusinessProcessException;
   
   /**
    * The engine configuration.
    * @return IAeEngineConfiguration The engine configuration for this engine instance.
    */
   public IAeEngineConfiguration getEngineConfiguration();
   
   /**
    * Gets the date/time that the engine started.
    */
   public Date getStartDate();

   /**
    * Gets the serialized state of a process, by ID.
    * @param aProcessId ID of the process.
    * @return Document containing the XML serialized state of the process.
    * @throws AeBusinessProcessException
    */
   public Document getProcessState( long aProcessId ) throws AeBusinessProcessException;

   /**
    * Gets the serialized variable for a process, by ID and location.
    * @param aProcessId ID of the process.
    * @param aLocationPath The location XPath for the variable's enclosing scope (?)
    * @return Document containing the XML serialized variable data.
    * @throws AeBusinessProcessException
    */
   public Document getProcessVariable( long aProcessId, String aLocationPath ) throws AeBusinessProcessException;

   /**
    * Returns the type mapping helper for the engine for schema to java and back.
    */
   public AeTypeMapping getTypeMapping();

   /**
    * Returns <code>true</code> if and only if the process with the given
    * process id is restartable.
    *
    * @param aProcessId The process id of the process to restart.
    * @throws AeBusinessProcessException
    */
   public boolean isRestartable(long aProcessId) throws AeBusinessProcessException;

   /**
    * Restarts the business process identified by the given process id.
    *
    * @param aProcessId The process id of the process to restart.
    * @throws AeBusinessProcessException
    */
   public void restartProcess(long aProcessId) throws AeBusinessProcessException;
}
