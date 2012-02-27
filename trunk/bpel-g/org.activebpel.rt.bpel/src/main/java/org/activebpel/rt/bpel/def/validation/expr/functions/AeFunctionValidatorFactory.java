// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/functions/AeFunctionValidatorFactory.java,v 1.2 2008/02/17 21:37:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.expr.functions;

import org.activebpel.rt.expr.validation.functions.IAeFunctionValidator;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides lookup of validator given a function qname and BPEL namespace.
 */
public class AeFunctionValidatorFactory implements IAeFunctionValidatorFactory {
    private Map<QName, IAeFunctionValidator> mFunctionValidators = new HashMap<QName, IAeFunctionValidator>();

    public IAeFunctionValidator getValidator(QName aQName) {
        return getFunctionValidators().get(aQName);
    }

    public Map<QName, IAeFunctionValidator> getFunctionValidators() {
        return mFunctionValidators;
    }

    public void setFunctionValidators(Map<QName, IAeFunctionValidator> aFunctionValidators) {
        mFunctionValidators = aFunctionValidators;
    }
}
