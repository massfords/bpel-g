// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeProcessViewBean.java,v 1.21 2008/02/17 21:43:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.graph.AeGraphProperties;
import org.activebpel.rt.bpeladmin.war.web.AeWebUtil;
import org.activebpel.rt.util.AeUtil;

/**
 * This bean is responsible for providing BPEL activity attribute data
 * as well as other details (when applicable) such as variable state
 * information.
 */
public class AeProcessViewBean extends AeProcessViewBase
{
   /** Image tile width and height, in pixels. */
   public static final int DEFAULT_IMAGE_TILE_SIZE = 1000;
   /** Minimum tile width and height. */
   public static final int MINIMUM_IMAGE_TILE_SIZE = 50;

   public static final int NONE = 0;
   public static final int SUSPEND = 1;
   public static final int RESUME = 2;
   public static final int TERMINATE = 3;
   public static final int RESTART = 4;

   /** Action to be performed on the process. */
   private int mProcessOp = NONE;

   /** Location path to current this BPEL model.*/
   private String mPath = null;

   /** The simple BPEL model behind this view */
   private AeBpelObjectBase mBpelObj = null;

   /** Object display attributes */
   private Map mProperties = new LinkedHashMap();

   /** Element attributes as an array to support Indexed JSPT tags. */
   private AePropertyNameValue mPropNvList [] = null;

   /** List of additional (String) data.*/
   private List mDetails = new ArrayList();

   /** Indicates if log file is available for the process view. */
   private boolean mHasLogData = false;

   /** Indicates if the log data for the process should be retrieved to be displayed. */
   private boolean mRetrieveLogData = false;

   /**
    * Indicates that the process state may have changed and web view may need to be updated.
    */
   private boolean mStateChanged = false;

   /** List of attachments (AeAttachmentViewBean objects) data.*/
   private List mAttachments = new LinkedList();

   /**
    * Constructs the basic bean.
    */
   public AeProcessViewBean()
   {
      super();
   }

   /**
    * Retrieves the BPEL element's attribute (such as name), as well as
    * additional data such a instance variable information.
    * <p/>
    * This method is invoked when the path to the element is set.
    * Assumes that the active process id has already been set via SetProcessId()
    * method i.e. call SetProcessId(pid) first, followed by SetPath(theLocationPath).
    */
   protected void retrieveProperties()
   {
      // Return if this is not a valid object or process.
      if ( !isValid() )
      {
         return;
      }

      // get the underlying model
      mBpelObj = getBpelProcess().getWebModel(mPath);
      if (mBpelObj != null)
      {
         // prepare, build property and other data for display.
         AeProcessViewPropertyBuilder builder = createPropertyBuilder();
         builder.build();
      }
      else
      {
         setValid(false);
         setMessage( AeMessages.format("AeProcessViewBean.invalidPath", mPath) ); //$NON-NLS-1$
      }

      if (isValid() && isRootProcess())
      {
         // udpate process state property - which is not the same as the BPEL object activity state.
         AePropertyNameValue nv = getProperty(IAeImplStateNames.STATE_STATE) ;
         if (nv != null)
         {
            String state = nv.getValue();
            nv.setValue( AeMessages.getString("AeProcessViewBean." + state) ); //$NON-NLS-1$
         }
      }
   }

   /**
    * Creates a property builder to build properties associated with objects.  Derived
    * classes may implement this to return their own builder.
    */
   protected AeProcessViewPropertyBuilder createPropertyBuilder()
   {
      AeProcessViewPropertyBuilder builder = new AeProcessViewPropertyBuilder(this);
      return builder;
   }

   /**
    * Creates and returns an array of AePropertyNameValue objects from the
    * internal hash map mProperties.
    * @return array of name value objects
    */
   private  AePropertyNameValue[] getPropertyNvList()
   {
      if (mPropNvList == null)
      {
         mPropNvList = new AePropertyNameValue[ mProperties.keySet().size() ];
         Iterator it = mProperties.keySet().iterator();
         int i = 0;
         while (it.hasNext())
         {
            String key = (String) it.next();
            AePropertyNameValue value = (AePropertyNameValue) mProperties.get(key);
            mPropNvList[i++] = value;
         }
      }
      return mPropNvList;
   }

   /**
    * Sets the XPath to this element.
    * @param aPath Xpath to this element
    */
   public void setPath(String aPath)
   {
      setPath(aPath, true);
   }

   /**
    * Sets the Xpath for the currently selected element.
    * @param aPath
    * @param aRetrieveProperties
    */
   protected void setPath(String aPath, boolean aRetrieveProperties)
   {
      mPath = aPath;
      if (aRetrieveProperties)
      {
         retrieveProperties();
      }
   }

   /**
    * @return Returns the elements location path.
    */
   public String getPath()
   {
      return mPath;
   }

   /**
    * @return Returns the single quote escaped value of the location path.
    */
   public String getEscapedPath()
   {
      return AeWebUtil.escapeSingleQuotes(getPath());
   }

   /**
    * Sets the location path by is
    * @param aPathIdString
    */
   public void setPathIdString(String aPathIdString)
   {
      if (AeUtil.notNullOrEmpty(aPathIdString) && !"null".equalsIgnoreCase(aPathIdString))  //$NON-NLS-1$
      {
         try
         {
            int pathId = Integer.parseInt(aPathIdString);
            // first check the process def (so that we will not need to access the Process impl i.e. skip lock/migrate code)
            String path = null;
            if (getBpelProcess() != null)
            {
               path = getBpelProcess().getProcessDef().getLocationPath(pathId);
            }
            // if path was not available in the process def, then get the path via Process object.
            // (applies mostly to run time paths such as ForEach parallel).
            if (AeUtil.isNullOrEmpty(path))
            {
               path = AeEngineFactory.getEngineAdministration().getLocationPathById(getProcessId(), pathId);
            }
            if (AeUtil.notNullOrEmpty(path))
            {
               setPath(path, false);
            }
         } catch(Exception e)
         {
            // ignore
         }
      }
   }

   /**
    * @return Returns the BPEL objects tag (Node) name.
    */
   public String getTagName()
   {
      if (getBpelObject() != null)
      {
         return AeProcessViewUtil.formatLabel( getBpelObject().getTagName() );
      }
      else
      {
         return ""; //$NON-NLS-1$
      }
   }

   /**
    * @return The image path to the assoicated image or icon.
    */
   public String getBpelImagePath()
   {
      if ( getBpelObject() != null)
      {
         // (bpelObject may be null if the process is not available. e.g. invalid pid).
         return getImagePaths().getBpelObjectImagePath( getBpelObject().getIconName());
      }
      else
      {
         return ""; //$NON-NLS-1$
      }
   }


   /**
    * @return true if this bean has top level properties to be displayed.
    */
   public boolean isHasProperties()
   {
      return ( !mProperties.isEmpty() );
   }

   /**
    * An indexed access method to support Indexed JSP tags.
    * @param aIndex zero based index to the property.
    * @return the name value at the given index.
    */
   public AePropertyNameValue getProperties(int aIndex)
   {
      AePropertyNameValue[]  list = getPropertyNvList();
      return list[aIndex];
   }

   /**
    * @return  Returns the number of properties. Thus method is used to support Indexed JSP tags.
    */
   public int getPropertiesSize()
   {
      AePropertyNameValue[]  list = getPropertyNvList();
      return list.length;
   }

   /**
    * @return Returns true if this element is the root i.e. a BPEL Process element.
    */
   public boolean isRootProcess()
   {
      return getBpelObject() != null && "process".equals(getBpelObject().getTagName()); //$NON-NLS-1$
   }

   /**
    * @return Returns the BPEL object if there is a current selection or <code>null</code> otherwise.
    */
   protected AeBpelObjectBase getBpelObject()
   {
      return mBpelObj;
   }

   /**
    * Adds additional property details. This method is called by the
    * AePropertyDetailBuilder.
    * @param aDetails name value wrapper.
    */
   protected void addDetails(AePropertyNameValue aDetails)
   {
      if (!mDetails.contains(aDetails))
      {
         mDetails.add(aDetails);
      }
   }

   /**
    * @return true if there is further there is more information or properties.
    */
   public boolean isHasDetails()
   {
      return (mDetails.size() > 0);
   }

   /**
    * An indexed access method to support Indexed JSP tags.
    * @param aIndex zero based index of the name value to be returned.
    * @return name value for the given index.
    */
   public AePropertyNameValue getDetails(int aIndex)
   {
      return (AePropertyNameValue) mDetails.get(aIndex);
   }

   /**
    * @return  Returns the number of additional details. Thus method is used to support Indexed JSP tags.
    */
   public int getDetailsSize()
   {
      return mDetails.size();
   }

   /**
    * Adds attachment details. This method is called by the
    * AeProcessViewPropertyBuilder.
    * @param aAttachment attachment items wrapper.
    */
   protected void addAttachments(AeAttachmentViewBean aAttachment)
   {
      if (!mAttachments.contains(aAttachment))
      {
         mAttachments.add(aAttachment);
      }
   }

   /**
    * @return Returns the hasAttachments.
    */
   public boolean isHasAttachments()
   {
      return (mAttachments.size() > 0);
   }

   /**
    * An indexed access method to support Indexed JSP tags.
    * @param aIndex zero based index of the name value to be returned.
    * @return name value for the given index.
    */
   public AeAttachmentViewBean getAttachments(int aIndex)
   {
      return (AeAttachmentViewBean) mAttachments.get(aIndex);
   }

   /**
    * @return  Returns the number of additional attachments. Thus method is used to support Indexed JSP tags.
    */
   public int getAttachmentsSize()
   {
      return mAttachments.size();
   }

   /**
    * adds the property name and value.
    * @param aName propery name
    * @param aValue propery value
    */
   public void addProperty(String aName, String aValue, String aLocationPath)
   {
      addProperty(aName, aName, aValue, aLocationPath);
   }

   /**
    * adds the property name and value.
    * @param aName propery name
    * @param aValue propery value
    */
   public void addProperty(String aKey, String aName, String aValue, String aLocationPath)
   {
      AePropertyNameValue nv = new AePropertyNameValue(aKey, aName, aValue, false, aLocationPath, false);
      addProperty(nv);
   }

   /**
    * Adds a property to the collection.
    * @param aNameValue property wrapped in a Name value object.
    */
   public void addProperty(AePropertyNameValue aNameValue)
   {
      mProperties.put(aNameValue.getKey(), aNameValue);
   }

   /**
    * Removes given property.
    * @param aKey the key of the property to be removed.
    */
   public void removeProperty(String aKey)
   {
      mProperties.remove(aKey);
   }

   /**
    * Returns the given property.
    * @param aKey the entry key of the property.
    */
   public AePropertyNameValue getProperty(String aKey)
   {
      return (AePropertyNameValue) mProperties.get(aKey);
   }

   /**
    * @return Returns the hasLogData.
    */
   public boolean isHasLogData()
   {
      return mHasLogData;
   }

   /**
    * @param aHasLogData The hasLogData to set.
    */
   public void setHasLogData(boolean aHasLogData)
   {
      mHasLogData = aHasLogData;
   }

   /**
    * @return Returns the retrieveLogData.
    */
   public boolean isRetrieveLogData()
   {
      return mRetrieveLogData;
   }

   /**
    * @param aRetrieveLogData The retrieveLogData to set.
    */
   public void setRetrieveLogData(boolean aRetrieveLogData)
   {
      mRetrieveLogData = aRetrieveLogData;
   }

   /**
    * Sets the retrieveLogData flag.
    * @param aRetrieveLogData The retrieveLogData to set. This should be true or false.
    */
   public void setRetrieveLogDataString(String aRetrieveLogData)
   {
      if ( !AeUtil.isNullOrEmpty(aRetrieveLogData) && "true".equalsIgnoreCase(aRetrieveLogData.trim()))  //$NON-NLS-1$
      {
         setRetrieveLogData(true);
      }
      else
      {
         setRetrieveLogData(false);
      }
   }

   /**
    * Overrides method to (when applicable) either suspend,resume or terminate process before
    * building the process model.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBase#initialize()
    */
   protected void initialize()
   {
      if (getProcessOp() == SUSPEND)
      {
         suspendProcess();
      }
      else if (getProcessOp() == RESUME)
      {
         resumeProcess();
      }
      else if (getProcessOp() == TERMINATE)
      {
         terminateProcess();
      }
      else if (getProcessOp() == RESTART)
      {
         restartProcess();
      }
      super.initialize();
   }

   /**
    * Setting the terminate flag causes the process to be terminated.
    */
   public void setTerminate(boolean aFlag)
   {
      if(aFlag)
      {
         setProcessOp(TERMINATE);
      }
   }

   /**
    * Terminates the process.
    */
   protected void terminateProcess()
   {
      try
      {
         AeEngineFactory.getEngine().terminateProcess( getProcessId());
         setStateChanged(true);
      }
      catch (AeBusinessProcessException e)
      {
         String error = AeMessages.getString("AeProcessViewBean.1"); //$NON-NLS-1$
         error += AeUtil.getStacktrace(e);
         setMessage(error);
      }

   }

   /**
    * Setting the suspend flag causes the process to be suspended.
    */
   public void setSuspend(boolean aFlag)
   {
      if(aFlag)
      {
         setProcessOp(SUSPEND);
      }
   }

   /**
    * Suspends the process.
    */
   protected void suspendProcess()
   {
      try
      {
         AeEngineFactory.getEngine().suspendProcess( getProcessId());
         setStateChanged(true);
      }
      catch (AeBusinessProcessException e)
      {
         String error = AeMessages.getString("AeProcessViewBean.2"); //$NON-NLS-1$
         error += AeUtil.getStacktrace(e);
         setMessage(error);
      }
   }

   /**
    * Setting the resume flag causes the process to be resumed.
    */
   public void setResume(boolean aFlag)
   {
      if(aFlag)
      {
         setProcessOp(RESUME);
      }
   }

   /**
    * Resumes the process.
    */
   protected void resumeProcess()
   {
      try
      {
         AeEngineFactory.getEngine().resumeProcess( getProcessId() );
         setStateChanged(true);
      }
      catch (AeBusinessProcessException e)
      {
         String error = AeMessages.getString("AeProcessViewBean.3"); //$NON-NLS-1$
         String stacktrace = AeUtil.getStacktrace(e);
         error += stacktrace;
         setMessage(error);
      }
   }

   /**
    * Setting the restart flag causes the process to be restarted.
    */
   public void setRestart(boolean aFlag)
   {
      if (aFlag)
      {
         setProcessOp(RESTART);
      }
   }

   /**
    * Restarts the process.
    */
   protected void restartProcess()
   {
      try
      {
         AeEngineFactory.getEngine().restartProcess(getProcessId());
         setStateChanged(true);
      }
      catch (AeBusinessProcessException e)
      {
         String error = AeMessages.getString("AeProcessViewBean.4"); //$NON-NLS-1$
         error += AeUtil.getStacktrace(e);
         setMessage(error);
      }
   }

   //----------[ Button States ]------------------------------------------------

   /**
    * Returns true if the process can be terminated.
    */
   public boolean isTerminatable()
   {
      return AeProcessViewButtonStates.isTerminatable( getProcessDetails() );
   }

   /**
    * Returns true if the process can be suspended.
    */
   public boolean isSuspendable()
   {
      return AeProcessViewButtonStates.isSuspendable( getProcessDetails() );
   }

   /**
    * Returns true if the process can be resumed.
    */
   public boolean isResumable()
   {
      return AeProcessViewButtonStates.isResumable( getProcessDetails() );
   }
   
   /**
    * Returns true if the process restart feature is enabled at the engine level.
    */
   public boolean isRestartEnabled()
   {
      return AeEngineFactory.getEngineAdministration().getEngineConfig().isProcessRestartEnabled(); 
   }

   /**
    * Returns <code>true</code> if and only if the process can be restarted.
    */
   public boolean isRestartable()
   {
      return AeProcessViewButtonStates.isRestartable(getProcessDetails());
   }

   /**
    * @return true if any of the activity level actions can be performed.
    */
   public boolean isEnableActivityActions()
   {
      return !isRootProcess() && (isCompleteActivityEnabled() || isRetryActivityEnabled() || isExecuteActivityEnabled());
   }

   /**
    * Return true if this process view contains an activity bpel object with
    * a state of ready to execute, executing or faulting.
    */
   public boolean isCompleteActivityEnabled()
   {
      ensureProcessDetailsAreLoaded();
      return AeProcessViewButtonStates.isCompleteActivityEnabled( getProcessDetails(), getBpelObject() );
   }

   /**
    * Return true if this process view contains an activity bpel object with
    * a state of executing or faulting.
    */
   public boolean isRetryActivityEnabled()
   {
      ensureProcessDetailsAreLoaded();
      return AeProcessViewButtonStates.isRetryActivityEnabled( getProcessDetails(), getBpelObject() );
   }

   /**
    * Return true if this process view contains an activity bpel object with
    * a state of ready to execute.
    */
   public boolean isExecuteActivityEnabled()
   {
      ensureProcessDetailsAreLoaded();
      return AeProcessViewButtonStates.isExecuteActivityEnabled( getProcessDetails(), getBpelObject() );
   }

   /**
    * Loads the process instance details if needed.
    */
   protected void ensureProcessDetailsAreLoaded()
   {
      if( getProcessDetails() == null )
      {
         loadProcessInstanceDetails();
      }
   }

   /**
    * @return Returns the stateChanged.
    */
   public boolean isStateChanged()
   {
      return mStateChanged;
   }
   /**
    * @param aStateChanged The stateChanged to set.
    */
   public void setStateChanged(boolean aStateChanged)
   {
      mStateChanged = aStateChanged;
   }


   /**
    * @return Returns the processOp.
    */
   protected int getProcessOp()
   {
      return mProcessOp;
   }
   /**
    * @param aProcessOp The processOp to set.
    */
   protected void setProcessOp(int aProcessOp)
   {
      mProcessOp = aProcessOp;
   }

   /**
    * @return image tile width in pixels.
    */
   public int getTileWidth()
   {
      return getTileSize();
   }

   /**
    * @return image tile height in pixels.
    */
   public int getTileHeight()
   {
      return getTileSize();
   }

   /**
    * @return Returns the image tile size from the graph properties.
    */
   protected int getTileSize()
   {
      int size = AeGraphProperties.getInstance().getPropertyInt(AeGraphProperties.IMAGE_TILE_SIZE, -1);
      if (size >= MINIMUM_IMAGE_TILE_SIZE)
      {
         return size;
      }
      else
      {
         return DEFAULT_IMAGE_TILE_SIZE;
      }
   }
}
