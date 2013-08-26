// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/AeCatalogMappings.java,v 1.2 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AePreferences;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;

import bpelg.services.deploy.types.catalog.BaseCatalogEntryType;
import bpelg.services.deploy.types.catalog.Catalog;
import bpelg.services.deploy.types.catalog.OtherEntryType;

/**
 * Wraps the details of catalog xml file. It creates a
 * <code>AeCatalogBprMapping</code> object for every entry in the catalog
 * document. It also validates that the documents exist in the BPR file.
 */
public class AeCatalogMappings {
    /**
     * The bpr file containing the catalog.
     */
    private final IAeBpr mBpr;
    /**
     * Store the AeCatalogBprMapping mappings.
     */
    private final Map<String, IAeCatalogMapping> mResources = new HashMap<>();
    /**
     * Store the AeCatalogBprMapping of missing resources.
     */
    private final Map<String, IAeCatalogMapping> mMissingResources = new HashMap<>();
    /**
     * Replace any existing resource entries.
     */
    private boolean mReplaceExistingResource;

    /**
     * Constructor.
     *
     * @param aBpr The Bpr containing the catalog.
     * @throws AeException
     */
    public AeCatalogMappings(IAeBpr aBpr) throws AeException {
        mBpr = aBpr;
        parse(getBpr().getCatalogDocument());
    }

    /**
     * Constructor, which allows overriding of catalog overwrite flag.
     *
     * @param aBpr                     The Bpr containing the catalog.
     * @param aReplaceExistingResource the flag to use for replace existing resources.
     * @throws AeException
     */
    public AeCatalogMappings(IAeBpr aBpr, boolean aReplaceExistingResource)
            throws AeException {
        this(aBpr);
        mReplaceExistingResource = aReplaceExistingResource;
    }

    /**
     * Populate the wsdl and schema maps.
     *
     * @param aCatalogDocument
     */
    protected void parse(Catalog aCatalogDocument) {
        if (aCatalogDocument != null) {
            addEntries(aCatalogDocument.getWsdlEntry(),
                    IAeBPELExtendedWSDLConst.WSDL_NAMESPACE);
            addEntries(aCatalogDocument.getSchemaEntry(),
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
            addEntries(new ArrayList<BaseCatalogEntryType>(aCatalogDocument.getOtherEntry()), null);
            initReplaceResourceFlag(aCatalogDocument);
        }
    }

    /**
     * Initializes the "replace resource file" flag. Uses the attribute in the
     * catalog if it is there, or the value in the engine configuration
     * otherwise.
     *
     * @param aCatalogDocument
     */
    protected void initReplaceResourceFlag(Catalog aCatalogDocument) {
        // if the "replace-existing" attribute is specified in the catalog
        // then respect it's value - otherwise, consult the engine config for
        // the
        // global setting
        if (aCatalogDocument.isReplaceExisting() != null) {
            mReplaceExistingResource = aCatalogDocument.isReplaceExisting();
        } else {
            mReplaceExistingResource = AePreferences.isResourceReplaceEnabled();
        }
    }

    /**
     * Return true if any existing resource entries should be replaced.
     */
    public boolean replaceExistingResource() {
        return mReplaceExistingResource;
    }

    /**
     * add the entries to the given map.
     *
     * @param aEntries
     * @param aDefaultTypeURI the default type uri or null if one must be in element
     */
    protected void addEntries(List<BaseCatalogEntryType> aEntries,
                              String aDefaultTypeURI) {
        for (BaseCatalogEntryType entry : aEntries) {
            String urlKey = makeKey(entry.getLocation());
            if (aDefaultTypeURI == null)
                aDefaultTypeURI = ((OtherEntryType) entry).getTypeURI();
            AeCatalogBprMapping mapping = new AeCatalogBprMapping(getBpr(),
                    urlKey, aDefaultTypeURI, entry.getClasspath());
            if (mapping.exists())
                getResources().put(urlKey, mapping);
            else
                getMissingResources().put(urlKey, mapping);
        }
    }

    /**
     * @return Returns the Mappings which are of type
     *         <code>AeCatalogBprMapping</code> these are mapping that exist in
     *         the BPR.
     */
    public Map<String, IAeCatalogMapping> getResources() {
        return mResources;
    }

    /**
     * @return Returns the missingMappings map which are of type
     *         <code>AeCatalogBprMapping</code>.
     */
    public Map<String, IAeCatalogMapping> getMissingResources() {
        return mMissingResources;
    }

    /**
     * @return Returns the bpr.
     */
    public IAeBpr getBpr() {
        return mBpr;
    }

    /**
     * Construct a key for resource location hints.
     *
     * @param aLocation
     */
    public static String makeKey(String aLocation) {
        return aLocation.replace('\\', '/');
    }
}
