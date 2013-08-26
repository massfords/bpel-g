// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/writers/def/AeWriterVisitor.java,v 1.45 2008/03/11 14:47:08 JPerrotto Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.writers.def;

import org.activebpel.rt.bpel.def.*;
import org.activebpel.rt.bpel.def.activity.*;
import org.activebpel.rt.bpel.def.activity.support.*;
import org.activebpel.rt.bpel.def.io.AeCorrelationPatternIOFactory;
import org.activebpel.rt.bpel.def.io.IAeCorrelationPatternIO;
import org.activebpel.rt.bpel.def.io.writers.AeCorrelationSetUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.io.writers.AeAbstractDefWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.XMLConstants;
import java.util.Iterator;
import java.util.Map;

/**
 * Impl of the def visitor that serializes the def to a DOM.
 */
public abstract class AeWriterVisitor extends AeAbstractDefWriter implements IAeBPELConstants, IAeDefVisitor {
    /**
     * Creates a new element under the passed parent and starts visiting for it.
     *
     * @param aDef                  The def that is being serialized
     * @param aParentElement        The parent element of the objects created.
     * @param aNamespace            The namespace of the element we're creating
     * @param aTagName              The tag of the new element to create.
     * @param aPreferredPrefixesMap map of namespace URI's to prefix.
     */
    public AeWriterVisitor(AeBaseXmlDef aDef, Element aParentElement, String aNamespace, String aTagName, Map<String, String> aPreferredPrefixesMap) {
        super(aDef, aParentElement, aNamespace, aTagName, aPreferredPrefixesMap);
    }

    /**
     * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
     */
    public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement) {
        aBaseDef.accept(this);
        return getElement();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
     */
    public void visit(AeActivityAssignDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
     */
    public void visit(AeActivityCompensateDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
     */
    public void visit(AeActivityCompensateScopeDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
     */
    public void visit(AeActivityEmptyDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
     */
    public void visit(AeActivityContinueDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
     */
    public void visit(AeActivityBreakDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
     */
    public void visit(AeActivityFlowDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
     */
    public void visit(AeActivityInvokeDef def) {
        writeAttributes(def);
        setAttribute(TAG_INPUT_VARIABLE, def.getInputVariable());
        setAttribute(TAG_OUTPUT_VARIABLE, def.getOutputVariable());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
     */
    public void visit(AeActivityPickDef def) {
        writeAttributes(def);
        setAttribute(TAG_CREATE_INSTANCE, def.isCreateInstance(), false);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
     */
    public void visit(AeActivityReceiveDef def) {
        writeAttributes(def);
        setAttribute(TAG_VARIABLE, def.getVariable());
        setAttribute(TAG_CREATE_INSTANCE, def.isCreateInstance(), false);
        writeMessageExchange(def.getMessageExchange());
    }

    /**
     * Writes the message exchange value if not empty or null.
     *
     * @param aMessageExchangeValue
     */
    protected abstract void writeMessageExchange(String aMessageExchangeValue);

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
     */
    public void visit(AeActivityReplyDef def) {
        writeAttributes(def);
        setAttribute(TAG_VARIABLE, def.getVariable());
        setAttribute(TAG_FAULT_NAME, def.getFaultName());
        writeMessageExchange(def.getMessageExchange());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
     */
    public void visit(AeActivitySuspendDef def) {
        writeAttributes(def);
        setAttribute(TAG_VARIABLE, def.getVariable());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
     */
    public void visit(AeActivityScopeDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
     */
    public void visit(AeActivitySequenceDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
     */
    public void visit(AeActivityExitDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
     */
    public void visit(AeActivityThrowDef def) {
        writeAttributes(def);
        setAttribute(TAG_FAULT_NAME, def.getFaultName());
        setAttribute(TAG_FAULT_VARIABLE, def.getFaultVariable());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
     */
    public void visit(AeActivityWaitDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
     */
    public void visit(AeActivityWhileDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
     */
    public void visit(AeActivityRepeatUntilDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
     */
    public void visit(AeActivityForEachDef def) {
        writeAttributes(def);
        setAttribute(TAG_FOREACH_COUNTERNAME, def.getCounterName());
        setAttribute(TAG_FOREACH_PARALLEL, def.isParallel(), true);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef)
     */
    public void visit(AeForEachCompletionConditionDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef)
     */
    public void visit(AeForEachFinalDef def) {
        writeExpressionDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef)
     */
    public void visit(AeForEachStartDef aDef) {
        writeExpressionDef(aDef);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
     */
    public void visit(AeForEachBranchesDef def) {
        writeExpressionDef(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
     */
    public void visit(AeAssignCopyDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
     */
    public void visit(AeFromDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
     */
    public void visit(AeToDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
     */
    public void visit(AeQueryDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
     */
    public void visit(AeCorrelationsDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
     */
    public void visit(AeCorrelationDef def) {
        writeStandardAttributes(def);
        setAttribute(TAG_SET, def.getCorrelationSetName());
        if (def.getPattern() != null) {
            AeCorrelationDef.AeCorrelationPatternType type = def.getPattern();
            IAeCorrelationPatternIO patternIO = AeCorrelationPatternIOFactory.getInstance(getElement().getNamespaceURI());
            setAttribute(TAG_PATTERN, patternIO.toString(type));
        }
        setAttribute(TAG_INITIATE, def.getInitiate());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
     */
    public void visit(AeCorrelationSetsDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
     */
    public void visit(AeCorrelationSetDef aDef) {
        writeAttributes(aDef);
        setAttribute(TAG_PROPERTIES,
                AeCorrelationSetUtil.formatProperties(aDef, getElement()));
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
     */
    public void visit(AeCatchAllDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
     */
    public void visit(AeEventHandlersDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
     */
    public void visit(AeCatchDef def) {
        writeStandardAttributes(def);
        setAttribute(TAG_FAULT_NAME, def.getFaultName());
        setAttribute(TAG_FAULT_VARIABLE, def.getFaultVariable());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
     */
    public void visit(AeFaultHandlersDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinksDef)
     */
    public void visit(AeLinksDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
     */
    public void visit(AeLinkDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
     */
    public void visit(AeMessageExchangesDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
     */
    public void visit(AeMessageExchangeDef def) {
        writeStandardAttributes(def);

        setAttribute(TAG_NAME, def.getName());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
     */
    public void visit(AeOnAlarmDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
     */
    public void visit(AeOnMessageDef def) {
        writeStandardAttributes(def);
        setAttribute(TAG_PARTNER_LINK, def.getPartnerLink());
        if (writePortTypeAttrib()) {
            setAttribute(TAG_PORT_TYPE, def.getPortType());
        }
        setAttribute(TAG_OPERATION, def.getOperation());
        setAttribute(TAG_VARIABLE, def.getVariable());
        writeMessageExchange(def.getMessageExchange());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
     */
    public void visit(AeOnEventDef def) {
        visit((AeOnMessageDef) def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
     */
    public void visit(AePartnerDef def) {
        writeAttributes(def);

        for (Iterator iter = def.getPartnerLinks(); iter.hasNext(); ) {
            String qualifier = AeUtil.isNullOrEmpty(getElement().getPrefix()) ? "" : getElement().getPrefix() + ":"; //$NON-NLS-1$ //$NON-NLS-2$
            Element element = getElement().getOwnerDocument().createElementNS(getElement().getNamespaceURI(), qualifier + TAG_PARTNER_LINK);
            // use setAttributeNS to ensure Attr impl is NS aware
            element.setAttributeNS(null, TAG_NAME, (String) iter.next());
            getElement().appendChild(element);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
     */
    public void visit(AePartnerLinkDef def) {
        writeAttributes(def);
        setAttribute(TAG_PARTNER_LINK_TYPE, def.getPartnerLinkTypeName());
        setAttribute(TAG_MY_ROLE, def.getMyRole());
        setAttribute(TAG_PARTNER_ROLE, def.getPartnerRole());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
     */
    public void visit(AePartnerLinksDef def) {
        writeStandardAttributes(def);
    }


    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
     */
    public void visit(AePartnersDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
     */
    public void visit(AeProcessDef def) {
        // Add preamble comment if available (user comments written with attributes method)
        if (!AeUtil.isNullOrEmpty(def.getProcessPreambleComment())) {
            Document doc = getElement().getOwnerDocument();
            Node commentNode = doc.createComment(def.getProcessPreambleComment());
            getElement().getParentNode().insertBefore(commentNode, getElement());
        }

        writeAttributes(def);

        // write the default namespace out if it's present.
        if (AeUtil.notNullOrEmpty(def.getDefaultNamespace())) {
            getElement().setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", def.getDefaultNamespace()); //$NON-NLS-1$
        }

        setAttribute(TAG_TARGET_NAMESPACE, def.getTargetNamespace());
        setAttribute(TAG_QUERY_LANGUAGE, def.getQueryLanguage());
        setAttribute(TAG_EXPRESSION_LANGUAGE, def.getExpressionLanguage());

        setAttribute(TAG_SUPPRESS_JOIN_FAILURE, def.getSuppressJoinFailure(), false);
        setAttribute(TAG_ENABLE_INSTANCE_COMPENSATION, def.getEnableInstanceCompensation(), false);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
     */
    public void visit(AeCompensationHandlerDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
     */
    public void visit(AeScopeDef def) {
        writeAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
     */
    public void visit(AeSourcesDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
     */
    public void visit(AeSourceDef def) {
        writeStandardAttributes(def);
        setAttribute(TAG_LINK_NAME, def.getLinkName());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
     */
    public void visit(AeTargetsDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
     */
    public void visit(AeTargetDef def) {
        writeStandardAttributes(def);
        setAttribute(TAG_LINK_NAME, def.getLinkName());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
     */
    public void visit(AeVariableDef def) {
        writeAttributes(def);
        setAttribute(TAG_MESSAGE_TYPE, def.getMessageType());
        setAttribute(TAG_TYPE, def.getType());
        setAttribute(TAG_ELEMENT, def.getElement());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
     */
    public void visit(AeVariablesDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
     */
    public void visit(AeImportDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
     */
    public void visit(AeDocumentationDef aDef) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
     */
    public void visit(AeExtensibleAssignDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
     */
    public void visit(AeActivityValidateDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
     */
    public void visit(AeExtensionDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
     */
    public void visit(AeExtensionsDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
     */
    public void visit(AeFromPartsDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
     */
    public void visit(AeToPartsDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
     */
    public void visit(AeFromPartDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
     */
    public void visit(AeToPartDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
     */
    public void visit(AeJoinConditionDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
     */
    public void visit(AeTransitionConditionDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
     */
    public void visit(AeForDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
     */
    public void visit(AeUntilDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(AeChildExtensionActivityDef)
     */
    public void visit(AeChildExtensionActivityDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
     */
    public void visit(AeExtensionActivityDef def) {
        // The writer registry for bpel 1.1 will skip the extension activity wrapper
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
     */
    public void visit(AeActivityIfDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
     */
    public void visit(AeConditionDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
     */
    public void visit(AeElseDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
     */
    public void visit(AeElseIfDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
     */
    public void visit(AeIfDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
     */
    public void visit(AeActivityRethrowDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
     */
    public void visit(AeRepeatEveryDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
     */
    public void visit(AeTerminationHandlerDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef)
     */
    public void visit(AeLiteralDef def) {
        writeStandardAttributes(def);

        for (Node node : def.getChildNodes()) {
            Node importedNode = getElement().getOwnerDocument().importNode(node, true);
            getElement().appendChild(importedNode);
            if (importedNode.getNodeType() == Node.ELEMENT_NODE)
                AeXmlUtil.removeDuplicateNSDecls((Element) importedNode);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
     */
    public void visit(AeActivityOpaqueDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
     */
    public void visit(AeExtensionAttributeDef aDef) {
        // Note: the extension attribute def is skipped in the registry
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
     */
    public void visit(AeExtensionElementDef aDef) {
        // Note: a special writer is used to write out the extension element def.
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeBaseXmlDef)
     */
    public void visit(AeBaseXmlDef aDef) {
        throw new UnsupportedOperationException();
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeAttributes(AeActivityPartnerLinkBaseDef aDef) {
        writeStandardAttributes(aDef);
        writeNamedAttributes(aDef);
        writeActivityAttributes(aDef);
        writeActivityPartnerLinkBasedAttributes(aDef);
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeAttributes(AeActivityDef aDef) {
        writeStandardAttributes(aDef);
        writeNamedAttributes(aDef);
        writeActivityAttributes(aDef);
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeAttributes(AeNamedDef aDef) {
        writeStandardAttributes(aDef);
        writeNamedAttributes(aDef);
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeAttributes(AeToDef aDef) {
        writeStandardAttributes(aDef);
        writeAssignToAttributes(aDef);
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeAttributes(AeFromDef aDef) {
        writeStandardAttributes(aDef);
        writeAssignFromAttributes(aDef);
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeActivityPartnerLinkBasedAttributes(AeActivityPartnerLinkBaseDef aDef) {
        setAttribute(TAG_PARTNER_LINK, aDef.getPartnerLink());
        if (writePortTypeAttrib()) {
            setAttribute(TAG_PORT_TYPE, aDef.getPortType());
        }
        setAttribute(TAG_OPERATION, aDef.getOperation());
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeActivityAttributes(AeActivityDef aDef) {
        // Suppress join failure is an optional flag, do not set if null
        if (aDef.getSuppressFailure() != null) {
            setAttribute(TAG_SUPPRESS_FAILURE,
                    aDef.getSuppressFailure(), true);
        }
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeNamedAttributes(AeNamedDef aDef) {
        setAttribute(TAG_NAME, aDef.getName());
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeAssignVarAttributes(AeVarDef aDef) {
        setAttribute(TAG_VARIABLE, aDef.getVariable());
        setAttribute(TAG_PROPERTY, aDef.getProperty());
        setAttribute(TAG_PART, aDef.getPart());
        setAttribute(TAG_PARTNER_LINK, aDef.getPartnerLink());
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeAssignToAttributes(AeToDef aDef) {
        writeAssignVarAttributes(aDef);
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeAssignFromAttributes(AeFromDef aDef) {
        writeAssignVarAttributes(aDef);
        setAttribute(TAG_ENDPOINT_REFERENCE, aDef.getEndpointReference());
    }

    /**
     * Visits an expression base def in order to write out the expressionLanguage attribute
     * and the value of the expression.
     *
     * @param aDef
     */
    protected void writeExpressionDef(AeExpressionBaseDef aDef) {
        writeExpressionLang(aDef);
        if (AeUtil.notNullOrEmpty(aDef.getExpression())) {
            Text textNode = getElement().getOwnerDocument().createTextNode(aDef.getExpression());
            getElement().appendChild(textNode);
        }
    }

    /**
     * Method used to write out expression language for expression conditions
     *
     * @param aDef Expression Definition object
     */
    protected void writeExpressionLang(IAeExpressionDef aDef) {
        setAttribute(TAG_EXPRESSION_LANGUAGE, aDef.getExpressionLanguage());
    }

    /**
     * BPEL uses yes/no values for boolean attributes rather than
     * xsd:boolean's true/false or 1/0 convention.
     *
     * @see org.activebpel.rt.xml.def.io.writers.AeAbstractDefWriter#booleanToString(boolean)
     */
    protected String booleanToString(boolean aBoolean) {
        if (aBoolean)
            return "yes"; //$NON-NLS-1$
        return "no"; //$NON-NLS-1$
    }

    /**
     * Determines if the WSIO activities (receive, reply, invoke, onMessage, onEvent) should
     * have their portType attribute serialized.
     * <p/>
     * True by default. Derived class may override.
     *
     * @return boolean true write portType attribute, otherwise false.
     */
    protected boolean writePortTypeAttrib() {
        return true;
    }
}
