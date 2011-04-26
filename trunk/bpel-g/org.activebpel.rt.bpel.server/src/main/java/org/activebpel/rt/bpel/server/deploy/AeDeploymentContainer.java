// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentContainer.java,v 1.13 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.w3c.dom.Document;

import bpelg.services.deploy.types.catalog.Catalog;
import bpelg.services.deploy.types.pdd.Pdd;

/**
 * Deployment container impl.
 */
public class AeDeploymentContainer implements IAeDeploymentContainer {
	/** Deployment context. */
	protected final IAeDeploymentContext mContext;
	/** Bpr file. */
	protected final IAeBpr mBprFile;
	/** File name string - used for logging. */
	protected final String mFileName;
	/** Deployment id url. */
	protected final URL mUrlForId;
	/** Service deployment information */
	protected List<IAeServiceDeploymentInfo> mServiceInfo;

	/**
	 * Constructor.
	 * 
	 * @param aContext
	 * @param aBprFile
	 * @param aUrl
	 */
	public AeDeploymentContainer(IAeDeploymentContext aContext,
			IAeBpr aBprFile, URL aUrl) {
		mContext = aContext;
		mBprFile = aBprFile;
		mUrlForId = aUrl;
		mFileName = aUrl.getFile().replace('\\', '/');
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#exists(java.lang.String)
	 */
	public boolean exists(String aResourceName) {
		if (mBprFile == null)
			return false;

		return mBprFile.exists(aResourceName);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getBprFileName()
	 */
	public String getBprFileName() {
		return mFileName;
	}

	public IAeDeploymentSource getDeploymentSource(Pdd aPdd) throws AeException {
		return mBprFile.getDeploymentSource(aPdd);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getResourceAsDocument(java.lang.String)
	 */
	public Document getResourceAsDocument(String aResourceName)
			throws AeException {
		return mBprFile.getResourceAsDocument(aResourceName);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getPddResources()
	 */
	public Collection<AePddResource> getPddResources() {
		return mBprFile.getPddResources();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getCatalogDocument()
	 */
	public Catalog getCatalogDocument() throws AeException {
		return mBprFile.getCatalogDocument();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getDeploymentId()
	 */
	public IAeDeploymentId getDeploymentId() {
		return new AeDeploymentId(mUrlForId);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getDeploymentLocation()
	 */
	public URL getDeploymentLocation() {
		return mContext.getDeploymentLocation();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String aResourceName) {
		return mContext.getResourceAsStream(aResourceName);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceURL(java.lang.String)
	 */
	public URL getResourceURL(String aResourceName) {
		return mContext.getResourceURL(aResourceName);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getShortName()
	 */
	public String getShortName() {
		return mFileName.substring(mFileName.lastIndexOf('/') + 1);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getTempDeploymentLocation()
	 */
	public URL getTempDeploymentLocation() {
		return mContext.getDeploymentLocation();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext#getResourceClassLoader()
	 */
	public ClassLoader getResourceClassLoader() {
		return mContext.getResourceClassLoader();
	}

	/**
	 * Implements method by returning this as it is itself the deployment
	 * context.
	 * 
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr#getDeploymentContext()
	 */
	public IAeDeploymentContext getDeploymentContext() {
		return this;
	}

	/**
	 * @throws AeException 
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer#getServiceDeploymentInfo()
	 */
	public List<IAeServiceDeploymentInfo> getServiceDeploymentInfo() throws AeException {
		if (mServiceInfo == null) {
			mServiceInfo = new ArrayList();

			for (AePddResource pddr : mBprFile.getPddResources()) {
				IAeDeploymentSource source = getDeploymentSource(pddr.getPdd());
				mServiceInfo.addAll(getServiceInfo(source));
			}
		}
		return Collections.unmodifiableList(mServiceInfo);
	}

	/**
	 * Gets the service deployment info from a source
	 * 
	 * @param aSource
	 * @throws AeDeploymentException
	 */
	protected List<IAeServiceDeploymentInfo> getServiceInfo(
			IAeDeploymentSource aSource) throws AeDeploymentException {
		// Get the service info
		AeProcessDef processDef = aSource.getProcessDef();
		return AeServiceDeploymentUtil
				.getServices(processDef, aSource.getPdd());
	}
}
