//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/receivers/AeAxisReceiveHandlerFactory.java,v 1.2 2008/02/17 21:29:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.receivers;

import java.util.Map;

import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeReceiveHandler;
import org.activebpel.rt.bpel.server.engine.AeInvokeHandlerUri;
import org.activebpel.rt.bpel.server.engine.IAeReceiveHandlerFactory;

/**
 * A receive handler factory for Axis extensions to the AeSOAPReceiveHandler.
 */
public class AeAxisReceiveHandlerFactory implements IAeReceiveHandlerFactory {

    private Map<String, IAeReceiveHandler> mReceiveHandlers;

    /**
     * Returns the appropriate receive handler for binding type.
     *
     * @see org.activebpel.rt.bpel.server.engine.IAeReceiveHandlerFactory#createReceiveHandler(java.lang.String)
     */
    public IAeReceiveHandler createReceiveHandler(String aProtocol)
            throws AeBusinessProcessException {
        String binding = AeInvokeHandlerUri.getInvokerString(aProtocol);
        IAeReceiveHandler rh = getReceiveHandlers().get(binding);
        if (rh == null) {
            throw new AeBusinessProcessException(AeMessages.format(
                    "AeAxisReceiveHandlerFactory.0", binding)); //$NON-NLS-1$
        }
        return rh;
    }

    public Map<String, IAeReceiveHandler> getReceiveHandlers() {
        return mReceiveHandlers;
    }

    public void setReceiveHandlers(
            Map<String, IAeReceiveHandler> aReceiveHandlers) {
        mReceiveHandlers = aReceiveHandlers;
    }
}
