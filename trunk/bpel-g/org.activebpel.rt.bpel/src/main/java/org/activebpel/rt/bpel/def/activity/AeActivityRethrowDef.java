// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the 'rethrow' bpel construct that was introduced in WS-BPEL 2.0.
 */
public class AeActivityRethrowDef extends AeActivityDef {
    /**
     *
     */
    private static final long serialVersionUID = -7681516990162573194L;

    /**
     * Default c'tor.
     */
    public AeActivityRethrowDef() {
        super();
    }

    /**
     * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
     */
    public void accept(IAeDefVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
