//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AePartnerLinkValidator.java,v 1.6 2007/06/06 20:26:03 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;

/**
 * Issue warnings if that any partner links defined in the bpel are not defined
 * in the process deployment descriptor.
 * 
 * Fail validation if any partner links (partnerrole or myrole) are present in
 * the pdd BUT NOT in the bpel process.
 */
public class AePartnerLinkValidator extends AeAbstractPddIterator {
	protected void validateImpl(AePddResource aPdd, IAeBpr aBprFile,
			IAeBaseErrorReporter aReporter) throws AeException {
		// FIXME deploy validation - impl the comments below

		// find all partner links with partner roles that have an
		// initializePartnerRole = no
		// these MUST NOT be initialized by the pdd

		// find all partner links with partner roles that have
		// initializePartnerRole = yes
		// these MUST be initialized by the pdd either with a static or have
		// invoker style

		// find all partner links with myrole. They must be present in the pdd

		// warn on all pdd plinks that are not in bpel
	}
}
