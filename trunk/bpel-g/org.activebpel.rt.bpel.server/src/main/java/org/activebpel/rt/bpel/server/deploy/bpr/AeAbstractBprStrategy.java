//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeAbstractBprStrategy.java,v 1.2 2006/07/18 20:05:33 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMSource;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import bpelg.services.deploy.types.catalog.Catalog;

/**
 * Base class for <code>IAeBprStrategy</code> implementations. Provides common
 * accessor methods for bpr file resources.
 */
public abstract class AeAbstractBprStrategy implements IAeBprAccessor {
    /**
     * The deployment context.
     */
    private final IAeDeploymentContext mDeploymentContext;
    /**
     * The pdd resource names.
     */
    private Collection<AePddResource> mPddResources;
    /**
     * XML parser.
     */
    private AeXMLParserBase mParser;
    private Catalog mCatalog;

    /**
     * Constructor.
     *
     * @param aDeploymentContext
     */
    protected AeAbstractBprStrategy(IAeDeploymentContext aDeploymentContext) {
        mDeploymentContext = aDeploymentContext;
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#getPddResources()
     */
    public Collection<AePddResource> getPddResources() {
        return mPddResources;
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream(String aResourceName) {
        return getDeploymentContext().getResourceAsStream(aResourceName);
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#getCatalogDocument()
     */
    public Catalog getCatalogDocument() throws AeException {
        if (mCatalog == null) {
            Document catalogXml = getResourceAsDocument(CATALOG);
            try {
                JAXBContext context = JAXBContext.newInstance(Catalog.class);
                Unmarshaller u = context.createUnmarshaller();
                // FIXME deploy - need to add schema validation here
                mCatalog = (Catalog) u.unmarshal(new DOMSource(catalogXml));
            } catch (JAXBException e) {
                throw new AeException(e);
            }
        }
        return mCatalog;
    }

    /**
     * @param aPddResources The pddResources to set.
     */
    protected void setPddResources(Collection<AePddResource> aPddResources) {
        mPddResources = aPddResources;
    }

    /**
     * @return Returns the deploymentContext.
     */
    protected IAeDeploymentContext getDeploymentContext() {
        return mDeploymentContext;
    }

    /**
     * Accessor for the XML parser.
     */
    protected AeXMLParserBase getParser() {
        if (mParser == null) {
            mParser = new AeXMLParserBase();
            mParser.setValidating(false);
            mParser.setNamespaceAware(true);
        }
        return mParser;
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#getResourceAsDocument(java.lang.String)
     */
    public Document getResourceAsDocument(String aResourceName)
            throws AeException {
        InputStream in = null;
        try {
            URL url = getDeploymentContext().getResourceURL(aResourceName);
            if (url == null) {
                return null;
            } else {
                in = url.openStream();
                return getParser().loadDocument(in, null);
            }
        } catch (Throwable t) {
            String detailReason;
            if (t.getCause() == null)
                detailReason = AeMessages
                        .getString("AeJarFileBprAccessor.UNKNOWN"); //$NON-NLS-1$
            else
                detailReason = t.getCause().getLocalizedMessage();

            Object args[] = new Object[]{aResourceName,
                    getDeploymentContext().getDeploymentLocation(),
                    detailReason};
            throw new AeDeploymentException(AeMessages.format(
                    "AeJarFileBprAccessor.ERROR_1", args), t); //$NON-NLS-1$
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#hasResource(java.lang.String)
     */
    public boolean hasResource(String aResourceName) {
        return getDeploymentContext().getResourceURL(aResourceName) != null;
    }
}
