//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/AeContainer.java,v 1.1 2005/04/18 18:31:46 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//          PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;


/**
 * AeContainer UI widget acts as a container to hold AeComponent objects using
 * the composite pattern. This class is also responsible for delegating the 
 * paint calls to its children.
 */
public class AeContainer extends AeComponent
{
   /**
    * Default constructor.
    */
   public AeContainer()
   {
      this(null);
   }
   
   /**
    * Constructs the container with the given name.
    * @param aName container name.
    */
   public AeContainer(String aName)
   {
      super(aName);      
   }

   /** 
    * Overrides method to delegate paint calls to paintComponent(), paintBorder and paintChildren.
    * It is not recomended that subclasses override this method.
    * @see java.awt.Component#paint(java.awt.Graphics)
    */
   public void paint(Graphics g) 
   {
      if (!isVisible())
      {
         return;
      }      
      Point p = getLocation();
      int x = p.x;
      int y = p.y;
      g.translate(x, y);
      paintComponent(g);
      paintBorder(g);
      paintChildren(g);
      g.translate(-x,-y);
   }

   /**
    * Paints this component. Subclasses should over ride this method to render itself.
    * @param g graphics context
    */
   public void paintComponent(Graphics g) 
   {
   } 
   
   /**
    * Paints this component. Subclasses should over ride this method to render it's border.
    * @param g graphics context
    */   
   public void paintBorder(Graphics g) 
   {
   } 
   
   /**
    * Paints the children contained in this component.
    * @param g graphics context
    */   
   
   public void paintChildren(Graphics g) 
   {
      int n = getComponentCount();
      for (int i = 0; i < n; i++)
      {
         Component c = getComponent(i);
         c.paint(g);
      }         
   }      
}
