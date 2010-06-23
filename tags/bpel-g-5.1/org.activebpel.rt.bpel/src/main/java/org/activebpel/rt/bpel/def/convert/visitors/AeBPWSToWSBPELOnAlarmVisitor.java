// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/visitors/AeBPWSToWSBPELOnAlarmVisitor.java,v 1.2 2007/10/01 17:11:14 mford Exp $
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
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;

/**
 * This is a visitor used by the BPEL 1.1 -> BPEL 2.0 converter.  It is responsible for adding 
 * a scope child to onAlarm constructs within event handlers.
 */
public class AeBPWSToWSBPELOnAlarmVisitor extends AeAbstractBPWSToWSBPELVisitor
{
   /**
    * Constructor.
    */
   public AeBPWSToWSBPELOnAlarmVisitor()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      if (aDef.getParent() instanceof AeEventHandlersDef)
      {
         AeActivityDef oldChild = aDef.getActivityDef();
         if (!(oldChild instanceof AeActivityScopeDef))
         {
            AeActivityScopeDef newScopeChild = new AeActivityScopeDef();
            newScopeChild.setActivityDef(oldChild);
            newScopeChild.setParentXmlDef(oldChild.getParent());
            oldChild.setParentXmlDef(newScopeChild);
            aDef.setActivityDef(newScopeChild);
         }
      }

      super.visit(aDef);
   }
}
