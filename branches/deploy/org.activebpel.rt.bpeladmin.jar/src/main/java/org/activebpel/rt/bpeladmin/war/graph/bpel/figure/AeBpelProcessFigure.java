//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/figure/AeBpelProcessFigure.java,v 1.1 2005/04/18 18:31:46 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.figure;

import org.activebpel.rt.bpeladmin.war.graph.ui.AeIconLabel;

/**
 * Figure to represent the root Process. This class overrides the addLabel()
 * method so that the label is not shown.
 */
public class AeBpelProcessFigure extends AeBpelFigureBase
{

   /**
    * Constructs the figure with the given name
    * @param aBpelName
    */
   public AeBpelProcessFigure(String aBpelName)
   {
      super(aBpelName);
   }

   /** 
    * Overrides method so that the label is not added to this component. 
    * @see org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase#addLabel(org.activebpel.rt.bpeladmin.war.graph.ui.AeIconLabel)
    */
   protected void addLabel(AeIconLabel aLabel)
   {
   }  
     
}
