//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/web/tabs/AeTabBean.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.web.tabs; 

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of tabs to be displayed on a JSP.
 */
public class AeTabBean
{
   /** The list of tabs */
   private List mTabs = new ArrayList();
   /** The tab at this offset is the currently selected tab */
   private int mSelectedOffset;
   
   /**
    * Adds a new tab
    * 
    * @param aTab
    */
   public void addTab(AeTab aTab)
   {
      aTab.setBean(this);
      aTab.setOffset(mTabs.size());
      mTabs.add(aTab);
   }
   
   /**
    * Setter to be used from the JSP in order to set the selected tab from a request param.
    * 
    * @param aTabOffset
    */
   public void setSelectedTabString(String aTabOffset)
   {
      try
      {
         mSelectedOffset = Integer.parseInt(aTabOffset);
      }
      catch (NumberFormatException e)
      {
         // ignore error
      }
   }
   
   /**
    * Gets the tab by offset.
    * 
    * @param aOffset
    */
   public AeTab getTab(int aOffset)
   {
      return (AeTab) mTabs.get(aOffset);
   }
   
   /**
    * Gets the number of tabs.
    */
   public int getTabSize()
   {
      return mTabs.size();
   }
   
   /**
    * Gets the page to load for the selected tab
    */
   public String getSelectedPage()
   {
      AeTab tab = getTab(getSelectedOffset());
      return tab.getPage();
   }
   
   /**
    * Gets the offset of the selected tab
    */
   public int getSelectedOffset()
   {
      return mSelectedOffset;
   }
   
   /**
    * Returns true if the tab is selected
    * 
    * @param aTab
    */
   protected boolean isSelected(AeTab aTab)
   {
      return getSelectedOffset() == (aTab.getOffset());
   }
}
 