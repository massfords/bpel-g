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

import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeCatalogItem;
import org.activebpel.rt.bpel.impl.list.AeCatalogListResult;
import org.activebpel.rt.bpel.impl.list.AeListResult;
import org.activebpel.rt.bpeladmin.war.AeMessages;

/**
 * Used for displaying the top level wsdl deployments listing.
 */
public class AeCatalogListingBean extends AeAbstractListingBean
{
   /** Listing results. */
   private AeListResult<AeCatalogItem> mResults;
   
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
           List<AeCatalogItem> resultz = getAdmin().getCatalogListing(getItemType(getFilterType()).getTypeURI(), getFilterResource(), getFilterNamespace(), getRowCount(), getRowStart() ); 
           mResults = new AeCatalogListResult(resultz.size(), resultz, true);
           
           if( mResults != null )
           {
               setTotalRowCount( mResults.getTotalRowCount() );
               updateNextPageStatus();
               setRowsDisplayed( mResults.getResults().size() );
               
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
           return mResults.getResults().size();
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
       return mResults.getResults().get(aIndex);
   }
   
   /**
    * Return the number of types for filter.
    */
   public int getItemTypeSize()
   {
       return Integer.valueOf(AeMessages.getString("AeCatalogItemType.CATALOG_FILTER_TYPE.COUNT")); //$NON-NLS-1$
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
      return getAdmin().getCatalogCacheSize();
   }
   
   /**
    * Accessor for raw total cache reads.
    */
   public long getCacheHits()
   {
      return getAdmin().getCacheHits();
   }
   
   /**
    * Accessor for raw disk reads.
    */
   public long getCacheMisses()
   {
      return getAdmin().getCacheMisses();
   }
   
   /**
    * Return true if there are no results to display.
    */
   public boolean isEmpty()
   {
       return mResults == null || mResults.getResults().size() == 0;
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
