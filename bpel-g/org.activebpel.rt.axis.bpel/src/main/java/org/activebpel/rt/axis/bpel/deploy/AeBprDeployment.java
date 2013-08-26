// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/deploy/AeBprDeployment.java,v 1.3 2006/10/18 23:35:02 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002, 2003, 2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.deploy;

// axis config and utils

import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.server.deploy.IAeWsddConstants;
import org.apache.axis.ConfigurationException;
import org.apache.axis.deployment.wsdd.*;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bpr deployment extends the wsdd deployment of axis for monitoring and
 * setting classloaders used for contexts.
 */
public class AeBprDeployment extends WSDDDeployment {
    private static final long serialVersionUID = -1534981658258732140L;

    /**
     * Provider <code>QName</code> for BPEL RPC services.
     */
    private static final QName BPEL_RPC_PROVIDER_QNAME =
            new QName(IAeWsddConstants.PROVIDER_NAMESPACE_URI, IAeWsddConstants.NAME_RPC_BINDING);

    /**
     * Provider <code>QName</code> for BPEL RPC Literal services.
     */
    private static final QName BPEL_RPC_LIT_PROVIDER_QNAME =
            new QName(IAeWsddConstants.PROVIDER_NAMESPACE_URI, IAeWsddConstants.NAME_RPC_LIT_BINDING);

    /**
     * Provider <code>QName</code> for BPEL MSG services.
     */
    private static final QName BPEL_MSG_PROVIDER_QNAME =
            new QName(IAeWsddConstants.PROVIDER_NAMESPACE_URI, IAeWsddConstants.NAME_MSG_BINDING);

    /**
     * for deployment logging purposes
     */
    protected static final Logger log = Logger.getLogger("ActiveBPEL"); //$NON-NLS-1$

    /**
     * Mapping of service classloaders (qname to classloader).
     */
    protected Map<Object, ClassLoader> mServiceClassloaderMap;

    /**
     * Constructor for Deployment.
     *
     * @param aRoot root element of the deployment document
     * @throws WSDDException
     */
    public AeBprDeployment(Element aRoot) throws WSDDException {
        super(aRoot);
        Element[] elements = getChildElements(aRoot, "typeMapping"); //$NON-NLS-1$
        for (Element element : elements) {
            WSDDTypeMapping mapping = new WSDDTypeMapping(element);
            deployTypeMapping(mapping);
        }
    }

    /**
     * Returns the service to classloader map
     */
    protected synchronized Map<Object, ClassLoader> getServiceClassloaderMap() {
        if (mServiceClassloaderMap == null)
            mServiceClassloaderMap = new HashMap<>();
        return mServiceClassloaderMap;
    }

    /**
     * Returns <code>true</code> if and only if the given service is a service
     * deployed for a BPEL process.
     *
     * @param aService
     */
    protected static boolean isBpelService(WSDDService aService) {
        QName providerQName = aService.getProviderQName();
        return BPEL_RPC_PROVIDER_QNAME.equals(providerQName)
                || BPEL_RPC_LIT_PROVIDER_QNAME.equals(providerQName)
                || BPEL_MSG_PROVIDER_QNAME.equals(providerQName);
    }

    //////////////////////////////////////////////////////////////////////////
    // WSDDDeployment methods which are taken over for class loader mapping.
    //////////////////////////////////////////////////////////////////////////

    /**
     * @see org.apache.axis.deployment.wsdd.WSDDDeployment#deployService(org.apache.axis.deployment.wsdd.WSDDService)
     */
    public void deployService(WSDDService aService) {
        aService.deployToRegistry(this);

        // If the service is for a BPEL process, then don't save the current
        // classloader, because saving and restoring the classloader for an
        // incoming message screws up EJB lookups on WebLogic 9.0. See defect
        // 1076, "Custom invokes that are invoking EJB's are not working on
        // WebLogic."
        if (!isBpelService(aService)) {
            getServiceClassloaderMap().put(aService.getQName(), Thread.currentThread().getContextClassLoader());
        }
    }

    /**
     * @see org.apache.axis.deployment.wsdd.WSDDDeployment#registerNamespaceForService(java.lang.String, org.apache.axis.deployment.wsdd.WSDDService)
     */
    public void registerNamespaceForService(String aNamespaceURI, WSDDService aService) {
        super.registerNamespaceForService(aNamespaceURI, aService);

        // If the service is for a BPEL process, then don't save the current
        // classloader, because saving and restoring the classloader for an
        // incoming message screws up EJB lookups on WebLogic 9.0. See defect
        // 1076, "Custom invokes that are invoking EJB's are not working on
        // WebLogic."
        if (!isBpelService(aService)) {
            getServiceClassloaderMap().put(aNamespaceURI, Thread.currentThread().getContextClassLoader());
        }
    }

    /**
     * @see org.apache.axis.deployment.wsdd.WSDDDeployment#deployToRegistry(org.apache.axis.deployment.wsdd.WSDDDeployment)
     */
    public void deployToRegistry(WSDDDeployment aDeploymentTarget) throws ConfigurationException {
        super.deployToRegistry(aDeploymentTarget);
        if (aDeploymentTarget instanceof AeBprDeployment) {
            ((AeBprDeployment) aDeploymentTarget).getServiceClassloaderMap().putAll(getServiceClassloaderMap());
        }
    }

    /**
     * Returns the classloader that loaded the given service.
     */
    public ClassLoader getClassLoader(QName aServiceName) {
        return getServiceClassloaderMap().get(aServiceName);
    }

    /**
     * Returns the classloader that loaded the given namespace.
     */
    public ClassLoader getClassLoader(String aNamespace) {
        return getServiceClassloaderMap().get(aNamespace);
    }

    /**
     * @see org.apache.axis.deployment.wsdd.WSDDDeployment#removeNamespaceMapping(java.lang.String)
     */
    public void removeNamespaceMapping(String aServiceQName) {
        getServiceClassloaderMap().remove(aServiceQName);
        super.removeNamespaceMapping(aServiceQName);
    }

    /**
     * @see org.apache.axis.deployment.wsdd.WSDDDeployment#undeployService(javax.xml.namespace.QName)
     */
    public void undeployService(QName aServiceQName) {
        getServiceClassloaderMap().remove(aServiceQName);
        super.undeployService(aServiceQName);
    }

    /**
     * Overriden for classloader setup before service calls.
     *
     * @see org.apache.axis.EngineConfiguration#getDeployedServices()
     */
    public Iterator<ServiceDesc> getDeployedServices() throws ConfigurationException {
        List<ServiceDesc> serviceDescs = new ArrayList<>();
        WSDDService[] services = getServices();

        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            for (WSDDService service : services) {
                ClassLoader newClassLoader = getClassLoader(service.getQName());
                if (newClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(newClassLoader);
                } else {
                    Thread.currentThread().setContextClassLoader(origClassLoader);
                }

                try {
                    // we need this soap service object because the service desc
                    // impl contained in the WSDDService is a JavaServiceDesc (and
                    // it doesn't look like that ever changes???!!!) - the SOAPService
                    // (which can't be accessed directly through the WSDDService)
                    // has the correct (ae version) of the service desc impl
                    SOAPService desc = (SOAPService) service.makeNewInstance(this);
                    serviceDescs.add(desc.getServiceDescription());
                } catch (WSDDNonFatalException ex) {
                    // If it's non-fatal, just keep on going
                    log.log(Level.INFO, AeMessages.getString("AeBprDeployment.0"), ex); //$NON-NLS-1$
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }

        Comparator<ServiceDesc> sorter = new Comparator<ServiceDesc>() {
            public int compare(ServiceDesc o1, ServiceDesc o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        };
        Collections.sort(serviceDescs, sorter);
        return serviceDescs.iterator();
    }

    /**
     * Overriden for classloader setup before service calls.
     *
     * @see org.apache.axis.EngineConfiguration#getServiceByNamespaceURI(java.lang.String)
     */
    public SOAPService getServiceByNamespaceURI(String aNamespaceURI) throws ConfigurationException {
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader newClassLoader = getClassLoader(aNamespaceURI);
            if (newClassLoader != null) {
                Thread.currentThread().setContextClassLoader(newClassLoader);
            }

            return super.getServiceByNamespaceURI(aNamespaceURI);
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
    }

    /**
     * Overriden for classloader setup before service calls.
     *
     * @see org.apache.axis.EngineConfiguration#getService(javax.xml.namespace.QName)
     */
    public SOAPService getService(QName aServiceQName) throws ConfigurationException {
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader newClassLoader = getClassLoader(aServiceQName);

            // Fix for JBoss 4.0.4: Don't set a null context classloader here,
            // because a null context classloader may cause JBoss 4.0.4 to fail to
            // load org.apache.axis.deployment.wsdd.WSDDProvider leading to
            // mysterious "Exception - java.lang.ExceptionInInitializerError"
            // messages when Axis tries to generate WSDL or handle an inbound
            // message.
            if (newClassLoader != null) {
                Thread.currentThread().setContextClassLoader(newClassLoader);
            }

            return super.getService(aServiceQName);
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
    }
}
