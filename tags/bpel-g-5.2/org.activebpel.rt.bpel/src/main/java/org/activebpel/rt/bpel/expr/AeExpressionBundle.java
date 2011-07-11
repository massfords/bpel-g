package org.activebpel.rt.bpel.expr;

import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;

public class AeExpressionBundle {
    private String mURI;
    private IAeExpressionAnalyzer mAnalyzer;
    private IAeExpressionValidator mValidator;
    private IAeExpressionRunner mRunner;
    
    public String getURI() {
        return mURI;
    }
    public void setURI(String aURI) {
        mURI = aURI;
    }
    public IAeExpressionAnalyzer getAnalyzer() {
        return mAnalyzer;
    }
    public void setAnalyzer(IAeExpressionAnalyzer aAnalyzer) {
        mAnalyzer = aAnalyzer;
    }
    public IAeExpressionValidator getValidator() {
        return mValidator;
    }
    public void setValidator(IAeExpressionValidator aValidator) {
        mValidator = aValidator;
    }
    public IAeExpressionRunner getRunner() {
        return mRunner;
    }
    public void setRunner(IAeExpressionRunner aRunner) {
        mRunner = aRunner;
    }
}
