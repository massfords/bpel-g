//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeVariablesValidator.java,v 1.4 2007/09/28 21:45:39 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope;

import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.AeContainerValidatorUtil;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;

import java.util.List;

/**
 * model provides validation for variables def
 */
public class AeVariablesValidator extends AeBaseValidator {
    /**
     * ctor
     *
     * @param aDef
     */
    public AeVariablesValidator(AeVariablesDef aDef) {
        super(aDef);
    }

    /**
     * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
     */
    public void validate() {
        super.validate();
        AeVariablesDef def = (AeVariablesDef) getDefinition();
        AeContainerValidatorUtil.checkForDupes(getReporter(), "variable", def);
        // TODO (MF) check for variable type overrides for BPWS (old code produced warnings for overridden variables of diff type)
    }

    /**
     * Gets the given variable model by its name or null if not defined here
     *
     * @param aName
     * @param aMode
     */
    public AeVariableValidator getVariableValidator(String aName, int aMode) {
        List vars = getChildren(AeVariableValidator.class);
        for (Object var : vars) {
            AeVariableValidator variableModel = (AeVariableValidator) var;
            if (variableModel.getName().equals(aName)) {
                variableModel.addVariableUsage(aMode);
                return variableModel;
            }
        }
        return null;
    }
}
 