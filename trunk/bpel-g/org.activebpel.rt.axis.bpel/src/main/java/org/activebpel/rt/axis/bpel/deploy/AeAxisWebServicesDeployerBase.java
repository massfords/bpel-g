// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/deploy/AeAxisWebServicesDeployerBase.java,v 1.5 2008/02/17 21:29:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.deploy;

import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.AeWsdlReferenceTracker;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLPolicyHelper;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDUndeployment;
import org.apache.axis.server.AxisServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bpelg.services.deploy.types.pdd.MyRoleBindingType;
import bpelg.services.processes.types.ServiceDeployment;
import bpelg.services.processes.types.ServiceDeployments;

/**
 * WebServicesDeployer impl that deploys web services to Axis.
 */
public abstract class AeAxisWebServicesDeployerBase extends AeAxisBase
        implements IAeDeploymentHandler {

    @Override
    public void deploy(IAeDeploymentContainer aContainer,
                       IAeDeploymentLogger aLogger) throws AeException {
        ServiceDeployments services = aContainer.getServiceDeploymentInfo();
        for (ServiceDeployment service : services.getServiceDeployment()) {
            resolveServicePolicies(service);
        }
        Document wsddDoc = createWsdd(aContainer.getServiceDeploymentInfo());
        if (wsddDoc != null)
            deployToWebServiceContainer(wsddDoc,
                    aContainer.getResourceClassLoader());
    }

    @Override
    public void undeploy(IAeDeploymentContainer aContainer) throws AeException {
        Document wsddDoc = null;
        if (aContainer.getServiceDeploymentInfo() != null) {
            wsddDoc = createWsddForUndeployment(aContainer
                    .getServiceDeploymentInfo());
        }
        if (wsddDoc != null)
            undeployFromWebServiceContainer(wsddDoc);
    }

    /**
     * Creates a wsdd service undeployment descriptor based on service names
     * only.
     *
     * @param aServices
     * @return wsdd document
     * @throws AeDeploymentException
     */
    protected Document createWsddForUndeployment(
            ServiceDeployments aServices) throws AeDeploymentException {
        AeWsddBuilder builder = new AeWsddBuilder();
        for (ServiceDeployment serviceData : aServices.getServiceDeployment()) {
            builder.addServiceElement(serviceData.getService(), null);
        }
        return builder.getWsddDocument();
    }

    /**
     * Creates a wsdd service deployment descriptor
     *
     * @param aServices
     * @return wsdd document
     * @throws AeDeploymentException
     */
    protected Document createWsdd(ServiceDeployments aServices)
            throws AeDeploymentException {
        AeWsddBuilder builder = new AeWsddBuilder();
        for (ServiceDeployment serviceData : aServices.getServiceDeployment()) {
            MyRoleBindingType binding = serviceData.getBinding();
            if (binding == MyRoleBindingType.RPC) {
                builder.addRpcService(serviceData);
            } else if (binding == MyRoleBindingType.RPC_LIT) {
                builder.addRpcLiteralService(serviceData);
            } else if (binding == MyRoleBindingType.MSG) {
                builder.addMsgService(serviceData);
            } else if (binding == MyRoleBindingType.POLICY) {
                builder.addPolicyService(serviceData);
            } else if (binding != MyRoleBindingType.EXTERNAL) {
                AeException
                        .logWarning(AeMessages
                                .format("AeAxisWebServicesDeployerBase.UNKNOWN_ROLE_IN_WSDD", serviceData.getService())); //$NON-NLS-1$
            }
        }

        return builder.getWsddDocument();
    }

    /**
     * @see org.activebpel.rt.axis.bpel.deploy.AeAxisBase#deployToAxis(java.lang.ClassLoader,
     *      org.w3c.dom.Document, org.w3c.dom.Document)
     */
    protected void deployToAxis(ClassLoader aClassLoader, Document aAxisDoc,
                                Document aAdminDoc) throws Exception {
        // remember old classloader
        ClassLoader previous = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(aClassLoader);
            EngineConfiguration config = getAxisServer().getConfig();
            if (config instanceof WSDDEngineConfiguration) {
                WSDDDeployment deployment = ((WSDDEngineConfiguration) config)
                        .getDeployment();
                new AeBprDeployment(aAxisDoc.getDocumentElement())
                        .deployToRegistry(deployment);
                // TODO --- may need client deployment here
            }

            refreshAndSave();
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    /**
     * Update global options and configuration.
     *
     * @throws Exception
     */
    protected void refreshAndSave() throws Exception {
        getAxisServer().refreshGlobalOptions();
        getAxisServer().saveConfiguration();
    }

    /**
     * @see org.activebpel.rt.axis.bpel.deploy.AeAxisBase#undeployFromAxis(org.w3c.dom.Document,
     *      org.w3c.dom.Document)
     */
    protected void undeployFromAxis(Document aAxisDoc, Document aAdminDoc)
            throws Exception {
        Element undeployEl = aAxisDoc.getDocumentElement();

        EngineConfiguration config = getAxisServer().getConfig();

        if (config instanceof WSDDEngineConfiguration) {
            new WSDDUndeployment(undeployEl)
                    .undeployFromRegistry(((WSDDEngineConfiguration) config)
                            .getDeployment());

            removeWsdlReferences(aAxisDoc);
        }

        refreshAndSave();
    }

    /**
     * Once the services have been removed, remove any
     * <code>IAeWsdlRefereces</code> that were associated with those services.
     *
     * @param aWsdd
     */
    protected void removeWsdlReferences(Document aWsdd) {
        NodeList services = aWsdd.getElementsByTagName("service"); //$NON-NLS-1$
        for (int i = 0; i < services.getLength(); i++) {
            String serviceName = ((Element) services.item(i))
                    .getAttribute("name"); //$NON-NLS-1$
            AeWsdlReferenceTracker.unregisterReference(serviceName);
        }
    }

    /**
     * Resolves policy references for service deployment
     *
     * @param aService
     * @throws AeException
     */
    protected void resolveServicePolicies(ServiceDeployment aService) throws AeException {
        if (!AeUtil.isNullOrEmpty(aService.getAny())) {
            IAeDeploymentProvider provider = AeEngineFactory.getBean(IAeDeploymentProvider.class);
            IAeContextWSDLProvider wsdlProvider = provider.findCurrentDeployment(aService.getProcessName());
            List<Element> policies = AeWSDLPolicyHelper.resolvePolicyReferences(wsdlProvider, aService.getAny());
            if (!AeUtil.isNullOrEmpty(policies)) {
                aService.getAny().clear();
                aService.getAny().addAll(policies);
            }
        }
    }

    /**
     * Accessor for Axis server.
     */
    abstract protected AxisServer getAxisServer();
}
