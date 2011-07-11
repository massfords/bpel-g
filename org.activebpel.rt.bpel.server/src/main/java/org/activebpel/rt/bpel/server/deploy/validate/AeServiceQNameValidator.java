//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeServiceQNameValidator.java,v 1.24 2008/02/17 21:38:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.catalog.AeCatalogMappings;
import org.activebpel.rt.bpel.server.catalog.IAeCatalog;
import org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.bpel.server.wsdl.AeResourceResolver;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bpelg.services.deploy.types.catalog.Catalog;
import bpelg.services.deploy.types.pdd.PartnerLinkType;
import bpelg.services.deploy.types.pdd.PartnerRoleEndpointReferenceType;
import bpelg.services.deploy.types.pdd.ReferenceType;

/**
 * Validate that the service QNames for static endpoint refs defined in the pdd
 * files point to valid service names in the wsdl.
 */
public class AeServiceQNameValidator implements IAePredeploymentValidator {

	/** Validation error message template. */
	private static final String WARNING_SERVICE_NOT_FOUND = AeMessages
			.getString("AeServiceQNameValidator.0"); //$NON-NLS-1$
	/** Wsdl error message template. */
	private static final String WSDL_ERROR = AeMessages
			.getString("AeServiceQNameValidator.1"); //$NON-NLS-1$
	/** No port found for service qname error. */
	private static final String NO_PORT_FOUND = AeMessages
			.getString("AeServiceQNameValidator.2"); //$NON-NLS-1$
	/** Invalid or undefined port. */
	private static final String INVALID_OR_UNDEFINED_PORT_FOUND = AeMessages
			.getString("AeServiceQNameValidator.InvalidOrUndefinedPort"); //$NON-NLS-1$
	/** console error message */
	private static final String CONSOLE_ERROR = AeMessages
			.getString("AeServiceQNameValidator.3"); //$NON-NLS-1$

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr,
	 *      org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
	 */
	public void validate(IAeBpr aBprFile, IAeBaseErrorReporter aReporter)
			throws AeException {
		AeResourceResolver resolver = createResolver(aBprFile);
		Map<QName,Service> servicesMap = extractServices(aBprFile, aReporter, resolver);

		if (!aReporter.hasErrors()) {
			for (AePddResource pddResource : aBprFile.getPddResources()) {
				
				for(PartnerLinkType plink : pddResource.getPdd().getPartnerLinks().getPartnerLink()) {
					if (plink.getPartnerRole() != null && 
							plink.getPartnerRole().getEndpointReference() == PartnerRoleEndpointReferenceType.STATIC && 
							!plink.getPartnerRole().getAny().isEmpty()) {
						
						Element pddDom = plink.getPartnerRole().getAny().get(0);
						NodeList services = pddDom.getElementsByTagNameNS(
								WSA_NAMESPACE_URI, SERVICE_NAME_ELEMENT);
						checkServiceQNames(services, servicesMap, pddResource.getName(), aReporter);
						services = pddDom.getElementsByTagNameNS(
								WSA_NAMESPACE_URI_2004_03, SERVICE_NAME_ELEMENT);
						checkServiceQNames(services, servicesMap, pddResource.getName(), aReporter);
						services = pddDom.getElementsByTagNameNS(
								WSA_NAMESPACE_URI_2004_08, SERVICE_NAME_ELEMENT);
						checkServiceQNames(services, servicesMap, pddResource.getName(), aReporter);
						services = pddDom.getElementsByTagNameNS(
								WSA_NAMESPACE_URI_2005_08, SERVICE_NAME_ELEMENT);
						checkServiceQNames(services, servicesMap, pddResource.getName(), aReporter);
					}
				}
				
			}
		}
	}

	/**
	 * Validate that the service QNames extracted from the pdd are in the
	 * reference collection.
	 * 
	 * @param aServices
	 * @param aServicesMap
	 * @param aPddName
	 * @param aReporter
	 */
	protected void checkServiceQNames(NodeList aServices, Map<QName,Service> aServicesMap, String aPddName, IAeBaseErrorReporter aReporter) {
		for (int i = 0; i < aServices.getLength(); i++) {
			Element serviceElement = (Element) aServices.item(i);
			QName serviceQName = extractServiceQName(serviceElement);
			String portName = serviceElement.getAttribute("PortName"); //$NON-NLS-1$

			if (!aServicesMap.containsKey(serviceQName)) {
				String[] args = { aPddName, AeUtil.qNameToString(serviceQName) };
				aReporter.addWarning(WARNING_SERVICE_NOT_FOUND, args, null);
			} else {
				// ensure that if the service is going to be accessed that
				// it contains a port
				Service wsdl4JService = aServicesMap.get(serviceQName);
				Port port = wsdl4JService.getPort(portName);
				if (port == null) {
					String[] args = { aPddName,
							AeUtil.qNameToString(serviceQName) };
					aReporter.addError(NO_PORT_FOUND, args, null);
				} else if (port.getBinding() == null
						|| port.getBinding().isUndefined()) {
					String[] args = { aPddName,
							AeUtil.qNameToString(serviceQName) };
					aReporter.addError(INVALID_OR_UNDEFINED_PORT_FOUND, args,
							null);
				}
			}
		}
	}

	/**
	 * Create a wsdl resolver and populate it with entries from the bpr file.
	 * 
	 * @param aBprFile
	 * @throws AeException
	 */
	protected AeResourceResolver createResolver(IAeBpr aBprFile)
			throws AeException {
		// populate a wsdl resolver
		AeResourceResolver resolver = new AeResourceResolver();
		Catalog catalogDocument = aBprFile.getCatalogDocument();

		if (catalogDocument != null) {
			AeCatalogMappings catalog = new AeCatalogMappings(aBprFile,
					IAeCatalog.KEEP_EXISTING_RESOURCE);
			IAeCatalogMapping[] mappings = (IAeCatalogMapping[]) catalog
					.getResources()
					.values()
					.toArray(
							new IAeCatalogMapping[catalog.getResources()
									.values().size()]);
			resolver.addEntries(mappings, IAeCatalog.KEEP_EXISTING_RESOURCE);
		}
		return resolver;
	}

	/**
	 * Extract the service QName from the service element.
	 * 
	 * @param aServiceElement
	 */
	protected QName extractServiceQName(Element aServiceElement) {
		String prefixedName = AeXmlUtil.getText(aServiceElement);
		String nsPrefix = AeXmlUtil.extractPrefix(prefixedName);
		String localPart = AeXmlUtil.extractLocalPart(prefixedName);
		String nsUri = AeXmlUtil.getNamespaceForPrefix(aServiceElement,
				nsPrefix);
		return new QName(nsUri, localPart);
	}

	/**
	 * Extract service qnames for all wsdl contained in all pdd wsdl refs.
	 * 
	 * @param aFile
	 * @param aResolver
	 */
	protected Map<QName,Service> extractServices(IAeBpr aFile, IAeBaseErrorReporter aReporter, AeResourceResolver aResolver) {
		// load all of the wsdl defs for each wsdl ref section of each pdd
		// and create wsdl defs - ask each def for its service names
		Map<QName,Service> services = new HashMap<QName,Service>();
		String pddName = null;

		try {
			for (AePddResource pdd : aFile.getPddResources()) {
				addServiceNamesFromWsdlRefs(pdd, services, aResolver);
			}
		} catch (Throwable ae) {
			AeException.logError(ae,
					MessageFormat.format(CONSOLE_ERROR, pddName));
			aReporter.addError(WSDL_ERROR, new String[] { pddName }, null);
			return null;
		}

		return services;
	}

	/**
	 * Walk the wsdl refs node list, create the def for each entry and add its
	 * services to the map (service qname key, service).
	 * 
	 * @param aWsdlRefs
	 * @param aServices
	 * @param aResolver
	 * @throws AeException
	 */
	protected void addServiceNamesFromWsdlRefs(AePddResource aPddResource, Map<QName,Service> aServices, AeResourceResolver aResolver) throws AeException {
		for (ReferenceType ref : aPddResource.getPdd().getReferences().getWsdl()) {
			if (aResolver.hasMapping(ref.getLocation())) {
				AeBPELExtendedWSDLDef def = aResolver.newInstance(ref.getLocation());
				aServices.putAll(def.getServices());
			}
		}
	}
}
