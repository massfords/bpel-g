//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/AeExpressionLanguageFactory.java,v 1.10 2008/02/27 17:54:22 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.expr.IAeBpelExpressionLanguageFactory;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;

import javax.inject.Singleton;
import java.util.Map;

/**
 * This implementation of the expression language factory uses the engine
 * configuration file to map expression languages to implementations of
 * validators and runners.
 */
@Singleton
public class AeExpressionLanguageFactory implements IAeExpressionLanguageFactory {

    /** The map of BPEL Namespace URI -> expression language factory. */
    private Map<String, IAeBpelExpressionLanguageFactory> mFactoryMap;

    /**
     * Gets the configured factory for the given bpel namespace URI.
     * 
     * @param aBpelNamespace
     * @throws AeException
     */
    protected IAeBpelExpressionLanguageFactory getFactory(String aBpelNamespace) throws AeException {
        IAeBpelExpressionLanguageFactory factory = mFactoryMap.get(aBpelNamespace);
        if (factory == null)
            throw new AeException(
                    AeMessages.format(
                            "AeExpressionLanguageFactory.ERROR_MISSING_EXPRESSION_LANGUAGE_FACTORY", aBpelNamespace)); //$NON-NLS-1$
        return factory;
    }

    /**
     * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#supportsLanguage(java.lang.String,
     *      java.lang.String)
     */
    public boolean supportsLanguage(String aBpelNamespace, String aLanguageUri) throws AeException {
        IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
        return factory.supportsLanguage(aLanguageUri);
    }

    /**
     * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#isBpelDefaultLanguage(java.lang.String,
     *      java.lang.String)
     */
    public boolean isBpelDefaultLanguage(String aBpelNamespace, String aLanguageUri)
            throws AeException {
        IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
        return factory.isBpelDefaultLanguage(aLanguageUri);
    }

    /**
     * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#getBpelDefaultLanguage(java.lang.String)
     */
    public String getBpelDefaultLanguage(String aBpelNamespace) throws AeException {
        IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
        return factory.getBpelDefaultLanguage();
    }

    /**
     * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#createExpressionValidator(java.lang.String,
     *      java.lang.String)
     */
    public IAeExpressionValidator createExpressionValidator(String aBpelNamespace,
            String aLanguageUri) throws AeException {
        IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
        return factory.createExpressionValidator(aLanguageUri);
    }

    /**
     * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#createExpressionAnalyzer(java.lang.String,
     *      java.lang.String)
     */
    public IAeExpressionAnalyzer createExpressionAnalyzer(String aBpelNamespace, String aLanguageUri)
            throws AeException {
        IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
        return factory.createExpressionAnalyzer(aLanguageUri);
    }

    /**
     * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#createExpressionRunner(java.lang.String,
     *      java.lang.String)
     */
    public IAeExpressionRunner createExpressionRunner(String aBpelNamespace, String aLanguageUri)
            throws AeException {
        IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
        return factory.createExpressionRunner(aLanguageUri);
    }

    /**
     * @return Returns the factoryMap.
     */
    public Map<String, IAeBpelExpressionLanguageFactory> getFactoryMap() {
        return mFactoryMap;
    }

    /**
     * @param aFactoryMap
     *            The factoryMap to set.
     */
    public void setFactoryMap(Map<String, IAeBpelExpressionLanguageFactory> aFactoryMap) {
        mFactoryMap = aFactoryMap;
    }

}
