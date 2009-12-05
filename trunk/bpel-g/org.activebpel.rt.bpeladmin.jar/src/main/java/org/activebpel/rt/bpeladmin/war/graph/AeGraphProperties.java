// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/AeGraphProperties.java,v 1.7 2008/02/17 21:43:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph;

import java.awt.Color;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;

/**
 * Reads the graph color and layout properties from graph.properties resource.
 * The properties may be overrride via the graph image servlet init properties.
 * Note: If a new property is add, then new property with the default value
 * must be entered in the graph.properties resource resource file.
 */
public class AeGraphProperties extends Properties
{
   /** Property name for enabling or disabling the graph feature. */
   public static final String ENABLE_GRAPHING         = "org.activebpel.rt.bpeladmin.war.graph.enable";//$NON-NLS-1$

   /** Property name for buffered image color depth. */
   public static final String COLOR_DEPTH             = "org.activebpel.rt.bpeladmin.war.graph.image.colordepth";//$NON-NLS-1$

   /** Property name for buffered image max memory that could be allocated */
   public static final String MAX_MEMORY             = "org.activebpel.rt.bpeladmin.war.graph.image.maxmemory";//$NON-NLS-1$

   /** Property name to use larger icons. */
   public static final String USE_LARGE_ICONS         = "org.activebpel.rt.bpeladmin.war.graph.icons.large";//$NON-NLS-1$

   /** Property name  to show or hide adornments. */
   public static final String SHOW_ADORMENTS          = "org.activebpel.rt.bpeladmin.war.graph.adorments";//$NON-NLS-1$

   /** Property name  for text color. */
   public static final String TEXT_COLOR              = "org.activebpel.rt.bpeladmin.war.graph.text.color";//$NON-NLS-1$

   /** Property name  for label font name. */
   public static final String FONT_NAME               = "org.activebpel.rt.bpeladmin.war.graph.font.name";//$NON-NLS-1$

   /** Property name for font size. */
   public static final String FONT_SIZE               = "org.activebpel.rt.bpeladmin.war.graph.font.size";//$NON-NLS-1$

   /** Property name for activity container's border color. */
   public static final String BORDER_COLOR            = "org.activebpel.rt.bpeladmin.war.graph.border.color";//$NON-NLS-1$

   /** Property name for activity container's background color. */
   public static final String BACKGROUND_COLOR        = "org.activebpel.rt.bpeladmin.war.graph.background.color";//$NON-NLS-1$

   /** Property name  for link color. */
   public static final String LINK_NORMAL_COLOR       = "org.activebpel.rt.bpeladmin.war.graph.link.normal.color";//$NON-NLS-1$

   /** Property name for link's active color. */
   public static final String LINK_ACTIVE_COLOR       = "org.activebpel.rt.bpeladmin.war.graph.link.active.color";//$NON-NLS-1$

   /** Property name  for link's inactive color. */
   public static final String LINK_INACTIVE_COLOR     = "org.activebpel.rt.bpeladmin.war.graph.link.inactive.color";//$NON-NLS-1$

   /** Property name for layout top margin. */
   public static final String LAYOUT_MARGIN_TOP       = "org.activebpel.rt.bpeladmin.war.graph.layout.margin.top";//$NON-NLS-1$

   /** Property name for layout bottom margin.  */
   public static final String LAYOUT_MARGIN_BOTTOM    = "org.activebpel.rt.bpeladmin.war.graph.layout.margin.bottom";//$NON-NLS-1$

   /** Property name for layout left margin. */
   public static final String LAYOUT_MARGIN_LEFT      = "org.activebpel.rt.bpeladmin.war.graph.layout.margin.left";//$NON-NLS-1$

   /** Property name for layout right margin. */
   public static final String LAYOUT_MARGIN_RIGHT     = "org.activebpel.rt.bpeladmin.war.graph.layout.margin.right";//$NON-NLS-1$

   /** Property name  for layout interlevel spacing.*/
   public static final String LAYOUT_LEVEL_SPACING    = "org.activebpel.rt.bpeladmin.war.graph.layout.level.spacing";//$NON-NLS-1$

   /** Property name for layout internode spacing. */
   public static final String LAYOUT_NODE_SPACING     = "org.activebpel.rt.bpeladmin.war.graph.layout.node.spacing";//$NON-NLS-1$

   /** Property name for drawing debug lines.*/
   public static final String ENABLE_DEBUG_DRAW       = "org.activebpel.rt.bpeladmin.war.graph.debug";//$NON-NLS-1$

   /** Property name for logo image base path .*/
   public static final String LOGO_IMAGE_PATH         = "org.activebpel.rt.bpeladmin.war.graph.logo.path";//$NON-NLS-1$
   
   /** Property name image tile size (width=height, in pixels).*/
   public static final String IMAGE_TILE_SIZE         = "org.activebpel.rt.bpeladmin.war.graph.image.tilesize";//$NON-NLS-1$
   

   /** Singleton instance */
   private static AeGraphProperties sInstance = null;
   /** Property file or resource name */
   private String mResourceName = "graph.properties"; //$NON-NLS-1$

   /**
    * private constructor.
    */
   private AeGraphProperties()
   {
      super();

      InputStream inputStream = null;
      try
      {
         inputStream = getClass().getResourceAsStream(mResourceName);
         super.load(inputStream);
      }
      catch (Exception e)
      {
         AeException.logError(e, e.getMessage());
      }
      finally
      {
         AeCloser.close(inputStream);
      }// finally
   }

   /**
    * Overrides method to trim the result value if it is not <code>null</code>.
    *
    * @see java.util.Properties#getProperty(java.lang.String)
    */
   public String getProperty(String aName)
   {
      String value = super.getProperty(aName);
      if (value != null)
      {
         value = value.trim();
      }
      return value;
   }

   /**
    * Overrides method to trim the result value if it is not <code>null</code>.
    *
    * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
    */
   public String getProperty(String aName, String aDefaultValue)
   {
      String value = super.getProperty(aName, aDefaultValue);
      if (value != null)
      {
         value = value.trim();
      }
      return value;
   }

   /**
    * Returns the named integer property.
    * @param aName value of property
    * @param aDefaultValue default value.
    * @return named value or the default value if not found.
    */
   public int getPropertyInt(String aName, int aDefaultValue)
   {
      return AeUtil.parseInt(getProperty(aName), aDefaultValue);
   }

   /**
    * Returns the named boolean property.
    * @param aName value of property
    * @param aDefaultValue default value.
    * @return named value or the default value if not found.
    */
   public boolean getPropertyBoolean(String aName, boolean aDefaultValue)
   {
      String value = getProperty(aName, Boolean.toString(aDefaultValue));

      if ("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)) //$NON-NLS-1$ //$NON-NLS-2$
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Returns the named Color property.
    * @param aName value of property
    * @param aDefaultColor default value if the rgb value is not available.
    * @return named value or the default value if not found or unable to parse.
    */
   public Color getPropertyColor(String aName, Color aDefaultColor)
   {
      String rgb = getProperty(aName);
      if (AeUtil.isNullOrEmpty(rgb))
      {
         return aDefaultColor;
      }
      StringTokenizer st = new StringTokenizer(rgb,",");//$NON-NLS-1$
      if (st.countTokens() != 3)
      {
         return aDefaultColor;
      }

      try
      {
         int r = Integer.parseInt(st.nextToken());
         int g = Integer.parseInt(st.nextToken());
         int b = Integer.parseInt(st.nextToken());
         if (r >= 0 && r < 256 && g>= 0 && g < 256 && b >=0 && b < 256)
         {
            return new Color(r,g,b);
         }
      }
      catch(Exception e)
      {
      }

      return aDefaultColor;
   }

   /**
    * Returns a single instance of the AeGraphProperties.
    * @return single instance.
    */
   public static AeGraphProperties getInstance()
   {
      synchronized(AeGraphProperties.class)
      {
         if (sInstance == null)
         {
            sInstance = new AeGraphProperties();
         }
         return sInstance;
      }
   }
}
