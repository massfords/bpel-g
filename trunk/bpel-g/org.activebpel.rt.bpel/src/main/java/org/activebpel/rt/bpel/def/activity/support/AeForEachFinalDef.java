//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/AeForEachFinalDef.java,v 1.2 2006/06/26 16:50:32 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the final expression for a forEach
 */
public class AeForEachFinalDef extends AeExpressionBaseDef {
    /**
     *
     */
    private static final long serialVersionUID = 3623903748352647468L;

    /**
     * Default c'tor.
     */
    public AeForEachFinalDef() {
        super();
    }

    /**
     * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
     */
    public void accept(IAeDefVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
 