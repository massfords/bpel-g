//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelSwitchActivityController.java,v 1.1 2005/04/18 18:31:54 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import java.awt.Color;

import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;

/**
 * The AeBpelSwitchActivityController creates figure to Switch type activities.
 */
public class AeBpelSwitchActivityController extends AeBpelChoiceContainerController
{

   /**
    * Default constructor.
    */
   public AeBpelSwitchActivityController()
   {
      super();      
   }

   /** 
    * Overrides method to change the debug draw color of the figure. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.controller.AeGraphController#createFigure()
    */
   protected AeGraphFigure createFigure()
   {
      AeBpelFigureBase fig = (AeBpelFigureBase) super.createFigure();
      fig.getUiPrefs().setDebugActivityContainerColor(Color.ORANGE);
      return fig;
   }

}
