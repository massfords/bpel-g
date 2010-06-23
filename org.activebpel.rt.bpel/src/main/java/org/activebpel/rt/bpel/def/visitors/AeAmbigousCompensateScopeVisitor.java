//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeAmbigousCompensateScopeVisitor.java,v 1.1 2007/09/28 21:45:39 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;

/**
 * 
 */
public class AeAmbigousCompensateScopeVisitor extends AeChildScopeByNameVisitor
{
   /** the scope that we're looking for; <code>null</code> until it is found */
   private List mScopeDef = new ArrayList();
   /**
    * @param aScopeName
    */
   protected AeAmbigousCompensateScopeVisitor(String aScopeName)
   {
      super(aScopeName);
   }

   /**
    * Searches for a child scope of the given scope that has the given name.
    * Returns the scope definition if a matching child scope is found;
    * otherwise; returns <code>null</code>.
    *
    * @param aRootScopeDef
    * @param aScopeName
    * @return List
    */
   public static List findChildScopesByName(AeScopeDef aRootScopeDef, String aScopeName)
   {
      AeAmbigousCompensateScopeVisitor visitor = new AeAmbigousCompensateScopeVisitor(aScopeName);
      aRootScopeDef.getActivityDef().accept(visitor);
      return (List) visitor.getScopeDefs();
   }
   /**
    * Returns the scope that we're looking for; <code>null</code> until it is
    * found.
    */
   protected Object getScopeDefs()
   {
      return mScopeDef;
   }
   
   /**
    * Setter for the scope def
    * @param aScopeDef
    */
   protected void setScopeDef(AeActivityScopeDef aScopeDef)
   {
      mScopeDef.add(aScopeDef);
   }
   
   /**
    * Returns <code>true</code> if and only if found.
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractSearchVisitor#isFound()
    */
   public boolean isFound()
   {
      return false;
   }
}
