// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeDuplicateServiceNameValidator.java,v 1.8 2006/07/18 20:05:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.deploy.AeRoutingInfo;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

import bpelg.services.deploy.types.pdd.MyRoleType;
import bpelg.services.deploy.types.pdd.PartnerLinkType;

/**
 * Validates that same service name is not used twice (by two diff myRole
 * elements) within a BPR file.
 */
public class AeDuplicateServiceNameValidator implements
		IAePredeploymentValidator {
	/** Error msg pattern for duplicate service name desc within the BPR. */
	private static final String DUPLICATE_SERVICE_WITHIN_BPR = AeMessages
			.getString("AeDuplicateServiceNameValidator.0"); //$NON-NLS-1$
	/** Error msg pattern for duplicate service name with another BPR */
	private static final String DUPLICATE_SERVICE_OTHER_BPR = AeMessages
			.getString("AeDuplicateServiceNameValidator.OtherBpr"); //$NON-NLS-1$

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr,
	 *      org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
	 */
	public void validate(IAeBpr aBprFile, IAeBaseErrorReporter aReporter)
			throws AeException {
		Map<String,String> myRoleServices = new HashMap();

		for (AePddResource pddResource : aBprFile.getPddResources()) {
			for (PartnerLinkType plinkType : pddResource.getPdd()
					.getPartnerLinks().getPartnerLink()) {
				MyRoleType myRole = plinkType.getMyRole();
				if (myRole == null)
					continue;

				String serviceName = myRole.getService();

				if (myRoleServices.containsKey(serviceName)) {
					String otherPdd = myRoleServices.get(serviceName);
					String[] args = { serviceName, pddResource.getName(),
							otherPdd, aBprFile.getBprFileName() };
					aReporter
							.addError(DUPLICATE_SERVICE_WITHIN_BPR, args, null);
				} else {
					myRoleServices.put(serviceName, pddResource.getName());

					// check to see if the plan is already deployed in another
					// bpr
					AeRoutingInfo routingInfo = null;
					try {
						routingInfo = AeEngineFactory.getBean(
								IAeDeploymentProvider.class)
								.getRoutingInfoByServiceName(serviceName);
						QName conflictingProcess = routingInfo.getServiceData()
								.getProcessQName();
						String[] args = { serviceName, pddResource.getName(),
								aBprFile.getBprFileName(),
								conflictingProcess.getNamespaceURI(),
								conflictingProcess.getLocalPart() };
						aReporter.addError(DUPLICATE_SERVICE_OTHER_BPR, args,
								null);
					} catch (AeBusinessProcessException e) {
						// an exception means that there is no process deployed
						// using this service
					}
				}
			}
		}
	}
}
