// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/writers/def/AeBPWSWriterVisitor.java,v 1.6 2008/01/11 01:50:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io.writers.def;

import java.util.Collections;
import java.util.Map;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeVarDef;
import org.activebpel.rt.bpel.def.io.IAeBpelLegacyConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Element;

/**
 * An implementation of a writer visitor for bpel4ws 1.1.
 */
public class AeBPWSWriterVisitor extends AeWriterVisitor {
    /**
     * mapping of namespaces to preferred prefixes
     */
    private static final Map<String, String> sPreferredPrefixes = Collections.singletonMap(IAeBPELConstants.BPWS_NAMESPACE_URI, "bpws"); //$NON-NLS-1$

    /**
     * Constructs a bpel4ws writer visitor.
     *
     * @param aDef
     * @param aParentElement
     * @param aNamespace
     * @param aTagName
     */
    public AeBPWSWriterVisitor(AeBaseXmlDef aDef, Element aParentElement, String aNamespace, String aTagName) {
        super(aDef, aParentElement, aNamespace, aTagName, sPreferredPrefixes);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writeAssignVarAttributes(org.activebpel.rt.bpel.def.activity.support.AeVarDef)
     */
    protected void writeAssignVarAttributes(AeVarDef aDef) {
        super.writeAssignVarAttributes(aDef);

        setAttribute(TAG_QUERY, aDef.getQuery());
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writeAssignFromAttributes(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
     */
    protected void writeAssignFromAttributes(AeFromDef aDef) {
        super.writeAssignFromAttributes(aDef);

        setAttribute(TAG_EXPRESSION, aDef.getExpression());
        setAttribute(TAG_OPAQUE_ATTR, aDef.isOpaque(), false);
    }

    /**
     * Write attributes to the Element.
     *
     * @param aDef
     */
    protected void writeActivityAttributes(AeActivityDef aDef) {
        super.writeActivityAttributes(aDef);

        AeJoinConditionDef joinConditionDef = aDef.getJoinConditionDef();
        if (joinConditionDef != null)
            setAttribute(TAG_JOIN_CONDITION, joinConditionDef.getExpression());
    }

    /**
     * Writes the message exchange value if not empty or null.  Overrides the base in order
     * to put the message exchange attribute in the abx namespace.
     *
     * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writeMessageExchange(java.lang.String)
     */
    protected void writeMessageExchange(String aMessageExchangeValue) {
        if (AeUtil.notNullOrEmpty(aMessageExchangeValue)) {
            String prefix = AeXmlUtil.getOrCreatePrefix(getElement(), IAeBPELConstants.ABX_2_0_NAMESPACE_URI);
            getElement().setAttributeNS(IAeBPELConstants.ABX_2_0_NAMESPACE_URI,
                    prefix + ":" + TAG_MESSAGE_EXCHANGE,  //$NON-NLS-1$
                    aMessageExchangeValue);
        }
    }

    /**
     * Overrides to append the abstractProcess boolean attribute.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
     */
    public void visit(AeProcessDef def) {
        super.visit(def);
        setAttribute(TAG_ABSTRACT_PROCESS, def.isAbstractProcess(), false);
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
     */
    public void visit(AeForEachBranchesDef def) {
        super.visit(def);
        setAttribute(IAeBpelLegacyConstants.COUNT_COMPLETED_BRANCHES_ONLY, def.isCountCompletedBranchesOnly(), false);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
     */
    public void visit(AeSourceDef def) {
        super.visit(def);

        setAttribute(TAG_TRANSITION_CONDITION, def.getTransitionCondition());
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
     */
    public void visit(AeActivityWaitDef def) {
        super.visit(def);

        setAttribute(TAG_FOR, def.getFor());
        setAttribute(TAG_UNTIL, def.getUntil());
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
     */
    public void visit(AeOnAlarmDef def) {
        super.visit(def);

        setAttribute(TAG_FOR, def.getFor());
        setAttribute(TAG_UNTIL, def.getUntil());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
     */
    public void visit(AeActivityWhileDef def) {
        super.visit(def);

        setAttribute(TAG_CONDITION, def.getConditionDef().getExpression());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
     */
    public void visit(AeActivityScopeDef def) {
        super.visit(def);

        setAttribute(IAeBpelLegacyConstants.TAG_VARIABLE_ACCESS_SERIALIZABLE, def.isIsolated(), false);
    }

    /**
     * Visit the if activity.  Note that in 1.1, the if activity is really a switch activity.  We
     * model it this way in order to have a single model for both 2.0 and 1.1.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
     */
    public void visit(AeActivityIfDef def) {
        writeAttributes(def);
    }

    /**
     * Visits the ifDef to write out its state.  Note that the ifDef in bpel 1.1 is really the first
     * case in a switch.  We model it as an ifDef in order to have a single model for both 1.1 and 2.0.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
     */
    public void visit(AeIfDef def) {
        visit((AeElseIfDef) def);
    }

    /**
     * Visits the elseIfDef to write out its state.  Note that the elseIfDef in bpel 1.1 is really
     * a case def.  We model it as an elseIfDef in order to have a single model for both 1.1 and 2.0.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
     */
    public void visit(AeElseIfDef def) {
        writeStandardAttributes(def);
        if (def.getConditionDef() != null)
            setAttribute(TAG_CONDITION, def.getConditionDef().getExpression());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
     */
    public void visit(AeElseDef def) {
        writeStandardAttributes(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
     */
    public void visit(AeActivityCompensateScopeDef def) {
        // Even though this construct doesn't exist in bpel 1.1, we model the <compensate scope="S1" form of the
        // bpel 1.1 activity by using the bpel 2.0 compensateScope def.
        writeAttributes(def);

        setAttribute(TAG_SCOPE, def.getTarget());
    }

    /**
     * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
     */
    public void visit(AeToDef def) {
        super.visit(def);

        setAttribute(TAG_QUERY, def.getQuery());
    }
}
