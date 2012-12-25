//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/controller/IAeGraphController.java,v 1.1 2005/04/18 18:32:01 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//                PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.ui.controller;

import java.util.List;

import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;

/**
 * Inteface that defines a controller based on the Model-View-Controller.
 */
public interface IAeGraphController
{
   /**
    * Returns the main view. 
    * @return main figure
    */
   public AeGraphFigure getFigure();
   
   /**
    * Returns the content view.
    * @return figure which contains the content of the model.
    */
   public AeGraphFigure getContentFigure();
   
   /**
    * Parent of this controller.
    * @return parent controller.
    */
   public IAeGraphController getParent();
   
   /**
    * Sets the parent controller.
    * @param aParent parent controller.
    */
   public void setParent(IAeGraphController aParent);
   
   /**
    * Adds a child controller.
    * @param aChild child controller.
    */
   public void addChild(IAeGraphController aChild);
   
   /**
    * Returns list of child controllers.
    * @return list of IAeGraphController child objects.
    */
   public List getChildren();
   
   /**
    * Returns the model associated with this controller.
    * @return model
    */
   public Object getModel();
   
   /**
    * Sets the model that is associated with this view.
    * @param model controller model.
    */
   public void setModel(Object model);
   
   /**
    * Returns list of model's children.
    * @return list of child models contained in the model.
    */
   public List getModelChildren();

}
