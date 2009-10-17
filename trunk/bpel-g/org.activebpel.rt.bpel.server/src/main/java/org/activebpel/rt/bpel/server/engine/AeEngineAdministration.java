// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AeEngineAdministration.java,v 1.69 2007/12/26 17:38:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.coord.AeCoordinationNotFoundException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.impl.AeUnmatchedReceive;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.IAeQueueManager;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.AeMessageReceiverListResult;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.impl.queue.AeAlarm;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerAddressingAdmin;
import org.activebpel.rt.bpel.server.admin.AeBuildInfo;
import org.activebpel.rt.bpel.server.admin.AeProcessDeploymentDetail;
import org.activebpel.rt.bpel.server.admin.AeQueuedReceiveDetail;
import org.activebpel.rt.bpel.server.admin.AeQueuedReceiveMessageData;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.catalog.report.IAeCatalogAdmin;
import org.activebpel.rt.bpel.server.deploy.AeServiceMap;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.bpel.urn.IAeURNResolver;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeLongMap;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.wsio.AeWebServiceAttachment;
import org.w3c.dom.Document;

/**
 * Provides administration/console support for the engine. This class uses the
 * AeEngineFactory to get access to the engine and deployment descriptor information. 
 * 
 * TODO This catches various BusinessProcessExceptions, logs them, and returns null (in several methods).  Those exceptions should be propagated to the caller.
 */
public class AeEngineAdministration implements IAeEngineAdministration
{
   /** Holds build information. */
   private AeBuildInfo[] mBuildInfo = null;

   /** comparator for sorting the deployment detail objects */
   private Comparator mDeploymentComparator = new Comparator()
   {
      public int compare(Object o1, Object o2)
      {
         AeProcessDeploymentDetail one = (AeProcessDeploymentDetail) o1;
         AeProcessDeploymentDetail two = (AeProcessDeploymentDetail) o2;
         return one.getName().getLocalPart().compareToIgnoreCase(two.getName().getLocalPart());
      }
   };

   /** comparator for service deployment objects */
   private Comparator mServiceComparator = new Comparator()
   {
      public int compare(Object o1, Object o2)
      {
         IAeServiceDeploymentInfo one = (IAeServiceDeploymentInfo) o1;
         IAeServiceDeploymentInfo two = (IAeServiceDeploymentInfo) o2;
         return one.getServiceName().compareTo(two.getServiceName());
      }
   };

   
   /**
    * Default constructor.
    */
   public AeEngineAdministration()
   {
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getDeployedServices()
    */
   public IAeServiceDeploymentInfo[] getDeployedServices()
   {
      List sortedList = AeServiceMap.getServiceEntries(); 
      Collections.sort(sortedList, mServiceComparator);
      
      IAeServiceDeploymentInfo[] services = new IAeServiceDeploymentInfo[sortedList.size()];
      sortedList.toArray(services);
      return services;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getDeployedProcesses()
    */
   public AeProcessDeploymentDetail[] getDeployedProcesses()
   {
      IAeDeploymentProvider deploymentProvider = AeEngineFactory.getDeploymentProvider();
      List list = new ArrayList();
      for (Iterator iter = deploymentProvider.getDeployedPlans(); iter.hasNext();)
      {
         IAeProcessDeployment deployedProcess = (IAeProcessDeployment) iter.next();
         list.add( createProcessDetail(deployedProcess) );
      }
      Collections.sort(list, mDeploymentComparator);
      AeProcessDeploymentDetail[] details = new AeProcessDeploymentDetail[list.size()];
      list.toArray(details);
      return details;
   }
   
   /**
    * Create the <code>AeProcessDeploymentDetail</code> from the given
    * <code>IAeProcessDeployment</code>.
    * @param aDeployment 
    */
   protected AeProcessDeploymentDetail createProcessDetail( IAeProcessDeployment aDeployment )
   {
       AeProcessDeploymentDetail detail = new AeProcessDeploymentDetail();
       detail.setName(aDeployment.getProcessDef().getQName());
       detail.setSourceXml(AeXMLParserBase.documentToString(aDeployment.getSourceElement()));
       detail.setBpelSourceXml(aDeployment.getBpelSource());
       return detail;
   }
   
   /**
    * Gets the current <code>AeProcessDeploymentDetail</code> that
    * represents the current plan data for the given QName.
    * @return process deployment detail or <code>null</code> if the the details are not available.
    */
   public AeProcessDeploymentDetail getDeployedProcessDetail( QName aQName )
   {
      AeProcessDeploymentDetail detail = null;
       try
       {
           IAeDeploymentProvider deploymentProvider = AeEngineFactory.getDeploymentProvider();
           IAeProcessDeployment deploymentPlan = deploymentProvider.findCurrentDeployment( aQName );
           // deployment plan maybe null if it was removed (e.g. .bpr removed. See defect # 1368)
           if (deploymentPlan != null)
           {
              detail = createProcessDetail(deploymentPlan);
           }
       }
       catch( AeBusinessProcessException abe )
       {
           abe.logError();
       }
       return detail;
   }
   
   /**
    * Getter for the bpel engine
    */
   protected AeBpelEngine getBpelEngine()
   {
      return (AeBpelEngine)AeEngineFactory.getEngine();
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getProcessDetail(long)
    */
   public AeProcessInstanceDetail getProcessDetail(long aId)
   {
      return getBpelEngine().getProcessInstanceDetails(aId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getMessageReceivers(org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter)
    */
   public AeMessageReceiverListResult getMessageReceivers( AeMessageReceiverFilter aFilter )
   {
      try
      {
         AeLongMap processDefMap = new AeLongMap();

         AeMessageReceiverListResult results = getBpelEngine().getQueueManager().getMessageReceivers( aFilter );
         for( int i = 0; i < results.getResults().length; i++  )
         {
            AeMessageReceiver qObj = results.getResults()[i];
            AeProcessDef def = getProcessDef(qObj.getProcessId(), processDefMap);
            if (def != null)
            {
               String path = def.getLocationPath(qObj.getMessageReceiverPathId());
               results.addPathMapping( qObj.getMessageReceiverPathId(), path);
            }
         }
         return results;
      }
      catch (AeBusinessProcessException ex)
      {
         ex.logError();
         return null;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getAlarms(org.activebpel.rt.bpel.impl.list.AeAlarmFilter)
    */
   public AeAlarmListResult getAlarms(AeAlarmFilter aFilter)
   {
      try
      {
         AeLongMap processDefMap = new AeLongMap();
         
         AeAlarmListResult results = getBpelEngine().getQueueManager().getAlarms( aFilter );
         for( int i = 0; i < results.getResults().length; i++  )
         {
            AeAlarm alarm = results.getResults()[i];
            
            long pid = alarm.getProcessId();
            AeProcessDef processDef = getProcessDef(pid, processDefMap);
            if (processDef != null)
            {
               // processDef will only be null if there was an error finding it
               String path = processDef.getLocationPath(alarm.getPathId());
               results.addPathMapping(alarm.getPathId(), path);
            }
         }
         return results;
      }
      catch (AeBusinessProcessException ex)
      {
         ex.logError();
         return null;
      }
   }

   /**
    * Gets the process def from the deployment provider, using the map as a cache. 
    * @param aProcessId
    * @param aProcessDefMap
    */
   private AeProcessDef getProcessDef(long aProcessId, AeLongMap aProcessDefMap)
   {
      AeProcessDef processDef = (AeProcessDef) aProcessDefMap.get(aProcessId);
      if (processDef == null)
      {
         try
         {
            QName processName = getBpelEngine().getProcessManager().getProcessQName(aProcessId);
            IAeProcessPlan plan =
               AeEngineFactory.getDeploymentProvider().findDeploymentPlan(
                  aProcessId,
                  processName);
            processDef = plan.getProcessDef();
            aProcessDefMap.put(aProcessId, processDef);
         }
         catch (AeException e)
         {
            e.logError();
         }
      }
      return processDef;
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getUnmatchedQueuedReceives()
    */
   public AeQueuedReceiveDetail[] getUnmatchedQueuedReceives()
   {
      IAeQueueManager mgr = getBpelEngine().getQueueManager();
      List coll = new ArrayList();
      addAll( coll, mgr.getUnmatchedReceivesIterator() );
      
      AeQueuedReceiveDetail[] details = new AeQueuedReceiveDetail[coll.size()];
      int i=0;
      for (Iterator iter = coll.iterator(); iter.hasNext(); i++)
      {
         AeUnmatchedReceive unmatchedReceive = (AeUnmatchedReceive) iter.next();
         String messageReceiverPath = null;
         AeInboundReceive qObj = unmatchedReceive.getInboundReceive();
         IAeMessageData messageData = qObj.getMessageData();
         AeQueuedReceiveMessageData mData = null;
         if( messageData != null )
         {
            mData = new AeQueuedReceiveMessageData(messageData.getMessageType());
            for (Iterator iter2 = messageData.getPartNames(); iter2.hasNext();)
            {
               String name = (String)iter2.next();
               mData.addPartData( name, messageData.getData(name) );
            }
         }

         // TODO (EPW) returns only the partner link name, probably should return the path/id too
         AeQueuedReceiveDetail detail = 
            new AeQueuedReceiveDetail(
               0, 
               qObj.getPartnerLinkOperationKey().getPartnerLinkName(),
               qObj.getPortType(), 
               qObj.getOperation(), 
               messageReceiverPath,
               qObj.getCorrelation(),
               mData );
         details[i] = detail;
      }
      return details;
   }

   /**
    * Convenience method for adding source iterator objects to the 
    * target collection.
    * @param aTarget The target collection.
    * @param aSource The source iterator.
    */
   protected void addAll( Collection aTarget, Iterator aSource )
   {
      while( aSource.hasNext() )
      {
         aTarget.add( aSource.next() );
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getBuildInfo()
    */
   public AeBuildInfo[] getBuildInfo()
   {
      if (mBuildInfo == null)
      {
         List list = createBuildInfo();
         mBuildInfo = (AeBuildInfo[]) list.toArray(new AeBuildInfo[list.size()]);
      }
      return mBuildInfo;
   }

   /**
    * Creates the list of build info objects.
    */
   protected List createBuildInfo()
   {
      ArrayList list = new ArrayList();
      list.add(new AeBuildInfo(AeMessages.getString("AeEngineAdministration.RT"), org.activebpel.rt.AeBuildNumber.getBuildNumber(), AeBuildInfo.convertCVSDateString(org.activebpel.rt.AeBuildNumber.getBuildDate())));  //$NON-NLS-1$
      list.add(new AeBuildInfo(AeMessages.getString("AeEngineAdministration.RT_BPEL"), org.activebpel.rt.bpel.AeBuildNumber.getBuildNumber(), AeBuildInfo.convertCVSDateString(org.activebpel.rt.bpel.AeBuildNumber.getBuildDate())));  //$NON-NLS-1$
      list.add(new AeBuildInfo(AeMessages.getString("AeEngineAdministration.RT_BPEL_SERVER"), org.activebpel.rt.bpel.server.AeBuildNumber.getBuildNumber(), AeBuildInfo.convertCVSDateString(org.activebpel.rt.bpel.server.AeBuildNumber.getBuildDate())));  //$NON-NLS-1$

      AeBuildInfo.createBuildInfoFor(list, "org.activebpel.rt.axis.bpel.AeBuildNumber", AeMessages.getString("AeEngineAdministration.RT_AXIS")); //$NON-NLS-1$ //$NON-NLS-2$
      AeBuildInfo.createBuildInfoFor(list, "org.activebpel.rt.tomcat.AeBuildNumber", AeMessages.getString("AeEngineAdministration.RT_TOMCAT")); //$NON-NLS-1$ //$NON-NLS-2$
      
      return list;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getStartDate()
    */
   public Date getStartDate()
   {
      return getBpelEngine().getStartDate();
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getEngineState()
    */
   public int getEngineState()
   {
      return getBpelEngine().getState();
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getMonitorStatus()
    */
   public int getMonitorStatus()
   {
      return getBpelEngine().getMonitorStatus();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getEngineErrorInfo()
    */
   public String getEngineErrorInfo()
   {
      return getBpelEngine().getErrorInfo();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getProcessLog(long)
    */
   public String getProcessLog(long aProcessId)
   {
      try
      {
         return AeEngineFactory.getLogger().getAbbreviatedLog(aProcessId).toString();
      }
      catch (Exception e)
      {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw, true);
         e.printStackTrace(pw);
         return sw.toString();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getProcessList(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public AeProcessListResult getProcessList(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getBpelEngine().getProcesses(aFilter);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getProcessCount(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int getProcessCount(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getBpelEngine().getProcessCount(aFilter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getProcessState(long)
    */
   public Document getProcessState(long aPid) throws AeBusinessProcessException
   {
      return getBpelEngine().getProcessState(aPid); 
   }

   /**
    * Returns process variable for the specified process and the variable
    * location path.
    * @param aPid the ID of the process.
    * @param aVariablePath location path of the variable
    * @throws AeBusinessProcessException
    */
   public Document getVariable(long aPid, String aVariablePath) throws AeBusinessProcessException
   {
      return getBpelEngine().getProcessVariable(aPid, aVariablePath);
   }
   
   /** 
    * Overrides method to return location path given the id of the BPEL object and the process id. 
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getLocationPathById(long, int)
    */
   public String getLocationPathById(long aProcessId, int aLocationId) throws AeBusinessProcessException
   {
      return getBpelEngine().getLocationPathById(aProcessId, aLocationId);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getEngineConfig()
    */
   public IAeEngineConfiguration getEngineConfig()
   {
      return AeEngineFactory.getEngineConfig();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getPartnerAddressingAdmin()
    */
   public IAePartnerAddressingAdmin getPartnerAddressingAdmin()
   {
      return (IAePartnerAddressingAdmin)AeEngineFactory.getPartnerAddressProvider();
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getCatalogAdmin()
    */
   public IAeCatalogAdmin getCatalogAdmin()
   {
      return (IAeCatalogAdmin)AeEngineFactory.getCatalog();
   }
   
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getDeploymentLogListing()
    */
   public String[] getDeploymentLogListing()
   {
      return new String[] {"Deployment Log"}; //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getDeploymentLog()
    */   
   public String getDeploymentLog()
   {
      String[] lines = AeEngineFactory.getDeploymentLoggerFactory().getDeploymentLog();
      if( lines != null )
      {
         String lineSep = System.getProperty("line.separator"); //$NON-NLS-1$
         StringBuffer sb = new StringBuffer();
         for( int i = 0; i < lines.length; i++ )
         {
            sb.append( lines[i] );
            sb.append( lineSep );                             
         }
         return sb.toString();
      }
      else
      {
         return ""; //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#start()
    */
   public void start() throws AeBusinessProcessException
   {
      AeEngineFactory.start();
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#stop()
    */
   public void stop() throws AeBusinessProcessException
   {
      getBpelEngine().stop();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#isRunning()
    */
   public boolean isRunning()
   {
      return getEngineState() == IAeEngineAdministration.RUNNING; 
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#removeProcesses(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public int removeProcesses(AeProcessFilter aFilter) throws AeBusinessProcessException
   {
      return getBpelEngine().removeProcesses(aFilter);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#deployNewBpr(java.io.File, java.lang.String, org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger)
    */
   public void deployNewBpr(File aBprFile, String aBprFilename, IAeDeploymentLogger aLogger) throws AeException
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#getURNAddressResolver()
    */
   public IAeURNResolver getURNAddressResolver()
   {
      return AeEngineFactory.getURNResolver();
   }
   
   /**
    * Returns the correlation set data
    * @param aProcessId process id
    * @param aLocationPath location path of the correlation set.
    * @return correlation set as a string
    * @throws AeBusinessProcessException
    */
   public String getCorrelationSetData(long aProcessId, String aLocationPath) throws AeBusinessProcessException
   {
      Map correlationData = getBpelEngine().getCorrelationData(aProcessId, aLocationPath);
      return AeCorrelationSet.convertCorrSetDataToString(correlationData);      
   }

   /**
    * Get the requested partner role endpoint reference for the given partnerLink path.
    * @param aProcessId process id
    * @param aLocationPath location path of the correlation set. 
    * @return partner role data as a string
    * @throws AeBusinessProcessException
    */
   public String getPartnerRoleData(long aProcessId, String aLocationPath) throws AeBusinessProcessException
   {
      IAeEndpointReference epr = getBpelEngine().getPartnerRoleEndpointReference(aProcessId, aLocationPath);
      String eprData = ""; //$NON-NLS-1$
      if( epr != null )
      {
         eprData = AeXMLParserBase.documentToString( epr.toDocument(), true );
      }
      return eprData;      
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#isInternalWorkManager()
    */
   public boolean isInternalWorkManager()
   {
      return AeEngineFactory.isInternalWorkManager();
   }
 
   /**
    * Returns the coordination information for the parent process given the child process id.
    * @param aChildProcessId
    * @return AeCoordinationDetail of the coordinator or null if not found.
    * @throws AeException
    */
   public AeCoordinationDetail getCoordinatorForProcessId(long aChildProcessId) throws AeException
   {
      try
      {
         return getBpelEngine().getCoordinationManager().getCoordinatorDetail(aChildProcessId);
      }
      catch(AeCoordinationNotFoundException cnfe)
      {
         return null;
      }
   }
   
   /**
    * Returns a list of AeCoordinationDetail for all subprocess (participants) given the parent process id.
    * @param aParentProcessId
    * @throws AeException
    */
   public List getParticipantForProcessId(long aParentProcessId) throws AeException
   {
      try
      {
         return getBpelEngine().getCoordinationManager().getParticipantDetail(aParentProcessId);
      }
      catch(AeCoordinationNotFoundException cnfe)
      {
         return Collections.EMPTY_LIST;
      }      
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#addVariableAttachment(long, java.lang.String, org.activebpel.wsio.AeWebServiceAttachment)
    */
   public IAeAttachmentItem addVariableAttachment(long aPid, String aLocationPath, AeWebServiceAttachment aWsioAttachment) throws AeException
   {
      return getBpelEngine().addVariableAttachment(aPid, aLocationPath, aWsioAttachment);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.IAeEngineAdministration#removeVariableAttachments(long, java.lang.String, int[])
    */
   public void removeVariableAttachments(long aPid, String aLocationPath, int[] aAttachmentItemNumbers) throws AeException
   {
      getBpelEngine().removeVariableAttachments(aPid, aLocationPath, aAttachmentItemNumbers);
   }
}