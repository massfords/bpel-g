// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AePddValidator.java,v 1.9 2007/12/20 19:12:52 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.text.MessageFormat;
import java.util.HashSet;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import bpelg.services.deploy.types.pdd.PartnerLinkType;
import bpelg.services.deploy.types.pdd.PartnerRoleEndpointReferenceType;

/**
 * Schema+ validation of pdd files.
 */
public class AePddValidator extends AeAbstractPddIterator {
	/**
	 * This class implements an <code>IAePddValidationErrorHandler</code> that
	 * will delegate the reporting of the erorrs and warnings to a base error
	 * reporter.
	 */
	private class AePddErrorHandler implements
			IAeResourceValidationErrorHandler {
		/** The PDD info. */
		private final AePddResource mPdd;
		/** The error reporter to use when an error is handled. */
		private final IAeBaseErrorReporter mReporter;

		/**
		 * Constructor.
		 * 
		 * @param aReporter
		 */
		public AePddErrorHandler(AePddResource aPdd, IAeBaseErrorReporter aReporter) {
			mPdd = aPdd;
			mReporter = aReporter;
		}

		/**
		 * Reports an error to the error reporter.
		 * 
		 * @param aMessage
		 */
		protected void reportError(String aMessage) {
			Object[] params = { mPdd.getName(), aMessage };
			mReporter
					.addError(
							AeMessages
									.getString("AePddValidator.REPORT_ERROR_FORMAT_SANS_LINENUMBER"), params, null); //$NON-NLS-1$
		}

		/**
		 * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#fatalError(java.lang.String)
		 */
		public void fatalError(String aMessage) {
			reportError(aMessage);
		}

		/**
		 * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#parseError(java.lang.String,
		 *      int)
		 */
		public void parseError(String aMessage, int aLineNumber) {
			reportError(aMessage);
		}

		/**
		 * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#parseFatalError(java.lang.String,
		 *      int)
		 */
		public void parseFatalError(String aMessage, int aLineNumber) {
			reportError(aMessage);
		}

		/**
		 * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#parseWarning(java.lang.String,
		 *      int)
		 */
		public void parseWarning(String aMessage, int aLineNumber) {
			Object[] params = { mPdd.getName(), aMessage,
					new Integer(aLineNumber) };
			mReporter
					.addWarning(
							AeMessages
									.getString("AePddValidator.REPORT_ERROR_FORMAT_WITH_LINENUMBER"), params, null); //$NON-NLS-1$
		}

		/**
		 * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#contentError(java.lang.String,
		 *      org.w3c.dom.Node)
		 */
		public void contentError(String aMessage, Node aNode) {
			reportError(aMessage);
		}

		/**
		 * @see org.activebpel.rt.bpel.server.deploy.validate.IAeResourceValidationErrorHandler#contentWarning(java.lang.String,
		 *      org.w3c.dom.Node)
		 */
		public void contentWarning(String aMessage, Node aNode) {
			Object[] params = { mPdd.getName(), aMessage };
			mReporter
					.addWarning(
							AeMessages
									.getString("AePddValidator.REPORT_ERROR_FORMAT_SANS_LINENUMBER"), params, null); //$NON-NLS-1$
		}
	}

	protected void validateImpl(AePddResource aPdd, IAeBpr aBprFile,
			IAeBaseErrorReporter aReporter) throws AeException {
		AePddErrorHandler handler = new AePddErrorHandler(aPdd, aReporter);
		doAdditionalPddValidation(aPdd, handler);
	}

	/**
	 * Does some additional PDD validation checks. This checks for problems that
	 * can not be caught by the schema.
	 * 
	 * @param aDocument
	 * @param aHandler
	 */
	private void doAdditionalPddValidation(AePddResource aPdd,
			IAeResourceValidationErrorHandler aHandler) {
		try {
			checkPartnerRoles(aPdd, aHandler);
			checkPartnerLinks(aPdd, aHandler);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = MessageFormat
					.format(AeMessages
							.getString("AePddValidator.ERROR_DURING_STATIC_VALIDATION"), new Object[] { e.getLocalizedMessage() }); //$NON-NLS-1$
			aHandler.fatalError(msg);
		}
	}

	/**
	 * Ensures that each partner link has a myRole or a partnerRole or both. And
	 * that there isn't more than one entry per partnelink name.
	 * 
	 * @param aDocument
	 * @param aHandler
	 */
	private void checkPartnerLinks(AePddResource aPdd,
			IAeResourceValidationErrorHandler aHandler) {
		HashSet<String> names = new HashSet<String>();
		// Make sure that every partner link has either a partner role, or a my
		// role, or both.
		for (PartnerLinkType partnerLink : aPdd.getPdd().getPartnerLinks()
				.getPartnerLink()) {

			// check for duplicates
			String name = partnerLink.getName() + ":"
					+ partnerLink.getLocation();
			if (AeUtil.notNullOrEmpty(name)) {
				if (!names.add(name)) {
					aHandler.contentError(
							AeMessages
									.format("AePddValidator.DUPLICATE_PARTNER_LINK_CONTENT_ERROR", name), null); //$NON-NLS-1$
				}
			}

			// check that it has a myrole or partnerrole assignment
			if (partnerLink.getMyRole() == null
					&& partnerLink.getPartnerRole() == null)
				aHandler.contentError(
						AeMessages
								.getString("AePddValidator.INVALID_PARTNER_LINK_CONTENT_ERROR"), null); //$NON-NLS-1$
		}

		// check for duplicate my role services
		HashSet<String> serviceNames = new HashSet<String>();
		for (PartnerLinkType partnerLink : aPdd.getPdd().getPartnerLinks()
				.getPartnerLink()) {
			if (partnerLink.getMyRole() != null) {
				if (!serviceNames.add(partnerLink.getMyRole().getService())) {
					aHandler.contentError(
							AeMessages
									.format("AePddValidator.DUPLICATE_MYROLE_SERVICE_NAME_ERROR", partnerLink.getMyRole().getService()), null); //$NON-NLS-1$ 
				}
			}
		}

	}

	/**
	 * Runs a few checks on the partner roles that we can't incorporate into the
	 * schema. These include the following: 1. if the attribute
	 * endpointReference="static" then there must be an endpoint defined 2. if
	 * the attribute endpointReference!="static" then there must NOT be an
	 * endpoint defined 3. if the attributes customInvokerUri and invokeHandler
	 * are both present, generate a warning since customInvokerUri is deprecated
	 * and ignored when invokeHandler is present. 4. if customInvokerUri is
	 * present and invokeHandler is missing, then add warning that they should
	 * switch to invokeHandler.
	 */
	private void checkPartnerRoles(AePddResource aPdd,
			IAeResourceValidationErrorHandler aHandler) {
		// Get a list of the partnerRole nodes to check if they are "static".
		// If they ARE, then they need a wsa:EndpointReference node.
		for (PartnerLinkType plink : aPdd.getPdd().getPartnerLinks().getPartnerLink()) {
			if (plink.getPartnerRole() != null && plink.getPartnerRole().getEndpointReference() == PartnerRoleEndpointReferenceType.STATIC) {
				
				int numChildren = 0;
				boolean hasEndpointRef = false;
				for(Element node : plink.getPartnerRole().getAny()) {
					numChildren++;
					if ("EndpointReference".equals(node.getLocalName()) && //$NON-NLS-1$
							(IAeConstants.WSA_NAMESPACE_URI.equals(node
									.getNamespaceURI())
									|| IAeConstants.WSA_NAMESPACE_URI_2004_03
											.equals(node.getNamespaceURI())
									|| IAeConstants.WSA_NAMESPACE_URI_2005_08
											.equals(node.getNamespaceURI()) || IAeConstants.WSA_NAMESPACE_URI_2004_08
									.equals(node.getNamespaceURI()))) {
						hasEndpointRef = true;
					}
					
				}
				if (numChildren != 1 || !hasEndpointRef) {
					String msg = AeMessages
							.getString("AePddValidator.INVALID_STATIC_ENDPOINT_CONTENT_ERROR"); //$NON-NLS-1$
					aHandler.contentError(msg, null);
				}
			} else if (plink.getPartnerRole() != null && plink.getPartnerRole().getEndpointReference() != PartnerRoleEndpointReferenceType.STATIC){
				if (!plink.getPartnerRole().getAny().isEmpty()) {
					String msg = AeMessages
							.getString("AePddValidator.INVALID_NONSTATIC_ENDPOINT_CONTENT_ERROR"); //$NON-NLS-1$
					aHandler.contentError(msg, null);
				}
			}
		}
	}
}
