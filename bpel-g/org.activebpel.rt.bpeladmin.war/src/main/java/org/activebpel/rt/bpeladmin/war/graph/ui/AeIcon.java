//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/AeIcon.java,v 1.1 2005/04/18 18:31:46 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

/**
 * A component used to display an image.
 */
public class AeIcon extends AeComponent
{
   /**
    * Image to be displayed.
    */
   private Image mImage = null;
   /** Width of the image */
   private int mIconWidth = 0;
   /** Height of the image, in pixels. */
   private int mIconHeight = 0;
   
   /**
    * Contructs AeIcon using the filename of the image to be displayed.
    * @param aFilename filename of image. 
    */
   public AeIcon(String aFilename)
   {
      super( (new File(aFilename)).getName());     
      try 
      {
         Image image = AeUIUtil.loadImage(aFilename);
         setImage(image);
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
      initialize();
   }

   /**
    * Constructs with the given image.
    * @param aImage image to be displayed.
    */
   public AeIcon(Image aImage)
   {
      this("", aImage); //$NON-NLS-1$
   }

   /**
    * Constructs with the given name and image.
    * @param aName name of this component.
    * @param aImage image to be displayed.
    */   
   public AeIcon(String aName, Image aImage)
   {
      super(aName);     
      setImage(aImage);
      initialize();
   }   
   
   /** 
    * Calculates the size of this component.
    */
   private void initialize()
   {
      int w = 0;
      int h = 0;
      if (getImage() != null)
      {
         w = getImage().getWidth(null);
         h = getImage().getHeight(null);
      }
      setIconWidth(w);
      setIconHeight(h);
      setSize(w, h);
      setPreferredSize( w , h);
   }
     
   /**
    * @return Returns the image.
    */
   protected Image getImage()
   {
      return mImage;
   }
   
   /**
    * @param aImage The image to set.
    */
   protected void setImage(Image aImage)
   {
      mImage = aImage;
   }
   
   /**
    * @return Returns the imageHeight.
    */
   public int getIconHeight()
   {
      return mIconHeight;
   }
   
   /**
    * @param aImageHeight The imageHeight to set.
    */
   public void setIconHeight(int aImageHeight)
   {
      mIconHeight = aImageHeight;
   }
   
   /**
    * @return Returns the imageWidth.
    */
   public int getIconWidth()
   {
      return mIconWidth;
   }
   
   /**
    * @param aImageWidth The imageWidth to set.
    */
   public void setIconWidth(int aImageWidth)
   {
      mIconWidth = aImageWidth;
   }
      
   /**
    * Overrides method to render the image at the given location. 
    * @see java.awt.Component#paint(java.awt.Graphics)
    */
   public void paint(Graphics aGraphics)
   {      
      Image image = getImage();      
      int x = getLocation().x;
      int y = getLocation().y;
      if (image != null) {
         aGraphics.drawImage(image, x, y, null);
     }
   }
}
