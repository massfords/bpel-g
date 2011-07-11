//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelBandedContainerController.java,v 1.1 2005/04/18 18:31:54 pjayanetti Exp $
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

import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBandedContainerFigure;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeFlowLayoutManager;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;

/**
 * Base controller for creating banded figures. A Scope activity's controller uses
 * an implementation of this controller.
 */
public class AeBpelBandedContainerController extends AeBpelContainerController
{

   /**
    * Default constructor.
    */
   public AeBpelBandedContainerController()
   {
      super();      
   }
   
   /** 
    * Overrides method to return flow layout manager. 
    * @see org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelContainerController#getContentLayoutManager(org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure)
    */
   protected LayoutManager getContentLayoutManager(AeGraphFigure aForFigure)
   {
      return new AeFlowLayoutManager(true);
   }
   
   /** 
    * Overrides method to a AeBandedContainerFigure. 
    * @see org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelContainerController#createContentFigure()
    */
   protected AeGraphFigure createContentFigure()
   {
      AeBandedContainerFigure contents = new AeBandedContainerFigure("BANDEDCONTENTS_" + getLabelText());  //$NON-NLS-1$      
      return contents;
   }
}
