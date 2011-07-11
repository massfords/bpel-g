package org.activebpel.rt.bpel.def.validation.expr.functions;

import org.activebpel.rt.expr.validation.functions.IAeFunctionValidator;

public class AeFunctionValidatorEntry {
    private String mNamespace;
    private String mLocalPart;
    private IAeFunctionValidator mValidator;
    
    public String getNamespace() {
        return mNamespace;
    }
    public void setNamespace(String aNamespace) {
        mNamespace = aNamespace;
    }
    public String getLocalPart() {
        return mLocalPart;
    }
    public void setLocalPart(String aLocalPart) {
        mLocalPart = aLocalPart;
    }
    public IAeFunctionValidator getValidator() {
        return mValidator;
    }
    public void setValidator(IAeFunctionValidator aValidator) {
        mValidator = aValidator;
    }
}
