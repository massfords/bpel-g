// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeProcessViewBase.java,v 1.23 2008/02/17 21:43:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//                   PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.readers.AeBpelLocationPathVisitor;
import org.activebpel.rt.bpel.def.visitors.AeBPWSMessageExchangeDefPathSegmentVisitor;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.admin.AeProcessDeploymentDetail;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.graph.AeBpelGraph;
import org.activebpel.rt.bpeladmin.war.graph.AeGraphProperties;
import org.activebpel.rt.bpeladmin.war.graph.bpel.AeBpelActivityCoordinates;
import org.activebpel.rt.bpeladmin.war.web.AeWebUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;
import org.w3c.dom.Document;

/**
 * Base class for used by the presentation beans responsible for displaying process details.
 * TODO (pj/cck) javascrtipt for jsp duplicated for deployed and active process, plus doesn't account for pick as sole activity.
 */
public class AeProcessViewBase
{
   /** Mode to indicate the active process instance details **/
   public static int ACTIVE_PROCESS_DETAIL = 0;

   /** Mode to indicate the deploayed process details **/
   public static int DEPLOYED_PROCESS_DETAIL = 1;

   /**
    * Indicates the which mode the process view is based on. The mode can be ACTIVE_PROCESS_DETAIL
    * for an active process instance given the pid or DEPLOYED_PROCESS_DETAIL for a deployed process
    * given the pdid (process detail id) or planId.
    */
   private int mMode = ACTIVE_PROCESS_DETAIL;

   /** process id  - used in ACTIVE_PROCESS_DETAIL mode */
   private long mProcessId;

   /** process deployment id (offset) - used in DEPLOYED_PROCESS_DETAIL mode */
   private int mProcessDeploymentId = -1;

   /** Simple model representing the process. */
   private AeBpelProcessObject mProcessObj = null;

   /** Flag to indicate if the bean contains a valid process. If false, see message. */
   private boolean mValid;

   /** Error message */
   private String mMessage;

   /** Active process's BPEL xml document. */
   private Document mBpelDoc = null;

   /** Active process's current state document. */
   private Document mStateDoc = null;

   /** Map containing BPEL element tag name to image resource. */
   private AeProcessViewImageResources mImagePaths = new AeProcessViewImageResources();

   /** Current process instance details. */
   private AeProcessInstanceDetail mProcessDetails = null;

   /** Part which the graphing routine should render. */
   private int mPartId = 0;

   /** Graphing module */
   private AeBpelGraph mBpelGraph = null;

   /** The http request parameter name used to identify the active process or the deployed
    * process. For active process, this value is pid and for deployed processes, the name
    * is pdid.
    */
   private String mPidParamName = "";//$NON-NLS-1$

   /** The value of the current request parameter (pid or dpid). **/
   private String mPidParamValue = "";//$NON-NLS-1$

   /** Location path used to build forEachParallel instance. */
   private String mPivotPath = "";//$NON-NLS-1$

   /**
    * Indicates if the model should be based on using a single instance of forEachParallel constructs.
    * The default value is true.
    */
   private boolean mSingleInstance = true;

   /**
    * Constructs the Bean.
    */
   public AeProcessViewBase()
   {
      setMessage(AeMessages.getString("AeProcessViewBase.0")); //$NON-NLS-1$
      // initially reset to false. (e.g. model is not valid/initialized if the "pid"
      // parameter was not passed in as part of the http query string. (setting the pid param triggers the initialization).
      setValid(false);
   }

   /**
    * @return Returns the mode.
    */
   public int getMode()
   {
      return mMode;
   }

   /**
    * @param aMode The mode to set.
    */
   public void setMode(int aMode)
   {
      mMode = aMode;
   }

   /**
    * @return Returns the pivotPath.
    */
   public String getPivotPath()
   {
      return mPivotPath;
   }

   /**
    * @param aPivotPath The pivotPath to set.
    */
   public void setPivotPath(String aPivotPath)
   {
      mPivotPath = aPivotPath;
   }

   /**
    * @return Returns the singleInstance.
    */
   public boolean isSingleInstance()
   {
      return mSingleInstance;
   }

   /**
    * Sets the single-instance view mode.
    * @param aBooleanValue
    */
   public void setSingleInstanceString(String aBooleanValue)
   {
      setSingleInstance( aBooleanValue != null && "true".equalsIgnoreCase(aBooleanValue.trim()) ); //$NON-NLS-1$
   }

   /**
    * This method was added to satisfy the JSP to ASP converter. It changes false and true strings
    * to booleans.
    * @param aSingleInstance The singleInstance to set.
    */
   public void setSingleInstanceString(boolean aSingleInstance)
   {
      mSingleInstance = aSingleInstance;
   }

   /**
    * @param aSingleInstance The singleInstance to set.
    */
   public void setSingleInstance(boolean aSingleInstance)
   {
      mSingleInstance = aSingleInstance;
   }

   /**
    * @return Returns the pidParamName.
    */
   public String getPidParamName()
   {
      return mPidParamName;
   }

   /**
    * @param aPidParamName The pidParamName to set.
    */
   public void setPidParamName(String aPidParamName)
   {
      mPidParamName = aPidParamName;
   }

   /**
    * @return Returns the pidParamValue.
    */
   public String getPidParamValue()
   {
      return mPidParamValue;
   }

   /**
    * @param aPidParamValue The pidParamValue to set.
    */
   public void setPidParamValue(String aPidParamValue)
   {
      mPidParamValue = aPidParamValue;
   }

   /**
    * @return The AeProcessViewImagePaths object responsible for mapping element tag names to the resource.
    */
   public AeProcessViewImageResources getImagePaths()
   {
      return mImagePaths;
   }

   /**
    * @return Returns the relative path to image resources.
    */
   public String getImagePath()
   {
      return mImagePaths.getImagePath();
   }

   /**
    * Sets the base path to the image resources.
    * @param aImagePath The imagePath to set.
    */
   public void setImagePath(String aImagePath)
   {
      mImagePaths.setImagePath(aImagePath);
   }

   /**
    * Returns the plan for the deployed process view.
    * @param aProcessName
    * @throws AeBusinessProcessException
    */
   protected IAeProcessPlan getDeployedProcessPlan(QName aProcessName) throws AeException
   {
      IAeDeploymentProvider pvd = AeEngineFactory.getDeploymentProvider();
      IAeProcessPlan wsdlPvd = pvd.findCurrentPlan(aProcessName);
      return wsdlPvd;
   }

   /**
    * Returns the process plan wsdl provider to an active process given process id and QName.
    * @param aProcessId
    * @param aProcessName
    * @return process plan
    * @throws AeBusinessProcessException
    */
   protected IAeProcessPlan getActiveProcessPlan(long aProcessId, QName aProcessName) throws AeException
   {
      IAeDeploymentProvider pvd = AeEngineFactory.getDeploymentProvider();
      IAeProcessPlan wsdlPvd = pvd.findDeploymentPlan(aProcessId , aProcessName );
      return wsdlPvd;
   }

   /**
    * Builds the process model.
    * @param aBpelDoc BPEL process def document
    * @param aStateDoc process state document.
    */
   protected void build(Document aBpelDoc, Document aStateDoc)
   {
      try
      {
         AeWebBpelProcessLoader pl = new AeWebBpelProcessLoader(aBpelDoc, aStateDoc);
         // pre-process process def.
         IAeProcessPlan wsdlPvd = null;
         if (getMode() == ACTIVE_PROCESS_DETAIL)
         {
            wsdlPvd = getActiveProcessPlan(getProcessId() , pl.getProcessDef().getQName());
         }
         else
         {
            wsdlPvd = getDeployedProcessPlan(pl.getProcessDef().getQName());
         }
         IAeExpressionLanguageFactory expressionLanguageFactory = AeEngineFactory.getExpressionLanguageFactory();
         pl.getProcessDef().preProcessForValidation(wsdlPvd, expressionLanguageFactory);

         if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals( pl.getProcessDef().getNamespace() ) )
         {
            // assign paths for bpws (1.1) process message exchange/s.
            // bpws 1.1 process's messageExchanges and messageExchange objects do not have location paths.
            // We assign one here - purely for the use of the admin console.
            IAePathSegmentBuilder mxExchangeSegVisitor = new AeBPWSMessageExchangeDefPathSegmentVisitor();
            AeNullLocationPathBuilder pathBuilder = new AeNullLocationPathBuilder(mxExchangeSegVisitor);
            pathBuilder.setNextLocationId(pl.getProcessDef().getMaxLocationId());
            pl.getProcessDef().accept(pathBuilder);
         }

         AeProcessDefToWebModelVisitor visitor = null;
         if (pl.hasStateDocument())
         {
            visitor = new AeProcessDefToWebStateModelVisitor(pl.getProcessDef(), pl.getStateDocument(), isSingleInstance());
            ((AeProcessDefToWebStateModelVisitor)visitor).setPivotPath( getPivotPath());
         }
         else
         {
            visitor = new AeProcessDefToWebModelVisitor(pl.getProcessDef());
         }
         visitor.startVisiting();
         AeBpelProcessObject processObj = visitor.getBpelProcessModel();
         setBpelDocument(aBpelDoc);
         setStateDocument(aStateDoc);
         setBpelProcess(processObj);
         setMessage(""); //$NON-NLS-1$
         setValid(true);
      }
      catch (Throwable t)
      {
         String message = MessageFormat.format(AeMessages.getString("AeProcessViewBase.5"), //$NON-NLS-1$
                        new Object[] { String.valueOf(mProcessId) });
         AeException.logError(t, message);
         setMessage(message);
         setValid(false);
      }
   }

   /**
    * Updates location id only for defs that don't have a location path 
    */
   private static class AeNullLocationPathBuilder extends AeBpelLocationPathVisitor
   {
      public AeNullLocationPathBuilder(IAePathSegmentBuilder aSegmentBuilder)
      {
         super(aSegmentBuilder);
      }
      
      /** 
       * Overrides method to update and record location id and path if and only if the def does
       * not already have a location id and path assigned. 
       * @see org.activebpel.rt.xml.def.visitors.AeLocationPathVisitor#updateLocationId(org.activebpel.rt.xml.def.AeBaseXmlDef)
       */
      protected void updateLocationId(AeBaseXmlDef aDef)
      {
         if (AeUtil.isNullOrEmpty( aDef.getLocationPath() ))
         {
            super.updateLocationId(aDef);
         }
      }    
   }

   /**
    * Initializes the bean by retrieving the BPEL and State documents for the selected process, followed by
    * building a simple model representing the Process and its current state. <p/>This method is called when
    * the presenation layer (JSP) sets process id via setProcessIdStr() method.
    */
   protected void initialize()
   {
      // set valid = true. Errors in getProcessBpelDocument will reset this to false.
      setValid(true);
      Document bpelDoc = getProcessBPELDocument();
      if (bpelDoc != null)
      {
         Document stateDoc = null;
         if (getMode() == ACTIVE_PROCESS_DETAIL)
         {
            // get the state information only for the active process details mode.
            stateDoc = getProcessState();
         }
         // valid maybe reset to false if there were errors when getting the process state.
         if (isValid())
         {
            build(bpelDoc, stateDoc);
         }
      }
   }

   /**
    * Loads the process state document from the engine admin
    */
   protected Document getProcessState()
   {
      Document stateDoc = null;
      try
      {
         stateDoc = AeEngineFactory.getEngineAdministration().getProcessState(mProcessId);
      }
      catch (Throwable t)
      {
         String message = MessageFormat.format(AeMessages.getString("AeProcessViewBase.5"), //$NON-NLS-1$
            new Object[] { String.valueOf(mProcessId) });
         AeException.logError(t, message);
         setMessage(message);
         setValid(false);
      }
      return stateDoc;
   }

   /**
    * Loads the process BPEL Document from the engine admin
    */
   protected Document getProcessBPELDocument()
   {
      // Get the BPEL def xml document
      Document bpelDoc = null;
      AeProcessDeploymentDetail deployDetails = null;
      if (getMode() == ACTIVE_PROCESS_DETAIL)
      {
         // get active process def given PID.
         // load the process instance details for the PID.
         loadProcessInstanceDetails();
         // Get the def deployment details.
         if (getProcessDetails() != null)
         {
            deployDetails = AeEngineFactory.getEngineAdministration().getDeployedProcessDetail(getProcessDetails().getName());
         }
      }
      else
      {
         // get deployed process def given pdid.
         int pdidOffset = getProcessDeploymentId();
         AeProcessDeploymentDetail details[] = AeEngineFactory.getEngineAdministration().getDeployedProcesses();
         if (pdidOffset >= 0 && pdidOffset < details.length)
         {
            deployDetails = details[pdidOffset];
         }
      }

      if (deployDetails == null)
      {
         reportNoProcessDetail();
      }
      else
      {
         try
         {
            bpelDoc = AeProcessViewUtil.domFromString(deployDetails.getBpelSourceXml());
         }
         catch (Exception e)
         {
            reportErrorLoadingBPEL();
         }
      }
      return bpelDoc;
   }

   /**
    * Sets the error message indicating a problem loading the bpel
    */
   protected void reportErrorLoadingBPEL()
   {
      setMessage(MessageFormat.format(AeMessages.getString("AeProcessViewBase.3"), //$NON-NLS-1$
            new Object[] { String.valueOf(mProcessId) }));
      setValid(false);
   }

   /**
    * Sets the error message that there is no process detail available for the pid
    */
   protected void reportNoProcessDetail()
   {
      String id = String.valueOf( getMode() == ACTIVE_PROCESS_DETAIL? getProcessId() : getProcessDeploymentId() );
      setMessage(MessageFormat.format(AeMessages.getString("AeProcessViewBase.2"), //$NON-NLS-1$
            new Object[] { id }));
      setValid(false);
   }

   /**
    * Loads the process instance details and sets them as member data.
    */
   protected void loadProcessInstanceDetails()
   {
      AeProcessInstanceDetail processInstanceDetails = null;
      processInstanceDetails = AeEngineFactory.getEngineAdministration().getProcessDetail(mProcessId);

      if ( processInstanceDetails == null )
      {
         setMessage(MessageFormat.format(AeMessages.getString("AeProcessViewBase.6"), //$NON-NLS-1$
               new Object[] { String.valueOf(mProcessId) }));
         setValid(false);
      }
      setProcessDetails(processInstanceDetails);
   }

   /**
    * @return Returns the processDetails.
    */
   public AeProcessInstanceDetail getProcessDetails()
   {
      return mProcessDetails;
   }

   /**
    * @param aProcessDetails The processDetails to set.
    */
   public void setProcessDetails(AeProcessInstanceDetail aProcessDetails)
   {
      mProcessDetails = aProcessDetails;
   }

   /**
    * @return The process model. Returns null if not initialized.
    */
   public AeBpelProcessObject getBpelProcess()
   {
      return mProcessObj;
   }

   /**
    * Set the process model.
    * @param aProcessObj BPEL process model
    */
   protected void setBpelProcess(AeBpelProcessObject aProcessObj)
   {
      mProcessObj = aProcessObj;
   }

   /**
    * Sets the process id and initializes the bean for the process instance detail mode.
    * @param aPid process id.
    */
   public void setProcessIdString(String aPid)
   {
      try
      {
         long pid = Long.parseLong(aPid);
         setProcessId(pid);
      }
      catch (Exception e)
      {
         AeException.logError( e, e.getMessage() );
         setMessage(MessageFormat.format(AeMessages.getString("AeProcessViewBase.6"), //$NON-NLS-1$
               new Object[] { String.valueOf(mProcessId) }));
         setValid(false);
      }
   }

   /**
    * Sets the process id.
    * @param aPid
    */
   public void setProcessId(long aPid)
   {
      mProcessId = aPid;
      setMode(ACTIVE_PROCESS_DETAIL);
      initialize();
   }

   /**
    * @return the process id.
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * Sets the deployed process id (offset) and initializes the bean for the deployed process detail mode.
    * @param aPdid deployed process id offset
    */
   public void setProcessDeploymentIdString(String aPdid)
   {
      try
      {
         int pdid = Integer.parseInt(aPdid);
         setProcessDeploymentId(pdid);
      }
      catch (Exception e)
      {
         setMessage(MessageFormat.format(AeMessages.getString("AeProcessViewBase.6"), //$NON-NLS-1$
               new Object[] { aPdid }));
         setValid(false);
      }
   }

   /**
    * @return Returns the processDeploymentId.
    */
   public int getProcessDeploymentId()
   {
      return mProcessDeploymentId;
   }

   /**
    * @param aProcessDeploymentId The processDeploymentId to set.
    */
   public void setProcessDeploymentId(int aProcessDeploymentId)
   {
      mProcessDeploymentId = aProcessDeploymentId;
      setMode(DEPLOYED_PROCESS_DETAIL);
      initialize();
   }

   /**
    * Returns true if the bean has been initialized with out any errors. If this method returns false, then
    * the bean has not been initialized due to some error. See getMessage() method for reason.
    * @return true if the bean ready to display the correct information.
    */
   public boolean isValid()
   {
      return mValid;
   }

   /**
    * Sets the bean's valid flag.
    * @param aValid
    */
   protected void setValid(boolean aValid)
   {
      mValid = aValid;
   }

   /**
    * @return Returns the bean's status message.
    */
   public String getMessage()
   {
      return mMessage;
   }

   /**
    * Sets the bean's status or error message.
    * @param aMessage message
    */
   public void setMessage(String aMessage)
   {
      mMessage = aMessage;
   }

   /**
    * @return The process's xml document.
    */
   public Document getBpelDocument()
   {
      return mBpelDoc;
   }

   /**
    * Sets the process's xml document.
    * @param aDoc BPEL def document
    */
   public void setBpelDocument(Document aDoc)
   {
      mBpelDoc = aDoc;
   }

   /**
    * @return The process state's xml document.
    */
   public Document getStateDocument()
   {
      return mStateDoc;
   }

   /**
    * Sets the process's state document.
    * @param aDoc Process state document
    */
   public void setStateDocument(Document aDoc)
   {
      mStateDoc = aDoc;
   }

   /**
    * @return Java Script source for the current process.ye
    */
   public String getJavaScript()
   {
     try
     {
        AeJsTreeBuilder jsBuilder = new AeJsTreeBuilder(getBpelProcess(), getImagePaths());
        String script = jsBuilder.build();
        return script;
     }
     catch(Exception e)
     {
        (new AeException(e)).logError();
        return "// Script generation error : " + e.getMessage(); //$NON-NLS-1$
     }
   }

   /**
    * @return The active process's BPEL source xml.
    */
   public String getBpelXmlSource()
   {
      return getXmlSource( getBpelDocument() );
   }

   /**
    * @return the active process's state document.
    */
   public String getStateXmlSource()
   {
      return getXmlSource( getStateDocument() );
   }

   /**
    * Returns the Document xml as a string.
    * @param aDocument BPEL process or state document.
    * @return xml as a string.
    */
   protected String getXmlSource(Document aDocument)
   {
      String xml = null;
      if (aDocument != null)
      {
         xml = AeProcessViewUtil.stringFromDom(aDocument, true);
      }
      if (AeUtil.isNullOrEmpty(xml))
      {
         xml = "Process xml source not available for ID: " + getProcessId(); //$NON-NLS-1$
      }
      return xml;
   }

   /**
    * Intialize and create the graph layout component.
    */
   protected void initializeGraph()
   {
      String caption = null;
      Date startDate = null;
      Date endDate = null;
      if (getMode() == ACTIVE_PROCESS_DETAIL)
      {
         caption = getActiveProcessGraphCaption();
         startDate = getProcessDetails().getStarted();
         endDate = getProcessDetails().getEnded();
      }
      else
      {
         caption = getDeployedProcessGraphCaption();
      }
      AeBpelGraph bpelGraph = new AeBpelGraph(getBpelProcess(), getPartId(), caption, startDate, endDate);
      setBpelGraph(bpelGraph);
   }

   /**
    * @return The title caption to be displayed in the graph image for an active process.
    */
   protected String getActiveProcessGraphCaption()
   {
      String caption = null;
      caption = getProcessName()
      + " (" + AeMessages.getString("AeProcessViewBase.id") + ": " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      + getProcessId() + ")"; //$NON-NLS-1$
      return caption;
   }

   /**
    * @return The title caption to be displayed in the graph image for a deployed process definition.
    */
   protected String getDeployedProcessGraphCaption()
   {
      String caption = null;
      caption = getProcessName();
      return caption;
   }

   /**
    * @return Returns the bpelGraph.
    */
   public AeBpelGraph getBpelGraph()
   {
      return mBpelGraph;
   }

   /**
    * @param aBpelGraph The bpelGraph to set.
    */
   public void setBpelGraph(AeBpelGraph aBpelGraph)
   {
      mBpelGraph = aBpelGraph;
   }

   /**
    * Sets the process's part id and initializes the bean's graphing module.
    * @param aPartId part id.
    */
   public void setPartIdString(String aPartId)
   {
      try
      {
         int partId = Integer.parseInt(aPartId);
         setPartId(partId);
      }
      catch (Exception e)
      {
         setMessage(MessageFormat.format(AeMessages.getString("AeProcessViewBase.6"), //$NON-NLS-1$
               new Object[] { String.valueOf(mProcessId) }));
         setValid(false);
      }
   }

   /**
    * Sets the part id to be graphed and initializes the graphing process.
    * @param aPartId The part id for which a graph will be rendered.
    */
   public void setPartId(int aPartId)
   {
      mPartId = aPartId;
      initializeGraph();
   }

   /**
    * @return The part id for which a graph will be rendered.
    */
   public int getPartId()
   {
      return mPartId;
   }

   /**
    * @return Process name
    */
   public String getProcessName()
   {
      if (getBpelProcess() != null)
      {
         return getBpelProcess().getName();
      }
      else
      {
         return "";//$NON-NLS-1$
      }
   }

   /**
    * @return Process part name that will be rendered as a graph.
    */
   public String getPartName()
   {
      if (getBpelGraph() != null)
      {
         return getBpelGraph().getPartName();
      }
      else
      {
         return "";//$NON-NLS-1$
      }
   }

   /**
    * Width of the graph, in pixels.
    * @return width or 0 if a graph is not available.
    */
   public int getWidth()
   {
      if (getBpelGraph() != null)
      {
         return getBpelGraph().getWidth();
      }
      else
      {
         return 0;
      }
   }

   /**
    * Height of the graph, in pixels.
    * @return height or 0 if a graph is not available.
    */
   public int getHeight()
   {
      if (getBpelGraph() != null)
      {
         return getBpelGraph().getHeight();
      }
      else
      {
         return 0;
      }
   }

   /**
    * Returns true if this process has a graph
    * @return true if an image can be generated.
    */
   public boolean isHasImage()
   {
      if (getBpelGraph() != null)
      {
         return getBpelGraph().hasImage();
      }
      else
      {
         return false;
      }
   }

   /**
    * Returns the client side image map area elements for the current graph
    * @return string containg map areas.
    */
   public String getGraphImageMapArea()
   {
      StringBuffer sb = new StringBuffer();
      if (isHasImage())
      {
         synchronized(sb)
         {
            List list = getBpelGraph().getGraphContainer().getCoordinateList();
            for (int i = 0; i < list.size(); i++)
            {
               AeBpelActivityCoordinates ac = (AeBpelActivityCoordinates) list.get(i);
               int x1 = ac.getBounds().x;
               int y1 = ac.getBounds().y;
               int x2 = x1 + ac.getBounds().width;
               int y2 = y1 + ac.getBounds().height;
               String coords = String.valueOf(x1) + "," + y1 + "," + x2 + "," + y2;  //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
               String path = AeWebUtil.escapeSingleQuotes( ac.getLocationPath());

               sb.append("<area shape=\"RECT\" COORDS=\"");//$NON-NLS-1$
               sb.append(coords);
               sb.append("\"\n");//$NON-NLS-1$
               sb.append("   onClick=\"onActivitySelect('" + path + "');return false;\"\n");//$NON-NLS-1$ //$NON-NLS-2$
               sb.append("   onMouseOver=\"onActivityMouseOver('" + path + "');return true;\"\n"); //$NON-NLS-1$  //$NON-NLS-2$
               sb.append("   onMouseOut=\"onActivityMouseOut('" + path + "');return true;\"\n"); //$NON-NLS-1$ //$NON-NLS-2$
               sb.append("   title=\"" + ac.getLocationPath() + "\"\n"); //$NON-NLS-1$ //$NON-NLS-2$
               sb.append("   href=\"javascript:void();\"\n"); //$NON-NLS-1$
               sb.append("/>\n");//$NON-NLS-1$
            }
         }
      }
      return sb.toString();
   }

   /**
    * @return true if this process has a global compensation handler.
    */
   public boolean isHasCompensationHandler()
   {
      return (getBpelProcess() != null && getBpelProcess().getCompensationHandler() != null);
   }

   /**
    * @return true if this process has a global termination handler.
    */
   public boolean isHasTerminationHandler()
   {
      return (getBpelProcess() != null && getBpelProcess().getTerminationHandler() != null);
   }

   /**
    * @return true if this process has event handlers.
    */
   public boolean isHasEventHandlers()
   {
      return (getBpelProcess() != null && getBpelProcess().getEventHandlers() != null);
   }

   /**
    * @return true if this process has fault handlers.
    */
   public boolean isHasFaultHandlers()
   {
      return (getBpelProcess() != null && getBpelProcess().getFaultHandlers() != null);
   }

   /**
    * Returns true if the graphing moduled is enabled. The module can be enabled or disabled
    * via a user preference.
    * @return true if the graphing module should be displayed.
    */
   public boolean isGraphingEnabled()
   {
      return AeGraphProperties.getInstance().getPropertyBoolean(AeGraphProperties.ENABLE_GRAPHING, true);
   }

   /**
    * Sets enabled or disables the graph.
    * @param aEnable
    */
   protected void setGraphingEnabled(boolean aEnable)
   {
      AeGraphProperties.getInstance().setProperty(AeGraphProperties.ENABLE_GRAPHING, String.valueOf(aEnable));
   }

   /**
    * @param aShowGraph Sets enable the graph from a JSP.
    */
   public void setShowGraph(String aShowGraph)
   {
      if ( AeUtil.notNullOrEmpty(aShowGraph) )
      {
         boolean enable = !"false".equalsIgnoreCase(aShowGraph); //$NON-NLS-1$
         setGraphingEnabled(enable);
      }
   }
}