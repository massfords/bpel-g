//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/AeIconLabel.java,v 1.2 2005/06/28 17:18:59 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.ui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

/**
 * Draws a text label with an icon on the left.
 */
public class AeIconLabel extends AeContainer
{
   /** Icon to be displayed. */
   private AeIcon mIcon = null;
   /** Text label to be displayed. */
   private AeTextLabel mLabel;

   /**
    * Constructs an label with the given text and icon image.
    * @param aText text to be displayed.
    * @param aIconImage image to be displayed. 
    */
   public AeIconLabel(String aText, Image aIconImage)
   {
      super(aText);
      setLayout( new FlowLayout(FlowLayout.CENTER,0,0));
      if (aIconImage != null)
      {
         mIcon = new AeIcon(aIconImage);
         add(mIcon);
      }      
      mLabel = new AeTextLabel(aText);
      add(mLabel);
   }
   
   /**
    * Overrides method to set the font.
    * @see java.awt.Component#setFont(java.awt.Font)
    */
   public void setFont(Font aFont)
   {
      super.setFont(aFont);
      if (mLabel != null)
      {
         mLabel.setFont(aFont);
      }
   }
   
   /** 
    * @return Icon associated with this component or null if one does not exist.
    */
   public AeIcon getIcon()
   {
      return mIcon;
   }
}
