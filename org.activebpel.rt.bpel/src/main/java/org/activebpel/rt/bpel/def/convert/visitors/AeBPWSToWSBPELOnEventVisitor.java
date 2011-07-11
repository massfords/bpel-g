// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/visitors/AeBPWSToWSBPELOnEventVisitor.java,v 1.4 2007/10/01 17:11:14 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.convert.visitors;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;

/**
 * This is a visitor used by the BPEL 1.1 -> BPEL 2.0 converter.  It is responsible for adding 
 * a scope child to all onEvent constructs.
 */
public class AeBPWSToWSBPELOnEventVisitor extends AeAbstractBPWSToWSBPELVisitor
{
   /**
    * Constructor.
    */
   public AeBPWSToWSBPELOnEventVisitor()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      String variable = aDef.getVariable();
      AeVariableDef varDef = AeDefUtil.getVariableByName(variable, aDef);
      if (varDef != null)
      {
         aDef.setMessageType(varDef.getMessageType());
      }

      AeActivityDef oldChild = aDef.getActivityDef();
      AeActivityScopeDef childScope = null;
      if (!(oldChild instanceof AeActivityScopeDef))
      {
         AeActivityScopeDef newScopeChild = new AeActivityScopeDef();
         newScopeChild.setActivityDef(oldChild);
         newScopeChild.setParentXmlDef(oldChild.getParent());
         oldChild.setParentXmlDef(newScopeChild);
         aDef.setActivityDef(newScopeChild);

         childScope = newScopeChild;
      }
      else
      {
         childScope = (AeActivityScopeDef) oldChild;
      }
      
      // Now create the variable and mark it as implicit.
      AeVariableDef newVariable = new AeVariableDef();
      newVariable.setName(variable);
      newVariable.setMessageType(varDef.getMessageType());
      newVariable.setImplicit(true);

      AeVariablesDef variablesDef = childScope.getVariablesDef();
      if (variablesDef == null)
      {
         variablesDef = new AeVariablesDef();
         childScope.setVariablesDef(variablesDef);
      }
      
      variablesDef.addVariableDef(newVariable);

      super.visit(aDef);
   }
}
