// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeChildScopeByNameVisitor.java,v 1.3 2006/11/08 23:05:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;

/**
 * Visitor that locates a child scope by name.
 */
public class AeChildScopeByNameVisitor extends AeAbstractSearchVisitor
{
   /** name of the scope we're looking for */
   private String mScopeName;
   /** the scope that we're looking for; <code>null</code> until it is found */
   private AeActivityScopeDef mScopeDef;
   
   /**
    * ctor
    * @param aScopeName
    */
   protected AeChildScopeByNameVisitor(String aScopeName)
   {
      mScopeName = aScopeName;
   }

   /**
    * Searches for a child scope of the given scope that has the given name.
    * Returns the scope definition if a matching child scope is found;
    * otherwise; returns <code>null</code>.
    *
    * @param aRootScopeDef
    * @param aScopeName
    */
   public static AeActivityScopeDef findChildScopeByName(AeScopeDef aRootScopeDef, String aScopeName)
   {
      AeChildScopeByNameVisitor visitor = new AeChildScopeByNameVisitor(aScopeName);
      aRootScopeDef.getActivityDef().accept(visitor);
      return visitor.getScopeDef();
   }

   /**
    * Returns the scope that we're looking for; <code>null</code> until it is
    * found.
    */
   protected AeActivityScopeDef getScopeDef()
   {
      return mScopeDef;
   }
   
   /**
    * Setter for the scope def
    * @param aScopeDef
    */
   protected void setScopeDef(AeActivityScopeDef aScopeDef)
   {
      mScopeDef = aScopeDef;
   }

   /**
    * Returns <code>true</code> if and only if found.
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractSearchVisitor#isFound()
    */
   public boolean isFound()
   {
      return getScopeDef() != null;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      if (mScopeName.equals(aDef.getName()))
      {
         // found what we're looking for
         setScopeDef(aDef);
      }
      // either way, no traversing into the def since we can only reference scopes 1 level deep.
   }
}