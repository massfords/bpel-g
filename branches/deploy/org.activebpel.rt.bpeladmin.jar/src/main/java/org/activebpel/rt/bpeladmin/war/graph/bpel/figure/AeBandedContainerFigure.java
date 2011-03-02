//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/figure/AeBandedContainerFigure.java,v 1.1 2005/04/18 18:31:47 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.figure;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;


/**
 * Activity container which draws vertical lines separating each of the child activities.
 * A Scope is an example of where a banded container is used.
 */
public class AeBandedContainerFigure extends AeContainerFigure
{

   /**
    * Constructs the container with the given name.
    * @param aName name of this component.
    */
   public AeBandedContainerFigure(String aName)
   {
      super(aName);
   }
      
   /** 
    * Overrides method to draw the children followed by the vertical lines separating each child. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.AeContainer#paintChildren(java.awt.Graphics)
    */
   public void paintChildren(Graphics g) 
   {
      // paint the children
      super.paintChildren(g);
      
      // draw vertical lines separating each child.
      int height = this.getHeight();
      Color c = g.getColor();
      g.setColor(Color.LIGHT_GRAY);      
      int n = getComponentCount();
      Rectangle rv = new Rectangle();
      for (int i = 0; i < n - 1; i++)
      {
         Component child = getComponent(i);
         child.getBounds(rv);
         g.drawLine(rv.x + rv.width, 0, rv.x + rv.width, height - getInsets().bottom);
      }
      // restore color.
      g.setColor(c);
   }   
}
