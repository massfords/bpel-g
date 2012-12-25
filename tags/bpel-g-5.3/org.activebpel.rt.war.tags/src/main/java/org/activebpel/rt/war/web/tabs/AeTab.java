//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/web/tabs/AeTab.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.web.tabs; 

/**
 * Simple data structure for a tab on a JSP. 
 */
public class AeTab
{
   /** Name of the JSP that should be loaded when the tab is active */
   private String mPage;
   /** name of the tab */
   private String mName;
   /** reference back to the bean to see if we're the selected tab */
   private AeTabBean mBean;
   /** this tabs offset w/in the tab set */
   private int mOffset;
   
   
   /**
    * @return Returns the jSPName.
    */
   public String getPage()
   {
      return mPage;
   }

   /**
    * @param aName The jSPName to set.
    */
   public void setPage(String aName)
   {
      mPage = aName;
   }
   
   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * @param aName The name to set.
    */
   public void setName(String aName)
   {
      mName = aName;
   }
   
   /**
    * @return Returns the selected.
    */
   public boolean isSelected()
   {
      return mBean.isSelected(this);
   }
   
   /**
    * Setter for the bean reference.
    * 
    * @param aBean
    */
   protected void setBean(AeTabBean aBean)
   {
      mBean = aBean;
   }
   
   /**
    * Getter for the offset
    */
   public int getOffset()
   {
      return mOffset;
   }
   
   /**
    * Setter for the offset
    * 
    * @param aOffset
    */
   protected void setOffset(int aOffset)
   {
      mOffset = aOffset;
   }
   
}
 