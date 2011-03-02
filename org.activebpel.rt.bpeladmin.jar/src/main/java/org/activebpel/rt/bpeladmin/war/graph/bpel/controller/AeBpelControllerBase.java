//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelControllerBase.java,v 1.5 2006/07/25 17:56:38 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import java.awt.Image;
import java.util.List;

import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpeladmin.war.graph.bpel.AeBpelImageResources;
import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeUiPrefs;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeIcon;
import org.activebpel.rt.bpeladmin.war.graph.ui.controller.AeGraphController;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectContainer;
import org.activebpel.rt.util.AeUtil;

/**
 * Base controller for all BPEL definitions.
 */
public class AeBpelControllerBase extends AeGraphController
{
   /** Indicates the acitvity is in Active state or Normal if the state information is not available.*/
   public static int NORMAL_STATE = 0;
   /** Indicates an Inactive state */
   public static int INACTIVE_STATE = 1;
   /** Indicates a Dead path state */
   public static int DEADPATH_STATE = 2;
   
   /** Animation state (normal, inactive or dead path) */
   private int mAnimationState = NORMAL_STATE;
   /** Image resource manager and cache */
   private AeBpelImageResources mImageResources= null;
   /** Icon image that is to be displayed */
   private AeIcon mStateImageIcon = null;
   
   /** If true, indicates that the activity was executed.*/
   private boolean mExecuted = false;
   
   /**
    * Default constructor.
    */
   public AeBpelControllerBase()
   {
   }
   
   /**
    * @return Returns the animationState.
    */
   public int getAnimationState()
   {
      return mAnimationState;
   }
   
   /**
    * @param aAnimationState The animationState to set.
    */
   public void setAnimationState(int aAnimationState)
   {
      mAnimationState = aAnimationState;
   }
   /**
    * @return Returns the stateImageIcon.
    */
   public AeIcon getStateImageIcon()
   {
      return mStateImageIcon;
   }
   
   /**
    * @param aStateImageIcon The stateImageIcon to set.
    */
   public void setStateImageIcon(AeIcon aStateImageIcon)
   {
      mStateImageIcon = aStateImageIcon;
   }
   
   /** 
    * Returns true if the adormnent image for the given state should displayed.
    * @param aStateCode state code
    * @return true if the adornment should be displayed.
    */
   public boolean isShowStateAdornments(String aStateCode)
   {
      boolean rVal = AeUiPrefs.isShowStateAdornments();
      
      // Override user preferences:
      // always show Currently executing, Faulted  Ready to execute state adormnents.
      if ( AeBpelState.READY_TO_EXECUTE.toString().equals( aStateCode )
         || AeBpelState.EXECUTING.toString().equals( aStateCode )
         || AeBpelState.TERMINATED.toString().equals( aStateCode )
         || AeBpelState.FAULTED.toString().equals( aStateCode ))
      {
         rVal = true;
      }
      return rVal;
   }
   
   /**
    * @return Returns the executed.
    */
   public boolean isExecuted()
   {
      return mExecuted;
   }
   
   /**
    * @param aExecuted The executed to set.
    */
   public void setExecuted(boolean aExecuted)
   {
      mExecuted = aExecuted;
   }
   
   /** 
    * Overrides method to set the model as well as create the list of the model's children. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphController#setModel(java.lang.Object)
    */
   public void setModel(Object aModel)
   {      
      super.setModel(aModel);
      if (aModel == null)
      {
         return;
      }
      
      if (aModel instanceof AeBpelObjectBase)
      {
         String state = ((AeBpelObjectBase)aModel).getState();
         if (AeBpelState.DEAD_PATH.toString().equals(state)
               ||AeBpelState.TERMINATED.toString().equals(state))
         {
            setAnimationState(DEADPATH_STATE);
         }
         else if (AeBpelState.INACTIVE.toString().equals(state) 
               || AeBpelState.QUEUED_BY_PARENT.toString().equals(state))
         {
            setAnimationState(INACTIVE_STATE);
         }         
         else
         {
            setAnimationState(NORMAL_STATE);
         }
         // executed state
         if (AeBpelState.FINISHED.toString().equals(state)
               || AeBpelState.EXECUTING.toString().equals(state)
               )
         {
            setExecuted(true);
         }
      }
      
      // if the model is a model container, then add its children.
      if (aModel instanceof AeBpelObjectContainer 
            && ((AeBpelObjectContainer)aModel).getChildren().size() > 0)
      {  
         List children = ((AeBpelObjectContainer) aModel).getChildren();
         for (int i = 0; i < children.size(); i++)
         {
            AeBpelObjectBase child = (AeBpelObjectBase) children.get(i);
            // add child model
            getModelChildren().add(child);
         }// for
      }      
   }
   
   /** 
    * Convenience method to return the BPEL model.
    * @return BPEL model.
    */
   public AeBpelObjectBase getBpelModel()
   {
      return (AeBpelObjectBase) getModel();
   }
   
   
   /**
    * @return Returns the imageResources.
    */
   public AeBpelImageResources getImageResources()
   {
      return mImageResources;
   }
   
   /**
    * @param aImageResources The imageResources to set.
    */
   public void setImageResources(AeBpelImageResources aImageResources)
   {
      mImageResources = aImageResources;
   }
   
   /**
    * Returns the text to be displayed. 
    * @return text to be displayed.
    */
   protected String getLabelText()
   {
      AeBpelObjectBase model = getBpelModel();
      String label = "";  //$NON-NLS-1$
      if (model != null)
      {
         if (!AeUtil.isNullOrEmpty(model.getName()))
         {
            label = model.getName(); 
         }
         else
         {
            label = model.getTagName();
         }
      }
      return label;
   }
   
   /**
    * Returns the image associated with this BPEL definition.
    * @return bpel icon image or null if an image is not available.
    */
   public Image getActivityIconImage()
   {
      Image image = null;
      AeBpelObjectBase model = getBpelModel();
      if (model != null && getImageResources() != null)
      {
         if (getAnimationState() == DEADPATH_STATE)
         {
            image = getImageResources().getDeadpathActivityImage( model.getIconName() );
         }
         else if (getAnimationState() == INACTIVE_STATE)
         {
            image = getImageResources().getInactiveActivityImage( model.getIconName() );
         }         
         else
         {
            image = getImageResources().getActivityImage( model.getIconName() );
         }
      }
      return image;
   }
   
   /**
    * Returns the BPEL object's state adorment image.
    * @return state adornment image or null if there is no state.
    */
   public Image getStateAdornmentIconImage()
   {
      Image image = null;
      AeBpelObjectBase model = getBpelModel();
      if (model != null && isShowStateAdornments(model.getDisplayState()) &&  getImageResources() != null)
      {
         image = getImageResources().getStateAdornmentImage( model.getDisplayState() );
      }
      return image;
   }   
      
}
