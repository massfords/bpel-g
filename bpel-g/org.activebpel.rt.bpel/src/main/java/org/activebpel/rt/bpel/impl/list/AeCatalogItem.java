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

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;

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

    /**
     * Default constructor.
     */
    public AeCatalogItem(String aLocation, String aNamespace, String aTypeURI)
    {
       mLocation = aLocation;
       mNamespace = aNamespace;
       mTypeURI = aTypeURI;
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
       if(mFormattedName == null)
          mFormattedName = AeUtil.getShortNameForLocation(getLocation());
       return mFormattedName;
    }
    
    /**
     * Return the short name of the file.
     * Strips off any preceeding path information from the location.
     */
    public String getTypeDisplay()
    {
       // TODO (cck) this is mostly for console display, so we should have localization there
       if(IAeBPELExtendedWSDLConst.WSDL_NAMESPACE.equals(getTypeURI()))
          return AeMessages.getString("AeCatalogItem.WSDL"); //$NON-NLS-1$
       else if(IAeConstants.W3C_XML_SCHEMA.equals(getTypeURI()))
          return AeMessages.getString("AeCatalogItem.SCHEMA"); //$NON-NLS-1$
       else if(IAeConstants.XSL_NAMESPACE.equals(getTypeURI()))
          return AeMessages.getString("AeCatalogItem.XSL"); //$NON-NLS-1$
       return AeMessages.getString("AeCatalogItem.OTHER"); //$NON-NLS-1$
    }
}
