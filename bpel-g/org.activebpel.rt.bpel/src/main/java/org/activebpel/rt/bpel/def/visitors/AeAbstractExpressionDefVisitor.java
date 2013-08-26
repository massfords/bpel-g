// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeAbstractExpressionDefVisitor.java,v 1.1 2006/10/12 20:15:22 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;

/**
 * An abstract visitor that visits all expression defs.
 */
public abstract class AeAbstractExpressionDefVisitor extends AeAbstractDefVisitor {
    /**
     * Default c'tor.
     */
    public AeAbstractExpressionDefVisitor() {
        setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
     */
    public void visit(AeConditionDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
     */
    public void visit(AeForDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
     */
    public void visit(AeRepeatEveryDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
     */
    public void visit(AeForEachBranchesDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef)
     */
    public void visit(AeForEachFinalDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef)
     */
    public void visit(AeForEachStartDef aDef) {
        visitExpressionDef(aDef);
        super.visit(aDef);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef)
     */
    public void visit(AeForEachCompletionConditionDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
     */
    public void visit(AeJoinConditionDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
     */
    public void visit(AeTransitionConditionDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
     */
    public void visit(AeUntilDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
     */
    public void visit(AeFromDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
     */
    public void visit(AeToDef def) {
        visitExpressionDef(def);
        super.visit(def);
    }

    /**
     * Called when an expression def is visited.
     *
     * @param aExpressionDef
     */
    protected abstract void visitExpressionDef(IAeExpressionDef aExpressionDef);
}
