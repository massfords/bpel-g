//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelContainerController.java,v 1.2 2005/06/14 17:17:27 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelActivityContainerFigure;
import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase;
import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeContainerFigure;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeIcon;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;

/**
 * Base controller for BPEL container type definitions.
 */
public abstract class AeBpelContainerController extends AeBpelControllerBase
{
   /**
    * Default constructor.
    */
   public AeBpelContainerController()
   {
      super();
   }
        
   /** 
    * Overrides method to Activity container which has its icon label in the top (North)
    * and the content (container) in the bottom (Center). 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.controller.AeGraphController#createFigure()
    */
   protected AeGraphFigure createFigure()
   {      
      AeGraphFigure figure = createContainerFigure();
      if (getStateAdornmentIconImage() != null)
      {
         AeIcon stateIcon = new AeIcon(getStateAdornmentIconImage());
         setStateImageIcon(stateIcon);
         ((AeBpelFigureBase)figure).getLabel().add(stateIcon);
      }
      
      AeGraphFigure contents = createContentFigure();
      contents.setLayout(getContentLayoutManager(contents) );      
      setContentFigure(contents);      
      figure.add(contents, BorderLayout.CENTER);
      return figure;
   }  
  
   /**
    * Creates and returns the main figure for this controller.
    */
   protected AeGraphFigure createContainerFigure()
   {
      AeBpelActivityContainerFigure figure = new AeBpelActivityContainerFigure(getLabelText(), getActivityIconImage());
      figure.setEvaluated(isExecuted());
      return figure;
   }
   
   /**
    * Creates and returns the container which holds the children.
    * @return contents figure.
    */
   protected AeGraphFigure createContentFigure()
   {
      AeGraphFigure contents = new AeContainerFigure("CONTENTS_" + getLabelText());  //$NON-NLS-1$      
      return contents;
   }
   
   /**
    * Returns the layout manager used for the contents figure.
    * @param aForFigure the content figure for which the layout manager is used.
    * @return layout manager
    */
   protected abstract LayoutManager getContentLayoutManager(AeGraphFigure aForFigure);

}
