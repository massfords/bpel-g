//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/AeTextLabel.java,v 1.2 2005/06/28 17:18:59 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import org.activebpel.rt.util.AeUtil;

/**
 * Renders a text label.
 */
public class AeTextLabel extends AeComponent
{
   /** Text to be displayed. */
   private String mText = null;
   /** Ascent of the text based on the current font selection */
   private int mAscent;
   /** Descent of the text based on the current font selection */
   private int mDescent;
   /** Vertical padding */
   private int mVerticalPadding = 2;
   /** Horizontal padding */
   private int mHorizontalPadding = 6;

   /**
    * Default constructor.
    */
   public AeTextLabel()
   {
      this("", null);//$NON-NLS-1$
   }

   /** 
    * Constructs the label with the given text.
    * @param aText text to be displayed.
    */
   public AeTextLabel(String aText)
   {
      this(aText, null);      
   }
   
   /** 
    * Constructs the label with the given text.
    * @param aText text to be displayed.
    * @param aName name of this component.
    */
   public AeTextLabel(String aText, String aName)
   {
      super(aName);
      super.setFont(new Font("Dialog", Font.PLAIN, 10));//$NON-NLS-1$
      setText(aText);
   }

   /**
    * Overrides method to set the font.
    * @see java.awt.Component#setFont(java.awt.Font)
    */
   public void setFont(Font aFont)
   {
      super.setFont(aFont);
      calculateMetrics();
   }
   
   /** 
    * Calculates the components size.
    */
   protected void calculateMetrics() 
   {          
      Font font = getFont();
      if (font == null)
      {
         return;
      }
      String text = getText();
      if (text.length() == 0) {
         text = " ";//$NON-NLS-1$ 
      }
      
      FontRenderContext frc = new FontRenderContext(null, false,false);
      LineMetrics lineMetrics = font.getLineMetrics(text,frc);
      
      int ascent = (int)lineMetrics.getAscent();
      setAscent(ascent);
      
      int descent = (int)lineMetrics.getDescent();
      setDescent(descent);
      
      Rectangle2D r2d = font.getStringBounds(text,frc);
      Rectangle r = r2d.getBounds();
      
      int width = r.width +  getHorizontalPadding();
      int height = r.height;
      if (height < lineMetrics.getHeight())
      {
         height =  (int) lineMetrics.getHeight();
      }
      height += getVerticalPadding() + (int) lineMetrics.getLeading();
      setSize(width, height);
      setPreferredSize(width, height);
   } 
   
   /**
    * @return Returns the text.
    */
   public String getText()
   {
      return mText;
   }
   
   /**
    * @param aText The text to set.
    */
   public void setText(String aText)
   {
      String oldText = mText;
      if (!AeUtil.isNullOrEmpty(aText))
      {
         mText = aText;
      }
      else
      {
         mText = ""; //$NON-NLS-1$
      }
      
      if (oldText == null || !mText.equals(oldText))
      {
         calculateMetrics();
      }
   }
   
   /**
    * @return Returns the ascent.
    */
   public int getAscent()
   {
      return mAscent;
   }
   
   /**
    * @param aAscent The ascent to set.
    */
   public void setAscent(int aAscent)
   {
      mAscent = aAscent;
   }
  
   /**
    * @return Returns the descent.
    */
   public int getDescent()
   {
      return mDescent;
   }
   
   /**
    * @param aDescent The descent to set.
    */
   public void setDescent(int aDescent)
   {
      mDescent = aDescent;
   }
   
   
   /**
    * @return Returns the horizontalPadding.
    */
   public int getHorizontalPadding()
   {
      return mHorizontalPadding;
   }
   
   /**
    * @param aHorizontalPadding The horizontalPadding to set.
    */
   public void setHorizontalPadding(int aHorizontalPadding)
   {
      mHorizontalPadding = aHorizontalPadding;
   }
   
   /**
    * @return Returns the verticalPadding.
    */
   public int getVerticalPadding()
   {
      return mVerticalPadding;
   }
   
   /**
    * @param aVerticalPadding The verticalPadding to set.
    */
   public void setVerticalPadding(int aVerticalPadding)
   {
      mVerticalPadding = aVerticalPadding;
   }
   
   /** 
    * Overrides method to render the text. 
    * @see java.awt.Component#paint(java.awt.Graphics)
    */
   public void paint(Graphics aGraphics)
   {
      Rectangle rect = getBounds();
      Color c = aGraphics.getColor();
      Font f = aGraphics.getFont();
      Font textFont = getFont();
      if (textFont != null) {
         aGraphics.setFont(textFont);
      }
      aGraphics.setColor(getForeground());
      aGraphics.drawString(getText() , 
               rect.x + getHorizontalPadding()/2, 
               rect.y + getAscent() + getVerticalPadding());
      
      aGraphics.setFont(f);
      aGraphics.setColor(c);  
   }
}
