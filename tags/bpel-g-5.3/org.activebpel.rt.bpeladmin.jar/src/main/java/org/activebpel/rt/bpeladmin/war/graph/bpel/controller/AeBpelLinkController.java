//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelLinkController.java,v 1.1 2005/04/18 18:31:52 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelLinkFigure;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelLinkObject;

/**
 * Controller which creates a link figure.
 */
public class AeBpelLinkController extends AeBpelControllerBase
{

   /**
    * Default constructor.
    */
   public AeBpelLinkController()
   {
      super();
   }

   /** 
    * Overrides method to return AeBpelLinkFigure.
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.controller.AeGraphController#createFigure()
    */
   protected AeGraphFigure createFigure()
   {
      AeBpelLinkFigure fig = new AeBpelLinkFigure(getLabelText());
      // set draw visual clues.
      AeBpelLinkObject linkModel = (AeBpelLinkObject)getBpelModel();      
      fig.setDrawTransistionCondition( linkModel.hasTransistionCondition());
      fig.setEvaluated(linkModel.isEvaluated());
      fig.setInactive(getAnimationState() == INACTIVE_STATE);
      return fig;
   }   
}
