// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/report/IAeCatalogAdmin.java,v 1.3 2007/03/06 21:52:55 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog.report;

import org.activebpel.rt.bpel.impl.list.AeCatalogListingFilter;
import org.activebpel.rt.bpel.impl.list.AeCatalogItemDetail;
import org.activebpel.rt.bpel.impl.list.AeCatalogListResult;
import org.activebpel.rt.bpel.server.catalog.resource.IAeResourceStats;
import org.xml.sax.InputSource;

/**
 *  Admin interface into the catalog.
 */
public interface IAeCatalogAdmin
{
   /**
    * Return the <code>AeCatalogListResult</code> for the catalog
    * contents listing.
    * @param aFilter
    */
   public AeCatalogListResult getCatalogListing( AeCatalogListingFilter aFilter );
   
   /**
    * Return a <code>AeCatalogItemDetail</code> object for a single resource deployment.
    * @param aLocationHint
    * @return catalog item detail if available or <code>null</code> if not found.
    */
   public AeCatalogItemDetail getCatalogItemDetail( String aLocationHint );

   /**
    * Return a <code>InputSource</code> object for a catalog item.
    * @param aLocationHint
    */
   public InputSource getCatalogInputSource(String aLocationHint);
   
   /**
    * Accessor for the resource stats.
    */
   public IAeResourceStats getResourceStats();  
}