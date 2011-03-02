//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/AeAutoLayout.java,v 1.2 2005/06/28 17:19:01 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//                PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelBandedContainerController;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelControllerBase;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelLinkController;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelProcessRootController;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelSequenceActivityController;
import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase;
import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelLinkFigure;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeFlowLayoutManager;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeXyLayoutManager;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelActivityObject;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelLinkObject;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectContainer;


/**
 * AeAutoLayout laysout the BPEL activities using the algorithm found in the AWF Designer.
 */
public class AeAutoLayout
{
   /** Root controller */
   private AeBpelProcessRootController mProcess = null;
   /** Preferences used for the layout */
   private AeLayoutPrefs mLayoutPrefs = null;
   
   /**
    * Constructs the layout given the root controller. 
    */
   public AeAutoLayout(AeBpelProcessRootController aProcess)
   {
      mProcess = aProcess;
      mLayoutPrefs  = new AeLayoutPrefs();
   }
   
   /**
    * Performs the autolayout of the BPEL activities, followed by the links.
    */
   public void doAutoLayout()
   {
      layout(mProcess);
      layoutLinks();
   }
   
   /** 
    * Lays out all of the  links.
    */
   protected void layoutLinks()
   {
      // get list of controllers for links
      for (int i = 0; i < mProcess.getChildren().size(); i++)
      {
         AeBpelControllerBase aController = (AeBpelControllerBase) mProcess.getChildren().get(i);
         if (aController instanceof AeBpelLinkController)
         {
            layoutLink(mProcess, (AeBpelLinkController)aController);
         }
      }      
   }
   
   /** 
    * Laysout a given link.
    * @param aProcess root process
    * @param aLinkController child (link) controller.
    */
   protected void layoutLink(AeBpelProcessRootController aProcess, AeBpelLinkController aLinkController)
   {
      // Get the link figure and its BPEL model.
      AeBpelLinkFigure linkFigure =  (AeBpelLinkFigure) aLinkController.getFigure();
      AeBpelLinkObject linkModel = (AeBpelLinkObject) aLinkController.getBpelModel();
      
      // Get the source and target models.
      AeBpelActivityObject sourceModel = linkModel.getSource();
      AeBpelActivityObject targetModel = linkModel.getTarget();
      
      // Get the source and target's controllers.
      AeBpelControllerBase sourceController = findController(aProcess, sourceModel);
      AeBpelControllerBase targetController = findController(aProcess, targetModel);
      
      if (sourceController == null || targetController == null)
      {
         // do not show the link if it is going out or coming in from outside of this scope.
         // (for example, if we are rendering the FaultHandlers, the link referenced here
         // maybe from the EventHandlers).
         linkFigure.setVisible(false);
         return;
      }
      
      // get the source and target figure to which this link is connected to.
      AeBpelFigureBase sourceFigure = (AeBpelFigureBase) sourceController.getFigure();
      AeBpelFigureBase targetFigure = (AeBpelFigureBase) targetController.getFigure();
      
      // The anchor points - typically the bounding box of the icon image.
      Rectangle sourceAnchor = sourceFigure.getAnchorBounds();
      Rectangle targetAnchor = targetFigure.getAnchorBounds();
      
      // translate the coordinates so that they are relative to the process's figure.
      translateCoordinates(aProcess.getFigure(), sourceFigure.getAnchorComponent(), sourceAnchor);
      translateCoordinates(aProcess.getFigure(), targetFigure.getAnchorComponent(), targetAnchor);
      
      // set this information in the link figure.
      linkFigure.setSourceAnchorBounds( sourceAnchor );
      linkFigure.setTargetAnchorBounds( targetAnchor );      
   }
   
   /**
    * Translate the given rectangle to so that it is relative to the given parent container.
    * @param aParentFigure reference container.
    * @param aChildFigure figure containing the rectangle.
    * @param aRect rectangle which needs to be translated.
    */
   protected void translateCoordinates(Container aParentFigure, Component aChildFigure, Rectangle aRect)
   {
      
      Container container = aChildFigure.getParent();
      int x = aChildFigure.getLocation().x;
      int y = aChildFigure.getLocation().y;
      Point p = container.getLocation();
      while (container != null && container != aParentFigure)
      {         
         x += p.x;
         y += p.y;
         container = container.getParent();
         p = container.getLocation();
         
      }
      aRect.setLocation(x,y);
   }
   
   /**
    * Finds the controller associated with the given BPEL model.
    * @param aParentController parent controller.
    * @param aModel model.
    * @return controller associated with the model.
    */
   protected AeBpelControllerBase findController(AeBpelControllerBase aParentController, AeBpelObjectBase aModel)
   {
      AeBpelControllerBase controller = null;
      AeBpelObjectBase m = aParentController.getBpelModel();
      if (m == aModel)
      {
         return aParentController;
      }
      List children = aParentController.getChildren();
      for (int i = 0; i < children.size(); i++)
      {
         controller = findController( (AeBpelControllerBase) children.get(i), aModel);
         if  (controller != null)
         {
            return controller;
         }
      }
      return null;
   }
   
   /**
    * Recursively lays out the components.
    * @param aController
    */
   protected void layout(AeBpelControllerBase aController)
   {
      // depth first recursion.
      List children = aController.getChildren();
      for (int i = 0; i < children.size(); i++)
      {
         AeBpelControllerBase child = (AeBpelControllerBase) children.get(i);
         layout(child);
      }
      LayoutManager layoutMgr = aController.getContentFigure().getLayout();
      Dimension dim = null;

      if (layoutMgr != null && layoutMgr instanceof AeXyLayoutManager)
      {
         dim = layoutXyContainer(aController);
      }
      else if (layoutMgr != null && layoutMgr instanceof AeFlowLayoutManager)
      {
         if (aController instanceof AeBpelSequenceActivityController)
         {
            dim = layoutSequenceContainer(aController);
         }
         else
         {
            dim = layoutChoiceContainer(aController);
         }
      }
      else if (aController instanceof AeBpelBandedContainerController)
      {
         dim = layoutBandedContainer(aController);
      }
      else
      {
         dim = aController.getContentFigure().getPreferredSize();
      }
      
      if (dim != null)
      {
         aController.getContentFigure().setSize(dim);
      }
      
      if (aController.getFigure() != aController.getContentFigure())
      {
         // adjust if the figure and the content figure are not the same.
         AeGraphFigure fig = aController.getFigure();
         dim = fig.getPreferredSize();      
         aController.getFigure().setSize(dim);
      }
      
   }

   /**
    * Lays out the BPEL activities found in sequence container.
    * @param aController Sequence controller
    * @return size of the container after the layout.
    */
   protected Dimension layoutSequenceContainer(AeBpelControllerBase aController)
   {
      AeGraphFigure fig = aController.getFigure();
      Dimension dim = fig.getPreferredSize();      
      Insets inset = fig.getInsets();
      int h = (inset.top + inset.bottom + mLayoutPrefs.getMarginBottom()); 
      int w = (inset.left + inset.right + mLayoutPrefs.getMarginRight());
      dim.width += w;
      dim.height += h;
      return dim;
      
   }
   
   /**
    * Lays out the BPEL activities found in choice container.
    * @param aController choice controller
    * @return size of the container after the layout.
    */
   protected Dimension layoutChoiceContainer(AeBpelControllerBase aController)
   {
      AeGraphFigure fig = aController.getFigure();
      LayoutManager layoutMgr = fig.getLayout();
      Dimension dim = layoutMgr.preferredLayoutSize(fig);      
      return dim;
   }
   
   /**
    * Lays out the BPEL activities found in banded container (such as a scope).
    * @param aController a banded controller
    * @return size of the container after the layout.
    */
   
   protected Dimension layoutBandedContainer(AeBpelControllerBase aController)
   {
      AeGraphFigure fig = aController.getFigure();
      Dimension dim = fig.getPreferredSize();      
      Insets inset = fig.getInsets();
      int h = (inset.top + inset.bottom + mLayoutPrefs.getMarginBottom()); 
      int w = (inset.left + inset.right + mLayoutPrefs.getMarginRight());
      dim.width += w;
      dim.height += h;
      return dim;

   }
   
   /**
    * Lays out the BPEL activities found in container where its contents's layout is based on absolute positioning.
    * @param aController controller which uses a XY layout manager
    * @return size of the container after the layout.
    */
   protected Dimension layoutXyContainer(AeBpelControllerBase aController)
   {
      Dimension dim;
      AeBpelObjectBase parentModel = aController.getBpelModel();
      AeGridTable grid = new AeGridTable(mLayoutPrefs);
     
      // Loop through all components adding them to the grid based upon their
      // distance from the root node.
      Map traversedNodes = new HashMap();
      List children = aController.getChildren();
      for (int i = 0; i < children.size(); i++)
      {
         AeBpelControllerBase child = (AeBpelControllerBase) children.get(i);
         // do not layout links
         if (child instanceof AeBpelLinkController)
         {
            continue;
         }         

         int level = getMaxChildLevel(parentModel, child.getBpelModel(), traversedNodes);
         List parents = getParentList(parentModel, child.getBpelModel());
         grid.addGridElement(child, parents, level, -1);
      }
      
      // Adjust the grid layout and reposition the components within the container
      grid.adjustLayout();
      
      if (mLayoutPrefs.getLayoutDirection() == AeLayoutPrefs.LAYOUT_VERTICAL)
      {
         dim = grid.repositionVertical();
      }
      else
      {
         dim = grid.repositionHorizontal();      
      }
      // Add parent insets to size as well as default padding
      Insets inset = aController.getContentFigure().getInsets();
      int h = (inset.top + inset.bottom + mLayoutPrefs.getMarginBottom()); 
      int w = (inset.left + inset.right + mLayoutPrefs.getMarginRight());

      dim.width += w;
      dim.height += h;
      
      return dim;      
   }
         
   /**
    * Determines if a node is a member of the container at any level. Note this 
    * method is recursive.
    *  
    * @param aContainer the container we are searching
    * @param aNode the node we are looking for
    * @return boolean flag (true if is child, false otherwise)
    */
   public static boolean isContainerMember(AeBpelObjectBase aContainer, AeBpelObjectBase aNode)
   {
      if (! (aContainer instanceof AeBpelObjectContainer) )
      {
         return false;
      }      
      boolean isMember = false;
      List children = ((AeBpelObjectContainer)aContainer).getChildren();
      
      for (int i = 0; i < children.size() && !isMember; i++)
      {  
         AeBpelObjectBase child = (AeBpelObjectBase)children.get(i);       
         isMember = (child == aNode);
         if (!isMember)
            isMember = isContainerMember(child, aNode);
      }
      return isMember;
   }   
   
   /**
    * Obtains a list of all parents for this child in a given container.
    * 
    * @param aContainer the container we are processing
    * @param aChild child node which we want parents for
    * @return a list of AeBpelActivityObject parent objects.
    */
   public static List getParentList(AeBpelObjectBase aContainer, AeBpelObjectBase aChild)
   {
      if (!(aContainer instanceof AeBpelObjectContainer) || !(aChild instanceof AeBpelActivityObject) )
      {
         return Collections.EMPTY_LIST;
      } 
      List parentList = new ArrayList();
      AeBpelActivityObject activityObj = (AeBpelActivityObject) aChild;      
      List targetConnections = activityObj.getTargetLinks();      
      for (int i = 0; i < targetConnections.size(); i++)
      {
         AeBpelLinkObject linkObj = (AeBpelLinkObject) targetConnections.get(i);
         if (isContainerMember(aContainer, linkObj.getSource()))
         {
            parentList.add( linkObj.getSource() );
         }
      }
      return parentList;
   }
   
   /**
    * Determine the maximum level that the given child is from a parent in the 
    * given container.
    * 
    * @param aContainer the container we are processing
    * @param aChild child node we are determining the max level for
    * @return maximum depth of child node in container
    */
   public static int getMaxChildLevel(AeBpelObjectBase aContainer, AeBpelObjectBase aChild, Map aTraversedNodes)
   {
      if (!(aContainer instanceof AeBpelObjectContainer) || !(aChild instanceof AeBpelActivityObject) )
      {
         return 0;
      } 
      
      int maxLevel = 0;
      aTraversedNodes.put(aChild, aChild);
      AeBpelActivityObject activityObj = (AeBpelActivityObject) aChild;      
      List targetConnections = activityObj.getTargetLinks();      
      for (int i = 0; i < targetConnections.size(); i++)
      {
         AeBpelLinkObject linkObj = (AeBpelLinkObject) targetConnections.get(i);
         AeBpelActivityObject node = linkObj.getSource();
         // if not cyclic link then traverse to find level
         if(aTraversedNodes.get(node) == null)
         {
            int childLevel = getMaxChildLevel(aContainer, node, aTraversedNodes);      
            if (isContainerMember(aContainer, node))
            {
               ++childLevel;
               // if the container is nested then increment the level (e.g. link out of switch) 
               if(node.getParent() != aContainer)
               {
                  ++childLevel;
               }
            }
            if (childLevel > maxLevel)
            {
               maxLevel = childLevel;
            }
         }         
      }// for
      aTraversedNodes.remove(aChild);
      return maxLevel;
   }
   
}
