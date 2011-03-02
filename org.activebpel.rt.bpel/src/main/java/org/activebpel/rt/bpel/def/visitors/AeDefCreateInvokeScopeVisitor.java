// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeActivityContainerDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;

/**
 * A def visitor that will find any Invokes that have implicit scopes and make those scopes
 * explicit in the Def tree.
 */
public class AeDefCreateInvokeScopeVisitor extends AeAbstractDefVisitor
{
   /**
    * Default c'tor.
    */
   public AeDefCreateInvokeScopeVisitor()
   {
      setTraversalVisitor( new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      AeBaseDef parentDef = aDef.getParent();

      if (aDef.hasImplicitScopeDef())
      {
         AeActivityScopeDef scopeDef = aDef.removeImplicitScopeDef();
         scopeDef.setName(aDef.getName());
         scopeDef.setSourcesDef(aDef.getSourcesDef());
         scopeDef.setTargetsDef(aDef.getTargetsDef());
         scopeDef.getScopeDef().setParentXmlDef(scopeDef);

         // ************************************************************************************
         // Note here that the name of the invoke activity does not need to be voided - but we 
         // used to do that, so we continue to do it for location-path legacy reasons.
         // ************************************************************************************
         aDef.setName(""); //$NON-NLS-1$
         aDef.setSourcesDef(null);
         aDef.setTargetsDef(null);
         
         // Now do the following:
         // 1) make the invoke a child of the scope
         // 2) tell the invoke's old parent to replace the invoke with the scope
         // 3) make the scope the parent of the invoke
         // 4) make the invoke's old 'parent' the parent of the scope.
         scopeDef.setActivityDef(aDef);
         ((IAeActivityContainerDef) parentDef).replaceActivityDef(aDef, scopeDef);
         aDef.setParentXmlDef(scopeDef);
         scopeDef.setParentXmlDef(parentDef);
      }
      super.visit(aDef);
   }
}
