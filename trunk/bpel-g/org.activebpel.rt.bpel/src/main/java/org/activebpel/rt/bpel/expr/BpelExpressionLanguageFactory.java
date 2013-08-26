package org.activebpel.rt.bpel.expr;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;
import org.activebpel.rt.util.AeUtil;

public class BpelExpressionLanguageFactory implements IAeBpelExpressionLanguageFactory {

    private String mBpelDefaultLanguage;
    private Map<String, AeExpressionBundle> mBundles;

    @Override
    public IAeExpressionValidator createExpressionValidator(String aLanguageUri) throws AeException {
        return getBundle(aLanguageUri).getValidator();
    }

    @Override
    public IAeExpressionRunner createExpressionRunner(String aLanguageUri) throws AeException {
        return getBundle(aLanguageUri).getRunner();
    }

    @Override
    public IAeExpressionAnalyzer createExpressionAnalyzer(String aLanguageUri) throws AeException {
        return getBundle(aLanguageUri).getAnalyzer();
    }

    @Override
    public boolean supportsLanguage(String aLanguageUri) {
        String lang = resolveLanguageUri(aLanguageUri);
        return mBundles.containsKey(lang);
    }

    @Override
    public boolean isBpelDefaultLanguage(String aLanguageUri) {
        return mBpelDefaultLanguage.equals(aLanguageUri);
    }

    @Override
    public String getBpelDefaultLanguage() {
        return mBpelDefaultLanguage;
    }

    public void setBpelDefaultLanguage(String aDefaultLanguage) {
        mBpelDefaultLanguage = aDefaultLanguage;
    }

    protected AeExpressionBundle getBundle(String aLanguageUri) throws AeException {
        AeExpressionBundle bundle = mBundles.get(resolveLanguageUri(aLanguageUri));
        if (bundle == null)
            throw new AeException(AeMessages.format("AeAbstractBpelExpressionLanguageFactory.UNKNOWN_EXPR_LANGUAGE_ERROR", aLanguageUri)); //$NON-NLS-1$
        return bundle;
    }

    /**
     * This method resolves a language into a language URI.  The value that is passed in
     * to this method is whatever is in the 'expressionLanguage' attribute of the BPEL
     * 'process' element.  In most cases, the value of this attribute will be the URI of
     * the language itself, so nothing is done.  However, an implementation of this
     * factory could map short names to URIs, for example.  In addition, this method is
     * used to determine the default expression language if no value is specified in the
     * 'expressionLanguage' attribute.  By default (and unless overridden in the engine
     * config), the default language will be XPath 1.0 (as written in the BPEL spec).
     *
     * @param aLanguage
     */
    private String resolveLanguageUri(String aLanguage) {
        if (AeUtil.isNullOrEmpty(aLanguage))
            return getBpelDefaultLanguage();
        else
            return aLanguage;
    }

    public void setBundles(Map<String, AeExpressionBundle> aBundles) {
        mBundles = aBundles;
    }
}
