//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeCatalogItem.java,v 1.4 2006/09/26 18:07:33 ckeller Exp $
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

/**
 * Item for display in listing of the catalog.
 */
public class AeCatalogItem
{
    /** The catalog item file name. */
    private String mFormattedName;
    /** The catalog item location. */
    private String mLocation;
    /** The catalog item namespace. */
    private String mNamespace;
    /** The catalog item type. */
    private String mTypeURI;
    
    public AeCatalogItem() {
    }

    /**
     * Default constructor.
     */
    @ConstructorProperties({"location", "namespace", "typeURI", "formattedName"})
    public AeCatalogItem(String aLocation, String aNamespace, String aTypeURI, String aFormattedName)
    {
       mLocation = aLocation;
       mNamespace = aNamespace;
       mTypeURI = aTypeURI;
       mFormattedName = aFormattedName;

    }

    /**
     * Getter for the location.
     */
    public String getLocation()
    {
       return mLocation;
    }
    
    /**
     * Getter for the namespace.
     */
    public String getNamespace()
    {
       return mNamespace;
    }
    
    /**
     * @return Returns the typeURI.
     */
    public String getTypeURI()
    {
       return mTypeURI;
    }
    
    /**
     * Return the short name of the file.
     * Strips off any preceeding path information from the location.
     */
    public String getFormattedName()
    {
       return mFormattedName;
    }
    
    public void setFormattedName(String aFormattedName) {
        mFormattedName = aFormattedName;
    }
}
