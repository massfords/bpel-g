//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/figure/AeUiPrefs.java,v 1.4 2005/06/28 17:18:59 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.figure;

import java.awt.Color;
import java.awt.Font;

import org.activebpel.rt.bpeladmin.war.graph.AeGraphProperties;

/**
 *  Defines the draw preferences such as line colors.
 */
public class AeUiPrefs
{
   /** Default UI preferences */
   private static AeUiPrefs sDefaultUiPrefs = null;
   
   /** If true, draws debug lines */
   private static  boolean sDrawDebugLines = false;
   /** If true, enable drawing of source and target anchor bounding boxes */
   private static  boolean sDrawDebugAnchorPoints = false;
   /** If true, activity state adormnents are shown. */
   private static boolean sShowStateAdornments = true;
   /** Font used in the labels */
   private Font  mFont = null;
   /** Label text color */
   private Color mTextColor = Color.BLACK;
   /** normal Link color */
   private Color mLinkNormalColor = Color.GRAY;   
   /** inactive Link color */
   private Color mLinkInActiveColor = Color.LIGHT_GRAY;   
   /** Color of the link if it has been evaluated */
   private Color mLinkActiveColor = new Color(0,51,0);   
   /** Background color for containers. */
   private Color mBackgroundColor = new Color(230, 230, 230);
   /** Border color of containers */
   private Color mBorderColor = Color.GRAY;   
   /** Color of source anchor used when drawing debug lines */
   private Color mDebugSourceAnchorColor = Color.MAGENTA;
   /** Color of taget anchor used when drawing debug lines */
   private Color mDebugTargetAnchorColor = Color.BLUE;
   /** Color of Activity bounding box used when drawing debug lines */
   private Color mDebugActivityColor = Color.CYAN;
   /** Color of Activity Container bounding box used when drawing debug lines */
   private Color mDebugActivityContainerColor = Color.GREEN;
   
   /**
    * Default contructor.
    */
   public AeUiPrefs()
   {
   }
      
   /**
    * @return Returns the debugMode.
    */
   public static boolean isDrawDebugLines()
   {
      return sDrawDebugLines;
   }
   
   /**
    * @return Returns the debugMode.
    */
   public static boolean isDrawDebugAnchorPoints()
   {
      return sDrawDebugAnchorPoints;
   }   
      
   /**
    * @return Returns the showStateAdornments.
    */
   public static boolean isShowStateAdornments()
   {
      return sShowStateAdornments;
   }
   
   /**
    * @param aShowStateAdornments The showStateAdornments to set.
    */
   public static void setShowStateAdornments(boolean aShowStateAdornments)
   {
      sShowStateAdornments = aShowStateAdornments;
   }
   
   /**
    * @return Returns the font.
    */
   public Font getFont()
   {
      if (mFont == null)
      {
         mFont = getDefaultFont();
      }
      return mFont;
   }
   
   /**
    * @param aFont The font to set.
    */
   public void setFont(Font aFont)
   {
      mFont = aFont;
   }
        
   /**
    * @return Returns the textColor.
    */
   public Color getTextColor()
   {
      return mTextColor;
   }
   
   /**
    * @param aTextColor The textColor to set.
    */
   public void setTextColor(Color aTextColor)
   {
      mTextColor = aTextColor;
   }
   
   /**
    * @return Returns the backgroundColor.
    */
   public Color getBackgroundColor()
   {
      return mBackgroundColor;
   }
   
   /**
    * @param aBackgroundColor The backgroundColor to set.
    */
   public void setBackgroundColor(Color aBackgroundColor)
   {
      mBackgroundColor = aBackgroundColor;
   }
   
   /**
    * @return Returns the borderColor.
    */
   public Color getBorderColor()
   {
      return mBorderColor;
   }
   
   /**
    * @param aBorderColor The borderColor to set.
    */
   public void setBorderColor(Color aBorderColor)
   {
      mBorderColor = aBorderColor;
   }
   
   /**
    * @return Returns the linkColor.
    */
   public Color getLinkNormalColor()
   {
      return mLinkNormalColor;
   }
   
   /**
    * @param aLinkColor The linkColor to set.
    */
   public void setLinkNormalColor(Color aLinkColor)
   {
      mLinkNormalColor = aLinkColor;
   }
   
   /**
    * @return Returns the evaluated link color.
    */
   public Color getLinkActiveColor()
   {
      return mLinkActiveColor;
   }
   
   /**
    * @param aLinkColor The evaluated link color to set.
    */
   public void setLinkActiveColor(Color aLinkColor)
   {
      mLinkActiveColor = aLinkColor;
   }  
   
   /**
    * @return Returns the linkInActiveColor.
    */
   public Color getLinkInActiveColor()
   {
      return mLinkInActiveColor;
   }
   
   /**
    * @param aLinkInActiveColor The linkInActiveColor to set.
    */
   public void setLinkInActiveColor(Color aLinkInActiveColor)
   {
      mLinkInActiveColor = aLinkInActiveColor;
   }
   
   /**
    * @return Returns the activityColor.
    */
   public Color getDebugActivityColor()
   {
      return mDebugActivityColor;
   }
   
   /**
    * @param aActivityColor The activityColor to set.
    */
   public void setDebugActivityColor(Color aActivityColor)
   {
      mDebugActivityColor = aActivityColor;
   }
   
   /**
    * @return Returns the activityContainerColor.
    */
   public Color getDebugActivityContainerColor()
   {
      return mDebugActivityContainerColor;
   }
   
   /**
    * @param aActivityContainerColor The activityContainerColor to set.
    */
   public void setDebugActivityContainerColor(Color aActivityContainerColor)
   {
      mDebugActivityContainerColor = aActivityContainerColor;
   }
   
   /**
    * @return Returns the sourceAnchorColor.
    */
   public Color getDebugSourceAnchorColor()
   {
      return mDebugSourceAnchorColor;
   }
   
   /**
    * @param aSourceAnchorColor The sourceAnchorColor to set.
    */
   public void setDebugSourceAnchorColor(Color aSourceAnchorColor)
   {
      mDebugSourceAnchorColor = aSourceAnchorColor;
   }
   
   /**
    * @return Returns the targetAnchorColor.
    */
   public Color getDebugTargetAnchorColor()
   {
      return mDebugTargetAnchorColor;
   }
   
   /**
    * @param aTargetAnchorColor The targetAnchorColor to set.
    */
   public void setDebugTargetAnchorColor(Color aTargetAnchorColor)
   {
      mDebugTargetAnchorColor = aTargetAnchorColor;
   }
   
   /**
    * @param aDrawDebugAnchorPoints The drawDebugAnchorPoints to set.
    */
   public static void setDrawDebugAnchorPoints(boolean aDrawDebugAnchorPoints)
   {
      sDrawDebugAnchorPoints = aDrawDebugAnchorPoints;
   }
   
   /**
    * @param aDrawDebugLines The drawDebugLines to set.
    */
   public static void setDrawDebugLines(boolean aDrawDebugLines)
   {
      sDrawDebugLines = aDrawDebugLines;
   }
   
   /**
    * @return Returns the default font - Dialog, Plain, 12.
    */
   public static Font getDefaultFont()
   {
      return  new Font("Dialog", Font.PLAIN, 12);  //$NON-NLS-1$
   }
   
   public static AeUiPrefs getDefaultPrefs()
   {
      synchronized(AeUiPrefs.class)
      {
        if (sDefaultUiPrefs == null)
        {
           sDefaultUiPrefs = new AeUiPrefs();
           readUIPreferences(sDefaultUiPrefs);
        }
      }
      return sDefaultUiPrefs;   
   }
   
   /** 
    * Read preference from the resource file and override the default settings.
    */
   protected static void readUIPreferences(AeUiPrefs aPrefs)
   {
            
      AeGraphProperties props = AeGraphProperties.getInstance();
      Color c = null;
      
      c = props.getPropertyColor(AeGraphProperties.LINK_NORMAL_COLOR, Color.GRAY); 
      aPrefs.setLinkNormalColor(c);

      c = props.getPropertyColor(AeGraphProperties.LINK_ACTIVE_COLOR, Color.DARK_GRAY);
      aPrefs.setLinkActiveColor(c);
      
      c = props.getPropertyColor(AeGraphProperties.LINK_INACTIVE_COLOR, Color.LIGHT_GRAY);
      aPrefs.setLinkInActiveColor(c);

      c = props.getPropertyColor(AeGraphProperties.BORDER_COLOR, Color.GRAY); 
      aPrefs.setBorderColor(c);

      c = props.getPropertyColor(AeGraphProperties.BACKGROUND_COLOR, new Color(230,230,230)); 
      aPrefs.setBackgroundColor(c);

      c = props.getPropertyColor(AeGraphProperties.TEXT_COLOR, Color.BLACK); 
      aPrefs.setTextColor(c);
      
      try
      {
       String fontName = props.getProperty(AeGraphProperties.FONT_NAME, "Dialog"); //$NON-NLS-1$
       int fontSize = props.getPropertyInt(AeGraphProperties.FONT_SIZE, 12); 
       if (fontSize < 9)
       {
          fontSize = 9;
       }
       else if (fontSize > 20)
       {
          fontSize = 20;
       }
       Font font = new Font(fontName, Font.PLAIN, fontSize);
       aPrefs.setFont(font);
      } 
      catch(Exception e)
      {
         // ignore error.
      }      
      
      boolean adornments = props.getPropertyBoolean(AeGraphProperties.SHOW_ADORMENTS,true); 
      AeUiPrefs.setShowStateAdornments(adornments);
      
      boolean debug = props.getPropertyBoolean(AeGraphProperties.ENABLE_DEBUG_DRAW,false);
      AeUiPrefs.setDrawDebugLines(debug);
      AeUiPrefs.setDrawDebugAnchorPoints(debug);
      
   }   
}
