//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeCatalogListResult.java,v 1.1 2006/07/18 20:02:46 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.util.Collection;

/**
 * Wraps the catalog listing results.
 */
public class AeCatalogListResult extends AeListResult
{
    /**
     * Constructor.
     * @param aTotalRowCount
     * @param aResults
     * @param aCompleteRowCount
     */
    public AeCatalogListResult(int aTotalRowCount, Collection aResults,
            boolean aCompleteRowCount)
    {
        super(aTotalRowCount, aResults, aCompleteRowCount);
    }
    
    /**
     * Accessor for catalog listing detail array.
     */
    public AeCatalogItem[] getDetails()
    {
        return (AeCatalogItem[])getResultsInternal().toArray( new AeCatalogItem[getResultsInternal().size()]);
    }
}
