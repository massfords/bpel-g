//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelSimpleActivityController.java,v 1.1 2005/04/18 18:31:54 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelSimpleActivityFigure;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeIcon;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;

/**
 * Controller responsible for creating AeBpelSimpleActivityFigure.  
 */
public class AeBpelSimpleActivityController extends AeBpelControllerBase
{

   /**
    * Default constructor.
    */
   public AeBpelSimpleActivityController()
   {
      super();
   }
   
   /** 
    * Overrides method to AeBpelSimpleActivityFigure.
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.controller.AeGraphController#createFigure()
    */
   protected AeGraphFigure createFigure()
   {
      AeBpelSimpleActivityFigure fig = new AeBpelSimpleActivityFigure(getLabelText(), getActivityIconImage());
      fig.setEvaluated(isExecuted());
      if (getStateAdornmentIconImage() != null)
      {
         AeIcon stateIcon = new AeIcon(getStateAdornmentIconImage());
         setStateImageIcon(stateIcon);
         fig.getLabel().add(stateIcon);
      }
      return fig;
   }   
}
