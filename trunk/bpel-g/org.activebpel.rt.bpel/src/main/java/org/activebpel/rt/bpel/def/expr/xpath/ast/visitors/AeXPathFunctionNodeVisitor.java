// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.expr.xpath.ast.visitors;

import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode;
import org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor;
import org.activebpel.rt.expr.def.AeScriptFuncDef;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This visitor will visit the xpath AST looking for functions.
 */
public class AeXPathFunctionNodeVisitor extends AeAbstractXPathNodeVisitor {
    /**
     * The functions founds by the visitor.
     */
    private Set<AeScriptFuncDef> mFunctions;

    /**
     * Default c'tor.
     */
    public AeXPathFunctionNodeVisitor() {
        setFunctions(new LinkedHashSet<AeScriptFuncDef>());
    }

    /**
     * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode)
     */
    public void visit(AeXPathFunctionNode aNode) {
        AeScriptFuncDef funcDef = new AeScriptFuncDef(aNode.getFunctionQName());

        List<Object> arguments = new ArrayList<>();
        for (AeAbstractXPathNode child : aNode.getChildren()) {
            if (child instanceof AeXPathLiteralNode)
                arguments.add(((AeXPathLiteralNode) child).getValue());
            else
                arguments.add(AeScriptFuncDef.__EXPRESSION__);
        }
        funcDef.setArgs(arguments);

        getFunctions().add(funcDef);
    }

    /**
     * @return Returns the functions.
     */
    public Set<AeScriptFuncDef> getFunctions() {
        return mFunctions;
    }

    /**
     * @param aFunctions The functions to set.
     */
    protected void setFunctions(Set<AeScriptFuncDef> aFunctions) {
        mFunctions = aFunctions;
    }
}
