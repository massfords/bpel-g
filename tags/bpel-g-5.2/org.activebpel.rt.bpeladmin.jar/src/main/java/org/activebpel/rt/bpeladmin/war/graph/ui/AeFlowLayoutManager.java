//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/AeFlowLayoutManager.java,v 1.1 2005/04/18 18:31:46 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
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

/**
 * Implements a simple layout based on the left to right flow layout algorithm.
 */
public class AeFlowLayoutManager extends AeLayoutManagerAdopter
{
   /** If true, layout direction is horizontal. */ 
   private boolean mHorizontal = true;
   /** Horizontal gap */   
   private int mHgap = 5;
   /** Vertical gap */
   private int mVgap = 5;
   
   /**
    * Constructs horizontal flow layout.
    */
   public AeFlowLayoutManager()
   {
      this(true);
   }

   /**
    * Constructs flow layout.
    * @param aHorizontal if true, the layout is horizontal, otherwise vertical layout is assumed.
    */   
   public AeFlowLayoutManager(boolean aHorizontal)
   {
      super();
      mHorizontal = aHorizontal;
   }
     
   /**
    * @return Returns the horizontal.
    */
   public boolean isHorizontal()
   {
      return mHorizontal;
   }
   
   /**
    * @param aHorizontal The horizontal to set.
    */
   public void setHorizontal(boolean aHorizontal)
   {
      mHorizontal = aHorizontal;
   }
   
   /**
    * @return Returns the hgap.
    */
   public int getHgap()
   {
      return mHgap;
   }
   
   /**
    * @param aHgap The hgap to set.
    */
   public void setHgap(int aHgap)
   {
      mHgap = aHgap;
   }
   
   /**
    * @return Returns the vgap.
    */
   public int getVgap()
   {
      return mVgap;
   }
   
   /**
    * @param aVgap The vgap to set.
    */
   public void setVgap(int aVgap)
   {
      mVgap = aVgap;
   }
   
   /**
    * Overrides method to 
    * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
    */
   public Dimension maximumLayoutSize(Container aTarget)
   {
      return preferredLayoutSize(aTarget);
   }  
   
   /**
    * Overrides method to 
    * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
    */
   public Dimension preferredLayoutSize(Container aParent)
   {
      return calculateLayoutSize(aParent, false);
   }

   /**
    * Overrides method to 
    * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
    */
   public Dimension minimumLayoutSize(Container aParent)
   {
      return calculateLayoutSize(aParent, true);
   }   
   
   /**
    * Calculates the preferred size.
    * @param aParent container being layed out.
    * @param aMinimumSize if true, calculates the minimum size required.
    * @return preferred size for this layout.
    */
   private Dimension calculateLayoutSize(Container aParent, boolean aMinimumSize)
   {
      Dimension dim = new Dimension();
      int hgap = getHgap();
      int vgap = getVgap();
      int count = aParent.getComponentCount();
      for (int i = 0; i < count; i++)
      {
         Component child = aParent.getComponent(i);
         if (!child.isVisible())
         {
            continue;
         }
         Dimension childSize = aMinimumSize ? child.getMinimumSize() : child.getPreferredSize();
         if (isHorizontal())
         {
           dim.height = Math.max(dim.height, childSize.height);
           dim.width = dim.width +  childSize.width + hgap;
         }
         else
         {
            dim.width = Math.max(dim.width, childSize.width);
            dim.height = dim.height +  childSize.height + vgap;            
         }
      }
      dim.width = aParent.getInsets().left + dim.width + aParent.getInsets().right + hgap * 2;
      dim.height = aParent.getInsets().top + dim.height + aParent.getInsets().bottom + vgap * 2;
      return dim;
   }   
   
   /**
    * Overrides method to 
    * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
    */
   public void layoutContainer(Container aParent)
   {
      int hgap = getHgap();
      int vgap = getVgap();      
      int leftMargin = aParent.getInsets().left + hgap;
      int rightMargin = aParent.getInsets().right + hgap;
      int topMargin = aParent.getInsets().top + vgap;
      int botMargin = aParent.getInsets().bottom + vgap;
      int maxX = aParent.getSize().width - rightMargin;
      int maxY = aParent.getSize().height - botMargin;
      
      int n = aParent.getComponentCount();
      int x = leftMargin;
      int y = topMargin;
      int rowH = 0;
      int colW = 0;
      
      for (int i = 0; i < n; i++)
      {
         Component child = aParent.getComponent(i);
         Dimension dim = child.getPreferredSize();
         child.setSize(dim);
         //center component for vertical layouts
         if (!isHorizontal())
         {
            x = leftMargin + (leftMargin + maxX - dim.width) / 2; 
         }
         child.setLocation(x, y);                 
         if (isHorizontal())
         {
            rowH = Math.max(rowH, dim.height);
            x = x + dim.width + hgap;            
            if (x  >= maxX)
            {
               x = leftMargin;
               y = y + rowH + vgap;
               rowH = 0;               
            }
         }
         else
         {
            colW = Math.max(colW, dim.width);
            y = y + dim.height + vgap;
            if (y > maxY)
            {
               y = topMargin;
               x = x + colW + hgap;
               colW = 0;
            }//end if y > max
         } //end  else if horizontal      
      } //for            
   }    
}
