//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/AeBpelGraph.java,v 1.3 2006/10/30 23:00:28 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph;

import java.awt.Dimension;
import java.util.Date;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpeladmin.war.graph.bpel.AeBpelGraphContainer;
import org.activebpel.rt.bpeladmin.war.graph.bpel.AeBpelImageResources;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelControllerFactory;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelProcessRootController;
import org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphController;
import org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphControllerFactory;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelProcessObject;

/**
 * AeBpelGraph is wrapper around the objects required to generate
 * a BPEL process graph.
 */
public class AeBpelGraph
{
   /** BPEL process model */
   private AeBpelProcessObject mModel = null;
   /** UI root container where the graph components are layed out */
   private AeBpelGraphContainer mGraphContainer = null;
   /** Name of process */
   private String mProcessName = null;
   /** Part id of the process to be displayed. E.g: Process, FaultHandlers, EventHandlers, CompensationHandler or TerminationHandler. */
   private int mPartId;
   /** BPEL part name */
   private String mPartName = null;
   
   /**
    * Constructs a graph object given the BPEL model, part id to be rendered and the caption or title of the graph.
    * @param aModel BPEL process model.
    * @param aPartId root part to be rendered.
    * @param aCaption text to be display at the bottom of the graph.
    * @param aStartDate process start date
    * @param aEndDate process end date    
    */
   public AeBpelGraph(AeBpelProcessObject aModel, int aPartId, String aCaption, Date aStartDate, Date aEndDate)
   {
      setModel(aModel);
      setPartId(aPartId);
      
      try
      {
         AeBpelGraphContainer graph = createGraphContainer(aModel, aPartId, aCaption, aStartDate, aEndDate);
         setGraphContainer(graph);
      }
      catch(Exception e)
      {
         AeException.logError(e, e.getMessage());
      }
   }
   
   /**
    * Width of the graph, in pixels.
    * @return width or 0 if a graph is not available.
    */
   public int getWidth()
   {
      if (getGraphContainer() != null)
      {
         return getGraphContainer().getWidth();
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
      if (getGraphContainer() != null)
      {
         return getGraphContainer().getHeight();
      }
      else
      {
         return 0;
      }
   }
   
   /**
    * @return true if the graph is availabled to be rendered.
    */
   public boolean hasImage()
   {
      return (getWidth() > 0 && getHeight() > 0);
   }
   
   /**
    * Returns the UI container that holds the graph widgets.
    * @return Returns the graphContainer.
    */
   public AeBpelGraphContainer getGraphContainer()
   {
      return mGraphContainer;
   }
   
   /**
    * @param aGraphContainer The graphContainer to set.
    */
   public void setGraphContainer(AeBpelGraphContainer aGraphContainer)
   {
      mGraphContainer = aGraphContainer;
   }
   
   /**
    * @return Returns the model.
    */
   public AeBpelProcessObject getModel()
   {
      return mModel;
   }
   
   /**
    * @param aModel The model to set.
    */
   public void setModel(AeBpelProcessObject aModel)
   {
      mModel = aModel;
      if (mModel != null)
      {
         setProcessName(mModel.getName());
      }
      else
      {
         setProcessName("");//$NON-NLS-1$
      }
   }
   
   /**
    * @return Returns the part.
    */
   public int getPartId()
   {
      return mPartId;
   }
   
   /**
    * @param aPart The part to set.
    */
   public void setPartId(int aPart)
   {
      if (aPart != AeBpelProcessRootController.PROCESS_ACTIVITY
            && aPart != AeBpelProcessRootController.COMPENSATION_HANDLER 
            && aPart != AeBpelProcessRootController.EVENT_HANDLERS
            && aPart != AeBpelProcessRootController.FAULT_HANDLERS
            )
      {
         aPart = AeBpelProcessRootController.PROCESS_ACTIVITY;
      }      
      mPartId = aPart;
   }
   
   /** 
    * @return true if this process has a global compensation handler.
    */
   public boolean hasCompensationHandler()
   {
      return (getModel() != null && getModel().getCompensationHandler() != null);
   }

   /** 
    * @return true if this process has a global termination handler.
    */
   public boolean hasTerminationHandler()
   {
      return (getModel() != null && getModel().getTerminationHandler() != null);
   }

   /** 
    * @return true if this process has event handlers.
    */
   public boolean hasEventHandlers()
   {
      return (getModel() != null && getModel().getEventHandlers() != null);
   }

   /** 
    * @return true if this process has fault handlers.
    */
   public boolean hasFaultHandlers()
   {
      return (getModel() != null && getModel().getFaultHandlers() != null);
   }
   
   
   /**
    * @return Returns the partName.
    */
   public String getPartName()
   {
      return mPartName;
   }
   
   /**
    * @param aPartName The partName to set.
    */
   public void setPartName(String aPartName)
   {
      mPartName = aPartName;
   }
   
   /**
    * @return Returns the processName.
    */
   public String getProcessName()
   {
      return mProcessName;
   }
   
   /**
    * @param aProcessName The processName to set.
    */
   public void setProcessName(String aProcessName)
   {
      mProcessName = aProcessName;
   }
   
   /**
    * Returns the root controller for this process.
    * @param aProcessModel process model
    * @param aPart part to be rendered.
    * @return root controller based on the given part.
    * @throws AeException
    */
   protected AeBpelProcessRootController getProcessController(AeBpelProcessObject aProcessModel, 
         int aPart) throws AeException
   {      
      AeBpelProcessRootController c = new AeBpelProcessRootController(aProcessModel, aPart);
      return c;
   }
   
   /**
    * Creates container to hold the UI components and lays out the components.
    * @param aProcessModel process model
    * @param aPart part to be rendered
    * @param aCaption title to be written at the bottom.
    * @param aStartDate process start date
    * @param aEndDate process end date   
    * @return UI Container that holds the graph or null if unable to create a container.
    * @throws AeException
    */
   protected AeBpelGraphContainer createGraphContainer(AeBpelProcessObject aProcessModel, int aPart, String aCaption,
            Date aStartDate, Date aEndDate)
      throws AeException
   {
      IAeGraphControllerFactory factory = createControllerFactory();
      IAeGraphController rootController = getProcessController(aProcessModel, aPart);
      AeBpelObjectBase rootModel  = (AeBpelObjectBase) rootController.getModel();
      if (rootModel == null )
      {
         return null;
      }
      String partName = rootModel.getName();
      setPartName(partName);
      AeBpelGraphContainer graphContainer = new AeBpelGraphContainer("graphcontainer_" + partName, factory); //$NON-NLS-1$
      graphContainer.setCaption(aCaption);
      graphContainer.setStartStopDates(aStartDate, aEndDate);
      graphContainer.addNotify();
      graphContainer.setRootController(rootController);
      graphContainer.validate();      
      Dimension preferredSize =  graphContainer.getPreferredSize();
      graphContainer.setSize(preferredSize.width, preferredSize.height);       
      graphContainer.validate();
      return graphContainer;
   }
   
   /**
    * Creates and returns the factory required to create the controllers.
    * @return factory that creates the controllers.
    */
   protected IAeGraphControllerFactory createControllerFactory()
   {
      AeBpelImageResources imageResources = AeBpelImageResources.getInstance();      
      AeBpelControllerFactory factory = new AeBpelControllerFactory();
      factory.setImageResources(imageResources);
      return factory;
   }   

}
