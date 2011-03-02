//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/AeBpelImageResources.java,v 1.10 2008/02/17 21:43:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//                PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel;

import java.awt.Image;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpeladmin.war.graph.AeGraphProperties;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeUIUtil;
import org.activebpel.rt.bpeladmin.war.web.processview.IAeWebProcessStates;

/**
 * Class to load and cache images resources.
 */
public class AeBpelImageResources
{
   /**
    * Global instance.
    */
   private static AeBpelImageResources sInstance = null;

   /**
    * Base path to the resource icons.
    */
   private String mBasePath = "/org/activebpel/rt/bpeladmin/war/graph/bpel/icons/";  //$NON-NLS-1$
   /**
    * Cache for the images.
    */
   private Map mImageCache = new Hashtable();

   /**
    * @return Returns the useLargeIcons.
    */
   public boolean isUseLargeIcons()
   {
      return AeGraphProperties.getInstance().getPropertyBoolean(AeGraphProperties.USE_LARGE_ICONS, true);
   }

   /**
    * @return base path to the icon resources.
    */
   protected String getBasePath()
   {
      return mBasePath;
   }

   /**
    * @return base path to the BPEL activity icon resources.
    */
   protected String getActivityImagePath(boolean aUseLargeIcons)
   {
      if (aUseLargeIcons)
      {
         return getBasePath() + "bpelLarge/";  //$NON-NLS-1$
      }
      else
      {
         return getBasePath() + "bpel/";  //$NON-NLS-1$
      }
   }

   /**
    * @return base path to the BPEL activity's state icon resources.
    */
   protected String getStateImagePath()
   {
      return getBasePath() + "state/";  //$NON-NLS-1$
   }

   /**
    * Returns logo image.
    * @return image
    */
   public Image getLogoImage()
   {
      String basePath = AeGraphProperties.getInstance().getProperty(AeGraphProperties.LOGO_IMAGE_PATH, getBasePath());
      if (!basePath.endsWith("/"))//$NON-NLS-1$
      {
         basePath = basePath + "/";//$NON-NLS-1$
      }
      return loadImage( basePath + "graph_logo.png");  //$NON-NLS-1$
   }

   /**
    * Returns image given its name, relative to the base path.
    * @param aImageName name of image resource.
    * @return image
    */
   public Image getImage(String aImageName)
   {
      return loadImage( getBasePath() + aImageName);
   }

   /**
    * Returns image given the BPEL activity name.
    * @param aActivityName name of BPEL resource.
    * @return image
    */
   public Image getActivityImage(String aActivityName)
   {
      return getActivityImage(aActivityName, isUseLargeIcons());
   }

   /**
    * Returns image given the BPEL activity name.
    * @param aActivityName name of BPEL resource.
    * @return image
    */
   public Image getActivityImage(String aActivityName, boolean aUseLargeIcons)
   {
      Image image = loadImage(getActivityImagePath( aUseLargeIcons ) + aActivityName + ".png");  //$NON-NLS-1$
      if (image == null)
      {
         // image not found - use place holder.
         image = loadImage(getActivityImagePath( aUseLargeIcons ) + "unknownActivity.png");  //$NON-NLS-1$
      }
      return image;
   }

   /**
    * Returns inactive state image given the BPEL activity name.
    * @param aActivityName name of BPEL resource.
    * @return image
    */
   public Image getInactiveActivityImage(String aActivityName)
   {
      String key = aActivityName + ".inactive"; //  //$NON-NLS-1$
      // check the cache.
      Image image = (Image) mImageCache.get(key);
      if (image == null)
      {
         // get the 'normal' (or active) image
         image  = getActivityImage(aActivityName);
         // create slightly lighter or transparent version of the normal image.
         try
         {
            ImageFilter filter = new AeActivityStateImageFilter(128); // alpha filtering.
            image = AeUIUtil.createFilteredImage(image,filter);
         }
         catch(AeException e)
         {
            // ignore error. Null image handled by client components.
         }
         // add to cache
         if (image != null)
         {
            mImageCache.put(key, image);
         }
      }
      return image;
   }

   /**
    * Returns dead path state image given the BPEL activity name.
    * @param aActivityName name of BPEL resource.
    * @return image
    */
   public Image getDeadpathActivityImage(String aActivityName)
   {
      String key = aActivityName + ".deadpath"; //$NON-NLS-1$
      // check the cache.
      Image image = (Image) mImageCache.get(key);
      if (image == null)
      {
         // get the 'normal' (or active) image
         image  = getActivityImage(aActivityName);
         // create gray scale version of the normal image.
         try
         {
            ImageFilter filter = new AeActivityStateImageFilter();
            image = AeUIUtil.createFilteredImage(image,filter);
         }
         catch(AeException e)
         {
            // ignore error. Null image handled by client components.
         }
         // add to cache
         if (image != null)
         {
            mImageCache.put(key, image);
         }
      }
      return image;
   }

   /**
    * Returns state adornment image given the BPEL activity's state code.
    * @param aStateCode state of the activity.
    * @return image icon for the state
    */
   public Image getStateAdornmentImage(String aStateCode)
   {
      String imageName = null;
      if ( AeBpelState.READY_TO_EXECUTE.toString().equals( aStateCode ))
         imageName = "pickExecuteAdorn.gif";   //$NON-NLS-1$
      else if ( AeBpelState.EXECUTING.toString().equals( aStateCode ))
         imageName = "executing.gif"; //$NON-NLS-1$
      else if ( AeBpelState.FINISHED.toString().equals( aStateCode ))
         imageName = "executed.gif";  //$NON-NLS-1$
      else if ( AeBpelState.TERMINATED.toString().equals( aStateCode ))
         imageName = "terminated.gif";//$NON-NLS-1$
      else if ( AeBpelState.FAULTED.toString().equals( aStateCode ))
         imageName = "fault.gif";  //$NON-NLS-1$
      else if ( AeBpelState.FAULTING.toString().equals( aStateCode ))
         imageName = "faulting.gif";  //$NON-NLS-1$
      else if ( AeBpelState.DEAD_PATH.toString().equals( aStateCode ))
         imageName = "deadActivity.gif"; //$NON-NLS-1$
      else if ( IAeWebProcessStates.PROCESS_STATE_RUNNING.equals( aStateCode ))
         imageName = "executing.gif"; //$NON-NLS-1$
      else if ( IAeWebProcessStates.PROCESS_STATE_SUSPENDED.equals( aStateCode ))
         imageName = "suspended.gif"; //$NON-NLS-1$
      else if ( IAeWebProcessStates.PROCESS_STATE_COMPLETED.equals( aStateCode ))
         imageName = "executed.gif"; //$NON-NLS-1$
      else if ( IAeWebProcessStates.PROCESS_STATE_FAULTED.equals( aStateCode ))
         imageName = "fault.gif"; //$NON-NLS-1$

      if (imageName != null)
      {
         return loadImage(getStateImagePath() + imageName);
      }
      else
      {
         return null;
      }
   }

   /**
    * Loads and returns the image given iths fullname and path. If the image if found in the cache,
    * then the cached image is returned.
    * @param aImageName fullpath and name of image resource.
    * @return image
    */
   public synchronized Image loadImage(String aImageName)
   {
      Image image = (Image) mImageCache.get(aImageName);
      if (image == null)
      {
         try
         {
            image = AeUIUtil.loadImage(getClass(), aImageName);
         }
         catch(Exception e)
         {
            // ignore error. Null image handled by client components.
            AeException.logError(e);
            image = null;
         }
         if (image != null)
         {
            mImageCache.put(aImageName, image);
         }
      }
      return image;
   }

   /**
    * Returns the inputstream to the given resource.
    * @param aImageName resource image name
    * @return Inputstream to the resource.
    * @throws IOException
    */
   public InputStream getImageInputStream(String aImageName) throws IOException
   {
      return new BufferedInputStream(AeUIUtil.getImageInputStream(getClass(), getBasePath() + aImageName));
   }

   /**
    * Accessor to the global instance
    * @return global AeBpelImageResources instance.
    */
   public static AeBpelImageResources getInstance()
   {
      synchronized(AeBpelImageResources.class)
      {
         if (sInstance == null)
         {
            sInstance = new AeBpelImageResources();
         }
      }
      return sInstance;
   }
}

/**
 * Image filter class to apply gray scale as well as change the
 * image's alpha level.
 */
class AeActivityStateImageFilter extends RGBImageFilter
{
   /** Apply gray scale filtering if trye */
   private boolean mApplyGrayScale = false;
   /** Apply alpha level filtering if true */
   private boolean mApplyAlpha = false;
   /** Alpha component value */
   private int mAlphaLevel = 255;

   /**
    * Creates a Gray scale filter.
    */
   public AeActivityStateImageFilter()
   {
      this(true, false,255);
   }

   /**
    * Creates a alpha level filter.
    * @param aAlphaLevel alpha value of the image. The value range is 0 - 255.
    */
   public AeActivityStateImageFilter( int aAlphaLevel)
   {
      this(false, true, aAlphaLevel);
   }

   /**
    * Constructs the filter
    * @param aGrayScale apply gray scale if true
    * @param aAlphaLevel alpha value of the image. The value range is 0 - 255.
    */
   public AeActivityStateImageFilter(boolean aGrayScale, boolean aApplyAlpha, int aAlphaLevel)
   {
     canFilterIndexColorModel = true;
     setApplyGrayScale(aGrayScale);
     setApplyAlpha(aApplyAlpha);
     if (isApplyAlpha() && aAlphaLevel >= 0 && aAlphaLevel < 256)
     {
        setAlphaLevel(aAlphaLevel);
     }
   }

   /**
    * @return Returns the applyAlpha.
    */
   public boolean isApplyAlpha()
   {
      return mApplyAlpha;
   }

   /**
    * @param aApplyAlpha The applyAlpha to set.
    */
   public void setApplyAlpha(boolean aApplyAlpha)
   {
      mApplyAlpha = aApplyAlpha;
   }

   /**
    * @return Returns the alphaLevel.
    */
   public int getAlphaLevel()
   {
      return mAlphaLevel;
   }

   /**
    * @param aAlphaLevel The alphaLevel to set.
    */
   public void setAlphaLevel(int aAlphaLevel)
   {
      mAlphaLevel = aAlphaLevel;
   }

   /**
    * @return Returns the applyGrayScale.
    */
   public boolean isApplyGrayScale()
   {
      return mApplyGrayScale;
   }

   /**
    * @param aApplyGrayScale The applyGrayScale to set.
    */
   public void setApplyGrayScale(boolean aApplyGrayScale)
   {
      mApplyGrayScale = aApplyGrayScale;
   }

   /**
    * Overrides method to apply alpha level and gray scale filtering.
    * @see java.awt.image.RGBImageFilter#filterRGB(int, int, int)
    */
   public int filterRGB(int x, int y, int rgb) {
      int a = (rgb >> 24) & 0xff; ;
      int r = (rgb >> 16) & 0xff;
      int g = (rgb >> 8) & 0xff;
      int b = rgb & 0xff;

      if (mApplyAlpha)
      {
         a = (a * mAlphaLevel)/255;
      }

      if (mApplyGrayScale)
      {
         int k = ((int)(.56 * g + .33 * r + .11 * b)) & 0xff;
         r  = k;
         g = k;
         b = k;
      }
      return (a << 24 | r << 16 | g << 8 | b);
   }
}

