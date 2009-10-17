// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeIsolatedScopeVisitor.java,v 1.2 2007/09/26 02:21:04 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Visitor that sets {@link AeActivityDef#setIsolatedScope(AeActivityScopeDef)}
 * for all activities.
 */
public class AeIsolatedScopeVisitor extends AeAbstractDefVisitor
{
   /**
    * The isolated scope that we are currently traversing, or
    * <code>null</code> if we are not within an isolated scope.
    */
   private AeActivityScopeDef mIsolatedScope;

   /**
    * Constructs the visitor with the default traversal implementation. 
    */
   public AeIsolatedScopeVisitor()
   {
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }
   
   /**
    * Returns the isolated scope that we are currently traversing or
    * <code>null</code> if we are not within an isolated scope.
    */
   protected AeActivityScopeDef getIsolatedScope()
   {
      return mIsolatedScope;
   }

   /**
    * Sets the isolated scope that we are currently traversing.
    *
    * @param aIsolatedScope
    */
   protected void setIsolatedScope(AeActivityScopeDef aIsolatedScope)
   {
      mIsolatedScope = aIsolatedScope;
   }

   /**
    * Overrides method to save the current isolated scope to activities.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#traverse(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      if (aDef instanceof AeActivityDef)
      {
         ((AeActivityDef) aDef).setIsolatedScope(getIsolatedScope());
      }

      super.traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      if (aDef.isIsolated())
      {
         // Save (and restore) the current isolated scope in case we have an
         // nested isolated scope (which should never happen).
         AeActivityScopeDef oldIsolatedScope = getIsolatedScope();
         
         // Set the current isolated scope.
         setIsolatedScope(aDef);

         super.visit(aDef);

         setIsolatedScope(oldIsolatedScope);
      }
      else
      {
         super.visit(aDef);
      }
   }
}