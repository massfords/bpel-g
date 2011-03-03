// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeBprDeploymentSource.java,v 1.36 2007/11/21 03:26:02 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.io.AeBpelIO;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.AeServiceDeploymentUtil;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.IAePddXmlConstants;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.pdd.AePartnerLinkDescriptor;
import org.activebpel.rt.bpel.server.deploy.pdd.AePartnerLinkDescriptorFactory;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import bpelg.services.deploy.types.pdd.PartnerLinkType;
import bpelg.services.deploy.types.pdd.Pdd;

/**
 * Wraps the deployment of a single pdd from the BPR archive.
 */
public class AeBprDeploymentSource implements IAeDeploymentSource,
		IAePddXmlConstants {
	private Pdd mPdd;
	/** deserialized process def */
	private AeProcessDef mProcessDef;
	/** deployment context */
	private IAeDeploymentContext mContext;
	/** partner link data */
	private Collection<AePartnerLinkDescriptor> mPartnerLinkData;

	private static final String CONSOLE_ERROR = AeMessages
			.getString("AeBprDeploymentSource.ERROR_0"); //$NON-NLS-1$

	/**
	 * Create a deployment source and initialize a deployment source from the
	 * pass pdd and context,
	 * 
	 * @param aPdd
	 * @param aContext
	 */
	public AeBprDeploymentSource(Pdd aPdd, IAeDeploymentContext aContext)
			throws AeDeploymentException {
		mPdd = aPdd;
		mContext = aContext;
		init();
	}

	/**
	 * Initializes the internal state of the deployment source.
	 * 
	 * @throws AeDeploymentException
	 */
	protected void init() throws AeDeploymentException {
		initProcessDef();
		initPartnerLinkData();
	}

	/**
	 * Create the partner link data objects. Only the partnerRole data objects
	 * are created (because they are the only objects needed by the process
	 * deployment).
	 * 
	 * @throws AeDeploymentException
	 */
	protected void initPartnerLinkData() throws AeDeploymentException {
		mPartnerLinkData = new ArrayList();

		// using the ns from the doc element here because it's possible that
		// someone has
		// a pdd w/ one of our older namespaces
		for(PartnerLinkType plink : getPdd().getPartnerLinks().getPartnerLink()) {
			AePartnerLinkDescriptor partnerLinkData = AePartnerLinkDescriptorFactory
			.getInstance().createPartnerLinkDesc(plink, getProcessDef());
			mPartnerLinkData.add(partnerLinkData);
		}
	}

	/**
	 * Load the bpel and deserialize.
	 * 
	 * @throws AeDeploymentException
	 */
	protected void initProcessDef() throws AeDeploymentException {
		String location = getPdd().getLocation();
		InputStream in = null;
		try {
			in = getContext().getResourceAsStream(location);
			mProcessDef = AeBpelIO.deserialize(new InputSource(in));
		} catch (AeBusinessProcessException e) {
			String rootMsg = ""; //$NON-NLS-1$
			if (e.getRootCause() != null)
				rootMsg = e.getRootCause().getLocalizedMessage();
			Object[] args = { location, getPdd().getName().getLocalPart(), rootMsg };
			throw new AeDeploymentException(MessageFormat.format(CONSOLE_ERROR,
					args), e);
		} finally {
			AeCloser.close(in);
		}
	}

	/**
	 * Accessor for the deployment context.
	 */
	protected IAeDeploymentContext getContext() {
		return mContext;
	}

	/**
	 * Accessor for process qname.
	 * 
	 * @param aProcessElement
	 */
	public static QName getProcessName(Element aProcessElement) {
		String processQName = aProcessElement.getAttribute(ATT_NAME);
		// TODO - can this ever be null or empty?
		// probably should have failed some sort of
		// validation before it ever got here
		if (AeUtil.isNullOrEmpty(processQName)) {
			return null;
		}
		return AeXmlUtil.createQName(aProcessElement, processQName);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getProcessDef()
	 */
	public AeProcessDef getProcessDef() {
		return mProcessDef;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getPlanId()
	 */
	public int getPlanId() {
		// plan id's don't apply to non-versioned sources
		return 0;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getPartnerLinkDescriptors()
	 */
	public Collection<AePartnerLinkDescriptor> getPartnerLinkDescriptors() {
		return mPartnerLinkData;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getServices()
	 */
	public IAeServiceDeploymentInfo[] getServices()
			throws AeDeploymentException {
		return AeServiceDeploymentUtil.getServices(getProcessDef(),getPdd());
	}

	@Override
	public Pdd getPdd() {
		return mPdd;
	}
}
