//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelContainerImplicitActivityController.java,v 1.1 2005/04/18 18:31:52 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//          PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeXyLayoutManager;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;

/**
 * Controller which is responsible for creating implicit activity
 * containers.
 */
public class AeBpelContainerImplicitActivityController extends AeBpelControllerBase
{

   /**
    * Default constructer.
    */
   public AeBpelContainerImplicitActivityController()
   {
      super();
   }

   /** 
    * Overrides method to return a figure without an icon. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.controller.AeGraphController#createFigure()
    */
   protected AeGraphFigure createFigure()
   {      
      AeGraphFigure fig = new AeBpelFigureBase(getLabelText());
      fig.setLayout(new AeXyLayoutManager());
      return fig;
   }
         
}
