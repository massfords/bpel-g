// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/xpath/ast/visitors/AeXPathVariableNodeVisitor.java,v 1.2 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.expr.xpath.ast.visitors;

import java.util.LinkedHashSet;
import java.util.Set;

import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathPathExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode;
import org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor;
import org.activebpel.rt.expr.def.AeScriptVarDef;

/**
 * This visitor will visit the xpath AST looking for variable references.
 */
public class AeXPathVariableNodeVisitor extends AeAbstractXPathNodeVisitor
{
   /** The variable references founds by the visitor. */
   private Set mVariableReferences;

   /**
    * Default c'tor.
    */
   public AeXPathVariableNodeVisitor()
   {
      setVariableReferences(new LinkedHashSet());
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode)
    */
   public void visit(AeXPathVariableNode aNode)
   {
      getVariableReferences().add(new AeScriptVarDef(aNode.getVariableQName(), getQueryForVariable(aNode)));
   }
   
   /**
    * Tries to determine the relative path/query used with this variable.  If there is a relative
    * path associated with the variable, the var node will have a parent of type AeXPathPathExprNode
    * and a single sibling (which will be the path).
    * 
    * @param aNode
    */
   protected String getQueryForVariable(AeXPathVariableNode aNode)
   {
      // Note: to get the query portion of a variable reference, examine the parent:
      //     - the parent should be a PathExpr with two children - the 2nd child will be the query (relative location path node)
      AeAbstractXPathNode parent = aNode.getParent();
      if (parent instanceof AeXPathPathExprNode)
      {
         AeAbstractXPathNode sibling = (AeAbstractXPathNode) parent.getChildren().get(1);
         return sibling.serialize();
      }
      return null;
   }

   /**
    * @return Returns the variableReferences.
    */
   public Set getVariableReferences()
   {
      return mVariableReferences;
   }

   /**
    * @param aVariableReferences The variableReferences to set.
    */
   protected void setVariableReferences(Set aVariableReferences)
   {
      mVariableReferences = aVariableReferences;
   }
}
