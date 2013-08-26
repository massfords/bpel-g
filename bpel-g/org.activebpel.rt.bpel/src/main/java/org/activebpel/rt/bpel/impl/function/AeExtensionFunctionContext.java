//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeExtensionFunctionContext.java,v 1.11 2008/02/02 19:17:41 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import java.util.Map;

import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;

/**
 * A <code>FunctionContext</code> implementation that handles returning BPEL extension functions. Currently
 * supported function are:
 * <p/>
 * <pre>
 * getProcessId()
 * getProcessName()
 * getProcessInitiator()
 * getMyRoleProperty()
 * getAttachmentCount()
 * copyAttachment()
 * copyAllAttachments()
 * getAttachmentType()
 * getAttachmentProperty()
 * removeAttachment()
 * removeAllAttachments()
 * replaceAttachment()
 * getAttachmentSize()
 * createAttachment()
 * resolveURN()
 * base64Encode()
 * getPlanExtensions()
 * </pre>
 */
public class AeExtensionFunctionContext extends AeAbstractFunctionContext {
    Map<String, IAeFunction> mFunctions;

    /**
     * @see org.activebpel.rt.bpel.function.IAeFunctionContext#getFunction(java.lang.String)
     *      TODO: (JB) refractor into a function factory or chain of command pattern to return the objects
     */
    public IAeFunction getFunction(String aLocalName) throws AeUnresolvableException {
        IAeFunction func = getFunctions().get(aLocalName);
        if (func == null)
            throw new AeUnresolvableException(formatFunctionNotFoundErrorMsg(aLocalName));
        return func;
    }

    public void setFunctions(Map<String, IAeFunction> aFuncs) {
        mFunctions = aFuncs;
    }

    public Map<String, IAeFunction> getFunctions() {
        return mFunctions;
    }
}
