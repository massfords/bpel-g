//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeCatalogItemPlanReference.java,v 1.1 2006/07/18 20:02:46 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.beans.ConstructorProperties;

import org.activebpel.rt.xml.AeQName;

/**
 * Wraps the plan detail information (QName) for plans
 * that are associated with a catalog item detail object.
 */
public class AeCatalogItemPlanReference
{
    /** Plan QName. */
    protected final AeQName mPlanQName;
    
    /**
     * Constructor.
     * @param aQName
     */
    @ConstructorProperties({"planQName"})
    public AeCatalogItemPlanReference( AeQName aQName )
    {
        mPlanQName = aQName;
    }
    
    /**
     * Accessor for the plan QName.
     */
    public AeQName getPlanQName()
    {
        return mPlanQName;
    }
}
