//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelChoicePartController.java,v 1.1 2005/04/18 18:31:52 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelActivityContainerFigure;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;

/**
 * Controller responsible for creating choice part figures such as Case, Otherwise,
 * OnMessage, OnAlarm, Catch and CatchAll.
 */
public class AeBpelChoicePartController extends AeBpelContainerActivityController
{

   /**
    * Default constructor.
    */
   public AeBpelChoicePartController()
   {
      super();
   }

   /** 
    * Overrides method to AeBpelActivityContainerFigure with an empty label (since we do not want to
    * display the label).
    * @see org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelContainerController#createContainerFigure()
    */
   protected AeGraphFigure createContainerFigure()
   {
      AeBpelActivityContainerFigure figure = new AeBpelActivityContainerFigure(" ", getActivityIconImage());  //$NON-NLS-1$
      figure.setEvaluated(isExecuted());
      return figure;
   }   
}
