//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/figure/AeBpelSimpleActivityFigure.java,v 1.1 2005/04/18 18:31:46 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.figure;

import java.awt.Image;

/**
 * Figure for displaying simple acitvities.
 */
public class AeBpelSimpleActivityFigure extends AeBpelFigureBase
{

   /**
    * Constructs the figure with the given name.
    * @param aBpelName
    */
   public AeBpelSimpleActivityFigure(String aBpelName)
   {
      super(aBpelName);
   }
   
   /**
    * Constructs the figure with the given name and icon image.
    * @param aBpelName
    * @param aIconImage activity icon image.
    */   
   public AeBpelSimpleActivityFigure(String aBpelName, Image aIconImage)
   {   
      super(aBpelName, aIconImage);     
   }

}
