//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeProcessViewImageResources.java,v 1.4 2005/12/03 01:10:55 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import org.activebpel.rt.bpel.impl.AeBpelState;

/**
 * A utility class used to map BPEL element names to its corresponding image filename.
 */
public class AeProcessViewImageResources
{

   /** Base (context relative) path to the image resources */
   private String mImagePath = null;

   /**
    * Construts the object and uses give path to 'images' folder. 
    */
   public AeProcessViewImageResources(String aImagePath)
   {
      setImagePath(aImagePath);
   }

   
   /**
    * Construts the object and sets the default path to 'images' folder. 
    */
   public AeProcessViewImageResources()
   {
      // set default path.
      setImagePath("images"); //$NON-NLS-1$
   }

   /**
    * @return Returns the imagePath.
    */
   public String getImagePath()
   {
      return mImagePath;
   }
   
   /**
    * @param aImagePath The imagePath to set.
    */
   public void setImagePath(String aImagePath)
   {
      mImagePath = aImagePath;
   }

   /** 
    * Returns the filename (excluding path) of the image mapped to this tag
    * @param aTagName
    * @return resouce filename
    */
   public String getBpelObjectImage(String aTagName)
   {
      return aTagName + ".jpg";   //$NON-NLS-1$    
   }

   /** 
    * Returns the filename (including path) of the image mapped to this tag
    * @param aTagName
    * @return resouce filename
    */   
   public String getBpelObjectImagePath(String aTagName)
   {
      return mImagePath + "/bpel/" + getBpelObjectImage(aTagName);   //$NON-NLS-1$    
   }
   
   
   /**
    * Return the name of the image resource mapped to a given state.
    * @param aStateCode current state
    * @return image resource or null if the state code is not supported.
    */
   public String getStateImage(String aStateCode )
   {
      if ( AeBpelState.READY_TO_EXECUTE.toString().equals( aStateCode ))
         return "pickExecuteAdorn.gif";   //$NON-NLS-1$
      else if ( AeBpelState.EXECUTING.toString().equals( aStateCode ))
         return "executing.gif"; //$NON-NLS-1$
      else if ( AeBpelState.FINISHED.toString().equals( aStateCode ))
         return "executed.gif";  //$NON-NLS-1$
      else if ( AeBpelState.TERMINATED.toString().equals( aStateCode ))
         return "terminated.gif"; //$NON-NLS-1$ 
      else if ( AeBpelState.FAULTED.toString().equals( aStateCode ))
         return "fault.gif";  //$NON-NLS-1$
      else if ( AeBpelState.DEAD_PATH.toString().equals( aStateCode ))
         return "deadActivity.gif"; //$NON-NLS-1$
      else if ( AeBpelState.FAULTING.toString().equals( aStateCode ))
         return "faulting.gif"; //$NON-NLS-1$
      else if ( IAeWebProcessStates.PROCESS_STATE_RUNNING.equals( aStateCode ))
         return "executing.gif"; //$NON-NLS-1$
      else if ( IAeWebProcessStates.PROCESS_STATE_SUSPENDED.equals( aStateCode ))
         return "suspended.gif"; //$NON-NLS-1$
      else if ( IAeWebProcessStates.PROCESS_STATE_COMPLETED.equals( aStateCode ))
         return "executed.gif"; //$NON-NLS-1$
      else if ( IAeWebProcessStates.PROCESS_STATE_FAULTED.equals( aStateCode ))
         return "fault.gif"; //$NON-NLS-1$
      else  
         return null;
   }
   
   /**
    * Return the path to the image resource mapped to a given state.
    * @param aStateCode current state
    * @return image resource or null if the state code.
    */
   public String getStateImagePath(String aStateCode )
   {
      String stateImage = getStateImage(aStateCode);
      if (stateImage != null)
      { 
         return mImagePath + "/bpel/state/" + stateImage;   //$NON-NLS-1$  
      }
      else
      {
         return null;
      }
   }   
}
