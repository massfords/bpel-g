//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AeDelegatingHandlerFactory.java,v 1.2 2007/12/27 18:16:31 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * An abstract handler factory that delegates to other factories based on the protocol in the URN.
 */
public class AeDelegatingHandlerFactory {
    /**
     * map of protocol to factory
     */
    private Map mDelegates = new HashMap();

    public AeDelegatingHandlerFactory() {
    }

    /**
     * @return Returns the delegates.
     */
    public Map getDelegates() {
        return mDelegates;
    }

    public void setDelegates(Map aMap) {
        mDelegates = aMap;
    }

    /**
     * Gets the protocol from the invoker uri or the default protocol if the uri is null or empty
     *
     * @param aURI
     */
    protected String getProtocol(String aURI) {
        if (AeUtil.isNullOrEmpty(aURI)) {
            return "default";
        }
        return AeInvokeHandlerUri.getProtocolString(aURI);
    }

    /**
     * Gets the factory to delegate the requests to.
     *
     * @param aURI
     */
    protected Object getDelegate(String aURI) throws AeBusinessProcessException {
        String protocol = getProtocol(aURI);
        Object delegate = getDelegates().get(protocol);
        if (delegate == null) {
            Object[] args = new Object[2];
            args[0] = getProtocol(aURI);
            args[1] = aURI;
            throw new AeBusinessProcessException(AeMessages.format("AeDelegatingHandlerFactory.MissingHandlerFactory", args)); //$NON-NLS-1$
        }
        return delegate;
    }
}
 