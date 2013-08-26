//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeWSDLExtensionLoader.java,v 1.1 2007/08/13 17:47:32 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.wsdl.def.policy.*;

import javax.wsdl.*;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.xml.namespace.QName;

/**
 * Populates an extension registry with IO objects for reading and writing
 * WSDL extensions to and from a WSDL document.
 */
public class AeWSDLExtensionLoader {
    /**
     * No reason to instantiate this class
     */
    private AeWSDLExtensionLoader() {
    }

    /**
     * Loads the registry with readers/writers for BPWS, WS-BPEL, and WS-Policy
     *
     * @param aRegistry
     */
    public static void loadRegistry(ExtensionRegistry aRegistry) {
        loadBPELExtensions(aRegistry, AeBPELWSDLExtensionIOFactory.getFactory(IAeBPELExtendedWSDLConst.BPWS_NAMESPACE_URI));
        loadBPELExtensions(aRegistry, AeBPELWSDLExtensionIOFactory.getFactory(IAeBPELExtendedWSDLConst.WSBPEL_2_0_NAMESPACE_URI));
        loadWSPolicyExtensions(aRegistry);
    }

    /**
     * Loads the BPEL extensions
     *
     * @param aRegistry
     */
    private static void loadBPELExtensions(ExtensionRegistry aRegistry, IAeBPELWSDLExtensionIOFactory factory) {
        // partner link reading and writing
        loadExtension(aRegistry,
                Definition.class,
                factory.getPartnerLinkTypeQName(),
                AePartnerLinkTypeImpl.class,
                factory.getPartnerLinkTypeDeserializer(),
                factory.getPartnerLinkTypeSerializer());

        // property reading and writing
        loadExtension(aRegistry,
                Definition.class,
                factory.getPropertyQName(),
                AePropertyImpl.class,
                factory.getPropertyDeserializer(),
                factory.getPropertySerializer());

        // property alias reading and writing
        loadExtension(aRegistry,
                Definition.class,
                factory.getPropertyAliasQName(),
                AePropertyAliasImpl.class,
                factory.getPropertyAliasDeserializer(),
                factory.getPropertyAliasSerializer());
    }

    /**
     * Loads the policy extensions
     *
     * @param aRegistry
     */
    private static void loadWSPolicyExtensions(ExtensionRegistry aRegistry) {
        Class[] clazz = new Class[]{
                Definition.class,
                Service.class,
                Port.class,
                Binding.class,
                BindingOperation.class,
                BindingInput.class,
                Operation.class,
                Message.class
        };

        QName policyName = new QName(IAeConstants.WSP_NAMESPACE_URI, IAePolicy.POLICY_ELEMENT);
        AePolicyIO policyIO = new AePolicyIO();

        QName policyRefName = new QName(IAeConstants.WSP_NAMESPACE_URI, IAePolicyReference.POLICY_REFERENCE_ELEMENT);
        AePolicyRefIO policyRefIO = new AePolicyRefIO();

        for (Class c : clazz) {
            loadExtension(aRegistry, c, policyName, AePolicyImpl.class, policyIO, policyIO);
            loadExtension(aRegistry, c, policyRefName, AePolicyRefImpl.class, policyRefIO, policyRefIO);
        }
    }

    /**
     * Loads the extension into the registry
     *
     * @param aRegistry
     * @param aSubject
     * @param policyName
     * @param clazz
     * @param aDeserializer
     * @param aSerializer
     */
    private static void loadExtension(ExtensionRegistry aRegistry, Class aSubject, QName policyName, Class clazz, ExtensionDeserializer aDeserializer, ExtensionSerializer aSerializer) {
        aRegistry.mapExtensionTypes(aSubject, policyName, clazz);
        aRegistry.registerDeserializer(aSubject, policyName, aDeserializer);
        aRegistry.registerSerializer(aSubject, policyName, aSerializer);
    }
}
 