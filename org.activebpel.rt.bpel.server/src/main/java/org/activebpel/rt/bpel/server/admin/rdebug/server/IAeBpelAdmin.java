// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/server/IAeBpelAdmin.java,v 1.16 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.rdebug.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.wsio.AeWebServiceAttachment;

/** 
 * Describes the interface used for interacting with a business process engine 
 */
public interface IAeBpelAdmin extends Remote
{
   /** The API version for the BPEL administration API. */
   public static final String CURRENT_API_VERSION = "4.2";    //$NON-NLS-1$
   
   /**
    * Gets the current engine configuration as xml. This is intended to be used 
    * as a debugging and testing tool. The structure of the xml is subject to change 
    * without notice so don't rely on it to be consistent across versions.
    * Finer grain access to configuration properties may be forthcoming in another
    * part of the api.
    * 
    * @return xml string representing the engine config
    * @throws RemoteException
    * @throws AeException
    */
   public String getConfiguration() throws RemoteException, AeException;
   
   /**
    * Setter for the engine config. Accepts an xml string that will be parsed into
    * a configuration object and update the current engine config. Note: this string
    * must contain ALL of the config properties.
    * 
    * @param aXmlString
    * @throws RemoteException
    * @throws AeException
    */
   public void setConfiguration(String aXmlString) throws RemoteException, AeException;

   /**
    * Suspends the business process identified by the passed pid.
    * @param aPid The process id of the process to suspend.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void suspendProcess(long aPid) throws RemoteException, AeBusinessProcessException;

   /**
    * Resumes the business process identified by the passed pid.
    * @param aPid The process id of the process to resume.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void resumeProcess(long aPid) throws RemoteException, AeBusinessProcessException;

   /**
    * Resumes the business process identified by the passed pid for the passed 
    * suspended location.
    * @param aPid The process id of the process to resume.
    * @param aLocation A location path for a suspended bpel object.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void resumeProcessObject(long aPid, String aLocation) throws RemoteException, AeBusinessProcessException;

   /**
    * Restarts the business process identified by the passed pid.
    *
    * @param aPid The process id of the process to resume.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void restartProcess(long aPid) throws RemoteException, AeBusinessProcessException;

   /**
    * Terminates the business process identified by the passed pid.
    * @param aPid The process id of the process to terminate.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void terminateProcess(long aPid) throws RemoteException, AeBusinessProcessException;

   /**
    * Add a listener for engine notification events.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener 
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void addEngineListener(long aContextId, String aEndpointURL) throws RemoteException, AeBusinessProcessException;

   /**
    * Add a listener for engine breakpoint notification events.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener
    * @param aBreakpointList The list of breakpoints. 
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void addBreakpointListener(long aContextId, String aEndpointURL, AeBreakpointList aBreakpointList ) throws RemoteException, AeBusinessProcessException;
   
   /**
    * Remove a listener for engine breakpoint notification events.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void removeBreakpointListener(long aContextId, String aEndpointURL) throws RemoteException, AeBusinessProcessException ;

   /**
    * Update the list of breakpoints defined by the user for remote debug.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener
    * @param aBreakpointList The list of breakpoints. 
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void updateBreakpointList(long aContextId, String aEndpointURL, AeBreakpointList aBreakpointList ) throws RemoteException, AeBusinessProcessException;

   /**
    * Remove the given listener from receiving engine notification events.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener 
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void removeEngineListener(long aContextId, String aEndpointURL) throws RemoteException, AeBusinessProcessException;

   /**
    * Add a listener to those notified of process events for the given process ID.
    * @param aContextId the context id used to locate the callback
    * @param aPid the process id we are being installed for
    * @param aEndpointURL The endpoint reference of the listener 
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void addProcessListener(long aContextId, long aPid, String aEndpointURL) throws RemoteException, AeBusinessProcessException;

   /**
    * Removes the passed listener from list of those notified of process events
    * for the given process ID.
    * @param aContextId the context id used to locate the callback
    * @param aPid the process id we are being removed for
    * @param aEndpointURL The endpoint reference of the listener 
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void removeProcessListener(long aContextId, long aPid, String aEndpointURL) throws RemoteException, AeBusinessProcessException;
   
   /**
    * Returns a list of processes currently running on the BPEL engine. This
    * list may be optionally filtered by passing a filter object.
    * @return AeProcessListResult
    * @param aFilter the process filter to use when obtaining the result set.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public AeProcessListResult getProcessList(AeProcessFilter aFilter) throws RemoteException, AeBusinessProcessException; 

   /**
    * Returns the process detail for the given process id or null if the process does
    * not exist on the server.
    * @return AeProcessInstanceDetail
    * @param aPid the process id we are looking for
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public AeProcessInstanceDetail getProcessDetail(long aPid) throws RemoteException, AeBusinessProcessException; 

   /**
    * Returns the state of the process specified by the given process ID.
    * @return String
    * @param aPid the ID of the process we want state information for.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public String getProcessState(long aPid) throws RemoteException, AeBusinessProcessException; 

   /**
    * Returns the message digest code of the deployed BPEL file for the given process.
    * @return byte[]  
    * @param aProcessId 
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public byte [] getProcessDigest(long aProcessId) throws RemoteException, AeBusinessProcessException; 

   /**
    * Returns the process definition (bpel xml) for the given process.
    * @return String
    * @param aProcessId 
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public String getProcessDef(long aProcessId) throws RemoteException, AeBusinessProcessException; 

   /**
    * Returns the process log for the given process, if logging is enabled on the server.
    * @return String  
    * @param aPid the ID of the process we want the log for.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public String getProcessLog(long aPid) throws RemoteException, AeBusinessProcessException; 

   /**
    * Returns the data for the variable being referenced by the variable path.
    * @return Document.
    * @param aPid the process Id
    * @param aVariablePath the path which uniquely identifies the desired variable 
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public String getVariable(long aPid, String aVariablePath) throws RemoteException, AeBusinessProcessException;

   /**
    * Sets the variable specified by the given variable path in the given process with the given data.
    * @param aPid the process Id
    * @param aVariablePath the path which uniquely identifies the desired variable
    * @param aVariableData the variable data to be set.
    * @return String null is data set successfully on the server, otherwise an error message is returned.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public String setVariable(long aPid, String aVariablePath, String aVariableData) throws RemoteException, AeBusinessProcessException;
   
   /**
    * Adds an attachment to the  variable specified by the given variable path in the given process with the given data.
    * @param aPid the process Id
    * @param aVariablePath the path which uniquely identifies the desired variable
    * @param aWsioAttachment 
    * @return AeAddAttachmentResponse
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see AeWebServiceAttachment
    */
   public AeAddAttachmentResponse addAttachment(long aPid, String aVariablePath, AeWebServiceAttachment aWsioAttachment) throws RemoteException, AeBusinessProcessException;
   
   /**
    * Adds an attachment to the  variable specified by the given variable path in the given process with the given data.
    * @param aPid the process Id
    * @param aVariablePath the path which uniquely identifies the desired variable
    * @param aAttachmentItemNumbers
    * @return String null is data set successfully on the server, otherwise an error message is returned.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public String removeAttachments(long aPid, String aVariablePath, int[] aAttachmentItemNumbers) throws RemoteException, AeBusinessProcessException;
   
   /**
    * Returns the API version for the BPEL administration service.
    * @return String
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public String getAPIVersion() throws RemoteException, AeBusinessProcessException;
   
   /**
    * Deploys a BPR file.  The parameter to this method is the full contents of the BPR
    * file base64 encoded.  This method returns a summary of the deployment as a serialized
    * XML document.
    * 
    * @param aBprFilename
    * @param aBase64File
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public String deployBpr(String aBprFilename, String aBase64File) throws RemoteException, AeBusinessProcessException;
   
   /**
    * Sets the partner link data for the given process id and location path. 
    * @param aPid the process id we are looking for
    * @param aIsPartnerRole true if partner role being set, false for myrole
    * @param aLocationPath the location path to the partner link
    * @param aData the data to be set
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void setPartnerLinkData(long aPid, boolean aIsPartnerRole, String aLocationPath, String aData) throws RemoteException, AeBusinessProcessException;
   
   /**
    * Sets the correlation set data for the given process id and location path.
    * @param aPid the process id we are looking for
    * @param aLocationPath the location path to the correlation set
    * @param aData the data to be set
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void setCorrelationSetData(long aPid, String aLocationPath, String aData) throws RemoteException, AeBusinessProcessException;

   /**
    * Retry the activity associated with the passed location path or its enclosing scope.
    * @param aPid the process Id.
    * @param aLocationPath the path which uniquely identifies the desired activity.
    * @param aAtScope flag indicating if the enclosing scope is to be retried.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void retryActivity(long aPid, String aLocationPath, boolean aAtScope) throws RemoteException, AeBusinessProcessException;
   
   /**
    * Step resume the process and mark the activity associated with passed location path as complete.  
    * @param aPid the process Id.
    * @param aLocationPath the path which uniquely identifies the desired activity.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public void completeActivity(long aPid,  String aLocationPath) throws RemoteException, AeBusinessProcessException;

   /**
    * Returns True is using the native ActiveBPEL WorkManager and False if using a server provide WorkManager.
    * @throws RemoteException
    * @throws AeBusinessProcessException
    */
   public boolean isInternalWorkManager() throws RemoteException, AeBusinessProcessException;
}