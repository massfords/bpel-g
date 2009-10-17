// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeCatalogListingBean.java,v 1.3 2006/08/16 14:23:18 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.text.NumberFormat;

import org.activebpel.rt.bpel.impl.list.AeCatalogItem;
import org.activebpel.rt.bpel.impl.list.AeCatalogListResult;
import org.activebpel.rt.bpel.impl.list.AeCatalogListingFilter;
import org.activebpel.rt.bpeladmin.war.AeMessages;

/**
 * Used for displaying the top level wsdl deployments listing.
 */
public class AeCatalogListingBean extends AeAbstractListingBean
{
   /** Listing results. */
   private AeCatalogListResult mResults;
   
   /** Filter type selection. */
   private int mFilterType;
   
   /** Filter resource name selection. */
   private String mFilterResource;
   
   /** Filter resource name selection. */
   private String mFilterNamespace;
   
   /**
    * Constructor.  Initializes the top level wsdl deployment rows.
    */
   public AeCatalogListingBean()
   {
      setRowCount(20);
      setFilterType(0);
      setFilterResource(""); //$NON-NLS-1$
      setFilterNamespace(""); //$NON-NLS-1$
   }
   
   /**
    * Setting this value to true will populate
    * the results field.
    * @param aValue
    */
   public void setFinished( boolean aValue )
   {
       if( aValue )
       {
           AeCatalogListingFilter filter = new AeCatalogListingFilter();
           filter.setTypeURI(getItemType(getFilterType()).getTypeURI());
           filter.setResource(getFilterResource());
           filter.setNamespace(getFilterNamespace());
           filter.setListStart( getRowStart() );
           filter.setMaxReturn( getRowCount() );
           mResults = getAdmin().getCatalogAdmin().getCatalogListing( filter );
           
           if( mResults != null )
           {
               setTotalRowCount( mResults.getTotalRowCount() );
               updateNextPageStatus();
               setRowsDisplayed( mResults.getDetails().length );
               
               // Display "+" after row count if the row count wasn't completed.
               // setTotalRowCountSuffix(mResults.isCompleteRowCount() ? "" : "+");
           }
       }
   }
   
   /**
    * Return the number of details.
    */
   public int getDetailSize()
   {
       if( mResults != null )
       {
           return mResults.getDetails().length;
       }
       else
       {
           return 0;
       }
   }
   
   /**
    * Indexed accessor for the <code>AeCatalogItem</code> object.
    * @param aIndex
    */
   public AeCatalogItem getDetail( int aIndex )
   {
       return mResults.getDetails()[ aIndex ];
   }
   
   /**
    * Return the number of types for filter.
    */
   public int getItemTypeSize()
   {
       return Integer.valueOf(AeMessages.getString("AeCatalogItemType.CATALOG_FILTER_TYPE.COUNT")).intValue(); //$NON-NLS-1$
   }
   
   /**
    * Indexed accessor for the <code>AeCatalogItemType</code> object.
    * @param aIndex
    */
   public AeCatalogItemType getItemType( int aIndex )
   {
       return new AeCatalogItemType(aIndex, aIndex == getFilterType());
   }
   
   /**
    * Accessor for cache size.
    */
   public int getCacheSize()
   {
      return getAdmin().getEngineConfig().getUpdatableEngineConfig().getResourceCacheMax();
   }
   
   /**
    * Accessor for raw total cache reads.
    */
   public int getTotalReads()
   {
      return getAdmin().getCatalogAdmin().getResourceStats().getTotalReads();
   }
   
   /**
    * Accessor for raw disk reads.
    */
   public int getDiskReads()
   {
      return getAdmin().getCatalogAdmin().getResourceStats().getDiskReads();
   }
   
   /**
    * Accessor for percentage of reads from disk.
    */
   public String getDiskReadsPercent()
   {
      double value = 0;
      if( getTotalReads() != 0 )
      {
         value = ((double)getDiskReads()/(double)getTotalReads());
      }
      return NumberFormat.getPercentInstance().format( value );
   }
   
   /**
    * Return true if there are no results to display.
    */
   public boolean isEmpty()
   {
       return mResults == null || mResults.getDetails().length == 0;
   }

   /**
    * @return Returns the filterType.
    */
   public int getFilterType()
   {
      return mFilterType;
   }

   /**
    * @param aFilterType The filterType to set.
    */
   public void setFilterType(int aFilterType)
   {
      mFilterType = aFilterType;
   }
   
   /**
    * @return Returns the filterResource.
    */
   public String getFilterResource()
   {
      return mFilterResource;
   }

   /**
    * @param aFilterResource The filterResource to set.
    */
   public void setFilterResource(String aFilterResource)
   {
      mFilterResource = aFilterResource;
   }

   /**
    * @return Returns the filterNamespace.
    */
   public String getFilterNamespace()
   {
      return mFilterNamespace;
   }

   /**
    * @param aFilterNamespace The filterNamespace to set.
    */
   public void setFilterNamespace(String aFilterNamespace)
   {
      mFilterNamespace = aFilterNamespace;
   }
}
