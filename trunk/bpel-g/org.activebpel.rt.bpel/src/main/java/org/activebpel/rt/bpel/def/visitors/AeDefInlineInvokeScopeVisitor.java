// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefInlineInvokeScopeVisitor.java,v 1.2 2007/10/01 17:11:14 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.IAeActivityContainerDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.util.AeUtil;

/**
 * This is the foil to the AeDefCreateInvokeScopeVisitor class.  This
 * visitor will inline a scope under an invoke if that is possible.  In
 * other words, if an invoke is the only child of a scope, and the scope
 * meets the following criteria, then the scope will be removed and its
 * fault and compensation handlers inlined under the invoke itself:
 * 
 * 1) the scope must not be isolated
 * 2) the scope must not have a termination handler
 * 3) the scope must not have any event handlers
 * 4) the scope must not have any declared variables
 * 5) the scope must not have any declared correlation sets
 * 6) the scope must not have any declared partner links
 * 7) the scope must not have any extension elements or attributes
 */
public class AeDefInlineInvokeScopeVisitor extends AeAbstractDefVisitor
{
   /**
    * Default c'tor.
    */
   public AeDefInlineInvokeScopeVisitor()
   {
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * Returns true if the scope can be inlined.
    * 
    * @param aDef
    */
   private static boolean canScopeBeInlined(AeActivityScopeDef aDef)
   {
      AeScopeDef scope = aDef.getScopeDef();
      return (aDef.getActivityDef() instanceof AeActivityInvokeDef)
          && !aDef.isIsolated()
          && !scope.hasTerminationHandler()
          && !scope.hasEventHandlers()
          && !scope.hasVariables()
          && !scope.hasCorrelationSets()
          && !scope.hasPartnerLinks()
          && !scopeContainsExtensions(aDef);
   }

   /**
    * Returns true if the invoke can be inlined.  An invoke can be inlined 
    * as long as it has no links to or from it.
    * 
    * @param aInvoke
    */
   private static boolean canInvokeBeInlined(AeActivityInvokeDef aInvoke)
   {
      return !aInvoke.getSourceDefs().hasNext() && !aInvoke.getTargetDefs().hasNext();
   }
   
   /**
    * Returns true if the given scope contains any extension 
    * element or extension attributes.
    * 
    * @param aDef
    */
   private static boolean scopeContainsExtensions(AeActivityScopeDef aDef)
   {
      AeDefExtensionFinderVisitor visitor = new AeDefExtensionFinderVisitor();
      aDef.accept(visitor);
      return visitor.isFound();
   }

   /**
    * Returns true if the scope and invoke names match (for inlining).  The
    * names match if they are either 1) the same or 2) the scope name is 
    * empty.
    * 
    * This behavior is a holdover from ActiveBPEL 2.x.  I believe this 
    * behavior stems from the ActiveBPEL Designer training, where we tell
    * users to model an inline invoke scope by creating a named scope with
    * an un-named invoke inside the scope.
    * 
    * @param aScope
    * @param aInvoke
    */
   private static boolean isNameMatch(AeActivityScopeDef aScope, AeActivityInvokeDef aInvoke)
   {
      return AeUtil.compareObjects(aScope.getName(), aInvoke.getName()) || 
             AeUtil.isNullOrEmpty(aInvoke.getName());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aScopeDef)
   {
      if (canScopeBeInlined(aScopeDef))
      {
         AeActivityInvokeDef invokeDef = (AeActivityInvokeDef) aScopeDef.getActivityDef();
         if (canInvokeBeInlined(invokeDef) && isNameMatch(aScopeDef, invokeDef))
         {
            invokeDef.setImplicitScopeDef(aScopeDef);
            // Clear out the scope's child
            aScopeDef.setActivityDef(null);
            
            // Set the scope's name as the invoke's name.
            invokeDef.setName(aScopeDef.getName());
            
            // Move the sources and targets from the scope to the invoke.
            invokeDef.setSourcesDef(aScopeDef.getSourcesDef());
            invokeDef.setTargetsDef(aScopeDef.getTargetsDef());
            aScopeDef.setSourcesDef(null);
            aScopeDef.setTargetsDef(null);
            
            // Tell the scope's parent to replace the scope with the invoke
            invokeDef.setParentXmlDef(aScopeDef.getParent());
            ((IAeActivityContainerDef) aScopeDef.getParent()).replaceActivityDef(aScopeDef, invokeDef);
            aScopeDef.setParentXmlDef(null);
            aScopeDef.getScopeDef().setParentXmlDef(invokeDef);
         }
      }
      
      super.visit(aScopeDef);
   }
}
