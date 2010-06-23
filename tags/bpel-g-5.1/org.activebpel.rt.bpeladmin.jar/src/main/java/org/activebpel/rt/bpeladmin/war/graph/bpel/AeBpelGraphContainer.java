//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/AeBpelGraphContainer.java,v 1.6 2006/10/25 16:11:40 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelContainerImplicitActivityController;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelControllerBase;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelLinkController;
import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelProcessRootController;
import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelFigureBase;
import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeUiPrefs;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeComponent;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeGraphContainer;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeIcon;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeTextLabel;
import org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphControllerFactory;
import org.activebpel.rt.util.AeUtil;


/**
 *  This is the root level container for all BPEL activities.
 */
public class AeBpelGraphContainer extends AeGraphContainer
{
   /** Color of text used for time stamp etc. */
   public static final Color TEXT_COLOR = new Color(72,130,180);
   /** Product graphic to be displayed on the bottom right of the final image */
   private AeIcon mLogoIcon = null;
   /** Date when the graph was generated */
   private AeTextLabel mDateLabel = null;
   /** Caption for this image */
   private AeTextLabel mCaptionLabel = null;
   /** Text of the caption */
   private String mCaption = null;
      
   /**
    * Constructs the container given the controller factory.
    * @param aFactory controller factory.
    */
   public AeBpelGraphContainer(IAeGraphControllerFactory aFactory)
   {
      this(null, aFactory);
   }

   /**
    * Constructs the container given the name and controller factory.
    * @param aName name of this component.
    * @param aFactory controller factory.
    */
   public AeBpelGraphContainer(String aName, IAeGraphControllerFactory aFactory)
   {
      super(aName, aFactory);
      setBackground(Color.WHITE);
      Image image = AeBpelImageResources.getInstance().getLogoImage();
      if (image != null)
      {
         mLogoIcon = new AeIcon(image);
      }
      setDate( new Date() );
   }
   
   /**
    * Formats the given date to be displayed.
    * @param aDate to be formatted.
    * @return formatted text
    */
   private String formatDate(Date aDate)
   {
      String formattedText = null;
      if (aDate != null)
      {
         try
         {
            String datePattern = AeMessages.getString("AeBpelGraphContainer.date_time_pattern");  //$NON-NLS-1$
            Format formatter = null;
            if (!AeUtil.isNullOrEmpty(datePattern) )   
            {
               formatter = new SimpleDateFormat(datePattern);
            }
            else
            {
               // pattern not given - use default locale
               formatter = new SimpleDateFormat();
            }
            formattedText = formatter.format(aDate);
         }
         catch(Exception e)
         {
            //ignore error.
            DateFormat formatter = DateFormat.getDateTimeInstance();
            formattedText = formatter.format(aDate);
         }
      }      
      return formattedText;
   }
   
   /**
    * Sets the current time stamp
    * @param aDate current date
    */
   public void setDate(Date aDate)
   {   
      if (aDate != null)
      {
         mDateLabel = new AeTextLabel( formatDate(aDate) );
         mDateLabel.setForeground(TEXT_COLOR);
         mDateLabel.setFont(AeUiPrefs.getDefaultPrefs().getFont());
      }
   }
   
   /**
    * Sets process start and end dates instead of the current time stamp.
    * @param aStartDate process start date
    * @param aEndDate process end date   
    */
   public void setStartStopDates(Date aStartDate, Date aEndDate)
   {   
      StringBuffer sb = new StringBuffer();
      String sep = ""; //$NON-NLS-1$
      if (aStartDate != null)
      {
         sb.append(AeMessages.getString("AeBpelGraphContainer.date_started") + ": " + formatDate(aStartDate)); //$NON-NLS-1$ //$NON-NLS-2$ 
         sep = ", "; //$NON-NLS-1$
      }
      if (aStartDate != null && aEndDate != null)
      {
         sb.append(sep);
         sb.append(AeMessages.getString("AeBpelGraphContainer.date_ended") + ": " + formatDate(aEndDate) + " "); //$NON-NLS-1$  //$NON-NLS-2$ //$NON-NLS-3$
      }
      else if (aStartDate != null && aEndDate == null)
      {
         sb.append(sep);
         sb.append(AeMessages.getString("AeBpelGraphContainer.date_current") + ": " + formatDate(new Date()) + " "); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
      }
 
      if (sb.length() > 0) 
      {
         mDateLabel = new AeTextLabel( sb.toString() );
         mDateLabel.setForeground(TEXT_COLOR);
         mDateLabel.setFont(AeUiPrefs.getDefaultPrefs().getFont());
      }
   }
   
   
   /**
    * @return Returns the caption.
    */
   public String getCaption()
   {
      return mCaption;
   }
   
   /**
    * @param aCaption The caption to set.
    */
   public void setCaption(String aCaption)
   {
      mCaption = aCaption;
      if (mCaption != null)
      {
         mCaptionLabel = new AeTextLabel(mCaption);
         mCaptionLabel.setForeground(TEXT_COLOR);
         mCaptionLabel.setFont(AeUiPrefs.getDefaultPrefs().getFont());
      }
      else
      {
         mCaptionLabel = null;
      }
   }
   
   /** 
    * Overrides method to do the AWT Layout followed by the autolayout. 
    * @see java.awt.Component#doLayout()
    */
   public void doLayout()
   {
      super.doLayout();
      AeBpelProcessRootController controller = (AeBpelProcessRootController) getRootController();
      if (controller != null)
      {
         AeAutoLayout alm = new AeAutoLayout(controller);
         alm.doAutoLayout();
      }
   }
   
   /** 
    * Overrides method to calculate  preferred size after accounting for the caption and logo.  
    * @see java.awt.Component#getPreferredSize()
    */
   public Dimension getPreferredSize()
   {
      Dimension d = super.getPreferredSize();
      int minW = 0;
      int minH = 0;
      if (mDateLabel != null)
      {
         minW = mDateLabel.getWidth();
         minH  = mDateLabel.getHeight();
      }
      if (mCaptionLabel != null)
      {
         minH +=  mCaptionLabel.getHeight() + 10;
         if (mCaptionLabel.getWidth() > minW)
         {
            minW = mCaptionLabel.getWidth();
         }
      }
      if (mLogoIcon != null)
      {
         minW += mLogoIcon.getIconWidth() + 10;
         if (mLogoIcon.getIconHeight() > minH)
         {
            minH = mLogoIcon.getIconHeight();
         }
         
      }
      // add some extra padding
      minW += 15;
      minH += 10;
      d.height = d.height +  minH;               
      if (d.width < minW)
      {
         d.width = minW;
      }
      return d;
   }
   
   /** 
    * Overrides method to render the caption and logo. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.AeContainer#paintComponent(java.awt.Graphics)
    */
   public void paintComponent(Graphics g) {
      Color c = g.getColor();
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, getWidth(), getHeight());
      
      int padding = 5; 
      int left = 5;
      int bottom = 15;
      
      int logoH = mLogoIcon != null ? mLogoIcon.getIconHeight() : 0;
      int logoW = mLogoIcon != null ? mLogoIcon.getIconWidth() : 0;
      
      int textW = 0;
      int textH = 0;
      
      if (mDateLabel != null && mCaptionLabel != null)
      {
         textW = Math.max(mDateLabel.getWidth(), mCaptionLabel.getWidth());
         textH = mDateLabel.getHeight() + mCaptionLabel.getHeight();
      }
      else if (mDateLabel != null)
      {
         textW = mDateLabel.getWidth();
         textH = mDateLabel.getHeight();
      }
      else if (mCaptionLabel != null)
      {
         textW = mCaptionLabel.getWidth();
         textH = mCaptionLabel.getHeight();
      }
      
      int maxH = Math.max(logoH, textH);
      int x = left;
      int y = 0;
      g.setColor(Color.GRAY);
      if (mLogoIcon != null)
      {
         y = getHeight() - bottom - maxH + (int)((maxH - logoH)/2);
         mLogoIcon.setLocation(x, y);
         mLogoIcon.paint(g);
         x = x + logoW + padding;
      }
      
      g.setColor(TEXT_COLOR);
      y = getHeight() - bottom - maxH;
      g.drawLine(x,y, x + padding, y);
      g.drawLine(x,y, x, y + maxH);
      g.drawLine(x,y + maxH, x + padding, y + maxH);
      x = x + 2 * padding;
      y = getHeight() - bottom - maxH + (int)((maxH - textH)/2);
      if (mDateLabel != null && mCaptionLabel != null)
      {         
         mCaptionLabel.setLocation(x, y);
         y = y + mCaptionLabel.getHeight();
         mDateLabel.setLocation(x, y);
      }
      else if (mDateLabel != null)
      {
         mDateLabel.setLocation(x, y);
      }
      else if (mCaptionLabel != null)
      {
         mCaptionLabel.setLocation(x, y);
      }
            
      if (mCaptionLabel != null)
      {        
         mCaptionLabel.paint(g);
      }
      if (mDateLabel != null)
      {                 
         mDateLabel.paint(g);
      } 
      x = x + textW;
      g.setColor(TEXT_COLOR);
      y = getHeight() - bottom - maxH;
      g.drawLine(x,y, x - padding, y);
      g.drawLine(x,y, x, y + maxH);
      g.drawLine(x,y + maxH, x - padding, y + maxH);
      x = x + padding;
      
      g.setColor(c);
   }
   
   /** 
    * @return List of AeBpelActivityCoordinates objects which contains the hit test areas.
    */
   public List getCoordinateList()
   {
      List list = new ArrayList();
      AeBpelProcessRootController root = (AeBpelProcessRootController) getRootController();
      getCoordinates(root, list);
      return list;
   }
   
   /**
    * Recursivley calculates the hit test areas. The hit test area used will be the area encompasing the
    * icon and the label of the BPEL activity.
    * @param aController parent controller
    * @param list list containing the hit test objects.
    */
   private void getCoordinates(AeBpelControllerBase aController, List list)
   {
      for (int i = 0; i < aController.getChildren().size(); i++)
      {
         AeBpelControllerBase aChild = (AeBpelControllerBase) aController.getChildren().get(i);
         getCoordinates(aChild, list);
      }            
      if (aController !=  getRootController() && !(aController instanceof AeBpelLinkController)
            && !(aController instanceof AeBpelContainerImplicitActivityController) )
      {
         Rectangle r = new Rectangle();
         AeBpelFigureBase activityFig = (AeBpelFigureBase)aController.getFigure();
         AeComponent fig = activityFig.getLabel();
         fig.getBounds(r);
         AeBpelActivityCoordinates loc = new AeBpelActivityCoordinates();
         Container parentFig = fig.getParent();
         while (parentFig != null && parentFig != this)
         {
            r.x = r.x + parentFig.getLocation().x;
            r.y = r.y + parentFig.getLocation().y;
            parentFig = parentFig.getParent();  
         }
         loc.setBounds(r);
         loc.setBpelModel(aController.getBpelModel());
         list.add(loc);
      }
   }
}
