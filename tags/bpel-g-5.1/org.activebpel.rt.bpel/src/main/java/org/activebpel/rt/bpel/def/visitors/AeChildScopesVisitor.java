// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeChildScopesVisitor.java,v 1.2 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.util.LinkedHashSet;
import java.util.Set;

import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;

/**
 * Visitor that locates child scopes.
 */
public class AeChildScopesVisitor extends AeAbstractDefVisitor
{
   /** the set of child scopes */
   private Set mChildScopeDefs = new LinkedHashSet();
   
   /**
    * ctor
    */
   protected AeChildScopesVisitor()
   {
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this)); 
   }

   /**
    * Returns child scopes of the given scope.
    *
    * @param aRootScopeDef
    */
   public static Set findChildScopes(AeScopeDef aRootScopeDef)
   {
      AeChildScopesVisitor visitor = new AeChildScopesVisitor();
      aRootScopeDef.getActivityDef().accept(visitor);
      return visitor.getChildScopeDefs();
   }

   /**
    * Returns the set of child scopes.
    */
   protected Set getChildScopeDefs()
   {
      return mChildScopeDefs;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      // Found a child scope.
      getChildScopeDefs().add(aDef);
      
      // No traversing into the def since we can only reference scopes 1 level deep.
   }
}