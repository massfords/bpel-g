//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/figure/AeBpelLinkFigure.java,v 1.4 2005/06/28 17:18:59 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//          PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.figure;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;

import org.activebpel.rt.bpeladmin.war.graph.ui.AeIconLabel;

/**
 * Draws the links between the source anchor point and the target anchor point.
 */
public class AeBpelLinkFigure extends AeBpelFigureBase
{
   /** Indicates the line direction heading North (up) **/
   public static final int NORTH = 0;
   /** Indicates the line direction heading EAST (right) **/
   public static final int EAST = 1;
   /** Indicates the line direction heading South (down) **/
   public static final int SOUTH = 2;
   /** Indicates the line direction heading West (left) **/
   public static final int WEST = 3;
   /** Bounding box of the source point. The coordinates are relative to the top level (root) container */
   private Rectangle mSourceAnchorBounds = new Rectangle();
   /** Bounding box of the target point. The coordinates are relative to the top level (root) container */
   private Rectangle mTargetAnchorBounds = new Rectangle();
   /** True if this link has a transition condition */   
   private boolean mDrawTransistionCondition = false;
   /** Color of link's transition figure (yellow diamond) */
   private Color mTransistionFigureColor = new Color(255,255,0);
   /** Indicates that the link is inactive */
   private boolean mInactive = false;
      
   /**
    * Constructs the link figure with the given name.
    * @param aBpelName
    */
   public AeBpelLinkFigure(String aBpelName)
   {
      super(aBpelName, null);
   }

   /** 
    * Overrides method to not to add the icon label. 
    * @see org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase#addLabel(org.activebpel.rt.bpeladmin.war.graph.ui.AeIconLabel)
    */
   protected void addLabel(AeIconLabel aLabel)
   {
      setLayout(null);
   }
      
   /**
    * @return Returns the transistionFigureColor.
    */
   public Color getTransistionFigureColor()
   {
      return mTransistionFigureColor;
   }
   
   /**
    * @param aTransistionFigureColor The transistionFigureColor to set.
    */
   public void setTransistionFigureColor(Color aTransistionFigureColor)
   {
      mTransistionFigureColor = aTransistionFigureColor;
   }
   
   /**
    * @return Returns the inactive.
    */
   public boolean isInactive()
   {
      return mInactive;
   }
   
   /**
    * @param aInactive The inactive to set.
    */
   public void setInactive(boolean aInactive)
   {
      mInactive = aInactive;
   }
   
   /**
    * @return Returns the drawTransistionCondition.
    */
   public boolean isDrawTransistionCondition()
   {
      return mDrawTransistionCondition;
   }
   
   /**
    * @param aDrawTransistionCondition The drawTransistionCondition to set.
    */
   public void setDrawTransistionCondition(boolean aDrawTransistionCondition)
   {
      mDrawTransistionCondition = aDrawTransistionCondition;
   }
   
   /**
    * @return Returns the sourceAnchorBounds.
    */
   public Rectangle getSourceAnchorBounds()
   {
      return mSourceAnchorBounds;
   }
   
   /**
    * Sets anchor point's bounding box based on coordinates relative to the root container.
    * @param aSourceAnchorBounds The sourceAnchorBounds to set.
    */
   public void setSourceAnchorBounds(Rectangle aSourceAnchorBounds)
   {
      mSourceAnchorBounds = aSourceAnchorBounds;
   }
   
   /**
    * @return Returns the targetAnchorBounds.
    */
   public Rectangle getTargetAnchorBounds()
   {
      return mTargetAnchorBounds;
   }
   
   /**
    * Sets anchor point's bounding box based on coordinates relative to the root container.
    * @param aTargetAnchorBounds The targetAnchorBounds to set.
    */
   public void setTargetAnchorBounds(Rectangle aTargetAnchorBounds)
   {
      mTargetAnchorBounds = aTargetAnchorBounds;
      updateBounds();
   }
   
   /**
    * Calculates the bounding box of this link. The bounding box is the union of
    * the source anchor point and the target anchor point. This method is updated
    * when the source or target anchor points are updated.  
    *
    */
   public void updateBounds()
   {
      Rectangle srcRect = getSourceAnchorBounds();
      Rectangle rv = new Rectangle(srcRect.x, srcRect.y, srcRect.width, srcRect.height);
      rv = rv.union(getTargetAnchorBounds());
      setBounds(rv);
      setSize(rv.getSize());
      setPreferredSize(rv.getSize());
   }
   
   /** 
    * Overrides method to (0,0,0,0) for the insets. 
    * @see java.awt.Container#getInsets()
    */
   public Insets getInsets()
   {
      return new Insets(0,0,0,0);
   }
      
   /** 
    * Overrides method to paint the link. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.AeContainer#paintComponent(java.awt.Graphics)
    */
   public void paintComponent(Graphics g) 
   {      
      paintLink(g);
   }
   
   /**
    * Paints the links.
    * @param g
    */
   protected void paintLink(Graphics g) 
   {      
      Color c = g.getColor();      
      Color linkColor = getUiPrefs().getLinkNormalColor();
      Color transColor = getTransistionFigureColor(); 
      boolean drawDashedLine = false;
      if (isEvaluated())
      {
         linkColor = getUiPrefs().getLinkActiveColor();
      }
      else if (isInactive())
      {
         linkColor = getUiPrefs().getLinkInActiveColor();
         transColor = getUiPrefs().getBackgroundColor();
         drawDashedLine = true;
      }
      g.setColor(linkColor);
      
      int sourceDir = getAnchorDirection(getSourceAnchorBounds(),getTargetAnchorBounds(), true);
      int targetDir = getAnchorDirection(getSourceAnchorBounds(),getTargetAnchorBounds(), false);

      Point sourcePoint = getAnchorPoint(getSourceAnchorBounds(), sourceDir);
      Point targetPoint = getAnchorPoint(getTargetAnchorBounds(), targetDir);

      // container location (offset)
      int x = getLocation().x;
      int y = getLocation().y;
      
      // link source point
      int x1 = sourcePoint.x - x;
      int y1 = sourcePoint.y - y;
      // link target points
      int x2 = targetPoint.x - x;
      int y2 = targetPoint.y - y;
      // mid point between source and target points
      int mx = (x2 - x1)/2;
      int my = (y2 - y1)/2;
      
      // vector of points containing link start, end and its bendpoints.
      int px[];
      int py[];
      
      //transistion point.
      int tx = 0;
      int ty = 0;
      
      if (sourceDir == EAST && targetDir == WEST)
      {
         px = new int[4];
         py = new int[4];
         px[0] = x1 + 2;
         py[0] = y1;
         
         px[1] = x1 + mx;
         py[1] = y1;
         
         px[2] = x1 + mx;
         py[2] = y2;
         
         px[3] = x2 - 4;
         py[3] = y2;
         
         tx = px[1];
         ty = py[1] + (py[1] - py[2])/2;
      }
      else if (sourceDir == SOUTH && targetDir == WEST)
      {
         px = new int[5];
         py = new int[5];
         int d = 15;
         px[0] = x1;
         py[0] = y1 + 1;         
         px[1] = x1;
         py[1] = y1 + d;         
         
         px[2] = x2 - d;
         py[2] = y1 + d;
         
         px[3] = x2 - d;
         py[3] = y2;
         
         px[4] = x2 - 4;
         py[4] = y2;
         if (Math.abs(px[1] - px[2]) > 60)
         {
            tx = px[1] + 50 * (px[2] > px[1] ? 1 : -1);
         }
         else
         {
            tx = px[1] - (px[1] - px[2])/2;
         }
         ty = py[1];         
      }
      else
      {
         px = new int[4];
         py = new int[4];
         px[0] = x1;
         py[0] = y1 + 1;
         
         px[1] = x1;
         py[1] = y1 + my;
         
         px[2] = x2;
         py[2] = y1 + my;
         
         px[3] = x2;
         py[3] = y2 - 4;
         
         tx = px[1] + (px[2] - px[1])/2;
         ty = py[1];         
      }  

      Stroke stroke = null;
      Graphics2D g2 = (Graphics2D)g;

      if (drawDashedLine)
      {
         // if the link is in-active, then draw a dashed line
         Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[]{3,3,3,3}, 0);   
         stroke = g2.getStroke();
         g2.setStroke(dashedStroke);
      }
      // drawlink and arrow
      g.drawPolyline(px, py, px.length);
      
      if (drawDashedLine)
      {
         // restore to normal stroke.
         g2.setStroke(stroke);
      }
      paintLinkArrow(g, x2, y2, targetDir);
      
      // draw transistion point
      if (isDrawTransistionCondition())
      {
         paintTransition(g, tx, ty, linkColor, transColor);
      }
      
      if (AeUiPrefs.isDrawDebugAnchorPoints())
      {
         // for debugging - source and target anchor points and its bounding boxes.
         g.setColor(getUiPrefs().getDebugSourceAnchorColor());
         Rectangle r = getSourceAnchorBounds();
         g.drawRect(r.x - x, r.y - y, r.width, r.height);
         g.fillOval(px[0]-5, py[0]-5, 10,10);
         
         g.setColor(getUiPrefs().getDebugTargetAnchorColor());
         r = getTargetAnchorBounds();
         g.drawRect(r.x - x + 2, r.y - y + 2, r.width, r.height);
         g.fillOval(px[px.length-1]-5, py[px.length-1]-5, 10,10);
         
      }
      g.setColor(c);      
   }

   /**
    * Paints the transistion point diamond figure.
    * @param g
    * @param aX transistion point x
    * @param aY transistion point x
    * @param aBorderColor border color
    * @param aBackgroundColor background color.
    */
   protected void paintTransition(Graphics g, int aX, int aY, Color aBorderColor, Color aBackgroundColor) 
   {
      int d = 6;
      int x[] = { aX,     aX + d, aX,     aX - d};
      int y[] = { aY - d, aY,     aY + d, aY};
      g.setColor(aBackgroundColor);
      g.fillPolygon(x,y,x.length);
      g.setColor(aBorderColor);
      g.drawPolygon(x,y,x.length);
   }
   
   /**
    * Paints the arrow head, in the given direction.
    * @param g
    * @param aX x coordinate
    * @param aY y coordinate
    * @param aDir direction of arrow.
    */
   protected void paintLinkArrow(Graphics g, int aX, int aY, int aDir) 
   {
      Polygon p = new Polygon();
      p.addPoint(aX, aY);
      int dx = 4;
      int dy = 8;
      if (aDir == NORTH)
      {
         p.addPoint(aX - dx, aY - dy);
         p.addPoint(aX + dx, aY - dy);
      }
      else if (aDir == WEST)
      {
         p.addPoint(aX - dy, aY - dx);
         p.addPoint(aX - dy, aY + dx);
      }
      
      g.fillPolygon(p);
   }
   
   /**
    * Calculates and returns the direction of the link from source to the target.
    * @param aSourceRect source bounding box
    * @param aTargetRect target boudning box
    * @param aSource true if we need to direction out of the source. If false, then the direction is into the target.
    */
   private int getAnchorDirection(Rectangle aSourceRect, Rectangle aTargetRect, boolean aSource)
   {
      int dir = NORTH;
      if (aSource)
      {
         Point p1 = getAnchorPoint(aSourceRect, EAST);
         Point p2 = getAnchorPoint(aTargetRect, WEST);
         // if target is to the right of the source (E), or above, right (NE),
         // then out going link will be from the EAST.
         if (p1.x < p2.x && p1.y == p2.y)
         {
            dir = EAST;
         }
         else
         {
            dir = SOUTH;
         }         
      }
      else
      {
         Point p1 = getAnchorPoint(aSourceRect, SOUTH);
         Point p2 = getAnchorPoint(aTargetRect, NORTH);
         // if source is above the target, then incoming links to the
         // target will be from the west.
         if (p1.y > p2.y)
         {
            dir = WEST;
         }
         else
         {
            dir = NORTH;
         }
      }
      return dir;
   }
   
   /**
    * Calculates and returns the anchor point (x,y) coordinates (usually a mid point) given the
    * bounding box  and the direction.
    * @param aRect bounding box of the anchor.
    * @param aDir direction of incoming or out going link.
    */
   private Point getAnchorPoint(Rectangle aRect, int aDir)
   {
      int x = 0;
      int y = 0;
      
      if (aDir == NORTH)
      {
         x = aRect.x + aRect.width/2;
         y = aRect.y;
      }
      else if (aDir == EAST)
      {
         x = aRect.x + aRect.width;
         y = aRect.y + aRect.height/2;
      }
      else if (aDir == SOUTH)
      {
         x = aRect.x + aRect.width/2;
         y = aRect.y + aRect.height;
      }
      else 
      {
         x = aRect.x;
         y = aRect.y + aRect.height/2;
      }
      return new Point(x,y);
   }
}
