// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeServiceDeploymentUtil.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bpelg.services.deploy.types.pdd.MyRoleBindingType;
import bpelg.services.deploy.types.pdd.MyRoleType;
import bpelg.services.deploy.types.pdd.PartnerLinkType;
import bpelg.services.deploy.types.pdd.Pdd;

/**
 * Static utilities to determine service info from deployment documents
 */
public class AeServiceDeploymentUtil implements IAeConstants {

	/**
	 * Gets the service info from the pdd element
	 * 
	 * @param aProcessDef
	 * @param aProcessElement
	 * @throws AeDeploymentException
	 */
	public static IAeServiceDeploymentInfo[] getServices(
			AeProcessDef aProcessDef, Pdd aPdd) throws AeDeploymentException {
		try {
			// data for creating the processNamespace and
			// processName parameter elements
			// - this data is static for all partnerLink elements
			// with the myRole child for a given process
			QName processQname = aPdd.getName();

			List services = new ArrayList();

			// locate all of the myRole elements and build the
			// appropriate service element for each one
			for (PartnerLinkType plink : aPdd.getPartnerLinks()
					.getPartnerLink()) {
				if (plink.getMyRole() == null)
					continue;
				
				MyRoleType myRole = plink.getMyRole();

				String serviceName = myRole.getService();
				MyRoleBindingType binding = myRole.getBinding();

				// Get the partner link name and (optional) location.
				String partnerLinkName = plink.getName();
				String partnerLinkLocation = plink.getLocation();
				String partnerLink = partnerLinkName;
				if (AeUtil.notNullOrEmpty(partnerLinkLocation))
					partnerLink = partnerLinkLocation;

				// Look up the partner link def in the process.
				AePartnerLinkDef plDef = aProcessDef
						.findPartnerLink(partnerLink);
				AePartnerLinkDefKey plDefKey = new AePartnerLinkDefKey(plDef);

				String allowedRoles = myRole.getAllowedRoles();
				List<Element> policies = myRole.getAny();

				AeServiceDeploymentInfo serviceData = new AeServiceDeploymentInfo(
						serviceName, plDefKey, binding, allowedRoles, policies);
				serviceData.setProcessQName(processQname);
				services.add(serviceData);
			}

			IAeServiceDeploymentInfo[] servicesAdded = new IAeServiceDeploymentInfo[services
					.size()];
			services.toArray(servicesAdded);
			return servicesAdded;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AeDeploymentException(
					AeMessages.getString("AeWsddService.ERROR_7"), e); //$NON-NLS-1$
		}
	}

	/**
	 * Gets policy nodes from a myRole or partnerRole element.
	 * 
	 * @param aElement
	 * @return list of matching element nodes.
	 * @throws Exception
	 */
	protected static List getPolicyNodes(Element aElement) throws Exception {
		NodeList policies = aElement.getElementsByTagNameNS(
				IAeConstants.WSP_NAMESPACE_URI, "*"); //$NON-NLS-1$
		List elements = new ArrayList(policies.getLength());
		for (int i = 0; i < policies.getLength(); i++) {
			elements.add(policies.item(i));
		}
		return elements;
	}
}
