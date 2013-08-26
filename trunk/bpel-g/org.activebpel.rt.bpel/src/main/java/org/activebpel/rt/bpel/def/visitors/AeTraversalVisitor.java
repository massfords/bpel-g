// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeTraversalVisitor.java,v 1.18 2007/10/12 16:09:48 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AeExtensionDef;
import org.activebpel.rt.bpel.def.AeExtensionsDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.AeMessageExchangeDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AePartnerDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinksDef;
import org.activebpel.rt.bpel.def.AePartnersDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.activity.AeActivityBreakDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityContinueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityExitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityFlowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef;
import org.activebpel.rt.bpel.def.activity.AeActivityThrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinksDef;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
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
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * A specialized visitor that takes another visitor and a traversal object and
 * calls the appropriate traverse method during each visitation.
 */
public class AeTraversalVisitor implements IAeDefVisitor {
    /**
     * Visitor that gets passed to the traversal object
     */
    private IAeBaseXmlDefVisitor mVisitor;
    /**
     * Traversal object
     */
    private IAeDefTraverser mTravers;

    /**
     * Accepts both traversal object and visitor.
     *
     * @param aTravers - used to traverse the underlying object model
     * @param aVisitor - passed to traverse object for subsequent visitation
     */
    public AeTraversalVisitor(IAeDefTraverser aTravers, IAeBaseXmlDefVisitor aVisitor) {
        setTravers(aTravers);
        setVisitor(aVisitor);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
     */
    public void visit(AeProcessDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
     */
    public void visit(AeActivityScopeDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
     */
    public void visit(AeCorrelationSetDef aDef) {
        getTravers().traverse(aDef, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
     */
    public void visit(AeCatchDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
     */
    public void visit(AeVariableDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
     */
    public void visit(AeVariablesDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
     */
    public void visit(AeEventHandlersDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
     */
    public void visit(AeCompensationHandlerDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
     */
    public void visit(AeCorrelationSetsDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
     */
    public void visit(AeFaultHandlersDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
     */
    public void visit(AeOnMessageDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
     */
    public void visit(AeOnEventDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
     */
    public void visit(AeOnAlarmDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
     */
    public void visit(AeActivityAssignDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
     */
    public void visit(AeActivityCompensateDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
     */
    public void visit(AeActivityCompensateScopeDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
     */
    public void visit(AeActivityEmptyDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
     */
    public void visit(AeActivityFlowDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
     */
    public void visit(AeActivityInvokeDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
     */
    public void visit(AeActivityPickDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
     */
    public void visit(AeActivityReceiveDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
     */
    public void visit(AeActivityReplyDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
     */
    public void visit(AeActivitySuspendDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
     */
    public void visit(AeActivitySequenceDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
     */
    public void visit(AeActivityExitDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
     */
    public void visit(AeActivityThrowDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
     */
    public void visit(AeActivityWaitDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
     */
    public void visit(AeActivityWhileDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
     */
    public void visit(AeActivityRepeatUntilDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
     */
    public void visit(AeActivityForEachDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef)
     */
    public void visit(AeForEachFinalDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef)
     */
    public void visit(AeForEachStartDef aDef) {
        getTravers().traverse(aDef, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
     */
    public void visit(AeForEachBranchesDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef)
     */
    public void visit(AeForEachCompletionConditionDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
     */
    public void visit(AeActivityContinueDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
     */
    public void visit(AeActivityBreakDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
     */
    public void visit(AeCatchAllDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
     */
    public void visit(AeAssignCopyDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
     */
    public void visit(AeFromDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
     */
    public void visit(AeToDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
     */
    public void visit(AeCorrelationDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
     */
    public void visit(AeLinkDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
     */
    public void visit(AePartnerDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
     */
    public void visit(AePartnerLinkDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
     */
    public void visit(AeScopeDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
     */
    public void visit(AeMessageExchangesDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
     */
    public void visit(AeMessageExchangeDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
     */
    public void visit(AeSourcesDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
     */
    public void visit(AeSourceDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
     */
    public void visit(AeTargetsDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
     */
    public void visit(AeTargetDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
     */
    public void visit(AePartnerLinksDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
     */
    public void visit(AePartnersDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinksDef)
     */
    public void visit(AeLinksDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
     */
    public void visit(AeCorrelationsDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
     */
    public void visit(AeImportDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
     */
    public void visit(AeDocumentationDef aDef) {
        getTravers().traverse(aDef, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
     */
    public void visit(AeActivityValidateDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
     */
    public void visit(AeExtensibleAssignDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
     */
    public void visit(AeExtensionDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
     */
    public void visit(AeExtensionsDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
     */
    public void visit(AeFromPartsDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
     */
    public void visit(AeToPartsDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
     */
    public void visit(AeFromPartDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
     */
    public void visit(AeToPartDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
     */
    public void visit(AeJoinConditionDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
     */
    public void visit(AeTransitionConditionDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
     */
    public void visit(AeForDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
     */
    public void visit(AeUntilDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
     */
    public void visit(AeExtensionActivityDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
     */
    public void visit(AeActivityIfDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
     */
    public void visit(AeConditionDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
     */
    public void visit(AeElseDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
     */
    public void visit(AeElseIfDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
     */
    public void visit(AeIfDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
     */
    public void visit(AeActivityRethrowDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
     */
    public void visit(AeRepeatEveryDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
     */
    public void visit(AeTerminationHandlerDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef)
     */
    public void visit(AeLiteralDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
     */
    public void visit(AeQueryDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
     */
    public void visit(AeActivityOpaqueDef def) {
        getTravers().traverse(def, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
     */
    public void visit(AeExtensionAttributeDef aDef) {
        getTravers().traverse(aDef, getVisitor());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
     */
    public void visit(AeExtensionElementDef aDef) {
        getTravers().traverse(aDef, getVisitor());
    }

    /**
     * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeBaseXmlDef)
     */
    public void visit(AeBaseXmlDef aDef) {
        throw new UnsupportedOperationException();
    }

    /**
     * Setter for the other visitor we're using
     *
     * @param aVisitor
     */
    public void setVisitor(IAeBaseXmlDefVisitor aVisitor) {
        mVisitor = aVisitor;
    }

    /**
     * Getter for the other visitor we're using
     */
    public IAeBaseXmlDefVisitor getVisitor() {
        return mVisitor;
    }

    /**
     * Setter for the traversal object
     *
     * @param aTravers
     */
    public void setTravers(IAeDefTraverser aTravers) {
        mTravers = aTravers;
    }

    /**
     * Getter for the traversal object
     */
    public IAeDefTraverser getTravers() {
        return mTravers;
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef)
     */
    public void visit(AeChildExtensionActivityDef def) {
        getTravers().traverse(def, getVisitor());
    }
}
