//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeScopeSnapshotOptimizationVisitor.java,v 1.2 2006/07/26 21:47:12 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import java.util.Stack;

import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;

/**
 * Visits the scopes to determine which scopes need to record snapshots when they complete
 * due to a nested explicit compensation handler's access to variables, correlationSets, partnerLinks.
 */
public class AeScopeSnapshotOptimizationVisitor extends AeAbstractDefVisitor
{
   /** stack of scopes enclosed scopes */
   private Stack mEnclosedScopes = new Stack();
   
   /**
    * Ctor
    */
   public AeScopeSnapshotOptimizationVisitor()
   {
      setTraversalVisitor( new AeTraversalVisitor(new AeDefTraverser(), this));
   }
   

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      getEnclosedScopes().push(aDef);
      super.visit(aDef);
      getEnclosedScopes().pop();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void visit(AeCompensationHandlerDef aDef)
   {
      for(int i=getEnclosedScopes().size()-1; i>=0; i--)
      {
         AeActivityScopeDef scope = (AeActivityScopeDef) getEnclosedScopes().get(i);
         // Note: was previously short circuiting as soon as I encountered a scope w/ 
         // its flag set to "true" but this requires an understanding of the order of 
         // traversal and not worth the risk.
         // In other words, short circuting in this particular case is an optimization 
         // which provides no real benefit but only adds code that can either be broken 
         // later or misunderstood.
         // TODO (MF) further optimization possible to detect what variables/correlationSets/partnerLinks are referenced by said CH
         scope.setRecordSnapshotEnabled(true);
      }
      super.visit(aDef);
   }
   
   /**
    * Getter for the enclosed scopes
    */
   protected Stack getEnclosedScopes()
   {
      return mEnclosedScopes;
   }
}
 