// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeProcessDeployment.java,v 1.63 2008/02/19 14:20:09 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeNamespaceFilteredWSDLIterator;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.IAePartnerLink;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.io.AeBpelIO;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.addressing.IAePartnerAddressing;
import org.activebpel.rt.bpel.server.catalog.IAeCatalog;
import org.activebpel.rt.bpel.server.catalog.resource.AeReferenceTypeUtil;
import org.activebpel.rt.bpel.server.deploy.pdd.AePartnerLinkDescriptor;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.IAePartnerLinkType;
import org.activebpel.rt.wsdl.def.IAeRole;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.IAeWsAddressingHeaders;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.exolab.castor.xml.schema.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bpelg.services.deploy.types.pdd.PartnerLinkType;
import bpelg.services.deploy.types.pdd.PartnerRoleEndpointReferenceType;
import bpelg.services.deploy.types.pdd.Pdd;
import bpelg.services.deploy.types.pdd.ReferenceType;
import bpelg.services.deploy.types.pdd.SuspendFlag;

/**
 * Manages process deployment information for a BPEL deployment.
 */
public class AeProcessDeployment implements IAeProcessDeployment {
	/** The process definition which was deployed */
	private AeProcessDef mProcess;

	private Pdd mPdd;

	/** Plan id. */
	protected int mPlanId;

	/** Map of partner link name to partner link descriptor object. */
	protected Map mPartnerLinkDescriptors = new HashMap();

	/** Map of plDefKeys to AeServiceDeploymentInfo */
	private Map mServices = new HashMap();

	private IAeExpressionLanguageFactory mExpressionLanguageFactory;
	
	private Element mExtensions;

	/**
	 * Constructs the deployment under the passed context.
	 */
	public AeProcessDeployment(IAeDeploymentSource aSource)
			throws AeDeploymentException {
		mPdd = aSource.getPdd();
		mPlanId = aSource.getPlanId();
		for (IAeServiceDeploymentInfo service : aSource.getServices()) {
			// Touching all nodes to avoid issues when multiple threads examine
			// the same element
			AeXmlUtil.touchXmlNodes(service.getPolicies());
			getServiceMap()
					.put(service.getPartnerLinkDefKey(), service);
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessPlan#getMyRolePortType(org.activebpel.rt.bpel.def.AePartnerLinkDefKey)
	 */
	public QName getMyRolePortType(AePartnerLinkDefKey aPartnerLinkKey) {
		AePartnerLinkDef plink = getProcessDef().findPartnerLink(
				aPartnerLinkKey);
		IAePartnerLinkType plinkType = plink.getPartnerLinkType();
		IAeRole role = plinkType.findRole(plink.getMyRole());
		return role.getPortType().getQName();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.IAeProcessDeployment#getBpelSource()
	 */
	public String getBpelSource() {
		try {
			Document bpelDom = AeBpelIO.serialize(getProcessDef());
			return AeXMLParserBase.documentToString(bpelDom, true);
		} catch (Exception e) {
			AeException.logError(e,
					AeMessages.getString("AeProcessDeployment.ERROR_0")); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessPlan#isCreateInstance(org.activebpel.rt.bpel.def.AePartnerLinkOpKey)
	 */
	public boolean isCreateInstance(AePartnerLinkOpKey aPartnerLinkOpKey) {
		return getProcessDef().isCreateInstance(aPartnerLinkOpKey);
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessPlan#getCorrelatedPropertyNames(org.activebpel.rt.bpel.def.AePartnerLinkOpKey)
	 */
	public Collection getCorrelatedPropertyNames(
			AePartnerLinkOpKey aPartnerLinkOpKey) {
		return getProcessDef().getCorrelatedPropertyNames(aPartnerLinkOpKey);
	}

	/**
	 * Returns an endpoint reference for the given partner link for partnerRole,
	 * or null if not found.
	 * 
	 * @param aPartnerLink
	 *            the name of the partner link we are looking for
	 * @see org.activebpel.rt.bpel.server.IAeProcessDeployment#getPartnerEndpointRef(java.lang.String)
	 */
	public IAeEndpointReference getPartnerEndpointRef(String aPartnerLink) {
		AePartnerLinkDescriptor pLinkData = getPartnerLinkDescriptor(aPartnerLink);
		if (pLinkData != null) {
			return pLinkData.getPartnerEndpointReference();
		} else {
			return null;
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessPlan#getProcessDef()
	 */
	public AeProcessDef getProcessDef() {
		return mProcess;
	}

	/**
	 * Sets the process definition for this deployment.
	 * 
	 * @param aDef
	 *            the definition to be set
	 */
	public void setProcess(AeProcessDef aDef) {
		mProcess = aDef;
	}

	/**
	 * @see org.activebpel.rt.wsdl.IAeWSDLProvider#getWSDLIterator(java.lang.String)
	 */
	public Iterator getWSDLIterator(String aNamespaceUri) {
		return new AeNamespaceFilteredWSDLIterator(aNamespaceUri,
				getWSDLIterator(), this);
	}

	/**
	 * @see org.activebpel.rt.wsdl.IAeWSDLProvider#dereferenceIteration(java.lang.Object)
	 */
	public AeBPELExtendedWSDLDef dereferenceIteration(Object aIteration) {
		// if the iteration object is a key then its a request for context
		// resources
		// (resource included in the deployment), so extract the resource
		// directly from the cache
		// and return a wsdl object (schemas are wrapped, other hand back empty
		// wsdl object)
		if (aIteration instanceof ReferenceType) {
			ReferenceType key = (ReferenceType) aIteration;
			try {
				if (AeReferenceTypeUtil.isWsdlEntry(key)) {
					return (AeBPELExtendedWSDLDef) AeEngineFactory
							.getBean(IAeCatalog.class).getResourceCache()
							.getResource(key);
				} else if (AeReferenceTypeUtil.isSchemaEntry(key)) {
					Schema schema = (Schema) AeEngineFactory
							.getBean(IAeCatalog.class).getResourceCache()
							.getResource(key);
					return new AeBPELExtendedWSDLDef(schema);
				} else {
					return AeBPELExtendedWSDLDef.getDefaultDef();
				}
			} catch (AeException ex) {
				ex.logError();
				return AeBPELExtendedWSDLDef.getDefaultDef();
			}
		} else {
			return (AeBPELExtendedWSDLDef) aIteration;
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.server.IAeProcessDeployment#updatePartnerLink(org.activebpel.rt.bpel.IAePartnerLink,
	 *      org.activebpel.wsio.receive.IAeMessageContext)
	 */
	public void updatePartnerLink(IAePartnerLink aPartnerLink,
			IAeMessageContext aMessageContext)
			throws AeBusinessProcessException {
		IAeWsAddressingHeaders wsAddressing = aMessageContext
				.getWsAddressingHeaders();

		// update the myRole partnerlink to include the service qname that's
		// being hit.
		if (aPartnerLink.getMyReference() != null
				&& wsAddressing.getRecipient() != null) {
			aPartnerLink.getMyReference().updateReferenceData(
					wsAddressing.getRecipient());
			// Increment the partner link version
			aPartnerLink.incrementVersionNumber();
		}

		if (AeUtil
				.notNullOrEmpty(aPartnerLink.getDefinition().getPartnerRole())) {
			IAeEndpointReference partnerEndpoint = aPartnerLink
					.getPartnerReference();

			// Get the reply addressing headers
			IAePartnerAddressing partnerAddressing = AeEngineFactory
					.getBean(IAePartnerAddressing.class);
			IAeAddressingHeaders replyHeaders = partnerAddressing
					.getReplyAddressing(wsAddressing, wsAddressing.getAction());
			// This is the epr that came in with the request
			IAeWebServiceEndpointReference invokerEndpoint = replyHeaders
					.getRecipient();

			PartnerRoleEndpointReferenceType type = (PartnerRoleEndpointReferenceType) getEndpointSourceType(aPartnerLink
					.getLocationPath());
			if (type == PartnerRoleEndpointReferenceType.INVOKER) {
				if (invokerEndpoint == null) {
					if (partnerEndpoint == null) {
						throw new AeBusinessProcessException(
								AeMessages
										.getString("AeProcessDeployment.ERROR_3") + aPartnerLink); //$NON-NLS-1$
					}
				} else {
					partnerEndpoint.setReferenceData(invokerEndpoint);
				}
			}

			// Add any message context reference properties to partnerlink
			for (Iterator refProps = aMessageContext.getReferenceProperties(); refProps
					.hasNext();) {
				partnerEndpoint.addReferenceProperty((Element) refProps.next());
			}

			// Update the partner link with WS-Addressing info
			if (wsAddressing.getReplyTo() != null) {
				IAeEndpointReference newEndpoint = partnerAddressing
						.updateEndpointHeaders(replyHeaders, partnerEndpoint);
				partnerEndpoint.setReferenceData(newEndpoint);
			}

			// Increment the partner link version
			aPartnerLink.incrementVersionNumber();
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.server.IAeProcessDeployment#getEndpointSourceType(java.lang.String)
	 */
	public PartnerRoleEndpointReferenceType getEndpointSourceType(
			String aPartnerLink) {
		return getPartnerLinkDescriptor(aPartnerLink).getPartnerLinkType()
				.getPartnerRole().getEndpointReference();
	}

	/**
	 * @see org.activebpel.rt.wsdl.IAeContextWSDLProvider#getWSDLIterator()
	 */
	public Iterator getWSDLIterator() {
		return getAllReferenceTypes().iterator();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.IAeProcessDeployment#getPlanId()
	 */
	public int getPlanId() {
		return mPlanId;
	}

	/**
	 * Return the invoker uri for this partner link name or null if none is
	 * found.
	 * 
	 * @param aPartnerLink
	 */
	public String getInvokeHandler(String aPartnerLink) {
		return getPartnerLinkDescriptor(aPartnerLink).getInvokeHandler();
	}

	/**
	 * Accessor for a given partner link descriptor object.
	 * 
	 * @param aPartnerLink
	 */
	protected AePartnerLinkDescriptor getPartnerLinkDescriptor(
			String aPartnerLink) {
		AePartnerLinkDef plDef = getProcessDef().findPartnerLink(aPartnerLink);
		AePartnerLinkDefKey key = new AePartnerLinkDefKey(plDef);
		AePartnerLinkDescriptor descriptor = (AePartnerLinkDescriptor) mPartnerLinkDescriptors
				.get(key);
		if (descriptor == null) {
			try {
				descriptor = new AePartnerLinkDescriptor(
						new PartnerLinkType(), plDef.getLocationId());
			} catch (AeBusinessProcessException e) {
				// FIXME deploy - lazy
				throw new RuntimeException(e);
			}
			addPartnerLinkDescriptor(descriptor);
		}
		return descriptor;
	}

	/**
	 * Add partner link decriptor information.
	 * 
	 * @param aPartnerLinkDesc
	 */
	public void addPartnerLinkDescriptor(
			AePartnerLinkDescriptor aPartnerLinkDesc) {
		AePartnerLinkDef plDef = getProcessDef().findPartnerLink(
				aPartnerLinkDesc.getPartnerLinkDefKey());
		mPartnerLinkDescriptors.put(new AePartnerLinkDefKey(plDef),
				aPartnerLinkDesc);
	}

	/**
	 * Preprocess the underlying bpel def object.
	 * 
	 * @throws AeBusinessProcessException
	 */
	public void preProcessDefinition() throws AeBusinessProcessException {
		try {
			getProcessDef().preProcessForExecution(this,
					getExpressionLanguageFactory());
		} catch (AeException ae) {
			throw new AeBusinessProcessException(ae.getLocalizedMessage(), ae);
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessPlan#isSuspendProcessOnUncaughtFaultEnabled()
	 */
	public boolean isSuspendProcessOnUncaughtFaultEnabled() {
		return AeExceptionManagementUtil.isSuspendOnUncaughtFaultEnabled(
				getPdd().getSuspendProcessOnUncaughtFault(), getPdd().getPersistenceType());
	}

	/**
	 * @see org.activebpel.rt.bpel.server.IAeProcessDeployment#getServiceInfo(java.lang.String)
	 */
	public IAeServiceDeploymentInfo getServiceInfo(String aPartnerLink) {
		AePartnerLinkDef plDef = getProcessDef().findPartnerLink(aPartnerLink);
		// plDef should never be null
		if (plDef == null)
			return null;
		AePartnerLinkDefKey key = new AePartnerLinkDefKey(plDef);
		return (IAeServiceDeploymentInfo) getServiceMap().get(key);
	}

	/**
	 * 
	 * @return service map as array of IAeServiceDeploymentInfo
	 */
	public IAeServiceDeploymentInfo[] getServiceInfos() {
		List list = new ArrayList(getServiceMap().values());
		IAeServiceDeploymentInfo[] infos = new IAeServiceDeploymentInfo[list
				.size()];
		return (IAeServiceDeploymentInfo[]) list.toArray(infos);
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessPlan#getMyRolePolicies(org.activebpel.rt.bpel.impl.AePartnerLink)
	 */
	public List getMyRolePolicies(AePartnerLink aPartnerLink) {
		AePartnerLinkDefKey key = new AePartnerLinkDefKey(
				aPartnerLink.getDefinition());
		IAeServiceDeploymentInfo serviceInfo = (IAeServiceDeploymentInfo) getServiceMap()
				.get(key);

		if (serviceInfo != null)
			return serviceInfo.getPolicies();

		return Collections.EMPTY_LIST;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessPlan#getPartnerRolePolicies(org.activebpel.rt.bpel.impl.AePartnerLink)
	 */
	public List getPartnerRolePolicies(AePartnerLink aPartnerLink) {
		List policies = aPartnerLink.getPartnerReference()
				.getEffectivePolicies(this);
		return (policies != null ? policies : Collections.EMPTY_LIST);
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessPlan#isSuspendProcessOnInvokeRecoveryEnabled()
	 */
	public boolean isSuspendProcessOnInvokeRecoveryEnabled() {
		if (getPdd().getSuspendProcessOnInvokeRecovery() != null) {
			return getPdd().getSuspendProcessOnInvokeRecovery() == SuspendFlag.TRUE;
		} else {
			return AeEngineFactory.getEngineConfig().isSuspendProcessOnInvokeRecovery();
		}
	}

	/**
	 * Accessor for service map
	 * 
	 * @return Map
	 */
	protected Map getServiceMap() {
		return mServices;
	}

	/**
	 * @see org.activebpel.rt.bpel.impl.IAeProcessPlan#getExtensions()
	 */
	public Element getExtensions() {
		if (mExtensions == null && mPdd.getExtensions() != null && !mPdd.getExtensions().getAny().isEmpty()) {
            Document doc = AeXmlUtil.newDocument();
            Element root = doc.createElement("extensions"); //$NON-NLS-1$
            for(Element e : mPdd.getExtensions().getAny()) {
            	root.appendChild(doc.importNode(e, true));
            }
		}
		return mExtensions;
	}

	/**
	 * Setter for the extensions element
	 * 
	 * @param aExtensions
	 */
	public void setExtensions(Element aExtensions) {
		mExtensions = aExtensions;
		if (mExtensions != null)
			AeXmlUtil.touchXmlNodes(mExtensions);
	}

	public void setExpressionLanguageFactory(
			IAeExpressionLanguageFactory aFactory) {
		mExpressionLanguageFactory = aFactory;
	}

	public IAeExpressionLanguageFactory getExpressionLanguageFactory() {
		return mExpressionLanguageFactory;
	}

	@Override
	public Pdd getPdd() {
		return mPdd;
	}

	@Override
	public Collection<ReferenceType> getAllReferenceTypes() {
		List<ReferenceType> list = new LinkedList();
		List<ReferenceType> wsdl = getPdd().getReferences().getWsdl();
		for(ReferenceType ref : wsdl) {
			ref.setTypeURI(IAeConstants.WSDL_NAMESPACE);
		}
		list.addAll(wsdl);
		List<ReferenceType> xsd = getPdd().getReferences().getSchema();
		for(ReferenceType ref : xsd) {
			ref.setTypeURI(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		}
		list.addAll(xsd);
		list.addAll(getPdd().getReferences().getOther());
		return list;
	}
}