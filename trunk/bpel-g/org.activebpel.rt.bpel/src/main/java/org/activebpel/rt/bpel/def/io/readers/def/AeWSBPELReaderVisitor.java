// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/AeWSBPELReaderVisitor.java,v 1.18 2007/11/15 22:31:11 EWittmann Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io.readers.def;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AeExtensionDef;
import org.activebpel.rt.bpel.def.AeExtensionsDef;
import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.IAeConditionParentDef;
import org.activebpel.rt.bpel.def.IAeForUntilParentDef;
import org.activebpel.rt.bpel.def.IAeFromParentDef;
import org.activebpel.rt.bpel.def.IAeFromPartsParentDef;
import org.activebpel.rt.bpel.def.IAeTerminationHandlerParentDef;
import org.activebpel.rt.bpel.def.IAeToPartsParentDef;
import org.activebpel.rt.bpel.def.activity.AeAbstractExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;
import org.activebpel.rt.bpel.def.activity.support.IAeQueryParentDef;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.w3c.dom.Element;

/**
 * Implements a WS-BPEL 2.0 version of the def reader visitor.
 */
public class AeWSBPELReaderVisitor extends AeBpelReaderVisitor {
    /**
     * Constructor.
     */
    public AeWSBPELReaderVisitor(AeBaseDef aParentDef, Element aElement) {
        super(aParentDef, aElement);
    }

    /**
     * Reads the namespace qualified attribute for message exchange.
     */
    protected String getMessageExchangeValue() {
        return getAttribute(TAG_MESSAGE_EXCHANGE);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#readAttributes(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
     */
    protected void readAttributes(AeFromDef aFromDef) {
        super.readAttributes(aFromDef);

        aFromDef.setExpressionLanguage(getAttribute(TAG_EXPRESSION_LANGUAGE));
        aFromDef.setExpression(AeXmlUtil.getText(getCurrentElement()));
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#readAttributes(org.activebpel.rt.bpel.def.activity.support.AeToDef)
     */
    protected void readAttributes(AeToDef aToDef) {
        super.readAttributes(aToDef);

        aToDef.setExpressionLanguage(getAttribute(TAG_EXPRESSION_LANGUAGE));
        aToDef.setExpression(AeXmlUtil.getText(getCurrentElement()));
    }

    /**
     * Overides to use the name space to determine the abstract process.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
     */
    public void visit(AeProcessDef def) {
        super.visit(def);

        def.setExitOnStandardFault(getAttributeBoolean(TAG_EXIT_ON_STANDARD_FAULT));
        if (AeUtil.notNullOrEmpty(getAttributeNS(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI, TAG_ABSTRACT_PROCESS_PROFILE))) {
            def.setAbstractProcessProfile(getAttributeNS(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI, TAG_ABSTRACT_PROCESS_PROFILE));
        }
        def.setCreateTargetXPath(getAttributeBooleanNS(
                IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_QUERY_HANDLING, TAG_CREATE_TARGET_XPATH));
        def.setDisableSelectionFailure(getAttributeBooleanNS(
                IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_QUERY_HANDLING, TAG_DISABLE_SELECTION_FAILURE));
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
     */
    public void visit(AeImportDef def) {
        readAttributes(def);

        def.setNamespace(getAttribute(TAG_NAMESPACE));
        def.setLocation(getAttribute(TAG_LOCATION));
        def.setImportType(getAttribute(TAG_IMPORT_TYPE));

        ((AeProcessDef) getParentDef()).addImportDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
     */
    public void visit(AeDocumentationDef aDef) {
        readAttributes(aDef);

        aDef.setSource(getAttribute(ATTR_DOCUMENTATION_SOURCE));
        aDef.setLanguage(getAttributeNS(XMLConstants.XML_NS_URI, ATTR_DOCUMENTATION_LANG));

        aDef.setValue(AeXmlUtil.getText(getCurrentElement()));
        getParentDef().addDocumentationDef(aDef);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
     */
    public void visit(AePartnerLinkDef def) {
        super.visit(def);

        // will set the Boolean or null if not present in xml
        def.setInitializePartnerRole(getAttributeBoolOptional((TAG_INITIALIZE_PARTNER_ROLE)));
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
     */
    public void visit(AeAssignCopyDef def) {
        super.visit(def);

        def.setKeepSrcElementName(getAttributeBoolean(TAG_KEEP_SRC_ELEMENT_NAME));
        def.setIgnoreMissingFromData(getAttributeBoolean(TAG_IGNORE_MISSING_FROM_DATA));
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
     */
    public void visit(AeActivityAssignDef def) {
        super.visit(def);

        def.setValidate(getAttributeBoolean(TAG_VALIDATE));
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
     */
    public void visit(AeExtensibleAssignDef def) {
        readAttributes(def);

        // TODO (MF) if we don't understand the extensible assign then we should ignore it

        ((AeActivityAssignDef) getParentDef()).addExtensibleAssignDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
     */
    public void visit(AeExtensionsDef def) {
        readAttributes(def);

        ((AeProcessDef) getParentDef()).setExtensionsDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
     */
    public void visit(AeExtensionDef def) {
        readAttributes(def);

        def.setMustUnderstand(getAttributeBoolean(TAG_MUST_UNDERSTAND));
        def.setNamespace(getAttribute(TAG_NAMESPACE));

        ((AeExtensionsDef) getParentDef()).addExtensionDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
     */
    public void visit(AeFromPartsDef def) {
        readAttributes(def);

        ((IAeFromPartsParentDef) getParentDef()).setFromPartsDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
     */
    public void visit(AeToPartsDef def) {
        readAttributes(def);

        ((IAeToPartsParentDef) getParentDef()).setToPartsDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
     */
    public void visit(AeFromPartDef def) {
        readAttributes(def);

        def.setPart(getAttribute(TAG_PART));
        def.setToVariable(getAttribute(TAG_TO_VARIABLE));

        ((AeFromPartsDef) getParentDef()).addFromPartDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
     */
    public void visit(AeToPartDef def) {
        readAttributes(def);

        def.setPart(getAttribute(TAG_PART));
        def.setFromVariable(getAttribute(TAG_FROM_VARIABLE));

        ((AeToPartsDef) getParentDef()).addToPartDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
     */
    public void visit(AeSourcesDef def) {
        readAttributes(def);

        ((AeActivityDef) getParentDef()).setSourcesDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
     */
    public void visit(AeSourceDef def) {
        super.visit(def);

        ((AeSourcesDef) getParentDef()).addSourceDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
     */
    public void visit(AeTargetsDef def) {
        readAttributes(def);

        ((AeActivityDef) getParentDef()).setTargetsDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
     */
    public void visit(AeTargetDef def) {
        super.visit(def);

        ((AeTargetsDef) getParentDef()).addTargetDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
     */
    public void visit(AeJoinConditionDef def) {
        readAttributes(def);

        readExpressionDef(def);
        ((AeTargetsDef) getParentDef()).setJoinConditionDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
     */
    public void visit(AeTransitionConditionDef def) {
        readAttributes(def);

        readExpressionDef(def);
        ((AeSourceDef) getParentDef()).setTransitionConditionDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
     */
    public void visit(AeForDef def) {
        readAttributes(def);

        readExpressionDef(def);
        ((IAeForUntilParentDef) getParentDef()).setForDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
     */
    public void visit(AeUntilDef def) {
        readAttributes(def);

        readExpressionDef(def);
        ((IAeForUntilParentDef) getParentDef()).setUntilDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
     */
    public void visit(AeActivityIfDef def) {
        readAttributes(def);
        addActivityToParent(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
     */
    public void visit(AeConditionDef def) {
        readAttributes(def);

        readExpressionDef(def);
        ((IAeConditionParentDef) getParentDef()).setConditionDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
     */
    public void visit(AeElseDef def) {
        readAttributes(def);

        ((AeActivityIfDef) getParentDef()).setElseDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
     */
    public void visit(AeElseIfDef def) {
        readAttributes(def);

        ((AeActivityIfDef) getParentDef()).addElseIfDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
     */
    public void visit(AeRepeatEveryDef def) {
        readAttributes(def);
        readExpressionDef(def);
        ((AeOnAlarmDef) getParentDef()).setRepeatEveryDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
     */
    public void visit(AeActivityScopeDef def) {
        super.visit(def);

        def.setIsolated(getAttributeBoolean(TAG_ISOLATED));
        if (AeUtil.notNullOrEmpty(getAttribute(TAG_EXIT_ON_STANDARD_FAULT))) {
            def.setExitOnStandardFault(getAttributeBoolean(TAG_EXIT_ON_STANDARD_FAULT));
        }
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
     */
    public void visit(AeCatchDef def) {
        super.visit(def);

        def.setFaultMessageType(getAttributeQName(TAG_FAULT_MESSAGE_TYPE));
        def.setFaultElementName(getAttributeQName(TAG_FAULT_ELEMENT));

        if (AeUtil.notNullOrEmpty(def.getFaultVariable())) {
            AeVariableDef varDef = new AeVariableDef();
            varDef.setName(def.getFaultVariable());
            if (def.getFaultElementName() != null) {
                varDef.setElement(def.getFaultElementName());
            } else if (def.getFaultMessageType() != null) {
                varDef.setMessageType(def.getFaultMessageType());
            }
            def.setFaultVariableDef(varDef);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
     */
    public void visit(AeActivityRepeatUntilDef def) {
        readAttributes(def);
        addActivityToParent(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
     */
    public void visit(AeActivityValidateDef def) {
        readAttributes(def);
        def.setVariables(getAttribute(TAG_VARIABLES));
        addActivityToParent(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(AeChildExtensionActivityDef)
     */
    public void visit(AeChildExtensionActivityDef def) {
        readExtensionActivities(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
     */
    public void visit(AeExtensionActivityDef def) {
        readAttributes(def);
        addActivityToParent(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
     */
    public void visit(AeActivityRethrowDef def) {
        readAttributes(def);
        addActivityToParent(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
     */
    public void visit(AeTerminationHandlerDef def) {
        readAttributes(def);

        ((IAeTerminationHandlerParentDef) getParentDef()).setTerminationHandlerDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef)
     */
    public void visit(AeLiteralDef def) {
        readAttributes(def);

        addChildrenToLiteral(getCurrentElement(), def);

        ((AeFromDef) getParentDef()).setLiteralDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
     */
    public void visit(AeForEachBranchesDef def) {
        super.visit(def);

        def.setCountCompletedBranchesOnly(getAttributeBoolean(IAeBPELConstants.TAG_FOREACH_BRANCH_COUNTCOMPLETED));
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
     */
    public void visit(AeActivityCompensateScopeDef def) {
        readAttributes(def);
        def.setTarget(getAttribute(TAG_TARGET));
        addActivityToParent(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.readers.def.AeBpelReaderVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
     */
    public void visit(AeQueryDef def) {
        readAttributes(def);

        readQueryDef(def);
        ((IAeQueryParentDef) getParentDef()).setQueryDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
     */
    public void visit(AeActivityOpaqueDef def) {
        readAttributes(def);
        addActivityToParent(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
     */
    public void visit(AeFromDef def) {
        if (TAG_OPAQUE_FROM.equals(getCurrentElement().getLocalName())) {
            readCommonAttributes(def);
            def.setOpaque(true);
        } else {
            readAttributes(def);
        }
        ((IAeFromParentDef) getParentDef()).setFromDef(def);
    }

    /**
     * Visits a query def in order to Oread the query language and query value.
     *
     * @param aDef
     */
    protected void readQueryDef(AeQueryDef aDef) {
        aDef.setQueryLanguage(getAttribute(TAG_QUERY_LANGUAGE));
        aDef.setQuery(AeXmlUtil.getText(getCurrentElement()));
    }

    /**
     * Reads extension activities
     */
    private void readExtensionActivities(AeAbstractExtensionActivityDef aDef) {
        QName elemName = new QName(getCurrentElement().getNamespaceURI(), getCurrentElement().getLocalName());
        aDef.setElementName(elemName);

        readAttributes(aDef);
        addActivityToParent(aDef);
    }
}
