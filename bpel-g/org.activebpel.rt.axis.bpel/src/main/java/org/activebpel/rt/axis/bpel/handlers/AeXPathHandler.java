//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeXPathHandler.java,v 1.3 2006/09/07 15:21:07 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.IAePolicyConstants;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Base Class with utility methods for mapping SOAP Headers into process variable properties
 */
public abstract class AeXPathHandler extends BasicHandler implements IAeConstants, IAePolicyConstants {
    /**
     *
     */
    private static final long serialVersionUID = -381801208752177487L;

    /**
     * Constructor
     */
    public void init() {
        setOptionsLockable(true);
        super.init();
    }

    /**
     * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
     */
    abstract public void invoke(MessageContext aMsgContext) throws AxisFault;

    /**
     * Utility method to create all the XPaths defined in the handler
     * options
     *
     * @return Map Map of defined XPath queries
     */
    @SuppressWarnings("unchecked")
    protected Map<String, XPath> getXpaths(MessageContext aMsgContext) throws JaxenException {
        // Figure out if map is coming from options (server) or context (client)
        if (XPATH_QUERY_SOURCE_CONTEXT.equals(getStringValue(XPATH_QUERY_SOURCE, aMsgContext))) {
            Map<String, String> xpathParams = (Map<String, String>) aMsgContext.getProperty(XPATH_QUERY_PARAMS);
            Map<String, XPath> xpaths = createXpathMap(xpathParams);
            return xpaths;
        } else {
            // see if we already have the list stashed in
            // the handler options table

            Map<String, XPath> xpaths = (Map<String, XPath>) this.getOption(XPATH_MAP);
            if (xpaths == null) {
                xpaths = createXpathMap(getOptions());
                // Add to options table
                synchronized (AeXPathHandler.class) {
                    this.options.put(XPATH_MAP, xpaths);
                }
            }
            return xpaths;
        }
    }

    /**
     * Creates a map of XPath queries
     *
     * @param aXpathParams
     * @throws JaxenException
     */
    private Map<String, XPath> createXpathMap(Map<String, String> aXpathParams) throws JaxenException {
        // Create XPaths
        Map<String, XPath> xpaths = new HashMap<>();

        // get namespace declarations
        NamespaceContext nsc = getNamespaceContext(aXpathParams);

        for (String name : aXpathParams.keySet()) {
            if (name.startsWith(XPATH_PREFIX)) {
                String query = aXpathParams.get(name);
                XPath xpath = new DOMXPath(query);
                xpath.setNamespaceContext(nsc);
                xpaths.put(name.substring(XPATH_PREFIX.length()), xpath);
            }
        }
        return xpaths;
    }

    /**
     * Utility method to add all the namespaces in the handler
     * options to the XPath context
     *
     * @return NamespaceContext contains specified namespaces
     */
    protected NamespaceContext getNamespaceContext(Map<String, String> aXPathParams) throws JaxenException {

        XPath xpath = new DOMXPath("/"); //$NON-NLS-1$

        // get namespace declarations from the options set
        for (String key : aXPathParams.keySet()) {
            if (key.startsWith(XMLNS_PREFIX)) {
                String prefix = key.substring(XMLNS_PREFIX.length());
                xpath.addNamespace(prefix, aXPathParams.get(key));
            }
        }
        return xpath.getNamespaceContext();
    }

    /**
     * Helper method to get a property from either options or message context
     * Properties set in the wsdd will always take precedence
     *
     * @param key
     * @param aMessageContext
     */
    protected String getStringValue(String key, MessageContext aMessageContext) {
        // Try from deployment options
        String value = (String) getOption(key);
        if (value == null) {
            // see if it's set on MessageContext
            value = (String) aMessageContext.getProperty(key);
        }
        return value;
    }
}