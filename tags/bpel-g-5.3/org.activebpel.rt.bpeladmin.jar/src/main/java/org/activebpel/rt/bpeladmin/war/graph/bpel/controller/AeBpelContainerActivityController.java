//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelContainerActivityController.java,v 1.2 2005/06/14 17:17:27 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import java.awt.LayoutManager;

import org.activebpel.rt.bpeladmin.war.graph.ui.AeXyLayoutManager;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;

/**
 * The AeBpelContainerActivityController creates and returns figure that will be
 * used for container type activies such as Flow, While. 
 */
public class AeBpelContainerActivityController extends AeBpelContainerController
{

   /**
    * Default constructor.
    */
   public AeBpelContainerActivityController()
   {
      super();   
   }

   /**
    * Overrides method to return a XY layout manager. 
    * @see org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelContainerController#getContentLayoutManager(org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure)
    */
   protected LayoutManager getContentLayoutManager(AeGraphFigure aForFigure)
   {
      return new AeXyLayoutManager();
   }   
}
