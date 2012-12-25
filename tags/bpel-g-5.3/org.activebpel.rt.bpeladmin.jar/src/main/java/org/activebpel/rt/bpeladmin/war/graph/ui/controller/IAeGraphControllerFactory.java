//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/controller/IAeGraphControllerFactory.java,v 1.1 2005/04/18 18:32:01 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.ui.controller;

/**
 * Interface that defines the factory which is responsible for creating
 * the model-view-controller hierarchy.
 */
public interface IAeGraphControllerFactory
{
   /**
    * Creates and returns an implementation of IAeGraphController.
    * @param aContext context
    * @param model model to be assoicated with the controller.
    * @return controller.
    */
   public IAeGraphController createController(IAeGraphController aContext, Object model);
   
}
