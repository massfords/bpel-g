//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/figure/AeBpelChoiceContainerFigure.java,v 1.1 2005/04/18 18:31:47 pjayanetti Exp $
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
 * Activity container which draws connecting lines from the icon label to its the child activities.
 * Switch, Pick are examples where this container is used.
 */
public class AeBpelChoiceContainerFigure extends AeContainerFigure
{

   /**
    * Constructs the container with the given name.
    * @param aBpelName
    */
   public AeBpelChoiceContainerFigure(String aBpelName)
   {
      super(aBpelName);
      setDrawBorder(false);
      setDrawBackground(true);
   }
   
   /** 
    * Overrides method to do the layout followed by moving the child activities slight up
    * so that they align with the top of this container. 
    * @see java.awt.Component#doLayout()
    */
   public void doLayout()
   {
      super.doLayout();
      // move choice part up a little bit to align with the top of the container.
      int n = getComponentCount();
      for (int i = 0; i < n; i++)
      { 
         Component child = getComponent(i);         
         child.setLocation(child.getLocation().x,0);
      }
   } 
   
   /** 
    * Overrides method to draw the connecting lines between the icon label and its children. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.AeContainer#paintChildren(java.awt.Graphics)
    */
   public void paintChildren(Graphics g) 
   {
      super.paintChildren(g);
      // draw interconnecting (horizontal) lines between each of the choice parts.
      Color c = g.getColor();
      g.setColor(getUiPrefs().getBorderColor());      
      int n = getComponentCount();
      Rectangle rv1 = new Rectangle();
      Rectangle rv2 = new Rectangle();
      for (int i = 0; i < n - 1; i++)
      {
         Component child1 = getComponent(i);
         Component child2 = getComponent(i + 1);
         child1.getBounds(rv1);
         child2.getBounds(rv2);
         int x1 = rv1.x + rv1.width/2;
         int y1 = 0;
         int x2 = rv2.x + rv2.width/2;
         int y2 = 0;
         g.drawLine(x1,y1, x2,y2);
         g.drawLine(x1,y1,x1, y1 + 5);
         if (i == (n - 2))
         {
            // vertical line for the last choice part.
            g.drawLine(x2,y2,x2, y2 + 5);
         }
      }  
      
      // check to see if an activity has been executed. If so, redraw its connecting lines
      // with the link's active (evaulated) color.
      for (int i = 0; i < n ; i++)
      {
         Component child = getComponent(i);
         if ((child instanceof AeBpelFigureBase) && ((AeBpelFigureBase)child).isEvaluated())
         {
            g.setColor(getUiPrefs().getLinkActiveColor());             
            child.getBounds(rv1);
            int x1 = rv1.x + rv1.width/2;
            int y1 = 0;
            g.drawLine(x1,y1,x1, y1 + 5);
            int x2 = getWidth()/2;
            g.drawLine(x1,y1,x2, y1);
            break;
         }
      }
      g.setColor(c);
   }     
}
