//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeCatalogItemType.java,v 1.1 2006/08/16 14:23:18 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import org.activebpel.rt.bpeladmin.war.AeMessages;

/**
 * Simple class for wrapping a type for display in catalog filter.
 */
public class AeCatalogItemType
{
   /** The type number. */
   private int mTypeNumber;

   /** True if this is the selected filter type. */
   private boolean mSelected;
   
   /**
    * Constructor.
    */
   public AeCatalogItemType(int aTypeNumber, boolean aSelected)
   {
      mTypeNumber = aTypeNumber;
      mSelected = aSelected;
   }

   /**
    * @return Returns the typeNumber.
    */
   public int getTypeNumber()
   {
      return mTypeNumber;
   }

   /**
    * @return Returns the true if selected type for filtered, false otherwise.
    */
   public boolean isSelected()
   {
      return mSelected;
   }
   
   /**
    * @return Returns the typeDisplay.
    */
   public String getTypeDisplay()
   {
      String prop = "AeCatalogItemType.CATALOG_FILTER_TYPE." + getTypeNumber(); //$NON-NLS-1$ 
      return AeMessages.getString(prop);
   }

   /**
    * @return Returns the typeDisplay.
    */
   public String getTypeURI()
   {
      String prop = "AeCatalogItemType.CATALOG_FILTER_TYPE_URI." + getTypeNumber(); //$NON-NLS-1$ 
      return AeMessages.getString(prop);
   }
}
