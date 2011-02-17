// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentFactoryImpl.java,v 1.6 2008/01/22 17:11:18 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.bpel.server.deploy.validate.IAeValidationHandler;

/**
 * Default IAeDeploymentFactory impl. Gets all of its deployer impls from the
 * config map. None of its deployers should contain any state info (as they are
 * all treated like singletons).
 */
public class AeDeploymentFactoryImpl implements IAeDeploymentFactory {

	/** Validation handler. */
	private IAeValidationHandler mValidationHandler;
	/** Partner definition deployer. */
	private IAePdefDeployer mPdefDeployer;
	/** Wsdl catalog deployer. */
	private IAeCatalogDeployer mCatalogDeployer;
	/** Web services deployer. */
	private IAeWebServicesDeployer mWebServicesDeployer;
	/** BPEL process deployer. */
	private IAeBpelDeployer mBpelDeployer;

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getBpelDeployer()
	 */
	public IAeBpelDeployer getBpelDeployer() {
		return mBpelDeployer;
	}

	public void setBpelDeployer(IAeBpelDeployer aDeployer) {
		mBpelDeployer = aDeployer;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getPDefDeployer()
	 */
	public IAePdefDeployer getPDefDeployer() {
		return mPdefDeployer;
	}

	public void setPdefDeployer(IAePdefDeployer aDeployer) {
		mPdefDeployer = aDeployer;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getCatalogDeployer()
	 */
	public IAeCatalogDeployer getCatalogDeployer() {
		return mCatalogDeployer;
	}

	public void setCatalogDeployer(IAeCatalogDeployer aDeployer) {
		mCatalogDeployer = aDeployer;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getWebServicesDeployer()
	 */
	public IAeWebServicesDeployer getWebServicesDeployer() {
		return mWebServicesDeployer;
	}

	public void setWebServicesDeployer(IAeWebServicesDeployer aDeployer) {
		mWebServicesDeployer = aDeployer;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getValidationHandler()
	 */
	public IAeValidationHandler getValidationHandler() {
		return mValidationHandler;
	}
	
	public void setValidationHandler(IAeValidationHandler aHandler) {
		mValidationHandler= aHandler;
	}
}
