//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/pdd/AePartnerLinkDescriptor.java,v 1.7 2006/06/26 18:28:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.pdd;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.impl.AeEndpointReference;
import org.w3c.dom.Element;

import bpelg.services.deploy.types.pdd.PartnerLinkType;
import bpelg.services.deploy.types.pdd.PartnerRoleEndpointReferenceType;

/**
 * Wraps the pdd partner link information. This object will also be created in
 * the persistence layer, reconstructed from DB information.
 */
public class AePartnerLinkDescriptor {

	private final PartnerLinkType mPlinkType;
	/** Parnter endpoint reference. */
	protected IAeEndpointReference mPartnerEndpointReference;
	/** Partner link path. */
	protected final int mPartnerLinkId;
	
	public AePartnerLinkDescriptor(PartnerLinkType aType, int aPartnerLinkId) throws AeBusinessProcessException {
		mPlinkType = aType;
		mPartnerLinkId = aPartnerLinkId;
		initPartnerEndpointReference();
	}

	/**
	 * @return Returns the <code>IAeEndpointReference</code> impl.
	 * @throws AeBusinessProcessException 
	 */
	public IAeEndpointReference getPartnerEndpointReference() {
		return mPartnerEndpointReference;
	}

	private void initPartnerEndpointReference() throws AeBusinessProcessException {
		if (mPlinkType.getPartnerRole() != null && 
				mPlinkType.getPartnerRole().getEndpointReference() == PartnerRoleEndpointReferenceType.STATIC) {
			for (Element e : mPlinkType.getPartnerRole().getAny()) {
				if (e.getLocalName().equals("EndpointReference")) {
					mPartnerEndpointReference = new AeEndpointReference();
					mPartnerEndpointReference.setReferenceData(e);
					break;
				}
			}
		}
	}

	/**
	 * @return Returns the invoke handler
	 */
	public String getInvokeHandler() {
		if (mPlinkType.getPartnerRole() != null)
			return mPlinkType.getPartnerRole().getInvokeHandler();
		return null;
	}

	/**
	 * @return Returns the partnerLinkName.
	 */
	public String getPartnerLinkName() {
		return mPlinkType.getName();
	}

	/**
	 * @return Returns the partnerLinkId.
	 */
	public int getPartnerLinkId() {
		return mPartnerLinkId;
	}

	/**
	 * Returns a new partner link def key for this partner link.
	 */
	public AePartnerLinkDefKey getPartnerLinkDefKey() {
		return new AePartnerLinkDefKey(getPartnerLinkName(), getPartnerLinkId());
	}
	
	public PartnerLinkType getPartnerLinkType() {
		return mPlinkType;
	}
}
