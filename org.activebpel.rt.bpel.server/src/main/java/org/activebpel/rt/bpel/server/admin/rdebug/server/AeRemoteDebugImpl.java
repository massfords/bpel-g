// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/server/AeRemoteDebugImpl.java,v 1.37 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.rdebug.server;

import commonj.work.Work;
import commonj.work.WorkItem;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.base64.BASE64Decoder;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEngineAlert;
import org.activebpel.rt.bpel.IAeEngineEvent;
import org.activebpel.rt.bpel.IAeEngineListener;
import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.IAeProcessListener;
import org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration;
import org.activebpel.rt.bpel.def.util.AeLocationPathUtils;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandler;
import org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandlerService;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSummary;
import org.activebpel.rt.bpel.server.deploy.bpr.AeTempFileUploadHandler;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.AeStructuredDeploymentLog;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.AeXMLToSimpleString;
import org.activebpel.work.AeAbstractWork;
import org.activebpel.wsio.AeWebServiceAttachment;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Remote implementation of the BPEL Engine used by Web Service invocation.
 */
public class AeRemoteDebugImpl implements IAeBpelAdmin
{   
   /** locator for the event handler service routine for BPEL engine events. */ 
   private static String sEventHandlerLocator;
   
   /** Map of context Id to engine listener used for dispatching remote debug events. */
   private static Hashtable sEngineListeners = new Hashtable();
   
   /** Map of context Id to process listener used for dispatching remote debug events. */
   private static Hashtable sProcessListeners = new Hashtable();

   /** Map of context Id to breakpoint listener used for dispatching remote debug events. */
   private static Hashtable sBreakpointListeners = new Hashtable();

   /**
    * Constructor for remote engine implementation.
    *  
    * @param aEventHandlerLocator class name of locator for the event handler service routine for BPEL engine events. 
    */
   public AeRemoteDebugImpl(String aEventHandlerLocator)
   {
      sEventHandlerLocator = aEventHandlerLocator;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getConfiguration()
    */
   public String getConfiguration() throws RemoteException, AeException
   {
      AeDefaultEngineConfiguration config = (AeDefaultEngineConfiguration) AeEngineFactory.getEngineConfig();
      StringWriter sw = new StringWriter();
      config.save(sw);
      return sw.toString();
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#setConfiguration(java.lang.String)
    */
   public void setConfiguration(String aXmlString) throws RemoteException, AeException
   {
      AeDefaultEngineConfiguration config = (AeDefaultEngineConfiguration) AeEngineFactory.getEngineConfig();
      AeDefaultEngineConfiguration.loadConfig(config, AeUTF8Util.getInputStream(aXmlString), null);
      config.update();
   }

   /**
    * Note this implementation will schhedule the suspend and wait up to 30 seconds for
    * the suspend to actually happen, this avoid deadlocks under rare occasions.
    * But under normal circumstances the call will appear synchronous to client.
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#suspendProcess(long)
    */
   public void suspendProcess(long aPid) throws RemoteException, AeBusinessProcessException
   {
      try
      {
         Work work = new AeRemoteSuspendWork(aPid);
         WorkItem workItem = AeEngineFactory.getWorkManager().schedule(work) ;
         // wait for work to finish as suspend should be synchronous
         // but schedule it to avoid any potential deadlocks 30 seconds 
         // should be more than enough time to wait for completion
         AeEngineFactory.getWorkManager().waitForAny(Collections.singleton(workItem), 30000);
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeRemoteDebugImpl.ERROR_0",new Long(aPid)), e);  //$NON-NLS-1$
      }
   }

   /**
    * Schedules process resume.
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#resumeProcess(long)
    */
   public void resumeProcess(long aPid) throws RemoteException, AeBusinessProcessException
   {
      try
      {
         Work work = new AeRemoteResumeWork(aPid);
         AeEngineFactory.getWorkManager().schedule(work) ;
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeRemoteDebugImpl.ERROR_1",new Long(aPid)), e);  //$NON-NLS-1$
      }
   }

   /**
    * Schedules process resume object.
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#resumeProcessObject(long, java.lang.String)
    */
   public void resumeProcessObject(long aPid, String aLocation) throws RemoteException, AeBusinessProcessException
   {
      try
      {
         Work work = new AeRemoteResumeObjectWork(aPid, aLocation);
         AeEngineFactory.getWorkManager().schedule(work) ;
      }
      catch (Exception e)
      {
         Object[] args = new Object[2];
         args[0] = new Long(aPid);
         args[1] = aLocation;
         throw new AeBusinessProcessException(AeMessages.format("AeRemoteDebugImpl.ERROR_2", args), e);  //$NON-NLS-1$ 
      }
   }

   /**
    * Schedules process restart.
    *
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#restartProcess(long)
    */
   public void restartProcess(long aPid) throws RemoteException, AeBusinessProcessException
   {
      try
      {
         Work work = new AeRemoteRestartWork(aPid);
         AeEngineFactory.getWorkManager().schedule(work) ;
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeRemoteDebugImpl.ERROR_3", String.valueOf(aPid)), e);  //$NON-NLS-1$
      }
   }

   /**
    * Schedules process terminate.
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#terminateProcess(long)
    */
   public void terminateProcess(long aPid) throws RemoteException, AeBusinessProcessException
   {
      try
      {
         Work work = new AeRemoteTerminateWork(aPid);
         AeEngineFactory.getWorkManager().schedule(work) ;
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeRemoteDebugImpl.ERROR_4",new Long(aPid)), e);  //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#addEngineListener(long, java.lang.String)
    */
   public void addEngineListener(long aContextId, String aEndpointURL) throws RemoteException
   {
      doAddEngineListener(aContextId, aEndpointURL);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#addBreakpointListener(long, java.lang.String, org.activebpel.rt.bpel.server.admin.rdebug.server.AeBreakpointList)
    */
   public void addBreakpointListener(long aContextId, String aEndpointURL, AeBreakpointList aBreakpointList) throws RemoteException
   {
      doAddBreakpointListener(aContextId, aEndpointURL, aBreakpointList);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#updateBreakpointList(long, java.lang.String, org.activebpel.rt.bpel.server.admin.rdebug.server.AeBreakpointList)
    */
   public void updateBreakpointList( long aContextId, String aEndpointURL, AeBreakpointList aBreakpointList )
      throws RemoteException
   {
      doUpdateBreakpointList(aContextId, aEndpointURL, aBreakpointList);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#removeBreakpointListener(long, java.lang.String)
    */
   public void removeBreakpointListener(long aContextId, String aEndpointURL) throws RemoteException
   {
      doRemoveBreakpointListener(aContextId, aEndpointURL);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#removeEngineListener(long, java.lang.String)
    */
   public void removeEngineListener(long aContextId, String aEndpointURL) throws RemoteException
   {
      doRemoveEngineListener(aContextId, aEndpointURL);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#addProcessListener(long, long, java.lang.String)
    */
   public void addProcessListener(long aContextId, long aPid, String aEndpointURL) throws RemoteException
   {
      doAddProcessListener(aContextId, aPid, aEndpointURL);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#removeProcessListener(long, long, java.lang.String)
    */
   public void removeProcessListener(long aContextId, long aPid, String aEndpointURL) throws RemoteException
   {
      doRemoveProcessListener(aContextId, aPid, aEndpointURL);   
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessList(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public AeProcessListResult getProcessList(AeProcessFilter aFilter) throws RemoteException
   {
      try
      {
         return AeEngineFactory.getEngineAdministration().getProcessList(aFilter);
      }
      catch (AeBusinessProcessException e)
      {
         throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_5"), e); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessDetail(long)
    */
   public AeProcessInstanceDetail getProcessDetail(long aPid) throws RemoteException
   {
      return AeEngineFactory.getEngineAdministration().getProcessDetail(aPid);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessState(long)
    */
   public String getProcessState(long aPid) throws RemoteException
   {
      try
      {
         Document doc = AeEngineFactory.getEngine().getProcessState(aPid);
         return AeXMLParserBase.documentToString(doc, true);
      }
      catch (AeBusinessProcessException e)
      {
         throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_6"), e); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessDigest(long)
    */
   public byte [] getProcessDigest(long aProcessId) throws RemoteException
   {
      try
      {
         // First find the deployment plan for the process name
         IAeProcessDeployment deployment = findDeploymentPlan(aProcessId);
         // Get the input stream for the process, so we can get the MD5 code
         String input = deployment.getBpelSource();
         // Get the message digest for the core XML
         String serverXML = AeXMLToSimpleString.extractCoreXML(new InputSource(new StringReader(input)), true);
         return AeUtil.getMessageDigest( AeUTF8Util.getInputStream( serverXML ) );
      } 
      catch (Exception e)
      {
         throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_7"), e); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessDef(long)
    */
   public String getProcessDef(long aProcessId) throws RemoteException
   { 
      try
      {
         // Find the process plan which contains the process definition
         IAeProcessDeployment plan = findDeploymentPlan(aProcessId);
         return plan.getBpelSource();
      }
      catch(Exception e)
      {
         throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_8"), e); //$NON-NLS-1$
      }
   }

   /**
    * Returns the deployment plan for the given process id.
    *
    * @param aProcessId
    * @throws AeBusinessProcessException
    */
   protected IAeProcessDeployment findDeploymentPlan(long aProcessId) throws AeBusinessProcessException
   {
      QName processName = AeEngineFactory.getEngine().getProcessManager().getProcessQName(aProcessId);
      IAeProcessDeployment deployment = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(aProcessId, processName);
      return deployment;
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessLog(long)
    */
   public String getProcessLog(long aPid) throws RemoteException
   { 
      return AeEngineFactory.getEngineAdministration().getProcessLog(aPid);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getVariable(long, java.lang.String)
    */
   public String getVariable(long aPid, String aVariablePath) throws RemoteException
   {
      try
      {
         Document doc = AeEngineFactory.getEngine().getProcessVariable(aPid, aVariablePath);
         return AeXMLParserBase.documentToString(doc, true);
      }
      catch (AeBusinessProcessException e)
      {
         throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_9"), e); //$NON-NLS-1$
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#setVariable(long, java.lang.String, java.lang.String)
    */
   public String setVariable(long aPid, String aVariablePath, String aVariableData) throws RemoteException
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase();
         parser.setValidating( false );
         parser.setNamespaceAware( true );
         String xml = aVariableData;
         Document doc = parser.loadDocument( new StringReader(xml), null );
         
         AeEngineFactory.getEngine().setVariableData(aPid, aVariablePath, doc);
         return "";  // success indicator //$NON-NLS-1$
      }
      catch (Throwable e)
      {
         throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_SettingVariable"), e); //$NON-NLS-1$
      }
   }  
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#addAttachment(long, java.lang.String, org.activebpel.wsio.AeWebServiceAttachment)
    */
   public AeAddAttachmentResponse addAttachment(long aPid, String aVariablePath, AeWebServiceAttachment aWsioAttachment) throws RemoteException, AeBusinessProcessException
   {
      try
      {
         IAeAttachmentItem addedItem =  AeEngineFactory.getEngine().addVariableAttachment(aPid, aVariablePath, aWsioAttachment);
        
         AeAddAttachmentResponse response =  new AeAddAttachmentResponse();
         response.setAttachmentId(addedItem.getAttachmentId());
         response.setAttachmentAttributes(convertAttachmentAttributes(addedItem.getHeaders()));
         return response;
        
      }
      catch (Throwable e)
      {
         throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_AddingAttachment"), e); //$NON-NLS-1$
      }
   }
   
   
   /**
    * Converts attachment attribute map to the EngineAdmin API
    * @param aMap
    * @return AeAttachmentAttributeList
    */
   protected static AeAttachmentAttributeList convertAttachmentAttributes(Map aMap)
   {
      // Convert attribute map to AeAttachmentAttributeList
      AeAttachmentAttribute tgtAttributes[] = new AeAttachmentAttribute[aMap.size()];
      int i = 0;
      for(Iterator itr = aMap.entrySet().iterator();itr.hasNext();i++)
      {
         Map.Entry instance = (Map.Entry)itr.next();
         tgtAttributes[i] = new AeAttachmentAttribute();
         tgtAttributes[i].setAttributeName((String)instance.getKey());
         tgtAttributes[i].setAttributeValue((String)instance.getValue());
      }
      AeAttachmentAttributeList attributes = new AeAttachmentAttributeList();
      attributes.setAttributeName(tgtAttributes);
     
      return attributes;  
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#removeAttachments(long, java.lang.String, int[])
    */
   public String removeAttachments(long aPid, String aVariablePath, int[] aAttachmentItemNumbers) throws RemoteException, AeBusinessProcessException
   {
      AeEngineFactory.getEngine().removeVariableAttachments(aPid, aVariablePath, aAttachmentItemNumbers );
      return ""; //$NON-NLS-1$
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getAPIVersion()
    */
   public String getAPIVersion() throws RemoteException
   { 
      return IAeBpelAdmin.CURRENT_API_VERSION;
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#deployBpr(java.lang.String, java.lang.String)
    */
   public String deployBpr(String aBprFilename, String aBase64File) throws RemoteException
   {
      try
      {
         // Decode the base64 input
         BASE64Decoder decoder = new BASE64Decoder();
         byte [] buffer = decoder.decodeBuffer(aBase64File);
         ByteArrayInputStream in = new ByteArrayInputStream( buffer );
         AeStructuredDeploymentLog logger = createDeploymentLogger();
         AeTempFileUploadHandler.handleUpload( aBprFilename, in, logger );
         IAeDeploymentSummary summary = logger.getDeploymentSummary();
         Document dom = summary.toDocument();
         return AeXMLParserBase.documentToString(dom.getDocumentElement());
      }
      catch (RemoteException re)
      {
         throw re;
      }
      catch (Throwable t)
      {
         throw new RemoteException(t.getLocalizedMessage());
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#setPartnerLinkData(long, boolean, java.lang.String, java.lang.String)
    */
   public void setPartnerLinkData(long aPid, boolean aIsPartnerRole, String aLocationPath, String aData) throws RemoteException
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase(true, false);
         Document doc = parser.loadDocumentFromString(aData, null);
         
         AeEngineFactory.getEngine().setPartnerLinkData(aPid, aIsPartnerRole, aLocationPath, doc);
      }
      catch (AeException e)
      {
         throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_10"), e); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#setCorrelationSetData(long, java.lang.String, java.lang.String)
    */
   public void setCorrelationSetData(long aPid, String aLocationPath, String aData) throws RemoteException
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase();         
         Document doc = parser.loadDocumentFromString("<CorrSetProps>" + aData + "</CorrSetProps>", null); //$NON-NLS-1$//$NON-NLS-2$
         
         Map corrSetData = AeCorrelationSet.convertCorrSetDataToMap(doc);
         if (corrSetData.size() > 0)
            AeEngineFactory.getEngine().setCorrelationData(aPid, aLocationPath, corrSetData);
      }
      catch (AeException e)
      {
         throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_11"), e); //$NON-NLS-1$
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#retryActivity(long, java.lang.String, boolean)
    */
   public void retryActivity(long aPid, String aLocationPath, boolean aAtScope) throws RemoteException, AeBusinessProcessException
   {
      AeEngineFactory.getEngine().retryActivity(aPid, aLocationPath, aAtScope);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#completeActivity(long, java.lang.String)
    */
   public void completeActivity(long aPid, String aLocationPath) throws RemoteException, AeBusinessProcessException
   {
      AeEngineFactory.getEngine().completeActivity(aPid, aLocationPath);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#isInternalWorkManager()
    */
   public boolean isInternalWorkManager() throws RemoteException
   { 
      return AeEngineFactory.isInternalWorkManager();
   }
   
   /**
    * Creates the deployment logger to use.
    */
   protected AeStructuredDeploymentLog createDeploymentLogger()
   {
      return new AeStructuredDeploymentLog();
   }

   // STATIC METHODS
   //
   
   /**
    * Returns the key to use for locating listeners given the contextId and endpoint URL. 
    * @param aContextId The context id
    * @param aEndpointURL The URL of the administrative web service
    */
   protected static String getKey(long aContextId, String aEndpointURL)
   {
      return aContextId + aEndpointURL;
   }
   
   /**
    * Add a listener for engine notification events.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener 
    * @throws RemoteException
    */
   public static void doAddEngineListener(long aContextId, String aEndpointURL) throws RemoteException
   {
      // In case this is already registered deregister first
      doRemoveEngineListener(aContextId, aEndpointURL);
      
      // Register new listener
      IAeEngineListener listener = new AeEngineListener(aContextId, aEndpointURL);
      AeEngineFactory.getEngine().addEngineListener(listener);

      sEngineListeners.put(getKey(aContextId, aEndpointURL), listener);
   }

   /**
    * Remove the given listener from receiving engine notification events.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener 
    * @throws RemoteException
    */
   public static void doRemoveEngineListener(long aContextId, String aEndpointURL) throws RemoteException
   {
      IAeEngineListener listener = (IAeEngineListener)sEngineListeners.remove(getKey(aContextId, aEndpointURL));
      if (listener != null)
         AeEngineFactory.getEngine().removeEngineListener(listener);
   }

   /**
    * Add a listener to those notified of process events for the given process ID.
    * @param aContextId the context id used to locate the callback
    * @param aPid the process id we are being installed for
    * @param aEndpointURL The endpoint reference of the listener 
    * @throws RemoteException
    */
   public static void doAddProcessListener(long aContextId, long aPid, String aEndpointURL) throws RemoteException
   {
      // In case this is already registered deregister first
      doRemoveProcessListener(aContextId, aPid, aEndpointURL);

      // Register new listener
      IAeProcessListener listener = new AeProcessListener(aContextId, aPid, aEndpointURL);
      AeEngineFactory.getEngine().addProcessListener(listener, aPid);

      sProcessListeners.put(getKey(aContextId, aEndpointURL), listener);
   }
   
   /**
    * Removes the passed listener from list of those notified of process events
    * for the given process ID.
    * @param aContextId the context id used to locate the callback
    * @param aPid the process id we are being removed for
    * @param aEndpointURL The endpoint reference of the listener 
    * @throws RemoteException
    */
   public static void doRemoveProcessListener(long aContextId, long aPid, String aEndpointURL) throws RemoteException
   {
      IAeProcessListener listener = (IAeProcessListener)sProcessListeners.remove(getKey(aContextId, aEndpointURL));
      if (listener != null)
         AeEngineFactory.getEngine().removeProcessListener(listener, aPid);
   }

   /**
    * Add a listener for engine breakpoint notification events.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener
    * @param aBreakpointList The list of breakpoints. 
    * @throws RemoteException
    */
   public static void doAddBreakpointListener(long aContextId, String aEndpointURL, AeBreakpointList aBreakpointList) 
      throws RemoteException
   {
      // In case this is already registered deregister first
      doRemoveBreakpointListener(aContextId, aEndpointURL);

      // Register new listener
      AeBreakpointListener listener = new AeBreakpointListener(aContextId, aEndpointURL, aBreakpointList);
      AeEngineFactory.getEngine().addProcessListener(listener);

      sBreakpointListeners.put(getKey(aContextId, aEndpointURL), listener);
   }

   /**
    * Remove a listener for engine breakpoint notification events.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener
    * @throws RemoteException
    */
   public static void doRemoveBreakpointListener(long aContextId, String aEndpointURL) throws RemoteException
   {
      AeBreakpointListener listener = (AeBreakpointListener)sBreakpointListeners.remove(getKey(aContextId, aEndpointURL));
      if (listener != null)
         AeEngineFactory.getEngine().removeProcessListener(listener);
   }

   /**
    * Update the list of breakpoints defined by the user for remote debug.
    * @param aContextId the context id used to locate the callback
    * @param aEndpointURL The endpoint reference of the listener
    * @param aBreakpointList The list of breakpoints. 
    */
   public static void doUpdateBreakpointList(long aContextId, String aEndpointURL, AeBreakpointList aBreakpointList)
      throws RemoteException
   {
      AeBreakpointListener listener = (AeBreakpointListener)sBreakpointListeners.get(getKey(aContextId, aEndpointURL));
      if (listener != null)
         listener.updateBreakpointList(aBreakpointList);
   }
   
   /**
    * Returns an instance of the configured event handler service locator class.
    * @param aEndpointURL the endpoint events that will be dispatched through.
    * @return IAeEvenHandlerService
    * @throws AeBusinessProcessException
    */
   protected static IAeEventHandlerService getEventHandlerServiceLocator(String aEndpointURL) 
      throws AeBusinessProcessException
   {
      if ( AeUtil.isNullOrEmpty(sEventHandlerLocator) )
         throw new AeBusinessProcessException(
               AeMessages.getString("AeRemoteDebugImpl.ERROR_12")); //$NON-NLS-1$
      
      try
      {
         Class c = Class.forName(sEventHandlerLocator);
         Constructor constructor = c.getConstructor( new Class[] {String.class} );
         return (IAeEventHandlerService) constructor.newInstance( new Object[] {aEndpointURL} );
      }
      catch (Exception e)
      {
         throw new AeBusinessProcessException( MessageFormat.format(AeMessages.getString("AeRemoteDebugImpl.ERROR_13"), //$NON-NLS-1$
                                                                    new Object[] {sEventHandlerLocator}), e);
      }
   }
   
   /**
    * Base class for our work involving calls into the engine that affect a process. 
    */
   protected static abstract class AeRemoteWork extends AeAbstractWork
   {
      /** Process id to operate on. */
      private long mPid;
      
      /** error message to include along with any raised exception during the execution of the work */
      private String mErrorMessage;
      
      /**
       * Constructor which required process id
       * @param aPid the process id used by work
       * @param aErrorMessage error message to include in addition to a caught exception during execution of work
       */
      public AeRemoteWork(long aPid, String aErrorMessage)
      {
         setPid(aPid);
         mErrorMessage = aErrorMessage;
      }

      /**
       * @see java.lang.Runnable#run()
       */
      public void run()
      {
         try
         {
            doRun();
         }
         catch (AeBusinessProcessException e)
         {
            // All of the subclasses have a placeholder in their error messages
            // for the process id
            Object[] args = new Object[1];
            args[0] = new Long(getPid());
            String errorMessage = MessageFormat.format(getErrorMessage(), args);
            AeBusinessProcessException.logError(e, errorMessage);
         }
      }
      
      /**
       * Getter for the pid.
       */
      protected long getPid()
      {
         return mPid;
      }
      
      /**
       * @param aPid The pid to set.
       */
      protected void setPid(long aPid)
      {
         mPid = aPid;
      }

      /**
       * Getter for the error message token.
       */
      protected String getErrorMessage()
      {
         return mErrorMessage;
      }
      
      /**
       * Subclasses need to override in order to make their call into the engine.
       * 
       * @throws AeBusinessProcessException
       */
      protected abstract void doRun() throws AeBusinessProcessException;
   }
   
   /** Scheduled work for suspending a process. */
   protected static class AeRemoteSuspendWork extends AeRemoteWork
   {
      /**
       * Suspend a process.
       * @param aPid the process id to suspend
       */
      public AeRemoteSuspendWork(long aPid)
      {
         super(aPid, AeMessages.getString("AeRemoteDebugImpl.ERROR_18")); //$NON-NLS-1$
      }

      /**
       * @see org.activebpel.rt.bpel.server.admin.rdebug.server.AeRemoteDebugImpl.AeRemoteWork#doRun()
       */
      protected void doRun() throws AeBusinessProcessException
      {
         AeEngineFactory.getEngine().suspendProcess(getPid());
      }
   }
   
   /** Scheduled work for terminating a process. */
   protected static class AeRemoteTerminateWork extends AeRemoteWork
   {
      /**
       * Suspend a process.
       * @param aPid the process id to suspend
       */
      public AeRemoteTerminateWork(long aPid)
      {
         super(aPid,AeMessages.getString("AeRemoteDebugImpl.ERROR_19")); //$NON-NLS-1$
      }

      /**
       * @see org.activebpel.rt.bpel.server.admin.rdebug.server.AeRemoteDebugImpl.AeRemoteWork#doRun()
       */
      protected void doRun() throws AeBusinessProcessException
      {
         AeEngineFactory.getEngine().terminateProcess(getPid());
      }
   }

   /** Scheduled work for resuming a process. */
   protected static class AeRemoteResumeWork extends AeRemoteWork
   {
      /**
       * Resume a process.
       * @param aPid The process id to resume.
       */
      public AeRemoteResumeWork(long aPid)
      {
         super(aPid, AeMessages.getString("AeRemoteDebugImpl.ERROR_20")); //$NON-NLS-1$
      }

      /**
       * @see org.activebpel.rt.bpel.server.admin.rdebug.server.AeRemoteDebugImpl.AeRemoteWork#doRun()
       */
      protected void doRun() throws AeBusinessProcessException
      {
         AeEngineFactory.getEngine().resumeProcess(getPid());
      }
   }

   /** Scheduled work for resuming an object in a process. */
   protected static class AeRemoteResumeObjectWork extends AeRemoteWork
   {
      /** Path to resume. */
      protected String mPath;
      
      /**
       * Resume a process.
       * @param aPid The process id to resume.
       * @param aPath the path of the object to resume.
       */
      public AeRemoteResumeObjectWork(long aPid, String aPath)
      {
         super(aPid, AeMessages.getString("AeRemoteDebugImpl.ERROR_20")); //$NON-NLS-1$
         mPath = aPath;
      }

      /**
       * @see org.activebpel.rt.bpel.server.admin.rdebug.server.AeRemoteDebugImpl.AeRemoteWork#doRun()
       */
      protected void doRun() throws AeBusinessProcessException
      {
         AeEngineFactory.getEngine().resumeProcessObject(getPid(), mPath);
      }
   }
   
   /** Scheduled work for restarting a process. */
   protected static class AeRemoteRestartWork extends AeRemoteWork
   {
      /**
       * Restart a process.
       *
       * @param aPid The process id to restart.
       */
      public AeRemoteRestartWork(long aPid)
      {
         super(aPid, AeMessages.getString("AeRemoteDebugImpl.ERROR_21")); //$NON-NLS-1$
      }

      /**
       * @see org.activebpel.rt.bpel.server.admin.rdebug.server.AeRemoteDebugImpl.AeRemoteWork#doRun()
       */
      protected void doRun() throws AeBusinessProcessException
      {
         AeEngineFactory.getEngine().restartProcess(getPid());
      }
   }

   /**
    * The process listener stub which will forward events to 
    * the remote event handler. 
    */
   protected static class AeProcessListener implements IAeProcessListener
   {
      /** The context id */
      private long mContextId;

      /** The process id */
      private long mProcessId;

      /** The endpoint URL we are listening on. */
      private String mEndpointURL;
      
      /** The event handler object we will forward our requests to. */
      private IAeEventHandler mHandler;

      /**
       * Contructor for listener which requires the remote context id and
       * endpoint URL to dispatch events to. 
       * @param aContextId the context id of the remote engine
       * @param aProcessId the process Id we are handling events for
       * @param aEndpointURL the endpoint events will be dispatched through
       */
      public AeProcessListener(long aContextId, long aProcessId, String aEndpointURL) throws RemoteException
      {
         try
         {
            mContextId = aContextId;
            mProcessId = aProcessId;         
            mEndpointURL = aEndpointURL;        
            mHandler = AeRemoteDebugImpl.getEventHandlerServiceLocator(aEndpointURL).getRemoteDebugService();
         }
         catch(Exception e)
         {
            throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_22"), e); //$NON-NLS-1$
         }      
      }

      /**
       * Handle an event fired by the BPEL Engine for a process.
       * @param aEvent The event to handle.
       * @return boolean false always returned.
       * @see org.activebpel.rt.bpel.IAeProcessListener#handleProcessEvent(org.activebpel.rt.bpel.IAeProcessEvent)
       */
      public boolean handleProcessEvent(IAeProcessEvent aEvent)
      {
         boolean suspend = false;
         try
         {
            if (mHandler != null)
            {
               suspend = mHandler.processEventHandler(mContextId, aEvent.getPID(), aEvent.getNodePath(), 
                     aEvent.getEventID(), aEvent.getFaultName(), aEvent.getAncillaryInfo(), aEvent.getQName(),
                     aEvent.getTimestamp());
            }
         }
         catch (RemoteException re)
         {
            AeException.logError(re, AeMessages.getString("AeRemoteDebugImpl.ERROR_23")); //$NON-NLS-1$
            
            try
            {
               AeRemoteDebugImpl.doRemoveProcessListener(mContextId, mProcessId, mEndpointURL);
            }
            catch (RemoteException e)
            {
               AeException.logError(e, AeMessages.getString("AeRemoteDebugImpl.ERROR_24")); //$NON-NLS-1$
            }
         }
         return suspend;
      }

      /**
       * @see org.activebpel.rt.bpel.IAeProcessListener#handleProcessInfoEvent(org.activebpel.rt.bpel.IAeProcessInfoEvent)
       */
      public void handleProcessInfoEvent(IAeProcessInfoEvent aEvent)
      {
         try
         {
            if (mHandler != null)
            {
               mHandler.processInfoEventHandler(mContextId, aEvent.getPID(), aEvent.getNodePath(), 
                     aEvent.getEventID(), aEvent.getFaultName(), aEvent.getAncillaryInfo(),
                     aEvent.getTimestamp());
            }
         }
         catch (RemoteException re)
         {
            AeException.logError(re, AeMessages.getString("AeRemoteDebugImpl.ERROR_23")); //$NON-NLS-1$
            
            try
            {
               AeRemoteDebugImpl.doRemoveProcessListener(mContextId, mProcessId, mEndpointURL);
            }
            catch (RemoteException e)
            {
               AeException.logError(e, AeMessages.getString("AeRemoteDebugImpl.ERROR_24")); //$NON-NLS-1$
            }
         }
      }
   }

   /**
    * The engine listener implementation which will forward events to 
    * the remote event handler. 
    */
   protected static class AeEngineListener implements IAeEngineListener
   {
      /** The context id */
      private long mContextId;

      /** The endpoint URL we are listening on. */
      private String mEndpointURL;
      
      /** The event handler object we will forward our requests to . */
      private IAeEventHandler mHandler;
      
      /**
       * Contructor for listener which requires the remote context id and
       * endpoint URL to dispatch events to. 
       * @param aContextId the context id of the remote engine
       * @param aEndpointURL the endpoint events will be dispatched through
       */
      public AeEngineListener(long aContextId, String aEndpointURL) throws RemoteException
      {
         try
         {
            mContextId = aContextId;
            mEndpointURL = aEndpointURL;         
            mHandler = AeRemoteDebugImpl.getEventHandlerServiceLocator(aEndpointURL).getRemoteDebugService();
         }
         catch(Exception e)
         {
            throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_25"), e); //$NON-NLS-1$
         }      
      }
      
      /**
       * @see org.activebpel.rt.bpel.IAeEngineListener#handleAlert(org.activebpel.rt.bpel.IAeEngineAlert)
       */
      public void handleAlert(IAeEngineAlert aEvent)
      {
         try
         {
            if (mHandler != null)
            {
               mHandler.engineAlertHandler(mContextId, aEvent.getPID(), aEvent.getEventID(), 
                     aEvent.getProcessName(), aEvent.getLocation(), aEvent.getFaultName(), aEvent.getDetails(), 
                     aEvent.getTimestamp());
            }
         }
         catch (RemoteException re)
         {
            AeException.logError(re, AeMessages.getString("AeRemoteDebugImpl.ERROR_26")); //$NON-NLS-1$
            
            try
            {
               AeRemoteDebugImpl.doRemoveEngineListener(mContextId, mEndpointURL);
            }
            catch (RemoteException e)
            {
               AeException.logError(e, AeMessages.getString("AeRemoteDebugImpl.ERROR_24")); //$NON-NLS-1$
            }
         }
      }
      
      /**
       * @see org.activebpel.rt.bpel.IAeEngineListener#handleEngineEvent(org.activebpel.rt.bpel.IAeEngineEvent)
       */
      public boolean handleEngineEvent(IAeEngineEvent aEvent)
      {
         try
         {
            if (mHandler != null)
            {
               return mHandler.engineEventHandler(mContextId, aEvent.getPID(), aEvent.getEventID(), 
                     aEvent.getProcessName(), aEvent.getTimestamp());
            }
         }
         catch (RemoteException re)
         {
            AeException.logError(re, AeMessages.getString("AeRemoteDebugImpl.ERROR_26")); //$NON-NLS-1$
            
            try
            {
               AeRemoteDebugImpl.doRemoveEngineListener(mContextId, mEndpointURL);
            }
            catch (RemoteException e)
            {
               AeException.logError(e, AeMessages.getString("AeRemoteDebugImpl.ERROR_24")); //$NON-NLS-1$
            }
         }
         return false;
      }
   }

   /**
    * This is the global process listener stub which will forward breakpoint events to 
    * the remote event handler.
    * 
    * Technically, this class listens globally for <i>process</i> events that indicate a breakpoint
    * may have been hit.  A test is performed and if so, an actual breakpoint event is
    * generated.
    */
   protected static class AeBreakpointListener implements IAeProcessListener
   {
      /** The context id */
      private long mContextId;

      /** The endpoint URL we are listening on. */
      private String mEndpointURL;

      /** The event handler object we will forward our requests to. */
      private IAeEventHandler mHandler;

      /** The list of breakpoints for this proxy to monitor. */
      private HashMap mBreakpointList;

      /**
       * Contructor for listener which requires the remote context id,
       * endpoint URL to dispatch breakpoint events to and a list of breakpoints to monitor. 
       * @param aContextId the context id of the remote engine
       * @param aEndpointURL the endpoint breakpoint events will be dispatched through
       * @param aBreakpointList the list of breakpoints to be monitored.
       */
      public AeBreakpointListener(long aContextId, String aEndpointURL, AeBreakpointList aBreakpointList) throws RemoteException
      {
         try
         {
            mContextId = aContextId;
            mEndpointURL = aEndpointURL;
            updateBreakpointList( aBreakpointList );
            mHandler = AeRemoteDebugImpl.getEventHandlerServiceLocator(aEndpointURL).getRemoteDebugService();
         }
         catch(Exception e)
         {
            throw new RemoteException(AeMessages.getString("AeRemoteDebugImpl.ERROR_28"), e); //$NON-NLS-1$
         }      
      }

      /**
       * Updates the list of breakpoints.
       * 
       * @param aBreakpointList the new list of breakpoints.
       */
      public void updateBreakpointList(AeBreakpointList aBreakpointList)
      {
         if (aBreakpointList == null || aBreakpointList.getTotalRowCount() <= 0)
         {
            mBreakpointList = null ;
         }
         else
         {
            HashMap breakpointList = new HashMap();
            for (int i=0 ; i < aBreakpointList.getTotalRowCount() ; i++)
            {
               AeBreakpointInstanceDetail detail = aBreakpointList.getRowDetails()[i];
               if (! AeUtil.isNullOrEmpty(detail.getNodePath()) &&
                   ! AeUtil.isNullOrEmpty(detail.getProcessName().toString()))
               {
                  breakpointList.put(
                        AeLocationPathUtils.removeInstanceInfo(detail.getNodePath()) + detail.getProcessName().toString(), detail);
               }
            }
            
            mBreakpointList = breakpointList ;
         }
      }

      /**
       * Handle an event fired by the BPEL Engine for a process.
       * @param aEvent The event to handle.
       * @return boolean true if suspend needed, otherwise false.
       * @see org.activebpel.rt.bpel.IAeProcessListener#handleProcessEvent(org.activebpel.rt.bpel.IAeProcessEvent)
       */
      public boolean handleProcessEvent(IAeProcessEvent aEvent)
      {
         try
         {
            // If applicable, fire off a breakpoint event.
            if (mHandler != null && aEvent.getEventID() == IAeProcessEvent.READY_TO_EXECUTE )
            {
               // Getting ready to execute - see if there's a breakpoint set for this node path.
               if ( mBreakpointList != null && 
                    mBreakpointList.get( AeLocationPathUtils.removeInstanceInfo(aEvent.getNodePath())
                                         + aEvent.getQName().toString() ) != null )
               {
                  // First suspend the process locally.
                  AeEngineFactory.getEngine().suspendProcess( aEvent.getPID());
                  
                  // Then dispatch a breakpoint event based on this RTE event.
                  final IAeProcessEvent event = aEvent; 
                  AeEngineFactory.getWorkManager().schedule(new AeAbstractWork()
                  {
                     public void run()
                     {
                        try
                        {
                           mHandler.breakpointEventHandler(mContextId, event.getPID(), event.getNodePath(),
                                 event.getQName(), event.getTimestamp());
                        } 
                        catch (RemoteException e)
                        {
                           AeBusinessProcessException.logError( e, AeMessages.format("AeRemoteDebugImpl.ERROR_33", new Long(event.getPID())));  //$NON-NLS-1$
                           
                           try
                           {
                              AeEngineFactory.getEngine().resumeProcess(event.getPID());
                           }
                           catch (Throwable th)
                           {
                              AeException.logError(th, AeMessages.format("AeRemoteDebugImpl.ERROR_1", new Long(event.getPID()))); //$NON-NLS-1$
                           }

                           try
                           {
                              AeRemoteDebugImpl.doRemoveBreakpointListener(mContextId, mEndpointURL);
                           }
                           catch (Throwable th)
                           {
                              AeException.logError(th, AeMessages.getString("AeRemoteDebugImpl.ERROR_30")); //$NON-NLS-1$
                           }
                        }
                     }
                  });               
                  
                  return true;
               }
            }
         }
         catch (Exception e)
         {
            AeException.logError(e, AeMessages.format("AeRemoteDebugImpl.ERROR_31", new Long(aEvent.getPID()))); //$NON-NLS-1$
         }
         
         return false;
      }
      
      /**
       * @see org.activebpel.rt.bpel.IAeProcessListener#handleProcessInfoEvent(org.activebpel.rt.bpel.IAeProcessInfoEvent)
       */
      public void handleProcessInfoEvent(IAeProcessInfoEvent aEvent)
      {
         // @todo No need to handle this at the moment, may decide to later
      }
   }
}