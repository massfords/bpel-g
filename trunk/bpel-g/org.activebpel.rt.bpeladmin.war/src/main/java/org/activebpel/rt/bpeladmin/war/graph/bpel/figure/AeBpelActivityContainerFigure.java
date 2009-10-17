//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/figure/AeBpelActivityContainerFigure.java,v 1.1 2005/04/18 18:31:47 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.figure;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import org.activebpel.rt.bpeladmin.war.graph.ui.AeIconLabel;

/**
 * A container for a BPEL activity which contain child activities.
 */
public class AeBpelActivityContainerFigure extends AeBpelFigureBase
{

   /**
    * Constructs the container with the given name.
    * @param aBpelName
    */
   public AeBpelActivityContainerFigure(String aBpelName)
   {
      this(aBpelName,null);      
   }
   
   /**
    * Constructs the Acitivity container with the given name and BPEL activity icon.
    * @param aBpelName name of container
    * @param aIconImage acitivity icon.
    */
   public AeBpelActivityContainerFigure(String aBpelName, Image aIconImage)
   {   
      super(aBpelName, aIconImage);   
   }

   /** 
    * Overrides method to add the BPEL activity icon and label so that is is displayed
    * at the top center (Border layout North). 
    * @see org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase#addLabel(org.activebpel.rt.bpeladmin.war.graph.ui.AeIconLabel)
    */
   protected void addLabel(AeIconLabel aLabel)
   {     
      setLayout( new BorderLayout(0,15) );
      if (aLabel != null)
      {
         add(aLabel, BorderLayout.NORTH);
      }
   }  
   
   /** 
    * Overrides method to perform the AWT border layout followed by centering of the icon label. 
    * @see java.awt.Component#doLayout()
    */
   public void doLayout()
   {
      super.doLayout();
      if (getLabel() != null && getLabel().getIcon() != null)
      {
         int width = getLabel().getPreferredSize().width;
         int x = getLabel().getLocation().x;
         x = x + width/2 - getLabel().getIcon().getIconWidth()/2 ;
         getLabel().setLocation(x,getLabel().getLocation().y);
      }
   }
   
   /** 
    * Overrides method to draw debug lines (to help trace bounding boxes). The debug lines
    * are drawn only if debug draw mode is enabled. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.AeContainer#paintComponent(java.awt.Graphics)
    */
   public void paintComponent(Graphics g) 
   {
      super.paintComponent(g);
      AeContainerFigure contentFig = getContentFigure();
      if (AeUiPrefs.isDrawDebugLines())
      {      
         int height = this.getHeight();
         int width = this.getWidth();
         Color c = g.getColor();
         g.setColor(getUiPrefs().getDebugActivityContainerColor());
         g.drawRect(0, 0, width - 1, height - 1);
         int x = width / 2;
         c  = c.darker();
         g.drawLine(x,0, x, height - 1);
         g.setColor(c);
      }
      
      // draw vertical line join icon/label with content container.
      if (contentFig != null)
      {
         Color c = g.getColor();
         g.setColor(isEvaluated() ? getUiPrefs().getLinkActiveColor() : getUiPrefs().getBorderColor());
         int x = getWidth()/2;
         int y = getLabel().getLocation().y + getLabel().getHeight(); 
         g.drawLine(x, y, x, contentFig.getLocation().y);
         g.setColor(c);
      }
      
   } 
   
   /**
    * Returns the container which actually holds other child activities.
    * @return Figure which contains the child figures.
    */
   public AeContainerFigure getContentFigure()
   {    
      int n = getComponentCount();
      for (int i = 0; i < n; i++)
      { 
         Component child = getComponent(i);
         if (getLabel() != null && child != getLabel())
         {                        
            return (AeContainerFigure) child;
         }
      }
      return null;
   } 
   
   /** 
    * Overrides method to return the bounding box for this component. 
    * @see org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase#getAnchorComponent()
    */
   public Component getAnchorComponent()
   {
      return this;      
   }
   
   /** 
    * Overrides method to return the bounding box of the Activity's label and icon if available,
    * otherwise the bounding box of this component is returned as the anchor box. 
    * @see org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase#getAnchorBounds()
    */
   public Rectangle getAnchorBounds()
   {
      Rectangle r = new Rectangle();
      AeContainerFigure contentFig = getContentFigure();
      if (getLabel() != null && contentFig != null)
      {
         getLabel().getBounds(r);
         r = r.union(contentFig.getBounds());
         int width = getPreferredSize().width;
         int height = contentFig.getBounds().y + contentFig.getPreferredSize().height - getLabel().getLocation().y;
         r.setSize(width, height);
      }
      else
      {
         getBounds(r);
      }
      return r;
   }    
}
