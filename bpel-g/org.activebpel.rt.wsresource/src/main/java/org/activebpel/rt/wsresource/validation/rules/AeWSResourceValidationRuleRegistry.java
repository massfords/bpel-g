// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/rules/AeWSResourceValidationRuleRegistry.java,v 1.5 2008/01/28 18:35:21 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation.rules;

import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.wsdl.def.castor.AeSchemaParserUtil;
import org.activebpel.rt.wsresource.IAeWSResourceConstants;
import org.activebpel.rt.wsresource.validation.AeWSResourceValidationRule;
import org.activebpel.rt.wsresource.validation.IAeWSResourceValidationRule;
import org.activebpel.rt.wsresource.validation.IAeWSResourceValidationRuleRegistry;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.AeXMLParserErrorHandler;
import org.exolab.castor.xml.schema.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * Default rules registry.  Loads rules from the rules.xml config
 * file.
 */
public class AeWSResourceValidationRuleRegistry implements IAeWSResourceValidationRuleRegistry {
    private static final Schema sRulesFileSchema;
    private static final Map<String, String> sPrefixMap = new HashMap<>();

    static {
        sPrefixMap.put("aerule", IAeWSResourceConstants.RULES_NAMESPACE); //$NON-NLS-1$

        sRulesFileSchema = AeSchemaParserUtil.loadSchema("aeRules.xsd", AeWSResourceValidationRuleRegistry.class); //$NON-NLS-1$

    }

    /**
     * Map of resource namespace -> List of rules.
     */
    private Map<String, List<IAeWSResourceValidationRule>> mRules = new HashMap<>();

    /**
     * C'tor.
     */
    public AeWSResourceValidationRuleRegistry() {
    }

    /**
     * Loads the rules found in the rules config file.
     *
     * @param aRulesFilesURL
     */
    public void loadRules(URL aRulesFilesURL) {
        try {
            AeXMLParserBase parser = new AeXMLParserBase(true, true);
            parser.setErrorHandler(new AeXMLParserErrorHandler());
            InputSource rulesIS = new InputSource(new InputStreamReader(aRulesFilesURL.openStream()));
            rulesIS.setSystemId(aRulesFilesURL.toString());
            Document rulesDoc = parser.loadDocument(rulesIS, Collections.singleton(sRulesFileSchema).iterator());

            // Fail if the rules file is not schema valid.
            if (parser.getErrorHandler().hasParseWarnings()) {
                throw parser.getErrorHandler().getParseException();
            }

            @SuppressWarnings("unchecked")
            List<Element> ruleNodes = AeXPathUtil.selectNodes(rulesDoc, "aerule:rules/aerule:rule", sPrefixMap); //$NON-NLS-1$
            List<IAeWSResourceValidationRule> rules = new ArrayList<>();
            String targetNS = AeXPathUtil.selectSingleNode(rulesDoc, "aerule:rules/@targetNamespace", //$NON-NLS-1$
                    sPrefixMap).getNodeValue();
            for (Element ruleElem : ruleNodes) {
                String code = AeXPathUtil.selectText(ruleElem, "aerule:code", sPrefixMap); //$NON-NLS-1$
                Integer defaultSeverity = AeRulesUtil.convertSeverity(AeXPathUtil.selectText(ruleElem,
                        "aerule:defaultSeverity", sPrefixMap)); //$NON-NLS-1$
                String description = AeXPathUtil.selectText(ruleElem, "aerule:description", sPrefixMap); //$NON-NLS-1$
                String validator = AeXPathUtil.selectText(ruleElem, "aerule:validator", sPrefixMap); //$NON-NLS-1$
                QName id = new QName(targetNS, code);
                rules.add(new AeWSResourceValidationRule(id, defaultSeverity, description, validator));
            }
            getRules().put(targetNS, rules);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidationRuleRegistry#getRules(java.lang.String)
     */
    public List<IAeWSResourceValidationRule> getRules(String aWSResourceType) {
        return getRules().get(aWSResourceType);
    }

    /**
     * @return Returns the rules.
     */
    protected Map<String, List<IAeWSResourceValidationRule>> getRules() {
        return mRules;
    }

    /**
     * @param aRules the rules to set
     */
    protected void setRules(Map<String, List<IAeWSResourceValidationRule>> aRules) {
        mRules = aRules;
    }

}
