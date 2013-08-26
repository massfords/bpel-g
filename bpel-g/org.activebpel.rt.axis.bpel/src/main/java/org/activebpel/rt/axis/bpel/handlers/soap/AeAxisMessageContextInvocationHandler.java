// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/soap/AeAxisMessageContextInvocationHandler.java,v 1.2 2006/10/04 22:12:53 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.axis.bpel.handlers.soap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.xml.rpc.handler.soap.SOAPMessageContext;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;

/**
 * An invocation handler that is used to proxy an Axis MessageContext.  This handler only kicks
 * in when the getMessage() method is called (in order to return a proxied Message object).
 */
public class AeAxisMessageContextInvocationHandler implements InvocationHandler {
    /**
     * The proxied msg ctx.
     */
    private MessageContext mProxiedMessageContext;

    /**
     * Constructs the invocation handler with the given message context.
     *
     * @param aProxiedMessageContext
     */
    public AeAxisMessageContextInvocationHandler(MessageContext aProxiedMessageContext) {
        setProxiedMessageContext(aProxiedMessageContext);
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object aProxy, Method aMethod, Object[] args) throws Throwable {
        if (aMethod.equals(getGetMessageMethod())) {
            return AeAxisObjectProxyFactory.getMessageProxy((Message) getProxiedMessageContext().getMessage());
        }
        return aMethod.invoke(getProxiedMessageContext(), args);
    }

    /**
     * Returns the getMessage() method reference.
     *
     * @throws NoSuchMethodException
     */
    private Method getGetMessageMethod() throws NoSuchMethodException {
        return SOAPMessageContext.class.getMethod("getMessage", (Class[]) null); //$NON-NLS-1$
    }

    /**
     * @return Returns the proxiedMessageContext.
     */
    protected MessageContext getProxiedMessageContext() {
        return mProxiedMessageContext;
    }

    /**
     * @param aProxiedMessageContext The proxiedMessageContext to set.
     */
    protected void setProxiedMessageContext(MessageContext aProxiedMessageContext) {
        mProxiedMessageContext = aProxiedMessageContext;
    }
}
