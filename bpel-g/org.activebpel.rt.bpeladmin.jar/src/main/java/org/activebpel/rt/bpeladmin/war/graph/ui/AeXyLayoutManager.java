//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/AeXyLayoutManager.java,v 1.1 2005/04/18 18:31:46 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//          PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Implements a arbitrary coordinate based layout mechanism..
 */
public class AeXyLayoutManager extends AeLayoutManagerAdopter
{

   /**
    * Default constructor. 
    */
   public AeXyLayoutManager()
   {
      super();
   }
      
   /**
    * Overrides method to calculate the minumum size. 
    * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
    */
   public Dimension minimumLayoutSize(Container aParent)
   {
      int n = aParent.getComponentCount();
      Rectangle rect = new Rectangle();
      Rectangle rv = new Rectangle();
      for (int i = 0; i < n; i++)
      {  
         Component c = aParent.getComponent(i);
         c.getBounds(rv);
         rect = rect.union(rv);
      } 
      return aParent.getSize();
   }   
   
   /**
    * Overrides method to layout the components based on their absolute locations.
    * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
    */
   public void layoutContainer(Container aParent)
   {
      int n = aParent.getComponentCount();
      int x = 5;
      for (int i = 0; i < n; i++)
      {
         Component c = aParent.getComponent(i);
         Dimension dim = c.getPreferredSize();         
         c.setSize(dim);       
         x = x + 5 + dim.width;
      }             
   }   
}
