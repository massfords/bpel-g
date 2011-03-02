//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelControllerFactory.java,v 1.5 2008/01/10 18:49:39 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpeladmin.war.graph.bpel.AeBpelImageResources;
import org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphController;
import org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphControllerFactory;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelActivityObject;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase;

/**
 * Factory responsible for creating controllers given the BPEL model.
 */
public class AeBpelControllerFactory implements IAeGraphControllerFactory
{
   /** Image resources and cache. */
   private AeBpelImageResources mImageResources= null;
   
   private static final Map CONTROLLER_MAP = new HashMap();
   static
   {
      // switch
      CONTROLLER_MAP.put("switch", AeBpelSwitchActivityController.class); //$NON-NLS-1$
      // container controller for if-elseif-else parts.
      CONTROLLER_MAP.put("ifelse", AeBpelIfElseActivityController.class); //$NON-NLS-1$
      
      // scope
      CONTROLLER_MAP.put("scope", AeBpelScopeActivityController.class); //$NON-NLS-1$
      
      // sequence
      CONTROLLER_MAP.put("sequence", AeBpelSequenceActivityController.class); //$NON-NLS-1$
      
      // choice parts
      CONTROLLER_MAP.put("case", AeBpelChoicePartController.class); //$NON-NLS-1$      
      CONTROLLER_MAP.put("otherwise", AeBpelChoicePartController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("if", AeBpelChoicePartController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("elseif", AeBpelChoicePartController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("else", AeBpelChoicePartController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("onMessage", AeBpelChoicePartController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("onAlarm", AeBpelChoicePartController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("onEvent", AeBpelChoicePartController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("catch", AeBpelChoicePartController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("catchAll", AeBpelChoicePartController.class); //$NON-NLS-1$
      
      // choice containers
      CONTROLLER_MAP.put("eventHandler", AeBpelChoiceContainerController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("eventHandlers", AeBpelChoiceContainerController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("faultHandler", AeBpelChoiceContainerController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("faultHandlers", AeBpelChoiceContainerController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("faultHandlerCatch", AeBpelChoiceContainerController.class); //$NON-NLS-1$
      
      // pick
      CONTROLLER_MAP.put("pick", AeBpelPickActivityController.class); //$NON-NLS-1$
      
      // other containers
      CONTROLLER_MAP.put("compensationHandler", AeBpelContainerActivityController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("while", AeBpelContainerActivityController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("repeatUntil", AeBpelContainerActivityController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("forEach", AeBpelContainerActivityController.class); //$NON-NLS-1$
      CONTROLLER_MAP.put("terminationHandler", AeBpelContainerActivityController.class); //$NON-NLS-1$
   }
   
   
   /**
    *  Default construct
    */
   public AeBpelControllerFactory()
   {
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
    * Overrides method to create and return the controller based on the BPEL model.
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphControllerFactory#createController(org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphController, java.lang.Object)
    * @param aContext the parent controller
    * @param aModel BPEL object model.
    * @return controller or null if a controller cannot be created for the given context and model.
    */
   public IAeGraphController createController(IAeGraphController aContext, Object aModel)
   {     
      if (aModel == null || !(aModel instanceof AeBpelObjectBase) )
      {         
         return null;
      }
      
      AeBpelControllerBase controller = null;
      AeBpelObjectBase bpelModel = (AeBpelObjectBase) aModel;
      // If outline only then do not draw it on canvas
      if (bpelModel.isDisplayOutlineOnly())
         return null;
      
      if (CONTROLLER_MAP.containsKey(bpelModel.getControllerType()))
      {
         Class c = (Class) CONTROLLER_MAP.get(bpelModel.getControllerType());
         if (c != null)
         {
            try
            {
               controller = (AeBpelControllerBase) c.newInstance();
            }
            catch (Exception e)
            {
               // should never reach here since all controllers have no-arg ctor
               e.printStackTrace();
            }
         }
      }
      else if ("link".equals(bpelModel.getTagName()) )  //$NON-NLS-1$
      {
         if (aContext instanceof AeBpelProcessRootController )
         {
            controller = new AeBpelLinkController();
         }
      }      
      else if ("flow".equals(bpelModel.getTagName())) //$NON-NLS-1$
      {
         AeBpelActivityObject flowModel = (AeBpelActivityObject)bpelModel;
         // check to see if the parent is a Sequence or if this Flow has any 
         // outgoing links. If this is the case, then show a Flow container figure
         // otherwise use a implicity figure.
         if ( (flowModel.getParent() != null && "sequence".equals(flowModel.getParent().getTagName()))  //$NON-NLS-1$
               || flowModel.getSourceLinks().size() > 0 || flowModel.getTargetLinks().size() > 0
               )
         {
            controller = new AeBpelContainerActivityController();
         }
         else
         {
           controller = new AeBpelContainerImplicitActivityController();
         }
      }      
      else
      {
         controller = new AeBpelSimpleActivityController();
      }
      
      if (controller != null)
      {
         if (getImageResources() != null)
         {
            controller.setImageResources(getImageResources());
         }         
         controller.setModel(bpelModel);
      }
      return controller;
   }
}
