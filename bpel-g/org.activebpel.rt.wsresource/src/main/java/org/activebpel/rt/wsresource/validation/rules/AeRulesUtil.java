// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/rules/AeRulesUtil.java,v 1.3 2008/02/29 17:38:06 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation.rules;

import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.wsresource.validation.IAeWSResourceValidationPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for working with validation rules.
 */
public class AeRulesUtil {
    /**
     * Mapping of string serverity codes to Integer objects
     */
    private static final Map<String, Integer> sSeverityMap = new HashMap<>();

    /** load the severity mapping */
    static {
        sSeverityMap.put("ERROR", IAeWSResourceValidationPreferences.SEVERITY_ERROR); //$NON-NLS-1$
        sSeverityMap.put("WARNING", IAeWSResourceValidationPreferences.SEVERITY_WARNING); //$NON-NLS-1$
        sSeverityMap.put("INFO", IAeWSResourceValidationPreferences.SEVERITY_INFO); //$NON-NLS-1$
        sSeverityMap.put("SKIP", IAeWSResourceValidationPreferences.SEVERITY_SKIP); //$NON-NLS-1$
    }

    /**
     * Convert the error severity string to an integer code (i.e. "ERROR" = 0).
     * If aSeverity is null then an <code>Integer</code> with the value of -1 is returned.
     *
     * @param aSeverity
     */
    public static Integer convertSeverity(String aSeverity) {
        Integer code = -1;

        if (sSeverityMap.containsKey(aSeverity)) {
            code = sSeverityMap.get(aSeverity);
        }

        return code;
    }

    /**
     * Find first occurance of a non literal argument.  Returns -1 of they
     * are all literal, otherwise returns the index of the first non literal argument.
     *
     * @param aFunction
     */
    public static int findFirstNonLiteralArgument(AeScriptFuncDef aFunction) {
        int index = -1;
        int foundIndex = -1;

        for (Object o : aFunction.getArgs()) {
            index++;
            if (AeScriptFuncDef.__EXPRESSION__ == o) {
                foundIndex = index;
                break;
            }
        }
        return foundIndex;
    }
}
