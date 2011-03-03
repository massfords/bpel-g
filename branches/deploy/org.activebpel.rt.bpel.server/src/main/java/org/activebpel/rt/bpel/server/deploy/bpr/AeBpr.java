// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeBpr.java,v 1.4 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;

import java.io.InputStream;
import java.util.Collection;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.w3c.dom.Document;

import bpelg.services.deploy.types.catalog.Catalog;
import bpelg.services.deploy.types.pdd.Pdd;

/**
 * Standard IAeBpr impl for BPR archive file deployments.
 */
public class AeBpr implements IAeBpr {
	/** bpr strategy */
	private IAeBprAccessor mBprStrategy;
	/** deployment context */
	private IAeDeploymentContext mDeploymentContext;

	// ----------[ Static creation methods
	// ]--------------------------------------

	/**
	 * Creates a <code>AeBpr</code> object using the
	 * <code>AeJarFileBprStrategy</code> impl and null
	 * <code>IAePartnerAddressing</code> impl. Used for offline validation.
	 * 
	 * @param aContext
	 * @throws AeException
	 */
	public static IAeBpr createValidationBpr(IAeDeploymentContext aContext)
			throws AeException {
		IAeBprAccessor jarStrategy = new AeJarFileBprAccessor(aContext);
		return createBpr(aContext, jarStrategy);
	}

	/**
	 * Creates a <code>AeBpr</code> object using the
	 * <code>AeUnpackedBprStrategy</code> impl and the
	 * <code>IAePartnerAddressing</code> impl installed in the
	 * <code>AeEngineFactory</code>.
	 * 
	 * @param aContext
	 * @throws AeException
	 */
	public static IAeBpr createUnpackedBpr(IAeDeploymentContext aContext)
			throws AeException {
		IAeBprAccessor unpackedStrategy = new AeUnpackedBprAccessor(aContext);
		return createBpr(aContext, unpackedStrategy);
	}

	/**
	 * Create the <code>AeBpr</code> object with the given context, strategy and
	 * partner addressing impl. Ensure that the bpr file object is properly
	 * initialized.
	 * 
	 * @param aContext
	 * @param aStrategy
	 * @throws AeException
	 */
	protected static AeBpr createBpr(IAeDeploymentContext aContext,
			IAeBprAccessor aStrategy) throws AeException {
		AeBpr file = new AeBpr(aContext, aStrategy);
		file.init();
		return file;
	}

	// ----------[ X-Tor and instance methods
	// ]-----------------------------------

	/**
	 * Constructor.
	 * 
	 * @param aContext
	 * @param aStrategy
	 */
	protected AeBpr(IAeDeploymentContext aContext, IAeBprAccessor aStrategy) {
		mDeploymentContext = aContext;
		mBprStrategy = aStrategy;
	}

	/**
	 * Reads through the BPR archive and sets up the internal state.
	 * 
	 * @throws AeDeploymentException
	 */
	public void init() throws AeDeploymentException {
		try {
			getBprStrategy().init();
		} catch (AeException ae) {
			throw new AeDeploymentException(ae.getLocalizedMessage(),
					ae.getRootCause());
		}
	}

	/**
	 * Returns the deployment context associated with this bpr.
	 * 
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getDeploymentContext()
	 */
	public IAeDeploymentContext getDeploymentContext() {
		return mDeploymentContext;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getBprFileName()
	 */
	public String getBprFileName() {
		return getDeploymentContext().getShortName();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getPddResources()
	 */
	public Collection<AePddResource> getPddResources() {
		return getBprStrategy().getPddResources();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String aResourceName) {
		return getBprStrategy().getResourceAsStream(aResourceName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getDeploymentSource(bpelg
	 * .services.deploy.types.pdd.Pdd)
	 */
	public IAeDeploymentSource getDeploymentSource(Pdd aPdd) throws AeException {
		AeBprDeploymentSource source = new AeBprDeploymentSource(aPdd,
				getDeploymentContext());
		return source;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getCatalogDocument()
	 */
	public Catalog getCatalogDocument() throws AeException {
		return getBprStrategy().getCatalogDocument();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#exists(java.lang.String)
	 */
	public boolean exists(String aResourceName) {
		return getBprStrategy().hasResource(aResourceName);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getResourceAsDocument(java.lang.String)
	 */
	public Document getResourceAsDocument(String aResourceName)
			throws AeException {
		return getBprStrategy().getResourceAsDocument(aResourceName);
	}

	/**
	 * @return Returns the bprStrategy.
	 */
	protected IAeBprAccessor getBprStrategy() {
		return mBprStrategy;
	}
}
