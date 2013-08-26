//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr.bsf/src/org/activebpel/rt/bpel/ext/expr/bsf/impl/AeBSFGenericExtensionFunctionBean.java,v 1.2.16.1 2008/04/21 16:12:12 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.bsf.impl;

import java.util.Arrays;

import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.util.AeXmlUtil;

/**
 * This is a class that allows BSF compatible scripting languages access to the installed extension
 * functions.  Scripting languages will have access to a global variable called "bpel".  This
 * class implements the functionality for that global variable.  Note that, for convenience,
 * we also provide separate access to the bpel specific extension functions (in other words
 * the bpws:* functions).  This class provides several versions of a "call" method that can
 * be used to execute any extension function (including custom extension functions).  The
 * symantics are simple.<br/>
 * <br/>
 * <em>For example:</em><br/>
 * <pre>
 * bpel.call('bpws:getVariableData', 'requestVar', '/requestVar/node1')
 * bpel.call('prfx:myFunction', 'arg1', 'arg2')
 * </pre>
 * <br/>
 * This mechanism currently supports any function with six or fewer arguments.
 */
public class AeBSFGenericExtensionFunctionBean extends AeBSFAbstractExtensionFunctionBean {
    /**
     * Constructs a BSF bpel context given the expression runtime context.
     *
     * @param aFunctionExecutionContext
     */
    protected AeBSFGenericExtensionFunctionBean(IAeFunctionExecutionContext aFunctionExecutionContext) {
        super(aFunctionExecutionContext);
    }

    /**
     * @see org.activebpel.rt.bpel.ext.expr.bsf.impl.AeBSFAbstractExtensionFunctionBean#getNamespace()
     */
    protected String getNamespace() {
        // Return null - this bean handles multiple namespaces/prefixes.
        return null;
    }

    /**
     * Calls the given 0 argument function spec.
     *
     * @param aFunctionSpec
     * @throws Exception
     */
    public Object call(String aFunctionSpec) throws Exception {
        return callWithArgs(aFunctionSpec, new Object[]{});
    }

    /**
     * Calls the given 1 argument function spec.
     *
     * @param aFunctionSpec
     * @param aArg1
     * @throws Exception
     */
    public Object call(String aFunctionSpec, Object aArg1) throws Exception {
        return callWithArgs(aFunctionSpec, new Object[]{aArg1});
    }

    /**
     * Calls the given 2 argument function spec.
     *
     * @param aFunctionSpec
     * @param aArg1
     * @param aArg2
     * @throws Exception
     */
    public Object call(String aFunctionSpec, Object aArg1, Object aArg2) throws Exception {
        return callWithArgs(aFunctionSpec, new Object[]{aArg1, aArg2});
    }

    /**
     * Calls the given 3 argument function spec.
     *
     * @param aFunctionSpec
     * @param aArg1
     * @param aArg2
     * @param aArg3
     * @throws Exception
     */
    public Object call(String aFunctionSpec, Object aArg1, Object aArg2, Object aArg3) throws Exception {
        return callWithArgs(aFunctionSpec, new Object[]{aArg1, aArg2, aArg3});
    }

    /**
     * Calls the given 4 argument function spec.
     *
     * @param aFunctionSpec
     * @param aArg1
     * @param aArg2
     * @param aArg3
     * @param aArg4
     * @throws Exception
     */
    public Object call(String aFunctionSpec, Object aArg1, Object aArg2, Object aArg3, Object aArg4) throws Exception {
        return callWithArgs(aFunctionSpec, new Object[]{aArg1, aArg2, aArg3, aArg4});
    }

    /**
     * Calls the given 5 argument function spec.
     *
     * @param aFunctionSpec
     * @param aArg1
     * @param aArg2
     * @param aArg3
     * @param aArg4
     * @param aArg5
     * @throws Exception
     */
    public Object call(String aFunctionSpec, Object aArg1, Object aArg2, Object aArg3, Object aArg4,
                       Object aArg5) throws Exception {
        return callWithArgs(aFunctionSpec, new Object[]{aArg1, aArg2, aArg3, aArg4, aArg5});
    }

    /**
     * Calls the given 6 argument function spec.
     *
     * @param aFunctionSpec
     * @param aArg1
     * @param aArg2
     * @param aArg3
     * @param aArg4
     * @param aArg5
     * @param aArg6
     * @throws Exception
     */
    public Object call(String aFunctionSpec, Object aArg1, Object aArg2, Object aArg3, Object aArg4,
                       Object aArg5, Object aArg6) throws Exception {
        return callWithArgs(aFunctionSpec, new Object[]{aArg1, aArg2, aArg3, aArg4, aArg5, aArg6});
    }

    /**
     * Does the actual work of looking up the function and calling it.
     *
     * @param aFunctionSpec
     * @throws Exception
     */
    protected Object callWithArgs(String aFunctionSpec, Object[] aArgs) throws Exception {
        String prefix = AeXmlUtil.extractPrefix(aFunctionSpec);
        String funcName = AeXmlUtil.extractLocalPart(aFunctionSpec);
        String ns = getFunctionExecutionContext().getNamespaceContext().resolvePrefixToNamespace(prefix);

        // Create the argument list.
        return callFunction(ns, funcName, Arrays.asList(aArgs));
    }
}
