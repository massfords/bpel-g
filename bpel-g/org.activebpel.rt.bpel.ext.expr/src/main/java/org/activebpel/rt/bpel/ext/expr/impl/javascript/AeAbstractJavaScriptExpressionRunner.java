//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/javascript/AeAbstractJavaScriptExpressionRunner.java,v 1.2 2006/09/18 20:08:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.impl.javascript;

import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.expr.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.Set;

/**
 * Base class for JavaScript 1.5 expression runners. This implementation is capable of executing expressions that
 * conform to the ECMA 262 specification (http://www.ecma-international.org/publications/standards/Ecma-262.htm).
 * This class uses Rhino to execute the JavaScript expression.
 */
public abstract class AeAbstractJavaScriptExpressionRunner extends AeAbstractExpressionRunner {
    /**
     * Default constructor.
     */
    public AeAbstractJavaScriptExpressionRunner() {
        super();
    }

    /**
     * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionRunner#createExpressionTypeConverter(org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext)
     */
    protected IAeExpressionTypeConverter createExpressionTypeConverter(IAeExpressionRunnerContext aContext) {
        return new AeJavaScriptExpressionTypeConverter();
    }

    /**
     * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionRunner#doExecuteExpression(java.lang.String, org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext)
     */
    protected Object doExecuteExpression(String aExpression, IAeExpressionRunnerContext aContext)
            throws AeBpelException {
        Context ctx = Context.enter();
        try {
            IAeExpressionTypeConverter typeConverter = createExpressionTypeConverter(aContext);
            IAeFunctionExecutionContext funcExecContext = new AeExprFunctionExecutionContext(aContext, typeConverter);

            Scriptable scope = ctx.initStandardObjects();
            // Get a list of all the function contexts installed (by namespace).
            for (String fcNS : aContext.getFunctionContext().getFunctionContextNamespaceList()) {
                Set prefixes = aContext.getNamespaceContext().resolveNamespaceToPrefixes(fcNS);

                // Now add a scriptable for each prefix mapped to the function context's namespace.
                for (Object prefixe : prefixes) {
                    String prefix = (String) prefixe;
                    Scriptable scriptable = createFunctionContainer(funcExecContext, scope, fcNS);
                    ScriptableObject.putProperty(scope, prefix, scriptable);
                }
            }
            return ctx.evaluateString(scope, aExpression, "<java>", 1, null); //$NON-NLS-1$
        } catch (AeExpressionException ee) {
            throw ee.getWrappedException();
        } catch (Throwable t) {
            throwSubLangExecutionFault(aExpression, t, aContext);
            return null; // Will never get here - the above call will always throw.
        } finally {
            Context.exit();
        }
    }

    /**
     * Creates the function container for the given namespace.  The function container is a JavaScript
     * Scriptable compatible object that routes calls to custom functions.
     *
     * @param aFuncExecContext
     * @param aScope
     * @param aFunctionNamespace
     */
    protected AeJavaScriptFunctionContainer createFunctionContainer(
            IAeFunctionExecutionContext aFuncExecContext, Scriptable aScope, String aFunctionNamespace) {
        return new AeJavaScriptFunctionContainer(aFunctionNamespace, aScope, aFuncExecContext);
    }
}
