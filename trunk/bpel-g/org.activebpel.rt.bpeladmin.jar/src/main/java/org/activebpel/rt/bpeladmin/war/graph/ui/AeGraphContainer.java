//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/AeGraphContainer.java,v 1.2 2006/06/26 18:38:21 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//          PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.ui;

import java.awt.BorderLayout;
import java.util.List;

import org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphController;
import org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphControllerFactory;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;

/**
 * Basic figure container that is the root level container for all of the
 * figure components. The container is responsible for recursively creating
 * controllers and figures given the root controller. The controller factory is
 * used to create the child controls.
 */
public class AeGraphContainer extends AeGraphFigure
{
   /** Controller factory */
   private IAeGraphControllerFactory mControllerFactory = null;
   /** The root or top level controller */
   private IAeGraphController mRootController = null;
   /** The view associated with the root controller. */
   private AeGraphFigure mFigure = null;

   /**
    * Creates the graph container.
    * @param aFactory factory responsible for creating child controllers.
    */
   public AeGraphContainer(IAeGraphControllerFactory aFactory)
   {
      this(null, aFactory);
   }

   /**
    * Creates the graph container.
    * @param aName name of container.
    * @param aFactory factory responsible for creating child controllers.
    */
   public AeGraphContainer(String aName, IAeGraphControllerFactory aFactory)
   {
      super(aName);
      setLayout (new BorderLayout());
      setControllerFactory(aFactory);
   }

   /**
    * Mutator to set the factory.
    * @param aFactory
    */
   protected void setControllerFactory(IAeGraphControllerFactory aFactory)
   {
      mControllerFactory = aFactory;
   }

   /** 
    * @return The controller part factory.
    */
   public IAeGraphControllerFactory getControllerFactory()
   {
      return mControllerFactory;
   }

   /**
    * Sets the root controller. Setting the root controller creates the hiearchical system of MVCs. 
    * @param aController
    */
   public void setRootController(IAeGraphController aController)
   {
      mRootController = aController;
      initializeMvc();
   }

   /**
    * @return The root controller associated with this container.
    */
   public IAeGraphController getRootController()
   {
      return mRootController;
   }

   /**
    * Recursively creates and adds the controllers based on the root controller's hierarchy.
    */
   protected void initializeMvc()
   {
      if (getRootController() == null)
      {
         throw new NullPointerException("Root edit part is null"); //$NON-NLS-1$
      }

      IAeGraphController root = getRootController();
      // create main container for the root.
      mFigure = root.getFigure();
      
      // recursively create and add container
      AeGraphFigure panel = new AeGraphFigure();
      addChildrenUI(panel, mFigure,root);
      add(panel);
   }

   /**
    * Recursively creates the MVC.
    * @param aParentContainer
    * @param aChild
    * @param aParentController
    */
   protected void addChildrenUI(AeGraphFigure aParentContainer, AeGraphFigure aChild, IAeGraphController aParentController)
   {
      if (aParentContainer == this)
      {
         add(aChild, BorderLayout.CENTER);
      }
      else
      {
         aParentContainer.add(aChild);
      }
      
      List modelList = aParentController.getModelChildren();
      for (int i = 0; i < modelList.size(); i++)
      {
         Object model = modelList.get(i);
         IAeGraphController controller = createController(aParentController, model);
         if (controller == null)
         {
            continue;
         }
         AeGraphFigure contentFigure = aParentController.getContentFigure();
         AeGraphFigure figure = controller.getFigure();
         addChildrenUI(contentFigure, figure, controller);                        
         aParentController.addChild(controller);
      }
   }

   /**
    * Convenience method to create a controller for the given model and context.
    * @param aContext
    * @param aModel model to be associated with the controller.
    * @return controller
    */
   protected IAeGraphController createController(IAeGraphController aContext, Object aModel)
   {
      return mControllerFactory.createController(aContext, aModel);
   }
}
