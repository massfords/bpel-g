//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/figure/AeSequenceContainerFigure.java,v 1.1 2005/04/18 18:31:47 pjayanetti Exp $
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
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Container figure which draws the implicit links between child activities.
 */
public class AeSequenceContainerFigure extends AeContainerFigure
{

   /**
    * Constructs this container with the given name.
    * @param aName
    */
   public AeSequenceContainerFigure(String aName)
   {
      super(aName);
   }

   /** 
    * Overrides method to draw the implicit links between child activities. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.AeContainer#paintComponent(java.awt.Graphics)
    */
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      Color c = g.getColor();
            
      int n = getComponentCount();
      Rectangle rv1 = new Rectangle();
      Rectangle rv2 = new Rectangle();
      
      for (int i = 0; i < n - 1; i++)
      {
         Component child1 = getComponent(i);
         Component child2 = getComponent(i + 1);
         child1.getBounds(rv1);
         child2.getBounds(rv2);            
         int offset1 = 0;
         int offset2 = 0;

         boolean eval2 = false;
         if (child1 instanceof AeBpelFigureBase)
         {
            offset1 = ((AeBpelFigureBase)child1).getLabel().getLocation().y;
         }
         
         if (child2 instanceof AeBpelFigureBase)
         {
            offset2 = ((AeBpelFigureBase)child2).getLabel().getLocation().y;
            eval2 = ((AeBpelFigureBase)child2).isEvaluated();
         }
         
         int x1 = rv1.x + rv1.width/2;
         int y1 = rv1.y + rv1.height - offset1;
         int x2 = rv2.x + rv2.width/2;
         int y2 = rv2.y + offset2; 
         if (child1 instanceof Container)
         {
            Insets insets = ((Container)child1).getInsets();
            y1 -= (insets.bottom);
         }
         if (child2 instanceof Container)
         {
            Insets insets = ((Container)child2).getInsets();
            y1 += (insets.top);
         }
         g.setColor(eval2 ? getUiPrefs().getLinkActiveColor() : getUiPrefs().getLinkNormalColor());
         g.drawLine(x1, y1, x2, y2);
         
         Polygon p = new Polygon();
         p.addPoint(x2, y2);
         int dx = 2;
         int dy = 4;
         p.addPoint(x2 - dx, y2 - dy);
         p.addPoint(x2 + dx, y2 - dy);
         g.fillPolygon(p);         
      }            
      g.setColor(c);
   }   
}
