//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/AeUIUtil.java,v 1.3 2008/01/03 15:47:46 PJayanetti Exp $
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
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.activebpel.rt.AeException;

/**
 * Utility methods used for the UI components.
 */
public class AeUIUtil
{
   /** AWT component to act as a media observer */
   private static final Component sImageObserver = new Container();
   /** Media tracker used when loading images */
   private static final MediaTracker sMediaTracker = new MediaTracker(sImageObserver);
   
   private AeUIUtil()
   {
   }

   /**
    * Loads and returns the image given its filename.
    * @param aFilename image filename
    * @return image
    * @throws IOException
    */
   public static Image loadImage(String aFilename) throws IOException {
      File file = new File(aFilename);
      return loadImage(file.toURI().toURL());
   }      

   /**
    * Loads and returns the image given class and the resource path. 
    * @param aClass class from which this resource is to be loaded.
    * @param aResourceName path or name of resource.
    * @return image
    * @throws IOException
    */
   public static Image loadImage(Class aClass, String aResourceName) throws IOException  {
      return loadImage( aClass.getResource(aResourceName) );
   }      
   
   /**
    * Loads the image given its URL. This method will block until the image has been loaded.
    * @param aURL url of the image
    * @return image or <code>null</code> if not found.
    * @throws IOException
    */
   public static Image loadImage(URL aURL) throws IOException  {
      // aURL param maybe null if the url was constructed using aClass.getResource(aResourceName)  
      // and the resource did not exist (ie. class loader was not able to locate the resource.
      if (aURL == null)
      {
         return null;
      }
      try
      {
         Toolkit toolkit = sImageObserver.getToolkit();
         Image image = toolkit.getImage(aURL);         
         waitUntilReady(image);
         return image;
      }
      catch(Throwable t)
      {
         AeException.logError(t,t.getLocalizedMessage());
         throw new IOException(t.getLocalizedMessage());
      }      
   }
   
   /**
    * Returns the inputstream to the given resource.
    * @param aClass resource class 
    * @param aResourceName name of resource
    * @return inputstream to the resource
    * @throws IOException
    */
   public static InputStream getImageInputStream(Class aClass, String aResourceName) {
         return aClass.getResourceAsStream(aResourceName);
   }
   
   /**
    * Returns a filtered image given the original image and the image filter.
    * @param aImage original source image
    * @param aImageFilter image filter to apply
    * @return filtered image
    * @throws AeException
    */
   public static Image createFilteredImage(Image aImage, ImageFilter aImageFilter) throws AeException
   {
      try
      {
         ImageProducer producer = new FilteredImageSource(aImage.getSource(), aImageFilter);
         Image image = sImageObserver.createImage(producer);
         waitUntilReady(image);
         return image;
      }
      catch(Throwable t)
      {
         throw new AeException(t);
      }
   }
   
   /** 
    * @return image observer component used by the media tracker.
    */
   public static Component getImageObserver()
   {
      return sImageObserver;
   }
   
   /**
    * Adds the image to the media tracker and blocks until the image is ready (produced).
    * @param aImage image to add to the media tracker.
    */
   public static void waitUntilReady(Image aImage)
   {
      if (aImage != null)
      {
         try 
         { 
            int id = aImage.hashCode();
            sMediaTracker.addImage(aImage, id);
            sMediaTracker.waitForID(id);
         } 
         catch (Exception e) 
         {
            // ignore errors image didn't load, handled by display
         }
      }
   }
}
